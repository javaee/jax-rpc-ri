/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Id: TieBase.java,v 1.3 2007-07-13 23:36:22 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
