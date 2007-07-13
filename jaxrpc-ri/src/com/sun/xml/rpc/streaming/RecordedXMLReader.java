/*
 * $Id: RecordedXMLReader.java,v 1.3 2007-07-13 23:36:33 ofung Exp $
 */

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

/**
*
* @author JAX-RPC Development Team
*/
package com.sun.xml.rpc.streaming;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.sp.NamespaceSupport;
import com.sun.xml.rpc.util.StructMap;
import com.sun.xml.rpc.util.xml.XmlUtil;

public class RecordedXMLReader extends XMLReaderBase {
    //protected static final QName EMPTY_QNAME = new QName("");
    protected static final QName EMPTY_QNAME = new QName("fooqname");
    int frameIndex;
    List frames;
    ReaderFrame currentFrame;
    NamespaceSupport originalNamespaces;
    NamespaceSupport namespaceSupport;
    boolean lastRetWasEnd;

    public RecordedXMLReader(XMLReader reader, NamespaceSupport namespaces) {
        frameIndex = 0;
        frames = new ArrayList();

        originalNamespaces = new NamespaceSupport(namespaces);
        namespaceSupport = new NamespaceSupport(namespaces);
        lastRetWasEnd = false;

        int targetElementId = reader.getElementId();

        while (reader.getState() != END
            || reader.getElementId() != targetElementId) {
            recordFrame(reader);
            reader.next();
        }
        recordFrame(reader);

        setFrame(0);
    }

    protected void recordFrame(XMLReader reader) {
        Attributes attributeFrame = null;
        switch (reader.getState()) {
            case START :
                attributeFrame = new AttributeFrame(reader.getAttributes());
            case END :
                addFrame(
                    new ReaderFrame(
                        reader.getState(),
                        reader.getElementId(),
                        reader.getLineNumber(),
                        reader.getName(),
                        attributeFrame));
                break;
            case PI :
                // we neither anticipate nor handle Processing instructions
                break;
            case CHARS :
                addFrame(
                    new ReaderFrame(
                        reader.getState(),
                        reader.getElementId(),
                        reader.getLineNumber(),
                        reader.getValue()));
            default :
                // TODO: throw an exception
        }
    }

    protected void addFrame(ReaderFrame frame) {
        frames.add(frame);
    }
    protected ReaderFrame getFrame(int index) {
        return (ReaderFrame) frames.get(index);
    }
    protected void setFrame(int index) {
        currentFrame = getFrame(index);
        frameIndex = index;
    }
    protected void nextFrame() {
        setFrame(frameIndex + 1);
    }
    public void reset() {
        frameIndex = 0;
        lastRetWasEnd = false;
    }

    static class ReaderFrame {
        QName name;
        int state;
        Attributes attributes;
        String value;
        int elementId;
        int lineNumber;

        ReaderFrame(int state) {
            this.state = state;
            this.name = EMPTY_QNAME;
            this.attributes = null;
            this.value = null;
            this.elementId = -1;
            this.lineNumber = 0;
        }
        ReaderFrame(int state, int elementId, int lineNumber) {
            this(state);
            this.elementId = elementId;
            this.lineNumber = lineNumber;
        }
        ReaderFrame(
            int state,
            int elementId,
            int lineNumber,
            QName name,
            Attributes attributes) {
            this(state, elementId, lineNumber);
            this.name = name;
            this.attributes = attributes;
        }
        ReaderFrame(int state, int elementId, int lineNumber, String value) {
            this(state, elementId, lineNumber);
            this.value = value;
        }
    }

    static class AttributeFrame implements Attributes {
        private static final String XMLNS_NAMESPACE_URI =
            XMLReaderImpl.AttributesAdapter.XMLNS_NAMESPACE_URI;

        StructMap recordedAttributes = new StructMap();
        List qnames = null;
        List qnameLocalParts = null;
        List values = null;

        AttributeFrame(Attributes attributes) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                recordedAttributes.put(
                    attributes.getName(i),
                    attributes.getValue(i));
            }
        }

        List getQNames() {
            if (qnames == null) {
                qnames = (List) recordedAttributes.keys();
            }

            return qnames;
        }

        //needed for bug 483378
        List getQNameLocalParts() {
            List tempQNames = new ArrayList();

            if (qnames == null) {
                qnames = (List) recordedAttributes.keys();
            }

            if (qnameLocalParts == null)
                qnameLocalParts = new ArrayList();
            //make list of qnameLocalParts(Strings)
            for (int i = 0; i < qnames.size(); i++) {
                QName qname = (QName) qnames.get(i);
                qnameLocalParts.add(qname.getLocalPart());
            }
            return qnameLocalParts;
        }

        List getValues() {
            if (values == null) {
                values = (List) recordedAttributes.values();
            }

            return values;
        }
        public int getIndex(QName name) {
            List qnames = getQNames();

            for (int i = 0; i < qnames.size(); ++i) {
                if (qnames.get(i).equals(name)) {
                    return i;
                }
            }

            return -1;
        }

        public int getIndex(String uri, String localName) {
            List qnames = getQNames();

            for (int i = 0; i < qnames.size(); ++i) {
                QName qname = (QName) qnames.get(i);
                if (qname.getNamespaceURI().equals(uri)
                    && qname.getLocalPart().equals(localName)) {
                    return i;
                }
            }

            return -1;
        }

        public int getIndex(String localName) {
            List qnames = getQNames();

            for (int i = 0; i < qnames.size(); ++i) {
                QName qname = (QName) qnames.get(i);
                if (qname.getLocalPart().equals(localName)) {
                    return i;
                }
            }

            return -1;
        }

        public int getLength() {
            return recordedAttributes.size();
        }

        public String getLocalName(int index) {
            return getName(index).getLocalPart();
        }

        public QName getName(int index) {
            List qnames = getQNames();

            return (QName) qnames.get(index);
        }

        public String getPrefix(int index) {
            QName qname = getName(index);

            return XmlUtil.getPrefix(qname.getNamespaceURI());
        }

        public String getURI(int index) {
            return getName(index).getNamespaceURI();
        }

        public String getValue(int index) {
            if (index == -1) {
                return null;
            }

            List values = getValues();

            return (String) values.get(index);
        }

        public String getValue(QName name) {
            return getValue(getIndex(name));
        }

        public String getValue(String uri, String localName) {
            return getValue(getIndex(uri, localName));
        }

        public String getValue(String localName) {
            return getValue(getIndex(localName));
        }

        public boolean isNamespaceDeclaration(int index) {
            return getURI(index) == XMLNS_NAMESPACE_URI;
        }
    }

    public void close() {
        reset();
    }

    public int getState() {
        return currentFrame.state;
    }

    public QName getName() {
        return currentFrame.name;
    }

    public String getURI() {
        return getName().getNamespaceURI();
    }

    public String getLocalName() {
        return getName().getLocalPart();
    }

    public Attributes getAttributes() {
        return currentFrame.attributes;
    }

    public String getValue() {
        return currentFrame.value;
    }

    public int getElementId() {
        return currentFrame.elementId;
    }

    public int getLineNumber() {
        return currentFrame.lineNumber;
    }

    public String getURI(String prefix) {
        return namespaceSupport.getURI(prefix);
    }

    public Iterator getPrefixes() {
        return namespaceSupport.getPrefixes();
    }

    public int next() {
        if (frameIndex + 1 >= frames.size() - 1) {
            // throw new StreamingException("xmlrecorder.recording.ended");
            return EOF;
        }
        nextFrame();
        int ret = getState();

        if (lastRetWasEnd) {
            namespaceSupport.popContext();
            lastRetWasEnd = false;
        }

        if (ret == START) {
            namespaceSupport.pushContext();
            Attributes attributes = getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                if (attributes.isNamespaceDeclaration(i)) {
                    String prefix = attributes.getLocalName(i);
                    String value = attributes.getValue(i);
                    namespaceSupport.declarePrefix(prefix, value);
                }
            }
        } else if (ret == END) {
            lastRetWasEnd = true;
        }

        return ret;
    }

    public XMLReader recordElement() {
        return new RecordedXMLReader(this, namespaceSupport);
    }

    public void skipElement(int elementId) {
        while (!(currentFrame.state == EOF
            || (currentFrame.state == END
                && currentFrame.elementId == elementId))) {
            if (next() == EOF) {
                return;
            }
        }
    }
}
