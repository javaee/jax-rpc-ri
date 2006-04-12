/*
 * $Id: HTTPUrlEncoded.java,v 1.1 2006-04-12 20:35:14 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.http;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * A HTTP urlEncoded extension.
 *
 * @author JAX-RPC Development Team
 */
public class HTTPUrlEncoded extends Extension {

    public HTTPUrlEncoded() {}

    public QName getElementName() {
        return HTTPConstants.QNAME_URL_ENCODED;
    }

    public void validateThis() {
    }
}
