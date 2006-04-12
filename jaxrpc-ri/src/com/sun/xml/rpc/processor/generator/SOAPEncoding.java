/*
 * $Id: SOAPEncoding.java,v 1.1 2006-04-12 20:33:42 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator;

import java.io.IOException;
import java.util.Set;

import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPEncoding implements GeneratorConstants {

    public static void writeStaticSerializer(
        IndentingWriter p,
        String portPackage,
        SOAPType type,
        Set processedTypes,
        SerializerWriterFactory writerFactory,
        Names names)
        throws IOException {

        String qnameMember;
        if (processedTypes
            .contains(
                type.getName() + ";" + type.getJavaType().getRealName())) {
            return;
        }
        processedTypes.add(
            type.getName() + ";" + type.getJavaType().getRealName());
        qnameMember = names.getTypeQName(type.getName());
        if (!processedTypes.contains(type.getName() + "TYPE_QNAME")) {
            GeneratorUtil.writeQNameTypeDeclaration(p, type.getName(), names);
            processedTypes.add(type.getName() + "TYPE_QNAME");
        }
        SerializerWriter writer = writerFactory.createWriter(portPackage, type);
        writer.declareSerializer(p, false, false);
    }
}
