/*
 * $Id: NamespaceMappingRegistryInfo.java,v 1.2 2006-04-13 01:28:27 ofung Exp $
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

package com.sun.xml.rpc.processor.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class NamespaceMappingRegistryInfo
    implements com.sun.xml.rpc.spi.tools.NamespaceMappingRegistryInfo {

    public NamespaceMappingRegistryInfo() {
        namespaceMap = new HashMap();
        javaPackageNameMap = new HashMap();
    }

    public void addMapping(com.sun.xml.rpc.spi.tools.NamespaceMappingInfo i) {
        addMapping((NamespaceMappingInfo)i);
    }
    
    public void addMapping(NamespaceMappingInfo i) {
        namespaceMap.put(i.getNamespaceURI(), i);
        javaPackageNameMap.put(i.getJavaPackageName(), i);
    }

    public NamespaceMappingInfo getNamespaceMappingInfo(QName xmlType) {
        NamespaceMappingInfo i =
            (NamespaceMappingInfo) namespaceMap.get(xmlType.getNamespaceURI());
        return i;
    }

    public NamespaceMappingInfo getNamespaceMappingInfo(
        String javaPackageName) {
            
        NamespaceMappingInfo i =
            (NamespaceMappingInfo) javaPackageNameMap.get(javaPackageName);
        return i;
    }

    public Iterator getNamespaceMappings() {
        return namespaceMap.values().iterator();
    }
    
    private Map namespaceMap;
    private Map javaPackageNameMap;
}
