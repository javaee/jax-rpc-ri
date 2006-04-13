/*
 * $Id: ServiceInfo.java,v 1.2 2006-04-13 01:26:49 ofung Exp $
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

/**
 * @author JAX-RPC Development Team
 */
package com.sun.xml.rpc.client.dii;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

public class ServiceInfo {
    protected Map portInfoMap;
    protected String defaultNamespace;

    //stores service information from examined wsdl definitions
    //stores service port information in a port map
    public ServiceInfo() {
        init();
    }

    protected void init() {
        portInfoMap = new HashMap();
        defaultNamespace = "";
    }

    public void setDefaultNamespace(String namespace) {
        defaultNamespace = namespace;
    }

    public PortInfo getPortInfo(QName portName) {
        PortInfo port = (PortInfo) portInfoMap.get(portName);
        if (port == null) {
            port = new PortInfo(portName);
            port.setDefaultNamespace(defaultNamespace);
            portInfoMap.put(portName, port);
        }

        return port;
    }

    public Iterator getPortNames() {
        return portInfoMap.keySet().iterator();
    }
}