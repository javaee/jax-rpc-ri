/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.rpc.processor.generator.nodes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author  Qingqing Ouyang
 * @version 
 */
public abstract class JaxRpcMappingNode extends java.lang.Object {

    private static final String QNAME_SEPARATOR = ":";
    private static String DEBUG =
        System.getProperty("com.sun.xml.rpc.j2ee.debug");

    /**
     *  <p>
     * @return the Document for the given node
     * </p>
     */
    static protected Document getOwnerDocument(Node node) {

        if (node instanceof Document) {
            return (Document) node;
        }
        return node.getOwnerDocument();
    }

    /**
     * <p>
     * Append a new element child to the current node 
     * </p>
     * @param parentNode is the parent node for the new child element
     * @param elementName is new element tag name
     * @return the newly created child node
     */
    public static Element appendChild(Node parent, String elementName) {
        Element child =
            getOwnerDocument(parent).createElementNS(
                JaxRpcMappingTagNames.J2EE_NAMESPACE,
                elementName);
        parent.appendChild(child);
        return child;
    }

    /**
     * <p>
     * Append a new text child
     * </p>
     * @param parent for the new child element
     * @param elementName is the new element tag name
     * @param text the text for the new element
     * @result the newly create child node
     */
    public static Node appendTextChild(
        Node parent,
        String elementName,
        String text) {

        if (text == null || text.length() == 0)
            return null;

        Node child = appendChild(parent, elementName);
        child.appendChild(getOwnerDocument(child).createTextNode(text));
        return child;
    }

    /**
     * <p>
     * Append a new text child
     * </p>
     * @param parent for the new child element
     * @param elementName is the new element tag name
     * @param value the int value for the new element
     * @result the newly create child node
     */
    public static Node appendTextChild(
        Node parent,
        String elementName,
        int value) {
        return appendTextChild(parent, elementName, String.valueOf(value));
    }

    /**
     * <p>
     * Append a new text child even if text is empty
     * </p>
     * @param parent for the new child element
     * @param elementName is the new element tag name
     * @param text the text for the new element
     * @result the newly create child node
     */
    public static Node forceAppendTextChild(
        Node parent,
        String elementName,
        String text) {

        Node child = appendChild(parent, elementName);
        if (text != null && text.length() != 0) {
            child.appendChild(getOwnerDocument(child).createTextNode(text));
        }
        return child;
    }

    /**
     * <p>
     * Append a new attribute to an element
     * </p>
     * @param parent for the new child element
     * @param elementName is the new element tag name
     * @param text the text for the new element
     * @result the newly create child node
     */
    public static void setAttribute(
        Element parent,
        String elementName,
        String text) {

        if (text == null || text.length() == 0)
            return;
        parent.setAttributeNS(
            JaxRpcMappingTagNames.J2EE_NAMESPACE,
            elementName,
            text);
    }

    /**
     * Set a namespace attribute on an element.
     * @param element on which to set attribute
     * @param prefix raw prefix (without "xmlns:")
     * @param namespaceURI namespace URI to which prefix is mapped.
     */
    public static void setAttributeNS(
        Element element,
        String prefix,
        String namespaceURI) {

        String nsPrefix =
            prefix.equals("") ? "xmlns" : "xmlns" + QNAME_SEPARATOR + prefix;

        element.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            nsPrefix,
            namespaceURI);
    }

    //XXX FIXME.  Logging in jaxrpc?
    protected void debug(String className, String msg) {
        if (DEBUG != null) {
            System.out.println("[" + className + "] --> " + msg);
        }
    }
}
