/*
 * $Id: ProcessorOptions.java,v 1.2 2006-04-13 01:28:20 ofung Exp $
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

package com.sun.xml.rpc.processor;

/**
 * Property names used by ProcessorActions
 *
 * @author JAX-RPC Development Team
 */
public class ProcessorOptions {
    
    public final static String SOURCE_DIRECTORY_PROPERTY = "sourceDirectory";
    public final static String DESTINATION_DIRECTORY_PROPERTY =
        "destinationDirectory";
    public final static String NONCLASS_DESTINATION_DIRECTORY_PROPERTY =
        "nonclassDestinationDirectory";
    public final static String ENCODE_TYPES_PROPERTY = "encodeTypes";
    public final static String MULTI_REF_ENCODING_PROPERTY = "multiRefEncoding";
    public final static String VALIDATE_WSDL_PROPERTY = "validationWSDL";
    public final static String EXPLICIT_SERVICE_CONTEXT_PROPERTY =
        "explicitServiceContext";
    public final static String PRINT_STACK_TRACE_PROPERTY = "printStackTrace";
    public final static String GENERATE_SERIALIZABLE_IF = "serializable";
    public final static String DONOT_OVERRIDE_CLASSES = "donotOverride";
    public final static String NO_DATA_BINDING_PROPERTY = "noDataBinding";
    public final static String SERIALIZE_INTERFACES_PROPERTY =
        "serializerInterfaces";
    public final static String USE_DATA_HANDLER_ONLY = "useDataHandlerOnly";
    public final static String SEARCH_SCHEMA_FOR_SUBTYPES =
        "searchSchemaForSubtypes";
    public final static String DONT_GENERATE_RPC_STRUCTURES =
        "dontGenerateRPCStructures";
    public final static String USE_DOCUMENT_LITERAL_ENCODING =
        "useDocumentLiteralEncoding";
    public final static String USE_RPC_LITERAL_ENCODING =
        "useRPCLiteralEncoding";
    public final static String USE_WSI_BASIC_PROFILE = "useWSIBasicProfile";
    public final static String GENERATE_ONE_WAY_OPERATIONS =
        "generateOneWayOperations";
    public final static String ENABLE_IDREF = "resolveIDREF";
    public final static String STRICT_COMPLIANCE = "strictCompliance";
    public final static String JAXB_ENUMTYPE = "jaxbenum";
    public final static String JAXRPC_SOURCE_VERSION = "sourceVersion";
    public final static String UNWRAP_DOC_LITERAL_WRAPPERS =
        "unwrapDocLitWrappers";
    public final static String DONT_GENERATE_WRAPPER_CLASSES =
        "dontGenerateWrapperClasses";
}
