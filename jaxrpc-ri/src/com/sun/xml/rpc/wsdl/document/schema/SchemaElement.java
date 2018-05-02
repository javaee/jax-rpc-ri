/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.rpc.wsdl.document.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.util.NullIterator;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.framework.ValidationException;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SchemaElement {

    public SchemaElement() {
    }

    public SchemaElement(String localName) {
        _localName = localName;
    }

    public SchemaElement(QName name) {
        _qname = name;
        _localName = name.getLocalPart();
        _nsURI = name.getNamespaceURI();
    }

    public String getNamespaceURI() {
        return _nsURI;
    }

    public void setNamespaceURI(String s) {
        _nsURI = s;
    }

    public String getLocalName() {
        return _localName;
    }

    public void setLocalName(String s) {
        _localName = s;
    }

    public QName getQName() {
        if (_qname == null) {
            _qname = new QName(_nsURI, _localName);
        }
        return _qname;
    }

    public SchemaElement getParent() {
        return _parent;
    }

    public void setParent(SchemaElement e) {
        _parent = e;
    }

    public SchemaElement getRoot() {
        return _parent == null ? this : _parent.getRoot();
    }

    public Schema getSchema() {
        return _parent == null ? _schema : _parent.getSchema();
    }

    public void setSchema(Schema s) {
        _schema = s;
    }

    public void addChild(SchemaElement e) {
        if (_children == null) {
            _children = new ArrayList();
        }

        _children.add(e);
        e.setParent(this);
    }

    public void insertChildAtTop(SchemaElement e) {
        if (_children == null) {
            _children = new ArrayList();
        }

        _children.add(0, e);
        e.setParent(this);
    }

    public Iterator children() {
        if (_children == null) {
            return NullIterator.getInstance();
        } else {
            return _children.iterator();
        }
    }

    public void addAttribute(SchemaAttribute a) {
        if (_attributes == null) {
            _attributes = new ArrayList();
        }

        _attributes.add(a);
        a.setParent(this);
        a.getValue();
        // this is a hack to force namespace declarations to be added, if needed
    }

    public void addAttribute(String name, String value) {
        SchemaAttribute attr = new SchemaAttribute();
        attr.setLocalName(name);
        attr.setValue(value);
        addAttribute(attr);
    }

    public void addAttribute(String name, QName value) {
        SchemaAttribute attr = new SchemaAttribute();
        attr.setLocalName(name);
        attr.setValue(value);
        addAttribute(attr);
    }

    public Iterator attributes() {
        if (_attributes == null) {
            return NullIterator.getInstance();
        } else {
            return _attributes.iterator();
        }
    }

    public SchemaAttribute getAttribute(String localName) {
        if (_attributes != null) {
            for (Iterator iter = _attributes.iterator(); iter.hasNext();) {
                SchemaAttribute attr = (SchemaAttribute) iter.next();
                if (localName.equals(attr.getLocalName())) {
                    return attr;
                }
            }
        }
        return null;
    }

    public String getValueOfMandatoryAttribute(String localName) {
        SchemaAttribute attr = getAttribute(localName);
        if (attr == null) {
            throw new ValidationException(
                "validation.missingRequiredAttribute",
                new Object[] { localName, _localName });
        }
        return attr.getValue();
    }

    public String getValueOfAttributeOrNull(String localName) {
        SchemaAttribute attr = getAttribute(localName);
        if (attr == null) {
            return null;
        } else {
            return attr.getValue();
        }
    }

    public boolean getValueOfBooleanAttributeOrDefault(
        String localName,
        boolean defaultValue) {
        String stringValue = getValueOfAttributeOrNull(localName);
        if (stringValue == null) {
            return defaultValue;
        }
        if (stringValue.equals("true") || stringValue.equals("1")) {
            return true;
        } else if (stringValue.equals("false") || stringValue.equals("0")) {
            return false;
        } else {
            throw new ValidationException(
                "validation.invalidAttributeValue",
                new Object[] { localName, stringValue });
        }
    }

    public int getValueOfIntegerAttributeOrDefault(
        String localName,
        int defaultValue) {
        String stringValue = getValueOfAttributeOrNull(localName);
        if (stringValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            throw new ValidationException(
                "validation.invalidAttributeValue",
                new Object[] { localName, stringValue });
        }
    }

    public QName getValueOfQNameAttributeOrNull(String localName) {
        String stringValue = getValueOfAttributeOrNull(localName);
        if (stringValue == null)
            return null;

        String prefix = XmlUtil.getPrefix(stringValue);
        String uri =
            (prefix == null ? getURIForPrefix("") : getURIForPrefix(prefix));
        if (uri == null) {
            throw new ValidationException(
                "validation.invalidAttributeValue",
                new Object[] { localName, stringValue });
        }
        return new QName(uri, XmlUtil.getLocalPart(stringValue));
    }

    public void addPrefix(String prefix, String uri) {
        if (_nsPrefixes == null) {
            _nsPrefixes = new HashMap();
        }

        _nsPrefixes.put(prefix, uri);
    }

    public String getURIForPrefix(String prefix) {
        if (_nsPrefixes != null) {
            String result = (String) _nsPrefixes.get(prefix);
            if (result != null)
                return result;
        }
        if (_parent != null) {
            return _parent.getURIForPrefix(prefix);
        }
        if (_schema != null) {
            return _schema.getURIForPrefix(prefix);
        }
        // give up
        return null;
    }

    public boolean declaresPrefixes() {
        return _nsPrefixes != null;
    }

    public Iterator prefixes() {
        if (_nsPrefixes == null) {
            return NullIterator.getInstance();
        } else {
            return _nsPrefixes.keySet().iterator();
        }
    }

    public QName asQName(String s) {
        String prefix = XmlUtil.getPrefix(s);
        if (prefix == null) {
            prefix = "";
        }
        String uri = getURIForPrefix(prefix);
        if (uri == null) {
            throw new ValidationException("validation.invalidPrefix", prefix);
        }
        String localPart = XmlUtil.getLocalPart(s);
        return new QName(uri, localPart);
    }

    public String asString(QName name) {
        if (name.getNamespaceURI().equals("")) {
            return name.getLocalPart();
        } else {
            // look for a prefix
            for (Iterator iter = prefixes(); iter.hasNext();) {
                String prefix = (String) iter.next();
                String uri = getURIForPrefix(prefix);
                if (uri.equals(name.getNamespaceURI())) {
                    if (prefix.equals("")) {
                        return name.getLocalPart();
                    } else {
                        return prefix + ":" + name.getLocalPart();
                    }
                }
            }

            // not found
            if (_parent != null) {
                return _parent.asString(name);
            }
            if (_schema != null) {
                String result = _schema.asString(name);
                if (result != null) {
                    return result;
                }
            }

            // not found and no parent
            String prefix = getNewPrefix();
            addPrefix(prefix, name.getNamespaceURI());
            return asString(name);
        }
    }

    protected String getNewPrefix() {
        String base = NEW_NS_PREFIX_BASE;
        int count = 2;
        String prefix = null;
        for (boolean needNewOne = true; needNewOne; ++count) {
            prefix = base + Integer.toString(count);
            needNewOne = getURIForPrefix(prefix) != null;
        }
        return prefix;
    }

    private String _nsURI;
    private String _localName;
    private List _children;
    private List _attributes;
    private Map _nsPrefixes;
    private SchemaElement _parent;
    private QName _qname;
    private Schema _schema;

    private static final String NEW_NS_PREFIX_BASE = "ns";
}
