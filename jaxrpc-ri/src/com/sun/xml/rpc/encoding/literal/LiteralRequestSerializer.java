/*
 * $Id: LiteralRequestSerializer.java,v 1.3 2007-07-13 23:35:58 ofung Exp $
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

import javax.xml.namespace.QName;

import com.sun.xml.rpc.client.dii.ParameterMemberInfo;
import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.DynamicInternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.EncodingException;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializationException;
import com.sun.xml.rpc.encoding.SerializerBase;
import com.sun.xml.rpc.encoding.SerializerCallback;
import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDListTypeEncoder;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;


/**
 * A data-driven (de)serializer for a request structure.
 *
 * @author JAX-RPC Development Team
 */

public class LiteralRequestSerializer
    extends GenericLiteralObjectSerializer
    implements Initializable {

    protected QName[] parameterNames;
    protected QName[] parameterXmlTypes;
    protected QName[] parameterXmlTypeQNames;
    protected Class[] parameterJavaTypes;
    protected ArrayList parameterMembers;
    protected String operationStyle = "document"; //default is document lit

    protected JAXRPCSerializer[] serializers;
    protected JAXRPCDeserializer[] deserializers;

    protected InternalTypeMappingRegistry typeRegistry = null;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        this.soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    //these two not found - need to check
    public LiteralRequestSerializer(QName type, boolean encodeType, boolean isNullable,
                                    String encodingStyle, String operationStyle,
                                    QName[] parameterNames, QName[] parameterTypes,
                                    Class[] parameterClasses, ArrayList parameterMembers) {
        this(type, encodeType, isNullable, "", operationStyle, parameterNames,
             parameterTypes, parameterClasses, parameterMembers, SOAPVersion.SOAP_11);
    }

    public LiteralRequestSerializer(QName type, boolean encodeType, boolean isNullable,
                                    String encodingStyle, String operationStyle, QName[] parameterNames,
                                    QName[] parameterTypes, Class[] parameterClasses, ArrayList parameterMembers,
                                    SOAPVersion ver) {

        super(type, encodeType, isNullable, encodingStyle);
        init(ver);
        this.parameterNames = parameterNames;
        this.parameterXmlTypes = parameterTypes;
        this.parameterJavaTypes = parameterClasses;
        this.parameterMembers = parameterMembers;
        this.operationStyle = operationStyle;
    }

    public LiteralRequestSerializer(QName type, boolean encodeType, boolean isNullable,
                                    String encodingStyle, String operationStyle,
                                    QName[] parameterNames, QName[] parameterTypes,
                                    QName[] parameterXmlTypeQNames, Class[] parameterClasses,
                                    ArrayList parameterMembers) {
        this(type, encodeType, isNullable, "", operationStyle, parameterNames,
             parameterTypes, parameterXmlTypeQNames, parameterClasses, parameterMembers, SOAPVersion.SOAP_11);
    }

    public LiteralRequestSerializer(QName type, boolean encodeType, boolean isNullable,
                                    String encodingStyle, String operationStyle, QName[] parameterNames,
                                    QName[] parameterTypes, QName[] parameterXmlTypeQNames, Class[] parameterClasses, ArrayList parameterMembers,
                                    SOAPVersion ver) {

        super(type, encodeType, isNullable, encodingStyle);
        init(ver);
        this.parameterNames = parameterNames;
        this.parameterXmlTypes = parameterTypes;
        this.parameterXmlTypeQNames = parameterXmlTypeQNames;
        this.parameterJavaTypes = parameterClasses;
        this.parameterMembers = parameterMembers;
        this.operationStyle = operationStyle;
    }

    //todo: get rid of constructors not used
    public LiteralRequestSerializer(QName type, boolean encodeType, boolean isNullable,
                                    String encodingStyle,
                                    QName[] parameterNames, QName[] parameterTypes, Class[] parameterClasses) {
        this(type, encodeType, isNullable, "", parameterNames,
             parameterTypes, parameterClasses, SOAPVersion.SOAP_11);
    }

    public LiteralRequestSerializer(QName type, boolean encodeType, boolean isNullable,
                                    String encodingStyle, QName[] parameterNames,
                                    QName[] parameterTypes, Class[] parameterClasses, SOAPVersion ver) {

        super(type, encodeType, isNullable, encodingStyle);
        init(ver);
        this.parameterNames = parameterNames;
        this.parameterXmlTypes = parameterTypes;
        this.parameterJavaTypes = parameterClasses;

    }


    public LiteralRequestSerializer(QName type, QName[] parameterNames, QName[] parameterTypes,
                                    Class[] parameterClasses) {
        this(type, parameterNames, parameterTypes, parameterClasses, SOAPVersion.SOAP_11);

    }

    public LiteralRequestSerializer(QName type, QName[] parameterNames, QName[] parameterTypes,
                                    Class[] parameterClasses, SOAPVersion ver) {
        this(type, DONT_ENCODE_TYPE, NULLABLE, "",
             parameterNames, parameterTypes, parameterClasses);

    }

    public LiteralRequestSerializer(QName type, boolean isNullable, String encodingStyle) {
        super(type, isNullable, DONT_ENCODE_TYPE, encodingStyle);
    }

    public LiteralRequestSerializer(QName type, boolean isNullable, String encodingStyle, boolean encodeType) {
        super(type, isNullable, encodeType, encodingStyle);
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

        int membersSize = parameterMembers.size();
        if (operationStyle.equals("document")) {
            if (parameterXmlTypeQNames != null) {
                serializers = new JAXRPCSerializer[parameterXmlTypeQNames.length];
                deserializers = new JAXRPCDeserializer[parameterXmlTypeQNames.length];

                for (int i = 0; i < parameterXmlTypeQNames.length; ++i) {
                    if (parameterXmlTypeQNames[i] != null && parameterJavaTypes[i] != null) {

                        if (i < membersSize) {
                            ParameterMemberInfo[] thisMembers =
                                (ParameterMemberInfo[]) parameterMembers.get(i);
                            ((DynamicInternalTypeMappingRegistry) //?encoding
                                registry).addDynamicRegistryMembers(parameterJavaTypes[i],
                                                                    parameterXmlTypeQNames[i],
                                                                    "", thisMembers);
                            ((DynamicInternalTypeMappingRegistry) //?encoding
                                registry).addDynamicRegistryMembers(parameterJavaTypes[i],
                                                                    parameterXmlTypes[i],
                                                                    "", thisMembers);
                        }

                        if (DynamicInternalTypeMappingRegistry.isLiteralArray(parameterJavaTypes[i], null, null)
                            || DynamicInternalTypeMappingRegistry.isValueType(parameterJavaTypes[i])) {

                            serializers[i] = (JAXRPCSerializer)
                                registry.getSerializer("", parameterJavaTypes[i], parameterXmlTypes[i]);
                            deserializers[i] = (JAXRPCDeserializer)
                                registry.getDeserializer("", parameterJavaTypes[i], parameterXmlTypes[i]);
                        } else {
                            serializers[i] = (JAXRPCSerializer)
                                registry.getSerializer("", parameterJavaTypes[i], parameterXmlTypeQNames[i]);
                            deserializers[i] = (JAXRPCDeserializer)
                                registry.getDeserializer("", parameterJavaTypes[i], parameterXmlTypeQNames[i]);
                        }

                    } else {
                        serializers[i] = null;
                        deserializers[i] = null;
                    }
                }
            }
        } else if (operationStyle.equals("rpc")) {
            if (parameterXmlTypes != null) {
                serializers = new JAXRPCSerializer[parameterXmlTypes.length];
                deserializers = new JAXRPCDeserializer[parameterXmlTypes.length];

                for (int i = 0; i < parameterXmlTypes.length; ++i) {
                    if (parameterXmlTypes[i] != null && parameterJavaTypes[i] != null) {

                        if (i < membersSize) {
                            ParameterMemberInfo[] thisMembers =
                                (ParameterMemberInfo[]) parameterMembers.get(i);
                            ((DynamicInternalTypeMappingRegistry) //?encoding
                                registry).addDynamicRegistryMembers(parameterJavaTypes[i],
                                                                    parameterXmlTypes[i],
                                                                    "", thisMembers);
                        }
                        serializers[i] = (JAXRPCSerializer)
                            registry.getSerializer("", parameterJavaTypes[i], parameterXmlTypes[i]);
                        deserializers[i] = (JAXRPCDeserializer)
                            registry.getDeserializer("", parameterJavaTypes[i], parameterXmlTypes[i]);
                    } else {
                        serializers[i] = null;
                        deserializers[i] = null;
                    }
                }
            }
        }
        typeRegistry = registry;
    }

    public void serialize(
        Object value,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context) {

        try {

            if (isRPCLiteral()) //actually don't need this check as for doclit name should = null
                internalSerialize(value, name, writer, context);
            else //change this name to not null
                internalSerialize(value, null, writer, context);
        } catch (SerializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        } catch (Exception e) {
            throw new SerializationException(
                new LocalizableExceptionAdapter(e));
        }
    }

    protected void internalSerialize(
        Object obj,
        QName name,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        context.beginSerializing(obj);

        if (isRPCLiteral()) { //for rpc/literal only
            writer.startElement(name.getLocalPart(), name.getNamespaceURI());
        }
        boolean pushedEncodingStyle = false;
        if (encodingStyle != null)
            pushedEncodingStyle =
                context.pushEncodingStyle(encodingStyle, writer);

        if (encodeType) {
            String attrVal = XMLWriterUtil.encodeQName(writer, type);
            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_TYPE, attrVal);
        }
        if (obj == null) {
            if (!isNullable) {
                throw new SerializationException("literal.unexpectedNull");
            }

            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_NIL, "1");
        } else {
            writeAdditionalNamespaceDeclarations(obj, writer);
            doSerializeAttributes(obj, writer, context);
            doSerialize(obj, writer, context);
        }

        if (isRPCLiteral())
            writer.endElement();
        if (pushedEncodingStyle) {
            context.popEncodingStyle();
        }

        context.doneSerializing(obj);
    }

    protected void doSerializeInstance(
        Object instance,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        if (typeRegistry == null) {
            throw new EncodingException("initializable.not.initialized");
        }

        //ParameterMemberInfo[] memberInfos = ((DynamicInternalTypeMappingRegistry)
        //     (ParameterMemberInfo[])registry.getDynamicRegistryMembers()

        Object[] parameters = (Object[]) instance;

        //checkParameterListLength(parameters);
        for (int i = 0; i < parameters.length; ++i) {
            Object parameter = parameters[i];

            if (isRPCLiteral())
                getParameterSerializer(i, parameter).serialize(
                    parameter,
                    getParameterName(i),
                    null,
                    writer,
                    context);
            else {
                getParameterSerializer(i, parameter).serialize(
                    parameter,
                    null,
                    null,
                    writer,
                    context);
            }
        }
    }

    protected Object doDeserialize(
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {
        if (typeRegistry == null) {
            throw new EncodingException("initializable.not.initialized");
        }

        Object[] instance = new Object[parameterXmlTypes.length];
        Object parameter;
        LiteralRequestSerializer.ParameterArrayBuilder builder = null;
        boolean isComplete = true;

        for (int i = 0; i < parameterXmlTypes.length; ++i) {
            reader.nextElementContent();
            QName parameterName = getParameterName(i);

            if (reader.getName().equals(parameterName)) {
                parameter =
                    getParameterDeserializer(i, reader).deserialize(
                        parameterName,
                        reader,
                        context);
                if (parameter instanceof SOAPDeserializationState) {
                    if (builder == null) {
                        builder =
                            new LiteralRequestSerializer.ParameterArrayBuilder(
                                instance);
                    }
                    isComplete = false;
                } else {
                    instance[i] = parameter;
                }
            }
        }

        reader.nextElementContent();
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return instance;
    }

    protected void writeAdditionalNamespaceDeclarations(
        Object obj,
        XMLWriter writer)
        throws Exception {

    }

    protected void doSerialize(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        doSerializeInstance(obj, writer, context);
    }

    protected void doSerializeAttributes(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

    }

    protected JAXRPCSerializer getParameterSerializer(
        int index,
        Object parameter)
        throws Exception {
        //use the info that you have
        JAXRPCSerializer serializer = getSerializer(index);
        if (serializer == null) {
            Class parameterClass = null;
            //need to use java types here-
            if (parameter != null) {
                parameterClass = parameter.getClass();
            }
            //try with class only as well and with xmlType only as well
            serializer =
                (JAXRPCSerializer) typeRegistry.getSerializer(
                    "",
                    parameterClass,
                    getParameterXmlType(index));
            if (serializer == null) //try with class only
                serializer =
                    (JAXRPCSerializer) typeRegistry.getSerializer(
                        "",
                        parameterClass,
                        null);
            if (serializer == null)
            //actually shouldthrow no serializer register for xx.class
                return null;
        }

        if (!isRPCLiteral()) {
            if (serializer instanceof LiteralSimpleTypeSerializer) {
                //I believe this is the wrong incoder - could use QName of simpleType
                SimpleTypeEncoder encoder =
                    ((LiteralSimpleTypeSerializer) serializer).getEncoder();
                if (((LiteralSimpleTypeSerializer) serializer).getEncoder()
                    instanceof XSDListTypeEncoder) {
                    //do nothing to the serializer
                } else
                    serializer =
                        new LiteralSimpleTypeSerializer(
                            getParameterXmlType(index),
                            "",
                            encoder);
            }
        }
        if (serializer != null) {
            serializers[index] = serializer;
            deserializers[index] = (JAXRPCDeserializer) serializer;
        }
        return serializer;
    }

    protected JAXRPCDeserializer getParameterDeserializer(
        int index,
        XMLReader reader)
        throws Exception {

        JAXRPCDeserializer deserializer = getDeserializer(index);
        if (deserializer == null) {
            QName parameterXmlType =
                parameterXmlTypes[index] != null
                ? parameterXmlTypes[index]
                : SerializerBase.getType(reader);
            deserializer =
                (JAXRPCDeserializer) typeRegistry.getDeserializer(
                    "",
                    getParameterJavaType(index),
                    parameterXmlType);
        }
        return deserializer;
    }

    protected static class ParameterArrayBuilder
        implements SOAPInstanceBuilder {

        Object[] instance = null;

        ParameterArrayBuilder(Object[] instance) {
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
                instance[index] = memberValue;
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
            instance = (Object[]) instance;
        }

        public Object getInstance() {
            return instance;
        }
    }

    private Class getParameterJavaType(int index) {
        if (index < parameterJavaTypes.length) {
            return parameterJavaTypes[index];
        }
        return null;
    }

    private QName getParameterXmlType(int index) {
        if (index < parameterXmlTypes.length) {
            return parameterXmlTypes[index];
        }
        return null;
    }

    private QName getParameterName(int index) {
        if (index < parameterNames.length) {
            return parameterNames[index];
        }
        return null;
    }

    private JAXRPCDeserializer getDeserializer(int index) {
        if (index < deserializers.length) {
            return deserializers[index];
        }
        return null;
    }

    private JAXRPCSerializer getSerializer(int index) {
        if (index < serializers.length) {
            return serializers[index];
        }
        return null;
    }

    private void checkParameterListLength(Object[] parameters) {
        if (serializers == null)
            return;
        if (serializers.length > 0 && parameters.length != serializers.length) {
            String expectedParameters = "\n";
            String actualParameters = "\n";

            for (int i = 0; i < parameterNames.length; i++) {
                QName name = parameterNames[i];
                QName xmlType = parameterXmlTypes[i];
                expectedParameters += name + ":" + xmlType;

                if (i + 1 != parameterNames.length) {
                    expectedParameters += "\n";
                }
            }
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String javaType =
                    parameter == null ? "null" : parameter.getClass().getName();
                actualParameters += javaType;

                if (i + 1 != parameters.length) {
                    actualParameters += "\n";
                }
            }

            throw new SerializationException(
                "request.parameter.count.incorrect",
                new Object[]{
                    new Integer(serializers.length),
                    new Integer(parameters.length),
                    expectedParameters,
                    actualParameters});
        }
    }

    protected boolean isRPCLiteral() {
        return (
            (operationStyle.equalsIgnoreCase("rpc"))
            && (encodingStyle.equals("")));
    }
}
