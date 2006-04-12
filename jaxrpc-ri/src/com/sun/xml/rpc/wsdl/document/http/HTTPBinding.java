/*
 * $Id: HTTPBinding.java,v 1.1 2006-04-12 20:35:14 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.http;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * A HTTP binding extension.
 *
 * @author JAX-RPC Development Team
 */
public class HTTPBinding extends Extension {

    public HTTPBinding() {
    }

    public QName getElementName() {
        return HTTPConstants.QNAME_BINDING;
    }

    public String getVerb() {
        return _verb;
    }

    public void setVerb(String s) {
        _verb = s;
    }

    public void validateThis() {
        if (_verb == null) {
            failValidation("validation.missingRequiredAttribute", "verb");
        }
    }

    private String _verb;
}
