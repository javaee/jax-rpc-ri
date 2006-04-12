/*
 * $Id: Parameter.java,v 1.1 2006-04-12 20:33:08 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.model.java.JavaParameter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Parameter extends ModelObject {
    
    public Parameter() {}
    
    public Parameter(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String s) {
        name = s;
    }
    
    public JavaParameter getJavaParameter() {
        return javaParameter;
    }
    
    public void setJavaParameter(JavaParameter p) {
        javaParameter = p;
    }
    
    public AbstractType getType() {
        return type;
    }
    
    public void setType(AbstractType t) {
        type = t;
    }
    
    public Block getBlock() {
        return block;
    }
    
    public void setBlock(Block d) {
        block = d;
    }
    
    public Parameter getLinkedParameter() {
        return link;
    }
    
    public void setLinkedParameter(Parameter p) {
        link = p;
    }
    
    public boolean isEmbedded() {
        return embedded;
    }
    
    public void setEmbedded(boolean b) {
        embedded = b;
    }
    
    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private String name;
    private JavaParameter javaParameter;
    private AbstractType type;
    private Block block;
    private Parameter link;
    private boolean embedded;
}
