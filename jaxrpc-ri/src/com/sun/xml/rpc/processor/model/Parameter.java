/*
 * $Id: Parameter.java,v 1.2 2006-04-13 01:29:29 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
