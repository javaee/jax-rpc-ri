/*
 * $Id: LiteralInterfaceSerializerWriter.java,v 1.1 2006-04-12 20:35:11 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.literal.LiteralType;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralInterfaceSerializerWriter
    extends LiteralSequenceSerializerWriter
    implements GeneratorConstants {

    public LiteralInterfaceSerializerWriter(
        String basePackage,
        LiteralType type,
        Names names) {
        super(basePackage, type, names);
        serializerName =
            names.typeInterfaceSerializerClassName(
                basePackage,
                (LiteralType) type);
        serializerMemberName = names.getClassMemberName(serializerName);
    }
}
