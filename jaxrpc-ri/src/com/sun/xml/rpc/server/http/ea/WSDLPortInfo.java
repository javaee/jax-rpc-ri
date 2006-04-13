/*
 * $Id: WSDLPortInfo.java,v 1.2 2006-04-13 01:32:15 ofung Exp $
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
