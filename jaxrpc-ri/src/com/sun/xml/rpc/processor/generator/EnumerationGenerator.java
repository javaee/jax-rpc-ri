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
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.encoding.simpletype.XSDBase64BinaryEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDateTimeCalendarEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDHexBinaryEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDListTypeEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDStringEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDTimeEncoder;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationEntry;
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
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class EnumerationGenerator extends GeneratorBase {
    private Set types;
    private AbstractType type;

    public EnumerationGenerator() {
        super();
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new EnumerationGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new EnumerationGenerator(model, config, properties);
    }

    private EnumerationGenerator(
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
        generateEnumeration(type);
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
        if (!type.getJavaType().isPresent()) {
            String className = type.getJavaType().getName();
            if (!(donotOverride
                && GeneratorUtil.classExists(env, className))) {
                generateEnumeration(type);
            } else {
                log("Class " + className + " exists. Not overriding.");
            }
        }
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

    private void generateEnumeration(AbstractType type) {
        log("generating Enumeration for: " + type.getJavaType().getName());
        this.type = type;
        try {
            String className = type.getJavaType().getName();
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_ENUMERATION);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            out.pln();
            writeImports(out, (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeClassDecl(out, className);
            writeMembers(out, (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeClassConstructor(
                out,
                className,
                (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeGetValue(out, (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeFromValue(out, (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeFromString(out, (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeToString(out, (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeResolveMethod(out, (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeEquals(out, (JavaEnumerationType) type.getJavaType());
            out.pln();
            writeHashCode(out, (JavaEnumerationType) type.getJavaType());
            out.pOln("}"); // class
            out.close();
        } catch (Exception e) {
            fail(e);
        }
    }

    /**
     * @return
     */
    private boolean isBaseTypeHexBinary() {
        return false;
    }

    /**
     * @return
     */
    private boolean isBaseTypeBase64Binary() {
        return false;
    }

    /**
     * @param out
     * @param type
     */
    private void writeImports(IndentingWriter p, JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        p.pln("import java.util.Map;");
        p.pln("import java.util.HashMap;");
        if (isBaseArrayType(baseTypeStr)) {
            p.pln("import java.util.StringTokenizer;");
            p.pln("import java.util.Arrays;");
        }

    }

    private boolean isBaseArrayType(String type) {
        return type.endsWith("[]");
    }

    private void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        // bug fix: 4853867, enumeration class will not be Serializable. but for backward source
        // compatibility it will be Serializble for previouse released version 1.0.1 and 1.0.3

        if (VersionUtil.isVersion101(targetVersion)
            || VersionUtil.isVersion103(targetVersion)
            || this.generateSerializableIf) {

            p.plnI(
                "public class "
                    + Names.stripQualifier(className)
                    + " implements java.io.Serializable {");
        } else {
            writeClassDeclWithoutSerializable(p, className);
        }
    }

    private void writeClassDeclWithoutSerializable(
        IndentingWriter p,
        String className)
        throws IOException {
        p.plnI("public class " + Names.stripQualifier(className) + " {");
    }

    private void writeMembers(IndentingWriter p, JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(javaEnum.getName());
        p.pln("private " + baseTypeStr + " value;");
        p.pln("private static java.util.Map valueMap = new HashMap();");
        Iterator enums = javaEnum.getEntries();
        JavaEnumerationEntry entry;
        if (!SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
            enums = javaEnum.getEntries();
            while (enums.hasNext()) {
                entry = (JavaEnumerationEntry) enums.next();
                p.pln(
                    "public static final java.lang.String _"
                        + entry.getName()
                        + "String = \""
                        + entry.getLiteralValue()
                        + "\";");
            }
            p.pln();
        }
        enums = javaEnum.getEntries();
        while (enums.hasNext()) {
            entry = (JavaEnumerationEntry) enums.next();
            if (SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
                if (baseTypeStr.equals("long")) {
                    p.pln(
                        "public static final "
                            + baseTypeStr
                            + " _"
                            + entry.getName()
                            + " = "
                            + entry.getLiteralValue()
                            + "L;");
                } else {
                    p.pln(
                        "public static final "
                            + baseTypeStr
                            + " _"
                            + entry.getName()
                            + " = ("
                            + baseTypeStr
                            + ")"
                            + entry.getLiteralValue()
                            + ";");
                }
            } else {
                if (baseTypeStr.equals("java.net.URI")) { //bug fix: 4923072
                    p.pln(
                        "public static final "
                            + baseTypeStr
                            + " _"
                            + entry.getName()
                            + " = getURI(_"
                            + entry.getName()
                            + "String);");
                } else if (baseTypeStr.equals("java.util.Calendar")) {
                    java.util.Calendar val = null;
                    if (type instanceof LiteralEnumerationType) {
                        if (((LiteralEnumerationType) type)
                            .getBaseType()
                            .getName()
                            .equals(SchemaConstants.QNAME_TYPE_DATE_TIME)
                            || ((LiteralEnumerationType) type)
                                .getBaseType()
                                .getName()
                                .equals(
                                SchemaConstants.QNAME_TYPE_DATE)) {
                            try {
                                val =
                                    (java
                                        .util
                                        .Calendar) XSDDateTimeCalendarEncoder
                                        .getInstance()
                                        .stringToObject(
                                        entry.getLiteralValue(),
                                        null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (
                            ((LiteralEnumerationType) type)
                                .getBaseType()
                                .getName()
                                .equals(
                                SchemaConstants.QNAME_TYPE_TIME)) {
                            try {
                                val =
                                    (java.util.Calendar) XSDTimeEncoder
                                        .getInstance()
                                        .stringToObject(
                                        entry.getLiteralValue(),
                                        null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (type instanceof SOAPEnumerationType) {
                        if (((SOAPEnumerationType) type)
                            .getBaseType()
                            .getName()
                            .equals(SchemaConstants.QNAME_TYPE_DATE_TIME)
                            || ((SOAPEnumerationType) type)
                                .getBaseType()
                                .getName()
                                .equals(
                                SchemaConstants.QNAME_TYPE_DATE)) {
                            try {
                                val =
                                    (java
                                        .util
                                        .Calendar) XSDDateTimeCalendarEncoder
                                        .getInstance()
                                        .stringToObject(
                                        entry.getLiteralValue(),
                                        null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (
                            ((SOAPEnumerationType) type)
                                .getBaseType()
                                .getName()
                                .equals(
                                SchemaConstants.QNAME_TYPE_TIME)) {
                            try {
                                val =
                                    (java.util.Calendar) XSDTimeEncoder
                                        .getInstance()
                                        .stringToObject(
                                        entry.getLiteralValue(),
                                        null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    p.pln(
                        "public static final "
                            + baseTypeStr
                            + " _"
                            + entry.getName()
                            + " = getCalendar("
                            + val.getTimeInMillis()
                            + "L);");
                } else if (isBaseArrayType(baseTypeStr)) {
                    byte[] val = null;
                    String[] strVal = null;
                    String str = null;

                    if (type instanceof LiteralEnumerationType) {
                        if (((LiteralEnumerationType) type)
                            .getBaseType()
                            .getName()
                            .equals(SchemaConstants.QNAME_TYPE_BASE64_BINARY)) {
                            try {
                                val =
                                    (byte[]) XSDBase64BinaryEncoder
                                        .getInstance()
                                        .stringToObject(
                                        entry.getLiteralValue(),
                                        null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            str = "{" + getArrayInitializer(val) + "};";
                        } else if (
                            ((LiteralEnumerationType) type)
                                .getBaseType()
                                .getName()
                                .equals(
                                SchemaConstants.QNAME_TYPE_HEX_BINARY)) {
                            try {
                                val =
                                    (byte[]) XSDHexBinaryEncoder
                                        .getInstance()
                                        .stringToObject(
                                        entry.getLiteralValue(),
                                        null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            str = "{" + getArrayInitializer(val) + "};";
                        } else if (
                            ((LiteralEnumerationType) type)
                                .getBaseType()
                                .getName()
                                .equals(
                                SchemaConstants.QNAME_TYPE_NMTOKENS)) {
                            try {
                                strVal =
                                    (String[]) XSDListTypeEncoder
                                        .getInstance(
                                            XSDStringEncoder.getInstance(),
                                            java.lang.String.class)
                                        .stringToObject(
                                            entry.getLiteralValue(),
                                            null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            str = "{" + getArrayInitializer(strVal) + "};";
                        }

                    } else if (type instanceof SOAPEnumerationType) {
                        if (((SOAPEnumerationType) type)
                            .getBaseType()
                            .getName()
                            .equals(SchemaConstants.QNAME_TYPE_BASE64_BINARY)) {
                            try {
                                val =
                                    (byte[]) XSDBase64BinaryEncoder
                                        .getInstance()
                                        .stringToObject(
                                        entry.getLiteralValue(),
                                        null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            str = "{" + getArrayInitializer(val) + "};";
                        } else if (
                            ((SOAPEnumerationType) type)
                                .getBaseType()
                                .getName()
                                .equals(
                                SchemaConstants.QNAME_TYPE_HEX_BINARY)) {
                            try {
                                val =
                                    (byte[]) XSDHexBinaryEncoder
                                        .getInstance()
                                        .stringToObject(
                                        entry.getLiteralValue(),
                                        null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            str = "{" + getArrayInitializer(val) + "};";
                        } else if (
                            ((SOAPEnumerationType) type)
                                .getBaseType()
                                .getName()
                                .equals(
                                SchemaConstants.QNAME_TYPE_NMTOKENS)) {
                            try {
                                strVal =
                                    (String[]) XSDListTypeEncoder
                                        .getInstance(
                                            XSDStringEncoder.getInstance(),
                                            java.lang.String.class)
                                        .stringToObject(
                                            entry.getLiteralValue(),
                                            null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            str = "{" + getArrayInitializer(strVal) + "};";
                        }
                    }
                    p.pln(
                        "public static final "
                            + baseTypeStr
                            + " _"
                            + entry.getName()
                            + " = new "
                            + baseTypeStr
                            + str);
                } else if (baseTypeStr.equals("javax.xml.namespace.QName")) {

                    p.pln(
                        "public static final "
                            + baseTypeStr
                            + " _"
                            + entry.getName()
                            + " = javax.xml.namespace.QName.valueOf(\""
                            + entry.getValue().toString()
                            + "\");");
                } else {
                    p.pln(
                        "public static final "
                            + baseTypeStr
                            + " _"
                            + entry.getName()
                            + " = new "
                            + baseTypeStr
                            + "(_"
                            + entry.getName()
                            + "String);");
                }
            }
        }
        p.pln();
        enums = javaEnum.getEntries();
        while (enums.hasNext()) {
            entry = (JavaEnumerationEntry) enums.next();
            p.pln(
                "public static final "
                    + className
                    + " "
                    + entry.getName()
                    + " = new "
                    + className
                    + "(_"
                    + entry.getName()
                    + ");");
        }
        // bug fix: 4923072
        if (baseTypeStr.equals("java.net.URI")) {
            p.pln();
            p.plnI("private static java.net.URI getURI(String val) {");
            p.plnI("try {");
            p.pln("return new java.net.URI(val);");
            p.pOln(
                "}catch(java.net.URISyntaxException e){e.printStackTrace();}");
            p.pln("return null;");
            p.pOln("}");
        }

        if (baseTypeStr.equals("java.util.Calendar")) {
            p.pln();
            p.plnI("private static java.util.Calendar getCalendar(long val) {");
            p.pln(
                "java.util.Calendar cal = new java.util.GregorianCalendar();");
            p.pln("cal.setTimeInMillis(val);");
            p.pln("return cal;");
            p.pOln("}");
        }
    }

    /**
     * @param val
     * @return
     */
    private String getArrayInitializer(Object obj) {
        if (null == obj)
            return null;
        if (!obj.getClass().isArray())
            throw new IllegalArgumentException();
        StringBuffer ret = new StringBuffer();
        int len = Array.getLength(obj);
        if (len == 0)
            return "";
        for (int i = 0; i < len; i++) {
            if (i > 0)
                ret.append(", ");
            if (obj instanceof String[]) {
                ret.append("\"" + Array.get(obj, i) + "\"");
            } else {
                ret.append(Array.get(obj, i));
            }
        }
        return ret.toString();
    }

    private void writeClassConstructor(
        IndentingWriter p,
        String className,
        JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        p.plnI(
            "protected "
                + Names.stripQualifier(className)
                + "("
                + baseTypeStr
                + " value) {");
        p.pln("this.value = value;");
        p.pln("valueMap.put(this.toString(), this);");
        p.pOln("}");
    }

    private void writeGetValue(IndentingWriter p, JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        p.plnI("public " + baseTypeStr + " getValue() {");
        p.pln("return value;");
        p.pOln("}");
    }

    private void writeFromValue(
        IndentingWriter p,
        JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(javaEnum.getName());
        p.plnI(
            "public static "
                + className
                + " fromValue("
                + baseTypeStr
                + " value)");
        p.pln("throws java.lang.IllegalStateException {");
        Iterator enums = javaEnum.getEntries();
        JavaEnumerationEntry entry;
        for (int i = 0; enums.hasNext(); i++) {
            entry = (JavaEnumerationEntry) enums.next();
            if (i > 0)
                p.p(" else ");
            if (SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
                p.plnI("if (" + entry.getName() + ".value == value) {");
            } else {
                if (isBaseArrayType(baseTypeStr)) {
                    p.plnI(
                        "if (java.util.Arrays.equals("
                            + entry.getName()
                            + ".value, value)) {");
                } else if (baseTypeStr.equals("java.util.Calendar")) {
                    p.plnI(
                        "if ("
                            + entry.getName()
                            + ".value.getTimeInMillis() == value.getTimeInMillis()) {");
                } else {
                    p.plnI(
                        "if (" + entry.getName() + ".value.equals(value)) {");
                }
            }
            p.pln("return " + entry.getName() + ";");
            p.pO("}");
        }
        p.pln();
        p.pln("throw new java.lang.IllegalArgumentException();");
        p.pOln("}");
    }

    private void writeFromString(
        IndentingWriter p,
        JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(javaEnum.getName());
        p.plnI("public static " + className + " fromString(java.lang.String value)");
        p.pln("throws java.lang.IllegalStateException {");

        p.pln(className+" ret = ("+className+")valueMap.get(value);");
        p.plnI("if (ret != null) {");
        p.pln("return ret;");
        p.pOln("}");

        Iterator enums = javaEnum.getEntries();
        JavaEnumerationEntry entry;
        for (int i = 0; enums.hasNext(); i++) {
            entry = (JavaEnumerationEntry) enums.next();
            if (i > 0)
                p.p(" else ");
            if (SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
                p.plnI(
                    "if (value.equals(\"" + entry.getLiteralValue() + "\")) {");
            } else {
                if (isBaseArrayType(baseTypeStr)
                    || baseTypeStr.equals("java.util.Calendar")) {
                    p.plnI(
                        "if (value.equals(_"
                            + entry.getName()
                            + ".toString())) {");
                } else {
                    p.plnI(
                        "if (value.equals(_" + entry.getName() + "String)) {");
                }
            }
            p.pln("return " + entry.getName() + ";");
            p.pO("}");
        }
        p.pln();
        p.pln("throw new IllegalArgumentException();");
        p.pOln("}");
    }

    private void writeToString(IndentingWriter p, JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(javaEnum.getName());
        p.plnI("public java.lang.String toString() {");
        String exp = "value";
        if (SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
            exp = SimpleToBoxedUtil.getBoxedExpressionOfType(exp, baseTypeStr);
        }
        p.pln("return " + exp + ".toString();");
        p.pOln("}");
    }

    private void writeResolveMethod(
        IndentingWriter p,
        JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(javaEnum.getName());
        p.plnI("private java.lang.Object readResolve()");
        p.pln("throws java.io.ObjectStreamException {");
        p.pln("return fromValue(getValue());");
        p.pOln("}");
    }

    private void writeEquals(IndentingWriter p, JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        String className = Names.stripQualifier(javaEnum.getName());
        p.plnI("public boolean equals(java.lang.Object obj) {");
        p.plnI("if (!(obj instanceof " + className + ")) {");
        p.pln("return false;");
        p.pOln("}"); // if
        if (SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
            p.pln("return ((" + className + ")obj).value == value;");
        } else {
            p.pln("return ((" + className + ")obj).value.equals(value);");
        }
        p.pOln("}");
    }

    private void writeHashCode(IndentingWriter p, JavaEnumerationType javaEnum)
        throws IOException {
        String baseTypeStr = javaEnum.getBaseType().getName();
        p.plnI("public int hashCode() {");
        if (SimpleToBoxedUtil.isPrimitive(baseTypeStr)) {
            String boxedExp =
                SimpleToBoxedUtil.getBoxedExpressionOfType(
                    "value",
                    baseTypeStr);
            p.pln("return " + boxedExp + ".toString().hashCode();");
        } else {
            p.pln("return value.hashCode();");
        }
        p.pOln("}");
    }
}
