/*
 * $Id: EndpointMappingInfo.java,v 1.1 2006-04-12 20:34:15 kohlert Exp $
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
public class EndpointMappingInfo {
    
    public EndpointMappingInfo() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String s) {
        name = s;
    }
    
    public String getUrlPattern() {
        return urlPattern;
    }
    
    public void setUrlPattern(String s) {
        urlPattern = s;
    }
    
    private String name;
    private String urlPattern;
}
