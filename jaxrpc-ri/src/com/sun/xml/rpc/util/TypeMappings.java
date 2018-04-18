/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

/*
 * $Id: TypeMappings.java,v 1.3 2007-07-13 23:36:38 ofung Exp $
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
