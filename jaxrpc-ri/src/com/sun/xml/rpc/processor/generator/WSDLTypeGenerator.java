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
 * $Id: WSDLTypeGenerator.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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

package com.sun.xml.rpc.processor.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.ImportedDocumentInfo;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationEntry;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationType;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeOwningType;
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
import com.sun.xml.rpc.processor.modeler.rmi.LiteralSimpleTypeCreator;
import com.sun.xml.rpc.processor.modeler.rmi.SOAPSimpleTypeCreatorBase;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.JAXRPCClassFactory;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.Definitions;
import com.sun.xml.rpc.wsdl.document.Import;
import com.sun.xml.rpc.wsdl.document.Types;
import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.schema.Schema;
import com.sun.xml.rpc.wsdl.document.schema.SchemaAttribute;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;
import com.sun.xml.rpc.wsdl.document.soap.SOAP12Constants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;

/**
 *
 * @author JAX-RPC Development Team
 */
public class WSDLTypeGenerator {

    private com.sun.xml.rpc.soap.SOAPWSDLConstants soapWSDLConstants = null;

    private Model model;
    private WSDLDocument document;
    private Definitions definitions;
    private Set generatedTypes;
    private Set generatedElements;
    private Map nsSchemaMap;
    private Set actuallyImportedDocuments;
    private SOAPSimpleTypeCreatorBase soapTypes;
    private LiteralSimpleTypeCreator literalTypes;
    private SOAPVersion soapVer;
    private boolean isEncodedWsdl;
    private Properties options;

    private boolean log = false;
    private void log(String msg) {
        if (log) {
            System.out.println("[WSDLTypeModeler] " + msg);
        }
    }
    
    public WSDLTypeGenerator(Model model, WSDLDocument document,
        Properties options) {
            
        this(model, document, options, SOAPVersion.SOAP_11);
    }

    public WSDLTypeGenerator(
        Model model,
        WSDLDocument document,
        Properties options,
        SOAPVersion ver) {
        init(ver);
        this.model = model;
        this.document = document;
        this.definitions = document.getDefinitions();
        this.options = options;
        generatedTypes = new HashSet();
        generatedElements = new HashSet();
        nsSchemaMap = new HashMap();
        actuallyImportedDocuments = new HashSet();
        soapTypes =
            JAXRPCClassFactory.newInstance().createSOAPSimpleTypeCreator();
        literalTypes = new LiteralSimpleTypeCreator();
        init(ver);
    }

    private void init(SOAPVersion ver) {
        soapWSDLConstants = SOAPConstantsFactory.getSOAPWSDLConstants(ver);
        this.soapVer = ver;
    }

    protected SOAPVersion getSOAPVersion(AbstractType type) {
        if (type.isSOAPType()) {
            String ver = type.getVersion();
            if (ver.equals(SOAPVersion.SOAP_11.toString()))
                soapVer = SOAPVersion.SOAP_11;
            else if (ver.equals(SOAPVersion.SOAP_12.toString()))
                soapVer = SOAPVersion.SOAP_12;
        }
        return soapVer;
    }

    protected String getSOAPEncodingNamespace(AbstractType type) {
        if (getSOAPVersion(type).equals(SOAPVersion.SOAP_11.toString()))
            return SOAPConstants.NS_SOAP_ENCODING;
        else
            return SOAP12Constants.NS_SOAP_ENCODING;
    }

    protected String getNamespacePrefix(AbstractType type) {
        if (SOAPVersion.SOAP_12.equals(type.getVersion()))
            return "soap12-enc";
        else
            return "soap11-enc";
    }

    //needs to be updated for soap12
    /*    protected String getQNameAttrArrayTypeString(){
            if (this.soapVer.equals(SOAPVersion.SOAP_11))
                return "SOAPConstants.QNAME_ATTR_ARRAY_TYPE ";
            else
                return "SOAP12Constant.QNAME_ATTR_ARRAY_TYPE ";
            
        }*/

    protected QName getQNameAttrArrayType(AbstractType type) {
        String ver = null;
        if (type == null)
            ver = this.soapVer.toString();
        else
            ver = type.getVersion();
        if (ver.equals(SOAPVersion.SOAP_11.toString()))
            return SOAPConstants.QNAME_ATTR_ARRAY_TYPE;
        else
            return SOAP12Constants.QNAME_ATTR_ARRAY_TYPE;
    }

    public void run() throws Exception {
        isEncodedWsdl = false;
        Types types = new Types();

        // first collect all types, classifying them by targetNamespaceURI
        for (Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service) services.next();
////log ("Service: "+ service.getName());
            for (Iterator ports = service.getPorts(); ports.hasNext();) {
                Port port = (Port) ports.next();
////log ("Port: "+ port.getName());
                for (Iterator operations = port.getOperations();
                    operations.hasNext();
                    ) {
                    Operation operation = (Operation) operations.next();
//log ("Operation: "+ operation.getName());

                    processTypesInMessage(operation.getRequest());
                    processTypesInMessage(operation.getResponse());

                    // bug fix 4977230
                    Set faultSet = new TreeSet(new GeneratorUtil.FaultComparator());
                    faultSet.addAll(operation.getAllFaultsSet());
                    for (Iterator faults = faultSet.iterator(); 
                        faults != null && faults.hasNext();) {

//                    for (Iterator faults = operation.getAllFaults();
//                        faults != null && faults.hasNext();
//                        ) {
                    // end bug fix 4977230
                        Fault fault = (Fault) faults.next();
//log("Fault: "+fault.getName());
                        processFault(fault);
                    }
                }
            }
        }
        for (Iterator iter = model.getExtraTypes(); iter.hasNext();) {
            processType((AbstractType) iter.next());
        }

        // process elements (for the document/literal case)
        for (Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service) services.next();
            for (Iterator ports = service.getPorts(); ports.hasNext();) {
                Port port = (Port) ports.next();
                for (Iterator operations = port.getOperations();
                    operations.hasNext();
                    ) {
                    Operation operation = (Operation) operations.next();
                    if (operation.getStyle() == SOAPStyle.DOCUMENT) {
                        processElementsInMessage(operation.getRequest());
                        processElementsInMessage(operation.getResponse());
                    }
                    for (Iterator faults = operation.getAllFaults();
                        faults != null && faults.hasNext();
                        ) {
                        Fault fault = (Fault) faults.next();
                        processElementInFault(fault);
                    }
                }
            }
        }

        // process all top-level imports
        List importedDocumentsInReverseOrder = new ArrayList();
        for (Iterator iter = actuallyImportedDocuments.iterator();
            iter.hasNext();
            ) {
            ImportedDocumentInfo docInfo = (ImportedDocumentInfo) iter.next();
            Import anImport = new Import();
            anImport.setNamespace(docInfo.getNamespace());
            anImport.setLocation(docInfo.getLocation());
            definitions.add(anImport);
            importedDocumentsInReverseOrder.add(0, docInfo);
        }

        for (Iterator iter = nsSchemaMap.values().iterator();
            iter.hasNext();
            ) {
            Schema schema = (Schema) iter.next();
            Iterator definedEntities = schema.definedEntities();

            // only add to schema elements that define something
            if (definedEntities.hasNext()) {
                // add import statements to each and every schema
                // this is a bit of an overkill, but it's simpler than tracking
                // exactly which schemas need to import what
                // we iterate in reverse order because we want to put the <xsd:import/>
                // elements at the top of the schema, and the only operation we have there
                // is insert-at-first-position; at the same time, readability is improved
                // if we use the same ordering for <wsdl:import/> and <xsd:import/> elements

                SchemaElement schemaContent = schema.getContent();
                for (Iterator iter2 =
                    importedDocumentsInReverseOrder.iterator();
                    iter2.hasNext();
                    ) {
                    ImportedDocumentInfo docInfo =
                        (ImportedDocumentInfo) iter2.next();
                    SchemaElement importElement =
                        new SchemaElement(SchemaConstants.QNAME_IMPORT);
                    importElement.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAMESPACE,
                        docInfo.getNamespace());
                    importElement.addAttribute(
                        com
                            .sun
                            .xml
                            .rpc
                            .wsdl
                            .parser
                            .Constants
                            .ATTR_SCHEMA_LOCATION,
                        docInfo.getLocation());
                    schemaContent.insertChildAtTop(importElement);
                }

                // now add import statements for all other schemas present in the same WSDL document
                for (Iterator iter3 = nsSchemaMap.keySet().iterator();
                    iter3.hasNext();
                    ) {
                    String nsURI = (String) iter3.next();
                    if (schema.getTargetNamespaceURI().equals(nsURI)) {
                        continue;
                    }

                    Schema anotherSchema = (Schema) nsSchemaMap.get(nsURI);
                    Iterator anotherSchemaDefinedEntities =
                        anotherSchema.definedEntities();
                    if (anotherSchemaDefinedEntities.hasNext()) {
                        SchemaElement importElement =
                            new SchemaElement(SchemaConstants.QNAME_IMPORT);
                        importElement.addAttribute(
                            com
                                .sun
                                .xml
                                .rpc
                                .wsdl
                                .parser
                                .Constants
                                .ATTR_NAMESPACE,
                            nsURI);
                        schemaContent.insertChildAtTop(importElement);
                    }
                }

                // finally add an import for the SOAP-ENC namespace
                if (isEncodedWsdl) {
                    SchemaElement importElement =
                        new SchemaElement(SchemaConstants.QNAME_IMPORT);
                    importElement.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAMESPACE,
                        SOAPConstants.NS_SOAP_ENCODING);
                    schemaContent.insertChildAtTop(importElement);
                    // TODO fix this for SOAP 1.2
                    //                importElement = new SchemaElement(SchemaConstants.QNAME_IMPORT);
                    //                importElement.addAttribute(com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAMESPACE, SOAP12Constants.NS_SOAP_ENCODING);
                    //                schemaContent.insertChildAtTop(importElement);
                }

                types.addExtension(schema);
            }
        }

        definitions.setTypes(types);

    }

    private void processTypesInMessage(Message message) throws Exception {
        if (message == null) {
            return;
        }

        for (Iterator iter = message.getBodyBlocks(); iter.hasNext();) {
            Block block = (Block) iter.next();
//log("Block: " +block.getName());
            AbstractType type = block.getType();
//log("BlockType: "+type.getName());
            processType(type);
        }

        for (Iterator iter = message.getHeaderBlocks(); iter.hasNext();) {
            Block block = (Block) iter.next();
//log("HeaderBlock: " + block.getName());
            AbstractType type = block.getType();
//log("HeaderBlockType: "+type.getName());
            processType(type);
        }
        for (Iterator iter = message.getParameters(); iter.hasNext();) {
            Parameter param = (Parameter) iter.next();
//log("Parameter: " + param.getName());
            AbstractType type = param.getType();
//log("ParamType: "+type.getName());
            processType(type);
        }
    }

    private void processElementsInMessage(Message message) throws Exception {
        if (message == null) {
            return;
        }

        for (Iterator iter = message.getBodyBlocks(); iter.hasNext();) {
            Block block = (Block) iter.next();
//log("MessageBlock: "+block.getName());

            QName name = block.getName();
            AbstractType type = block.getType();
//log("MessageBlockType: "+type.getName());
            if (type.isLiteralType()) {
                processElement(name, (LiteralType) type);
            }
        }
    }

    private void processElementInFault(Fault fault) throws Exception {
//log("FaultElement: " + fault.getName());
        if (fault.getElementName() != null
            && fault.getBlock().getType().isLiteralType()) {
//log("FaultElementName: " + fault.getElementName());
            processElement(
                fault.getElementName(),
                (LiteralType) fault.getBlock().getType());
        }
    }

    private void processType(AbstractType type) throws Exception {
//log("ProcessType: " + type.getName());
        boolean isReqOrRespStruct =
            (type instanceof RPCRequestOrderedStructureType
                || type instanceof RPCRequestOrderedStructureType
                || type instanceof RPCResponseStructureType);
        if (!isReqOrRespStruct
            && (type.getName() == null
                || generatedTypes.contains(type.getName()))) {
            return;
        }

        // check for well-known types
        if (type.getName().getNamespaceURI().equals(SchemaConstants.NS_XSD)
            || type.getName().getNamespaceURI().equals(
                SOAPConstants.NS_SOAP_ENCODING)
            || type.getName().getNamespaceURI().equals(
                SOAP12Constants.NS_SOAP_ENCODING)) {
            return;
        }

        ImportedDocumentInfo docInfo =
            (ImportedDocumentInfo) model.getImportedDocument(
                type.getName().getNamespaceURI());
        if (docInfo != null
            && docInfo.getType() == ImportedDocumentInfo.SCHEMA_DOCUMENT) {
            // right now we assume an imported schema will take care of all types in that namespace
            actuallyImportedDocuments.add(docInfo);
            return;
        }

        Schema schema =
            (Schema) nsSchemaMap.get(type.getName().getNamespaceURI());
        if (schema == null) {
            schema = new Schema(document);
            schema.setTargetNamespaceURI(type.getName().getNamespaceURI());
            SchemaElement schemaElement =
                new SchemaElement(SchemaConstants.QNAME_SCHEMA);
            schemaElement.addAttribute(
                com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TARGET_NAMESPACE,
                schema.getTargetNamespaceURI());
            schemaElement.addPrefix("", SchemaConstants.NS_XSD);
            schemaElement.addPrefix("wsdl", WSDLConstants.NS_WSDL);
            schemaElement.addPrefix("xsi", SchemaConstants.NS_XSI);
            schemaElement.addPrefix(
                getNamespacePrefix(type),
                getSOAPEncodingNamespace(type));
            schemaElement.addPrefix("tns", schema.getTargetNamespaceURI());
            schema.setContent(schemaElement);
            nsSchemaMap.put(type.getName().getNamespaceURI(), schema);
        }

        if (!isReqOrRespStruct) {
            generatedTypes.add(type.getName());
        }
        
        // assume wsdl is literal unless there is an encoded type
        if (type.isLiteralType()) {
            processType((LiteralType) type, schema);
        } else {
            isEncodedWsdl = true;
            processType((SOAPType) type, schema);
        }
    }

    private void processType(SOAPType type, final Schema schema)
        throws Exception {
//log("ProcessSOAPType: "+type.getName());
        // type must be a soap type
        type.accept(new SOAPTypeVisitor() {

            public void visit(SOAPArrayType type) throws Exception {
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());
                SchemaElement complexContent =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_CONTENT);
                complexType.addChild(complexContent);
                SchemaElement restriction =
                    new SchemaElement(SchemaConstants.QNAME_RESTRICTION);
                complexContent.addChild(restriction);
                restriction.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    getNamespacePrefix(type) + ":Array");

                // this code is for "encoded-style" arrays only
                SchemaElement attribute =
                    new SchemaElement(SchemaConstants.QNAME_ATTRIBUTE);
                restriction.addChild(attribute);
                attribute.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_REF,
                    getQNameAttrArrayType(type));

                // note: it's important that we add this element as a child of "restriction"
                // BEFORE trying to encode the value of the wsdl:arrayType attribute, or the
                // namespace declarations in force won't be considered!
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());

                SchemaAttribute wsdlArrayTypeAttribute =
                    new SchemaAttribute(
                        WSDLConstants.QNAME_ATTR_ARRAY_TYPE.getLocalPart());
                wsdlArrayTypeAttribute.setNamespaceURI(
                    WSDLConstants.QNAME_ATTR_ARRAY_TYPE.getNamespaceURI());
                // encode the wsdl:arrayType string
                String arrayTypeString =
                    attribute.asString(type.getElementType().getName()) + "[]";
                wsdlArrayTypeAttribute.setValue(arrayTypeString);
                attribute.addAttribute(wsdlArrayTypeAttribute);

                processType(type.getElementType());
            }

            public void visit(SOAPCustomType type) throws Exception {
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());
                SchemaElement complexContent =
                    new SchemaElement(SchemaConstants.QNAME_SEQUENCE);
                complexType.addChild(complexContent);
                SchemaElement restriction =
                    new SchemaElement(SchemaConstants.QNAME_ANY);
                complexContent.addChild(restriction);
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());
            }

            public void visit(SOAPEnumerationType type) throws Exception {
                SchemaElement simpleType =
                    new SchemaElement(SchemaConstants.QNAME_SIMPLE_TYPE);
                schema.getContent().addChild(simpleType);
                simpleType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());

                SchemaElement restriction =
                    new SchemaElement(SchemaConstants.QNAME_RESTRICTION);
                simpleType.addChild(restriction);
                restriction.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    type.getBaseType().getName());
                JavaEnumerationType javaType =
                    (JavaEnumerationType) type.getJavaType();
                for (Iterator iter = javaType.getEntries(); iter.hasNext();) {
                    JavaEnumerationEntry entry =
                        (JavaEnumerationEntry) iter.next();
                    SchemaElement enumeration =
                        new SchemaElement(SchemaConstants.QNAME_ENUMERATION);
                    enumeration.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_VALUE,
                        entry.getLiteralValue());
                    restriction.addChild(enumeration);
                }
                schema.defineEntity(
                    simpleType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());
            }

            public void visit(SOAPSimpleType type) throws Exception {
                if (type
                    .getName()
                    .getNamespaceURI()
                    .equals(InternalEncodingConstants.JAXRPC_URI)) {
                    if (type
                        .getName()
                        .equals(
                            InternalEncodingConstants
                                .QNAME_TYPE_JAX_RPC_MAP_ENTRY)) {
                        writeJAXRpcMapEntryType();
                    } else if (
                        type.getName().equals(
                            InternalEncodingConstants.QNAME_TYPE_MAP)) {
                        writeMapType(type);
                        processType(soapTypes.JAX_RPC_MAP_ENTRY_SOAPTYPE);
                    } else if (
                        type.getName().equals(
                            InternalEncodingConstants.QNAME_TYPE_HASH_MAP)
                            || type.getName().equals(
                                InternalEncodingConstants.QNAME_TYPE_TREE_MAP)
                            || type.getName().equals(
                                InternalEncodingConstants.QNAME_TYPE_HASHTABLE)
                            || type.getName().equals(
                                InternalEncodingConstants
                                    .QNAME_TYPE_PROPERTIES)) {
                        writeTypeSubtype(
                            type,
                            InternalEncodingConstants.QNAME_TYPE_MAP);
                        processType(soapTypes.MAP_SOAPTYPE);
                    } else if (
                        type.getName().equals(
                            InternalEncodingConstants.QNAME_TYPE_COLLECTION)) {
                        writeCollectionType(type);
                    } else if (
                        type.getName().equals(
                            InternalEncodingConstants.QNAME_TYPE_LIST)
                            || type.getName().equals(
                                InternalEncodingConstants.QNAME_TYPE_SET)) {
                        writeTypeSubtype(
                            type,
                            InternalEncodingConstants.QNAME_TYPE_COLLECTION);
                        processType(soapTypes.COLLECTION_SOAPTYPE);
                    } else if (
                        type.getName().equals(
                            InternalEncodingConstants.QNAME_TYPE_ARRAY_LIST)
                            || type.getName().equals(
                                InternalEncodingConstants.QNAME_TYPE_VECTOR)
                            || type.getName().equals(
                                InternalEncodingConstants.QNAME_TYPE_STACK)
                            || type.getName().equals(
                                InternalEncodingConstants
                                    .QNAME_TYPE_LINKED_LIST)) {
                        writeTypeSubtype(
                            type,
                            InternalEncodingConstants.QNAME_TYPE_LIST);
                        processType(soapTypes.LIST_SOAPTYPE);
                    } else if (
                        type.getName().equals(
                            InternalEncodingConstants.QNAME_TYPE_HASH_SET)
                            || type.getName().equals(
                                InternalEncodingConstants
                                    .QNAME_TYPE_TREE_SET)) {
                        writeTypeSubtype(
                            type,
                            InternalEncodingConstants.QNAME_TYPE_SET);
                        processType(soapTypes.SET_SOAPTYPE);
                    } else {
                        // add definitions for special JAX-RPC RI types
                        SchemaElement simpleType =
                            new SchemaElement(
                                SchemaConstants.QNAME_SIMPLE_TYPE);
                        schema.getContent().addChild(simpleType);
                        simpleType.addAttribute(
                            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                            type.getName().getLocalPart());
                        SchemaElement restriction =
                            new SchemaElement(
                                SchemaConstants.QNAME_RESTRICTION);
                        simpleType.addChild(restriction);
                        // base type is normally binary, except for XML documents, in which case it's string
                        QName baseType =
                            SchemaConstants.QNAME_TYPE_BASE64_BINARY;
                        if (type
                            .getName()
                            .equals(
                                InternalEncodingConstants.QNAME_TYPE_SOURCE)) {
                            baseType = SchemaConstants.QNAME_TYPE_STRING;
                        }
                        restriction.addAttribute(
                            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                            baseType);
                        schema.defineEntity(
                            simpleType,
                            SchemaKinds.XSD_TYPE,
                            type.getName());
                    }
                }
            }

            private void writeJAXRpcMapEntryType() throws Exception {
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    InternalEncodingConstants
                        .QNAME_TYPE_JAX_RPC_MAP_ENTRY
                        .getLocalPart());
                SchemaElement sequenceParent = complexType;
                SchemaElement sequence =
                    new SchemaElement(SchemaConstants.QNAME_SEQUENCE);
                sequenceParent.addChild(sequence);
                SchemaElement element =
                    new SchemaElement(SchemaConstants.QNAME_ELEMENT);
                sequence.addChild(element);
                element.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    InternalEncodingConstants
                        .JAX_RPC_MAP_ENTRY_KEY_NAME
                        .getLocalPart());
                element.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TYPE,
                    SchemaConstants.QNAME_TYPE_URTYPE);
                element = new SchemaElement(SchemaConstants.QNAME_ELEMENT);
                sequence.addChild(element);
                element.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    InternalEncodingConstants
                        .JAX_RPC_MAP_ENTRY_VALUE_NAME
                        .getLocalPart());
                element.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TYPE,
                    SchemaConstants.QNAME_TYPE_URTYPE);
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    InternalEncodingConstants.QNAME_TYPE_JAX_RPC_MAP_ENTRY);
            }

            private void writeMapType(AbstractType type) throws Exception {
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    InternalEncodingConstants.QNAME_TYPE_MAP.getLocalPart());
                SchemaElement complexContent =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_CONTENT);
                complexType.addChild(complexContent);
                SchemaElement restriction =
                    new SchemaElement(SchemaConstants.QNAME_RESTRICTION);
                complexContent.addChild(restriction);
                restriction.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    getNamespacePrefix(type) + ":Array");

                // this code is for "encoded-style" arrays only
                SchemaElement attribute =
                    new SchemaElement(SchemaConstants.QNAME_ATTRIBUTE);
                restriction.addChild(attribute);
                //kw problem need type or namespace
                attribute.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_REF,
                    getQNameAttrArrayType(null));

                // note: it's important that we add this element as a child of "restriction"
                // BEFORE trying to encode the value of the wsdl:arrayType attribute, or the
                // namespace declarations in force won't be considered!
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    InternalEncodingConstants.QNAME_TYPE_MAP);

                SchemaAttribute wsdlArrayTypeAttribute =
                    new SchemaAttribute(
                        WSDLConstants.QNAME_ATTR_ARRAY_TYPE.getLocalPart());
                wsdlArrayTypeAttribute.setNamespaceURI(
                    WSDLConstants.QNAME_ATTR_ARRAY_TYPE.getNamespaceURI());
                // encode the wsdl:arrayType string
                String arrayTypeString =
                    attribute.asString(
                        InternalEncodingConstants.QNAME_TYPE_JAX_RPC_MAP_ENTRY)
                        + "[]";
                wsdlArrayTypeAttribute.setValue(arrayTypeString);
                attribute.addAttribute(wsdlArrayTypeAttribute);
            }

            private void writeTypeSubtype(
                AbstractType type,
                QName superTypeName)
                throws Exception {
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());
                SchemaElement complexContent =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_CONTENT);
                complexType.addChild(complexContent);
                SchemaElement extension =
                    new SchemaElement(SchemaConstants.QNAME_EXTENSION);
                complexContent.addChild(extension);
                extension.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    superTypeName);
                SchemaElement sequenceParent = extension;
                SchemaElement sequence =
                    new SchemaElement(SchemaConstants.QNAME_SEQUENCE);
                sequenceParent.addChild(sequence);
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());
            }

            private void writeCollectionType(AbstractType type)
                throws Exception {
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());
                SchemaElement complexContent =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_CONTENT);
                complexType.addChild(complexContent);
                SchemaElement restriction =
                    new SchemaElement(SchemaConstants.QNAME_RESTRICTION);
                complexContent.addChild(restriction);
                restriction.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    getNamespacePrefix(type) + ":Array");

                // this code is for "encoded-style" arrays only
                SchemaElement attribute =
                    new SchemaElement(SchemaConstants.QNAME_ATTRIBUTE);
                restriction.addChild(attribute);
                attribute.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_REF,
                    getQNameAttrArrayType(type));

                // note: it's important that we add this element as a child of "restriction"
                // BEFORE trying to encode the value of the wsdl:arrayType attribute, or the
                // namespace declarations in force won't be considered!
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());

                SchemaAttribute wsdlArrayTypeAttribute =
                    new SchemaAttribute(
                        WSDLConstants.QNAME_ATTR_ARRAY_TYPE.getLocalPart());
                wsdlArrayTypeAttribute.setNamespaceURI(
                    WSDLConstants.QNAME_ATTR_ARRAY_TYPE.getNamespaceURI());
                // encode the wsdl:arrayType string
                String arrayTypeString =
                    attribute.asString(SchemaConstants.QNAME_TYPE_URTYPE)
                        + "[]";
                wsdlArrayTypeAttribute.setValue(arrayTypeString);
                attribute.addAttribute(wsdlArrayTypeAttribute);
            }

            public void visit(SOAPAnyType type) throws Exception {
            }

            public void visit(SOAPListType type) throws Exception {
                // TODO
            }
            public void visit(SOAPOrderedStructureType type) throws Exception {
                visit((SOAPStructureType) type);
            }

            public void visit(SOAPUnorderedStructureType type)
                throws Exception {
                //                visit((SOAPStructureType) type);
                SOAPStructureType parentType =
                    (SOAPStructureType) type.getParentType();
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());
                SchemaElement sequenceParent = complexType;
                if (parentType != null) {
                    SchemaElement complexContent =
                        new SchemaElement(
                            SchemaConstants.QNAME_COMPLEX_CONTENT);
                    complexType.addChild(complexContent);
                    SchemaElement extension =
                        new SchemaElement(SchemaConstants.QNAME_EXTENSION);
                    complexContent.addChild(extension);
                    extension.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                        parentType.getName());
                    sequenceParent = extension;
                }
                SchemaElement sequence =
                    new SchemaElement(SchemaConstants.QNAME_ALL);
                sequenceParent.addChild(sequence);
                for (Iterator iter = type.getMembers(); iter.hasNext();) {
                    SOAPStructureMember member =
                        (SOAPStructureMember) iter.next();
                    if (member.isInherited() && type.getParentType() != null) {
                        continue;
                    }
                    SchemaElement element =
                        new SchemaElement(SchemaConstants.QNAME_ELEMENT);
                    sequence.addChild(element);
                    element.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                        member.getName().getLocalPart());
                    element.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TYPE,
                        member.getType().getName());
                }
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());

                if (parentType != null) {
                    processType((SOAPStructureType) type.getParentType());
                }

                for (Iterator iter = type.getMembers(); iter.hasNext();) {
                    SOAPStructureMember member =
                        (SOAPStructureMember) iter.next();
                    processType(member.getType());
                }
                Iterator subtypes = type.getSubtypes();
                if (subtypes != null) {
                    while (subtypes.hasNext()) {
                        processType((AbstractType) subtypes.next());
                    }
                }
            }

            public void visit(RPCRequestOrderedStructureType type)
                throws Exception {
                for (Iterator iter = type.getMembers(); iter.hasNext();) {
                    SOAPStructureMember member =
                        (SOAPStructureMember) iter.next();
                    processType(member.getType());
                }
            }

            public void visit(RPCRequestUnorderedStructureType type)
                throws Exception {
                for (Iterator iter = type.getMembers(); iter.hasNext();) {
                    SOAPStructureMember member =
                        (SOAPStructureMember) iter.next();
                    processType(member.getType());
                }
            }

            public void visit(RPCResponseStructureType type) throws Exception {
                for (Iterator iter = type.getMembers(); iter.hasNext();) {
                    SOAPStructureMember member =
                        (SOAPStructureMember) iter.next();
                    processType(member.getType());
                }
            }

            protected void visit(SOAPStructureType type) throws Exception {
                SOAPStructureType parentType =
                    (SOAPStructureType) type.getParentType();
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());
                SchemaElement sequenceParent = complexType;
                if (parentType != null) {
                    SchemaElement complexContent =
                        new SchemaElement(
                            SchemaConstants.QNAME_COMPLEX_CONTENT);
                    complexType.addChild(complexContent);
                    SchemaElement extension =
                        new SchemaElement(SchemaConstants.QNAME_EXTENSION);
                    complexContent.addChild(extension);
                    extension.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                        parentType.getName());
                    sequenceParent = extension;
                }
                SchemaElement sequence =
                    new SchemaElement(SchemaConstants.QNAME_SEQUENCE);
                sequenceParent.addChild(sequence);
                for (Iterator iter = type.getMembers(); iter.hasNext();) {
                    SOAPStructureMember member =
                        (SOAPStructureMember) iter.next();
                    if (member.isInherited() && type.getParentType() != null) {
                        continue;
                    }
                    SchemaElement element =
                        new SchemaElement(SchemaConstants.QNAME_ELEMENT);
                    sequence.addChild(element);
                    element.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                        member.getName().getLocalPart());
                    element.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TYPE,
                        member.getType().getName());
                }
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());

                if (parentType != null) {
                    processType((SOAPStructureType) type.getParentType());
                }

                for (Iterator iter = type.getMembers(); iter.hasNext();) {
                    SOAPStructureMember member =
                        (SOAPStructureMember) iter.next();
                    processType(member.getType());
                }
                Iterator subtypes = type.getSubtypes();
                if (subtypes != null) {
                    while (subtypes.hasNext()) {
                        processType((AbstractType) subtypes.next());
                    }
                }
            }
        });
    }

    private void processType(LiteralType type, final Schema schema)
        throws Exception {
//log("ProcessLiteralType: " + type.getName());
        type.accept(new LiteralTypeVisitor() {

            public void visit(LiteralArrayType type) throws Exception {
                AbstractType elemType = type.getElementType();
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());
                SchemaElement sequenceParent = complexType;
                SchemaElement sequence =
                    new SchemaElement(SchemaConstants.QNAME_SEQUENCE);
                sequenceParent.addChild(sequence);
                SchemaElement element =
                    new SchemaElement(SchemaConstants.QNAME_ELEMENT);
                sequence.addChild(element);
                element.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    elemType.getName().getLocalPart());
                element.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TYPE,
                    elemType.getName());
                if (elemType.isNillable()) {
                    element.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NILLABLE,
                        "true");
                }
                element.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_MIN_OCCURS,
                    "0");
                element.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_MAX_OCCURS,
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTRVALUE_UNBOUNDED);
                schema.defineEntity(
                    complexType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());

                processType(elemType);
            }

            public void visit(LiteralArrayWrapperType type) throws Exception {
                visitStructuredType(SchemaConstants.QNAME_SEQUENCE, type);
            }

            public void visit(LiteralSimpleType type) throws Exception {
                // TODO
            }

            public void visit(LiteralListType type) throws Exception {
                // TODO
            }

            public void visit(LiteralIDType type) throws Exception {
                // TODO
            }

            public void visit(LiteralSequenceType type) throws Exception {
                visitStructuredType(SchemaConstants.QNAME_SEQUENCE, type);
            }

            public void visit(LiteralAllType type) throws Exception {
                visitStructuredType(SchemaConstants.QNAME_ALL, type);
            }

            public void visit(LiteralEnumerationType type) throws Exception {
                SchemaElement simpleType =
                    new SchemaElement(SchemaConstants.QNAME_SIMPLE_TYPE);
                schema.getContent().addChild(simpleType);
                simpleType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());

                SchemaElement restriction =
                    new SchemaElement(SchemaConstants.QNAME_RESTRICTION);
                simpleType.addChild(restriction);
                restriction.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    type.getBaseType().getName());
                JavaEnumerationType javaType =
                    (JavaEnumerationType) type.getJavaType();
                for (Iterator iter = javaType.getEntries(); iter.hasNext();) {
                    JavaEnumerationEntry entry =
                        (JavaEnumerationEntry) iter.next();
                    SchemaElement enumeration =
                        new SchemaElement(SchemaConstants.QNAME_ENUMERATION);
                    enumeration.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_VALUE,
                        entry.getLiteralValue());
                    restriction.addChild(enumeration);
                }
                schema.defineEntity(
                    simpleType,
                    SchemaKinds.XSD_TYPE,
                    type.getName());
            }

            public void visit(LiteralFragmentType type) throws Exception {
                SchemaElement complexType =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                schema.getContent().addChild(complexType);
                complexType.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                    type.getName().getLocalPart());

                SchemaElement complexContent =
                    new SchemaElement(SchemaConstants.QNAME_COMPLEX_CONTENT);
                complexType.addChild(complexContent);
                SchemaElement extension =
                    new SchemaElement(SchemaConstants.QNAME_EXTENSION);
                complexContent.addChild(extension);
                extension.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    SchemaConstants.QNAME_TYPE_URTYPE);
                SchemaElement sequence =
                    new SchemaElement(SchemaConstants.QNAME_SEQUENCE);
                extension.addChild(sequence);
            }

            protected void visitStructuredType(
                QName compositor,
                LiteralStructuredType type)
                throws Exception {
                LiteralAttributeOwningType parentType =
                    (LiteralStructuredType) type.getParentType();
                if (!type.isRpcWrapper()) {
                    SchemaElement complexType =
                        new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
                    schema.getContent().addChild(complexType);
                    complexType.addAttribute(
                        com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                        type.getName().getLocalPart());
                    SchemaElement sequenceParent = complexType;
                    if (parentType != null) {
                        SchemaElement complexContent =
                            new SchemaElement(
                                SchemaConstants.QNAME_COMPLEX_CONTENT);
                        complexType.addChild(complexContent);
                        SchemaElement extension =
                            new SchemaElement(SchemaConstants.QNAME_EXTENSION);
                        complexContent.addChild(extension);
                        extension.addAttribute(
                            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                            parentType.getName());
                        sequenceParent = extension;
                    }
                    SchemaElement sequence = new SchemaElement(compositor);
                    sequenceParent.addChild(sequence);
                    for (Iterator iter = type.getElementMembers();
                        iter.hasNext();
                        ) {
                        LiteralElementMember member =
                            (LiteralElementMember) iter.next();
                        if (member.isInherited()
                            && type.getParentType() != null) {
                            continue;
                        }
                        SchemaElement element =
                            new SchemaElement(SchemaConstants.QNAME_ELEMENT);
                        sequence.addChild(element);
                        element.addAttribute(
                            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                            member.getName().getLocalPart());
                        element.addAttribute(
                            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TYPE,
                            member.getType().getName());
                        if (member.isNillable()) {
                            element.addAttribute(
                                com
                                    .sun
                                    .xml
                                    .rpc
                                    .wsdl
                                    .parser
                                    .Constants
                                    .ATTR_NILLABLE,
                                "true");
                        }
                        if (member.isRepeated()) {
                            element.addAttribute(
                                com
                                    .sun
                                    .xml
                                    .rpc
                                    .wsdl
                                    .parser
                                    .Constants
                                    .ATTR_MIN_OCCURS,
                                "0");
                            element.addAttribute(
                                com
                                    .sun
                                    .xml
                                    .rpc
                                    .wsdl
                                    .parser
                                    .Constants
                                    .ATTR_MAX_OCCURS,
                                com
                                    .sun
                                    .xml
                                    .rpc
                                    .wsdl
                                    .parser
                                    .Constants
                                    .ATTRVALUE_UNBOUNDED);
                        }
                    }
                    schema.defineEntity(
                        complexType,
                        SchemaKinds.XSD_TYPE,
                        type.getName());
                }

                for (Iterator iter = type.getElementMembers();
                    iter.hasNext();
                    ) {
                    LiteralElementMember member =
                        (LiteralElementMember) iter.next();
                    processType(member.getType());
                }

                for (Iterator iter = type.getAttributeMembers();
                    iter.hasNext();
                    ) {
                    LiteralAttributeMember member =
                        (LiteralAttributeMember) iter.next();
                    processType(member.getType());
                }
                if (parentType != null) {
                    processType((LiteralStructuredType) type.getParentType());
                }

                Iterator subtypes = type.getSubtypes();
                if (subtypes != null) {
                    while (subtypes.hasNext()) {
                        processType((AbstractType) subtypes.next());
                    }
                }
            }

            public void visit(LiteralAttachmentType type) throws Exception {
                // TODO Auto-generated method stub
                
            }
        });
    }

    private void processFault(Fault fault) throws Exception {
//log("ProcessFault: " + fault.getName());
        AbstractType type = fault.getBlock().getType();
        AbstractType faultType;
        if (type instanceof SOAPStructureType
            || type instanceof LiteralStructuredType)
            faultType = type;
        else
            faultType = (AbstractType) fault.getJavaException().getOwner();
//log("ProcessFault type: " + faultType.getName());
        if (faultType.getName() == null
            || generatedTypes.contains(faultType.getName())) {
            return;
        }
        // check for well-known types
        if ((type.getName().getNamespaceURI().equals(SchemaConstants.NS_XSD)
            || (type
                .getName()
                .getNamespaceURI()
                .equals(SOAP12Constants.NS_SOAP_ENCODING)
                || type.getName().getNamespaceURI().equals(
                    SOAPConstants.NS_SOAP_ENCODING)))
            && fault.getAllFaults() == null) {
            return;
        }

        ImportedDocumentInfo docInfo =
            (ImportedDocumentInfo) model.getImportedDocument(
                type.getName().getNamespaceURI());
        if (docInfo != null
            && docInfo.getType() == ImportedDocumentInfo.SCHEMA_DOCUMENT) {
            // right now we assume an imported schema will take care of all types in that namespace
            actuallyImportedDocuments.add(docInfo);
            return;
        }

        Schema schema =
            (Schema) nsSchemaMap.get(faultType.getName().getNamespaceURI());
        if (schema == null) {
            schema = new Schema(document);
            schema.setTargetNamespaceURI(type.getName().getNamespaceURI());
            SchemaElement schemaElement =
                new SchemaElement(SchemaConstants.QNAME_SCHEMA);
            schemaElement.addAttribute(
                com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TARGET_NAMESPACE,
                schema.getTargetNamespaceURI());
            schemaElement.addPrefix("", SchemaConstants.NS_XSD);
            schemaElement.addPrefix("wsdl", WSDLConstants.NS_WSDL);
            schemaElement.addPrefix("xsi", SchemaConstants.NS_XSI);
            schemaElement.addPrefix(
                getNamespacePrefix(type),
                getSOAPEncodingNamespace(type));
            schemaElement.addPrefix("tns", schema.getTargetNamespaceURI());
            schema.setContent(schemaElement);
            nsSchemaMap.put(type.getName().getNamespaceURI(), schema);
        }
        generatedTypes.add(type.getName());
        processFault(fault, schema);
    }

    private void processFault(Fault fault, final Schema schema)
        throws Exception {
        Fault parentFault = fault.getParentFault();
        AbstractType type = fault.getBlock().getType();
        AbstractType parentType =
            (parentFault != null) ? parentFault.getBlock().getType() : null;

        SchemaElement complexType =
            new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
        schema.getContent().addChild(complexType);
        boolean deserializeToDetail = false;
        if (type instanceof SOAPStructureType
            || type instanceof LiteralStructuredType) {
            complexType.addAttribute(
                com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                type.getName().getLocalPart());
            deserializeToDetail =
                SOAPObjectSerializerGenerator.deserializeToDetail(type);
        } else {
            JavaException javaException = fault.getJavaException();
            String localName = null;
            if (type instanceof SOAPType) {
                localName =
                    ((SOAPType) javaException.getOwner())
                        .getName()
                        .getLocalPart();
            } else if (type instanceof LiteralType) {
                localName =
                    ((LiteralType) javaException.getOwner())
                        .getName()
                        .getLocalPart();
            } else {
                throw new GeneratorException(
                    "generator.unsupported.type.encountered",
                    new Object[] {
                        type.getName().getLocalPart(),
                        type.getName().getNamespaceURI()});
            }
            complexType.addAttribute(
                com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
                localName);
        }
        SchemaElement sequenceParent = complexType;
        if (parentType != null) {
            SchemaElement complexContent =
                new SchemaElement(SchemaConstants.QNAME_COMPLEX_CONTENT);
            complexType.addChild(complexContent);
            SchemaElement extension =
                new SchemaElement(SchemaConstants.QNAME_EXTENSION);
            complexContent.addChild(extension);
            if (parentType instanceof SOAPStructureType)
                extension.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    parentType.getName());
            else {
                JavaException javaException = parentFault.getJavaException();
                QName ownerName =
                    ((AbstractType) javaException.getOwner()).getName();
                extension.addAttribute(
                    com.sun.xml.rpc.wsdl.parser.Constants.ATTR_BASE,
                    ownerName);
            }
            sequenceParent = extension;
        }
        SchemaElement sequence =
            new SchemaElement(SchemaConstants.QNAME_SEQUENCE);
        sequenceParent.addChild(sequence);
        if (type instanceof SOAPStructureType) {
            for (Iterator iter = ((SOAPStructureType) type).getMembers();
                iter.hasNext();
                ) {
                SOAPStructureMember member = (SOAPStructureMember) iter.next();
                if (member.isInherited() && parentFault != null) {
                    continue;
                }
                // bug fix: 4977230
                addChildElement(sequence, member.getName(), member.getType(),
                    VersionUtil.isVersion11(options.getProperty(
                                        ProcessorOptions.JAXRPC_SOURCE_VERSION)),
                    false);                
            }
            schema.defineEntity(
                complexType,
                SchemaKinds.XSD_TYPE,
                type.getName());
        } else if (type instanceof LiteralStructuredType) {
            for (Iterator iter =
                ((LiteralStructuredType) type).getElementMembers();
                iter.hasNext();
                ) {
                LiteralElementMember member =
                    (LiteralElementMember) iter.next();
                if (member.isInherited() && parentFault != null) {
                    continue;
                }
                addChildElement(sequence, member.getName(), member.getType(),
                                member.isNillable(), member.isRepeated());

            }
            schema.defineEntity(
                complexType,
                SchemaKinds.XSD_TYPE,
                type.getName());
        } else {
            JavaException javaException = fault.getJavaException();
            AbstractType ownerType = (AbstractType) javaException.getOwner();
            if (ownerType instanceof SOAPType) {
                SOAPStructureType soapStruct = (SOAPStructureType) ownerType;
                Iterator iter = soapStruct.getMembers();
                SOAPStructureMember member = (SOAPStructureMember) iter.next();
                if (!member.isInherited() || fault.getParentFault() == null) {
                    // bug fix: 4977230
                    addChildElement(
                        sequence,
                        member.getName(),
                        member.getType(),
                        model.getSource().equals(VersionUtil.JAXRPC_VERSION_11),
                        false);  
                }
            } else {
                LiteralSequenceType literalStruct =
                    (LiteralSequenceType) ownerType;
                Iterator iter = literalStruct.getElementMembers();
                LiteralElementMember member =
                    (LiteralElementMember) iter.next();
                if (fault.getParentFault() == null) {
                    addChildElement(
                        sequence,
                        member.getName(),
                        member.getType(),
                        member.isNillable(),
                        member.isRepeated());
                }
            }
            schema.defineEntity(
                complexType,
                SchemaKinds.XSD_TYPE,
                ownerType.getName());
        }

        if (parentType != null) {
            processFault(parentFault);
        }
        // bug fix: 4977230
        Iterator subFaults = fault.getSortedSubfaults();
        while (subFaults.hasNext()) {
            Fault subFault = (Fault)subFaults.next();
            processFault(subFault);
        }        
        if (type instanceof SOAPStructureType) {
            for (Iterator iter = ((SOAPStructureType) type).getMembers();
                iter.hasNext();
                ) {
                SOAPStructureMember member = (SOAPStructureMember) iter.next();
                processType(member.getType());
            }
            Iterator subtypes = ((SOAPStructureType) type).getSubtypes();
            if (subtypes != null) {
                while (subtypes.hasNext()) {
                    processType((AbstractType) subtypes.next());
                }
            }
        } else if (type instanceof LiteralStructuredType) {
            for (Iterator iter =
                ((LiteralStructuredType) type).getElementMembers();
                iter.hasNext();
                ) {
                LiteralElementMember member =
                    (LiteralElementMember) iter.next();
                processType(member.getType());
            }
            Iterator subtypes = ((LiteralStructuredType) type).getSubtypes();
            if (subtypes != null) {
                while (subtypes.hasNext()) {
                    processType((AbstractType) subtypes.next());
                }
            }
        }
    }

    private void addChildElement(
        SchemaElement sequence,
        QName name,
        AbstractType type,
        boolean isNillable,
        boolean isRepeated) {
            
        SchemaElement element =
            new SchemaElement(SchemaConstants.QNAME_ELEMENT);
        sequence.addChild(element);
        element.addAttribute(
            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
            name.getLocalPart());
        element.addAttribute(
            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TYPE,
            type.getName());
        if (isNillable && !isRepeated) {
            element.addAttribute(
                com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NILLABLE,
                "true");
        }
        // bug fix: 6067119
        if (isRepeated) {
            element.addAttribute(
                com.sun.xml.rpc.wsdl.parser.Constants.ATTR_MIN_OCCURS,
                "0");
            element.addAttribute(
                com.sun.xml.rpc.wsdl.parser.Constants.ATTR_MAX_OCCURS,
                com.sun.xml.rpc.wsdl.parser.Constants.ATTRVALUE_UNBOUNDED);
        }
    }

    private void processElement(QName name, LiteralType type)
        throws Exception {
        if (generatedElements.contains(name))
            return;
        // check for well-known elements
        if (name.getNamespaceURI().equals(SchemaConstants.NS_XSD)
            || name.getNamespaceURI().equals(SOAPConstants.NS_SOAP_ENCODING)
            || name.getNamespaceURI().equals(SOAP12Constants.NS_SOAP_ENCODING)) {
            return;
        }

        ImportedDocumentInfo docInfo =
            (ImportedDocumentInfo) model.getImportedDocument(
                name.getNamespaceURI());
        if (docInfo != null
            && docInfo.getType() == ImportedDocumentInfo.SCHEMA_DOCUMENT) {
            // right now we assume an imported schema will take care of all elements in that namespace
            actuallyImportedDocuments.add(docInfo);
            return;
        }

        Schema schema = (Schema) nsSchemaMap.get(name.getNamespaceURI());
        if (schema == null) {
            schema = new Schema(document);
            schema.setTargetNamespaceURI(name.getNamespaceURI());
            SchemaElement schemaElement =
                new SchemaElement(SchemaConstants.QNAME_SCHEMA);
            schemaElement.addAttribute(
                com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TARGET_NAMESPACE,
                schema.getTargetNamespaceURI());
            schemaElement.addPrefix("", SchemaConstants.NS_XSD);
            schemaElement.addPrefix("wsdl", WSDLConstants.NS_WSDL);
            schemaElement.addPrefix("xsi", SchemaConstants.NS_XSI);
            //kw problem here - what type? what namespace  --name.getNamespaceURI?
            schemaElement.addPrefix(
                getNamespacePrefix(type),
                getSOAPEncodingNamespace(type));
            //kw
            schemaElement.addPrefix("tns", schema.getTargetNamespaceURI());
            schema.setContent(schemaElement);
            nsSchemaMap.put(name.getNamespaceURI(), schema);
        }

        generatedElements.add(name);

        SchemaElement element =
            new SchemaElement(SchemaConstants.QNAME_ELEMENT);
        schema.getContent().addChild(element);
        element.addAttribute(
            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_NAME,
            name.getLocalPart());
        element.addAttribute(
            com.sun.xml.rpc.wsdl.parser.Constants.ATTR_TYPE,
            type.getName());
    }
    
}
