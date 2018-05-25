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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.LiteralSimpleSerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralContentMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralIDType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.literal.LiteralWildcardMember;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralObjectSerializerGenerator extends GeneratorBase {
    // vector of customized serializers/deserializers
    private Set visitedTypes;

    public LiteralObjectSerializerGenerator() {
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new LiteralObjectSerializerGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new LiteralObjectSerializerGenerator(model, config, properties);
    }

    private LiteralObjectSerializerGenerator(
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
            if (type.isLiteralType())
                 ((LiteralType) type).accept(this);
        }
        visitedTypes = null;
    }

    protected void visitFault(Fault fault) throws Exception {
        if (fault.getBlock().getType().isLiteralType()) {
            ((LiteralType) fault.getBlock().getType()).accept(this);
            JavaException exception = fault.getJavaException();
            Iterator members = exception.getMembers();
            AbstractType aType = (AbstractType) exception.getOwner();
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

    // literal type visits

    protected void preVisitLiteralSimpleType(LiteralSimpleType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    protected void preVisitLiteralSequenceType(LiteralSequenceType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
        Iterator attributes = type.getAttributeMembers();
        LiteralAttributeMember attribute;
        while (attributes.hasNext()) {
            attribute = (LiteralAttributeMember) attributes.next();
            attribute.getType().accept(this);
        }
        LiteralContentMember content = type.getContentMember();
        if (content != null) {
            content.getType().accept(this);
        }
        Iterator elements = type.getElementMembers();
        LiteralElementMember element;
        while (elements.hasNext()) {
            element = (LiteralElementMember) elements.next();
            element.getType().accept(this);
        }
        try {
            generateObjectSerializerForType(type);
        } catch (IOException e) {
            fail("generator.cant.write", type.getName().getLocalPart());
        }
    }

    protected void preVisitLiteralArrayWrapperType(LiteralArrayWrapperType type)
        throws Exception {
        preVisitLiteralSequenceType(type);
    }

    protected void preVisitLiteralAllType(LiteralAllType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
        Iterator attributes = type.getAttributeMembers();
        LiteralAttributeMember attribute;
        while (attributes.hasNext()) {
            attribute = (LiteralAttributeMember) attributes.next();
            attribute.getType().accept(this);
        }
        LiteralContentMember content = type.getContentMember();
        if (content != null) {
            content.getType().accept(this);
        }
        Iterator elements = type.getElementMembers();
        LiteralElementMember element;
        while (elements.hasNext()) {
            element = (LiteralElementMember) elements.next();
            element.getType().accept(this);
        }
        try {
            generateObjectSerializerForType(type);
        } catch (IOException e) {
            fail("generator.cant.write", type.getName().getLocalPart());
        }
    }

    protected void preVisitLiteralFragmentType(LiteralFragmentType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    private boolean haveVisited(AbstractType type) {
        return visitedTypes.contains(type);
    }

    private void typeVisited(AbstractType type) {
        visitedTypes.add(type);
    }

    private void generateObjectSerializerForType(LiteralStructuredType type)
        throws IOException {
        //xsd:ID/IDREF - set the ID/IDREF flag
        setIDAndIDREFFlags(type);
        writeObjectSerializerForType(type);
    }

    /**
     * Generate a class to to custom Serialization/Desirialization for
     * a particular type
     */
    private void writeObjectSerializerForType(LiteralStructuredType type)
        throws IOException {

        JavaType javaType = type.getJavaType();
        if (javaType == null) {
            fail(
                "generator.invalid.model.state.no.javatype",
                type.getName().getLocalPart());
        }
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

        try {

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_LITERAL_OBJECT_SERIALIZER);
            env.addGeneratedFile(fi);

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
        LiteralStructuredType type)
        throws IOException {

        log(
            "writing  serializer/deserializer for: "
                + type.getName().getLocalPart());
        String className =
            env.getNames().typeObjectSerializerClassName(servicePackage, type);
        if ((donotOverride && GeneratorUtil.classExists(env, className))) {
            log("Class " + className + " exists. Not overriding.");
            return;
        }
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
        if (enableIDTypeHandling) {
            writeGetIDMethod(p, type);
            p.pln();
        }
        writeDoDeserializeMethod(p, type);
        p.pln();
        writeDoSerializeAttributesMethod(p, type);
        writeDoSerializeMethod(p, type);
        if (enableIDREFTypeHandling) {
            writeIDObjectResolver(p, type);
        }
        p.pOln("}"); // end
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.xsd.XSDConstants;");
        p.pln("import com.sun.xml.rpc.encoding.literal.*;");
        p.pln(
            "import com.sun.xml.rpc.encoding.literal.DetailFragmentDeserializer;");
        p.pln("import com.sun.xml.rpc.encoding.simpletype.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAP12Constants;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.namespace.QName;");
        p.pln("import java.util.List;");
        p.pln("import java.util.ArrayList;");
    }

    private void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        if (enableIDTypeHandling) {
            p.plnI(
                "public class "
                    + Names.stripQualifier(className)
                    + " extends LiteralObjectSerializerBase implements Initializable, IDREFSerializerHelper {");
        } else {
            p.plnI(
                "public class "
                    + Names.stripQualifier(className)
                    + " extends LiteralObjectSerializerBase implements Initializable  {");
        }
    }

    private void writeMembers(IndentingWriter p, LiteralStructuredType type)
        throws IOException {
        Set processedTypes = new HashSet();

        for (Iterator iter = type.getAttributeMembers(); iter.hasNext();) {
            LiteralAttributeMember member =
                (LiteralAttributeMember) iter.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            GeneratorUtil.writeQNameDeclaration(
                p,
                member.getName(),
                env.getNames());
        }

        for (Iterator iter = type.getElementMembers(); iter.hasNext();) {
            LiteralElementMember member = (LiteralElementMember) iter.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            if (!member.isWildcard()) {
                GeneratorUtil.writeQNameDeclaration(
                    p,
                    member.getName(),
                    env.getNames());
            }
            LiteralEncoding.writeStaticSerializer(
                p,
                servicePackage,
                member.getType(),
                processedTypes,
                writerFactory,
                env.getNames());
        }

        if (enableIDREFTypeHandling) {
            p.pln("private InternalTypeMappingRegistry registry;");
        }
    }

    private void writeConstructor(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public "
                + Names.stripQualifier(className)
                + "(javax.xml.namespace.QName type, java.lang.String encodingStyle) {");
        p.pln("this(type, encodingStyle, false);");
        p.pOln("}");
        p.pln();
        p.plnI(
            "public "
                + Names.stripQualifier(className)
                + "(javax.xml.namespace.QName type, java.lang.String encodingStyle, boolean encodeType) {");
        p.pln("super(type, true, encodingStyle, encodeType);");
        p.pOln("}");
    }

    private void writeInitialize(IndentingWriter p, LiteralStructuredType type)
        throws IOException {
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        Set processedTypes = new HashSet();
        SerializerWriter writer;
        p.plnI(
            "public void initialize(InternalTypeMappingRegistry registry) throws Exception {");

        // do not initialize serializers for attributes for now
        // TODO - do for encoders something similar to what we do for serializers
        for (Iterator iter = type.getElementMembers(); iter.hasNext();) {
            LiteralElementMember member = (LiteralElementMember) iter.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            AbstractType memType = member.getType();
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

        if (enableIDREFTypeHandling) {
            p.pln("this.registry = registry;");
        }

        p.pOln("}");
    }

    private void writeDoDeserializeMethod(
        IndentingWriter p,
        LiteralStructuredType type)
        throws IOException {
        if (((JavaStructureType) type.getJavaType()).isAbstract()) {
            p.plnI("public java.lang.Object doDeserialize(XMLReader reader,");
            p.pln("SOAPDeserializationContext context) throws java.lang.Exception {");
            p.p(
                "throw new DeserializationException(\"soap.unsupportedType\", ");
            GeneratorUtil.writeNewQName(p, type.getName());
            p.pln(".toString());");
            p.pOln("}");
        } else if (SOAPObjectSerializerGenerator.deserializeToDetail(type)) {
            // Some exceptions need to be deserialized to a javax.xml.soap.Detail
            // element
            SOAPObjectSerializerGenerator.writeDetailDoDeserializeMethod(
                p,
                type);
        } else {
            writeStandardDoDeserializeMethod(p, type);
        }
    }

    private void writeStandardDoDeserializeMethod(
        IndentingWriter p,
        LiteralStructuredType type)
        throws IOException {
        JavaStructureType javaType = (JavaStructureType) type.getJavaType();
        JavaStructureMember[] constructorArgs =
            SOAPObjectBuilderGenerator.getConstructorArgs(javaType);
        boolean usesConstructor = constructorArgs.length > 0;

        p.plnI("public java.lang.Object doDeserialize(XMLReader reader,");
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
                        "Object "
                            + member.getName()
                            + "Temp = new "
                            + javaName
                            + "("
                            + initVal
                            + ");");
                } else {
                    p.pln("Object " + member.getName() + "Temp = null;");
                }
            }
        } else {
            p.pln(
                javaType.getName()
                    + " instance = new "
                    + javaType.getName()
                    + "();");
        }
        p.pln("java.lang.Object member=null;");
        p.pln("javax.xml.namespace.QName elementName;");
        p.pln("java.util.List values;"); // used for arrays
        p.pln("java.lang.Object value;"); // used for arrays
        p.pln();
        if (type.getAttributeMembersCount() > 0) {
            writeDeserializeAttributes(p, type, "reader", usesConstructor);
            p.pln();
        }

        if (type.getContentMember() != null) {
            p.pln("reader.nextContent();");
            writeDeserializeContent(p, type, "reader", usesConstructor);
        } else {
            p.pln("reader.nextElementContent();");
            if (type.getElementMembersCount() > 0) {
                writeDeserializeElements(p, type, "reader", usesConstructor);
                p.pln();
            }
        }

        if (usesConstructor) {
            String javaName;
            p.p("instance = new " + javaType.getName() + "(");
            String valueStr;
            JavaStructureMember javaMember;
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
        }

        p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.END);");
        p.pln("return (java.lang.Object)instance;");
        p.pOln("}");
    }

    private void writeDeserializeAttributes(
        IndentingWriter p,
        LiteralStructuredType type,
        String reader,
        boolean usesConstructor)
        throws IOException {

        p.pln("Attributes attributes = reader.getAttributes();");
        p.pln("java.lang.String attribute = null;");
        for (Iterator iterator = type.getAttributeMembers();
            iterator.hasNext();
            ) {
            LiteralAttributeMember member =
                (LiteralAttributeMember) iterator.next();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            String memberConstName =
                member.getName().getLocalPart().toUpperCase();
            String memberQName = env.getNames().getQNameName(member.getName());
            p.pln("attribute = attributes.getValue(" + memberQName + ");");
            p.plnI("if (attribute != null) {");

            String encoder;
            if (member.getType() instanceof LiteralEnumerationType) {
                encoder = member.getType().getJavaType().getName() + "_Encoder";

            } else {
                encoder =
                    LiteralSimpleSerializerWriter.getTypeEncoder(member.getType());
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
                //xsd:IDREF, its attribute and part of complexType,   bug 4845163 fix
                if (member
                    .getType()
                    .getName()
                    .equals(SchemaConstants.QNAME_TYPE_IDREF)
                    && enableIDREFTypeHandling) {
                    p.pln(
                        "PostDeserializationAction action = (PostDeserializationAction)new "
                            + env.getNames().getIDObjectResolverName(
                                member.getName().getLocalPart())
                            + "((java.lang.String)member, instance);");
                    p.pln("context.addPostDeserializationAction(action);");
                } else {
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
                    //xsd:ID
                    if (member
                        .getType()
                        .getName()
                        .equals(SchemaConstants.QNAME_TYPE_ID)
                        && enableIDTypeHandling) {
                        p.pln(
                            "context.addXSDIdObjectSerializer((java.lang.String)member, instance);");
                    }
                }
            }

            p.pOln("}"); // if attribute not null
            if (member.isRequired()) {
                p.plnI("else {");
                p.pln(
                    "throw new DeserializationException(\"literal.missingRequiredAttribute\", new Object[] {"
                        + memberQName
                        + "});");
                p.pOln("}");
            }
        }
    }

    private void writeDeserializeContent(
        IndentingWriter p,
        LiteralStructuredType type,
        String reader,
        boolean usesConstructor)
        throws IOException {

        LiteralContentMember member = type.getContentMember();
        JavaStructureMember javaMember = member.getJavaStructureMember();
        String encoder;
        if (member.getType() instanceof LiteralEnumerationType) {
            encoder = member.getType().getJavaType().getName() + "_Encoder";
        } else {
            encoder =
                LiteralSimpleSerializerWriter.getTypeEncoder(member.getType());
        }
        String javaName = javaMember.getType().getName();
        String valueStr;
        if (SimpleToBoxedUtil.isPrimitive(javaName)) {
            String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
            valueStr =
                SimpleToBoxedUtil.getUnboxedExpressionOfType(
                    "(" + boxName + ")member",
                    javaName);
        } else {
            valueStr = "(" + javaName + ")member";
        }

        p.plnI("if (reader.getState() == XMLReader.CHARS) {");
        if (usesConstructor) {
            p.pln(
                javaMember.getName()
                    + "Temp = "
                    + encoder
                    + ".getInstance().stringToObject(reader.getValue(), reader);");
        } else {
            p.pln(
                "member = "
                    + encoder
                    + ".getInstance().stringToObject(reader.getValue(), reader);");
        }

        p.pln("reader.nextContent();");
        p.pOln("}");

        p.plnI("else if (reader.getState() == XMLReader.END) {");
        p.pln(
            "member = "
                + encoder
                + ".getInstance().stringToObject(\"\", reader);");
        p.pOln("}");

        p.plnI("else if (reader.getState() == XMLReader.START) {");
        p.pln(
            "throw new DeserializationException(\"literal.simpleContentExpected\", new Object[] {reader.getName()});");

        p.pOln("}");
        //xsd:IDREF
        if (member.getType().getName().equals(SchemaConstants.QNAME_TYPE_IDREF)
            && enableIDREFTypeHandling) {
            p.pln(
                "PostDeserializationAction action = (PostDeserializationAction)new "
                    + env.getNames().getIDObjectResolverName(
                        type.getName().getLocalPart())
                    + "((java.lang.String)member, instance);");
            p.pln("context.addPostDeserializationAction(action);");
        } else {
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
    }

    private void writeDeserializeElements(
        IndentingWriter p,
        LiteralStructuredType type,
        String reader,
        boolean usesConstructor)
        throws IOException {

        if (type instanceof LiteralSequenceType) {
            Iterator iterator = type.getElementMembers();
            while (iterator.hasNext()) {
                LiteralElementMember elementMember =
                    (LiteralElementMember) iterator.next();
                p.pln("elementName = " + reader + ".getName();");
                if (elementMember.isRepeated()) {
                    writeArrayElementMemberDeserializer(
                        p,
                        type,
                        elementMember,
                        reader,
                        false,
                        usesConstructor);
                } else {
                    writeScalarElementMemberDeserializer(
                        p,
                        type,
                        elementMember,
                        reader,
                        false,
                        usesConstructor);
                }
            }
        } else {
            // an "all" type
            if (type.getElementMembersCount() > 0) {
                p.plnI("while (reader.getState() == XMLReader.START) {");
                p.pln("elementName = " + reader + ".getName();");
                Iterator iterator = type.getElementMembers();
                boolean gotOne = false;
                while (iterator.hasNext()) {
                    if (gotOne) {
                        // here we assume that both methods invoked below will generate
                        // a statement of the form "if (xyz) { }", so that we can chain them
                        // together with "else" keywords
                        p.p("else ");
                    }
                    LiteralElementMember elementMember =
                        (LiteralElementMember) iterator.next();
                    if (elementMember.isRepeated()) {
                        writeArrayElementMemberDeserializer(
                            p,
                            type,
                            elementMember,
                            reader,
                            true,
                            usesConstructor);
                    } else {
                        writeScalarElementMemberDeserializer(
                            p,
                            type,
                            elementMember,
                            reader,
                            true,
                            usesConstructor);
                    }
                    gotOne = true;
                }
                p.plnI("else {");
                p.pln(
                    "throw new DeserializationException(\"literal.unexpectedElementName\", new Object[] { elementName, "
                        + reader
                        + ".getName()"
                        + "});");
                p.pOln("}");

                p.pOln("}"); // while statement
            }
        }
    }

    private void writeScalarElementMemberDeserializer(
        IndentingWriter p,
        LiteralStructuredType type,
        LiteralElementMember member,
        String reader,
        boolean isAllType,
        boolean usesConstructor)
        throws IOException {

        String serializer;
        JavaStructureMember javaMember = member.getJavaStructureMember();
        String memberQName =
            member.getName() == null
                ? null
                : env.getNames().getQNameName(member.getName());

        if (!isAllType) {
            p.plnI("if (" + reader + ".getState() == XMLReader.START) {");
        }

        if (member.isWildcard()) {
            LiteralWildcardMember wildcard = (LiteralWildcardMember) member;
            if (wildcard.getExcludedNamespaceName() == null) {
                p.plnI("if (true) {");
            } else {
                p.plnI(
                    "if (!elementName.getNamespaceURI().equals(\""
                        + wildcard.getExcludedNamespaceName()
                        + "\")) {");
            }
        } else {
            p.plnI("if (elementName.equals(" + memberQName + ")) {");
        }
        SerializerWriter writer =
            writerFactory.createWriter(servicePackage, member.getType());
        serializer = writer.deserializerMemberName();
        String memberName =
            usesConstructor ? javaMember.getName() + "Temp" : "member";
        p.pln(
            memberName
                + " = "
                + serializer
                + ".deserialize("
                + memberQName
                + ", "
                + reader
                + ", context);");

        if (!member.isNillable()) {
            p.plnI("if (" + memberName + " == null) {");
            p.pln(
                "throw new DeserializationException(\"literal.unexpectedNull\");");
            p.pOln("}");
        }
        //xsd:IDREF, its part of complexType, bug 4845163 fix
        if (member.getType().getName().equals(SchemaConstants.QNAME_TYPE_IDREF)
            && enableIDREFTypeHandling) {
            if (member.isNillable()) {
                p.plnI("if (" + memberName + " != null) {");
            }
            p.pln(
                "PostDeserializationAction action = (PostDeserializationAction)new "
                    + env.getNames().getIDObjectResolverName(
                        member.getName().getLocalPart())
                    + "((java.lang.String)member, instance);");
            p.pln("context.addPostDeserializationAction(action);");
            if (member.isNillable()) {
                p.pOln("}");
            }
        } else {
            String valueStr = null;
            String javaName = javaMember.getType().getName();

            if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
                valueStr =
                    SimpleToBoxedUtil.getUnboxedExpressionOfType(
                        "(" + boxName + ")" + memberName,
                        javaName);
            } else {
                valueStr = "(" + javaName + ")" + memberName;
            }

            if (!usesConstructor) {
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
            //xsd:ID
            if (member
                .getType()
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_ID)
                && enableIDTypeHandling) {
                p.pln(
                    "context.addXSDIdObjectSerializer((java.lang.String)member, instance);");
            }
        }
        p.pln(reader + ".nextElementContent();");
        p.pO("}"); // elemName match
        if (!isAllType && member.isRequired()) {
            p.plnI(" else {");
            p.pln(
                "throw new DeserializationException(\"literal.unexpectedElementName\", new Object[] { "
                    + memberQName
                    + ", "
                    + reader
                    + ".getName()"
                    + " });");
            p.pOln("}");
        } else {
            p.pln();
        }

        if (!isAllType) {
            p.pOln("}"); // XMLReader.START check
            if (member.isRequired()) {
                p.plnI("else {");
                p.pln(
                    "throw new DeserializationException(\"literal.expectedElementName\", "
                        + reader
                        + ".getName().toString());");
                p.pOln("}");
            }
        }
    }

    private void writeArrayElementMemberDeserializer(
        IndentingWriter p,
        LiteralStructuredType type,
        LiteralElementMember member,
        String reader,
        boolean isAllType,
        boolean usesConstructor)
        throws IOException {

        String serializer;
        JavaStructureMember javaMember = member.getJavaStructureMember();
        String memberQName =
            member.getName() == null
                ? null
                : env.getNames().getQNameName(member.getName());

        if (member.isWildcard()) {
            LiteralWildcardMember wildcard = (LiteralWildcardMember) member;
            if (wildcard.getExcludedNamespaceName() == null) {
                p.plnI("if (" + reader + ".getState() == XMLReader.START) {");
                p.pln("values = new ArrayList();");
                p.plnI("for(;;) {");
                p.plnI("if (" + reader + ".getState() == XMLReader.START) {");
            } else {
                p.plnI(
                    "if (("
                        + reader
                        + ".getState() == XMLReader.START) && (!elementName.getNamespaceURI().equals(\""
                        + wildcard.getExcludedNamespaceName()
                        + "\"))) {");
                p.pln("values = new ArrayList();");
                p.plnI("for(;;) {");
                p.pln("elementName = " + reader + ".getName();");
                p.plnI(
                    "if (("
                        + reader
                        + ".getState() == XMLReader.START) && (!elementName.getNamespaceURI().equals(\""
                        + wildcard.getExcludedNamespaceName()
                        + "\"))) {");
            }
        } else {
            p.plnI(
                "if (("
                    + reader
                    + ".getState() == XMLReader.START) && (elementName.equals("
                    + memberQName
                    + "))) {");
            p.pln("values = new ArrayList();");
            p.plnI("for(;;) {");

            p.pln("elementName = " + reader + ".getName();");
            p.plnI(
                "if (("
                    + reader
                    + ".getState() == XMLReader.START) && (elementName.equals("
                    + memberQName
                    + "))) {");
        }

        SerializerWriter writer =
            writerFactory.createWriter(servicePackage, member.getType());
        serializer = writer.deserializerMemberName();
        p.pln(
            "value = "
                + serializer
                + ".deserialize("
                + memberQName
                + ", "
                + reader
                + ", context);");
        if (!member.isNillable()) {
            p.plnI("if (value == null) {");
            p.pln(
                "throw new DeserializationException(\"literal.unexpectedNull\");");
            p.pOln("}");
        }

        p.pln("values.add(value);");

        String valueStr = null;
        String javaName = member.getType().getJavaType().getName();
        boolean javaTypeIsArray = javaName.endsWith(BRACKETS);
        p.pln(reader + ".nextElementContent();");
        p.pO("}"); // elemName match
        p.plnI(" else {");
        p.pln("break;");
        p.pOln("}");

        p.pOln("}"); // for loop
        String memberName =
            usesConstructor ? javaMember.getName() + "Temp" : "member";
        if (javaTypeIsArray) {
            int idx = javaName.indexOf(BRACKETS);
            p.pln(
                memberName
                    + " = new "
                    + javaName.substring(0, idx)
                    + "[values.size()]"
                    + javaName.substring(idx)
                    + ";");
        } else {
            p.pln(memberName + " = new " + javaName + "[values.size()];");
        }

        if (SimpleToBoxedUtil.isPrimitive(javaName)) {
            String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
            p.plnI("for (int i = 0; i < values.size(); ++i) {");
            p.pln(
                "(("
                    + javaName
                    + "[]) "
                    + memberName
                    + ")[i] = "
                    + SimpleToBoxedUtil.getUnboxedExpressionOfType(
                        "(" + boxName + ")(values.get(i))",
                        javaName)
                    + ";");
            p.pOln("}");
        } else {
            p.pln(
                memberName
                    + " = values.toArray((Object[]) "
                    + memberName
                    + ");");
        }

        if (!usesConstructor) {
            // unwrap the array
            String structArrayName =
                ((JavaStructureMember) ((JavaStructureType) type.getJavaType())
                    .getMembers()
                    .next())
                    .getType()
                    .getName();
            structArrayName =
                structArrayName.substring(0, structArrayName.length() - 2);

            if (member.getType() instanceof LiteralArrayWrapperType
                && !javaName.equals(structArrayName)) {
                LiteralArrayWrapperType arrayType =
                    (LiteralArrayWrapperType) member.getType();
                p.pln("// LiteralArrayWrapper");
                String javaTypeName =
                    arrayType.getJavaArrayType().getName() + BRACKETS;
                int idx = javaTypeName.indexOf(BRACKETS) + 1;
                String tmp = javaTypeName.substring(0, idx);
                p.pln(
                    javaTypeName
                        + " tmpArray = new "
                        + tmp
                        + "values.size()"
                        + javaTypeName.substring(idx)
                        + ";");
                javaTypeName = arrayType.getJavaArrayType().getName();
                idx = javaTypeName.indexOf(BRACKETS) + 1;
                tmp = javaTypeName.substring(0, idx);
                p.plnI("for (int i=0;i<tmpArray.length;i++) {");
                p.pln(
                    javaTypeName
                        + " inner = (("
                        + javaName
                        + ")((java.lang.Object[])member)[i]).toArray();");
                p.pln(
                    "tmpArray[i] = new "
                        + tmp
                        + "inner.length"
                        + javaTypeName.substring(idx)
                        + ";");
                p.plnI("for (int j=0; j<tmpArray[i].length;j++) {");
                p.pln("tmpArray[i][j] = inner[j];");
                p.pOln("}");
                p.pOln("}");
                valueStr = "tmpArray";
            } else {
                valueStr = "(" + javaName + "[])" + memberName;
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

        p.pOln("}"); // XMLReader.START check

        if (member.isRequired()) {
            // for empty element <foo/> the state can be XMLReader.END, fix for 4811595
            p.plnI("else if(!(reader.getState() == XMLReader.END)) {");
            //            p.plnI("else {");	  
            p.pln(
                "throw new DeserializationException(\"literal.expectedElementName\", "
                    + reader
                    + ".getName().toString());");
            p.pOln("}");
        } else {
            p.plnI("else {");
            if (!usesConstructor) {
                if (convertArrayWrapper(member, type)) {
                    LiteralArrayWrapperType arrayType =
                        (LiteralArrayWrapperType) member.getType();
                    String javaTypeName =
                        arrayType.getJavaArrayType().getName() + BRACKETS;
                    int idx = javaTypeName.indexOf(BRACKETS) + 1;
                    String tmp = javaTypeName.substring(0, idx);
                    javaName = tmp + "0" + javaTypeName.substring(idx);
                } else {
                    if (javaName.equals(BYTE_ARRAY_CLASSNAME)
                        || javaName.equals(BOXED_BYTE_ARRAY_CLASSNAME)) {
                        javaName =
                            javaName.substring(0, javaName.length() - 1)
                                + "0][0]";
                    } else {
                        javaName = javaName + "[0]";
                    }
                }
                if (javaMember.isPublic()) {
                    p.pln(
                        "instance."
                            + javaMember.getName()
                            + " = new "
                            + javaName
                            + ";");
                } else {
                    p.pln(
                        "instance."
                            + javaMember.getWriteMethod()
                            + "(new "
                            + javaName
                            + ");");
                }
            }
            p.pOln("}");
        }
    }

    private void writeDoSerializeAttributesMethod(
        IndentingWriter p,
        LiteralStructuredType type)
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
            LiteralAttributeMember member =
                (LiteralAttributeMember) iterator.next();
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
            if (member.getType() instanceof LiteralEnumerationType) {
                encoder = member.getType().getJavaType().getName() + "_Encoder";

            } else {
                encoder =
                    LiteralSimpleSerializerWriter.getTypeEncoder(member.getType());
            }
            if (javaMember.isPublic()) {
                valueStr = "instance." + javaMember.getName();
            } else {
                String methName = javaMember.getReadMethod();
                valueStr = "instance." + methName + "()";
            }

            // xsd:IDREF, part of complexType, bug 4845163 fix
            if (member
                .getType()
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_IDREF)
                && enableIDREFTypeHandling) {
                // Bug 4845389 fix
                p.plnI("if (" + valueStr + " != null) {");
                p.pln("java.lang.Object idObj = " + valueStr + ";");
                p.pln(
                    "CombinedSerializer idSerializer = "
                        + "(CombinedSerializer)registry.getSerializer(\"\", idObj.getClass());");
                p.plnI(
                    "if((idSerializer !=null) && "
                        + "(idSerializer instanceof IDREFSerializerHelper)) {");
                p.pln(
                    "IDREFSerializerHelper idrefSerializer = (IDREFSerializerHelper)idSerializer;");
                p.pln(
                    "writer.writeAttribute("
                        + memberQName
                        + ", "
                        + encoder
                        + ".getInstance().objectToString("
                        + "idrefSerializer.getID(idObj)"
                        + ", writer));");
                p.pOln("}");
                // fix for bug: 4847576
                p.plnI("else {");
                p.pln(
                    "throw new DeserializationException(\"literal.notIdentifiableObject\", new Object[] {idObj.getClass()});");
                p.pOln("}");
                p.pOln("}");
                if (member.isRequired()) {
                    p.plnI("else {");
                    p.pln(
                        "throw new DeserializationException(\"literal.requiredAttributeConstraint\", new Object[] {"
                            + memberQName
                            + "});");
                    p.pOln("}");
                }
            } else {
                // TODO this should be removed if the new SAAJ handles the 
                // mustUnderstand header properly.
                // lets not use useWSIBasicProfile. We can get literal mode from the type
                if (!member.getType().isSOAPType()
                    && isMustUnderstandHeader(member)) {
                    // bug fix: 4968046
                    if (!SimpleToBoxedUtil.isPrimitive(javaName)) {
                        valueStr = valueStr+".booleanValue()";
                    }      
                    p.plnI("if (" + valueStr + ")");
                    p.pln("writer.writeAttribute(" + memberQName + ", \"1\");");
                    p.pOlnI("else");
                    p.pln("writer.writeAttribute(" + memberQName + ", \"0\");");
                    p.pO();
                } else {
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
                            "throw new DeserializationException(\"literal.requiredAttributeConstraint\", new Object[] {"
                                + memberQName
                                + "});");
                        p.pOln("}");
                    }
                }
            }
        }

        p.pOln("}"); // method
    }

    // TODO this should be removed if SAAJ properly formats the header value.
    private boolean isMustUnderstandHeader(LiteralAttributeMember member) {

        QName typeName = member.getType().getName();
        if (member.getName().equals(SOAPConstants.QNAME_MUSTUNDERSTAND)
            && (typeName.equals(SOAPConstants.QNAME_TYPE_BOOLEAN)
                || typeName.equals(SchemaConstants.QNAME_TYPE_BOOLEAN))) {
            return true;
        }
        return false;
    }

    public static boolean convertArrayWrapper(
        LiteralElementMember member,
        LiteralStructuredType type) {
        String structArrayName =
            ((JavaStructureMember) ((JavaStructureType) type.getJavaType())
                .getMembers()
                .next())
                .getType()
                .getName();
        String javaName = member.getType().getJavaType().getName() + BRACKETS;
        return (
            member.getType() instanceof LiteralArrayWrapperType
                && !javaName.equals(structArrayName));

    }

    private void writeDoSerializeMethod(
        IndentingWriter p,
        LiteralStructuredType type)
        throws IOException {

        p.plnI(
            "public void doSerialize(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {");
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

        if (type.getContentMember() != null) {
            Iterator iterator = type.getElementMembers();
            if (iterator.hasNext()) {
                // both simple and element content -- must be mixed,
                // but we don't support those right now
                fail(
                    "generator.unsupported.type.encountered",
                    type.getName().getLocalPart(),
                    type.getName().getNamespaceURI());
            }
            LiteralContentMember member = type.getContentMember();
            JavaStructureMember javaMember = member.getJavaStructureMember();
            writer =
                writerFactory.createWriter(servicePackage, member.getType());
            serializer = writer.serializerMemberName();
            String valueStr = null;
            String javaName = javaMember.getType().getName();

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

            // instead of calling a serializer, we call an encoder
            String encoder;
            if (member.getType() instanceof LiteralEnumerationType) {
                encoder = member.getType().getJavaType().getName() + "_Encoder";
            } else {
                encoder =
                    LiteralSimpleSerializerWriter.getTypeEncoder(member.getType());
            }

            //xsd:IDREF
            if (member
                .getType()
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_IDREF)
                && enableIDREFTypeHandling) {
                //Bug 4845389 fix
                p.plnI("if (" + valueStr + " == null) {");
                p.pln(
                    "writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_NIL, \"1\");");
                //fix for bug: 4847576
                p.plnI("}else {");
                p.pln("java.lang.Object idObj = " + valueStr + ";");
                p.pln(
                    "CombinedSerializer idSerializer = (CombinedSerializer)registry.getSerializer(\"\", idObj.getClass());");
                p.plnI(
                    "if((idSerializer !=null) && (idSerializer instanceof IDREFSerializerHelper)) {");
                p.pln(
                    "IDREFSerializerHelper idrefSerializer = (IDREFSerializerHelper)idSerializer;");
                p.pln(
                    "writer.writeChars("
                        + encoder
                        + ".getInstance().objectToString(idrefSerializer.getID(idObj), writer));");
                p.pOln("}");
                //fix for bug: 4847576
                p.plnI("else {");
                p.pln(
                    "throw new DeserializationException(\"literal.notIdentifiableObject\", new Object[] {idObj.getClass()});");
                p.pOln("}");
                p.pOln("}");
            } else
                //bug fix: 4862786
                p.plnI("if (" + valueStr + " == null) {");
            p.pln(
                "writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_NIL, \"1\");");
            p.pOln("}");
            p.plnI("else {");
            p.pln(
                "writer.writeChars("
                    + encoder
                    + ".getInstance().objectToString("
                    + valueStr
                    + ", writer));");
            p.pOln("}");
        } else {
            for (Iterator iterator = type.getElementMembers();
                iterator.hasNext();
                ) {
                LiteralElementMember member =
                    (LiteralElementMember) iterator.next();
                JavaStructureMember javaMember =
                    member.getJavaStructureMember();
                memberQName =
                    member.getName() == null
                        ? null
                        : env.getNames().getQNameName(member.getName());
                writer =
                    writerFactory.createWriter(
                        servicePackage,
                        member.getType());
                serializer = writer.serializerMemberName();
                String valueStr = null;
                String javaName = javaMember.getType().getName();

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
                // bug fix: 4860484
                if (!member.isNillable()
                    && member.isRequired()
                    && !member.isRepeated()) {
                    p.plnI("if (" + valueStr + " == null) {");
                    p.pln(
                        "throw new SerializationException(\"literal.unexpectedNull\");");
                    p.pOln("}");
                }

                if (member.isRepeated()) {
                    String javaElementName =
                        member.getType().getJavaType().getName();
                    p.plnI("if (" + valueStr + " != null) {");
                    p.plnI(
                        "for (int i = 0; i < " + valueStr + ".length; ++i) {");
                    if (SimpleToBoxedUtil.isPrimitive(javaElementName)) {
                        p.pln(
                            serializer
                                + ".serialize("
                                + SimpleToBoxedUtil.getBoxedExpressionOfType(
                                    valueStr + "[i]",
                                    javaElementName)
                                + ", "
                                + memberQName
                                + ", null, writer, context);");
                    } else {
                        // wrap the array if necessary
                        if (convertArrayWrapper(member, type)) {
                            p.pln(
                                serializer
                                    + ".serialize(new "
                                    + javaElementName
                                    + "("
                                    + valueStr
                                    + "[i]), "
                                    + memberQName
                                    + ", null, writer, context);");
                        } else {
                            p.pln(
                                serializer
                                    + ".serialize("
                                    + valueStr
                                    + "[i], "
                                    + memberQName
                                    + ", null, writer, context);");
                        }
                    }
                    p.pOln("}");
                    p.pOln("}");
                } else {
                    boolean skipIfNull =
                        !member.isRequired()
                            && !member.isNillable()
                            && !SimpleToBoxedUtil.isPrimitive(javaName);

                    if (skipIfNull) {
                        p.plnI("if (" + valueStr + " != null) {");
                    }
                    // xsd:IDREF, part of complexType, bug 4845163 fix
                    if (member
                        .getType()
                        .getName()
                        .equals(SchemaConstants.QNAME_TYPE_IDREF)
                        && enableIDREFTypeHandling) {
                        // Bug 4845389 fix
                        p.plnI("if (" + valueStr + " == null) {");
                        p.pln(
                            serializer
                                + ".serialize(null, "
                                + memberQName
                                + ",null, writer, context);");
                        //fix for bug: 4847576
                        p.plnI("}else {");
                        p.pln("java.lang.Object idObj = " + valueStr + ";");
                        p.pln(
                            "CombinedSerializer idSerializer = (CombinedSerializer)registry.getSerializer(\"\", idObj.getClass());");
                        p.plnI(
                            "if((idSerializer !=null) && (idSerializer instanceof IDREFSerializerHelper)) {");
                        p.pln(
                            "IDREFSerializerHelper idrefSerializer = (IDREFSerializerHelper)idSerializer;");
                        p.pln(
                            serializer
                                + ".serialize(idrefSerializer.getID(idObj), "
                                + memberQName
                                + ",null, writer, context);");
                        p.pOln("}");
                        // fix for bug: 4847576
                        p.plnI("else {");
                        p.pln(
                            "throw new DeserializationException(\"literal.notIdentifiableObject\", new java.lang.Object[] {idObj.getClass()});");
                        p.pOln("}");
                        p.pOln("}");
                    } else {
                        p.pln(
                            serializer
                                + ".serialize("
                                + valueStr
                                + ", "
                                + memberQName
                                + ", null, writer, context);");
                    }
                    if (skipIfNull) {
                        p.pOln("}");
                    }
                }
            }
        }
        p.pOln("}"); // method
    }

    /**
     * xsd:ID/IDREF feature. This method generates code to get the ID string from xsd:ID type attribute.
     *
     */
    private void writeGetIDMethod(
        IndentingWriter p,
        LiteralStructuredType type)
        throws IOException {
        if (!enableIDTypeHandling)
            return;
        boolean done = false;
        String idMember = null;
        // only one attribute or element can be xsd:ID type
        for (Iterator iterator = type.getAttributeMembers();
            iterator.hasNext() && !done;
            ) {
            LiteralAttributeMember member =
                (LiteralAttributeMember) iterator.next();
            //xsd:ID
            if (member
                .getType()
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_ID)) {
                JavaStructureMember javaMember =
                    member.getJavaStructureMember();
                if (javaMember.isPublic()) {
                    idMember = javaMember.getName();
                } else {
                    idMember = javaMember.getReadMethod() + "()";
                }
                done = true;
            }
        }

        Iterator elements = type.getElementMembers();
        while (elements.hasNext() && !done) {
            LiteralElementMember member =
                (LiteralElementMember) elements.next();
            if (member
                .getType()
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_ID)) {
                JavaStructureMember javaMember =
                    member.getJavaStructureMember();
                if (javaMember.isPublic()) {
                    idMember = javaMember.getName();
                } else {
                    idMember = javaMember.getReadMethod() + "()";
                }
                done = true;
            }
        }

        if (done) {
            JavaStructureType javaType = (JavaStructureType) type.getJavaType();
            p.plnI(" public java.lang.String getID(java.lang.Object obj) {");
            p.pln("return ((" + javaType.getName() + ")obj)." + idMember + ";");
            p.pOln("}");
        }
    }

    /**
     * xsd:ID/IDREF feature. This method declares an inner class which implements 
     * PostDeserializationAction. Its added when deserializing IDREF element to the context 
     * and run at the end of deserialization.
     */
    private void writeIDObjectResolver(
        IndentingWriter p,
        LiteralStructuredType type)
        throws IOException {
        if (!enableIDREFTypeHandling)
            return;
        LiteralContentMember content = type.getContentMember();
        if ((content != null)
            && content.getType().getName().equals(
                SchemaConstants.QNAME_TYPE_IDREF)) {
            String className =
                env.getNames().getIDObjectResolverName(
                    type.getName().getLocalPart());
            p.plnI(
                "private static class "
                    + Names.stripQualifier(className)
                    + " implements PostDeserializationAction {");
            p.pln("private final java.lang.String value;");
            p.pln(
                "private final "
                    + type.getJavaType().getName()
                    + " "
                    + env.getNames().validJavaMemberName(
                        type.getName().getLocalPart())
                    + ";");
            //write constructor
            p.plnI(
                className
                    + "(java.lang.String value, "
                    + type.getJavaType().getName()
                    + " idObj) {");
            p.pln("this.value = value;");
            p.pln(
                "this."
                    + env.getNames().validJavaMemberName(
                        type.getName().getLocalPart())
                    + " = idObj;");
            p.pOln("}");

            //write run()
            p.plnI(
                "public void run(SOAPDeserializationContext deserContext) {");
            String idObjInstance =
                env.getNames().validJavaMemberName(
                    type.getName().getLocalPart());
            JavaStructureMember javaMember = content.getJavaStructureMember();
            String valueStr =
                new String("(java.lang.Object) deserContext.getXSDIdObjectSerializer(value)");
            if (javaMember.isPublic()) {
                p.pln(
                    idObjInstance
                        + "."
                        + javaMember.getName()
                        + " = "
                        + valueStr
                        + ";");
            } else {
                p.pln(
                    idObjInstance
                        + "."
                        + javaMember.getWriteMethod()
                        + "("
                        + valueStr
                        + ");");
            }
            p.pOln("}");
            p.pOln("}");
        }

        Iterator elements = type.getElementMembers();
        while (elements.hasNext()) {
            LiteralElementMember element =
                (LiteralElementMember) elements.next();
            if (element
                .getType()
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_IDREF)) {
                String className =
                    env.getNames().getIDObjectResolverName(
                        element.getName().getLocalPart());
                p.plnI(
                    "private static class "
                        + Names.stripQualifier(className)
                        + " implements PostDeserializationAction {");
                p.pln("private final java.lang.String value;");
                p.pln(
                    "private final "
                        + type.getJavaType().getName()
                        + " "
                        + env.getNames().validJavaMemberName(
                            type.getName().getLocalPart())
                        + ";");
                //write constructor
                p.plnI(
                    className
                        + "(String value, "
                        + type.getJavaType().getName()
                        + " idObj) {");
                p.pln("this.value = value;");
                p.pln(
                    "this."
                        + env.getNames().validJavaMemberName(
                            type.getName().getLocalPart())
                        + " = idObj;");
                p.pOln("}");

                //write run()
                p.plnI(
                    "public void run(SOAPDeserializationContext deserContext) {");
                String idObjInstance =
                    env.getNames().validJavaMemberName(
                        type.getName().getLocalPart());
                JavaStructureMember javaMember =
                    element.getJavaStructureMember();
                String valueStr =
                    new String("(java.lang.Object) deserContext.getXSDIdObjectSerializer(value)");
                if (javaMember.isPublic()) {
                    p.pln(
                        idObjInstance
                            + "."
                            + javaMember.getName()
                            + " = "
                            + valueStr
                            + ";");
                } else {
                    p.pln(
                        idObjInstance
                            + "."
                            + javaMember.getWriteMethod()
                            + "("
                            + valueStr
                            + ");");
                }
                p.pOln("}");
                p.pOln("}");
            }
        }

        Iterator attributes = type.getAttributeMembers();
        while (attributes.hasNext()) {
            LiteralAttributeMember member =
                (LiteralAttributeMember) attributes.next();
            if (member
                .getType()
                .getName()
                .equals(SchemaConstants.QNAME_TYPE_IDREF)) {
                String className =
                    env.getNames().getIDObjectResolverName(
                        member.getName().getLocalPart());
                p.plnI(
                    "private static class "
                        + Names.stripQualifier(className)
                        + " implements PostDeserializationAction {");
                p.pln("private final java.lang.String value;");
                p.pln(
                    "private final "
                        + type.getJavaType().getName()
                        + " "
                        + env.getNames().validJavaMemberName(
                            type.getName().getLocalPart())
                        + ";");
                //write constructor
                p.plnI(
                    className
                        + "(java.lang.String value, "
                        + type.getJavaType().getName()
                        + " idObj) {");
                p.pln("this.value = value;");
                p.pln(
                    "this."
                        + env.getNames().validJavaMemberName(
                            type.getName().getLocalPart())
                        + " = idObj;");
                p.pOln("}");

                //write run()
                p.plnI(
                    "public void run(SOAPDeserializationContext deserContext) {");
                String idObjInstance =
                    env.getNames().validJavaMemberName(
                        type.getName().getLocalPart());
                JavaStructureMember javaMember =
                    member.getJavaStructureMember();
                String valueStr =
                    new String("(java.lang.Object) deserContext.getXSDIdObjectSerializer(value)");
                if (javaMember.isPublic()) {
                    p.pln(
                        idObjInstance
                            + "."
                            + javaMember.getName()
                            + " = "
                            + valueStr
                            + ";");
                } else {
                    p.pln(
                        idObjInstance
                            + "."
                            + javaMember.getWriteMethod()
                            + "("
                            + valueStr
                            + ");");
                }
                p.pOln("}");
                p.pOln("}");
            }
        }
    }

    /**
     * Set the ID/IDREF handling flags. These are set to "true" if there is attribute 
     * or element of QNAME ID or IDREF and this feature is enabled from command line.
     */
    private void setIDAndIDREFFlags(LiteralStructuredType type) {
        Iterator attributes = type.getAttributeMembers();
        LiteralAttributeMember attribute;
        //reset the flags
        enableIDTypeHandling = false;
        enableIDREFTypeHandling = false;
        while (attributes.hasNext()) {
            attribute = (LiteralAttributeMember) attributes.next();
            //xsd:ID
            LiteralType literalType = attribute.getType();
            if (literalType.getName().equals(SchemaConstants.QNAME_TYPE_ID)
                && (literalType instanceof LiteralIDType)) {
                if (((LiteralIDType) literalType).getResolveIDREF())
                    enableIDTypeHandling = true;
            }
            if (literalType.getName().equals(SchemaConstants.QNAME_TYPE_IDREF)
                && (literalType instanceof LiteralIDType)) {
                if (((LiteralIDType) literalType).getResolveIDREF())
                    enableIDREFTypeHandling = true;
            }
        }

        //xsd:IDREF element is mapped to a valueType which has Object as Conetent.
        LiteralContentMember content = type.getContentMember();
        if (content != null) {
            LiteralType literalType = content.getType();
            if (literalType.getName().equals(SchemaConstants.QNAME_TYPE_IDREF)
                && (literalType instanceof LiteralIDType)) {
                if (((LiteralIDType) literalType).getResolveIDREF())
                    enableIDREFTypeHandling = true;
            }
        }

        //xsd:IDREF, part of complexType, bug 4845163 fix	
        Iterator elements = type.getElementMembers();
        LiteralElementMember element;
        while (elements.hasNext()) {
            element = (LiteralElementMember) elements.next();
            LiteralType literalType = element.getType();
            //xsd:IDREF
            if (literalType.getName().equals(SchemaConstants.QNAME_TYPE_IDREF)
                && (literalType instanceof LiteralIDType)) {
                //bug fix 4854669
                if (((LiteralIDType) literalType).getResolveIDREF())
                    enableIDREFTypeHandling = true;
            }
            //xsd:ID
            if (literalType.getName().equals(SchemaConstants.QNAME_TYPE_ID)
                && (literalType instanceof LiteralIDType)) {
                if (((LiteralIDType) literalType).getResolveIDREF())
                    enableIDTypeHandling = true;
            }
        }
    }

    private boolean enableIDTypeHandling = false;
    private boolean enableIDREFTypeHandling = false;

}
