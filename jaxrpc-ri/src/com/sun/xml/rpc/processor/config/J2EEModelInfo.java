/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
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

// @(#) 1.3 jsr109ri/src/java/com/ibm/webservices/ri/tools/wsdlc1_1/JSR109ModelInfo.java, jsr109ri, jsr10911, b0240.03 10/6/02 20:26:09 [10/7/02 11:55:32]
/*************************************************************************
   Licensed Materials - Property of IBM
   5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business M
achines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with
   IBM Corp.
**************************************************************************/
/*********************************************************************
Change History
Date     user       defect    purpose
---------------------------------------------------------------------------
08/12/02 mcheng     142035    new code drop
09/02/02 mcheng     144753    more changes to support mapping meta-data
09/12/02 mcheng     146157    changes to accommodate javaWsdlMapping javabean
10/06/02 mcheng     149269    merge in JSR109 1.0 changes
*********************************************************************/
package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModelerBase;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.modeler.j2ee.J2EEModeler;
import com.sun.xml.rpc.processor.modeler.j2ee.J2EEModelerHelper;
import com.sun.xml.rpc.processor.modeler.j2ee.J2EEModelerIf;
import com.sun.xml.rpc.processor.modeler.j2ee.J2EEModeler111;
import com.sun.xml.rpc.processor.modeler.j2ee.J2EEModeler112;
import com.sun.xml.rpc.processor.modeler.j2ee.JaxRpcMappingXml;
import com.sun.xml.rpc.processor.modeler.j2ee.NamespaceHelper;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.constructorParameterOrderType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.exceptionMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.javaWsdlMapping;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.javaXmlTypeMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.methodParamPartsMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.portMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.serviceEndpointInterfaceMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.serviceEndpointMethodMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.serviceInterfaceMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.variableMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.wsdlMessageMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.wsdlMessageType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.wsdlReturnValueMappingType;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.xsdQNameType;
import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.schema.TypeDefinitionComponent;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.wsdl.document.Message;
import com.sun.xml.rpc.wsdl.document.MessagePart;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.xml.namespace.QName;

/**
 * ModelInfo used for JSR109 deployment
 */
public class J2EEModelInfo extends WSDLModelInfo 
    implements com.sun.xml.rpc.spi.tools.J2EEModelInfo {
        
    public J2EEModelInfo() {}

    /**
     * Constructor for J2EEModelInfo
     * @param args args for this run of wsdlc
     */
    public J2EEModelInfo (JaxRpcMappingXml mappingXml) {
        setJaxRcpMappingXml(mappingXml);
    }
    
    
    public void setJaxRcpMappingXml(JaxRpcMappingXml mappingXml) {
        mappingFileXml = mappingXml;
        /* set NS to Pkg mapping */
        NamespaceMappingRegistryInfo nsMapInfo = new NamespaceMappingRegistryInfo();
        HashMap nsMap = mappingFileXml.getNSToPkgMapping();
        Set keys = nsMap.keySet();
        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            String ns = (String)it.next();
            String pkg = (String)nsMap.get(ns);
            NamespaceMappingInfo map = new NamespaceMappingInfo(ns, pkg);
            nsMapInfo.addMapping(map);
        }
        setNamespaceMappingRegistry(nsMapInfo);
        
        
        /* traverse the mapping meta-data and initialize internal 
           data structure */
        init();
    }


    protected Modeler getModeler(Properties options) {
  
        if (VersionUtil.isVersion111(options.getProperty(
            ProcessorOptions.JAXRPC_SOURCE_VERSION))) {
            return new J2EEModeler111(this, options);
        } else if (VersionUtil.isVersion112(options.getProperty(
            ProcessorOptions.JAXRPC_SOURCE_VERSION))) {
            return new J2EEModeler112(this, options);
        }
        return new J2EEModeler(this, options);
    }

    /**
     * Find the service name for a WSDL service.  If the
     * mapping meta-data specifies the name, then use it.
     * Otherwise, use the regular javxrpc name
     * @return the fully qualified Java name for a WSDL service
     */
    public String javaNameOfService(QName service) {
        
        String serviceInterface = null;
        serviceInterfaceMappingType serviceMapping = (serviceInterfaceMappingType)serviceMap.get(service);
        if ( serviceMapping != null) {
            /* it's specified in mapping meta-data */
            serviceInterface = serviceMapping.getServiceInterface().getElementValue();
        } else {
            serviceInterface = getNames().validJavaClassName(service.getLocalPart());
            String javaPackageName = getJavaPackageName(service);
            if ( javaPackageName != null ) {
                serviceInterface = javaPackageName + "." + serviceInterface;
            }
        }
        return serviceInterface;
    }

    /**
     * Find java name Service Endpoint Interaface
     * @param bindingQName QName of binding
     * @param portypeQName QName of portType
     * @param portQName    QName of port
     * @return the fully qualified Java name for a portType
     */
    public String javaNameOfSEI(QName bindingQName, QName portTypeQName,
             QName portQName) {
        String sei = null;
        MetadataSEIInfo seiInfo = (MetadataSEIInfo)serviceEndpointMap.get(bindingQName);
        if ( seiInfo != null) {
            /* it's specified in mapping meta-data */
            sei = seiInfo.javaName;
        } else {
            debug("javaNameOfSEI: seiInfo is null");
            /* use pre-existing default algorithm in original Schema Analyzer */
            if ( portTypeQName != null ) {
                /* use protType's name */
                sei = makePackageQualified(getNonQualifiedNameFor(portTypeQName), portTypeQName);
            } else {
                /* somehow, portType name not available.  Use port name */
                 sei = makePackageQualified(getNonQualifiedNameFor(portQName), portQName);
            }
        }
        debug("javaNameofSEI" +  bindingQName + " is:" +  sei);
        return sei;
    }

    /* 
     * Find info about java operation
     * Find info about java operation
     * @param bindingQName QName of binding
     * @param operationName name of Wsdl Operation
     * @param inputMsg representation of WSDL operation's input message
     * @param outputMsg representation of WSDL operation's output message
     * @param modeler to access schema methods 
     * @return the opeationInfo that represents the operation, or null
     *      if full mapping metadata not specified
     * @throws ModelerException if full mapping data is specified,
     *   but the operation can't be found
     */
    public MetadataOperationInfo findOperationInfo(QName bindingQName,
        String operationName, Message inputMsg, Message outputMsg,
        J2EEModelerIf modeler) {

        MetadataSEIInfo seiInfo = (MetadataSEIInfo)serviceEndpointMap.get(
            bindingQName);
        if (seiInfo == null) {
            return null;                 // no mapping meta-data
        }

        QName inMsgQName = J2EEModelerHelper.getQNameOf(inputMsg);
        QName outMsgQName = (outputMsg != null)
            ? J2EEModelerHelper.getQNameOf(outputMsg) : null;
        for (Iterator it=seiInfo.operationInfo.iterator(); it.hasNext(); ) {
            boolean match = true;
            MetadataOperationInfo opInfo = (MetadataOperationInfo)it.next();

            if (!opInfo.wsdlOpName.equals(operationName)) {    
                continue;                // operation name mismatch
            }
            if (opInfo.inputMessage != null && inputMsg != null
                && !opInfo.inputMessage.equals(inMsgQName)) {
                continue;                // input message name mismatch
            }
            if (opInfo.outputMessage != null && outputMsg != null
                && !opInfo.outputMessage.equals(outMsgQName)) {
                continue;                // output message name mismatch
            }
            Map retMap = new HashMap();
            if (opInfo.retPart != null) {
                retMap.put(opInfo.retPart.partName, opInfo.retPart);
            }
            if (opInfo.isWrapped) {
                if (!matchPartsWrapped(opInfo.inputParts, inputMsg, modeler) ||         
                    !matchPartsWrapped(opInfo.outputParts, outputMsg, modeler) ||                
                    !matchPartsWrapped(opInfo.inoutParts, inputMsg, modeler) ||
                    !matchPartsWrapped(opInfo.inoutParts, outputMsg, modeler) ||
                    !matchPartsWrapped(retMap, outputMsg, modeler)) {
                    continue;
                }  
            } else {
                if (!matchParts(opInfo.inputParts, inputMsg) ||         
                    !matchParts(opInfo.outputParts, outputMsg) ||                
                    !matchParts(opInfo.inoutParts, inputMsg) ||
                    !matchParts(opInfo.inoutParts, outputMsg) ||
                    !matchParts(retMap, outputMsg)) {
                    continue;
                }
            }
            return opInfo;
        }                    
        /* no match */
        throw new ModelerException(
            "Unable to locate jax-rpc mapping meta-data for wsdl operation "
            + operationName + " in binding " + bindingQName);
    }
    
    private boolean matchParts(Map parts, Message msg) {
        Iterator it = parts.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            String partName = (String)e.getKey();
            MetadataParamInfo param = (MetadataParamInfo)e.getValue();
            // Donot match headers as they may belong to other wsdl messages
            if (!param.isSoapHeader) {
                if (msg == null || msg.getPart(partName) == null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean matchPartsWrapped(Map parts, Message msg,
        J2EEModelerIf modeler) {
        
        LiteralSequenceType seqType = null;
        Iterator it = parts.entrySet().iterator(); 
        while(it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            String partName = (String)e.getKey();
            MetadataParamInfo param = (MetadataParamInfo)e.getValue();   
            // Donot match headers as they may belong to other wsdl messages
            if (!param.isSoapHeader) {
                if (seqType == null) {
                    if (msg == null || msg.numParts() != 1) {
                        return false;
                    }
                    MessagePart part = (MessagePart)msg.parts().next();
                    if (part.getDescriptorKind() != SchemaKinds.XSD_ELEMENT) {
                        return false;
                    }
                    QName elementType = part.getDescriptor();
                    LiteralType literalType = modeler.getElementTypeToLiteralType(
                        elementType);
                    if (literalType == null ||
                        !(literalType instanceof LiteralSequenceType)) {
                        return false;
                    }
                    seqType = (LiteralSequenceType)literalType;
                }
                if (seqType.getElementMemberByName(partName) == null) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Find the java name of a simple or complex type
     * @param component the TypeDefinitionComponent that represents a type
     * @return the fully qualified class name of the type
     */
    public String javaNameOfType(TypeDefinitionComponent component) {
        QName typeQName = component.getName();
        debug("javaNameOfType component.getName = " + component.getName());
        javaXmlTypeMappingType xmlMap = null;

        if ( component.isSimple() ) {
            xmlMap = (javaXmlTypeMappingType)simpleTypeMap.get(typeQName);
 
        }
        else if ( component.isComplex() ) {
            xmlMap = (javaXmlTypeMappingType)complexTypeMap.get(typeQName);

            //XXX we don't utilize qname-scope, but in case others do, we should
            //be able to find it in the meta data mapping
            if (xmlMap == null) {
                xmlMap = (javaXmlTypeMappingType)elementMap.get(typeQName);
            }
        }
        else {
            /* should not happen */
            throw new IllegalArgumentException("type is neither simple nor complex");
        }

        if ( xmlMap != null )  {
                return xmlMap.getJavaType().getElementValue();
        }
        else {
            /* not overridden by jax-rpc mapping meta-data */
            return  makePackageQualified(getNames().validJavaClassName(typeQName.getLocalPart ()),typeQName);
        }
    }

    /**
     * Find the java name of a member of a complex type
     * @param component the TypeDefinitionComponent that represents a type
     * @param member name of the mbmer in XML schema
     * @return the member name to use in java
     */
    public JavaMemberInfo javaMemberInfo(TypeDefinitionComponent component, 
            String member) {
        QName typeQName = component.getName();
        javaXmlTypeMappingType xmlMap = null;
        if ( component.isComplex() ) {
            xmlMap = (javaXmlTypeMappingType)complexTypeMap.get(typeQName);
        }
        else {
            /* should not happen */
            throw new IllegalArgumentException("type is neither simple nor complex");
        }

        JavaMemberInfo ret = null;
        if ( xmlMap != null )  {
            int numVariableMappings = xmlMap.getVariableMappingCount();
            for (int i=0; i < numVariableMappings; i++) {
                variableMappingType variableMap = xmlMap.getVariableMapping(i);
                if ( variableMap.getXmlElementName()!= null && variableMap.getXmlElementName().getElementValue().equals(member)){
                    ret = new JavaMemberInfo();
                    ret.javaMemberName = variableMap.getJavaVariableName().getElementValue();
                    ret.isDataMember = (variableMap.getDataMember()== null? false: true);
                    break;
                } 
            }
        }

        if ( ret == null ) {
            /* not overridden by jax-rpc mapping meta-data */
            ret = new JavaMemberInfo();
            ret.javaMemberName = getNames().validJavaMemberName(member);
            ret.isDataMember = false;
        }
        return ret;
    }


    /**
     * Find the java name of an element
     * @param typeQName the Qname of the element
     * @return the fully qualified class name of the type
     */
    public String javaNameOfElementType(QName typeQName, String anonymousName) {

        javaXmlTypeMappingType xmlMap = null;

        debug("Looking for type = " + typeQName + "; anonymous = " + anonymousName);

        if (anonymousName != null) {
            String anonymousTypeName = typeQName.getNamespaceURI() + ":" + anonymousName;
            xmlMap = (javaXmlTypeMappingType)anonymousElementMap.get(anonymousTypeName);

            //try the complex type map if elementmap doesn't return anything
            if (xmlMap == null) {
                xmlMap = (javaXmlTypeMappingType)anonymousComplexTypeMap.get(anonymousTypeName);
            }
        } else {
            xmlMap = (javaXmlTypeMappingType)elementMap.get(typeQName);

            //try the complex type map if elementmap doesn't return anything
            if (xmlMap == null) {
                xmlMap = (javaXmlTypeMappingType)complexTypeMap.get(typeQName);
            }
        }

        debug("111 typeQName = " + typeQName + "; xmlMap = " + xmlMap);
        if ( xmlMap != null )  {
              return xmlMap.getJavaType().getElementValue();
        }
        else {
            /* not overridden by jax-rpc mapping meta-data */
            return  makePackageQualified(getNames().validJavaClassName(typeQName.getLocalPart ()),typeQName);
        }
    }

    /**
     * Find the java name of a member of an element 
     * @param typeQName QName of element
     * @param member name of the mbmer in XML schema
     * @return JavaMemberInfo with info about member name, and whether it's a field
     */
    public JavaMemberInfo javaMemberOfElementInfo(QName typeQName, String member) {
        javaXmlTypeMappingType xmlMap = 
             (javaXmlTypeMappingType)complexTypeMap.get(typeQName);

        JavaMemberInfo ret = null;
        if ( xmlMap != null )  {
            int numVariableMappings = xmlMap.getVariableMappingCount();
            for (int i=0; i < numVariableMappings; i++) {
                variableMappingType variableMap = xmlMap.getVariableMapping(i);
                if (variableMap.getXmlElementName() != null && variableMap.getXmlElementName().getElementValue().equals(member)){
                    ret = new JavaMemberInfo();
                    ret.javaMemberName = variableMap.getJavaVariableName().getElementValue();
                    ret.isDataMember = (variableMap.getDataMember()== null? false: true);
                    break;
                }
            }
        }

        if ( ret == null ) {
            /* not overridden by jax-rpc mapping meta-data */
            ret = new JavaMemberInfo();
            ret.javaMemberName = getNames().validJavaMemberName(member);
            ret.isDataMember = false;
        }
        return ret;
    }

    /**
     * Find Java name for port as specified in mapping meta-data,
     * for use in generating get<Port> method,
     * @return Java name of port , or null if not in mapping meta-data
     */
    public String javaNameOfPort(QName port) {
        portMappingType portMapping = (portMappingType)portMap.get(port);
        if ( portMapping != null )
            return portMapping.getJavaPortName().getElementValue();
        else return null;

    }


    /*
     * Find Java name of an exception generated from a
     * WSDL message
     * @param wsdlMessage QName of the wsdl message
     * @param partName name of the message part being referenced by
     *        a header fault (should be null if not for headerfault)
     * @return the ExceptionInfo, or null if not found
     */
    public ExceptionInfo getExceptionInfo(QName wsdlMessage, String partName) {
        return (ExceptionInfo)exceptionMap.get(wsdlMessage.toString()+partName);
    }

    /**
     * Find the constructor parameter order for an exception generated
     * from a WSDL message that points to a complexType
     * @return HashMap containg mapping of element name to constructor order
     * or null if wsdlMessage can't be found
     */
    public HashMap exceptionConstructorOrder(QName wsdlMessage) {
        ExceptionInfo exInfo = (ExceptionInfo)exceptionMap.get(wsdlMessage.toString());
        if ( exInfo == null ) {
            return null;
        } else {
            return exInfo.constructorOrder;
        }
    }

    private String getJavaPackageName(QName qname) {
        String ret = null;     
        NamespaceMappingRegistryInfo nsMap = getNamespaceMappingRegistry();
        if ( nsMap != null )  {
            NamespaceMappingInfo info = nsMap.getNamespaceMappingInfo(qname);
            if (info != null ) 
                return info.getJavaPackageName();
        }
        return null;
    }


    private String makePackageQualified(String s, QName name) {
        String javaPackageName = getJavaPackageName(name);
        if (javaPackageName != null) {
            return javaPackageName + "." + s;
        }
        else {
            return s;
        }
    }

    
    /* XXX: need to debug this */
   private QName  stringToQName(String s) {
       return new QName(s);
   }


    /* initialize internal state */
    private void init() {
        NamespaceHelper nsHelper = new NamespaceHelper();
        javaWsdlMapping javaWsdlMap =  mappingFileXml.getJavaWsdlMapping();
        nsHelper = nsHelper.push(javaWsdlMap);

        int numjavaXmlMap = javaWsdlMap.getJavaXmlTypeMappingCount();
        debug("----------- numcount = : " + numjavaXmlMap);
        for (int i=0; i < numjavaXmlMap; i++ ) {
            javaXmlTypeMappingType xmlMap = javaWsdlMap.getJavaXmlTypeMapping(i);
            nsHelper = nsHelper.push(xmlMap);
            String scope = xmlMap.getQnameScope().getElementValue();
            xsdQNameType rtQname = xmlMap.getRootTypeQname();

            if (rtQname != null) {
                nsHelper = nsHelper.push(rtQname);
                QName qName = nsHelper.getQName(rtQname.getElementValue());
                nsHelper= nsHelper.pop();
                debug("rootTypeQNameID = " + rtQname.getId() + "; scope = " + scope + "; qName = " + qName);

                if ( scope.equals("simpleType")) {
                    simpleTypeMap.put(qName, xmlMap);
                } else if ( scope.equals("complexType")) {
                    complexTypeMap.put(qName, xmlMap);
                } else if ( scope.equals("element")) {
                    elementMap.put(qName, xmlMap);
                }
            } else {
                String anonymousTypeName = xmlMap.getAnonymousTypeQname().getElementValue();
                debug("anonymousTypeQName = " + anonymousTypeName +" scope = " + scope);

                if (scope.equals("simpleType")) {
                    anonymousSimpleTypeMap.put(anonymousTypeName, xmlMap);
                } else if (scope.equals("complexType")) {
                    anonymousComplexTypeMap.put(anonymousTypeName, xmlMap);
                } else if (scope.equals("element")) {
                    anonymousElementMap.put(anonymousTypeName, xmlMap);
                }
            }
            nsHelper = nsHelper.pop();
        }

        int numExceptions = javaWsdlMap.getExceptionMappingCount();
        for (int i=0; i < numExceptions; i++) {
            ExceptionInfo exInfo = new ExceptionInfo();
            exceptionMappingType exMap = javaWsdlMap.getExceptionMapping(i);
            nsHelper = nsHelper.push(exMap);
            exInfo.exceptionType = exMap.getExceptionType().getElementValue();
            wsdlMessageType wsdlMsg = exMap.getWsdlMessage();
            nsHelper = nsHelper.push(wsdlMsg);
            QName qname = nsHelper.getQName(wsdlMsg.getElementValue());
            nsHelper = nsHelper.pop();
            exInfo.wsdlMessage = qname;
            String partName = null;
            if (exMap.getWsdlMessagePartName() != null) {
                partName = exMap.getWsdlMessagePartName().getElementValue();
                exInfo.wsdlMessagePartName = partName;
            }
            exInfo.constructorOrder = new HashMap();
            constructorParameterOrderType ctorOrder = exMap.getConstructorParameterOrder();
            if ( ctorOrder != null ) {
               int numConstructorParams = ctorOrder.getElementNameCount();
                for (int j=0; j < numConstructorParams ; j++){
                    String param = ctorOrder.getElementName(j).getElementValue()
;
                    exInfo.constructorOrder.put(param, new Integer(j));
                }
            }
            exceptionMap.put(qname.toString()+partName, exInfo);
            debug("===> ADD EXCEPTION MAP = " + (qname.toString()+partName));
            nsHelper= nsHelper.pop();
        }

        int numServiceInterfaceMappings = javaWsdlMap.getServiceInterfaceMappingCount();
        debug("Retrieving " + numServiceInterfaceMappings + "serviceInterfaceMapping");

        for (int i=0; i < numServiceInterfaceMappings ; i++){
            serviceInterfaceMappingType serviceMapping = javaWsdlMap.getServiceInterfaceMapping(i);
            nsHelper = nsHelper.push(serviceMapping);
            xsdQNameType wsdlSvcname = serviceMapping.getWsdlServiceName();
            nsHelper = nsHelper.push(wsdlSvcname);
            QName serviceQName = nsHelper.getQName(wsdlSvcname.getElementValue());
            nsHelper= nsHelper.pop();
            String serviceNS = serviceQName.getNamespaceURI();
            serviceMap.put(serviceQName, serviceMapping);

            int numPortMaps = serviceMapping.getPortMappingCount();
            for (int j=0; j < numPortMaps; j++) {
                portMappingType portMapping = serviceMapping.getPortMapping(j);
                QName portQName = new QName(
                 serviceNS,
                 portMapping.getPortName().getElementValue());
                portMap.put(portQName, portMapping);
            }

            nsHelper = nsHelper.pop();
        }

        int numSEIMapping = javaWsdlMap.getServiceEndpointInterfaceMappingCount();
        debug("Retrieving " + numSEIMapping + "serviceEndpointInterfaceMapping");
        for (int i=0; i < numSEIMapping; i++) {      
            serviceEndpointInterfaceMappingType seiMap = 
                     javaWsdlMap.getServiceEndpointInterfaceMapping(i);
            nsHelper = nsHelper.push(seiMap);

            MetadataSEIInfo seiInfo = new MetadataSEIInfo();
            xsdQNameType wsdlBnd = seiMap.getWsdlBinding();
            nsHelper = nsHelper.push(wsdlBnd);
            seiInfo.bindingQName = nsHelper.getQName(
                 wsdlBnd.getElementValue());
            nsHelper = nsHelper.pop();

            xsdQNameType wsdlPT = seiMap.getWsdlPortType();
            nsHelper = nsHelper.push(wsdlPT);
            seiInfo.portTypeQName = nsHelper.getQName(wsdlPT.getElementValue());
            nsHelper = nsHelper.pop();

            seiInfo.javaName = seiMap.getServiceEndpointInterface().getElementValue();

            int numMethodMaps = seiMap.getServiceEndpointMethodMappingCount();
            debug("adding binding: " + seiInfo.bindingQName + " portType " + seiInfo.portTypeQName + " with " + numMethodMaps + " methods");
            for (int j=0; j < numMethodMaps; j++) {
                serviceEndpointMethodMappingType methodMap =
                     seiMap.getServiceEndpointMethodMapping(j);
                nsHelper= nsHelper.push(methodMap);
                MetadataOperationInfo opInfo = new MetadataOperationInfo();
                opInfo.wsdlOpName = methodMap.getWsdlOperation().getElementValue();
                opInfo.javaOpName = methodMap.getJavaMethodName().getElementValue();
                opInfo.isWrapped = methodMap.getWrappedElement() == null ? false : true;
                int numParams = methodMap.getMethodParamPartsMappingCount();
                debug("adding wsdlOp " + opInfo.wsdlOpName + " javaOp " + opInfo.javaOpName + " with " + numParams + " parameters" + "; isWrapped = " + opInfo.isWrapped);
                for (int k=0;k < numParams; k++) {
                    methodParamPartsMappingType methodParamMap = methodMap.
                       getMethodParamPartsMapping(k);
                    nsHelper = nsHelper.push(methodParamMap);
                    MetadataParamInfo paramInfo = new MetadataParamInfo();
                    paramInfo.position = (new Integer(methodParamMap.getParamPosition().getElementValue())).intValue();
                    paramInfo.javaType = methodParamMap.getParamType().getElementValue();
                    wsdlMessageMappingType wsdlMsgMap = methodParamMap.getWsdlMessageMapping();
                    paramInfo.partName = wsdlMsgMap.getWsdlMessagePartName().getElementValue();
                    paramInfo.mode = wsdlMsgMap.getParameterMode().getElementValue();
                    xsdQNameType wsdlmsg = wsdlMsgMap.getWsdlMessage();
                    nsHelper = nsHelper.push(wsdlmsg);
                    QName msgQName = nsHelper.getQName(
                        wsdlmsg.getElementValue());
                    nsHelper = nsHelper.pop();
                    paramInfo.isSoapHeader = (wsdlMsgMap.getSoapHeader()== null? false: true);
                    if ( paramInfo.mode.equals("IN") ){
                        if (paramInfo.isSoapHeader) {
                            paramInfo.headerMessage = msgQName;
                            opInfo.explicitcontext = true;
                        } else {
                            if ( opInfo.inputMessage == null) {
                                opInfo.inputMessage = msgQName;
                            } else if ( !opInfo.inputMessage.equals(msgQName)) {
                                throw new ModelerException("inconsistent input message QNames found: " + opInfo.inputMessage + " and " + msgQName + " for IN param of operation " + opInfo.wsdlOpName);
                            }
                        }
                        opInfo.inputParts.put(paramInfo.partName, paramInfo);
                    }
                    else if ( paramInfo.mode.equals("OUT")){
                        if (paramInfo.isSoapHeader) {
                            paramInfo.headerMessage = msgQName;
                            opInfo.explicitcontext = true;
                        } else {
                            if ( opInfo.outputMessage == null) {
                                opInfo.outputMessage = msgQName;
                            } else if ( !opInfo.outputMessage.equals(msgQName)) {
                                throw new ModelerException("Inconsistent output message QNames found: " + opInfo.outputMessage + " and " + msgQName);
                            }
                        }                                
                        opInfo.outputParts.put(paramInfo.partName, paramInfo);
                    }
                    else if ( paramInfo.mode.equals("INOUT")) {
                        if (paramInfo.isSoapHeader) {
                            paramInfo.headerMessage = msgQName;
                            opInfo.explicitcontext = true;
                        } else {
                            /* for inout param, we specify in message in mapping
                            meta-data */
                            if ( opInfo.inputMessage == null) 
                               opInfo.inputMessage = msgQName;
                            else if ( !opInfo.inputMessage.equals(msgQName)) {
                                throw new ModelerException("inconsistent input message QNames found: " + opInfo.inputMessage + " and " + msgQName + " for INOUT param of operation " + opInfo.wsdlOpName);
                            }
                        }
                        opInfo.inoutParts.put(paramInfo.partName, paramInfo);
                    }
                    else throw new ModelerException("invalid jaxrpc mapping meta-data: found param mode " + paramInfo.mode);
                    nsHelper = nsHelper.pop();

                    debug("adding parameter: " + paramInfo.javaType + " from message: " + msgQName + " and part " + paramInfo.partName + "; input msg = " + opInfo.inputMessage + "; outputMsg = " + opInfo.outputMessage + "; is header = " + paramInfo.isSoapHeader);

                }
                
                wsdlReturnValueMappingType retMap = methodMap.getWsdlReturnValueMapping();
                if ( retMap != null ) {
                    /* not one-way */
                    xsdQNameType wsdlMsg = retMap.getWsdlMessage();
                    nsHelper = nsHelper.push(wsdlMsg);
                    QName msgQName = nsHelper.getQName(
                        wsdlMsg.getElementValue());
                    nsHelper = nsHelper.pop();

                    if ( opInfo.outputMessage == null) 
                       opInfo.outputMessage = msgQName;
                    else if ( !opInfo.outputMessage.equals(msgQName)) {
                        throw new ModelerException("inconsistent input message QNames found: " + opInfo.outputMessage + " and " + msgQName + " in return value of operation " + opInfo.wsdlOpName);
                    }
                    if ( retMap.getWsdlMessagePartName() != null ) {
                        /* not void return type */
                        MetadataParamInfo retParam = new MetadataParamInfo();
                        retParam.javaType = retMap.getMethodReturnValue().getElementValue();
                        retParam.partName = retMap.getWsdlMessagePartName().getElementValue();
                        opInfo.retPart = retParam;
                        debug("adding return value: " + retParam.javaType + " from message: " + msgQName + " and part " + retParam.partName);
                    }
                }
                debug("adding wsdlOp " + opInfo.wsdlOpName + " javaOp " + opInfo.javaOpName + " with " + numParams + " parameters" + "; isWrapped = " + opInfo.isWrapped + "; inputMsg = " + opInfo.inputMessage + "; outputMessage = " + opInfo.outputMessage);
                seiInfo.operationInfo.add(opInfo);

                nsHelper = nsHelper.pop();
            }

            debug("putting " + seiInfo.bindingQName + " in serviceEndpointMap");
            serviceEndpointMap.put(seiInfo.bindingQName, seiInfo);

            nsHelper = nsHelper.pop();
        }
        nsHelper = nsHelper.pop();

        debug("init complete");
    }

    String getNonQualifiedNameFor(QName name) {
          return getNames().validJavaClassName(name.getLocalPart());
    }

    private Names getNames() {
        return ((com.sun.xml.rpc.processor.util.ProcessorEnvironment) 
              getConfiguration().getEnvironment()).getNames();
    }

    private JaxRpcMappingXml mappingFileXml;
    HashMap serviceMap = new HashMap(); // map of services
    HashMap serviceEndpointMap = new HashMap(); // map of services
    HashMap portMap = new HashMap();    // map of ports
    HashMap elementMap = new HashMap(); // map of elements
    HashMap exceptionMap = new HashMap(); // map of exceptions 
    HashMap complexTypeMap = new HashMap(); // map of complex types
    HashMap simpleTypeMap = new HashMap(); // map of simple types
    HashMap anonymousSimpleTypeMap = new HashMap(); // map of simple anonymous types
    HashMap anonymousComplexTypeMap = new HashMap(); // map of complex anonymous types
    HashMap anonymousElementMap = new HashMap(); // map of elements containing anonymous types

    /* information about Java member of an XML type */
    public static class JavaMemberInfo extends SchemaAnalyzerBase.SchemaJavaMemberInfo {
    }

    /* Information about exception */
    public static class ExceptionInfo extends WSDLModelerBase.WSDLExceptionInfo {
    }


    /* Information about SEI */
    public class MetadataSEIInfo {
         public String javaName; // java name of SEI
         public QName bindingQName; // Qname of binding
         public QName portTypeQName; // QName of port type
         List operationInfo = new Vector(); // list of MetadataOperationInfo
    };


    /* Information about operation meta-data */
    public class MetadataOperationInfo {
        public String wsdlOpName; // operation name in wsdl
        public String javaOpName;  // operation name in java
        public QName inputMessage; // input message Qname
        public QName outputMessage; // output message QName
        public HashMap inputParts = new HashMap();  // map of partName to MetadataParamInfo
        public HashMap outputParts = new HashMap();  // map part name to MetadataparamInfo
        public HashMap inoutParts = new HashMap();  // map of part name to MetaataparamInfo
        public boolean isWrapped;
        public boolean explicitcontext = false;
        public MetadataParamInfo retPart; // info about return param
    }

    /* information about a parameter */
    public class MetadataParamInfo {
        public QName headerMessage; // header message QName if soapHeader
        public int position; // param position
        public String javaType; // java class name of parameter
        public String partName; // WSDL part name
        public String mode;   // IN, OUT, or INOUT
        public boolean isSoapHeader;
    }

    private void debug (String msg) {
        if (DEBUG != null) {
            System.out.println("[J2EEModelInfo] ==> " + msg);
        }
    }
    private static String DEBUG = System.getProperty("com.sun.xml.rpc.j2ee.debug");
};
