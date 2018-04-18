/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: SerializerWriterBase.java,v 1.3 2007-07-13 23:36:03 ofung Exp $
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

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.GeneratorUtil;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAP12Constants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SerializerWriterBase
    implements SerializerWriter, GeneratorConstants {
    AbstractType type;
    Names names;
    SOAPVersion soapVer = SOAPVersion.SOAP_11;

    public SerializerWriterBase(AbstractType type, Names names) {

        if (type.isSOAPType()) {
            String ver = type.getVersion();
            if (ver.equals(SOAPVersion.SOAP_11.toString()))
                soapVer = SOAPVersion.SOAP_11;
            else if (ver.equals(SOAPVersion.SOAP_12.toString()))
                soapVer = SOAPVersion.SOAP_12;
        }
        this.type = type;
        this.names = names;
    }

    public void registerSerializer(
        IndentingWriter p,
        boolean encodeTypes,
        boolean multiRefEncoding,
        String typeMapping)
        throws IOException {
        StringBuffer typeName = new StringBuffer(40);
        typeName.append("type");
        createSerializer(
            p,
            typeName,
            "serializer",
            encodeTypes,
            multiRefEncoding,
            typeMapping);
        writeRegisterFactories(
            p,
            typeName.toString(),
            "serializer",
            typeMapping);
    }

    public void initializeSerializer(
        IndentingWriter p,
        String typeName,
        String registry)
        throws IOException {
        //bug 4845163 fix
        String javaType = null;
        if (type.getName().equals(SchemaConstants.QNAME_TYPE_IDREF)) {
            javaType = STRING_CLASSNAME;
        } else {
            javaType = type.getJavaType().getName();
        }
        p.pln(
            serializerMemberName()
                + " = (CombinedSerializer)registry.getSerializer("
                + getEncodingStyleString()
                + ", "
                + javaType
                + ".class, "
                + typeName
                + ");");
    }

    public String serializerName() {
        return GeneratorConstants.BASE_SERIALIZER_NAME;
    }

    public String deserializerName() {
        return serializerName();
    }

    public static boolean handlesType(AbstractType type) {
        return false;
    }

    protected String getEncodingStyleString() {
        if (soapVer.equals(SOAPVersion.SOAP_12))
            return SOAP12CONSTANTS_NS_SOAP_ENCODING;
        else if (soapVer.equals(SOAPVersion.SOAP_11))
            return SOAPCONSTANTS_NS_SOAP_ENCODING;
        return null;
    }

    protected String getEncodingStyle() {
        if (soapVer.equals(SOAPVersion.SOAP_12))
            return SOAP12Constants.NS_SOAP_ENCODING;
        else if (soapVer.equals(SOAPVersion.SOAP_11))
            return SOAPConstants.NS_SOAP_ENCODING;
        return null;
    }

    protected String getSOAPVersionString() {
        if (soapVer.equals(SOAPVersion.SOAP_12))
            return SOAP_VERSION_12;
        else if (soapVer.equals(SOAPVersion.SOAP_11))
            return SOAP_VERSION_11;
        return null;
    }

    protected void declareType(
        IndentingWriter p,
        StringBuffer member,
        QName type,
        boolean isStatic,
        boolean isFinal)
        throws IOException {

        String qnameConstant = GeneratorUtil.getQNameConstant(type);
        if (qnameConstant != null) {
            member.delete(0, member.length());
            member.append(qnameConstant);
        } else {
            String modifier = getModifier(isStatic, isFinal);
            p.p(modifier + "QName " + member + " = ");
            GeneratorUtil.writeNewQName(p, type);
            p.pln(";");
        }
    }

    protected void writeRegisterFactories(
        IndentingWriter p,
        String typeName,
        String memberName,
        String mapping)
        throws IOException {
        p.pln(
            "registerSerializer("
                + mapping
                + ","
                + type.getJavaType().getName()
                + ".class, "
                + typeName
                + ", "
                + memberName
                + ");");
    }

    protected String getModifier(boolean isStatic, boolean isFinal) {
        String modifier = "";
        if (isStatic) {
            modifier += "static ";
        }
        if (isFinal) {
            modifier += "final ";
        }
        return modifier;
    }

    protected String getPrefix(AbstractType type) {
        QName typeName = type.getName();
        String prefix = names.getPrefix(typeName);
        return (prefix);
    }
}
