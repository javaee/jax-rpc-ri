/*
 * $Id: MIMEPart.java,v 1.1 2006-04-12 20:35:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.mime;

import java.util.Iterator;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.EntityAction;
import com.sun.xml.rpc.wsdl.framework.ExtensibilityHelper;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * A MIME part extension.
 *
 * @author JAX-RPC Development Team
 */
public class MIMEPart extends Extension implements Extensible {

    public MIMEPart() {
        _helper = new ExtensibilityHelper();
    }

    public QName getElementName() {
        return MIMEConstants.QNAME_PART;
    }

    public String getName() {
        return _name;
    }

    public void setName(String s) {
        _name = s;
    }

    public void addExtension(Extension e) {
        _helper.addExtension(e);
    }

    public Iterator extensions() {
        return _helper.extensions();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        _helper.withAllSubEntitiesDo(action);
    }

    public void validateThis() {
    }

    private String _name;
    private ExtensibilityHelper _helper;
}
