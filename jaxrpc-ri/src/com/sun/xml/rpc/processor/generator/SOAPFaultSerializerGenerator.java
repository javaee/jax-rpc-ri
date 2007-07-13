/*
 * $Id: SOAPFaultSerializerGenerator.java,v 1.3 2007-07-13 23:36:02 ofung Exp $
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
import java.util.TreeSet;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.HeaderFault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPFaultSerializerGenerator extends GeneratorBase {
    private Port port;
    private Set generatedFaultSerializers;
    private boolean writeFaultSerializerElse;

    public SOAPFaultSerializerGenerator() {
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new SOAPFaultSerializerGenerator(model, config, properties);
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new SOAPFaultSerializerGenerator(model, config, properties);
    }

    private SOAPFaultSerializerGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        super(model, config, properties);
    }

    private SOAPFaultSerializerGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        super(model, config, properties);
    }

    protected void preVisitModel(Model model) throws Exception {
        generatedFaultSerializers = new HashSet();
    }

    protected void postVisitModel(Model model) throws Exception {
        generatedFaultSerializers = null;
    }

    protected void preVisitPort(Port port) throws Exception {
        super.preVisitPort(port);
        this.port = port;
    }

    protected void postVisitPort(Port port) throws Exception {
        this.port = null;
        super.postVisitPort(port);
    }

    protected void postVisitOperation(Operation operation) throws Exception {
        if (needsFaultSerializer(operation)) {
            generateFaultSerializer(operation);
        }
    }

    protected void visitFault(Fault fault) throws Exception {
        AbstractType type = fault.getBlock().getType();
        if (type.isSOAPType()) {
            ((SOAPType) type).accept(this);
        }
        if (type.isLiteralType()) {
            ((LiteralType) type).accept(this);
        }
    }

    private boolean needsFaultSerializer(Operation operation) {
        Iterator faults = operation.getFaults();
        Fault fault;
        boolean needsFaultSerializer = false;
        String className =
            env.getNames().faultSerializerClassName(
                servicePackage,
                port,
                operation);
        if (faults != null && !generatedFaultSerializers.contains(className)) {
            while (!needsFaultSerializer && faults.hasNext()) {
                fault = (Fault) faults.next();
                needsFaultSerializer = true;
            }
            generatedFaultSerializers.add(className);
        }
        return needsFaultSerializer;
    }

    private void generateFaultSerializer(Operation operation) {
        log("generating FaultHandler for: " + operation.getUniqueName());
        try {
            String className =
                env.getNames().faultSerializerClassName(
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

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_SOAP_FAULT_SERIALIZER);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            out.pln();
            writeImports(out);
            out.pln();
            writeClassDecl(out, className);
            writeMembers(out, operation);
            out.pln();
            writeClassConstructor(out, className);
            out.pln();
            writeInitialize(out, operation);
            out.pln();
            writeDeserializeDetail(out, operation);
            out.pln();
            writeSerializeDetail(out, operation);
            out.pOln("}"); // class
            out.close();
        } catch (Exception e) {
            fail(e);
        }
    }
    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAPConstants;");
        p.pln("import com.sun.xml.rpc.encoding.soap.SOAP12Constants;");
        p.pln("import com.sun.xml.rpc.soap.message.SOAPFaultInfo;");
        p.pln("import com.sun.xml.rpc.streaming.*;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.namespace.QName;");
    }

    private void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public class "
                + Names.stripQualifier(className)
                + " extends SOAPFaultInfoSerializer {");
    }

    private void writeMembers(IndentingWriter p, Operation operation)
        throws IOException, GeneratorException {
        Set processedTypes = new HashSet();
        Iterator faults = operation.getFaults();
        Fault fault;
        SerializerWriter writer;
        Set faultNames = new HashSet();
        while (faults.hasNext()) {
            fault = (Fault) faults.next();
            if (!faultNames.contains(fault.getBlock().getName())) {
                GeneratorUtil.writeQNameDeclaration(
                    p,
                    fault.getElementName(),
                    env.getNames());
                faultNames.add(fault.getBlock().getName());
            }
            String suffix = "_Serializer";
            if (fault.getBlock().getType().isSOAPType()) {
                SOAPEncoding.writeStaticSerializer(
                    p,
                    servicePackage,
                    (SOAPType) fault.getBlock().getType(),
                    processedTypes,
                    writerFactory,
                    env.getNames());
            } else { // literal
                LiteralEncoding.writeStaticSerializer(
                    p,
                    servicePackage,
                    (LiteralType) fault.getBlock().getType(),
                    processedTypes,
                    writerFactory,
                    env.getNames());
            }
            writer =
                writerFactory.createWriter(
                    servicePackage,
                    fault.getBlock().getType());
            if (!processedTypes
                .contains(
                    fault.getBlock().getType().getName()
                        + writer.serializerMemberName()
                        + suffix)) {
                p.pln(
                    "private "
                        + BASE_SERIALIZER_NAME
                        + " "
                        + writer.serializerMemberName()
                        + suffix
                        + ";");
                processedTypes.add(
                    fault.getBlock().getType().getName()
                        + writer.serializerMemberName()
                        + suffix);
            }
        }

        faults = operation.getFaults();
        for (int i = 0; faults.hasNext(); i++) {
            fault = (Fault) faults.next();
            p.pln(
                "private static final int "
                    + fault
                        .getJavaException()
                        .getRealName()
                        .toUpperCase()
                        .replace(
                        '.',
                        '_')
                    + "_INDEX = "
                    + i
                    + ";");
        }
    }

    private void writeClassConstructor(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public "
                + Names.stripQualifier(className)
                + "(boolean encodeType, "
                + "boolean isNullable) {");
        p.pln("super(encodeType, isNullable);");
        p.pOln("}");
    }

    private void writeInitialize(IndentingWriter p, Operation operation)
        throws IOException {
        Iterator faults = operation.getFaults();
        Set processedTypes = new HashSet();
        Fault fault;
        AbstractType type;
        p.plnI(
            "public void initialize(InternalTypeMappingRegistry registry) throws java.lang.Exception {");
        p.pln("super.initialize(registry);");
        while (faults.hasNext()) {
            fault = (Fault) faults.next();
            type = fault.getBlock().getType();
            if (processedTypes.contains(type)) {
                continue;
            }
            String suffix = "_Serializer";
            SerializerWriter writer =
                writerFactory.createWriter(servicePackage, type);
            writer.initializeSerializer(
                p,
                env.getNames().getTypeQName(type.getName()),
                "registry");
            p.pln(
                writer.serializerMemberName()
                    + suffix
                    + " = "
                    + writer.serializerMemberName()
                    + ".getInnermostSerializer();");
            processedTypes.add(type);
        }
        p.pOln("}");
    }

    private void writeDeserializeDetail(IndentingWriter p, Operation operation)
        throws IOException {
        Set faultsSet = new TreeSet(new GeneratorUtil.FaultComparator(true));
        faultsSet.addAll(operation.getFaultsSet());
        Iterator faults = faultsSet.iterator();
        String detailNames = "";
        Fault fault;
        p.plnI(
            "protected java.lang.Object deserializeDetail(SOAPDeserializationState state, XMLReader reader,");
        p.pln(
            "SOAPDeserializationContext context, SOAPFaultInfo instance) throws java.lang.Exception {");
        p.pln("boolean isComplete = true;");
        p.pln("javax.xml.namespace.QName elementName;");
        p.pln("javax.xml.namespace.QName elementType = null;");
        p.pln("SOAPInstanceBuilder builder = null;");
        p.pln("java.lang.Object detail = null;");
        p.pln("java.lang.Object obj = null;");
        p.pln();
        p.pln("reader.nextElementContent();");
        p.plnI("if (reader.getState() == XMLReader.END)");
        p.pln("return deserializeDetail(reader, context);");
        p.pO();
        p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.START);");
        p.pln("elementName = reader.getName();");
        p.pln("elementType = getType(reader);");
        String faultName = "";
        String nextName = "";
        Fault nextFault = faults.hasNext() ? (Fault) faults.next() : null;
        // this code assumes faults are sorted on block names first, then type 
        boolean wroteTypeCheck = false;
        boolean startName = false;
        for (int i = 0; nextFault != null; i++) {
            fault = nextFault;
            boolean hasNext = false;
            nextFault = faults.hasNext() ? (Fault) faults.next() : null;
            nextName =
                (nextFault != null)
                    ? env.getNames().getQNameName(nextFault.getElementName())
                    : null;
            if (!faultName
                .equals(env.getNames().getQNameName(fault.getElementName()))) {
                faultName = env.getNames().getQNameName(fault.getElementName());
                boolean writeTypeCheck = false; //(nextName != null) ?
                startName = true;
                if (i > 0) {
                    p.pln();
                    p.pO("} else ");
                }
                p.plnI("if (elementName.equals(" + faultName + ")) {");
                if (writeTypeCheck) {
                    p.plnI("if (elementType != null) {");
                    wroteTypeCheck = true;
                }
            }
            hasNext = (nextName != null) ? faultName.equals(nextName) : false;

            if (!hasNext && wroteTypeCheck) {
                p.pln();
                p.pO("} else "); // elementType != null
                wroteTypeCheck = false;
            } else if (!startName) {
                p.p("else ");
            }
            startName = false;
            writeFaultDeserializer(p, fault, operation, "reader", hasNext);
        }
        p.pln();
        p.pOln("}"); // close if (elementName.equals... 
        writeCatchAllDetailDeserializer(p);

        p.pOln("}");
    }

    private void writeCatchAllDetailDeserializer(IndentingWriter p)
        throws IOException {
        p.pln("return deserializeDetail(reader, context);");
    }

    private void writeSerializeDetail(IndentingWriter p, Operation operation)
        throws IOException {
        Set faultsSet = new TreeSet(new GeneratorUtil.FaultComparator(false));
        faultsSet.addAll(operation.getFaultsSet());
        Iterator faults = faultsSet.iterator();
        String detailNames = "";
        Fault fault;

        p.plnI(
            "protected void serializeDetail(java.lang.Object detail, XMLWriter writer, SOAPSerializationContext context)");
        p.pln("throws java.lang.Exception {");
        p.plnI("if (detail == null) {");
        p.pln("throw new SerializationException(\"soap.unexpectedNull\");");
        p.pOln("}");
        //headerfault, need to optimize the generated code(instead of if(), if(..||..||...)
        for (Iterator iter = faultsSet.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (obj instanceof HeaderFault) {
                HeaderFault headerFault = (HeaderFault) obj;
                String faultExceptionName =
                    env.getNames().customExceptionClassName(headerFault);
                p.plnI("if (detail instanceof " + faultExceptionName + ") {");
                p.pln("return;");
                p.pOln("}");
            }
        }
        p.pln("writer.startElement(DETAIL_QNAME);");
        p.pln();
        p.pln("boolean pushedEncodingStyle = false;");
        p.plnI("if (encodingStyle != null) {");
        p.pln("context.pushEncodingStyle(encodingStyle, writer);");
        p.pOln("}");

        writeFaultSerializerElse = false;
        while (faults.hasNext()) {
            fault = (Fault) faults.next();
            if (!(fault instanceof HeaderFault)) {
                writeFaultSerializer(p, fault, "writer");
                writeFaultSerializerElse = true; // use "else" from now on
            }
        }
        p.pln("writer.endElement();");
        p.plnI("if (pushedEncodingStyle) {");
        p.pln("context.popEncodingStyle();");
        p.pOln("}");
        p.pOln("}");
    }

    private void writeFaultDeserializer(
        IndentingWriter p,
        Fault fault,
        Operation operation,
        String reader,
        boolean hasNext)
        throws IOException {

        String serializer;
        boolean referenceable;
        Block block = fault.getBlock();
        String memberConstName = "0";
        String memberQName =
            env.getNames().getQNameName(fault.getElementName());
        AbstractType type = block.getType();

        SerializerWriter writer =
            writerFactory.createWriter(servicePackage, type);
        serializer = writer.deserializerMemberName();
        referenceable =
            type.isSOAPType() && ((SOAPType) type).isReferenceable();
        String suffix = type.isSOAPType() ? "_Serializer" : "";
        if (!hasNext) {
            if (fault.getSubfaults() == null) {
                p.plnI("if (elementType == null || ");
                p.pln(
                    "(elementType.equals(" + serializer + ".getXmlType()) ||");
                p.pln(
                    "("
                        + writer.serializerMemberName()
                        + suffix
                        + " instanceof ArraySerializerBase &&");
                p.pln(
                    "elementType.equals(SOAPConstants.QNAME_ENCODING_ARRAY)) ) ) {");
            }
        } else {
            p.plnI(
                "if (elementType.equals(" + serializer + ".getXmlType()) ||");
            p.pln(
                "("
                    + writer.serializerMemberName()
                    + suffix
                    + " instanceof ArraySerializerBase &&");
            p.pln(
                "elementType.equals(SOAPConstants.QNAME_ENCODING_ARRAY)) ) {");
        }
        p.pln(
            "obj = "
                + serializer
                + ".deserialize("
                + memberQName
                + ", "
                + reader
                + ", context);");
        // see if we need to handle references
        if (referenceable || type.isLiteralType()) {
            JavaException javaException = fault.getJavaException();
            if (referenceable) {
                p.plnI("if (obj instanceof SOAPDeserializationState) {");
                String index =
                    javaException.getRealName().toUpperCase() + "_INDEX";
                p.pln(
                    "builder = new "
                        + env.getNames().faultBuilderClassName(
                            servicePackage,
                            port,
                            operation)
                        + "();");
                p.plnI("state = registerWithMemberState(instance, state, obj,");
                p.pln(index.replace('.', '_') + ", builder);");
                p.pO();
                p.pln("isComplete = false;");
                p.pOlnI("} else {");
            }
            if ((type instanceof SOAPStructureType
                || type instanceof LiteralStructuredType)
                && SOAPObjectSerializerGenerator.deserializeToDetail(type)) {
                p.pln("detail = (javax.xml.soap.Detail)obj;");
            } else {
                if (!(type instanceof SOAPStructureType
                    || type instanceof LiteralStructuredType)
                    && javaException.getMembersCount() == 1
                    && fault.getSubfaults() == null) {
                    Iterator members =
                        ((JavaStructureType) fault.getJavaException())
                            .getMembers();
                    JavaStructureMember member =
                        (JavaStructureMember) members.next();
                    // bug fix 4967940      
/*                    String valueStr =
                        "(" + member.getType().getRealName() + ")obj";
                    p.pln(
                        "detail = new "
                            + env.getNames().customExceptionClassName(fault)
                            + "("
                            + valueStr
                            + ");");*/
                    String valueStr = null;
                    String javaName = type.getJavaType().getName();

                    if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                        String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
                        valueStr =
                            SimpleToBoxedUtil.getUnboxedExpressionOfType(
                                "(" + boxName + ")obj",
                                javaName);
                    } else {
                        valueStr = "(" + javaName + ")obj";
                    }
                    p.pln(
                        "detail = new "
                            + env.getNames().customExceptionClassName(fault)
                            + "("
                            + valueStr
                            + ");");
                    // end bug fix 4967940                                     
                } else {
                    p.pln("detail = obj;");
                }
            }
            if (referenceable) {
                p.pOln("}"); // member instanceof
            }

            p.pln("reader.nextElementContent();");

            /* skip any additional DetailEntries */
            p.pln("skipRemainingDetailEntries(reader);");
            p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.END);");
            p.pln("return (isComplete ? (Object)detail : (Object)state);");
        } else { // primitive
            String valueStr = null;
            String javaName = type.getJavaType().getName();

            if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                String boxName = SimpleToBoxedUtil.getBoxedClassName(javaName);
                valueStr =
                    SimpleToBoxedUtil.getUnboxedExpressionOfType(
                        "(" + boxName + ")obj",
                        javaName);
            } else {
                valueStr = "(" + javaName + ")obj";
            }
            p.pln(
                "detail = new "
                    + env.getNames().customExceptionClassName(fault)
                    + "("
                    + valueStr
                    + ");");
            p.pln("reader.nextElementContent();");

            p.pln("XMLReaderUtil.verifyReaderState(reader, XMLReader.END);");
            p.pln("return detail;");
        }
        if (hasNext || fault.getSubfaults() == null)
            p.pO("} ");
    }

    private static boolean deserializeToDetail(SOAPStructureType type) {
        boolean detail =
            SOAPObjectSerializerGenerator.deserializeToDetail(type);
        return detail;
    }

    private void writeFaultSerializer(
        IndentingWriter p,
        Fault fault,
        String writer)
        throws IOException {

        String serializer;
        Block block = fault.getBlock();
        String memberQName =
            env.getNames().getQNameName(fault.getElementName());
        AbstractType type = block.getType();
        String faultExceptionName =
            env.getNames().customExceptionClassName(fault);
        if (writeFaultSerializerElse) {
            p.p("else ");
        }
        p.plnI("if (detail instanceof " + faultExceptionName + ") {");
        SerializerWriter sWriter =
            writerFactory.createWriter(servicePackage, type);
        serializer = sWriter.deserializerMemberName() + "_Serializer";
        String detailStr = "detail";
        String javaName;
        JavaException exception = fault.getJavaException();
        if (!(type instanceof SOAPStructureType
            || type instanceof LiteralStructuredType)
            && exception.getMembersCount() == 1
            && fault.getSubfaults() == null) {
            // Just serialize the exceptions single property not the entire structure
            Iterator members = exception.getMembers();
            JavaStructureMember javaMember =
                (JavaStructureMember) members.next();
            detailStr =
                "(("
                    + faultExceptionName
                    + ")"
                    + detailStr
                    + ")."
                    + javaMember.getReadMethod()
                    + "()";
            javaName = javaMember.getType().getName();
            if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                detailStr =
                    SimpleToBoxedUtil.getBoxedExpressionOfType(
                        detailStr,
                        javaName);
            }
        }
        p.pln(
            serializer
                + ".serialize("
                + detailStr
                + ", "
                + memberQName
                + ", null, "
                + writer
                + ", context);");
        p.pOln("}");
    }
}
