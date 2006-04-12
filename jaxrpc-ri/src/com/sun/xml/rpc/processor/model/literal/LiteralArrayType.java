/*
 * $Id: LiteralArrayType.java,v 1.1 2006-04-12 20:32:46 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralArrayType extends LiteralType {
    
    public LiteralArrayType() {}
    
    public LiteralType getElementType() {
        return elementType;
    }
    
    public void setElementType(LiteralType type) {
        elementType = type;
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private LiteralType elementType;
}
