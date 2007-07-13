/*
 * $Id: CustomSerializerWriter.java,v 1.3 2007-07-13 23:36:03 ofung Exp $
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

import javax.naming.OperationNotSupportedException;

import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.processor.config.TypeMappingInfo;
import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.GeneratorException;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class CustomSerializerWriter
    extends SerializerWriterBase
    implements GeneratorConstants {
    private String serializerName;
    private String serializerMemberName;
    private String deserializerName;
    private String deserializerMemberName;
    private SOAPType dataType;

    public CustomSerializerWriter(SOAPType type, Names names) {
        super(type, names);
        dataType = type;
        serializerName = names.getTypeQName(type.getName()) + "_Serializer";
        serializerMemberName = names.getClassMemberName(serializerName);
        deserializerName = names.getTypeQName(type.getName()) + "_Deserializer";
        deserializerMemberName = names.getClassMemberName(deserializerName);
    }

    public void createSerializer(
        IndentingWriter p,
        StringBuffer typeName,
        String varName,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        throw new GeneratorException(
            "generator.nestedGeneratorError",
            new LocalizableExceptionAdapter(
                new OperationNotSupportedException()));
    }

    public void registerSerializer(
        IndentingWriter p,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        TypeMappingInfo mappingInfo =
            ((JavaCustomType) type.getJavaType()).getTypeMappingInfo();
        if ((soapVer.equals(SOAPVersion.SOAP_11)
            && !mappingInfo.getEncodingStyle().equals(
                SOAPConstants.NS_SOAP_ENCODING))
            || (soapVer.equals(SOAPVersion.SOAP_12)
                && !mappingInfo.getEncodingStyle().equals(
                    SOAP12Constants.NS_SOAP_ENCODING))) {
            throw new GeneratorException(
                "generator.unsupported.encoding.encountered",
                mappingInfo.getEncodingStyle().toString());
        }
        String serFac = mappingInfo.getSerializerFactoryName();
        String deserFac = mappingInfo.getDeserializerFactoryName();
        StringBuffer typeName = new StringBuffer("type");
        declareType(p, typeName, type.getName(), false, false);
        p.pln(
            typeMapping
                + ".register("
                + type.getJavaType().getName()
                + ".class, "
                + typeName.toString()
                + ", "
                + "new "
                + serFac
                + "(), "
                + "new "
                + deserFac
                + "());");
    }

    public void declareSerializer(
        IndentingWriter p,
        boolean isStatic,
        boolean isFinal)
        throws IOException {
        String modifier = getPrivateModifier(isStatic, isFinal);
        p.pln("private JAXRPCSerializer " + serializerMemberName() + ";");
        p.pln("private JAXRPCDeserializer " + deserializerMemberName() + ";");
    }

    public void initializeSerializer(
        IndentingWriter p,
        String typeName,
        String registry)
        throws IOException {
        p.pln(
            serializerMemberName()
                + " = (JAXRPCSerializer)registry.getSerializer("
                + getEncodingStyleString()
                + ", "
                + type.getJavaType().getName()
                + ".class, "
                + typeName
                + ");");
        p.pln(
            deserializerMemberName()
                + " = (JAXRPCDeserializer)registry.getDeserializer("
                + getEncodingStyleString()
                + ", "
                + type.getJavaType().getName()
                + ".class, "
                + typeName
                + ");");
    }

    public String serializerName() {
        return serializerName;
    }

    public String serializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + serializerMemberName;
    }

    public String deserializerName() {
        return deserializerName;
    }

    public String deserializerMemberName() {
        return getPrefix(dataType) + UNDERSCORE + deserializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }

    public AbstractType getElementType() {
        SOAPType elemType = ((SOAPArrayType) type).getElementType();
        while (elemType instanceof SOAPArrayType) {
            elemType = ((SOAPArrayType) elemType).getElementType();
        }
        return elemType;
    }
}
