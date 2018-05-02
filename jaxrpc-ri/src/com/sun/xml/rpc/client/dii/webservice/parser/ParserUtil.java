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

package com.sun.xml.rpc.client.dii.webservice.parser;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.client.dii.webservice.WebServicesClientException;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.xml.XmlUtil;

/**
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
            failWithLocalName("client.invalidAttributeValue", reader, name);
        }
        return value;
    }

    public static String getMandatoryAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if (value == null) {
            failWithLocalName("client.missing.attribute", reader, name);
        }
        return value;
    }

    public static String getMandatoryNonEmptyAttribute(
        XMLReader reader,
        String name) {
        String value = getAttribute(reader, name);
        if (value == null) {
            failWithLocalName("client.missing.attribute", reader, name);
        } else if (value.equals("")) {
            failWithLocalName("client.invalidAttributeValue", reader, name);
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
                failWithLocalName("client.invalidAttributeValue", reader, name);
            }
        }
        String localPart = XmlUtil.getLocalPart(value);
        return new QName(uri, localPart);
    }

    public static void ensureNoContent(XMLReader reader) {
        if (reader.nextElementContent() != XMLReader.END) {
            fail("client.unexpectedContent", reader);
        }
    }

    public static void fail(String key, XMLReader reader) {
        throw new WebServicesClientException(
            key,
            Integer.toString(reader.getLineNumber()));
    }

    public static void failWithFullName(String key, XMLReader reader) {
        throw new WebServicesClientException(
            key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getName().toString()});
    }

    public static void failWithLocalName(String key, XMLReader reader) {
        throw new WebServicesClientException(
            key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getLocalName()});
    }

    public static void failWithLocalName(
        String key,
        XMLReader reader,
        String arg) {
        throw new WebServicesClientException(
            key,
            new Object[] {
                Integer.toString(reader.getLineNumber()),
                reader.getLocalName(),
                arg });
    }
}
