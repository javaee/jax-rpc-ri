/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

/*
 * $Id: ClassNameCollector.java,v 1.3 2007-07-13 23:36:22 ofung Exp $
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

package com.sun.xml.rpc.processor.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.ExtendedModelVisitor;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;

import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralIDType;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.literal.LiteralTypeVisitor;

import com.sun.xml.rpc.processor.model.soap.RPCRequestOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPListType;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.model.soap.SOAPTypeVisitor;
import com.sun.xml.rpc.processor.model.soap.SOAPUnorderedStructureType;

import com.sun.xml.rpc.processor.model.java.JavaInterface;

/**
 * This class writes out a Model as an XML document.
 *
 * @author JAX-RPC Development Team
 */
public class ClassNameCollector extends ExtendedModelVisitor
    implements SOAPTypeVisitor, LiteralTypeVisitor {
    
    public ClassNameCollector() {
    }
    
    public void process(Model model) {
        try {
            _allClassNames = new HashSet();
            _exceptions = new HashSet();
            _wsdlBindingNames = new HashSet();
            _conflictingClassNames = new HashSet();
            _visitedTypes = new HashSet();
            _visitedFaults = new HashSet();
            visit(model);
        } catch (Exception e) {
            // fail silently
        } finally {
            _allClassNames = null;
            _exceptions = null;
            _visitedTypes = null;
            _visitedFaults = null;
        }
    }
    
    public Set getConflictingClassNames() {
        return _conflictingClassNames;
    }
    
    protected void postVisit(Model model) throws Exception {
        for (Iterator iter = model.getExtraTypes(); iter.hasNext();) {
            visitType((AbstractType)iter.next());
        }
    }
    
    protected void preVisit(Service service) throws Exception {
        registerClassName(
            ((JavaInterface)service.getJavaInterface()).getName());
        registerClassName(
            ((JavaInterface)service.getJavaInterface()).getImpl());
    }
    
    
    protected void preVisit(Port port) throws Exception {
        QName wsdlBindingName = (QName) port.getProperty(
            ModelProperties.PROPERTY_WSDL_BINDING_NAME);
        if (!_wsdlBindingNames.contains(wsdlBindingName)) {
            
            // multiple ports can share a binding without causing a conflict
            registerClassName(port.getJavaInterface().getName());
        }
        registerClassName((String) port.getProperty(
            ModelProperties.PROPERTY_STUB_CLASS_NAME));
        registerClassName((String) port.getProperty(
            ModelProperties.PROPERTY_TIE_CLASS_NAME));
    }
    
    protected void postVisit(Port port) throws Exception {
        QName wsdlBindingName = (QName) port.getProperty(
            ModelProperties.PROPERTY_WSDL_BINDING_NAME);
        if (!_wsdlBindingNames.contains(wsdlBindingName)) {
            _wsdlBindingNames.add(wsdlBindingName);
        }
    }
    
    protected boolean shouldVisit(Port port) {
        QName wsdlBindingName = (QName) port.getProperty(
            ModelProperties.PROPERTY_WSDL_BINDING_NAME);
        return !_wsdlBindingNames.contains(wsdlBindingName);
    }
    
    protected void preVisit(Fault fault) throws Exception {
        if (!_exceptions.contains(fault.getJavaException())) {
            
            /* the same exception can be used in several faults, but that
             * doesn't mean that there is a conflict
             */
            _exceptions.add(fault.getJavaException());
            
            registerClassName(fault.getJavaException().getName());
            
            if (fault.getParentFault() != null) {
                preVisit(fault.getParentFault());
            }
            for (Iterator iter = fault.getSubfaults();
                iter != null && iter.hasNext();) {
                    
                Fault subfault = (Fault) iter.next();
                preVisit(subfault);
            }
        }
    }
    
    protected void visitBodyBlock(Block block) throws Exception {
        visitBlock(block);
    }
    
    protected void visitHeaderBlock(Block block) throws Exception {
        visitBlock(block);
    }
    
    protected void visitFaultBlock(Block block) throws Exception {
        AbstractType type = block.getType();
        if (type instanceof SOAPStructureType) {
            for (Iterator iter = ((SOAPStructureType)type).getMembers();
                iter.hasNext();) {
                    
                SOAPStructureMember member = (SOAPStructureMember) iter.next();
                visitType(member.getType());
            }
        } else if (type instanceof SOAPArrayType) {
            visitType(((SOAPArrayType)type).getElementType());
        } else if (type instanceof LiteralStructuredType) { // bug fix: 5025492
            for (Iterator iter = ((LiteralStructuredType)type).getAttributeMembers(); 
                 iter.hasNext();) {
                LiteralAttributeMember attribute =
                    (LiteralAttributeMember) iter.next();
                visitType(attribute.getType());
            }
            for (Iterator iter = ((LiteralStructuredType)type).getElementMembers(); 
                 iter.hasNext();) {
                LiteralElementMember element =
                    (LiteralElementMember) iter.next();
                visitType(element.getType());
            }
        } else if (type instanceof LiteralArrayType) {
            visitType(((LiteralArrayType)type).getElementType());
        } else if (type instanceof LiteralArrayWrapperType) {
            visitType(((LiteralArrayWrapperType)type).getElementMember().getType());
        }  // end bugfix: 5025492
    }
    
    protected void visitBlock(Block block) throws Exception {
        visitType(block.getType());
    }
    
    protected void visit(Parameter parameter) throws Exception {
        visitType(parameter.getType());
    }
    
    private void visitType(AbstractType type) throws Exception {
        if (type != null) {
            if (type.isLiteralType()) {
                visitType((LiteralType) type);
            } else if (type.isSOAPType()) {
                visitType((SOAPType) type);
            }
        }
    }
    
    private void visitType(LiteralType type) throws Exception {
        type.accept(this);
    }
    
    public void visit(LiteralSimpleType type) throws Exception {
    }
    
    public void visit(LiteralSequenceType type) throws Exception {
        visitLiteralStructuredType(type);
    }
    
    public void visit(LiteralAllType type) throws Exception {
        visitLiteralStructuredType(type);
    }
    
    private void visitLiteralStructuredType(LiteralStructuredType type)
        throws Exception {
            
        boolean alreadySeen = _visitedTypes.contains(type);
        if (!alreadySeen) {
            _visitedTypes.add(type);
            registerClassName(type.getJavaType().getName());
            for (Iterator iter = type.getAttributeMembers(); iter.hasNext();) {
                LiteralAttributeMember attribute =
                    (LiteralAttributeMember) iter.next();
                visitType(attribute.getType());
            }
            for (Iterator iter = type.getElementMembers(); iter.hasNext();) {
                LiteralElementMember element =
                    (LiteralElementMember) iter.next();
                visitType(element.getType());
            }
        }
    }
    
    public void visit(LiteralArrayType type) throws Exception {
        visitType(type.getElementType());
    }
    
    public void visit(LiteralArrayWrapperType type) throws Exception {
        boolean alreadySeen = _visitedTypes.contains(type);
        if (!alreadySeen) {
            _visitedTypes.add(type);
            registerClassName(type.getJavaType().getName());
            visitType(type.getElementMember().getType());
        }
    }
    
    public void visit(LiteralFragmentType type) throws Exception {
    }
    
    public void visit(LiteralListType type) throws Exception {
    }
    
    public void visit(SOAPListType type) throws Exception {
    }
    
    public void visit(LiteralIDType type) throws Exception {
    }
    
    
    public void visit(LiteralEnumerationType type) throws Exception {
        boolean alreadySeen = _visitedTypes.contains(type);
        if (!alreadySeen) {
            _visitedTypes.add(type);
            registerClassName(type.getJavaType().getName());
        }
    }
    
    private void visitType(SOAPType type) throws Exception {
        type.accept(this);
    }
    
    public void visit(SOAPArrayType type) throws Exception {
        visitType(type.getElementType());
    }
    
    public void visit(SOAPCustomType type) throws Exception {
    }
    
    public void visit(SOAPEnumerationType type) throws Exception {
        visitType(type.getBaseType());
    }
    
    public void visit(SOAPSimpleType type) throws Exception {
    }
    
    public void visit(SOAPAnyType type) throws Exception {
    }
    
    public void visit(SOAPOrderedStructureType type) throws Exception {
        visitSOAPStructureType(type);
    }
    
    public void visit(SOAPUnorderedStructureType type) throws Exception {
        visitSOAPStructureType(type);
    }
    
    public void visit(RPCRequestOrderedStructureType type) throws Exception {
        visitSOAPStructureType(type);
    }
    
    public void visit(RPCRequestUnorderedStructureType type) throws Exception {
        visitSOAPStructureType(type);
    }
    
    public void visit(RPCResponseStructureType type) throws Exception {
        visitSOAPStructureType(type);
    }
    
    private void visitSOAPStructureType(SOAPStructureType type)
        throws Exception {
            
        boolean alreadySeen = _visitedTypes.contains(type);
        if (!alreadySeen) {
            _visitedTypes.add(type);
            
            if (_exceptions.contains(type.getJavaType())) {
                return;
            }
            
            registerClassName(type.getJavaType().getName());
            for (Iterator iter = type.getMembers(); iter.hasNext();) {
                SOAPStructureMember member = (SOAPStructureMember) iter.next();
                visitType(member.getType());
            }
            for (Iterator iter = type.getSubtypes();
                iter != null && iter.hasNext();) {
                    
                SOAPStructureType subType = (SOAPStructureType) iter.next();
                visitType(subType);
            }
        }
    }
    
    private void registerClassName(String name) {
        if (name == null || name.equals("")) {
            return;
        }
        if (_allClassNames.contains(name)) {
            _conflictingClassNames.add(name);
        } else {
            _allClassNames.add(name);
        }
    }
    
    private Set _allClassNames;
    private Set _exceptions;
    private Set _wsdlBindingNames;
    private Set _conflictingClassNames;
    private Set _visitedTypes;
    private Set _visitedFaults;
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.model.literal.LiteralTypeVisitor#visit(com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType)
     */
    public void visit(LiteralAttachmentType type) throws Exception {
        // TODO Auto-generated method stub
        
    }
}
