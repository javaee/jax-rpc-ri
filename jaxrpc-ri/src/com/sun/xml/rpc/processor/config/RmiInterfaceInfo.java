/*
 * $Id: RmiInterfaceInfo.java,v 1.1 2006-04-12 20:34:51 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
