/*
 * $Id: JavaEnumerationEntry.java,v 1.1 2006-04-12 20:34:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.model.java;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JavaEnumerationEntry {
    
    public JavaEnumerationEntry() {}
    
    public JavaEnumerationEntry(String name, Object value,
        String literalValue) {
            
        this.name = name;
        this.value = value;
        this.literalValue = literalValue;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String s) {
        name = s;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object obj) {
        value = obj;
    }
    
    public String getLiteralValue() {
        return literalValue;
    }
    
    public void setLiteralValue(String s) {
        literalValue = s;
    }
    
    private String name;
    private Object value;
    private String literalValue;
}
