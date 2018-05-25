/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2002 International Business Machines Corp. 2002. All rights reserved.
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

package com.sun.xml.rpc.processor.modeler.j2ee.xml;
import java.util.Date;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Base class for all generated classes that are of type Complex Type
 * Provides operations that are applicable to complex type content.
 * 
 * For example, element and element reference that have a sequence semantics
 * (i.e maxOccurs=unbounded) are only applicable to the content of a complex type.
 *
 * Attributes can only be added to the content of a complex type.
 */
public class ComplexType extends BaseType {
    /**
     * Remove the element indicated by elementName and index from the list of children 
     */
    public boolean removeElement(int index, String elementName) {
        NodeList nodes = xmlElement.getElementsByTagName(elementName);
        int length = nodes.getLength();

        if (index < length) {
            Node deleteNode = nodes.item(index);
            if (deleteNode != null) {
                try {
                    xmlElement.removeChild(deleteNode);
                    return true;
                } catch (DOMException ex) {
                    System.out.println(
                        "DOM Exception from removeElement." + ex);
                }
            }
        }
        return false;
    }

    /**
     * Either add a new element if the index if bigger than the existing length
     * or update the existing element at index
     */
    public void setElementValue(
        int index,
        String elementName,
        BaseType baseType) {
        // Get the XML Element from the BaseType
        Element childXml = baseType.getXMLElement();
        insertXMLElementAtLocation(childXml, index, elementName);
    }

    public void setElementValue(
        int index,
        String elementName,
        String elementValue) {
        // Create a new XML element and its text node
        Element newChild =
            factory.createXMLElementAndText(elementName, elementValue);
        insertXMLElementAtLocation(newChild, index, elementName);
    }

    public void setElementValue(int index, String elementName, boolean value) {
        setElementValue(index, elementName, (new Boolean(value)).toString());
    }

    public void setElementValue(int index, String elementName, int value) {
        setElementValue(index, elementName, (new Integer(value)).toString());
    }

    public void setElementValue(int index, String elementName, float value) {
        setElementValue(index, elementName, (new Float(value)).toString());
    }

    public void setElementValue(int index, String elementName, double value) {
        setElementValue(index, elementName, (new Double(value)).toString());
    }

    public void setElementValue(int index, String elementName, long value) {
        setElementValue(index, elementName, (new Long(value)).toString());
    }

    public void setElementValue(int index, String elementName, short value) {
        setElementValue(index, elementName, (new Short(value)).toString());
    }

    public void setElementValue(int index, String elementName, byte value) {
        setElementValue(index, elementName, (new Byte(value)).toString());
    }

    public void setElementValue(int index, String elementName, Date value) {
        setElementValue(index, elementName, simpleDateFormat.format(value));
    }

    public boolean getElementBooleanValue(String elementName, int index) {
        return new Boolean(getElementValue(elementName, index)).booleanValue();
    }

    public int getElementIntegerValue(String elementName, int index) {
        return new Integer(getElementValue(elementName, index)).intValue();
    }

    public float getElementFloatValue(String elementName, int index) {
        return new Float(getElementValue(elementName, index)).floatValue();
    }

    public double getElementDoubleValue(String elementName, int index) {
        return new Double(getElementValue(elementName, index)).doubleValue();
    }

    public long getElementLongValue(String elementName, int index) {
        return new Long(getElementValue(elementName, index)).longValue();
    }

    public short getElementShortValue(String elementName, int index) {
        return new Short(getElementValue(elementName, index)).shortValue();
    }

    public byte getElementByteValue(String elementName, int index) {
        return new Byte(getElementValue(elementName, index)).byteValue();
    }

    public Date getElementDateValue(String elementName, int index) {
        try {
            String result = getElementValue(elementName, index);
            if (result != null) {
                return simpleDateFormat.parse(result);
            }
        } catch (java.text.ParseException ex) {
            System.out.println("getElementDateValue exception.." + ex);
        }
        return null;
    }

    /**
     * Set the attribute value for the specified attribute name.
     *   @param attrname - attribute name
     *   @param attrvalue - attribute value
     */
    public void setAttributeValue(String attrname, String attrvalue) {
        if (xmlElement == null) {
            // must be setting value on the xmlAttr created for Simple Type
            xmlAttr.setValue(attrvalue);
        } else {
            Attr attr = xmlElement.getAttributeNode(attrname);
            if (attr == null) {
                attr = factory.createAttribute(attrname, xmlElement);
            }
            attr.setValue(attrvalue);
        }
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, SimpleType attrObject) {
        setAttributeValue(attrname, attrObject.getElementValue());
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, boolean attrObject) {
        setAttributeValue(attrname, (new Boolean(attrObject)).toString());
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, int attrObject) {
        setAttributeValue(attrname, (new Integer(attrObject)).toString());
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, float attrObject) {
        setAttributeValue(attrname, (new Float(attrObject)).toString());
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, double attrObject) {
        setAttributeValue(attrname, (new Double(attrObject)).toString());
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, long attrObject) {
        setAttributeValue(attrname, (new Long(attrObject)).toString());
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, short attrObject) {
        setAttributeValue(attrname, (new Short(attrObject)).toString());
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, byte attrObject) {
        setAttributeValue(attrname, (new Byte(attrObject)).toString());
    }

    /**
     * Set the attribute to the specified value
     */
    public void setAttributeValue(String attrname, Date attrObject) {
        setAttributeValue(attrname, simpleDateFormat.format(attrObject));
    }

    /**
     * Get the attribute value for the specified attribute name
     */
    public String getAttributeValue(String attrname) {
        Attr attr = xmlElement.getAttributeNode(attrname);
        if (attr != null) {
            return attr.getValue();
        }
        return null;
    }

    public boolean getAttributeBooleanValue(String attrname) {
        return new Boolean(getAttributeValue(attrname)).booleanValue();
    }

    public int getAttributeIntegerValue(String attrname) {
        return new Integer(getAttributeValue(attrname)).intValue();
    }

    public float getAttributeFloatValue(String attrname) {
        return new Float(getAttributeValue(attrname)).floatValue();
    }

    public double getAttributeDoubleValue(String attrname) {
        return new Double(getAttributeValue(attrname)).doubleValue();
    }

    public long getAttributeLongValue(String attrname) {
        return new Long(getAttributeValue(attrname)).longValue();
    }

    public short getAttributeShortValue(String attrname) {
        return new Short(getAttributeValue(attrname)).shortValue();
    }

    public byte getAttributeByteValue(String attrname) {
        return new Byte(getAttributeValue(attrname)).byteValue();
    }

    public Date getAttributeDateValue(String attrname) {
        try {
            String result = getAttributeValue(attrname);
            if (result != null) {
                return simpleDateFormat.parse(result);
            }
        } catch (java.text.ParseException ex) {
            System.out.println("getElementDateValue exception.." + ex);
        }
        return null;
    }

    /**
     * @param className - the name of the class corresponding to this element
     */
    public Object getAttributeValue(String property, String className) {
        Attr attr = xmlElement.getAttributeNode(property);
        return factory.newInstance(attr, className);
    }

    /**
    * Remove the attribute from this element
    */
    public boolean removeAttribute(String attrname) {
        try {
            xmlElement.removeAttribute(attrname);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Either append this XML element to the bottom or insert at a particular position
     * respecting the order of the content model
     */
    private void insertXMLElementAtLocation(
        Element newChild,
        int index,
        String elementName) {
        NodeList nodes = xmlElement.getElementsByTagName(elementName);
        int length = nodes.getLength();

        if (index >= length) {
            if (length > 0) {
                // there are existing elements with the same tag
                // find the "relative" location of the last element
                Node lastContent = nodes.item(length - 1);
                if (lastContent != null) {
                    Node nextSibling = lastContent.getNextSibling();
                    if (nextSibling != null) {
                        this.xmlElement.insertBefore(newChild, nextSibling);
                        return;
                    }
                }
            }

            // Append it to the end
            this.xmlElement.appendChild(newChild);
        } else {
            Node refChild = nodes.item(index);
            this.xmlElement.replaceChild(newChild, refChild);
        }
    }

    /**
     * @return array of attribute names, or empty array if none exists
     */
    public String[] getAttributeNames() {
        NamedNodeMap nnm = this.xmlElement.getAttributes();
        int len = 0;
        if (nnm != null)
            len = nnm.getLength();
        String[] ret = new String[len];
        for (int i = 0; i < len; i++) {
            Attr attr = (Attr) nnm.item(i);
            ret[i] = attr.getName();
        }
        return ret;
    }
}
