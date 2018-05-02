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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralSimpleTypeCreator
    extends JavaSimpleTypeCreator
    implements RmiConstants {
        
    // xsd: simpletypes
    public LiteralSimpleType XSD_BOOLEAN_LITERALTYPE;
    public LiteralSimpleType XSD_BOXED_BOOLEAN_LITERALTYPE;
    public LiteralSimpleType XSD_BYTE_LITERALTYPE;
    public LiteralSimpleType XSD_BYTE_ARRAY_LITERALTYPE;
    public LiteralSimpleType XSD_BOXED_BYTE_LITERALTYPE;
    public LiteralSimpleType XSD_BOXED_BYTE_ARRAY_LITERALTYPE;
    public LiteralSimpleType XSD_DOUBLE_LITERALTYPE;
    public LiteralSimpleType XSD_BOXED_DOUBLE_LITERALTYPE;
    public LiteralSimpleType XSD_FLOAT_LITERALTYPE;
    public LiteralSimpleType XSD_BOXED_FLOAT_LITERALTYPE;
    public LiteralSimpleType XSD_INT_LITERALTYPE;
    public LiteralSimpleType XSD_BOXED_INTEGER_LITERALTYPE;
    public LiteralSimpleType XSD_INTEGER_LITERALTYPE;
    public LiteralSimpleType XSD_LONG_LITERALTYPE;
    public LiteralSimpleType XSD_BOXED_LONG_LITERALTYPE;
    public LiteralSimpleType XSD_SHORT_LITERALTYPE;
    public LiteralSimpleType XSD_BOXED_SHORT_LITERALTYPE;
    public LiteralSimpleType XSD_DECIMAL_LITERALTYPE;
    public LiteralSimpleType XSD_DATE_TIME_LITERALTYPE;
    public LiteralSimpleType XSD_DATE_TIME_CALENDAR_LITERALTYPE;
    public LiteralSimpleType XSD_STRING_LITERALTYPE;
    public LiteralSimpleType XSD_QNAME_LITERALTYPE;
    public LiteralSimpleType XSD_VOID_LITERALTYPE;
    //    public SOAPAnyType XSD_ANYTYPE_LITERALTYPE;
    public LiteralSimpleType XSD_ANYTYPE_LITERALTYPE;
    public LiteralSimpleType XSD_ANY_URI_LITERALTYPE;


    public LiteralSimpleTypeCreator() {
        // xsd: simpletypes
        XSD_BOOLEAN_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.BOOLEAN, BOOLEAN_JAVATYPE);
        XSD_BOXED_BOOLEAN_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.BOOLEAN,
                BOXED_BOOLEAN_JAVATYPE,
                true);
        XSD_BYTE_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.BYTE, BYTE_JAVATYPE);
        XSD_BYTE_ARRAY_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.BASE64_BINARY,
                BYTE_ARRAY_JAVATYPE,
                true);
        XSD_BOXED_BYTE_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.BYTE, BOXED_BYTE_JAVATYPE, true);
        XSD_BOXED_BYTE_ARRAY_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.BASE64_BINARY,
                BOXED_BYTE_ARRAY_JAVATYPE,
                true);
        XSD_DOUBLE_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.DOUBLE, DOUBLE_JAVATYPE);
        XSD_BOXED_DOUBLE_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.DOUBLE,
                BOXED_DOUBLE_JAVATYPE,
                true);
        XSD_FLOAT_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.FLOAT, FLOAT_JAVATYPE);
        XSD_BOXED_FLOAT_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.FLOAT,
                BOXED_FLOAT_JAVATYPE,
                true);
        XSD_INT_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.INT, INT_JAVATYPE);
        XSD_BOXED_INTEGER_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.INT,
                BOXED_INTEGER_JAVATYPE,
                true);
        XSD_INTEGER_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.INTEGER,
                BIG_INTEGER_JAVATYPE,
                true);
        XSD_LONG_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.LONG, LONG_JAVATYPE);
        XSD_BOXED_LONG_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.LONG, BOXED_LONG_JAVATYPE, true);
        XSD_SHORT_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.SHORT, SHORT_JAVATYPE);
        XSD_BOXED_SHORT_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.SHORT,
                BOXED_SHORT_JAVATYPE,
                true);
        XSD_DECIMAL_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.DECIMAL, DECIMAL_JAVATYPE, true);
        XSD_DATE_TIME_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.DATE_TIME, DATE_JAVATYPE, true);
        XSD_DATE_TIME_CALENDAR_LITERALTYPE =
            new LiteralSimpleType(
                BuiltInTypes.DATE_TIME,
                CALENDAR_JAVATYPE,
                true);
        XSD_STRING_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.STRING, STRING_JAVATYPE, true);
        XSD_QNAME_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.QNAME, QNAME_JAVATYPE, true);
        XSD_VOID_LITERALTYPE = new LiteralSimpleType(null, VOID_JAVATYPE);
        XSD_ANYTYPE_LITERALTYPE =
            new LiteralSimpleType(
                SchemaConstants.QNAME_TYPE_URTYPE,
                OBJECT_JAVATYPE,
                true);
        XSD_ANY_URI_LITERALTYPE =
            new LiteralSimpleType(BuiltInTypes.ANY_URI, URI_JAVATYPE, true);
    }
    
    public void initializeJavaToXmlTypeMap(Map typeMap) {

        Map typeNameToDescription = new HashMap();
        initializeTypeMap(typeNameToDescription);

        for (Iterator eachClassName = typeNameToDescription.keySet().iterator();
            eachClassName.hasNext();
            ) {
            String className = (String) eachClassName.next();
            LiteralType typeDescription =
                (LiteralType) typeNameToDescription.get(className);

            Class javaType = classForName(className);
            if (javaType != null) {
                typeMap.put(javaType, typeDescription.getName());
            }
        }
    }

    private static Class classForName(String name) {
        return SOAPSimpleTypeCreatorBase.classForName(name);
    }

    public void initializeTypeMap(Map typeMap) {
        typeMap.put(BOXED_BOOLEAN_CLASSNAME, XSD_BOXED_BOOLEAN_LITERALTYPE);
        typeMap.put(BOOLEAN_CLASSNAME, XSD_BOOLEAN_LITERALTYPE);
        typeMap.put(BOXED_BYTE_CLASSNAME, XSD_BOXED_BYTE_LITERALTYPE);
        // don't uncomment this because Byte[]s can have nulls and soapenc:base64
        // doesn't handle that
        //        typeMap.put(BOXED_BYTE_ARRAY_CLASSNAME, LITERAL_BOXED_BYTE_ARRAY_LITERALTYPE);
        typeMap.put(BYTE_CLASSNAME, XSD_BYTE_LITERALTYPE);
        typeMap.put(BYTE_ARRAY_CLASSNAME, XSD_BYTE_ARRAY_LITERALTYPE);
        typeMap.put(BOXED_DOUBLE_CLASSNAME, XSD_BOXED_DOUBLE_LITERALTYPE);
        typeMap.put(DOUBLE_CLASSNAME, XSD_DOUBLE_LITERALTYPE);
        typeMap.put(BOXED_FLOAT_CLASSNAME, XSD_BOXED_FLOAT_LITERALTYPE);
        typeMap.put(FLOAT_CLASSNAME, XSD_FLOAT_LITERALTYPE);
        typeMap.put(BOXED_INTEGER_CLASSNAME, XSD_BOXED_INTEGER_LITERALTYPE);
        typeMap.put(INT_CLASSNAME, XSD_INT_LITERALTYPE);
        typeMap.put(BOXED_LONG_CLASSNAME, XSD_BOXED_LONG_LITERALTYPE);
        typeMap.put(LONG_CLASSNAME, XSD_LONG_LITERALTYPE);
        typeMap.put(BOXED_SHORT_CLASSNAME, XSD_BOXED_SHORT_LITERALTYPE);
        typeMap.put(SHORT_CLASSNAME, XSD_SHORT_LITERALTYPE);
        typeMap.put(STRING_CLASSNAME, XSD_STRING_LITERALTYPE);
        typeMap.put(BIGDECIMAL_CLASSNAME, XSD_DECIMAL_LITERALTYPE);
        typeMap.put(BIGINTEGER_CLASSNAME, XSD_INTEGER_LITERALTYPE);
        typeMap.put(DATE_CLASSNAME, XSD_DATE_TIME_LITERALTYPE);
        typeMap.put(CALENDAR_CLASSNAME, XSD_DATE_TIME_CALENDAR_LITERALTYPE);
        typeMap.put(QNAME_CLASSNAME, XSD_QNAME_LITERALTYPE);
        typeMap.put(VOID_CLASSNAME, XSD_VOID_LITERALTYPE);


        if (VersionUtil.isJavaVersionGreaterThan1_3())
            typeMap.put(java.net.URI.class.getName(), XSD_ANY_URI_LITERALTYPE);
        else
            typeMap.put(URI_CLASSNAME, XSD_ANY_URI_LITERALTYPE);
    }
}
