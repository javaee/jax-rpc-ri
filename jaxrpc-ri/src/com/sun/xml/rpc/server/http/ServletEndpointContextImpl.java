/*
 * $Id: ServletEndpointContextImpl.java,v 1.2 2006-04-13 01:32:11 ofung Exp $
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
