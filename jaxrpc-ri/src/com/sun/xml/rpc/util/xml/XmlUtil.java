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

package com.sun.xml.rpc.util.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.rpc.util.WSDLParseException;

/**
 * @author JAX-RPC Development Team
 */
public class XmlUtil {

	public static String getPrefix(String s) {
		int i = s.indexOf(':');
		if (i == -1)
			return null;
		return s.substring(0, i);
	}

	public static String getLocalPart(String s) {
		int i = s.indexOf(':');
		if (i == -1)
			return s;
		return s.substring(i + 1);
	}

	public static String getAttributeOrNull(Element e, String name) {
		Attr a = e.getAttributeNode(name);
		if (a == null)
			return null;
		return a.getValue();
	}

	public static String getAttributeNSOrNull(
		Element e,
		String name,
		String nsURI) {
		Attr a = e.getAttributeNodeNS(nsURI, name);
		if (a == null)
			return null;
		return a.getValue();
	}

	public static boolean matchesTagNS(Element e, String tag, String nsURI) {
		try {
			return e.getLocalName().equals(tag)
				&& e.getNamespaceURI().equals(nsURI);
		} catch (NullPointerException npe) {

			// localname not null since parsing would fail before here
			throw new WSDLParseException(
				"null.namespace.found",
				e.getLocalName());
		}
	}

	public static boolean matchesTagNS(
		Element e,
		javax.xml.namespace.QName name) {
		try {
			return e.getLocalName().equals(name.getLocalPart())
				&& e.getNamespaceURI().equals(name.getNamespaceURI());
		} catch (NullPointerException npe) {

			// localname not null since parsing would fail before here
			throw new WSDLParseException(
				"null.namespace.found",
				e.getLocalName());
		}
	}

	public static Iterator getAllChildren(Element element) {
		return new NodeListIterator(element.getChildNodes());
	}

	public static Iterator getAllAttributes(Element element) {
		return new NamedNodeMapIterator(element.getAttributes());
	}

	public static List parseTokenList(String tokenList) {
		List result = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(tokenList, " ");
		while (tokenizer.hasMoreTokens()) {
			result.add(tokenizer.nextToken());
		}
		return result;
	}

	public static String getTextForNode(Node node) {
		StringBuffer sb = new StringBuffer();

		NodeList children = node.getChildNodes();
		if (children.getLength() == 0)
			return null;

		for (int i = 0; i < children.getLength(); ++i) {
			Node n = children.item(i);

			if (n instanceof Text)
				sb.append(n.getNodeValue());
			else if (n instanceof EntityReference) {
				String s = getTextForNode(n);
				if (s == null)
					return null;
				else
					sb.append(s);
			} else
				return null;
		}

		return sb.toString();
	}

	public static InputStream getUTF8Stream(String s) {
		try {
			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			Writer w = new OutputStreamWriter(bas, "utf-8");
			w.write(s);
			w.close();
			byte[] ba = bas.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(ba);
			return bis;
		} catch (IOException e) {
			throw new RuntimeException("should not happen");
		}
	}

	public static ByteInputStream getUTF8ByteInputStream(String s) {
		try {
			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			Writer w = new OutputStreamWriter(bas, "utf-8");
			w.write(s);
			w.close();
			byte[] ba = bas.toByteArray();
			ByteInputStream bis = new ByteInputStream(ba, ba.length);
			return bis;
		} catch (IOException e) {
			throw new RuntimeException("should not happen");
		}
	}

	static TransformerFactory transformerFactory = null;

	public static Transformer newTransformer() {
		Transformer t = null;

		if (transformerFactory == null)
			transformerFactory = TransformerFactory.newInstance();

		try {
			t = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException tex) {
			throw new IllegalStateException("Unable to create a JAXP transformer");
		}
		return t;
	}

}
