/*
 * $Id: ArraySerializerWriter.java,v 1.2 2006-04-13 01:29:09 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.generator.SimpleToBoxedUtil;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ArraySerializerWriter
    extends SerializerWriterBase
    implements GeneratorConstants {
    private String serializerMemberName;
    private AbstractType baseElemType;
    private SerializerWriterFactory writerFactory;
    private SOAPType dataType;
    private String basePackage;

    public ArraySerializerWriter(
        String basePackage,
        SOAPType type,
        Names names) {
        super(type, names);
        dataType = type;
        this.basePackage = basePackage;

        String serializerName =
            names.typeObjectArraySerializerClassName(
                basePackage,
                (SOAPType) type);
        serializerMemberName =
            names.getClassMemberName(serializerName, type)
                + ((SOAPArrayType) type).getRank();
        baseElemType = getBaseElementType();
        writerFactory = new SerializerWriterFactoryImpl(names);
    }

    public void createSerializer(
        IndentingWriter p,
        StringBuffer typeName,
        String serName,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        SOAPArrayType type = (SOAPArrayType) this.type;
        String nillable = (type.isNillable() ? NULLABLE_STR : NOT_NULLABLE_STR);
        String referenceable =
            (type.isReferenceable()
                ? REFERENCEABLE_STR
                : NOT_REFERENCEABLE_STR);
        String multiRef =
            (multiRefEncoding
                && type.isReferenceable()
                    ? SERIALIZE_AS_REF_STR
                    : DONT_SERIALIZE_AS_REF_STR);
        String encodeType =
            (encodeTypes ? ENCODE_TYPE_STR : DONT_ENCODE_TYPE_STR);

        declareType(p, typeName, type.getName(), false, false);
        StringBuffer elemName = new StringBuffer("elemName");
        if (type.getElementName() != null) {
            declareType(p, elemName, type.getElementName(), false, false);
        } else {
            elemName = new StringBuffer("null");
        }
        StringBuffer elemType = new StringBuffer("elemType");
        declareType(p, elemType, baseElemType.getName(), false, false);
        if (isSimpleType(baseElemType.getJavaType().getName())
            && !((SOAPType) baseElemType).isReferenceable()) {
            SerializerWriter writer =
                writerFactory.createWriter(basePackage, baseElemType);
            StringBuffer serNameElemType = new StringBuffer(serName + elemType);
            writer.createSerializer(
                p,
                serNameElemType,
                serName + "elemSerializer",
                encodeTypes,
                multiRefEncoding,
                typeMapping);
            p.plnI(
                serializerName()
                    + " "
                    + serName
                    + " = new SimpleTypeArraySerializer("
                    + typeName
                    + ",");
            p.pln(
                encodeType
                    + ", "
                    + nillable
                    + ", "
                    + getEncodingStyleString()
                    + " , ");
            p.pln(
                elemName
                    + ", "
                    + elemType
                    + ", "
                    + baseElemType.getJavaType().getName()
                    + ".class, "
                    + type.getRank()
                    + ", "
                    + type.getSize()
                    + ", (SimpleTypeSerializer)"
                    + serName
                    + "elemSerializer, "
                    + getSOAPVersionString()
                    + ");");
            p.pO();
        } else {
            p.plnI(
                serializerName()
                    + " "
                    + serName
                    + " = new ObjectArraySerializer("
                    + typeName
                    + ",");
            p.pln(
                encodeType
                    + ", "
                    + nillable
                    + ", "
                    + getEncodingStyleString()
                    + " , ");
            p.pln(
                elemName
                    + ", "
                    + elemType
                    + ", "
                    + baseElemType.getJavaType().getName()
                    + ".class, "
                    + type.getRank()
                    + ", "
                    + type.getSize()
                    + ", "
                    + getSOAPVersionString()
                    + ");");
            p.pO();
        }
        if (type.isReferenceable()) {
            p.pln(
                serName
                    + " = new "
                    + REFERENCEABLE_SERIALIZER_NAME
                    + "("
                    + multiRef
                    + ", "
                    + serName
                    + ", "
                    + getSOAPVersionString()
                    + ");");
        }
    }

    public void declareSerializer(
        IndentingWriter p,
        boolean isStatic,
        boolean isFinal)
        throws IOException {
        String modifier = getPrivateModifier(isStatic, isFinal);
        p.pln(modifier + serializerName() + " " + serializerMemberName() + ";");
    }

    public String serializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    public String deserializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }

    public AbstractType getBaseElementType() {
        SOAPType elemType = ((SOAPArrayType) type).getElementType();
        while (elemType instanceof SOAPArrayType) {
            elemType = ((SOAPArrayType) elemType).getElementType();
        }
        return elemType;
    }

    private boolean isSimpleType(String javaName) {
        return SimpleToBoxedUtil.isPrimitive(javaName)
            || boxedSet.contains(javaName);
    }

    private static Set boxedSet = null;

    static {
        boxedSet = new HashSet();
        boxedSet.add("java.lang.Boolean");
        boxedSet.add("java.lang.Byte");
        boxedSet.add("java.lang.Double");
        boxedSet.add("java.lang.Float");
        boxedSet.add("java.lang.Int");
        boxedSet.add("java.lang.Long");
        boxedSet.add("java.lang.Short");
        boxedSet.add("java.lang.String");
        boxedSet.add("javax.xml.namespace.QName");
        boxedSet.add("java.lang.BigDecimal");
        boxedSet.add("java.lang.BigInteger");
        boxedSet.add("java.net.URI");
        boxedSet.add("java.util.Calendar");
        boxedSet.add("java.util.Date");
    }
}
