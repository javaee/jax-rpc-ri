/*
 * $Id: TypeMappingRegistryImpl.java,v 1.1 2006-04-12 20:33:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;

/**
 * An implementation of the standard TypeMappingRegistry interface
 *
 * @author JAX-RPC Development Team
 */

public class TypeMappingRegistryImpl
    implements TypeMappingRegistry, SerializerConstants {
    protected Map mappings;
    protected TypeMapping defaultMapping;

    public TypeMappingRegistryImpl() {
        init();
    }

    protected void init() {
        mappings = new HashMap();
        defaultMapping = null;
    }

    public TypeMapping register(String namespaceURI, TypeMapping mapping) {
        if (mapping == null || namespaceURI == null) {
            throw new IllegalArgumentException();
        }

        if (!mappingSupportsEncoding(mapping, namespaceURI)) {
            throw new TypeMappingException(
                "typemapping.mappingDoesNotSupportEncoding",
                namespaceURI);
        }

        TypeMapping oldMapping = (TypeMapping) mappings.get(namespaceURI);
        mappings.put(namespaceURI, mapping);
        return oldMapping;
    }
    
    public void registerDefault(TypeMapping mapping) {
        defaultMapping = mapping;
    }
    
    public TypeMapping getDefaultTypeMapping() {
        return defaultMapping;
    }
    
    public String[] getRegisteredEncodingStyleURIs() {
        Set namespaceSet = mappings.keySet();
        return (String[]) namespaceSet.toArray(new String[namespaceSet.size()]);
    }
    
    public TypeMapping getTypeMapping(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }

        TypeMapping mapping = (TypeMapping) mappings.get(namespaceURI);
        if (mapping == null) {
            mapping = defaultMapping;
        }
        return mapping;
    }
    
    public TypeMapping createTypeMapping() {
        return new TypeMappingImpl();
    }
    
    public TypeMapping unregisterTypeMapping(String namespaceURI) {
        return (TypeMapping) mappings.remove(namespaceURI);
    }
    
    public boolean removeTypeMapping(TypeMapping mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping cannot be null");
        }
        Set typeEntries = mappings.entrySet();
        Iterator eachEntry = typeEntries.iterator();
        boolean typeMappingFound = false;

        while (eachEntry.hasNext()) {
            Map.Entry currentEntry = (Map.Entry) eachEntry.next();
            if (mapping.equals(currentEntry.getValue())) {
                eachEntry.remove();
                typeMappingFound = true;
            }
        }
        return typeMappingFound;
    }
    
    public void clear() {
        mappings.clear();
    }
    
    protected static boolean mappingSupportsEncoding(
        TypeMapping mapping,
        String namespaceURI) {
        String[] encodings =
            ((TypeMappingImpl) mapping).getSupportedEncodings();
        for (int i = 0; i < encodings.length; ++i) {
            if (encodings[i].equals(namespaceURI)) {
                return true;
            }
        }

        return false;
    }
}
