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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.sun.xml.rpc.server.http.JAXRPCServletException;

/**
 * A registry mapping port names to ImplementorInfo objects.
 *
 * @author JAX-RPC Development Team
 */
public class ImplementorRegistry {

    public ImplementorRegistry() {
    }

    public ImplementorInfo getImplementorInfo(String name) {
        ImplementorInfo info = (ImplementorInfo) _implementors.get(name);

        if (info == null) {
            throw new JAXRPCServletException(
                "error.implementorRegistry.unknownName",
                name);
        }

        return info;
    }

    public boolean containsName(String name) {
        return _implementors.containsKey(name);
    }

    public Iterator names() {
        return _implementors.keySet().iterator();
    }

    public void readFrom(String filename) {
        try {
            readFrom(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new JAXRPCServletException(
                "error.implementorRegistry.fileNotFound",
                filename);
        }
    }

    public void readFrom(InputStream inputStream) {

        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();

            int portCount =
                Integer.parseInt(properties.getProperty(PROPERTY_PORT_COUNT));
            for (int i = 0; i < portCount; ++i) {
                String portPrefix = PROPERTY_PORT + Integer.toString(i) + ".";
                String name =
                    properties.getProperty(portPrefix + PROPERTY_NAME);
                String tieClassName =
                    properties.getProperty(portPrefix + PROPERTY_TIE);
                String servantClassName =
                    properties.getProperty(portPrefix + PROPERTY_SERVANT);
                if (name == null
                    || tieClassName == null
                    || servantClassName == null) {
                    throw new JAXRPCServletException("error.implementorRegistry.incompleteInformation");
                }
                register(name, tieClassName, servantClassName);
            }
        } catch (IOException e) {
            throw new JAXRPCServletException("error.implementorRegistry.cannotReadConfiguration");
        }
    }

    public void register(
        String name,
        String tieClassName,
        String servantClassName) {
        Class tieClass = null;
        Class servantClass = null;
        try {
            tieClass =
                Thread.currentThread().getContextClassLoader().loadClass(
                    tieClassName);
        } catch (ClassNotFoundException e) {
            throw new JAXRPCServletException(
                "error.implementorRegistry.classNotFound",
                tieClassName);
        }

        try {
            servantClass =
                Thread.currentThread().getContextClassLoader().loadClass(
                    servantClassName);
        } catch (ClassNotFoundException e) {
            throw new JAXRPCServletException(
                "error.implementorRegistry.classNotFound",
                servantClassName);
        }

        register(name, new ImplementorInfo(tieClass, servantClass));
    }

    public void register(String name, ImplementorInfo info) {
        if (_implementors.containsKey(name)) {
            throw new JAXRPCServletException(
                "error.implementorRegistry.duplicateName",
                name);
        } else {
            _implementors.put(name, info);
        }
    }

    private Map _implementors = new HashMap();

    private final static String PROPERTY_PORT_COUNT = "portcount";
    private final static String PROPERTY_PORT = "port";
    private final static String PROPERTY_NAME = "name";
    private final static String PROPERTY_TIE = "tie";
    private final static String PROPERTY_SERVANT = "servant";
}
