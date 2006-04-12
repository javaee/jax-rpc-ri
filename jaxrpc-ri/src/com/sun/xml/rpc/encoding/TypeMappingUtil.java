/*
 * $Id: TypeMappingUtil.java,v 1.1 2006-04-12 20:33:13 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;

/**
 *
 * @author JAX-RPC Development Team
 */
public class TypeMappingUtil {

    public static TypeMapping getTypeMapping(
        TypeMappingRegistry registry,
        String encodingStyle)
        throws Exception {

        TypeMapping mapping = registry.getTypeMapping(encodingStyle);

        if (mapping == null) {
            throw new TypeMappingException(
                "typemapping.noMappingForEncoding",
                encodingStyle);
        }

        return mapping;
    }

    public static Serializer getSerializer(
        TypeMapping mapping,
        Class javaType,
        QName xmlType)
        throws Exception {

        SerializerFactory sf = mapping.getSerializer(javaType, xmlType);

        if (sf == null) {
            throw new TypeMappingException(
                "typemapping.serializerNotRegistered",
                new Object[] { javaType, xmlType });
        }

        return sf.getSerializerAs(EncodingConstants.JAX_RPC_RI_MECHANISM);
    }

    public static Deserializer getDeserializer(
        TypeMapping mapping,
        Class javaType,
        QName xmlType)
        throws Exception {

        DeserializerFactory df = mapping.getDeserializer(javaType, xmlType);

        if (df == null) {
            throw new TypeMappingException(
                "typemapping.deserializerNotRegistered",
                new Object[] { javaType, xmlType });
        }

        return df.getDeserializerAs(EncodingConstants.JAX_RPC_RI_MECHANISM);
    }

}
