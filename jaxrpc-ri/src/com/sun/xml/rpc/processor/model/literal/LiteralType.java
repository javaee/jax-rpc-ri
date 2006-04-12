/*
 * $Id: LiteralType.java,v 1.1 2006-04-12 20:32:44 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class LiteralType extends AbstractType {
    
    protected LiteralType() {}
    
    protected LiteralType(QName name, JavaType javaType) {
        super(name, javaType);
    }
    
    public QName getSchemaTypeRef() {
        return _schemaTypeRef;
    }
    
    public void setSchemaTypeRef(QName n) {
        _schemaTypeRef = n;
    }
    
    public boolean isLiteralType() {
        return true;
    }
    
    public void setNillable(boolean nillable) {
        isNillable = nillable;
    }
    
    public boolean isNillable() {
        return isNillable;
    }
    
    public abstract void accept(LiteralTypeVisitor visitor) throws Exception;
    
    private QName _schemaTypeRef;
    protected boolean isNillable = false;
}
