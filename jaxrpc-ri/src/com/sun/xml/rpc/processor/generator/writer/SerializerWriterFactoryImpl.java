/*
 * $Id: SerializerWriterFactoryImpl.java,v 1.2 2006-04-13 01:29:20 ofung Exp $
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

/*
 * SerializerWriter.java
 *
 * Created on January 11, 2002, 12:58 PM
 */

package com.sun.xml.rpc.processor.generator.writer;

import java.util.HashMap;
import java.util.Map;

import com.sun.xml.rpc.processor.generator.GeneratorException;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralIDType;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPListType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SerializerWriterFactoryImpl implements SerializerWriterFactory {
    private Map writerMap;
    private Names names;

    public SerializerWriterFactoryImpl(Names names) {
        writerMap = new HashMap();
        this.names = names;
    }

    public SerializerWriter createWriter(
        String basePackage,
        AbstractType type) {
        if (type == null)
            return null;
        SerializerWriter writer = getTypeSerializerWriter(type);
        if (writer == null) {
            if (type instanceof SOAPAnyType) {
                writer = new DynamicSerializerWriter((SOAPType) type, names);
            } else if (type instanceof SOAPSimpleType) {
                if (CollectionSerializerWriter.handlesType(type)) {
                    writer =
                        new CollectionSerializerWriter((SOAPType) type, names);
                } else {
                    writer =
                        new SimpleTypeSerializerWriter((SOAPType) type, names);
                }
            } else if (type instanceof SOAPStructureType) {
                if (((SOAPStructureType) type).getSubtypes() != null) {
                    writer =
                        new InterfaceSerializerWriter(
                            basePackage,
                            (SOAPType) type,
                            names);
                } else {
                    writer =
                        new SOAPObjectSerializerWriter(
                            basePackage,
                            (SOAPType) type,
                            names);
                }
            } else if (type instanceof SOAPArrayType) {
                writer =
                    new ArraySerializerWriter(
                        basePackage,
                        (SOAPType) type,
                        names);
            } else if (type instanceof SOAPEnumerationType) {
                writer =
                    new EnumerationSerializerWriter(
                        basePackage,
                        (SOAPType) type,
                        names);
            } else if (type instanceof LiteralEnumerationType) {
                writer =
                    new LiteralEnumerationSerializerWriter(
                        basePackage,
                        (LiteralType) type,
                        names);
            } else if (type instanceof SOAPCustomType) {
                writer = new CustomSerializerWriter((SOAPType) type, names);
            } else if (type instanceof LiteralFragmentType) {
                writer =
                    new LiteralFragmentSerializerWriter(
                        (LiteralFragmentType) type,
                        names);
            } else if (type instanceof LiteralSimpleType) {
                writer =
                    new LiteralSimpleSerializerWriter(
                        (LiteralSimpleType) type,
                        names);
            } else if (type instanceof LiteralSequenceType) {
                if (((LiteralSequenceType) type).getSubtypes() != null) {
                    writer =
                        new LiteralInterfaceSerializerWriter(
                            basePackage,
                            (LiteralType) type,
                            names);
                } else {
                    writer =
                        new LiteralSequenceSerializerWriter(
                            basePackage,
                            (LiteralType) type,
                            names);
                }
            } else if (type instanceof LiteralAllType) {
                // we can reuse the sequence serializer writer for "all" types too!
                if (((LiteralAllType) type).getSubtypes() != null) {
                    writer =
                        new LiteralInterfaceSerializerWriter(
                            basePackage,
                            (LiteralType) type,
                            names);
                } else {
                    writer =
                        new LiteralSequenceSerializerWriter(
                            basePackage,
                            (LiteralType) type,
                            names);
                }
            } else if (type instanceof LiteralArrayType) {
                // we can reuse the sequence serializer writer for "array" types too!
                writer =
                    new LiteralSequenceSerializerWriter(
                        basePackage,
                        (LiteralArrayType) type,
                        names);
            } else if (type instanceof LiteralArrayWrapperType) {
                // we can reuse the sequence serializer writer for "array" types too!
                writer =
                    new LiteralSequenceSerializerWriter(
                        basePackage,
                        (LiteralArrayWrapperType) type,
                        names);
            } else if (type instanceof LiteralListType) {
                writer =
                    new LiteralSimpleSerializerWriter(
                        (LiteralListType) type,
                        names);
            } else if (type instanceof LiteralIDType) {
                writer =
                    new LiteralSimpleSerializerWriter(
                        (LiteralIDType) type,
                        names);
            } else if (type instanceof SOAPListType) {
                writer =
                    new SimpleTypeSerializerWriter((SOAPListType) type, names);
            }
            if (writer == null) {
                throw new GeneratorException(
                    "generator.unsupported.type.encountered",
                    new Object[] {
                        type.getName().getLocalPart(),
                        type.getName().getNamespaceURI()});
            }
            setTypeSerializerWriter(type, writer);
        }
        return writer;
    }

    private SerializerWriter getTypeSerializerWriter(AbstractType type) {
        String key = genKey(type);
        SerializerWriter writer = (SerializerWriter) writerMap.get(key);
        return writer;
    }

    private void setTypeSerializerWriter(
        AbstractType type,
        SerializerWriter writer) {
        String key = genKey(type);
        writerMap.put(key, writer);
    }

    protected static String genKey(AbstractType type) {
        String schemaType = type.getName().toString();
        String javaType = type.getJavaType().getRealName();
        String typeType;
        if (type instanceof LiteralListType)
            typeType = type.toString();
        else
            typeType = type.getClass().getName();
        return schemaType + ";" + javaType + ";" + typeType;
    }
}
