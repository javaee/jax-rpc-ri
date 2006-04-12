/*
 * $Id: HTTPUrlReplacement.java,v 1.1 2006-04-12 20:35:14 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.http;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * A HTTP urlReplacement extension.
 *
 * @author JAX-RPC Development Team
 */
public class HTTPUrlReplacement extends Extension {

    public HTTPUrlReplacement() {
    }

    public QName getElementName() {
        return HTTPConstants.QNAME_URL_REPLACEMENT;
    }

    public void validateThis() {
    }
}
