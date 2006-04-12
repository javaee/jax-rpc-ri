/*
 * $Id: RmiModelInfo.java,v 1.1 2006-04-12 20:34:49 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.rmi.RmiModeler;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RmiModelInfo extends ModelInfo {

    public RmiModelInfo() {
        interfaces = new ArrayList();
    }

    public String getTargetNamespaceURI() {
        return targetNamespaceURI;
    }

    public void setTargetNamespaceURI(String s) {
        targetNamespaceURI = s;
    }

    public String getTypeNamespaceURI() {
        return typeNamespaceURI;
    }

    public void setTypeNamespaceURI(String s) {
        typeNamespaceURI = s;
    }

    public String getJavaPackageName() {
        return javaPackageName;
    }

    public void setJavaPackageName(String s) {
        javaPackageName = s;
    }

    public void add(RmiInterfaceInfo i) {
        interfaces.add(i);
        i.setParent(this); 
    }

    public Iterator getInterfaces() {
        return interfaces.iterator();
    }

    protected Modeler getModeler(Properties options) {
        return new RmiModeler(this, options);
    }

    private String targetNamespaceURI;
    private String typeNamespaceURI;
    private List interfaces;
    private String javaPackageName;
    private HandlerChainInfo clientHandlerChainInfo;
    private HandlerChainInfo serverHandlerChainInfo;
}
