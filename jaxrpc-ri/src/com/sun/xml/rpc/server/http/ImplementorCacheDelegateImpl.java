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
 * $Id: ImplementorCacheDelegateImpl.java,v 1.3 2007-07-13 23:36:22 ofung Exp $
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

package com.sun.xml.rpc.server.http;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.ServiceException;

import com.sun.xml.rpc.server.Tie;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ImplementorCacheDelegateImpl
    extends com.sun.xml.rpc.spi.runtime.ImplementorCacheDelegate {

    public ImplementorCacheDelegateImpl(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
        this.servletContext = servletConfig.getServletContext();
        cachedImplementors = new HashMap();
    }

    // bug fix: 4900801 - have the signature exactly as of 
    // com.sun.xml.rpc.spi.runtime.ImplementorCacheDelegate() so that this
    // method gets called and not the one with the superclass.
    public com.sun.xml.rpc.spi.runtime.Implementor getImplementorFor(
        com.sun.xml.rpc.spi.runtime.RuntimeEndpointInfo targetEndpoint) {

        synchronized (this) {
            Implementor implementor =
                (Implementor) cachedImplementors.get(targetEndpoint);
            if (implementor != null) {
                return implementor;
            }
        }

        // NOTE - here we avoid synchronizing so that if an init() method never
        // terminates, we don't block the whole JAX-RPC dispatching engine
        // the drawback is that sometimes we create multiple implementors for
        // the same endpoint, and all but one will be destroyed immediately
        // thereafter

        try {
            if (servletConfig != null) {

                Tie tie = (Tie) targetEndpoint.getTieClass().newInstance();
                Remote servant =
                    (Remote) targetEndpoint
                        .getImplementationClass()
                        .newInstance();
                tie.setTarget(servant);

                Implementor implementor = new Implementor(servletContext, tie);
                implementor.init();

                postImplementorInit(
                    implementor,
                    (RuntimeEndpointInfo) targetEndpoint);

                Implementor existingImplementor = null;

                synchronized (this) {
                    existingImplementor =
                        (Implementor) cachedImplementors.get(targetEndpoint);
                    if (existingImplementor == null) {
                        cachedImplementors.put(targetEndpoint, implementor);
                    }
                }

                if (existingImplementor == null) {
                    return implementor;
                } else {
                    // it turns out we don't need the new implementor we just
                    // constructed!
                    preImplementorDestroy(implementor);
                    implementor.destroy();
                    return existingImplementor;
                }
            } else {
                // NOTE - this branch is only used by some unit tests
                // as it does NOT include the necessary synchronization code

                Tie tie = (Tie) targetEndpoint.getTieClass().newInstance();
                Remote servant =
                    (Remote) targetEndpoint
                        .getImplementationClass()
                        .newInstance();
                tie.setTarget(servant);

                Implementor implementor = new Implementor(null, tie);
                cachedImplementors.put(targetEndpoint, implementor);
                return implementor;
            }
        } catch (IllegalAccessException e) {
        	logger.log(Level.SEVERE, e.getMessage(), e);
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                ((RuntimeEndpointInfo) targetEndpoint).getName());
        } catch (InstantiationException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                ((RuntimeEndpointInfo) targetEndpoint).getName());
        } catch (ServiceException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                ((RuntimeEndpointInfo) targetEndpoint).getName());
        } catch (JAXRPCServletException e) {
            throw e;
        } catch (JAXRPCException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
            throw new JAXRPCServletException(
                "error.implementorFactory.servantInitFailed",
                ((RuntimeEndpointInfo) targetEndpoint).getName());
        }
    }

    public void releaseImplementor(
        RuntimeEndpointInfo targetEndpoint,
        Implementor implementor) {
        // this seems overly defensive now; given the current implementation
        // of getImplementorFor(), the implementor cache will be monotonic

        boolean mustDestroy = false;
        synchronized (this) {
            Implementor cachedImplementor =
                (Implementor) cachedImplementors.get(targetEndpoint);
            if (cachedImplementor != implementor) {
                mustDestroy = true;
            }
        }

        if (mustDestroy) {
            preImplementorDestroy(implementor);
            implementor.destroy();
        }
    }

    public void destroy() {
        // this "if" is there because if the server configuration was null,
        // init() was not called, so we shouldn't call destroy either
        if (servletConfig != null) {

            for (Iterator iter = cachedImplementors.values().iterator();
                iter.hasNext();
                ) {
                Implementor implementor = (Implementor) iter.next();
                preImplementorDestroy(implementor);
                implementor.destroy();
            }
        }

        try {
            cachedImplementors.clear();
        } catch (UnsupportedOperationException e) {
        }
    }

    protected void postImplementorInit(
        Implementor implementor,
        RuntimeEndpointInfo targetEndpoint) {
    }

    protected void preImplementorDestroy(Implementor implementor) {
    }

    private ServletConfig servletConfig;
    private ServletContext servletContext;
    private Map cachedImplementors = new HashMap();
    private static final Logger logger =
        Logger.getLogger(
            com.sun.xml.rpc.util.Constants.LoggingDomain + ".server.http");
}
