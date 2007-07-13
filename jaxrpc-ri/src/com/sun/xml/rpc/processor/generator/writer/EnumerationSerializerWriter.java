/*
 * $Id: EnumerationSerializerWriter.java,v 1.3 2007-07-13 23:36:03 ofung Exp $
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

package com.sun.xml.rpc.processor.generator.writer;

import java.io.IOException;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.GeneratorException;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class EnumerationSerializerWriter
    extends SerializerWriterBase
    implements GeneratorConstants {
    private String serializerMemberName;
    private SOAPType dataType;

    public EnumerationSerializerWriter(
        String basePackage,
        SOAPType type,
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
        SOAPType type = (SOAPType) this.type;
        String nillable = (type.isNillable() ? NULLABLE_STR : NOT_NULLABLE_STR);
        String referenceable =
            (type.isReferenceable()
                ? REFERENCEABLE_STR
                : NOT_REFERENCEABLE_STR);
        String multiRef =
            (multiRefEncoding
                && type.isReferenceable()
                    ? SERIALIZE_AS_REF_STR
                    : DONT_SERIALIZE_AS_REF_STR);
        String encodeType =
            (encodeTypes ? ENCODE_TYPE_STR : DONT_ENCODE_TYPE_STR);
        declareType(p, typeName, type.getName(), false, false);
        String encoder = type.getJavaType().getName() + "_Encoder";
        ;
        p.plnI(
            serializerName()
                + " "
                + serName
                + " = new "
                + SIMPLE_TYPE_SERIALIZER_NAME
                + "("
                + typeName
                + ",");
        p.pln(
            encodeType
                + ", "
                + nillable
                + ", "
                + getEncodingStyleString()
                + ", "
                + encoder
                + ".getInstance());");
        p.pO();
        if (type.isReferenceable()) {
            p.pln(
                serName
                    + " = new "
                    + REFERENCEABLE_SERIALIZER_NAME
                    + "("
                    + multiRef
                    + ", "
                    + serName
                    + ", "
                    + getSOAPVersionString()
                    + ");");
        }
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
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    public String deserializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }
}
