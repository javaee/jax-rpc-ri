/*
 * $Id: MIMEXml.java,v 1.1 2006-04-12 20:35:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.mime;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * A MIME mimeXml extension.
 *
 * @author JAX-RPC Development Team
 */
public class MIMEXml extends Extension {

    public MIMEXml() {
    }

    public QName getElementName() {
        return MIMEConstants.QNAME_MIME_XML;
    }

    public String getPart() {
        return _part;
    }

    public void setPart(String s) {
        _part = s;
    }

    public void validateThis() {
    }

    private String _part;
}
