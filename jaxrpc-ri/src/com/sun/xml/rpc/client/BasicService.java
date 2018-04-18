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
 * $Id: BasicService.java,v 1.3 2007-07-13 23:35:55 ofung Exp $
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

package com.sun.xml.rpc.client;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerRegistry;

import com.sun.xml.rpc.client.dii.BasicCall;
import com.sun.xml.rpc.client.dii.CallEx;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistryImpl;
import com.sun.xml.rpc.encoding.SerializerConstants;
import com.sun.xml.rpc.encoding.StandardTypeMappings;
import com.sun.xml.rpc.encoding.TypeMappingImpl;
import com.sun.xml.rpc.encoding.TypeMappingRegistryImpl;
import com.sun.xml.rpc.naming.ServiceReferenceResolver;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 * @author JAX-RPC Development Team
 */

public class BasicService
    implements Service, SerializerConstants, Referenceable {
    protected static final String DEFAULT_OPERATION_STYLE = "rpc";
    protected QName name;
    protected List ports;
    protected TypeMappingRegistry typeRegistry;
    protected InternalTypeMappingRegistry internalTypeRegistry;

    private HandlerRegistry handlerRegistry;

    protected QName[] getPortsAsArray() {
        return (QName[]) ports.toArray(new QName[ports.size()]);
    }

    protected void init(QName name, TypeMappingRegistry registry) {
        init();
        this.name = name;
        this.typeRegistry = registry;
        this.internalTypeRegistry =
            new InternalTypeMappingRegistryImpl(registry);
    }

    protected void init() {
        this.ports = new ArrayList();
        this.handlerRegistry = null;
    }

    //implementation for javax.xml.rpc.Service
    public BasicService(QName name, TypeMappingRegistry registry) {
        init(name, registry);
    }

    public BasicService(QName name) {
        init(name, createStandardTypeMappingRegistry());
    }

    public BasicService(QName name, QName[] ports) {
        this(name);
        addPorts(ports);
    }

    public BasicService(
        QName name,
        QName[] ports,
        TypeMappingRegistry registry) {
        this(name, registry);
        addPorts(ports);
    }

    protected void addPorts(QName[] ports) {
        if (ports != null) {
            for (int i = 0; i < ports.length; ++i) {
                addPort(ports[i]);
            }
        }
    }

    public BasicService(QName name, Iterator eachPort) {
        this(name);
        while (eachPort.hasNext()) {
            addPort((QName) eachPort.next());
        }
    }

    protected void addPort(QName port) {
        ports.add(port);
    }

    //if there is no wsdl available, exception is thrown
    public Remote getPort(Class portInterface) throws ServiceException {
        throw noWsdlException();
    }

    protected ServiceException noWsdlException() {
        return new ServiceExceptionImpl("dii.service.no.wsdl.available");
    }

    public Remote getPort(QName portName, Class portInterface)
        throws ServiceException {

        throw noWsdlException();
    }

    public Call[] getCalls(QName portName) throws ServiceException {
        throw noWsdlException();
    }

    //variations of javax.xml.rpc.Service.createCall()
    public Call createCall(QName portName) throws ServiceException {
        if (!ports.contains(portName)) {
            addPort(portName);
        }
        CallEx newCall = (CallEx) createCall();
        newCall.setPortName(portName);

        return newCall;
    }

    public Call createCall(QName portName, String operationName)
        throws ServiceException {
        return createCall(portName, new QName(operationName));
    }

    public Call createCall(QName portName, QName operationName)
        throws ServiceException {
        CallEx newCall = (CallEx) createCall(portName);
        newCall.setOperationName(operationName);

        return newCall;
    }

    public Call createCall() throws ServiceException {
        BasicCall call =
            new BasicCall(internalTypeRegistry, getHandlerRegistry());
        call.setProperty(
            Call.OPERATION_STYLE_PROPERTY,
            DEFAULT_OPERATION_STYLE);

        return call;
    }

    public QName getServiceName() {
        return name;
    }

    public Iterator getPorts() throws ServiceException {
        if (ports.size() == 0)
            throw noWsdlException();
        return ports.iterator();
    }

    public java.net.URL getWSDLDocumentLocation() {
        return null;
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        //if (registryCreationException != null) {
        //    throw registryCreationException;
        //}
        return typeRegistry;
    }

    public HandlerRegistry getHandlerRegistry() {
        if (handlerRegistry == null) {
            handlerRegistry = new HandlerRegistryImpl();
        }

        return handlerRegistry;
    }

    public static TypeMappingRegistry createStandardTypeMappingRegistry() {
        TypeMappingRegistry registry = new TypeMappingRegistryImpl();

        /** Register SOAP 1.1 and SOAP 1.2 mappings */
        try {

            TypeMapping soapMappings = createSoapMappings(SOAPVersion.SOAP_11);
            registry.register(
                com.sun.xml.rpc.encoding.soap.SOAPConstants.URI_ENCODING,
                soapMappings);

            TypeMapping soap12Mappings =
                createSoapMappings(SOAPVersion.SOAP_12);
            registry.register(
                com.sun.xml.rpc.encoding.soap.SOAP12Constants.URI_ENCODING,
                soap12Mappings);

            //create literal type mappings
            TypeMapping literalMappings = createLiteralMappings();
            registry.register("", literalMappings);

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return registry;
    }

    protected static TypeMapping createSoapMappings() {
        return createSoapMappings(SOAPVersion.SOAP_11);
    }

    protected static TypeMapping createSoapMappings(SOAPVersion ver) {
        TypeMappingImpl soapMappings =
            new TypeMappingImpl(StandardTypeMappings.getSoap(ver));

        if (ver == SOAPVersion.SOAP_11)
            soapMappings.setSupportedEncodings(
                new String[]{
                    com
                    .sun
                    .xml
                    .rpc
                    .encoding
                    .soap
                    .SOAPConstants
                    .URI_ENCODING});
        else if (ver == SOAPVersion.SOAP_12)
            soapMappings.setSupportedEncodings(
                new String[]{
                    com
                    .sun
                    .xml
                    .rpc
                    .encoding
                    .soap
                    .SOAP12Constants
                    .URI_ENCODING});

        return soapMappings;
    }

    protected static TypeMapping createLiteralMappings() {
        TypeMappingImpl rpcLiteralMappings =
            new TypeMappingImpl(StandardTypeMappings.getRPCLiteral());

        rpcLiteralMappings.setSupportedEncodings(new String[]{""});

        return rpcLiteralMappings;
    }

    protected static class HandlerRegistryImpl implements HandlerRegistry {
        Map handlerChainsForPorts;

        public HandlerRegistryImpl() {
            init();
        }

        protected void init() {
            handlerChainsForPorts = new HashMap();
        }

        public List getHandlerChain(QName portName) {
            if (handlerChainsForPorts.get(portName) == null) {
                setHandlerChain(
                    portName,
                    new com.sun.xml.rpc.client.HandlerChainInfoImpl());
            }
            return (List) handlerChainsForPorts.get(portName);
        }

        public void setHandlerChain(QName portName, List chainInfo) {
            handlerChainsForPorts.put(portName, chainInfo);
        }

    }

    public Reference getReference() throws NamingException {
        Reference reference =
            new Reference(
                getClass().getName(),
                "com.sun.xml.rpc.naming.ServiceReferenceResolver",
                null);
        String serviceName = ServiceReferenceResolver.registerService(this);
        reference.add(new StringRefAddr("ServiceName", serviceName));
        return reference;
    }
}
