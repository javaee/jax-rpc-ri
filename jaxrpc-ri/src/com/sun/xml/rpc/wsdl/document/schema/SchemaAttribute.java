/*
 * $Id: SchemaAttribute.java,v 1.1 2006-04-12 20:33:38 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.schema;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.WriterContext;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SchemaAttribute {

    public SchemaAttribute() {
    }

    public SchemaAttribute(String localName) {
        _localName = localName;
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
        return new QName(_nsURI, _localName);
    }

    public String getValue() {
        if (_qnameValue != null) {
            if (_parent == null) {
                throw new IllegalStateException();
            } else {
                return _parent.asString(_qnameValue);
            }
        } else {
            return _value;
        }
    }

    public String getValue(WriterContext context) {
        if (_qnameValue != null) {
            return context.getQNameString(_qnameValue);
        } else {
            return _value;
        }
    }

    public void setValue(String s) {
        _value = s;
    }

    public void setValue(QName name) {
        _qnameValue = name;
    }

    public SchemaElement getParent() {
        return _parent;
    }

    public void setParent(SchemaElement e) {
        _parent = e;
    }

    private String _nsURI;
    private String _localName;
    private String _value;
    private QName _qnameValue;
    private SchemaElement _parent;
}
