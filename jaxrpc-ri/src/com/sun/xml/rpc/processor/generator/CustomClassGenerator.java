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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
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
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class CustomClassGenerator extends GeneratorBase {
    private Set types;
    private Set faults;
    private boolean dontGenerateRPCStructures;
    private boolean dontGenerateWrapperClasses;

    public CustomClassGenerator() {
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new CustomClassGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new CustomClassGenerator(model, config, properties);
    }

    private CustomClassGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
        String key = ProcessorOptions.DONT_GENERATE_RPC_STRUCTURES;
        dontGenerateRPCStructures =
            Boolean.valueOf(properties.getProperty(key)).booleanValue();
        key = ProcessorOptions.DONT_GENERATE_WRAPPER_CLASSES;
        dontGenerateWrapperClasses =
            Boolean.valueOf(properties.getProperty(key)).booleanValue();
    }

    protected void visitFault(Fault fault) throws Exception {
        if (isRegistered(fault))
            return;
        registerFault(fault);
        JavaException exception = fault.getJavaException();
        AbstractType aType = (AbstractType) exception.getOwner();
        if (aType.isSOAPType()) {
            ((SOAPType) aType).accept(this);
            Iterator members = exception.getMembers();
            SOAPType type;
            while (members.hasNext()) {
                type =
                    ((SOAPStructureMember) ((JavaStructureMember) members
                        .next())
                        .getOwner())
                        .getType();
                type.accept(this);
            }
        } else { // Literal
             ((LiteralType) aType).accept(this);
            Iterator members = exception.getMembers();
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
        if (fault.getParentFault() != null)
            fault.getParentFault().accept(this);
        for (Iterator iter = fault.getSubfaults();
            iter != null && iter.hasNext();
            ) {
            ((Fault) iter.next()).accept(this);
        }
    }

    protected void preVisitModel(Model model) throws Exception {
        types = new HashSet();
        faults = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        types = null;
        faults = null;
    }

    public void preVisitSOAPArrayType(SOAPArrayType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
    }

    public void preVisitSOAPStructureType(SOAPStructureType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        if (dontGenerateRPCStructures
            && ((type instanceof RPCRequestOrderedStructureType)
                || (type instanceof RPCRequestUnorderedStructureType)
                || (type instanceof RPCResponseStructureType))) {
            return;
        }
        registerType(type);
        if (!type.getJavaType().isPresent()) {
            String className = env.getNames().customJavaTypeClassName(type);
            if (!(donotOverride
                && GeneratorUtil.classExists(env, className))) {
                generateJavaClass(type);
            } else {
                log("Class " + className + " exists. Not overriding.");
            }
        }
        if (type.getParentType() != null)
             ((SOAPStructureType) type.getParentType()).accept(this);
        for (Iterator iter = type.getSubtypes();
            iter != null && iter.hasNext();
            ) {
            ((SOAPStructureType) iter.next()).accept(this);
        }
    }

    public void preVisitLiteralSequenceType(LiteralSequenceType type)
        throws Exception {
        visitLiteralStructuredType(type);
    }

    public void preVisitLiteralAllType(LiteralAllType type) throws Exception {
        visitLiteralStructuredType(type);
    }

    protected void preVisitLiteralArrayWrapperType(LiteralArrayWrapperType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (!type.getJavaType().isPresent()) {
            String className = env.getNames().customJavaTypeClassName(type);
            if (!(donotOverride
                && GeneratorUtil.classExists(env, className))) {
                generateJavaClass(type);
            } else {
                log("Class " + className + " exists. Not overriding.");
            }
        }
    }

    private void visitLiteralStructuredType(LiteralStructuredType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        if (dontGenerateRPCStructures && type.isRpcWrapper()) {
            return;
        }
        registerType(type);
        if (!type.getJavaType().isPresent()) {
            String className = env.getNames().customJavaTypeClassName(type);
            if (!(donotOverride
                && GeneratorUtil.classExists(env, className))) {
                generateJavaClass(type);
            } else {
                log("Class " + className + " exists. Not overriding.");
            }
        }
        if (type.getParentType() != null)
             ((LiteralStructuredType) type.getParentType()).accept(this);
        for (Iterator iter = type.getSubtypes();
            iter != null && iter.hasNext();
            ) {
            ((LiteralStructuredType) iter.next()).accept(this);
        }
    }

    private boolean isRegistered(AbstractType type) {
        return types.contains(type);
    }

    private void registerType(AbstractType type) {
        types.add(type);
    }

    private boolean isRegistered(Fault fault) {
        return faults.contains(fault);
    }

    private void registerFault(Fault fault) {
        faults.add(fault);
    }

    private void generateJavaClass(SOAPStructureType type) {
        if (type.getJavaType() instanceof JavaException)
            return;
        log("generating JavaClass for: " + type.getName().getLocalPart());

        try {
            String className = env.getNames().customJavaTypeClassName(type);
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);
            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_VALUETYPE);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            out.pln();
            JavaStructureType javaStructure =
                (JavaStructureType) type.getJavaType();
            writeClassDecl(out, className, javaStructure);
            Iterator members = javaStructure.getMembers();
            JavaStructureMember member;
            String typeName;
            while (members.hasNext()) {
                member = (JavaStructureMember) members.next();
                if (member.isInherited()
                    && javaStructure.getSuperclass() != null)
                    continue;
                typeName = member.getType().getName();
                if (member.isPublic()) {
                    out.pln(
                        "public " + typeName + " " + member.getName() + ";");
                } else {
                    out.pln(
                        "protected " + typeName + " " + member.getName() + ";");
                }
            }
            out.pln();
            writeClassConstructor(out, className, javaStructure);
            members = javaStructure.getMembers();
            while (members.hasNext()) {
                member = (JavaStructureMember) members.next();
                if (member.isInherited()
                    && javaStructure.getSuperclass() != null)
                    continue;
                out.pln();
                out.plnI(
                    "public "
                        + member.getType().getName()
                        + " "
                        + member.getReadMethod()
                        + "() {");
                out.pln("return " + member.getName() + ";");
                out.pOln("}");
                out.pln();
                out.plnI(
                    "public void "
                        + member.getWriteMethod()
                        + "("
                        + member.getType().getName()
                        + " "
                        + member.getName()
                        + ") {");
                out.pln(
                    "this."
                        + member.getName()
                        + " = "
                        + member.getName()
                        + ";");
                out.pOln("}");
            }
            out.pOln("}"); // class
            out.close();
        } catch (Exception e) {
            fail(e);
        }
    }

    private void generateJavaClass(LiteralStructuredType type) {
        if (type.getJavaType() instanceof JavaException
            || (dontGenerateWrapperClasses
                && type instanceof LiteralSequenceType
                && ((LiteralSequenceType) type).isUnwrapped()))
            return;

        // NOTE - right now, this is the exact same code as generateJavaClass(SOAPStructureType type),
        // but things may change (since we have a lot more freedom in choosing the mapping we use
        // when doing literal data binding than in the SOAP case)

        log("generating JavaClass for: " + type.getName().getLocalPart());

        try {
            String className = env.getNames().customJavaTypeClassName(type);
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);
            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_VALUETYPE);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            out.pln();
            JavaStructureType javaStructure =
                (JavaStructureType) type.getJavaType();
            writeClassDecl(out, className, javaStructure);
            Iterator members = javaStructure.getMembers();
            JavaStructureMember member;
            String typeName;
            while (members.hasNext()) {
                member = (JavaStructureMember) members.next();
                if (member.isInherited())
                    continue;
                typeName = member.getType().getName();
                if (member.isPublic()) {
                    out.pln(
                        "public " + typeName + " " + member.getName() + ";");
                } else {
                    out.pln(
                        "protected " + typeName + " " + member.getName() + ";");
                }
            }
            out.pln();
            writeClassConstructor(out, className, javaStructure);
            members = javaStructure.getMembers();
            while (members.hasNext()) {
                member = (JavaStructureMember) members.next();
                if (member.isInherited())
                    continue;
                out.pln();
                out.plnI(
                    "public "
                        + member.getType().getName()
                        + " "
                        + member.getReadMethod()
                        + "() {");
                out.pln("return " + member.getName() + ";");
                out.pOln("}");
                out.pln();
                out.plnI(
                    "public void "
                        + member.getWriteMethod()
                        + "("
                        + member.getType().getName()
                        + " "
                        + member.getName()
                        + ") {");
                out.pln(
                    "this."
                        + member.getName()
                        + " = "
                        + member.getName()
                        + ";");
                out.pOln("}");
            }
            out.pOln("}"); // class
            out.close();
        } catch (Exception e) {
            fail(e);
        }
    }

    private void generateJavaClass(LiteralArrayWrapperType type) {
        if (type.getJavaType() instanceof JavaException)
            return;

        // NOTE - right now, this is the exact same code as generateJavaClass(SOAPStructureType type),
        // but things may change (since we have a lot more freedom in choosing the mapping we use
        // when doing literal data binding than in the SOAP case)

        log("generating JavaClass for: " + type.getName().getLocalPart());

        try {
            String className = env.getNames().customJavaTypeClassName(type);
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);
            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_VALUETYPE);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            out.pln();
            JavaStructureType javaStructure =
                (JavaStructureType) type.getJavaType();
            writeClassDecl(out, className, javaStructure);
            String structArrayName =
                ((JavaStructureMember) javaStructure.getMembers().next())
                    .getType()
                    .getName();
            String memberName =
                ((JavaStructureMember) javaStructure.getMembers().next())
                    .getName();
            out.pln("private " + structArrayName + " " + memberName + ";");

            out.pln();
            writeArrayWrapperTypeClassConstructors(out, className, type);
            out.pln();
            writeFromArrayToArrayMethods(out, type);
            JavaStructureMember member =
                type.getElementMember().getJavaStructureMember();
            out.pln();
            out.plnI(
                "public "
                    + structArrayName
                    + " "
                    + member.getReadMethod()
                    + "() {");
            out.pln("return " + memberName + ";");
            out.pOln("}");
            out.pln();
            out.plnI(
                "public void "
                    + member.getWriteMethod()
                    + "("
                    + structArrayName
                    + " "
                    + memberName
                    + ") {");
            out.pln("this." + memberName + " = " + memberName + ";");
            out.pOln("}");
            out.pOln("}"); // class
            out.close();
        } catch (Exception e) {
            fail(e);
        }
    }

    private void writeClassDecl(
        IndentingWriter p,
        String className,
        JavaStructureType javaStruct)
        throws IOException {
        JavaStructureType superclass = javaStruct.getSuperclass();
        String classDeclStr = "public class " + Names.stripQualifier(className);
        if (superclass != null) {
            classDeclStr += " extends " + superclass.getName();
        }
        if (generateSerializableIf) {
            classDeclStr += " implements java.io.Serializable";
        }
        classDeclStr += " {";
        p.plnI(classDeclStr);
    }

    private void writeClassConstructor(
        IndentingWriter p,
        String className,
        JavaStructureType javaStructure)
        throws IOException {
        p.pln("public " + Names.stripQualifier(className) + "() {");
        p.pln("}");
        Iterator members = javaStructure.getMembers();
        if (members.hasNext()) {
            p.pln();
            p.p("public " + Names.stripQualifier(className) + "(");
            JavaStructureMember member;
            for (int i = 0; members.hasNext(); i++) {
                if (i != 0)
                    p.p(", ");
                member = (JavaStructureMember) members.next();
                p.p(member.getType().getName() + " " + member.getName());
            }
            p.plnI(") {");
            members = javaStructure.getMembers();
            for (int i = 0; members.hasNext(); i++) {
                member = (JavaStructureMember) members.next();
                p.pln(
                    "this."
                        + member.getName()
                        + " = "
                        + member.getName()
                        + ";");
            }
            p.pOln("}");
        }
    }

    private void writeArrayWrapperTypeClassConstructors(
        IndentingWriter p,
        String className,
        LiteralArrayWrapperType arrayWrapperType)
        throws IOException {
        String theClassName = Names.stripQualifier(className);
        String structArrayName =
            arrayWrapperType
                .getElementMember()
                .getType()
                .getJavaType()
                .getName()
                + "[]";
        JavaStructureMember member;
        JavaStructureType javaStructure =
            (JavaStructureType) arrayWrapperType.getJavaType();
        member = (JavaStructureMember) javaStructure.getMembers().next();
        String memberName = member.getName();
        p.pln("public " + theClassName + "() {");
        p.pln("}");
        p.pln();
        p.p("public " + theClassName + "(" + structArrayName);
        p.p(" sourceArray");
        p.plnI(") {");
        p.pln(memberName + " = sourceArray;");
        p.pOln("}");
        if (arrayWrapperType.getElementMember().getType()
            instanceof LiteralArrayWrapperType) {
            p.pln();
            p.p("public " + theClassName + "(");
            p.p(arrayWrapperType.getJavaArrayType().getName() + " sourceArray");
            p.plnI(") {");
            p.pln("fromArray(sourceArray);");
            p.pOln("}");
        }
    }

    private void writeFromArrayToArrayMethods(
        IndentingWriter p,
        LiteralArrayWrapperType type)
        throws IOException {

        boolean elemTypeIsArrayWrapperType =
            type.getElementMember().getType()
                instanceof LiteralArrayWrapperType;
        JavaStructureMember member;
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        member = (JavaStructureMember) javaStructure.getMembers().next();
        JavaType javaType = member.getType();
        String typeName = type.getJavaArrayType().getName();
        String elementTypeName = javaType.getName();
        String memberName = member.getName();

        p.plnI("public void fromArray(" + typeName + " sourceArray) {");
        if (elemTypeIsArrayWrapperType) {
            int idx = elementTypeName.indexOf(BRACKETS);
            String tmp = elementTypeName.substring(0, idx);
            p.pln(memberName + " = new " + tmp + "[sourceArray.length];");
            p.plnI("for (int i=0; i<sourceArray.length; i++) {");
            p.pln(memberName + "[i] = new " + tmp + "(sourceArray[i]);");
            p.pOln("}");
        } else {
            p.pln("this." + memberName + " = sourceArray;");
        }
        p.pOln("}");
        p.pln();

        p.plnI("public " + typeName + " toArray() {");
        if (elemTypeIsArrayWrapperType) {
            String javaTypeName = type.getJavaArrayType().getName();
            int idx = javaTypeName.indexOf(BRACKETS) + 1;
            String tmp = javaTypeName.substring(0, idx);
            p.pln(
                javaTypeName
                    + " tmpArray = new "
                    + tmp
                    + memberName
                    + ".length"
                    + javaTypeName.substring(idx)
                    + ";");
            p.plnI("for (int i=0; i<" + memberName + ".length; i++) {");
            p.pln("tmpArray[i] = " + memberName + "[i].toArray();");
            p.pOln("}");
            p.pln("return tmpArray;");
        } else {
            p.pln("return " + memberName + ";");
        }
        p.pOln("}");

    }
}
