/*
 * $Id: LiteralEnumerationType.java,v 1.1 2006-04-12 20:32:45 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaType;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralEnumerationType extends LiteralType {
    
    public LiteralEnumerationType() {}
    
    public LiteralEnumerationType(QName name, LiteralType baseType,
        JavaType javaType) {
            
        super(name, javaType);
        this.baseType = baseType;
    }
    
    public LiteralType getBaseType() {
        return baseType;
    }
    
    public void setBaseType(LiteralType t) {
        baseType = t;
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private LiteralType baseType;
}
