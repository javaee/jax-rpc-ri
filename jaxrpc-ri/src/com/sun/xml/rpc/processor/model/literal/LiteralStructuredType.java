/*
 * $Id: LiteralStructuredType.java,v 1.2 2006-04-13 01:29:55 ofung Exp $
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
