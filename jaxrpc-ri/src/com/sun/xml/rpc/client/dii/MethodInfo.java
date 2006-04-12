/*
 * $Id: MethodInfo.java,v 1.1 2006-04-12 20:33:56 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
