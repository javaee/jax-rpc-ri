/*
 * $Id: ClassNameCollector.java,v 1.1 2006-04-12 20:35:00 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
