/*
 * $Id: SOAPAttributeOwningType.java,v 1.1 2006-04-12 20:34:43 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.model.soap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SOAPAttributeOwningType extends SOAPType {
    
    protected SOAPAttributeOwningType() {}
    
    protected SOAPAttributeOwningType(QName name) {
        this(name, null);
    }
    
    protected SOAPAttributeOwningType(QName name, JavaStructureType javaType) {
        super(name, javaType);
    }
    
    public void add(SOAPAttributeMember m) {
        if (_attributeMembersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness");
        }
        _attributeMembers.add(m);
        _attributeMembersByName.put(m.getName(), m);
    }
    
    public SOAPAttributeMember getAttributeMemberByName(String name) {
        if (_attributeMembersByName.size() != _attributeMembers.size()) {
            initializeAttributeMembersByName();
        }
        return (SOAPAttributeMember) _attributeMembersByName.get(name);
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
                    
                SOAPAttributeMember m = (SOAPAttributeMember) iter.next();
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
