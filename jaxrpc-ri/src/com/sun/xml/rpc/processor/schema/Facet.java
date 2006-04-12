/*
 * $Id: Facet.java,v 1.1 2006-04-12 20:35:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class Facet {
    
    public Facet(QName name) {
        this.name = name;
    }
    
    private QName name;
}
