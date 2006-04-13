/*
 * $Id: ParserUtil.java,v 1.2 2006-04-13 01:28:37 ofung Exp $
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
