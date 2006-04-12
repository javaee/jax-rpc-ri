/*
 * $Id: WebServicesInfo.java,v 1.1 2006-04-12 20:34:14 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.tools.wsdeploy;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author JAX-RPC Development Team
 */
public class WebServicesInfo {
    
    public WebServicesInfo() {
        endpoints = new HashMap();
        endpointMappings = new HashMap();
        endpointClients = new HashMap();
    }
    
    public String getTargetNamespaceBase() {
        return targetNamespaceBase;
    }
    
    public void setTargetNamespaceBase(String s) {
        targetNamespaceBase = s;
    }
    
    public String getTypeNamespaceBase() {
        return typeNamespaceBase;
    }
    
    public void setTypeNamespaceBase(String s) {
        typeNamespaceBase = s;
    }
    
    public String getUrlPatternBase() {
        return urlPatternBase;
    }
    
    public void setUrlPatternBase(String s) {
        urlPatternBase = s;
    }
    
    public void add(EndpointClientInfo i) {
        endpointClients.put(i.getName(), i);
    }
    
    public void add(EndpointInfo i) {
        endpoints.put(i.getName(), i);
    }
    
    public Map getEndpoints() {
        return endpoints;
    }
    
    public void add(EndpointMappingInfo i) {
        endpointMappings.put(i.getName(), i);
    }
    
    public Map getEndpointMappings() {
        return endpointMappings;
    }
    
    public Map getEndpointClients() {
        return endpointClients;
    }
    
    private String targetNamespaceBase;
    private String typeNamespaceBase;
    private String urlPatternBase;
    private Map endpoints;
    private Map endpointMappings;
    private Map endpointClients;
}
