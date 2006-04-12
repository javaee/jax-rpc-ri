/*
 * $Id: DynamicProxyBuilder.java,v 1.1 2006-04-12 20:33:58 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.rmi.Remote;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import javax.xml.rpc.handler.HandlerRegistry;

import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;

/**
 * @author JAX-RPC Development Team
 */
public class DynamicProxyBuilder {
    protected InternalTypeMappingRegistry internalTypeRegistry;
    protected HandlerRegistry handlerRegistry;
    protected ServiceInfo configuration;

    public DynamicProxyBuilder(
        InternalTypeMappingRegistry internalTypeRegistry,
        HandlerRegistry handlerRegistry,
        ServiceInfo configuration) {

        this.internalTypeRegistry = internalTypeRegistry;
        this.handlerRegistry = handlerRegistry;
        this.configuration = configuration;

    }

    public Remote buildDynamicProxyFor(PortInfo portInfo, Class portInterface)
        throws ServiceException {

        CallInvocationHandler handler =
            new CallInvocationHandler(portInterface);
        handler._setProperty(
            Stub.ENDPOINT_ADDRESS_PROPERTY,
            portInfo.getTargetEndpoint());

        Method[] interfaceMethods = portInterface.getMethods();
        for (int i = 0; i < interfaceMethods.length; ++i) {
            Method currentMethod = interfaceMethods[i];

            if (Modifier.isPublic(currentMethod.getModifiers())) {
                ConfiguredCall methodCall =
                    new ConfiguredCall(
                        internalTypeRegistry,
                        handlerRegistry,
                        configuration);

                String methodName = currentMethod.getName();

                methodCall.setPortName(portInfo.getName());
                methodCall.setOperationMethod(currentMethod);
                methodCall.setMethodName(methodName);
                methodCall.setIsProxy(true);

                handler.addCall(currentMethod, methodCall);
            }
        }

        return (Remote) Proxy.newProxyInstance(
            portInterface.getClassLoader(),
            new Class[] { portInterface, Stub.class, Remote.class },
            handler);
    }
}
