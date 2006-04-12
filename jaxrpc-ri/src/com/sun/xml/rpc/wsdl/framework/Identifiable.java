/*
 * $Id: Identifiable.java,v 1.1 2006-04-12 20:32:57 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

/**
 * An interface implemented by entities which have an ID.
 *
 * @author JAX-RPC Development Team
 */
public interface Identifiable extends Elemental {
    public String getID();
}
