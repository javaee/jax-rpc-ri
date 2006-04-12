/*
 * $Id: LiteralIDType.java,v 1.1 2006-04-12 20:32:45 kohlert Exp $
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
public class LiteralIDType extends LiteralType {
    
    public LiteralIDType() {}
    
    public LiteralIDType(QName name) {
        this(name, null);
    }
    
    public LiteralIDType(QName name, JavaSimpleType javaType) {
        this(name, javaType, false);
    }
    
    public LiteralIDType(QName name, JavaSimpleType javaType,
        boolean resolveIDREF) {
            
        super(name, javaType);
        this.resolveIDREF = resolveIDREF;
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public boolean getResolveIDREF() {
        return resolveIDREF;
    }
    
    public void setResolveIDREF(boolean resolveIDREF) {
        this.resolveIDREF = resolveIDREF;
    }
    
    //flag which represents command line -f:resolveidref flag.
    private boolean resolveIDREF;
    
}
