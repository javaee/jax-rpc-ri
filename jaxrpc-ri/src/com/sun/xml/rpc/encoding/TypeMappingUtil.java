/*
 * $Id: TypeMappingUtil.java,v 1.2 2006-04-13 01:27:30 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
