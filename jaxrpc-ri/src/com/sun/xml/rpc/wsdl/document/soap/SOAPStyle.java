/*
 * $Id: SOAPStyle.java,v 1.1 2006-04-12 20:34:20 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.soap;

/**
 * A SOAP "style" enumeration. 
 *
 * @author JAX-RPC Development Team
 */
public final class SOAPStyle {

    public static final SOAPStyle RPC = new SOAPStyle();
    public static final SOAPStyle DOCUMENT = new SOAPStyle();

    private SOAPStyle() {
    }
}
