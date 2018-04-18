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
 * $Id: FaultExceptionBuilderGenerator.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;

public class FaultExceptionBuilderGenerator extends GeneratorBase {
    private Set operations;
    private Port port;

    public FaultExceptionBuilderGenerator() {
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new FaultExceptionBuilderGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new FaultExceptionBuilderGenerator(model, config, properties);
    }

    private FaultExceptionBuilderGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
    }

    public void preVisitModel(Model model) {
        operations = new HashSet();
    }

    public void postVisitModel(Model model) {
        operations = null;
    }

    protected void preVisitPort(Port port) throws Exception {
        super.preVisitPort(port);
        this.port = port;
    }

    protected void postVisitPort(Port port) throws Exception {
        this.port = null;
        super.postVisitPort(port);
    }

    public void preVisitOperation(Operation operation) throws Exception {
        if (!isRegistered(operation))
            registerFault(operation);
    }
    private boolean isRegistered(Operation operation) {
        return operations.contains(getInputMessageName(operation));
    }

    private String getInputMessageName(Operation operation) {
        QName value =
            (QName) operation.getRequest().getProperty(
                ModelProperties.PROPERTY_WSDL_MESSAGE_NAME);
        if (value != null) {
            return value.getLocalPart();
        } else {
            return operation.getName().getLocalPart();
        }
    }

    private void registerFault(Operation operation) throws Exception {
        operations.add(getInputMessageName(operation));
        generateBuilderForOperation(operation);
    }

    /**
     * Generate a class to do build faults for
     * a particular fault/exception
     */
    private void generateBuilderForOperation(Operation operation)
        throws IOException, GeneratorException {

        if (needsBuilder(operation))
            writeBuilderForOperation(operation);
    }

    public static boolean needsBuilder(Operation operation) {
        //        Iterator faults = operation.getAllFaults();
        Iterator faults = operation.getFaults();
        return faults != null ? faults.hasNext() : false;
    }

    private void writeBuilderForOperation(Operation operation)
        throws IOException, GeneratorException {

        String className =
            env.getNames().faultBuilderClassName(
                servicePackage,
                port,
                operation);
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
            fi.setType(GeneratorConstants.FILE_TYPE_FAULT_EXCEPTION_BUILDER);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writeObjectBuilderCode(out, operation, className);
            out.close();
        } catch (IOException e) {
            fail("generator.cant.write", classFile.toString());
        }
    }

    private void writeObjectBuilderCode(
        IndentingWriter p,
        Operation operation,
        String className)
        throws IOException, GeneratorException {

        log("writing object builder for: " + operation.getName());
        // Write package and import statements...
        writePackage(p, className);
        writeImports(p);
        p.pln();
        writeObjectClassDecl(p, className);
        writeMembers(p, operation);
        p.pln();
        writeMemberGateTypeMethod(p);
        p.pln();
        writeConstructMethod(p);
        p.pln();
        writeSetMemberMethod(p, operation);
        p.pln();
        writeInitializeMethod(p, operation);
        p.pln();
        writeGetSetInstanceMethods(p);
        p.pOln("}"); // end
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.soap.message.SOAPFaultInfo;");
        p.pln("import java.lang.IllegalArgumentException;");
    }

    private void writeObjectClassDecl(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public final class "
                + Names.stripQualifier(className)
                + " implements com.sun.xml.rpc.encoding.SOAPInstanceBuilder {");
    }

    private void writeMembers(IndentingWriter p, Operation operation)
        throws IOException {
        p.pln("private SOAPFaultInfo instance = null;");
        p.pln("private java.lang.Object detail;");
        p.pln("// this is the index of the fault deserialized");
        p.pln("private int index = -1;");
        //        Iterator faults = operation.getAllFaults();
        Iterator faults = operation.getFaults();
        Fault fault;
        for (int i = 0; faults.hasNext();) {
            fault = (Fault) faults.next();
            p.pln(
                "private static final int "
                    + fault.getJavaException().getName().toUpperCase().replace(
                        '.',
                        '_')
                    + "_INDEX = "
                    + i
                    + ";");
            i++;
        }
    }

    private void writeMemberGateTypeMethod(IndentingWriter p)
        throws IOException {
        p.plnI("public int memberGateType(int memberIndex) {");
        p.pln("return GATES_INITIALIZATION + REQUIRES_COMPLETION;");
        p.pOln("}"); // method
    }

    private void writeConstructMethod(IndentingWriter p) throws IOException {
        p.plnI("public void construct() {");
        p.pOln("}");
    }

    private void writeSetMemberMethod(IndentingWriter p, Operation operation)
        throws IOException {
        p.plnI("public void setMember(int index, java.lang.Object memberValue) {");
        p.pln("this.index = index;");
        p.pln("detail = memberValue;");
        p.pOln("}"); //method
    }

    private void writeInitializeMethod(IndentingWriter p, Operation operation)
        throws IOException {
        p.plnI("public void initialize() {");
        p.plnI("switch (index) {");
        //        Iterator faults = operation.getAllFaults();
        Iterator faults = operation.getFaults();
        Fault fault;
        JavaException javaException;
        SOAPType type;
        String javaName;
        for (int i = 0; faults.hasNext();) {
            fault = (Fault) faults.next();
            javaException = fault.getJavaException();
            p.plnI(
                "case "
                    + fault.getJavaException().getName().toUpperCase().replace(
                        '.',
                        '_')
                    + "_INDEX"
                    + ":");
            p.pln("instance.setDetail(detail);");
            p.pln("break;");
            p.pO();
        }
        p.pOln("}"); // switch
        p.pOln("}"); //method
    }

    private void writeGetSetInstanceMethods(IndentingWriter p)
        throws IOException {
        p.plnI("public void setInstance(java.lang.Object instance) {");
        p.pln("this.instance = (SOAPFaultInfo)instance;");
        p.pOln("}");
        p.pln();
        p.plnI("public java.lang.Object getInstance() {");
        p.pln("return instance;");
        p.pOln("}");
    }
}
