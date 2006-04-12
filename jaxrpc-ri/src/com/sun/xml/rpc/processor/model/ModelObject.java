/*
 * $Id: ModelObject.java,v 1.1 2006-04-12 20:33:07 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.rpc.util.NullIterator;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class ModelObject
    implements com.sun.xml.rpc.spi.model.ModelObject{
    
    public abstract void accept(ModelVisitor visitor) throws Exception;
    
    public Object getProperty(String key) {
        if (_properties == null) {
            return null;
        }
        return _properties.get(key);
    }
    
    public void setProperty(String key, Object value) {
        if (value == null) {
            removeProperty(key);
            return;
        }
        
        if (_properties == null) {
            _properties = new HashMap();
        }
        _properties.put(key, value);
    }
    
    public void removeProperty(String key) {
        if (_properties != null) {
            _properties.remove(key);
        }
    }
    
    public Iterator getProperties() {
        if (_properties == null) {
            return NullIterator.getInstance();
        } else {
            return _properties.keySet().iterator();
        }
    }
    
    /* serialization */
    public Map getPropertiesMap() {
        return _properties;
    }
    
    /* serialization */
    public void setPropertiesMap(Map m) {
        _properties = m;
    }
    
    private Map _properties;
}
