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

/**
*
* @author JAX-RPC Development Team
*/
package com.sun.xml.rpc.streaming;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Text;

import org.w3c.dom.Element;



public class XmlTreeReader extends XMLReaderBase {
    protected SOAPElement root;
    protected SOAPElement currentNode;
    protected int state;
    protected QName name;
    protected ElementIdStack elementIds;
    protected int elementId;
    protected String currentValue;
    protected AttributesAdapter attributes = new AttributesAdapter();

    public XmlTreeReader(SOAPElement root) {
        elementIds = new ElementIdStack();
        setRoot(root);
    }

    private void setRoot(SOAPElement root) {
        this.root = root;
        this.state = BOF;
    }

    public void close() {
        state = EOF;
    }

    public Attributes getAttributes() {
        attributes.initialize();
        return attributes;
    }

    public int getElementId() {
        return elementId;
    }

    // not supported
    public int getLineNumber() {
        return 0;
    }

    public String getLocalName() {
        return currentNode.getElementName().getLocalName();
    }

	//Narayana Murthy P: Added this method to return the currentNode 
    public Element getCurrentNode() {
		return currentNode;
	}

    public QName getName() {
        if (name == null) {
            name = new QName(getURI(), getLocalName());
        }
        return name;
    }

    public Iterator getPrefixes() {
        return currentNode.getVisibleNamespacePrefixes();
    }

    public int getState() {
        return state;
    }

    public String getURI() {

        return currentNode.getElementName().getURI();
    }

    public String getURI(String prefix) {
        return currentNode.getNamespaceURI(prefix);
    }

    public String getValue() {
        //TODO: remove once SAAJ fixes text splitting bug
        currentNode.normalize();
        return currentNode.getValue();
    }

    public int next() {
        if (state == EOF) {
            return EOF;
        }
        name = null;
        attributes.unintialize();

        parse(); // sets state, currentNode
        switch (state) {
            case START :
                elementId = elementIds.pushNext();
                break;
            case END :
                elementId = elementIds.pop();
                break;
            case CHARS :
            case EOF :
            case PI :
                break;
            default :
                throw new XMLReaderException(
                    "xmlreader.illegalStateEncountered",
                    Integer.toString(state));
        }
        return state;
    }

    public void parse() {

        switch (state) {
            case BOF :
                currentNode = root;
                state = START;
                break;

            case START :
                // SAAJ tree might contain multiple contiguous text nodes
                currentNode.normalize();
                org.w3c.dom.Node first = currentNode.getFirstChild();
                if (first != null) {
                    if (first instanceof Text) {
                        org.w3c.dom.Node sec = first.getNextSibling();
                        if (sec != null) {
                            // ignoring inter-element whitespace
                            currentNode = (SOAPElement) sec;
                            state = START;
                        } else {
                            state = CHARS;
                            currentValue = currentNode.getValue();
                            if (currentValue == null) {
                                state = END;
                            }
                        }
                    } else if (first instanceof SOAPElement) {
                        state = START;
                        currentNode = (SOAPElement) first;
                    } else {
                        throw new XMLReaderException(
                            "xmlreader.illegalType " + first.getClass());
                    }
                } else {
                    state = END;
                }
                break;

            case END :
                org.w3c.dom.Node nextNode = currentNode.getNextSibling();
                if (nextNode != null && nextNode instanceof Text) {
                    // ignoring inter-element whitespace
                    nextNode = nextNode.getNextSibling();
                }
                if (nextNode == null) {
                    // use root instead null so that it works with subtrees
                    if (currentNode == root) {
                        state = EOF;
                    } else {
                        state = END;
                        currentNode = currentNode.getParentElement();
                    }
                } else {
                    state = START;
                    currentNode = (SOAPElement) nextNode;
                }
                break;

            case EOF :
                break;
            case PI :
                break;
            case CHARS :
                state = END;
                break;
            default :
                throw new XMLReaderException(
                    "xmlreader.illegalStateEncountered",
                    Integer.toString(state));
        }
    }

    public XMLReader recordElement() {
        state = END;
        return new XmlTreeReader(currentNode);
    }

    public void skipElement(int elementId) {
        if (state == EOF)
            return;
        while (this.elementId != elementId) {
            currentNode = currentNode.getParentElement();
            this.elementId = elementIds.pop();
        }
        state = END;
    }

    public class AttributesAdapter implements Attributes {
        static final String XMLNS_NAMESPACE_URI =
            "http://www.w3.org/2000/xmlns/";

        private boolean initialized = false;
        ArrayList attributeUris = new ArrayList();
        ArrayList attributePrefixes = new ArrayList();
        ArrayList attributeNames = new ArrayList();
        ArrayList attributeValues = new ArrayList();
        ArrayList attributeQNames = new ArrayList();

        void unintialize() {
            if (initialized) {
                attributeUris.clear();
                attributePrefixes.clear();
                attributeNames.clear();
                attributeValues.clear();
                attributeQNames.clear();
                initialized = false;
            }
        }
        void initialize() {
            if (!initialized) {
                Iterator eachAttribute = currentNode.getAllAttributes();
                while (eachAttribute.hasNext()) {
                    Name name = (Name) eachAttribute.next();
                    attributeUris.add(name.getURI());
                    attributePrefixes.add(name.getPrefix());
                    attributeNames.add(name.getLocalName());
                    attributeValues.add(currentNode.getAttributeValue(name));
                    attributeQNames.add(null);
                }
                initialized = true;
            }
        }

        public int getLength() {
            initialize();
            return attributeValues.size();
        }

        public boolean isNamespaceDeclaration(int index) {
            initialize();
            return XMLNS_NAMESPACE_URI.equals(getURI(index));
        }

        public QName getName(int index) {
            initialize();
            if (attributeQNames.get(index) == null) {
                QName qname = new QName(getURI(index), getLocalName(index));
                attributeQNames.set(index, qname);
            }
            return (QName) attributeQNames.get(index);
        }

        public String getURI(int index) {
            initialize();
            return (String) attributeUris.get(index);
        }

        public String getLocalName(int index) {
            initialize();
            return (String) attributeNames.get(index);
        }

        public String getPrefix(int index) {
            initialize();
            String prefix = (String) attributePrefixes.get(index);
            // return null instead of "" to be compatible with XMLReaderImpl
            if (prefix != null && prefix.equals("")) {
                prefix = null;
            }
            return prefix;
        }

        public String getValue(int index) {
            initialize();
            return (String) attributeValues.get(index);
        }

        public int getIndex(QName name) {
            return getIndex(name.getNamespaceURI(), name.getLocalPart());
        }

        public int getIndex(String uri, String localName) {
            initialize();

            for (int index = 0; index < attributeNames.size(); ++index) {
                if (attributeUris.get(index).equals(uri)
                    && attributeNames.get(index).equals(localName)) {
                    return index;
                }
            }

            return -1;
        }

        public int getIndex(String localName) {
            initialize();

            for (int index = 0; index < attributeNames.size(); ++index) {
                if (attributeNames.get(index).equals(localName)) {
                    return index;
                }
            }

            return -1;
        }

        public String getValue(QName name) {
            int index = getIndex(name);
            if (index != -1) {
                return (String) attributeValues.get(index);
            }
            return null;
        }

        public String getValue(String uri, String localName) {
            int index = getIndex(uri, localName);
            if (index != -1) {
                return (String) attributeValues.get(index);
            }
            return null;
        }

        public String getValue(String localName) {
            int index = getIndex(localName);
            if (index != -1) {
                return (String) attributeValues.get(index);
            }
            return null;
        }
    }

}
