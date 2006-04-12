/*
 * $Id: ImplementorCacheDelegateImpl.java,v 1.1 2006-04-12 20:33:40 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
