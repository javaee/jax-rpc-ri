/*
 * $Id: Names103.java,v 1.1 2006-04-12 20:33:42 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.java.JavaType;

/**
 * @author JAX-RPC Development Team
 *
 * JAXRPC 1.0.3 Names class
 */
public class Names103 extends Names {
    public String holderClassName(Port port, JavaType type) {
        if (type.getHolderName() != null)
            return type.getHolderName();
        return holderClassName(port, type.getName());
    }

    protected String holderClassName(Port port, String typeName) {
        String holderTypeName = (String) holderClassNames.get(typeName);
        if (holderTypeName == null) {
            // not a built-in holder class
            String className = port.getJavaInterface().getName();
            String packageName = getPackageName(className);
            if (packageName.length() > 0) {
                packageName += ".holders.";
            } else {
                packageName = "holders.";
            }
            //                if (!(typeName.startsWith("java.") || typeName.startsWith("javax."))) {
            if (!isInJavaOrJavaxPackage(typeName)) {
                typeName = stripQualifier(typeName);
            }
            int idx = typeName.indexOf(BRACKETS);
            while (idx > 0) {
                typeName =
                    typeName.substring(0, idx)
                        + ARRAY
                        + typeName.substring(idx + 2);
                idx = typeName.indexOf(BRACKETS);
            }
            holderTypeName = packageName + typeName + HOLDER_SUFFIX;
        }
        return holderTypeName;
    }

}
