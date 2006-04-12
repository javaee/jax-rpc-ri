/*
 * $Id: LiteralEnumerationSerializerWriter.java,v 1.1 2006-04-12 20:35:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.GeneratorException;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralEnumerationSerializerWriter
    extends LiteralSerializerWriterBase
    implements GeneratorConstants {
    private String serializerMemberName;
    private LiteralType dataType;

    public LiteralEnumerationSerializerWriter(
        String basePackage,
        LiteralType type,
        Names names) {
        super(type, names);
        dataType = type;
        String serializerName =
            names.typeObjectSerializerClassName(basePackage, type);
        serializerMemberName = names.getClassMemberName(serializerName, type);
    }

    public void createSerializer(
        IndentingWriter p,
        StringBuffer typeName,
        String serName,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException, GeneratorException {
        LiteralType type = (LiteralType) this.type;
        declareType(p, typeName, type.getName(), false, false);
        String encoder = type.getJavaType().getName() + "_Encoder";
        ;
        p.plnI(
            serializerName()
                + " "
                + serName
                + " = new "
                + LITERAL_SIMPLE_TYPE_SERIALIZER_NAME
                + "("
                + typeName
                + ", \"\",");
        p.pln(encoder + ".getInstance());");
        p.pO();
    }

    public void declareSerializer(
        IndentingWriter p,
        boolean isStatic,
        boolean isFinal)
        throws IOException, GeneratorException {
        String modifier = getPrivateModifier(isStatic, isFinal);
        p.pln(modifier + serializerName() + " " + serializerMemberName() + ";");
    }

    public String serializerMemberName() {
        return getPrefix(dataType) + serializerMemberName;
    }

    public String deserializerMemberName() {
        return getPrefix(dataType) + serializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }
}
