/*
 * $Id: JAXRPCContextListener.java,v 1.1 2006-04-12 20:33:38 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
