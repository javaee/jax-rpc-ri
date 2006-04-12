/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.ObjectSerializerBase;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

public final class JAXRpcMapEntrySerializer
    extends ObjectSerializerBase
    implements Initializable {
        
    private static final QName key_QNAME =
        InternalEncodingConstants.JAX_RPC_MAP_ENTRY_KEY_NAME;
    private static final QName anyType_TYPE_QNAME =
        SchemaConstants.QNAME_TYPE_URTYPE;
    private CombinedSerializer anyType_DynamicSerializer;
    private static final QName value_QNAME =
        InternalEncodingConstants.JAX_RPC_MAP_ENTRY_VALUE_NAME;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        this.soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public JAXRpcMapEntrySerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {
            
        this(type, encodeType, isNullable, encodingStyle, SOAPVersion.SOAP_11);
    }

    public JAXRpcMapEntrySerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        SOAPVersion ver) {
            
        super(type, encodeType, isNullable, encodingStyle);
        init(ver); // Initialize SOAP constants
    }

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {
            
        anyType_DynamicSerializer =
            (CombinedSerializer) registry.getSerializer(
                soapEncodingConstants.getSOAPEncodingNamespace(),
                java.lang.Object.class,
                anyType_TYPE_QNAME);
    }

    public Object doDeserialize(
        SOAPDeserializationState state,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {
            
        JAXRpcMapEntry instance = new JAXRpcMapEntry();
        JAXRpcMapEntryBuilder builder = null;
        Object member;
        boolean isComplete = true;
        QName elementName;

        reader.nextElementContent();
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(key_QNAME)) {
                member =
                    anyType_DynamicSerializer.deserialize(
                        key_QNAME,
                        reader,
                        context);
                if (member instanceof SOAPDeserializationState) {
                    if (builder == null) {
                        builder = new JAXRpcMapEntryBuilder();
                    }
                    state =
                        registerWithMemberState(
                            instance,
                            state,
                            member,
                            KEY_INDEX,
                            builder);
                    isComplete = false;
                } else {
                    instance.setKey((java.lang.Object) member);
                }
                reader.nextElementContent();
            }
        }
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(value_QNAME)) {
                member =
                    anyType_DynamicSerializer.deserialize(
                        value_QNAME,
                        reader,
                        context);
                if (member instanceof SOAPDeserializationState) {
                    if (builder == null) {
                        builder = new JAXRpcMapEntryBuilder();
                    }
                    state =
                        registerWithMemberState(
                            instance,
                            state,
                            member,
                            VALUE_INDEX,
                            builder);
                    isComplete = false;
                } else {
                    instance.setValue((java.lang.Object) member);
                }
                reader.nextElementContent();
            }
        }

        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (isComplete ? (Object) instance : (Object) state);
    }

    public void doSerializeInstance(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {
            
        JAXRpcMapEntry instance = (JAXRpcMapEntry) obj;

        anyType_DynamicSerializer.serialize(
            instance.getKey(),
            key_QNAME,
            null,
            writer,
            context);
        anyType_DynamicSerializer.serialize(
            instance.getValue(),
            value_QNAME,
            null,
            writer,
            context);
    }

    private class JAXRpcMapEntryBuilder implements SOAPInstanceBuilder {
        private JAXRpcMapEntry instance;
        private java.lang.Object key;
        private java.lang.Object value;
        private static final int KEY_INDEX = 0;
        private static final int VALUE_INDEX = 1;

        public int memberGateType(int memberIndex) {
            switch (memberIndex) {
                case KEY_INDEX :
                    return GATES_INITIALIZATION + REQUIRES_CREATION;
                case VALUE_INDEX :
                    return GATES_INITIALIZATION + REQUIRES_CREATION;
                default :
                    throw new IllegalArgumentException();
            }
        }

        public void construct() {
        }

        public void setMember(int index, Object memberValue) {
            switch (index) {
                case KEY_INDEX :
                    instance.setKey((java.lang.Object) memberValue);
                    break;
                case VALUE_INDEX :
                    instance.setValue((java.lang.Object) memberValue);
                    break;
                default :
                    throw new IllegalArgumentException();
            }
        }

        public void initialize() {
        }

        public void setInstance(Object instance) {
            this.instance = (JAXRpcMapEntry) instance;
        }

        public Object getInstance() {
            return instance;
        }
    }

}
