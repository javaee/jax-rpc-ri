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
