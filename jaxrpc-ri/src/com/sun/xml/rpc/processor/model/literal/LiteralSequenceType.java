/*
 * $Id: LiteralSequenceType.java,v 1.1 2006-04-12 20:32:43 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaStructureType;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralSequenceType extends LiteralStructuredType {
    
    public LiteralSequenceType() {}
    
    public LiteralSequenceType(QName name) {
        this(name, null);
    }
    
    public LiteralSequenceType(QName name, JavaStructureType javaType) {
        super(name, javaType);
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public void setUnwrapped(boolean unwrapped) {
        this.unwrapped = unwrapped;
    }
    
    public boolean isUnwrapped() {
        return unwrapped;
    }
    
    private boolean unwrapped = false;
}
