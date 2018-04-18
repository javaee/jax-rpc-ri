/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Id: CallInvokerImpl.java,v 1.3 2007-07-13 23:35:55 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.client.dii;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.HandlerChain;

import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportFactory;
import com.sun.xml.rpc.client.StreamingSender;
import com.sun.xml.rpc.client.StreamingSenderState;
import com.sun.xml.rpc.client.StubPropertyConstants;
import com.sun.xml.rpc.client.http.HttpClientTransport;
import com.sun.xml.rpc.client.http.HttpClientTransportFactory;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.soap.SOAPResponseStructure;
import com.sun.xml.rpc.soap.message.InternalSOAPMessage;
import com.sun.xml.rpc.soap.message.SOAPBlockInfo;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.soap.streaming.SOAPNamespaceConstants;
import com.sun.xml.rpc.streaming.XMLReader;

import com.sun.xml.rpc.client.HandlerChainImpl;
import com.sun.xml.rpc.client.dii.CallPropertyConstants;
import com.sun.xml.rpc.streaming.XMLWriterFactory;
import com.sun.xml.rpc.streaming.XMLWriterFactoryImpl;
import com.sun.xml.rpc.streaming.FastInfosetWriterFactoryImpl;

// FI requires our implementation of SAAJ
import com.sun.xml.messaging.saaj.soap.MessageImpl;

/**
 * @author JAX-RPC Development Team
 */
public class CallInvokerImpl
        extends StreamingSender
        implements CallInvoker, CallPropertyConstants {

    private static ClientTransportFactory defaultTransportFactory = null;

    private static final String BASIC_CALL_PROPERTY =
            "com.sun.xml.rpc.client.dii.BasicCall";

    protected JAXRPCDeserializer faultDeserializer;
    protected JAXRPCDeserializer responseDeserializer;
    protected ClientTransportFactory transportFactory;
    protected ClientTransport clientTransport;

    protected String defaultEnvEncodingStyle = SOAPNamespaceConstants.ENCODING;
    protected String implicitEnvEncodingStyle = null;
    protected String[] additionalNamespaces = null;
 
    /**
     * Set to true if USE_FAST_INFOSET_PROPERTY is set in Call instance.
     * Default: false.
     */
    protected boolean useFastInfoset;

    /**
     * Set to true if ACCEPT_FAST_INFOSET_PROPERTY is set in Call instance.
     * Default: true.
     */
    protected boolean acceptFastInfoset;

    public static void setDefaultTransportFactory(ClientTransportFactory factory) {
        defaultTransportFactory = factory;
    }

    public CallInvokerImpl() {
        transportFactory = defaultTransportFactory;
    }

    public SOAPResponseStructure doInvoke(CallRequest callInfo,
                                          JAXRPCSerializer requestSerializer,
                                          JAXRPCDeserializer responseDeserializer,
                                          JAXRPCDeserializer faultDeserializer)
            throws Exception {

        this.responseDeserializer = responseDeserializer;
        this.faultDeserializer = faultDeserializer;

        BasicCall call = callInfo.call;
        
        initContentNegotiationState(call);
        
        //sets up request in InternalSoapMessage - the InternalSOAPMessage
        //is carried in StreamingSenderState
        StreamingSenderState state = setupRequest(callInfo, requestSerializer);

        //StreamingSender _send actually sends the
        //request over the wire
        _send(call.getTargetEndpointAddress(), state);

        //the response is returned carried by the state so we get
        //its value
        SOAPResponseStructure responseStruct = null;
        Object responseObject = state.getResponse().getBody().getValue();
        
        //for rpc/encoded the object may be store in SOAPDeserializationState
        //get the object instance
        if (responseObject instanceof SOAPDeserializationState) {
            responseStruct =
                    (SOAPResponseStructure)
                    ((SOAPDeserializationState) responseObject)
                    .getInstance();
        } else {
            responseStruct = (SOAPResponseStructure) responseObject;
        }

        return responseStruct;
    }

    public void doInvokeOneWay(CallRequest callInfo,
                               JAXRPCSerializer requestSerializer)
            throws Exception 
    {
        //with one way invocation there is not return
        BasicCall call = callInfo.call;
        initContentNegotiationState(call);
        StreamingSenderState state = setupRequest(callInfo, requestSerializer);
        _sendOneWay(call.getTargetEndpointAddress(), state);
    }

    /**
     * Get FI properties from Call instance and set internal state.
     */
    private void initContentNegotiationState(BasicCall call) {
        String value = (String) 
            call.getProperty(CallPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
        useFastInfoset = (value == "optimistic");
        acceptFastInfoset = (useFastInfoset || value == "pessimistic");                
    }
    
    /**
     * Overrides definition in StreamingSender to create a StreamingSenderState
     * object using the FI flags set in this object.
     */
    protected StreamingSenderState _start(HandlerChain handlerChain) {
        //create the SOAPMessageContext
        SOAPMessageContext messageContext = new SOAPMessageContext();
        ((HandlerChainImpl) handlerChain).addUnderstoodHeaders(
            _getUnderstoodHeaders());
        //create and return StreamingSenderState containing message context
        //and a handler chain 
        return new StreamingSenderState(messageContext, handlerChain, 
            useFastInfoset, acceptFastInfoset);
    }
    
    protected void _handleRuntimeExceptionInSend(RuntimeException rex)
            throws Exception {

        if (rex instanceof JAXRPCException) {
            throw rex;
        } else {
            super._handleRuntimeExceptionInSend(rex);
        }
    }
    
    private StreamingSenderState setupRequest(CallRequest callInfo,
                                              JAXRPCSerializer requestSerializer)
            throws Exception {

        BasicCall call = callInfo.call;

        String encodingStyle =
                (String) call.getProperty(CallPropertyConstants.ENCODING_STYLE_PROPERTY);
        String operationStyle =
                (String) call.getProperty(CallPropertyConstants.OPERATION_STYLE_PROPERTY);

        StreamingSenderState state = _start(call.getHandlerChain());
        
        // Set call in StreamingSenderState object for content negotiation
        state.setCall(call);

        //get the Empty InternalSoapRequest from the state
        InternalSOAPMessage request = state.getRequest();

        //for rpc/literal the namespace is the same as the
        //operation namespace
        if (isRPCLiteral(operationStyle, encodingStyle)) {
            setNamespaceDeclarations("ns0",
                    call.getOperationName().getNamespaceURI());
        }

        //for no encoding, that is literal, set the envelope
        //encoding styles
        if ("".equals(encodingStyle)) {
            setImplicitEnvelopeEncodingStyle("");
            setDefaultEnvelopeEncodingStyle(null);
        }
        //for rpc the body block info name is the operation name
        SOAPBlockInfo bodyBlock = null;
        if (isRPC(operationStyle, encodingStyle)) {
            bodyBlock = new SOAPBlockInfo(call.getOperationName());
        } else {
            bodyBlock = new SOAPBlockInfo(null);
        }
        //set the call request and serializer on the bodyBlock
        bodyBlock.setValue(callInfo.request);
        bodyBlock.setSerializer(requestSerializer);
        //set the bodyBlock on the SOAPMessage
        request.setBody(bodyBlock);

        SOAPMessageContext messageContext = state.getMessageContext();
        messageContext.setProperty(BASIC_CALL_PROPERTY, call);

        //for rpc/literal set the expected response qname
        if (isRPCLiteral(operationStyle, encodingStyle)) {
            messageContext.setProperty(RPC_LITERAL_RESPONSE_QNAME,
                    new QName(call.getOperationName().getNamespaceURI(),
                            call.getOperationName().getLocalPart() + "Response"));
        }
        return state;
    }

    protected void _preSendingHook(StreamingSenderState state)
            throws Exception {

        //sets up properties in the message context that are
        //used during the invocation
        BasicCall call =
                (BasicCall) state.getMessageContext().getProperty(BASIC_CALL_PROPERTY);
        SOAPMessageContext messageContext = state.getMessageContext();
        Object username = call.getProperty(USERNAME_PROPERTY);
        if (username != null) {
            messageContext.setProperty(USERNAME_PROPERTY, username);
        }
        Object password = call.getProperty(PASSWORD_PROPERTY);
        if (password != null) {
            messageContext.setProperty(PASSWORD_PROPERTY, password);
        }
        Object endpoint = call.getProperty(ENDPOINT_ADDRESS_PROPERTY);
        if (endpoint != null) {
            messageContext.setProperty(ENDPOINT_ADDRESS_PROPERTY, endpoint);
        }
        Object operation = call.getProperty(OPERATION_STYLE_PROPERTY);
        if (operation != null) {
            messageContext.setProperty(OPERATION_STYLE_PROPERTY, operation);
        }
        Boolean isSOAPActionUsed =
                (Boolean) call.getRequiredProperty(SOAPACTION_USE_PROPERTY);
        if (isSOAPActionUsed.booleanValue()) {
            messageContext.setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY,
                    call.getRequiredProperty(SOAPACTION_URI_PROPERTY));
        }
        Object encoding = call.getProperty(ENCODING_STYLE_PROPERTY);
        if (encoding != null) {
            messageContext.setProperty(ENCODING_STYLE_PROPERTY, encoding);
        }

        Object verification = call.getProperty(HOSTNAME_VERIFICATION_PROPERTY);
        if (verification != null) {
            messageContext.setProperty(HOSTNAME_VERIFICATION_PROPERTY,
                    verification);
        }

        Object maintainSession = call.getProperty(SESSION_MAINTAIN_PROPERTY);
        if (maintainSession != null) {
            messageContext.setProperty(SESSION_MAINTAIN_PROPERTY,
                    maintainSession);
        }
        if (maintainSession != null && maintainSession.equals(Boolean.TRUE)) {
            Object cookieJar =
                    call.getProperty(StubPropertyConstants.HTTP_COOKIE_JAR);
            if (cookieJar != null)
                messageContext.setProperty(StubPropertyConstants.HTTP_COOKIE_JAR,
                        cookieJar);
        }
    }

    protected void _postSendingHook(StreamingSenderState state)
            throws Exception {

        //properties that must be maintained among invocations
        BasicCall call =
                (BasicCall) state.getMessageContext().getProperty(BASIC_CALL_PROPERTY);
        Object maintainSession = call.getProperty(SESSION_MAINTAIN_PROPERTY);
        if (maintainSession != null && maintainSession.equals(Boolean.TRUE)) {
            Object cookieJar =
                    call.getProperty(StubPropertyConstants.HTTP_COOKIE_JAR);
            if (cookieJar == null) {
                SOAPMessageContext messageContext = state.getMessageContext();
                cookieJar =
                        messageContext.getProperty(StubPropertyConstants.HTTP_COOKIE_JAR);
                call.setProperty(StubPropertyConstants.HTTP_COOKIE_JAR,
                        cookieJar);
            }
        }
        
        /*
         * Pessimistic content negotiation: If request in XML and reply in
         * FI, then switch to FI in subsequent calls.
         */
        MessageImpl response = (MessageImpl) state.getResponse().getMessage();
        if (!useFastInfoset && response.isFastInfoset()) {
            state.getCall().setProperty(
                CallPropertyConstants.CONTENT_NEGOTIATION_PROPERTY,
                "optimistic");
        }
    }

    public ClientTransportFactory _getTransportFactory() {
        if (transportFactory == null) {
            transportFactory = new HttpClientTransportFactory();
        }

        return transportFactory;
    }

    public void _setTransportFactory(ClientTransportFactory factory) {
        transportFactory = factory;
        clientTransport = null;
    }

    public ClientTransport _getTransport() {
        if (clientTransport == null) {
            clientTransport = _getTransportFactory().create();
        }

        return clientTransport;
    }

    //rpc lit adds responseQname
    protected void _readFirstBodyElement(XMLReader bodyReader,
                                         SOAPDeserializationContext deserializationContext,
                                         StreamingSenderState state)
            throws Exception {

        String operationStyle =
                (String) state.getMessageContext().getProperty(OPERATION_STYLE_PROPERTY);
        String encoding =
                (String) state.getMessageContext().getProperty(ENCODING_STYLE_PROPERTY);
        QName responseQName = null;
        if (isRPCLiteral(operationStyle, encoding))
            responseQName =
                    (QName) state.getMessageContext().getProperty(RPC_LITERAL_RESPONSE_QNAME);

        //deserialize response
        Object responseStructObj =
                getResponseDeserializer().deserialize(responseQName,
                        bodyReader,
                        deserializationContext);

        //put bodyBlock in the state response object
        SOAPBlockInfo bodyBlock = new SOAPBlockInfo(responseQName);
        bodyBlock.setValue(responseStructObj);
        state.getResponse().setBody(bodyBlock);

    }

    protected JAXRPCDeserializer getFaultDeserializer() {
        return faultDeserializer;
    }

    protected JAXRPCDeserializer getResponseDeserializer() {
        return responseDeserializer;
    }

    //used by StreamingSender
    public String _getDefaultEnvelopeEncodingStyle() {
        return defaultEnvEncodingStyle;
    }

    void setDefaultEnvelopeEncodingStyle(String style) {
        defaultEnvEncodingStyle = style;
    }

    public void setImplicitEnvelopeEncodingStyle(String style) {
        implicitEnvEncodingStyle = style;
    }

    public String _getImplicitEnvelopeEncodingStyle() {
        return implicitEnvEncodingStyle;
    }

    protected String[] _getNamespaceDeclarations() {
        return additionalNamespaces;
    }

    protected void setNamespaceDeclarations(String pre, String name) {
        additionalNamespaces = new String[]{pre, name};
    }

    //utility methods
    private boolean isDocumentLiteral(String operationStyle,
                                      String encodingStyle) {
        return (
                ("document".equalsIgnoreCase(operationStyle))
                && ("".equals(encodingStyle)));
    }

    private boolean isRPCLiteral(String operationStyle, String encodingStyle) {
        return (
                ("rpc".equalsIgnoreCase(operationStyle))
                && ("".equals(encodingStyle)));
    }

    private boolean isRPC(String operationStyle, String encodingStyle) {
        return ("rpc".equalsIgnoreCase(operationStyle));
    }
    
    
    /**
     * Overrides definition in StreamingSender to return an FI factory 
     * instance when property is set on the stub. The method 
     * _getXMLReaderFactory() does not need to be redefined since SAAJ 
     * already returns an FastInfosetSource.
     */
    protected XMLWriterFactory _getXMLWriterFactory() {
        return useFastInfoset ?
            (XMLWriterFactory) FastInfosetWriterFactoryImpl.newInstance() :
            (XMLWriterFactory) XMLWriterFactoryImpl.newInstance();
    }
}
