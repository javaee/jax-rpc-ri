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

package com.sun.xml.rpc.encoding.soap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;

import com.sun.xml.rpc.encoding.AttachmentSerializer;
import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.DynamicSerializer;
import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.encoding.ReferenceableSerializerImpl;
import com.sun.xml.rpc.encoding.SerializerConstants;
import com.sun.xml.rpc.encoding.SimpleMultiTypeSerializer;
import com.sun.xml.rpc.encoding.SimpleTypeSerializer;
import com.sun.xml.rpc.encoding.SingletonDeserializerFactory;
import com.sun.xml.rpc.encoding.SingletonSerializerFactory;
import com.sun.xml.rpc.encoding.TypeMappingImpl;
import com.sun.xml.rpc.encoding.simpletype.DataHandlerAttachmentEncoder;
import com.sun.xml.rpc.encoding.simpletype.ImageAttachmentEncoder;
import com.sun.xml.rpc.encoding.simpletype.MimeMultipartAttachmentEncoder;
import com.sun.xml.rpc.encoding.simpletype.SourceAttachmentEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDBase64BinaryEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDBooleanEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDByteEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDateEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDateTimeCalendarEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDateTimeDateEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDecimalEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDoubleEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDFloatEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDHexBinaryEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDIntEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDIntegerEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDListTypeEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDLongEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDNegativeIntegerEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDNonNegativeIntegerEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDNonPositiveIntegerEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDPositiveIntegerEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDQNameEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDShortEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDStringEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDTimeEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDUnsignedByteEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDUnsignedIntEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDUnsignedLongEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDUnsignedShortEncoder;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 * An implementation of the standard TypeMapping interface
 *
 * @author JAX-RPC Development Team
 */
public class StandardSOAPTypeMappings
    extends TypeMappingImpl
    implements SerializerConstants, InternalEncodingConstants {

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        this.soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public StandardSOAPTypeMappings() throws Exception {
        this(SOAPVersion.SOAP_11);
    }

    public StandardSOAPTypeMappings(SOAPVersion ver) throws Exception {
        super();
        init(ver); // Initialize SOAP constants
        setSupportedEncodings(
            new String[] { soapEncodingConstants.getSOAPEncodingNamespace()});
        QName base64Types[] =
            new QName[] {
                SchemaConstants.QNAME_TYPE_BASE64_BINARY,
                soapEncodingConstants.getQNameTypeBase64Binary(),
                soapEncodingConstants.getQNameTypeBase64()};
        {
            QName type = SchemaConstants.QNAME_TYPE_BOOLEAN;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDBooleanEncoder.getInstance());
            registerSerializer(boolean.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeBoolean();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDBooleanEncoder.getInstance());
            registerReferenceableSerializer(boolean.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_BOOLEAN;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDBooleanEncoder.getInstance());
            registerSerializer(Boolean.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeBoolean();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDBooleanEncoder.getInstance());
            registerReferenceableSerializer(Boolean.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_BYTE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDByteEncoder.getInstance());
            registerSerializer(byte.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeByte();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDByteEncoder.getInstance());
            registerReferenceableSerializer(byte.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_BYTE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDByteEncoder.getInstance());
            registerSerializer(Byte.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeByte();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDByteEncoder.getInstance());
            registerReferenceableSerializer(Byte.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_BASE64_BINARY;
            CombinedSerializer serializer =
                new SimpleMultiTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDBase64BinaryEncoder.getInstance(),
                    base64Types);
            registerReferenceableSerializer(byte[].class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeBase64Binary();
            CombinedSerializer serializer =
                new SimpleMultiTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDBase64BinaryEncoder.getInstance(),
                    base64Types);
            registerReferenceableSerializer(byte[].class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeBase64();
            CombinedSerializer serializer =
                new SimpleMultiTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDBase64BinaryEncoder.getInstance(),
                    base64Types);
            registerReferenceableSerializer(byte[].class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_HEX_BINARY;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDHexBinaryEncoder.getInstance());
            registerReferenceableSerializer(byte[].class, type, serializer);
        }
        /*
                {
                    QName type = SchemaConstants.QNAME_TYPE_BASE64_BINARY;
                    CombinedSerializer serializer = new SimpleTypeSerializer(type,
                        ENCODE_TYPE, NULLABLE, soapEncodingConstants.getSOAPEncodingNamespace(), XSDBase64BinaryEncoder.getInstance());
                    registerSerializer(Byte[].class, type, serializer);
                }
                {
                    QName type = soapEncodingConstants.QNAME_TYPE_BASE64_BINARY;
                    CombinedSerializer serializer = new SimpleTypeSerializer(type,
                        ENCODE_TYPE, NULLABLE, soapEncodingConstants.getSOAPEncodingNamespace(), XSDBase64BinaryEncoder.getInstance());
                    registerSerializer(Byte[].class, type, serializer);
                }
        */ {
            QName type = SchemaConstants.QNAME_TYPE_DECIMAL;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDecimalEncoder.getInstance());
            registerSerializer(java.math.BigDecimal.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeDecimal();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDecimalEncoder.getInstance());
            registerReferenceableSerializer(
                java.math.BigDecimal.class,
                type,
                serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_DOUBLE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDoubleEncoder.getInstance());
            registerSerializer(double.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeDouble();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDoubleEncoder.getInstance());
            registerReferenceableSerializer(double.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_DOUBLE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDoubleEncoder.getInstance());
            registerSerializer(Double.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeDouble();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDoubleEncoder.getInstance());
            registerReferenceableSerializer(Double.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_FLOAT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDFloatEncoder.getInstance());
            registerSerializer(float.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeFloat();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDFloatEncoder.getInstance());
            registerReferenceableSerializer(float.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_FLOAT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDFloatEncoder.getInstance());
            registerSerializer(Float.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeFloat();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDFloatEncoder.getInstance());
            registerReferenceableSerializer(Float.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_INT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDIntEncoder.getInstance());
            registerSerializer(int.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeInt();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDIntEncoder.getInstance());
            registerReferenceableSerializer(int.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_INT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDIntEncoder.getInstance());
            registerSerializer(Integer.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeInt();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDIntEncoder.getInstance());
            registerReferenceableSerializer(Integer.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_INTEGER;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDIntegerEncoder.getInstance());
            registerSerializer(java.math.BigInteger.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeInteger();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDIntegerEncoder.getInstance());
            registerReferenceableSerializer(
                java.math.BigInteger.class,
                type,
                serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_LONG;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDLongEncoder.getInstance());
            registerSerializer(long.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeLong();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDLongEncoder.getInstance());
            registerReferenceableSerializer(long.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_LONG;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDLongEncoder.getInstance());
            registerSerializer(Long.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeLong();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDLongEncoder.getInstance());
            registerReferenceableSerializer(Long.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_SHORT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDShortEncoder.getInstance());
            registerSerializer(short.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeShort();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDShortEncoder.getInstance());
            registerReferenceableSerializer(short.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_SHORT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDShortEncoder.getInstance());
            registerSerializer(Short.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeShort();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDShortEncoder.getInstance());
            registerReferenceableSerializer(Short.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_STRING;
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    false,
                    XSDStringEncoder.getInstance(),
                    ver);
            // make sure we never serialize as an HREF
            registerReferenceableSerializer(
                String.class,
                type,
                serializer,
                DONT_SERIALIZE_AS_REF);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeString();
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    false,
                    XSDStringEncoder.getInstance(),
                    ver);
            // make sure we never serialize as an HREF
            registerReferenceableSerializer(
                String.class,
                type,
                serializer,
                DONT_SERIALIZE_AS_REF);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_DATE_TIME;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDateTimeCalendarEncoder.getInstance());
            registerSerializer(java.util.Calendar.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeDateTime();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDateTimeCalendarEncoder.getInstance());
            registerReferenceableSerializer(
                java.util.Calendar.class,
                type,
                serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_DATE_TIME;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDateTimeDateEncoder.getInstance());
            registerSerializer(java.util.Date.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeDateTime();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDateTimeDateEncoder.getInstance());
            registerReferenceableSerializer(
                java.util.Date.class,
                type,
                serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_QNAME;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDQNameEncoder.getInstance());
            registerSerializer(
                javax.xml.namespace.QName.class,
                type,
                serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeQName();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDQNameEncoder.getInstance());
            registerReferenceableSerializer(
                javax.xml.namespace.QName.class,
                type,
                serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_URTYPE; // anyType
            CombinedSerializer serializer =
                new DynamicSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    ver);
            registerReferenceableSerializer(Object.class, type, serializer);
        }
        // Collection Types
        {
            QName type = QNAME_TYPE_COLLECTION;
            CombinedSerializer serializer =
                new CollectionInterfaceSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    ver);
            registerReferenceableSerializer(Collection.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_LIST;
            CombinedSerializer serializer =
                new CollectionInterfaceSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    ver);
            registerReferenceableSerializer(List.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_ARRAY_LIST;
            CombinedSerializer serializer =
                new CollectionSerializer(
                    type,
                    ArrayList.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    COLLECTION_ELEMENT_NAME,
                    SchemaConstants.QNAME_TYPE_URTYPE,
                    Object.class,
                    ver);
            registerReferenceableSerializer(ArrayList.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_VECTOR;
            CombinedSerializer serializer =
                new CollectionSerializer(
                    type,
                    Vector.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    COLLECTION_ELEMENT_NAME,
                    SchemaConstants.QNAME_TYPE_URTYPE,
                    Object.class,
                    ver);
            registerReferenceableSerializer(Vector.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_LINKED_LIST;
            CombinedSerializer serializer =
                new CollectionSerializer(
                    type,
                    LinkedList.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    COLLECTION_ELEMENT_NAME,
                    SchemaConstants.QNAME_TYPE_URTYPE,
                    Object.class,
                    ver);
            registerReferenceableSerializer(LinkedList.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_STACK;
            CombinedSerializer serializer =
                new CollectionSerializer(
                    type,
                    Stack.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    COLLECTION_ELEMENT_NAME,
                    SchemaConstants.QNAME_TYPE_URTYPE,
                    Object.class,
                    ver);
            registerReferenceableSerializer(Stack.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_SET;
            CombinedSerializer serializer =
                new CollectionInterfaceSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    ver);
            registerReferenceableSerializer(Set.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_HASH_SET;
            CombinedSerializer serializer =
                new CollectionSerializer(
                    type,
                    HashSet.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    COLLECTION_ELEMENT_NAME,
                    SchemaConstants.QNAME_TYPE_URTYPE,
                    Object.class,
                    ver);
            registerReferenceableSerializer(HashSet.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_TREE_SET;
            CombinedSerializer serializer =
                new CollectionSerializer(
                    type,
                    TreeSet.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    COLLECTION_ELEMENT_NAME,
                    SchemaConstants.QNAME_TYPE_URTYPE,
                    Object.class,
                    ver);
            registerReferenceableSerializer(TreeSet.class, type, serializer);
        }

        // Map Types
        {
            QName type = QNAME_TYPE_MAP;
            CombinedSerializer serializer =
                new MapInterfaceSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getURIEncoding(),
                    ver);
            registerReferenceableSerializer(Map.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_JAX_RPC_MAP_ENTRY;
            CombinedSerializer serializer =
                new JAXRpcMapEntrySerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getURIEncoding(),
                    ver);
            registerReferenceableSerializer(
                JAXRpcMapEntry.class,
                type,
                serializer);
        }
        {
            QName type = QNAME_TYPE_HASH_MAP;
            CombinedSerializer serializer =
                new MapSerializer(
                    type,
                    HashMap.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getURIEncoding(),
                    ver);
            registerReferenceableSerializer(HashMap.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_TREE_MAP;
            CombinedSerializer serializer =
                new MapSerializer(
                    type,
                    TreeMap.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getURIEncoding(),
                    ver);
            registerReferenceableSerializer(TreeMap.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_HASHTABLE;
            CombinedSerializer serializer =
                new MapSerializer(
                    type,
                    Hashtable.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getURIEncoding(),
                    ver);
            registerReferenceableSerializer(Hashtable.class, type, serializer);
        }
        {
            QName type = QNAME_TYPE_PROPERTIES;
            CombinedSerializer serializer =
                new MapSerializer(
                    type,
                    Properties.class,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getURIEncoding(),
                    ver);
            registerReferenceableSerializer(Properties.class, type, serializer);
        }
        /*
                {
                    QName type = QNAME_TYPE_WEAK_HASH_MAP;
                    CombinedSerializer serializer = new MapSerializer(type, WeakHashMap.class,
                        ENCODE_TYPE, NULLABLE, soapEncodingConstants.getURIEncoding());
                    serializer = new ReferenceableSerializerImpl(SERIALIZE_AS_REF, serializer);
                    registerSerializer(WeakHashMap.class, type, serializer);
                }
        */

        // Attachment Types
        {
            QName type =
                new QName("http://java.sun.com/jax-rpc-ri/internal", "image");
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    true,
                    ImageAttachmentEncoder.getInstance(),
                    ver);
            registerReferenceableSerializer(
                java.awt.Image.class,
                type,
                serializer);
        }
        {
            QName type =
                new QName(
                    "http://java.sun.com/jax-rpc-ri/internal",
                    "datahandler");
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    true,
                    DataHandlerAttachmentEncoder.getInstance(),
                    ver);
            registerReferenceableSerializer(
                javax.activation.DataHandler.class,
                type,
                serializer);
        }
        {
            QName type =
                new QName(
                    "http://java.sun.com/jax-rpc-ri/internal",
                    "multipart");
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    true,
                    MimeMultipartAttachmentEncoder.getInstance(),
                    ver);
            registerReferenceableSerializer(
                javax.mail.internet.MimeMultipart.class,
                type,
                serializer);
        }
        {
            QName type =
                new QName(
                    "http://java.sun.com/jax-rpc-ri/internal",
                    "text_xml");
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    true,
                    SourceAttachmentEncoder.getInstance(),
                    ver);
            registerReferenceableSerializer(
                javax.xml.transform.Source.class,
                type,
                serializer);
        }
        {
            QName type =
                new QName("http://java.sun.com/jax-rpc-ri/internal", "image");
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    true,
                    DataHandlerAttachmentEncoder.getInstance(),
                    ver);
            registerReferenceableSerializer(
                javax.activation.DataHandler.class,
                type,
                serializer);
        }
        {
            QName type =
                new QName(
                    "http://java.sun.com/jax-rpc-ri/internal",
                    "multipart");
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    true,
                    DataHandlerAttachmentEncoder.getInstance(),
                    ver);
            registerReferenceableSerializer(
                javax.activation.DataHandler.class,
                type,
                serializer);
        }
        {
            QName type =
                new QName(
                    "http://java.sun.com/jax-rpc-ri/internal",
                    "text_xml");
            CombinedSerializer serializer =
                new AttachmentSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    true,
                    DataHandlerAttachmentEncoder.getInstance(),
                    ver);
            registerReferenceableSerializer(
                javax.activation.DataHandler.class,
                type,
                serializer);
        }

        // New types 12/3/02
        {
            QName type = SchemaConstants.QNAME_TYPE_LANGUAGE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeLanguage();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_NORMALIZED_STRING;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeNormalizedString();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_TOKEN;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeToken();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_NMTOKEN;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeNMTOKEN();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_NAME;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeName();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_NCNAME;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeNCNAME();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_ID;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeID();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }

        //xsd:positiveInteger
        {
            QName type = SchemaConstants.QNAME_TYPE_POSITIVE_INTEGER;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDPositiveIntegerEncoder.getInstance());
            registerSerializer(java.math.BigInteger.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypePositiveInteger();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDPositiveIntegerEncoder.getInstance());
            registerReferenceableSerializer(
                java.math.BigInteger.class,
                type,
                serializer);
        }

        //xsd:nonPositiveInteger
        {
            QName type = SchemaConstants.QNAME_TYPE_NON_POSITIVE_INTEGER;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDNonPositiveIntegerEncoder.getInstance());
            registerSerializer(java.math.BigInteger.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeNonPositiveInteger();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDNonPositiveIntegerEncoder.getInstance());
            registerReferenceableSerializer(
                java.math.BigInteger.class,
                type,
                serializer);
        }

        //xsd:negativeInteger
        {
            QName type = SchemaConstants.QNAME_TYPE_NEGATIVE_INTEGER;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDNegativeIntegerEncoder.getInstance());
            registerSerializer(java.math.BigInteger.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeNegativeInteger();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDNegativeIntegerEncoder.getInstance());
            registerReferenceableSerializer(
                java.math.BigInteger.class,
                type,
                serializer);
        }

        //xsd:nonNegativeInteger
        {
            QName type = SchemaConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDNonNegativeIntegerEncoder.getInstance());
            registerSerializer(java.math.BigInteger.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeNonNegativeInteger();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDNonNegativeIntegerEncoder.getInstance());
            registerReferenceableSerializer(
                java.math.BigInteger.class,
                type,
                serializer);
        }

        //xsd:unsignedLong
        {
            QName type = SchemaConstants.QNAME_TYPE_UNSIGNED_LONG;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedLongEncoder.getInstance());
            registerSerializer(java.math.BigInteger.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeUnsignedLong();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedLongEncoder.getInstance());
            registerReferenceableSerializer(
                java.math.BigInteger.class,
                type,
                serializer);
        }

        //unsignedInt
        {
            QName type = SchemaConstants.QNAME_TYPE_UNSIGNED_INT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedIntEncoder.getInstance());
            registerSerializer(long.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeUnsignedInt();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDLongEncoder.getInstance());
            registerReferenceableSerializer(long.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_UNSIGNED_INT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedIntEncoder.getInstance());
            registerSerializer(Long.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeUnsignedInt();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedIntEncoder.getInstance());
            registerReferenceableSerializer(Long.class, type, serializer);
        }

        //unsignedShort
        {
            QName type = SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedShortEncoder.getInstance());
            registerSerializer(int.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeUnsignedShort();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedShortEncoder.getInstance());
            registerReferenceableSerializer(int.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedShortEncoder.getInstance());
            registerSerializer(Integer.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeUnsignedShort();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedShortEncoder.getInstance());
            registerReferenceableSerializer(Integer.class, type, serializer);
        }

        //unsignedByte
        {
            QName type = SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedByteEncoder.getInstance());
            registerSerializer(short.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeUnsignedByte();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedByteEncoder.getInstance());
            registerReferenceableSerializer(short.class, type, serializer);
        }
        {
            QName type = SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedByteEncoder.getInstance());
            registerSerializer(Short.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeUnsignedByte();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDUnsignedByteEncoder.getInstance());
            registerReferenceableSerializer(Short.class, type, serializer);
        }

        //xsd:Duration
        {
            QName type = SchemaConstants.QNAME_TYPE_DURATION;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeDuration();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }

        //xsd:time
        {
            QName type = SchemaConstants.QNAME_TYPE_TIME;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDTimeEncoder.getInstance());
            registerSerializer(Calendar.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeTime();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDTimeEncoder.getInstance());
            registerReferenceableSerializer(Calendar.class, type, serializer);
        }

        //xsd:date
        {
            QName type = SchemaConstants.QNAME_TYPE_DATE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDateEncoder.getInstance());
            registerSerializer(Calendar.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeDate();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDDateTimeCalendarEncoder.getInstance());
            registerReferenceableSerializer(Calendar.class, type, serializer);
        }

        //xsd:G_YEAR_MONTH
        {
            QName type = SchemaConstants.QNAME_TYPE_G_YEAR_MONTH;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeGYearMonth();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }

        //xsd:G_YEAR
        {
            QName type = SchemaConstants.QNAME_TYPE_G_YEAR;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeGYear();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }

        //xsd:G_MONTH_DAY
        {
            QName type = SchemaConstants.QNAME_TYPE_G_MONTH_DAY;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeGMonthDay();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }

        //xsd:G_DAY
        {
            QName type = SchemaConstants.QNAME_TYPE_G_DAY;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeGDay();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }

        //xsd:G_Month
        {
            QName type = SchemaConstants.QNAME_TYPE_G_MONTH;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeGMonth();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }
        //xsd:IDREF
        {
            QName type = SchemaConstants.QNAME_TYPE_IDREF;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerSerializer(String.class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeIDREF();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }

        //xsd:IDREFS
        //bug fix: 4863162
        {
            QName type = SchemaConstants.QNAME_TYPE_IDREFS;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDListTypeEncoder.getInstance(
                        XSDStringEncoder.getInstance(),
                        String.class));
            registerSerializer(String[].class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeIDREFS();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDListTypeEncoder.getInstance(
                        XSDStringEncoder.getInstance(),
                        String.class));
            registerReferenceableSerializer(String[].class, type, serializer);
        }

        //xsd:NMTOKENS
        //bug fix: 4863162
        {
            QName type = SchemaConstants.QNAME_TYPE_NMTOKENS;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDListTypeEncoder.getInstance(
                        XSDStringEncoder.getInstance(),
                        String.class));
            registerSerializer(String[].class, type, serializer);
        }
        {
            QName type = soapEncodingConstants.getQNameTypeNMTOKENS();
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDListTypeEncoder.getInstance(
                        XSDStringEncoder.getInstance(),
                        String.class));
            registerReferenceableSerializer(String[].class, type, serializer);
        }

        // bugfix: 4925400
        {
            QName type = SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE;
            CombinedSerializer serializer =
                new SimpleTypeSerializer(
                    type,
                    ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getSOAPEncodingNamespace(),
                    XSDStringEncoder.getInstance());
            registerReferenceableSerializer(String.class, type, serializer);
        }
    }

    private void registerReferenceableSerializer(
        Class javaType,
        QName xmlType,
        CombinedSerializer ser)
        throws Exception {
            
        registerReferenceableSerializer(
            javaType,
            xmlType,
            ser,
            SOAPVersion.SOAP_11);
    }

    private void registerReferenceableSerializer(
        Class javaType,
        QName xmlType,
        CombinedSerializer ser,
        SOAPVersion version)
        throws Exception {
            
        registerReferenceableSerializer(
            javaType,
            xmlType,
            ser,
            DONT_SERIALIZE_AS_REF,
            version);
    }

    private void registerReferenceableSerializer(
        Class javaType,
        QName xmlType,
        CombinedSerializer ser,
        boolean serializeAsRef)
        throws Exception {
            
        registerReferenceableSerializer(
            javaType,
            xmlType,
            ser,
            serializeAsRef,
            SOAPVersion.SOAP_11);
    }

    private void registerReferenceableSerializer(
        Class javaType,
        QName xmlType,
        CombinedSerializer ser,
        boolean serializeAsRef,
        SOAPVersion version)
        throws Exception {
            
        if (!(ser instanceof ReferenceableSerializerImpl))
            ser = new ReferenceableSerializerImpl(serializeAsRef, ser, version);
        registerSerializer(javaType, xmlType, ser);
    }

    private void registerSerializer(
        Class javaType,
        QName xmlType,
        CombinedSerializer ser)
        throws Exception {

        register(
            javaType,
            xmlType,
            new SingletonSerializerFactory(ser),
            new SingletonDeserializerFactory((Deserializer) ser));
    }
}
