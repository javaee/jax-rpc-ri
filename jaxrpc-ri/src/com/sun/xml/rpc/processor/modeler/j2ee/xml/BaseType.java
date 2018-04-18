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

// @(#) 1.3 jsr109ri/src/java/com/ibm/webservices/ri/etools/xmlschema/beans/BaseType.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:48:38 [10/7/02 11:54:12]
/**
 * <copyright>
 * IBM WebSphere Studio Application Developer XML Tools
 * (C) Copyright IBM Corp. 2001.
 * </copyright>
 */
/*********************************************************************
Change History
Date     user       defect    purpose
---------------------------------------------------------------------------
08/21/02 mcheng     142306    Enable validation
09/30/02 mcheng     148356    XML Schema support
*********************************************************************/
package com.sun.xml.rpc.processor.modeler.j2ee.xml;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class BaseType implements java.io.Serializable {
    protected Factory factory;
    protected Element xmlElement;
    protected Attr xmlAttr;

    protected SimpleDateFormat simpleDateFormat;

    public BaseType() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Remember the factory that creates this XML element or attribute
     */
    protected void setFactory(Factory factory) {
        this.factory = factory;
    }

    /**
     * Remember the XML element that corresponds to this bean
     */
    protected void setXMLElement(Element element) {
        xmlElement = element;
    }

    /** 
     * Return the XML element that corresponds to this bean
     */
    public Element getXMLElement() {
        return xmlElement;
    }

    /**
     * Remember the XML attribute that corresponds to this bean
     */
    protected void setXMLAttribute(Attr xmlAttr) {
        this.xmlAttr = xmlAttr;
    }

    /**
     * Return the XML attribute that corresponds to this bean
     */
    public Attr getXMLAttribute() {
        return xmlAttr;
    }

    /**
     * Create the element and the text node if it does not exist.
     * Otherwise update the text value.
     *  @param elementName - the name of the element
     *  @param elementValue - the value of the element
     */
    public void setElementValue(String elementName, String elementValue) {
        NodeList nodes = xmlElement.getElementsByTagName(elementName);

        if (nodes.getLength() == 0) {
            // Create a new element and text node and add 
            // that as a child to the current XML element
            Element elementNode =
                factory.createXMLElementAndText(elementName, elementValue);
            xmlElement.appendChild(elementNode);
        } else {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String item = node.getNodeName().trim();

                if (item.equals(elementName)) {
                    // Update the text node
                    Node textNode = node.getFirstChild();
                    if (textNode instanceof Text) {
                        ((Text) textNode).setNodeValue(elementValue);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Create an element and set it to the specified value
     */
    public void setElementValue(String elementName, boolean value) {
        setElementValue(elementName, (new Boolean(value)).toString());
    }

    /**
     * Create an element and set it to the specified value
     */
    public void setElementValue(String elementName, int value) {
        setElementValue(elementName, (new Integer(value)).toString());
    }

    /**
     * Create an element and set it to the specified value
     */
    public void setElementValue(String elementName, float value) {
        setElementValue(elementName, (new Float(value)).toString());
    }

    /**
     * Create an element and set it to the specified value
     */
    public void setElementValue(String elementName, double value) {
        setElementValue(elementName, (new Double(value)).toString());
    }

    /**
     * Create an element and set it to the specified value
     */
    public void setElementValue(String elementName, long value) {
        setElementValue(elementName, (new Long(value)).toString());
    }

    /**
     * Create an element and set it to the specified value
     */
    public void setElementValue(String elementName, short value) {
        setElementValue(elementName, (new Short(value)).toString());
    }

    /**
     * Create an element and set it to the specified value
     */
    public void setElementValue(String elementName, byte value) {
        setElementValue(elementName, (new Byte(value)).toString());
    }

    /**
     * Create an element and set it to the specified value. 
     * The Date string is converted into the XML Schema date format CCYY-MM-DD.
     */
    public void setElementValue(String elementName, Date value) {
        setElementValue(elementName, simpleDateFormat.format(value));
    }

    /**
     * Create an element and set it to the specified value
     */
    public void setElementValue(String elementName, BaseType baseType) {
        Element childXml = baseType.getXMLElement();
        this.xmlElement.appendChild(childXml);
    }

    /**
     * Get the text value for this element
     */
    public String getElementValue() {
        // return node value
        if (xmlAttr != null) {
            return xmlAttr.getValue();
        }

        String value = xmlElement.getNodeValue();
        if (value != null) {
            return value;
        }

        // fails, return PCDATA
        NodeList nList = xmlElement.getChildNodes();

        for (int i = 0; i < nList.getLength(); i++) {
            if (nList.item(i) instanceof Text)
                return ((Text) nList.item(i)).getData();
        }
        return null;
    }

    /**
     * Get the element with the specifed name and at the specified location
     */
    public String getElementValue(String property, int index) {
        //    NodeList nodes = xmlElement.getElementsByTagName(property);
        NodeList nodes = xmlElement.getChildNodes();
        int idx = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (!(node instanceof Element))
                continue;
            String localName = node.getLocalName();
            if (localName == null)
                continue;
            if (!localName.equals(property))
                continue;
            idx++;

            if (idx == index) {
                Element element = (Element) node;
                Node child = element.getFirstChild();
                if (child instanceof Text) {
                    return ((Text) child).getData();
                } else
                    return null;
            }
        }
        return null;
    }

    /**
     * Get the value for the input element
     *  @param elementName The name of the element
     *  @return String The value of the element (from the text node)
     */
    public String getElementValue(String elementName) {
        NodeList nList = xmlElement.getChildNodes();
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            // Find the node that matches the element name
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element anElement = (Element) node;
                if (anElement.getLocalName().equals(elementName)) {
                    Node child = anElement.getFirstChild();
                    if (child instanceof Text) {
                        return ((Text) child).getData();
                    }
                }
            }
        }
        return null;
    }

    public boolean getElementBooleanValue(String elementName) {
        return new Boolean(getElementValue(elementName)).booleanValue();
    }

    public int getElementIntegerValue(String elementName) {
        return new Integer(getElementValue(elementName)).intValue();
    }

    public float getElementFloatValue(String elementName) {
        return new Float(getElementValue(elementName)).floatValue();
    }

    public double getElementDoubleValue(String elementName) {
        return new Double(getElementValue(elementName)).doubleValue();
    }

    public long getElementLongValue(String elementName) {
        return new Long(getElementValue(elementName)).longValue();
    }

    public short getElementShortValue(String elementName) {
        return new Short(getElementValue(elementName)).shortValue();
    }

    public byte getElementByteValue(String elementName) {
        return new Byte(getElementValue(elementName)).byteValue();
    }

    /** 
     * Get the value for the input element
     *  @param elementName The name of the element
     *  @return Date The Java Date for the element value if the value conforms to the
     *               pattern yyyy-mm-dd
     */
    public Date getElementDateValue(String elementName) {
        try {
            String result = getElementValue(elementName);
            if (result != null) {
                return simpleDateFormat.parse(result);
            }
        } catch (java.text.ParseException ex) {
            System.out.println("getElementDateValue exception.." + ex);
        }
        return null;
    }

    /**
     * Get the value for the input element
     * @param className - the name of the class corresponding to this element
     *                    Could be an inner class.
     */
    public BaseType getElementValue(String property, String className) {
        NodeList nList = xmlElement.getChildNodes();

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if (node instanceof Element
                && node.getLocalName().equals(property)) {
                // ressurect this object
                return factory.newInstance((Element) node, className);
            }
        }

        // System.out.println("Ooops...error.. in getElementValue()");
        return null;
    }

    /**
     * Get the value for the input element
     *   @param property - the name of the element
     *   @param className - the name of the class corresponding to this element
     *                      Could be an inner class
     *   @param index - an index for collection
     */
    public Object getElementValue(
        String property,
        String className,
        int index) {
        NodeList nList = xmlElement.getChildNodes();
        int count = 0;

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node instanceof Element
                && node.getLocalName().equals(property)) {
                // ressurect this object
                if (count == index) {
                    return factory.newInstance((Element) node, className);
                } else
                    count++;
            }
        }
        // System.out.println("Ooops...error.. in getElementValue() with index");
        return null;
    }

    /**
     * Update the text node for this element
     */
    public void updateElementValue(String value) {
        // If the text node for this element has never been created, create it.
        // Otherwise, update it.
        Node textNode = xmlElement.getFirstChild();
        if (textNode == null) {
            factory.createText(xmlElement, value);
        } else {
            textNode.setNodeValue(value);
        }
    }

    public void updateElementValue(boolean value) {
        updateElementValue((new Boolean(value)).toString());
    }

    public void updateElementValue(int value) {
        updateElementValue((new Integer(value)).toString());
    }

    public void updateElementValue(float value) {
        updateElementValue((new Float(value)).toString());
    }

    public void updateElementValue(double value) {
        updateElementValue((new Double(value)).toString());
    }

    public void updateElementValue(long value) {
        updateElementValue((new Long(value)).toString());
    }

    public void updateElementValue(short value) {
        updateElementValue((new Short(value)).toString());
    }

    public void updateElementValue(byte value) {
        updateElementValue((new Byte(value)).toString());
    }

    public void updateElementValue(Date value) {
        updateElementValue(simpleDateFormat.format(value));
    }

    /**
     * Remove the element with the input elementName from the current node
     */
    public boolean removeElement(String elementName) {
        //    NodeList nodes = xmlElement.getElementsByTagName(elementName);
        NodeList nodes = xmlElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element
                && node.getLocalName().equals(elementName)) {
                try {
                    xmlElement.removeChild(node);
                    return true;
                } catch (DOMException ex) {
                }
            }
        }
        return false;
    }

    /**
     * Return the number of children with this name
     */
    public int sizeOfElement(String name) {
        //    NodeList nodes = xmlElement.getElementsByTagName(name);
        NodeList nodes = xmlElement.getChildNodes();
        int len = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element && node.getLocalName().equals(name))
                len++;
        }
        return len;
    }

    /**
     * Return the node name for this element
     */
    public String getElementName() {
        // return xmlElement.getNodeName();
        return xmlElement.getLocalName();
    }

}
