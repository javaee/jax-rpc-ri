/*
 * $Id: PGraph.java,v 1.1 2006-04-12 20:34:35 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.processor.model.exporter;

import javax.xml.namespace.QName;

/**
 * @author JAX-RPC Development Team
 */
public class PGraph {
    
    public PGraph() {}
    
    public PObject getRoot() {
        return root;
    }
    
    public void setRoot(PObject o) {
        root = o;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String s) {
        version = s;
    }
    
    public QName getName() {
        return name;
    }
    
    public void setName(QName n) {
        name = n;
    }
    
    private QName name;
    private PObject root;
    private String version;
}

