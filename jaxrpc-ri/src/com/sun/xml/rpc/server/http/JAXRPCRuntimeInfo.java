/*
 * $Id: JAXRPCRuntimeInfo.java,v 1.1 2006-04-12 20:33:40 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http;

import java.util.List;

/**
 * @author JAX-RPC Development Team
 */
public class JAXRPCRuntimeInfo {

    public JAXRPCRuntimeInfo() {
    }

    public List getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List l) {
        endpoints = l;
    }

    private List endpoints;
}
