/*
 * $Id: ClientTransport.java,v 1.1 2006-04-12 20:35:20 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.soap.message.SOAPMessageContext;

/**
 * @author JAX-RPC Development Team
 */
public interface ClientTransport {
    public void invoke(String endpoint, SOAPMessageContext context);

    public void invokeOneWay(String endpoint, SOAPMessageContext context);
}
