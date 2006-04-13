/*
 * $Id: RmiInterfaceInfo.java,v 1.2 2006-04-13 01:28:28 ofung Exp $
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
 
package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.soap.SOAPVersion;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RmiInterfaceInfo {

    public RmiInterfaceInfo() {}

    public RmiModelInfo getParent() {
        return parent;
    }

    public void setParent(RmiModelInfo rsi) {
        parent = rsi;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public String getServantName() {
        return servantName;
    }

    public void setServantName(String s) {
        servantName = s;
    }

    public String getSOAPAction() {
        return soapAction;
    }

    public void setSOAPAction(String s) {
        soapAction = s;
    }

    public String getSOAPActionBase() {
        return soapActionBase;
    }

    public void setSOAPActionBase(String s) {
        soapActionBase = s;
    }
    
    public SOAPVersion getSOAPVersion() {
        return soapVersion;
    }
    
    public void setSOAPVersion(SOAPVersion version) {
        soapVersion = version;
    }

    public HandlerChainInfo getClientHandlerChainInfo() {
        if (clientHandlerChainInfo != null) {
            return clientHandlerChainInfo;
        } else {
            return parent.getClientHandlerChainInfo();
        }
    }

    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        clientHandlerChainInfo = i;
    }

    public HandlerChainInfo getServerHandlerChainInfo() {
        if (serverHandlerChainInfo != null) {
            return serverHandlerChainInfo;
        } else {
            return parent.getServerHandlerChainInfo();
        }
    }

    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        serverHandlerChainInfo = i;
    }

    private RmiModelInfo parent;
    private String soapAction;
    private String soapActionBase;
    private String name;
    private String servantName;
    private HandlerChainInfo clientHandlerChainInfo;
    private HandlerChainInfo serverHandlerChainInfo;
    private SOAPVersion soapVersion = SOAPVersion.SOAP_11;
}
