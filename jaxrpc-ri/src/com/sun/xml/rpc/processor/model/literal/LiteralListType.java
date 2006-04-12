/*
 * $Id: LiteralListType.java,v 1.1 2006-04-12 20:32:43 kohlert Exp $
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
public class LiteralListType extends LiteralType {
    
    public LiteralListType() {}
    
    public LiteralListType(QName name, LiteralType itemType,
        JavaType javaType) {
            
        super(name, javaType);
        this.itemType = itemType;
    }
    
    public LiteralType getItemType() {
        return itemType;
    }
    
    public void setItemType(LiteralType t) {
        itemType = t;
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private LiteralType itemType;
}
