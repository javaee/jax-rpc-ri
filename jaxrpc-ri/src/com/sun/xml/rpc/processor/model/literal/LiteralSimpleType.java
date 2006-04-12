/*
 * $Id: LiteralSimpleType.java,v 1.1 2006-04-12 20:32:44 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaSimpleType;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralSimpleType extends LiteralType {
    
    public LiteralSimpleType() {}
    
    public LiteralSimpleType(QName name) {
        this(name, null);
    }
    
    public LiteralSimpleType(QName name, JavaSimpleType javaType) {
        super(name, javaType);
    }
    
    public LiteralSimpleType(QName name, JavaSimpleType javaType,
        boolean nillable) {
            
        super(name, javaType);
        setNillable(nillable);
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
}
