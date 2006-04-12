/*
 * $Id: SOAPUse.java,v 1.1 2006-04-12 20:34:18 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.soap;

/**
 * A SOAP "use" enumeration. 
 *
 * @author JAX-RPC Development Team
 */
public final class SOAPUse {

    public static final SOAPUse LITERAL = new SOAPUse();
    public static final SOAPUse ENCODED = new SOAPUse();

    private SOAPUse() {
    }
}
