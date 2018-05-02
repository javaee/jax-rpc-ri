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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import com.sun.xml.rpc.processor.config.Configuration;
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
import com.sun.xml.rpc.processor.model.soap.SOAPAttributeMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPObjectBuilderGenerator extends GeneratorBase {
    // vector of builders
    private ArrayList soapBuilders;
    // types processed

    public SOAPObjectBuilderGenerator() {
        soapBuilders = null;
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new SOAPObjectBuilderGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new SOAPObjectBuilderGenerator(model, config, properties);
    }

    private SOAPObjectBuilderGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
        soapBuilders = new ArrayList();
    }

    protected void postVisitModel(Model model) throws Exception {
        Iterator types = model.getExtraTypes();
        AbstractType type;
        while (types.hasNext()) {
            type = (AbstractType) types.next();
            if (type.isSOAPType())
                 ((SOAPType) type).accept(this);
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

    protected void preVisitSOAPStructureType(SOAPStructureType structureType)
        throws Exception {
        if (hasObjectBuilder(structureType)) {
            return;
        }
        try {
            generateObjectBuilderForType(structureType);
        } catch (IOException e) {
            fail(
                "generator.cant.write",
                structureType.getName().getLocalPart());
        }
    }

    /**
     * Generate a class to do custom Serialization/Deserialization for
     * a particular type
     */
    private void generateObjectBuilderForType(SOAPStructureType type)
        throws IOException {

        addObjectBuilder(type);
        if (needBuilder(type))
            writeObjectBuilderForType(type);
    }

    public static boolean needBuilder(SOAPStructureType type) {
        Iterator members = type.getMembers();
        SOAPStructureMember member;
        boolean needBuilder = false;
        JavaStructureMember javaMember;
        if (((JavaStructureType) type.getJavaType()).isAbstract())
            return false;
        while (members.hasNext() && !needBuilder) {
            member = (SOAPStructureMember) members.next();
            javaMember = member.getJavaStructureMember();
            if (!javaMember.isPublic()
                && javaMember.getConstructorPos() < 0
                && javaMember.getWriteMethod() == null) {
                return false;
            }
            if (member.getType().isReferenceable())
                needBuilder = true;
        }
        return needBuilder;
    }

    public static JavaStructureMember[] getConstructorArgs(JavaStructureType type) {
        ArrayList args = new ArrayList(); //type.getMembersCount());
        JavaStructureMember member;
        for (int i = 0; i < type.getMembersCount(); i++) {
            Iterator members = type.getMembers();
            while (members.hasNext()) {
                member = (JavaStructureMember) members.next();
                if (member.getConstructorPos() == i) {
                    args.add(member);
                    break;
                }
            }
        }
        JavaStructureMember[] argArray = new JavaStructureMember[args.size()];
        return (JavaStructureMember[]) args.toArray(argArray);
    }

    private void writeObjectBuilderForType(SOAPStructureType type)
        throws IOException {

        JavaType javaType = type.getJavaType();
        if (javaType == null) {
            fail(
                "generator.invalid.model.state.no.javatype",
                type.getName().getLocalPart());
        }
        String className =
            env.getNames().typeObjectBuilderClassName(servicePackage, type);
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
            fi.setType(GeneratorConstants.FILE_TYPE_SOAP_OBJECT_BUILDER);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writeObjectBuilderCode(out, type);
            out.close();
        } catch (IOException e) {
            fail("generator.cant.write", classFile.toString());
        }
    }

    private void writeObjectBuilderCode(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {

        log("writing object builder for: " + type.getName().getLocalPart());
        String className =
            env.getNames().typeObjectBuilderClassName(servicePackage, type);
        // Write package and import statements...
        writePackage(p, className);
        writeImports(p);
        p.pln();
        writeObjectClassDecl(p, className);
        writeMembers(p, type);
        p.pln();
        writeConstructor(p, className, type);
        p.pln();
        writeSetMembers(p, type);
        p.pln();
        writeMemberGateTypeMethod(p, type);
        p.pln();
        writeConstructMethod(p, type);
        p.pln();
        writeSetMemberMethod(p, type);
        p.pln();
        writeInitializeMethod(p, type);
        p.pln();
        writeGetSetInstanceMethods(p, type);
        p.pOln("}"); // end
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln(
            "import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;");
    }

    private void writeObjectClassDecl(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public class "
                + Names.stripQualifier(className)
                + " implements SOAPInstanceBuilder {");
    }

    private void writeMembers(IndentingWriter p, SOAPStructureType type)
        throws IOException {
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        p.pln("private " + javaStructure.getName() + " _instance;");
        Iterator iterator = javaStructure.getMembers();
        JavaStructureMember javaMember;
        SOAPStructureMember member;
        while (iterator.hasNext()) {
            javaMember = (JavaStructureMember) iterator.next();
            p.pln(
                "private "
                    + javaMember.getType().getName()
                    + " "
                    + env.getNames().validJavaName(javaMember.getName())
                    + ";");
        }
        iterator = javaStructure.getMembers();
        int i;
        for (i = 0; iterator.hasNext(); i++) {
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

    public static boolean needsConstructor(SOAPStructureType type) {
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        JavaStructureMember member;
        Iterator members = javaStructure.getMembers();
        boolean writeContent = false;
        for (int i = 0; members.hasNext(); i++) {
            member = (JavaStructureMember) members.next();
            if (member.getConstructorPos() >= 0) {
                return true;
            }
        }
        return false;
    }

    private void writeConstructor(
        IndentingWriter p,
        String className,
        SOAPStructureType type)
        throws IOException {
        p.pln("public " + Names.stripQualifier(className) + "() {");
        p.pln("}");

        if (!needsConstructor(type))
            return;
        p.pln();
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        JavaStructureMember member;
        Iterator members = javaStructure.getMembers();
        if (members.hasNext()) {
            p.pln();
            p.p("public " + Names.stripQualifier(className) + "(");
            JavaType javaType;
            for (int i = 0; members.hasNext(); i++) {
                if (i != 0)
                    p.p(", ");
                member = (JavaStructureMember) members.next();
                javaType = member.getType();
                p.p(
                    member.getType().getName()
                        + " "
                        + env.getNames().validJavaName(member.getName()));
            }
            p.plnI(") {");
            members = javaStructure.getMembers();
            for (int i = 0; members.hasNext(); i++) {
                member = (JavaStructureMember) members.next();
                javaType = member.getType();
                p.pln(
                    "this."
                        + env.getNames().validJavaName(member.getName())
                        + " = "
                        + env.getNames().validJavaName(member.getName())
                        + ";");
            }
            p.pOln("}");
        }
    }

    private void writeSetMembers(IndentingWriter p, SOAPStructureType type)
        throws IOException {
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        JavaStructureMember javaMember;
        String writeMethod;
        for (int i = 0; iterator.hasNext(); i++) {
            if (i > 0)
                p.pln();
            javaMember = (JavaStructureMember) iterator.next();
            writeMethod = javaMember.getWriteMethod();
            if (writeMethod == null) {
                writeMethod =
                    "set" + StringUtils.capitalize(javaMember.getName());
            }
            p.plnI(
                "public void "
                    + writeMethod
                    + "("
                    + javaMember.getType().getName()
                    + " "
                    + env.getNames().validJavaName(javaMember.getName())
                    + ") {");
            p.pln(
                "this."
                    + env.getNames().validJavaName(javaMember.getName())
                    + " = "
                    + env.getNames().validJavaName(javaMember.getName())
                    + ";");
            p.pOln("}");
        }
    }

    private void writeMemberGateTypeMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        p.plnI("public int memberGateType(int memberIndex) {");
        p.plnI("switch (memberIndex) {");
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        JavaStructureMember javaMember;
        SOAPStructureMember member;
        boolean referenceable;
        Object owner;
        for (int i = 0; iterator.hasNext(); i++) {
            javaMember = (JavaStructureMember) iterator.next();
            owner = javaMember.getOwner();
            SOAPType ownerType = null;
            // bug fix: 4940424            
            if (owner instanceof SOAPStructureMember) {
                ownerType = ((SOAPStructureMember) owner).getType();
            } else { // must be an attribute
                ownerType = ((SOAPAttributeMember) owner).getType();
            }
            referenceable = ownerType.isReferenceable();
            if (referenceable) {
                p.plnI(
                    "case "
                        + env.getNames().memberName(
                            javaMember.getName().toUpperCase() + "_INDEX")
                        + ":");
                if (javaMember.getConstructorPos() < 0) {
                    p.pln("return GATES_INITIALIZATION | REQUIRES_CREATION;");
                } else {
                    p.pln(
                        "return GATES_CONSTRUCTION | REQUIRES_INITIALIZATION;");
                }
                p.pO();
            }
        }
        p.plnI("default:");
        p.pln("throw new IllegalArgumentException();");
        p.pO();
        p.pOln("}"); //switch
        p.pOln("}"); // method
    }

    private void writeConstructMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        JavaStructureMember[] constructorArgs =
            getConstructorArgs(javaStructure);
        p.plnI("public void construct() {");
        if (constructorArgs.length > 0) {
            JavaStructureMember javaMember;
            p.p("_instance = new " + javaStructure.getName() + "(");
            for (int i = 0; i < constructorArgs.length; i++) {
                if (i > 0)
                    p.p(", ");
                javaMember = constructorArgs[i];
                p.p(env.getNames().validJavaName(javaMember.getName()));
            }
            p.pln(");");
        }
        p.pOln("}");
    }

    private void writeSetMemberMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        p.plnI("public void setMember(int index, java.lang.Object memberValue) {");
        // The try is part of the fix for BugID: 4823474
        p.plnI("try {");
        p.plnI("switch(index) {");
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        JavaStructureMember javaMember;
        boolean referenceable;
        Object owner;
        for (int i = 0; iterator.hasNext(); i++) {
            javaMember = (JavaStructureMember) iterator.next();
            owner = javaMember.getOwner();
            SOAPType ownerType = null;
            // bug fix: 4940424
            if (owner instanceof SOAPStructureMember) {
                ownerType = ((SOAPStructureMember) owner).getType();
            } else { // must be an attribute
                ownerType = ((SOAPAttributeMember) owner).getType();
            }
            referenceable = ownerType.isReferenceable();
            if (referenceable) {
                p.plnI(
                    "case "
                        + env.getNames().memberName(
                            javaMember.getName().toUpperCase() + "_INDEX")
                        + ":");
                if (javaMember.isPublic()) {
                    p.p("_instance.");
                    p.pln(
                        env.getNames().validJavaName(javaMember.getName())
                            + " = ("
                            + javaMember.getType().getName()
                            + ")memberValue;");
                } else if (javaMember.getConstructorPos() < 0) {
                    p.p("_instance.");
                    p.pln(
                        javaMember.getWriteMethod()
                            + "(("
                            + javaMember.getType().getName()
                            + ")memberValue);");
                } else {
                    String writeMethod = javaMember.getWriteMethod();
                    if (writeMethod == null) {
                        writeMethod =
                            "set"
                                + StringUtils.capitalize(javaMember.getName());
                    }
                    p.pln(
                        writeMethod
                            + "(("
                            + javaMember.getType().getName()
                            + ")memberValue);");
                }
                p.pln("break;");
                p.pO();
            }
        }
        p.plnI("default:");
        p.pln("throw new java.lang.IllegalArgumentException();");
        p.pO();
        p.pOln("}"); //switch
        // Fix for BugID: 4823474
        p.pOln("}");
        p.plnI("catch (java.lang.RuntimeException e) {");
        p.pln("throw e;");
        p.pOln("}");
        p.plnI("catch (java.lang.Exception e) {");
        p.pln(
            "throw new DeserializationException(new LocalizableExceptionAdapter(e));");
        p.pOln("}");
        // end bug fix 4823474
        p.pOln("}"); //method
    }

    private void writeInitializeMethod(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Iterator iterator = javaStructure.getMembers();
        JavaStructureMember javaMember;
        SOAPStructureMember soapMember;
        boolean writeContent = false;
        p.plnI("public void initialize() {");
        if (writeContent) {
            iterator = javaStructure.getMembers();
            p.plnI("for (int i=0; i<memberSet.length; i++) {");
            p.plnI("if (!memberSet[i]) {");
            p.pln("continue;");
            p.pOln("}");
            p.plnI("switch(i) {");
            for (int i = 0; iterator.hasNext(); i++) {
                javaMember = (JavaStructureMember) iterator.next();
                soapMember = (SOAPStructureMember) javaMember.getOwner();
                if (soapMember.getType().isReferenceable()
                    && !javaMember.isPublic()
                    && javaMember.getConstructorPos() < 0) {
                    p.plnI(
                        "case "
                            + env.getNames().memberName(
                                javaMember.getName().toUpperCase() + "_INDEX")
                            + ":");
                    p.pln(
                        "_instance."
                            + javaMember.getWriteMethod()
                            + "("
                            + env.getNames().validJavaName(javaMember.getName())
                            + ");");
                    p.pln("break;");
                    p.pO();
                }
            }
            p.pOln("}"); // switch
            p.pOln("}"); // for
        }
        p.pOln("}"); //method
    }

    private void writeGetSetInstanceMethods(
        IndentingWriter p,
        SOAPStructureType type)
        throws IOException {
        p.plnI("public void setInstance(java.lang.Object instance) {");
        p.pln("_instance = (" + type.getJavaType().getName() + ")instance;");
        p.pOln("}");
        p.pln();
        p.plnI("public java.lang.Object getInstance() {");
        p.pln("return _instance;");
        p.pOln("}");
    }

    private boolean hasObjectBuilder(SOAPType type) {

        return soapBuilders.contains(type);
    }

    private void addObjectBuilder(SOAPType type) throws IOException {
        if (soapBuilders.contains(type)) {
            // this should never happen
            fail("Internal error: attempting to add duplicate SOAP builder");
        }
        soapBuilders.add(type);
    }
}
