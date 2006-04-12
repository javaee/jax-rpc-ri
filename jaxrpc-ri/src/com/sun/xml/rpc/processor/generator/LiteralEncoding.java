/*
 * $Id: LiteralEncoding.java,v 1.1 2006-04-12 20:33:45 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator;


import com.sun.xml.rpc.processor.generator.writer.*;
import com.sun.xml.rpc.processor.model.literal.*;
import com.sun.xml.rpc.processor.util.*;

import java.io.IOException;
import java.util.*;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralEncoding implements GeneratorConstants {

    public static void writeStaticSerializer(IndentingWriter p, String portPackage, LiteralType type, Set processedTypes, SerializerWriterFactory writerFactory, Names names) throws IOException {
        String qnameMember;
        if (processedTypes.contains(type.getName()+";"+type.getJavaType().getRealName())) {
            return;
        }
        processedTypes.add(type.getName()+";"+type.getJavaType().getRealName());

        qnameMember = names.getTypeQName(type.getName());
        if (!processedTypes.contains(type.getName()+"TYPE_QNAME")) {
            GeneratorUtil.writeQNameTypeDeclaration(p, type.getName(), names);
            processedTypes.add(type.getName()+"TYPE_QNAME");
        }

        if (type instanceof LiteralFragmentType) {
            SerializerWriter writer = writerFactory.createWriter(portPackage, type);
            String serializerClassName = writer.serializerName();
            String memberName = writer.serializerMemberName();
            p.pln("private " +serializerClassName+" "+memberName+";");
        }
        else {
            SerializerWriter writer = writerFactory.createWriter(portPackage, type);
            writer.declareSerializer(p, false, false);
        }
    }
}
