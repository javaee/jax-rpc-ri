/*
 * $Id: NamespaceMappingRegistryInfo.java,v 1.1 2006-04-12 20:34:49 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
