/*
 * $Id: TypeMappingRegistryInfo.java,v 1.2 2006-04-13 01:28:30 ofung Exp $
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

package com.sun.xml.rpc.processor.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public class TypeMappingRegistryInfo {

    public TypeMappingRegistryInfo() {
        xmlTypeMap = new HashMap();
        javaTypeMap = new HashMap();
        extraTypeNames = new HashSet();
        importedDocuments = new HashMap();
    }

    public void addMapping(TypeMappingInfo i) {
        xmlTypeMap.put(getKeyFor(i.getEncodingStyle(), i.getXMLType()), i);
        javaTypeMap.put(
            getKeyFor(i.getEncodingStyle(), i.getJavaTypeName()), i);
    }

    public TypeMappingInfo getTypeMappingInfo(String encodingStyle,
        QName xmlType) {
            
        return (TypeMappingInfo) xmlTypeMap.get(
            getKeyFor(encodingStyle, xmlType));
    }

    public TypeMappingInfo getTypeMappingInfo(String encodingStyle,
        String javaTypeName) {
            
        return (TypeMappingInfo) javaTypeMap.get(
            getKeyFor(encodingStyle, javaTypeName));
    }

    public Iterator getExtraTypeNames() {
        return extraTypeNames.iterator();
    }

    public void addExtraTypeName(String s) {
        extraTypeNames.add(s);
    }

    public int getExtraTypeNamesCount() {
        return extraTypeNames.size();
    }
    
    public Iterator getImportedDocuments() {
        return importedDocuments.values().iterator();
    }

    public ImportedDocumentInfo getImportedDocument(String namespace) {
        return (ImportedDocumentInfo) importedDocuments.get(namespace);
    }

    public void addImportedDocument(ImportedDocumentInfo i) {
        importedDocuments.put(i.getNamespace(), i);
    }

    private String getKeyFor(String s, QName q) {
        return getKeyFor(s, q.toString());
    }

    private String getKeyFor(String s, String t) {
        return s + "***" + t;
    }

    private Map xmlTypeMap;
    private Map javaTypeMap;
    private Set extraTypeNames;
    private Map importedDocuments;
}
