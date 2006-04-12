/*
 * $Id: PObject.java,v 1.1 2006-04-12 20:34:34 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.processor.model.exporter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.rpc.util.NullIterator;

/**
 * @author JAX-RPC Development Team
 */
public class PObject {
    
    public PObject() {}
    
    public String getType() {
        return type;
    }
    
    public void setType(String s) {
        type = s;
    }
    
    public Object getProperty(String name) {
        if (properties == null) {
            return null;
        } else {
            return properties.get(name);
        }
    }
    
    public void setProperty(String name, Object value) {
        if (properties == null) {
            properties = new HashMap();
        }
        properties.put(name, value);
    }
    
    public Iterator getProperties() {
        if (properties == null) {
            return NullIterator.getInstance();
        } else {
            return properties.values().iterator();
        }
    }
    
    public Iterator getPropertyNames() {
        if (properties == null) {
            return NullIterator.getInstance();
        } else {
            return properties.keySet().iterator();
        }
    }
    
    private Map properties;
    private String type;
}

