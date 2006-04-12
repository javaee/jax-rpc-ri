/*
 * $Id: HTTPAddress.java,v 1.1 2006-04-12 20:35:14 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.http;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * A HTTP address extension.
 *
 * @author JAX-RPC Development Team
 */
public class HTTPAddress extends Extension {

    public HTTPAddress() {
    }

    public QName getElementName() {
        return HTTPConstants.QNAME_ADDRESS;
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String s) {
        _location = s;
    }

    public void validateThis() {
        if (_location == null) {
            failValidation("validation.missingRequiredAttribute", "location");
        }
    }

    private String _location;
}
