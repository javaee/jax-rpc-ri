/*
 * $Id: LiteralArraySerializer.java,v 1.3 2007-07-13 23:35:58 ofung Exp $
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

package com.sun.xml.rpc.encoding.literal;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.EncodingException;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializationException;
import com.sun.xml.rpc.encoding.SerializerBase;
import com.sun.xml.rpc.encoding.SerializerCallback;
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
public class LiteralArraySerializer extends LiteralObjectSerializerBase {

    protected Class javaType;
    protected Class componentType;
    protected InternalTypeMappingRegistry typeRegistry = null;
    protected JAXRPCSerializer componentSerializer;
    protected JAXRPCDeserializer componentDeserializer;

    public LiteralArraySerializer(
        QName type,
        boolean isNullable,
        String encodingStyle,
        Class javaType,
        JAXRPCSerializer componentSerializer,
        Class componentType) {
            
        super(type, isNullable, encodingStyle, false);
        this.componentSerializer = componentSerializer;
        this.componentDeserializer = (JAXRPCDeserializer) componentSerializer;
        this.javaType = javaType;
        this.componentType = componentType;

    }

    public LiteralArraySerializer(
        QName type,
        boolean isNullable,
        String encodingStyle,
        boolean encodeType,
        Class javaType,
        JAXRPCSerializer componentSerializer,
        Class componentType) {

        super(type, isNullable, encodingStyle, encodeType);
        this.javaType = javaType;
        this.componentSerializer = componentSerializer;
        this.componentType = componentType;
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
        return (CombinedSerializer) this.componentSerializer;
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

    protected void internalSerialize(
        Object obj,
        QName name,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        context.beginSerializing(obj);

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

            return obj;
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }

    protected Object doDeserialize(
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {
        if (typeRegistry == null) {
            throw new EncodingException("initializable.not.initialized");
        }
        if (this.componentDeserializer == null)
            this.componentDeserializer =
                (JAXRPCDeserializer) this.componentSerializer;

        ArrayList values = null;
        Object currentValue = null;
        Object array = null;
        QName elementName = null;
        elementName = reader.getName();

        //check to see if the state is start and if the elementName is what you expect(Qname of each arrayElement)
        if ((reader.getState() == XMLReader.START)
            && (elementName.equals(super.type))) {

            values = new ArrayList();
            for (;;) {
                elementName = reader.getName();
                if ((reader.getState() == XMLReader.START)
                    && (elementName.equals(super.type))) {

                    //super - we got what we expect
                    //the next thing should be a value
                    currentValue =
                        componentDeserializer.deserialize(
                            super.type,
                            reader,
                            context);

                    if (currentValue != null)
                        values.add(currentValue);
                    else
                        throw new DeserializationException("literal unexpected null");

                    XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
                    reader.nextElementContent();

                } else {
                    break;
                }
            } // done reading components of array - need to create the array with
            //with the values;
            //create array

            array = Array.newInstance(componentType, values.size());
            Object[] valuesArray = values.toArray();

            //populate array
            for (int i = 0; i < valuesArray.length; i++) {
                Array.set(array, i, valuesArray[i]);
            }
        }

        return array;
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

    protected void doSerialize(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        doSerializeInstance(obj, writer, context);

    }

    protected void doSerializeInstance(
        Object instance,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {
        if (typeRegistry == null) {
            throw new EncodingException("initializable.not.initialized");
        }
        if (instance.getClass().isArray()) {
            int length = Array.getLength(instance);

            for (int i = 0; i < length; i++) {
                Object parameter = Array.get(instance, i);
                if (componentSerializer != null) {
                }
                ((CombinedSerializer) componentSerializer).serialize(
                    parameter,
                    null,
                    null,
                    writer,
                    context);
            }
        }
    }

    protected void doSerializeAttributes(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {
    }

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

    protected JAXRPCSerializer getParameterSerializer(
        int index,
        Object parameter)
        throws Exception {

        return null;
    }

    protected JAXRPCDeserializer getParameterDeserializer(
        int index,
        XMLReader reader)
        throws Exception {

        return null;
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

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {
        if (typeRegistry != null) {
            return;
        }

        typeRegistry = registry;

    }
}
