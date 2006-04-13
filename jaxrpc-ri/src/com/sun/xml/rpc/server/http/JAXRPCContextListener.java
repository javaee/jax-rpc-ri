/*
 * $Id: JAXRPCContextListener.java,v 1.2 2006-04-13 01:32:06 ofung Exp $
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

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 * @author JAX-RPC Development Team
 */
public class JAXRPCContextListener
    implements ServletContextAttributeListener, ServletContextListener {

    public JAXRPCContextListener() {
        localizer = new Localizer();
        messageFactory =
            new LocalizableMessageFactory("com.sun.xml.rpc.resources.jaxrpcservlet");
    }

    public void attributeAdded(ServletContextAttributeEvent event) {
    }

    public void attributeRemoved(ServletContextAttributeEvent event) {
    }

    public void attributeReplaced(ServletContextAttributeEvent event) {
    }

    public void contextDestroyed(ServletContextEvent event) {
        context = null;
        if (logger.isLoggable(Level.INFO)) {
            logger.info(
                localizer.localize(
                    messageFactory.getMessage("listener.info.destroy")));
        }
    }

    public void contextInitialized(ServletContextEvent event) {
        context = event.getServletContext();
        if (logger.isLoggable(Level.INFO)) {
            logger.info(
                localizer.localize(
                    messageFactory.getMessage("listener.info.initialize")));
        }
    }

    private Localizer localizer;
    private LocalizableMessageFactory messageFactory;
    private ServletContext context;

    private static final Logger logger =
        Logger.getLogger(
            com.sun.xml.rpc.util.Constants.LoggingDomain + ".server.http");
}
