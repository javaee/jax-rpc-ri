/*
 * $Id: XMLReaderImpl.java,v 1.2 2006-04-13 01:33:20 ofung Exp $
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

package com.sun.xml.rpc.streaming;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.xml.sax.InputSource;

import com.sun.xml.rpc.sp.AttributesEx;
import com.sun.xml.rpc.sp.ParseException;
import com.sun.xml.rpc.sp.Parser2;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.XmlUtil;

/**
 * <p> A concrete XMLReader implementation class. </p>
 *
 * <p> This implementation uses a specially modified streaming parser
 * to avoid duplicating work already done by the parser itself.
 * This approach makes normal parsing very fast, but recording is
 * expected to be quite poor performancewise. </p>
 *
 * @author JAX-RPC Development Team
 */
public class XMLReaderImpl extends XMLReaderBase {

    public XMLReaderImpl(InputSource source) {
        this(source, false);
    }

    public XMLReaderImpl(InputSource source, boolean rejectDTDs) {
        _state = BOF;

        _stream = source.getByteStream();

        _parser = new Parser2(_stream, true, true, rejectDTDs);
        _elementIds = new ElementIdStack();
        _attributeAdapter = new AttributesAdapter();
    }

    public void close() {
        try {
            _state = EOF;

            _stream.close();
        } catch (IOException e) {
            throw new XMLReaderException(
                "xmlreader.ioException",
                new LocalizableExceptionAdapter(e));
        }
    }

    public int getState() {
        return _state;
    }

    public QName getName() {
        if (_name == null) {
            _name = new QName(getURI(), getLocalName());
        }
        return _name;
    }

    public String getURI() {
        return _parser.getCurURI();
    }

    public String getLocalName() {
        return _parser.getCurName();
    }

    public Attributes getAttributes() {
        _attributeAdapter.setTarget(_parser.getAttributes());
        return _attributeAdapter;
    }

    public String getValue() {
        return _parser.getCurValue();
    }

    public int getElementId() {
        return _elementId;
    }

    public int getLineNumber() {
        return _parser.getLineNumber();
    }

    public String getURI(String prefix) {
        return _parser.getNamespaceSupport().getURI(prefix);
    }

    public Iterator getPrefixes() {
        return _parser.getNamespaceSupport().getPrefixes();
    }

    public int next() {
        if (_state == EOF) {
            return EOF;
        }

        _name = null;

        try {
            _state = _parser.parse();
            if (_state == DOC_END) {
                _state = EOF;
            }
        } catch (ParseException e) {
            throw new XMLReaderException(
                "xmlreader.parseException",
                new LocalizableExceptionAdapter(e));
        } catch (IOException e) {
            throw new XMLReaderException(
                "xmlreader.ioException",
                new LocalizableExceptionAdapter(e));
        }

        switch (_state) {
            case START :
                _elementId = _elementIds.pushNext();
                break;
            case END :
                _elementId = _elementIds.pop();
                break;
            case CHARS :
            case EOF :
            case PI :
                break;
            default :
                throw new XMLReaderException(
                    "xmlreader.illegalStateEncountered",
                    Integer.toString(_state));
        }

        return _state;
    }

    public XMLReader recordElement() {
        return new RecordedXMLReader(this, _parser.getNamespaceSupport());

        //throw new UnsupportedOperationException();
    }

    public void skipElement(int elementId) {
        while (!(_state == EOF
            || (_state == END && _elementId == elementId))) {
            next();
        }
    }

    private int _state;
    private QName _name;
    private InputStream _stream;
    private AttributesAdapter _attributeAdapter;
    private ElementIdStack _elementIds;
    private int _elementId;
    private Parser2 _parser;

    private static final int DOC_END = -1;
    private static final int DOC_START = -2;
    private static final int EMPTY = -3;
    private static final int EXCEPTION = -4;

    static class AttributesAdapter implements Attributes {
        public AttributesAdapter() {
        }

        public void setTarget(AttributesEx attr) {
            _attr = attr;
        }

        public int getLength() {
            return _attr.getLength();
        }

        public boolean isNamespaceDeclaration(int index) {
            // use "==" instead of equals() because we know that the string
            // will always be interned
            return _attr.getURI(index) == XMLNS_NAMESPACE_URI;
        }

        public QName getName(int index) {
            return new QName(getURI(index), getLocalName(index));
        }

        public String getURI(int index) {
            return _attr.getURI(index);
        }

        public String getLocalName(int index) {
            return _attr.getLocalName(index);
        }

        public String getPrefix(int index) {
            String qname = _attr.getQName(index);
            if (qname == null) {
                return null;
            } else {
                return XmlUtil.getPrefix(qname);
            }
        }

        public String getValue(int index) {
            return _attr.getValue(index);
        }

        public int getIndex(QName name) {
            return _attr.getIndex(name.getNamespaceURI(), name.getLocalPart());
        }

        public int getIndex(String uri, String localName) {
            return _attr.getIndex(uri, localName);
        }

        public int getIndex(String localName) {
            return _attr.getIndex(localName);
        }

        public String getValue(QName name) {
            return _attr.getValue(name.getNamespaceURI(), name.getLocalPart());
        }

        public String getValue(String uri, String localName) {
            return _attr.getValue(uri, localName);
        }

        public String getValue(String localName) {
            return _attr.getValue(localName);
        }

        public String toString() {
            StringBuffer attributes = new StringBuffer();
            for (int i = 0; i < getLength(); ++i) {
                if (i != 0) {
                    attributes.append("\n");
                }
                attributes.append(
                    getURI(i) + ":" + getLocalName(i) + " = " + getValue(i));
            }
            return attributes.toString();
        }

        private AttributesEx _attr;

        static final String XMLNS_NAMESPACE_URI =
            "http://www.w3.org/2000/xmlns/";
    }
}
