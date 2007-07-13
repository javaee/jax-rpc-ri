/*
 * $Id: JAXRPCRuntimeInfoParser.java,v 1.3 2007-07-13 23:36:23 ofung Exp $
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderException;
import com.sun.xml.rpc.streaming.XMLReaderFactory;

/**
 * @author JAX-RPC Development Team
 */
public class JAXRPCRuntimeInfoParser {

    public JAXRPCRuntimeInfoParser(ClassLoader cl) {
        classLoader = cl;
    }

    public JAXRPCRuntimeInfo parse(InputStream is) {
        try {
            XMLReader reader =
                XMLReaderFactory.newInstance().createXMLReader(is);
            reader.next();
            return parseEndpoints(reader);
        } catch (XMLReaderException e) {
            throw new JAXRPCServletException("runtime.parser.xmlReader", e);
        }
    }

    protected JAXRPCRuntimeInfo parseEndpoints(XMLReader reader) {
        if (!reader.getName().equals(QNAME_ENDPOINTS)) {
            failWithFullName("runtime.parser.invalidElement", reader);
        }

        JAXRPCRuntimeInfo info = new JAXRPCRuntimeInfo();
        List endpoints = new ArrayList();

        String version = getMandatoryNonEmptyAttribute(reader, ATTR_VERSION);
        if (!version.equals(ATTRVALUE_VERSION_1_0)) {
            failWithLocalName(
                "runtime.parser.invalidVersionNumber",
                reader,
                version);
        }

        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(QNAME_ENDPOINT)) {
                RuntimeEndpointInfo rei = new RuntimeEndpointInfo();
                rei.setName(getMandatoryNonEmptyAttribute(reader, ATTR_NAME));
                String interfaceName =
                    getMandatoryNonEmptyAttribute(reader, ATTR_INTERFACE);
                rei.setRemoteInterface(loadClass(interfaceName));
                String implementationName =
                    getMandatoryNonEmptyAttribute(reader, ATTR_IMPLEMENTATION);
                rei.setImplementationClass(loadClass(implementationName));
                String tieName =
                    getMandatoryNonEmptyAttribute(reader, ATTR_TIE);
                rei.setTieClass(loadClass(tieName));
                rei.setModelFileName(getAttribute(reader, ATTR_MODEL));
                rei.setWSDLFileName(getAttribute(reader, ATTR_WSDL));
                rei.setServiceName(getQNameAttribute(reader, ATTR_SERVICE));
                rei.setPortName(getQNameAttribute(reader, ATTR_PORT));
                rei.setUrlPattern(
                    getMandatoryNonEmptyAttribute(reader, ATTR_URL_PATTERN));
                ensureNoContent(reader);
                rei.setDeployed(true);
                endpoints.add(rei);
            } else {
                failWithLocalName("runtime.parser.invalidElement", reader);
            }
        }

        reader.close();

        info.setEndpoints(endpoints);
        return info;
    }

    protected String getAttribute(XMLReader reader, String name) {
        Attributes attributes = reader.getAttributes();
        String value = attributes.getValue(name);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }

    protected QName getQNameAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if (value == null || value.equals("")) {
            return null;
        } else {
            return QName.valueOf(value);
        }
    }

    protected String getNonEmptyAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if (value != null && value.equals("")) {
            failWithLocalName(
                "runtime.parser.invalidAttributeValue",
                reader,
                name);
        }
        return value;
    }

    protected String getMandatoryAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if (value == null) {
            failWithLocalName("runtime.parser.missing.attribute", reader, name);
        }
        return value;
    }

    protected String getMandatoryNonEmptyAttribute(
        XMLReader reader,
        String name) {
        String value = getAttribute(reader, name);
        if (value == null) {
            failWithLocalName("runtime.parser.missing.attribute", reader, name);
        } else if (value.equals("")) {
            failWithLocalName(
                "runtime.parser.invalidAttributeValue",
                reader,
                name);
        }
        return value;
    }

    protected static void ensureNoContent(XMLReader reader) {
        if (reader.nextElementContent() != XMLReader.END) {
            fail("runtime.parser.unexpectedContent", reader);
        }
    }

    protected static void fail(String key, XMLReader reader) {
        logger.log(Level.SEVERE, key + reader.getLineNumber());
        throw new JAXRPCServletException(
            key,
            Integer.toString(reader.getLineNumber()));
    }

    protected static void failWithFullName(String key, XMLReader reader) {
        throw new JAXRPCServletException(
            key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getName().toString()});
    }

    protected static void failWithLocalName(String key, XMLReader reader) {
        throw new JAXRPCServletException(
            key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getLocalName()});
    }

    protected static void failWithLocalName(
        String key,
        XMLReader reader,
        String arg) {
        throw new JAXRPCServletException(
            key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getLocalName(),
                arg });
    }

    protected Class loadClass(String name) {
        try {
            return Class.forName(name, true, classLoader);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new JAXRPCServletException(
                "runtime.parser.classNotFound",
                name);
        }
    }

    protected ClassLoader classLoader;

    public static final String NS_RUNTIME =
        "http://java.sun.com/xml/ns/jax-rpc/ri/runtime";

    public static final QName QNAME_ENDPOINTS =
        new QName(NS_RUNTIME, "endpoints");
    public static final QName QNAME_ENDPOINT =
        new QName(NS_RUNTIME, "endpoint");

    public static final String ATTR_VERSION = "version";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_INTERFACE = "interface";
    public static final String ATTR_IMPLEMENTATION = "implementation";
    public static final String ATTR_TIE = "tie";
    public static final String ATTR_MODEL = "model";
    public static final String ATTR_WSDL = "wsdl";
    public static final String ATTR_SERVICE = "service";
    public static final String ATTR_PORT = "port";
    public static final String ATTR_URL_PATTERN = "urlpattern";

    public static final String ATTRVALUE_VERSION_1_0 = "1.0";
    private static final Logger logger =
        Logger.getLogger(
            com.sun.xml.rpc.util.Constants.LoggingDomain + ".server.http");
}
