/*
 * $Id: SOAPSimpleTypeCreator11.java,v 1.2 2006-04-13 01:31:24 ofung Exp $
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
package com.sun.xml.rpc.processor.modeler.rmi;

import java.util.Map;

import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.soap.SOAPWSDLConstants;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 * @author JAX-RPC Development Team
 *
 * JAXRPC 1.1 SOAPSimpleTypeCreator
 */
public class SOAPSimpleTypeCreator11 extends SOAPSimpleTypeCreatorBase {

    /**
     * 
     */
    public SOAPSimpleTypeCreator11() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param useStrictMode
     */
    public SOAPSimpleTypeCreator11(boolean useStrictMode) {
        super(useStrictMode);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param useStrictMode
     * @param version
     */
    public SOAPSimpleTypeCreator11(boolean useStrictMode, SOAPVersion version) {
        super(useStrictMode, version);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.modeler.rmi.SOAPSimpleTypeCreatorBase#initializeTypeMap(java.util.Map)
     */
    public void initializeTypeMap(Map typeMap) {
        typeMap.put(BOXED_BOOLEAN_CLASSNAME, SOAP_BOXED_BOOLEAN_SOAPTYPE);
        typeMap.put(BOOLEAN_CLASSNAME, XSD_BOOLEAN_SOAPTYPE);
        typeMap.put(BOXED_BYTE_CLASSNAME, SOAP_BOXED_BYTE_SOAPTYPE);
        // don't uncomment this because Byte[]s can have nulls and soapenc:base64
        // doesn't handle that
        //        typeMap.put(BOXED_BYTE_ARRAY_CLASSNAME, SOAP_BOXED_BYTE_ARRAY_SOAPTYPE);
        typeMap.put(BYTE_CLASSNAME, XSD_BYTE_SOAPTYPE);
        typeMap.put(BYTE_ARRAY_CLASSNAME, XSD_BYTE_ARRAY_SOAPTYPE);
        typeMap.put(BOXED_DOUBLE_CLASSNAME, SOAP_BOXED_DOUBLE_SOAPTYPE);
        typeMap.put(DOUBLE_CLASSNAME, XSD_DOUBLE_SOAPTYPE);
        typeMap.put(BOXED_FLOAT_CLASSNAME, SOAP_BOXED_FLOAT_SOAPTYPE);
        typeMap.put(FLOAT_CLASSNAME, XSD_FLOAT_SOAPTYPE);
        typeMap.put(BOXED_INTEGER_CLASSNAME, SOAP_BOXED_INTEGER_SOAPTYPE);
        typeMap.put(INT_CLASSNAME, XSD_INT_SOAPTYPE);
        typeMap.put(BOXED_LONG_CLASSNAME, SOAP_BOXED_LONG_SOAPTYPE);
        typeMap.put(LONG_CLASSNAME, XSD_LONG_SOAPTYPE);
        typeMap.put(BOXED_SHORT_CLASSNAME, SOAP_BOXED_SHORT_SOAPTYPE);
        typeMap.put(SHORT_CLASSNAME, XSD_SHORT_SOAPTYPE);
        typeMap.put(STRING_CLASSNAME, XSD_STRING_SOAPTYPE);
        typeMap.put(BIGDECIMAL_CLASSNAME, XSD_DECIMAL_SOAPTYPE);
        typeMap.put(BIGINTEGER_CLASSNAME, XSD_INTEGER_SOAPTYPE);
        typeMap.put(DATE_CLASSNAME, XSD_DATE_TIME_SOAPTYPE);
        typeMap.put(CALENDAR_CLASSNAME, XSD_DATE_TIME_CALENDAR_SOAPTYPE);
        typeMap.put(QNAME_CLASSNAME, XSD_QNAME_SOAPTYPE);
        typeMap.put(VOID_CLASSNAME, XSD_VOID_SOAPTYPE);
        //typeMap.put(URI_CLASSNAME, XSD_ANY_URI_SOAPTYPE);
        if (VersionUtil.isJavaVersionGreaterThan1_3())
            typeMap.put(URI_CLASSNAME, XSD_ANY_URI_SOAPTYPE);
        else
            typeMap.put(STRING_CLASSNAME, XSD_ANY_URI_SOAPTYPE);

        if (!useStrictMode) {
            typeMap.put(OBJECT_CLASSNAME, XSD_ANYTYPE_SOAPTYPE);

            // Collection Types
            typeMap.put(COLLECTION_CLASSNAME, COLLECTION_SOAPTYPE);
            typeMap.put(LIST_CLASSNAME, LIST_SOAPTYPE);
            typeMap.put(SET_CLASSNAME, SET_SOAPTYPE);
            typeMap.put(VECTOR_CLASSNAME, VECTOR_SOAPTYPE);
            typeMap.put(STACK_CLASSNAME, STACK_SOAPTYPE);
            typeMap.put(LINKED_LIST_CLASSNAME, LINKED_LIST_SOAPTYPE);
            typeMap.put(ARRAY_LIST_CLASSNAME, ARRAY_LIST_SOAPTYPE);
            typeMap.put(HASH_SET_CLASSNAME, HASH_SET_SOAPTYPE);
            typeMap.put(TREE_SET_CLASSNAME, TREE_SET_SOAPTYPE);

            // Map Types
            typeMap.put(MAP_CLASSNAME, MAP_SOAPTYPE);
            typeMap.put(HASH_MAP_CLASSNAME, HASH_MAP_SOAPTYPE);
            typeMap.put(TREE_MAP_CLASSNAME, TREE_MAP_SOAPTYPE);
            typeMap.put(HASHTABLE_CLASSNAME, HASHTABLE_SOAPTYPE);
            typeMap.put(PROPERTIES_CLASSNAME, PROPERTIES_SOAPTYPE);
            //        typeMap.put(WEAK_HASH_MAP_CLASSNAME, WEAK_HASH_MAP_SOAPTYPE);
        }
        // Attachment types
        typeMap.put(IMAGE_CLASSNAME, IMAGE_SOAPTYPE);
        typeMap.put(MIME_MULTIPART_CLASSNAME, MIME_MULTIPART_SOAPTYPE);
        typeMap.put(SOURCE_CLASSNAME, SOURCE_SOAPTYPE);
        typeMap.put(DATA_HANDLER_CLASSNAME, DATA_HANDLER_SOAPTYPE);
        typeMap.put(JAX_RPC_MAP_ENTRY_CLASSNAME, JAX_RPC_MAP_ENTRY_SOAPTYPE);
    }    

}
