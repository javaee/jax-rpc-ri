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

package com.sun.xml.rpc.processor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.ExtendedModelVisitor;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
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
import com.sun.xml.rpc.processor.model.literal.LiteralWildcardMember;

import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
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

import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;

/**
 * This class writes out a Model. It is intended for debugging purposes only.
 *
 * @author JAX-RPC Development Team
 */
public class ModelWriter extends ExtendedModelVisitor
    implements ProcessorAction, SOAPTypeVisitor, LiteralTypeVisitor {
    
    public ModelWriter(IndentingWriter w) {
        _writer = w;
        _componentWriter = new ComponentWriter(_writer);
    }
    
    public ModelWriter(OutputStream out) {
        this(new IndentingWriter(new OutputStreamWriter(out), 2));
    }
    
    public ModelWriter(File f) throws FileNotFoundException {
        this(new FileOutputStream(f));
    }
    
    public void write(Model model) {
        try {
            _visitedComplexTypes = new HashSet();
            visit(model);
            _writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            _visitedComplexTypes = null;
            _currentNamespaceURI = null;
        }
    }
    
    public void perform(Model model, Configuration config, Properties options) {
        write(model);
    }
    
    protected void preVisit(Model model) throws Exception {
        _writer.p("MODEL ");
        writeQName(model.getName());
        _writer.pln();
        _writer.pI();
        _currentNamespaceURI = model.getTargetNamespaceURI();
        if (_currentNamespaceURI != null) {
            _writer.p("TARGET-NAMESPACE ");
            _writer.pln(_currentNamespaceURI);
        }
    }
    
    protected void postVisit(Model model) throws Exception {
        processTypes(model);
        _writer.pO();
    }
    
    protected void processTypes(Model model) throws Exception {
        for (Iterator iter = model.getExtraTypes(); iter.hasNext();) {
            AbstractType extraType = (AbstractType) iter.next();
            if (extraType.isLiteralType()) {
                describe((LiteralType) extraType);
            } else if (extraType.isSOAPType()) {
                describe((SOAPType) extraType);
            }
        }
    }
    
    protected void preVisit(Service service) throws Exception {
        _writer.p("SERVICE ");
        writeQName(service.getName());
        _writer.p(" INTERFACE ");
        _writer.p(service.getJavaInterface().getName());
        _writer.pln();
        _writer.pI();
        _currentNamespaceURI = service.getName().getNamespaceURI();
    }
    
    protected void postVisit(Service service) throws Exception {
        _writer.pO();
    }
    
    protected void preVisit(Port port) throws Exception {
        _writer.p("PORT ");
        writeQName(port.getName());
        _writer.p(" INTERFACE ");
        _writer.p(port.getJavaInterface().getName());
        _writer.pln();
        _writer.pI();
        _currentNamespaceURI = port.getName().getNamespaceURI();
    }
    
    protected void postVisit(Port port) throws Exception {
        _writer.pO();
    }
    
    protected void preVisit(Operation operation) throws Exception {
        _writer.p("OPERATION ");
        writeQName(operation.getName());
        if (operation.isOverloaded()) {
            _writer.p(" (OVERLOADED)");
        }
        if (operation.getStyle() != null) {
            if (operation.getStyle().equals(SOAPStyle.RPC)) {
                _writer.p(" (RPC)");
            } else if (operation.getStyle().equals(SOAPStyle.DOCUMENT)) {
                _writer.p(" (DOCUMENT)");
            }
        }
        _writer.pln();
        _writer.pI();
    }
    
    protected void postVisit(Operation operation) throws Exception {
        _writer.pO();
    }
    
    protected void preVisit(Request request) throws Exception {
        _writer.plnI("REQUEST");
    }
    
    protected void postVisit(Request request) throws Exception {
        _writer.pO();
    }
    
    protected void preVisit(Response response) throws Exception {
        _writer.plnI("RESPONSE");
    }
    
    protected void postVisit(Response response) throws Exception {
        _writer.pO();
    }
    
    protected void preVisit(Fault fault) throws Exception {
        _writer.p("FAULT ");
        _writer.p(fault.getName());
        _writer.pln();
        _writer.pI();
    }
    
    protected void postVisit(Fault fault) throws Exception {
        _writer.pO();
    }
    
    protected void visitBodyBlock(Block block) throws Exception {
        _writer.p("BODY-BLOCK ");
        writeQName(block.getName());
        _writer.p(" TYPE ");
        writeQName(block.getType().getName());
        if (block.getType().isLiteralType()) {
            _writer.pln(" (LITERAL)");
            describe((LiteralType) block.getType());
        } else if (block.getType().isSOAPType()) {
            _writer.pln(" (ENCODED)");
            describe((SOAPType) block.getType());
        }
    }
    
    protected void visitHeaderBlock(Block block) throws Exception {
        _writer.p("HEADER-BLOCK ");
        writeQName(block.getName());
        _writer.p(" TYPE ");
        writeQName(block.getType().getName());
        if (block.getType().isLiteralType()) {
            _writer.pln(" (LITERAL)");
            describe((LiteralType) block.getType());
        } else if (block.getType().isSOAPType()) {
            _writer.pln(" (ENCODED)");
            describe((SOAPType) block.getType());
        }
    }
    
    protected void visitFaultBlock(Block block) throws Exception {
        _writer.p("FAULT-BLOCK ");
        writeQName(block.getName());
        _writer.p(" TYPE ");
        writeQName(block.getType().getName());
        if (block.getType().isLiteralType()) {
            _writer.pln(" (LITERAL)");
            describe((LiteralType) block.getType());
        } else if (block.getType().isSOAPType()) {
            _writer.pln(" (ENCODED)");
            describe((SOAPType) block.getType());
        }
    }
    
    protected void visit(Parameter parameter) throws Exception {
        _writer.p("PARAMETER ");
        _writer.p(parameter.getName());
        _writer.p(" TYPE ");
        writeQName(parameter.getType().getName());
        if (parameter.isEmbedded()) {
            _writer.p(" (EMBEDDED)");
        }
        if (parameter.getType().isLiteralType()) {
            _writer.pln(" (LITERAL)");
            describe((LiteralType) parameter.getType());
        } else if (parameter.getType().isSOAPType()) {
            _writer.pln(" (ENCODED)");
            describe((SOAPType) parameter.getType());
        }
    }
    
    protected void describe(LiteralType type) throws Exception {
        _writer.pI();
        type.accept(this);
        _writer.pO();
    }
    
    public void visit(LiteralEnumerationType type) throws Exception {
        _writer.p("LITERAL-ENUMERATION-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getBaseType());
    }
    
    public void visit(LiteralSimpleType type) throws Exception {
        _writer.p("LITERAL-SIMPLE-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
    }
    
    public void visit(LiteralIDType type) throws Exception {
        _writer.p("LITERAL-SIMPLE-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.p(" resloveIDREF " +
            new Boolean(type.getResolveIDREF()).toString());
        _writer.pln();
    }
    
    public void visit(LiteralSequenceType type) throws Exception {
        visitLiteralStructuredType(type, "LITERAL-SEQUENCE-TYPE ", true);
    }
    
    public void visit(LiteralAllType type) throws Exception {
        visitLiteralStructuredType(type, "LITERAL-ALL-TYPE ", true);
    }
    
    private void visitLiteralStructuredType(LiteralStructuredType type,
        String header, boolean detailed) throws Exception {
            
        boolean alreadySeen = _visitedComplexTypes.contains(type);
        _writer.p(header);
        writeQName(type.getName());
        if (alreadySeen) {
            _writer.p(" (REF)");
        } else {
            _visitedComplexTypes.add(type);
        }
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        
        if (alreadySeen) {
            return;
        }
        
        if (detailed) {
            _writer.pI();
            processContentMember(type);
            processAttributeMembers(type);
            processElementMembers(type);
            _writer.pO();
        }
    }
    
    protected void processContentMember(LiteralStructuredType type)
        throws Exception {
            
        if (type.getContentMember() != null) {
            _writer.p("CONTENT");
            _writer.pln();
            describe(type.getContentMember().getType());
        }
    }
    
    protected void processAttributeMembers(LiteralStructuredType type)
        throws Exception {
            
        for (Iterator iter = type.getAttributeMembers(); iter.hasNext();) {
            LiteralAttributeMember attribute =
                (LiteralAttributeMember) iter.next();
            writeAttributeMember(attribute);
        }
    }
    
    protected void writeAttributeMember(LiteralAttributeMember attribute)
        throws Exception {
            
        _writer.p("ATTRIBUTE ");
        _writer.p(attribute.getName().getLocalPart());
        if (attribute.isRequired()) {
            _writer.p(" (REQUIRED)");
        }
        _writer.pln();
        describe(attribute.getType());
    }
    
    protected void processElementMembers(LiteralStructuredType type)
        throws Exception {
            
        for (Iterator iter = type.getElementMembers(); iter.hasNext();) {
            LiteralElementMember  member = (LiteralElementMember) iter.next();
            if (member.isWildcard()) {
                writeWildcardMember((LiteralWildcardMember) member);
            } else {
                writeElementMember(member);
            }
        }
    }
    
    protected void writeElementMember(LiteralElementMember element)
        throws Exception {
            
        _writer.p("ELEMENT ");
        _writer.p(element.getName().getLocalPart());
        if (element.isNillable()) {
            _writer.p(" (NILLABLE)");
        }
        if (element.isRequired()) {
            _writer.p(" (REQUIRED)");
        }
        if (element.isRepeated()) {
            _writer.p(" (REPEATED)");
        }
        _writer.pln();
        describe(element.getType());
    }
    
    protected void writeWildcardMember(LiteralWildcardMember wildcard)
        throws Exception {
            
        _writer.p("WILDCARD (ANY)");
        if (wildcard.getExcludedNamespaceName() != null) {
            _writer.p(" (OTHER) ");
            _writer.p(wildcard.getExcludedNamespaceName());
        }
        if (wildcard.isNillable()) {
            _writer.p(" (NILLABLE)");
        }
        if (wildcard.isRequired()) {
            _writer.p(" (REQUIRED)");
        }
        if (wildcard.isRepeated()) {
            _writer.p(" (REPEATED)");
        }
        _writer.pln();
        describe(wildcard.getType());
    }
    
    public void visit(LiteralArrayType type) throws Exception {
        _writer.p("LITERAL-ARRAY-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getElementType());
    }
    
    public void visit(LiteralArrayWrapperType type) throws Exception {
        _writer.p("LITERAL-ARRAY-WRAPPER-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.p("ELEMENT-MEMBER ");
        writeQName(type.getElementMember().getName());
        _writer.p(": TYPE: ");
        writeQName(type.getElementMember().getType().getName());
        _writer.pln();
        describe(type.getElementMember().getType());
    }
    
    public void visit(LiteralListType type) throws Exception {
        _writer.p("LITERAL-LIST-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getItemType());
    }
    
    public void visit(SOAPListType type) throws Exception {
        _writer.p("SOAP-LIST-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getItemType());
    }
    
    public void visit(LiteralFragmentType type) throws Exception {
        _writer.p("LITERAL-FRAGMENT-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
    }
    
    protected void describe(SOAPType type) throws Exception {
        _writer.pI();
        type.accept(this);
        _writer.pO();
    }
    
    public void visit(SOAPArrayType type) throws Exception {
        _writer.p("SOAP-ARRAY-TYPE ");
        writeQName(type.getName());
        _writer.p(" RANK ");
        _writer.p(Integer.toString(type.getRank()));
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getElementType());
    }
    
    public void visit(SOAPCustomType type) throws Exception {
        _writer.p("SOAP-CUSTOM-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        _writer.pI();
        _writer.pO();
    }
    
    public void visit(SOAPEnumerationType type) throws Exception {
        _writer.p("SOAP-ENUMERATION-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getBaseType());
    }
    
    public void visit(SOAPSimpleType type) throws Exception {
        _writer.p("SOAP-SIMPLE-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
    }
    
    public void visit(SOAPAnyType type) throws Exception {
        _writer.p("SOAP-ANY-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
    }
    
    public void visit(SOAPOrderedStructureType type) throws Exception {
        visitSOAPStructureType(type, "SOAP-ORDERED-STRUCTURE-TYPE", true);
    }
    
    public void visit(SOAPUnorderedStructureType type) throws Exception {
        visitSOAPStructureType(type, "SOAP-UNORDERED-STRUCTURE-TYPE", true);
    }
    
    public void visit(RPCRequestOrderedStructureType type) throws Exception {
        visitSOAPStructureType(type,
            "RPC-REQUEST-ORDERED-STRUCTURE-TYPE", false);
    }
    
    public void visit(RPCRequestUnorderedStructureType type) throws Exception {
        visitSOAPStructureType(type,
            "RPC-REQUEST-UNORDERED-STRUCTURE-TYPE", false);
    }
    
    public void visit(RPCResponseStructureType type) throws Exception {
        visitSOAPStructureType(type, "RPC-RESPONSE-STRUCTURE-TYPE", false);
    }
    
    private void visitSOAPStructureType(SOAPStructureType type, String header,
        boolean detailed) throws Exception {
            
        boolean alreadySeen = _visitedComplexTypes.contains(type);
        _writer.p(header);
        _writer.p(" ");
        writeQName(type.getName());
        if (alreadySeen) {
            _writer.p(" (REF)");
        } else {
            _visitedComplexTypes.add(type);
        }
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        
        if (alreadySeen) {
            return;
        }
        
        if (detailed) {
            _writer.pI();
            if (type.getParentType() != null) {
                _writer.pln("PARENT TYPE");
                _writer.pI();
                describe(((SOAPStructureType)type.getParentType()));
                _writer.pO();
            }
            processMembers(type);
            _writer.pO();
        }
    }
    
    protected void processMembers(SOAPStructureType type) throws Exception {
        for (Iterator iter = type.getMembers(); iter.hasNext();) {
            SOAPStructureMember member = (SOAPStructureMember) iter.next();
            writeMember(member);
        }
    }
    
    protected void writeMember(SOAPStructureMember member) throws Exception {
        _writer.p("MEMBER ");
        if (member.isInherited()) {
            _writer.p("(INHERITED) ");
        }
        _writer.p(member.getName().getLocalPart());
        _writer.pln();
        describe(member.getType());
    }
    
    /* this method is a bit complicated because we try to be smart and
     * make QNames as short as possible by abbreviating their namespace URIs.
     * basically, "(-)" denotes "the same namespace as the last containing
     * element" while "(wsdl)" and "(xsd)" are used as shorthands for
     * well-known namespaces
     */
    protected void writeQName(QName name) throws IOException {
        
        if (name == null) {
            _writer.p("null");
        } else {
            String nsURI = name.getNamespaceURI();
            if (nsURI.equals(_currentNamespaceURI)) {
                
                // do nothing
            } else if (nsURI.length() > 0) {
                if (nsURI.equals(WSDLConstants.NS_WSDL)) {
                    _writer.p("{wsdl}");
                } else if (nsURI.equals(SchemaConstants.NS_XSD)) {
                    _writer.p("{xsd}");
                } else {
                    _writer.p("{");
                    _writer.p(nsURI);
                    _writer.p("}");
                }
            }
            _writer.p(name.getLocalPart());
        }
    }
    
    private IndentingWriter _writer;
    private ComponentWriter _componentWriter;
    private String _currentNamespaceURI;
    private Set _visitedComplexTypes;
    
    private final static boolean writeComponentInformation = false;
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.model.literal.LiteralTypeVisitor#visit(com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType)
     */
    public void visit(LiteralAttachmentType type) throws Exception {
        // TODO Auto-generated method stub
        
    }
}
