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

/* $Id: BasicCall.java,v 1.3 2007-07-13 23:35:55 ofung Exp $
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

package com.sun.xml.rpc.client.dii;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.handler.HandlerChain;
import javax.xml.rpc.handler.HandlerRegistry;
import javax.xml.rpc.soap.SOAPFaultException;

import com.sun.xml.rpc.client.HandlerChainImpl;
import com.sun.xml.rpc.client.ContentNegotiationProperties;
import com.sun.xml.rpc.encoding.DynamicInternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.ReferenceableSerializerImpl;
import com.sun.xml.rpc.encoding.SOAPFaultInfoSerializer;
import com.sun.xml.rpc.encoding.SerializerConstants;
import com.sun.xml.rpc.encoding.literal.LiteralRequestSerializer;
import com.sun.xml.rpc.encoding.literal.LiteralResponseSerializer;
import com.sun.xml.rpc.encoding.soap.SOAPRequestSerializer;
import com.sun.xml.rpc.encoding.soap.SOAPResponseSerializer;
import com.sun.xml.rpc.encoding.soap.SOAPResponseStructure;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * @author JAX-RPC RI Development Team
 */
public class BasicCall implements CallEx {
    protected static final QName EMPTY_QNAME = new QName("");;
    protected static final QName RESULT_QNAME = null;
    //new QName("", "result");
    protected static final int RETURN_VALUE_INDEX = 0;
    private boolean isProxy = false;
    protected boolean isOneWay;

    /**
     * for SOAP 1.2 needs work.
     */
    protected static final JAXRPCDeserializer faultDeserializer =
            new ReferenceableSerializerImpl(SerializerConstants.DONT_SERIALIZE_AS_REF,
                    new SOAPFaultInfoSerializer(false, false));


    protected static final Set recognizedProperties;

    static {
        Set temp = new HashSet();
        temp.add(CallPropertyConstants.USERNAME_PROPERTY);
        temp.add(CallPropertyConstants.PASSWORD_PROPERTY);
        temp.add(CallPropertyConstants.ENDPOINT_ADDRESS_PROPERTY);
        temp.add(CallPropertyConstants.OPERATION_STYLE_PROPERTY);
        temp.add(CallPropertyConstants.SOAPACTION_USE_PROPERTY);
        temp.add(CallPropertyConstants.SOAPACTION_URI_PROPERTY);
        temp.add(CallPropertyConstants.SESSION_MAINTAIN_PROPERTY);
        temp.add(CallPropertyConstants.ENCODING_STYLE_PROPERTY);
        temp.add(CallPropertyConstants.HTTP_COOKIE_JAR);
        temp.add(CallPropertyConstants.HOSTNAME_VERIFICATION_PROPERTY);
        temp.add(CallPropertyConstants.REDIRECT_REQUEST_PROPERTY);
        temp.add(CallPropertyConstants.SECURITY_CONTEXT);
//        temp.add(CallPropertyConstants.ATTACHMENT_CONTEXT);
        temp.add(CallPropertyConstants.SET_ATTACHMENT_PROPERTY);
        temp.add(CallPropertyConstants.GET_ATTACHMENT_PROPERTY);
        temp.add(CallPropertyConstants.CONTENT_NEGOTIATION_PROPERTY);
        recognizedProperties = Collections.unmodifiableSet(temp);
    }

    protected JAXRPCSerializer requestSerializer = null;
    protected JAXRPCDeserializer responseDeserializer = null;

    protected List inParameterNames = new ArrayList();
    protected List outParameterNames = new ArrayList();
    protected List inParameterXmlTypes = new ArrayList();
    protected List outParameterXmlTypes = new ArrayList();
    protected List inParameterXmlTypeQNames = new ArrayList();
    protected List outParameterXmlTypeQNames = new ArrayList();
    protected List inParameterClasses = new ArrayList();
    protected List outParameterClasses = new ArrayList();
    protected SOAPResponseStructure response = null;
    protected List inParameterMembers = new ArrayList();
    protected List outParameterMembers = new ArrayList();
    protected QName returnXmlType = null;
    protected QName returnXmlTypeQName = null;
    protected Class returnClass = null;
    protected String returnClassName = null;
    protected QName returnTypeQName = null;
    protected ParameterMemberInfo[] returnParameterMembers = null;

    protected QName operationName = EMPTY_QNAME;
    protected QName portName = EMPTY_QNAME;
    protected QName portTypeName = EMPTY_QNAME;

    protected String targetEndpointAddress = null;
    protected Map properties = new HashMap();
    protected InternalTypeMappingRegistry typeRegistry;
    protected CallInvoker invoker = new CallInvokerImpl();
    protected Collection packages;

    protected HandlerRegistry handlerRegistry;
    protected OperationInfo operationInfo;
    private com.sun.xml.rpc.soap.SOAPNamespaceConstants soapNamespaceConstants = null;

    private void init(SOAPVersion ver) {
        soapNamespaceConstants = SOAPConstantsFactory.getSOAPNamespaceConstants(ver);
    }

    public BasicCall(InternalTypeMappingRegistry registry,
                     HandlerRegistry handlerRegistry) {
        this(registry, handlerRegistry, SOAPVersion.SOAP_11);        
    }

    public BasicCall(InternalTypeMappingRegistry registry,
                     HandlerRegistry handlerRegistry, SOAPVersion ver) {
        if (registry == null) {
            throw new DynamicInvocationException("dii.typeregistry.missing.in.call");
        }
        init(ver); // Initialize SOAP constants

        typeRegistry = new DynamicInternalTypeMappingRegistry(registry, this);
        String operationStyle =
                (String) getProperty(Call.OPERATION_STYLE_PROPERTY);
        ((DynamicInternalTypeMappingRegistry) typeRegistry).setStyles((String) operationStyle);
        this.handlerRegistry = handlerRegistry;

        setProperty(SOAPACTION_USE_PROPERTY, new Boolean(false));
        
        ContentNegotiationProperties.initFromSystemProperties(properties);
   }

    public boolean isParameterAndReturnSpecRequired(QName operation) {
        return true;
    }

    // not in spec
    public HandlerChain getHandlerChain() {
        if (handlerRegistry.getHandlerChain(portName) == null) {
            return null;
        }
        // TBD: Should be able to somehow cache these HandlerChainImpl instances.
        return new HandlerChainImpl(handlerRegistry.getHandlerChain(portName));
    }

    public void addParameter(String paramName,
                             QName paramXmlType,
                             ParameterMode parameterMode) {
        checkIsParameterAndReturnTypeSpecAllowed();
        doAddParameter(paramName, paramXmlType, parameterMode);
    }

    protected void doAddParameter(String paramName,
                                  QName paramXmlType,
                                  ParameterMode parameterMode) {
        doAddParameter(paramName, paramXmlType, null, parameterMode);
    }

    public void addParameter(String paramName,
                             QName paramXmlType,
                             Class paramClass,
                             ParameterMode parameterMode) {
        checkIsParameterAndReturnTypeSpecAllowed();
        doAddParameter(paramName, paramXmlType, paramClass, parameterMode);
    }

    protected void doAddParameter(String paramName,
                                  QName paramXmlType,
                                  Class paramClass,
                                  ParameterMode parameterMode) {
        resetSerializers();

        if (parameterMode == ParameterMode.OUT) {
            outParameterNames.add(new QName(paramName));
            outParameterXmlTypes.add(paramXmlType);
            outParameterClasses.add(paramClass);
        } else if (parameterMode == ParameterMode.IN) {
            inParameterNames.add(new QName(paramName));
            inParameterXmlTypes.add(paramXmlType);
            inParameterClasses.add(paramClass);
        } else if (parameterMode == ParameterMode.INOUT) {
            inParameterNames.add(new QName(paramName));
            inParameterXmlTypes.add(paramXmlType);
            inParameterClasses.add(paramClass);
            outParameterNames.add(new QName(paramName));
            outParameterXmlTypes.add(paramXmlType);
            outParameterClasses.add(paramClass);
        }
    }

    protected void doAddParameter(String paramName,
                                  QName paramXmlType,
                                  Class paramClass,
                                  ParameterMemberInfo[] parameterMemberInfos,
                                  ParameterMode parameterMode) {
        resetSerializers();

        if (parameterMode == ParameterMode.OUT) {
            outParameterNames.add(new QName(paramName));
            outParameterXmlTypes.add(paramXmlType);
            outParameterClasses.add(paramClass);
            outParameterMembers.add(parameterMemberInfos);
        } else if (parameterMode == ParameterMode.IN) {
            inParameterNames.add(new QName(paramName));
            inParameterXmlTypes.add(paramXmlType);
            inParameterClasses.add(paramClass);
            inParameterMembers.add(parameterMemberInfos);
        } else if (parameterMode == ParameterMode.INOUT) {
            inParameterNames.add(new QName(paramName));
            inParameterXmlTypes.add(paramXmlType);
            inParameterClasses.add(paramClass);
            inParameterMembers.add(parameterMemberInfos);
            outParameterNames.add(new QName(paramName));
            outParameterXmlTypes.add(paramXmlType);
            outParameterClasses.add(paramClass);
            outParameterMembers.add(parameterMemberInfos);
        }
    }

    protected void doAddParameter(String paramName,
                                  QName paramXmlType,
                                  QName paramXmlTypeQName,
                                  Class paramClass,
                                  ParameterMemberInfo[] parameterMemberInfos,
                                  ParameterMode parameterMode) {
        resetSerializers();

        if (parameterMode == ParameterMode.OUT) {
            outParameterNames.add(new QName(paramName));
            outParameterXmlTypes.add(paramXmlType);
            outParameterXmlTypeQNames.add(paramXmlTypeQName);
            outParameterClasses.add(paramClass);
            outParameterMembers.add(parameterMemberInfos);
        } else if (parameterMode == ParameterMode.IN) {
            inParameterNames.add(new QName(paramName));
            inParameterXmlTypes.add(paramXmlType);
            inParameterXmlTypeQNames.add(paramXmlTypeQName);
            inParameterClasses.add(paramClass);
            inParameterMembers.add(parameterMemberInfos);
        } else if (parameterMode == ParameterMode.INOUT) {
            inParameterNames.add(new QName(paramName));
            inParameterXmlTypes.add(paramXmlType);
            inParameterXmlTypeQNames.add(paramXmlTypeQName);
            inParameterClasses.add(paramClass);
            inParameterMembers.add(parameterMemberInfos);
            outParameterNames.add(new QName(paramName));
            outParameterXmlTypes.add(paramXmlType);
            outParameterXmlTypeQNames.add(paramXmlTypeQName);
            outParameterClasses.add(paramClass);
            outParameterMembers.add(parameterMemberInfos);
        }
    }


    public QName[] getInParameterXmlTypes() {
        return (QName[]) inParameterXmlTypes.toArray(new QName[inParameterXmlTypes.size()]);
    }

    public QName getParameterTypeByName(String parameterName) {
        ListIterator eachName = inParameterNames.listIterator();
        while (eachName.hasNext()) {
            String currentName = ((QName) eachName.next()).getLocalPart();
            if (currentName.equals(parameterName)) {
                return (QName) inParameterXmlTypes.get(eachName.previousIndex());
            }
        }
        eachName = outParameterNames.listIterator();
        while (eachName.hasNext()) {
            String currentName = ((QName) eachName.next()).getLocalPart();
            if (currentName.equals(parameterName)) {
                return (QName) outParameterXmlTypes.get(eachName.previousIndex());
            }
        }
        return null;
    }

    protected boolean isProxy() {
        return isProxy;
    }

    protected void setIsProxy(boolean value) {
        this.isProxy = value;
    }

    public void setReturnTypeQName(QName returnTypeQName) {
        this.returnTypeQName = returnTypeQName;
    }

    public void setReturnXmlTypeQName(QName returnXmlTypeQName) {
        this.returnXmlTypeQName = returnXmlTypeQName;
    }

    public void setReturnType(QName type) {
        if (type != null)
            checkIsParameterAndReturnTypeSpecAllowed();

        doSetReturnType(type);
    }

    protected void doSetReturnType(QName type) {
        setReturnType(type, null);
    }

    public void setReturnParameterInfos(ParameterMemberInfo[] infos) {
        returnParameterMembers = infos;
    }

    public ParameterMemberInfo[] getReturnParameterMembers() {
        return returnParameterMembers;
    }

    public void setReturnType(QName type, Class javaType) {
        if ((type != null) && (javaType != null))
            checkIsParameterAndReturnTypeSpecAllowed();

        doSetReturnType(type, javaType);
    }

    protected void doSetReturnType(QName type, Class javaType) {
        resetSerializers();

        returnXmlType = type;
        returnClass = javaType;
    }

    public QName getReturnType() {
        return returnXmlType;
    }

    protected void setReturnTypeName(String name) {
        this.returnClassName = name;
    }

    public void removeAllParameters() {
        //comment out #4932240
        //checkIsParameterAndReturnTypeSpecAllowed();
        doRemoveAllParameters();
    }

    protected void doRemoveAllParameters() {
        resetSerializers();

        inParameterNames.clear();
        inParameterXmlTypes.clear();
        //inParameterXmlTypeQNames.clear();
        inParameterClasses.clear();
        //inParameterMembers.clear();
        outParameterNames.clear();
        outParameterXmlTypes.clear();
        //outParameterXmlTypeQNames.clear();
        outParameterClasses.clear();
        //outParameterMembers.clear();

    }

    protected void resetSerializers() {
        requestSerializer = null;
        responseDeserializer = null;
    }

    public QName getPortTypeName() {
        return portTypeName;
    }

    public void setPortTypeName(QName portType) {
        portTypeName = portType;
    }

    public QName getPortName() {
        return portName;
    }

    public void setPortName(QName port) {
        portName = port;
    }

    public QName getOperationName() {
        return operationName;
    }

    protected void setOperationInfo(OperationInfo info) {
        this.operationInfo = info;
    }

    public OperationInfo getOperationInfo() {
        return this.operationInfo;
    }

    public void setOperationName(QName operationName) {
        this.operationName = operationName;
    }

    public void setTargetEndpointAddress(String address) {
        targetEndpointAddress = address;
        invoker = new CallInvokerImpl();
    }

    public String getTargetEndpointAddress() {
        return targetEndpointAddress;
    }

    public void setProperty(String name, Object value) {
        if (!recognizedProperties.contains(name)) {
            throw new DynamicInvocationException("dii.call.property.set.unrecognized",
                    new Object[]{name});
        }
        
        // Internalize value of CONTENT_NEGOTIATION_PROPERTY
        if (name.equals(CallPropertyConstants.CONTENT_NEGOTIATION_PROPERTY)) {
            properties.put(name, ((String) value).intern());
        }
        else {
            properties.put(name, value);
        }
    }

    public Object getProperty(String name) {
        if (!recognizedProperties.contains(name)) {
            throw new DynamicInvocationException("dii.call.property.get.unrecognized",
                    new Object[]{name});
        }

        return properties.get(name);
    }

    public void removeProperty(String name) {
        properties.remove(name);
    }

    public Iterator getPropertyNames() {
        return recognizedProperties.iterator();
    }

    //todo: can take if-else out now - do later
    public Object invoke(Object[] parameters) throws java.rmi.RemoteException {

        if (isOneWay) {
            invokeOneWay(parameters);
        } else {
            if (packages != null)
                packages.clear();

            if (parameters == null) {
                parameters = new Object[0];
            } else
                packages = collectPackages(parameters);
            //side utility if missing return class look in parameter Packages

            this.inParameterClasses = validateParameters(this.inParameterClasses, null, packages);

            this.returnClass = validateReturnClass(this.returnClass, returnClassName, packages);

            try {
                String operationStyle =
                        (String) getProperty(OPERATION_STYLE_PROPERTY);
                ((DynamicInternalTypeMappingRegistry) typeRegistry).setStyles(operationStyle);
                String encodingStyle =
                        (String) getProperty(CallPropertyConstants.ENCODING_STYLE_PROPERTY);

                if ("document".equals(operationStyle) && "".equals(encodingStyle)) {
                    response =
                            getInvoker().doInvoke(new CallRequest(this, parameters),
                                    getRequestSerializer(),
                                    getResponseDeserializer(),
                                    getFaultDeserializer());
                } else if ("rpc".equals(operationStyle) && ("".equals(encodingStyle))) {
                    response =
                            getInvoker().doInvoke(new CallRequest(this, parameters),
                                    getRequestSerializer(),
                                    getResponseDeserializer(),
                                    getFaultDeserializer());
                } else if ("rpc".equals(operationStyle) && soapNamespaceConstants.getEncoding().equals(encodingStyle)) {  //add sopaEncodingNamespace later kw
                    response =
                            getInvoker().doInvoke(new CallRequest(this, parameters),
                                    getRequestSerializer(),
                                    getResponseDeserializer(),
                                    getFaultDeserializer());
                } else {
                    throw unsupportedOperationStyleException(operationStyle);
                }
            } catch (java.rmi.RemoteException e) {
                throw e;
            } catch (Exception e) {
                if (e instanceof SOAPFaultException) {
                    if (isProxy())
                        throw (SOAPFaultException) e;
                    else
                        throw new java.rmi.RemoteException(((SOAPFaultException) e).getFaultString());
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new java.rmi.RemoteException("",
                            new DynamicInvocationException(new LocalizableExceptionAdapter(e)));
                }
            }
            if (response != null)
                return response.returnValue;
        }
        return null;
    }

    public Object invoke(QName operationName, Object[] inputParams)
            throws java.rmi.RemoteException {
        setOperationName(operationName);
        Object returnValue = invoke(inputParams);
        return returnValue;
    }

    public void invokeOneWay(Object[] parameters) {

        if (parameters == null) {
            parameters = new Object[0];
        }

        try {
            //todo:can actually take if - else out now-
            String operationStyle =
                    (String) getProperty(OPERATION_STYLE_PROPERTY);
            String encodingStyle =
                    (String) getProperty(CallPropertyConstants.ENCODING_STYLE_PROPERTY);

            if ("document".equals(operationStyle) && "".equals(encodingStyle)) {
                getInvoker().doInvokeOneWay(new CallRequest(this, parameters),
                        getRequestSerializer());
            } else if ("rpc".equals(operationStyle) && ("".equals(encodingStyle))) {
                getInvoker().doInvokeOneWay(new CallRequest(this, parameters),
                        getRequestSerializer());
            } else if ("rpc".equals(operationStyle) && soapNamespaceConstants.getEncoding().equals(encodingStyle)) {
                getInvoker().doInvokeOneWay(new CallRequest(this, parameters),
                        getRequestSerializer());
            } else {
                throw unsupportedOperationStyleException(operationStyle);
            }
        } catch (Exception e) {
            if (e instanceof SOAPFaultException) {
                //throw new java.rmi.RemoteException(e.getMessage());
                //one way should not throw an exception
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new DynamicInvocationException(new LocalizableExceptionAdapter(e));
            }
        }
    }

    protected DynamicInvocationException unsupportedOperationStyleException(String operationStyle) {
        return new DynamicInvocationException("dii.operation.style.unsupported",
                new Object[]{operationStyle});
    }

    public Map getOutputParams() {
        if (response == null) {
            throw new DynamicInvocationException("dii.outparameters.not.available");
        }
        //bug fix bug 4833378
        return Collections.unmodifiableMap(response.outParametersStringKeys);
    }

    //backward compatabilitu
    public Map getOutputParamsQNames() {
        if (response == null) {
            throw new DynamicInvocationException("dii.outparameters.not.available");
        }
        return Collections.unmodifiableMap(response.outParameters);
    }

    public List getOutputValues() {
        return Collections.unmodifiableList((List) response.outParameters.values());
    }


    /*package*/
    Object getRequiredProperty(String requiredProperty) {
        Object property = getProperty(requiredProperty);
        if (property == null) {
            throw propertyNotFoundException(requiredProperty);
        }
        return property;
    }

    protected void checkIsParameterAndReturnTypeSpecAllowed() {
        if (isParameterAndReturnSpecRequired(operationName) == false) {
            throw new DynamicInvocationException("dii.parameterandreturntypespec.not.allowed");
        }
    }

    protected CallInvoker getInvoker() {
        return invoker;
    }

    protected JAXRPCSerializer getRequestSerializer() throws Exception {
        //lots of repetition here
        String operationStyle =
                (String) getProperty(OPERATION_STYLE_PROPERTY);
        String encodingStyle =
                (String) getProperty(CallPropertyConstants.ENCODING_STYLE_PROPERTY);
        if (requestSerializer == null) {

            if (soapNamespaceConstants.getEncoding().equals(encodingStyle) && "rpc".equals(operationStyle)) {
                createRpcRequestSerializer();
            } else if ("".equals(encodingStyle) && "rpc".equals(operationStyle)) { // literal
                createRpcLiteralRequestSerializer();
            } else if ("".equals(encodingStyle) && "document".equals(operationStyle)) { // literal
                createLiteralRequestSerializer();
            } else {
                throw new DynamicInvocationException("dii.encoding.style.unsupported",
                        new Object[]{encodingStyle});
            }
        }
        return requestSerializer;
    }

    protected void createRpcRequestSerializer() throws Exception {
        int parameterCount = inParameterNames.size();

        requestSerializer =
                new SOAPRequestSerializer(EMPTY_QNAME,
                        (QName[]) inParameterNames.toArray(new QName[parameterCount]),
                        (QName[]) inParameterXmlTypes.toArray(new QName[parameterCount]),
                        (Class[]) inParameterClasses.toArray(new Class[parameterCount]));
        ((Initializable) requestSerializer).initialize(typeRegistry);
    }


    protected void createLiteralRequestSerializer() throws Exception {
        String operationStyle =
                (String) getProperty(OPERATION_STYLE_PROPERTY);
        int parameterCount = inParameterNames.size();

        requestSerializer =
                new LiteralRequestSerializer((QName) inParameterXmlTypes.get(0),
                        SerializerConstants.DONT_ENCODE_TYPE, true, "", operationStyle,
                        (QName[]) inParameterNames.toArray(new QName[parameterCount]),
                        (QName[]) inParameterXmlTypes.toArray(new QName[parameterCount]),
                        (QName[]) inParameterXmlTypeQNames.toArray(new QName[parameterCount]),
                        (Class[]) inParameterClasses.toArray(new Class[parameterCount]),
                        (ArrayList) inParameterMembers);

        ((Initializable) requestSerializer).initialize(typeRegistry);
    }


    protected void createRpcLiteralRequestSerializer() throws Exception {
        String operationStyle =
                (String) getProperty(OPERATION_STYLE_PROPERTY);
        int parameterCount = inParameterNames.size();
        requestSerializer =
                new LiteralRequestSerializer(getOperationName(),
                        SerializerConstants.DONT_ENCODE_TYPE, true, "", operationStyle,
                        (QName[]) inParameterNames.toArray(new QName[parameterCount]),
                        (QName[]) inParameterXmlTypes.toArray(new QName[parameterCount]),
                        (Class[]) inParameterClasses.toArray(new Class[parameterCount]),
                        (ArrayList) inParameterMembers);


        ((Initializable) requestSerializer).initialize(typeRegistry);
    }


    protected JAXRPCDeserializer getResponseDeserializer() throws Exception {
        if (responseDeserializer == null) {
            //kw modified -- need to do thisrrrr
            String operationStyle =
                    (String) getProperty(OPERATION_STYLE_PROPERTY);
            String encodingStyle =
                    (String) getProperty(CallPropertyConstants.ENCODING_STYLE_PROPERTY);

            if (soapNamespaceConstants.getEncoding().equals(encodingStyle) && "rpc".equals(operationStyle)) {
                createRpcResponseSerializer();
            } else if ("".equals(encodingStyle) && "rpc".equals(operationStyle)) { // literal
                createRpcLiteralResponseSerializer();
            } else if ("".equals(encodingStyle) && "document".equals(operationStyle)) { // literal
                createLiteralResponseSerializer();
            } else {
                throw new DynamicInvocationException("dii.encoding.style.unsupported",
                        new Object[]{encodingStyle});
            }
        }
        return responseDeserializer;
    }

    protected void createRpcResponseSerializer() throws Exception {
        int parameterCount = outParameterNames.size();
        responseDeserializer =
                new ReferenceableSerializerImpl(SerializerConstants.DONT_SERIALIZE_AS_REF,
                        new SOAPResponseSerializer(EMPTY_QNAME,
                                (QName[]) outParameterNames.toArray(new QName[parameterCount]),
                                (QName[]) outParameterXmlTypes.toArray(new QName[parameterCount]),
                                (Class[]) outParameterClasses.toArray(new Class[parameterCount]),
                                returnXmlType,
                                returnClass));
        ((Initializable) responseDeserializer).initialize(typeRegistry);
    }


    protected void createLiteralResponseSerializer() throws Exception {
        String operationStyle =
                (String) getProperty(OPERATION_STYLE_PROPERTY);
        //need to set responseQName property--
        int parameterCount = outParameterNames.size();

        if ((returnClass == null) && (returnXmlType != null))
            throw serializerNotFoundException(0, returnXmlType, null, returnXmlType);
        // return null;

        responseDeserializer =
                new LiteralResponseSerializer(returnXmlType,
                        SerializerConstants.DONT_ENCODE_TYPE, true, "",
                        operationStyle,
                        (QName[]) outParameterNames.toArray(new QName[parameterCount]),
                        (QName[]) outParameterXmlTypes.toArray(new QName[parameterCount]),
                        (QName[]) outParameterXmlTypeQNames.toArray(new QName[parameterCount]),
                        (Class[]) outParameterClasses.toArray(new Class[parameterCount]),
                        (ArrayList) outParameterMembers,
                        returnXmlType, returnXmlTypeQName,
                        returnClass, returnParameterMembers);

        ((Initializable) responseDeserializer).initialize(typeRegistry);
    }

    protected void createRpcLiteralResponseSerializer() throws Exception {
        String operationStyle =
                (String) getProperty(OPERATION_STYLE_PROPERTY);
        QName responseQName = new QName(operationName.getNamespaceURI(),
                operationName.getLocalPart() + "Response");
        //need to set responseQName property--
        int parameterCount = outParameterNames.size();
        responseDeserializer =
                new LiteralResponseSerializer(responseQName, SerializerConstants.DONT_ENCODE_TYPE, true, "",
                        operationStyle,
                        (QName[]) outParameterNames.toArray(new QName[parameterCount]),
                        (QName[]) outParameterXmlTypes.toArray(new QName[parameterCount]),
                        (Class[]) outParameterClasses.toArray(new Class[parameterCount]),
                        (ArrayList) outParameterMembers,
                        returnXmlType, returnClass, returnParameterMembers);


        ((Initializable) responseDeserializer).initialize(typeRegistry);
    }

    protected JAXRPCDeserializer getFaultDeserializer() {
        return faultDeserializer;
    }

    protected String getOperationStyle() {
        return (String) getRequiredProperty(OPERATION_STYLE_PROPERTY);
    }


    protected DynamicInvocationException serializerNotFoundException(int index,
                                                                     QName name,
                                                                     Class clazz,
                                                                     QName xmlType) {
        Integer indexObject = new Integer(index);
        if (clazz == null) {
            if (xmlType == null) {
                return new DynamicInvocationException("dii.parameter.type.underspecified",
                        new Object[]{indexObject, name});
            }
            return new DynamicInvocationException("dii.parameter.type.ambiguous.no.class",
                    new Object[]{indexObject, name, xmlType});
        } else if (xmlType == null) {
            return new DynamicInvocationException("dii.parameter.type.ambiguous.no.typename",
                    new Object[]{indexObject, name, clazz});
        }
        return new DynamicInvocationException("dii.parameter.type.unknown",
                new Object[]{indexObject, name, clazz, xmlType});
    }

    protected DynamicInvocationException propertyNotFoundException(String property) {
        return new DynamicInvocationException("dii.required.property.not.set",
                new Object[]{property});
    }

    protected Collection collectPackages(Object[] params) {
        if (packages != null)
            packages.clear();

        Collection packages = new ArrayList();
        if (params == null)
            return packages;
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null)
                return null;
            Class clazz = params[i].getClass();
            Package pack = null;
            if (clazz != null)
                pack = clazz.getPackage();

            if (pack != null) {
                breakPackageDown(pack.getName(), packages);
                packages.add(pack.getName());
            }
        }
        return packages;
    }


    private List validateParameters(List parameterClasses, String[] classNames,
                                    Collection packages) {
        if (packages == null) return parameterClasses;
        Iterator iter = parameterClasses.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Class pclass = (Class) iter.next();
            pclass = validateClassPackage(pclass, null,
                    packages);
        }
        return parameterClasses;
    }

    private Class validateReturnClass(Class returnClass, String returnClassName,
                                      Collection packages) {
        if (returnClass == null)
            returnClass = getClassForName(returnClassName, packages);
        return validateClassPackage(returnClass, returnClassName, packages);
    }

    protected Class getClassForName(String name, Collection packages) {
        if (name == null)
            return null;
        // first type the class name alone
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException cnfe) {
            //ok now try with the know packages
            if (packages == null)
                return null;
            Iterator iter = packages.iterator();
            while (iter.hasNext()) {
                String qualifiedName = (String) iter.next() + "." + name;
                try {
                    return Class.forName(qualifiedName);
                } catch (ClassNotFoundException cfe) {
                    continue;
                }
            }
        }
        return null;
    }

    protected Class validateClassPackage(Class returnClass, String classname,
                                         Collection packages) {
        if (packages == null)
            return returnClass;
        if (returnClass != null) {
            Package clPack = null;
            try {
                clPack = returnClass.getPackage();
            } catch (java.lang.NullPointerException npe) {
                return returnClass;
            }
            if (clPack == null)
                return returnClass;
            String packName = clPack.getName();
            if (packName == null)
                return returnClass;
            //check to see if this pack is in known packages
            Iterator piter = packages.iterator();
            boolean found = false;
            while (piter.hasNext()) {
                if (packName.equals((String) piter.next())) {
                    found = true;
                    break;
                }
            }
            if (found) //we propable have the correct class
                return returnClass;
            //not found - could there be a dup of this class in a different package?
            //should it be this package?
            //is there one in this package
            //string off the package
            //assume classname is unqualified
            Class newClass = getClassForName(classname, packages);
            if (newClass != null)
                return newClass;
            else
                return returnClass;

        }
        return returnClass;

    }

    private void breakPackageDown(String packName, Collection packages) {

        int idx = 0;
        while (idx != -1) {
            idx = packName.lastIndexOf(".");
            String newPack = null;
            if (idx != -1) {
                newPack = packName.substring(0, idx);
            } else
                return;
            packages.add(newPack);
            packName = newPack;
        }
    }

}
