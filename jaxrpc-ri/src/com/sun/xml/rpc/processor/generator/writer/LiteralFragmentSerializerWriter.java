/*
 * $Id: LiteralFragmentSerializerWriter.java,v 1.1 2006-04-12 20:35:09 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralFragmentSerializerWriter
    extends LiteralSerializerWriterBase
    implements GeneratorConstants {
    private String serializerMemberName;
    private LiteralType dataType;

    public LiteralFragmentSerializerWriter(
        LiteralFragmentType type,
        Names names) {
        super(type, names);
        dataType = type;
        String serializerName = LITERAL_FRAGMENT_SERIALIZER_NAME;
        serializerMemberName =
            names.getLiteralFragmentTypeSerializerMemberName(type);
    }

    public void createSerializer(
        IndentingWriter p,
        StringBuffer typeName,
        String serName,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        LiteralFragmentType type = (LiteralFragmentType) this.type;
        String nillable = (type.isNillable() ? NULLABLE_STR : NOT_NULLABLE_STR);
        declareType(p, typeName, type.getName(), false, false);
        p.pln(
            serializerName()
                + " "
                + serName
                + " = new "
                + LITERAL_FRAGMENT_SERIALIZER_NAME
                + "("
                + typeName
                + ", "
                + nillable
                + ", \"\");");
    }

    public void declareSerializer(
        IndentingWriter p,
        boolean isStatic,
        boolean isFinal)
        throws IOException {
        String modifier = getPrivateModifier(isStatic, isFinal);
        p.pln(modifier + serializerName() + " " + serializerMemberName() + ";");
    }

    public String serializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    public String deserializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }
}
