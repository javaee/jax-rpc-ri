/*
 * $Id: StreamingSender.java,v 1.2.2.4 2009-12-24 18:48:39 lx194240 Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.xml.rpc.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.ReferenceableSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPFaultInfoSerializer;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPBlockInfo;
import com.sun.xml.rpc.soap.message.SOAPFaultInfo;
import com.sun.xml.rpc.soap.message.SOAPHeaderBlockInfo;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.soap.streaming.SOAPNamespaceConstants;
import com.sun.xml.rpc.soap.streaming.SOAPProtocolViolationException;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.PrefixFactoryImpl;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderFactory;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterFactory;
import com.sun.xml.rpc.streaming.XmlTreeReader;
import com.sun.xml.rpc.streaming.XmlTreeWriter;

import org.jvnet.fastinfoset.FastInfosetSource;
import com.sun.xml.rpc.streaming.FastInfosetWriter;
import com.sun.xml.rpc.streaming.FastInfosetReaderFactoryImpl;

/**
 * <p> A base class for streaming-oriented message senders (such as stubs). </p>
 *
 * @author JAX-RPC Development Team
 */
public abstract class StreamingSender {

    protected StreamingSender() {
    }

    protected StreamingSenderState _start(HandlerChain handlerChain) {
        //create the SOAPMessageContext
        SOAPMessageContext messageContext = new SOAPMessageContext();
        ((HandlerChainImpl) handlerChain).addUnderstoodHeaders(
            _getUnderstoodHeaders());
        //create and return StreamingSenderState containing message context
        //and a handler chain 
        return new StreamingSenderState(messageContext, handlerChain, false, true);
    }

    protected String _getActor() {
        return null;
    }

    protected void _send(String endpoint, StreamingSenderState state)
        throws Exception {
        
        //send the request over the wire
        
        _preSendingHook(state);

        _preRequestWritingHook(state);

        _writeRequest(state);

        _postRequestWritingHook(state);

        boolean invoke = _preRequestSendingHook(state);

        //get the transport mechanism and invoke the request
        //the transport may be HTTP or Local transport
        if (invoke == true)
            _getTransport().invoke(endpoint, state.getMessageContext());

        _postRequestSendingHook(state);

        XMLReader reader = null;

        //gets the response and reads it using XMLReader
        try {

            SOAPFaultInfo fault = null;

            _preHandlingHook(state);

            Source source =
                state.getResponse().getMessage().getSOAPPart().getContent();
            
            if (source instanceof StreamSource
                && ((StreamSource) source).getInputStream() != null) 
            {
                InputStream istream = ((StreamSource) source).getInputStream();
                reader = _getXMLReaderFactory().createXMLReader(istream, true);
            } 
            else if (source instanceof FastInfosetSource) {
                // Avoid going through an XmlTreeReader by using FI factory
                InputStream istream = 
                    ((FastInfosetSource) source).getInputSource().getByteStream();
                reader = FastInfosetReaderFactoryImpl.newInstance().createXMLReader(istream, true);
            }
            else {
                reader =
                    new XmlTreeReader(
                        state
                            .getResponse()
                            .getMessage()
                            .getSOAPPart()
                            .getEnvelope());
            }

            _preEnvelopeReadingHook(state);

            reader.nextElementContent();

            SOAPDeserializationContext deserializationContext =
                new SOAPDeserializationContext();
            deserializationContext.setMessage(state.getResponse().getMessage());

            //make sure that this is a valid SOAP Envelope
            if (reader.getState() == XMLReader.START
                && SOAPNamespaceConstants.ENVELOPE.equals(reader.getURI())
                && SOAPNamespaceConstants.TAG_ENVELOPE.equals(
                    reader.getLocalName())) {

                boolean envelopePushedEncodingStyle =
                    deserializationContext.processEncodingStyle(reader);

                _preHeaderReadingHook(state);

                if (state.isFailure()) {
                    return;
                }

                reader.nextElementContent();
                //check for valid SOAP Headers
                if (reader.getState() == XMLReader.START
                    && SOAPNamespaceConstants.ENVELOPE.equals(reader.getURI())) {

                    if (SOAPNamespaceConstants
                        .TAG_HEADER
                        .equals(reader.getLocalName())) {

                        boolean headerPushedEncodingStyle =
                            deserializationContext.processEncodingStyle(reader);

                        _processHeaders(reader, deserializationContext, state);

                        // if (state.isFailure()) { return; }

                        _postHeaderReadingHook(state);

                        // if (state.isFailure()) { return; }

                        if (headerPushedEncodingStyle) {
                            deserializationContext.popEncodingStyle();
                        }

                        reader.nextElementContent();
                    }

                    //Has the SOAP Body been reached?
                    if (reader.getState() == XMLReader.START
                        && SOAPNamespaceConstants.ENVELOPE.equals(reader.getURI())
                        && SOAPNamespaceConstants.TAG_BODY.equals(
                            reader.getLocalName())) {

                        boolean bodyPushedEncodingStyle =
                            deserializationContext.processEncodingStyle(reader);
                        Object faultOrFaultState = null;

                        // go through all body blocks

                        //case of empty SOAP Body
                        if (reader.nextElementContent() == XMLReader.END) {
                            _handleEmptyBody(
                                reader,
                                deserializationContext,
                                state);

                            if (state.isFailure()) {
                                return;
                            }

                            _preBodyReadingHook(state);

                            if (state.isFailure()) {
                                return;
                            }
                        } else {
                            _preBodyReadingHook(state);

                            if (state.isFailure()) {
                                return;
                            }

                            //read the SOAP Fault
                            if (reader.getName().equals(QNAME_SOAP_FAULT)) {
                                faultOrFaultState =
                                    _readBodyFaultElement(
                                        reader,
                                        deserializationContext,
                                        state);
                            } else {
                                //or else read the first Body Element
                                _readFirstBodyElement(
                                    reader,
                                    deserializationContext,
                                    state);
                            }

                            if (state.isFailure()) {
                                return;
                            }

                            //deserialize the rest of the response
                            deserializationContext.deserializeMultiRefObjects(
                                reader);
                            //xsd:IDREF
                            deserializationContext
                                .runPostDeserializationAction();

                        }

                        _postBodyReadingHook(state);

                        // now deal with the trailer blocks
                        // deserializationContext.deserializeMultiRefObjects(reader);
                        while (reader.nextElementContent()
                            == XMLReader.START) {
                            reader.skipElement();
                        }

                        deserializationContext.doneDeserializing();

                        if (bodyPushedEncodingStyle) {
                            deserializationContext.popEncodingStyle();
                        }

                        if (faultOrFaultState != null) {
                            if (faultOrFaultState instanceof SOAPFaultInfo) {
                                fault = (SOAPFaultInfo) faultOrFaultState;
                            } else if (
                                faultOrFaultState
                                    instanceof SOAPDeserializationState) {
                                fault =
                                    (SOAPFaultInfo)
                                        (
                                            (SOAPDeserializationState) faultOrFaultState)
                                        .getInstance();
                            } else {
                                throw new SenderException("sender.response.unrecognizedFault");
                            }
                        }
                    } else {
                        throw new SOAPProtocolViolationException("soap.protocol.missingBody");
                    }
                } else {
                    throw new SOAPProtocolViolationException("soap.protocol.invalidEnvelopeContent");
                }

                if (envelopePushedEncodingStyle) {
                    deserializationContext.popEncodingStyle();
                }
            } else if (
                reader.getState() == XMLReader.START
                    && SOAPNamespaceConstants.TAG_ENVELOPE.equals(
                        reader.getLocalName())) {
                // got an Envelope element in the wrong namespace
                throw new SOAPProtocolViolationException("soap.protocol.envelopeVersionMismatch");
            } else {
                // not an envelope
                throw new SOAPProtocolViolationException("soap.protocol.notAnEnvelope");
            }

            if (state.isFailure()) {
                return;
            }

            _postEnvelopeReadingHook(state);

            // raise a fault
            if (fault != null) {
                //if header, get the headerelement
                _raiseFault(fault, state);
            }

        } catch (SOAPFaultException e) {
            throw e;
        } catch (RuntimeException rex) {
            _handleRuntimeExceptionInSend(rex);
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }

            try {
                _postHandlingHook(state);
            } finally {
                _postSendingHook(state);
            }
        }
    }

    protected void _handleRuntimeExceptionInSend(RuntimeException rex)
        throws Exception {
        throw new RemoteException("Runtime exception", rex);
    }

    protected void _sendOneWay(String endpoint, StreamingSenderState state)
        throws Exception {

        _preSendingHook(state);

        _preRequestWritingHook(state);

        _writeRequest(state);

        _postRequestWritingHook(state);

        boolean invoke = _preRequestSendingHook(state);

        if (invoke == true)
            _getTransport().invokeOneWay(endpoint, state.getMessageContext());

        _postRequestSendingHook(state);

        _postSendingHook(state);
    }

    protected void _processHeaders(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingSenderState state)
        throws Exception {
        // try to process all headers, stopping prematurely only if
        // we encounter a serious failure (typically, a mustUnderstand
        // attribute we cannot honor)
        while (reader.nextElementContent() != XMLReader.END) {
            _processHeaderElement(reader, deserializationContext, state);
        }
    }

    protected void _processHeaderElement(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingSenderState state)
        throws Exception {
        Attributes attributes = reader.getAttributes();
        String actorAttr =
            attributes.getValue(
                SOAPNamespaceConstants.ENVELOPE,
                SOAPNamespaceConstants.ATTR_ACTOR);
        String mustUnderstandAttr =
            attributes.getValue(
                SOAPNamespaceConstants.ENVELOPE,
                SOAPNamespaceConstants.ATTR_MUST_UNDERSTAND);
        boolean mustUnderstand = false;
        if (mustUnderstandAttr != null) {
            // TODO - remember to change this if mustUnderstand is made to allow "true", "false" as values
            if (mustUnderstandAttr.equals("1")
                || mustUnderstandAttr.equals("true")) {
                mustUnderstand = true;
            } else if (
                mustUnderstandAttr.equals("0")
                    || mustUnderstandAttr.equals("false")) {
                // no-op
            } else {
                throw new SenderException(
                    "sender.response.illegalValueOfMustUnderstandAttribute",
                    mustUnderstandAttr);
            }
        }

        if ((_getActor() == null
            && (actorAttr == null
                || actorAttr.equals(SOAPNamespaceConstants.ACTOR_NEXT)))
            || (_getActor() != null && (_getActor().equals(actorAttr)))) {
            // intended for us
            SOAPHeaderBlockInfo headerInfo =
                new SOAPHeaderBlockInfo(
                    reader.getName(),
                    actorAttr,
                    mustUnderstand);
            boolean succeeded =
                _readHeaderElement(
                    headerInfo,
                    reader,
                    deserializationContext,
                    state);
            return;
            /*if (!succeeded && mustUnderstand) {
                throw new SOAPFaultException(
                    SOAPConstants.FAULT_CODE_MUST_UNDERSTAND,
                    MUST_UNDERSTAND_FAULT_MESSAGE_STRING,
                    _getActor(),
                    null);
            }*/
        } else {
            // not intended for us: check mustUnderstand
            // just ignore it
            reader.skipElement();
            return;

            /* removed this code and added above 2 statements to fix bug# 4731903
                        if (mustUnderstand) {
                            throw new SOAPFaultException(SOAPConstants.FAULT_CODE_MUST_UNDERSTAND,
                                                         MUST_UNDERSTAND_FAULT_MESSAGE_STRING,
                                                         _getActor(),
                                                         null);
                            // TODO - when moving to SOAP 1.2, use a "Misunderstood" header fault here
                        }
                        else {
                        // just ignore it
                        reader.skipElement();
                        return;
                        }*/
        }
    }

    protected boolean _readHeaderElement(
        SOAPHeaderBlockInfo headerInfo,
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingSenderState state)
        throws Exception {
        // by default, ignore the header
        reader.skipElement();
        return false;
    }

    protected Object _readBodyFaultElement(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingSenderState state)
        throws Exception {
        return faultInfoSerializer.deserialize(
            null,
            reader,
            deserializationContext);
    }

    protected void _readFirstBodyElement(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingSenderState state)
        throws Exception {
        reader.skipElement();
    }

    protected void _handleEmptyBody(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingSenderState state)
        throws Exception {
        throw new SOAPProtocolViolationException("soap.protocol.emptyBody");
    }

    protected void _raiseFault(SOAPFaultInfo fault, StreamingSenderState state)
        throws Exception {
        if (fault.getDetail() != null
            && fault.getDetail() instanceof Exception) {
            throw (Exception) fault.getDetail();
        }

        Object detail = fault.getDetail();
        if (detail != null && (detail instanceof Detail)) {
            throw new SOAPFaultException(
                fault.getCode(),
                fault.getString(),
                fault.getActor(),
                (Detail) detail);
        } else if (detail == null) {
            //its headerfault
            Object obj = null;
            if (state.getResponse().headers().hasNext()) {
                obj = state.getResponse().headers().next();
                throw new RemoteException(
                    fault.getString(),
                    new com.sun.xml.rpc.util.HeaderFaultException(
                        fault.getString(),
                        obj));
            }
            
            // CR-6660308, Merge from JavaCAPS RTS for backward compatibility

            //If the header is not set in the InternalSOAPMessage, still look for it
            //in the received soap message.  It is assumed here that the header in
            //the SOAP response has the headerfault details.
            //This will cover cases where the wsdl does not define the 
            //headerfault SOAP binding, yet the server is throwing the
            //headerfault.  For instance, even though the wsdl does not describe
            //that the web service expects the ws-security header, the client
            //is aware that it needs to send the ws-security header and therefore
            //need to handle the headerfaults for the ws-security header.
            //We put the SOAPFaultException inside the RemoteException to 
            //indicate this fact.
            Detail lDetail = null;
            try {
                SOAPMessage lMsg = ((InternalSOAPMessage) state.getResponse()).getMessage();
                SOAPHeader lHeader = lMsg.getSOAPHeader();
                if (lHeader != null) {
                    SOAPFactory sf = SOAPFactory.newInstance();
                    lDetail = sf.createDetail();
                    DetailEntry lEntry = lDetail.addDetailEntry(sf.createName("Header", "env", "http://schemas.xmlsoap.org/soap/envelope/"));
                    for (Iterator lIt = lHeader.examineAllHeaderElements(); lIt.hasNext();) {
                        lEntry.addChildElement((SOAPElement) lIt.next());
                    }
                }
            } catch (SOAPException e) {
                //Failed to get the header; just ignore this excepion
                //SOAPFaultException will be thrown without the detail
                //by the code below.
            }
            
            if (lDetail != null) {
                throw new RemoteException(fault.getString(), 
                                          new SOAPFaultException(fault.getCode(), fault.getString(), fault.getActor(), lDetail));
            }
            
            //If header is null, a SOAPFaultException is thrown without detail
            //by the code below.

        }

        if (fault.getCode().equals(SOAPConstants.FAULT_CODE_SERVER)) {
            throw new ServerException(fault.getString());
        } else if (
            fault.getCode().equals(
                SOAPConstants.FAULT_CODE_DATA_ENCODING_UNKNOWN)) {
            throw new MarshalException(fault.getString());
        } else if (
            fault.getCode().equals(
                SOAPConstants.FAULT_CODE_PROCEDURE_NOT_PRESENT)
                || fault.getCode().equals(
                    SOAPConstants.FAULT_CODE_BAD_ARGUMENTS)) {
            throw new RemoteException(fault.getString());
        } else {
            Object obj = null;
            // somehow put all the information from a fault into a remote exception
            //javax.xml.soap.Detail detail=null;
            if (state.getResponse().headers().hasNext()) {
                obj = state.getResponse().headers().next();
            }
            throw new SOAPFaultException(
                fault.getCode(),
                fault.getString(),
                fault.getActor(),
                (javax.xml.soap.Detail) detail);
        }

    }

    protected void _writeRequest(StreamingSenderState state) throws Exception {

        // make sure there is something to write
        SOAPBlockInfo bodyInfo = state.getRequest().getBody();
        boolean pushedEncodingStyle = false;

        if (bodyInfo == null || bodyInfo.getSerializer() == null) {
            throw new SenderException("sender.request.missingBodyInfo");
        }

        //write out the XML Request using the XMLWriter
        XMLWriter writer = null;
        ByteArrayOutputStream bufferedStream = null;
        HandlerChain handlerChain = state.getHandlerChain();
        if (handlerChain == null || handlerChain.size() == 0) {
            // no handlers are present.
            bufferedStream = new ByteArrayOutputStream();
            writer =
                _getXMLWriterFactory().createXMLWriter(
                    bufferedStream,
                    _getPreferredCharacterEncoding());
        } else {
            writer =
                new XmlTreeWriter(
                    (SOAPPart) state.getRequest().getMessage().getSOAPPart());
        }

        writer.setPrefixFactory(new PrefixFactoryImpl("ans"));

        SOAPSerializationContext serializationContext =
            new SOAPSerializationContext("ID");
        serializationContext.setMessage(state.getRequest().getMessage());

        //write SOAP Envelope
        writer.startElement(
            SOAPNamespaceConstants.TAG_ENVELOPE,
            SOAPNamespaceConstants.ENVELOPE,
            "env");

        //write needed SOAP Namespace declarations
        writer.writeNamespaceDeclaration("xsd", SOAPNamespaceConstants.XSD);
        writer.writeNamespaceDeclaration("xsi", SOAPNamespaceConstants.XSI);
        writer.writeNamespaceDeclaration(
            "enc",
            SOAPNamespaceConstants.ENCODING);

        //look for request namspaces and write those
        String[] namespaceDeclarations = _getNamespaceDeclarations();
        if (namespaceDeclarations != null) {
        // CR-6660363, Merge from JavaCAPS RTS for backward compatibility

            /* 101878
             * since the referenced namespaces will be declared at the subnodes inside both of the soap:Header and soap:Body, 
             * we remove them from the top soap:Envelop node

            for (int i = 0; i < namespaceDeclarations.length; i += 2) {
                writer.writeNamespaceDeclaration(
                    namespaceDeclarations[i],
                    namespaceDeclarations[i + 1]);
            }
             */
        }

        if (_getDefaultEnvelopeEncodingStyle() != null) {
            pushedEncodingStyle =
                serializationContext.pushEncodingStyle(
                    _getDefaultEnvelopeEncodingStyle(),
                    writer);
        } else if (_getImplicitEnvelopeEncodingStyle() != null) {
            pushedEncodingStyle =
                serializationContext.setImplicitEncodingStyle(
                    _getImplicitEnvelopeEncodingStyle());
        }

        //write SOAP Header
        boolean wroteHeader = false;
        for (Iterator iter = state.getRequest().headers(); iter.hasNext();) {
            SOAPHeaderBlockInfo headerInfo = (SOAPHeaderBlockInfo) iter.next();
            if (headerInfo.getValue() != null
                && headerInfo.getSerializer() != null) {
                if (!wroteHeader) {
                    writer.startElement(
                        SOAPNamespaceConstants.TAG_HEADER,
                        SOAPNamespaceConstants.ENVELOPE);
                    wroteHeader = true;
                }

                serializationContext.beginFragment();
                JAXRPCSerializer serializer = headerInfo.getSerializer();
                if (serializer instanceof ReferenceableSerializer) {
                    ((ReferenceableSerializer) serializer).serializeInstance(
                        headerInfo.getValue(),
                        headerInfo.getName(),
                        false,
                        writer,
                        serializationContext);
                } else {
                    serializer.serialize(
                        headerInfo.getValue(),
                        headerInfo.getName(),
                        null,
                        writer,
                        serializationContext);
                }
                serializationContext.endFragment();
            }
        }

        if (wroteHeader) {
            writer.endElement(); // env:HEADER
        }

        //start writing the SOAP Body
        writer.startElement(
            SOAPNamespaceConstants.TAG_BODY,
            SOAPNamespaceConstants.ENVELOPE,
            "env");

        serializationContext.beginFragment();
        //serialize the request
        bodyInfo.getSerializer().serialize(
            bodyInfo.getValue(),
            bodyInfo.getName(),
            null,
            writer,     // XML or FI writer passed to serializer
            serializationContext);
        serializationContext.serializeMultiRefObjects(writer);
        serializationContext.endFragment();
        
        writer.endElement(); // env:BODY
        writer.endElement(); // env:ENVELOPE
        if (pushedEncodingStyle) {
            serializationContext.popEncodingStyle();
        }
        writer.close();

        if (handlerChain == null || handlerChain.size() == 0) {
            // no handlers are present.
            byte[] data = bufferedStream.toByteArray();
            
            // Set source in SAAJ object depending on which XMLWriter was used
            ByteInputStream bis = new ByteInputStream(data, data.length);            
            state.getRequest().getMessage().getSOAPPart().setContent(
                writer instanceof FastInfosetWriter ? 
                    (Source) new FastInfosetSource(bis) :
                    (Source) new StreamSource(bis));
        } else {
            // XMLTreeWriter must have updated the SOAPPart
        }
    }

    protected String[] _getNamespaceDeclarations() {
        return null;
    }

    public QName[] _getUnderstoodHeaders() {
        return new QName[0];
    }

    //for soap
    protected String _getDefaultEnvelopeEncodingStyle() {
        return SOAPNamespaceConstants.ENCODING;
        //        return null;
    }

    protected String _getImplicitEnvelopeEncodingStyle() {
        return null;
    }

    protected String _getPreferredCharacterEncoding() {
        return "UTF-8";
    }

    ////////

    protected void _preSendingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _postSendingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _preHandlingHook(StreamingSenderState state)
        throws Exception {
        _callResponseHandlers(state);
    }

    protected void _postHandlingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _preRequestWritingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _postRequestWritingHook(StreamingSenderState state)
        throws Exception {
    }

    protected boolean _preRequestSendingHook(StreamingSenderState state)
        throws Exception {
        return _callRequestHandlers(state);
    }

    protected void _postRequestSendingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _preEnvelopeReadingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _preHeaderReadingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _postHeaderReadingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _preBodyReadingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _postBodyReadingHook(StreamingSenderState state)
        throws Exception {
    }

    protected void _postEnvelopeReadingHook(StreamingSenderState state)
        throws Exception {
    }

    protected boolean _callRequestHandlers(StreamingSenderState state)
        throws Exception {
        HandlerChain handlerChain = state.getHandlerChain();

        if (handlerChain != null) {
            try {
                return handlerChain.handleRequest(state.getMessageContext());
            } catch (RuntimeException e) {
                throw new RemoteException("request handler error: ", e);
            }
        }

        return true;
    }

    protected void _callResponseHandlers(StreamingSenderState state)
        throws Exception {
        HandlerChainImpl handlerChain =
            (HandlerChainImpl) state.getHandlerChain();
        if (hasNonEmptyHandlerChain(handlerChain)) {
            boolean allUnderstood =
                handlerChain.checkMustUnderstand(state.getMessageContext());
            if (allUnderstood == false) {
                throw new SOAPFaultException(
                    SOAPConstants.FAULT_CODE_MUST_UNDERSTAND,
                    MUST_UNDERSTAND_FAULT_MESSAGE_STRING,
                    _getActor(),
                    null);
            }
            try {
                handlerChain.handleResponse(state.getMessageContext());

            // CR-6894009, Merge from JavaCAPS RTS for backward compatibility
            } catch (SOAPFaultException sfe) {
            	throw sfe;

            } catch (RuntimeException e) {
                throw new RemoteException("response handler error: ", e);
            }
        }
    }

    private boolean hasNonEmptyHandlerChain(HandlerChain chain) {
        return (chain != null && !chain.isEmpty());
    }

    ////////

    protected abstract ClientTransport _getTransport();

    protected XMLReaderFactory _getXMLReaderFactory() {
        return XMLReaderFactory.newInstance();
    }

    protected XMLWriterFactory _getXMLWriterFactory() {
        return XMLWriterFactory.newInstance();
    }
        
    private static final SOAPFaultInfoSerializer faultInfoSerializer =
        new SOAPFaultInfoSerializer(true, false);

    private final static QName QNAME_SOAP_FAULT =
        new QName(SOAPNamespaceConstants.ENVELOPE, "Fault");

    private final static String MUST_UNDERSTAND_FAULT_MESSAGE_STRING =
        "SOAP must understand error";
}
