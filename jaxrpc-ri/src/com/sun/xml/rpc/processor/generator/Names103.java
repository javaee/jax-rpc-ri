/*
 * $Id: Names103.java,v 1.2 2006-04-13 01:28:50 ofung Exp $
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
