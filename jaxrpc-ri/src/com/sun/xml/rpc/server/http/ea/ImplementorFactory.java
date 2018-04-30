/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.rpc.server.http.ea;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.ServiceException;

import com.sun.xml.rpc.server.http.Implementor;
import com.sun.xml.rpc.server.http.JAXRPCServletException;

/**
 * A factory for port implementation objects.
 *
 * @author JAX-RPC Development Team
 */
public class ImplementorFactory {

    public ImplementorFactory(ServletConfig servletConfig) {
        _servletConfig = servletConfig;
    }

    public ImplementorFactory(
        ServletConfig servletConfig,
        String configFilePath) {
        if (configFilePath == null) {
            throw new JAXRPCServletException("error.implementorFactory.noConfiguration");
        }
        _registry.readFrom(configFilePath);
        _servletConfig = servletConfig;
    }

    public ImplementorFactory(
        ServletConfig servletConfig,
        InputStream configInputStream) {
        if (configInputStream == null) {
            throw new IllegalArgumentException("error.implementorFactory.noInputStream");
        }
        _registry.readFrom(configInputStream);
        _servletConfig = servletConfig;
    }

    public Implementor getImplementorFor(String name) {

        synchronized (this) {
            Implementor implementor =
                (Implementor) _cachedImplementors.get(name);
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
            ImplementorInfo info = _registry.getImplementorInfo(name);
            if (_servletConfig != null) {
                Implementor implementor =
                    info.createImplementor(_servletConfig.getServletContext());
                implementor.init();

                Implementor existingImplementor = null;

                synchronized (this) {
                    existingImplementor =
                        (Implementor) _cachedImplementors.get(name);
                    if (existingImplementor == null) {
                        _cachedImplementors.put(name, implementor);
                    }
                }

                if (existingImplementor == null) {
                    return implementor;
                } else {
                    // it turns out we don't need the new implementor we just
                    // constructed!
                    implementor.destroy();
                    return existingImplementor;
                }
            } else {
                // NOTE - this branch is only used by some unit tests
                // as it does NOT include the necessary synchronization code

                Implementor implementor = info.createImplementor(null);
                _cachedImplementors.put(name, implementor);
                return implementor;
            }
        } catch (IllegalAccessException e) {
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                name);
        } catch (InstantiationException e) {
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                name);
        } catch (ServiceException e) {
            throw new JAXRPCServletException(
                "error.implementorFactory.newInstanceFailed",
                name);
        } catch (JAXRPCServletException e) {
            throw e;
        } catch (JAXRPCException e) {
            throw new JAXRPCServletException(
                "error.implementorFactory.servantInitFailed",
                name);
        }
    }

    public void releaseImplementor(String name, Implementor implementor) {
        // this seems overly defensive now; given the current implementation
        // of getImplementorFor(), the implementor cache will be monotonic

        boolean mustDestroy = false;
        synchronized (this) {
            Implementor cachedImplementor =
                (Implementor) _cachedImplementors.get(name);
            if (cachedImplementor != implementor) {
                mustDestroy = true;
            }
        }

        if (mustDestroy) {
            implementor.destroy();
        }
    }

    public Iterator names() {
        return _registry.names();
    }

    public void destroy() {
        // this "if" is there because if the server configuration was null,
        // init() was not called, so we shouldn't call destroy either
        if (_servletConfig != null) {

            for (Iterator iter = _cachedImplementors.values().iterator();
                iter.hasNext();
                ) {
                Implementor implementor = (Implementor) iter.next();
                implementor.destroy();
            }
        }

        try {
            _cachedImplementors.clear();
        } catch (UnsupportedOperationException e) {
        }
    }

    protected ServletConfig _servletConfig;
    protected ImplementorRegistry _registry = new ImplementorRegistry();
    protected Map _cachedImplementors = new HashMap();
}
