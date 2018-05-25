/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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
