/*
 * $Id: EndpointClientInfo.java,v 1.2 2006-04-13 01:33:37 ofung Exp $
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