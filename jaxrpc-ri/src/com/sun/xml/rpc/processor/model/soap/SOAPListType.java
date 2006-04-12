/*
 * $Id: SOAPListType.java,v 1.1 2006-04-12 20:34:43 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaType;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPListType extends SOAPType {
    
    public SOAPListType() {}
    
    public SOAPListType(QName name, SOAPType itemType, JavaType javaType) {
        super(name, javaType);
        this.itemType = itemType;
    }
    
    public SOAPType getItemType() {
        return itemType;
    }
    
    public void setItemType(SOAPType t) {
        itemType = t;
    }
    
    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private SOAPType itemType;
}
