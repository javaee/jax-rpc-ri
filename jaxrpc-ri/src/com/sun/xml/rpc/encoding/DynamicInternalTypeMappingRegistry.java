/*
 * $Id: DynamicInternalTypeMappingRegistry.java,v 1.1 2006-04-12 20:33:13 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * <p> A decorator for TypeMappings that attempts to provide (de)serializers
 * for types that are not registered in the underlying TypeMapping. </p>
 *
 * @author JAX-RPC Development Team
 */

package com.sun.xml.rpc.encoding;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.encoding.SerializerFactory;

import com.sun.xml.rpc.client.dii.BasicCall;
import com.sun.xml.rpc.client.dii.OperationInfo;
import com.sun.xml.rpc.client.dii.ParameterMemberInfo;
import com.sun.xml.rpc.encoding.literal.LiteralArraySerializer;
import com.sun.xml.rpc.encoding.literal.LiteralFragmentSerializer;
import com.sun.xml.rpc.encoding.literal.LiteralObjectArraySerializer;
import com.sun.xml.rpc.encoding.literal.LiteralSimpleTypeSerializer;
import com.sun.xml.rpc.encoding.literal.ValueTypeLiteralSerializer;
import com.sun.xml.rpc.encoding.simpletype.XSDIntEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDListTypeEncoder;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

public class DynamicInternalTypeMappingRegistry implements
    InternalTypeMappingRegistry, SerializerConstants {
    // fix for bug 4773552
    protected static final QName ELEMENT_NAME = null;

    protected static String DEFAULT_OPERATION_STYLE = "document";
    protected InternalTypeMappingRegistry registry = null;

    private TypeMappingImpl cachedEncodedMappings = new TypeMappingImpl();
    private TypeMappingImpl cachedLiteralMappings = new TypeMappingImpl();
    private HashMap qnameToJavaClass = new HashMap();
    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;
    private String operationStyle;
    private BasicCall currentCall;
    private HashMap dynamicMemberRegistry = new HashMap();


    public void setStyles(String operationStyle) {
        this.operationStyle = operationStyle;
        if (operationStyle == null)
            operationStyle = DEFAULT_OPERATION_STYLE;
    }

    public HashMap getDynamicMemberRegistry() {
        return dynamicMemberRegistry;
    }

    public void addDynamicRegistryMembers(Class parentClass,
                                          QName parentXmlType, String encoding,
                                          ParameterMemberInfo[] memberInfo) {

        String key = makeKey(parentClass, parentXmlType, encoding);

        dynamicMemberRegistry.put(key, memberInfo);
    }

    public ParameterMemberInfo[] getDynamicRegistryMembers(Class parentClass,
                                                           QName parentXmlType, String encoding) {

        String key = makeKey(parentClass, parentXmlType, encoding);
        return (ParameterMemberInfo[]) dynamicMemberRegistry.get(key);
    }

    private String makeKey(Class parentClass, QName parentXmlType, String encoding) {
        String pname = parentClass != null ? parentClass.getName() : "";
        String pxml = parentXmlType != null ? parentXmlType.getLocalPart() : "";
        return new String(pname + pxml + encoding);
    }

    public String getStyle() {
        return this.operationStyle;
    }

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public DynamicInternalTypeMappingRegistry(
        InternalTypeMappingRegistry registry,
        BasicCall currentCall) {

        this(registry, currentCall, SOAPVersion.SOAP_11);
    }

    public DynamicInternalTypeMappingRegistry(InternalTypeMappingRegistry registry) {

        this(registry, null, SOAPVersion.SOAP_11);
    }

    public DynamicInternalTypeMappingRegistry(
        InternalTypeMappingRegistry registry,
        BasicCall currentCall,
        SOAPVersion ver) {

        init(ver); // Initialize SOAP constants
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        this.currentCall = currentCall;
        this.registry = registry;
    }

    public Serializer getSerializer(
        String encoding,
        Class javaType,
        QName xmlType)
        throws Exception {

        try {
            return registry.getSerializer(encoding, javaType, xmlType);
        } catch (TypeMappingException ex) {
            try {
                if (encoding
                    .equals(soapEncodingConstants.getSOAPEncodingNamespace())) {
                    Serializer serializer =
                        getCachedEncodedSerializer(javaType, xmlType);
                    if (serializer != null) {
                        return serializer;
                    }
                    if (isArray(javaType, xmlType)) {
                        return createArraySerializer(javaType, xmlType);

                    } else { //everything else is treated as valueType
                        return createValueSerializer(javaType, xmlType);
                    }
                } else if (
                    encoding.equals("") && "document".equals(operationStyle)) {
                    Serializer serializer =
                        getCachedLiteralSerializer(javaType, xmlType);
                    if (serializer != null) {
                        return serializer;
                    }
                    if (isLiteralArray(javaType, xmlType)) {
                        return createLiteralArraySerializer(javaType, xmlType);
                    } else { //everything else is treated as valueType
                        return createLiteralValueTypeSerializer(
                            xmlType,
                            javaType);
                    }
                } else if (
                    encoding.equals("") && "rpc".equals(operationStyle)) {
                    Serializer serializer =
                        getCachedLiteralSerializer(javaType, xmlType);
                    if (serializer != null) {
                        return serializer;
                    }
                    if (isLiteralArray(javaType, xmlType)) {
                        return createRPCLiteralArraySerializer(
                            javaType,
                            xmlType);
                    } else { //everything else is treated as valueType
                        return createLiteralValueTypeSerializer(
                            xmlType,
                            javaType);
                    }
                }
            } catch (JAXRPCExceptionBase e) {
                throw new SerializationException(e);
            } catch (Exception e) {
                throw new SerializationException(
                    new LocalizableExceptionAdapter(e));
            }
        }
        return null;
    }

    public Deserializer getDeserializer(
        String encoding,
        Class javaType,
        QName xmlType)
        throws Exception {

        try {
            return registry.getDeserializer(encoding, javaType, xmlType);
        } catch (TypeMappingException ex) {
            try {
                if (encoding
                    .equals(soapEncodingConstants.getSOAPEncodingNamespace())) {
                    Deserializer deserializer =
                        getCachedEncodedDeserializer(javaType, xmlType);
                    if (deserializer != null) {
                        return deserializer;
                    }
                    // we'd like to have the reader here so that we could do:
                    // reader.getAttributes().getValue(soapEncodingConstants.URI_ENCODING, "arrayType");
                    // and check to see if the result is not null. For now we'll content ourselves with:
                    if (isArray(javaType, xmlType)) {
                        return createArraySerializer(javaType, xmlType);
                    } else { // We treat anything we don't recognize as a value type
                        return createValueSerializer(javaType, xmlType);
                    }
                } else if (
                    encoding.equals("") && "document".equals(operationStyle)) {
                    Deserializer deserializer =
                        getCachedLiteralDeserializer(javaType, xmlType);
                    if (deserializer != null) {
                        return deserializer;
                    } //need to make this doclit
                    if (isLiteralArray(javaType, xmlType)) {
                        return (Deserializer) createLiteralArraySerializer(
                            javaType,
                            xmlType);
                    } else { // We treat anything we don't recognize as a value type
                        return (Deserializer) createLiteralValueTypeSerializer(
                            xmlType,
                            javaType);
                    }
                } else if (
                    (encoding.equals("") && "rpc".equals(operationStyle))) {
                    Deserializer deserializer =
                        getCachedLiteralDeserializer(javaType, xmlType);
                    if (deserializer != null) {
                        return deserializer;
                    }
                    if (isLiteralArray(javaType, xmlType)) {
                        return (Deserializer) createRPCLiteralArraySerializer(
                            javaType,
                            xmlType);
                    } else { // We treat anything we don't recognize as a value type
                        return (Deserializer) createLiteralValueTypeSerializer(
                            xmlType,
                            javaType);
                    }
                }
                throw ex;
            } catch (JAXRPCExceptionBase e) {
                throw new SerializationException(e);
            } catch (Exception e) {
                throw new SerializationException(
                    new LocalizableExceptionAdapter(e));
            }
        }
    }

    protected Serializer getCachedEncodedSerializer(
        Class javaType,
        QName xmlType) {

        SerializerFactory serializerFactory =
            cachedEncodedMappings.getSerializer(javaType, xmlType);
        if (serializerFactory != null) {
            return serializerFactory.getSerializerAs(
                EncodingConstants.JAX_RPC_RI_MECHANISM);
        }
        return null;
    }

    protected Deserializer getCachedEncodedDeserializer(
        Class javaType,
        QName xmlType) {

        DeserializerFactory deserializerFactory =
            cachedEncodedMappings.getDeserializer(javaType, xmlType);
        if (deserializerFactory != null) {
            return deserializerFactory.getDeserializerAs(
                EncodingConstants.JAX_RPC_RI_MECHANISM);
        }
        return null;
    }

    protected Serializer getCachedLiteralSerializer(
        Class javaType,
        QName xmlType) {

        SerializerFactory serializerFactory =
            cachedLiteralMappings.getSerializer(javaType, xmlType);
        if (serializerFactory != null) {
            return serializerFactory.getSerializerAs(
                EncodingConstants.JAX_RPC_RI_MECHANISM);
        }
        return null;
    }

    protected Deserializer getCachedLiteralDeserializer(
        Class javaType,
        QName xmlType) {

        DeserializerFactory deserializerFactory =
            cachedLiteralMappings.getDeserializer(javaType, xmlType);
        if (deserializerFactory != null) {
            return deserializerFactory.getDeserializerAs(
                EncodingConstants.JAX_RPC_RI_MECHANISM);
        }
        return null;
    }

    //ToDo: rpc/encoded - needs much work - very buggy
    private ReferenceableSerializerImpl createArraySerializer(
        Class javaType,
        QName xmlType)
        throws Exception {
        if (javaType == null || xmlType == null) {
            return null;
        }

        Class elementType = javaType.getComponentType();
        String elementName = null;
        if (elementType != null) {
            elementName = elementType.getName();
            int idx = elementName.lastIndexOf(".");
            if (idx != -1)
                elementName =
                    (elementName.substring(idx + 1, elementName.length()))
                    .toLowerCase();
        }
        Serializer componentSerializer = null;
        try {
            componentSerializer =
                registry.getSerializer(
                    soapEncodingConstants.getURIEncoding(),
                    elementType);
        } catch (TypeMappingException ex) {
            //not found continue
        }
        ObjectArraySerializer objectArraySerializer = null;
        ReferenceableSerializerImpl serializer = null;
        if (componentSerializer != null) {
            objectArraySerializer =
                new ObjectArraySerializer(
                    xmlType,
                    ENCODE_TYPE,
                    NULLABLE,
                    SOAPConstants.NS_SOAP_ENCODING,
                    ELEMENT_NAME,
                    new QName(SchemaConstants.NS_XSD, elementName),
                    elementType,
                    1,
                    null);
            serializer =
                new ReferenceableSerializerImpl(
                    DONT_SERIALIZE_AS_REF,
                    objectArraySerializer);
            cachedEncodedMappings.register(
                javaType,
                soapEncodingConstants.getQNameEncodingArray(),
                new SingletonSerializerFactory(objectArraySerializer),
                new SingletonDeserializerFactory(objectArraySerializer));
        } else {
            serializer =
                new ReferenceableSerializerImpl(
                    DONT_SERIALIZE_AS_REF,
                    new PolymorphicArraySerializer(
                        xmlType,
                        DONT_ENCODE_TYPE,
                        NULLABLE,
                        soapEncodingConstants.getURIEncoding(),
                        ELEMENT_NAME));
            cachedEncodedMappings.register(
                javaType,
                xmlType,
                new SingletonSerializerFactory(serializer),
                new SingletonDeserializerFactory(serializer));
        }
        serializer.initialize(this);

        return serializer;
    }

    //todo: review - update workarounds - check logic flow-
    private Serializer createRPCLiteralArraySerializer(
        Class javaType,
        QName xmlType)
        throws Exception {
        if (javaType == null || xmlType == null) {
            return null;
        }

        if (javaType.isArray()) {
            QName foundXmlType = checkParameterXmlTypesUsingModels(xmlType);
            if (foundXmlType != null)
                xmlType = foundXmlType;
        }

        ParameterMemberInfo[] pmemberInfos =
            getDynamicRegistryMembers(javaType, xmlType, "");

        //todo:-- this is to get qname which I don't have as the model isn't propagated
        int size = 0;
        if (pmemberInfos != null)
            size = pmemberInfos.length;
        String pmname = null;
        QName pmXmlType = null;
        Class pmClass = null;
        if (size > 0) {
            ParameterMemberInfo pinfo = pmemberInfos[0];
            pmname = pinfo.getMemberName();
            pmXmlType = pinfo.getMemberXmlType();
            pmClass = pinfo.getMemberJavaClass();
        }



        //replace part of this
        Class elementType = javaType.getComponentType();
        String elementName = null;
        if (elementType != null) {
            elementName = elementType.getName();
            int idx = elementName.lastIndexOf(".");
            if (idx != -1) {
                elementName =
                    (elementName.substring(idx + 1, elementName.length()))
                    .toLowerCase();
                //bug 4908124
                idx = elementName.indexOf("big");
                if (idx != -1)
                    elementName =
                        (elementName.substring(idx + 3, elementName.length()));
                //bug 4908124 - calendar limited to xsd:time
                if (elementName.equals("calendar"))
                    elementName = "dateTime";
            }
        }

        QName componentQName = null;
        if (elementType == pmClass) {
            if (pmXmlType != null)
                componentQName = pmXmlType;

        }

        componentQName = new QName(SchemaConstants.NS_XSD, elementName);

        JAXRPCSerializer componentSerializer = null;
        try {
            componentSerializer =
                (JAXRPCSerializer) registry.getSerializer(
                    "",
                    elementType,
                    componentQName);
        } catch (TypeMappingException ex) {
            //eat it for now
        }
        JAXRPCSerializer ArraySerializer = null;

        if ((componentSerializer != null)
            && (componentSerializer instanceof LiteralSimpleTypeSerializer)) {

            componentSerializer =
                new LiteralSimpleTypeSerializer(
                    xmlType,
                    "",
                    ((LiteralSimpleTypeSerializer) componentSerializer)
                    .getEncoder());

            ArraySerializer =
                new LiteralArraySerializer(
                    xmlType,
                    NULLABLE,
                    "",
                    DONT_ENCODE_TYPE,
                    javaType,
                    componentSerializer,
                    elementType);
            cachedLiteralMappings.register(
                javaType,
                xmlType,
                new SingletonSerializerFactory(ArraySerializer),
                new SingletonDeserializerFactory(
                    (Deserializer) ArraySerializer));

        } else if (componentSerializer != null) {
            //component's not a it's not a LiteralSimpleType - need object array serializer
            ArraySerializer =
                new LiteralObjectArraySerializer(
                    xmlType,
                    NULLABLE,
                    "",
                    DONT_ENCODE_TYPE,
                    javaType,
                    componentSerializer,
                    elementType);
            cachedLiteralMappings.register(
                javaType,
                xmlType,
                new SingletonSerializerFactory(ArraySerializer),
                new SingletonDeserializerFactory(
                    (Deserializer) ArraySerializer));
        } else {
            //check compinentelement Type -- is it an array-
            //if not we assume that it is avalueType and create valueType serializer for this
            //the below is to check model for correct qname which does not get propagated
            //via the model -- it is a workaround for the time being
            QName foundQName =
                checkParameterXmlTypesUsingModels(componentQName);
            if (foundQName != null)
                componentQName = foundQName;

            if (!isLiteralArray(elementType, componentQName)) {
                //assuem valueType    //xmlType instead of componentQname - which is correct
                componentSerializer =
                    (JAXRPCSerializer) createLiteralValueTypeSerializer(xmlType,
                                                                        elementType);
                ArraySerializer =
                    new LiteralObjectArraySerializer(
                        xmlType,
                        NULLABLE,
                        "",
                        DONT_ENCODE_TYPE,
                        javaType,
                        componentSerializer,
                        elementType);
                cachedLiteralMappings.register(
                    javaType,
                    xmlType,
                    new SingletonSerializerFactory(ArraySerializer),
                    new SingletonDeserializerFactory(
                        (Deserializer) ArraySerializer));
            }
        }
        try {
            if (ArraySerializer instanceof LiteralArraySerializer)
                ((LiteralArraySerializer) ArraySerializer).initialize(this);
            else
                ((LiteralObjectArraySerializer) ArraySerializer).initialize(
                    this);
        } catch (ClassCastException ce) {
            System.out.println(
                "literal ArraySerializer.initialize" + ce.getMessage());
            ce.printStackTrace();
        }
        return ArraySerializer;
    }

    private Serializer createLiteralArraySerializer(
        Class javaType,
        QName xmlType)
        throws Exception {

        if (javaType == null || xmlType == null) {
            return null;
        }

        LiteralType type = getParameterLiteralType(xmlType.getLocalPart());
        if ((type != null) && (type instanceof LiteralListType))
            return createLiteralListSerializer(javaType, xmlType);

        if (javaType.isArray()) {
            QName foundXmlType = checkParameterXmlTypesUsingModels(xmlType);
            if (foundXmlType != null)
                xmlType = foundXmlType;
        }

        //replace part of this
        Class elementType = javaType.getComponentType();
        String elementName = null;
        if (elementType != null) {
            elementName = elementType.getName();
            int idx = elementName.lastIndexOf(".");
            if (idx != -1)
                elementName =
                    (elementName.substring(idx + 1, elementName.length()))
                    .toLowerCase();
        }

        QName componentQName = new QName(SchemaConstants.NS_XSD, elementName);

        JAXRPCSerializer componentSerializer = null;
        try {
            componentSerializer =
                (JAXRPCSerializer) registry.getSerializer(
                    "",
                    elementType,
                    componentQName);
        } catch (TypeMappingException ex) {
            //eat it for now
        }
        JAXRPCSerializer ArraySerializer = null;

        if ((componentSerializer != null)
            && (componentSerializer instanceof LiteralSimpleTypeSerializer)) {

            componentSerializer =
                new LiteralSimpleTypeSerializer(
                    xmlType,
                    "",
                    ((LiteralSimpleTypeSerializer) componentSerializer)
                    .getEncoder());

            ArraySerializer =
                new LiteralArraySerializer(
                    xmlType,
                    NULLABLE,
                    "",
                    DONT_ENCODE_TYPE,
                    javaType,
                    componentSerializer,
                    elementType);
            cachedLiteralMappings.register(
                javaType,
                xmlType,
                new SingletonSerializerFactory(ArraySerializer),
                new SingletonDeserializerFactory(
                    (Deserializer) ArraySerializer));

        } else if (componentSerializer != null) {
            //it's not a LiteralSimpleType
            ArraySerializer =
                new LiteralObjectArraySerializer(
                    xmlType,
                    NULLABLE,
                    "",
                    DONT_ENCODE_TYPE,
                    javaType,
                    componentSerializer,
                    elementType);
            cachedLiteralMappings.register(
                javaType,
                xmlType,
                new SingletonSerializerFactory(ArraySerializer),
                new SingletonDeserializerFactory(
                    (Deserializer) ArraySerializer));

        } else {
            //check compinentelement Type -- is it an array-
            //if not we assume that it is avalueType and create valueType serializer for this
            QName foundQName =
                checkParameterXmlTypesUsingModels(componentQName);
            if (foundQName != null)
                componentQName = foundQName;

            if (!isLiteralArray(elementType, componentQName)) {
                //assuem valueType    //xmlType instead of componentQname - which is correct
                componentSerializer =
                    (JAXRPCSerializer) createLiteralValueTypeSerializer(xmlType,
                                                                        elementType);
                ArraySerializer =
                    new LiteralObjectArraySerializer(
                        xmlType,
                        NULLABLE,
                        "",
                        DONT_ENCODE_TYPE,
                        javaType,
                        componentSerializer,
                        elementType);
                cachedLiteralMappings.register(
                    javaType,
                    xmlType,
                    new SingletonSerializerFactory(ArraySerializer),
                    new SingletonDeserializerFactory(
                        (Deserializer) ArraySerializer));
            }
        }

        try {
            if (ArraySerializer instanceof LiteralArraySerializer)
                ((LiteralArraySerializer) ArraySerializer).initialize(this);
            else
                ((LiteralObjectArraySerializer) ArraySerializer).initialize(
                    this);
        } catch (ClassCastException ce) {
            System.out.println(
                "literal ArraySerializer.initialize" + ce.getMessage());
            ce.printStackTrace();
        }
        return ArraySerializer;
    }

    Serializer createLiteralListSerializer(Class javaType, QName xmlType) {
        QName type = xmlType;
        //bug fix: 4906014
        CombinedSerializer serializer =
            new LiteralSimpleTypeSerializer(
                type,
                "",
                XSDListTypeEncoder.getInstance(
                    XSDIntEncoder.getInstance(),
                    int.class));
        cachedLiteralMappings.register(
            javaType,
            xmlType,
            new SingletonSerializerFactory(serializer),
            new SingletonDeserializerFactory((Deserializer) serializer));

        return serializer;
    }

    //todo: buggy needs fixes - model propagation -> qname issues - member issues
    private ReferenceableSerializerImpl createValueSerializer(
        Class javaType,
        QName xmlType)
        throws Exception {

        if (javaType == null || xmlType == null) {
            return null;
        }

        ReferenceableSerializerImpl serializer =
            new ReferenceableSerializerImpl(
                SERIALIZE_AS_REF,
                new ValueTypeSerializer(
                    xmlType,
                    DONT_ENCODE_TYPE,
                    NULLABLE,
                    soapEncodingConstants.getURIEncoding(),
                    javaType));
        cachedEncodedMappings.register(
            javaType,
            xmlType,
            new SingletonSerializerFactory(serializer),
            new SingletonDeserializerFactory(serializer));

        serializer.initialize(this);

        return serializer;
    }

    private Serializer createLiteralValueTypeSerializer(
        QName xmlType,
        Class javaType)
        throws Exception {

        if ((javaType != null)
            && javax.xml.soap.SOAPElement.class.isAssignableFrom(javaType)) {
            return SOAPElementLiteralSerializer(xmlType, javaType);
        }

        JAXRPCSerializer stdSerializer =
            getStandardSerializer("", javaType, xmlType);
        if (stdSerializer != null) {
            cachedLiteralMappings.register(
                javaType,
                xmlType,
                new SingletonSerializerFactory(stdSerializer),
                new SingletonDeserializerFactory(
                    (JAXRPCDeserializer) stdSerializer));
            return stdSerializer;
        }

        Collection params = orderCurrentMembersPerWsdl(xmlType, javaType);
        ValueTypeLiteralSerializer serializer =
            new ValueTypeLiteralSerializer(
                xmlType,
                DONT_ENCODE_TYPE,
                NULLABLE,
                "",
                javaType,
                params);
        cachedLiteralMappings.register(
            javaType,
            xmlType,
            new SingletonSerializerFactory(serializer),
            new SingletonDeserializerFactory(serializer));

        serializer.initialize(this);

        return serializer;
    }

    public Serializer SOAPElementLiteralSerializer(
        QName xmlType,
        Class javaType) {

        CombinedSerializer serializer =
            new LiteralFragmentSerializer(xmlType, NOT_NULLABLE, "");
        cachedLiteralMappings.register(
            javaType,
            xmlType,
            new SingletonSerializerFactory(serializer),
            new SingletonDeserializerFactory(serializer));
        return serializer;
    }

    public Serializer getSerializer(String encoding, Class javaType)
        throws Exception {

        return registry.getSerializer(encoding, javaType);
    }

    public Serializer getSerializer(String encoding, QName xmlType)
        throws Exception {

        return registry.getSerializer(encoding, xmlType);
    }

    public Deserializer getDeserializer(String encoding, QName xmlType)
        throws Exception {

        return registry.getDeserializer(encoding, xmlType);
    }

    public Class getJavaType(String encoding, QName xmlType) throws Exception {
        return registry.getJavaType(encoding, xmlType);
    }

    public QName getXmlType(String encoding, Class javaType) throws Exception {
        return registry.getXmlType(encoding, javaType);
    }

    // Static methods
    public static boolean isArray(Class javaType, QName xmlType) {
        return isArray(javaType, xmlType, SOAPVersion.SOAP_11);
    }

    // Static methods
    public static boolean isLiteralArray(Class javaType, QName xmlType) {
        return isLiteralArray(javaType, xmlType, SOAPVersion.SOAP_11);
    }

    public static boolean isArray(
        Class javaType,
        QName xmlType,
        SOAPVersion ver) {

        if (javaType == null)
            return false;

        QName encArray =
            com.sun.xml.rpc.encoding.soap.SOAPConstants.QNAME_ENCODING_ARRAY;

        if (ver == SOAPVersion.SOAP_12)
            encArray =
                com
                .sun
                .xml
                .rpc
                .encoding
                .soap
                .SOAP12Constants
                .QNAME_ENCODING_ARRAY;
        boolean isArray = javaType.isArray();
        boolean isSame = false;
        if (xmlType != null) {
            if ((xmlType.getLocalPart().indexOf(encArray.getLocalPart()))
                != -1)
                isSame = true;
            return isArray && isSame;
        } else
            return isArray;
    }

    public static boolean isLiteralArray(
        Class javaType,
        QName xmlType,
        SOAPVersion ver) {

        if (javaType == null)
            return false;
        return javaType.isArray();
    }

    public static boolean isValueType(Class javaType) throws Exception {
        if (javaType == null
            || java.rmi.Remote.class.isAssignableFrom(javaType)) {
            return false;
        }

        boolean hasPublicConstructor = false;
        Constructor[] constructors = javaType.getConstructors();
        for (int i = 0; i < constructors.length; ++i) {
            if (constructors[i].getParameterTypes().length == 0) {
                hasPublicConstructor = true;
                break;
            }
        }

        if (!hasPublicConstructor) {
            return false;
        }

        boolean hasPropertiesOrPublicFields = false;
        if (Introspector.getBeanInfo(javaType).getPropertyDescriptors().length
            == 0) {
            Field[] fields = javaType.getFields();
            for (int i = 0; i < fields.length; ++i) {
                final Field currentField = fields[i];

                int fieldModifiers = currentField.getModifiers();
                if (!Modifier.isPublic(fieldModifiers)) {
                    continue;
                }
                if (Modifier.isTransient(fieldModifiers)) {
                    continue;
                }
                if (Modifier.isFinal(fieldModifiers)) {
                    continue;
                }

                hasPropertiesOrPublicFields = true;
                break;
            }
        } else {
            hasPropertiesOrPublicFields = true;
        }

        if (!hasPropertiesOrPublicFields) {
            return false;
        }

        return true;
    }

    private Collection getCurrentOperationParameterModels() {
        if (this.currentCall != null) {
            OperationInfo info = this.currentCall.getOperationInfo();
            if (info != null)
                return info.getParameterModels();
        }
        return new ArrayList();
    }

    //returns xmlType from the model
    private QName checkParameterXmlTypesUsingModels(QName suppliedXmlType) {
        Collection parameterModels = getCurrentOperationParameterModels();
        if (parameterModels == null)
            return null;
        Iterator iter = parameterModels.iterator();
        return recursiveCheck(iter, suppliedXmlType);
    }

    private QName recursiveCheck(Iterator iter, QName suppliedQName) {

        while (iter.hasNext()) {
            LiteralElementMember type = (LiteralElementMember) iter.next();
            QName qname = type.getName();
            if (qname
                .getLocalPart()
                .equalsIgnoreCase(suppliedQName.getLocalPart()))
                return qname;

            //this is the problem
            LiteralType literalType = type.getType();
            qname = literalType.getName();
            if (qname
                .getLocalPart()
                .equalsIgnoreCase(suppliedQName.getLocalPart()))
                return qname;

            if ((literalType instanceof LiteralSequenceType)
                || (literalType instanceof LiteralAllType)) {
                Iterator members =
                    ((LiteralSequenceType) literalType).getElementMembers();
                return recursiveCheck(members, suppliedQName);
            } else
                break;
        }
        return null;
    }

    private Collection orderCurrentMembersPerWsdl(
        QName suppliedQName,
        Class javaType) {

        //should really use the visitor pattern
        if (javaType == null)
            return null;
        String className = javaType.getName();
        Collection names = new ArrayList();
        Collection parameterModels = getCurrentOperationParameterModels();
        if (parameterModels == null)
            return null;
        Iterator iter = parameterModels.iterator();
        while (iter.hasNext()) {
            Object model = iter.next();
            if (model instanceof LiteralElementMember) {
                QName qname = ((LiteralElementMember) model).getName();
                if (suppliedQName != null) {
                    if (!qname
                        .getLocalPart()
                        .equalsIgnoreCase(suppliedQName.getLocalPart()))
                        continue;
                }
                qnameToJavaClass.put(qname, className);
                //is this the model I am looking for
                LiteralType literalType =
                    ((LiteralElementMember) model).getType();
                if ((literalType instanceof LiteralSequenceType)
                    || (literalType instanceof LiteralAllType)) {
                    qname = literalType.getName();
                    //this is the valueType we are looking for
                    Iterator elements = null;
                    if (literalType instanceof LiteralSequenceType)
                        elements =
                            ((LiteralSequenceType) literalType)
                            .getElementMembers();
                    if (literalType instanceof LiteralAllType)
                        elements =
                            ((LiteralAllType) literalType).getElementMembers();
                    while (elements.hasNext()) {
                        LiteralElementMember member =
                            (LiteralElementMember) elements.next();
                        if (member.getType() instanceof LiteralSequenceType) {
                            //need to recurse here
                            JavaStructureMember structureMember =
                                member.getJavaStructureMember();
                            String javaName =
                                structureMember.getType().getName();
                            //the qname from this class is as above
                            qnameToJavaClass.put(qname, javaName);
                        }
                        String name = member.getName().getLocalPart();
                        names.add(name);
                    } //need to do a recursive check
                }
            }
        }
        return names;
    }

    LiteralType getParameterLiteralType(String parameterName) {
        Collection parameterModels = getCurrentOperationParameterModels();
        Iterator modelsIter = parameterModels.iterator();
        while (modelsIter.hasNext()) {
            Object model = modelsIter.next();
            if (model instanceof LiteralElementMember) {
                //what do I want to get
                QName name = ((LiteralElementMember) model).getName();
                if (name.getLocalPart().equalsIgnoreCase(parameterName)) {
                    //this is what I want
                    LiteralType type = ((LiteralElementMember) model).getType();
                    return type;
                }
                //need to recurse
            }
        }
        return null;
    }

    JAXRPCSerializer getStandardSerializer(
        String encoding,
        Class javaType,
        QName xmlType)
        throws TypeMappingException, Exception {

        Serializer serializer = null;
        try {
            return (JAXRPCSerializer) registry.getSerializer(
                encoding,
                javaType,
                xmlType);
        } catch (Exception e) {
            try {
                return (JAXRPCSerializer) registry.getSerializer(
                    encoding,
                    javaType);
            } catch (Exception ex) {
                //bad practice
                return null;
            }
        }
    }

    JAXRPCDeserializer getStandardDeserializer(
        String encoding,
        Class javaType,
        QName xmlType)
        throws TypeMappingException, Exception {

        Deserializer serializer = null;
        try {
            return (JAXRPCDeserializer) registry.getDeserializer(
                encoding,
                javaType,
                xmlType);
        } catch (Exception e) {
            try {
                return (JAXRPCDeserializer) registry.getSerializer(
                    encoding,
                    javaType);
            } catch (Exception ex) {
                //bad practice
                return null;
            }
        }
    }
}
