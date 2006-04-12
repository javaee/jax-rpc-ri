/*
 * $Id: SerializerBase.java,v 1.1 2006-04-12 20:33:14 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SerializerBase
    implements CombinedSerializer, SerializerConstants {

    protected QName type;
    protected boolean encodeType;
    protected boolean isNullable;
    protected String encodingStyle;

    protected SerializerBase(
        QName xmlType,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {
        if (xmlType == null) {
            throw new IllegalArgumentException("xmlType parameter is not allowed to be null");
        }
        this.type = xmlType;
        this.encodeType = encodeType;
        this.isNullable = isNullable;
        this.encodingStyle = encodingStyle;
    }

    public QName getXmlType() {
        return type;
    }

    public boolean getEncodeType() {
        return encodeType;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public String getEncodingStyle() {
        return encodingStyle;
    }

    public CombinedSerializer getInnermostSerializer() {
        return this;
    }

    public Object deserialize(
        DataHandler dataHandler,
        SOAPDeserializationContext context) {
        throw new UnsupportedOperationException();
    }

    protected QName getName(XMLReader reader) throws Exception {
        return reader.getName();
    }

    public static QName getType(XMLReader reader) throws Exception {
        QName type = null;

        Attributes attrs = reader.getAttributes();
        String typeVal = attrs.getValue(XSDConstants.URI_XSI, "type");

        if (typeVal != null) {
            type = XMLReaderUtil.decodeQName(reader, typeVal);
        }

        return type;
    }

    public static boolean getNullStatus(XMLReader reader) throws Exception {
        boolean isNull = false;

        Attributes attrs = reader.getAttributes();
        String nullVal = attrs.getValue(XSDConstants.URI_XSI, "nil");
        isNull = (nullVal != null && decodeBoolean(nullVal));

        return isNull;
    }

    public static boolean decodeBoolean(String str) throws Exception {
        return (str.equals("true") || str.equals("1"));
    }

    protected String getID(XMLReader reader) throws Exception {
        Attributes attrs = reader.getAttributes();
        return attrs.getValue("", "id");
    }

    protected void verifyName(XMLReader reader, QName expectedName)
        throws Exception {
        QName actualName = getName(reader);

        if (!actualName.equals(expectedName)) {
            throw new DeserializationException(
                "soap.unexpectedElementName",
                new Object[] { expectedName.toString(), actualName.toString()});
        }
    }

    protected void verifyType(XMLReader reader) throws Exception {
        if (typeIsEmpty()) {
            return;
        }

        QName actualType = getType(reader);

        if (actualType != null) {
            if (!actualType.equals(type) && !isAcceptableType(actualType)) {
                throw new DeserializationException(
                    "soap.unexpectedElementType",
                    new Object[] { type.toString(), actualType.toString()});
            }
        }
    }

    protected boolean isAcceptableType(QName actualType) {
        return false;
    }

    protected void skipEmptyContent(XMLReader reader) throws Exception {
        reader.skipElement();
    }

    public String getMechanismType() {
        return com.sun.xml.rpc.encoding.EncodingConstants.JAX_RPC_RI_MECHANISM;
    }

    protected boolean typeIsEmpty() {
        return type.getNamespaceURI().equals("")
            && type.getLocalPart().equals("");
    }
}
