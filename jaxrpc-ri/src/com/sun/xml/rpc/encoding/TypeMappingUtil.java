/*
 * $Id: TypeMappingUtil.java,v 1.3 2007-07-13 23:35:58 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
