/*
 * $Id: Elemental.java,v 1.1 2006-04-12 20:32:58 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.namespace.QName;

/**
 * Interface implemented by classes that are mappable to XML elements.
 *
 * @author JAX-RPC Development Team
 */
public interface Elemental {
    public QName getElementName();
}
