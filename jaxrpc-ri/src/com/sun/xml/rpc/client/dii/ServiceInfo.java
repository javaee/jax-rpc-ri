/*
 * $Id: ServiceInfo.java,v 1.1 2006-04-12 20:33:59 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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