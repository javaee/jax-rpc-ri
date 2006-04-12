/*
 * $Id: Constants.java,v 1.1 2006-04-12 20:34:34 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.model.exporter;

import javax.xml.namespace.QName;


/**
 * @author JAX-RPC Development Team
 */
public interface Constants {
    public static final String NS_MODEL =
        "http://java.sun.com/xml/ns/jax-rpc/ri/model";
    public static final QName QNAME_MODEL= new QName(NS_MODEL, "model");
}

