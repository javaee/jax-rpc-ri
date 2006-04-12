/*
 * $Id: SerializerWriterBase.java,v 1.1 2006-04-12 20:35:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.GeneratorUtil;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAP12Constants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SerializerWriterBase
    implements SerializerWriter, GeneratorConstants {
    AbstractType type;
    Names names;
    SOAPVersion soapVer = SOAPVersion.SOAP_11;

    public SerializerWriterBase(AbstractType type, Names names) {

        if (type.isSOAPType()) {
            String ver = type.getVersion();
            if (ver.equals(SOAPVersion.SOAP_11.toString()))
                soapVer = SOAPVersion.SOAP_11;
            else if (ver.equals(SOAPVersion.SOAP_12.toString()))
                soapVer = SOAPVersion.SOAP_12;
        }
        this.type = type;
        this.names = names;
    }

    public void registerSerializer(
        IndentingWriter p,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        StringBuffer typeName = new StringBuffer(40);
        typeName.append("type");
        createSerializer(
            p,
            typeName,
            "serializer",
            encodeTypes,
            multiRefEncoding,
            typeMapping);
        writeRegisterFactories(
            p,
            typeName.toString(),
            "serializer",
            typeMapping);
    }

    public void initializeSerializer(
        IndentingWriter p,
        String typeName,
        String registry)
        throws IOException {
        //bug 4845163 fix
        String javaType = null;
        if (type.getName().equals(SchemaConstants.QNAME_TYPE_IDREF)) {
            javaType = STRING_CLASSNAME;
        } else {
            javaType = type.getJavaType().getName();
        }
        p.pln(
            serializerMemberName()
                + " = (CombinedSerializer)registry.getSerializer("
                + getEncodingStyleString()
                + ", "
                + javaType
                + ".class, "
                + typeName
                + ");");
    }

    public String serializerName() {
        return GeneratorConstants.BASE_SERIALIZER_NAME;
    }

    public String deserializerName() {
        return serializerName();
    }

    public static boolean handlesType(AbstractType type) {
        return false;
    }

    protected String getEncodingStyleString() {
        if (soapVer.equals(SOAPVersion.SOAP_12))
            return SOAP12CONSTANTS_NS_SOAP_ENCODING;
        else if (soapVer.equals(SOAPVersion.SOAP_11))
            return SOAPCONSTANTS_NS_SOAP_ENCODING;
        return null;
    }

    protected String getEncodingStyle() {
        if (soapVer.equals(SOAPVersion.SOAP_12))
            return SOAP12Constants.NS_SOAP_ENCODING;
        else if (soapVer.equals(SOAPVersion.SOAP_11))
            return SOAPConstants.NS_SOAP_ENCODING;
        return null;
    }

    protected String getSOAPVersionString() {
        if (soapVer.equals(SOAPVersion.SOAP_12))
            return SOAP_VERSION_12;
        else if (soapVer.equals(SOAPVersion.SOAP_11))
            return SOAP_VERSION_11;
        return null;
    }

    protected void declareType(
        IndentingWriter p,
        StringBuffer member,
        QName type,
        boolean isStatic,
        boolean isFinal)
        throws IOException {

        String qnameConstant = GeneratorUtil.getQNameConstant(type);
        if (qnameConstant != null) {
            member.delete(0, member.length());
            member.append(qnameConstant);
        } else {
            String modifier = getModifier(isStatic, isFinal);
            p.p(modifier + "QName " + member + " = ");
            GeneratorUtil.writeNewQName(p, type);
            p.pln(";");
        }
    }

    protected void writeRegisterFactories(
        IndentingWriter p,
        String typeName,
        String memberName,
        String mapping)
        throws IOException {
        p.pln(
            "registerSerializer("
                + mapping
                + ","
                + type.getJavaType().getName()
                + ".class, "
                + typeName
                + ", "
                + memberName
                + ");");
    }

    protected String getModifier(boolean isStatic, boolean isFinal) {
        String modifier = "";
        if (isStatic) {
            modifier += "static ";
        }
        if (isFinal) {
            modifier += "final ";
        }
        return modifier;
    }

    protected String getPrefix(AbstractType type) {
        QName typeName = type.getName();
        String prefix = names.getPrefix(typeName);
        return (prefix);
    }
}
