/*
 * $Id: SchemaAnalyzer11.java,v 1.2 2006-04-13 01:31:27 ofung Exp $ 
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

/*
 * 
 * 
 *  * @ author Vivek Pandey	
 * 
 */
package com.sun.xml.rpc.processor.modeler.wsdl;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.encoding.simpletype.XSDAnyURIEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDBase64BinaryEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDBooleanEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDByteEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDateTimeCalendarEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDecimalEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDDoubleEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDFloatEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDHexBinaryEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDIntEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDIntegerEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDListTypeEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDLongEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDQNameEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDShortEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDStringEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDTimeEncoder;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;

/**
 * @author JAX-RPC Development Team
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SchemaAnalyzer11 extends SchemaAnalyzerBase {

    /**
     * @param document
     * @param modelInfo
     * @param options
     * @param conflictingClassNames
     * @param javaTypes
     */
    public SchemaAnalyzer11(
        AbstractDocument document,
        ModelInfo modelInfo,
        Properties options,
        Set conflictingClassNames,
        JavaSimpleTypeCreator javaTypes) {
        super(document, modelInfo, options, conflictingClassNames, javaTypes);
    }

    protected void initializeMaps() {
        _builtinSchemaTypeToJavaTypeMap = new HashMap();
        if (_useDataHandlerOnly) {
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_IMAGE,
                javaTypes.DATA_HANDLER_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_MIME_MULTIPART,
                javaTypes.DATA_HANDLER_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_SOURCE,
                javaTypes.DATA_HANDLER_JAVATYPE);
        } else {
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_IMAGE,
                javaTypes.IMAGE_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_MIME_MULTIPART,
                javaTypes.MIME_MULTIPART_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_SOURCE,
                javaTypes.SOURCE_JAVATYPE);
        }
        _builtinSchemaTypeToJavaTypeMap.put(
            InternalEncodingConstants.QNAME_TYPE_DATA_HANDLER,
            javaTypes.DATA_HANDLER_JAVATYPE);

        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.STRING,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.INTEGER,
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.INT,
            javaTypes.INT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.LONG,
            javaTypes.LONG_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.SHORT,
            javaTypes.SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.DECIMAL,
            javaTypes.DECIMAL_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.FLOAT,
            javaTypes.FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.DOUBLE,
            javaTypes.DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.BOOLEAN,
            javaTypes.BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.BYTE,
            javaTypes.BYTE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.QNAME,
            javaTypes.QNAME_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.DATE_TIME,
            javaTypes.CALENDAR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.BASE64_BINARY,
            javaTypes.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.HEX_BINARY,
            javaTypes.BYTE_ARRAY_JAVATYPE);
        // New types 12/3/02
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.NORMALIZED_STRING,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.TOKEN,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.LANGUAGE,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.NAME,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.NMTOKEN,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.NCNAME,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.ID,
            javaTypes.STRING_JAVATYPE);

        // New Types 12/3/02, vivekp
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.POSITIVE_INTEGER,
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.NON_POSITIVE_INTEGER,
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.NEGATIVE_INTEGER,
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.NON_NEGATIVE_INTEGER,
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.UNSIGNED_LONG,
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.UNSIGNED_INT,
            javaTypes.LONG_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.UNSIGNED_SHORT,
            javaTypes.INT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.UNSIGNED_BYTE,
            javaTypes.SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.DURATION,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.DATE,
            javaTypes.CALENDAR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.TIME,
            javaTypes.CALENDAR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.G_YEAR_MONTH,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.G_YEAR,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.G_MONTH_DAY,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.G_DAY,
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.G_MONTH,
            javaTypes.STRING_JAVATYPE);

        // for jdk < 1.4 map xsd:anyURI to String, otherwise to java.net.URI
        if (!VersionUtil.isJavaVersionGreaterThan1_3())
            _builtinSchemaTypeToJavaTypeMap.put(
                BuiltInTypes.ANY_URI,
                javaTypes.STRING_JAVATYPE);
        else
            _builtinSchemaTypeToJavaTypeMap.put(
                BuiltInTypes.ANY_URI,
                javaTypes.URI_JAVATYPE);

        // map xsd:IDREF to java.lang.Object if compile time flag 
        // "resolveidref" is set otherwise to String           
        if (_resolveIDREF)
            _builtinSchemaTypeToJavaTypeMap.put(
                BuiltInTypes.IDREF,
                javaTypes.OBJECT_JAVATYPE);
        else
            _builtinSchemaTypeToJavaTypeMap.put(
                BuiltInTypes.IDREF,
                javaTypes.STRING_JAVATYPE);
        //bug fix: 4863162
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.IDREFS,
            javaTypes.STRING_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.NMTOKENS,
            javaTypes.STRING_ARRAY_JAVATYPE);

        _builtinSchemaTypeToJavaTypeMap.put(
            SchemaConstants.QNAME_TYPE_URTYPE,
            javaTypes.OBJECT_JAVATYPE);

        // bug fix: 4925400
        _builtinSchemaTypeToJavaTypeMap.put(
            BuiltInTypes.ANY_SIMPLE_URTYPE,
            javaTypes.STRING_JAVATYPE);

        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeString(),
            javaTypes.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeInteger(),
            javaTypes.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeInt(),
            javaTypes.BOXED_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeLong(),
            javaTypes.BOXED_LONG_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeShort(),
            javaTypes.BOXED_SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeDecimal(),
            javaTypes.DECIMAL_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeFloat(),
            javaTypes.BOXED_FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeDouble(),
            javaTypes.BOXED_DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeBoolean(),
            javaTypes.BOXED_BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeByte(),
            javaTypes.BOXED_BYTE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeQName(),
            javaTypes.QNAME_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeDateTime(),
            javaTypes.CALENDAR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeBase64Binary(),
            javaTypes.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeHexBinary(),
            javaTypes.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(
            soap11WSDLConstants.getQNameTypeBase64(),
            javaTypes.BYTE_ARRAY_JAVATYPE);
        if (!_strictCompliance) {
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_COLLECTION,
                javaTypes.COLLECTION_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_LIST,
                javaTypes.LIST_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_SET,
                javaTypes.SET_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_ARRAY_LIST,
                javaTypes.ARRAY_LIST_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_VECTOR,
                javaTypes.VECTOR_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_STACK,
                javaTypes.STACK_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_LINKED_LIST,
                javaTypes.LINKED_LIST_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_HASH_SET,
                javaTypes.HASH_SET_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_TREE_SET,
                javaTypes.TREE_SET_JAVATYPE);

            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_MAP,
                javaTypes.MAP_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_JAX_RPC_MAP_ENTRY,
                javaTypes.JAX_RPC_MAP_ENTRY_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_HASH_MAP,
                javaTypes.HASH_MAP_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_TREE_MAP,
                javaTypes.TREE_MAP_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_HASHTABLE,
                javaTypes.HASHTABLE_JAVATYPE);
            _builtinSchemaTypeToJavaTypeMap.put(
                InternalEncodingConstants.QNAME_TYPE_PROPERTIES,
                javaTypes.PROPERTIES_JAVATYPE);
        }
        _builtinSchemaTypeToJavaWrapperTypeMap = new HashMap();
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.INT,
            javaTypes.BOXED_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.LONG,
            javaTypes.BOXED_LONG_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.SHORT,
            javaTypes.BOXED_SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.FLOAT,
            javaTypes.BOXED_FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.DOUBLE,
            javaTypes.BOXED_DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.BOOLEAN,
            javaTypes.BOXED_BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(
            BuiltInTypes.BYTE,
            javaTypes.BOXED_BYTE_JAVATYPE);

        _simpleTypeEncoderMap = new HashMap();
        _simpleTypeEncoderMap.put(
            BuiltInTypes.STRING,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.INTEGER,
            XSDIntegerEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.INT,
            XSDIntEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.LONG,
            XSDLongEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.SHORT,
            XSDShortEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.DECIMAL,
            XSDDecimalEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.FLOAT,
            XSDFloatEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.DOUBLE,
            XSDDoubleEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.BYTE,
            XSDByteEncoder.getInstance());
        // Doug 12/20/02
        _simpleTypeEncoderMap.put(
            BuiltInTypes.NORMALIZED_STRING,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.TOKEN,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.LANGUAGE,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.NAME,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.NMTOKEN,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.NCNAME,
            XSDStringEncoder.getInstance());

        /** rest of simple types, bug fix 4923072, support added except boolean, xsd:dateTime, xsd:QName, xsd:base64Binary,
            xsd:hexBinary, xsd:date, xsd:time.
          */
        _simpleTypeEncoderMap.put(
            BuiltInTypes.UNSIGNED_INT,
            XSDLongEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.UNSIGNED_SHORT,
            XSDIntEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.UNSIGNED_BYTE,
            XSDShortEncoder.getInstance());
        if (!VersionUtil.isJavaVersionGreaterThan1_3())
            _simpleTypeEncoderMap.put(
                BuiltInTypes.ANY_URI,
                XSDStringEncoder.getInstance());
        else
            _simpleTypeEncoderMap.put(
                BuiltInTypes.ANY_URI,
                XSDAnyURIEncoder.getInstance());
        //_simpleTypeEncoderMap.put(BuiltInTypes.ANY_SIMPLE_URTYPE, XSDStringEncoder.getInstance());

        //derived
        _simpleTypeEncoderMap.put(
            BuiltInTypes.NMTOKENS,
            XSDListTypeEncoder.getInstance(
                XSDStringEncoder.getInstance(),
                String.class));
        _simpleTypeEncoderMap.put(
            BuiltInTypes.DURATION,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.G_YEAR_MONTH,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.G_YEAR,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.G_MONTH_DAY,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.G_DAY,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.G_MONTH,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.ID,
            XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.NON_POSITIVE_INTEGER,
            XSDIntegerEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.NEGATIVE_INTEGER,
            XSDIntegerEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.NON_NEGATIVE_INTEGER,
            XSDIntegerEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.UNSIGNED_LONG,
            XSDIntegerEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.POSITIVE_INTEGER,
            XSDIntegerEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.BASE64_BINARY,
            XSDBase64BinaryEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.HEX_BINARY,
            XSDHexBinaryEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.TIME,
            XSDTimeEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.DATE,
            XSDDateTimeCalendarEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.DATE_TIME,
            XSDDateTimeCalendarEncoder.getInstance());
        /* Amlan 02/28/2003 : NB : Support for QName in _simpleTypeEncoderMap is provided
           to fascillate support for QName for rpc literal in TCK Testing. Note this was
           commented out by Doug on 12/20/2002 above. */
        _simpleTypeEncoderMap.put(
            BuiltInTypes.BOOLEAN,
            XSDBooleanEncoder.getInstance());
        _simpleTypeEncoderMap.put(
            BuiltInTypes.QNAME,
            XSDQNameEncoder.getInstance());

    }

}
