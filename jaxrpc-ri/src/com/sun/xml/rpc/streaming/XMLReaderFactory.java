/*
 * $Id: XMLReaderFactory.java,v 1.2 2006-04-13 01:33:19 ofung Exp $
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

import java.io.InputStream;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.xml.parsers.FactoryConfigurationError;

import org.xml.sax.InputSource;

/**
 * <p> Define a factory API to enable pluggable XMLReader implementations. </p>
 *
 * @see XMLReader
 *
 * @author JAX-RPC Development Team
 */
public abstract class XMLReaderFactory {

    protected XMLReaderFactory() {
    }

    /**
     * Obtain an instance of a factory.
     *
     * <p> Since factories are stateless, only one copy of a factory exists and
     * is returned to the application each time this method is called. </p>
     *
     * <p> The implementation class to be used can be overridden by setting the
     * com.sun.xml.rpc.streaming.XMLReaderFactory system property. </p>
     *
     */
    public static XMLReaderFactory newInstance() {
        if (_instance == null) {
            String factoryImplName = getFactoryImplName();
            XMLReaderFactory factoryImpl;
            try {
                Class clazz = Class.forName(factoryImplName);
                _instance = (XMLReaderFactory) clazz.newInstance();
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
                        "com.sun.xml.rpc.streaming.XMLReaderFactory",
                        "com.sun.xml.rpc.streaming.XMLReaderFactoryImpl");
                }
            });
        } catch (AccessControlException e) {
            factoryImplName = "com.sun.xml.rpc.streaming.XMLReaderFactoryImpl";
        }
        return factoryImplName;
    }
    /**
     * Obtain an XMLReader on the given InputStream.
     *
     */
    public abstract XMLReader createXMLReader(InputStream in);

    /**
     * Obtain an XMLReader on the given InputSource.
     *
     */
    public abstract XMLReader createXMLReader(InputSource source);

    /**
     * Obtain an XMLReader on the given InputStream.
     *
     */
    public abstract XMLReader createXMLReader(
        InputStream in,
        boolean rejectDTDs);

    /**
     * Obtain an XMLReader on the given InputSource.
     *
     */
    public abstract XMLReader createXMLReader(
        InputSource source,
        boolean rejectDTDs);

    private static XMLReaderFactory _instance;
}
