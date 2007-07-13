/*
 * $Id: TypeMappingRegistryInfo.java,v 1.3 2007-07-13 23:36:01 ofung Exp $
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
