/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
