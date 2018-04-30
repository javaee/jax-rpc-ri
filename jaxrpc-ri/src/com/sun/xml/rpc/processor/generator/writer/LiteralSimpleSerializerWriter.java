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

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.GeneratorException;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralSimpleSerializerWriter
    extends LiteralSerializerWriterBase
    implements GeneratorConstants {
    private String encoder = null;
    private String serializerMemberName;
    private LiteralType dataType;

    public LiteralSimpleSerializerWriter(LiteralType type, Names names) {
        super(type, names);
        dataType = type;
        encoder = getTypeEncoder(type);
        if (encoder == null) {
            throw new GeneratorException(
                "generator.simpleTypeSerializerWriter.no.encoder.for.type",
                new Object[] {
                    type.getName().toString(),
                    type.getJavaType().getName()});
        }
        String partialSerializerName =
            encoder.substring(3, encoder.lastIndexOf("Encoder"));
        String serializerName = partialSerializerName + "_Serializer";
        serializerMemberName =
            names.getClassMemberName(
                partialSerializerName,
                type,
                "_Serializer");
    }

    public void createSerializer(
        IndentingWriter p,
        StringBuffer typeName,
        String serName,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        LiteralType type = (LiteralType) this.type;
        declareType(p, typeName, type.getName(), false, false);
        QName typeQName = type.getName();
        p.plnI(
            serializerName()
                + " "
                + serName
                + " = new "
                + LITERAL_SIMPLE_TYPE_SERIALIZER_NAME
                + "("
                + typeName
                + ",");
        if (type instanceof LiteralListType) {
            p.pln("\"\", " + encoder + ".getInstance(" + getItemType() + "));");
        } else
            p.pln("\"\", " + encoder + ".getInstance());");
        p.pO();
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
        if (type instanceof LiteralListType) {
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
                    || name.equals(SOAPConstants.QNAME_TYPE_HEX_BINARY)
                    || name.equals(SOAP12Constants.QNAME_TYPE_HEX_BINARY)) {
                if (javaName.equals(BYTE_ARRAY_CLASSNAME)) {
                    encoder = XSD_HEX_BINARY_ENCODER_NAME;
                }
            } else if (javaName.equals(STRING_CLASSNAME)) {
                encoder = XSD_STRING_ENCODER_NAME;
            } else if (name.equals(BuiltInTypes.IDREF)) {
                //xsd:IDREF - to support String and Object mapping. In case of 
                //Object mapping still the encoder is String one
                encoder = XSD_STRING_ENCODER_NAME;
            }
        }
        return encoder;
    }

    protected String getEncoder() {
        return getTypeEncoder(type);
    }

    protected String getItemType() {
        String strType = null;
        LiteralListType lt = (LiteralListType) this.type;

        //handle enumeration simple type specially (its encoder is generated)
        if (lt.getItemType() instanceof LiteralEnumerationType) {
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

    static {
        encoderMap = new HashMap();
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

        //new encoders, vivekp
        encoderMap.put(BuiltInTypes.TIME, XSD_TIME_ENCODER_NAME);
        //bug fix: 4863162
        encoderMap.put(BuiltInTypes.NMTOKENS, XSD_LIST_TYPE_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.IDREFS, XSD_LIST_TYPE_ENCODER_NAME);
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
        encoderMap.put(BuiltInTypes.DATE, XSD_DATE_ENCODER_NAME);
        //xsd:list
        encoderMap.put(BuiltInTypes.LIST, XSD_LIST_TYPE_ENCODER_NAME);

        // bug fix: 4925400
        encoderMap.put(BuiltInTypes.ANY_SIMPLE_URTYPE, XSD_STRING_ENCODER_NAME);

        //for jdk < 1.4 map xsd:anyURI to String, otherwise to java.net.URI
        if (!VersionUtil.isJavaVersionGreaterThan1_3())
            encoderMap.put(BuiltInTypes.ANY_URI, XSD_STRING_ENCODER_NAME);
        else
            encoderMap.put(BuiltInTypes.ANY_URI, XSD_ANY_URI_ENCODER_NAME);

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

        //new encoders
        encoderMap.put(SOAPConstants.QNAME_TYPE_TIME, XSD_TIME_ENCODER_NAME);
        //bug fix: 4863162
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_NMTOKENS,
            XSD_LIST_TYPE_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_IDREFS,
            XSD_LIST_TYPE_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_POSITIVE_INTEGER,
            XSD_POSITIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_NON_POSITIVE_INTEGER,
            XSD_NON_POSITIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_NEGATIVE_INTEGER,
            XSD_NEGATIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER,
            XSD_NON_NEGATIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_UNSIGNED_LONG,
            XSD_UNSIGNED_LONG_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_UNSIGNED_INT,
            XSD_UNSIGNED_INT_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_UNSIGNED_SHORT,
            XSD_UNSIGNED_SHORT_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_UNSIGNED_BYTE,
            XSD_UNSIGNED_BYTE_ENCODER_NAME);
        encoderMap.put(
            SOAPConstants.QNAME_TYPE_DATE,
            XSD_DATE_ENCODER_NAME);

        //for jdk < 1.4 map xsd:anyURI to String, otherwise to java.net.URI
        if (!VersionUtil.isJavaVersionGreaterThan1_3())
            encoderMap.put(
                SOAPConstants.QNAME_TYPE_ANY_URI,
                XSD_STRING_ENCODER_NAME);
        else
            encoderMap.put(
                SOAPConstants.QNAME_TYPE_ANY_URI,
                XSD_ANY_URI_ENCODER_NAME);

        //SOAP_ENC 12
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

        //new encoders        
        encoderMap.put(SOAP12Constants.QNAME_TYPE_TIME, XSD_TIME_ENCODER_NAME);
        //bug fix: 4863162
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_NMTOKENS,
            XSD_LIST_TYPE_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_IDREFS,
            XSD_LIST_TYPE_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_POSITIVE_INTEGER,
            XSD_POSITIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_NON_POSITIVE_INTEGER,
            XSD_NON_POSITIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_NEGATIVE_INTEGER,
            XSD_NEGATIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_NON_NEGATIVE_INTEGER,
            XSD_NON_NEGATIVE_INTEGER_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_UNSIGNED_LONG,
            XSD_UNSIGNED_LONG_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_UNSIGNED_INT,
            XSD_UNSIGNED_INT_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_UNSIGNED_SHORT,
            XSD_UNSIGNED_SHORT_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_UNSIGNED_BYTE,
            XSD_UNSIGNED_BYTE_ENCODER_NAME);
        encoderMap.put(
            SOAP12Constants.QNAME_TYPE_DATE,
            XSD_DATE_ENCODER_NAME);
        //for jdk < 1.4 map xsd:anyURI to String, otherwise to java.net.URI
        if (!VersionUtil.isJavaVersionGreaterThan1_3())
            encoderMap.put(
                SOAP12Constants.QNAME_TYPE_ANY_URI,
                XSD_STRING_ENCODER_NAME);
        else
            encoderMap.put(
                SOAP12Constants.QNAME_TYPE_ANY_URI,
                XSD_ANY_URI_ENCODER_NAME);
    }
}
