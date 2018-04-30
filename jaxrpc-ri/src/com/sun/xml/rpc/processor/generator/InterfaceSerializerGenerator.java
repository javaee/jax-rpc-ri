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
import java.util.TreeSet;

import com.sun.xml.rpc.processor.config.Configuration;
import com
    .sun
    .xml
    .rpc
    .processor
    .generator
    .writer
    .LiteralSequenceSerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SOAPObjectSerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class InterfaceSerializerGenerator extends GeneratorBase {
    // vector of customized serializers/deserializers
    private Set visitedTypes;
    private Fault currentFault = null;

    private static final String OBJECT_SERIALIZER_BASE = "ObjectSerializerBase";
    private static final String INTERFACE_SERIALIZER_BASE =
        "InterfaceSerializerBase";

    public InterfaceSerializerGenerator() {
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new InterfaceSerializerGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new InterfaceSerializerGenerator(model, config, properties);
    }

    private InterfaceSerializerGenerator(
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

    protected void preVisitFault(Fault fault) throws Exception {
        if (fault.getBlock().getType().isSOAPType()) {
            currentFault = fault;
            ((SOAPType) fault.getBlock().getType()).accept(this);
            currentFault = null;
        }
        if (fault.getBlock().getType().isLiteralType()) {
            currentFault = fault;
            ((LiteralType) fault.getBlock().getType()).accept(this);
            currentFault = null;
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

    public void preVisitLiteralSequenceType(LiteralSequenceType type)
        throws Exception {
        visitLiteralStructuredType(type);
    }

    public void preVisitLiteralAllType(LiteralAllType type) throws Exception {
        visitLiteralStructuredType(type);
    }

    private void visitLiteralStructuredType(LiteralStructuredType type)
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

    private boolean haveVisited(AbstractType type) {
        return visitedTypes.contains(type);
    }

    private void typeVisited(AbstractType type) {
        visitedTypes.add(type);
    }

    /**
     * Generate a class to to custom Serialization/Desirialization for
     * a particular type
     */
    private void writeObjectSerializerForType(AbstractType type)
        throws IOException {
        boolean isInterface =
            ((JavaStructureType) type.getJavaType()).getAllSubclasses() != null;
        // we don't have to do anything if it is not an interface
        if (!isInterface)
            return;

        JavaType javaType = type.getJavaType();
        String className =
            env.getNames().typeInterfaceSerializerClassName(
                servicePackage,
                type);
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

        try {

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_INTERFACE_SERIALIZER);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writeObjectSerializerCode(out, type);
            out.close();
            //            log("wrote file: " + classFile.getPath());
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
        AbstractType type)
        throws IOException {

        log(
            "writing  serializer/deserializer for: "
                + type.getName().getLocalPart());
        String className =
            env.getNames().typeInterfaceSerializerClassName(
                servicePackage,
                type);
        // Write package and import statements...
        writePackage(p, className);
        writeImports(p);
        p.pln();

        writeClassDecl(p, className);
        writeMembers(p, type);
        p.pln();
        writeConstructor(p, className, type);
        p.pln();
        writeInitialize(p, type);
        p.pln();
        writeDoDeserializeMethod(p, type);
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
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.soap.SOAPVersion;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.namespace.QName;");
    }

    private void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        String baseClass = INTERFACE_SERIALIZER_BASE;
        p.plnI(
            "public class "
                + Names.stripQualifier(className)
                + " extends "
                + baseClass
                + " implements Initializable {");
    }

    private void writeMembers(IndentingWriter p, AbstractType type)
        throws IOException {
        Set processedTypes = new HashSet();
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Set subclassSet = new TreeSet(new GeneratorUtil.SubclassComparator());
        subclassSet.addAll(javaStructure.getAllSubclassesSet());
        Iterator iterator = subclassSet.iterator();
        while (iterator != null && iterator.hasNext()) {
            if (type.isSOAPType())
                SOAPEncoding.writeStaticSerializer(
                    p,
                    servicePackage,
                    (SOAPType) ((JavaStructureType) iterator.next()).getOwner(),
                    processedTypes,
                    writerFactory,
                    env.getNames());
            else
                LiteralEncoding.writeStaticSerializer(
                    p,
                    servicePackage,
                    (LiteralType) ((JavaStructureType) iterator.next())
                        .getOwner(),
                    processedTypes,
                    writerFactory,
                    env.getNames());
        }
        SerializerWriter writer;
        if (type.isSOAPType())
            writer =
                new SOAPObjectSerializerWriter(
                    servicePackage,
                    (SOAPType) type,
                    env.getNames());
        else
            writer =
                new LiteralSequenceSerializerWriter(
                    servicePackage,
                    (LiteralType) type,
                    env.getNames());
        p.pln(
            "private CombinedSerializer "
                + writer.serializerMemberName()
                + ";");
    }

    private void writeConstructor(
        IndentingWriter p,
        String className,
        AbstractType type)
        throws IOException {
        if (type.isSOAPType()) {
            p.plnI(
                "public "
                    + Names.stripQualifier(className)
                    + "(QName type, boolean encodeType, "
                    + "boolean isNullable, String encodingStyle) {");
            p.pln("super(type, encodeType, isNullable, encodingStyle);");
            p.pOln("}");
        } else {
            p.plnI(
                "public "
                    + Names.stripQualifier(className)
                    + "(QName type, String encodingStyle, "
                    + "boolean encodeType) {");
            p.pln("super(type, encodeType, true, encodingStyle);");
            p.pOln("}");
        }
    }

    private void writeInitialize(IndentingWriter p, AbstractType type)
        throws IOException {
        p.plnI(
            "public void initialize(InternalTypeMappingRegistry registry) throws Exception {");
        Set processedTypes = new HashSet();
        SerializerWriter writer;
        AbstractType abstractType;
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Set subclassSet =
            new TreeSet(new GeneratorUtil.SubclassComparator(
        /*env.getClassLoader()*/
        ));
        subclassSet.addAll(javaStructure.getAllSubclassesSet());
        Iterator iterator = subclassSet.iterator();
        while (iterator != null && iterator.hasNext()) {
            abstractType =
                (AbstractType) ((JavaStructureType) iterator.next()).getOwner();
            writer = writerFactory.createWriter(servicePackage, abstractType);
            writer.initializeSerializer(
                p,
                env.getNames().getTypeQName(abstractType.getName()),
                "registry");
            p.pln(
                writer.serializerMemberName()
                    + " = "
                    + writer.serializerMemberName()
                    + ".getInnermostSerializer();");
        }
        if (type.isSOAPType())
            writer =
                new SOAPObjectSerializerWriter(
                    servicePackage,
                    (SOAPType) type,
                    env.getNames());
        else
            writer =
                new LiteralSequenceSerializerWriter(
                    servicePackage,
                    (LiteralType) type,
                    env.getNames());
        StringBuffer typeName = new StringBuffer(40);
        typeName.append("type");
        writer.createSerializer(
            p,
            typeName,
            "interfaceSerializer",
            encodeTypes,
            false,
            null);
        p.pln(
            writer.serializerMemberName()
                + " = interfaceSerializer.getInnermostSerializer();");
        p.plnI(
            "if ("
                + writer.serializerMemberName()
                + " instanceof Initializable) {");
        p.pln(
            "((Initializable)"
                + writer.serializerMemberName()
                + ").initialize(registry);");
        p.pOln("}");
        p.pOln("}");
    }

    private void writeDoDeserializeMethod(IndentingWriter p, AbstractType type)
        throws IOException {
        p.plnI("public java.lang.Object doDeserialize(javax.xml.namespace.QName name, XMLReader reader,");
        p.pln("SOAPDeserializationContext context) throws Exception {");
        JavaStructureType javaType = (JavaStructureType) type.getJavaType();
        Set subclassSet = new TreeSet(new GeneratorUtil.SubclassComparator());
        subclassSet.addAll(javaType.getAllSubclassesSet());
        Iterator subclasses = subclassSet.iterator();
        JavaStructureType subclass;
        AbstractType abstractType;
        SerializerWriter writer;
        String serializer;
        p.pln("javax.xml.namespace.QName elementType = getType(reader);");
        int i;
        for (i = 0; subclasses != null && subclasses.hasNext(); i++) {
            subclass = (JavaStructureType) subclasses.next();
            abstractType = (AbstractType) subclass.getOwner();
            writer = writerFactory.createWriter(servicePackage, abstractType);
            serializer = writer.deserializerMemberName();
            if (i > 0)
                p.p(" else ");
            p.plnI(
                "if (elementType != null && elementType.equals("
                    + serializer
                    + ".getXmlType())) {");
            p.pln(
                "return "
                    + serializer
                    + ".deserialize(name, reader, context);");
            p.pO("}");
        }
        if (i > 0)
            p.p(" else ");
        if (type.isSOAPType())
            writer =
                new SOAPObjectSerializerWriter(
                    servicePackage,
                    (SOAPType) type,
                    env.getNames());
        else
            writer =
                new LiteralSequenceSerializerWriter(
                    servicePackage,
                    (LiteralType) type,
                    env.getNames());
        serializer = writer.deserializerMemberName();
        p.plnI(
            "if (elementType == null || elementType.equals("
                + serializer
                + ".getXmlType())) {");
        p.pln(
            "Object obj = "
                + serializer
                + ".deserialize(name, reader, context);");
        /* for now only do this for exceptions, eventually we might need to consume extra elements
         * for value types as well
         */
        if (javaType instanceof JavaException) {
            p.plnI("while (reader.getState() == XMLReader.START) {");
            p.pln("reader.skipElement();");
            p.pln("reader.nextElementContent();");
            p.pOln("}");
        }
        p.pln("return obj;");
        p.pOln("}");
        p.pln(
            "throw new DeserializationException(\"soap.unexpectedElementType\", new Object[] {\"\", elementType.toString()});");
        p.pOln("}");
    }

    private void writeDoSerializeInstanceMethod(
        IndentingWriter p,
        AbstractType type)
        throws IOException {
        p.plnI(
            "public void doSerializeInstance(java.lang.Object obj, javax.xml.namespace.QName name, SerializerCallback callback,");
        p.pln(
            "XMLWriter writer, SOAPSerializationContext context) throws Exception {");
        p.pln(
            type.getJavaType().getName()
                + " instance = ("
                + type.getJavaType().getName()
                + ")obj;");
        p.pln();
        JavaStructureType javaStructure =
            (JavaStructureType) type.getJavaType();
        Set subclassSet =
            new TreeSet(new GeneratorUtil.SubclassComparator(
        /*env.getClassLoader()*/
        ));
        subclassSet.addAll(javaStructure.getAllSubclassesSet());
        Iterator subclasses = subclassSet.iterator();
        Iterator iterator = javaStructure.getMembers();
        SerializerWriter writer;
        String serializer;
        if (subclasses != null) {
            JavaStructureType subclass;
            AbstractType abstractType;
            for (int i = 0; subclasses.hasNext(); i++) {
                subclass = (JavaStructureType) subclasses.next();
                abstractType = (AbstractType) subclass.getOwner();
                writer =
                    writerFactory.createWriter(servicePackage, abstractType);
                serializer = writer.deserializerMemberName();
                if (i > 0)
                    p.p(" else ");
                p.plnI("if (obj instanceof " + subclass.getName() + ") {");
                p.pln(
                    serializer
                        + ".serialize(obj, name, callback, writer, context);");
                p.pO("}");
            }
            p.plnI(" else {");
        }
        if (type.isSOAPType())
            writer =
                new SOAPObjectSerializerWriter(
                    servicePackage,
                    (SOAPType) type,
                    env.getNames());
        else
            writer =
                new LiteralSequenceSerializerWriter(
                    servicePackage,
                    (LiteralType) type,
                    env.getNames());
        serializer = writer.deserializerMemberName();
        p.pln(serializer + ".serialize(obj, name, callback, writer, context);");
        p.pOln("}");
        p.pOln("}");
    }

    private void writeVerifyNameOverrideMethod(
        IndentingWriter p,
        AbstractType type)
        throws IOException {
        p.plnI(
            "protected void verifyName(XMLReader reader, javax.xml.namespace.QName expectedName) throws java.lang.Exception {");
        p.pOln("}"); // method
    }
}
