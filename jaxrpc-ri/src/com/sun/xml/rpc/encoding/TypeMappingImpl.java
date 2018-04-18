/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: TypeMappingImpl.java,v 1.3 2007-07-13 23:35:58 ofung Exp $
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

package com.sun.xml.rpc.encoding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * An implementation of the standard TypeMapping interface
 *
 * @author JAX-RPC Development Team
 */
public class TypeMappingImpl implements ExtendedTypeMapping {
    protected static final String[] EMPTY_STRING_ARRAY = new String[0];
    protected static final boolean UNIQUE_IS_REQUIRED = true;
    protected static final boolean UNIQUE_IS_OPTIONAL = false;
    protected static final Row NULL_ROW;
    protected static final Entry NULL_ENTRY;

    static {
        NULL_ROW = new Row();
        NULL_ENTRY = new Entry(null, 0, NULL_ROW);
        NULL_ENTRY.next = NULL_ENTRY;
    }

    private Entry table[];
    private int count;
    private int threshold;
    private float loadFactor;

    protected ExtendedTypeMapping parent = null;
    protected String[] encodingURIs = EMPTY_STRING_ARRAY;
    protected List tuples = new ArrayList();

    public static class Row implements TypeMappingDescriptor {
        Class javaType;
        QName xmlType;
        SerializerFactory serializerFactory;
        DeserializerFactory deserializerFactory;

        Row(
            Class javaType,
            QName xmlType,
            SerializerFactory sf,
            DeserializerFactory dsf) {
            if (javaType == null) {
                throw new IllegalArgumentException("javaType may not be null");
            }
            if (xmlType == null) {
                throw new IllegalArgumentException("xmlType may not be null");
            }
            if (sf == null) {
                throw new IllegalArgumentException("serializerFactory may not be null");
            }
            if (dsf == null) {
                throw new IllegalArgumentException("deserializerFactory may not be null");
            }

            this.javaType = javaType;
            this.xmlType = xmlType;
            this.serializerFactory = sf;
            this.deserializerFactory = dsf;
        }
        Row() {
            this.javaType = null;
            this.xmlType = null;
            this.serializerFactory = null;
            this.deserializerFactory = null;
        }

        public Class getJavaType() {
            return javaType;
        }
        public QName getXMLType() {
            return xmlType;
        }
        public SerializerFactory getSerializer() {
            return serializerFactory;
        }
        public DeserializerFactory getDeserializer() {
            return deserializerFactory;
        }
    }

    protected static class Entry {
        Entry next;
        int hash;
        Row row;

        Entry(Entry next, int hash, Row row) {
            if (row == null) {
                throw new IllegalArgumentException("row may not be null");
            }

            this.next = next;
            this.hash = hash;
            this.row = row;
        }

        Entry getEntryMatching(Class javaType) {
            Entry candidate = this;
            while (candidate != NULL_ENTRY
                && !candidate.row.javaType.equals(javaType)) {
                candidate = candidate.next;
            }

            return candidate;
        }
        Entry getEntryMatchingSuperclassOf(Class javaType) {
            Entry bestCandidate = NULL_ENTRY;
            Entry currentCandidate = this;

            while (currentCandidate != NULL_ENTRY) {
                if (currentCandidate.matchesSuperclassOf(javaType)) {
                    if (bestCandidate == NULL_ENTRY
                        || currentCandidate.matchesSubclassOf(
                            bestCandidate.row.javaType)) {
                        bestCandidate = currentCandidate;
                    }
                }
                currentCandidate = currentCandidate.next;
            }

            return bestCandidate;
        }

        boolean matchesSubclassOf(Class javaType) {
            Class currentJavaType = row.javaType;
            return javaType.equals(currentJavaType)
                || (javaType.isAssignableFrom(currentJavaType)
                    && javaType != Object.class);
        }
        boolean matchesSuperclassOf(Class javaType) {
            Class currentJavaType = row.javaType;
            return currentJavaType.equals(javaType)
                || (currentJavaType.isAssignableFrom(javaType)
                    && currentJavaType != Object.class);
        }
        Entry getEntryMatching(QName xmlType) {
            Entry candidate = this;
            while (candidate != NULL_ENTRY
                && !candidate.row.xmlType.equals(xmlType)) {
                candidate = candidate.next;
            }
            return candidate;
        }
        Entry getNonPrimitiveEntryMatching(QName xmlType) {
            Entry candidate = this;
            while (candidate != NULL_ENTRY
                && (!candidate.row.xmlType.equals(xmlType)
                    || candidate.row.javaType.isPrimitive())) {
                candidate = candidate.next;
            }
            return candidate;
        }
        Entry getEntryMatching(Class javaType, QName xmlType) {
            Entry candidate = this;

            while (candidate != NULL_ENTRY
                && !(candidate.row.javaType.equals(javaType)
                    && candidate.row.xmlType.equals(xmlType))) {
                candidate = candidate.next;
            }

            return candidate;
        }
        Entry getEntryMatchingSuperclassOf(Class javaType, QName xmlType) {
            Entry bestCandidate = NULL_ENTRY;
            Entry currentCandidate = this;

            while (currentCandidate != NULL_ENTRY) {
                if (currentCandidate.matchesSuperclassOf(javaType)
                    && currentCandidate.row.xmlType.equals(xmlType)) {
                    if (bestCandidate == NULL_ENTRY
                        || currentCandidate.matchesSubclassOf(
                            bestCandidate.row.javaType)) {
                        bestCandidate = currentCandidate;
                    }
                }
                currentCandidate = currentCandidate.next;
            }

            return bestCandidate;
        }
    }

    private int hashToIndex(int hash) {
        return (hash & 0x7FFFFFFF) % table.length;
    }
    
    private Entry getHashBucket(int hash) {
        return table[hashToIndex(hash)];
    }
    
    private void put(int hash, Row row) {
        if (count >= threshold) {
            rehash();
        }
        int index = hashToIndex(hash);
        table[index] = new Entry(table[index], hash, row);
        ++count;
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

    public TypeMappingImpl() {
        init();
    }

    private void init() {
        loadFactor = .75f;

        parent = null;
        encodingURIs = EMPTY_STRING_ARRAY;
        tuples = new ArrayList();
        int initialCapacity = 57;
        table = new Entry[initialCapacity];
        Arrays.fill(table, NULL_ENTRY);
        count = 0;
        threshold = (int) (initialCapacity * loadFactor);
    }

    public TypeMappingImpl(ExtendedTypeMapping parent) {
        this();
        this.parent = parent;
    }

    public String[] getSupportedEncodings() {
        return encodingURIs;
    }

    public void setSupportedEncodings(String[] encodingURIs) {
        if (encodingURIs != null) {
            this.encodingURIs = encodingURIs;
        } else {
            this.encodingURIs = EMPTY_STRING_ARRAY;
        }
    }

    public boolean isRegistered(Class javaType, QName xmlType) {
        if (xmlType == null) {
            throw new IllegalArgumentException("XML type may not be null");
        }
        if (javaType == null) {
            throw new IllegalArgumentException("Java type may not be null");
        }
        int jTypeHash = javaType.hashCode();
        int xTypeHash = xmlType.hashCode();
        int combinedHash = jTypeHash ^ xTypeHash;

        Entry existingEntry =
            getHashBucket(combinedHash).getEntryMatching(javaType, xmlType);

        boolean isRegistered = existingEntry != NULL_ENTRY;
        if (!isRegistered && parent != null) {
            isRegistered = parent.isRegistered(javaType, xmlType);
        }

        return isRegistered;
    }

    public void register(
        Class javaType,
        QName xmlType,
        SerializerFactory sf,
        DeserializerFactory dsf) {

        if (xmlType == null) {
            throw new IllegalArgumentException("XML type may not be null");
        }
        if (javaType == null) {
            throw new IllegalArgumentException("Java type may not be null");
        }
        try {
            int jTypeHash = javaType.hashCode();
            int xTypeHash = xmlType.hashCode();
            int combinedHash = jTypeHash ^ xTypeHash;

            Row existingRow =
                getHashBucket(combinedHash).getEntryMatching(
                    javaType,
                    xmlType).row;

            if (existingRow != NULL_ROW) {
                // each row is aliased in three places. The data only needs to change once.
                existingRow.serializerFactory = sf;
                existingRow.deserializerFactory = dsf;
            } else {
                Row newRow = new Row(javaType, xmlType, sf, dsf);

                put(jTypeHash, newRow);
                put(xTypeHash, newRow);
                put(combinedHash, newRow);
                tuples.add(newRow);
            }
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.registration.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }

    protected Entry getEntryMatching(Class javaType) {
        return getHashBucket(javaType.hashCode()).getEntryMatching(javaType);
    }
    
    protected Entry getEntryMatching(QName xmlType) {
        return getHashBucket(xmlType.hashCode()).getEntryMatching(xmlType);
    }
    
    protected Entry getNonPrimitiveEntryMatching(QName xmlType) {
        return getHashBucket(xmlType.hashCode()).getNonPrimitiveEntryMatching(
            xmlType);
    }
    
    protected Entry getEntryMatching(Class javaType, QName xmlType) {
        return getHashBucket(
            javaType.hashCode() ^ xmlType.hashCode()).getEntryMatching(
            javaType,
            xmlType);
    }
    
    protected Entry getEntryClosestTo(Class javaType, QName xmlType) {
        Entry entry = getEntryMatching(javaType, xmlType);
        if (entry == NULL_ENTRY) {
            entry =
                getEntryMatching(xmlType).getEntryMatchingSuperclassOf(
                    javaType,
                    xmlType);
        }
        return entry;
    }
    
    protected Entry getEntryCloesestTo(Class javaType) {
        Entry matchingEntry = getEntryMatching(javaType);
        if (matchingEntry != NULL_ENTRY) {
            return matchingEntry;
        }

        List superTypes = new ArrayList();
        Class superClass = javaType.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            superTypes.add(superClass);
        }
        superTypes.addAll(Arrays.asList(javaType.getInterfaces()));

        for (int i = 0; i < superTypes.size(); ++i) {
            Class currentType = (Class) superTypes.get(i);
            if (currentType == null) {
                continue;
            }
            matchingEntry = getEntryMatching(currentType);
            if (matchingEntry != NULL_ENTRY) {
                break;
            }

            superClass = currentType.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) {
                superTypes.add(superClass);
            }
        }
        return matchingEntry;
    }
    
    protected SerializerFactory getSerializer(
        Class javaType,
        boolean uniqueRequired) {
            
        try {
            Entry matchingRowEntry = getEntryCloesestTo(javaType);
            SerializerFactory factory = matchingRowEntry.row.serializerFactory;

            return factory;
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.retrieval.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }
    
    protected SerializerFactory getSerializer(
        QName xmlType,
        boolean uniqueRequired) {
            
        try {
            Entry matchingRowEntry = getNonPrimitiveEntryMatching(xmlType);
            SerializerFactory factory = matchingRowEntry.row.serializerFactory;

            if (uniqueRequired
                && matchingRowEntry.next.getNonPrimitiveEntryMatching(xmlType)
                    != NULL_ENTRY) {
                return null;
            }

            return factory;
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.retrieval.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }
    
    protected DeserializerFactory getDeserializer(
        Class javaType,
        boolean uniqueRequired) {
            
        try {
            Entry matchingRowEntry = getEntryMatching(javaType);
            DeserializerFactory factory =
                matchingRowEntry.row.deserializerFactory;

            if (uniqueRequired
                && matchingRowEntry.next.getEntryMatching(javaType)
                    != NULL_ENTRY) {
                return null;
            }

            return factory;
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.retrieval.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }
    
    protected DeserializerFactory getDeserializer(
        QName xmlType,
        boolean uniqueRequired) {
            
        try {
            Entry matchingRowEntry = getNonPrimitiveEntryMatching(xmlType);
            DeserializerFactory factory =
                matchingRowEntry.row.deserializerFactory;

            if (uniqueRequired
                && matchingRowEntry.next.getNonPrimitiveEntryMatching(xmlType)
                    != NULL_ENTRY) {
                return null;
            }

            return factory;
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.retrieval.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }
    protected Class getJavaType(QName xmlType, boolean uniqueRequired) {
        try {
            Entry matchingRowEntry = getNonPrimitiveEntryMatching(xmlType);
            Class javaType = matchingRowEntry.row.javaType;

            if (uniqueRequired
                && matchingRowEntry.next.getNonPrimitiveEntryMatching(xmlType)
                    != NULL_ENTRY) {
                return null;
            }

            return javaType;
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.retrieval.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }
    
    protected QName getXmlType(Class javaType, boolean uniqueRequired) {
        try {
            Entry matchingRowEntry = getEntryMatching(javaType);
            QName xmlType = matchingRowEntry.row.xmlType;

            if (uniqueRequired
                && matchingRowEntry.next.getEntryMatching(javaType)
                    != NULL_ENTRY) {
                return null;
            }

            return xmlType;
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.retrieval.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }

    public SerializerFactory getSerializer(Class javaType, QName xmlType) {
        SerializerFactory factory;

        if (javaType == null) {
            if (xmlType == null) {
                throw new IllegalArgumentException("getSerializer requires a Java type and/or an XML type");
            } else {
                factory = getSerializer(xmlType, UNIQUE_IS_OPTIONAL);
            }
        } else if (xmlType == null) {
            factory = getSerializer(javaType, UNIQUE_IS_OPTIONAL);
        } else {

            try {
                factory =
                    getEntryClosestTo(javaType, xmlType).row.serializerFactory;
            } catch (Exception e) {
                throw new TypeMappingException(
                    "typemapping.retrieval.failed.nested.exception",
                    new LocalizableExceptionAdapter(e));
            }
        }

        if (factory == null && parent != null) {
            factory = parent.getSerializer(javaType, xmlType);
        }

        return factory;
    }

    public DeserializerFactory getDeserializer(Class javaType, QName xmlType) {
        DeserializerFactory factory = null;
        if (javaType == null) {
            if (xmlType == null) {
                throw new IllegalArgumentException("getDeserializer requires a Java type and/or an XML type");
            }
            factory = getDeserializer(xmlType, UNIQUE_IS_OPTIONAL);
        } else if (xmlType == null) {
            factory = getDeserializer(javaType, UNIQUE_IS_OPTIONAL);
        } else {

            try {
                factory =
                    getEntryClosestTo(
                        javaType,
                        xmlType).row.deserializerFactory;
            } catch (Exception e) {
                throw new TypeMappingException(
                    "typemapping.retrieval.failed.nested.exception",
                    new LocalizableExceptionAdapter(e));
            }
        }

        if (factory == null && parent != null) {
            factory = parent.getDeserializer(javaType, xmlType);
        }

        return factory;
    }

    public void removeSerializer(Class javaType, QName xmlType) {
        if (javaType == null || xmlType == null) {
            throw new IllegalArgumentException();
        }

        try {
            getEntryMatching(javaType, xmlType).row.serializerFactory = null;
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.retrieval.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }

    public void removeDeserializer(Class javaType, QName xmlType) {
        if (javaType == null || xmlType == null) {
            throw new IllegalArgumentException();
        }

        try {
            getEntryMatching(javaType, xmlType).row.deserializerFactory = null;
        } catch (Exception e) {
            throw new TypeMappingException(
                "typemapping.retrieval.failed.nested.exception",
                new LocalizableExceptionAdapter(e));
        }
    }

    public Class getJavaType(QName xmlType) {
        if (xmlType == null) {
            throw new IllegalArgumentException("non null xmlType required");
        }

        Class javaType = getJavaType(xmlType, UNIQUE_IS_OPTIONAL);

        if (javaType == null && parent != null) {
            javaType = parent.getJavaType(xmlType);
        }

        return javaType;
    }

    public QName getXmlType(Class javaType) {
        if (javaType == null) {
            throw new IllegalArgumentException("non null xmjavaType required");
        }

        QName xmlType = getXmlType(javaType, UNIQUE_IS_OPTIONAL);

        if (xmlType == null && parent != null) {
            xmlType = parent.getXmlType(javaType);
        }

        return xmlType;
    }
}
