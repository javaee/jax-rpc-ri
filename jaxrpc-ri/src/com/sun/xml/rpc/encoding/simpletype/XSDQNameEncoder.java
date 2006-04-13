/*
 * $Id: XSDQNameEncoder.java,v 1.2 2006-04-13 01:28:01 ofung Exp $
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

package com.sun.xml.rpc.encoding.simpletype;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.util.xml.XmlUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDQNameEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDQNameEncoder();

    private XSDQNameEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        QName qn = (QName) obj;
        String str = "";

        String nsURI = qn.getNamespaceURI();
        if (nsURI != null && nsURI.length() > 0) {
            String prefix = writer.getPrefix(nsURI);
            if (prefix == null) {
                prefix = writer.getPrefixFactory().getPrefix(nsURI);
            }
            str += (prefix + ":");
        }

        str += qn.getLocalPart();

        return str;
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        String uri = "";
        str = EncoderUtils.collapseWhitespace(str);
        String prefix = XmlUtil.getPrefix(str);
        if (prefix != null) {
            uri = reader.getURI(prefix);
            if (uri == null) {
                throw new DeserializationException("xsd.unknownPrefix", prefix);
            }
        }

        String localPart = XmlUtil.getLocalPart(str);

        return new QName(uri, localPart);
    }

    public void writeValue(Object obj, XMLWriter writer) throws Exception {
        writer.writeCharsUnquoted(objectToString(obj, writer));
    }

    public void writeAdditionalNamespaceDeclarations(
        Object obj,
        XMLWriter writer)
        throws Exception {
            
        QName value = (QName) obj;
        if (value != null) {
            String uri = value.getNamespaceURI();
            if (!uri.equals("")) {
                if (writer.getPrefix(uri) == null) {
                    writer.writeNamespaceDeclaration(uri);
                }
            }
        }
    }
}
