/*
 * $Id: SOAPResponseSerializer.java,v 1.1 2006-04-12 20:34:06 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.soap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializerBase;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * A data-driven (de)serializer for the first element of a SOAP Body element (either a request or response structure).
 *
 * @author JAX-RPC Development Team
 */

public class SOAPResponseSerializer
    extends SOAPRequestSerializer
    implements Initializable {
        
    private static final QName RETURN_VALUE_QNAME = new QName("return");
    private static final QName[] EMPTY_QNAME_ARRAY = new QName[0];
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    protected QName returnXmlType;
    protected Class returnJavaType;

    protected JAXRPCSerializer returnSerializer;
    protected JAXRPCDeserializer returnDeserializer;
    protected boolean isReturnVoid;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        this.soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public SOAPResponseSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        QName[] parameterNames,
        QName[] parameterXmlTypes,
        Class[] parameterClasses,
        QName returnXmlType,
        Class returnJavaType) {
            
        this(
            type,
            encodeType,
            isNullable,
            encodingStyle,
            parameterNames,
            parameterXmlTypes,
            parameterClasses,
            returnXmlType,
            returnJavaType,
            SOAPVersion.SOAP_11);
    }

    public SOAPResponseSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        QName[] parameterNames,
        QName[] parameterXmlTypes,
        Class[] parameterClasses,
        QName returnXmlType,
        Class returnJavaType,
        SOAPVersion ver) {
            
        super(
            type,
            encodeType,
            isNullable,
            encodingStyle,
            parameterNames,
            parameterXmlTypes,
            parameterClasses);
        init(ver);
        this.isReturnVoid = returnJavaType == null && returnXmlType == null;
        this.returnXmlType = returnXmlType;
        this.returnJavaType = returnJavaType;
    }

    public SOAPResponseSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        QName returnXmlType,
        Class returnJavaType) {
            
        this(
            type,
            encodeType,
            isNullable,
            encodingStyle,
            returnXmlType,
            returnJavaType,
            SOAPVersion.SOAP_11);
    }

    public SOAPResponseSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        QName returnXmlType,
        Class returnJavaType,
        SOAPVersion ver) {
            
        this(
            type,
            encodeType,
            isNullable,
            encodingStyle,
            EMPTY_QNAME_ARRAY,
            EMPTY_QNAME_ARRAY,
            EMPTY_CLASS_ARRAY,
            returnXmlType,
            returnJavaType,
            ver);
    }

    public SOAPResponseSerializer(
        QName type,
        QName[] parameterNames,
        QName[] parameterXmlTypes,
        Class[] parameterClasses,
        QName returnXmlType,
        Class returnJavaType) {
            
        this(
            type,
            parameterNames,
            parameterXmlTypes,
            parameterClasses,
            returnXmlType,
            returnJavaType,
            SOAPVersion.SOAP_11);
    }

    public SOAPResponseSerializer(
        QName type,
        QName[] parameterNames,
        QName[] parameterXmlTypes,
        Class[] parameterClasses,
        QName returnXmlType,
        Class returnJavaType,
        SOAPVersion ver) {
            
        this(
            type,
            DONT_ENCODE_TYPE,
            NULLABLE,
            getURIEncoding(ver),
            parameterNames,
            parameterXmlTypes,
            parameterClasses,
            returnXmlType,
            returnJavaType,
            ver);

    }

    public SOAPResponseSerializer(
        QName type,
        QName returnXmlType,
        Class returnJavaType) {
            
        this(type, returnXmlType, returnJavaType, SOAPVersion.SOAP_11);
    }

    public SOAPResponseSerializer(
        QName type,
        QName returnXmlType,
        Class returnJavaType,
        SOAPVersion ver) {
            
        this(
            type,
            EMPTY_QNAME_ARRAY,
            EMPTY_QNAME_ARRAY,
            EMPTY_CLASS_ARRAY,
            returnXmlType,
            returnJavaType,
            ver);
    }

    private static String getURIEncoding(SOAPVersion ver) {
        if (ver == SOAPVersion.SOAP_11)
            return com.sun.xml.rpc.encoding.soap.SOAPConstants.URI_ENCODING;
        else if (ver == SOAPVersion.SOAP_11)
            return com.sun.xml.rpc.encoding.soap.SOAPConstants.URI_ENCODING;
        return null;
    }

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {
            
        if (typeRegistry != null) {
            return;
        }

        super.initialize(registry);

        if (returnJavaType != null && returnXmlType != null) {
            returnSerializer =
                (JAXRPCSerializer) registry.getSerializer(
                    encodingStyle,
                    returnJavaType,
                    returnXmlType);
            returnDeserializer =
                (JAXRPCDeserializer) registry.getDeserializer(
                    encodingStyle,
                    returnJavaType,
                    returnXmlType);
        }
    }

    protected void doSerializeInstance(
        Object instance,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        SOAPResponseStructure response = (SOAPResponseStructure) instance;
        getReturnSerializer(response.returnValue).serialize(
            response.returnValue,
            RETURN_VALUE_QNAME,
            null,
            writer,
            context);

        for (int i = 0; i < parameterXmlTypes.length; ++i) {
            QName parameterName = parameterNames[i];
            Object parameter = response.outParameters.get(parameterName);
            getParameterSerializer(i, parameter).serialize(
                parameter,
                parameterName,
                null,
                writer,
                context);
        }
    }

    protected Object doDeserialize(
        SOAPDeserializationState existingState,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {

        SOAPResponseStructure instance = new SOAPResponseStructure();
        Object returnedObject;
        SOAPResponseStructureBuilder builder = null;
        boolean isComplete = true;
        SOAPDeserializationState state = existingState;

        reader.nextElementContent(); // reading response struct
        int responseMemberIndex = 0;
        // TODO: For SOAP 1.2 check the name of the return element

        if (!isReturnVoid) {
            returnedObject =
                getReturnDeserializer(reader).deserialize(
                    null,
                    reader,
                    context);
            if (returnedObject instanceof SOAPDeserializationState) {
                if (builder == null) {
                    builder = new SOAPResponseStructureBuilder(instance);
                }
                state =
                    registerWithMemberState(
                        instance,
                        state,
                        returnedObject,
                        responseMemberIndex,
                        builder);
                isComplete = false;
            } else {
                instance.returnValue = returnedObject;
            }

            if (parameterXmlTypes.length > 0) {
                reader.nextElementContent();
            }
        }

        for (int i = 0; i < parameterXmlTypes.length; ++i) {
            QName parameterName = parameterNames[i];

            if (reader.getName().equals(parameterName)) {
                returnedObject =
                    getParameterDeserializer(i, reader).deserialize(
                        parameterName,
                        reader,
                        context);
                if (returnedObject instanceof SOAPDeserializationState) {
                    if (builder == null) {
                        builder = new SOAPResponseStructureBuilder(instance);
                    }
                    responseMemberIndex = i + 1;
                    builder.setOutParameterName(
                        responseMemberIndex,
                        parameterName);
                    state =
                        registerWithMemberState(
                            instance,
                            state,
                            returnedObject,
                            responseMemberIndex,
                            builder);
                    isComplete = false;
                } else {
                    instance.outParameters.put(parameterName, returnedObject);
                    //bug fix 4833378
                }
                instance.outParametersStringKeys.put(
                    parameterName.getLocalPart(),
                    returnedObject);
            } else {
                throw new DeserializationException(
                    "soap.unexpectedElementName",
                    new Object[] { parameterName, reader.getName()});
            }
        }

        reader.nextElementContent();
        return (isComplete ? (Object) instance : (Object) state);
    }

    protected JAXRPCSerializer getReturnSerializer(Object returnValue)
        throws Exception {
            
        JAXRPCSerializer serializer = returnSerializer;
        if (serializer == null) {
            serializer =
                (JAXRPCSerializer) typeRegistry.getSerializer(
                    encodingStyle,
                    returnValue.getClass(),
                    returnXmlType);
        }
        return serializer;
    }

    protected JAXRPCDeserializer getReturnDeserializer(XMLReader reader)
        throws Exception {
            
        JAXRPCDeserializer deserializer = returnDeserializer;
        if (deserializer == null) {
            QName xmlType =
                returnXmlType != null
                    ? returnXmlType
                    : SerializerBase.getType(reader);
            deserializer =
                (JAXRPCDeserializer) typeRegistry.getDeserializer(
                    encodingStyle,
                    returnJavaType,
                    xmlType);
        }
        return deserializer;
    }

    protected static class SOAPResponseStructureBuilder
        implements SOAPInstanceBuilder {

        SOAPResponseStructure instance = null;
        List outParameterNames = new ArrayList();

        public void setOutParameterName(int index, QName name) {
            outParameterNames.set(index, name);
        }

        SOAPResponseStructureBuilder(SOAPResponseStructure instance) {
            this.instance = instance;
        }

        public int memberGateType(int memberIndex) {
            return (
                SOAPInstanceBuilder.GATES_INITIALIZATION
                    | SOAPInstanceBuilder.REQUIRES_CREATION);
        }

        public void construct() {
            return;
        }

        public void setMember(int index, Object memberValue) {
            try {
                if (index == 0) {
                    instance.returnValue = memberValue;
                } else {
                    instance.outParameters.put(
                        outParameterNames.get(index),
                        memberValue);
                    //bug fix 4833378
                    instance.outParametersStringKeys.put(
                        ((QName) outParameterNames.get(index)).getLocalPart(),
                        memberValue);
                }
            } catch (Exception e) {
                throw new DeserializationException(
                    "nestedSerializationError",
                    new LocalizableExceptionAdapter(e));
            }
        }

        public void initialize() {
            return;
        }

        public void setInstance(Object instance) {
            instance = (SOAPResponseStructure) instance;
        }

        public Object getInstance() {
            return instance;
        }
    }
}
