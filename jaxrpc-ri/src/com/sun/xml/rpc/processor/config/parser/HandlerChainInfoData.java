/*
 * $Id: HandlerChainInfoData.java,v 1.1 2006-04-12 20:34:04 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package com.sun.xml.rpc.processor.config.parser;

import com.sun.xml.rpc.processor.config.HandlerChainInfo;

/**
 *
 * @author JAX-RPC Development Team
 */
public class HandlerChainInfoData {

    public HandlerChainInfoData() {
    }

    public HandlerChainInfo getClientHandlerChainInfo() {
        return client;
    }

    public void setClientHandlerChainInfo(HandlerChainInfo i) {
        client = i;
    }

    public HandlerChainInfo getServerHandlerChainInfo() {
        return server;
    }

    public void setServerHandlerChainInfo(HandlerChainInfo i) {
        server = i;
    }

    private HandlerChainInfo client;
    private HandlerChainInfo server;
}
