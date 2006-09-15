/*
 * $Id: WriterContext.java,v 1.2 2006-04-13 01:34:40 ofung Exp $
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

package com.sun.xml.rpc.wsdl.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.sp.NamespaceSupport;
import com.sun.xml.rpc.util.xml.PrettyPrintingXmlWriter;

/**
 * The context used by writer classes.
 *
 * @author JAX-RPC Development Team
 */
public class WriterContext {

    public WriterContext(OutputStream os) throws IOException {
        _writer = new PrettyPrintingXmlWriter(os);
        _nsSupport = new NamespaceSupport();
        _newPrefixCount = 2;
    }

    public void flush() throws IOException {
        _writer.flush();
    }

    public void close() throws IOException {
        _writer.close();
    }

    public void push() {
        if (_pendingNamespaceDeclarations != null) {
            throw new IllegalStateException("prefix declarations are pending");
        }
        _nsSupport.pushContext();
    }

    public void pop() {
        _nsSupport.popContext();
        _pendingNamespaceDeclarations = null;
    }

    public String getNamespaceURI(String prefix) {
        return _nsSupport.getURI(prefix);
    }

    public Iterator getPrefixes() {
        return _nsSupport.getPrefixes();
    }

    public String getDefaultNamespaceURI() {
        return getNamespaceURI("");
    }

    public void declarePrefix(String prefix, String uri) {
        _nsSupport.declarePrefix(prefix, uri);
        if (_pendingNamespaceDeclarations == null) {
            _pendingNamespaceDeclarations = new ArrayList();
        }
        _pendingNamespaceDeclarations.add(new String[] { prefix, uri });
    }

    public String getPrefixFor(String uri) {
        if (getDefaultNamespaceURI().equals(uri)) {
            return "";
        } else {
            return _nsSupport.getPrefix(uri);
        }
    }

    public String findNewPrefix(String base) {
        return base + Integer.toString(_newPrefixCount++);
    }

    public String getTargetNamespaceURI() {
        return _targetNamespaceURI;
    }

    public void setTargetNamespaceURI(String uri) {
        _targetNamespaceURI = uri;
    }

    public void writeStartTag(QName name) throws IOException {
        _writer.start(getQNameString(name));
    }

    public void writeEndTag(QName name) throws IOException {
        _writer.end(getQNameString(name));
    }

    public void writeAttribute(String name, String value) throws IOException {
        if (value != null) {
            _writer.attribute(name, value);
        }
    }

    public void writeAttribute(String name, QName value) throws IOException {
        if (value != null) {
            _writer.attribute(name, getQNameString(value));
        }
    }

    public void writeAttribute(String name, boolean value) throws IOException {
        writeAttribute(name, value ? "true" : "false");
    }

    public void writeAttribute(String name, Boolean value) throws IOException {
        if (value != null) {
            writeAttribute(name, value.booleanValue());
        }
    }

    public void writeAttribute(String name, int value) throws IOException {
        writeAttribute(name, Integer.toString(value));
    }

    public void writeAttribute(String name, Object value, Map valueToXmlMap)
        throws IOException {
        String actualValue = (String) valueToXmlMap.get(value);
        writeAttribute(name, actualValue);
    }

    public void writeNamespaceDeclaration(String prefix, String uri)
        throws IOException {
        _writer.attribute(getNamespaceDeclarationAttributeName(prefix), uri);
    }

    public void writeAllPendingNamespaceDeclarations() throws IOException {
        if (_pendingNamespaceDeclarations != null) {
            for (Iterator iter = _pendingNamespaceDeclarations.iterator();
                iter.hasNext();
                ) {
                String[] pair = (String[]) iter.next();
                writeNamespaceDeclaration(pair[0], pair[1]);
            }
        }
        _pendingNamespaceDeclarations = null;
    }

    private String getNamespaceDeclarationAttributeName(String prefix) {
        if (prefix.equals("")) {
            return "xmlns";
        } else {
            return "xmlns:" + prefix;
        }
    }

    public void writeTag(QName name, String value) throws IOException {
        _writer.leaf(getQNameString(name), value);
    }

    public String getQNameString(QName name) {
        String prefix = getPrefixFor(name.getNamespaceURI());
        if (prefix == null) {
            throw new IllegalArgumentException();
        } else if (prefix.equals("")) {
            return name.getLocalPart();
        } else {
            return prefix + ":" + name.getLocalPart();
        }
    }

    public String getQNameStringWithTargetNamespaceCheck(QName name) {
        if (name.getNamespaceURI().equals(_targetNamespaceURI)) {
            return name.getLocalPart();
        } else {
            return getQNameString(name);
        }
    }

    private PrettyPrintingXmlWriter _writer;
    private NamespaceSupport _nsSupport;
    private String _targetNamespaceURI;
    private int _newPrefixCount;
    private List _pendingNamespaceDeclarations;
}