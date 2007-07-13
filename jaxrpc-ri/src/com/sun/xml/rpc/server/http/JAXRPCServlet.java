/*
 * $Id: JAXRPCServlet.java,v 1.3 2007-07-13 23:36:23 ofung Exp $
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

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 * The JAX-RPC dispatcher servlet.
 *
 * @author JAX-RPC Development Team
 */
public class JAXRPCServlet extends HttpServlet {
    
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        localizer = new Localizer();
        messageFactory =
            new LocalizableMessageFactory("com.sun.xml.rpc.resources.jaxrpcservlet");

        // workaround for servlet v 2.3 not requiring listeners before servlet
        ServletContext context = servletConfig.getServletContext();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }

        try {
            JAXRPCRuntimeInfoParser parser =
                new JAXRPCRuntimeInfoParser(classLoader);
            InputStream is = context.getResourceAsStream(JAXRPC_RI_RUNTIME);
            JAXRPCRuntimeInfo info = parser.parse(is);
            context.setAttribute(JAXRPC_RI_RUNTIME_INFO, info);
            // end workaround
            
            String delegateClassName =
                servletConfig.getInitParameter(DELEGATE_PROPERTY);

            if (delegateClassName == null
                && servletConfig.getInitParameter(EA_CONFIG_FILE_PROPERTY)
                    != null) {
                // use EA backward compatibility mode
                delegateClassName = EA_DELEGATE_CLASS_NAME;
            }

            if (delegateClassName == null) {
                delegateClassName = DEFAULT_DELEGATE_CLASS_NAME;
            }

            Class delegateClass =
                Class.forName(
                    delegateClassName,
                    true,
                    Thread.currentThread().getContextClassLoader());
            delegate = (ServletDelegate) delegateClass.newInstance();
            delegate.init(servletConfig);

        } catch (ServletException e) {
            logger.log(Level.SEVERE,e.getMessage(), e);
            throw e;
        } catch (Throwable e) {
            String message =
                localizer.localize(
                    messageFactory.getMessage(
                        "error.servlet.caughtThrowableInInit",
                        new Object[] { e }));
            logger.log(Level.SEVERE, message, e);
            throw new ServletException(message);
        }
    }

    public void destroy() {
        if (delegate != null) {
            delegate.destroy();
        }
    }

    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException {
        if (delegate != null) {
            delegate.doPost(request, response);
        }
    }

    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException {
        if (delegate != null) {
            delegate.doGet(request, response);
        }
    }

    protected ServletDelegate delegate = null;
    private LocalizableMessageFactory messageFactory;
    private Localizer localizer;

    private static final String DELEGATE_PROPERTY = "delegate";
    private static final String DEFAULT_DELEGATE_CLASS_NAME =
        "com.sun.xml.rpc.server.http.JAXRPCServletDelegate";

    private static final String EA_CONFIG_FILE_PROPERTY = "configuration.file";
    private static final String EA_DELEGATE_CLASS_NAME =
        "com.sun.xml.rpc.server.http.ea.JAXRPCServletDelegate";
    private static final String JAXRPC_RI_RUNTIME =
        "/WEB-INF/jaxrpc-ri-runtime.xml";

    public static final String JAXRPC_RI_RUNTIME_INFO =
        "com.sun.xml.rpc.server.http.info";
    public static final String JAXRPC_RI_PROPERTY_PUBLISH_WSDL =
        "com.sun.xml.rpc.server.http.publishWSDL";
    public static final String JAXRPC_RI_PROPERTY_PUBLISH_MODEL =
        "com.sun.xml.rpc.server.http.publishModel";
    public static final String JAXRPC_RI_PROPERTY_PUBLISH_STATUS_PAGE =
        "com.sun.xml.rpc.server.http.publishStatusPage";

    private static final Logger logger =
        Logger.getLogger(
            com.sun.xml.rpc.util.Constants.LoggingDomain + ".server.http");
}
