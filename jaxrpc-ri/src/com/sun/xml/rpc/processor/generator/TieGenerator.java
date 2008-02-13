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

/*
 * $Id: TieGenerator.java,v 1.2.2.2 2008-02-13 01:16:57 anbubala Exp $
 */

/*
 * Copyrigha 2001 Sun MicCosystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.HandlerChainInfo;
import com.sun.xml.rpc.processor.config.HandlerInfo;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.HeaderFault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.tools.plugin.ToolPluginConstants;
import com.sun.xml.rpc.tools.plugin.ToolPluginFactory;
import com.sun.xml.rpc.tools.wscompile.TieHooksIf;
import com.sun.xml.rpc.tools.wscompile.TieHooksIf.TieHooksState;
import com.sun.xml.rpc.util.VersionUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public class TieGenerator extends StubTieGeneratorBase {

    private Set operationNames;
    private Set soapActionValues;
    private boolean hasUniqueOperationNames;
    private boolean hasUniqueSoapActions;
    private String dirPath = "";
    private String sourceVersion;

    public TieGenerator() {
        super();
    }

    public TieGenerator(SOAPVersion ver) {
        super(ver);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new TieGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new TieGenerator(model, config, properties);
    }

    private TieGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
        String key = ProcessorOptions.SOURCE_DIRECTORY_PROPERTY;
        dirPath = properties.getProperty(key);
        key = ProcessorOptions.JAXRPC_SOURCE_VERSION;
        sourceVersion = properties.getProperty(key);
    }

    private TieGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        super(model, config, properties, ver);
        String key = ProcessorOptions.SOURCE_DIRECTORY_PROPERTY;
        dirPath = properties.getProperty(key);
        key = ProcessorOptions.JAXRPC_SOURCE_VERSION;
        sourceVersion = properties.getProperty(key);
    }

    protected String getClassName() {
        return env.getNames().tieFor(port);
    }

    protected String getStateType() {
        return "StreamingHandlerState";
    }

    protected Message getMessageToDeserialize(Operation operation) {
        Message message = operation.getRequest();
        if (message.getBodyBlockCount() > 1) {
            // throw an exception - we cannot dispatch if there is more than one body block
            fail(
                "generator.tie.cannot.dispatch",
                operation.getName().getLocalPart());
        }
        return message;
    }

    protected String getStateGetRequestResponseString() {
        return "getRequest";
    }

    protected String getInitializeAccess() {
        return "private";
    }

    protected boolean superClassHasInitialize() {
        return false;
    }

    protected void writeImports(IndentingWriter p) throws IOException {
        super.writeImports(p);
        p.pln("import com.sun.xml.rpc.server.*;");
        p.pln("import javax.xml.rpc.handler.HandlerInfo;");
        p.pln("import com.sun.xml.rpc.client.HandlerChainImpl;");
    }

    protected void preVisitPort(Port port) throws Exception {
        super.preVisitPort(port);
        operationNames = new HashSet();
        soapActionValues = new HashSet();
        hasUniqueOperationNames = true;
        hasUniqueSoapActions = true;
    }

    protected void postVisitPort(Port port) throws Exception {
        operationNames = null;
        soapActionValues = null;
        super.postVisitPort(port);
    }

    protected void preVisitOperation(Operation operation) throws Exception {
        // CR-6660354, Merge from JavaCAPS RTS for backward compatibility
        //String name = operation.getName().getLocalPart();
        //Modified to use operation's QName instead of the operation's name. 
        //In case of document/literal style wsdl, if there are multiple operations in the port type and all the operations'
        //input message refer the same element then incorrect operation is getting invoked. 
        //To overcome this problem made the webservice operation invocation based on soap action        
        QName name = null;
    	 Message message = operation.getRequest();
         boolean hasEmptyBody = message.getBodyBlockCount() == 0;
         if (message.getBodyBlockCount() > 1) {
            // throw an exception - we cannot dispatch unless there is exactly one body block
            fail("generator.tie.cannot.dispatch", operation.getName().getLocalPart());
         }
         if (!(hasEmptyBody)) {
            Block bodyBlock = (Block) message.getBodyBlocks().next();
            name = bodyBlock.getName();
         }
        if (operationNames.contains(name)) {
            hasUniqueOperationNames = false;
        }
        operationNames.add(name);

        if (operation.getSOAPAction() != null) {
            if (soapActionValues.contains(operation.getSOAPAction())) {
                hasUniqueSoapActions = false;
            }
            soapActionValues.add(operation.getSOAPAction());
        } else {
            // if there is no SOAPAction specified, that it cannot be unique
            hasUniqueSoapActions = false;
        }
    }

    protected void writeClassDecl(IndentingWriter p, String tieClassName)
        throws IOException {
        /*
         * Declare the tie class; implement all remote interfaces.
         */

        /* Here the filename for the Tie to be geenrated is
           retrieved to be set in the GeneratedFileInfo Object */
        File classFile =
            env.getNames().sourceFileForClass(
                tieClassName,
                tieClassName,
                new File(dirPath),
                env);
        GeneratedFileInfo fi = new GeneratedFileInfo();
        fi.setFile(classFile);
        fi.setType(GeneratorConstants.FILE_TYPE_TIE);
        env.addGeneratedFile(fi);

        /* adding the file name and its type */
        p.plnI("public class " + Names.stripQualifier(tieClassName));
        p.pln("extends " + ID_TIE_BASE + " implements SerializerConstants {");
        p.pln();
    }

    protected String getSOAPVersion() {
        if (port.getSOAPVersion().equals(SOAPVersion.SOAP_11.toString()))
            return " SOAPVersion.SOAP_11";
        else
            return " SOAPVersion.SOAP_12";

    }

    //ToDo: kw need to have constructor with ver parameter
    protected void writeConstructor(IndentingWriter p, String tieClassName)
        throws IOException {
        JavaInterface intf = (JavaInterface) service.getJavaInterface();
        String serializerRegistryName =
            env.getNames().serializerRegistryClassName(intf);
        p.plnI(
            "public "
                + Names.stripQualifier(tieClassName)
                + "() throws Exception {");
        p.pln("super(new " + serializerRegistryName + "().getRegistry());");
        /*        String soapVersion = curSOAPVersion.equals(SOAPVersion.SOAP_11) ? "SOAPVersion.SOAP_11" :
              "SOAPVersion.SOAP_12";
                    p.pln("super(new "+serializerRegistryName+"().getRegistry()," + soapVersion + ");");*/
        p.pln("initialize(internalTypeMappingRegistry);");
        writeHandlerInfo(p);
        p.pOln("}");

    }

    private void writeHandlerInfo(IndentingWriter p) throws IOException {
        HandlerChainInfo portServiceHandlers =
            (HandlerChainInfo) port.getServerHandlerChainInfo();
        Iterator eachHandler = portServiceHandlers.getHandlers();
        if (eachHandler.hasNext()) {
            p.pln();
            p.plnI("{");
            p.pln("java.util.List handlerInfos = new java.util.Vector();");
            while (eachHandler.hasNext()) {
                HandlerInfo currentHandler = (HandlerInfo) eachHandler.next();
                Map properties = currentHandler.getProperties();
                String propertiesName = "props";
                p.plnI("{");
                p.pln(
                    "java.util.Map "
                        + propertiesName
                        + " = new java.util.HashMap();");
                for (Iterator entries = properties.entrySet().iterator();
                    entries.hasNext();
                    ) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    p.pln(
                        propertiesName
                            + ".put(\""
                            + (String) entry.getKey()
                            + "\", \""
                            + (String) entry.getValue()
                            + "\");");
                }

                Object[] headers = currentHandler.getHeaderNames().toArray();

                if (headers != null && headers.length > 0) {
                    p.plnI("QName[] headers = {");
                    for (int i = 0; i < headers.length; i++) {
                        QName hdr = (QName) headers[i];

                        p.pln(
                            "new QName("
                                + "\""
                                + hdr.getNamespaceURI()
                                + "\""
                                + ", "
                                + "\""
                                + hdr.getLocalPart()
                                + "\""
                                + ")"
                                + ((i != headers.length - 1) ? "," : ""));
                    }
                    p.pOln("};");
                } else
                    p.pln("QName[] headers = null;");

                p.pln(
                    "HandlerInfo handlerInfo = new HandlerInfo("
                        + currentHandler.getHandlerClassName()
                        + ".class"
                        + ", "
                        + propertiesName
                        + ", headers);");
                p.pln("handlerInfos.add(handlerInfo);");
                p.pOln("}");
            }

            Set roles = portServiceHandlers.getRoles();
            p.p("java.lang.String[] roles = new java.lang.String[] {");

            boolean first = true;
            Iterator i = roles.iterator();
            while (i.hasNext()) {
                if (!first) {
                    p.p(", ");
                } else
                    first = false;
                p.p("\"" + i.next() + "\"");
            }

            p.pln("};");

            p.pln("handlerChain = new HandlerChainImpl(handlerInfos);");
            p.pln("handlerChain.setRoles(roles);");
            p.pln("handlerChain.addUnderstoodHeaders(getUnderstoodHeaders());");

            p.pOln("}");
        }

    }

    protected void writePeekFirstBodyElementMethod(IndentingWriter p)
        throws IOException {

        boolean useOperationNameDispatching = hasUniqueOperationNames;
        boolean useSoapActionDispatching =
            !useOperationNameDispatching && hasUniqueSoapActions;

        if (!useOperationNameDispatching && !useSoapActionDispatching) {
            // we cannot possibly dispatch the operations in this port
            throw new GeneratorException(
                "generator.tie.port.cannot.dispatch",
                port.getName().getLocalPart());
        }

        p.pln("/*");
        p.pln(
            " * This method must determine the opcode of the operation that has been invoked.");
        p.pln(" */");
        p.plnI(
            "protected void peekFirstBodyElement(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingHandlerState state) throws Exception {");

        if (useSoapActionDispatching) {
            p.pln("java.lang.String soapaction = null;");
            p.pln(
                "java.lang.String[] soapactionheaders = state.getMessageContext().getMessage().getMimeHeaders().getHeader(\"SOAPAction\");");
            p.plnI("if (soapactionheaders.length > 0) {");
            p.pln("soapaction = soapactionheaders[0];");
            p.pOlnI("} else {");
            p.pln(
                "throw new SOAPProtocolViolationException(\"soap.request.missing.soapaction.cannot.dispatch\");");
            p.pOln("}");
        }

        Iterator operationsIter = operations.iterator();
        Operation operation;
        Message message;
        Operation operationWithEmptyBody = null;
        int j = 0;
        while (operationsIter.hasNext()) {
            operation = (Operation) operationsIter.next();
            if (useOperationNameDispatching) {
                message = operation.getRequest();
                boolean hasEmptyBody = message.getBodyBlockCount() == 0;
                if (hasEmptyBody) {
                    if (operationWithEmptyBody != null) {
                        fail(
                            "generator.tie.cannot.dispatch",
                            operation.getName().getLocalPart());
                    } else {
                        operationWithEmptyBody = operation;
                    }
                }
                if (message.getBodyBlockCount() > 1) {
                    // throw an exception - we cannot dispatch unless there is exactly one body block
                    fail(
                        "generator.tie.cannot.dispatch",
                        operation.getName().getLocalPart());
                }

                if (!hasEmptyBody) {
                    Block bodyBlock = (Block) message.getBodyBlocks().next();
                    if (j++ > 0)
                        p.p("else ");
                    p.plnI(
                        "if (bodyReader.getName().equals("
                            + env.getNames().getBlockQNameName(
                                operation,
                                bodyBlock)
                            + ")) {");

                    if (operation.isOverloaded()) {
                        p.pln(
                            "throw new SOAPProtocolViolationException(\"soap.operation.cannot.dispatch\", \""
                                + operation.getName().getLocalPart()
                                + "\");");
                    } else {
                        p.pln(
                            "state.getRequest().setOperationCode("
                                + env.getNames().getOPCodeName(
                                    operation.getUniqueName())
                                + ");");
                    }
                    p.pOln("}");
                }
            } else if (useSoapActionDispatching) {
                // notice that we need to use enclose the soap action string in quotes, hence the \\\"
                if (j++ > 0)
                    p.p("else ");
                p.plnI(
                    "if (soapaction.equals(\"\\\""
                        + operation.getSOAPAction()
                        + "\\\"\")) {");
                p.pln(
                    "state.getRequest().setOperationCode("
                        + env.getNames().getOPCodeName(operation.getUniqueName())
                        + ");");
                p.pOln("}");
            }
        }
        if (j > 0)
            p.plnI("else {");
        p.pln(
            "throw new SOAPProtocolViolationException(\"soap.operation.unrecognized\", bodyReader.getName().toString());");
        if (j > 0)
            p.pOln("}");
        p.pOln("}");
    }

    protected void writeUsesSOAPActionForDispatching(IndentingWriter p)
        throws IOException {
        boolean useOperationNameDispatching = hasUniqueOperationNames;
        boolean useSoapActionDispatching =
            !useOperationNameDispatching && hasUniqueSoapActions;

        if (useSoapActionDispatching) {
            p.plnI("public boolean usesSOAPActionForDispatching() {");
            p.pln("return true;");
            p.pOln("}");
            p.pln();
        }
    }

    protected void writeGetOpcodeForFirstBodyElementName(IndentingWriter p)
        throws IOException {
        boolean useOperationNameDispatching = hasUniqueOperationNames;

        if (useOperationNameDispatching) {
            p.pln("/*");
            p.pln(
                " * This method must determine the opcode of the operation given the QName of the first body element.");
            p.pln(" */");
            p.plnI("public int getOpcodeForFirstBodyElementName(QName name) {");

            Iterator operationsIter = operations.iterator();
            Operation operation;
            Message message;
            Operation operationWithEmptyBody = null;
            for (int j = 0; operationsIter.hasNext(); ++j) {
                operation = (Operation) operationsIter.next();
                message = operation.getRequest();
                boolean hasEmptyBody = message.getBodyBlockCount() == 0;
                if (hasEmptyBody) {
                    if (operationWithEmptyBody != null) {
                        fail(
                            "generator.tie.cannot.dispatch",
                            operation.getName().getLocalPart());
                    } else {
                        operationWithEmptyBody = operation;
                    }
                }
                if (message.getBodyBlockCount() > 1) {
                    // throw an exception - we cannot dispatch unless there is exactly one body block
                    fail(
                        "generator.tie.cannot.dispatch",
                        operation.getName().getLocalPart());
                }

                if (j == 0) {
                    p.plnI("if (name == null) {");
                    p.pln("return InternalSOAPMessage.NO_OPERATION;");
                    p.pOln("}");
                }

                if (!hasEmptyBody) {
                    Block bodyBlock = (Block) message.getBodyBlocks().next();
                    p.plnI(
                        "if (name.equals("
                            + env.getNames().getBlockQNameName(
                                operation,
                                bodyBlock)
                            + ")) {");
                    if (operation.isOverloaded()) {
                        p.pln("return InternalSOAPMessage.NO_OPERATION;");
                    } else {
                        p.pln(
                            "return "
                                + env.getNames().getOPCodeName(
                                    operation.getUniqueName())
                                + ";");
                    }
                    p.pOln("}");
                }
            }
            p.pln("return super.getOpcodeForFirstBodyElementName(name);");
            p.pOln("}");
        }
    }

    protected void writeGetOpcodeForSOAPAction(IndentingWriter p)
        throws IOException {
        boolean useOperationNameDispatching = hasUniqueOperationNames;
        boolean useSoapActionDispatching =
            !useOperationNameDispatching && hasUniqueSoapActions;

        if (useSoapActionDispatching) {
            p.pln("/*");
            p.pln(
                " * This method must determine the opcode of the operation given the SOAPAction string.");
            p.pln(" */");
            p.plnI("public int getOpcodeForSOAPAction(java.lang.String action) {");
            Iterator operationsIter = operations.iterator();
            Operation operation;
            Message message;
            Operation operationWithEmptyBody = null;
            int j = 0;
            while (operationsIter.hasNext()) {
                operation = (Operation) operationsIter.next();

                if (j++ == 0) {
                    p.plnI("if (action == null) {");
                    p.pln("return InternalSOAPMessage.NO_OPERATION;");
                    p.pOln("}");
                }

                p.plnI(
                    "if (action.equals(\"\\\""
                        + operation.getSOAPAction()
                        + "\\\"\")) {");
                p.pln(
                    "return "
                        + env.getNames().getOPCodeName(operation.getUniqueName())
                        + ";");
                p.pOln("}");
            }
            p.pln("return super.getOpcodeForSOAPAction(action);");
            p.pOln("}");
        }
    }

    protected void writeGetMethodForOpcode(IndentingWriter p)
        throws IOException, java.lang.ClassNotFoundException {
        Method theMethods = null;

        p.plnI(
            "private Method internalGetMethodForOpcode(int opcode) throws ClassNotFoundException, NoSuchMethodException {");

        p.pln();
        p.pln("Method theMethod = null;");
        p.pln();
        p.plnI("switch(opcode) {");

        Iterator operationsIter = operations.iterator();
        String str = "";
        String ops = "";
        int j = 0;
        Operation operation;
        Message message;
        Operation operationWithEmptyBody = null;
        for (j = 0; operationsIter.hasNext(); ++j) {
            operation = (Operation) operationsIter.next();
            message = operation.getRequest();

            boolean hasEmptyBody = message.getBodyBlockCount() == 0;
            if (hasEmptyBody) {
                if (operationWithEmptyBody != null) {
                    fail(
                        "generator.tie.cannot.dispatch",
                        operation.getName().getLocalPart());
                } else {
                    operationWithEmptyBody = operation;
                }
            }

            p.plnI(
                "case "
                    + env.getNames().getOPCodeName(operation.getUniqueName())
                    + ":");
            JavaMethod jvmmethod = operation.getJavaMethod();
            Iterator ite = jvmmethod.getParameters();
            int i = 0;
            String data = "";

            p.plnI("{");
            p.p("Class[] carray = { ");
            while (ite.hasNext()) {
                JavaParameter astr = ((JavaParameter) ite.next());
                str = astr.getType().getName();
                data = "";

                /* this is to make sure there is no
                   extra , as the array gets constructed */
                if (i != 0) {
                    p.p(",");
                }
                try {
                    /* Here we check first if the datatype is an array */
                    if (astr.isHolder()) {
                        if (astr.getHolderName() == null) {
                            p.p(
                                env.getNames().holderClassName(
                                    port,
                                    astr.getType())
                                    + ".class");
                        } else {
                            p.p(astr.getHolderName() + ".class");
                        }
                    } else if (str.indexOf("[") > 0) {
                        int end = str.lastIndexOf("]");
                        int range = (end - str.indexOf("[")) / 2;

                        /* if its an array we check for its key in
                           the hash table */
                        if (GeneratorUtil.ht.containsKey(str)) {
                            for (int counter = 0; counter <= range;) {
                                data += "[";
                                counter++;
                            }
                            data += (String) GeneratorUtil.ht.get(str);
                            p.p("Class.forName(\"" + data + "\")");
                        } else {
                            /* if the key is not present in the hashtable */
                            for (int counter = 0; counter <= range;) {
                                data += "[";
                                counter++;
                            }
                            data += "L"
                                + str.substring(0, str.indexOf("["))
                                + ";";
                            p.p("Class.forName(\"" + data + "\")");
                        }
                    } else {
                        /* this is when the datatype is not array type
                           , we check for a key in the hashtable */
                        if (GeneratorUtil.ht.containsKey(str)) {
                            /* This has been done to handle the
                               primitive data types */
                            p.p(GeneratorUtil.ht.get(str));
                        } else {
                            /* if the hashtable did not have an entry */
                            p.p(str + ".class");
                        }
                    }
                } catch (java.lang.Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
            JavaInterface intf = (JavaInterface) port.getJavaInterface();
            p.pln(" };");
            p.pln(
                "theMethod = ("
                    + intf.getName()
                    + ".class).getMethod(\""
                    + jvmmethod.getName()
                    + "\", carray);");
            p.pOln("}");
            p.pln("break;");
            p.pOln("");
        }
        p.pln("default:");
        p.pOln("}");
        p.pln("return theMethod;");
        p.pOln("}");

        /* Here j is the number of operations */
        p.pln();
        p.pln("private Method[] methodMap = new Method[" + j + "];");
        p.pln();

        p.pln("/*");
        p.pln(" * This method returns the Method Obj for a specified opcode.");
        p.pln(" */");
        p.plnI(
            "public Method getMethodForOpcode(int opcode) throws ClassNotFoundException, NoSuchMethodException {");
        p.pln(" ");

        p.plnI("if (opcode <= InternalSOAPMessage.NO_OPERATION ) {");
        p.pln("return null;");
        p.pOln("}");
        p.pln(" ");

        p.plnI("if (opcode >= " + j + " ) {");
        p.pln("return null;");
        p.pOln("}");
        p.pln(" ");

        p.plnI("if (methodMap[opcode] == null)  {");
        p.pln("methodMap[opcode] = internalGetMethodForOpcode(opcode);");
        p.pOln("}");
        p.pln(" ");

        p.pln("return methodMap[opcode];");
        p.pOln("}");
    }

    protected void writeHandleEmptyBody(IndentingWriter p, Operation operation)
        throws IOException {
        p.pln("/*");
        p.pln(" * This method handles the case of an empty SOAP body.");
        p.pln(" */");
        p.plnI(
            "protected void handleEmptyBody(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingHandlerState state) throws Exception {");
        p.pln(
            "state.getRequest().setOperationCode("
                + env.getNames().getOPCodeName(operation.getUniqueName())
                + ");");
        p.pOln("}");
    }

    protected void writeProcessingHookMethod(IndentingWriter p)
        throws IOException {

        p.pln("/*");
        p.pln(
            " * This method must invoke the correct method on the servant based on the opcode.");
        p.pln(" */");
        p.plnI(
            "protected void processingHook(StreamingHandlerState state) throws Exception {");
        Iterator operationsIter = operations.iterator();
        Operation operation;
        p.plnI("switch (state.getRequest().getOperationCode()) {");
        while (operationsIter.hasNext()) {
            operation = (Operation) operationsIter.next();
            p.plnI(
                "case "
                    + env.getNames().getOPCodeName(operation.getUniqueName())
                    + ":");
            p.pln(
                "invoke_"
                    + env.getNames().validInternalJavaIdentifier(
                        operation.getUniqueName())
                    + "(state);");
            p.pln("break;");
            p.pO();
        }
        p.plnI("default:");
        p.pln(
            "throw new SOAPProtocolViolationException(\"soap.operation.unrecognized\", java.lang.Integer.toString(state.getRequest().getOperationCode()));");
        p.pO();
        p.pOln("}"); // switch
        p.pOln("}"); // method
    }

    protected String getFaultCodeServer() {
        if (port.getSOAPVersion().equals(SOAPVersion.SOAP_12.toString()))
            return "com.sun.xml.rpc.encoding.soap.SOAP12Constants.FAULT_CODE_SERVER";
        else
            return "com.sun.xml.rpc.encoding.soap.SOAPConstants.FAULT_CODE_SERVER";
    }

    protected String getQNameSOAPFault() {
        if (port.getSOAPVersion().equals(SOAPVersion.SOAP_12))
            return "com.sun.xml.rpc.encoding.soap.SOAP12Constants.QNAME_SOAP_FAULT";
        else
            return "com.sun.xml.rpc.encoding.soap.SOAPConstants.QNAME_SOAP_FAULT";
    }

    protected void writeRpcEncodedOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException {
        String messageName = operation.getName().getLocalPart();
        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        JavaStructureMember javaMember;
        Iterator iterator;
        Parameter parameter;
        String requestObjType = null;
        String requestObjName = null;
        SOAPType requestBlockType = null;
        Message message;

        // find request object details
        message = operation.getRequest();
        iterator = message.getBodyBlocks();
        Block requestBlock = null;
        String requestObjInit = null;
        while (iterator.hasNext()) {
            requestBlock = (Block) iterator.next();
            if (requestBlock.getName().getLocalPart().equals(messageName)) {
                requestBlockType = (SOAPType) requestBlock.getType();
                requestObjType = requestBlockType.getJavaType().getName();
                requestObjInit = requestBlockType.getJavaType().getInitString();
                requestObjName =
                    env.getNames().getTypeMemberName(
                        requestBlockType.getJavaType());
                break;
            }
        }
        // declare the method
        writeInvokeMethodDecl(p, operation);

        // handle one way response first
        if (operation.getResponse() == null) {
            p.pln("flushHttpResponse(state);");
        }

        // declare the request/holder/header objects
        declareRequestObjects(
            p,
            requestObjType,
            requestObjName,
            requestObjInit);

        declareHolderHeaderObjects(p, requestBlock, operation);

        boolean hasRequestHeaders = false;
        iterator = operation.getRequest().getHeaderBlocks();
        hasRequestHeaders = iterator.hasNext();
        if (hasRequestHeaders) {
            writeRequestHeaders(p, operation);
        }
        p.plnI("try {");
        // declare the return type
        declareRpcReturnType(p, operation, resultType);

        // build the call to the servant
        JavaParameter javaParameter;
        if (javaMethod.getDeclaringClass() != null) {
            p.p(
                "(("
                    + javaMethod.getDeclaringClass().replace('$', '.')
                    + ") getTarget())."
                    + javaMethod.getName()
                    + "(");
        } else {
            p.p(
                "(("
                    + remoteClassName
                    + ") getTarget())."
                    + javaMethod.getName()
                    + "(");
        }
        if (resultType != null
            && !resultType.getName().equals(VOID_CLASSNAME)) {
            p.pO();
        }
        iterator = javaMethod.getParameters();
        message = operation.getRequest();
        Block paramBlock;
        for (int i = 0; iterator.hasNext(); i++) {
            if (i > 0)
                p.p(", ");
            javaParameter = (JavaParameter) iterator.next();
            parameter = javaParameter.getParameter();
            paramBlock = parameter.getBlock();
            if (paramBlock.getLocation() != Block.BODY) {
                if (javaParameter.isHolder()) {
                    p.p(javaParameter.getParameter().getName() + "_holder");
                } else {
                    p.p(parameter.getName());
                }
            } else {
                JavaType javaObjType = paramBlock.getType().getJavaType();
                String javaObjName =
                    env.getNames().getTypeMemberName(javaObjType);
                javaMember = getJavaMember(parameter);
                if (javaParameter.isHolder()) {
                    p.p(javaParameter.getParameter().getName() + "_holder");
                } else {
                    if (javaMember.isPublic())
                        p.p(javaObjName + "." + parameter.getName());
                    else
                        p.p(
                            javaObjName
                                + "."
                                + javaMember.getReadMethod()
                                + "()");
                }
            }
        }
        p.pln(");"); // end call to servant

        if (operation.getResponse() != null) {

            // declare the response object
            declareRpcResponseObject(p, operation);
        }

        writeCatchClauses(p, operation);

        p.pOln("}"); // catch
        p.pOln("}"); // end method
    }

    private void writeCatchClauses(IndentingWriter p, Operation operation)
        throws IOException {
        Set faultSet = new TreeSet(new GeneratorUtil.FaultComparator());
        faultSet.addAll(operation.getFaultsSet());
        Iterator faults = faultSet.iterator();
        Fault fault;
        while (faults.hasNext()) {
            fault = (Fault) faults.next();
            p.pOlnI(
                "} catch ("
                    + env.getNames().customExceptionClassName(fault)
                    + " e) {");
            p.plnI(
                "SOAPFaultInfo fault = new SOAPFaultInfo("
                    + getFaultCodeServer()
                    + ",");
            p.pln(
                "\""
                    + env.getNames().customExceptionClassName(fault)
                    + "\", null, e);");
            p.pO();
            p.pln(
                "SOAPBlockInfo faultBlock = new SOAPBlockInfo("
                    + getQNameSOAPFault()
                    + ");");
            p.pln("faultBlock.setValue(fault);");
            p.pln(
                "faultBlock.setSerializer("
                    + env.getNames().getClassMemberName(
                        env.getNames().faultSerializerClassName(
                            servicePackage,
                            port,
                            operation))
                    + ");");
            p.pln("state.getResponse().setBody(faultBlock);");
            p.pln("state.getResponse().setFailure(true);");
            //headerfault
            if (fault instanceof HeaderFault) {
                p.pln("SOAPHeaderBlockInfo headerInfo;");
                p.pln(
                    "headerInfo = new SOAPHeaderBlockInfo("
                        + env.getNames().getQNameName(fault.getBlock().getName())
                        + ", null, false);");
                p.pln("headerInfo.setValue(e);");
                p.pln(
                    "headerInfo.setSerializer("
                        + writerFactory
                            .createWriter(
                                servicePackage,
                                (AbstractType) fault.getBlock().getType())
                            .serializerMemberName()
                        + ");");
                p.pln("state.getResponse().add(headerInfo);");
            }
        }
        p.pOlnI("} catch (javax.xml.rpc.soap.SOAPFaultException e) {");
        p.plnI("SOAPFaultInfo fault = new SOAPFaultInfo(e.getFaultCode(),");
        p.pln("e.getFaultString(), e.getFaultActor(), e.getDetail());");
        p.pO();
        p.pln(
            "SOAPBlockInfo faultBlock = new SOAPBlockInfo("
                + getQNameSOAPFault()
                + ");");
        p.pln("faultBlock.setValue(fault);");
        p.pln(
            "faultBlock.setSerializer(new SOAPFaultInfoSerializer(false, e.getDetail()==null));");
        p.pln("state.getResponse().setBody(faultBlock);");
        p.pln("state.getResponse().setFailure(true);");
    }

    private void writeInvokeMethodDecl(IndentingWriter p, Operation operation)
        throws IOException {
        String messageName = operation.getName().getLocalPart();
        // declare the method
        p.pln("/*");
        p.pln(
            " * This method does the actual method invocation for operation: "
                + messageName);
        p.pln(" */");
        p.plnI(
            "private void invoke_"
                + env.getNames().validInternalJavaIdentifier(
                    operation.getUniqueName())
                + "(StreamingHandlerState state) throws Exception {");
        p.pln();
    }

    private void declareRequestObjects(
        IndentingWriter p,
        String requestObjType,
        String requestObjName,
        String requestObjInit)
        throws IOException {
        if (requestObjType != null && requestObjName != null) {
            String requestObjMemberName = requestObjName + "Obj";
            // declare the request objects
            // Fix for bug 4778917
            p.pln(
                requestObjType
                    + " "
                    + requestObjName
                    + " = "
                    + requestObjInit
                    + ";");
            //null;");
            // end fix
            p.plnI("Object " + requestObjMemberName + " =");
            p.pln("state.getRequest().getBody().getValue();");
            p.pO();
            p.pln();
            p.plnI(
                "if ("
                    + requestObjMemberName
                    + " instanceof SOAPDeserializationState) {");
            // Fix for bug 4778917
            String valueStr =
                "((SOAPDeserializationState)"
                    + requestObjMemberName
                    + ").getInstance()";
            if (SimpleToBoxedUtil.isPrimitive(requestObjType)) {
                String boxName =
                    SimpleToBoxedUtil.getBoxedClassName(requestObjType);
                valueStr =
                    SimpleToBoxedUtil.getUnboxedExpressionOfType(
                        "(" + boxName + ")" + valueStr,
                        requestObjType);
            } else {
                valueStr = "(" + requestObjType + ")" + valueStr;
            }
            p.pln(requestObjName + " = " + valueStr + ";");
            p.pOlnI("} else {");
            valueStr = requestObjMemberName;
            if (SimpleToBoxedUtil.isPrimitive(requestObjType)) {
                String boxName =
                    SimpleToBoxedUtil.getBoxedClassName(requestObjType);
                valueStr =
                    SimpleToBoxedUtil.getUnboxedExpressionOfType(
                        "(" + boxName + ")" + valueStr,
                        requestObjType);
            } else {
                valueStr = "(" + requestObjType + ")" + valueStr;
            }
            p.pln(requestObjName + " = " + valueStr + ";");
            p.pOln("}");
            p.pln();
        }
    }

    private void declareHolderHeaderObjects(
        IndentingWriter p,
        Block requestBlock,
        Operation operation)
        throws IOException {
        if (requestBlock != null) {
            AbstractType requestBlockType = requestBlock.getType();
            String requestObjType = requestBlockType.getJavaType().getName();
            String requestObjName =
                env.getNames().getTypeMemberName(
                    requestBlockType.getJavaType());
            String requestObjMemberName = requestObjName + "Obj";
            JavaMethod javaMethod = operation.getJavaMethod();
            // declare holder/header objects
            Iterator iterator = javaMethod.getParameters();
            JavaParameter javaParameter;
            JavaStructureMember javaMember;
            boolean declaredHeaderObj = false;
            boolean declaredAttachmentObj = false;
            for (int i = 0; iterator.hasNext();) {
                javaParameter = (JavaParameter) iterator.next();
                if (javaParameter.isHolder()) {
                    String holderClassName;
                    if (javaParameter.getHolderName() != null) {
                        holderClassName = javaParameter.getHolderName();
                    } else {
                        holderClassName =
                            env.getNames().holderClassName(
                                port,
                                javaParameter.getType());
                    }
                    p.plnI(
                        holderClassName
                            + " "
                            + javaParameter.getParameter().getName()
                            + "_holder =");
                    p.pln("new " + holderClassName + "();");
                    p.pO();
                    // in/out parameter initialize it
                    if (javaParameter.getParameter().getLinkedParameter()
                        != null
                        && !(javaParameter.getParameter().getBlock().getLocation()
                            == Block.HEADER)) {
                        javaMember =
                            getJavaMember(javaParameter.getParameter());
                        // check for array wrappers
                        if (javaMember != null
                            && javaMember.getType() instanceof JavaStructureType
                            && ((JavaStructureType) javaMember.getType())
                                .getOwner()
                                instanceof LiteralArrayWrapperType) {
                            LiteralArrayWrapperType owner =
                                (LiteralArrayWrapperType)
                                    ((JavaStructureType) javaMember
                                    .getType())
                                    .getOwner();
                            JavaStructureMember tmpMember =
                                (JavaStructureMember)
                                    ((JavaStructureType) owner
                                    .getJavaType())
                                    .getMembers()
                                    .next();
                            p.plnI(
                                javaParameter.getParameter().getName()
                                    + "_holder.value = ");
                            p.pln(
                                "("
                                    + requestObjName
                                    + "."
                                    + javaMember.getReadMethod()
                                    + "() != null) ?");
                            p.pln(
                                requestObjName
                                    + "."
                                    + javaMember.getReadMethod()
                                    + "()."
                                    + tmpMember.getReadMethod()
                                    + "() : null;");
                            p.pO();
                        } else if (
                            !(javaParameter
                                .getParameter()
                                .getBlock()
                                .getLocation()
                                == Block.ATTACHMENT)){
                            p.pln(
                                javaParameter.getParameter().getName()
                                    + "_holder.value = "
                                    + requestObjName
                                    + "."
                                    + javaMember.getReadMethod()
                                    + "();");
                        }
					//Nagesh: Added the following extra if condition to have the declaration of _headerObj in the generated Tie class
                    } else if (javaParameter.getParameter().getBlock().getLocation() == Block.HEADER) { // look for header
						if (!declaredHeaderObj) {
                        p.pln("Object _headerObj;");
                        declaredHeaderObj = true;
						}	
					}
                } else if (
                    javaParameter.getParameter().getBlock().getLocation()
                        == Block.HEADER) {
                    // look for header
                    if (!declaredHeaderObj) {
                        p.pln("Object _headerObj;");
                        declaredHeaderObj = true;
                    }
                    AbstractType paramType =
                        javaParameter.getParameter().getType();
                    String initValue = javaParameter.getType().getInitString();
                    p.pln(
                        paramType.getJavaType().getName()
                            + " "
                            + javaParameter.getParameter().getName()
                            + " = "
                            + initValue
                            + ";");
                }
            }
        }else{
            JavaMethod javaMethod = operation.getJavaMethod();
            // declare holder/header objects
            Iterator iterator = javaMethod.getParameters();
            while(iterator.hasNext()){
                JavaParameter javaParameter = (JavaParameter)iterator.next();
                if (javaParameter.isHolder()) {
                    String holderClassName;
                    if (javaParameter.getHolderName() != null) {
                        holderClassName = javaParameter.getHolderName();
                    } else {
                        holderClassName =
                            env.getNames().holderClassName(
                                    port,
                                    javaParameter.getType());
                    }
                    p.plnI(
                            holderClassName
                            + " "
                            + javaParameter.getParameter().getName()
                            + "_holder =");
                    p.pln("new " + holderClassName + "();");
                    p.pO();
                }
            }
        }
    }

    private boolean declareRpcReturnType(
        IndentingWriter p,
        Operation operation,
        JavaType resultType)
        throws IOException {
        // declare the return type
        Message message = operation.getResponse();
        if (message == null) {
            return false;
        }
        Iterator iterator = message.getBodyBlocks();
        Parameter parameter;
        if (resultType != null
            && !resultType.getName().equals(VOID_CLASSNAME)) {
            iterator = message.getParameters();
            if (iterator.hasNext()) {
                parameter = (Parameter) iterator.next();
                String resultName;
                // if we have an arrayWrapper we need to unwrap it
                if (resultType instanceof JavaStructureType
                    && ((JavaStructureType) resultType).getOwner()
                        instanceof LiteralArrayWrapperType) {
                    resultName =
                        (
                            (LiteralArrayWrapperType)
                                ((JavaStructureType) resultType)
                            .getOwner())
                            .getJavaArrayType()
                            .getName();
                } else {
                    resultName = resultType.getName();
                }

                p.plnI(resultName + " " + parameter.getName() + " = ");
                return true;
            }
        }
        return false;
    }

    private void writeRequestHeaders(IndentingWriter p, Operation operation)
        throws IOException {

        p.pln("Iterator headers = state.getRequest().headers();");
        p.pln("SOAPHeaderBlockInfo curHeader;");
        p.plnI("while (headers.hasNext()) {");
        p.pln("curHeader = (SOAPHeaderBlockInfo)headers.next();");
        Iterator iterator = operation.getRequest().getParameters();
        boolean startedHeaders = false;
        Parameter parameter;
        while (iterator.hasNext()) {
            parameter = (Parameter) iterator.next();
            if (parameter.getBlock().getLocation() == Block.HEADER) {
                if (startedHeaders) {
                    p.p(" else ");
                }
                startedHeaders = true;
                String paramName = parameter.getName();
                String paramType = parameter.getType().getJavaType().getName();
                String varName = null;
                if (parameter.getJavaParameter() != null && parameter.getJavaParameter().isHolder()) {
                    varName = paramName + "_holder.value";
                } else {
                    varName = paramName;
                }
                String qname =
                    env.getNames().getBlockQNameName(
                        null,
                        parameter.getBlock());
                p.plnI("if (curHeader.getName().equals(" + qname + ")) {");
                p.pln("_headerObj = (" + paramType + ")curHeader.getValue();");
                p.plnI("if (_headerObj instanceof SOAPDeserializationState) {");
                p.pln(
                    varName
                        + " = ("
                        + paramType
                        + ")((SOAPDeserializationState)"
                        + "_headerObj).getInstance();");
                p.pOlnI("} else {");
                p.pln(varName + " = (" + paramType + ")_headerObj;");
                p.pOln("}");
                p.pO("}");
            }
        }
        if (startedHeaders)
            p.pln();
        p.pOln("}"); // iterating headers
        p.pln();
    }

    private void declareRpcResponseObject(
        IndentingWriter p,
        Operation operation)
        throws IOException {
        String messageName = operation.getName().getLocalPart();
        // find the response object details
        Message message = operation.getResponse();
        if (message == null) {
            return;
        }
        Iterator iterator = message.getBodyBlocks();
        Block responseBlock = null;
        SOAPType responseBlockType = null;
        String responseObjType = null;
        String responseObjName = null;
        Parameter parameter;
        JavaParameter javaParameter;
        JavaStructureMember javaMember;
        while (iterator.hasNext()) {
            responseBlock = (Block) iterator.next();
            if (responseBlock
                .getName()
                .getLocalPart()
                .equals(messageName + "Response")) {
                responseBlockType = (SOAPType) responseBlock.getType();
                responseObjType = responseBlockType.getJavaType().getName();
                responseObjName =
                    env.getNames().getTypeMemberName(
                        responseBlockType.getJavaType());
                break;
            }
            responseBlock = null;
        }
        p.plnI(responseObjType + " " + responseObjName + " =");
        p.pln("new " + responseObjType + "();");
        p.pO();

        message = operation.getResponse();
        Iterator iterator2 = message.getParameters();
        String memberName;
        Block block;
        p.pln("SOAPHeaderBlockInfo headerInfo;");
        for (int i = 0; iterator2.hasNext(); i++) {
            parameter = (Parameter) iterator2.next();
            block = parameter.getBlock();
            if (block.getLocation() == Block.BODY) {
                javaMember = getJavaMember(parameter);
                javaParameter = parameter.getJavaParameter();
                if (parameter.getLinkedParameter() != null
                    || (javaParameter != null && javaParameter.isHolder())) {
                    memberName = parameter.getName() + "_holder.value";
                } else {
                    memberName = parameter.getName();
                }
                if (javaMember != null) {
                    if (javaMember.isPublic())
                        p.pln(
                            responseObjName
                                + "."
                                + javaMember.getName()
                                + " = "
                                + memberName
                                + ";");
                    else
                        p.pln(
                            responseObjName
                                + "."
                                + javaMember.getWriteMethod()
                                + "("
                                + memberName
                                + ");");
                }
            } else { // header block
                javaParameter = parameter.getJavaParameter();
                String qname = env.getNames().getBlockQNameName(null, block);
                if (parameter.getLinkedParameter() != null
                    || (javaParameter != null && javaParameter.isHolder())) {
                    memberName = parameter.getName() + "_holder.value";
                } else {
                    memberName = parameter.getName();
                }
                p.pln(
                    "headerInfo = new SOAPHeaderBlockInfo("
                        + qname
                        + ", null, false);");
                p.pln("headerInfo.setValue(" + memberName + ");");
                p.pln(
                    "headerInfo.setSerializer("
                        + writerFactory
                            .createWriter(
                                servicePackage,
                                (SOAPType) block.getType())
                            .serializerMemberName()
                        + ");");
                p.pln("state.getResponse().add(headerInfo);");
            }
        }
        p.pln();
        p.pln(
            "SOAPBlockInfo bodyBlock = new SOAPBlockInfo("
                + env.getNames().getBlockQNameName(operation, responseBlock)
                + ");");
        p.pln("bodyBlock.setValue(" + responseObjName + ");");
        p.pln(
            "bodyBlock.setSerializer("
                + writerFactory
                    .createWriter(servicePackage, responseBlockType)
                    .serializerMemberName()
                + ");");
        p.pln("state.getResponse().setBody(bodyBlock);");
    }

    protected void writeRpcLiteralOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException {

        String messageName = operation.getName().getLocalPart();
        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        Iterator iterator;
        Parameter parameter;
        JavaParameter javaParameter;
        JavaStructureMember javaMember;
        String requestObjType = null;
        String requestObjName = null;
        String responseObjType = null;
        String responseObjName = null;
        LiteralType responseBlockType = null;
        LiteralType requestBlockType = null;
        Message message;
        Block responseBlock = null;

        // find request object details
        Message requestMessage = operation.getRequest();
        int headerParameterCount = 0;
        int attachmentParameterCount = 0;
        for (iterator = requestMessage.getParameters(); iterator.hasNext();) {
            parameter = (Parameter) iterator.next();
            Block paramBlock = parameter.getBlock();
            if (paramBlock.getLocation() == Block.BODY) {
                //                if (parameter.isEmbedded()) {
                //                    ++embeddedParameterCount;
                //                }
                //                else {
                //                    ++nonEmbeddedParameterCount;
                //                }
            } else if (paramBlock.getLocation() == Block.HEADER) {
                // header block
                ++headerParameterCount;
            } else if (paramBlock.getLocation() == Block.ATTACHMENT) {
                // header block
                ++attachmentParameterCount;
            }
        }

        // sanity check
        iterator = requestMessage.getBodyBlocks();
        Block requestBlock = null;
        String requestObjInit = null;
        if (iterator.hasNext()) {
            requestBlock = (Block) iterator.next();
            requestBlockType = (LiteralType) requestBlock.getType();
            requestObjType = requestBlockType.getJavaType().getName();
            requestObjInit = requestBlockType.getJavaType().getInitString();
            requestObjName =
                env.getNames().getTypeMemberName(
                    requestBlockType.getJavaType());
        }
        // declare the method
        writeInvokeMethodDecl(p, operation);

        // handle one way response first
        if (operation.getResponse() == null) {
            p.pln("flushHttpResponse(state);");
        }

        // declare the request objects
        declareRequestObjects(
            p,
            requestObjType,
            requestObjName,
            requestObjInit);

        declareHolderHeaderObjects(p, requestBlock, operation);

        boolean hasRequestHeaders = false;
        iterator = operation.getRequest().getHeaderBlocks();
        hasRequestHeaders = iterator.hasNext();
        if (hasRequestHeaders) {
            writeRequestHeaders(p, operation);
        }
        // attachements
        declareHolderAttachmentObjects(p, requestBlock, operation);

        p.plnI("try {");
        // find the response object details
        message = operation.getResponse();
        boolean resultIsEmbedded = false;
        if (message != null) {
            iterator = message.getBodyBlocks();
            if (iterator.hasNext()) {
                responseBlock = (Block) iterator.next();
                responseBlockType = (LiteralType) responseBlock.getType();
                responseObjType = responseBlockType.getJavaType().getName();
                responseObjName = "_response";
            }

            resultIsEmbedded = false;
            if (resultType != null
                && !resultType.getName().equals(VOID_CLASSNAME)) {
                iterator = message.getParameters();
                if (iterator.hasNext()) {
                    parameter = (Parameter) iterator.next();
                    p.p(responseObjType + " " + responseObjName + " = new ");
                    p.pln(responseObjType + "();");
                }
            } else if (
                (resultType == null
                    || resultType.getName().equals(VOID_CLASSNAME))
                    && responseBlock != null) {
                resultIsEmbedded = true;
            }
        }

        // build the call to the servant
        // NOTE - we should rewrite this code to iterate over the java parameters
        // instead of the request/response ones
        boolean declaredResult = declareRpcReturnType(p, operation, resultType);
        p.p(
            "(("
                + remoteClassName
                + ") getTarget())."
                + operation.getJavaMethod().getName()
                + "(");

        boolean hasBodyParams = false;
        int count = 0;
        // bugid 4721551, parameterOrder bug.
        // iterate over all the java parameters of the method, so that holders for response types
        // can be generated
        for (iterator = javaMethod.getParameters();
            iterator.hasNext();
            ++count) {
            javaParameter = (JavaParameter) iterator.next();
            parameter = javaParameter.getParameter();
            if (parameter.getBlock().getLocation() == Block.BODY) {
                hasBodyParams = true;
                javaMember = getJavaMember(parameter);
                if (count > 0)
                    p.p(", ");
                String unWrapMethod = "";
                // if we have an arrayWrapper for rpc/literal, unwrap it
                if (parameter.getType() instanceof LiteralArrayWrapperType) {
                    JavaStructureMember tmpJMember =
                        (JavaStructureMember)
                            (
                                (JavaStructureType)
                                    ((LiteralArrayWrapperType) parameter
                            .getType())
                            .getJavaType())
                            .getMembers()
                            .next();
                    unWrapMethod = ".toArray()";
                }
                if (parameter.getJavaParameter().isHolder()) {
                    p.p(parameter.getName() + "_holder");
                } else {
                    if (javaMember.isPublic())
                        p.p(
                            requestObjName
                                + "."
                                + parameter.getName()
                                + unWrapMethod);
                    else
                        p.p(
                            requestObjName
                                + "."
                                + javaMember.getReadMethod()
                                + "()"
                                + unWrapMethod);
                }
            }else if (parameter.getBlock().getLocation() == Block.ATTACHMENT) {
                if (count > 0)
                    p.p(", ");
                javaParameter = parameter.getJavaParameter();
                if (javaParameter.isHolder()) {
                    p.p(parameter.getName() + "_holder");
                } else {
                    p.p(parameter.getName());
                }
            }
        }


        /*//attachments
        // request attachments parameters always follow
        boolean hasInputMimePart = false;
        if (attachmentParameterCount > 0) {
            int count1 = 0;
            for (iterator = requestMessage.getParameters();
            iterator.hasNext();
            ++count1) {
                parameter = (Parameter) iterator.next();
                if (parameter.getBlock().getLocation() == Block.ATTACHMENT && parameter.getLinkedParameter() == null) {
                    hasInputMimePart = true;
                    if (hasBodyParams || count1 > 0)
                        p.p(", ");
                    p.p(parameter.getName());
                }
            }
        }

        if (operation.getResponse() != null) {
            // response attachments parameters come last
            int count1 = 0;
            for (iterator = operation.getResponse().getParameters();
            iterator.hasNext(); ++count1) {
                parameter = (Parameter) iterator.next();
                if (parameter.getBlock().getLocation() == Block.ATTACHMENT) {
                    if (hasBodyParams || hasInputMimePart || count1 > 0)
                        p.p(", ");
                    javaParameter = parameter.getJavaParameter();
                    if (javaParameter.isHolder()) {
                        p.p(parameter.getName() + "_holder");
                    } else {
                        p.p(parameter.getName());
                    }
                }
            }
        }*/


        // bug fix: 4857524
        count = 0;

        // request header parameters always follow
        if (headerParameterCount > 0) {
            for (iterator = requestMessage.getParameters();
                iterator.hasNext();
                ++count) {
                parameter = (Parameter) iterator.next();
                if (!parameter.getJavaParameter().isHolder()
                    && parameter.getBlock().getLocation() == Block.HEADER) {
                    if (hasBodyParams || count > 0)
                        p.p(", ");
                    p.p(parameter.getName());
                }
            }
        }

        if (operation.getResponse() != null) {
            // response header parameters come last
            for (iterator = operation.getResponse().getParameters();
                iterator.hasNext();
                ) {
                parameter = (Parameter) iterator.next();
                if (parameter.getBlock().getLocation() == Block.HEADER) {
                    p.p(", ");
                    javaParameter = parameter.getJavaParameter();
                    if (javaParameter.isHolder()) {
                        p.p(parameter.getName() + "_holder");
                    } else {
                        p.p(parameter.getName());
                    }
                }
            }
        }

        p.pln(");"); // end call to servant

        if (declaredResult)
            p.pO();
        p.pln();

        if (operation.getResponse() != null) {
            p.pln("SOAPHeaderBlockInfo headerInfo;");
            if (resultIsEmbedded) {
                p.pln(
                    responseObjType
                        + " "
                        + responseObjName
                        + " = new "
                        + responseObjType
                        + "();");
                iterator = message.getParameters();
                if (iterator.hasNext()) {
                    parameter = (Parameter) iterator.next();
                    javaMember = getJavaMember(parameter);
                    if (javaMember != null
                        && !parameter.getJavaParameter().isHolder()) {
                        if (javaMember.isPublic())
                            p.pln(
                                responseObjName
                                    + "."
                                    + parameter.getName()
                                    + " = _result;");
                        else
                            p.pln(
                                responseObjName
                                    + "."
                                    + javaMember.getWriteMethod()
                                    + "(_result);");
                    }
                }
            }
            for (iterator = operation.getResponse().getParameters();
                iterator.hasNext();
                ) {
                String memberName;
                parameter = (Parameter) iterator.next();
                responseBlock = parameter.getBlock();
                if (responseBlock.getLocation() == Block.BODY) {
                    javaMember = getJavaMember(parameter);
                    javaParameter = parameter.getJavaParameter();
                    if (parameter.getLinkedParameter() != null
                        || (javaParameter != null && javaParameter.isHolder())) {
                        memberName = parameter.getName() + "_holder.value";
                    } else {
                        memberName = parameter.getName();
                    }
                    if (javaMember != null) {
                        // if we have an arrayWrapper in the case of rpc/literal, wrap it
                        if (parameter.getType()
                            instanceof LiteralArrayWrapperType) {
                            memberName =
                                "new "
                                    + parameter.getType().getJavaType().getName()
                                    + "("
                                    + memberName
                                    + ")";
                        }
                        if (javaMember.isPublic())
                            p.pln(
                                responseObjName
                                    + "."
                                    + javaMember.getName()
                                    + " = "
                                    + memberName
                                    + ";");
                        else
                            p.pln(
                                responseObjName
                                    + "."
                                    + javaMember.getWriteMethod()
                                    + "("
                                    + memberName
                                    + ");");
                    }
                }
                if (responseBlock.getLocation() == Block.HEADER) {
                    javaParameter = parameter.getJavaParameter();
                    String qname =
                        env.getNames().getBlockQNameName(null, responseBlock);
                    if (javaParameter.isHolder()) {
                        memberName = parameter.getName() + "_holder.value";
                    } else {
                        memberName = parameter.getName();
                    }
                    p.pln(
                        "headerInfo = new SOAPHeaderBlockInfo("
                            + qname
                            + ", null, false);");
                    p.pln("headerInfo.setValue(" + memberName + ");");
                    p.pln(
                        "headerInfo.setSerializer("
                            + writerFactory
                                .createWriter(
                                    servicePackage,
                                    (AbstractType) responseBlock.getType())
                                .serializerMemberName()
                            + ");");
                    p.pln("state.getResponse().add(headerInfo);");
                }
            }
            p.pln();
            p.pln();

            iterator = operation.getResponse().getBodyBlocks();
            if(iterator.hasNext()) {
                responseBlock = (Block)iterator.next();
                if (responseBlock != null
                    && responseBlock.getLocation() == Block.BODY) {
                    p.pln(
                        "SOAPBlockInfo bodyBlock = new SOAPBlockInfo("
                            + env.getNames().getBlockQNameName(
                                operation,
                                responseBlock)
                            + ");");
                    // Fix for bug 4778917
                    String valueStr = responseObjName;
                    if (SimpleToBoxedUtil.isPrimitive(responseObjType)) {
                        valueStr =
                            SimpleToBoxedUtil.getBoxedExpressionOfType(
                                valueStr,
                                responseObjType);
                    }
                    p.pln("bodyBlock.setValue(" + valueStr + ");");
                    // end bug fix
                    String serializer =
                        writerFactory
                            .createWriter(
                                servicePackage,
                                (LiteralType) responseBlockType)
                            .serializerMemberName();
                    p.pln("bodyBlock.setSerializer(" + serializer + ");");
                    p.pln("state.getResponse().setBody(bodyBlock);");
                } else {
                    p.pln("SOAPBlockInfo bodyBlock = new SOAPBlockInfo(null);");
                    p.pln(
                        "bodyBlock.setSerializer(DummySerializer.getInstance());");
                    p.pln("state.getResponse().setBody(bodyBlock);");
                }
            }
            p.pln();
            addAttachmentsToResponse(p, operation.getResponse().getParameters());

            //CR-6660354, Merge from JavaCAPS RTS for backward compatibility
            //Check if the parts in the output message are empty
            //If so send the output message name in the response rather than sending an empty body             
            if(!operation.getResponse().getParameters().hasNext()){
                Block resBlock = (Block) message.getBodyBlocks().next();

                p.pln("SOAPBlockInfo bodyBlock = new SOAPBlockInfo("+
                                env.getNames().getBlockQNameName(operation, resBlock)+");");
                String serializer = writerFactory.createWriter(servicePackage, (LiteralType)responseBlockType).serializerMemberName();
                p.pln("bodyBlock.setSerializer("+serializer+");");
                p.pln("state.getResponse().setBody(bodyBlock);");
            }
        }
        writeCatchClauses(p, operation);
        p.pOln("}"); // catch
        p.pOln("}"); // end method
    }

    protected void writeDocumentLiteralOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException {

        String messageName = operation.getName().getLocalPart();
        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        Iterator iterator;
        Parameter parameter;
        JavaParameter javaParameter;
        JavaStructureMember javaMember;
        String requestObjType = null;
        String requestObjName = null;
        String responseObjType = null;
        String responseObjName = null;
        LiteralType responseBlockType = null;
        LiteralType requestBlockType = null;
        Message message;
        Block responseBlock = null;

        // find request object details
        Message requestMessage = operation.getRequest();
        int embeddedParameterCount = 0;
        int nonEmbeddedParameterCount = 0;
        int headerParameterCount = 0;
        int attachmentParameterCount = 0;

        for (iterator = requestMessage.getParameters(); iterator.hasNext();) {
            parameter = (Parameter) iterator.next();
            Block paramBlock = parameter.getBlock();
            if (paramBlock.getLocation() == Block.BODY) {
                if (parameter.isEmbedded()) {
                    ++embeddedParameterCount;
                } else {
                    ++nonEmbeddedParameterCount;
                }
            } else if (paramBlock.getLocation() == Block.HEADER) {
                // header block
                ++headerParameterCount;
            } else if (paramBlock.getLocation() == Block.ATTACHMENT) {
                // header block
                ++attachmentParameterCount;
            }
        }

        // sanity check
        if (nonEmbeddedParameterCount > 1
            || (nonEmbeddedParameterCount > 0 && embeddedParameterCount > 0)) {
            throw new GeneratorException(
                "generator.internal.error.should.not.happen",
                "tie.generator.002");
        }

        iterator = requestMessage.getBodyBlocks();
        Block requestBlock = null;
        String requestObjInit = null;
        if (iterator.hasNext()) {
            requestBlock = (Block) iterator.next();
            requestBlockType = (LiteralType) requestBlock.getType();
            requestObjType = requestBlockType.getJavaType().getName();
            requestObjInit = requestBlockType.getJavaType().getInitString();
            requestObjName =
                env.getNames().getTypeMemberName(
                    requestBlockType.getJavaType());
        }

        // declare the method
        writeInvokeMethodDecl(p, operation);

        // handle one way response first
        if (operation.getResponse() == null) {
            p.pln("flushHttpResponse(state);");
        }

        // declare the request objects
        declareRequestObjects(
            p,
            requestObjType,
            requestObjName,
            requestObjInit);

        declareHolderHeaderObjects(p, requestBlock, operation);

        boolean hasRequestHeaders = false;
        iterator = operation.getRequest().getHeaderBlocks();
        hasRequestHeaders = iterator.hasNext();
        if (hasRequestHeaders) {
            writeRequestHeaders(p, operation);
        }

        // attachements
        declareHolderAttachmentObjects(p, requestBlock, operation);

        p.plnI("try {");
        // find the response object details
        message = operation.getResponse();
        boolean resultIsEmbedded = false;
        if (message != null) {
            iterator = message.getBodyBlocks();
            if (iterator.hasNext()) {
                responseBlock = (Block) iterator.next();
                responseBlockType = (LiteralType) responseBlock.getType();
                responseObjType = responseBlockType.getJavaType().getName();
                responseObjName = "_response";
            }

            if (resultType != null
                && !resultType.getName().equals(VOID_CLASSNAME)) {
                iterator = message.getParameters();
                if (iterator.hasNext()) {
                    parameter = (Parameter) iterator.next();
                    if (parameter.isEmbedded()) {
                        resultIsEmbedded = true;
                        p.p(resultType.getName() + " "+parameter.getName()+" = ");
                        //p.p(resultType.getName() + " _result = ");
                    } else {
                        //p.p(responseObjType + " " + responseObjName + " = ");
                        p.p(resultType.getName() + " "+parameter.getName()+" = ");
                    }
                }
            } else if (resultType == null) {
                resultIsEmbedded = true;
            } else {
                boolean cond = (VersionUtil.compare(sourceVersion,
                    VersionUtil.JAXRPC_VERSION_111) >= 0);
                if (cond && resultType.getName().equals(VOID_CLASSNAME)) {
                    resultIsEmbedded = true;
                }
            }
        }




        // build the call to the servant
        // NOTE - we should rewrite this code to iterate over the java parameters
        // instead of the request/response ones
        p.p(
            "(("
                + remoteClassName
                + ") getTarget())."
                + operation.getJavaMethod().getName()
                + "(");

        boolean hasBodyParams = false;
        int count = 0;
        for(Iterator params = operation.getJavaMethod().getParameters();params.hasNext();count++){
            JavaParameter jp = (JavaParameter)params.next();
            Parameter param = jp.getParameter();
            if(param.getBlock().getLocation() == Block.BODY){
                if(param.getBlock() == requestBlock) {
                    if (nonEmbeddedParameterCount > 0) {
                        hasBodyParams = true;
                        if (count > 0)
                            p.p(", ");
                        p.p(requestObjName);
                    } else {
                            hasBodyParams = true;
                            javaMember = getJavaMember(param);
                            if (count > 0)
                                p.p(", ");
                                if (javaMember.isPublic())
                                    p.p(requestObjName + "." + param.getName());
                                else
                                    p.p(
                                            requestObjName
                                            + "."
                                            + javaMember.getReadMethod()
                                            + "()");
                    }
                }else if(param.getBlock() == responseBlock) {
                        if(param.getJavaParameter().isHolder()) {
                            if(hasBodyParams)
                                p.p(", ");
                            p.p(param.getName() + "_holder");
                            hasBodyParams = true;
                        }
                }
            }else if(param.getBlock().getLocation() == Block.ATTACHMENT){
                if (hasBodyParams || count > 0)
                    p.p(", ");
                if(param.getJavaParameter().isHolder())
                    p.p(param.getName()+ "_holder");
                else
                    p.p(param.getName());
                hasBodyParams = true;count++;
            }
        }

        // request header parameters always follow
        if (headerParameterCount > 0) {
            int count1 = 0;
            for (iterator = requestMessage.getParameters();
                iterator.hasNext();
                ++count) {
                parameter = (Parameter) iterator.next();
               //if (parameter.getBlock().getLocation() == Block.HEADER) {
                //Nagesh (01-02-2006): Brought in the following  condition from earlier version of JAX RPC 1.1.1, build R5. 
                //Otherwise the above commented condition is resulting extra parameters in the method call 
				//for message parts associated to soapbind:header
                if (!parameter.getJavaParameter().isHolder() && parameter.getBlock().getLocation() == Block.HEADER) {       
                	if (hasBodyParams || count > 0)
                        p.p(", ");
                    p.p(parameter.getName());
                }
            }
        }

        if (operation.getResponse() != null) {
            // response header parameters come last
            for (iterator = operation.getResponse().getParameters();
                iterator.hasNext();
                ) {
                parameter = (Parameter) iterator.next();
                if (parameter.getBlock().getLocation() == Block.HEADER) {
                    p.p(", ");
                    javaParameter = parameter.getJavaParameter();
                    if (javaParameter.isHolder()) {
                        p.p(parameter.getName() + "_holder");
                    } else {
                        p.p(parameter.getName());
                    }
                }
            }
        }

        p.pln(");"); // end call to servant

        p.pln();

        if (operation.getResponse() != null) {
            p.pln("SOAPHeaderBlockInfo headerInfo;");
            for (iterator = operation.getResponse().getParameters();
                iterator.hasNext();
                ) {
                String memberName;
                parameter = (Parameter) iterator.next();
                Block responseBlock1 = parameter.getBlock();
                if (responseBlock1.getLocation() == Block.HEADER) {
                    javaParameter = parameter.getJavaParameter();
                    String qname =
                        env.getNames().getBlockQNameName(null, responseBlock1);
                    if (javaParameter.isHolder()) {
                        memberName = parameter.getName() + "_holder.value";
                    } else {
                        memberName = parameter.getName();
                    }
                    p.pln(
                        "headerInfo = new SOAPHeaderBlockInfo("
                            + qname
                            + ", null, false);");
                    p.pln("headerInfo.setValue(" + memberName + ");");
                    p.pln(
                        "headerInfo.setSerializer("
                            + writerFactory
                                .createWriter(
                                    servicePackage,
                                    (AbstractType) responseBlock1.getType())
                                .serializerMemberName()
                            + ");");
                    p.pln("state.getResponse().add(headerInfo);");
                }
            }
            p.pln();

            p.pln();
            iterator = operation.getResponse().getParameters();
            // "responseBlock!=null" fix for bugs 4820706, 4823861
            if (!iterator.hasNext() && resultIsEmbedded && (responseBlock != null)) {
                p.pln(
                        responseObjType
                        + " "
                        + responseObjName
                        + " = new "
                        + responseObjType
                        + "();");
                iterator = message.getParameters();
                if (iterator.hasNext()) {
                    parameter = (Parameter) iterator.next();
                    javaMember = getJavaMember(parameter);
                    if (javaMember.isPublic())
                        p.pln(
                                responseObjName
                                + "."
                                + parameter.getName()
                                + " = _result;");
                    else
                        p.pln(
                                responseObjName
                                + "."
                                + javaMember.getWriteMethod()
                                + "(_result);");
                }
            }

            while (iterator.hasNext()) {
                String memberName;
                parameter = (Parameter) iterator.next();
                if (parameter.getBlock().getLocation() == Block.BODY){
                    if(resultIsEmbedded && (responseBlock != null)) {
                        javaMember = getJavaMember(parameter);
                        javaParameter = parameter.getJavaParameter();
                        if (parameter.getLinkedParameter() != null
                                || (javaParameter != null && javaParameter.isHolder())) {
                            memberName = parameter.getName() + "_holder.value";
                        } else {
                            memberName = parameter.getName();
                        }
                        if (javaMember != null) {
                            p.pln(
                                    responseObjType
                                    + " "
                                    + responseObjName
                                    + " = new "
                                    + responseObjType
                                    + "();");
                            // if we have an arrayWrapper in the case of rpc/literal, wrap it
                            if (parameter.getType()
                                    instanceof LiteralArrayWrapperType) {
                                memberName =
                                    "new "
                                    + parameter.getType().getJavaType().getName()
                                    + "("
                                    + memberName
                                    + ")";
                            }
                            if (javaMember.isPublic())
                                p.pln(
                                        responseObjName
                                        + "."
                                        + javaMember.getName()
                                        + " = "
                                        + memberName
                                        + ";");
                            else
                                p.pln(
                                        responseObjName
                                        + "."
                                        + javaMember.getWriteMethod()
                                        + "("
                                        + memberName
                                        + ");");
                            break;
                        }else{
                            if(parameter.getBlock() == responseBlock) {
                                String pName = parameter.getName();
                                JavaParameter javaParam = parameter.getJavaParameter();
                                if(javaParam != null && javaParam.isHolder()) {
                                    pName += "_holder.value";
                                }
                                p.pln(responseObjType
                                         + " "
                                         + responseObjName
                                         + " = " + pName+";");
                                break;
                            }
                        }
                    }else {
                        if(parameter.getBlock() == responseBlock) {
                            String pName = parameter.getName();
                            JavaParameter javaParam = parameter.getJavaParameter();
                            if(javaParam != null && javaParam.isHolder()) {
                                pName += "_holder.value";
                            }
                            p.pln(responseObjType
                                + " "
                                + responseObjName
                                + " = " + pName+";");
                        break;
                        }
                    }
                }
            }


            if (responseBlock != null) {
                p.pln(
                    "SOAPBlockInfo bodyBlock = new SOAPBlockInfo("
                        + env.getNames().getBlockQNameName(
                            operation,
                            responseBlock)
                        + ");");
                // Fix for bug 4778917
                String valueStr = responseObjName;
                if (SimpleToBoxedUtil.isPrimitive(responseObjType)) {
                    valueStr =
                        SimpleToBoxedUtil.getBoxedExpressionOfType(
                            valueStr,
                            responseObjType);
                }
                p.pln("bodyBlock.setValue(" + valueStr + ");");
                // end bug fix
                String serializer =
                    writerFactory
                        .createWriter(
                            servicePackage,
                            (LiteralType) responseBlockType)
                        .serializerMemberName();
                p.pln("bodyBlock.setSerializer(" + serializer + ");");
                p.pln("state.getResponse().setBody(bodyBlock);");
            } else if (responseBlock == null) {
                p.pln("SOAPBlockInfo bodyBlock = new SOAPBlockInfo(null);");
                p.pln(
                    "bodyBlock.setSerializer(DummySerializer.getInstance());");
                p.pln("state.getResponse().setBody(bodyBlock);");
            }
            addAttachmentsToResponse(p, operation.getResponse().getParameters());
        }
        writeCatchClauses(p, operation);
        p.pOln("}"); // catch
        p.pOln("}"); // end method
    }

    protected void writeReadFirstBodyElementDefault(
        IndentingWriter p,
        String opCode)
        throws IOException {
        p.pln(
            "throw new SOAPProtocolViolationException(\"soap.operation.unrecognized\", java.lang.Integer.toString("
                + opCode
                + "));");
    }

    /**
     * @param p
     * @param response
     */
    private void addAttachmentsToResponse(IndentingWriter p, Iterator params) throws IOException{
        boolean gotone = false;
        while(params.hasNext()){
            String memberName;
            String getUUIDMethod = null;
            Parameter parameter = (Parameter) params.next();
            Block responseBlock = parameter.getBlock();
            if (responseBlock.getLocation() == Block.ATTACHMENT) {
                JavaParameter javaParameter = parameter.getJavaParameter();
                String paramName = parameter.getName();
                if (javaParameter!= null && javaParameter.isHolder())
                    paramName += "_holder.value";
                String mimeType = null;
                String contentID = null;
                AbstractType pType = parameter.getType();
                if(pType instanceof LiteralAttachmentType) {
                    LiteralAttachmentType attType = (LiteralAttachmentType)pType;
                    if(attType.getJavaType().getRealName().equals("javax.activation.DataHandler")) {
                        mimeType = "(("+parameter.getType().getJavaType().getName()+")"+paramName + ").getContentType()";
                    }else {
                        mimeType = "\""+attType.getMIMEType()+"\"";
                    }

                    contentID = attType.getContentID();
                }
                p.pln("addAttachment(state.getResponse().getMessage(), (java.lang.Object)"+paramName+", "+mimeType+", "+ "\""+contentID+"\");");
                setAddAttachmentMethodFlag(true);
            }
        }
    }

    /**
     * @param p
     * @param requestBlock
     * @param operation
     */
    private void declareHolderAttachmentObjects(
        IndentingWriter p,
        Block requestBlock,
        Operation operation)
        throws IOException {
        //if (requestBlock != null) {
            //AbstractType requestBlockType = requestBlock.getType();
            //String requestObjType = requestBlockType.getJavaType().getName();
            //String requestObjName =
              //  env.getNames().getTypeMemberName(
                //    requestBlockType.getJavaType());
            JavaMethod javaMethod = operation.getJavaMethod();
            // declare holder/header objects
            Iterator iterator = javaMethod.getParameters();
            JavaParameter javaParameter;
            JavaStructureMember javaMember;
            boolean declaredHeaderObj = false;
            boolean declaredAttachmentObj = false;
            boolean mimeTypesDeclared = false;
            boolean isDataHandler = false;
            for (int i = 0; iterator.hasNext();) {
                javaParameter = (JavaParameter) iterator.next();
                if (javaParameter.getParameter().getBlock().getLocation()
                    == Block.ATTACHMENT) {
                    if(!mimeTypesDeclared) {
                        p.pln("String[] mimeTypes = null;");
                        mimeTypesDeclared = true;
                    }
                    //if its linked, which wont happen for attachment(?), then access the attachment parameter
                    if((javaParameter.getParameter().getLinkedParameter() != null)){
                        AbstractType pType = javaParameter.getParameter().getType();
                        if(pType instanceof LiteralAttachmentType) {
                            LiteralAttachmentType attType = (LiteralAttachmentType)pType;
                            String javaType = javaParameter.getParameter().getType().getJavaType().getName();
                            int index = attType.getContentID().indexOf('@');
                            String cId = attType.getContentID().substring(index+1);
                            if(attType.getJavaType().getRealName().equals("javax.activation.DataHandler")) {
                                isDataHandler = true;
                            }else {
                                isDataHandler = false;
                            }
                            List mimeList = attType.getAlternateMIMETypes();
                            p.pln("mimeTypes = new String["+mimeList.size()+"];");

                            int j = 0;
                            for(Iterator iter = mimeList.iterator(); iter.hasNext();j++) {
                                p.pln("mimeTypes["+j+"] = new String(\"" +(String)iter.next()+"\");");
                            }

                            p.pln(
                                    javaParameter.getParameter().getName()
                                    + "_holder.value = ("+ javaType+")getAttachment(state.getRequest().getMessage(), mimeTypes, \""+ cId+"\", "+String.valueOf(isDataHandler)+");");
                            setGetAttachmentMethodFlag(true);
                        }
                    }else if(!javaParameter.isHolder()){
                        AbstractType pType = javaParameter.getParameter().getType();
                        if(pType instanceof LiteralAttachmentType) {
                            LiteralAttachmentType attType = (LiteralAttachmentType)pType;
                            AbstractType paramType =
                                javaParameter.getParameter().getType();
                            int index = attType.getContentID().indexOf('@');
                            String cId = attType.getContentID().substring(index+1);
                            if(attType.getJavaType().getRealName().equals("javax.activation.DataHandler")) {
                                isDataHandler = true;
                            }else {
                                isDataHandler = false;
                            }
                            List mimeList = attType.getAlternateMIMETypes();
                            p.pln("mimeTypes = new String["+mimeList.size()+"];");

                            int j = 0;
                            for(Iterator iter = mimeList.iterator(); iter.hasNext();j++) {
                                p.pln("mimeTypes["+j+"] = new String(\"" +(String)iter.next()+"\");");
                            }

                            p.pln(
                                    paramType.getJavaType().getName()
                                    + " "
                                    + javaParameter.getParameter().getName()
                                    + " = ("
                                    +paramType.getJavaType().getName()+")getAttachment(state.getRequest().getMessage(), mimeTypes, \""+ cId+"\", "+String.valueOf(isDataHandler)+");");

                            setGetAttachmentMethodFlag(true);
                        }
                    }
                }
            }
        //}
    }

    protected void writeStaticMembers(IndentingWriter p, Map headerMap)
        throws IOException {
        super.writeStaticMembers(p, headerMap);
    }

    protected void writeUnderstoodHeadersMember(
        IndentingWriter p,
        Map headerMap)
        throws IOException {

        p.p(
            "private static final QName[] understoodHeaderNames = new QName[] { ");
        boolean first = true;
        Iterator operationsIter = operations.iterator();
        for (int i = 0; operationsIter.hasNext(); i++) {
            Operation operation = (Operation) operationsIter.next();
            Iterator blocks = operation.getRequest().getHeaderBlocks();
            while (blocks.hasNext()) {
                Block block = (Block) blocks.next();
                String qname =
                    env.getNames().getBlockQNameName(operation, block);
                if (!first) {
                    p.p(", ");
                }
                p.p(qname);
                first = false;
            }
        }
        p.pln(" };");
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#writePreResponseWritingHook(com.sun.xml.rpc.processor.util.IndentingWriter)
     */
    protected void writePreResponseWritingHook(IndentingWriter p, List operations)
        throws IOException {
        p.pln("");
        p.plnI("protected void preResponseWritingHook(StreamingHandlerState state) throws Exception {");
        p.pln("super.preResponseWritingHook(state);");
        p.plnI("switch (state.getRequest().getOperationCode()) {");
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
            p.pln("addNonExplicitAttachment(state);");
            p.pln("break;");
            p.pO();

        }
        p.pOln("}");
        p.pOln("}");
        p.pln();
        /*//p.pln("System.out.println(\"TIE: preResponseWritingHook()\");");
        p.pln("javax.xml.rpc.handler.soap.SOAPMessageContext smc = state.getMessageContext();");
        p.pln("javax.xml.soap.SOAPMessage message = state.getResponse().getMessage();");
        p.pln("Object c = smc.getProperty(com.sun.xml.rpc.server.ServerPropertyConstants.SET_ATTACHMENT_PROPERTY);");
        p.pln("smc.setProperty(com.sun.xml.rpc.server.ServerPropertyConstants.SET_ATTACHMENT_PROPERTY, null);");
        p.plnI("if(c != null && c instanceof java.util.Collection) {");
        p.plnI("for(java.util.Iterator iter = ((java.util.Collection)c).iterator(); iter.hasNext();) {");
        p.pln("Object attachment = iter.next();");
        p.plnI("if(attachment instanceof javax.xml.soap.AttachmentPart) {");
        p.pln("message.addAttachmentPart((javax.xml.soap.AttachmentPart)attachment);");
        p.pOln("}");
        p.pOln("}");
        p.pOln("}");
        p.pOln("}");
        p.pln();*/
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#writePostEnvelopeReadingHook(com.sun.xml.rpc.processor.util.IndentingWriter)
     */
    protected void writePostEnvelopeReadingHook(IndentingWriter p, List operations)
        throws IOException {

        p.pln();
        p.plnI("protected void postEnvelopeReadingHook(StreamingHandlerState state) throws Exception {");
        p.pln("super.postEnvelopeReadingHook(state);");
        p.plnI("switch (state.getRequest().getOperationCode()) {");
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
            p.pln("getNonExplicitAttachment(state);");
            p.pln("break;");
            p.pO();

        }
        p.pOln("}");
        p.pOln("}");
        /*//p.pln("System.out.println(\"TIE: writePostEnvelopeReadingHook()\");");
        p.pln("javax.xml.rpc.handler.soap.SOAPMessageContext smc = state.getMessageContext();");
        p.pln("javax.xml.soap.SOAPMessage message = state.getRequest().getMessage();");
        p.pln("java.util.ArrayList attachments = null;");
        p.plnI("for(java.util.Iterator iter = message.getAttachments(); iter.hasNext();) {");
        p.plnI("if(attachments == null) {");
        p.pln("attachments = new java.util.ArrayList();");
        p.pOln("}");
        p.pln("attachments.add(iter.next());");
        p.pOln("}");
        p.pln("smc.setProperty(com.sun.xml.rpc.server.ServerPropertyConstants.GET_ATTACHMENT_PROPERTY, attachments);");
        p.pOln("}");
        p.pln();*/
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#writeAttachmentHooks(com.sun.xml.rpc.processor.util.IndentingWriter)
     */
    protected void writeAttachmentHooks(IndentingWriter p) throws IOException {

        boolean generateGetNonExplicitAttachmentMethod = false;
        boolean generateAddNonExplicitAttachmentMethod = false;
        List reqOps = new ArrayList();
        List resOps = new ArrayList();
        Iterator iter = operations.iterator();
        for (int i = 0; iter.hasNext(); i++) {
            Operation operation = (Operation) iter.next();
            Request req = operation.getRequest();
            if(req != null && (req.getProperty(WSDLModelerBase.MESSAGE_HAS_MIME_MULTIPART_RELATED_BINDING) != null)) {
                if(!generateGetNonExplicitAttachmentMethod)
                    generateGetNonExplicitAttachmentMethod = true;
                reqOps.add(operation);
            }

            Response res = operation.getResponse();
            if(res != null && (res.getProperty(WSDLModelerBase.MESSAGE_HAS_MIME_MULTIPART_RELATED_BINDING) != null)) {
                if(!generateAddNonExplicitAttachmentMethod)
                    generateAddNonExplicitAttachmentMethod = true;
                resOps.add(operation);
            }
        }

        if(generateAddNonExplicitAttachmentMethod) {
            writePreResponseWritingHook(p, resOps);
            writeAddNonExplicitAttachment(p);
        }

        if(generateGetNonExplicitAttachmentMethod) {
            writePostEnvelopeReadingHook(p, reqOps);
            writeGetNonExplicitAttachment(p);
        }
    }

    /**
     * @param p
     */
    private void writeGetNonExplicitAttachment(IndentingWriter p) throws IOException {
        p.plnI("private void getNonExplicitAttachment(StreamingHandlerState state) throws Exception {");
        p.pln("javax.xml.rpc.handler.soap.SOAPMessageContext smc = state.getMessageContext();");
        p.pln("javax.xml.soap.SOAPMessage message = state.getRequest().getMessage();");
        p.pln("java.util.ArrayList attachments = null;");
        p.plnI("for(java.util.Iterator iter = message.getAttachments(); iter.hasNext();) {");
        p.plnI("if(attachments == null) {");
        p.pln("attachments = new java.util.ArrayList();");
        p.pOln("}");
        p.pln("attachments.add(iter.next());");
        p.pOln("}");
        p.pln("smc.setProperty(com.sun.xml.rpc.server.ServerPropertyConstants.GET_ATTACHMENT_PROPERTY, attachments);");
        p.pOln("}");

    }

    /**
     * @param p
     */
    private void writeAddNonExplicitAttachment(IndentingWriter p) throws IOException {
        p.pln();
        p.plnI("private void addNonExplicitAttachment(StreamingHandlerState state) throws Exception {");
        p.pln("javax.xml.rpc.handler.soap.SOAPMessageContext smc = state.getMessageContext();");
        p.pln("javax.xml.soap.SOAPMessage message = state.getResponse().getMessage();");
        p.pln("Object c = smc.getProperty(com.sun.xml.rpc.server.ServerPropertyConstants.SET_ATTACHMENT_PROPERTY);");
        p.pln("smc.setProperty(com.sun.xml.rpc.server.ServerPropertyConstants.SET_ATTACHMENT_PROPERTY, null);");
        p.plnI("if(c != null && c instanceof java.util.Collection) {");
        p.plnI("for(java.util.Iterator iter = ((java.util.Collection)c).iterator(); iter.hasNext();) {");
        p.pln("Object attachment = iter.next();");
        p.plnI("if(attachment instanceof javax.xml.soap.AttachmentPart) {");
        p.pln("message.addAttachmentPart((javax.xml.soap.AttachmentPart)attachment);");
        p.pOln("}");
        p.pOln("}");
        p.pOln("}");
        p.pOln("}");
    }

    protected void writeHooks(IndentingWriter p) throws IOException {
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_TIE_HOOKS_EXT_POINT);
        if (iter != null && iter.hasNext()) {
            // Atleast one plugin is extending these points
            writePreHandlingHook(p);
            writePostResponseWritingHook(p);
        }
    }

    protected void writePreHandlingHook(IndentingWriter p) throws IOException {
        p.pln();
        p.plnI("protected boolean preHandlingHook(StreamingHandlerState state) throws Exception {");
        p.pln("boolean bool = false;");
        // plugins write their block of preHandlingHook()
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_TIE_HOOKS_EXT_POINT);
        TieHooksState state = new TieHooksState();
        state.superDone = false;
        while(iter != null && iter.hasNext()) {
            TieHooksIf plugin = (TieHooksIf)iter.next();
            plugin.preHandlingHook(model, p, state);
        }
        if (!state.superDone) {
            p.pln("bool = super.preHandlingHook(state);");
        }
        p.pln("return bool;");
        p.pOln("}");
        p.pln();
    }

    protected void writePostResponseWritingHook(IndentingWriter p) throws IOException {
        p.pln();
        p.plnI("protected void postResponseWritingHook(StreamingHandlerState state) throws Exception {");
        // plugins write their block of postResponseWritingHook()
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_TIE_HOOKS_EXT_POINT);
        TieHooksState state = new TieHooksState();
        state.superDone = false;
        while(iter != null && iter.hasNext()) {
            TieHooksIf plugin = (TieHooksIf)iter.next();
            plugin.postResponseWritingHook(model, p, state);
        }
        if (!state.superDone) {
            p.pln("super.postResponseWritingHook(state);");
        }
        p.pOln("}");
        p.pln();
    }

    protected void writeStatic(IndentingWriter p) throws IOException {
        // plugins write their static code
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_TIE_HOOKS_EXT_POINT);
        while(iter != null && iter.hasNext()) {
            TieHooksIf plugin = (TieHooksIf)iter.next();
            plugin.writeTieStatic(model, port, p);
        }
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#operationHasEmptyBody(com.sun.xml.rpc.processor.model.Operation)
     */
    protected Operation operationHasEmptyBody(Operation operation) {
        if (operation.getRequest() != null
                && operation.getRequest().getBodyBlockCount() == 0)
            return operation;
        return null;
    }

}
