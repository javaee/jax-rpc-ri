/*
 * $Id: WSDLPortInfo.java,v 1.1 2006-04-12 20:35:26 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http.ea;

/**
 * WSDLPortInfo contains information about a port inside a WSDL document.
 *
 * @author JAX-RPC Development Team
 */
public class WSDLPortInfo {

    public WSDLPortInfo(
        String targetNamespace,
        String serviceName,
        String portName) {
        this.targetNamespace = targetNamespace;
        this.serviceName = serviceName;
        this.portName = portName;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPortName() {
        return portName;
    }

    private String targetNamespace;
    private String serviceName;
    private String portName;
}
