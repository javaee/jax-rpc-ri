/*
 * $Id: MethodInfo.java,v 1.2 2006-04-13 01:26:47 ofung Exp $
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

/**
 * @author JAX-RPC Development Team
 */

public class MethodInfo {
    private Method method = null;

    //hold actual method information extracted from
    //the inspected java interface
    //this information is used to do a sanity check
    //between the operation information obtained from
    //examining the wsdl and the java interface
    //information
    public MethodInfo() {
    }

    public MethodInfo(Method method) {
        this.method = method;
    }

    public int getModifiers() {
        if (method != null) {
            return method.getModifiers();
        }

        return Modifier.PRIVATE;
    }

    public String getName() {
        if (method != null) {
            return method.getName();
        }

        return "";
    }

    public Class[] getParameterTypes() {
        if (method != null) {
            return method.getParameterTypes();
        }

        return getParameterTypes(0);
    }

    public Class[] getParameterTypes(int parameterCount) {
        if (method != null) {
            return method.getParameterTypes();
        }

        return new Class[parameterCount];
    }

    public int getParameterCount() {
        if (method != null) {
            return method.getParameterTypes().length;
        }

        return -1;
    }

    public Class getReturnType() {
        if (method != null) {
            return method.getReturnType();
        }

        return null;
    }

    public Method getMethodObject() {
        return method;
    }

    public boolean matches(String methodName, OperationInfo operation) {
        if (!operation.getName().getLocalPart().equals(methodName)) {
            return false;
        }
        if (method != null) {
            if (getParameterCount() != operation.getParameterCount()) {
                return false;
            }
            // TODO: support overloading based on parameter types
        }
        return true;
    }
}
