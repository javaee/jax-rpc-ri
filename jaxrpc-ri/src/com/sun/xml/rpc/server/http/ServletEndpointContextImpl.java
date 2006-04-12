/*
 * $Id: ServletEndpointContextImpl.java,v 1.1 2006-04-12 20:33:41 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.server.http;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.server.ServletEndpointContext;

/**
 * An implementation of the javax.xml.rpc.server.ServletEndpointContext interface.
 *
 * @author JAX-RPC Development Team
 */
public class ServletEndpointContextImpl implements ServletEndpointContext {

    public ServletEndpointContextImpl(ServletContext c) {
        this(c, null);
    }

    public ServletEndpointContextImpl(ServletContext c, Implementor i) {
        servletContext = c;
        implementor = i;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public Principal getUserPrincipal() {
        return getHttpServletRequest().getUserPrincipal();
    }

    public HttpSession getHttpSession() {
        return getHttpServletRequest().getSession();
    }

    public MessageContext getMessageContext() {
        return (MessageContext) messageContext.get();
    }

    public void setMessageContext(MessageContext c) {
        messageContext.set(c);
    }

    public HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) httpRequest.get();
    }

    public void setHttpServletRequest(HttpServletRequest r) {
        httpRequest.set(r);
    }

    public void clear() {
        setMessageContext(null);
        setHttpServletRequest(null);
    }

    public boolean isUserInRole(String role) {
        return ((HttpServletRequest) httpRequest.get()).isUserInRole(role);
    }

    public Implementor getImplementor() {
        return implementor;
    }

    private ServletContext servletContext;
    private Implementor implementor;

    // thread local data
    private static ThreadLocal messageContext = new ThreadLocal();
    private static ThreadLocal httpRequest = new ThreadLocal();
}
