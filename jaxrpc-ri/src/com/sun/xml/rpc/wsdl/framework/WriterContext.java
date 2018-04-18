/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: WriterContext.java,v 1.3 2007-07-13 23:36:46 ofung Exp $
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
