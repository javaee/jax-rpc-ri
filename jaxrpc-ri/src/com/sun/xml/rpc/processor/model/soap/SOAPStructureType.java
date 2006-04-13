/*
 * $Id: SOAPStructureType.java,v 1.2 2006-04-13 01:30:07 ofung Exp $
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

package com.sun.xml.rpc.processor.model.soap;

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
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SOAPStructureType extends SOAPAttributeOwningType {
    
    protected SOAPStructureType() {}
    
    protected SOAPStructureType(QName name) {
        this(name, SOAPVersion.SOAP_11);
    }
    
    protected SOAPStructureType(QName name, SOAPVersion version) {
        super(name, null);
    }
    
    protected SOAPStructureType(QName name, JavaStructureType javaType) {
        super(name, javaType);
    }
    
    public void add(SOAPStructureMember m) {
        if (_membersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _members.add(m);
        _membersByName.put(m.getName(), m);
    }
    
    public SOAPStructureMember getMemberByName(QName name) {
        if (_membersByName.size() != _members.size()) {
            initializeMembersByName();
        }
        return (SOAPStructureMember) _membersByName.get(name);
    }
    
    public Iterator getMembers() {
        return _members.iterator();
    }
    
    public int getMembersCount() {
        return _members.size();
    }
    
    /* serialization */
    public List getMembersList() {
        return _members;
    }
    
    /* serialization */
    public void setMembersList(List l) {
        _members = l;
    }
    
    private void initializeMembersByName() {
        _membersByName = new HashMap();
        if (_members != null) {
            for (Iterator iter = _members.iterator(); iter.hasNext();) {
                SOAPStructureMember m = (SOAPStructureMember) iter.next();
                if (m.getName() != null &&
                    _membersByName.containsKey(m.getName())) {
                        
                    throw new ModelException("model.uniqueness");
                }
                _membersByName.put(m.getName(), m);
            }
        }
    }
    
    public void addSubtype(SOAPStructureType type) {
        if (_subtypes == null) {
            _subtypes = new HashSet();
        }
        _subtypes.add(type);
        type.setParentType(this);
    }
    
    public Iterator getSubtypes() {
        if (_subtypes != null)
            return _subtypes.iterator();
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
    
    public void setParentType(SOAPStructureType parent) {
        if (_parentType != null &&
            parent != null &&
            !_parentType.equals(parent)) {
                
            throw new ModelException("model.parent.type.already.set",
                new Object[] {getName().toString(),
                    _parentType.getName().toString(),
                    parent.getName().toString()});
        }
        this._parentType = parent;
    }
    
    public SOAPStructureType getParentType() {
        return _parentType;
    }
    
    private List _members = new ArrayList();
    private Map _membersByName = new HashMap();
    private Set _subtypes = null;
    private SOAPStructureType _parentType = null;
}
