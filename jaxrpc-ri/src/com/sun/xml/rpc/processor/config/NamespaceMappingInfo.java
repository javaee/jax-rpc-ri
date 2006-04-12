/*
 * $Id: NamespaceMappingInfo.java,v 1.1 2006-04-12 20:34:49 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.config;

/**
 *
 * @author JAX-RPC Development Team
 */
public class NamespaceMappingInfo
    implements com.sun.xml.rpc.spi.tools.NamespaceMappingInfo {

    public NamespaceMappingInfo(String namespaceURI, String javaPackageName) {
        this.namespaceURI = namespaceURI;
        this.javaPackageName = javaPackageName;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getJavaPackageName() {
        return javaPackageName;
    }

    private String namespaceURI;
    private String javaPackageName;
}
