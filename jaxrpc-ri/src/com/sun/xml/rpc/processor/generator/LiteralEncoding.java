/*
 * $Id: LiteralEncoding.java,v 1.2 2006-04-13 01:28:47 ofung Exp $
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
