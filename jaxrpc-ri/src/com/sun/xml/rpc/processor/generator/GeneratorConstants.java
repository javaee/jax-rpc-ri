/*
 * $Id: GeneratorConstants.java,v 1.2 2006-04-13 01:28:43 ofung Exp $
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

package com.sun.xml.rpc.processor.generator;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface GeneratorConstants
    extends
        com.sun.xml.rpc.processor.modeler.ModelerConstants,
        com.sun.xml.rpc.spi.tools.GeneratorConstants {

    /*  
     * Constants used in the generators
     */
    public static final String FILE_TYPE_SERVICE_IMPL = "ServiceImpl";
    public static final String FILE_TYPE_SERIALIZER_REGISTRY =
        "SerializerRegistry";
    public static final String FILE_TYPE_VALUETYPE = "ValueType";
    public static final String FILE_TYPE_TIE = "Tie";
    public static final String FILE_TYPE_STUB = "Stub";
    public static final String FILE_TYPE_SERVLET_CONFIG = "ServletConfig";
    public static final String FILE_TYPE_SERIALIZER = "Serializer";
    public static final String FILE_TYPE_SOAP_FAULT_SERIALIZER =
        "SoapFaultSerializer";
    public static final String FILE_TYPE_SOAP_OBJECT_SERIALIZER =
        "SoapObjectSerializer";
    public static final String FILE_TYPE_LITERAL_OBJECT_SERIALIZER =
        "LiteralObjectSerializer";
    public static final String FILE_TYPE_INTERFACE_SERIALIZER =
        "InterfaceSerializer";
    public static final String FILE_TYPE_SOAP_OBJECT_BUILDER =
        "SOAPObject_Builder";
    public static final String FILE_TYPE_EXCEPTION = "Exception";
    public static final String FILE_TYPE_FAULT_EXCEPTION_BUILDER =
        "FaultExceptionBuilder";
    public static final String FILE_TYPE_HOLDER = "Holder";
    public static final String FILE_TYPE_ENUMERATION = "Enumeration";
    public static final String FILE_TYPE_ENUMERATION_ENCODER =
        "EnumerationEncoder";

    /*
     * Identifiers potentially useful for all Generators
     */
    public static final String ID_REMOTE_EXCEPTION = "java.rmi.RemoteException";
    public static final String ID_STUB_BASE = "com.sun.xml.rpc.client.StubBase";
    public static final String ID_TIE_BASE = "com.sun.xml.rpc.server.TieBase";

    // common base interface for all serializers
    public static final String BASE_SERIALIZER_NAME = "CombinedSerializer";
    // various serializer names
    public static final String REFERENCEABLE_SERIALIZER_NAME =
        "ReferenceableSerializerImpl";
    public static final String DYNAMIC_SERIALIZER_NAME = "DynamicSerializer";
    public static final String SIMPLE_TYPE_SERIALIZER_NAME =
        "SimpleTypeSerializer";
    public static final String SIMPLE_MULTI_TYPE_SERIALIZER_NAME =
        "SimpleMultiTypeSerializer";
    public static final String LITERAL_FRAGMENT_SERIALIZER_NAME =
        "LiteralFragmentSerializer";
    public static final String LITERAL_SIMPLE_TYPE_SERIALIZER_NAME =
        "LiteralSimpleTypeSerializer";
    public static final String ATTACHMENT_SERIALIZER_NAME =
        "AttachmentSerializer";
    public static final String COLLECTION_SERIALIZER_NAME =
        "CollectionSerializer";
    public static final String COLLECTION_INTERFACE_SERIALIZER_NAME =
        "CollectionInterfaceSerializer";
    public static final String MAP_SERIALIZER_NAME = "MapSerializer";
    public static final String MAP_INTERFACE_SERIALIZER_NAME =
        "MapInterfaceSerializer";
    public static final String JAX_RPC_MAP_ENTRY_SERIALIZER_NAME =
        "JAXRpcMapEntrySerializer";

    // encoder class names
    public static final String XSD_BASE64_BINARY_ENCODER_NAME =
        "XSDBase64BinaryEncoder";
    public static final String XSD_BOOLEAN_ENCODER_NAME = "XSDBooleanEncoder";
    public static final String XSD_BOXED_BASE64_BINARY_ENCODER_NAME =
        "XSDBoxedBase64BinaryEncoder";
    public static final String XSD_BOXED_HEX_BINARY_ENCODER_NAME =
        "XSDBoxedHexBinaryEncoder";
    public static final String XSD_BYTE_ENCODER_NAME = "XSDByteEncoder";
    public static final String XSD_DATE_TIME_CALENDAR_ENCODER_NAME =
        "XSDDateTimeCalendarEncoder";
    public static final String XSD_DATE_TIME_DATE_ENCODER_NAME =
        "XSDDateTimeDateEncoder";
    public static final String XSD_DATE_ENCODER_NAME = "XSDDateEncoder";
    public static final String XSD_DECIMAL_ENCODER_NAME = "XSDDecimalEncoder";
    public static final String XSD_DOUBLE_ENCODER_NAME = "XSDDoubleEncoder";
    public static final String XSD_FLOAT_ENCODER_NAME = "XSDFloatEncoder";
    public static final String XSD_HEX_BINARY_ENCODER_NAME =
        "XSDHexBinaryEncoder";
    public static final String XSD_INT_ENCODER_NAME = "XSDIntEncoder";
    public static final String XSD_INTEGER_ENCODER_NAME = "XSDIntegerEncoder";
    public static final String XSD_LONG_ENCODER_NAME = "XSDLongEncoder";
    public static final String XSD_QNAME_ENCODER_NAME = "XSDQNameEncoder";
    public static final String XSD_SHORT_ENCODER_NAME = "XSDShortEncoder";
    public static final String XSD_STRING_ENCODER_NAME = "XSDStringEncoder";
    public static final String XSD_ANY_URI_ENCODER_NAME = "XSDAnyURIEncoder";

    // new, vivekp
    public static final String XSD_TIME_ENCODER_NAME = "XSDTimeEncoder";
    public static final String XSD_LIST_ENCODER_NAME = "XSDListEncoder";
    public static final String XSD_POSITIVE_INTEGER_ENCODER_NAME =
        "XSDPositiveIntegerEncoder";
    public static final String XSD_NEGATIVE_INTEGER_ENCODER_NAME =
        "XSDNegativeIntegerEncoder";
    public static final String XSD_NON_NEGATIVE_INTEGER_ENCODER_NAME =
        "XSDNonNegativeIntegerEncoder";
    public static final String XSD_NON_POSITIVE_INTEGER_ENCODER_NAME =
        "XSDNonPositiveIntegerEncoder";
    public static final String XSD_UNSIGNED_LONG_ENCODER_NAME =
        "XSDUnsignedLongEncoder";
    public static final String XSD_UNSIGNED_INT_ENCODER_NAME =
        "XSDUnsignedIntEncoder";
    public static final String XSD_UNSIGNED_SHORT_ENCODER_NAME =
        "XSDUnsignedShortEncoder";
    public static final String XSD_UNSIGNED_BYTE_ENCODER_NAME =
        "XSDUnsignedByteEncoder";
    public static final String XSD_LIST_TYPE_ENCODER_NAME =
        "XSDListTypeEncoder";

    // Attachment Encoders
    public static final String IMAGE_ENCODER_NAME = "ImageAttachmentEncoder";
    public static final String MIME_MULTIPART_ENCODER_NAME =
        "MimeMultipartAttachmentEncoder";
    public static final String SOURCE_ENCODER_NAME = "SourceAttachmentEncoder";
    public static final String DATA_HANDLER_ENCODER_NAME =
        "DataHandlerAttachmentEncoder";

    // Strings used to declare serializers
    public static final String NULLABLE_STR = "NULLABLE";
    public static final String NOT_NULLABLE_STR = "NOT_NULLABLE";
    public static final String REFERENCEABLE_STR = "REFERENCEABLE";
    public static final String NOT_REFERENCEABLE_STR = "NOT_REFERENCEABLE";
    public static final String SERIALIZE_AS_REF_STR = "SERIALIZE_AS_REF";
    public static final String DONT_SERIALIZE_AS_REF_STR =
        "DONT_SERIALIZE_AS_REF";
    public static final String ENCODE_TYPE_STR = "ENCODE_TYPE";
    public static final String DONT_ENCODE_TYPE_STR = "DONT_ENCODE_TYPE";
    public static final String SOAP_VERSION_11 = "SOAPVersion.SOAP_11";
    public static final String SOAP_VERSION_12 = "SOAPVersion.SOAP_12";
    public static final String SOAPCONSTANTS_NS_SOAP_ENCODING =
        "SOAPConstants.NS_SOAP_ENCODING";
    public static final String SOAP12CONSTANTS_NS_SOAP_ENCODING =
        "SOAP12Constants.NS_SOAP_ENCODING";

    public static final String UNDERSCORE = "_";
    //    public static final String BRACKETS                        = "[]";
    public static final String STUB_SUFFIX = "_Stub";
    public static final String TIE_SUFFIX = "_Tie";
    public static final String SKELETON_SUFFIX = "_Skeleton";
    public static final String SERVANT_SUFFIX = "_Impl";
    public static final String HOLDER_SUFFIX = "Holder";
    public static final String JAVA_SRC_SUFFIX = ".java";
    public static final String SOAP_SERIALIZER_SUFFIX = "_SOAPSerializer";
    public static final String SOAP_INTERFACE_SERIALIZER_SUFFIX =
        "_InterfaceSOAPSerializer";
    public static final String ARRAY_SOAP_SERIALIZER_SUFFIX =
        "Array" + SOAP_SERIALIZER_SUFFIX;
    public static final String LITERAL_SERIALIZER_SUFFIX = "_LiteralSerializer";
    public static final String ARRAY_LITERAL_SERIALIZER_SUFFIX =
        "Array" + LITERAL_SERIALIZER_SUFFIX;
    public static final String SOAP_BUILDER_SUFFIX = "_SOAPBuilder";
    public static final String IMPL_SUFFIX = "_Impl";
    public static final String SERIALIZER_REGISTRY_SUFFIX =
        "_SerializerRegistry";
    public static final String ARRAY = "Array";
    public static final String MEMBER_PREFIX = "my";
    public static final String SERIALIZER_SUFFIX = "_Serializer";
    public static final String DESERIALIZER_SUFFIX = "_Deserializer";
    public static final String FAULT_SOAPSERIALIZER_SUFFIX =
        "_Fault_SOAPSerializer";
    public static final String FAULT_BUILDER_SUFFIX = "_Fault_SOAPBuilder";
    public static final String OPCODE_SUFFIX = "_OPCODE";
    public static final String QNAME_SUFFIX = "_QNAME";
    public static final String TYPE_QNAME_SUFFIX = "_TYPE" + QNAME_SUFFIX;
    public static final String GET = "get";
    public static final String IS = "is";
    public static final String SET = "set";
    public static final String RESPONSE = "Response";
    public static final String NS_PREFIX = "ns";
    public static final String SERVICE_SUFFIX = "_Service";
    public static final String SERVICE_IMPL_SUFFIX =
        SERVICE_SUFFIX + IMPL_SUFFIX;
    public static final String JAVA_PACKAGE_PREFIX = "java.";
    public static final String JAVAX_PACKAGE_PREFIX = "javax.";
    public static final String DOT_STR = ".";
}
