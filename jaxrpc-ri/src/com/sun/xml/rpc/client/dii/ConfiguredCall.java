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
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.handler.HandlerRegistry;

import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.util.Holders;

/**
 * @author JAX-RPC Development Team
 */

public class ConfiguredCall extends BasicCall {
    ServiceInfo configuration;
    Method operationMethod = null;
    String methodName = null;
    QName portTypeName = EMPTY_QNAME;
    //boolean isProxy = false;

    public ConfiguredCall(InternalTypeMappingRegistry registry,
                          HandlerRegistry handlerRegistry,
                          ServiceInfo configuration) {
        super(registry, handlerRegistry);
        if (configuration == null) {
            throw new IllegalArgumentException("configuration not allowed to be null");
        }

        this.configuration = configuration;
    }

    public boolean isParameterAndReturnSpecRequired(QName operation) {
        return false;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
        configureCall();
    }

    public void setOperationName(QName operationName) {
        super.setOperationName(operationName);
        methodName = operationName.getLocalPart();
        configureCall();
    }

    public void setPortName(QName port) {
        super.setPortName(port);
        configureCall();
    }

    public void setOperationMethod(Method method) {
        operationMethod = method;
    }

    protected void configureCall() {
        configureCall(operationMethod);
    }

    protected void configureCall(Method method) {
        //method that configures the call with the operation information
        //and parameters
        if (readyToConfigure()) {
            PortInfo currentPort = configuration.getPortInfo(getPortName());

            setPortTypeName(currentPort.getPortTypeName());

            MethodInfo currentMethod = new MethodInfo(method);
            //look for the java method in the operation information
            Iterator eachOperation = currentPort.getOperations();
            boolean operationHasBeenFound = false;
            while (eachOperation.hasNext() && !operationHasBeenFound) {
                OperationInfo currentOperation =
                        (OperationInfo) eachOperation.next();

                //if the current method matches the current operation
                //from the operation information configure the call
                if (currentMethod.matches(methodName, currentOperation)) {
                    operationHasBeenFound = true;

                    doConfigureCall(currentMethod, currentOperation);
                }
            }

            if (!operationHasBeenFound) {
                throw new DynamicInvocationException("dii.port.does.not.contain.operation",
                        new Object[]{getPortName(), methodName});
            }
        }
    }

    protected void doConfigureCall(MethodInfo currentMethod,
                                   OperationInfo currentOperation) {

        //get the port information from the ServiceInformation
        PortInfo currentPort = configuration.getPortInfo(getPortName());

        //set the operation name and information on the supercall BasicCall
        super.setOperationName(currentOperation.getName());
        super.setOperationInfo(currentOperation);
        isOneWay = currentOperation.isOneWay();

        //it's very important to set the endpoint address on the call
        setTargetEndpointAddress(currentPort.getTargetEndpoint());

        //get all the parameter names
        String[] parameterNames = currentOperation.getParameterNames();
        //get all the parameter QNames
        QName[] parameterTypes = currentOperation.getParameterXmlTypes();
        //get all the parameter classes
        Class[] parameterClasses =
                currentMethod.getParameterTypes(parameterTypes.length);
        //get the parameter modes
        ParameterMode[] parameterModes = currentOperation.getParameterModes();
        //needed for doclit wrapped
        QName[] parameterXmlTypeQNames = currentOperation.getParameterXmlTypeQNames();


        for (int i = 0; i < parameterNames.length; i++) {
            String parameterName = parameterNames[i];
            QName parameterType =
                    parameterTypes != null ? parameterTypes[i] : null;
            Class parameterClass =
                    Holders.stripHolderClass(parameterClasses[i]);
            ParameterMode mode = parameterModes[i];
            QName parameterXmlTypeQName = null;
            if (i < parameterXmlTypeQNames.length)
                parameterXmlTypeQName = parameterXmlTypeQNames[i];
            ParameterMemberInfo[] members = null;
            members =
                    currentOperation.getMemberInfo(i);

            doAddParameter(parameterName, parameterType, parameterXmlTypeQName, parameterClass, members, mode);
        }

        Class javaReturn = null;
        //set the return type on the BasicCall
        if (currentMethod != null) {
            javaReturn = currentMethod.getReturnType();
            if (javaReturn != null) {
                if (javaReturn.getName().equalsIgnoreCase("void"))
                    javaReturn = null;
            }
        }
        if (javaReturn == null)
            javaReturn = currentOperation.getReturnClass();
        doSetReturnType(currentOperation.getReturnXmlType(), javaReturn);
        //setReturnTypeQName(currentOperation.getReturnTypeQName());
        setReturnXmlTypeQName(currentOperation.getReturnXmlTypeQName());
        setReturnTypeName(currentOperation.getReturnClassName());
        setReturnParameterInfos(currentOperation.getReturnMembers());
        Iterator eachPropertyKey = currentOperation.getPropertyKeys();
        while (eachPropertyKey.hasNext()) {
            String currentKey = (String) eachPropertyKey.next();
            setProperty(currentKey, currentOperation.getProperty(currentKey));
        }
    }

    protected boolean readyToConfigure() {
        return (
                (getPortName() != null)
                && (!getPortName().equals(EMPTY_QNAME))
                && (methodName != null)
                && (!methodName.equals("")));
    }
}
