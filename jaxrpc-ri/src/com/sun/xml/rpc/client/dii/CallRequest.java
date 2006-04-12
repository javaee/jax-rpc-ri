/*
 * $Id: CallRequest.java,v 1.1 2006-04-12 20:33:56 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii;

/**
 * @author JAX-RPC Development Team
 */
public class CallRequest {
    public BasicCall call;
    public Object request;

    public CallRequest(BasicCall call, Object request) {
        this.call = call;
        this.request = request;
    }
}