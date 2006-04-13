/*
 * $Id: InterfaceSerializerBase.java,v 1.2 2006-04-13 01:27:09 ofung Exp $
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

package com.sun.xml.rpc.encoding;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class InterfaceSerializerBase extends SerializerBase {

    protected InterfaceSerializerBase(
        QName type,
        String encodingStyle,
        boolean encodeType) {
            
        super(type, encodeType, false, encodingStyle);
    }

    protected InterfaceSerializerBase(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {
            
        super(type, encodeType, isNullable, encodingStyle);
    }

    protected abstract Object doDeserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception;
        
    protected abstract void doSerializeInstance(
        Object obj,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception;

    public void serialize(
        Object obj,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context) {

        try {
            if (obj == null) {
                if (!isNullable) {
                    throw new SerializationException("soap.unexpectedNull");
                }
                serializeNull(name, writer, context);
            } else {
                doSerializeInstance(obj, name, callback, writer, context);
            }
        } catch (SerializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        } catch (Exception e) {
            throw new SerializationException(
                new LocalizableExceptionAdapter(e));
        }
    }

    protected void serializeNull(
        QName name,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        writer.startElement((name != null) ? name : type);

        boolean pushedEncodingStyle = false;
        if (encodingStyle != null)
            pushedEncodingStyle =
                context.pushEncodingStyle(encodingStyle, writer);

        if (encodeType) {
            String attrVal = XMLWriterUtil.encodeQName(writer, type);
            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_TYPE, attrVal);
        }

        writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_NIL, "1");
        writer.endElement();
        if (pushedEncodingStyle) {
            context.popEncodingStyle();
        }
    }

    public Object deserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context) {

        boolean pushedEncodingStyle = false;
        try {
            boolean isNull = getNullStatus(reader);
            if (!isNull) {
                return doDeserialize(name, reader, context);
            } else {
                if (!isNullable) {
                    throw new DeserializationException("soap.unexpectedNull");
                }
                String id = getID(reader);
                skipEmptyContent(reader);
                if (id != null) {
                    SOAPDeserializationState state = context.getStateFor(id);
                    state.setDeserializer(this);
                    state.setInstance(null);
                    state.doneReading();
                }

                return null;
            }
        } catch (DeserializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        } catch (Exception e) {
            throw new DeserializationException(
                new LocalizableExceptionAdapter(e));
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }
    public static SOAPDeserializationState registerWithMemberState(
        Object instance,
        SOAPDeserializationState state,
        Object member,
        int memberIndex,
        SOAPInstanceBuilder builder) {
            
        return ObjectSerializerBase.registerWithMemberState(
            instance,
            state,
            member,
            memberIndex,
            builder);
    }
}