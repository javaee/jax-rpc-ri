/*
 * $Id: StubBase.java,v 1.1 2006-04-12 20:35:22 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.Stub;
import javax.xml.rpc.handler.HandlerChain;

import com.sun.xml.rpc.client.http.HttpClientTransportFactory;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.SerializerConstants;
import com.sun.xml.rpc.encoding._Initializable;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.client.StubPropertyConstants;

import com.sun.xml.rpc.streaming.*;

// FI requires our implementation of SAAJ
import com.sun.xml.messaging.saaj.soap.MessageImpl;

/**
 * <p> A base class for stubs. </p>
 *
 * @author JAX-RPC Development Team
 */
public abstract class StubBase
    extends StreamingSender
    implements
        Stub,
        SerializerConstants,
        _Initializable,
        com.sun.xml.rpc.spi.runtime.StubBase {

    protected static final Set _recognizedProperties;

    private Map _properties = new HashMap();
    private boolean _mustInitialize = true;
    private ClientTransport _transport;
    private ClientTransportFactory _transportFactory;

    protected HandlerChain _handlerChain;

    static {
        Set temp = new HashSet();
        temp.add(USERNAME_PROPERTY);
        temp.add(PASSWORD_PROPERTY);
        temp.add(ENDPOINT_ADDRESS_PROPERTY);
        temp.add(SESSION_MAINTAIN_PROPERTY);
        temp.add(StubPropertyConstants.OPERATION_STYLE_PROPERTY);
        temp.add(StubPropertyConstants.ENCODING_STYLE_PROPERTY);
        temp.add(StubPropertyConstants.HTTP_COOKIE_JAR);
        temp.add(StubPropertyConstants.HOSTNAME_VERIFICATION_PROPERTY);
        temp.add(StubPropertyConstants.REDIRECT_REQUEST_PROPERTY);
        temp.add(StubPropertyConstants.SECURITY_CONTEXT);
//        temp.add(StubPropertyConstants.ATTACHMENT_CONTEXT);
        temp.add(StubPropertyConstants.SET_ATTACHMENT_PROPERTY);
        temp.add(StubPropertyConstants.GET_ATTACHMENT_PROPERTY);
        temp.add(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
        _recognizedProperties = Collections.unmodifiableSet(temp);
    }

    //implementation for javax.api.xml.rpc.Stub
    protected StubBase(HandlerChain handlerChain) {
        _handlerChain = handlerChain;        
        ContentNegotiationProperties.initFromSystemProperties(_properties);
    }
    
    // Redefined to propagate FI property
    protected StreamingSenderState _start(HandlerChain handlerChain) {       
        //create the SOAPMessageContext
        SOAPMessageContext messageContext = new SOAPMessageContext();        
        
        ((HandlerChainImpl) handlerChain).addUnderstoodHeaders(_getUnderstoodHeaders());
        
        //create and return StreamingSenderState containing message context
        //and a handler chain 
        return new StreamingSenderState(messageContext, handlerChain, 
            useFastInfoset(), acceptFastInfoset());
    }

    public HandlerChain _getHandlerChain() {
        if (_handlerChain == null) {

            // create empty handler chain so that client code can add handlers
            _handlerChain = new HandlerChainImpl(new ArrayList());
        }
        return _handlerChain;
    }

    public boolean useFastInfoset() {
        Object value =
            _properties.get(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
        return (value == "optimistic");
    }
    
    public boolean acceptFastInfoset() {
        Object value =
            _properties.get(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
        return (value != "none");
    }
    
    public void _setProperty(String name, Object value) {
        if (!_recognizedProperties.contains(name)) {
            throw new JAXRPCException(
                "Stub does not recognize property: " + name);
        }
        
        // Internalize value of CONTENT_NEGOTIATION_PROPERTY
        if (name.equals(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY)) {
            _properties.put(name, ((String) value).intern());
        }
        else {
            _properties.put(name, value);
        }
    }

    public Object _getProperty(String name) {
        if (!_recognizedProperties.contains(name)) {
            throw new JAXRPCException(
                "Stub does not recognize property: " + name);
        }

        return _properties.get(name);
    }

    public Iterator _getPropertyNames() {
        return _properties.keySet().iterator();
    }

    public void _initialize(InternalTypeMappingRegistry registry)
        throws Exception {
        _mustInitialize = false;
    }

    protected void _preSendingHook(StreamingSenderState state)
        throws Exception {
        if (_mustInitialize) {
            throw new SenderException("sender.stub.notInitialized");
        }
        //presending hook sets up the SOAPMessage context with properties
        //that may be used during invocation or to preserve session/cookie
        //state between invocations
        SOAPMessageContext messageContext = state.getMessageContext();
        Object userName = _getProperty(USERNAME_PROPERTY);
        if (userName != null)
            messageContext.setProperty(USERNAME_PROPERTY, userName);
        Object password = _getProperty(PASSWORD_PROPERTY);
        if (password != null)
            messageContext.setProperty(PASSWORD_PROPERTY, password);
        Object address = _getProperty(ENDPOINT_ADDRESS_PROPERTY);
        if (address != null)
            messageContext.setProperty(ENDPOINT_ADDRESS_PROPERTY, address);
        Object verification =
            _getProperty(StubPropertyConstants.HOSTNAME_VERIFICATION_PROPERTY);
        if (verification != null)
            messageContext.setProperty(
                StubPropertyConstants.HOSTNAME_VERIFICATION_PROPERTY,
                verification);
        Object style =
            _getProperty(StubPropertyConstants.OPERATION_STYLE_PROPERTY);
        if (style != null)
            messageContext.setProperty(
                StubPropertyConstants.OPERATION_STYLE_PROPERTY,
                style);
        Object encoding =
            _getProperty(StubPropertyConstants.ENCODING_STYLE_PROPERTY);
        if (encoding != null)
            messageContext.setProperty(
                StubPropertyConstants.ENCODING_STYLE_PROPERTY,
                encoding);
        Object maintainSession = _getProperty(SESSION_MAINTAIN_PROPERTY);
        if (maintainSession != null)
            messageContext.setProperty(
                SESSION_MAINTAIN_PROPERTY,
                maintainSession);
        if (maintainSession != null && maintainSession.equals(Boolean.TRUE)) {
            Object cookieJar =
                _getProperty(StubPropertyConstants.HTTP_COOKIE_JAR);
            if (cookieJar != null)
                messageContext.setProperty(
                    StubPropertyConstants.HTTP_COOKIE_JAR,
                    cookieJar);
        }
        Object securityContext =
            _getProperty(StubPropertyConstants.SECURITY_CONTEXT);
        if (securityContext != null) {
            messageContext.setProperty(
                StubPropertyConstants.SECURITY_CONTEXT,
                securityContext);
        }        
    }

    protected void _postSendingHook(StreamingSenderState state)
        throws Exception 
    {    	
        //post sending hook examines properties from the
        //streaming sender state and matintains session state or cookie state
        //if required
        Object maintainSession = _getProperty(SESSION_MAINTAIN_PROPERTY);
        if (maintainSession != null && maintainSession.equals(Boolean.TRUE)) {
            Object cookieJar =
                _getProperty(StubPropertyConstants.HTTP_COOKIE_JAR);
            if (cookieJar == null) {
                SOAPMessageContext messageContext = state.getMessageContext();
                cookieJar =
                    messageContext.getProperty(
                        StubPropertyConstants.HTTP_COOKIE_JAR);
                _setProperty(StubPropertyConstants.HTTP_COOKIE_JAR, cookieJar);
            }
        }        
        
        /*
         * Pessimistic content negotiation: If request in XML and reply in
         * FI, then switch to FI in subsequent calls.
         */
        MessageImpl response = (MessageImpl) state.getResponse().getMessage();
        if (!useFastInfoset() && response.isFastInfoset()) {
            _properties.put(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY,
                            "optimistic");
        }
    }

    protected ClientTransport _getTransport() {
        if (_transport == null) {
            _transport = _getTransportFactory().create();
        }
        return _transport;
    }

    public ClientTransportFactory _getTransportFactory() {
        if (_transportFactory == null) {
            _transportFactory = new HttpClientTransportFactory();
        }

        return _transportFactory;
    }

    public void _setTransportFactory(
        com.sun.xml.rpc.spi.runtime.ClientTransportFactory f) {
        _setTransportFactory((ClientTransportFactory) f);
    }

    public void _setTransportFactory(ClientTransportFactory f) {
        _transportFactory = (ClientTransportFactory) f;
        _transport = null;
    }

    /**
     * Overrides definition in StreamingSender to return an FI factory 
     * instance when property is set on the stub. The method 
     * _getXMLReaderFactory() does not need to be redefined since SAAJ 
     * already returns an FastInfosetSource.
     */
    protected XMLWriterFactory _getXMLWriterFactory() {
        Object value = 
            _getProperty(StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
        return (value == "optimistic") ?
            (XMLWriterFactory) FastInfosetWriterFactoryImpl.newInstance() :
            (XMLWriterFactory) XMLWriterFactoryImpl.newInstance();
    }

}
