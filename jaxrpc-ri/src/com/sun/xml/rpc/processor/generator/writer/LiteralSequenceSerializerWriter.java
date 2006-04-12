/*
 * $Id: LiteralSequenceSerializerWriter.java,v 1.1 2006-04-12 20:35:10 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralSequenceSerializerWriter
    extends LiteralSerializerWriterBase
    implements GeneratorConstants {
    protected String serializerName;
    protected String serializerMemberName;
    protected LiteralType dataType;

    public LiteralSequenceSerializerWriter(
        String basePackage,
        LiteralType type,
        Names names) {
        super(type, names);
        serializerName =
            names.typeObjectSerializerClassName(
                basePackage,
                (LiteralType) type);
        serializerMemberName = names.getClassMemberName(serializerName);
        dataType = type;
    }

    public void createSerializer(
        IndentingWriter p,
        StringBuffer typeName,
        String serName,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        LiteralType type = (LiteralType) this.type;
        boolean isExtendable =
            ((LiteralStructuredType) type).getParentType() != null;
        String encodeType =
            (encodeTypes
                && isExtendable ? ENCODE_TYPE_STR : DONT_ENCODE_TYPE_STR);
        declareType(p, typeName, type.getName(), false, false);
        p.plnI(
            BASE_SERIALIZER_NAME
                + " "
                + serName
                + " = new "
                + serializerName()
                + "("
                + typeName
                + ", \"\", "
                + encodeType
                + ");");
        p.pO();
    }

    public void declareSerializer(
        IndentingWriter p,
        boolean isStatic,
        boolean isFinal)
        throws IOException {
        String modifier = getPrivateModifier(isStatic, isFinal);
        p.pln(
            modifier
                + BASE_SERIALIZER_NAME
                + " "
                + serializerMemberName()
                + ";");
    }

    public String serializerName() {
        return serializerName;
    }

    public String serializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    public String deserializerName() {
        return serializerName;
    }

    public String deserializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }
}
