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
 * $Id: SOAPObjectSerializerGenerator.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SimpleTypeSerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPAttributeMember;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.model.soap.SOAPUnorderedStructureType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPObjectSerializerGenerator extends GeneratorBase {
    // vector of customized serializers/deserializers
    private Set visitedTypes;

    private static final String OBJECT_SERIALIZER_BASE = "ObjectSerializerBase";
    private static final String INTERFACE_SERIALIZER_BASE =
        "InterfaceSerializerBase";

    public SOAPObjectSerializerGenerator() {
        super();
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new SOAPObjectSerializerGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new SOAPObjectSerializerGenerator(model, config, properties);
    }

    private SOAPObjectSerializerGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        visitedTypes = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        Iterator types = model.getExtraTypes();
        AbstractType type;
        while (types.hasNext()) {
            type = (AbstractType) types.next();
            if (type.isSOAPType())
                 ((SOAPType) type).accept(this);
        }
        visitedTypes = null;
    }

    protected void visitFault(Fault fault) throws Exception {
        if (fault.getBlock().getType().isSOAPType()) {
            ((SOAPType) fault.getBlock().getType()).accept(this);
        }
        JavaException exception = fault.getJavaException();
        Iterator members = exception.getMembers();
        AbstractType aType = (AbstractType) exception.getOwner();
        if (aType.isSOAPType()) {
            SOAPType type;
            while (members.hasNext()) {
                type =
                    ((SOAPStructureMember) ((JavaStructureMember) members
                        .next())
                        .getOwner())
                        .getType();
                type.accept(this);
            }
        } else { // literal
            LiteralType type = null;
            JavaStructureMember javaMember;
            while (members.hasNext()) {
                javaMember = (JavaStructureMember) members.next();
                if (javaMember.getOwner() instanceof LiteralElementMember)
                    type =
                        ((LiteralElementMember) javaMember.getOwner())
                            .getType();
                else if (
                    javaMember.getOwner() instanceof LiteralAttributeMember)
                    type =
                        ((LiteralAttributeMember) javaMember.getOwner())
                            .getType();
                type.accept(this);
            }
        }
    }

    // SOAPType Visits
    protected void preVisitSOAPSimpleType(SOAPSimpleType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    protected void preVisitSOAPAnyType(SOAPAnyType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    protected void preVisitSOAPEnumerationType(SOAPEnumerationType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    protected void preVisitSOAPArrayType(SOAPArrayType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    public void preVisitSOAPStructureType(SOAPStructureType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);

        try {
            writeObjectSerializerForType(type);
        } catch (IOException e) {
            fail("generator.cant.write", type.getName().getLocalPart());
        }
    }

    private boolean haveVisited(SOAPType type) {
        return visitedTypes.contains(type);
    }

    private void typeVisited(SOAPType type) {
        visitedTypes.add(type);
    }

    /**
     * Generate a class to to custom Serialization/Desirialization for
     * a particular type
     */
    private void writeObjectSerializerForType(SOAPStructureType type)
        throws IOException {

        JavaType javaType = type.getJavaType();
        String className =
            env.getNames().typeObjectSerializerClassName(servicePackage, type);
        if ((donotOverride && GeneratorUtil.classExists(env, className))) {
            log("Class " + className + " exists. Not overriding.");
            return;
        }
        File classFile =
            env.getNames().sourceFileForClass(
                className,
                className,
                sourceDir,
                env);

        /* here the file Generated is added to 
           object along with its type */
        GeneratedFileInfo fi = new GeneratedFileInfo();
        fi.setFile(classFile);
        fi.setType(GeneratorConstants.FILE_TYPE_SOAP_OBJECT_SERIALIZER);
        env.addGeneratedFile(fi);

        try {
            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writeObjectSerializerCode(out, type);
            out.close();
        } catch (IOException e) {
            fail("generator.cant.write", classFile.toString());
        }
    }

    /**
     * Write the actual code for the Serializer/Deserializer for the Type
     * type
     */
    private void writeObjectSerializerCode(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {

        log("writing  serializer/deserializer for: " + type.getName());
        String className =
            env.getNames().typeObjectSerializerClassName(servicePackage, type);
        // Write package and import statements...
        writePackage(p, className);
        writeImports(p);
        p.pln();

        writeClassDecl(p, className);
        writeMembers(p, type);
        p.pln();
        writeConstructor(p, className);
        p.pln();
        writeInitialize(p, type);
        p.pln();
        writeDoDeserializeMethod(p, type);
        p.pln();
        writeDoSerializeAttributesMethod(p, type);
        p.pln();
        writeDoSerializeInstanceMethod(p, type);

        if (type instanceof RPCResponseStructureType) {
            // patch to avoid checking the name of a response structure
            writeVerifyNameOverrideMethod(p, type);
        }

        p.pOln("}"); // end
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln(
            "import com.sun.xml.rpc.encoding.literal.DetailFragmentDeserializer;");
        p.pln("import com.sun.xml.rpc.encoding.simpletype.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAP12Constants;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.namespace.QName;");
    }

    private void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        String baseClass = OBJECT_SERIALIZER_BASE;
        p.plnI(
            "public class "
                + Names.stripQualifier(className)
                + " extends "
                + baseClass
                + " implements Initializable {");
    }

    private void writeMembers(IndentingWriter p, SOAPStructureType type)
        throws IOException {
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        Set processedTypes = new HashSet();
        SOAPStructureMember member;
        JavaStructureMember javaMember;

        for (Iterator iter = type.getAttributeMembers(); iter.hasNext();) {
            SOAPAttributeMember attMember = (SOAPAttributeMember) iter.next();
            JavaStructureMember attJavaMember =
                attMember.getJavaStructureMember();
            GeneratorUtil.writeQNameDeclaration(
                p,
                attMember.getName(),
                env.getNames());
        }
        while (iterator.hasNext()) {
            javaMember = (JavaStructureMember) iterator.next();
            if (javaMember.getOwner() instanceof SOAPAttributeMember)
                continue;
            member = (SOAPStructureMember) javaMember.getOwner();
            GeneratorUtil.writeQNameDeclaration(
                p,
                member.getName(),
                env.getNames());
            SOAPEncoding.writeStaticSerializer(
                p,
                servicePackage,
                member.getType(),
                processedTypes,
                writerFactory,
                env.getNames());
        }
        iterator = javaStructure.getMembers();
        for (int i = 0; iterator.hasNext(); i++) {
            javaMember = (JavaStructureMember) iterator.next();
            p.p("private static final int ");
            p.pln(
                env.getNames().memberName(
                    javaMember.getName().toUpperCase() + "_INDEX")
                    + " = "
                    + i
                    + ";");
        }
    }

    private void writeConstructor(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public "
                + Names.stripQualifier(className)
                + "(QName type, boolean encodeType, "
                + "boolean isNullable, String encodingStyle) {");
        p.pln("super(type, encodeType, isNullable, encodingStyle);");
        p.pOln("}");
    }

    private void writeInitialize(IndentingWriter p, SOAPStructureType type)
        throws IOException {
        p.plnI(
            "public void initialize(InternalTypeMappingRegistry registry) throws java.lang.Exception {");
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        Set processedTypes = new HashSet();
        SOAPStructureMember member;
        JavaStructureMember javaMember;
        SerializerWriter writer;
        AbstractType memType;
        while (iterator.hasNext()) {
            javaMember = (JavaStructureMember) iterator.next();
            if (javaMember.getOwner() instanceof SOAPAttributeMember)
                continue;
            member = (SOAPStructureMember) javaMember.getOwner();
            memType = member.getType();
            if (!processedTypes
                .contains(
                    memType.getName()
                        + ";"
                        + memType.getJavaType().getRealName())) {
                writer = writerFactory.createWriter(servicePackage, memType);
                writer.initializeSerializer(
                    p,
                    env.getNames().getTypeQName(memType.getName()),
                    "registry");
                processedTypes.add(
                    member.getType().getName()
                        + ";"
                        + memType.getJavaType().getRealName());
            }
        }
        p.pOln("}");
    }

    private void writeDoDeserializeMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        if (((JavaStructureType) type.getJavaType()).isAbstract()) {
            p.plnI(
                "public java.lang.Object doDeserialize(SOAPDeserializationState state, XMLReader reader,");
            p.pln("SOAPDeserializationContext context) throws java.lang.Exception {");
            p.p(
                "throw new DeserializationException(\"soap.unsupportedType\", ");
            GeneratorUtil.writeNewQName(p, type.getName());
            p.pln(".toString());");
            p.pOln("}");
        } else if (deserializeToDetail(type)) {
            // Some exceptions need to be deserialized to a javax.xml.soap.Detail
            // element
            writeDetailDoDeserializeMethod(p, type);
        } else {
            writeStandardDoDeserializeMethod(p, type);
        }
    }

    public static boolean deserializeToDetail(AbstractType type) {
        JavaStructureType javaType = (JavaStructureType) type.getJavaType();
        // Some exceptions cannot be deserialized to detail
        if (!(javaType instanceof JavaException)) {
            return false;
        }
        Iterator members = javaType.getMembers();
        JavaStructureMember member;
        while (members.hasNext()) {
            member = (JavaStructureMember) members.next();
            if (member.getConstructorPos() < 0
                && member.getWriteMethod() == null) {
                return true;
            }
        }
        return false;
    }

    private void writeStandardDoDeserializeMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        JavaStructureType javaType = (JavaStructureType) type.getJavaType();
        JavaStructureMember[] constructorArgs =
            SOAPObjectBuilderGenerator.getConstructorArgs(javaType);
        boolean usesConstructor = constructorArgs.length > 0;
        p.plnI(
            "public java.lang.Object doDeserialize(SOAPDeserializationState state, XMLReader reader,");
        p.pln("SOAPDeserializationContext context) throws java.lang.Exception {");
        if (usesConstructor) {
            p.pln(javaType.getName() + " instance = null;");
            Iterator members = javaType.getMembers();
            JavaStructureMember member;
            String javaName;
            String initVal;
            while (members.hasNext()) {
                member = (JavaStructureMember) members.next();
                javaName = member.getType().getName();
                if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                    javaName = SimpleToBoxedUtil.getBoxedClassName(javaName);
                    initVal = member.getType().getInitString();
                    p.pln(
                        "java.lang.Object "
                            + member.getName()
                            + "Temp = new "
                            + javaName
                            + "("
                            + initVal
                            + ");");
                } else {
                    p.pln("java.lang.Object " + member.getName() + "Temp = null;");
                }
            }
        } else {
            p.pln(
                javaType.getName()
                    + " instance = new "
                    + javaType.getName()
                    + "();");
        }
        if (SOAPObjectBuilderGenerator.needBuilder(type)) {
            p.pln(
                env.getNames().typeObjectBuilderClassName(servicePackage, type)
                    + " builder = null;");
        }
        p.pln("java.lang.Object member;");
        p.pln("boolean isComplete = true;");
        p.pln("javax.xml.namespace.QName elementName;");
        p.pln();
        if (type.getAttributeMembersCount() > 0) {
            writeDeserializeAttributes(p, type, "reader", usesConstructor);
            p.pln();
        }
        p.pln("reader.nextElementContent();");

        if (type.getMembersCount() > 0) {
            if (type instanceof SOAPOrderedStructureType)
                writeDeserializeElements(
                    p,
                    (SOAPOrderedStructureType) type,
                    "reader",
                    constructorArgs);
            else if (type instanceof RPCResponseStructureType)
                writeDeserializeElements(
                    p,
                    (RPCResponseStructureType) type,
                    "reader");
            else if (type instanceof SOAPUnorderedStructureType)
                writeDeserializeElements(
                    p,
                    (SOAPUnorderedStructureType) type,
                    "reader");
            p.pln();
        }
        /* for now remove this for only JavaExceptions that have subclasses, eventually
         * we may need to remove this for all JavaTypes that have subclasses
         */
        if (type.getSubtypes() == null
            || !(javaType instanceof JavaException)
            || !type.getSubtypes().hasNext()) {
            p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.END);");
        }
        p.pln("return (isComplete ? (java.lang.Object)instance : (java.lang.Object)state);");
        p.pOln("}");
    }

    public static void writeDetailDoDeserializeMethod(
        IndentingWriter p,
        AbstractType type)
        throws IOException {

        if (type instanceof SOAPType) {
            p.plnI(
                "public java.lang.Object doDeserialize(SOAPDeserializationState state, XMLReader reader,");
            p.pln("SOAPDeserializationContext context) throws Exception {");
        } else if (type instanceof LiteralType) {
            p.plnI(
                "public java.lang.Object doDeserialize(XMLReader reader, SOAPDeserializationContext context) throws java.lang.Exception {");
        } else {
            fail(
                "generator.unsupported.type.encountered",
                type.getName().getLocalPart(),
                type.getName().getNamespaceURI());
        }
        p.pln("java.lang.Object detail;");
        p.pln(
            "DetailFragmentDeserializer detailDeserializer = new DetailFragmentDeserializer(type, encodingStyle);");
        p.pln(
            "detail = detailDeserializer.deserialize(reader.getName(), reader, context);");
        p.pln("return detail;");
        p.pOln("}");
    }

    private void writeDeserializeElements(
        IndentingWriter p,
        SOAPOrderedStructureType type,
        String reader,
        JavaStructureMember[] constructorArgs)
        throws IOException {

        boolean usesConstructor = constructorArgs.length > 0;
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        JavaStructureMember javaMember;
        while (iterator.hasNext()) {
            javaMember = (JavaStructureMember) iterator.next();
            p.pln("elementName = " + reader + ".getName();");
            writeMemberDeserializer(
                p,
                type,
                javaMember,
                reader,
                true,
                true,
                false,
                false,
                usesConstructor);
        }
        if (usesConstructor) {
            String javaName;
            p.plnI("if (isComplete) {");
            p.p("instance = new " + javaStructure.getName() + "(");
            String valueStr;
            for (int i = 0; i < constructorArgs.length; i++) {
                if (i > 0)
                    p.p(", ");
                javaMember = constructorArgs[i];
                javaName = javaMember.getType().getName();
                if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                    String boxName =
                        SimpleToBoxedUtil.getBoxedClassName(javaName);
                    valueStr =
                        SimpleToBoxedUtil.getUnboxedExpressionOfType(
                            "(" + boxName + ")" + javaMember.getName() + "Temp",
                            javaName);
                } else {
                    valueStr =
                        "(" + javaName + ")" + javaMember.getName() + "Temp";
                }
                p.p(valueStr);
            }
            p.pln(");");
            p.pO("}");
            if (SOAPObjectBuilderGenerator.needBuilder(type)) {
                p.plnI(" else {");
                iterator = javaStructure.getMembers();
                while (iterator.hasNext()) {
                    javaMember = (JavaStructureMember) iterator.next();
                    javaName = javaMember.getType().getName();
                    if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                        String boxName =
                            SimpleToBoxedUtil.getBoxedClassName(javaName);
                        valueStr =
                            SimpleToBoxedUtil.getUnboxedExpressionOfType(
                                "("
                                    + boxName
                                    + ")"
                                    + javaMember.getName()
                                    + "Temp",
                                javaName);
                    } else {
                        valueStr =
                            "("
                                + javaName
                                + ")"
                                + javaMember.getName()
                                + "Temp";
                    }
                    String writeMethod = javaMember.getWriteMethod();
                    if (writeMethod == null) {
                        writeMethod =
                            "set"
                                + StringUtils.capitalize(javaMember.getName());
                    }
                    p.pln("builder." + writeMethod + "(" + valueStr + ");");
                }
                p.pOln("}");
            }
        }
    }

    private void writeDeserializeElements(
        IndentingWriter p,
        RPCResponseStructureType type,
        String reader)
        throws IOException {

        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        JavaStructureMember javaMember;
        int memberCnt = javaStructure.getMembersCount();
        if (curSOAPVersion.equals(SOAPVersion.SOAP_12)) {
            p.pln("// SOAP 1.2 deserializer result element");
            p.plnI("if (" + reader + ".getState() == XMLReader.START) {");
            p.plnI(
                "if(reader.getName().equals(SOAP12Constants.QNAME_SOAP_RESULT)) {");
            p.pln("reader.skipElement();");
            p.pln("reader.nextElementContent();");
            p.pOln("}");
            p.pOln("}");
        }
        // bug fix 4955556    
        if (memberCnt > 1) {
            p.plnI("for (int i=0; i<"+memberCnt+"; i++) {");
            p.pln("elementName = "+reader+".getName();");
            p.plnI("if ("+reader+".getState() == XMLReader.END) {");
            p.pln("break;");
            p.pOln("}");
        } else {
            p.pln("elementName = "+reader+".getName();");
        }
        for (int i =0;iterator.hasNext();i++) {
            javaMember = (JavaStructureMember)iterator.next();
            writeMemberDeserializer(p, type, javaMember, reader, true, true,
                memberCnt>1, !iterator.hasNext(), false);
        }
        if (memberCnt > 1)
            p.pOln("}");
             
//        for (int i=0;iterator.hasNext();i++) {
//            javaMember = (JavaStructureMember)iterator.next();
//            writeMemberDeserializer(p, type, javaMember, reader, i>0, i>0,
//                memberCnt>2 && i>0, !iterator.hasNext(), false);
//            if (i==0) {
//                if (memberCnt > 2) {
//                    p.plnI("for (int i=0; i<"+(memberCnt-1)+"; i++) {");
//                }
//                p.pln("elementName = "+reader+".getName();");
//            }
//        }
//        if (memberCnt > 2)
//            p.pOln("}");
          // end bug fix 4955556
    }

    private void writeDeserializeElements(
        IndentingWriter p,
        SOAPUnorderedStructureType type,
        String reader)
        throws IOException {

        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        JavaStructureMember javaMember;
        int memberCnt = javaStructure.getMembersCount();
        if (memberCnt > 1) {
            p.plnI("for (int i=0; i<" + memberCnt + "; i++) {");
            p.pln("elementName = " + reader + ".getName();");
            p.plnI("if (" + reader + ".getState() == XMLReader.END) {");
            p.pln("break;");
            p.pOln("}");
        } else {
            p.pln("elementName = " + reader + ".getName();");
        }
        for (int i = 0; iterator.hasNext(); i++) {
            javaMember = (JavaStructureMember) iterator.next();
            writeMemberDeserializer(
                p,
                type,
                javaMember,
                reader,
                true,
                true,
                memberCnt > 1,
                !iterator.hasNext(),
                false);
        }
        if (memberCnt > 1)
            p.pOln("}");
    }

    private void writeMemberDeserializer(
        IndentingWriter p,
        SOAPStructureType type,
        JavaStructureMember javaMember,
        String reader,
        boolean preCheckElementName,
        boolean checkElementName,
        boolean unOrdered,
        boolean writeThrow,
        boolean usesConstructor)
        throws IOException {

        String serializer;
        boolean referenceable;

        if (javaMember.getOwner() instanceof SOAPAttributeMember)
            return;
        SOAPStructureMember member =
            (SOAPStructureMember) javaMember.getOwner();
        String memberConstName =
            env.getNames().memberName(javaMember.getName().toUpperCase());
        String memberQName = env.getNames().getQNameName(member.getName());
        if (checkElementName == false) {
            memberQName = "null";
        }

        if (!unOrdered) {
            p.plnI("if (" + reader + ".getState() == XMLReader.START) {");
        }

        if (preCheckElementName && checkElementName) {
            p.plnI("if (elementName.equals(" + memberQName + ")) {");
        }
        SerializerWriter writer =
            writerFactory.createWriter(servicePackage, member.getType());
        serializer = writer.deserializerMemberName();
        referenceable = member.getType().isReferenceable();
        String memberName = "member";
        // see if we need to handle references
        p.pln(
            "member = "
                + serializer
                + ".deserialize("
                + memberQName
                + ", "
                + reader
                + ", context);");
        if (referenceable) {
            p.plnI("if (member instanceof SOAPDeserializationState) {");
            p.plnI("if (builder == null) {");
            p.pln(
                "builder = new "
                    + env.getNames().typeObjectBuilderClassName(
                        servicePackage,
                        type)
                    + "();");
            p.pOln("}");
            p.pln(
                "state = registerWithMemberState(instance, state, member, "
                    + memberConstName
                    + "_INDEX, builder);");
            p.pln("isComplete = false;");
            p.pOlnI("} else {");
            if (usesConstructor) {
                p.pln(javaMember.getName() + "Temp = member;");
            } else if (javaMember.isPublic()) {
                p.pln(
                    "instance."
                        + javaMember.getName()
                        + " = ("
                        + javaMember.getType().getName()
                        + ")member;");
            } else {
                if (javaMember.getDeclaringClass() != null) {
                    p.pln(
                        "(("
                            + javaMember.getDeclaringClass().replace('$', '.')
                            + ")instance)."
                            + javaMember.getWriteMethod()
                            + "(("
                            + javaMember.getType().getName()
                            + ")member);");
                } else {
                    p.pln(
                        "instance."
                            + javaMember.getWriteMethod()
                            + "(("
                            + javaMember.getType().getName()
                            + ")member);");
                }
            }
            p.pOln("}"); // member instanceof
        } else { // primitive
            String valueStr = null;
            String javaName = javaMember.getType().getName();

            if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
                valueStr =
                    SimpleToBoxedUtil.getUnboxedExpressionOfType(
                        "(" + boxName + ")member",
                        javaName);
            } else {
                valueStr = "(" + javaName + ")member";
            }

            if (usesConstructor) {
                p.pln(javaMember.getName() + "Temp = member;");
            } else if (javaMember.isPublic()) {
                p.pln(
                    "instance."
                        + javaMember.getName()
                        + " = "
                        + valueStr
                        + ";");
            } else {
                if (javaMember.getDeclaringClass() != null) {
                    p.pln(
                        "(("
                            + javaMember.getDeclaringClass().replace('$', '.')
                            + ")instance)."
                            + javaMember.getWriteMethod()
                            + "("
                            + valueStr
                            + ");");
                } else {
                    p.pln(
                        "instance."
                            + javaMember.getWriteMethod()
                            + "("
                            + valueStr
                            + ");");
                }
            }
        }
        p.pln(reader + ".nextElementContent();");
        if (unOrdered)
            p.pln("continue;");
        if (preCheckElementName && checkElementName) {
            p.pO("}"); // elemName match
            if (writeThrow) {
                p.plnI(" else {");
                p.pln(
                    "throw new DeserializationException(\"soap.unexpectedElementName\", new Object[] {"
                        + memberQName
                        + ", elementName});");
                p.pOln("}");
            } else {
                p.pln();
            }
        }
        if (!unOrdered)
            p.pOln("}"); // XMLReader.START check
    }

    private void writeDoSerializeAttributesMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {

        p.plnI(
            "public void doSerializeAttributes(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {");
        p.pln(
            type.getJavaType().getName()
                + " instance = ("
                + type.getJavaType().getName()
                + ")obj;");
        p.pln();
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        SerializerWriter writer;
        String serializer;
        String memberConstName;
        String memberQName;

        for (Iterator iterator = type.getAttributeMembers();
            iterator.hasNext();
            ) {
            SOAPAttributeMember member = (SOAPAttributeMember) iterator.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            memberConstName = member.getName().getLocalPart().toUpperCase();
            memberQName = env.getNames().getQNameName(member.getName());
            writer =
                writerFactory.createWriter(servicePackage, member.getType());
            serializer = writer.serializerMemberName();
            String valueStr = null;
            String javaName = javaMember.getType().getName();

            // instead of calling a serializer, we call an encoder
            String encoder;
            if (member.getType() instanceof SOAPEnumerationType) {
                encoder = member.getType().getJavaType().getName() + "_Encoder";

            } else {
                encoder =
                    SimpleTypeSerializerWriter.getTypeEncoder(member.getType());
            }
            if (javaMember.isPublic()) {
                valueStr = "instance." + javaMember.getName();
            } else {
                String methName = javaMember.getReadMethod();
                valueStr = "instance." + methName + "()";
            }

            if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                valueStr =
                    SimpleToBoxedUtil.getBoxedExpressionOfType(
                        valueStr,
                        javaName);
            }
            //bug fix: 4862786
            p.plnI("if (" + valueStr + " != null) {");
            p.pln(
                "writer.writeAttribute("
                    + memberQName
                    + ", "
                    + encoder
                    + ".getInstance().objectToString("
                    + valueStr
                    + ", writer));");
            p.pOln("}");
            if (member.isRequired()) {
                p.plnI("else {");
                p.pln(
                    "throw new DeserializationException(\"literal.requiredAttributeConstraint\", new java.lang.Object[] {"
                        + memberQName
                        + "});");
                p.pOln("}");
            }
        }

        p.pOln("}"); // method
    }

    private void writeDoSerializeInstanceMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        p.plnI(
            "public void doSerializeInstance(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {");
        p.pln(
            type.getJavaType().getName()
                + " instance = ("
                + type.getJavaType().getName()
                + ")obj;");
        p.pln();
        boolean deserializeToDetail = deserializeToDetail(type);
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        SerializerWriter writer;
        String serializer;
        JavaStructureMember javaMember;
        SOAPStructureMember member;
        String memberConstName;
        String memberQName;

        for (int i = 0; iterator.hasNext(); i++) {
            javaMember = (JavaStructureMember) iterator.next();
            if (javaMember.getOwner() instanceof SOAPAttributeMember)
                continue;
            member = (SOAPStructureMember) javaMember.getOwner();
            memberConstName = member.getName().getLocalPart().toUpperCase();
            memberQName = env.getNames().getQNameName(member.getName());
            writer =
                writerFactory.createWriter(servicePackage, member.getType());
            serializer = writer.serializerMemberName();
            String valueStr = null;
            String javaName = javaMember.getType().getName();
            if (i == 0
                && type instanceof RPCResponseStructureType
                && curSOAPVersion.equals(SOAPVersion.SOAP_12)) {
                p.pln(
                    "// SOAP 1.2 - add rpc namespace, and add rpc:result and result element qname");
                p.pln(
                    "writer.startElement(SOAP12Constants.QNAME_SOAP_RESULT);");
                p.pln(
                    "writer.writeChars(writer.getPrefix("
                        + memberQName
                        + ".getNamespaceURI())+\":\"+"
                        + memberQName
                        + ".getLocalPart());");
                p.pln("writer.endElement();//rpc:result");
            }

            if (javaMember.isPublic()) {
                valueStr = "instance." + javaMember.getName();
            } else {
                String methName = javaMember.getReadMethod();
                valueStr = "instance." + methName + "()";
            }
            if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                valueStr =
                    SimpleToBoxedUtil.getBoxedExpressionOfType(
                        valueStr,
                        javaName);
            } else if (deserializeToDetail) {
                p.plnI("if (" + valueStr + " != null) {");
            }
            p.pln(
                serializer
                    + ".serialize("
                    + valueStr
                    + ", "
                    + memberQName
                    + ", null, writer, context);");
            if (deserializeToDetail
                && !SimpleToBoxedUtil.isPrimitive(javaName)) {
                p.pOln("}");
            }
        }
        p.pOln("}"); // method
    }

    private void writeDeserializeAttributes(
        IndentingWriter p,
        SOAPStructureType type,
        String reader,
        boolean usesConstructor)
        throws IOException {

        p.pln("Attributes attributes = reader.getAttributes();");
        p.pln("String attribute = null;");
        for (Iterator iterator = type.getAttributeMembers();
            iterator.hasNext();
            ) {
            SOAPAttributeMember member = (SOAPAttributeMember) iterator.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            String memberConstName =
                member.getName().getLocalPart().toUpperCase();
            String memberQName = env.getNames().getQNameName(member.getName());
            p.pln("attribute = attributes.getValue(" + memberQName + ");");
            p.plnI("if (attribute != null) {");

            String encoder;
            if (member.getType() instanceof SOAPEnumerationType) {
                encoder = member.getType().getJavaType().getName() + "_Encoder";
            } else {
                encoder =
                    SimpleTypeSerializerWriter.getTypeEncoder(member.getType());
            }
            if (usesConstructor) {
                p.pln(
                    javaMember.getName()
                        + "Temp = "
                        + encoder
                        + ".getInstance().stringToObject(attribute, reader);");
            } else {
                p.pln(
                    "member = "
                        + encoder
                        + ".getInstance().stringToObject(attribute, reader);");

                String javaName = javaMember.getType().getName();
                String valueStr;
                if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                    String boxName =
                        SimpleToBoxedUtil.getBoxedClassName(javaName);
                    valueStr =
                        SimpleToBoxedUtil.getUnboxedExpressionOfType(
                            "(" + boxName + ")member",
                            javaName);
                } else {
                    valueStr = "(" + javaName + ")member";
                }
                if (javaMember.isPublic()) {
                    p.pln(
                        "instance."
                            + javaMember.getName()
                            + " = "
                            + valueStr
                            + ";");
                } else {
                    if (javaMember.getDeclaringClass() != null) {
                        p.pln(
                            "(("
                                + javaMember.getDeclaringClass().replace(
                                    '$',
                                    '.')
                                + ")instance)."
                                + javaMember.getWriteMethod()
                                + "("
                                + valueStr
                                + ");");
                    } else {
                        p.pln(
                            "instance."
                                + javaMember.getWriteMethod()
                                + "("
                                + valueStr
                                + ");");
                    }
                }
            }

            p.pOln("}"); // if attribute not null
            if (member.isRequired()) {
                p.plnI("else {");
                p.pln(
                    "throw new DeserializationException(\"literal.missingRequiredAttribute\", new java.lang.Object[] {"
                        + memberQName
                        + "});");
                p.pOln("}");
            }
        }
    }

    private void writeVerifyNameOverrideMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        p.plnI(
            "protected void verifyName(XMLReader reader, javax.xml.namespace.QName expectedName) throws java.lang.Exception {");
        p.pOln("}"); // method
    }
}
