/*
 * $Id: LiteralFragmentSerializerWriter.java,v 1.2 2006-04-13 01:29:14 ofung Exp $
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
