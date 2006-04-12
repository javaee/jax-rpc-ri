/*
 * $Id: EndpointClientInfo.java,v 1.1 2006-04-12 20:34:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.tools.wsdeploy;


/**
 *
 * @author JAX-RPC Development Team
 */
public class EndpointClientInfo {
    
    public EndpointClientInfo() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String s) {
        name = s;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String s) {
        displayName = s;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String s) {
        description = s;
    }
    
    public String getService() {
        return serviceName;
    }
    
    public void setService(String n) {
        serviceName = n;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String s) {
        model = s;
    }
    
    private String name;
    private String displayName;
    private String description;
    private String model;
    private String serviceName;
}
