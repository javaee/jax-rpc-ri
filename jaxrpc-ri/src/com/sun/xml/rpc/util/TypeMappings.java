/*
 * $Id: TypeMappings.java,v 1.2 2006-04-13 01:33:51 ofung Exp $
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

package com.sun.xml.rpc.util;

import java.lang.reflect.Constructor;

import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.encoding.TypeMapping;

import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.EncodingException;
import com.sun.xml.rpc.encoding.ObjectSerializerBase;
import com.sun.xml.rpc.encoding.ReferenceableSerializerImpl;
import com.sun.xml.rpc.encoding.SerializerConstants;
import com.sun.xml.rpc.encoding.SingletonDeserializerFactory;
import com.sun.xml.rpc.encoding.SingletonSerializerFactory;
import com.sun.xml.rpc.encoding.ValueTypeSerializer;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
* @author JAX-RPC Development Team
*/

public class TypeMappings implements SerializerConstants {
	public static void createMapping(
		Class serializerType,
		QName xmlType,
		Class javaType,
		TypeMapping mappings) {
		if (!ObjectSerializerBase.class.isAssignableFrom(serializerType)) {
			throw new IllegalArgumentException("serializerType must be a derivitive of com.sun.xml.rpc.encoding.ObjectSerializerBase");
		}

		try {
			Constructor serializerConstructor =
				serializerType.getConstructor(
					new Class[] {
						QName.class,
						boolean.class,
						boolean.class,
						String.class });

			CombinedSerializer serializer =
				(CombinedSerializer) serializerConstructor.newInstance(
					new Object[] {
						xmlType,
						new Boolean(ENCODE_TYPE),
						new Boolean(NULLABLE),
						SOAPConstants.URI_ENCODING });

			serializer =
				new ReferenceableSerializerImpl(SERIALIZE_AS_REF, serializer);

			SingletonSerializerFactory serializerFactory =
				new SingletonSerializerFactory(serializer);
			SingletonDeserializerFactory deserializerFactory =
				new SingletonDeserializerFactory(serializer);

			mappings.register(
				javaType,
				xmlType,
				serializerFactory,
				deserializerFactory);
		} catch (Exception e) {
			throw new EncodingException(new LocalizableExceptionAdapter(e));
		}
	}

	public static void createMapping(
		Class serializerType,
		QName xmlType,
		Class javaType,
		Service service) {
		TypeMapping mappings =
			service.getTypeMappingRegistry().getTypeMapping(
				SOAPConstants.URI_ENCODING);
		createMapping(serializerType, xmlType, javaType, mappings);
	}

	public static void createValueTypeMapping(
		QName xmlType,
		Class javaType,
		Service service) {
		CombinedSerializer serializer =
			new ValueTypeSerializer(
				xmlType,
				ENCODE_TYPE,
				NULLABLE,
				SOAPConstants.URI_ENCODING,
				javaType);
		serializer =
			new ReferenceableSerializerImpl(SERIALIZE_AS_REF, serializer);

		SingletonSerializerFactory serializerFactory =
			new SingletonSerializerFactory(serializer);
		SingletonDeserializerFactory deserializerFactory =
			new SingletonDeserializerFactory(serializer);

		TypeMapping mappings =
			service.getTypeMappingRegistry().getTypeMapping(
				SOAPConstants.URI_ENCODING);
		mappings.register(
			javaType,
			xmlType,
			serializerFactory,
			deserializerFactory);
	}
}
