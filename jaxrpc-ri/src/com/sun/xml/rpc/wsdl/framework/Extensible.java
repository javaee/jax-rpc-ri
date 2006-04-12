/*
 * $Id: Extensible.java,v 1.1 2006-04-12 20:33:00 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

import java.util.Iterator;

/**
 * An entity that can be extended.
 *
 * @author JAX-RPC Development Team
 */
public interface Extensible extends Elemental {
    public void addExtension(Extension e);
    public Iterator extensions();
}
