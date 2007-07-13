/*
 * $Id: HolderGenerator.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPListType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class HolderGenerator extends GeneratorBase {
    private Set types;
    private Port port;
    private Map generatedHolderClassMap;

    private void init() {
        // keep the generated folder classname and the java type name.
        generatedHolderClassMap = new HashMap();
    }
    public HolderGenerator() {
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new HolderGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new HolderGenerator(model, config, properties, ver);
    }

    private HolderGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
        init();
    }

    private HolderGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        this(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        types = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        types = null;
    }

    protected void preVisitPort(Port port) throws Exception {
        super.preVisitPort(port);
        this.port = port;
    }

    protected void postVisitPort(Port port) throws Exception {
        this.port = null;
        super.postVisitPort(port);
    }

    // SOAPType Visits
    public void visit(SOAPCustomType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
    }

    public void visit(SOAPAnyType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
    }

    //bug fix: 4900251, generate holder for enumeration type
    public void visit(LiteralEnumerationType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
    }

    protected void visitSOAPArrayType(SOAPArrayType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
        super.visitSOAPArrayType(type);
    }

    // bug fix: 4900251, generate holder for array of simple type
    protected void visitSOAPListType(SOAPListType type) throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
        super.visitSOAPListType(type);
    }

    //bug fix: 4900251, generate holder for array of simple type
    protected void visitLiteralListType(LiteralListType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
        super.visitLiteralListType(type);
    }

    protected void visitSOAPStructureType(SOAPStructureType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
        super.visitSOAPStructureType(type);
    }

    protected void visitLiteralSimpleType(LiteralSimpleType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
    }

    protected void visitLiteralSequenceType(LiteralSequenceType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
        super.visitLiteralSequenceType(type);
    }

    protected void preVisitLiteralAllType(LiteralAllType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
        super.preVisitLiteralAllType(type);
    }

    protected void preVisitLiteralFragmentType(LiteralFragmentType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
    }

    protected void visitLiteralAttachmentType(LiteralAttachmentType type)
        throws Exception {
        if (isRegistered(type)) {
            return;
        }
        registerType(type);
        if (type.getJavaType().isHolder()) {
            generateHolder(type);
        }
    }

    private boolean isRegistered(AbstractType type) {
        return types.contains(type);
    }

    private void registerType(AbstractType type) {
        types.add(type);
    }

    private void generateHolder(AbstractType type) {
        // holder might already be present
        if (type.getJavaType().isHolderPresent()) {
            return;
        }
        try {
            String className = env.getNames().holderClassName(port, type);
            if (className.startsWith("javax.xml.rpc.holders.")) {
                // avoid generating holders for built-in classes
                return;
            }
            if (donotOverride && GeneratorUtil.classExists(env, className)) {
                log("Class " + className + " exists. Not overriding.");
                return;
            }
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);
            // avoid generating duplicate holders
            if (generatedHolderClassMap.get(className) == null) {
                /* adding the file name and its type */
                GeneratedFileInfo fi = new GeneratedFileInfo();
                fi.setFile(classFile);
                fi.setType(GeneratorConstants.FILE_TYPE_HOLDER);
                env.addGeneratedFile(fi);

                IndentingWriter out =
                    new IndentingWriter(
                        new OutputStreamWriter(
                            new FileOutputStream(classFile)));
                writePackage(out, className);
                out.pln();
                writeClassDecl(out, className);
                writeMembers(out, type);
                out.pln();
                writeClassConstructor(out, className, type);
                out.pOln("}"); // class
                out.close();
                generatedHolderClassMap.put(className, type.getJavaType());
            }
        } catch (Exception e) {
            fail(e);
        }
    }

    private void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public class "
                + Names.stripQualifier(className)
                + " implements javax.xml.rpc.holders.Holder {");
    }

    private void writeMembers(IndentingWriter p, AbstractType type)
        throws IOException {
        p.pln("public " + type.getJavaType().getName() + " value;");
    }

    private void writeClassConstructor(
        IndentingWriter p,
        String className,
        AbstractType type)
        throws IOException {
        p.pln("public " + Names.stripQualifier(className) + "() {");
        p.pln("}");
        p.pln();
        p.plnI(
            "public "
                + Names.stripQualifier(className)
                + "("
                + type.getJavaType().getName()
                + " "
                + env.getNames().getTypeMemberName(type)
                + ") {");
        p.pln("this.value = " + env.getNames().getTypeMemberName(type) + ";");
        p.pOln("}");
    }

}
