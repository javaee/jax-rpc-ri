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
