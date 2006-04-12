/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
