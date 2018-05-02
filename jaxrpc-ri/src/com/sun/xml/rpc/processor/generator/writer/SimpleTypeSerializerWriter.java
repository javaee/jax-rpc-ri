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

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.GeneratorException;
import com.sun.xml.rpc.processor.generator.GeneratorUtil;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPListType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SimpleTypeSerializerWriter
    extends SerializerWriterBase
    implements GeneratorConstants {
    private String encoder = null;
    private String serializerMemberName;
    private SOAPType dataType;

    public SimpleTypeSerializerWriter(SOAPType type, Names names) {
        super(type, names);
        encoder = getTypeEncoder(type);
        dataType = type;

        if (encoder == null) {
            throw new GeneratorException(
                "generator.simpleTypeSerializerWriter.no.encoder.for.type",
                new Object[] {
                    type.getName().toString(),
                    type.getJavaType().getName()});
        }
        String partialSerializerName =
            encoder.substring(0, encoder.lastIndexOf("Encoder"));
        if (partialSerializerName.startsWith("XSD"))
            partialSerializerName = partialSerializerName.substring(3);
        String serializerName = partialSerializerName + "_Serializer";
        serializerMemberName =
            names.getClassMemberName(
                partialSerializerName,
                type,
                "_Serializer");
    }

    private QName getQNameTypeString() {
        if (this.soapVer.equals(SOAPVersion.SOAP_12.toString()))
            return SOAP12Constants.QNAME_TYPE_STRING;
        else
            return SOAPConstants.QNAME_TYPE_STRING;
    }

    private QName getQNameTypeBase64Binary() {
        if (this.soapVer.equals(SOAPVersion.SOAP_12.toString()))
            return SOAP12Constants.QNAME_TYPE_BASE64_BINARY;
        else
            return SOAPConstants.QNAME_TYPE_BASE64_BINARY;
    }

    private QName getQNameTypeBase64() {
        if (this.soapVer.equals(SOAPVersion.SOAP_12.toString()))
            return SOAP12Constants.QNAME_TYPE_BASE64;
        else
            return SOAPConstants.QNAME_TYPE_BASE64;
    }

    public void createSerializer(
        IndentingWriter p,
        StringBuffer typeName,
        String serName,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        SOAPType type = (SOAPType) this.type;
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
        QName typeQName = type.getName();
        // check for attachment types
        if (attachmentTypes.contains(typeQName)) {
            boolean serAsAttachment =
                !(typeQName.equals(getQNameTypeString())
                    || typeQName.equals(BuiltInTypes.STRING));
            p.plnI(
                serializerName()
                    + " "
                    + serName
                    + " = new "
                    + ATTACHMENT_SERIALIZER_NAME
                    + "("
                    + typeName
                    + ",");
            p.pln(
                encodeType
                    + ", "
                    + nillable
                    + ", "
                    + getEncodingStyleString()
                    + ", "
                    + serAsAttachment
                    + ", "
                    + encoder
                    + ".getInstance(),"
                    + getSOAPVersionString()
                    + ");");
            multiRef = DONT_SERIALIZE_AS_REF_STR;
        } else if (
            typeQName.equals(BuiltInTypes.BASE64_BINARY)
                || typeQName.equals(getQNameTypeBase64Binary())
                || typeQName.equals(getQNameTypeBase64())) {
            p.plnI(
                serializerName()
                    + " "
                    + serName
                    + " = new "
                    + SIMPLE_MULTI_TYPE_SERIALIZER_NAME
                    + "("
                    + typeName
                    + ",");
            p.pln(
                encodeType
                    + ", "
                    + nillable
                    + ", "
                    + getEncodingStyleString()
                    + ", "
                    + encoder
                    + ".getInstance(),");
            p.plnI("new QName[] {");
            GeneratorUtil.writeNewQName(p, BuiltInTypes.BASE64_BINARY);
            p.pln(",");
            GeneratorUtil.writeNewQName(p, getQNameTypeBase64Binary());
            p.pln(",");
            GeneratorUtil.writeNewQName(p, getQNameTypeBase64());
            p.pOln("});");
        } else {
            p.plnI(
                serializerName()
                    + " "
                    + serName
                    + " = new "
                    + SIMPLE_TYPE_SERIALIZER_NAME
                    + "("
                    + typeName
                    + ",");
            if (type instanceof SOAPListType) {
                p.pln(
                    encodeType
                        + ", "
                        + nillable
                        + ", "
                        + getEncodingStyleString()
                        + ", "
                        + encoder
                        + ".getInstance("
                        + getItemType()
                        + "));");
            } else
                p.pln(
                    encodeType
                        + ", "
                        + nillable
                        + ", "
                        + getEncodingStyleString()
                        + ", "
                        + encoder
                        + ".getInstance());");
        }
        p.pO();
        if (type.isReferenceable()) {
            p.plnI(
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
            p.pO();
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

    public static String getTypeEncoder(AbstractType type) {
        QName name = type.getName();
        String encoder = null;
        if (type instanceof SOAPListType) {
            encoder = XSD_LIST_TYPE_ENCODER_NAME;
        } else {
            encoder = (String) encoderMap.get(name);
        }
        // DateTime maps to 2 different encoders based on the JavaType
        if (encoder == null) {
            String javaName = type.getJavaType().getName();
            if ((name.equals(BuiltInTypes.DATE_TIME)
                || name.equals(SOAP12Constants.QNAME_TYPE_DATE_TIME)
                || name.equals(SOAPConstants.QNAME_TYPE_DATE_TIME))) {
                if (javaName.equals(DATE_CLASSNAME)) {
                    encoder = XSD_DATE_TIME_DATE_ENCODER_NAME;
                } else if (javaName.equals(CALENDAR_CLASSNAME)) {
                    encoder = XSD_DATE_TIME_CALENDAR_ENCODER_NAME;
                }
            } else if (
                name.equals(BuiltInTypes.BASE64_BINARY)
                    || name.equals(SOAPConstants.QNAME_TYPE_BASE64_BINARY)
                    || name.equals(SOAP12Constants.QNAME_TYPE_BASE64_BINARY)
                    || name.equals(SOAPConstants.QNAME_TYPE_BASE64)
                    || name.equals(SOAP12Constants.QNAME_TYPE_BASE64)) {
                if (javaName.equals(BYTE_ARRAY_CLASSNAME)) {
                    encoder = XSD_BASE64_BINARY_ENCODER_NAME;
                }
            } else if (
                name.equals(BuiltInTypes.HEX_BINARY)
                    || name.equals(SOAP12Constants.QNAME_TYPE_HEX_BINARY)
                    || name.equals(SOAPConstants.QNAME_TYPE_HEX_BINARY)) {
                if (javaName.equals(BYTE_ARRAY_CLASSNAME)) {
                    encoder = XSD_HEX_BINARY_ENCODER_NAME;
                }
            }
        }
        return encoder;
    }

    public static String getTypeEncoder(QName typeName) {
        return (String) encoderMap.get(typeName);
    }

    protected String getEncoder() {
        return getTypeEncoder(type);
    }

    protected String getItemType() {
        String strType = null;
        SOAPListType lt = (SOAPListType) this.type;

        //handle enumeration simple type specially (its encoder is generated)
        if (lt.getItemType() instanceof SOAPEnumerationType) {
            // Adding "_Encoder" like its being added in LiteralEnumerationSerializerWriter.java, 
            // TODO: need to generalize this name generation.
            //bug fix: 4906014
            strType =
                new String(
                    lt.getItemType().getJavaType().getName()
                        + "_Encoder.getInstance(), "
                        + lt.getItemType().getJavaType().getName()
                        + ".class");
        } else {
            //bug fix: 4906014
            strType =
                getTypeEncoder(lt.getItemType())
                    + ".getInstance(), "
                    + lt.getItemType().getJavaType().getName()
                    + ".class";
        }

        if (strType == null) {
            throw new GeneratorException(
                "generator.simpleTypeSerializerWriter.invalidType",
                new Object[] {
                    lt.getItemType().getName().toString(),
                    lt.getItemType().getJavaType().getName()});
        }
        return strType;
    }

    private static Map encoderMap = null;
    private static Set attachmentTypes = null;

    static {
        attachmentTypes = new HashSet();
        attachmentTypes.add(QNAME_TYPE_IMAGE);
        attachmentTypes.add(QNAME_TYPE_MIME_MULTIPART);
        attachmentTypes.add(QNAME_TYPE_SOURCE);
        attachmentTypes.add(QNAME_TYPE_DATA_HANDLER);
        attachmentTypes.add(BuiltInTypes.STRING);
        attachmentTypes.add(SOAPConstants.QNAME_TYPE_STRING);
        attachmentTypes.add(SOAP12Constants.QNAME_TYPE_STRING);

        encoderMap = new HashMap();
        // Attachments
        encoderMap.put(QNAME_TYPE_IMAGE, IMAGE_ENCODER_NAME);
        encoderMap.put(QNAME_TYPE_MIME_MULTIPART, MIME_MULTIPART_ENCODER_NAME);
        encoderMap.put(QNAME_TYPE_SOURCE, SOURCE_ENCODER_NAME);
        encoderMap.put(QNAME_TYPE_DATA_HANDLER, DATA_HANDLER_ENCODER_NAME);

        // XSD
        encoderMap.put(BuiltInTypes.BOOLEAN, XSD_BOOLEAN_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.BYTE, XSD_BYTE_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.BASE64_BINARY, null); // two mappings
        encoderMap.put(BuiltInTypes.HEX_BINARY, null); // two mappings
        encoderMap.put(BuiltInTypes.DOUBLE, XSD_DOUBLE_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.FLOAT, XSD_FLOAT_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.INT, XSD_INT_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.INTEGER, XSD_INTEGER_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.LONG, XSD_LONG_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.SHORT, XSD_SHORT_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.DECIMAL, XSD_DECIMAL_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.DATE_TIME, null);
        // two mappings Date, Calendar
        encoderMap.put(BuiltInTypes.STRING, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.QNAME, XSD_QNAME_ENCODER_NAME);
        // New Types 12/3/02
        encoderMap.put(BuiltInTypes.LANGUAGE, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.NORMALIZED_STRING, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.TOKEN, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.NMTOKEN, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.NAME, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.ID, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.NCNAME, XSD_STRING_ENCODER_NAME);
        // New Types 12/4/02, vivek
        encoderMap.put(
            BuiltInTypes.POSITIVE_INTEGER,
            XSD_POSITIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            BuiltInTypes.NON_POSITIVE_INTEGER,
            XSD_NON_POSITIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            BuiltInTypes.NEGATIVE_INTEGER,
            XSD_NEGATIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            BuiltInTypes.NON_NEGATIVE_INTEGER,
            XSD_NON_NEGATIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            BuiltInTypes.UNSIGNED_LONG,
            XSD_UNSIGNED_LONG_ENCODER_NAME);
        encoderMap.put(
            BuiltInTypes.UNSIGNED_INT,
            XSD_UNSIGNED_INT_ENCODER_NAME);
        encoderMap.put(
            BuiltInTypes.UNSIGNED_SHORT,
            XSD_UNSIGNED_SHORT_ENCODER_NAME);
        encoderMap.put(
            BuiltInTypes.UNSIGNED_BYTE,
            XSD_UNSIGNED_BYTE_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.DURATION, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.TIME, XSD_TIME_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.DATE, XSD_DATE_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.G_YEAR_MONTH, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.G_YEAR, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.G_MONTH_DAY, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.G_DAY, XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.G_MONTH, XSD_STRING_ENCODER_NAME);
        //for jdk < 1.4 map xsd:anyURI to String, otherwise to java.net.URI
        if (!VersionUtil.isJavaVersionGreaterThan1_3())
            encoderMap.put(BuiltInTypes.ANY_URI, XSD_STRING_ENCODER_NAME);
        else
            encoderMap.put(BuiltInTypes.ANY_URI, XSD_ANY_URI_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.IDREF, XSD_STRING_ENCODER_NAME);
        //bug fix: 4863162
        encoderMap.put(BuiltInTypes.IDREFS, XSD_LIST_TYPE_ENCODER_NAME);
        //java.util.List       
        encoderMap.put(BuiltInTypes.NMTOKENS, XSD_LIST_TYPE_ENCODER_NAME);
        //java.util.List

        //SOAP_ENC
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_BOOLEAN,
            XSD_BOOLEAN_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_BYTE, XSD_BYTE_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_BASE64_BINARY, null);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_DOUBLE,
            XSD_DOUBLE_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_FLOAT, XSD_FLOAT_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_INT, XSD_INT_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_LONG, XSD_LONG_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_SHORT, XSD_SHORT_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_DECIMAL,
            XSD_DECIMAL_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_DATE_TIME, null);
        // two mappings Date, Calendar
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_STRING,
            XSD_STRING_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_QNAME, XSD_QNAME_ENCODER_NAME);

        //SOAP_ENC
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_BOOLEAN,
            XSD_BOOLEAN_ENCODER_NAME);
        encoderMap.put(SOAP12Constants.QNAME_TYPE_BYTE, XSD_BYTE_ENCODER_NAME);
        encoderMap.put(SOAP12Constants.QNAME_TYPE_BASE64_BINARY, null);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_DOUBLE,
            XSD_DOUBLE_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_FLOAT,
            XSD_FLOAT_ENCODER_NAME);
        encoderMap.put(SOAP12Constants.QNAME_TYPE_INT, XSD_INT_ENCODER_NAME);
        encoderMap.put(SOAP12Constants.QNAME_TYPE_LONG, XSD_LONG_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_SHORT,
            XSD_SHORT_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_DECIMAL,
            XSD_DECIMAL_ENCODER_NAME);
        encoderMap.put(SOAP12Constants.QNAME_TYPE_DATE_TIME, null);
        // two mappings Date, Calendar
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_STRING,
            XSD_STRING_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_QNAME,
            XSD_QNAME_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_DATE,
            XSD_DATE_ENCODER_NAME);
        
        // bug fix: 4925400
        encoderMap.put(BuiltInTypes.ANY_SIMPLE_URTYPE, XSD_STRING_ENCODER_NAME);
    }
}
