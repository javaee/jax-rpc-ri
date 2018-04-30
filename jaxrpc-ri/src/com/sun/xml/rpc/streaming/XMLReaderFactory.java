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
