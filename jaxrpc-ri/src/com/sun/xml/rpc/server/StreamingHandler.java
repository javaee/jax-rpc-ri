/*
 * $Id: StreamingHandler.java,v 1.2 2006-04-13 01:32:01 ofung Exp $
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

package com.sun.xml.rpc.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.messaging.saaj.soap.SOAPVersionMismatchException;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.rpc.client.HandlerChainImpl;
import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.MissingTrailingBlockIDException;
import com.sun.xml.rpc.encoding.ReferenceableSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPFaultInfoSerializer;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.server.http.MessageContextProperties;
import com.sun.xml.rpc.soap.message.Handler;
import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPBlockInfo;
import com.sun.xml.rpc.soap.message.SOAPFaultInfo;
import com.sun.xml.rpc.soap.message.SOAPHeaderBlockInfo;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.soap.streaming.SOAPNamespaceConstants;
import com.sun.xml.rpc.soap.streaming.SOAPProtocolViolationException;
import com.sun.xml.rpc.sp.ParseException;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.PrefixFactoryImpl;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderFactory;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterFactory;
import com.sun.xml.rpc.streaming.XmlTreeReader;
import com.sun.xml.rpc.streaming.XmlTreeWriter;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;

import org.jvnet.fastinfoset.FastInfosetSource;
import com.sun.xml.rpc.streaming.FastInfosetWriter;
import com.sun.xml.rpc.streaming.FastInfosetReaderFactoryImpl;
import com.sun.xml.rpc.streaming.FastInfosetWriterFactoryImpl;

// Static dependency with our impl of SAAJ for FI
import com.sun.xml.messaging.saaj.soap.MessageImpl;
    
/**
 * A base class for streaming-oriented handlers (such as ties).
 *
 * @author JAX-RPC Development Team
 */
public abstract class StreamingHandler
    implements Handler, com.sun.xml.rpc.spi.runtime.StreamingHandler {
        
    protected StreamingHandler() {
        localizer = new Localizer();
        messageFactory =
            new LocalizableMessageFactory("com.sun.xml.rpc.resources.tie");
    }

    protected String getActor() {
        return null;
    }

    public void handle(
        com.sun.xml.rpc.spi.runtime.SOAPMessageContext spiContext) {
        SOAPMessageContext context = (SOAPMessageContext) spiContext;

        // NOTE - due to the addition of handlers and their processing model,
        // the logic in this method has become quite obscure; we should rewrite
        // it so that it deals with handlers (and later interceptors)
        // explicitely, rather than through generic extension points

        StreamingHandlerState state = new StreamingHandlerState(context);

        try {

            XMLReader reader = null;

            try {
                
                boolean invoke = preHandlingHook(state);
                if (invoke == false) {
                    return;
                }

                if (context.isFailure() || state.isFailure()) {
                    return;
                }
                
                Source source = state.getRequest().getMessage().getSOAPPart().getContent();
                if (source instanceof StreamSource
                    && ((StreamSource) source).getInputStream() != null) {
                    InputStream istream =
                        ((StreamSource) source).getInputStream();
                    reader =
                        getXMLReaderFactory(state).createXMLReader(istream, true);
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
                                .getRequest()
                                .getMessage()
                                .getSOAPPart()
                                .getEnvelope());
                }

                preEnvelopeReadingHook(state);

                reader.nextElementContent();

                SOAPDeserializationContext deserializationContext =
                    new SOAPDeserializationContext();
                deserializationContext.setMessage(
                    state.getRequest().getMessage());

                if (reader.getState() == XMLReader.START
                    && SOAPNamespaceConstants.ENVELOPE.equals(reader.getURI())
                    && SOAPNamespaceConstants.TAG_ENVELOPE.equals(
                        reader.getLocalName())) {

                    boolean envelopePushedEncodingStyle =
                        deserializationContext.processEncodingStyle(reader);

                    preHeaderReadingHook(state);

                    if (state.isFailure()) {
                        return;
                    }

                    reader.nextElementContent();

                    if (reader.getState() == XMLReader.START
                        && SOAPNamespaceConstants.ENVELOPE.equals(
                            reader.getURI())) {

                        if (SOAPNamespaceConstants
                            .TAG_HEADER
                            .equals(reader.getLocalName())) {

                            boolean headerPushedEncodingStyle =
                                deserializationContext.processEncodingStyle(
                                    reader);

                            processHeaders(
                                reader,
                                deserializationContext,
                                state);

                            if (state.isFailure()) {
                                return;
                            }

                            postHeaderReadingHook(state);

                            if (state.isFailure()) {
                                return;
                            }

                            if (headerPushedEncodingStyle) {
                                deserializationContext.popEncodingStyle();
                            }

                            reader.nextElementContent();
                        }

                        if (reader.getState() == XMLReader.START
                            && SOAPNamespaceConstants.ENVELOPE.equals(
                                reader.getURI())
                            && SOAPNamespaceConstants.TAG_BODY.equals(
                                reader.getLocalName())) {

                            boolean bodyPushedEncodingStyle =
                                deserializationContext.processEncodingStyle(
                                    reader);

                            // go through all body blocks

                            if (reader.nextElementContent() == XMLReader.END) {
                                handleEmptyBody(
                                    reader,
                                    deserializationContext,
                                    state);

                                if (state.isFailure()) {
                                    return;
                                }

                                preBodyReadingHook(state);

                                if (state.isFailure()) {
                                    return;
                                }
                            } else {
                                peekFirstBodyElement(
                                    reader,
                                    deserializationContext,
                                    state);

                                if (state.isFailure()) {
                                    return;
                                }

                                preBodyReadingHook(state);

                                if (state.isFailure()) {
                                    return;
                                }

                                readFirstBodyElement(
                                    reader,
                                    deserializationContext,
                                    state);

                                if (state.isFailure()) {
                                    return;
                                }

                                deserializationContext
                                    .deserializeMultiRefObjects(
                                    reader);
                                //xsd:IDREF
                                deserializationContext
                                    .runPostDeserializationAction();
                            }

                            postBodyReadingHook(state);

                            // now deal with the trailer blocks
                            while (reader.nextElementContent()
                                == XMLReader.START) {
                                reader.skipElement();
                            }

                            if (bodyPushedEncodingStyle) {
                                deserializationContext.popEncodingStyle();
                            }

                            deserializationContext.doneDeserializing();

                        } else {
                            setBadRequestProp(state);
                            SOAPFaultInfo fault =
                                new SOAPFaultInfo(
                                    SOAPConstants.FAULT_CODE_CLIENT,
                                    BODY_EXPECTED_MESSAGE_STRING,
                                    getActor());
                            reportFault(fault, state);
                        }
                    } else {
                        setBadRequestProp(state);
                        SOAPFaultInfo fault =
                            new SOAPFaultInfo(
                                SOAPConstants.FAULT_CODE_CLIENT,
                                INVALID_ENVELOPE_CONTENT_MESSAGE_STRING,
                                getActor());
                        reportFault(fault, state);
                    }

                    if (envelopePushedEncodingStyle) {
                        deserializationContext.popEncodingStyle();
                    }

                } else if (
                    reader.getState() == XMLReader.START
                        && SOAPNamespaceConstants.TAG_ENVELOPE.equals(
                            reader.getLocalName())) {
                    // got an Envelope element in the wrong namespace
                    setBadRequestProp(state);
                    SOAPFaultInfo fault =
                        new SOAPFaultInfo(
                            SOAPConstants.FAULT_CODE_VERSION_MISMATCH,
                            ENVELOPE_VERSION_MISMATCH_MESSAGE_STRING,
                            getActor());
                    reportFault(fault, state);

                } else {
                    // not an envelope
                    setBadRequestProp(state);
                    SOAPFaultInfo fault =
                        new SOAPFaultInfo(
                            SOAPConstants.FAULT_CODE_CLIENT,
                            INVALID_ENVELOPE_MESSAGE_STRING,
                            getActor());
                    reportFault(fault, state);
                }

                if (state.isFailure()) {
                    return;
                }

                postEnvelopeReadingHook(state);

                if (state.isFailure()) {
                    return;
                }

                processingHook(state);

            } catch (Exception e) {

                logger.log(Level.SEVERE, e.getMessage(), e);
                String message = null;
                if (e instanceof Localizable) {
                    message = localizer.localize((Localizable) e);
                } else {
                    message =
                        localizer.localize(
                            messageFactory.getMessage(
                                "error.caughtExceptionWhileHandlingRequest",
                                new Object[] { e.toString()}));
                }

                SOAPFaultInfo fault = null;

                // DeserializationException here means it was an error in a well
                // formed request
                if (e instanceof SOAPProtocolViolationException
                    || e instanceof DeserializationException) {
                    logger.log(Level.SEVERE, message, e);
                    String faultMessage =
                        localizer.localize(
                            messageFactory.getMessage(
                                "error.caughtExceptionWhileHandlingRequest",
                                message));
                    fault =
                        new SOAPFaultInfo(
                            SOAPConstants.FAULT_CODE_CLIENT,
                            faultMessage,
                            getActor());
                    reportFault(fault, state);
                } else if (
                    e
                        instanceof com
                            .sun
                            .xml
                            .messaging
                            .saaj
                            .soap
                            .SOAPVersionMismatchException) {
                    logger.log(Level.SEVERE, message, e);
                    String faultMessage =
                        localizer.localize(
                            messageFactory.getMessage(
                                "error.caughtExceptionWhileHandlingRequest",
                                message));
                    fault =
                        new SOAPFaultInfo(
                            SOAPConstants.FAULT_CODE_VERSION_MISMATCH,
                            faultMessage,
                            getActor());
                    reportFault(fault, state);
                } else if (isMalformedXML(e)) { // the 400 case
                    setBadRequestProp(state);
                    logger.log(Level.SEVERE, message, e);
                    state.getResponse().setFailure(true);
                    state.getMessageContext().setFailure(true);
                } else {
                    logger.log(Level.SEVERE, message, e);
                    String faultMessage =
                        localizer.localize(
                            messageFactory.getMessage(
                                "message.faultMessageForException",
                                message));
                    fault =
                        new SOAPFaultInfo(
                            SOAPConstants.FAULT_CODE_SERVER,
                            faultMessage,
                            getActor());
                    reportFault(fault, state);
                }

            } finally {

                if (reader != null) {
                    reader.close();
                }

                try {

                    preResponseWritingHook(state);

                    writeResponse(state);

                    context.setFailure(state.getResponse().isFailure());

                    context.setMessage(state.getResponse().getMessage());

                    postResponseWritingHook(state);

                } catch (Exception e) {

                    String message = null;
                    if (e instanceof Localizable) {
                        message = localizer.localize((Localizable) e);
                    } else {
                        message =
                            localizer.localize(
                                messageFactory.getMessage(
                                    "error.caughtExceptionWhileHandlingRequest",
                                    new Object[] { e.toString()}));
                    }
                    logger.log(Level.SEVERE, message, e);
                    String faultMessage =
                        localizer.localize(
                            messageFactory.getMessage(
                                "message.faultMessageForException",
                                message));
                    SOAPFaultInfo fault =
                        new SOAPFaultInfo(
                            SOAPConstants.FAULT_CODE_SERVER,
                            faultMessage,
                            getActor());
                    reportFault(fault, state);
                    writeResponse(state);
                    context.setFailure(state.getResponse().isFailure());
                    context.setMessage(state.getResponse().getMessage());
                }

            }

        } catch (Exception e) {

            String message = null;
            if (e instanceof Localizable) {
                message = localizer.localize((Localizable) e);
            } else {
                message =
                    localizer.localize(
                        messageFactory.getMessage(
                            "error.caughtExceptionWhilePreparingResponse",
                            new Object[] { e.toString()}));
            }
            logger.log(Level.SEVERE, message, e);
            context.writeInternalServerErrorResponse();

        } finally {

            try {
                postHandlingHook(state);
            } catch (Exception e) {

                String message = null;
                if (e instanceof Localizable) {
                    logger.log(
                        Level.SEVERE,
                        localizer.localize((Localizable) e),
                        e);
                } else {
                    logger.log(
                        Level.SEVERE,
                        localizer.localize(
                            messageFactory.getMessage(
                                "error.caughtExceptionPostHandlingRequest",
                                new Object[] { e })));
                }
                logger.log(Level.SEVERE, message, e);
                context.writeInternalServerErrorResponse();

            }
        }
    }

    protected void processHeaders(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingHandlerState state)
        throws Exception {
        // try to process all headers, stopping prematurely only if
        // we encounter a serious failure (typically, a mustUnderstand
        // attribute we cannot honor)
        while (reader.nextElementContent() != XMLReader.END) {
            if (!processHeaderElement(reader, deserializationContext, state)) {
                return;
            }
        }
    }

    protected boolean processHeaderElement(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingHandlerState state)
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
            if (mustUnderstandAttr.equals("1")
                || mustUnderstandAttr.equals("true")) {
                mustUnderstand = true;
            } else if (
                mustUnderstandAttr.equals("0")
                    || mustUnderstandAttr.equals("false")) {
                // no-op
            } else {
                setBadRequestProp(state);
                SOAPFaultInfo fault =
                    new SOAPFaultInfo(
                        SOAPConstants.FAULT_CODE_CLIENT,
                        ILLEGAL_VALUE_OF_MUST_UNDERSTAND_ATTRIBUTE_FAULT_MESSAGE_STRING,
                        getActor());
                reportFault(fault, state);
                return false;
            }
        }

        if ((getActor() == null
            && (actorAttr == null
                || actorAttr.equals(SOAPNamespaceConstants.ACTOR_NEXT)))
            || (getActor() != null && (getActor().equals(actorAttr)))) {
            // intended for us
            SOAPHeaderBlockInfo headerInfo =
                new SOAPHeaderBlockInfo(
                    reader.getName(),
                    actorAttr,
                    mustUnderstand);
            boolean succeeded =
                readHeaderElement(
                    headerInfo,
                    reader,
                    deserializationContext,
                    state);
            if (!succeeded && mustUnderstand) {
                SOAPFaultInfo fault =
                    new SOAPFaultInfo(
                        SOAPConstants.FAULT_CODE_MUST_UNDERSTAND,
                        MUST_UNDERSTAND_FAULT_MESSAGE_STRING,
                        getActor());
                reportFault(fault, state);
                state.getRequest().setHeaderNotUnderstood(true);
                return false;
            }
        } else {
            // not intended for us: check mustUnderstand
            // just ignore it
            reader.skipElement();
        }

        return true;
    }

    protected boolean readHeaderElement(
        SOAPHeaderBlockInfo headerInfo,
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingHandlerState state)
        throws Exception {
        // by default, ignore the header
        reader.skipElement();
        return false;
    }

    protected void handleEmptyBody(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingHandlerState state)
        throws Exception {
        setBadRequestProp(state); //does this count as malformed xml
        SOAPFaultInfo fault =
            new SOAPFaultInfo(
                SOAPConstants.FAULT_CODE_CLIENT,
                NO_BODY_INFO_MESSAGE_STRING,
                getActor());
        reportFault(fault, state);
    }

    protected void peekFirstBodyElement(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingHandlerState state)
        throws Exception {
        // no-op
    }

    protected void readFirstBodyElement(
        XMLReader reader,
        SOAPDeserializationContext deserializationContext,
        StreamingHandlerState state)
        throws Exception {
        reader.skipElement();
    }

    public int getOpcodeForRequestMessage(SOAPMessage request) {
        if (usesSOAPActionForDispatching()) {
            String[] soapactionheaders =
                request.getMimeHeaders().getHeader("SOAPAction");
            if (soapactionheaders.length > 0) {
                String soapaction = soapactionheaders[0];
                return getOpcodeForSOAPAction(soapaction);
            }
        } else {
            try {
                SOAPBody body = request.getSOAPPart().getEnvelope().getBody();
                if (body != null) {
                    Iterator iter = body.getChildElements();
                    if (iter.hasNext()) {
                        SOAPElement firstBodyElement =
                            (SOAPElement) iter.next();
                        Name firstBodyElementName =
                            firstBodyElement.getElementName();
                        return getOpcodeForFirstBodyElementName(
                            new QName(
                                firstBodyElementName.getURI(),
                                firstBodyElementName.getLocalName()));
                    } else {
                        // body is empty
                        return getOpcodeForFirstBodyElementName(null);
                    }
                }
            } catch (SOAPException e) {
            }
        }

        // catch-all case
        return InternalSOAPMessage.NO_OPERATION;
    }

    public boolean usesSOAPActionForDispatching() {
        return false;
    }

    public int getOpcodeForFirstBodyElementName(QName name) {
        return InternalSOAPMessage.NO_OPERATION;
    }

    public int getOpcodeForSOAPAction(String s) {
        return InternalSOAPMessage.NO_OPERATION;
    }

    public Method getMethodForOpcode(int opcode)
        throws ClassNotFoundException, NoSuchMethodException {
        return null;
    }

    protected String[] getNamespaceDeclarations() {
        return null;
    }

    public QName[] getUnderstoodHeaders() {
        return new QName[0];
    }

    protected String getDefaultEnvelopeEncodingStyle() {
        return SOAPNamespaceConstants.ENCODING;
    }

    protected String getImplicitEnvelopeEncodingStyle() {
        return null;
    }

    protected String getPreferredCharacterEncoding() {
        return "UTF-8";
    }

    protected void writeResponse(StreamingHandlerState state)
        throws Exception {

        // make sure there is something to write
        SOAPBlockInfo bodyInfo = state.getResponse().getBody();
        boolean pushedEncodingStyle = false;

        if (bodyInfo == null || bodyInfo.getSerializer() == null) {
            if (state.getHandlerFlag()
                == StreamingHandlerState.CALL_NO_HANDLERS) {
                SOAPFaultInfo fault =
                    new SOAPFaultInfo(
                        SOAPConstants.FAULT_CODE_SERVER,
                        NO_BODY_INFO_MESSAGE_STRING,
                        getActor());
                reportFault(fault, state);
            }
            return;
        }

        ByteArrayOutputStream bufferedStream = null;
        XMLWriter writer = null;
        HandlerChain handlerChain = getHandlerChain();
        if (handlerChain == null || handlerChain.size() == 0) {
            // no handlers are present.
            bufferedStream = new ByteArrayOutputStream();
            writer =
                getXMLWriterFactory(state).createXMLWriter(
                    bufferedStream,
                    getPreferredCharacterEncoding());
        } else {
            // Ensure FI content negotiation is carried out
            MessageImpl response = (MessageImpl) state.getResponse().getMessage();
            MessageImpl request = (MessageImpl) state.getRequest ().getMessage(); 
            if (request.acceptFastInfoset()) {  //check if the Client accept FI
                response.setIsFastInfoset(true); 
            }
            // use our own XmlTreeWriter
            writer = new XmlTreeWriter((SOAPPart) response.getSOAPPart());
        }
        writer.setPrefixFactory(new PrefixFactoryImpl("ans"));

        SOAPSerializationContext serializationContext =
            new SOAPSerializationContext("ID");
        serializationContext.setMessage(state.getResponse().getMessage());

        writer.startElement(
            SOAPNamespaceConstants.TAG_ENVELOPE,
            SOAPNamespaceConstants.ENVELOPE,
            "env");

        writer.writeNamespaceDeclaration("xsd", SOAPNamespaceConstants.XSD);
        writer.writeNamespaceDeclaration("xsi", SOAPNamespaceConstants.XSI);
        writer.writeNamespaceDeclaration(
            "enc",
            SOAPNamespaceConstants.ENCODING);

        String[] namespaceDeclarations = getNamespaceDeclarations();
        if (namespaceDeclarations != null) {
            for (int i = 0; i < namespaceDeclarations.length; i += 2) {
                writer.writeNamespaceDeclaration(
                    namespaceDeclarations[i],
                    namespaceDeclarations[i + 1]);
            }
        }

        if (getDefaultEnvelopeEncodingStyle() != null) {
            pushedEncodingStyle =
                serializationContext.pushEncodingStyle(
                    getDefaultEnvelopeEncodingStyle(),
                    writer);
        } else if (getImplicitEnvelopeEncodingStyle() != null) {
            pushedEncodingStyle =
                serializationContext.setImplicitEncodingStyle(
                    getImplicitEnvelopeEncodingStyle());
        }

        boolean wroteHeader = false;
        for (Iterator iter = state.getResponse().headers(); iter.hasNext();) {
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

        writer.startElement(
            SOAPNamespaceConstants.TAG_BODY,
            SOAPNamespaceConstants.ENVELOPE,
            "env");

        serializationContext.beginFragment();
        bodyInfo.getSerializer().serialize(
            bodyInfo.getValue(),
            bodyInfo.getName(),
            null,
            writer,
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
            state.getResponse().getMessage().getSOAPPart().setContent(
                writer instanceof FastInfosetWriter ? 
                    (Source) new FastInfosetSource(bis) :
                    (Source) new StreamSource(bis));
        } else {
            // XmlTreeWriter must have modified the SOAPPart
        }
    }

    protected boolean preHandlingHook(StreamingHandlerState state)
        throws Exception {
        return callRequestHandlers(state);
    }

    protected void postHandlingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void preEnvelopeReadingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void preHeaderReadingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void postHeaderReadingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void preBodyReadingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void postBodyReadingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void postEnvelopeReadingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void processingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void preResponseWritingHook(StreamingHandlerState state)
        throws Exception {
    }

    protected void postResponseWritingHook(StreamingHandlerState state)
        throws Exception {
        String oneWay =
            (String) state.getMessageContext().getProperty(
                MessageContextProperties.ONE_WAY_OPERATION);
        if (oneWay == null || !oneWay.equalsIgnoreCase("true")) {
            // not a one-way message, so handle response or fault
            if (state.getHandlerFlag()
                == StreamingHandlerState.CALL_RESPONSE_HANDLERS) {
                callResponseHandlers(state);
            } else if (
                state.getHandlerFlag()
                    == StreamingHandlerState.CALL_FAULT_HANDLERS) {
                callFaultHandlers(state);
            }
        }
    }

    protected void callFaultHandlers(StreamingHandlerState state)
        throws Exception {
        HandlerChain handlerChain = getHandlerChain();

        if (handlerChain != null)
            handlerChain.handleFault(state.getMessageContext());
    }

    /**
     * This method has a number of different exit points. For all of them,
     * except when <code>true</code> is returned, FI content negotiation must
     * be carried out.
     */
    protected boolean callRequestHandlers(StreamingHandlerState state)
        throws Exception 
    {
        HandlerChainImpl handlerChain = getHandlerChain();
        if (hasNonEmptyHandlerChain(handlerChain)) {
            try {
                boolean allUnderstood =
                    handlerChain.checkMustUnderstand(state.getMessageContext());
                if (allUnderstood == false) {
                    SOAPFaultInfo fault =
                        new SOAPFaultInfo(
                            SOAPConstants.FAULT_CODE_MUST_UNDERSTAND,
                            MUST_UNDERSTAND_FAULT_MESSAGE_STRING,
                            getActor());                    
                    // FI Content negotiation is completed in this call
                    reportFault(fault, state);                    
                    state.getRequest().setHeaderNotUnderstood(true);
                    state.setHandlerFlag(
                        StreamingHandlerState.CALL_NO_HANDLERS);
                    return false;
                }

                state.setHandlerFlag(
                    StreamingHandlerState.CALL_RESPONSE_HANDLERS);
                boolean r =
                    handlerChain.handleRequest(state.getMessageContext());
                if (r == false) {
                    // Ensure FI content negotiation is carried out
                    MessageImpl request = (MessageImpl) state.getMessageContext().getMessage();
                    if (request.acceptFastInfoset()) {
                        request.setIsFastInfoset(true);
                    }
                    state.setResponse(new InternalSOAPMessage(request));
                }
                return r;
            } catch (SOAPFaultException sfe) {
                // Ensure FI content negotiation is carried out 
                MessageImpl request = (MessageImpl) state.getMessageContext().getMessage();
                if (request.acceptFastInfoset()) {
                    request.setIsFastInfoset(true);
                }
                state.setResponse(new InternalSOAPMessage(request));
                state.setHandlerFlag(StreamingHandlerState.CALL_FAULT_HANDLERS);
                return false;
            } catch (SOAPVersionMismatchException svme) {
                SOAPFaultInfo fault =
                    new SOAPFaultInfo(
                        SOAPConstants.FAULT_CODE_VERSION_MISMATCH,
                        ENVELOPE_VERSION_MISMATCH_MESSAGE_STRING,
                        getActor());
                // FI Content negotiation is completed in this call
                reportFault(fault, state);
                state.setHandlerFlag(StreamingHandlerState.CALL_NO_HANDLERS);
                return false;
            } catch (JAXRPCException jre) {
                state.setHandlerFlag(StreamingHandlerState.CALL_NO_HANDLERS);
                throw jre;
            } catch (RuntimeException rex) {
                state.setHandlerFlag(StreamingHandlerState.CALL_NO_HANDLERS);
                throw rex;
            }
        }
        return true;
    }

    private boolean hasNonEmptyHandlerChain(HandlerChain chain) {
        return (chain != null && !chain.isEmpty());
    }

    protected void callResponseHandlers(StreamingHandlerState state)
        throws Exception {
        HandlerChain handlerChain = getHandlerChain();

        if (handlerChain != null)
            handlerChain.handleResponse(state.getMessageContext());
    }

    protected HandlerChainImpl getHandlerChain() {
        return null;
    }

    /**
     * Return an FI reader if request is FI
     */
    protected XMLReaderFactory getXMLReaderFactory(StreamingHandlerState state) {
        return state.isFastInfoset() ? FastInfosetReaderFactoryImpl.newInstance()
                                     : XMLReaderFactory.newInstance();
    }

    /**
     * Return an FI writer if request is FI or FI is accept by client
     */
    protected XMLWriterFactory getXMLWriterFactory(StreamingHandlerState state) {
        return state.isFastInfoset() || state.acceptFastInfoset() ? 
            FastInfosetWriterFactoryImpl.newInstance() : XMLWriterFactory.newInstance();
    }

    /**
     * Note that a call to <code>StreamingHandlerState.resetResponse()</code>
     * creates a new SAAJ SOAPMessage for the response with the FI flag based
     * on the SOAPMessageContext state. This is needed for content negotiation.
     */
    protected void reportFault(
        SOAPFaultInfo fault,
        StreamingHandlerState state) {
        if (state.getRequest().isHeaderNotUnderstood()) {
            // avoid overwriting an already reported mustUnderstand fault
            return;
        }

        state.resetResponse();      // FI content negotiation
        SOAPBlockInfo faultBlock =
            new SOAPBlockInfo(SOAPConstants.QNAME_SOAP_FAULT);
        faultBlock.setValue(fault);
        faultBlock.setSerializer(soapFaultInfoSerializer);
        state.getResponse().setBody(faultBlock);
        state.getResponse().setFailure(true);
        state.getMessageContext().setFailure(true);
    }

    boolean isMalformedXML(Exception e) {
        //currently just use parseException as malformed xml here
        if (e instanceof JAXRPCExceptionBase) {
            if (e instanceof DeserializationException
                || e instanceof MissingTrailingBlockIDException) {
                return true;
            }
            Throwable cause = ((JAXRPCExceptionBase) e).getLinkedException();
            if (cause != null
                && cause instanceof LocalizableExceptionAdapter) {
                Throwable ex =
                    ((LocalizableExceptionAdapter) cause).getNestedException();
                if (ex != null) {
                    if (ex instanceof ParseException)
                        return true;
                    if (ex instanceof DeserializationException)
                        return true;
                }
            }
        } else if (e instanceof ParseException) {
            return true;
        }

        return false;
    }

    void setBadRequestProp(StreamingHandlerState state) {
        state.getMessageContext().setProperty(
            MessageContextProperties.CLIENT_BAD_REQUEST,
            "true");
    }

    private final static SOAPFaultInfoSerializer soapFaultInfoSerializer =
        new SOAPFaultInfoSerializer(false, false);

    private Localizer localizer;
    private LocalizableMessageFactory messageFactory;

    private final static String MUST_UNDERSTAND_FAULT_MESSAGE_STRING =
        "SOAP must understand error";
    private final static String NO_BODY_INFO_MESSAGE_STRING =
        "Missing body information";
    private final static String BODY_EXPECTED_MESSAGE_STRING =
        "SOAP body expected";
    private final static String INVALID_ENVELOPE_CONTENT_MESSAGE_STRING =
        "Invalid content in SOAP envelope";
    private final static String INVALID_ENVELOPE_MESSAGE_STRING =
        "Invalid SOAP envelope";
    private final static String ENVELOPE_VERSION_MISMATCH_MESSAGE_STRING =
        "Invalid SOAP envelope version";
    private final static String ILLEGAL_VALUE_OF_MUST_UNDERSTAND_ATTRIBUTE_FAULT_MESSAGE_STRING =
        "Illegal value of SOAP mustUnderstand attribute";

    private static final Logger logger =
        Logger.getLogger("com.sun.xml.rpc.server");
}
