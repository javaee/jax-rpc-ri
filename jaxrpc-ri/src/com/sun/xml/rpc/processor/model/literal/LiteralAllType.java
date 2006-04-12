/*
 * $Id: LiteralAllType.java,v 1.1 2006-04-12 20:32:45 kohlert Exp $
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
public class LiteralAllType extends LiteralStructuredType {
    
    public LiteralAllType() {}
    
    public LiteralAllType(QName name) {
        this(name, null);
    }
    
    public LiteralAllType(QName name, JavaStructureType javaType) {
        super(name, javaType);
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
