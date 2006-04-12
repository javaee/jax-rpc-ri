/*
 * $Id: JavaEnumerationType.java,v 1.1 2006-04-12 20:34:13 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.model.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JavaEnumerationType extends JavaType {
    
    public JavaEnumerationType() {}
    
    public JavaEnumerationType(String name, JavaType baseType,
        boolean present) {
            
        super(name, present, "null");
        this.baseType = baseType;
    }
    
    public JavaType getBaseType() {
        return baseType;
    }
    
    public void setBaseType(JavaType t) {
        baseType = t;
    }
    
    public void add(JavaEnumerationEntry e) {
        entries.add(e);
    }
    
    public Iterator getEntries() {
        return entries.iterator();
    }
    
    public int getEntriesCount() {
        return entries.size();
    }
    
    /* serialization */
    public List getEntriesList() {
        return entries;
    }
    
    /* serialization */
    public void setEntriesList(List l) {
        entries = l;
    }
    
    private List entries = new ArrayList();
    private JavaType baseType;
}
