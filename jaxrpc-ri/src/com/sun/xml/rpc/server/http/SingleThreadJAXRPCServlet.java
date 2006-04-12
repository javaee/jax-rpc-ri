/*
 * $Id: SingleThreadJAXRPCServlet.java,v 1.1 2006-04-12 20:33:39 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http;

import javax.servlet.SingleThreadModel;

/**
 * A single-threaded version of the JAX-RPC dispatcher servlet.
 *
 * @author JAX-RPC Development Team
 */
public class SingleThreadJAXRPCServlet
    extends JAXRPCServlet
    implements SingleThreadModel {
}
