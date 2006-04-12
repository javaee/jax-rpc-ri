/*
 * $Id: InternalTypeMappingRegistry.java,v 1.1 2006-04-12 20:33:10 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.Serializer;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface InternalTypeMappingRegistry {
    public Serializer getSerializer(
        String encoding,
        Class javaType,
        QName xmlType)
        throws Exception;
        
    public Serializer getSerializer(String encoding, Class javaType)
        throws Exception;
        
    public Serializer getSerializer(String encoding, QName xmlType)
        throws Exception;
        
    public Deserializer getDeserializer(
        String encoding,
        Class javaType,
        QName xmlType)
        throws Exception;
        
    public Deserializer getDeserializer(String encoding, QName xmlType)
        throws Exception;
        
    public Class getJavaType(String encoding, QName xmlType) throws Exception;
    public QName getXmlType(String encoding, Class javaType) throws Exception;
}