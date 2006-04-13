/*
 * $Id: XMLWriterFactory.java,v 1.2 2006-04-13 01:33:23 ofung Exp $
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

package com.sun.xml.rpc.streaming;

import java.io.OutputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.xml.parsers.FactoryConfigurationError;

/**
 * <p> Define a factory API to enable pluggable XMLWriter implementations. </p>
 *
 * @see XMLWriter
 *
 * @author JAX-RPC Development Team
 */
public abstract class XMLWriterFactory {

    protected XMLWriterFactory() {
    }

    /**
     * Obtain an instance of a factory.
     * Since factories are stateless, only one copy of a factory exists and is
     * returned to the application each time this method is called.
     *
     * The implementation class to be used can be overridden by setting the
     * com.sun.xml.rpc.streaming.XMLWriterFactory system property.
     *
     */
    public static XMLWriterFactory newInstance() {
        if (_instance == null) {
            String factoryImplName = getFactoryImplName();
            XMLWriterFactory factoryImpl;
            try {
                Class clazz = Class.forName(factoryImplName);
                _instance = (XMLWriterFactory) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                throw new FactoryConfigurationError(e);
            } catch (IllegalAccessException e) {
                throw new FactoryConfigurationError(e);
            } catch (InstantiationException e) {
                throw new FactoryConfigurationError(e);
            }
        }
        return _instance;
    }

    private static String getFactoryImplName() {
        String factoryImplName;
        try {
            factoryImplName =
                (String) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    // AccessControll problem
                    return System.getProperty(
                        "com.sun.xml.rpc.streaming.XMLWriterFactory",
                        "com.sun.xml.rpc.streaming.XMLWriterFactoryImpl");
                }
            });
        } catch (AccessControlException e) {
            factoryImplName = "com.sun.xml.rpc.streaming.XMLWriterFactoryImpl";
        }
        return factoryImplName;
    }

    /**
     * Obtain an XMLWriter on the given OutputStream using the default encoding
     * and XML declaration settings.
     *
     */
    public abstract XMLWriter createXMLWriter(OutputStream stream);

    /**
     * Obtain an XMLWriter on the given OutputStream using the given encoding
     * and the default XML declaration settings.
     *
     */
    public abstract XMLWriter createXMLWriter(
        OutputStream stream,
        String encoding);

    /**
     * Obtain an XMLWriter on the given OutputStream using the given encoding
     * and the given XML declaration settings.
     *
     */
    public abstract XMLWriter createXMLWriter(
        OutputStream stream,
        String encoding,
        boolean declare);

    private static XMLWriterFactory _instance;
}
