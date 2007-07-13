/*
 * $Id: LiteralStructuredType.java,v 1.3 2007-07-13 23:36:06 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
