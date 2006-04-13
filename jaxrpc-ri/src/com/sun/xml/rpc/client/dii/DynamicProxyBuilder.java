/*
 * $Id: DynamicProxyBuilder.java,v 1.2 2006-04-13 01:26:46 ofung Exp $
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
