/*
 * $Id: Model.java,v 1.1 2006-04-12 20:33:07 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
