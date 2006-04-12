/*
 * $Id: LiteralStructuredType.java,v 1.1 2006-04-12 20:32:44 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.literal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.ModelException;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class LiteralStructuredType extends LiteralAttributeOwningType {
    
    protected LiteralStructuredType() {}
    
    protected LiteralStructuredType(QName name) {
        this(name, null);
    }
    
    protected LiteralStructuredType(QName name, JavaStructureType javaType) {
        super(name, javaType);
    }
    
    public void add(LiteralElementMember m) {
        if (_elementMembersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _elementMembers.add(m);
        if (m.getName() != null) {
            _elementMembersByName.put(m.getName().getLocalPart(), m);
        }
    }
    
    public LiteralElementMember getElementMemberByName(String name) {
        if (_elementMembersByName.size() != _elementMembers.size()) {
            initializeElementMembersByName();
        }
        return (LiteralElementMember) _elementMembersByName.get(name);
    }
    
    public Iterator getElementMembers() {
        return _elementMembers.iterator();
    }
    
    public int getElementMembersCount() {
        return _elementMembers.size();
    }
    
    /* serialization */
    public List getElementMembersList() {
        return _elementMembers;
    }
    
    /* serialization */
    public void setElementMembersList(List l) {
        _elementMembers = l;
    }
    
    private void initializeElementMembersByName() {
        _elementMembersByName = new HashMap();
        if (_elementMembers != null) {
            for (Iterator iter = _elementMembers.iterator(); iter.hasNext();) {
                LiteralElementMember m = (LiteralElementMember) iter.next();
                if (m.getName() != null &&
                    _elementMembersByName.containsKey(m.getName())) {
                        
                    throw new ModelException("model.uniqueness");
                }
                if (m.getName() != null) {
                    _elementMembersByName.put(m.getName().getLocalPart(), m);
                }
            }
        }
    }
    
    public LiteralContentMember getContentMember() {
        return _contentMember;
    }
    
    public void setContentMember(LiteralContentMember t) {
        _contentMember = t;
    }
    
    public void addSubtype(LiteralStructuredType type) {
        if (_subtypes == null) {
            _subtypes = new HashSet();
        }
        _subtypes.add(type);
        type.setParentType(this);
    }
    
    public Iterator getSubtypes() {
        if (_subtypes != null) {
            return _subtypes.iterator();
        }
        return null;
    }
    
    /* serialization */
    public Set getSubtypesSet() {
        return _subtypes;
    }
    
    /* serialization */
    public void setSubtypesSet(Set s) {
        _subtypes = s;
    }
    
    public void setParentType(LiteralStructuredType parent) {
        if (_parentType != null &&
            parent != null &&
            !_parentType.equals(parent)) {
                
            throw new ModelException("model.parent.type.already.set",
                new Object[] { getName().toString(),
                    _parentType.getName().toString(),
                    parent.getName().toString()});
        }
        this._parentType = parent;
    }
    
    public LiteralStructuredType getParentType() {
        return _parentType;
    }
    
    public void setRpcWrapper(boolean rpcWrapper) {
        this._rpcWrapper = rpcWrapper;
    }
    
    public boolean isRpcWrapper() {
        return _rpcWrapper;
    }
   
    private List _elementMembers = new ArrayList();
    private Map _elementMembersByName = new HashMap();
    private LiteralContentMember _contentMember;
    private Set _subtypes = null;
    private LiteralStructuredType _parentType = null;
    private boolean _rpcWrapper = false;
    private boolean requestResponseStruct;
}
