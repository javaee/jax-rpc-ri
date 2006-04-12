/*
 * $Id: LiteralFragmentType.java,v 1.1 2006-04-12 20:32:44 kohlert Exp $
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
public class LiteralFragmentType extends LiteralType {
    
    public LiteralFragmentType() {}
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
}
