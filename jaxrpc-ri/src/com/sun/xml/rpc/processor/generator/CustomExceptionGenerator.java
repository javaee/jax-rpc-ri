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

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class CustomExceptionGenerator extends GeneratorBase {
    private Set faults;

    public CustomExceptionGenerator() {
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new CustomExceptionGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new CustomExceptionGenerator(model, config, properties);
    }

    private CustomExceptionGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        faults = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        faults = null;
    }

    protected void preVisitFault(Fault fault) throws Exception {
        if (isRegistered(fault))
            return;
        registerFault(fault);
        if (fault.getParentFault() != null) {
            preVisitFault(fault.getParentFault());
        }
    }

    private boolean isRegistered(Fault fault) {
        return faults.contains(fault.getJavaException().getName());
    }

    private void registerFault(Fault fault) {
        faults.add(fault.getJavaException().getName());
        generateCustomException(fault);
    }

    private void generateCustomException(Fault fault) {
        if (fault.getJavaException().isPresent())
            return;
        log(
            "generating CustomException for: "
                + fault.getJavaException().getName());
        try {
            String className = env.getNames().customExceptionClassName(fault);
            if ((donotOverride && 
                 GeneratorUtil.classExists(env, className))) {
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
            fi.setType(GeneratorConstants.FILE_TYPE_EXCEPTION);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            out.pln();
            JavaStructureType javaStructure =
                (JavaStructureType) fault.getJavaException();
            writeClassDecl(out, className, javaStructure);
            writeMembers(out, fault);
            out.pln();
            writeClassConstructor(out, className, fault);
            out.pln();
            writeGetter(out, fault);
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
        if (superclass != null) {
            p.plnI(
                "public class "
                    + Names.stripQualifier(className)
                    + " extends "
                    + superclass.getName()
                    + " {");
        } else {
            p.plnI(
                "public class "
                    + Names.stripQualifier(className)
                    + " extends java.lang.Exception {");
        }
    }

    private void writeMembers(IndentingWriter p, Fault fault)
        throws IOException {
        Iterator members =
            ((JavaStructureType) fault.getJavaException()).getMembers();
        JavaStructureMember member;
        while (members.hasNext()) {
            member = (JavaStructureMember) members.next();
            if (!member.isInherited()) {
                p.pln(
                    "private "
                        + member.getType().getName()
                        + " "
                        + member.getName()
                        + ";");
            }
        }
        p.pln();
    }

    private void writeClassConstructor(
        IndentingWriter p,
        String className,
        Fault fault)
        throws IOException {
        JavaStructureType javaStructure =
            (JavaStructureType) fault.getJavaException();

        p.p("public " + Names.stripQualifier(className) + "(");
        Iterator members = javaStructure.getMembers();
        JavaStructureMember member;
        int stringTypeCount = 0;
        String stringMemberName = null;
        for (int i = 0; members.hasNext(); i++) {
            member = (JavaStructureMember) members.next();
            if (i > 0)
                p.p(", ");
            p.p(member.getType().getName() + " " + member.getName());
            if (member
                .getType()
                .getName()
                .equals(ModelerConstants.STRING_CLASSNAME)) {
                stringTypeCount++;
                stringMemberName = member.getName();
            }
        }
        p.plnI(") {");
        if (fault.getParentFault() != null) {
            members = javaStructure.getMembers();
            int i = 0;
            while (members.hasNext()) {
                member = (JavaStructureMember) members.next();
                if (member.isInherited()) {
                    if (i++ == 0)
                        p.p("super(");
                    else
                        p.p(", ");
                    p.p(member.getName());
                }
            }
            if (i > 0)
                p.pln(");");
        } else if (stringTypeCount == 1) {
            p.pln("super(" + stringMemberName + ");");
        }
        members = fault.getJavaException().getMembers();
        for (int i = 0; members.hasNext(); i++) {
            member = (JavaStructureMember) members.next();
            if (!member.isInherited()) {
                p.pln(
                    "this."
                        + member.getName()
                        + " = "
                        + member.getName()
                        + ";");
            }
        }
        p.pOln("}");
    }

    private void writeGetter(IndentingWriter p, Fault fault)
        throws IOException {
        Iterator members =
            ((JavaStructureType) fault.getJavaException()).getMembers();
        JavaStructureMember member;
        int i = 0;
        while (members.hasNext()) {
            if (i > 0)
                p.pln();
            member = (JavaStructureMember) members.next();
            if (!member.isInherited()) {
                p.plnI(
                    "public "
                        + member.getType().getName()
                        + " "
                        + member.getReadMethod()
                        + "() {");
                p.pln("return " + member.getName() + ";");
                p.pOln("}");
                i++;
            }
        }
    }
}
