/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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
