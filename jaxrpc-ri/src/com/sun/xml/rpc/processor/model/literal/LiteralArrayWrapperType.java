/*
 * $Id: LiteralArrayWrapperType.java,v 1.1 2006-04-12 20:32:46 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.ModelException;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralArrayWrapperType extends LiteralSequenceType {
    
    public LiteralArrayWrapperType() {}
    
    public LiteralArrayWrapperType(QName name) {
        super(name, null);
    }
    
    public LiteralArrayWrapperType(QName name, JavaStructureType javaType) {
        super(name, javaType);
    }
    
    public void add(LiteralElementMember m) {
        super.add(m);
        if (elementMember != null) {
            throw new ModelException("model.arraywrapper.member.already.set");
        }
        elementMember = m;
    }
    
    public void setElementMembersList(List l) {
        if (l.size() >1) {
            throw new ModelException("model.arraywrapper.only.one.member");
        }
        super.setElementMembersList(l);
        elementMember = (LiteralElementMember)l.get(0);
    }
    
    public void addSubtype(LiteralStructuredType type) {
        throw new ModelException("model.arraywrapper.no.subtypes");
    }
    
    public void setSubtypesSet(Set s) {
        if (s != null && s.size() > 0) {
            throw new ModelException("model.arraywrapper.no.subtypes");
        }
        super.setSubtypesSet(s);
    }
    
    public void setParentType(LiteralStructuredType parent) {
        if (parent != null) {
            throw new ModelException("model.arraywrapper.no.parent");
        }
        super.setParentType(parent);
    }
    
    public void setContentMember(LiteralContentMember t) {
        if (t != null) {
            throw new ModelException("model.arraywrapper.no.content.member");
        }
        super.setContentMember(t);
    }
    
    public LiteralElementMember getElementMember() {
        return elementMember;
    }
    
    public JavaArrayType getJavaArrayType() {
        return javaArrayType;
    }
    
    public void setJavaArrayType(JavaArrayType javaArrayType) {
        this.javaArrayType = javaArrayType;
    }
    
    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private LiteralElementMember elementMember = null;
    private JavaArrayType javaArrayType = null;
}
