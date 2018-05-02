/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package com.sun.xml.rpc.processor.config.parser;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.ConfigurationException;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.xml.XmlUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ParserUtil {
    
    public static String getAttribute(XMLReader reader, String name) {
        Attributes attributes = reader.getAttributes();
        return attributes.getValue(name);
    }
    
    public static String getNonEmptyAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if (value != null && value.equals("")) {
            failWithLocalName("configuration.invalidAttributeValue",
                reader, name);
        }
        return value;
    }
    
    public static String getMandatoryAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if (value == null) {
            failWithLocalName("configuration.missing.attribute", reader, name);
        }
        return value;
    }
    
    public static String getMandatoryNonEmptyAttribute(XMLReader reader,
        String name) {
            
        String value = getAttribute(reader, name);
        if (value == null) {
            failWithLocalName("configuration.missing.attribute", reader, name);
        }
        else if (value.equals("")) {
            failWithLocalName("configuration.invalidAttributeValue",
                reader, name);
        }
        return value;
    }
    
    public static QName getQNameAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        
        if (value == null) {
            return null;
        }
        
        String prefix = XmlUtil.getPrefix(value);
        String uri = "";
        if (prefix != null) {
            uri = reader.getURI(prefix);
            if (uri == null) {
                failWithLocalName("configuration.invalidAttributeValue",
                    reader, name);
            }
        }
        String localPart = XmlUtil.getLocalPart(value);
        return new QName(uri, localPart);
    }
    
    public static void ensureNoContent(XMLReader reader) {
        if (reader.nextElementContent() != XMLReader.END) {
            fail("configuration.unexpectedContent", reader);
        }
    }
    
    public static void fail(String key, XMLReader reader) {
        throw new ConfigurationException(key,
            Integer.toString(reader.getLineNumber()));
    }
    
    public static void failWithFullName(String key, XMLReader reader) {
        throw new ConfigurationException(key, new Object[] { Integer.toString(
            reader.getLineNumber()), reader.getName().toString() });
    }
    
    public static void failWithLocalName(String key, XMLReader reader) {
        throw new ConfigurationException(key, new Object[] { Integer.toString(
            reader.getLineNumber()), reader.getLocalName() });
    }
    
    public static void failWithLocalName(String key, XMLReader reader,
        String arg) {
            
        throw new ConfigurationException(key, new Object[] { Integer.toString(
            reader.getLineNumber()), reader.getLocalName(), arg });
    }
}
