/*
 * $Id: LiteralObjectSerializerBase.java,v 1.2 2006-04-13 01:27:36 ofung Exp $
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

package com.sun.xml.rpc.encoding.literal;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializationException;
import com.sun.xml.rpc.encoding.SerializerBase;
import com.sun.xml.rpc.encoding.SerializerCallback;
import com.sun.xml.rpc.encoding.SerializerConstants;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class LiteralObjectSerializerBase
    implements SerializerConstants, CombinedSerializer {

    protected QName type;
    protected boolean isNullable;
    protected String encodingStyle;
    protected boolean encodeType = false;

    protected LiteralObjectSerializerBase(
        QName type,
        boolean isNullable,
        String encodingStyle) {
            
        init(type, isNullable, encodingStyle, false);
    }

    protected LiteralObjectSerializerBase(
        QName type,
        boolean isNullable,
        String encodingStyle,
        boolean encodeType) {
            
        init(type, isNullable, encodingStyle, encodeType);
    }

    private void init(
        QName type,
        boolean isNullable,
        String encodingStyle,
        boolean encodeType) {
            
        if (type == null) {
            throw new IllegalArgumentException();
        }

        this.type = type;
        this.isNullable = isNullable;
        this.encodingStyle = encodingStyle;
        this.encodeType = encodeType;
    }

    public QName getXmlType() {
        return type;
    }

    public boolean getEncodeType() {
        return false;
    }

    public CombinedSerializer getInnermostSerializer() {
        return this;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public String getEncodingStyle() {
        return encodingStyle;
    }

    public void serialize(
        Object value,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context) {

        try {
            internalSerialize(value, name, writer, context);
        } catch (SerializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        } catch (Exception e) {
            throw new SerializationException(
                new LocalizableExceptionAdapter(e));
        }
    }

    public Object deserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context) {

        try {
            return internalDeserialize(name, reader, context);
        } catch (DeserializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        } catch (Exception e) {
            throw new DeserializationException(
                new LocalizableExceptionAdapter(e));
        }
    }

    public Object deserialize(
        DataHandler dataHandler,
        SOAPDeserializationContext context)
        throws DeserializationException, UnsupportedOperationException {
            
        throw new UnsupportedOperationException();
    }

    protected void internalSerialize(
        Object obj,
        QName name,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        context.beginSerializing(obj);

        writer.startElement((name != null) ? name : type);

        boolean pushedEncodingStyle = false;
        if (encodingStyle != null)
            pushedEncodingStyle =
                context.pushEncodingStyle(encodingStyle, writer);

        if (encodeType) {
            String attrVal = XMLWriterUtil.encodeQName(writer, type);
            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_TYPE, attrVal);
        }
        if (obj == null) {
            if (!isNullable) {
                throw new SerializationException("literal.unexpectedNull");
            }

            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_NIL, "1");
        } else {
            writeAdditionalNamespaceDeclarations(obj, writer);
            doSerializeAttributes(obj, writer, context);
            doSerialize(obj, writer, context);
        }

        writer.endElement();
        if (pushedEncodingStyle) {
            context.popEncodingStyle();
        }

        context.doneSerializing(obj);
    }

    protected Object internalDeserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {

        boolean pushedEncodingStyle = context.processEncodingStyle(reader);
        try {
            context.verifyEncodingStyle(encodingStyle);

            if (name != null) {
                QName actualName = reader.getName();
                if (!actualName.equals(name)) {
                    throw new DeserializationException(
                        "xsd.unexpectedElementName",
                        new Object[] { name.toString(), actualName.toString()});
                }
            }

            verifyType(reader);

            Attributes attrs = reader.getAttributes();
            String nullVal = attrs.getValue(XSDConstants.URI_XSI, "nil");
            boolean isNull =
                (nullVal != null && SerializerBase.decodeBoolean(nullVal));
            Object obj = null;

            if (isNull) {
                if (!isNullable) {
                    throw new DeserializationException("xsd.unexpectedNull");
                }
                reader.next();
            } else {
                obj = doDeserialize(reader, context);
            }

            XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
            return obj;
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }

    protected void verifyType(XMLReader reader) throws Exception {
        QName actualType = getType(reader);

        if (actualType != null) {
            if (!actualType.equals(type) && !isAcceptableType(actualType)) {
                throw new DeserializationException(
                    "xsd.unexpectedElementType",
                    new Object[] { type.toString(), actualType.toString()});
            }
        }
    }

    protected boolean isAcceptableType(QName actualType) {
        return false;
    }

    protected void writeAdditionalNamespaceDeclarations(
        Object obj,
        XMLWriter writer)
        throws Exception {
    }

    protected abstract void doSerialize(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception;

    protected abstract void doSerializeAttributes(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception;

    protected abstract Object doDeserialize(
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception;

    public String getMechanismType() {
        return com.sun.xml.rpc.encoding.EncodingConstants.JAX_RPC_RI_MECHANISM;
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

    public static SOAPDeserializationState registerWithMemberState(
        Object instance,
        SOAPDeserializationState state,
        Object member,
        int memberIndex,
        SOAPInstanceBuilder builder) {
            
        try {
            SOAPDeserializationState deserializationState;
            if (state == null) {
                deserializationState = new SOAPDeserializationState();
            } else {
                deserializationState = state;
            }

            deserializationState.setInstance(instance);
            if (deserializationState.getBuilder() == null) {
                if (builder == null) {
                    throw new IllegalArgumentException();
                }
                deserializationState.setBuilder(builder);
            }

            SOAPDeserializationState memberState =
                (SOAPDeserializationState) member;
            memberState.registerListener(deserializationState, memberIndex);

            return deserializationState;
        } catch (JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        } catch (Exception e) {
            throw new DeserializationException(
                new LocalizableExceptionAdapter(e));
        }
    }
}
