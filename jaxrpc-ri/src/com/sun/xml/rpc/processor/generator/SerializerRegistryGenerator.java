/*
 * $Id: SerializerRegistryGenerator.java,v 1.1 2006-04-12 20:33:46 kohlert Exp $
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

import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import com.sun.xml.rpc.client.BasicService;
import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.writer.CollectionSerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.DynamicSerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.LiteralSimpleSerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SimpleTypeSerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPListType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.GeneratedFileInfo;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPEncodingConstants;
import com.sun.xml.rpc.soap.SOAPNamespaceConstants;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.soap.SOAPWSDLConstants;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SerializerRegistryGenerator extends GeneratorBase {
    private static final String SERIALIZER_FACTORY = "serializerFactory";
    private static final String DESERIALIZER_FACTORY = "deserializerFactory";
    private static final String MAPPING = "mapping";
    private static final String LITERAL_MAPPING = "mapping2";

    private SOAPVersion soapVer = SOAPVersion.SOAP_11;
    private SOAPEncodingConstants soapEncodingConstants = null;
    private SOAPNamespaceConstants soapNamespaceConstants = null;
    private SOAPWSDLConstants soapWSDLConstants = null;

    // indicator that we have custom types
    private boolean haveCustom = false;

    // model reference
    private Model model;

    // vector of customized serializers/deserializers
    private Set visitedTypes;

    public SerializerRegistryGenerator() {
        this(SOAPVersion.SOAP_11);
    }

    public SerializerRegistryGenerator(SOAPVersion ver) {
        super();
        init(ver);
    }

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
        soapNamespaceConstants =
            SOAPConstantsFactory.getSOAPNamespaceConstants(ver);
        soapWSDLConstants = SOAPConstantsFactory.getSOAPWSDLConstants(ver);
        this.soapVer = ver;
    }

    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        return new SerializerRegistryGenerator(model, config, properties);
    }
    public GeneratorBase getGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        return new SerializerRegistryGenerator(model, config, properties);
    }

    private SerializerRegistryGenerator(
        Model model,
        Configuration config,
        Properties properties) {
        this(model, config, properties, SOAPVersion.SOAP_11);
    }

    private SerializerRegistryGenerator(
        Model model,
        Configuration config,
        Properties properties,
        SOAPVersion ver) {
        super(model, config, properties);
        init(ver);
    }

    protected void preVisitModel(Model model) throws Exception {
        this.model = model;
    }

    protected void preVisitService(Service service) throws Exception {
        super.preVisitService(service);
        visitedTypes = new HashSet();
        Iterator types = model.getExtraTypes();
        AbstractType type;
        while (types.hasNext()) {
            type = (AbstractType) types.next();
            if (type.isSOAPType())
                 ((SOAPType) type).accept(this);
            else
                 ((LiteralType) type).accept(this);
        }
    }

    protected void postVisitService(Service service) throws Exception {
        try {
            generateSerializerRegistry(service);
        } catch (IOException e) {
            fail("generator.cant.write", service.getName().getLocalPart());
        }
        visitedTypes = null;
        servicePackage = null;
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

    public void visit(Fault fault) throws Exception {
        if (fault.getBlock().getType().isSOAPType()) {
            ((SOAPType) fault.getBlock().getType()).accept(this);
        } else if (fault.getBlock().getType().isLiteralType()) {
            ((LiteralType) fault.getBlock().getType()).accept(this);
        }
    }

    // SOAPType Visits
    public void visit(SOAPCustomType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
        haveCustom = true;
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    public void visit(SOAPAnyType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
        haveCustom = true;
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    public void preVisitSOAPArrayType(SOAPArrayType type) throws Exception {
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
    }

    // LiteralType Visits
    public void visit(LiteralSimpleType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    public void visit(LiteralFragmentType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    public void preVisitLiteralArrayWrapperType(LiteralArrayWrapperType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    public void preVisitLiteralSequenceType(LiteralSequenceType type)
        throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    public void preVisitLiteralAllType(LiteralAllType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    public void visit(LiteralEnumerationType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    //xsd:list
    public void visit(LiteralListType type) throws Exception {
        if (haveVisited(type)) {
            return;
        }
        typeVisited(type);
    }

    //xsd:list, rpc/enc
    public void visit(SOAPListType type) throws Exception {
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

    private void generateSerializerRegistry(Service service)
        throws IOException {
        try {
            JavaInterface intf = (JavaInterface) service.getJavaInterface();
            String className = env.getNames().serializerRegistryClassName(intf);
            if ((donotOverride && GeneratorUtil.classExists(env, className))) {
                log("Class " + className + " exists. Not overriding.");
                return;
            }
            log("creating serializer registry: " + className);
            File classFile =
                env.getNames().sourceFileForClass(
                    className,
                    className,
                    sourceDir,
                    env);

            /* adding the file name and its type */
            GeneratedFileInfo fi = new GeneratedFileInfo();
            fi.setFile(classFile);
            fi.setType(GeneratorConstants.FILE_TYPE_SERIALIZER_REGISTRY);
            env.addGeneratedFile(fi);

            IndentingWriter out =
                new IndentingWriter(
                    new OutputStreamWriter(new FileOutputStream(classFile)));
            writePackage(out, className);
            writeImports(out);
            out.pln();
            writeClassDecl(out, className);
            writeConstructor(out, className);
            out.pln();
            writeGetRegistry(out);
            out.pln();
            writeStatics(out);
            out.pOln("}");
            out.close();
        } catch (Exception e) {
            throw new GeneratorException(
                "generator.nestedGeneratorError",
                new LocalizableExceptionAdapter(e));
        }
    }

    private void writeImports(IndentingWriter p) throws IOException {
        p.pln("import com.sun.xml.rpc.client.BasicService;");
        p.pln("import com.sun.xml.rpc.encoding.*;");
        p.pln("import com.sun.xml.rpc.encoding.simpletype.*;");
        p.pln("import com.sun.xml.rpc.encoding.soap.*;");
        p.pln("import com.sun.xml.rpc.encoding.literal.*;");
        p.pln("import com.sun.xml.rpc.soap.SOAPVersion;");
        p.pln("import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;");
        p.pln("import javax.xml.rpc.*;");
        p.pln("import javax.xml.rpc.encoding.*;");
        p.pln("import javax.xml.namespace.QName;");
    }

    private void writeClassDecl(IndentingWriter p, String className)
        throws IOException {
        p.plnI(
            "public class "
                + Names.stripQualifier(className)
                + " implements SerializerConstants {");
    }

    private void writeStatics(IndentingWriter p)
        throws IOException, Exception {
        p.plnI(
            "private static void registerSerializer(TypeMapping mapping, java.lang.Class javaType, javax.xml.namespace.QName xmlType,");
        p.pln("Serializer ser) {");
        p.plnI(
            "mapping.register(javaType, xmlType, new SingletonSerializerFactory(ser),");
        p.pln("new SingletonDeserializerFactory((Deserializer)ser));");
        p.pO();
        p.pOln("}");
        p.pln();
    }

    private boolean mappingExistsForType(
        TypeMapping mapping,
        AbstractType type) {
        try {
            Class cls = null;
            String javaName = type.getJavaType().getName();
            if (SimpleToBoxedUtil.isPrimitive(javaName)) {
                if (javaName.equals(boolean.class.toString())) {
                    cls = boolean.class;
                } else if (javaName.equals(BYTE_CLASSNAME)) {
                    cls = byte.class;
                } else if (javaName.equals(DOUBLE_CLASSNAME)) {
                    cls = double.class;
                } else if (javaName.equals(INT_CLASSNAME)) {
                    cls = int.class;
                } else if (javaName.equals(FLOAT_CLASSNAME)) {
                    cls = float.class;
                } else if (javaName.equals(LONG_CLASSNAME)) {
                    cls = long.class;
                } else if (javaName.equals(SHORT_CLASSNAME)) {
                    cls = short.class;
                }
            }
            if (cls == null) {
                if (javaName.equals(BYTE_ARRAY_CLASSNAME)) {
                    cls = byte[].class;
                    //bug fix: 4863162
                }
                if (javaName.equals(STRING_ARRAY_CLASSNAME)) {
                    cls = String[].class;
                } else {
                    cls = Class.forName(javaName);
                }
            }
            SerializerFactory factory =
                mapping.getSerializer(cls, type.getName());
            if (factory != null) {
                return true;
            }
            // TODO fix this, is should just be sufficient to see if we got a factory back
            // however, do to the current implementation of the
            // BasicService.createStandardTypeMappingRegistry();, if a real registry entry is
            // not found and ValueTypeSerializer is return sometimes returned.

            //Serializer ser = factory.getSerializerAs(EncodingConstants.JAX_RPC_RI_MECHANISM);
            //if (!(ser instanceof CombinedSerializer) ||
            //!(((CombinedSerializer)ser).getInnermostSerializer() instanceof ValueTypeSerializer)) {
            //    return true;
            //}
        } catch (Exception e) {
        }
        return false;
    }

    private void writeConstructor(IndentingWriter p, String className)
        throws IOException {
        p.plnI("public " + Names.stripQualifier(className) + "() {");
        p.pOln("}");
    }

    private void writeGetRegistry(IndentingWriter p)
        throws IOException, ClassNotFoundException {
        p.plnI("public TypeMappingRegistry getRegistry() {");

        Set processedTypes = new HashSet();
        Iterator types;
        AbstractType type;
        p.pln();
        p.pln(
            "TypeMappingRegistry registry = BasicService.createStandardTypeMappingRegistry();");
        p.pln(
            "TypeMapping mapping12 = registry.getTypeMapping(SOAP12Constants.NS_SOAP_ENCODING);");
        p.pln(
            "TypeMapping mapping = registry.getTypeMapping(SOAPConstants.NS_SOAP_ENCODING);");
        p.pln("TypeMapping mapping2 = registry.getTypeMapping(\"\");");
        TypeMappingRegistry registry =
            BasicService.createStandardTypeMappingRegistry();
        TypeMapping mapping =
            registry.getTypeMapping(SOAPConstants.NS_SOAP_ENCODING);

        /* for the literal case */
        TypeMapping literalMapping = registry.getTypeMapping("");

        //kw comment out if it doesn't work
        TypeMapping mapping12 =
            registry.getTypeMapping(SOAP12Constants.NS_SOAP_ENCODING);
        types = visitedTypes.iterator();
        while (types.hasNext()) {
            type = (AbstractType) types.next();
            if (type.getJavaType().getName().equals(VOID_CLASSNAME)) {
                continue;
            }
            String key = genKey(type);
            if (processedTypes.contains(key)) {
                continue;
            }
            processedTypes.add(key);
            SerializerWriter writer =
                writerFactory.createWriter(servicePackage, type);
            if (writer instanceof SimpleTypeSerializerWriter
                || writer instanceof CollectionSerializerWriter
                || writer instanceof LiteralSimpleSerializerWriter
                || writer instanceof DynamicSerializerWriter) {
                // all of the simple types should already be registered, but check to be sure
                if (mappingExistsForType(literalMapping, type)
                    && type.isLiteralType()) {
                    /* this has been specifically added in to make sure that if the 
                       mappings exist in the soap encoding .. then it does not 
                       become an issue for literals. */
                    continue;
                } else if (
                    (mappingExistsForType(mapping, type)
                        || mappingExistsForType(mapping12, type))
                        && type.isSOAPType()) {
                    continue;
                }
                /* commented out as when the serializer is not found the code for it is
                   generated */
                //warn("generator.serializerRegistryGenerator.warning.no.standard.simpletype.serialzer",
                //new Object[] {type.getName().toString(), type.getJavaType().getRealName()});
            }
            p.plnI("{");
            if (type.isSOAPType()) {
                //TODo:kw need to check to see if this is the current soap version working on
                if (type.getVersion().equals(SOAPVersion.SOAP_12.toString()))
                    writer.registerSerializer(
                        p,
                        encodeTypes,
                        multiRefEncoding,
                        "mapping12");
                else
                    writer.registerSerializer(
                        p,
                        encodeTypes,
                        multiRefEncoding,
                        "mapping");
            } else if (type.isLiteralType()) {
                writer.registerSerializer(
                    p,
                    encodeTypes,
                    multiRefEncoding,
                    "mapping2");
            }
            p.pOln("}");
        }

        p.pln("return registry;");
        p.pOln("}");
    }

    protected static String genKey(AbstractType type) {
        String schemaType = type.getName().toString();
        String javaType = type.getJavaType().getName();
        String typeType;
        if ((type instanceof LiteralListType)
            || (type instanceof SOAPListType))
            typeType = type.toString();
        else
            typeType = type.getClass().getName();
        return schemaType + ";" + javaType + ";" + typeType;
    }
}
