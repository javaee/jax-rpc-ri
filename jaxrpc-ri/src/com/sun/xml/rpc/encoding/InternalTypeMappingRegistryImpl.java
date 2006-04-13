/*
 * $Id: InternalTypeMappingRegistryImpl.java,v 1.2 2006-04-13 01:27:11 ofung Exp $
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

import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class InternalTypeMappingRegistryImpl
    implements InternalTypeMappingRegistry, SerializerConstants {
    protected static final Row NULL_ROW;
    protected static final Entry NULL_ENTRY;

    static {
        NULL_ROW = Row.createNull();
        NULL_ENTRY = Entry.createNull(NULL_ROW);
    }

    private Entry table[];
    private int count;
    private int threshold;
    private float loadFactor;

    protected TypeMappingRegistry registry = null;

    protected static class Row {
        String encoding;
        Class javaType;
        QName xmlType;
        Serializer serializer;
        Deserializer deserializer;

        Row(String encoding, Class javaType, QName xmlType) {
            this(encoding, javaType, xmlType, null, null);
        }
        Row(
            String encoding,
            Class javaType,
            QName xmlType,
            Serializer serializer,
            Deserializer deserializer) {
            if (encoding == null) {
                throw new IllegalArgumentException("encoding may not be null");
            }
            if (javaType == null && xmlType == null) {
                throw new IllegalArgumentException("javaType and xmlType may not both be null");
            }

            this.encoding = encoding;
            this.javaType = javaType;
            this.xmlType = xmlType;
            this.serializer = serializer;
            this.deserializer = deserializer;
        }
        
        static Row createNull() {
            return new Row();
        }
        
        private Row() {
            this.encoding = null;
            this.javaType = null;
            this.xmlType = null;
            this.serializer = null;
            this.deserializer = null;
        }

        public String getEncoding() {
            return encoding;
        }
        
        public Class getJavaType() {
            return javaType;
        }
        
        public QName getXMLType() {
            return xmlType;
        }
        
        public Serializer getSerializer() {
            return serializer;
        }
        
        public Deserializer getDeserializer() {
            return deserializer;
        }
    }

    protected static class Entry {
        Entry next;
        int hash;
        Row row;

        static Entry createNull(Row nullRow) {
            Entry nullEntry = new Entry(0, nullRow);
            nullEntry.next = nullEntry;
            return nullEntry;
        }
        
        private Entry(int hash, Row row) {
            if (row == null) {
                throw new IllegalArgumentException("row may not be null");
            }

            this.next = null;
            this.hash = hash;
            this.row = row;
        }
        Entry(Entry next, int hash, Row row) {
            this(hash, row);
            if (next == null) {
                throw new IllegalArgumentException("next may not be null");
            }
            this.next = next;
        }

        boolean matches(String encoding, Class javaType) {
            return row.encoding.equals(encoding)
                && row.javaType != null ? row.javaType.equals(javaType) : false;
        }
        
        boolean matches(String encoding, QName xmlType) {
            return row.encoding.equals(encoding)
                && row.xmlType != null ? row.xmlType.equals(xmlType) : false;
        }
        
        boolean matches(String encoding, Class javaType, QName xmlType) {
            return (
                row.xmlType != null
                    ? row.xmlType.equals(xmlType)
                    : xmlType == null)
                && (row.javaType != null
                    ? row.javaType.equals(javaType)
                    : javaType == null)
                && row.encoding.equals(encoding);
        }
        Entry getEntryMatching(String encoding, Class javaType) {
            Entry candidate = this;
            while (candidate != NULL_ENTRY
                && !candidate.matches(encoding, javaType)) {
                candidate = candidate.next;
            }

            return candidate;
        }
        Entry getEntryMatching(String encoding, QName xmlType) {
            Entry candidate = this;
            while (candidate != NULL_ENTRY
                && !candidate.matches(encoding, xmlType)) {
                candidate = candidate.next;
            }
            return candidate;
        }
        Entry getEntryMatching(
            String encoding,
            Class javaType,
            QName xmlType) {
            Entry candidate = this;

            while (candidate != NULL_ENTRY
                && !candidate.matches(encoding, javaType, xmlType)) {
                candidate = candidate.next;
            }

            return candidate;
        }
    }

    private int hashToIndex(int hash) {
        return (hash & 0x7FFFFFFF) % table.length;
    }
    
    private Entry get(int hash) {
        return table[hashToIndex(hash)];
    }
    
    private Entry put(int hash, Row row) {
        if (count >= threshold) {
            rehash();
        }
        int index = hashToIndex(hash);
        table[index] = new Entry(table[index], hash, row);
        ++count;
        return table[index];
    }

    private void rehash() {
        int oldCapacity = table.length;
        Entry[] oldMap = table;

        int newCapacity = oldCapacity * 2 + 1;
        Entry[] newMap = new Entry[newCapacity];
        Arrays.fill(newMap, NULL_ENTRY);

        threshold = (int) (newCapacity * loadFactor);
        table = newMap;

        for (int i = oldCapacity; i-- > 0;) {
            for (Entry old = oldMap[i]; old != NULL_ENTRY;) {
                Entry e = old;
                old = old.next;

                int index = hashToIndex(e.hash);
                e.next = table[index];
                table[index] = e;
            }
        }
    }

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public InternalTypeMappingRegistryImpl(TypeMappingRegistry registry) {
        init();
        this.registry = registry;
        setupDynamicSerializers(SOAPVersion.SOAP_11);
        setupDynamicSerializers(SOAPVersion.SOAP_12);
    }

    protected void init() {
        int initialCapacity = 57;
        table = new Entry[initialCapacity];
        Arrays.fill(table, NULL_ENTRY);
        count = 0;
        loadFactor = .75f;
        threshold = (int) (initialCapacity * loadFactor);
    }

    protected void setupDynamicSerializers(SOAPVersion ver) {
        init(ver);
        try {
            ExtendedTypeMapping soapMappings =
                (ExtendedTypeMapping) registry.getTypeMapping(
                    soapEncodingConstants.getURIEncoding());
            if (soapMappings != null) {
                CombinedSerializer anyTypeSerializer =
                    new DynamicSerializer(
                        SchemaConstants.QNAME_TYPE_URTYPE,
                        ENCODE_TYPE,
                        NULLABLE,
                        soapEncodingConstants.getSOAPEncodingNamespace(),
                        ver);
                anyTypeSerializer =
                    new ReferenceableSerializerImpl(
                        DONT_SERIALIZE_AS_REF,
                        anyTypeSerializer,
                        ver);
                ((Initializable) anyTypeSerializer).initialize(this);

                soapMappings.register(
                    Object.class,
                    SchemaConstants.QNAME_TYPE_URTYPE,
                    new SingletonSerializerFactory(anyTypeSerializer),
                    new SingletonDeserializerFactory(
                        (Deserializer) anyTypeSerializer));

                // Fix for bug 4773552
                //                final QName ELEMENT_NAME = new QName("element");
                final QName ELEMENT_NAME = null;
                CombinedSerializer polymorphicArraySerializer =
                    new PolymorphicArraySerializer(
                        soapEncodingConstants.getQNameEncodingArray(),
                        DONT_ENCODE_TYPE,
                        NULLABLE,
                        soapEncodingConstants.getURIEncoding(),
                        ELEMENT_NAME,
                        ver);
                polymorphicArraySerializer =
                    new ReferenceableSerializerImpl(
                        DONT_SERIALIZE_AS_REF,
                        polymorphicArraySerializer,
                        ver);
                ((Initializable) polymorphicArraySerializer).initialize(this);

                soapMappings.register(
                    Object[].class,
                    soapEncodingConstants.getQNameEncodingArray(),
                    new SingletonSerializerFactory(polymorphicArraySerializer),
                    new SingletonDeserializerFactory(
                        (Deserializer) polymorphicArraySerializer));
            }
        } catch (Exception e) {
            throw new EncodingException(
                "nestedEncodingError",
                new LocalizableExceptionAdapter(e));
        }
    }

    protected Row getRowMatching(
        String encoding,
        Class javaType,
        QName xmlType) {
            
        int hash =
            encoding.hashCode() ^ javaType.hashCode() ^ xmlType.hashCode();
        Entry matchingRowEntry =
            get(hash).getEntryMatching(encoding, javaType, xmlType);
        if (matchingRowEntry == NULL_ENTRY) {
            Row row = new Row(encoding, javaType, xmlType);

            put(encoding.hashCode() ^ javaType.hashCode(), row);
            put(encoding.hashCode() ^ xmlType.hashCode(), row);
            matchingRowEntry = put(hash, row);
        }
        return matchingRowEntry.row;
    }

    protected Row getRowMatching(String encoding, QName xmlType) {
        int hash = encoding.hashCode() ^ xmlType.hashCode();
        Entry matchingRowEntry = get(hash).getEntryMatching(encoding, xmlType);
        if (matchingRowEntry == NULL_ENTRY) {
            Row row = new Row(encoding, null, xmlType);

            matchingRowEntry = put(hash, row);
        }
        return matchingRowEntry.row;
    }

    protected Row getRowMatching(String encoding, Class javaType) {
        int hash = encoding.hashCode() ^ javaType.hashCode();
        Entry matchingRowEntry = get(hash).getEntryMatching(encoding, javaType);
        if (matchingRowEntry == NULL_ENTRY) {
            Row row = new Row(encoding, javaType, null);

            matchingRowEntry = put(hash, row);
        }
        return matchingRowEntry.row;
    }

    public Serializer getSerializer(
        String encoding,
        Class javaType,
        QName xmlType)
        throws Exception {
            
        Row row;
        if (javaType == null) {
            if (xmlType == null) {
                throw new IllegalArgumentException("getSerializer requires a Java type and/or an XML type");
            }
            row = getRowMatching(encoding, xmlType);
        } else if (xmlType == null) {
            row = getRowMatching(encoding, javaType);
        } else {
            row = getRowMatching(encoding, javaType, xmlType);
        }

        if (row.serializer == null) {
            TypeMapping mapping =
                TypeMappingUtil.getTypeMapping(registry, encoding);
            Serializer serializer =
                TypeMappingUtil.getSerializer(mapping, javaType, xmlType);
            row.serializer = serializer;

            if (serializer instanceof Initializable) {
                ((Initializable) serializer).initialize(this);
            }
        }
        return row.serializer;
    }

    public Serializer getSerializer(String encoding, Class javaType)
        throws Exception {
            
        return getSerializer(encoding, javaType, null);
    }

    public Serializer getSerializer(String encoding, QName xmlType)
        throws Exception {
            
        return getSerializer(encoding, null, xmlType);
    }

    public Deserializer getDeserializer(
        String encoding,
        Class javaType,
        QName xmlType)
        throws Exception {
            
        Row row;
        if (javaType == null) {
            if (xmlType == null) {
                throw new IllegalArgumentException("getSerializer requires a Java type and/or an XML type");
            }
            row = getRowMatching(encoding, xmlType);
        } else if (xmlType == null) {
            row = getRowMatching(encoding, javaType);
        } else {
            row = getRowMatching(encoding, javaType, xmlType);
        }

        if (row.deserializer == null) {
            TypeMapping mapping =
                TypeMappingUtil.getTypeMapping(registry, encoding);
            Deserializer deserializer =
                TypeMappingUtil.getDeserializer(mapping, javaType, xmlType);
            row.deserializer = deserializer;

            if (deserializer instanceof Initializable) {
                ((Initializable) deserializer).initialize(this);
            }
        }
        return row.deserializer;
    }

    public Deserializer getDeserializer(String encoding, Class javaType)
        throws Exception {
            
        return getDeserializer(encoding, javaType, null);
    }

    public Deserializer getDeserializer(String encoding, QName xmlType)
        throws Exception {
            
        return getDeserializer(encoding, null, xmlType);
    }

    public Class getJavaType(String encoding, QName xmlType) throws Exception {
        Row row;
        if (xmlType == null) {
            throw new IllegalArgumentException("getJavaType requires an XML type");
        }

        row = getRowMatching(encoding, xmlType);

        if (row.javaType == null) {
            ExtendedTypeMapping mapping =
                (ExtendedTypeMapping) TypeMappingUtil.getTypeMapping(
                    registry,
                    encoding);
            if (mapping != null) {
                return mapping.getJavaType(xmlType);
            }
            return null;
        }
        return row.javaType;
    }
    public QName getXmlType(String encoding, Class javaType) throws Exception {
        Row row;
        if (javaType == null) {
            throw new IllegalArgumentException("getXmlType requires a Java type");
        }

        row = getRowMatching(encoding, javaType);

        if (row.xmlType == null) {
            ExtendedTypeMapping mapping =
                (ExtendedTypeMapping) TypeMappingUtil.getTypeMapping(
                    registry,
                    encoding);
            if (mapping != null) {
                return mapping.getXmlType(javaType);
            }
            return null;
        }
        return row.xmlType;
    }
}
