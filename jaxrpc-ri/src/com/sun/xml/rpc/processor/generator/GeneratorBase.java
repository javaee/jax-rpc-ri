/*
 * $Id: GeneratorBase.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.ProcessorConstants;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactoryImpl;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelVisitor;
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
import com.sun.xml.rpc.processor.model.soap.SOAPAttributeMember;
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
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class GeneratorBase
    implements
        GeneratorConstants,
        ProcessorAction,
        ModelVisitor,
        SOAPTypeVisitor,
        LiteralTypeVisitor {
    protected File sourceDir;
    protected File destDir;
    protected File nonclassDestDir;
    protected ProcessorEnvironment env;
    protected Model model;
    protected Service service;
    protected IndentingWriter out;
    protected boolean encodeTypes;
    protected boolean multiRefEncoding;
    protected boolean serializeInterfaces;
    protected SerializerWriterFactory writerFactory;
    protected SOAPVersion curSOAPVersion;
    protected String JAXRPCVersion;
    protected String targetVersion;
    protected boolean generateSerializableIf;
    protected boolean donotOverride;
    protected String servicePackage;

    private LocalizableMessageFactory messageFactory;
    private Set visitedTypes;

    public GeneratorBase() {
        sourceDir = null;
        destDir = null;
        nonclassDestDir = null;
        env = null;
        model = null;
        out = null;
    }

    public void perform(
        Model model,
        Configuration config,
        Properties properties) {
        ProcessorEnvironment env =
            (ProcessorEnvironment) config.getEnvironment();
        String key = ProcessorOptions.DESTINATION_DIRECTORY_PROPERTY;
        String dirPath = properties.getProperty(key);
        File destDir = new File(dirPath);
        key = ProcessorOptions.SOURCE_DIRECTORY_PROPERTY;
        String sourcePath = properties.getProperty(key);
        File sourceDir = new File(sourcePath);
        key = ProcessorOptions.NONCLASS_DESTINATION_DIRECTORY_PROPERTY;
        String nonclassDestPath = properties.getProperty(key);
        File nonclassDestDir = new File(nonclassDestPath);

        GeneratorBase generator = getGenerator(model, config, properties);

        generator.doGeneration();
    }

    public abstract GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties);
    public abstract GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver);

    protected GeneratorBase(
        Model model,
        Configuration config,
        Properties properties) {

        this.model = model;
        this.env = (ProcessorEnvironment) config.getEnvironment();
        String key = ProcessorOptions.DESTINATION_DIRECTORY_PROPERTY;
        String dirPath = properties.getProperty(key);
        this.destDir = new File(dirPath);
        key = ProcessorOptions.SOURCE_DIRECTORY_PROPERTY;
        String sourcePath = properties.getProperty(key);
        this.sourceDir = new File(sourcePath);
        key = ProcessorOptions.NONCLASS_DESTINATION_DIRECTORY_PROPERTY;
        String nonclassDestPath = properties.getProperty(key);
        this.nonclassDestDir = new File(nonclassDestPath);
        key = ProcessorOptions.ENCODE_TYPES_PROPERTY;
        this.encodeTypes =
            Boolean.valueOf(properties.getProperty(key)).booleanValue();
        key = ProcessorOptions.MULTI_REF_ENCODING_PROPERTY;
        this.multiRefEncoding =
            Boolean.valueOf(properties.getProperty(key)).booleanValue();
        messageFactory =
            new LocalizableMessageFactory("com.sun.xml.rpc.resources.generator");
        key = ProcessorOptions.SERIALIZE_INTERFACES_PROPERTY;
        this.serializeInterfaces =
            Boolean.valueOf(properties.getProperty(key)).booleanValue();
        this.JAXRPCVersion =
            properties.getProperty(ProcessorConstants.JAXRPC_VERSION);
        this.targetVersion =
            properties.getProperty(ProcessorOptions.JAXRPC_SOURCE_VERSION);
        key = ProcessorOptions.GENERATE_SERIALIZABLE_IF;
        this.generateSerializableIf =
            Boolean.valueOf(properties.getProperty(key)).booleanValue();
        key = ProcessorOptions.DONOT_OVERRIDE_CLASSES;
        this.donotOverride =
            Boolean.valueOf(properties.getProperty(key)).booleanValue();

    }

    protected void doGeneration() {
        try {
            model.accept(this);
        } catch (Exception e) {
            throw new GeneratorException(
                "generator.nestedGeneratorError",
                new LocalizableExceptionAdapter(e));
        }
    }

    public void visit(Model model) throws Exception {
        visitedTypes = new HashSet();
        preVisitModel(model);
        visitModel(model);
        postVisitModel(model);
    }

    protected void preVisitModel(Model model) throws Exception {
    }

    protected void visitModel(Model model) throws Exception {
        env.getNames().resetPrefixFactory();
        writerFactory = new SerializerWriterFactoryImpl(env.getNames());
        Iterator services = model.getServices();
        while (services.hasNext()) {
            ((Service) services.next()).accept(this);
        }
    }

    protected void postVisitModel(Model model) throws Exception {
    }

    public void visit(Service service) throws Exception {
        preVisitService(service);
        visitService(service);
        postVisitService(service);
    }

    protected void preVisitService(Service service) throws Exception {
        servicePackage = Names.getPackageName(service);
    }

    protected void visitService(Service service) throws Exception {
        this.service = service;
        Iterator ports = service.getPorts();
        while (ports.hasNext()) {
            ((Port) ports.next()).accept(this);
        }
        this.service = null;
    }

    protected void postVisitService(Service service) throws Exception {
        Iterator extraTypes = model.getExtraTypes();
        while (extraTypes.hasNext()) {
            AbstractType type = (AbstractType) extraTypes.next();
            if (type.isSOAPType()) {
                ((SOAPType) type).accept(this);
            } else if (type.isLiteralType()) {
                ((LiteralType) type).accept(this);
            }
        }
        servicePackage = null;
    }

    public void visit(Port port) throws Exception {
        preVisitPort(port);
        visitPort(port);
        postVisitPort(port);
    }

    protected void preVisitPort(Port port) throws Exception {
        curSOAPVersion = port.getSOAPVersion();
    }

    protected void visitPort(Port port) throws Exception {
        Iterator operations = port.getOperations();
        while (operations.hasNext()) {
            ((Operation) operations.next()).accept(this);
        }
    }

    protected void postVisitPort(Port port) throws Exception {
        curSOAPVersion = null;
    }

    public void visit(Operation operation) throws Exception {
        preVisitOperation(operation);
        visitOperation(operation);
        postVisitOperation(operation);
    }

    protected void preVisitOperation(Operation operation) throws Exception {
    }

    protected void visitOperation(Operation operation) throws Exception {
        operation.getRequest().accept(this);
        if (operation.getResponse() != null)
            operation.getResponse().accept(this);
        Iterator faults = operation.getAllFaults();
        if (faults != null) {
            Fault fault;
            while (faults.hasNext()) {
                fault = (Fault) faults.next();
                fault.accept(this);
            }
        }
    }

    protected void postVisitOperation(Operation operation) throws Exception {
    }

    public void visit(Parameter param) throws Exception {
        preVisitParameter(param);
        visitParameter(param);
        postVisitParameter(param);
    }

    protected void preVisitParameter(Parameter param) throws Exception {
    }

    protected void visitParameter(Parameter param) throws Exception {
    }

    protected void postVisitParameter(Parameter param) throws Exception {
    }

    public void visit(Block block) throws Exception {
        preVisitBlock(block);
        visitBlock(block);
        postVisitBlock(block);
    }

    protected void preVisitBlock(Block block) throws Exception {
    }

    protected void visitBlock(Block block) throws Exception {
    }

    protected void postVisitBlock(Block block) throws Exception {
    }

    public void visit(Response response) throws Exception {
        preVisitResponse(response);
        visitResponse(response);
        postVisitResponse(response);
    }

    protected void preVisitResponse(Response response) throws Exception {
    }

    protected void visitResponse(Response response) throws Exception {
        Iterator iter = response.getParameters();
        AbstractType type;
        Block block;
        while (iter.hasNext()) {
            ((Parameter) iter.next()).accept(this);
        }
        iter = response.getBodyBlocks();
        while (iter.hasNext()) {
            block = (Block) iter.next();
            type = block.getType();
            if (type.isSOAPType()) {
                ((SOAPType) type).accept(this);
            } else if (type.isLiteralType()) {
                ((LiteralType) type).accept(this);
            }
            responseBodyBlock(block);
        }
        iter = response.getHeaderBlocks();
        while (iter.hasNext()) {
            block = (Block) iter.next();
            type = block.getType();
            if (type.isSOAPType()) {
                ((SOAPType) type).accept(this);
            } else if (type.isLiteralType()) {
                ((LiteralType) type).accept(this);
            }
            responseHeaderBlock(block);
        }

        //attachment
        iter = response.getAttachmentBlocks();
        while (iter.hasNext()) {
            block = (Block) iter.next();
            type = block.getType();
            if (type.isSOAPType()) {
                ((SOAPType) type).accept(this);
            } else if (type.isLiteralType()) {
                ((LiteralType) type).accept(this);
            }
            responseAttachmentBlock(block);
        }

    }

    protected void responseBodyBlock(Block block) throws Exception {
    }

    protected void responseHeaderBlock(Block block) throws Exception {
    }

    protected void responseAttachmentBlock(Block block) throws Exception {
    }

    protected void postVisitResponse(Response response) throws Exception {
    }

    public void visit(Request request) throws Exception {
        preVisitRequest(request);
        visitRequest(request);
        postVisitRequest(request);
    }

    protected void preVisitRequest(Request request) throws Exception {
    }

    protected void visitRequest(Request request) throws Exception {
        Iterator iter = request.getParameters();
        AbstractType type;
        Block block;
        while (iter.hasNext()) {
            ((Parameter) iter.next()).accept(this);
        }
        iter = request.getBodyBlocks();
        while (iter.hasNext()) {
            block = (Block) iter.next();
            type = block.getType();
            if (type.isSOAPType()) {
                ((SOAPType) type).accept(this);
            } else if (type.isLiteralType()) {
                ((LiteralType) type).accept(this);
            }
            requestBodyBlock(block);
        }
        iter = request.getHeaderBlocks();
        while (iter.hasNext()) {
            block = (Block) iter.next();
            type = block.getType();
            if (type.isSOAPType()) {
                ((SOAPType) type).accept(this);
            } else if (type.isLiteralType()) {
                ((LiteralType) type).accept(this);
            }
            requestHeaderBlock(block);
        }
    }

    protected void requestBodyBlock(Block block) throws Exception {
    }

    protected void requestHeaderBlock(Block block) throws Exception {
    }

    protected void postVisitRequest(Request request) throws Exception {
    }

    public void visit(Fault fault) throws Exception {
        preVisitFault(fault);
        visitFault(fault);
        postVisitFault(fault);
    }

    protected void preVisitFault(Fault fault) throws Exception {
    }

    protected void visitFault(Fault fault) throws Exception {
        AbstractType type = fault.getBlock().getType();
        if (type.isSOAPType()) {
            ((SOAPType) type).accept(this);
        }
    }

    protected void postVisitFault(Fault fault) throws Exception {
    }

    // SOAPType Visits
    public void visit(SOAPCustomType type) throws Exception {
        preVisitSOAPCustomType(type);
        visitSOAPCustomType(type);
        postVisitSOAPCustomType(type);
    }

    protected void preVisitSOAPCustomType(SOAPCustomType type)
        throws Exception {
    }

    protected void visitSOAPCustomType(SOAPCustomType type) throws Exception {
    }

    protected void postVisitSOAPCustomType(SOAPCustomType type)
        throws Exception {
    }

    public void visit(SOAPSimpleType type) throws Exception {
        preVisitSOAPSimpleType(type);
        visitSOAPSimpleType(type);
        postVisitSOAPSimpleType(type);
    }

    protected void preVisitSOAPSimpleType(SOAPSimpleType type)
        throws Exception {
    }

    protected void visitSOAPSimpleType(SOAPSimpleType type) throws Exception {
    }

    protected void postVisitSOAPSimpleType(SOAPSimpleType type)
        throws Exception {
    }

    public void visit(SOAPAnyType type) throws Exception {
        preVisitSOAPAnyType(type);
        visitSOAPAnyType(type);
        postVisitSOAPAnyType(type);
    }

    protected void preVisitSOAPAnyType(SOAPAnyType type) throws Exception {
    }

    protected void visitSOAPAnyType(SOAPAnyType type) throws Exception {
    }

    protected void postVisitSOAPAnyType(SOAPAnyType type) throws Exception {
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        preVisitSOAPEnumerationType(type);
        visitSOAPEnumerationType(type);
        postVisitSOAPEnumerationType(type);
    }

    protected void preVisitSOAPEnumerationType(SOAPEnumerationType type)
        throws Exception {
    }

    protected void visitSOAPEnumerationType(SOAPEnumerationType type)
        throws Exception {
    }

    protected void postVisitSOAPEnumerationType(SOAPEnumerationType type)
        throws Exception {
    }

    //xsd:list, rpc/enc
    public void visit(SOAPListType type) throws Exception {
        preVisitSOAPListType(type);
        visitSOAPListType(type);
        postVisitSOAPListType(type);
    }

    /**
     * @param type
     */
    private void postVisitSOAPListType(SOAPListType type) {
        // TODO Auto-generated method stub

    }

    /**
     * @param type
     */
    private void preVisitSOAPListType(SOAPListType type) {
        // TODO Auto-generated method stub

    }

    public void visit(SOAPArrayType type) throws Exception {
        preVisitSOAPArrayType(type);
        visitSOAPArrayType(type);
        postVisitSOAPArrayType(type);
    }

    protected void preVisitSOAPArrayType(SOAPArrayType type) throws Exception {
    }

    protected void visitSOAPArrayType(SOAPArrayType type) throws Exception {
        SOAPType elemType = type.getElementType();
        elemType.accept(this);
    }

    protected void postVisitSOAPArrayType(SOAPArrayType type)
        throws Exception {
    }

    public void visit(SOAPOrderedStructureType type) throws Exception {
        preVisitSOAPOrderedStructureType(type);
        visitSOAPOrderedStructureType(type);
        postVisitSOAPOrderedStructureType(type);
    }

    protected void preVisitSOAPOrderedStructureType(SOAPOrderedStructureType type)
        throws Exception {            
        preVisitSOAPStructureType(type);
    }

    protected void visitSOAPOrderedStructureType(SOAPOrderedStructureType type)
        throws Exception {
        visit((SOAPStructureType) type);
    }

    protected void postVisitSOAPOrderedStructureType(SOAPOrderedStructureType type)
        throws Exception {            
        postVisitSOAPStructureType(type);
    }

    public void visit(SOAPUnorderedStructureType type) throws Exception {
        preVisitSOAPUnorderedStructureType(type);
        visitSOAPUnorderedStructureType(type);
        postVisitSOAPUnorderedStructureType(type);
    }

    protected void preVisitSOAPUnorderedStructureType(SOAPUnorderedStructureType type)
        throws Exception {            
        preVisitSOAPStructureType(type);
    }

    protected void visitSOAPUnorderedStructureType(SOAPUnorderedStructureType type)
        throws Exception {
        visit((SOAPStructureType) type);
    }

    protected void postVisitSOAPUnorderedStructureType(SOAPUnorderedStructureType type)
        throws Exception {            
        postVisitSOAPStructureType(type);
    }

    public void visit(RPCRequestOrderedStructureType type) throws Exception {
        preVisitRPCRequestOrderedStructureType(type);
        visitRPCRequestOrderedStructureType(type);
        postVisitRPCRequestOrderedStructureType(type);
    }

    protected void preVisitRPCRequestOrderedStructureType(RPCRequestOrderedStructureType type)
        throws Exception {            
        preVisitSOAPStructureType(type);
    }

    protected void visitRPCRequestOrderedStructureType(RPCRequestOrderedStructureType type)
        throws Exception {            
        visit((SOAPStructureType) type);
    }

    protected void postVisitRPCRequestOrderedStructureType(RPCRequestOrderedStructureType type)
        throws Exception {
        postVisitSOAPStructureType(type);
    }

    public void visit(RPCRequestUnorderedStructureType type) throws Exception {
        preVisitRPCRequestUnorderedStructureType(type);
        visitRPCRequestUnorderedStructureType(type);
        postVisitRPCRequestUnorderedStructureType(type);
    }

    protected void preVisitRPCRequestUnorderedStructureType(RPCRequestUnorderedStructureType type)
        throws Exception {            
        preVisitSOAPStructureType(type);
    }

    protected void visitRPCRequestUnorderedStructureType(RPCRequestUnorderedStructureType type)
        throws Exception {
        visit((SOAPStructureType) type);
    }

    protected void postVisitRPCRequestUnorderedStructureType(RPCRequestUnorderedStructureType type)
        throws Exception {            
        postVisitSOAPStructureType(type);
    }

    public void visit(RPCResponseStructureType type) throws Exception {
        preVisitRPCResponseStructureType(type);
        visitRPCResponseStructureType(type);
        postVisitRPCResponseStructureType(type);
    }

    protected void preVisitRPCResponseStructureType(RPCResponseStructureType type)
        throws Exception {
        preVisitSOAPStructureType(type);
    }

    protected void visitRPCResponseStructureType(RPCResponseStructureType type)
        throws Exception {
        visit((SOAPStructureType) type);
    }
    
    protected void postVisitRPCResponseStructureType(RPCResponseStructureType type)
        throws Exception {           
        postVisitSOAPStructureType(type);
    }

    public void visit(SOAPStructureType type) throws Exception {
        preVisitSOAPStructureType(type);
        visitSOAPStructureType(type);
        postVisitSOAPStructureType(type);
    }

    protected void preVisitSOAPStructureType(SOAPStructureType type)
        throws Exception {
    }

    protected void visitSOAPStructureType(SOAPStructureType type)
        throws Exception {           
        if (!visitedTypes.contains(type)) {
            visitedTypes.add(type);
            if (type.getParentType() != null) {
                ((SOAPStructureType) type.getParentType()).accept(this);
            }
            Iterator members = type.getMembers();
            SOAPStructureMember member;
            while (members.hasNext()) {
                member = (SOAPStructureMember) members.next();
                member.getType().accept(this);
            }
            // bug fix: 4940424 
            Iterator attributes = type.getAttributeMembers();
            SOAPAttributeMember attribute;
            while (attributes.hasNext()) {
                attribute = (SOAPAttributeMember) attributes.next();
                attribute.getType().accept(this);
            }
            for (Iterator subTypes = type.getSubtypes();
                subTypes != null && subTypes.hasNext();
                ) {
                ((SOAPStructureType) subTypes.next()).accept(this);
            }
        }
    }

    protected void postVisitSOAPStructureType(SOAPStructureType type)
        throws Exception {
    }

    // LiteralType Visits    
    //xsd:list
    public void visit(LiteralListType type) throws Exception {
        preVisitLiteralListType(type);
        visitLiteralListType(type);
        postVisitLiteralListType(type);
    }

    /**
     * @param type
     */
    private void postVisitLiteralListType(LiteralListType type) {
        // TODO Auto-generated method stub

    }

    /**
     * bug fix: 4900251
     * @param type
     */
    protected void visitLiteralListType(LiteralListType type)
        throws Exception {            
        LiteralType itemType = type.getItemType();
        itemType.accept(this);

    }

    /**
     * bug fix: 4900251
     * @param type
     */
    protected void visitSOAPListType(SOAPListType type) throws Exception {
        SOAPType itemType = type.getItemType();
        itemType.accept(this);
    }

    /**
     * @param type
     */
    private void preVisitLiteralListType(LiteralListType type) {
        // TODO Auto-generated method stub

    }

    public void visit(LiteralIDType type) throws Exception {
    }

    public void visit(LiteralSimpleType type) throws Exception {
        preVisitLiteralSimpleType(type);
        visitLiteralSimpleType(type);
        postVisitLiteralSimpleType(type);
    }

    protected void preVisitLiteralSimpleType(LiteralSimpleType type)
        throws Exception {
    }

    protected void visitLiteralSimpleType(LiteralSimpleType type)
        throws Exception {
    }

    protected void postVisitLiteralSimpleType(LiteralSimpleType type)
        throws Exception {
    }

    public void visit(LiteralSequenceType type) throws Exception {
        preVisitLiteralSequenceType(type);
        visitLiteralSequenceType(type);
        postVisitLiteralSequenceType(type);
    }

    protected void preVisitLiteralSequenceType(LiteralSequenceType type)
        throws Exception {
    }

    protected void visitLiteralSequenceType(LiteralSequenceType type)
        throws Exception {            
        if (!visitedTypes.contains(type)) {
            visitedTypes.add(type);
            if (type.getParentType() != null) {
                ((LiteralStructuredType) type.getParentType()).accept(this);
            }
            Iterator attributes = type.getAttributeMembers();
            LiteralAttributeMember attribute;
            while (attributes.hasNext()) {
                attribute = (LiteralAttributeMember) attributes.next();
                attribute.getType().accept(this);
            }
            Iterator elements = type.getElementMembers();
            LiteralElementMember element;
            while (elements.hasNext()) {
                element = (LiteralElementMember) elements.next();
                element.getType().accept(this);
            }
            for (Iterator subTypes = type.getSubtypes();
                subTypes != null && subTypes.hasNext();
                ) {
                ((LiteralStructuredType) subTypes.next()).accept(this);
            }
        }
    }

    protected void postVisitLiteralSequenceType(LiteralSequenceType type)
        throws Exception {
    }

    public void visit(LiteralAllType type) throws Exception {
        preVisitLiteralAllType(type);
        visitLiteralAllType(type);
        postVisitLiteralAllType(type);
    }

    protected void preVisitLiteralAllType(LiteralAllType type)
        throws Exception {
    }

    protected void visitLiteralAllType(LiteralAllType type) throws Exception {
        if (!visitedTypes.contains(type)) {
            visitedTypes.add(type);
            if (type.getParentType() != null) {
                ((LiteralStructuredType) type.getParentType()).accept(this);
            }
            Iterator attributes = type.getAttributeMembers();
            LiteralAttributeMember attribute;
            while (attributes.hasNext()) {
                attribute = (LiteralAttributeMember) attributes.next();
                attribute.getType().accept(this);
            }
            Iterator elements = type.getElementMembers();
            LiteralElementMember element;
            while (elements.hasNext()) {
                element = (LiteralElementMember) elements.next();
                element.getType().accept(this);
            }
            for (Iterator subTypes = type.getSubtypes();
                subTypes != null && subTypes.hasNext();
                ) {
                ((LiteralStructuredType) subTypes.next()).accept(this);
            }
        }
    }

    protected void postVisitLiteralAllType(LiteralAllType type)
        throws Exception {
    }

    public void visit(LiteralArrayType type) throws Exception {
        preVisitLiteralArrayType(type);
        visitLiteralArrayType(type);
        postVisitLiteralArrayType(type);
    }

    protected void preVisitLiteralArrayType(LiteralArrayType type)
        throws Exception {
    }

    protected void visitLiteralArrayType(LiteralArrayType type)
        throws Exception {
        type.getElementType().accept(this);
    }

    protected void postVisitLiteralArrayType(LiteralArrayType type)
        throws Exception {
    }

    public void visit(LiteralArrayWrapperType type) throws Exception {
        preVisitLiteralArrayWrapperType(type);
        visitLiteralArrayWrapperType(type);
        postVisitLiteralArrayWrapperType(type);
    }

    protected void preVisitLiteralArrayWrapperType(LiteralArrayWrapperType type)
        throws Exception {
    }

    protected void visitLiteralArrayWrapperType(LiteralArrayWrapperType type)
        throws Exception {
        type.getElementMember().getType().accept(this);
    }

    protected void postVisitLiteralArrayWrapperType(LiteralArrayWrapperType type)
        throws Exception {
    }

    public void visit(LiteralFragmentType type) throws Exception {
        preVisitLiteralFragmentType(type);
        visitLiteralFragmentType(type);
        postVisitLiteralFragmentType(type);
    }

    protected void preVisitLiteralFragmentType(LiteralFragmentType type)
        throws Exception {
    }

    protected void visitLiteralFragmentType(LiteralFragmentType type)
        throws Exception {
    }

    protected void postVisitLiteralFragmentType(LiteralFragmentType type)
        throws Exception {
    }

    public void visit(LiteralEnumerationType type) throws Exception {
        preVisitLiteralEnumerationType(type);
        visitLiteralEnumerationType(type);
        postVisitLiteralEnumerationType(type);
    }

    public void preVisitLiteralEnumerationType(LiteralEnumerationType type)
        throws Exception {
    }

    public void visitLiteralEnumerationType(LiteralEnumerationType type)
        throws Exception {
        type.getBaseType().accept(this);
    }

    public void postVisitLiteralEnumerationType(LiteralEnumerationType type)
        throws Exception {
    }

    //attachment
    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.model.literal.LiteralTypeVisitor#visit(com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType)
     */
    public void visit(LiteralAttachmentType type) throws Exception {
        preVisitLiteralAttachmentType(type);
        visitLiteralAttachmentType(type);
        postVisitLiteralAttachmentType(type);
    }

    protected void preVisitLiteralAttachmentType(LiteralAttachmentType type)
        throws Exception {
    }

    protected void visitLiteralAttachmentType(LiteralAttachmentType type)
        throws Exception {
    }

    protected void postVisitLiteralAttachmentType(LiteralAttachmentType type)
        throws Exception {
    }

    protected void writeWarning(IndentingWriter p) throws IOException {
        writeWarning(p, JAXRPCVersion, targetVersion);
    }

    public static void writeWarning(IndentingWriter p, String version,
        String targetVersion) throws IOException {
        /*
         * Write boiler plate comment.
         */
        p.pln("// This class was generated by the JAXRPC SI, do not edit.");
        p.pln("// Contents subject to change without notice.");
        p.pln("// " + version);
        p.pln("// Generated source version: " + targetVersion);
        p.pln();
    }

    public void writePackage(IndentingWriter p, String classNameStr)
        throws IOException {
            
        writePackage(p, classNameStr, JAXRPCVersion, targetVersion);
    }

    public static void writePackage(
        IndentingWriter p,
        String classNameStr,
        String version,
        String sourceVersion)
        throws IOException {
            
        writeWarning(p, version, sourceVersion);
        writePackageOnly(p, classNameStr);
    }

    public static void writePackageOnly(IndentingWriter p, String classNameStr)
        throws IOException {
        int idx = classNameStr.lastIndexOf(".");
        if (idx > 0) {
            p.pln("package " + classNameStr.substring(0, idx) + ";");
            p.pln();
        }
    }

    protected void log(String msg) {
        if (env.verbose()) {
            System.out.println(
                "["
                    + Names.stripQualifier(this.getClass().getName())
                    + ": "
                    + msg
                    + "]");
        }
    }

    protected void warn(String key) {
        env.warn(messageFactory.getMessage(key));
    }

    protected void warn(String key, String arg) {
        env.warn(messageFactory.getMessage(key, arg));
    }

    protected void warn(String key, Object[] args) {
        env.warn(messageFactory.getMessage(key, args));
    }

    protected void info(String key) {
        env.info(messageFactory.getMessage(key));
    }

    protected void info(String key, String arg) {
        env.info(messageFactory.getMessage(key, arg));
    }

    protected static void fail(String key) {
        throw new GeneratorException(key);
    }

    protected static void fail(String key, String arg) {
        throw new GeneratorException(key, arg);
    }

    protected static void fail(String key, String arg1, String arg2) {
        throw new GeneratorException(key, new Object[] { arg1, arg2 });
    }

    protected static void fail(Localizable arg) {
        throw new GeneratorException("generator.nestedGeneratorError", arg);
    }

    protected static void fail(Throwable arg) {
        throw new GeneratorException(
            "generator.nestedGeneratorError",
            new LocalizableExceptionAdapter(arg));
    }

}
