/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.rpc.processor.generator;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.modeler.rmi.RmiUtils;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class GeneratorUtil {

    private static QName QNAME_SOAP_FAULT = null;
    private final static String MUST_UNDERSTAND_FAULT_MESSAGE_STRING =
        "SOAP must understand error";

    private static com
        .sun
        .xml
        .rpc
        .soap
        .SOAPNamespaceConstants soapNamespaceConstants =
        null;
    private static com.sun.xml.rpc.soap.SOAPWSDLConstants soapWSDLConstants =
        null;
    private static com
        .sun
        .xml
        .rpc
        .soap
        .SOAPEncodingConstants soapEncodingConstants =
        null;

    protected GeneratorUtil() {
        /** Default to SOAP 1.1 */
        init(SOAPVersion.SOAP_11);
    }

    protected GeneratorUtil(SOAPVersion ver) {
        init(ver);
    }

    private void init(SOAPVersion ver) {
        soapNamespaceConstants =
            SOAPConstantsFactory.getSOAPNamespaceConstants(ver);
        soapWSDLConstants = SOAPConstantsFactory.getSOAPWSDLConstants(ver);
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
        //QNAME_SOAP_FAULT = new QName(soapNamespaceConstants.getEnvelope(), "Fault");
    }

    public static String getQNameConstant(QName name) {
        return (String) typeMap.get(name);
    }

    public static void writeNewQName(IndentingWriter p, QName name)
        throws IOException {
        String qnameConstant = getQNameConstant(name);
        if (qnameConstant != null) {
            p.p(qnameConstant);
        } else {
            p.p(
                "new QName(\""
                    + name.getNamespaceURI()
                    + "\", \""
                    + name.getLocalPart()
                    + "\")");
        }
    }

    public static void writeBlockQNameDeclaration(
        IndentingWriter p,
        Operation operation,
        Block block,
        Names names)
        throws IOException {
        String qname = names.getBlockQNameName(operation, block);
        p.p("private static final javax.xml.namespace.QName ");
        p.p(qname + " = ");
        writeNewQName(p, block.getName());
        p.pln(";");
    }

    public static void writeQNameDeclaration(
        IndentingWriter p,
        QName name,
        Names names)
        throws IOException {
        String qname = names.getQNameName(name);
        p.p("private static final javax.xml.namespace.QName ");
        p.p(qname + " = ");
        writeNewQName(p, name);
        p.pln(";");
    }

    public static void writeQNameTypeDeclaration(
        IndentingWriter p,
        QName name,
        Names names)
        throws IOException {
        String qname = names.getTypeQName(name);
        p.p("private static final javax.xml.namespace.QName ");
        p.p(qname + " = ");
        writeNewQName(p, name);
        p.pln(";");
    }

    public static boolean classExists(
        ProcessorEnvironment env,
        String className) {
        try {
            // Takes care of inner classes.
            String name = RmiUtils.getLoadableClassName(className,
                env.getClassLoader());
            return true;
        } catch(ClassNotFoundException ce) {
        }
        return false;
    }

    public static Hashtable ht = null;
    static {
        /* the primitive types have been preloaded */
        ht = new Hashtable();
        ht.put("int", "Integer.TYPE");
        ht.put("boolean", "Boolean.TYPE");
        ht.put("char", "Character.TYPE");
        ht.put("byte", "Byte.TYPE");
        ht.put("short", "Short.TYPE");
        ht.put("long", "Long.TYPE");
        ht.put("float", "Float.TYPE");
        ht.put("double", "Double.TYPE");
        ht.put("void", "Void.TYPE");

        /* here we load the condition for the primitive Array type */
        ht.put("int[]", "I");
        ht.put("boolean[]", "Z");
        ht.put("char[]", "C");
        ht.put("byte[]", "B");
        ht.put("short[]", "S");
        ht.put("long[]", "J");
        ht.put("float[]", "F");
        ht.put("double[]", "D");

    }

    private static Map typeMap;
    static {
        typeMap = new HashMap();
        // Schema
        typeMap.put(
            SchemaConstants.QNAME_TYPE_STRING,
            "SchemaConstants.QNAME_TYPE_STRING");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NORMALIZED_STRING,
            "SchemaConstants.QNAME_TYPE_NORMALIZED_STRING");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_TOKEN,
            "SchemaConstants.QNAME_TYPE_TOKEN");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_BYTE,
            "SchemaConstants.QNAME_TYPE_BYTE");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE,
            "SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_BASE64_BINARY,
            "SchemaConstants.QNAME_TYPE_BASE64_BINARY");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_HEX_BINARY,
            "SchemaConstants.QNAME_TYPE_HEX_BINARY");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_INTEGER,
            "SchemaConstants.QNAME_TYPE_INTEGER");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_POSITIVE_INTEGER,
            "SchemaConstants.QNAME_TYPE_POSITIVE_INTEGER");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NEGATIVE_INTEGER,
            "SchemaConstants.QNAME_TYPE_NEGATIVE_INTEGER");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER,
            "SchemaConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NON_POSITIVE_INTEGER,
            "SchemaConstants.QNAME_TYPE_NON_POSITIVE_INTEGER");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_INT,
            "SchemaConstants.QNAME_TYPE_INT");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_UNSIGNED_INT,
            "SchemaConstants.QNAME_TYPE_UNSIGNED_INT");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_LONG,
            "SchemaConstants.QNAME_TYPE_LONG");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_UNSIGNED_LONG,
            "SchemaConstants.QNAME_TYPE_UNSIGNED_LONG");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_SHORT,
            "SchemaConstants.QNAME_TYPE_SHORT");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT,
            "SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_DECIMAL,
            "SchemaConstants.QNAME_TYPE_DECIMAL");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_FLOAT,
            "SchemaConstants.QNAME_TYPE_FLOAT");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_DOUBLE,
            "SchemaConstants.QNAME_TYPE_DOUBLE");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_BOOLEAN,
            "SchemaConstants.QNAME_TYPE_BOOLEAN");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_TIME,
            "SchemaConstants.QNAME_TYPE_TIME");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_DATE_TIME,
            "SchemaConstants.QNAME_TYPE_DATE_TIME");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_DURATION,
            "SchemaConstants.QNAME_TYPE_DURATION");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_DATE,
            "SchemaConstants.QNAME_TYPE_DATE");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_G_MONTH,
            "SchemaConstants.QNAME_TYPE_G_MONTH");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_G_YEAR,
            "SchemaConstants.QNAME_TYPE_G_YEAR");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_G_YEAR_MONTH,
            "SchemaConstants.QNAME_TYPE_G_YEAR_MONTH");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_G_DAY,
            "SchemaConstants.QNAME_TYPE_G_DAY");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_G_MONTH_DAY,
            "SchemaConstants.QNAME_TYPE_G_MONTH_DAY");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NAME,
            "SchemaConstants.QNAME_TYPE_NAME");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_QNAME,
            "SchemaConstants.QNAME_TYPE_QNAME");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NCNAME,
            "SchemaConstants.QNAME_TYPE_NCNAME");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_ANY_URI,
            "SchemaConstants.QNAME_TYPE_ANY_URI");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_ID,
            "SchemaConstants.QNAME_TYPE_ID");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_IDREF,
            "SchemaConstants.QNAME_TYPE_IDREF");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_IDREFS,
            "SchemaConstants.QNAME_TYPE_IDREFS");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_ENTITY,
            "SchemaConstants.QNAME_TYPE_ENTITY");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_ENTITIES,
            "SchemaConstants.QNAME_TYPE_ENTITIES");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NOTATION,
            "SchemaConstants.QNAME_TYPE_NOTATION");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NMTOKEN,
            "SchemaConstants.QNAME_TYPE_NMTOKEN");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_NMTOKENS,
            "SchemaConstants.QNAME_TYPE_NMTOKENS");
        //xsd:list
        typeMap.put(SchemaConstants.QNAME_LIST, "SchemaConstants.QNAME_LIST");

        // QNames for special Schemactypes
        typeMap.put(
            SchemaConstants.QNAME_TYPE_URTYPE,
            "SchemaConstants.QNAME_TYPE_URTYPE");
        typeMap.put(
            SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE,
            "SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE");
        // SOAP
        typeMap.put(
            SOAPConstants.QNAME_TYPE_STRING,
            "SOAPConstants.QNAME_TYPE_STRING");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NORMALIZED_STRING,
            "SOAPConstants.QNAME_TYPE_NORMALIZED_STRING");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_TOKEN,
            "SOAPConstants.QNAME_TYPE_TOKEN");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_BYTE,
            "SOAPConstants.QNAME_TYPE_BYTE");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_UNSIGNED_BYTE,
            "SOAPConstants.QNAME_TYPE_UNSIGNED_BYTE");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_BASE64_BINARY,
            "SOAPConstants.QNAME_TYPE_BASE64_BINARY");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_BASE64,
            "SOAPConstants.QNAME_TYPE_BASE64");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_HEX_BINARY,
            "SOAPConstants.QNAME_TYPE_HEX_BINARY");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_INTEGER,
            "SOAPConstants.QNAME_TYPE_INTEGER");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_POSITIVE_INTEGER,
            "SOAPConstants.QNAME_TYPE_POSITIVE_INTEGER");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NEGATIVE_INTEGER,
            "SOAPConstants.QNAME_TYPE_NEGATIVE_INTEGER");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER,
            "SOAPConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NON_POSITIVE_INTEGER,
            "SOAPConstants.QNAME_TYPE_NON_POSITIVE_INTEGER");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_INT,
            "SOAPConstants.QNAME_TYPE_INT");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_UNSIGNED_INT,
            "SOAPConstants.QNAME_TYPE_UNSIGNED_INT");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_LONG,
            "SOAPConstants.QNAME_TYPE_LONG");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_UNSIGNED_LONG,
            "SOAPConstants.QNAME_TYPE_UNSIGNED_LONG");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_SHORT,
            "SOAPConstants.QNAME_TYPE_SHORT");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_UNSIGNED_SHORT,
            "SOAPConstants.QNAME_TYPE_UNSIGNED_SHORT");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_DECIMAL,
            "SOAPConstants.QNAME_TYPE_DECIMAL");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_FLOAT,
            "SOAPConstants.QNAME_TYPE_FLOAT");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_DOUBLE,
            "SOAPConstants.QNAME_TYPE_DOUBLE");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_BOOLEAN,
            "SOAPConstants.QNAME_TYPE_BOOLEAN");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_TIME,
            "SOAPConstants.QNAME_TYPE_TIME");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_DATE_TIME,
            "SOAPConstants.QNAME_TYPE_DATE_TIME");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_DURATION,
            "SOAPConstants.QNAME_TYPE_DURATION");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_DATE,
            "SOAPConstants.QNAME_TYPE_DATE");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_G_MONTH,
            "SOAPConstants.QNAME_TYPE_G_MONTH");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_G_YEAR,
            "SOAPConstants.QNAME_TYPE_G_YEAR");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_G_YEAR_MONTH,
            "SOAPConstants.QNAME_TYPE_G_YEAR_MONTH");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_G_DAY,
            "SOAPConstants.QNAME_TYPE_G_DAY");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_G_MONTH_DAY,
            "SOAPConstants.QNAME_TYPE_G_MONTH_DAY");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NAME,
            "SOAPConstants.QNAME_TYPE_NAME");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_QNAME,
            "SOAPConstants.QNAME_TYPE_QNAME");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NCNAME,
            "SOAPConstants.QNAME_TYPE_NCNAME");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_ANY_URI,
            "SOAPConstants.QNAME_TYPE_ANY_URI");
        typeMap.put(SOAPConstants.QNAME_TYPE_ID, "SOAPConstants.QNAME_TYPE_ID");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_IDREF,
            "SOAPConstants.QNAME_TYPE_IDREF");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_IDREFS,
            "SOAPConstants.QNAME_TYPE_IDREFS");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_ENTITY,
            "SOAPConstants.QNAME_TYPE_ENTITY");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_ENTITIES,
            "SOAPConstants.QNAME_TYPE_ENTITIES");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NOTATION,
            "SOAPConstants.QNAME_TYPE_NOTATION");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NMTOKEN,
            "SOAPConstants.QNAME_TYPE_NMTOKEN");
        typeMap.put(
            SOAPConstants.QNAME_TYPE_NMTOKENS,
            "SOAPConstants.QNAME_TYPE_NMTOKENS");
        typeMap.put(
            SOAPConstants.QNAME_MUSTUNDERSTAND,
            "SOAPConstants.QNAME_MUSTUNDERSTAND");

        // SOAP 12
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_STRING,
            "SOAP12Constants.QNAME_TYPE_STRING");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NORMALIZED_STRING,
            "SOAP12Constants.QNAME_TYPE_NORMALIZED_STRING");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_TOKEN,
            "SOAP12Constants.QNAME_TYPE_TOKEN");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_BYTE,
            "SOAP12Constants.QNAME_TYPE_BYTE");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_UNSIGNED_BYTE,
            "SOAP12Constants.QNAME_TYPE_UNSIGNED_BYTE");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_BASE64_BINARY,
            "SOAP12Constants.QNAME_TYPE_BASE64_BINARY");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_BASE64,
            "SOAP12Constants.QNAME_TYPE_BASE64");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_HEX_BINARY,
            "SOAP12Constants.QNAME_TYPE_HEX_BINARY");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_INTEGER,
            "SOAP12Constants.QNAME_TYPE_INTEGER");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_POSITIVE_INTEGER,
            "SOAP12Constants.QNAME_TYPE_POSITIVE_INTEGER");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NEGATIVE_INTEGER,
            "SOAP12Constants.QNAME_TYPE_NEGATIVE_INTEGER");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NON_NEGATIVE_INTEGER,
            "SOAP12Constants.QNAME_TYPE_NON_NEGATIVE_INTEGER");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NON_POSITIVE_INTEGER,
            "SOAP12Constants.QNAME_TYPE_NON_POSITIVE_INTEGER");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_INT,
            "SOAP12Constants.QNAME_TYPE_INT");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_UNSIGNED_INT,
            "SOAP12Constants.QNAME_TYPE_UNSIGNED_INT");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_LONG,
            "SOAP12Constants.QNAME_TYPE_LONG");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_UNSIGNED_LONG,
            "SOAP12Constants.QNAME_TYPE_UNSIGNED_LONG");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_SHORT,
            "SOAP12Constants.QNAME_TYPE_SHORT");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_UNSIGNED_SHORT,
            "SOAP12Constants.QNAME_TYPE_UNSIGNED_SHORT");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_DECIMAL,
            "SOAP12Constants.QNAME_TYPE_DECIMAL");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_FLOAT,
            "SOAP12Constants.QNAME_TYPE_FLOAT");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_DOUBLE,
            "SOAP12Constants.QNAME_TYPE_DOUBLE");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_BOOLEAN,
            "SOAP12Constants.QNAME_TYPE_BOOLEAN");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_TIME,
            "SOAP12Constants.QNAME_TYPE_TIME");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_DATE_TIME,
            "SOAP12Constants.QNAME_TYPE_DATE_TIME");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_DURATION,
            "SOAP12Constants.QNAME_TYPE_DURATION");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_DATE,
            "SOAP12Constants.QNAME_TYPE_DATE");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_G_MONTH,
            "SOAP12Constants.QNAME_TYPE_G_MONTH");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_G_YEAR,
            "SOAP12Constants.QNAME_TYPE_G_YEAR");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_G_YEAR_MONTH,
            "SOAP12Constants.QNAME_TYPE_G_YEAR_MONTH");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_G_DAY,
            "SOAP12Constants.QNAME_TYPE_G_DAY");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_G_MONTH_DAY,
            "SOAP12Constants.QNAME_TYPE_G_MONTH_DAY");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NAME,
            "SOAP12Constants.QNAME_TYPE_NAME");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_QNAME,
            "SOAP12Constants.QNAME_TYPE_QNAME");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NCNAME,
            "SOAP12Constants.QNAME_TYPE_NCNAME");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_ANY_URI,
            "SOAP12Constants.QNAME_TYPE_ANY_URI");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_ID,
            "SOAP12Constants.QNAME_TYPE_ID");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_IDREF,
            "SOAP12Constants.QNAME_TYPE_IDREF");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_IDREFS,
            "SOAP12Constants.QNAME_TYPE_IDREFS");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_ENTITY,
            "SOAP12Constants.QNAME_TYPE_ENTITY");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_ENTITIES,
            "SOAP12Constants.QNAME_TYPE_ENTITIES");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NOTATION,
            "SOAP12Constants.QNAME_TYPE_NOTATION");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NMTOKEN,
            "SOAP12Constants.QNAME_TYPE_NMTOKEN");
        typeMap.put(
            SOAP12Constants.QNAME_TYPE_NMTOKENS,
            "SOAP12Constants.QNAME_TYPE_NMTOKENS");
        typeMap.put(
            SOAP12Constants.QNAME_MUSTUNDERSTAND,
            "SOAP12Constants.QNAME_MUSTUNDERSTAND");

        // Internal Constants
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_IMAGE,
            "InternalEncodingConstants.QNAME_TYPE_IMAGE");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_MIME_MULTIPART,
            "InternalEncodingConstants.QNAME_TYPE_MIME_MULTIPART");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_SOURCE,
            "InternalEncodingConstants.QNAME_TYPE_SOURCE");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_DATA_HANDLER,
            "InternalEncodingConstants.QNAME_TYPE_DATA_HANDLER");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_COLLECTION,
            "InternalEncodingConstants.QNAME_TYPE_COLLECTION");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_LIST,
            "InternalEncodingConstants.QNAME_TYPE_LIST");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_SET,
            "InternalEncodingConstants.QNAME_TYPE_SET");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_ARRAY_LIST,
            "InternalEncodingConstants.QNAME_TYPE_ARRAY_LIST");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_VECTOR,
            "InternalEncodingConstants.QNAME_TYPE_VECTOR");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_STACK,
            "InternalEncodingConstants.QNAME_TYPE_STACK");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_LINKED_LIST,
            "InternalEncodingConstants.QNAME_TYPE_LINKED_LIST");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_HASH_SET,
            "InternalEncodingConstants.QNAME_TYPE_HASH_SET");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_TREE_SET,
            "InternalEncodingConstants.QNAME_TYPE_TREE_SET");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_MAP,
            "InternalEncodingConstants.QNAME_TYPE_MAP");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_JAX_RPC_MAP_ENTRY,
            "InternalEncodingConstants.QNAME_TYPE_JAX_RPC_MAP_ENTRY");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_HASH_MAP,
            "InternalEncodingConstants.QNAME_TYPE_HASH_MAP");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_TREE_MAP,
            "InternalEncodingConstants.QNAME_TYPE_TREE_MAP");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_HASHTABLE,
            "InternalEncodingConstants.QNAME_TYPE_HASHTABLE");
        typeMap.put(
            InternalEncodingConstants.QNAME_TYPE_PROPERTIES,
            "InternalEncodingConstants.QNAME_TYPE_PROPERTIES");
    }

    public static class FaultComparator implements Comparator {
        private boolean sortName = false;
        public FaultComparator() {
        }
        public FaultComparator(boolean sortName) {
            this.sortName = sortName;
        }

        public int compare(Object o1, Object o2) {
            if (sortName) {
                QName name1 = ((Fault) o1).getBlock().getName();
                QName name2 = ((Fault) o2).getBlock().getName();
                /* Faults that are processed by name first, then type */
                if (!name1.equals(name2)) {
                    return name1.toString().compareTo(name2.toString());
                }
            }
            JavaStructureType type1 = ((Fault) o1).getJavaException();
            JavaStructureType type2 = ((Fault) o2).getJavaException();
            int result = sort(type1, type2);
            return result;
        }

        protected int sort(JavaStructureType type1, JavaStructureType type2) {
            if (type1.getName().equals(type2.getName())) {
                return 0;
            }
            JavaStructureType superType;
            superType = type1.getSuperclass();
            while (superType != null) {
                if (superType.equals(type2)) {
                    return -1;
                }
                superType = superType.getSuperclass();
            }
            superType = type2.getSuperclass();
            while (superType != null) {
                if (superType.equals(type1)) {
                    return 1;
                }
                superType = superType.getSuperclass();
            }
            if (type1.getSubclasses() == null && type2.getSubclasses() != null)
                return -1;
            if (type1.getSubclasses() != null && type2.getSubclasses() == null)
                return 1;
            if (type1.getSuperclass() != null
                && type2.getSuperclass() == null) {
                return 1;
            }
            if (type1.getSuperclass() == null
                && type2.getSuperclass() != null) {
                return -1;
            }
            return type1.getName().compareTo(type2.getName());
        }
    }

    public static class SubclassComparator implements Comparator {
        public SubclassComparator() {
        }

        public int compare(Object o1, Object o2) {
            JavaStructureType type1 = (JavaStructureType) o1;
            JavaStructureType type2 = (JavaStructureType) o2;
            return sort(type1, type2);
        }

        protected int sort(JavaStructureType type1, JavaStructureType type2) {
            JavaStructureType parent = type1;
            while (parent.getSuperclass() != null) {
                parent = parent.getSuperclass();
                if (parent.equals(type2))
                    return -1;
            }
            parent = type2;
            while (parent.getSuperclass() != null) {
                parent = parent.getSuperclass();
                if (parent.equals(type1))
                    return 1;
            }
            return type1.getName().compareTo(type2.getName());
        }
    }

}
