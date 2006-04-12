/*
 * $Id: Implementor.java,v 1.1 2006-04-12 20:33:40 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http;

import java.rmi.Remote;

import javax.servlet.ServletContext;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import com.sun.xml.rpc.server.Tie;

/**
 * 
 * @author JAX-RPC Development Team
 */
public class Implementor implements com.sun.xml.rpc.spi.runtime.Implementor {

    public Implementor(
        ServletContext servletContext,
        com.sun.xml.rpc.spi.runtime.Tie tie) {
        this.tie = (Tie) tie;
        this.context = new ServletEndpointContextImpl(servletContext, this);
    }

    public com.sun.xml.rpc.spi.runtime.Tie getTie() {
        return tie;
    }

    public Remote getTarget() {
        return tie.getTarget();
    }

    public ServletEndpointContextImpl getContext() {
        return context;
    }

    public void init() throws ServiceException {
        Remote servant = tie.getTarget();
        if (servant != null && (servant instanceof ServiceLifecycle)) {
            ((ServiceLifecycle) servant).init(context);
        }
    }

    public void destroy() {
        Remote servant = tie.getTarget();
        if (servant != null && (servant instanceof ServiceLifecycle)) {
            ((ServiceLifecycle) servant).destroy();
        }
        tie.destroy();
    }

    private Tie tie;
    private Remote endpoint;
    private ServletEndpointContextImpl context;
}
