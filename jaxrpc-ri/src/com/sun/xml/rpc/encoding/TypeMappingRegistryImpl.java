/*
 * $Id: TypeMappingRegistryImpl.java,v 1.2 2006-04-13 01:27:29 ofung Exp $
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
