/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// @(#) 1.4 jsr109ri/src/java/com/ibm/webservices/ri/etools/xmlschema/beans/Factory.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:48:46 [10/7/02 11:54:12]
/*************************************************************************
   Licensed Materials - Property of IBM
   (C) COPYRIGHT International Business Machines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with 
   IBM Corp.
**************************************************************************/
/*********************************************************************
Change History
Date     user       defect    purpose
---------------------------------------------------------------------------
08/21/02 mcheng     142306    Enable validation
09/30/02 mcheng     148356    XML Schema support
*********************************************************************/
/**
 * <copyright>
 *   IBM WebSphere Application Developer XML Tools
 *   (C) Copyright IBM Corp. 2001.
 * </copyright>
 */
package com.sun.xml.rpc.processor.modeler.j2ee.xml;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * This factory class creates/loads Java bean and the corresponding DOM node.
 * The generated factory code extends this class.
 */
public class Factory implements java.io.Serializable {
    protected String xmlFile;
    protected String packageName;
    protected Document document;

    protected String encoding;
    protected String encodingTag;

    protected String dtdFileName;
    protected String dtdPublicId;

    protected String xsdFileName;
    protected String xsdNamespaceURI;
    protected Hashtable importedFileHashtable = new Hashtable();

    private String rootElementName;

    public Factory() {
    }

    /**
     * Set the name of the Java package
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Get the name of the Java package
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Set the name of the XML instance document
     */
    public void setXMLFilename(String filename) {
        this.xmlFile = filename;
    }

    /**
     * Get the name of the XML instance document
     */
    public String getXMLFilename() {
        return xmlFile;
    }

    /**
     * Set the encoding that will be used to output the xml document
     *   @param encoding the encoding value for the OutputStreamWriter
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Get the encoding that will be used to output the xml document
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Set the IANA value to be used in the XML declaration
     *  @param encodingTag the encoding tag to be used in the XML declaration
     */
    public void setEncodingTag(String encodingTag) {
        this.encodingTag = encodingTag;
    }

    /**
     * Get the IANA value to be used in the XML declaration
     */
    public String getEncodingTag(String encodingTag) {
        return encodingTag;
    }

    /**
     * Set the DTD file name so that a DOCTYPE will be generated
     * If this is set, the XSD filename should not be set.
     */
    public void setDTDFileName(String dtdFilename) {
        this.dtdFileName = dtdFilename;
    }

    /**
     * Get the DTD file name
     */
    public String getDTDFileName() {
        return dtdFileName;
    }

    /**
     * Set the public identifier in the DOCTYPE
     */
    public void setPublicId(String publicId) {
        this.dtdPublicId = publicId;
    }

    /**
     * Get the public identifier in the DOCTYPE
     */
    public String getPublicId() {
        return dtdPublicId;
    }

    /**
     * Set the XSD file name.
     * If this is set, the DTD filename should not be set
     */
    public void setXSDFileName(String xsdFilename) {
        this.xsdFileName = xsdFilename;
    }

    /**
     * Get the XSD file name
     */
    public String getXSDFileName() {
        return xsdFileName;
    }

    /**
     * Set the namespace URI for this XSD file. 
     */
    public void setNamespaceURI(String namespaceURI) {
        xsdNamespaceURI = namespaceURI;
    }

    /**
     * Get the namespace URI
     */
    public String getNamespaceURI() {
        return xsdNamespaceURI;
    }

    /**
     * Store the prefix and the namespace information for each imported file
     *  @param filename The filename for the imported schema
     *  @param nsprefix The prefix for the imported schemar
     *  @param nsURI The namespace URI for the imported schema
     */
    public void addImportedFileInfo(
        String filename,
        String nsprefix,
        String nsURI) {
        importedFileHashtable.put(nsprefix, nsURI + " " + filename);
    }

    /**
     * Load an existing instance document
     */
    public BaseType loadDocument(String classname, String xmlFile) {
        return loadDocument(classname, xmlFile, true);
    }

    public BaseType loadDocument(
        String classname,
        String xmlFile,
        boolean validate) {

        System.out.print("[Factory] ==> classname = " + classname);
        System.out.print("[Factory] ==> xmlFile = " + xmlFile);
        System.out.print("[Factory] ==> validate = " + validate);

        this.xmlFile = xmlFile;
        DOMParser parser = new DOMParser();
        try {
            if (validate) {
                parser.setEntityResolver(new EntityResolverRI());
                parser.setFeature(
                    "http://xml.org/sax/features/validation",
                    true);
            } else
                parser.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);
            parser.setFeature(
                "http://apache.org/xml/features/validation/schema",
                true);
            // parser.setFeature("http://xml.org/sax/features/namespaces",true);
            parser.setErrorHandler(new ErrorHandlerRI());
            parser.parse(xmlFile);
            document = parser.getDocument();
            Element rootElement = document.getDocumentElement();
            /* print out the elements */
            if (_trace)
                printElement(rootElement);

            return newInstance(rootElement, classname);
        } catch (Exception e) {
            System.out.println("Exception: Factory::loadDocument() " + e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load an existing instance document from an InputSource
     */
    public BaseType loadDocument(String classname, InputSource source) {
        return loadDocument(classname, source, true);

    }

    public BaseType loadDocument(
        String classname,
        InputSource source,
        boolean validate) {
        DOMParser parser = new DOMParser();
        try {
            if (validate) {
                parser.setEntityResolver(new EntityResolverRI());
                parser.setFeature(
                    "http://xml.org/sax/features/validation",
                    true);
            } else
                parser.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);
            parser.setFeature(
                "http://apache.org/xml/features/validation/schema",
                true);
            // parser.setFeature("http://xml.org/sax/features/namespaces",true);
            parser.setErrorHandler(new ErrorHandlerRI());
            parser.parse(source);
            document = parser.getDocument();
            Element rootElement = document.getDocumentElement();

            /* print out the elements */
            if (_trace)
                printElement(rootElement);

            return newInstance(rootElement, classname);
        } catch (Exception e) {
            System.out.println("Exception: Factory::loadDocument() " + e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load an existing instance document from an Element.
     */
    public BaseType loadDocument(String classname, Element element) {
        try {
            ComplexType domElement =
                (ComplexType) newInstance(element, classname);
            return domElement;
        } catch (Exception e) {
            System.out.println("Exception: Factory::loadDocument() " + e);
            return null;
        }
    }

    /**
     * Save the DOM to the specified file name
     */
    public void save(String filename) {
        String outputEncoding =
            (encoding != null && !encoding.equals("")) ? encoding : "UTF8";
        new DOMWriter(
            document,
            filename,
            outputEncoding,
            encodingTag,
            getDocTypeString());
    }

    /* Print the document
     * @param writer where to output
     * @param prefix prefix for each line of output
     */
    public void print(PrintWriter writer, String prefix) {
        new DOMWriter(
            document,
            writer,
            encodingTag,
            getDocTypeString(),
            prefix);
    }

    /**
     * Save the DOM to disk
     */
    public void save() {
        if (xmlFile != null) {
            String outputEncoding =
                (encoding != null && !encoding.equals("")) ? encoding : "UTF8";
            new DOMWriter(
                document,
                xmlFile,
                outputEncoding,
                encodingTag,
                getDocTypeString());
        }
    }

    /**
     * Create a new root element from a complex type
     *   @param classname - the name of the Java class that corresponds to the root element
     *   @param rootElementName - the root element name, it might contain a prefix
     *                            e.g. po:purchaseOrder or purchaseOrder
     *   @return ComplexType a Java bean that extends the ComplexType class
     */
    public ComplexType createRootDOMFromComplexType(
        String classname,
        String rootElementName) {
        return (ComplexType) createRootDOMHelper(classname, rootElementName);
    }

    /**
     * Create a new root element from a simple type
     *   @param classname - the name of the Java class that corresponds to the root element
     *   @param rootElementName - the root element name, it might contain a prefix
     *                            e.g. po:purchaseOrder or purchaseOrder
     *   @return SimpleType a Java bean that extends the SimpleType class
     */
    public SimpleType createRootDOMFromSimpleType(
        String classname,
        String rootElementName) {
        return (SimpleType) createRootDOMHelper(classname, rootElementName);
    }

    private Object createRootDOMHelper(
        String classname,
        String rootElementName) {
        document = new DocumentImpl();
        this.rootElementName = rootElementName;

        Element childElement = document.createElement(rootElementName);
        document.appendChild(childElement);

        if (xsdFileName != null && !xsdFileName.equals("")) {
            // This XML document is defined by an XSD file. Add the namespace attribute
            childElement.setAttribute(
                "xmlns:xsi",
                "http://www.w3.org/2001/XMLSchema-instance");

            // Set the schemaLocation for validation
            if (xsdNamespaceURI != null && !xsdNamespaceURI.equals("")) {
                int index = rootElementName.indexOf(":");
                if (index > 0) {
                    // Add the xmlns attribute for the current XSD file using the prefix
                    String prefix = ":" + rootElementName.substring(0, index);
                    childElement.setAttribute(
                        "xmlns" + prefix,
                        xsdNamespaceURI);
                } else {
                    // Add the xmlns attribute for the current XSD file - without prefix
                    childElement.setAttribute("xmlns", xsdNamespaceURI);
                }

                //
                // Add the xmlns attributes for all imported files
                // and compute the validation string to add to the 
                // xsi:schemaLocation attribute
                Enumeration enums = importedFileHashtable.keys();
                String importedFileValidation = "";
                while (enums.hasMoreElements()) {
                    Object nsprefix = enums.nextElement();
                    String nsURI = (String) importedFileHashtable.get(nsprefix);
                    int blank = nsURI.indexOf(' ');
                    childElement.setAttribute(
                        "xmlns:" + nsprefix,
                        nsURI.substring(0, blank));
                    importedFileValidation += " " + nsURI;
                }

                //
                // Add the xsi:schemaLocation attribute
                //
                childElement.setAttribute(
                    "xsi:schemaLocation",
                    xsdNamespaceURI
                        + " "
                        + xsdFileName
                        + importedFileValidation);
            } else {
                childElement.setAttribute(
                    "xsi:noNamespaceSchemaLocation",
                    xsdFileName);
            }
        }

        return newInstance(childElement, classname);
    }

    /**  
     * Create a new DOM element and the corresponding Java bean. 
     * Called by the generated Factory class.
     *   @param classname The name of the Java bean that corresponds to this element
     *   @param elementName The name of the element
     *   @return ComplexType A Java bean that extends the ComplexType class
     */
    public ComplexType createDOMElementFromComplexType(
        String classname,
        String elementName) {
        Element childElement = document.createElement(elementName);
        return (ComplexType) newInstance(childElement, classname);
    }

    /**  
     * Create a new DOM element and the corresponding Java bean. 
     * Called by the generated Factory class.
     *   @param classname The name of the Java bean that corresponds to this element
     *   @param elementName The name of the element
     *   @return SimpleType A Java bean that extends the ComplexType class
     */
    public SimpleType createDOMElementFromSimpleType(
        String classname,
        String elementName) {
        Element childElement = document.createElement(elementName);
        return (SimpleType) newInstance(childElement, classname);
    }

    /**
     * Create a new Java object for the input Node
     *  @param node - can be an element or an attribute
     *  @param className - the corresponding Java class name
     */
    protected BaseType newInstance(Node node, String className) {
        try {
            String fullname = className;
            if (packageName != null && !packageName.equals("")) {
                fullname = packageName + "." + className;
            }

            // Create the Java bean that wraps the DOM node
            Class javaClass = Class.forName(fullname);
            BaseType object = (BaseType) javaClass.newInstance();

            // Initialize the DOM node in the corresponding Java bean. It can be an 
            // element or an attribute (e.g. in the case when an element's type is a simple type)
            if (node instanceof Element) {
                object.setXMLElement((Element) node);
            } else {
                object.setXMLAttribute((Attr) node);
            }

            object.setFactory(this);

            return object;
        } catch (Exception e) {
            System.out.println("Factory::newInstance() error ***" + e);
            return null;
        }
    }

    /**
     * Create a new element and its text node
     *  @param elementName - the name of the new element
     *  @param elementValue - the value of the new element
     */
    protected Element createXMLElementAndText(
        String elementName,
        String elementValue) {
        Element item = document.createElement(elementName);
        Text text = document.createTextNode(elementValue);
        item.appendChild(text);
        return item;
    }

    /**
     * Create a new attribute and add it to the parent element
     */
    protected Attr createAttribute(String name, Element parentElement) {
        Attr attrNode = document.createAttribute(name);
        parentElement.setAttributeNode(attrNode);
        return attrNode;
    }

    /**
     * Create a new Text node and add it to the parent element
     */
    protected Text createText(Element parentElement, String value) {
        Text textNode = document.createTextNode(value);
        parentElement.appendChild(textNode);
        return textNode;
    }

    /**
     * Generate a DOCTYPE if the DTD filename is specified
     */
    private String getDocTypeString() {
        String docType = "";
        if (dtdFileName != null && !dtdFileName.equals("")) {
            if (dtdPublicId == null || dtdPublicId.equals("")) {
                // Generates a default public Id if it is not specified
                dtdPublicId = rootElementName + "Id";
            }

            docType =
                "<!DOCTYPE "
                    + rootElementName
                    + " PUBLIC \""
                    + dtdPublicId
                    + "\""
                    + " \""
                    + dtdFileName
                    + "\""
                    + ">";
        }
        return docType;
    }

    private static void printElement(Element elem) {
        printElement("", elem);
    }

    private static void printElement(String prefix, Element elem) {
        String newPrefix = prefix + "    ";
        System.out.println(
            prefix
                + " tagName: "
                + elem.getTagName()
                + " namespaceURI: "
                + elem.getNamespaceURI()
                + " localName: "
                + elem.getLocalName()
                + " node name "
                + elem.getNodeName());
        NamedNodeMap attrs = elem.getAttributes();
        if (attrs != null) {
            System.out.println(
                "  There are " + attrs.getLength() + " attributes");
            for (int i = 0; i < attrs.getLength(); i++) {
                Attr attr = (Attr) attrs.item(i);
                System.out.println(
                    prefix + "  " + attr.getName() + "=" + attr.getValue());

            }
        }
        NodeList children = elem.getChildNodes();
        int len = children.getLength();
        for (int i = 0; i < len; i++) {
            Node child = children.item(i);
            if (child instanceof Element)
                printElement(newPrefix, (Element) child);
            else
                System.out.println(
                    newPrefix
                        + "node: name "
                        + child.getNodeName()
                        + " namespaceURI: "
                        + child.getNamespaceURI()
                        + " localName: "
                        + child.getLocalName()
                        + " nodeName: "
                        + child.getNodeName());
        }

    }

    private static boolean _trace = false;

}
