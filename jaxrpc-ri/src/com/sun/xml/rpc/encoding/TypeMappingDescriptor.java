/*
 * $Id: TypeMappingDescriptor.java,v 1.1 2006-04-12 20:33:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface TypeMappingDescriptor {
    public Class getJavaType();
    public QName getXMLType();
    public SerializerFactory getSerializer();
    public DeserializerFactory getDeserializer();
}