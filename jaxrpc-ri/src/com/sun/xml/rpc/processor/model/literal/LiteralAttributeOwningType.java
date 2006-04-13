/*
 * $Id: LiteralAttributeOwningType.java,v 1.2 2006-04-13 01:29:50 ofung Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.ModelException;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class LiteralAttributeOwningType extends LiteralType {
    
    protected LiteralAttributeOwningType() {}
    
    protected LiteralAttributeOwningType(QName name) {
        this(name, null);
    }
    
    protected LiteralAttributeOwningType(QName name,
        JavaStructureType javaType) {
            
        super(name, javaType);
    }
    
    public void add(LiteralAttributeMember m) {
        if (_attributeMembersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _attributeMembers.add(m);
        _attributeMembersByName.put(m.getName(), m);
    }
    
    public LiteralAttributeMember getAttributeMemberByName(String name) {
        if (_attributeMembersByName.size() != _attributeMembers.size()) {
            initializeAttributeMembersByName();
        }
        return (LiteralAttributeMember) _attributeMembersByName.get(name);
    }
    
    public Iterator getAttributeMembers() {
        return _attributeMembers.iterator();
    }
    
    public int getAttributeMembersCount() {
        return _attributeMembers.size();
    }
    
    /* serialization */
    public List getAttributeMembersList() {
        return _attributeMembers;
    }
    
    /* serialization */
    public void setAttributeMembersList(List l) {
        _attributeMembers = l;
    }
    
    private void initializeAttributeMembersByName() {
        _attributeMembersByName = new HashMap();
        if (_attributeMembers != null) {
            for (Iterator iter = _attributeMembers.iterator();
                iter.hasNext();) {
                    
                LiteralAttributeMember m = (LiteralAttributeMember) iter.next();
                if (m.getName() != null &&
                    _attributeMembersByName.containsKey(m.getName())) {
                        
                    throw new ModelException("model.uniqueness");
                }
                _attributeMembersByName.put(m.getName(), m);
            }
        }
    }
    
    private List _attributeMembers = new ArrayList();
    private Map _attributeMembersByName = new HashMap();
}
