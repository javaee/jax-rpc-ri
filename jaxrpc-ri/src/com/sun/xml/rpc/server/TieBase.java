/*
 * $Id: TieBase.java,v 1.1 2006-04-12 20:35:24 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.rpc.encoding.TypeMappingRegistry;

import com.sun.xml.rpc.client.HandlerChainImpl;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistryImpl;
import com.sun.xml.rpc.server.http.MessageContextProperties;

/**
 * The base class for all generated ties.
 *
 * @author JAX-RPC Development Team
 */
public abstract class TieBase extends StreamingHandler implements Tie {
    protected TypeMappingRegistry typeMappingRegistry;
    protected InternalTypeMappingRegistry internalTypeMappingRegistry;
    protected HandlerChainImpl handlerChain;
    private java.rmi.Remote _servant;

    public HandlerChainImpl getHandlerChain() {
        if (handlerChain == null) {

            // create empty handler chain so that client code can add handlers
            handlerChain = new HandlerChainImpl(new ArrayList());
        }
        return handlerChain;
    }

    protected TieBase(TypeMappingRegistry registry) throws Exception {
        typeMappingRegistry = registry;
        internalTypeMappingRegistry =
            new InternalTypeMappingRegistryImpl(registry);
    }

    /*
     * Set flag to let servlet delegate know that this is a one-way
     * operation and then flush the http response buffer to send
     * the response back to the client (before the tie processes
     * the message).
     */
    protected void flushHttpResponse(StreamingHandlerState state)
        throws IOException {
        state.getMessageContext().setProperty(
            MessageContextProperties.ONE_WAY_OPERATION,
            "true");
        javax.servlet.http.HttpServletResponse httpResp =
            (javax.servlet.http.HttpServletResponse) state
                .getMessageContext()
                .getProperty(
                MessageContextProperties.HTTP_SERVLET_RESPONSE);

        if (httpResp != null) {
            httpResp.setStatus(
                javax.servlet.http.HttpServletResponse.SC_ACCEPTED);
            httpResp.setContentType("text/xml");
            httpResp.flushBuffer();
            httpResp.getWriter().close();
        }
    }

    public void setTarget(java.rmi.Remote servant) {
        _servant = servant;
    }

    public java.rmi.Remote getTarget() {
        return _servant;
    }

    public void destroy() {
        if (handlerChain != null) {
            handlerChain.destroy();
        }
    }
}
