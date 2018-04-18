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
 * $Id: Model.java,v 1.3 2007-07-13 23:36:04 ofung Exp $
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

package com.sun.xml.rpc.processor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.ImportedDocumentInfo;

/**
 *
 * @author JAX-RPC Development Team
 */ 
public class Model extends ModelObject
    implements com.sun.xml.rpc.spi.model.Model {
    
    public Model() {
    }
    
    public Model(QName name) {
        this.name = name;
    }
    
    public QName getName() {
        return name;
    }
    
    public void setName(QName n) {
        name = n;
    }
    
    public String getTargetNamespaceURI() {
        return targetNamespace;
    }
    
    public void setTargetNamespaceURI(String s) {
        targetNamespace = s;
    }
    
    public void addService(Service service) {
        if (servicesByName.containsKey(service.getName())) {
            throw new ModelException("model.uniqueness");
        }
        services.add(service);
        servicesByName.put(service.getName(), service);
    }
    
    public Iterator getServices() {
        return services.iterator();
    }
    
    public Service getServiceByName(QName name) {
        if (servicesByName.size() != services.size()) {
            initializeServicesByName();
        }
        return (Service)servicesByName.get(name);
    }
    
    /* serialization */
    public List getServicesList() {
        return services;
    }
    
    /* serialization */
    public void setServicesList(List l) {
        services = l;
    }
    
    private void initializeServicesByName() {
        servicesByName = new HashMap();
        if (services != null) {
            for (Iterator iter = services.iterator(); iter.hasNext();) {
                Service service = (Service)iter.next();
                if (service.getName() != null &&
                    servicesByName.containsKey(service.getName())) {
                        
                    throw new ModelException("model.uniqueness");
                }
                servicesByName.put(service.getName(), service);
            }
        }
    }
    
    public void addExtraType(AbstractType type) {
        extraTypes.add(type);
    }
    
    public Iterator getExtraTypes() {
        return extraTypes.iterator();
    }
    
    /* serialization */
    public Set getExtraTypesSet() {
        return extraTypes;
    }
    
    /* serialization */
    public void setExtraTypesSet(Set s) {
        extraTypes = s;
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
    
    /* serialization */
    public Map getImportedDocumentsMap() {
        return importedDocuments;
    }
    
    /* serialization */
    public void setImportedDocumentsMap(Map m) {
        importedDocuments = m;
    }
    
    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    /**
     * @return
     */
    public String getSource() {
        return source;
    }
    
    /**
     * @param string
     */
    public void setSource(String string) {
        source = string;
    }
    
    private QName name;
    private String targetNamespace;
    private List services = new ArrayList();
    private Map servicesByName = new HashMap();
    private Set extraTypes = new HashSet();
    private Map importedDocuments = new HashMap();
    private String source;
}
