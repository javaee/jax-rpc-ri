// @(#) 1.3 jsr109ri/src/java/com/ibm/webservices/ri/tools/xrpcc/TypeVisitor.java, jsr109ri, jsr10911, b0242.09 10/25/02 14:59:11 [10/28/02 10:12:39]
/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*************************************************************************
   IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
   ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
   PURPOSE. IN NO EVENT SHALL IBM BE LIABLE FOR ANY SPECIAL, INDIRECT OR
   CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
   USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
   OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
   OR PERFORMANCE OF THIS SOFTWARE.
**************************************************************************/
package com.sun.xml.rpc.processor.generator.nodes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.ExtendedModelVisitor;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelProperties;
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
import com.sun.xml.rpc.processor.util.ClassNameCollector;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAP12Constants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;

/**
 * Visit the type hierarcy to gather complex and simple types
 * @author Michael Cheng
 */
public class TypeVisitor
    extends ExtendedModelVisitor
    implements SOAPTypeVisitor, LiteralTypeVisitor {

    public TypeVisitor(Configuration config) {
        this.config = config;
        _visitedComplexTypes = new HashSet();
        _visitedSimpleTypes = new HashSet();
        _visitedFaults = new HashSet();
        _visitedNSPackages = new HashMap();
    }

    public Set getComplexTypes() {
        return _visitedComplexTypes;
    }

    public Set getSimpleTypes() {
        return _visitedSimpleTypes;
    }

    public Set getFaults() {
        return _visitedFaults;
    }

    public Map getNamespacePackages() {
        return _visitedNSPackages;
    }

    protected void preVisit(Model model) throws Exception {
        if (_trace) {
            System.out.println("preVisit: model");
        }
        ClassNameCollector collector = new ClassNameCollector();
        collector.process(model);
        Set names = collector.getConflictingClassNames();
        if (!names.isEmpty()) {
            throw new MappingException(
                "j2ee.nameCollision",
                new Object[] { names.toString() });
        }
    }

    protected void postVisit(Model model) throws Exception {
        
        processTypes(model);
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
        if (_trace) {
            System.out.println("preVisit server " + service.getName());
        }
    }

    protected void postVisit(Service service) throws Exception {
    }

    protected void preVisit(Port port) throws Exception {
        if (_trace) {
            System.out.println("preVisit port:" + port.getName());
        }
        processNamespacePackages(port);
    }

    protected void postVisit(Port port) throws Exception {
    }

    protected void preVisit(Operation operation) throws Exception {
        if (_trace) {
            System.out.println("preVisit operation:" + operation.getName());
        }
    }

    protected void postVisit(Operation operation) throws Exception {
    }

    protected void preVisit(Request request) throws Exception {
        if (_trace) {
            System.out.println("preVisit Request");
        }
    }

    protected void postVisit(Request request) throws Exception {
    }

    protected void preVisit(Response response) throws Exception {
        if (_trace) {
            System.out.println("preVisit Response");
        }
    }

    protected void postVisit(Response response) throws Exception {
    }

    protected void preVisit(Fault fault) throws Exception {
        if (_trace) {
            System.out.println(
                "preVisit Fault:"
                    + fault.getName()
                    + "; type = "
                    + fault.getClass().getName());
        }
        boolean alreadySeen = _visitedFaults.contains(fault);
        if (alreadySeen)
            return;
        _visitedFaults.add(fault);
    }

    protected void postVisit(Fault fault) throws Exception {
    }

    protected void visitBodyBlock(Block block) throws Exception {
        if (block.getType().isLiteralType()) {
            describe((LiteralType) block.getType());
        } else if (block.getType().isSOAPType()) {
            describe((SOAPType) block.getType());
        }
    }

    protected void visitHeaderBlock(Block block) throws Exception {
        if (block.getType().isLiteralType()) {
            describe((LiteralType) block.getType());
        } else if (block.getType().isSOAPType()) {
            describe((SOAPType) block.getType());
        }
    }

    protected void visitFaultBlock(Block block) throws Exception {
        if (block.getType().isLiteralType()) {
            describe((LiteralType) block.getType());
        } else if (block.getType().isSOAPType()) {
            describe((SOAPType) block.getType());
        }
    }

    protected void visit(Parameter parameter) throws Exception {
        if (_trace) {
            System.out.println("visit Parameter:" + parameter.getName());
        }
        if (parameter.getType().isLiteralType()) {
            describe((LiteralType) parameter.getType());
        } else if (parameter.getType().isSOAPType()) {
            describe((SOAPType) parameter.getType());
        }
    }

    protected void describe(LiteralType type) throws Exception {
        processNamespacePackages(type);
        type.accept(this);
    }

    public void visit(LiteralSimpleType type) throws Exception {
        if (_trace) {
            System.out.println("visit LiteralSimpleType:" + type.getName());
        }
    }

    public void visit(LiteralSequenceType type) throws Exception {
        if (_trace) {
            System.out.println("visit LiteralSequenceType:" + type.getName());
        }
        visitLiteralStructuredType(type, "LITERAL-SEQUENCE-TYPE ", true);
    }

    public void visit(LiteralAllType type) throws Exception {
        if (_trace) {
            System.out.println("visit LiteralAllType:" + type.getName());
        }
        visitLiteralStructuredType(type, "LITERAL-ALL-TYPE ", true);
    }

    public void visit(LiteralEnumerationType type) throws Exception {
        if (_trace) {
            System.out.println(
                "visit LiteralEnumerationType " + type.getName());
        }
        if (!_visitedSimpleTypes.contains(type)) {
            _visitedSimpleTypes.add(type);
            describe(type.getBaseType());
        }
    }

    public void visit(LiteralListType type) throws Exception {
        if (_trace) {
            System.out.println("visit LiteralListType " + type.getName());
        }
    }

    public void visit(LiteralIDType type) throws Exception {
        if (_trace) {
            System.out.println("visit LiteralIDType " + type.getName());
        }
    }
    public void visit(LiteralArrayWrapperType type) throws Exception {
        if (_trace) {
            System.out.println(
                "visit LiteralArrayWrapperType"
                    + type.getName()
                    + " javatype = "
                    + type.getJavaType().getName());
        }
    }

    private void visitLiteralStructuredType(
        LiteralStructuredType type,
        String header,
        boolean detailed)
        throws Exception {
        if (_trace) {
            System.out.println("visit LiteralStructuredType:" + type.getName());
        }

        boolean alreadySeen = _visitedComplexTypes.contains(type);
        if (alreadySeen) {
            return;
        }
        _visitedComplexTypes.add(type);

        if (detailed) {
            processAttributeMembers(type);
            processElementMembers(type);
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
        describe(attribute.getType());
    }

    protected void processElementMembers(LiteralStructuredType type)
        throws Exception {
        for (Iterator iter = type.getElementMembers(); iter.hasNext();) {
            LiteralElementMember element = (LiteralElementMember) iter.next();
            writeElementMember(element);
        }
    }

    protected void writeElementMember(LiteralElementMember element)
        throws Exception {
        describe(element.getType());
    }

    public void visit(LiteralArrayType type) throws Exception {
        if (_trace) {
            System.out.println("visit LiteralArrayType:" + type.getName());
        }
        describe(type.getElementType());
    }

    public void visit(LiteralFragmentType type) throws Exception {
        if (_trace) {
            System.out.println("visit LiteralFragmentType:" + type.getName());
        }
    }

    protected void describe(SOAPType type) throws Exception {
        processNamespacePackages(type);
        type.accept(this);
    }

    public void visit(SOAPArrayType type) throws Exception {
        if (_trace) {
            System.out.println("visit SOAPArrayType:" + type.getName());
        }
        describe(type.getElementType());
    }

    public void visit(SOAPCustomType type) throws Exception {
        if (_trace) {
            System.out.println("visit SOAPCustomType:" + type.getName());
        }
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        if (_trace) {
            System.out.println("visit SOAPEnumerationType:" + type.getName());
        }
        if (!_visitedSimpleTypes.contains(type)) {
            _visitedSimpleTypes.add(type);
            describe(type.getBaseType());
        }
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if (_trace) {
            System.out.println("visit SOAPSimpleType:" + type.getName());
        }
    }

    public void visit(SOAPAnyType type) throws Exception {
        if (_trace) {
            System.out.println("visit SOAPAnyType:" + type.getName());
        }
    }

    public void visit(SOAPOrderedStructureType type) throws Exception {
        if (_trace) {
            System.out.println("visit SOAPStructuredType:" + type.getName());
        }
        visitSOAPStructureType(type, "SOAP-ORDERED-STRUCTURE-TYPE", true);
    }

    public void visit(SOAPUnorderedStructureType type) throws Exception {
        if (_trace) {
            System.out.println(
                "visit SOAPUnorderedStructuredType:" + type.getName());
        }
        visitSOAPStructureType(type, "SOAP-UNORDERED-STRUCTURE-TYPE", true);
    }

    public void visit(SOAPListType type) throws Exception {
        if (_trace) {
            System.out.println(
                "visit SOAPUnorderedStructuredType:" + type.getName());
        }
    }

    public void visit(RPCRequestOrderedStructureType type) throws Exception {
        if (_trace) {
            System.out.println(
                "visit RPCRequestOrderedStructureType:" + type.getName());
        }
        visitSOAPStructureType(
            type,
            "RPC-REQUEST-ORDERED-STRUCTURE-TYPE",
            false);
    }

    public void visit(RPCRequestUnorderedStructureType type) throws Exception {
        if (_trace) {
            System.out.println(
                "visit RPCRequestUnorderedStructureType:" + type.getName());
        }
        visitSOAPStructureType(
            type,
            "RPC-REQUEST-UNORDERED-STRUCTURE-TYPE",
            false);
    }

    public void visit(RPCResponseStructureType type) throws Exception {
        if (_trace) {
            System.out.println(
                "visit RPCResponseStructureType:" + type.getName());
        }
        visitSOAPStructureType(type, "RPC-RESPONSE-STRUCTURE-TYPE", false);
    }

    private void visitSOAPStructureType(
        SOAPStructureType type,
        String header,
        boolean detailed)
        throws Exception {
        if (_trace) {
            System.out.println("visit SOAPStructureType:" + type.getName());
        }
        boolean alreadySeen = _visitedComplexTypes.contains(type);
        if (alreadySeen) {
            return;
        }
        _visitedComplexTypes.add(type);

        if (detailed) {
            if (type.getParentType() != null) {
                describe(type.getParentType());
            }
            processMembers(type);
        }
    }

    protected void processMembers(SOAPStructureType type) throws Exception {
        for (Iterator iter = type.getMembers(); iter.hasNext();) {
            SOAPStructureMember member = (SOAPStructureMember) iter.next();
            writeMember(member);
        }
    }

    protected void writeMember(SOAPStructureMember member) throws Exception {
        describe(member.getType());
    }

    /**
     * This is to record all the namespace to package mapping, using the
     * namespaceURI as a key.  This is due to jax-rpc implementation, when
     * generate artifacts from SEI, sometimes have to come up with different
     * namespaces to distinguish classes that might have same names (i.e. they
     * need to be in different packages).
     */
    private void processNamespacePackages(AbstractType type) {

        if (!(type instanceof RPCRequestOrderedStructureType
            || type instanceof RPCRequestOrderedStructureType
            || type instanceof RPCResponseStructureType)
            && (type.getName() == null
                || _visitedNSPackages.keySet().contains(
                    type.getName().getNamespaceURI()))) {
            return;
        }

        //check for well-known types (or if the type is empty)
        String uri = type.getName().getNamespaceURI();
        if ((SchemaConstants.NS_XSD).equals(uri)
            || (SOAPConstants.NS_SOAP_ENCODING).equals(uri)
            || (SOAP12Constants.NS_SOAP_ENCODING).equals(uri)
            || "".equals(uri)) {
            return;
        }

        String javaType = type.getJavaType().getName();
        String packageName = javaType.substring(0, javaType.lastIndexOf("."));
        if (_trace) {
            System.out.println("Namespace=" + uri + "; package=" + packageName);
        }

        _visitedNSPackages.put(uri, packageName);
    }

    private void processNamespacePackages(Port port) {
        QName bindingQName = (QName)port.getProperty(
            ModelProperties.PROPERTY_WSDL_PORT_TYPE_NAME);
        String uri = bindingQName.getNamespaceURI();
        String packageName = (String)_visitedNSPackages.get(uri);
        if (packageName == null) {
            ProcessorEnvironment env =
                (ProcessorEnvironment)config.getEnvironment();
            JavaInterface intf =  port.getJavaInterface();
            String className = env.getNames().customJavaTypeClassName(intf);
            packageName = className.substring(0, className.lastIndexOf("."));
            _visitedNSPackages.put(uri, packageName);
        }
    }

    private Configuration config;
    private Set _visitedComplexTypes;
    private Set _visitedSimpleTypes;
    private Set _visitedFaults;
    private Map _visitedNSPackages; //namespace - package names
    private boolean _trace = false;
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.model.literal.LiteralTypeVisitor#visit(com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType)
     */
    public void visit(LiteralAttachmentType type) throws Exception {
        // TODO Auto-generated method stub
        
    }
}
