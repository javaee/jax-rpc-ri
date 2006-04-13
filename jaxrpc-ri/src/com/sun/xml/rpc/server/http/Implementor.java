/*
 * $Id: Implementor.java,v 1.2 2006-04-13 01:32:04 ofung Exp $
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
