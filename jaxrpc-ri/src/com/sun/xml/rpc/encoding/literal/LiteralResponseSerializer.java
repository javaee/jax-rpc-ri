/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

/*
 * $Id: LiteralResponseSerializer.java,v 1.3 2007-07-13 23:35:58 ofung Exp $
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

package com.sun.xml.rpc.encoding.literal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.client.dii.ParameterMemberInfo;
import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.DynamicInternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SerializerBase;
import com.sun.xml.rpc.encoding.soap.SOAPResponseStructure;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 * A data-driven (de)serializer for the first element of a SOAP Body element (either a request or response structure).
 *
 * @author JAX-RPC RI Development Team
 */

public class LiteralResponseSerializer extends LiteralRequestSerializer implements Initializable {
    private static final QName RETURN_VALUE_QNAME = new QName("return");
    private static final QName[] EMPTY_QNAME_ARRAY = new QName[0];
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    protected QName returnXmlType;
    protected QName returnXmlTypeQName;
    protected Class returnJavaType;
    protected ParameterMemberInfo[] returnMemberInfo;

    protected JAXRPCSerializer returnSerializer;
    protected JAXRPCDeserializer returnDeserializer;
    protected boolean isReturnVoid;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants = null;

    //todo: take out unneeded constructors
    private void init(SOAPVersion ver) {
        this.soapEncodingConstants = SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public LiteralResponseSerializer(QName type, boolean isNullable, String encodingStyle) {
        super(type, isNullable, encodingStyle);
    }

    public LiteralResponseSerializer(QName type, boolean isNullable, String encodingStyle, boolean encodeType) {
        super(type, isNullable, encodingStyle, encodeType);
    }

    public LiteralResponseSerializer(QName type, boolean encodeType, boolean isNullable,
                                     String encodingStyle, String operationStyle,
                                     QName[] parameterNames, QName[] parameterXmlTypes,
                                     QName[] parameterXmlTypeQNames, Class[] parameterClasses,
                                     ArrayList parameterMembers,
                                     QName returnXmlType, QName returnXmlTypeQName,
                                     Class returnJavaType, ParameterMemberInfo[] returnMembers) {
        this(type, encodeType, isNullable, encodingStyle, operationStyle,
             parameterNames, parameterXmlTypes, parameterXmlTypeQNames,
             parameterClasses, parameterMembers,
             returnXmlType, returnXmlTypeQName,
             returnJavaType, returnMembers, SOAPVersion.SOAP_11);
    }

    public LiteralResponseSerializer(QName type, boolean encodeType, boolean isNullable,
                                     String encodingStyle, String operationStyle,
                                     QName[] parameterNames, QName[] parameterXmlTypes,
                                     QName[] parameterXmlTypeQNames,
                                     Class[] parameterClasses, ArrayList parameterMembers,
                                     QName returnXmlType, QName returnXmlTypeQName, Class returnJavaType,
                                     ParameterMemberInfo[] returnMembers, SOAPVersion ver) {

        super(type, encodeType, isNullable, "", operationStyle, parameterNames,
              parameterXmlTypes, parameterXmlTypeQNames, parameterClasses,
              parameterMembers, SOAPVersion.SOAP_11);

        init(ver);
        this.isReturnVoid = returnJavaType == null && returnXmlType == null;
        this.returnXmlType = returnXmlType;
        this.returnJavaType = returnJavaType;
        this.returnXmlTypeQName = returnXmlTypeQName;
        this.returnMemberInfo = returnMembers;


    }

    public LiteralResponseSerializer(QName type, boolean encodeType, boolean isNullable,
                                     String encodingStyle, String operationStyle,
                                     QName[] parameterNames, QName[] parameterXmlTypes,
                                     Class[] parameterClasses, ArrayList parameterMembers,
                                     QName returnXmlType, Class returnJavaType, ParameterMemberInfo[] returnMembers) {
        this(type, encodeType, isNullable, encodingStyle, operationStyle,
             parameterNames, parameterXmlTypes, parameterClasses, parameterMembers,
             returnXmlType, returnJavaType, returnMembers, SOAPVersion.SOAP_11);
    }

    public LiteralResponseSerializer(QName type, boolean encodeType, boolean isNullable,
                                     String encodingStyle, String operationStyle,
                                     QName[] parameterNames, QName[] parameterXmlTypes,
                                     Class[] parameterClasses, ArrayList parameterMembers,
                                     QName returnXmlType, Class returnJavaType,
                                     ParameterMemberInfo[] returnMembers, SOAPVersion ver) {

        super(type, encodeType, isNullable, "", operationStyle, parameterNames,
              parameterXmlTypes, parameterClasses, parameterMembers, SOAPVersion.SOAP_11);

        init(ver);
        this.isReturnVoid = returnJavaType == null && returnXmlType == null;
        this.returnXmlType = returnXmlType;
        this.returnJavaType = returnJavaType;
    }

    public LiteralResponseSerializer(QName type, boolean encodeType, boolean isNullable,
                                     String encodingStyle,
                                     QName[] parameterNames, QName[] parameterXmlTypes, Class[] parameterClasses,
                                     QName returnXmlType, Class returnJavaType) {
        this(type, encodeType, isNullable, encodingStyle,
             parameterNames, parameterXmlTypes, parameterClasses,
             returnXmlType, returnJavaType, SOAPVersion.SOAP_11);
    }

    public LiteralResponseSerializer(QName type, boolean encodeType, boolean isNullable,
                                     String encodingStyle,
                                     QName[] parameterNames, QName[] parameterXmlTypes, Class[] parameterClasses,
                                     QName returnXmlType, Class returnJavaType, SOAPVersion ver) {
        super(type, encodeType, isNullable, "", parameterNames,
              parameterXmlTypes, parameterClasses, SOAPVersion.SOAP_11);

        init(ver);
        this.isReturnVoid = returnJavaType == null && returnXmlType == null;
        this.returnXmlType = returnXmlType;
        this.returnJavaType = returnJavaType;
    }

    public LiteralResponseSerializer(QName type, boolean encodeType, boolean isNullable,
                                     String encodingStyle,
                                     QName returnXmlType, Class returnJavaType) {

        this(type, encodeType, isNullable, encodingStyle,
             returnXmlType, returnJavaType, SOAPVersion.SOAP_11);
    }

    public LiteralResponseSerializer(QName type, boolean encodeType, boolean isNullable,
                                     String encodingStyle,
                                     QName returnXmlType, Class returnJavaType, SOAPVersion ver) {
        this(type, encodeType, isNullable, encodingStyle,
             EMPTY_QNAME_ARRAY, EMPTY_QNAME_ARRAY, EMPTY_CLASS_ARRAY, returnXmlType, returnJavaType, ver);
    }

    public LiteralResponseSerializer(QName type,
                                     QName[] parameterNames, QName[] parameterXmlTypes, Class[] parameterClasses,
                                     QName returnXmlType, Class returnJavaType) {
        this(type, parameterNames, parameterXmlTypes, parameterClasses, returnXmlType, returnJavaType, SOAPVersion.SOAP_11);
    }

    public LiteralResponseSerializer(QName type,
                                     QName[] parameterNames, QName[] parameterXmlTypes, Class[] parameterClasses,
                                     QName returnXmlType, Class returnJavaType, SOAPVersion ver) {
        this(type, DONT_ENCODE_TYPE, NULLABLE, getURIEncoding(ver),
             parameterNames, parameterXmlTypes, parameterClasses, returnXmlType, returnJavaType, ver);

    }

    public LiteralResponseSerializer(QName type, QName returnXmlType, Class returnJavaType) {
        this(type, returnXmlType, returnJavaType, SOAPVersion.SOAP_11);
    }

    public LiteralResponseSerializer(QName type, QName returnXmlType, Class returnJavaType, SOAPVersion ver) {
        this(type, EMPTY_QNAME_ARRAY, EMPTY_QNAME_ARRAY, EMPTY_CLASS_ARRAY, returnXmlType, returnJavaType, ver);
    }


    private static String getURIEncoding(SOAPVersion ver) {
        if (ver == SOAPVersion.SOAP_11)
            return com.sun.xml.rpc.encoding.soap.SOAPConstants.URI_ENCODING;
        else if (ver == SOAPVersion.SOAP_11)
            return com.sun.xml.rpc.encoding.soap.SOAPConstants.URI_ENCODING;
        return null;
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        if (typeRegistry != null) {
            return;
        }


        ((DynamicInternalTypeMappingRegistry) //?encoding
            registry).addDynamicRegistryMembers(returnJavaType,
                                                returnXmlType,
                                                "", returnMemberInfo);

        /* ((DynamicInternalTypeMappingRegistry) //?encoding
             registry).addDynamicRegistryMembers(returnJavaType,
                                                 returnXmlTypeQName,
                                                 "", returnMemberInfo);
        */
        super.initialize(registry);
        //todo:not sure this should remain here or whether this should be consolidated with
        //getParamSerializer or getReturnSerializer()
        if (isRPCLiteral()) {
            if (returnJavaType != null && returnXmlType != null) {
                returnSerializer = (JAXRPCSerializer)
                    registry.getSerializer(encodingStyle, returnJavaType, returnXmlType);
                returnDeserializer = (JAXRPCDeserializer)
                    registry.getDeserializer(encodingStyle, returnJavaType, returnXmlType);
            } else if (returnXmlType != null) {
                returnSerializer = (JAXRPCSerializer)
                    registry.getSerializer(encodingStyle, null, returnXmlType);
                returnDeserializer = (JAXRPCDeserializer)
                    registry.getDeserializer(encodingStyle, null, returnXmlType);

            }
        } else {
            if (returnJavaType != null && returnXmlTypeQName != null) {

                if (DynamicInternalTypeMappingRegistry.isLiteralArray(returnJavaType, null, null)
                    || DynamicInternalTypeMappingRegistry.isValueType(returnJavaType)) {
                    returnSerializer = (JAXRPCSerializer)
                        registry.getSerializer(encodingStyle, returnJavaType, returnXmlType);
                    returnDeserializer = (JAXRPCDeserializer)
                        registry.getDeserializer(encodingStyle, returnJavaType, returnXmlType);
                } else {
                    returnSerializer = (JAXRPCSerializer)
                        registry.getSerializer(encodingStyle, returnJavaType, returnXmlTypeQName);
                    returnDeserializer = (JAXRPCDeserializer)
                        registry.getDeserializer(encodingStyle, returnJavaType, returnXmlTypeQName);
                }

            }
        }
    }

    protected Object doDeserialize(XMLReader reader, SOAPDeserializationContext context)
        throws Exception {

        SOAPResponseStructure instance = new SOAPResponseStructure();
        Object returnedObject;
        SOAPResponseStructureBuilder builder = null;
        boolean isComplete = true;

        if (isRPCLiteral())
            reader.nextElementContent(); // reading response struct
        int responseMemberIndex = 0;
        // TODO: For SOAP 1.2 check the name of the return element

        if (!isReturnVoid) {
            returnedObject = getReturnDeserializer(reader).deserialize(null, reader, context);
            if (returnedObject instanceof SOAPDeserializationState) {
                if (builder == null) {
                    builder = new SOAPResponseStructureBuilder(instance);
                }

                isComplete = false;
            } else {
                instance.returnValue = returnedObject;
            }

            if ((parameterXmlTypes != null) && (parameterXmlTypes.length > 0)) {
                //just rpclit
                //if (isRPCLiteral())
                //reader.nextElementContent();
            }
        }

        if (parameterXmlTypes != null) {
            for (int i = 0; i < parameterXmlTypes.length; ++i) {
                QName parameterName = parameterNames[i];
                //Todo: Where does this fit in ? holders?
                if (reader.getName().equals(parameterName)) {
                    returnedObject = getParameterDeserializer(i, reader).deserialize(parameterName,
                                                                                     reader, context);
                    if (returnedObject instanceof SOAPDeserializationState) {
                        if (builder == null) {
                            builder = new SOAPResponseStructureBuilder(instance);
                        }
                        responseMemberIndex = i + 1;
                        builder.setOutParameterName(responseMemberIndex, parameterName);

                        isComplete = false;
                    } else {
                        instance.outParameters.put(parameterName, returnedObject);
                        //bug fix 4833378
                        instance.outParametersStringKeys.put(parameterName.getLocalPart(), returnedObject);
                    }

                } else {
                    throw new DeserializationException("soap.unexpectedElementName", new Object[]{parameterName, reader.getName()});
                }
                //todo: does this work with both rpclit and doclit-kw
                XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
                //maybe just rpclit
                //reader.nextElementContent();
            }
        }
        if (isRPCLiteral())
            reader.nextElementContent();
        return instance;
    }

    public Object deserialize(QName name, XMLReader reader,
                              SOAPDeserializationContext context) {

        try {
            return internalDeserialize(name, reader, context);
        } catch (DeserializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        } catch (Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }


    protected Object internalDeserialize(QName name, XMLReader reader,
                                         SOAPDeserializationContext context)
        throws Exception {

        boolean pushedEncodingStyle = context.processEncodingStyle(reader);
        try {
            context.verifyEncodingStyle(encodingStyle);

            if (name != null) {
                QName actualName = reader.getName();
                if (!actualName.equals(name)) {
                    throw new DeserializationException("xsd.unexpectedElementName",
                                                       new Object[]{
                                                           name.toString(),
                                                           actualName.toString()
                                                       });
                }
            }

            verifyType(reader);

            Attributes attrs = reader.getAttributes();
            String nullVal = attrs.getValue(XSDConstants.URI_XSI, "nil");
            boolean isNull = (nullVal != null && SerializerBase.decodeBoolean(nullVal));
            Object obj = null;

            if (isNull) {
                if (!isNullable) {
                    throw new DeserializationException("xsd.unexpectedNull");
                }
                reader.next();
            } else {
                obj = doDeserialize(reader, context);
            }

            XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
            return obj;
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }

    }

    protected JAXRPCSerializer getReturnSerializer(Object returnValue) throws Exception {
        JAXRPCSerializer serializer = returnSerializer;
        if (serializer == null) {
            serializer = (JAXRPCSerializer)
                typeRegistry.getSerializer(encodingStyle, returnValue.getClass(), returnXmlType);
        }
        return serializer;
    }

    protected JAXRPCDeserializer getReturnDeserializer(XMLReader reader) throws Exception {
        JAXRPCDeserializer deserializer = returnDeserializer;
        if (deserializer == null) {
            QName xmlType = null;
            if (isRPCLiteral())
                xmlType = returnXmlType != null ? returnXmlType : SerializerBase.getType(reader);
            else {
                if (!"".equals(encodingStyle)) {
                    if (returnJavaType != null)
                        xmlType = new QName(SchemaConstants.NS_XSD, returnJavaType.getName());
                    else
                        xmlType = returnXmlType != null ? returnXmlType : SerializerBase.getType(reader);
                } else {

                    xmlType = returnXmlTypeQName != null ? returnXmlType : SerializerBase.getType(reader);

                }

            }
            if (DynamicInternalTypeMappingRegistry.isLiteralArray(returnJavaType, null, null)
                || DynamicInternalTypeMappingRegistry.isValueType(returnJavaType)) {
                deserializer = (JAXRPCDeserializer) typeRegistry.getDeserializer(encodingStyle,
                                                                                 returnJavaType, returnXmlType);
            } else {
                deserializer = (JAXRPCDeserializer) typeRegistry.getDeserializer(encodingStyle,
                                                                                 returnJavaType, returnXmlTypeQName);
            }

        }
        return deserializer;
    }

    //todo: take this out
    protected static class SOAPResponseStructureBuilder implements SOAPInstanceBuilder {

        SOAPResponseStructure instance = null;
        List outParameterNames = new ArrayList();

        public void setOutParameterName(int index, QName name) {
            outParameterNames.set(index, name);
        }

        SOAPResponseStructureBuilder(SOAPResponseStructure instance) {
            this.instance = instance;
        }

        public int memberGateType(int memberIndex) {
            return (SOAPInstanceBuilder.GATES_INITIALIZATION |
                SOAPInstanceBuilder.REQUIRES_CREATION);
        }

        public void construct() {
            return;
        }

        public void setMember(int index, Object memberValue) {
            try {
                if (index == 0) {
                    instance.returnValue = memberValue;
                } else {
                    instance.outParameters.put(outParameterNames.get(index), memberValue);
                    //bug fix 4833378
                    instance.outParametersStringKeys.put(((QName) outParameterNames.get(index)).getLocalPart(), memberValue);
                }
            } catch (Exception e) {
                throw new DeserializationException("nestedSerializationError", new LocalizableExceptionAdapter(e));
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
