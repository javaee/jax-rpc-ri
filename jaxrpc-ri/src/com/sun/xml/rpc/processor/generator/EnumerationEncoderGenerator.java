/*
 * $Id: EnumerationEncoderGenerator.java,v 1.1 2006-04-12 20:33:44 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
import com.sun.xml.rpc.processor.generator.writer.SimpleTypeSerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationType;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPListType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class EnumerationEncoderGenerator extends GeneratorBase {
    private Set types;

    public EnumerationEncoderGenerator() {
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new EnumerationEncoderGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new EnumerationEncoderGenerator(model, config, properties);
    }

    private EnumerationEncoderGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        types = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        types = null;
    }

    protected void visitParameter(Parameter param) throws Exception {
        AbstractType type = param.getType();
        if (type.isSOAPType()) {
            ((SOAPType) type).accept(this);
        } else {
            ((LiteralType) type).accept(this);
        }
    }

    protected void preVisitResponse(Response response) throws Exception {
        Iterator iter = response.getParameters();
        AbstractType type;
        while (iter.hasNext()) {
            ((Parameter) iter.next()).accept(this);
        }
    }

    protected void preVisitRequest(Request request) throws Exception {
        Iterator iter = request.getParameters();
        AbstractType type;
        while (iter.hasNext()) {
            ((Parameter) iter.next()).accept(this);
        }
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
    public void visit(SOAPCustomType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
    }

    public void visit(SOAPAnyType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        generateEnumerationSerializer(type);
    }

    protected void visitSOAPArrayType(SOAPArrayType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        super.visitSOAPArrayType(type);
    }

    protected void visitSOAPStructureType(SOAPStructureType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        super.visitSOAPStructureType(type);
    }

    public void visit(LiteralEnumerationType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        generateEnumerationSerializer(type);
    }

    //xsd:list
    public void visit(LiteralListType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        type.getItemType().accept(this);
        registerType(type);
    }

    //xsd:list, rpc/enc
    public void visit(SOAPListType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        type.getItemType().accept(this);
        registerType(type);
    }

    private boolean isRegistered(AbstractType type) {
        return types.contains(type);
    }

    private void registerType(AbstractType type) {
        types.add(type);
    }

    private void generateEnumerationSerializer(SOAPEnumerationType type) {
        log(
            "generating Enumeration for: "
                + env.getNames().typeObjectSerializerClassName(
                    servicePackage,
                    type));

        try {
            String className = type.getJavaType().getName() + "_Encoder";
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

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_ENUMERATION_ENCODER);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            out.pln();
            writeImports(out);
            out.pln();
            writeClassDecl(out, className);
            out.pln();
            writeMembers(out, type, className);
            out.pln();
            writeConstructor(out, className);
            out.pln();
            writeGetInstance(out);
            out.pln();
            writeObjectToString(out, type);
            out.pln();
            writeStringToObject(out, type);
            out.pln();
            writeGenericMethods(out, type);
            out.pOln("}"); // class
            out.close();
        } catch (Exception e) {
            fail(e);
        }
    }

    private void generateEnumerationSerializer(LiteralEnumerationType type) {
        log(
            "generating Enumeration for: "
                + env.getNames().typeObjectSerializerClassName(
                    servicePackage,
                    type));

        try {
            String className = type.getJavaType().getName() + "_Encoder";
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

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_ENUMERATION_ENCODER);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            out.pln();
            writeImports(out);
            out.pln();
            writeClassDecl(out, className);
            out.pln();
            writeMembers(out, type, className);
            out.pln();
            writeConstructor(out, className);
            out.pln();
            writeGetInstance(out);
            out.pln();
            writeObjectToString(out, type);
            out.pln();
            writeStringToObject(out, type);
            out.pln();
            writeGenericMethods(out, type);
            out.pOln("}"); // class
            out.close();
        } catch (Exception e) {
            fail(e);
        }
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.simpletype.*;");
        p.pln("import javax.xml.namespace.QName;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
    }

    private void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public class "
                + Names.stripQualifier(className)
                + " extends SimpleTypeEncoderBase {");
    }

    private void writeMembers(
        IndentingWriter p,
        SOAPEnumerationType type,
        String className)
        throws IOException {
        String encoder =
            SimpleTypeSerializerWriter.getTypeEncoder(type.getBaseType());
        if (encoder
            .equals(SimpleTypeSerializerWriter.XSD_LIST_TYPE_ENCODER_NAME)) {
            writeMembers(p, encoder, className, type.getBaseType());
        } else {
            writeMembers(p, encoder, className);
        }
    }
    private void writeMembers(
        IndentingWriter p,
        LiteralEnumerationType type,
        String className)
        throws IOException {
        String encoder =
            SimpleTypeSerializerWriter.getTypeEncoder(type.getBaseType());
        // the base type is an array
        if (encoder
            .equals(SimpleTypeSerializerWriter.XSD_LIST_TYPE_ENCODER_NAME)) {
            writeMembers(p, encoder, className, type.getBaseType());
        } else {
            writeMembers(p, encoder, className);
        }
    }
    private void writeMembers(
        IndentingWriter p,
        String encoder,
        String className)
        throws IOException {
        p.pln(
            "private static final SimpleTypeEncoder encoder = "
                + encoder
                + ".getInstance();");
        p.pln(
            "private static final "
                + Names.stripQualifier(className)
                + " instance = new "
                + Names.stripQualifier(className)
                + "();");
    }

    private void writeMembers(
        IndentingWriter p,
        String encoder,
        String className,
        AbstractType baseType)
        throws IOException {
        p.pln(
            "private static final SimpleTypeEncoder encoder = "
                + encoder
                + ".getInstance("
                + getListBaseTypeEncoder(baseType.getName())
                + ");");
        p.pln(
            "private static final "
                + Names.stripQualifier(className)
                + " instance = new "
                + Names.stripQualifier(className)
                + "();");
    }

    private String getListBaseTypeEncoder(QName type) {
        if (type.equals(SchemaConstants.QNAME_TYPE_NMTOKENS)) {
            return SimpleTypeSerializerWriter.XSD_STRING_ENCODER_NAME
                + ".getInstance(), java.lang.String.class";
        }
        return SimpleTypeSerializerWriter.XSD_STRING_ENCODER_NAME;
    }

    private void writeConstructor(IndentingWriter p, String className)
        throws IOException {
        p.plnI("private " + Names.stripQualifier(className) + "() {");
        p.pOln("}");
    }

    private void writeGetInstance(IndentingWriter p) throws IOException {
        p.plnI("public static SimpleTypeEncoder getInstance() {");
        p.pln("return instance;");
        p.pOln("}");
    }

    private void writeObjectToString(IndentingWriter p, AbstractType type)
        throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType) type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI(
            "public java.lang.String objectToString(java.lang.Object obj, XMLWriter writer) throws java.lang.Exception {");
        p.pln(baseTypeStr + " value = ((" + className + ")obj).getValue();");
        String valueExp = "value";
        if (SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
            valueExp =
                SimpleToBoxedUtil.getBoxedExpressionOfType(
                    valueExp,
                    baseTypeStr);
        }
        p.pln("return encoder.objectToString(" + valueExp + ", writer);");
        p.pOln("}");
    }

    private void writeStringToObject(IndentingWriter p, AbstractType type)
        throws IOException {
        JavaEnumerationType javaEnum = (JavaEnumerationType) type.getJavaType();
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI(
            "public java.lang.Object stringToObject(java.lang.String str, XMLReader reader) throws java.lang.Exception {");
        String objectExp =
            "("
                + SimpleToBoxedUtil.getBoxedClassName(baseTypeStr)
                + ")encoder.stringToObject(str, reader)";
        ;
        if (SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
            objectExp =
                SimpleToBoxedUtil.getUnboxedExpressionOfType(
                    objectExp,
                    baseTypeStr);
        }
        p.pln("return " + className + ".fromValue(" + objectExp + ");");
        p.pOln("}");
    }

    private void writeGenericMethods(IndentingWriter p, AbstractType type)
        throws IOException {
    }

    private void writeEquals(IndentingWriter p, SOAPEnumerationType type)
        throws IOException {
        String className = Names.stripQualifier(type.getJavaType().getName());
        p.plnI("public boolean equals(java.lang.Object obj) {");
        p.plnI("if (!obj instanceof " + className + ") {");
        p.pln("return false;");
        p.pOln("}"); // if
        p.pln("((" + className + ")obj).value.equals(value);");
        p.pOln("}");
    }

    private void writeHashCode(IndentingWriter p, SOAPEnumerationType type)
        throws IOException {
        p.plnI("public int hashCode() {");
        p.pln("return value.hashCode();");
        p.pOln("}");
    }
}
