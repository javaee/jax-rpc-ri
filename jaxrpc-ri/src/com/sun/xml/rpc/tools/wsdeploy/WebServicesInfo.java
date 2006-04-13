/*
 * $Id: WebServicesInfo.java,v 1.2 2006-04-13 01:33:40 ofung Exp $
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
