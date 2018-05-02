/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
