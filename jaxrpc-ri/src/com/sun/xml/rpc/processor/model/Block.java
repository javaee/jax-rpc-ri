/*
 * $Id: Block.java,v 1.1 2006-04-12 20:33:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Block extends ModelObject {
    
    public static final int BODY   = 1;
    public static final int HEADER = 2;
    public static final int ATTACHMENT = 3;

    public Block() {}
    
    public Block(QName name) {
        this.name = name;
    }
    
    public Block(QName name, AbstractType type) {
        this.name = name;
        this.type = type;
    }
    
    public QName getName() {
        return name;
    }
    
    public void setName(QName n) {
        name = n;
    }
    
    public AbstractType getType() {
        return type;
    }
    
    public void setType(AbstractType type) {
        this.type = type;
    }
    
    public int getLocation() {
        return location;
    }
    
    public void setLocation(int i) {
        location = i;
    }
    
    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private QName name;
    private AbstractType type;
    private int location;
}
