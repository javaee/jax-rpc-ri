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

package com.sun.xml.rpc.processor.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.HeaderFault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPEncodingConstants;
import com.sun.xml.rpc.soap.SOAPNamespaceConstants;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.soap.SOAPWSDLConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.document.soap.SOAPUse;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class StubTieGeneratorBase extends GeneratorBase {

    protected Port port;
    protected HashSet operations = null;
    protected Set types;
    protected Map portTypes;
    private String prefix;
    protected File srcFile;
    protected SOAPVersion soapVer = SOAPVersion.SOAP_11;
    protected SOAPEncodingConstants soapEncodingConstants = null;
    protected SOAPNamespaceConstants soapNamespaceConstants = null;
    protected SOAPWSDLConstants soapWSDLConstants = null;    
    private boolean genAddAttachmentMethod = false;
    private boolean genGetAttachmentMethod = false;
        
    public StubTieGeneratorBase() {
        this(SOAPVersion.SOAP_11);
    }

    public StubTieGeneratorBase(SOAPVersion ver) {
        super();
        init(ver);
    }

    protected String getPrefix() {
        return "";
    }

    protected abstract String getClassName();
    protected abstract String getStateType();
    protected abstract Message getMessageToDeserialize(Operation operation);
    protected abstract String getStateGetRequestResponseString();
    protected abstract String getInitializeAccess();
    protected abstract boolean superClassHasInitialize();

    protected StubTieGeneratorBase(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        super(model, config, properties);
        init(ver);
        prefix = getPrefix();
        srcFile = null;
    }

    protected StubTieGeneratorBase(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
        prefix = getPrefix();
        srcFile = null;
        init(SOAPVersion.SOAP_11);
    }

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
        soapNamespaceConstants =
            SOAPConstantsFactory.getSOAPNamespaceConstants(ver);
        soapWSDLConstants = SOAPConstantsFactory.getSOAPWSDLConstants(ver);
        this.soapVer = ver;
    }

    protected void preVisitModel(Model model) throws Exception {
        types = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        types = null;
    }

    protected void preVisitPort(Port port) throws Exception {
        super.preVisitPort(port);
        operations = new HashSet();
        portTypes = new HashMap();
        this.port = port;
    }

    protected void postVisitPort(Port port) throws Exception {
        writeClass();
        this.port = null;
        portTypes = null;
        operations = null;
        super.postVisitPort(port);
    }

    protected void postVisitOperation(Operation operation) throws Exception {
        operations.add(operation);
    }

    protected void responseBodyBlock(Block block) throws Exception {
        registerBlock(block);
    }

    protected void responseHeaderBlock(Block block) throws Exception {
        registerBlock(block);
    }

    protected void requestBodyBlock(Block block) throws Exception {
        registerBlock(block);
    }

    protected void requestHeaderBlock(Block block) throws Exception {
        registerBlock(block);
    }

    protected void preVisitSOAPEnumerationType(SOAPEnumerationType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
    }

    protected void preVisitSOAPArrayType(SOAPArrayType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
    }

    protected void preVisitSOAPStructureType(SOAPStructureType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
    }

    // LiteralType Visits
    protected void preVisitLiteralFragmentType(LiteralFragmentType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
    }

    private void registerBlock(Block block) {
        String key = null;
        if (block.getType().isSOAPType()) {
            key = block.getType().getJavaType().getRealName();
        } else if (block.getType().isLiteralType()) {
            key =
                block.getType().getName().toString()
                    + block.getType().getJavaType().getRealName();
        }
        if (!portTypes.containsKey(key)) {
            portTypes.put(key, block);
        }
    }

    private boolean isRegistered(AbstractType type) {
        return types.contains(type);
    }

    private void registerType(AbstractType type) {
        types.add(type);
    }

    protected void writeClass() {
        String remoteClassName = port.getJavaInterface().getName();
        String className = getClassName();
        if ((donotOverride && GeneratorUtil.classExists(env, className))) {
            log("Class " + className + " exists. Not overriding.");
            return;
        }
        srcFile =
            env.getNames().sourceFileForClass(
                className,
                className,
                sourceDir,
                env);
        /* the addition of generated files to the
           enviornment has been moved to TieGenerator
           and StubGenerator */
        //env.addGeneratedFile(srcFile);

        try {
            out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(srcFile)));
            writePackage(out, className);
            writeImports(out);
            out.pln();  
            writeClassDecl(out, className);
            out.pln();
            writeStatic(out);
            out.pln();
            writeConstructor(out, className);
            out.pln();
            writeOperations(out, remoteClassName);
            out.pln();
            writePeekFirstBodyElementMethod(out);
            out.pln();
            writeReadFirstBodyElement(out);
            out.pln();
            Map headerMap = writeReadHeaderElementMethod(out);
            out.pln();
            writeHeaderDeserializeMethods(out, headerMap.values().iterator());
            out.pln();
            writeOperationDeserializeMethods(out);
            out.pln();
            writeReadBodyFaultElement(out);
            out.pln();
            writeProcessingHookMethod(out);
            out.pln();
            writeGenericMethods(out);
            out.pln();
            writeUsesSOAPActionForDispatching(out);
            out.pln();
            writeGetOpcodeForFirstBodyElementName(out);
            out.pln();
            writeGetOpcodeForSOAPAction(out);
            out.pln();
            writeGetMethodForOpcode(out);
            out.pln();
            writeGetNamespaceDeclarationsMethod(out);
            out.pln();
            writeGetUnderstoodHeadersMethod(out);
            out.pln();
            if(genAddAttachmentMethod) {
                writeAddAttachmentMethod(out);
                out.pln();
            }
            if(genGetAttachmentMethod) {
                writeGetAttachmentMethod(out);
                out.pln();
            }
            writeHooks(out);
            writeAttachmentHooks(out);
            writeInitialize(out);
            out.pln();
            writeStaticMembers(out, headerMap);
            out.pln();
            writeUnderstoodHeadersMember(out, headerMap);
            closeSrcFile();
        } catch (IOException e) {
            fail("generator.cant.write", port.getName().getLocalPart());
        } catch (java.lang.ClassNotFoundException c) {
            fail("generator.cant.find Class");
        }
    }

    /**
     * @param out
     */
    protected void writeAttachmentHooks(IndentingWriter p) throws IOException{
    }
    
    /**
     * @param out
     */
    protected void writeHooks(IndentingWriter p) throws IOException{
    }

    protected void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.server.http.MessageContextProperties;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAP12Constants;");
        p.pln("import com.sun.xml.rpc.encoding.literal.*;");
        p.pln("import com.sun.xml.rpc.soap.streaming.*;");
        p.pln("import com.sun.xml.rpc.soap.message.*;");
        p.pln("import com.sun.xml.rpc.soap.SOAPVersion;"); //new kw
        p.pln("import com.sun.xml.rpc.soap.SOAPEncodingConstants;"); //new kw
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.namespace.QName;");
        p.pln("import java.rmi.RemoteException;");
        p.pln("import java.util.Iterator;");
        p.pln("import java.lang.reflect.*;");
        p.pln("import java.lang.Class;");
    }
    
    /*
     * Write static code
     */
    protected void writeStatic(IndentingWriter p) throws IOException {
    }
    
    protected void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        /*
         * Declare the class; implement all remote interfaces.
         */
        p.pln("public class " + Names.stripQualifier(className));
        p.pln(" {");
        p.pln();
    }

    protected void writeConstructor(IndentingWriter p, String stubClassName)
        throws IOException {
        p.pln("/*");
        p.pln(" *  public constructor");
        p.pln(" */");
        p.plnI("public " + Names.stripQualifier(stubClassName) + "() {");
        p.pOln("}");
    }

    protected void writeOperations(IndentingWriter p, String remoteClassName)
        throws IOException {
        Iterator iter = operations.iterator();
        Operation operation;
        for (int i = 0; iter.hasNext(); i++) {
            if (i > 0)
                p.pln();
            operation = (Operation) iter.next();
            if (operation.getStyle() == SOAPStyle.DOCUMENT) {
                writeDocumentLiteralOperation(p, remoteClassName, operation);
            } else {
                if (operation.getUse() == SOAPUse.LITERAL) {
                    writeRpcLiteralOperation(p, remoteClassName, operation);
                } else {
                    writeRpcEncodedOperation(p, remoteClassName, operation);
                }
            }            
        }
    }

    protected void writeRpcEncodedOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException, GeneratorException {
    }

    protected void writeRpcLiteralOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException, GeneratorException {
    }

    protected void writeDocumentLiteralOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException, GeneratorException {
    }

    protected void writePeekFirstBodyElementMethod(IndentingWriter p)
        throws IOException {
    }
    
    protected void writePreSendingHookMethod(IndentingWriter p, List operations)
    throws IOException {
    }
    
    protected void writePostSendingHook(IndentingWriter p, List operations) throws IOException{        
    }
    
    protected void writePostEnvelopeReadingHook(IndentingWriter p, List operations)
    throws IOException {
    }
    
    protected void writePreResponseWritingHook(IndentingWriter p, List operations)
    throws IOException {        
    }

    protected void writeReadFirstBodyElement(IndentingWriter p)
        throws IOException {
        Operation operationWithEmptyBody = null;

        String stateType = getStateType();
        p.pln("/*");
        p.pln(
            " *  this method deserializes the request/response structure in the body");
        p.pln(" */");
        p.plnI(
            "protected void "
                + prefix
                + "readFirstBodyElement(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, "
                + stateType
                + "  state) throws Exception {");
        p.pln("int opcode = state.getRequest().getOperationCode();");
        p.plnI("switch (opcode) {");
        Iterator operationsIter = operations.iterator();
        Operation operation;
        while (operationsIter.hasNext()) {
            operation = (Operation) operationsIter.next();
            if (!needsReadFirstBodyElementFor(operation))
                continue;
            p.plnI(
                "case "
                    + env.getNames().getOPCodeName(operation.getUniqueName())
                    + ":");
            p.pln(
                prefix
                    + "deserialize_"
                    + env.getNames().validInternalJavaIdentifier(
                        operation.getUniqueName())
                    + "(bodyReader, deserializationContext, state);");
            p.pln("break;");
            p.pO();
            //bug fix 5024031
            if(operationWithEmptyBody == null)
                operationWithEmptyBody = operationHasEmptyBody(operation);
        }
        p.plnI("default:");
        writeReadFirstBodyElementDefault(p, "opcode");
        p.pO();
        p.pOln("}"); // switch
        p.pOln("}"); // method

        if (operationWithEmptyBody != null) {
            writeHandleEmptyBody(p, operationWithEmptyBody);
        }
    }

    /**
     * @param operation
     * @return
     */
    protected Operation operationHasEmptyBody(Operation operation) {
        return null;
    }

    protected boolean needsReadFirstBodyElementFor(Operation operation) {
        return true;
    }

    protected void writeHandleEmptyBody(IndentingWriter p, Operation operation)
        throws IOException {
    }

    protected void writeReadFirstBodyElementDefault(
        IndentingWriter p,
        String state)
        throws IOException {
    }

    private void writeOperationDeserializeMethods(IndentingWriter p)
        throws IOException {
        Iterator operationsIter = operations.iterator();
        Operation operation;
        for (int i = 0; operationsIter.hasNext(); i++) {
            if (i > 0)
                p.pln();
            operation = (Operation) operationsIter.next();
            writeOperationDeserializeMethod(p, operation);
        }
    }

    private void writeOperationDeserializeMethod(
        IndentingWriter p,
        Operation operation)
        throws IOException {
        String stateType = getStateType();
        String messageName = operation.getName().getLocalPart();
        Iterator iterator;
        Message message = getMessageToDeserialize(operation);
        if (message == null)
            return;
        p.pln("/*");
        p.pln(
            " * This method deserializes the body of the "
                + messageName
                + " operation.");
        p.pln(" */");
        p.plnI(
            "private void "
                + prefix
                + "deserialize_"
                + env.getNames().validInternalJavaIdentifier(
                    operation.getUniqueName())
                + "(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, "
                + stateType
                + " state) throws Exception {");

        if (!message.getBodyBlocks().hasNext()) {
            p.pln("SOAPBlockInfo bodyBlock = new SOAPBlockInfo(null);");
            p.pln(
                "state."
                    + getStateGetRequestResponseString()
                    + "().setBody(bodyBlock);");
        } else {
            Block bodyBlock = (Block) message.getBodyBlocks().next();
            AbstractType type = bodyBlock.getType();
            String objName =
                env.getNames().getTypeMemberName(type.getJavaType());
            SerializerWriter writer =
                writerFactory.createWriter(servicePackage, type);
            String serializer = writer.serializerMemberName();

            p.plnI("java.lang.Object " + objName + "Obj =");
            p.plnI(
                serializer
                    + ".deserialize("
                    + env.getNames().getBlockQNameName(operation, bodyBlock)
                    + ",");
            p.pln("bodyReader, deserializationContext);");
            p.pO();
            p.pO();

            objName =
                env.getNames().getTypeMemberName(type.getJavaType()) + "Obj";
            p.pln();
            p.pln(
                "SOAPBlockInfo bodyBlock = new SOAPBlockInfo("
                    + env.getNames().getBlockQNameName(operation, bodyBlock)
                    + ");");
            p.pln("bodyBlock.setValue(" + objName + ");");
            p.pln(
                "state."
                    + getStateGetRequestResponseString()
                    + "().setBody(bodyBlock);");
        }

        p.pOln("}"); // method
    }

    protected void writeReadBodyFaultElement(IndentingWriter p)
        throws IOException {
    }

    protected Map writeReadHeaderElementMethod(IndentingWriter p)
        throws IOException {

        Iterator ops = operations.iterator();
        Operation operation;
        Message message;
        Iterator headers;
        boolean hasHeaders = false;
        while (!hasHeaders && ops.hasNext()) {
            operation = (Operation) ops.next();
            message = operation.getRequest();
            headers = message.getHeaderBlocks();
            hasHeaders = !hasHeaders && headers.hasNext() ? true : hasHeaders;
            message = operation.getResponse();
            headers = message != null ? message.getHeaderBlocks() : null;
            hasHeaders =
                !hasHeaders
                    && headers != null
                    && headers.hasNext() ? true : hasHeaders;
        }
        Map headerMap = new HashMap();
        if (!hasHeaders) {
            return headerMap;
        }
        String stateType = getStateType();
        p.pln("/*");
        p.pln(
            " * This method must deserialize headers. It dispatches to a read method based on the name");
        p.pln(" * of the header.");
        p.pln(" */");
        p.plnI(
            "protected boolean "
                + prefix
                + "readHeaderElement(SOAPHeaderBlockInfo headerInfo, XMLReader headerReader, SOAPDeserializationContext deserializationContext, "
                + stateType
                + " state) throws Exception {");
        ops = operations.iterator();
        boolean first = true;
        ArrayList list = new ArrayList();
        while (ops.hasNext()) {
            operation = (Operation) ops.next();

            message = operation.getRequest();
            list.clear();
            for (Iterator iter = message.getHeaderBlocks(); iter.hasNext();) {
                list.add(iter.next());
            }

            for (Iterator iter = operation.getFaults(); iter.hasNext();) {
                list.add(((Fault) iter.next()).getBlock());
            }
            //            writeHeaderChecks(p, message.getHeaderBlocks(), first, headerMap);
            writeHeaderChecks(p, list.iterator(), first, headerMap);
            first =
                first && message.getHeaderBlocks().hasNext() ? false : first;

            message = operation.getResponse();
            list.clear();
            // bug fix: 4953992
            if (message != null) {    
                for (Iterator iter = message.getHeaderBlocks(); iter.hasNext();) {
                    list.add(iter.next());
                }
            }
            for (Iterator iter = operation.getFaults(); iter.hasNext();) {
                list.add(((Fault) iter.next()).getBlock());
            }

            writeHeaderChecks(p, list.iterator(), first, headerMap);
            first =
                first && message.getHeaderBlocks().hasNext() ? false : first;
        }
        p.pln();
        p.pln("headerReader.skipElement();");
        p.pln("return false;");
        p.pOln("}"); // method
        return headerMap;
    }

    private void writeHeaderChecks(
        IndentingWriter p,
        Iterator headers,
        boolean first,
        Map headerMap)
        throws IOException {
        Block header;
        while (headers.hasNext()) {
            header = (Block) headers.next();
            if (headerMap.containsKey(header.getName())) {
                continue;
            } else {
                headerMap.put(header.getName(), header);
            }
            if (!first) {
                p.p(" else ");
            }
            first = false;
            String qname = env.getNames().getBlockQNameName(null, header);
            String uname = env.getNames().getBlockUniqueName(null, header);
            p.plnI("if (headerInfo.getName().equals(" + qname + ")) {");
            p.pln(
                prefix
                    + "deserialize_"
                    + uname
                    + "(headerInfo, headerReader, deserializationContext, state);");
            p.pln("return true;");
            p.pO("}");
        }
    }

    private void writeHeaderDeserializeMethods(
        IndentingWriter p,
        Iterator headers)
        throws IOException {
        Block header;
        for (int i = 0; headers.hasNext(); i++) {
            if (i > 0)
                p.pln();
            header = (Block) headers.next();
            writeHeaderDeserializeMethod(p, header);
        }
    }

    private void writeHeaderDeserializeMethod(IndentingWriter p, Block header)
        throws IOException {
        String javaType = header.getType().getJavaType().getName();
        String serializer =
            writerFactory
                .createWriter(servicePackage, header.getType())
                .serializerMemberName();
        String qname = env.getNames().getBlockQNameName(null, header);
        String uname = env.getNames().getBlockUniqueName(null, header);
        String stateType = getStateType();
        p.pln("/*");
        p.pln(
            " *  This method does the actual deserialization for the header: "
                + header.getName().getLocalPart()
                + ".");
        p.pln(" */");
        p.plnI(
            "private void "
                + prefix
                + "deserialize_"
                + uname
                + "(SOAPHeaderBlockInfo headerInfo, XMLReader bodyReader, SOAPDeserializationContext deserializationContext, "
                + stateType
                + " state) throws Exception {");
        p.pln("QName elementName = bodyReader.getName();");
        p.plnI("if (elementName.equals(" + qname + ")) {");
        
        //bug fix: 4980873
        String boxName = null;
        if (SimpleToBoxedUtil.isPrimitive(javaType)) {
            boxName = SimpleToBoxedUtil.getBoxedClassName(javaType);
        } else {
            boxName = javaType;
        }
        
        p.plnI(boxName + " obj =");
        p.pln(
            "("
                + boxName
                + ")"
                + serializer
                + ".deserialize("
                + qname
                + ", bodyReader, deserializationContext);");
        p.pOln("headerInfo.setValue(obj);");
        p.pln(
            "state."
                + getStateGetRequestResponseString()
                + "().add(headerInfo);");
        p.pOlnI("} else {");
        p.pln(
            "// the QName of the header is not what we expected and not a fault either");
        p.pln(
            "throw new SOAPProtocolViolationException(\"soap.unexpectedHeaderBlock\", elementName.getLocalPart());");
        p.pOln("}");
        p.pOln("}");
    }

    protected void writeProcessingHookMethod(IndentingWriter p)
        throws IOException {
    }

    public String getEncodingStyle() {
        if (port.getSOAPVersion().equals(SOAPVersion.SOAP_12.toString()))
            return "SOAP12NamespaceConstants.ENCODING";
        else
            return "SOAPNamespaceConstants.ENCODING";
    }

    public void writeGenericMethods(IndentingWriter p) throws IOException {
        writeGetDefaultEnvelopeEncodingStyle(p);
        p.pln();
        p.plnI(
            "public java.lang.String " + prefix + "getImplicitEnvelopeEncodingStyle() {");
        p.pln("return \"\";");
        p.pOln("}");
    }

    protected void writeUsesSOAPActionForDispatching(IndentingWriter p)
        throws IOException {
    }

    protected void writeGetOpcodeForFirstBodyElementName(IndentingWriter p)
        throws IOException {
    }

    protected void writeGetOpcodeForSOAPAction(IndentingWriter p)
        throws IOException {
    }

    protected void writeGetMethodForOpcode(IndentingWriter p)
        throws IOException, java.lang.ClassNotFoundException {
    }

    private void writeGetNamespaceDeclarationsMethod(IndentingWriter p)
        throws IOException {
        p.pln("/*");
        p.pln(
            " * This method returns an array containing (prefix, nsURI) pairs.");
        p.pln(" */");
        p.plnI("protected java.lang.String[] " + prefix + "getNamespaceDeclarations() {");
        p.pln("return myNamespace_declarations;");
        p.pOln("}");
    }

    protected void writeGetDefaultEnvelopeEncodingStyle(IndentingWriter p)
        throws IOException {
        boolean useLiteral = false;
        for (Iterator iter = operations.iterator(); iter.hasNext();) {
            Operation operation = (Operation) iter.next();
            if (!operation.getRequest().isBodyEncoded()) {
                useLiteral = true;
                break;
            }
        }

        if (useLiteral) {
            p.plnI(
                "protected java.lang.String "
                    + prefix
                    + "getDefaultEnvelopeEncodingStyle() {");
            p.pln("return null;");
            p.pOln("}");
        } else {
            p.plnI(
                "public java.lang.String "
                    + prefix
                    + "getDefaultEnvelopeEncodingStyle() {");
            p.pln("return " + getEncodingStyle() + ";");
            p.pOln("}");
        }
    }

    protected void writeGetUnderstoodHeadersMethod(IndentingWriter p)
        throws IOException {
        p.pln("/*");
        p.pln(
            " * This method returns an array containing the names of the headers we understand.");
        p.pln(" */");
        p.plnI("public javax.xml.namespace.QName[] " + prefix + "getUnderstoodHeaders() {");
        p.pln("return understoodHeaderNames;");
        p.pOln("}");
    }

    protected void writeInitialize(IndentingWriter p) throws IOException {
        Iterator types = portTypes.entrySet().iterator();
        Block block;
        AbstractType type;
        Map.Entry entry;

        String access = getInitializeAccess();
        p.plnI(
            access
                + " void "
                + prefix
                + "initialize(InternalTypeMappingRegistry registry) throws Exception {");
        if (superClassHasInitialize()) {
            p.pln("super." + prefix + "initialize(registry);");
        }
        while (types.hasNext()) {
            entry = (Map.Entry) types.next();
            block = (Block) entry.getValue();
            type = block.getType();
            SerializerWriter writer =
                writerFactory.createWriter(servicePackage, type);
            writer.initializeSerializer(
                p,
                env.getNames().getTypeQName(type.getName()),
                "registry");
        }
        Iterator operationsIter = operations.iterator();
        Operation operation;
        String serName;
        for (int i = 0; operationsIter.hasNext(); i++) {
            operation = (Operation) operationsIter.next();
            if (operation.getFaults().hasNext()) {
                serName =
                    env.getNames().getClassMemberName(
                        env.getNames().faultSerializerClassName(
                            servicePackage,
                            port,
                            operation));
                p.pln("((Initializable)" + serName + ").initialize(registry);");
            }
            //init headerfault serializers
            for (Iterator iter = operation.getFaults(); iter.hasNext();) {
                Fault fault = (Fault) iter.next();
                if (fault instanceof HeaderFault) {
                    type = fault.getBlock().getType();
                    SerializerWriter writer =
                        writerFactory.createWriter(servicePackage, type);
                    writer.initializeSerializer(
                        p,
                        env.getNames().getTypeQName(type.getName()),
                        "registry");
                }
            }
        }
        p.pOln("}");
    }

    protected void writeStaticMembers(IndentingWriter p, Map headerMap)
        throws IOException {
        ArrayList list = new ArrayList();
        ArrayList visited = new ArrayList();
        Iterator operationsIter = operations.iterator();
        Operation operation;
        // declare the portType
        p.p("private static final javax.xml.namespace.QName " + prefix + "portName = ");
        GeneratorUtil.writeNewQName(p, port.getName());
        p.pln(";");

        // write opcodes
        for (int i = 0; operationsIter.hasNext(); i++) {
            operation = (Operation) operationsIter.next();
            p.pln(
                "private static final int "
                    + env.getNames().getOPCodeName(operation.getUniqueName())
                    + " = "
                    + i
                    + ";");
        }
        // write FaultSerializers
        operationsIter = operations.iterator();
        Iterator faults;
        Set faultSet;
        for (int i = 0; operationsIter.hasNext(); i++) {
            operation = (Operation) operationsIter.next();
            faultSet = new TreeSet(new GeneratorUtil.FaultComparator());
            faultSet.addAll(operation.getFaultsSet());
            faults = faultSet.iterator();
            if (faults.hasNext()) {
                declareStaticFaultSerializerForOperation(
                    p,
                    port,
                    operation,
                    encodeTypes,
                    multiRefEncoding);
            }
            while (faults.hasNext()) {
                collectNamespaces(
                    ((Fault) faults.next()).getBlock().getType(),
                    list,
                    visited);
            }

            //declare headerfault QNAME and serializer
            for (Iterator iter = operation.getFaults(); iter.hasNext();) {
                Fault fault = (Fault) iter.next();
                if (fault instanceof HeaderFault) {
                    Set processedTypes = new HashSet();
                    Set faultNames = new HashSet();
                    SerializerWriter writer;
                }
            }
        }

        // write QNames and serializers
        Block block;
        Set processedTypes = new HashSet();
        Map.Entry entry;
        operationsIter = operations.iterator();
        Iterator blocks;
        blocks = headerMap.values().iterator();
        declareBlockTypes(p, null, blocks, processedTypes, list, visited);
        for (int i = 0; operationsIter.hasNext(); i++) {
            operation = (Operation) operationsIter.next();
            blocks = operation.getRequest().getHeaderBlocks();
            declareBlockTypes(
                p,
                operation,
                blocks,
                processedTypes,
                list,
                visited);
            blocks = operation.getRequest().getBodyBlocks();
            declareBlockTypes(
                p,
                operation,
                blocks,
                processedTypes,
                list,
                visited);
            if (operation.getResponse() != null) {
                blocks = operation.getResponse().getHeaderBlocks();
                declareBlockTypes(
                    p,
                    operation,
                    blocks,
                    processedTypes,
                    list,
                    visited);
                blocks = operation.getResponse().getBodyBlocks();
                declareBlockTypes(
                    p,
                    operation,
                    blocks,
                    processedTypes,
                    list,
                    visited);
            }
        }
        list.remove(soapNamespaceConstants.getXSD());
        list.remove(soapNamespaceConstants.getEncoding());
        Iterator namespaces = list.iterator();
        // namespace strings
        p.plnI("private static final java.lang.String[] " + "myNamespace_declarations =");
        p.pI(8);
        p.plnI("new java.lang.String[] {");
        for (int j = 0; namespaces.hasNext(); j++) {
            if (j > 0)
                p.pln(",");
            p.p("\"ns" + j + "\", ");
            p.p("\"" + (String) namespaces.next() + "\"");
        }
        p.pln();
        p.pOln("};");
        p.pO(8);
        p.pO();
    }
    
    protected void writeAddAttachmentMethod(IndentingWriter p)
    throws IOException {
        p.plnI("private void addAttachment(javax.xml.soap.SOAPMessage message, Object value, java.lang.String mimeType, java.lang.String part) throws Exception{");
        p.pln("java.lang.String contentId = java.net.URLEncoder.encode(part, \"UTF-8\")+\"=\"+com.sun.xml.rpc.util.JAXRPCUtils.getUUID()+\"@jaxrpc.sun.com\";");
        p.pln("javax.xml.soap.AttachmentPart _attPart = null;");
        p.plnI("if(value == null || mimeType == null) {");
        p.pln("return;");
        p.pOln("}");
        p.plnI("if(value instanceof javax.activation.DataHandler) {");
        p.pln("_attPart = message.createAttachmentPart((javax.activation.DataHandler)value);");
        p.pOln("}");
        p.plnI("else if(value instanceof javax.mail.internet.MimeMultipart) {");
        p.pln("java.lang.String contentType = ((javax.mail.internet.MimeMultipart) value).getContentType();");
        p.pln("javax.activation.DataHandler dataHandler = new javax.activation.DataHandler(value, contentType);");
        p.pln("_attPart = message.createAttachmentPart(dataHandler);");
        p.pOln("}");
        p.plnI("else {");
        p.pln("_attPart = message.createAttachmentPart(value, mimeType);");
        p.pOln("}");
        p.pln("_attPart.setContentId(\"<\"+contentId+\">\");");
        p.pln("message.addAttachmentPart(_attPart);");
        p.pOln("}");
    }

    protected void writeGetAttachmentMethod(IndentingWriter p)
    throws IOException {
        p.plnI("private Object getAttachment(javax.xml.soap.SOAPMessage message, java.lang.String[] mimeTypes, java.lang.String partName, boolean isDataHandler) throws Exception{");
        p.pln("javax.xml.soap.AttachmentPart _attPart = null;"); 
        p.plnI("for(int i = 0; i < mimeTypes.length; i++) {");
        p.pln("java.lang.String mimeType = mimeTypes[i];");
        p.pln("javax.xml.soap.MimeHeaders mimeHeaders = new javax.xml.soap.MimeHeaders();");
        p.pln("mimeHeaders.addHeader(\"Content-Type\", mimeType);");
        p.pln("java.util.Iterator attachments = null;");
        p.plnI("if(mimeType.endsWith(\"/*\") || mimeType.startsWith(\"multipart/\")) {");
        p.pln("attachments = message.getAttachments();");
        p.pOln("}");
        p.plnI("else {");
        p.pln("attachments = message.getAttachments(mimeHeaders);");
        p.pOln("}");
        p.plnI("if(attachments == null) {");
        p.pln("continue;");
        p.pOln("}");
        p.plnI("while (attachments.hasNext()) {");
        p.pln("_attPart = (javax.xml.soap.AttachmentPart)attachments.next();");
        p.pln("java.lang.String cId = _attPart.getContentId();");
        p.pln("int index = cId.lastIndexOf('@', cId.length());");
        p.plnI("if(index == -1){");
        p.pln("continue;");
        p.pOln("}");
        p.pln("java.lang.String localPart = cId.substring(0, index);");
        p.pln("index = localPart.lastIndexOf('=', localPart.length());");
        p.plnI("if(index == -1){");
        p.pln("continue;");
        p.pOln("}");
        p.pln("java.lang.String part = java.net.URLDecoder.decode(localPart.substring(0, index), \"UTF-8\");");
        p.plnI("if(part.equals(partName) || part.equals(\"<\"+partName)) {");
        p.plnI("if(isDataHandler) {");
        p.pln("return _attPart.getDataHandler();");
        p.pOln("}");
        p.plnI("else {");
        p.pln("return _attPart.getContent();");
        p.pOln("}");
        p.pOln("}");
        p.pOln("}");
        p.pOln("}");
        p.pln("throw new DeserializationException(\"soap.missing.attachment.for.id\", partName);");
        p.pOln("}");        
    }           
    
    protected void setAddAttachmentMethodFlag(boolean value){
        genAddAttachmentMethod = value;
    }
    
    
    protected void setGetAttachmentMethodFlag(boolean value){
        genGetAttachmentMethod = value;
    }    

    protected boolean getAddAttachmentMethodFlag(){
        return genAddAttachmentMethod;
    }
    
    
    protected boolean getGetAttachmentMethodFlag(){
        return genGetAttachmentMethod;
    }    
    
    
    protected abstract void writeUnderstoodHeadersMember(
        IndentingWriter p,
        Map headerMap)
        throws IOException;

    private void declareStaticFaultSerializerForOperation(
        IndentingWriter p,
        Port port,
        Operation operation,
        boolean encodeTypesNow,
        boolean multiRefEncodingNow)
        throws IOException {
        String serializerClassName;
        String nillable = NOT_NULLABLE_STR;
        String referenceable = REFERENCEABLE_STR;
        String multiRef = DONT_SERIALIZE_AS_REF_STR;
        String encodeType =
            (encodeTypesNow ? ENCODE_TYPE_STR : DONT_ENCODE_TYPE_STR);
        String memberName;

        serializerClassName =
            env.getNames().faultSerializerClassName(
                servicePackage,
                port,
                operation);
        memberName = env.getNames().getClassMemberName(serializerClassName);
        p.plnI(
            "private final CombinedSerializer "
                + memberName
                + " = new ReferenceableSerializerImpl("
                + multiRef
                + ",");
        p.pln(
            "new "
                + serializerClassName
                + "("
                + encodeType
                + ", "
                + nillable
                + "));");
        p.pO();
    }

    private void declareBlockTypes(
        IndentingWriter p,
        Operation operation,
        Iterator blocks,
        Set processedTypes,
        List list,
        List visited)
        throws IOException {
        Block block;
        while (blocks.hasNext()) {
            block = (Block) blocks.next();
            collectNamespaces(block.getType(), list, visited);
            if (!processedTypes
                .contains(env.getNames().getBlockQNameName(operation, block))) {
                GeneratorUtil.writeBlockQNameDeclaration(
                    p,
                    operation,
                    block,
                    env.getNames());
                processedTypes.add(
                    env.getNames().getBlockQNameName(operation, block));
            }
            if (block.getType().isSOAPType()) {
                SOAPEncoding.writeStaticSerializer(
                    p,
                    servicePackage,
                    (SOAPType) block.getType(),
                    processedTypes,
                    writerFactory,
                    env.getNames());
            } else if (block.getType().isLiteralType()) {
                LiteralEncoding.writeStaticSerializer(
                    p,
                    servicePackage,
                    (LiteralType) block.getType(),
                    processedTypes,
                    writerFactory,
                    env.getNames());
            }
        }
    }

    protected static void collectNamespaces(
        AbstractType type,
        List list,
        List visited) {
        if (visited.contains(type.getJavaType().getRealName()))
            return;
        visited.add(type.getJavaType().getRealName());
        if (type.getName().getNamespaceURI().length() > 0
            && !list.contains(type.getName().getNamespaceURI())) {
            list.add(type.getName().getNamespaceURI());
        }
        if (type instanceof SOAPStructureType) {
            SOAPStructureMember member;
            Iterator members = ((SOAPStructureType) type).getMembers();
            while (members.hasNext()) {
                member = (SOAPStructureMember) members.next();
                if (member.getName().getNamespaceURI().length() > 0
                    && !list.contains(member.getName().getNamespaceURI())) {
                    list.add(member.getName().getNamespaceURI());
                }
                collectNamespaces(member.getType(), list, visited);
            }
        } else if (type instanceof SOAPArrayType) {
            collectNamespaces(
                ((SOAPArrayType) type).getElementType(),
                list,
                visited);
        }
    }

    public static JavaStructureMember getJavaMember(Parameter parameter) {
        Block block = parameter.getBlock();
        JavaType type = block.getType().getJavaType();
        JavaStructureMember member = null;
        if (type instanceof JavaStructureType) {
            member =
                ((JavaStructureType) type).getMemberByName(parameter.getName());
            return member;
        } else {
            // should not happen
            return null;
        }
    }

    private void closeSrcFile() throws IOException {
        if (out != null) {
            out.pOln("}");
            out.close();
            out = null;
        }
    }
}
