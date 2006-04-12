/*
 * $Id: BasicService.java,v 1.1 2006-04-12 20:35:22 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
