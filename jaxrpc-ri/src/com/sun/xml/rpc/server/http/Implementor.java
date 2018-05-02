/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
