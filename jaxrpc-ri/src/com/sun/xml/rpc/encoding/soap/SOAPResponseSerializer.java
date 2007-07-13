/*
 * $Id: SOAPResponseSerializer.java,v 1.3 2007-07-13 23:36:00 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
