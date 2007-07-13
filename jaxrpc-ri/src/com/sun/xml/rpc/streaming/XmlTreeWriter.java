/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.rpc.streaming;

import java.util.Stack;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.w3c.dom.Document;

import com.sun.xml.rpc.sp.NamespaceSupport;
import com.sun.xml.rpc.util.xml.CDATA;

/**
 * @author JAX-RPC Development Team
 */
public class XmlTreeWriter extends XMLWriterBase implements XMLWriter {

    protected Document document;
    protected SOAPElement currentNode;
    protected SOAPElement parentNode;
    protected PrefixFactory pfactory;
    protected NamespaceSupport ns = new NamespaceSupport();
    protected Stack elementStack = new Stack();
    private static SOAPFactory soapFactory;

    public XmlTreeWriter(Document document) {
        this.document = document;
    }

    protected static SOAPFactory getSoapFactory() throws SOAPException {
        if (soapFactory == null) {
            soapFactory = SOAPFactory.newInstance();
        }
        return soapFactory;
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#startElement(String, String)
     */
    public void startElement(String localName, String uri) {
        try {
            ns.pushContext();

            boolean mustDeclarePrefix = false;
            String aPrefix = null;
            if (!uri.equals("")) {

                aPrefix = getKnownPrefix(uri);

                if (aPrefix == null) {
                    mustDeclarePrefix = true;
                    aPrefix = createPrefix(uri);
                }
            }

            addNewNode(localName, aPrefix, uri);

            if (mustDeclarePrefix) {
                writeNamespaceDeclaration(aPrefix, uri);
            }
            elementStack.push(currentNode.getElementName().getLocalName());
        } catch (Exception e) {
            throw new XmlTreeWriterException(e.getMessage());
        }
    }

    protected String createPrefix(String uri) {
        String aPrefix = null;
        if (pfactory != null) {
            aPrefix = pfactory.getPrefix(uri);
        }

        if (aPrefix == null) {
            throw new XmlTreeWriterException("xmlwriter.noPrefixForURI " + uri);
        }
        return aPrefix;
    }

    protected String getKnownPrefix(String uri) {
        String aPrefix;
        String defaultNamespaceURI = ns.getPrefix("");
        if (defaultNamespaceURI != null && uri.equals(defaultNamespaceURI)) {
            aPrefix = "";
        } else {
            aPrefix = ns.getPrefix(uri);
        }

        return aPrefix;
    }

    protected SOAPElement addNewNode(
        String localName,
        String prefix,
        String uri)
        throws SOAPException {
        if (currentNode != null) {
            parentNode = currentNode;
            currentNode = parentNode.addChildElement(localName, prefix, uri);
        } else {
            currentNode =
                getSoapFactory().createElement(localName, prefix, uri);
            currentNode = (SOAPElement) document.importNode(currentNode, true);
            document.insertBefore(currentNode, null);
        }

        return currentNode;
    }
    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#startElement(String, String,
     *     String)
     */
    public void startElement(String localName, String uri, String prefix) {
        try {
            ns.pushContext();
            if (!uri.equals("")) {
                String aPrefix = null;
                boolean mustDeclarePrefix = false;

                String defaultNamespaceURI = ns.getPrefix("");
                if (defaultNamespaceURI != null) {
                    if (uri.equals(defaultNamespaceURI)) {
                        aPrefix = "";
                    }
                }

                aPrefix = ns.getPrefix(uri);
                if (aPrefix == null) {
                    mustDeclarePrefix = true;

                    aPrefix = prefix;

                    if (aPrefix == null) {
                        throw new XmlTreeWriterException("xmlwriter.noPrefixForURI");
                    }
                }
                addNewNode(localName, aPrefix, uri);
                if (mustDeclarePrefix) {
                    writeNamespaceDeclaration(aPrefix, uri);
                }

            } else {
                addNewNode(localName, prefix, uri);
            }
            elementStack.push(currentNode.getElementName().getLocalName());
        } catch (SOAPException se) {
            throw new XmlTreeWriterException(se.getMessage());
        }
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#writeAttribute(String, String,
     *     String)
     */

    public void writeAttribute(String localName, String uri, String value) {
        value = quote(value);
        writeAttributeUnquoted(localName, uri, value);
    }

    protected String quote(String value) {
        int i;
        boolean didReplacement = false;
        StringBuffer replacementString = null;

        eachCharacter : for (i = 0; i < value.length(); i++) {
            switch (value.charAt(i)) {

                case '"' :
                    replacementString = new StringBuffer(value.length() + 20);
                    replacementString.append(value.substring(0, i));
                    replacementString.append("&quot;");
                    didReplacement = true;
                    break eachCharacter;
                case '&' :
                    replacementString = new StringBuffer(value.length() + 20);
                    replacementString.append(value.substring(0, i));
                    replacementString.append("&amp;");
                    didReplacement = true;
                    break eachCharacter;
                case '<' :
                    replacementString = new StringBuffer(value.length() + 20);
                    replacementString.append(value.substring(0, i));
                    replacementString.append("&lt;");
                    didReplacement = true;
                    break eachCharacter;
                case '>' :
                    replacementString = new StringBuffer(value.length() + 20);
                    replacementString.append(value.substring(0, i));
                    replacementString.append("&gt;");
                    didReplacement = true;
                    break eachCharacter;
            }
        }

        if (didReplacement == false) {
            return value;
        }

        // need to increment counter to avoid quoting same char twice
        i++;
        
        for (; i < value.length(); i++) {
            char nextCharacter = value.charAt(i);
            switch (nextCharacter) {

                case '"' :
                    replacementString.append("&quot;");
                    break;
                case '&' :
                    replacementString.append("&amp;");
                    break;
                case '<' :
                    replacementString.append("&lt;");
                    break;
                case '>' :
                    replacementString.append("&gt;");
                    break;
                default :
                    replacementString.append(nextCharacter);
            }
        }

        return replacementString.toString();
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#writeAttributeUnquoted(String,
     *     String, String)
     */
    public void writeAttributeUnquoted(
        String localName,
        String uri,
        String value) {
        try {
            if (!uri.equals("")) {

                String aPrefix = null;
                boolean mustDeclarePrefix = false;

                String defaultNamespaceURI = ns.getPrefix("");
                if (defaultNamespaceURI != null) {
                    if (uri.equals(defaultNamespaceURI)) {
                        aPrefix = "";
                    }
                }

                aPrefix = ns.getPrefix(uri);

                if (aPrefix == null) {
                    mustDeclarePrefix = true;

                    if (pfactory != null) {
                        aPrefix = pfactory.getPrefix(uri);
                    }

                    if (aPrefix == null) {
                        throw new XmlTreeWriterException(
                            "No Prefix For URI " + uri);
                    }
                }

                currentNode.addAttribute(
                    getSoapFactory().createName(
                        localName,
                        aPrefix,
                        uri),
                    value);

                if (mustDeclarePrefix) {
                    writeNamespaceDeclaration(aPrefix, uri);
                }

            } else {
                currentNode.addAttribute(
                    getSoapFactory().createName(localName),
                    value);

            }

        } catch (SOAPException se) {
            throw new XmlTreeWriterException(se.getMessage());
        }
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#writeNamespaceDeclaration(
     *     String, String)
     */
    public void writeNamespaceDeclaration(String prefix, String uri) {

        ns.declarePrefix(prefix, uri);

        try {
            this.currentNode.addNamespaceDeclaration(prefix, uri);

        } catch (SOAPException se) {
            throw new XmlTreeWriterException(se.getMessage());
        }
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#writeNamespaceDeclaration(String)
     */
    public void writeNamespaceDeclaration(String uri) {

        if (pfactory == null) {
            throw new XmlTreeWriterException("No Prefix set for the XmlTreeWriter");
        }
        writeNamespaceDeclaration(pfactory.getPrefix(uri), uri);
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#writeChars(String)
     */
    public void writeChars(String chars) {
        
        // do not need to quote characters, addTextNode() will do it
        //chars = quote(chars);
        writeCharsUnquoted(chars);
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#writeCharsUnquoted(String)
     */
    public void writeCharsUnquoted(String chars) {
        try {
            this.currentNode.addTextNode(chars);

        } catch (SOAPException se) {
            throw new XmlTreeWriterException(se.getMessage());
        }
    }

    /**
     * unsupport
     */
    public void writeCharsUnquoted(char[] buf, int offset, int len) {
        writeCharsUnquoted(new String(buf, offset, len));
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#endElement()
     */
    public void endElement() {
        try {

            if (elementStack.size() >= 1) {
                elementStack.pop();
                currentNode = currentNode.getParentElement();
                ns.popContext();
            } else {
                throw new XmlTreeWriterException("All Elements have already closed");
            }
        } catch (Exception e) {
            throw new XmlTreeWriterException(e.getMessage());
        }
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#getPrefixFactory()
     */
    public PrefixFactory getPrefixFactory() {
        return pfactory;
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#setPrefixFactory(PrefixFactory)
     */
    public void setPrefixFactory(PrefixFactory factory) {
        this.pfactory = factory;
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#getURI(String)
     */
    public String getURI(String prefix) {

        //return this.currentNode.getNamespaceURI(prefix);
        return this.ns.getURI(prefix);
    }

    /**
     * @see com.sun.xml.rpc.streaming.XMLWriter#getPrefix(String)
     */
    public String getPrefix(String uri) {
        /*
                return this.pfactory.getPrefix(uri);
        */
        return this.ns.getPrefix(uri);
    }

    /**
     * 
     */
    public void flush() {
    }

    /**
     * 
     */
    public void close() {
    }

    public void writeChars(CDATA chars) {
        try {
            this.currentNode.addTextNode(chars.getText());

        } catch (SOAPException se) {
            throw new XmlTreeWriterException(se.getMessage());
        }
    }

}
