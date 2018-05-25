/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.rpc.processor.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.HeaderFault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
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
import com.sun.xml.rpc.tools.wscompile.StubHooksIf;
import com.sun.xml.rpc.tools.wscompile.StubHooksIf.StubHooksState;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;

/**
 *
 * @author JAX-RPC Development Team
 */
public class StubGenerator extends StubTieGeneratorBase {

    private static final String prefix = "_";
    private String dirPath = "";

    public StubGenerator() {
        super();
    }

    public StubGenerator(SOAPVersion ver) {
        super(ver);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new StubGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new StubGenerator(model, config, properties, ver);
    }

    private StubGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
        String key = ProcessorOptions.SOURCE_DIRECTORY_PROPERTY;
        dirPath = properties.getProperty(key);
    }

    private StubGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        super(model, config, properties, ver);
        String key = ProcessorOptions.SOURCE_DIRECTORY_PROPERTY;
        dirPath = properties.getProperty(key);
    }

    protected String getClassName() {
        return env.getNames().stubFor(port);
    }

    protected String getPrefix() {
        return prefix;
    }

    protected String getStateType() {
        return "StreamingSenderState";
    }

    protected Message getMessageToDeserialize(Operation operation) {
        return operation.getResponse();
    }

    protected String getStateGetRequestResponseString() {
        return "getResponse";
    }

    protected String getInitializeAccess() {
        return "public";
    }

    protected boolean superClassHasInitialize() {
        return true;
    }

    protected String getSOAPVersion() {
        if (port.getSOAPVersion().equals(SOAPVersion.SOAP_12.toString()))
            return "SOAPVersion.SOAP_12";
        else
            return "SOAPVersion.SOAP_11";
    }

    protected void writeImports(IndentingWriter p) throws IOException {
        super.writeImports(p);
        p.pln("import com.sun.xml.rpc.client.SenderException;");
        p.pln("import com.sun.xml.rpc.client.*;");
        p.pln("import com.sun.xml.rpc.client.http.*;");
        p.pln("import javax.xml.rpc.handler.*;");
        p.pln("import javax.xml.rpc.JAXRPCException;");
        p.pln("import javax.xml.rpc.soap.SOAPFaultException;");
    }

    protected void writeClassDecl(IndentingWriter p, String stubClassName)
        throws IOException {
        JavaInterface javaInterface = port.getJavaInterface();
        /*
         * Declare the stub class; implement all remote interfaces.
         */

        /* Here the filename for the Tie to be geenrated is
           retrieved to be set in the GeneratedFileInfo Object */
        File classFile =
            env.getNames().sourceFileForClass(
                stubClassName,
                stubClassName,
                new File(dirPath),
                env);
        GeneratedFileInfo fi = new GeneratedFileInfo();
        fi.setFile(classFile);
        fi.setType(GeneratorConstants.FILE_TYPE_STUB);
        env.addGeneratedFile(fi);

        p.plnI("public class " + Names.stripQualifier(stubClassName));
        p.pln("extends " + ID_STUB_BASE);
        p.p("implements " + javaInterface.getName());
        Iterator remoteInterfaces = javaInterface.getInterfaces();
        if (remoteInterfaces.hasNext()) {
            while (remoteInterfaces.hasNext()) {
                p.p(", ");
                p.p((String) remoteInterfaces.next());
            }
        }
        p.pln(" {");
        p.pln();
    }

    protected void writeConstructor(IndentingWriter p, String stubClassName)
        throws IOException {

        p.pln("/*");
        p.pln(" *  public constructor");
        p.pln(" */");
        p.plnI(
            "public "
                + Names.stripQualifier(stubClassName)
                + "(HandlerChain handlerChain) {");
        p.pln("super(handlerChain);");
        String address = port.getAddress();
        if (address != null && address.length() > 0) {
            p.pln(
                "_setProperty(ENDPOINT_ADDRESS_PROPERTY, \""
                    + address
                    + "\");");
        }
        p.pOln("}");
        p.pln("");

        /** SOAP 1.2 generation code */
        //p.pln("/*");
        //p.pln(" *  public constructor");
        //p.pln(" */");
        //p.plnI("public "+env.getNames().stripQualifier(stubClassName)+"(HandlerChain handlerChain, SOAPVersion ver) {");
        //p.pln("super(handlerChain, ver);");
        //  address = port.getAddress();
        //  if (address != null && address.length() > 0) {
        //  p.pln("_setProperty(ENDPOINT_ADDRESS_PROPERTY, \""+address+"\");");
        //  }
        //p.pOln("}");

    }

    protected void writeRpcEncodedOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException, GeneratorException {

        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        Iterator iterator;

        declareOperationMethod(p, operation);

        // check for null Holders
        iterator = javaMethod.getParameters();
        JavaParameter javaParameter;
        for (int i = 0; iterator.hasNext(); i++) {
            javaParameter = (JavaParameter) iterator.next();
            if (javaParameter.isHolder()) {
                p.plnI("if (" + javaParameter.getName() + " == null) {");
                p.pln(
                    "throw new IllegalArgumentException(\""
                        + javaParameter.getName()
                        + " cannot be null\");");
                p.pOln("}");
            }
        }

        p.plnI("try {");
        Message message;
        message = operation.getRequest();
        Block block = null;
        iterator = message.getBodyBlocks();
        if (iterator.hasNext())
            block = (Block) iterator.next();
        SOAPType type = (SOAPType) block.getType();
        String objType = type.getJavaType().getName();
        String objName =
            prefix + env.getNames().getTypeMemberName(type.getJavaType());
        p.pln();

        QName name = port.getName();

        p.pln("StreamingSenderState _state = _start(_handlerChain);");
        p.pln();
        p.pln("InternalSOAPMessage _request = _state.getRequest();");
        p.pln(
            "_request.setOperationCode("
                + env.getNames().getOPCodeName(operation.getUniqueName())
                + ");");
        p.plnI(objType + " " + objName + " =");
        p.pln("new " + objType + "();");
        p.pO();
        p.pln();
        iterator = message.getParameters();
        JavaStructureMember javaMember;
        String memberName;
        Block paramBlock;
        Parameter parameter;
        boolean declaredHeaderBlockInfo = false;
        while (iterator.hasNext()) {
            parameter = (Parameter) iterator.next();
            paramBlock = parameter.getBlock();
            if (paramBlock.getLocation() == Block.BODY) {
                javaMember = getJavaMember(parameter);
                if (parameter.getJavaParameter() != null
                    && parameter.getJavaParameter().isHolder()) {
                    memberName = parameter.getName() + ".value";
                } else {
                    memberName = parameter.getName();
                }
                if (javaMember.isPublic())
                    p.pln(
                        objName
                            + "."
                            + javaMember.getName()
                            + " = "
                            + memberName
                            + ";");
                else
                    p.pln(
                        objName
                            + "."
                            + javaMember.getWriteMethod()
                            + "("
                            + memberName
                            + ");");
            } else { // header block
                if (!declaredHeaderBlockInfo) {
                    p.pln("SOAPHeaderBlockInfo _headerInfo;");
                    declaredHeaderBlockInfo = true;
                }
                javaParameter = parameter.getJavaParameter();
                String qname =
                    env.getNames().getBlockQNameName(null, paramBlock);
                if (parameter.getLinkedParameter() != null
                    || (javaParameter != null && javaParameter.isHolder())) {
                    memberName = parameter.getName() + ".value";
                } else {
                    memberName = parameter.getName();
                }
                String serializer =
                    writerFactory
                        .createWriter(
                            servicePackage,
                            (SOAPType) paramBlock.getType())
                        .serializerMemberName();
                p.pln(
                    "_headerInfo = new SOAPHeaderBlockInfo("
                        + qname
                        + ", null, false);");
                p.pln("_headerInfo.setValue(" + memberName + ");");
                p.pln("_headerInfo.setSerializer(" + serializer + ");");
                p.pln("_request.add(_headerInfo);");
            }
        }

        p.pln();
        p.pln(
            "SOAPBlockInfo _bodyBlock = new SOAPBlockInfo("
                + env.getNames().getBlockQNameName(operation, block)
                + ");");
        p.pln("_bodyBlock.setValue(" + objName + ");");
        p.pln(
            "_bodyBlock.setSerializer("
                + writerFactory
                    .createWriter(servicePackage, type)
                    .serializerMemberName()
                + ");");
        p.pln("_request.setBody(_bodyBlock);");
        p.pln();
        String soapAction =
            operation.getSOAPAction() != null ? operation.getSOAPAction() : "";
        p.pln(
            "_state.getMessageContext().setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY, \""
                + soapAction
                + "\");");
        p.pln();
        if (operation.getResponse() != null) {
            p.pln(
                "_send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);");
        } else {
            p.pln(
                "_sendOneWay((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);");
        }

        p.pln();

        // declare the return type
        message = operation.getResponse();
        if (message != null) {
            iterator = message.getBodyBlocks();
            objName = null;
            objType = null;
            block = null;
            while (iterator.hasNext()) {
                block = (Block) iterator.next();
                if (block
                    .getName()
                    .getLocalPart()
                    .equals(
                        env.getNames().getResponseName(
                            operation.getName().getLocalPart()))) {
                    type = (SOAPType) block.getType();
                    objType = type.getJavaType().getName();
                    objName =
                        prefix
                            + env.getNames().getTypeMemberName(
                                type.getJavaType());
                    break;
                }
            }
            // Fix for bug 4778917
            String initString =
                (type == null ? "null" : type.getJavaType().getInitString());
            p.pln(objType + " " + objName + " = " + initString + ";");

            String objMemberName = "_responseObj";
            p.pln(
                "Object "
                    + objMemberName
                    + " = _state.getResponse().getBody().getValue();");
            p.plnI(
                "if ("
                    + objMemberName
                    + " instanceof SOAPDeserializationState) {");
            p.plnI(objName + " =");
            p.pln(
                "("
                    + objType
                    + ")((SOAPDeserializationState)"
                    + objMemberName
                    + ").getInstance();");
            p.pO();
            p.pOlnI("} else {");
            p.plnI(objName + " =");
            p.pln("(" + objType + ")" + objMemberName + ";");
            p.pO();
            p.pOln("}");
            p.pln();
            iterator = message.getParameters();
            boolean hasReturn =
                resultType != null
                    && !resultType.getName().equals(VOID_CLASSNAME);
            while (iterator.hasNext()) {
                parameter = (Parameter) iterator.next();
                javaParameter = parameter.getJavaParameter();
                paramBlock = parameter.getBlock();
                if (javaParameter == null || !javaParameter.isHolder()) {
                    continue;
                }
                if (paramBlock.getLocation() == Block.BODY) {
                    javaMember = getJavaMember(parameter);
                    p.plnI(javaParameter.getName() + ".value =");
                    if (javaMember.isPublic())
                        p.pln(objName + "." + javaMember.getName() + ";");
                    else
                        p.pln(
                            objName + "." + javaMember.getReadMethod() + "();");
                    p.pO();
                }
            }
            boolean hasResponseHeaders = false;
            iterator = operation.getResponse().getHeaderBlocks();
            hasResponseHeaders = iterator.hasNext();
            iterator = operation.getResponse().getParameters();
            if (hasResponseHeaders && iterator.hasNext()) {
                p.pln("Iterator _headers = _state.getResponse().headers();");
                p.pln("SOAPHeaderBlockInfo _curHeader;");
                p.pln("Object _headerObj;");
                p.plnI("while (_headers.hasNext()) {");
                p.pln("_curHeader = (SOAPHeaderBlockInfo)_headers.next();");
                boolean startedHeaders = false;
                while (iterator.hasNext()) {
                    parameter = (Parameter) iterator.next();
                    if (parameter.getBlock().getLocation() == Block.HEADER) {
                        if (startedHeaders) {
                            p.p(" else ");
                        }
                        startedHeaders = true;
                        String paramName = parameter.getName();
                        String paramType =
                            parameter.getType().getJavaType().getName();
                        String qname =
                            env.getNames().getBlockQNameName(
                                null,
                                parameter.getBlock());
                        String varName;
                        if (parameter.getType().getJavaType().isHolder()) {
                            varName = paramName + ".value";
                        } else {
                            varName = paramName;
                        }
                        p.plnI(
                            "if (_curHeader.getName().equals("
                                + qname
                                + ")) {");
                        p.pln("_headerObj = _curHeader.getValue();");
                        p.plnI(
                            "if (_headerObj instanceof SOAPDeserializationState) {");
                        p.pln(
                            paramName
                                + ".value = ("
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
            }
            if (hasReturn) {
                iterator = message.getParameters();
                parameter = (Parameter) iterator.next();
                if (parameter.getBlock() == block) {
                    javaMember = getJavaMember(parameter);
                    if (javaMember.isPublic())
                        p.pln("return " + parameter.getName() + ";");
                    else
                        p.pln(
                            "return "
                                + objName
                                + "."
                                + javaMember.getReadMethod()
                                + "();");
                }
            }
            p.pO();
        }

        writeOperationCatchBlock(p, operation);

        p.pOln("}"); // end stub method

    }

    /*
     * Declare stub method; throw exceptions declared in remote
     * interface(s).
     */
    private void declareOperationMethod(IndentingWriter p, Operation operation)
        throws IOException {
        JavaMethod javaMethod = operation.getJavaMethod();
        String methodName = javaMethod.getName();
        JavaType resultType = javaMethod.getReturnType();
        Iterator iterator;

        p.pln("/*");
        p.pln(" *  implementation of " + methodName);
        p.pln(" */");

        String resultName = "void";
        if (resultType != null) {
            // If we have an arrayWrapper in the case of rpc/literal, unwrap it.
            if (resultType instanceof JavaStructureType) {
                AbstractType literalResultType =
                    (AbstractType) ((JavaStructureType) resultType).getOwner();
                if (literalResultType instanceof LiteralArrayWrapperType
                    && operation.getStyle().equals(SOAPStyle.RPC)) {
                    resultName =
                        ((LiteralArrayWrapperType) literalResultType)
                            .getJavaArrayType()
                            .getName();
                } else {
                    resultName = resultType.getName();
                }
            } else {
                resultName = resultType.getName();
            }
        }

        p.p("public " + resultName + " " + methodName + "(");

        iterator = javaMethod.getParameters();
        JavaParameter javaParameter;
        for (int i = 0; iterator.hasNext(); i++) {
            if (i > 0)
                p.p(", ");
            javaParameter = (JavaParameter) iterator.next();
            if (javaParameter.isHolder()) {
                if (javaParameter.getHolderName() == null) {
                    p.p(
                        env.getNames().holderClassName(
                            port,
                            javaParameter.getType())
                            + " "
                            + javaParameter.getName());
                } else {
                    p.p(
                        javaParameter.getHolderName()
                            + " "
                            + javaParameter.getName());
                }
            } else {
                AbstractType paramType = javaParameter.getParameter().getType();
                // if we have an arrayWrapper in the case of rpc/literal, wrap it
                if (paramType instanceof LiteralArrayWrapperType
                    && operation.getStyle().equals(SOAPStyle.RPC)) {
                    p.p(
                        ((LiteralArrayWrapperType) paramType)
                            .getJavaArrayType()
                            .getName()
                            + " "
                            + javaParameter.getName());
                } else {
                    p.p(
                        javaParameter.getType().getName()
                            + " "
                            + javaParameter.getName());
                }
            }
        }
        p.plnI(")");
        iterator = javaMethod.getExceptions();
        if (iterator.hasNext()) {
            p.p("throws ");
            for (int i = 0; iterator.hasNext(); i++) {
                if (i > 0)
                    p.p(", ");
                p.p((String) iterator.next());
            }
            p.p(", ");
        } else {
            p.p("throws ");
        }
        p.p(ID_REMOTE_EXCEPTION);
        p.pln(" {");
        p.pln();
    }

    private void writeOperationCatchBlock(
        IndentingWriter p,
        Operation operation)
        throws IOException {
        Set faultSet = new TreeSet(new GeneratorUtil.FaultComparator());
        faultSet.addAll(operation.getFaultsSet());
        Iterator faults = faultSet.iterator();

        boolean hasIOException = false;
        if (faults != null) {
            Fault fault;
            while (faults.hasNext()) {
                fault = (Fault) faults.next();
                if (env
                    .getNames()
                    .customExceptionClassName(fault)
                    .equals(IOEXCEPTION_CLASSNAME)) {
                    hasIOException = true;
                } else {
                    p.plnI(
                        "} catch ("
                            + env.getNames().customExceptionClassName(fault)
                            + " e) {");
                    p.pln("throw e;");
                    p.pO();
                }
            }
        }
        p.plnI("} catch (RemoteException e) {");
        //headerfault
        Iterator faultsIter = faultSet.iterator();
        if (faultsIter.hasNext()) {
            p.plnI(
                "if (e.detail instanceof com.sun.xml.rpc.util.HeaderFaultException) {");
            p.pln(
                "com.sun.xml.rpc.util.HeaderFaultException hfe = (com.sun.xml.rpc.util.HeaderFaultException) e.detail;");
            p.pln(
                "SOAPHeaderBlockInfo headerBlock = (SOAPHeaderBlockInfo) hfe.getObject();");
            p.pln("java.lang.Object obj = headerBlock.getValue();");
            while (faultsIter.hasNext()) {
                Fault fault = (Fault) faultsIter.next();
                if (fault instanceof HeaderFault) {
                    p.plnI(
                        "if (obj instanceof "
                            + env.getNames().customExceptionClassName(fault)
                            + ") {");
                    p.pln(
                        "throw ("
                            + env.getNames().customExceptionClassName(fault)
                            + ") obj;");
                    p.pOln("}");
                }
            }
            p.pOln("}");
        }
        p.pln("// let this one through unchanged");
        p.pln("throw e;");
        if (hasIOException) {
            p.pOlnI("} catch (java.io.IOException e) {");
            p.pln("throw e;");
        }
        p.pOlnI("} catch (JAXRPCException e) {");
        p.pln("throw new RemoteException(e.getMessage(), e);");
        p.pOlnI("} catch (Exception e) {");
        p.plnI("if (e instanceof RuntimeException) {");
        p.pln("throw (RuntimeException)e;");
        p.pOlnI("} else {");
        p.pln("throw new RemoteException(e.getMessage(), e);");
        p.pOln("}");
        p.pOln("}"); // catch block
    }

    protected void writeRpcLiteralOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException, GeneratorException {

        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        Iterator iterator;
        Parameter parameter;
        JavaParameter javaParameter;
        Message message;
        Block paramBlock;
        String memberName;

        declareOperationMethod(p, operation);

        // check for null Holders
        iterator = javaMethod.getParameters();
        for (int i = 0; iterator.hasNext(); i++) {
            javaParameter = (JavaParameter) iterator.next();
            if (javaParameter.isHolder()) {
                p.plnI("if (" + javaParameter.getName() + " == null) {");
                p.pln(
                    "throw new IllegalArgumentException(\""
                        + javaParameter.getName()
                        + " cannot be null\");");
                p.pOln("}");
            }
        }

        p.plnI("try {");
        message = operation.getRequest();
        Block block = null;
        iterator = message.getBodyBlocks();
        if (iterator.hasNext()) {
            block = (Block) iterator.next();
        }
        p.pln();
        p.pln("StreamingSenderState _state = _start(_handlerChain);");
        p.pln();
        p.pln("InternalSOAPMessage _request = _state.getRequest();");
        p.pln(
            "_request.setOperationCode("
                + env.getNames().getOPCodeName(operation.getUniqueName())
                + ");");
        p.pln();

        LiteralType type =
            (block == null ? null : (LiteralType) block.getType());
        String objType = (type == null ? null : type.getJavaType().getName());
        String objName =
            (type == null
                ? null
                : prefix + env.getNames().getTypeMemberName(type.getJavaType()));

        boolean declaredHeaderBlockInfo = false;
        for (iterator = message.getParameters(); iterator.hasNext();) {
            parameter = (Parameter) iterator.next();
            paramBlock = parameter.getBlock();
            if (paramBlock.getLocation() == Block.BODY) {
                /*                if (parameter.isEmbedded()) {
                                    ++embeddedParameterCount;
                                }
                                else {
                                    objName = parameter.getJavaParameter().getName();
                                    ++nonEmbeddedParameterCount;
                                }*/
            } else if(paramBlock.getLocation() == Block.HEADER) {
                // header block
                if (!declaredHeaderBlockInfo) {
                    p.pln("SOAPHeaderBlockInfo _headerInfo;");
                    declaredHeaderBlockInfo = true;
                }
                javaParameter = parameter.getJavaParameter();
                String qname =
                    env.getNames().getBlockQNameName(null, paramBlock);
                memberName = parameter.getName();
                String varName = null;

                // bugfix 4955162
                if (parameter.getLinkedParameter() != null
                    || (javaParameter != null && javaParameter.isHolder())) {
                    varName = memberName + ".value";
                } else {
                    varName = memberName;
                }

                String serializer =
                    writerFactory
                        .createWriter(
                            servicePackage,
                            (LiteralType) paramBlock.getType())
                        .serializerMemberName();
                p.pln(
                    "_headerInfo = new SOAPHeaderBlockInfo("
                        + qname
                        + ", null, false);");
                p.pln("_headerInfo.setValue(" + varName + ");");
                p.pln("_headerInfo.setSerializer(" + serializer + ");");
                p.pln("_request.add(_headerInfo);");
            }
        }
        addAttachmentsToRequest(p, message.getParameters());

        // sanity check
        if (objName != null && objType != null) {
            p.pln(objType + " " + objName + " = new " + objType + "();");
        }
        JavaStructureMember javaMember;
        iterator = message.getParameters();
        while (iterator.hasNext()) {
            parameter = (Parameter) iterator.next();
            paramBlock = parameter.getBlock();
            if (paramBlock.getLocation() == Block.BODY) {
                // body block
                if (parameter.isEmbedded()) {
                    javaMember = getJavaMember(parameter);
                    if (parameter.getJavaParameter() != null) {
                        if (parameter.getJavaParameter().isHolder()) {
                            memberName = parameter.getName() + ".value";
                        } else {
                            memberName = parameter.getName();
                        }
                        // if we have an arrayWrapper in the case of rpc/literal, wrap it
                        if (parameter.getType()
                            instanceof LiteralArrayWrapperType) {
                            memberName =
                                "new "
                                    + ((LiteralArrayWrapperType) parameter
                                        .getType())
                                        .getJavaType()
                                        .getName()
                                    + "("
                                    + memberName
                                    + ")";
                        }

                        if (javaMember.isPublic())
                            p.pln(
                                objName
                                    + "."
                                    + javaMember.getName()
                                    + " = "
                                    + memberName
                                    + ";");
                        else
                            p.pln(
                                objName
                                    + "."
                                    + javaMember.getWriteMethod()
                                    + "("
                                    + memberName
                                    + ");");
                    }
                }
            }
        }

        p.pln();
        iterator = message.getBodyBlocks();
        if (iterator.hasNext()) {
            paramBlock = (Block) iterator.next();
            p.pln(
                "SOAPBlockInfo _bodyBlock = new SOAPBlockInfo("
                    + env.getNames().getBlockQNameName(operation, block)
                    + ");");

            // Fix for bug 4778917
            String valueStr = objName;
            if (SimpleToBoxedUtil.isPrimitive(objType)) {
                valueStr =
                    SimpleToBoxedUtil.getBoxedExpressionOfType(
                        valueStr,
                        objType);
            }
            p.pln("_bodyBlock.setValue(" + valueStr + ");");
            // end bug fix
            String serializer =
                writerFactory
                    .createWriter(
                        servicePackage,
                        (LiteralType) paramBlock.getType())
                    .serializerMemberName();
            p.pln("_bodyBlock.setSerializer(" + serializer + ");");
            p.pln("_request.setBody(_bodyBlock);");
            p.pln();
        } else {
            p.pln("SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(null);");
            p.pln("_bodyBlock.setSerializer(DummySerializer.getInstance());");
            p.pln("_request.setBody(_bodyBlock);");
            p.pln();
        }

        String soapAction =
            operation.getSOAPAction() != null ? operation.getSOAPAction() : "";
        p.pln(
            "_state.getMessageContext().setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY, \""
                + soapAction
                + "\");");
        p.pln();
        if (operation.getResponse() != null) {
            p.pln(
                "_send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);");
        } else {
            p.pln(
                "_sendOneWay((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);");
        }
        p.pln();

        // declare the return type
        message = operation.getResponse();
        if (message != null) {
            iterator = message.getBodyBlocks();
            objName = null;
            objType = null;
            block = null;
            while (iterator.hasNext()) {
                block = (Block) iterator.next();
                if (block
                    .getName()
                    .getLocalPart()
                    .equals(
                        env.getNames().getResponseName(
                            operation.getName().getLocalPart()))) {
                    type = (LiteralType) block.getType();
                    objType = type.getJavaType().getName();
                    objName =
                        prefix
                            + env.getNames().getTypeMemberName(
                                type.getJavaType());
                    break;
                }
            }
            boolean hasReturn =
                resultType != null
                    && !resultType.getName().equals(VOID_CLASSNAME);
            // Fix for bug 4778917
            String initString =
                (type == null ? "null" : type.getJavaType().getInitString());
            p.pln(objType + " " + objName + " = " + initString + ";");

            String objMemberName = "_responseObj";
            p.pln(
                "Object "
                    + objMemberName
                    + " = _state.getResponse().getBody().getValue();");
            p.plnI(
                "if ("
                    + objMemberName
                    + " instanceof SOAPDeserializationState) {");
            p.plnI(objName + " =");
            p.pln(
                "("
                    + objType
                    + ")((SOAPDeserializationState)"
                    + objMemberName
                    + ").getInstance();");
            p.pO();
            p.pOlnI("} else {");
            p.plnI(objName + " =");
            p.pln("(" + objType + ")" + objMemberName + ";");
            p.pO();
            p.pOln("}");
            p.pln();
            iterator = message.getParameters();
            while (iterator.hasNext()) {
                parameter = (Parameter) iterator.next();
                javaParameter = parameter.getJavaParameter();
                paramBlock = parameter.getBlock();
                if (javaParameter == null || !javaParameter.isHolder()) {
                    continue;
                }
                if (paramBlock.getLocation() == Block.BODY) {
                    javaMember = getJavaMember(parameter);
                    p.plnI(javaParameter.getName() + ".value =");
                    if (javaMember.isPublic())
                        p.pln(objName + "." + javaMember.getName() + ";");
                    else {
                        // We need to handle ArrayWrappers
                        if (javaMember.getType() instanceof JavaStructureType
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
                            p.pln(
                                "("
                                    + objName
                                    + "."
                                    + javaMember.getReadMethod()
                                    + "() != null) ?");
                            p.pln(
                                objName
                                    + "."
                                    + javaMember.getReadMethod()
                                    + "()."
                                    + tmpMember.getReadMethod()
                                    + "() : null;");
                        } else {
                            p.pln(
                                objName
                                    + "."
                                    + javaMember.getReadMethod()
                                    + "();");
                        }
                    }
                    p.pO();
                }
            }
            boolean hasResponseHeaders = false;
            iterator = operation.getResponse().getHeaderBlocks();
            hasResponseHeaders = iterator.hasNext();
            iterator = operation.getResponse().getParameters();
            if (hasResponseHeaders && iterator.hasNext()) {
                p.pln("java.util.Iterator _headers = _state.getResponse().headers();");
                p.pln("SOAPHeaderBlockInfo _curHeader;");
                p.pln("java.lang.Object _headerObj;");
                p.plnI("while (_headers.hasNext()) {");
                p.pln("_curHeader = (SOAPHeaderBlockInfo)_headers.next();");
                boolean startedHeaders = false;
                while (iterator.hasNext()) {
                    parameter = (Parameter) iterator.next();
                    if (parameter.getBlock().getLocation() == Block.HEADER) {
                        if (startedHeaders) {
                            p.p(" else ");
                        }
                        startedHeaders = true;
                        String paramName = parameter.getName();
                        String paramType =
                            parameter.getType().getJavaType().getName();
                        String qname =
                            env.getNames().getBlockQNameName(
                                null,
                                parameter.getBlock());
                        String varName;
                        if (parameter.getType().getJavaType().isHolder()) {
                            varName = paramName + ".value";
                        } else {
                            varName = paramName;
                        }
                        p.plnI(
                            "if (_curHeader.getName().equals("
                                + qname
                                + ")) {");
                        p.pln("_headerObj = _curHeader.getValue();");
                        p.plnI(
                            "if (_headerObj instanceof SOAPDeserializationState) {");
                        p.pln(
                            paramName
                                + ".value = ("
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
            }
            p.pln();
            getAttachmentFromResponse(p, message.getParameters());
            if (hasReturn) {
                iterator = message.getParameters();
                parameter = (Parameter) iterator.next();
                if (parameter.getBlock() == block) {
                    javaMember = getJavaMember(parameter);
                    String unWrapMethod = "";
                    // if we have an arrayWrapper for rpc/literal, unwrap it
                    if (parameter.getType()
                        instanceof LiteralArrayWrapperType) {
                        unWrapMethod = ".toArray()";
                    }
                    if (javaMember.isPublic())
                        p.pln(
                            "return "
                                + parameter.getName()
                                + unWrapMethod
                                + ";");
                    else
                        p.pln(
                            "return "
                                + objName
                                + "."
                                + javaMember.getReadMethod()
                                + "()"
                                + unWrapMethod
                                + ";");
                }else if(parameter.getBlock().getLocation() == Block.ATTACHMENT){
                    p.pln(
                            "return "
                            + parameter.getName()+";");
                }
            }
            p.pO();
        }
        writeOperationCatchBlock(p, operation);

        p.pOln("}");
    }

    protected void writeDocumentLiteralOperation(
        IndentingWriter p,
        String remoteClassName,
        Operation operation)
        throws IOException, GeneratorException {

        JavaMethod javaMethod = operation.getJavaMethod();
        JavaType resultType = javaMethod.getReturnType();
        Iterator iterator;
        Parameter parameter;
        JavaParameter javaParameter;
        Message message;
        Block paramBlock;
        String memberName;

        declareOperationMethod(p, operation);

        // check for null Holders
        iterator = javaMethod.getParameters();
        for (int i = 0; iterator.hasNext(); i++) {
            javaParameter = (JavaParameter) iterator.next();
            if (javaParameter.isHolder()) {
                p.plnI("if (" + javaParameter.getName() + " == null) {");
                p.pln(
                    "throw new IllegalArgumentException(\""
                        + javaParameter.getName()
                        + " cannot be null\");");
                p.pOln("}");
            }
        }

        p.plnI("try {");
        message = operation.getRequest();
        Block block = null;
        iterator = message.getBodyBlocks();
        if (iterator.hasNext()) {
            block = (Block) iterator.next();
        }
        p.pln();
        p.pln("StreamingSenderState _state = _start(_handlerChain);");
        p.pln();
        p.pln("InternalSOAPMessage _request = _state.getRequest();");
        p.pln(
            "_request.setOperationCode("
                + env.getNames().getOPCodeName(operation.getUniqueName())
                + ");");
        p.pln();

        LiteralType type =
            (block == null ? null : (LiteralType) block.getType());
        String objType = (type == null ? null : type.getJavaType().getName());
        String objName =
            (type == null
                ? null
                : prefix + env.getNames().getTypeMemberName(type.getJavaType()));

        int embeddedParameterCount = 0;
        int nonEmbeddedParameterCount = 0;
        boolean declaredHeaderBlockInfo = false;
        boolean declaredAttachmentBlockInfo = false;
        for (iterator = message.getParameters(); iterator.hasNext();) {
            parameter = (Parameter) iterator.next();
            paramBlock = parameter.getBlock();
            if (paramBlock.getLocation() == Block.BODY) {
                if (parameter.isEmbedded()) {
                    ++embeddedParameterCount;
                } else {
                    objName = parameter.getJavaParameter().getName();
                    ++nonEmbeddedParameterCount;
                }
            } else if(paramBlock.getLocation() == Block.HEADER) {
                // header block
                if (!declaredHeaderBlockInfo) {
                    p.pln("SOAPHeaderBlockInfo _headerInfo;");
                    declaredHeaderBlockInfo = true;
                }
                javaParameter = parameter.getJavaParameter();
                String qname =
                    env.getNames().getBlockQNameName(null, paramBlock);
                memberName = parameter.getName();
                String varName = null;
                if (javaParameter != null && javaParameter.isHolder()) {
                    varName = memberName + ".value";
                } else {
                    varName = memberName;
                }
                String serializer =
                    writerFactory
                        .createWriter(
                            servicePackage,
                            (LiteralType) paramBlock.getType())
                        .serializerMemberName();
                p.pln(
                    "_headerInfo = new SOAPHeaderBlockInfo("
                        + qname
                        + ", null, false);");
                p.pln("_headerInfo.setValue(" + varName + ");");
                p.pln("_headerInfo.setSerializer(" + serializer + ");");
                p.pln("_request.add(_headerInfo);");
            }
        }

        addAttachmentsToRequest(p, message.getParameters());

        // sanity check
        if (nonEmbeddedParameterCount > 1
            || (nonEmbeddedParameterCount > 0 && embeddedParameterCount > 0)) {
            throw new GeneratorException(
                "generator.internal.error.should.not.happen",
                "stub.generator.002");
        }

        if (embeddedParameterCount > 0
            || (embeddedParameterCount == 0 && nonEmbeddedParameterCount == 0)) {
            if (objName != null && objType != null) {
                p.pln(objType + " " + objName + " = new " + objType + "();");
            }
        }

        JavaStructureMember javaMember;
        iterator = message.getParameters();
        while (iterator.hasNext()) {
            parameter = (Parameter) iterator.next();
            paramBlock = parameter.getBlock();
            if (paramBlock.getLocation() == Block.BODY) {
                // body block
                if (parameter.isEmbedded()) {
                    javaMember = getJavaMember(parameter);
                    if (parameter.getJavaParameter() != null) {
                        memberName = parameter.getJavaParameter().getName();
                        if (javaMember.isPublic())
                            p.pln(
                                objName
                                    + "."
                                    + javaMember.getName()
                                    + " = "
                                    + memberName
                                    + ";");
                        else
                            p.pln(
                                objName
                                    + "."
                                    + javaMember.getWriteMethod()
                                    + "("
                                    + memberName
                                    + ");");
                    }
                }
            }
        }

        p.pln();
        iterator = message.getBodyBlocks();
        if (iterator.hasNext()) {
            paramBlock = (Block) iterator.next();
            p.pln(
                "SOAPBlockInfo _bodyBlock = new SOAPBlockInfo("
                    + env.getNames().getBlockQNameName(operation, block)
                    + ");");

            // Fix for bug 4778917
            String valueStr = objName;
            if (SimpleToBoxedUtil.isPrimitive(objType)) {
                valueStr =
                    SimpleToBoxedUtil.getBoxedExpressionOfType(
                        valueStr,
                        objType);
            }
            p.pln("_bodyBlock.setValue(" + valueStr + ");");
            // end bug fix
            String serializer =
                writerFactory
                    .createWriter(
                        servicePackage,
                        (LiteralType) paramBlock.getType())
                    .serializerMemberName();
            p.pln("_bodyBlock.setSerializer(" + serializer + ");");
            p.pln("_request.setBody(_bodyBlock);");
            p.pln();
        } else {
            p.pln("SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(null);");
            p.pln("_bodyBlock.setSerializer(DummySerializer.getInstance());");
            p.pln("_request.setBody(_bodyBlock);");
            p.pln();
        }

        String soapAction =
            operation.getSOAPAction() != null ? operation.getSOAPAction() : "";
        p.pln(
            "_state.getMessageContext().setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY, \""
                + soapAction
                + "\");");
        p.pln();
        if (operation.getResponse() != null) {
            p.pln(
                "_send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);");
        } else {
            p.pln(
                "_sendOneWay((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);");
        }
        p.pln();

        message = operation.getResponse();
        if (message != null) {
            iterator = message.getBodyBlocks();
            if (iterator.hasNext()) {
                block = (Block) iterator.next();
            }
            type = (block == null ? null : (LiteralType) block.getType());


            boolean hasResponseHeaders = false;
            iterator = operation.getResponse().getHeaderBlocks();
            hasResponseHeaders = iterator.hasNext();
            iterator = operation.getResponse().getParameters();
            if (hasResponseHeaders && iterator.hasNext()) {
                p.pln("Iterator _headers = _state.getResponse().headers();");
                p.pln("SOAPHeaderBlockInfo _curHeader;");
                p.pln("Object _headerObj;");
                p.plnI("while (_headers.hasNext()) {");
                p.pln("_curHeader = (SOAPHeaderBlockInfo)_headers.next();");
                boolean startedHeaders = false;
                while (iterator.hasNext()) {
                    parameter = (Parameter) iterator.next();
                    if (parameter.getBlock().getLocation() == Block.HEADER) {
                        if (startedHeaders) {
                            p.p(" else ");
                        }
                        startedHeaders = true;
                        String paramName = parameter.getName();
                        String paramType =
                            parameter.getType().getJavaType().getName();
                        String qname =
                            env.getNames().getBlockQNameName(
                                null,
                                parameter.getBlock());
                        String varName;
                        if (parameter.getType().getJavaType().isHolder()) {
                            varName = paramName + ".value";
                        } else {
                            varName = paramName;
                        }
                        p.plnI(
                            "if (_curHeader.getName().equals("
                                + qname
                                + ")) {");
                        p.pln("_headerObj = _curHeader.getValue();");
                        p.plnI(
                            "if (_headerObj instanceof SOAPDeserializationState) {");
                        p.pln(
                            paramName
                                + ".value = ("
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
            }

            // this code assumes that the first parameter is the result
            iterator = message.getParameters();
            while (iterator.hasNext()) {
                parameter = (Parameter) iterator.next();
                if(parameter.getBlock().getLocation() == Block.BODY) {
                    if (parameter.isEmbedded()) {
                        objName = prefix + "result";
                        objType = type.getJavaType().getName();
                    } else {
                        objName = prefix + "result";
                        objType = parameter.getType().getJavaType().getName();
                    }

                    // Fix for bug 4778917
                    String initString =
                        (type == null
                            ? "null"
                            : type.getJavaType().getInitString());
                    p.pln(objType + " " + objName + " = " + initString + ";");
                    p.pln(
                        "java.lang.Object _responseObj = _state.getResponse().getBody().getValue();");
                    p.plnI(
                        "if (_responseObj instanceof SOAPDeserializationState) {");
                    String valueStr =
                        "((SOAPDeserializationState) _responseObj).getInstance()";
                    if (SimpleToBoxedUtil.isPrimitive(objType)) {
                        String boxName =
                            SimpleToBoxedUtil.getBoxedClassName(objType);
                        valueStr =
                            SimpleToBoxedUtil.getUnboxedExpressionOfType(
                                "(" + boxName + ")" + valueStr,
                                objType);
                    } else {
                        valueStr = "(" + objType + ")" + valueStr;
                    }
                    p.pln(objName + " = " + valueStr + ";");
                    p.pOlnI("} else {");
                    valueStr = "_responseObj";
                    if (SimpleToBoxedUtil.isPrimitive(objType)) {
                        String boxName =
                            SimpleToBoxedUtil.getBoxedClassName(objType);
                        valueStr =
                            SimpleToBoxedUtil.getUnboxedExpressionOfType(
                                "(" + boxName + ")" + valueStr,
                                objType);
                    } else {
                        valueStr = "(" + objType + ")" + valueStr;
                    }
                    p.pln(objName + " = " + valueStr + ";");
                    p.pOln("}");
                    // end bug fix
                    p.pln();
                    break;
                }
            }
            //if(resultType.getName().equals(VOID_CLASSNAME)) {
                iterator = message.getParameters();
                while (iterator.hasNext()) {
                    Parameter param = (Parameter) iterator.next();
                    javaParameter = param.getJavaParameter();
                    paramBlock = param.getBlock();
                    if (javaParameter == null || !javaParameter.isHolder()) {
                        continue;
                    }
                    if (paramBlock.getLocation() == Block.BODY) {
                        p.plnI(javaParameter.getName() + ".value = "+objName+";");
                        p.pO();
                    }
                }
            //}
            getAttachmentFromResponse(p, message.getParameters());
            iterator = message.getParameters();
            while (iterator.hasNext()) {
                parameter = (Parameter) iterator.next();
                if(!resultType.getName().equals(VOID_CLASSNAME)){
                    if(parameter.getBlock().getLocation() == Block.BODY) {
                        if (parameter.isEmbedded()) {
                            javaMember = getJavaMember(parameter);
                            if (javaMember.isPublic()) {
                                p.pln(
                                    "return "
                                        + objName
                                        + "."
                                        + parameter.getName()
                                        + ";");
                            } else {
                                p.pln(
                                    "return "
                                        + objName
                                        + "."
                                        + javaMember.getReadMethod()
                                        + "();");
                            }
                        } else {
                            p.pln("return " + objName + ";");
                        }
                        break;
                    }else if(parameter.getBlock().getLocation() == Block.ATTACHMENT) {
                        p.pln("return " + parameter.getName()+";");
                        break;
                    }
                }
            }

            p.pln();
            p.pO();
        }

        writeOperationCatchBlock(p, operation);

        p.pOln("}");
    }

    /**
     * @param p
     * @param iterator
     */
    private void getAttachmentFromResponse(IndentingWriter p, Iterator params) throws IOException{
        boolean mimeTypesDeclared = false;
        boolean isDataHandler = false;
        while (params.hasNext()) {
            Parameter parameter = (Parameter) params.next();
            if ((parameter.getBlock().getLocation() == Block.ATTACHMENT)) {
                if(!mimeTypesDeclared) {
                    p.pln("java.lang.String[] mimeTypes = null;");
                    mimeTypesDeclared = true;
                }
                String paramName = parameter.getName();
                JavaParameter javaParam = parameter.getJavaParameter();

                if(javaParam != null && javaParam.isHolder())
                    paramName += ".value";
                else
                    paramName = parameter.getType().getJavaType().getName() + " " + paramName;

                LiteralAttachmentType attType = null;
                AbstractType pType = parameter.getType();
                if(pType instanceof LiteralAttachmentType) {
                    attType = (LiteralAttachmentType)pType;
                    int index = attType.getContentID().indexOf('@');
                    String cId = attType.getContentID().substring(index+1);
                    if(attType.getJavaType().getRealName().equals("javax.activation.DataHandler")) {
                        isDataHandler = true;
                    }else {
                        isDataHandler = false;
                    }
                    List mimeList = attType.getAlternateMIMETypes();
                    p.pln("mimeTypes = new java.lang.String["+mimeList.size()+"];");

                    int i = 0;
                    for(Iterator iter = mimeList.iterator(); iter.hasNext();i++) {
                        p.pln("mimeTypes["+i+"] = new java.lang.String(\"" +(String)iter.next()+"\");");
                    }

                    String typeName = parameter.getType().getJavaType().getName();
                    p.pln(paramName + "= ("+ typeName+
                            ")getAttachment(_state.getResponse().getMessage(), mimeTypes, \""+ cId+"\", "+String.valueOf(isDataHandler)+");");
                    setGetAttachmentMethodFlag(true);
                }
            }
        }
    }


    /**
     * @param p
     * @param iterator
     */
    private void addAttachmentsToRequest(IndentingWriter p, Iterator params) throws IOException{
        while(params.hasNext()){
            String memberName;
            String getUUIDMethod = null;
            Parameter parameter = (Parameter) params.next();
            Block responseBlock = parameter.getBlock();
            if (responseBlock.getLocation() == Block.ATTACHMENT) {
                JavaParameter javaParameter = parameter.getJavaParameter();
                String paramName = parameter.getName();
                if (javaParameter.isHolder())
                    paramName += ".value";
                String mimeType = null;
                String contentID = null;
                AbstractType pType = javaParameter.getParameter().getType();
                if(pType instanceof LiteralAttachmentType) {
                    LiteralAttachmentType attType = (LiteralAttachmentType)pType;
                    if(attType.getJavaType().getRealName().equals("javax.activation.DataHandler")) {
                        mimeType = "(("+parameter.getType().getJavaType().getName()+")"+paramName + ").getContentType()";
                    }else {
                        mimeType = "\""+attType.getMIMEType()+"\"";
                    }
                    contentID = attType.getContentID();
                }
                p.pln("addAttachment(_state.getRequest().getMessage(), (Object)"+paramName+", "+mimeType+", "+ "\""+contentID+"\");");
                setAddAttachmentMethodFlag(true);
            }
        }
    }

    protected void writeReadBodyFaultElement(IndentingWriter p)
        throws IOException {
        boolean hasFaults = false;
        Iterator operationsIter = operations.iterator();
        Operation operation;
        while (!hasFaults && operationsIter.hasNext()) {
            operation = (Operation) operationsIter.next();
            hasFaults = operation.getFaults().hasNext();
        }
        if (!hasFaults)
            return;
        p.pln("/*");
        p.pln(" *  this method deserializes fault responses");
        p.pln(" */");
        p.plnI(
            "protected Object _readBodyFaultElement(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {");
        p.pln("Object faultInfo = null;");
        p.pln("int opcode = state.getRequest().getOperationCode();");
        p.plnI("switch (opcode) {");
        operationsIter = operations.iterator();
        while (operationsIter.hasNext()) {
            operation = (Operation) operationsIter.next();
            // only do this if there are faults
            if (operation.getFaults().hasNext()) {
                p.plnI(
                    "case "
                        + env.getNames().getOPCodeName(operation.getUniqueName())
                        + ":");
                p.pln(
                    "faultInfo = "
                        + env.getNames().getClassMemberName(
                            env.getNames().faultSerializerClassName(
                                servicePackage,
                                port,
                                operation))
                        + ".deserialize(null, bodyReader, deserializationContext);");
                p.pln("break;");
                p.pO();
            }
        }
        p.plnI("default:");
        p.pln(
            "return super._readBodyFaultElement(bodyReader, deserializationContext, state);");
        p.pO();
        p.pOln("}"); // switch
        p.pln("return faultInfo;");
        p.pOln("}"); // method
    }

    protected void writeReadFirstBodyElementDefault(
        IndentingWriter p,
        String opCode)
        throws IOException {
        p.pln(
            "throw new SenderException(\"sender.response.unrecognizedOperation\", java.lang.Integer.toString("
                + opCode
                + "));");
    }

    protected boolean needsReadFirstBodyElementFor(Operation operation) {
        return operation.getResponse() != null;
    }

    protected void writeHandleEmptyBody(IndentingWriter p, Operation operation)
        throws IOException {
        p.pln("/*");
        p.pln(" * This method handles the case of an empty SOAP body.");
        p.pln(" */");
        p.plnI(
            "protected void _handleEmptyBody(XMLReader reader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {");
        p.pOln("}");
    }

    public void writeGenericMethods(IndentingWriter p) throws IOException {
        super.writeGenericMethods(p);
        // generate encoding-related stuff
        p.pln();
        p.plnI("public java.lang.String _getEncodingStyle() {");
        p.pln("return " + getEncodingStyle() + ";");
        p.pOln("}");
        p.pln();
        p.plnI("public void _setEncodingStyle(java.lang.String encodingStyle) {");
        p.pln(
            "throw new UnsupportedOperationException(\"cannot set encoding style\");");
        p.pOln("}");
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
            if (operation.getResponse() != null) {
                Iterator blocks = operation.getResponse().getHeaderBlocks();
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
        }
        p.pln(" };");
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#writePreSendingHookMethod(com.sun.xml.rpc.processor.util.IndentingWriter)
     */
    protected void writePreSendingHookMethod(IndentingWriter p, List operations)
        throws IOException {
        p.plnI("protected void _preSendingHook(StreamingSenderState state) throws Exception {");
        p.pln("super._preSendingHook(state);");
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
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#writePostSendingHook(com.sun.xml.rpc.processor.util.IndentingWriter)
     */
    protected void writePostSendingHook(IndentingWriter p, List operations) throws IOException {
        p.pln();
        p.plnI("protected void _postSendingHook(StreamingSenderState state) throws Exception {");
        p.pln("super._postSendingHook(state);");
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
        p.pln();
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#writePostSendingHook(com.sun.xml.rpc.processor.util.IndentingWriter)
     */
    protected void writeAddNonExplicitAttachment(IndentingWriter p) throws IOException {
        p.pln();
        p.plnI("private void addNonExplicitAttachment(StreamingSenderState state) throws Exception {");
        p.pln("javax.xml.rpc.handler.soap.SOAPMessageContext smc = state.getMessageContext();");
        p.pln("javax.xml.soap.SOAPMessage message = state.getRequest().getMessage();");
        p.pln("Object c = _getProperty(StubPropertyConstants.SET_ATTACHMENT_PROPERTY);");
        p.pln("_setProperty(StubPropertyConstants.SET_ATTACHMENT_PROPERTY, null);");
        p.plnI("if(c != null && c instanceof java.util.Collection) {");
        p.plnI("for(java.util.Iterator iter = ((java.util.Collection)c).iterator(); iter.hasNext();) {");
        p.pln("Object attachment = iter.next();");
        p.plnI("if(attachment instanceof javax.xml.soap.AttachmentPart) {");
        p.pln("message.addAttachmentPart((javax.xml.soap.AttachmentPart)attachment);");
        p.pOln("}");
        p.pOln("}");
        p.pOln("}");
        p.pOln("}");
        p.pln();
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#writePostSendingHook(com.sun.xml.rpc.processor.util.IndentingWriter)
     */
    protected void writeGetNonExplicitAttachment(IndentingWriter p) throws IOException {
        p.pln();
        p.plnI("private void getNonExplicitAttachment(StreamingSenderState state) throws Exception {");
        p.pln("javax.xml.rpc.handler.soap.SOAPMessageContext smc = state.getMessageContext();");
        p.pln("javax.xml.soap.SOAPMessage message = state.getResponse().getMessage();");
        p.pln("java.util.ArrayList attachments = null;");
        p.pln("java.util.Iterator iter = message.getAttachments();");
        p.plnI("while(iter.hasNext()) {");
        p.plnI("if(attachments == null) {");
        p.pln("attachments = new java.util.ArrayList();");
        p.pOln("}");
        p.pln("attachments.add(iter.next());");
        p.pOln("}");
        p.pln("_setProperty(StubPropertyConstants.GET_ATTACHMENT_PROPERTY, attachments);");
        p.pOln("}");
        p.pln();
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
                if(!generateAddNonExplicitAttachmentMethod)
                    generateAddNonExplicitAttachmentMethod = true;
                reqOps.add(operation);
            }

            Response res = operation.getResponse();
            if(res != null && (res.getProperty(WSDLModelerBase.MESSAGE_HAS_MIME_MULTIPART_RELATED_BINDING) != null)) {
                if(!generateGetNonExplicitAttachmentMethod)
                    generateGetNonExplicitAttachmentMethod = true;
                resOps.add(operation);
            }
        }

        if(generateAddNonExplicitAttachmentMethod) {
            writePreSendingHookMethod(p, reqOps);
            writeAddNonExplicitAttachment(p);
        }

        if(generateGetNonExplicitAttachmentMethod) {
            writePostSendingHook(p, resOps);
            writeGetNonExplicitAttachment(p);
        }
    }

    protected void writeHooks(IndentingWriter p) throws IOException {
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_STUB_HOOKS_EXT_POINT);
        if (iter != null && iter.hasNext()) {
            // Atleast one plugin is extending these points
            writePreHandlingHook(p);
            writePreRequestSendingHook(p);
        }
    }

    protected void writePreHandlingHook(IndentingWriter p) throws IOException {
        p.pln();
        p.plnI("protected void _preHandlingHook(StreamingSenderState state) throws Exception {");
        // plugins write their block of _preHandlingHook()
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_STUB_HOOKS_EXT_POINT);
        StubHooksState state = new StubHooksState();
        state.superDone = false;
        while(iter != null && iter.hasNext()) {
            StubHooksIf plugin = (StubHooksIf)iter.next();
            plugin._preHandlingHook(model, p, state);
        }
        if (!state.superDone) {
            p.pln("super._preHandlingHook(state);");
        }
        p.pOln("}");
        p.pln();
    }

    protected void writePreRequestSendingHook(IndentingWriter p) throws IOException {
        p.pln();
        p.plnI("protected boolean _preRequestSendingHook(StreamingSenderState state) throws Exception {");
        p.pln("boolean bool = false;");
        // plugins write their block of _preRequestSendingHook()
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_STUB_HOOKS_EXT_POINT);
        StubHooksState state = new StubHooksState();
        state.superDone = false;
        while(iter != null && iter.hasNext()) {
            StubHooksIf plugin = (StubHooksIf)iter.next();
            plugin._preRequestSendingHook(model, p, state);
        }
        if (!state.superDone) {
            p.pln("bool = super._preRequestSendingHook(state);");
        }
        p.pln("return bool;");
        p.pOln("}");
        p.pln();
    }

    protected void writeStatic(IndentingWriter p) throws IOException {
        // plugins write their static code
        Iterator iter = ToolPluginFactory.getInstance().getExtensions(
            ToolPluginConstants.WSCOMPILE_PLUGIN,
            ToolPluginConstants.WSCOMPILE_STUB_HOOKS_EXT_POINT);
        while(iter != null && iter.hasNext()) {
            StubHooksIf plugin = (StubHooksIf)iter.next();
            plugin.writeStubStatic(model, port,p);
        }
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.generator.StubTieGeneratorBase#operationHasEmptyBody(com.sun.xml.rpc.processor.model.Operation)
     */
    protected Operation operationHasEmptyBody(Operation operation) {
        if (operation.getResponse() != null
                && operation.getResponse().getBodyBlockCount() == 0) {
            return operation;
        }
        return null;
    }

}
