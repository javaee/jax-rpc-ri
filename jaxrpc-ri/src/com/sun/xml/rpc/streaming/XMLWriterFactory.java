/*
 * $Id: XMLWriterFactory.java,v 1.3 2007-07-13 23:36:33 ofung Exp $
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
