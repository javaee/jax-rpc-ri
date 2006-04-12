/*
 * $Id: JavaCustomType.java,v 1.1 2006-04-12 20:34:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.model.java;

import com.sun.xml.rpc.processor.config.TypeMappingInfo;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JavaCustomType extends JavaType {
    
    public JavaCustomType() {}
    
    public JavaCustomType(String name) {
        super(name, true, null);
    }
    
    public JavaCustomType(String name, TypeMappingInfo typeMappingInfo) {
        super(name, true, null);
        this.typeMappingInfo = typeMappingInfo;
    }
    
    public TypeMappingInfo getTypeMappingInfo() {
        return typeMappingInfo;
    }
    
    public void setTypeMappingInfo(TypeMappingInfo typeMappingInfo) {
        this.typeMappingInfo = typeMappingInfo;
    }
    
    private TypeMappingInfo typeMappingInfo;
}
