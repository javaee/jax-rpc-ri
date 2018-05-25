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
