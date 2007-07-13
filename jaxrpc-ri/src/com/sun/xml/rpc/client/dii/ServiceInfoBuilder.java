/*
 * $Id: ServiceInfoBuilder.java,v 1.3 2007-07-13 23:35:56 ofung Exp $
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

import com.sun.xml.rpc.client.dii.webservice.WebService;
import com.sun.xml.rpc.client.dii.webservice.WebServicesClient;
import com.sun.xml.rpc.client.dii.webservice.parser.WebServicesClientParser;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.model.*;
import com.sun.xml.rpc.processor.model.exporter.ModelImporter;
import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ClientProcessorEnvironment;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.document.soap.SOAPUse;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ServiceInfoBuilder {
    protected WSDLModelInfo modelInfo;
    protected QName serviceName;
    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants = null;
    private Package[] packages;

    private void init(SOAPVersion ver) {
        soapEncodingConstants = SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public ServiceInfoBuilder(String wsdlLocation, QName serviceName) {
        this(wsdlLocation, serviceName, SOAPVersion.SOAP_11);
    }

    public ServiceInfoBuilder(String wsdlLocation, QName serviceName, SOAPVersion ver) {
        init(ver); // Initialize SOAP constants

        ClientProcessorEnvironment env = new ClientProcessorEnvironment(new ByteArrayOutputStream(), null, null);

        Configuration config = new Configuration(env);

        modelInfo = new WSDLModelInfo();

        config.setModelInfo(modelInfo);
        modelInfo.setParent(config);

        modelInfo.setLocation(wsdlLocation);

        this.serviceName = serviceName;
        modelInfo.setName(serviceName.getLocalPart());
    }

    public void setPackage(String packageName) {
        modelInfo.setJavaPackageName(packageName);
    }

    public ServiceInfo buildServiceInfo() throws ServiceException {

        Model wsdlModel = null;
        //try to get model via client.xml file
        wsdlModel = getModel(modelInfo.getLocation());

        if (wsdlModel == null) {
            //use default properties-this may not work
            wsdlModel = getModel(true, false);
        }

        Service serviceModel = wsdlModel.getServiceByName(serviceName);

        if (serviceModel == null) {
            String knownServiceNames = "";
            Iterator eachService = wsdlModel.getServices();
            while (eachService.hasNext()) {
                Service service = (Service) eachService.next();

                knownServiceNames += service.getName().toString();
                if (eachService.hasNext()) {
                    knownServiceNames += "\n";
                }
            }
            throw new DynamicInvocationException("dii.wsdl.service.unknown", new Object[]{serviceName, knownServiceNames});
        }

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setDefaultNamespace(wsdlModel.getTargetNamespaceURI());
        Iterator eachPort = serviceModel.getPorts();
        while (eachPort.hasNext()) {
            Port portModel = (Port) eachPort.next();
            PortInfo portInfo = serviceInfo.getPortInfo((QName) portModel.getProperty(ModelProperties.PROPERTY_WSDL_PORT_NAME));
            portInfo.setPortTypeName((QName) portModel.getProperty(ModelProperties.PROPERTY_WSDL_PORT_TYPE_NAME));

            buildPortInfo(portInfo, portModel);
        }

        return serviceInfo;
    }

    protected Model getModel() {
        Properties options = new Properties();

        options.put(ProcessorOptions.VALIDATE_WSDL_PROPERTY, new Boolean(false));
        options.put(ProcessorOptions.EXPLICIT_SERVICE_CONTEXT_PROPERTY, new Boolean(false));
        options.put(ProcessorOptions.USE_WSI_BASIC_PROFILE, "true");
        options.put(ProcessorOptions.UNWRAP_DOC_LITERAL_WRAPPERS, "false");
        return modelInfo.buildModel(options);
    }

    protected Model getModel(boolean useWSI, boolean done) throws ServiceException, ModelerException {
        Properties options = new Properties();
        //todo:check these properties and settings:
        options.put(ProcessorOptions.VALIDATE_WSDL_PROPERTY, new Boolean(false));
        options.put(ProcessorOptions.EXPLICIT_SERVICE_CONTEXT_PROPERTY, new Boolean(false));

        if (useWSI) {
            options.put(ProcessorOptions.USE_WSI_BASIC_PROFILE, "true");
            options.put(ProcessorOptions.UNWRAP_DOC_LITERAL_WRAPPERS, "false");
        }

        Model model = null;
        try {
            model = modelInfo.buildModel(options);
        } catch (ModelerException ex) {
            if (!done) {
                model = getModel(false, true);
            } else
                throw ex;
        }
        return model;
    }

    protected void buildPortInfo(PortInfo portInfo, Port portModel) {
        portInfo.setTargetEndpoint(portModel.getAddress());

        Iterator eachOperation = portModel.getOperations();
        while (eachOperation.hasNext()) {
            Operation operationModel = (Operation) eachOperation.next();
            OperationInfo operationInfo = portInfo.createOperationForName(operationModel.getName().getLocalPart());

            buildOperationInfo(operationInfo, operationModel);
        }
    }

    private Package[] getPackages() {
        if (packages == null)
            packages = Package.getPackages();

        return packages;
    }

    //modified
    protected void buildOperationInfo(OperationInfo operationInfo, Operation operationModel) {
        if ((operationModel.getStyle() == SOAPStyle.DOCUMENT) && (operationModel.getUse() == SOAPUse.LITERAL)) {
            buildDocumentOperation(operationInfo, operationModel);
        } else {
            if ((operationModel.getStyle() == SOAPStyle.RPC) && (operationModel.getUse() == SOAPUse.LITERAL)) {
                buildRpcLiteralOperation(operationInfo, operationModel);
            } else {
                if ((operationModel.getStyle() == SOAPStyle.RPC) && (operationModel.getUse() == SOAPUse.ENCODED))
                    buildRpcOperation(operationInfo, operationModel);
            }
        }
    }

    protected void buildRpcOperation(OperationInfo operationInfo, Operation operationModel) {

        JavaMethod methodModel = operationModel.getJavaMethod();
        Request requestModel = operationModel.getRequest();
        Response responseModel = operationModel.getResponse();

        Block requestWsdlModel = (Block) requestModel.getBodyBlocks().next();

        Block responseWsdlModel = null;
        if (responseModel != null)
            responseWsdlModel = (Block) responseModel.getBodyBlocks().next();
        else
            operationInfo.setIsOneWay(true);

        RPCRequestUnorderedStructureType requestType = (RPCRequestUnorderedStructureType) requestWsdlModel.getType();

        RPCResponseStructureType responseType = null;
        if (responseWsdlModel != null)
            responseType = (RPCResponseStructureType) responseWsdlModel.getType();

        Iterator eachRequestParameter = requestModel.getParameters();
        Iterator eachResponseParameter = null;
        if (responseModel != null)
            eachResponseParameter = responseModel.getParameters();
        Iterator eachRequestWsdlParameter = requestType.getMembers();

        Iterator eachResponseWsdlParameter = null;
        if (responseType != null)
            eachResponseWsdlParameter = responseType.getMembers();

        JavaType returnJavaTypeModel = methodModel.getReturnType();
        if (returnJavaTypeModel != null) {
            if (!"void".equals(returnJavaTypeModel.getName())) {
                Parameter returnParameter = (Parameter) eachResponseParameter.next();
// this parameter must be consumed even if it is not currently being used
                SOAPStructureMember returnWsdlParameter = (SOAPStructureMember) eachResponseWsdlParameter.next();
//QName returnParameterName = returnWsdlParameter.getName();
                QName returnXmlType = returnParameter.getType().getName();
                Class returnJavaType = getJavaClassFor(returnParameter.getType());
                operationInfo.setReturnType(returnXmlType, returnJavaType);
            }
        }

        while (eachRequestParameter.hasNext()) {
            Parameter currentParameter = (Parameter) eachRequestParameter.next();
            SOAPStructureMember currentWsdlParameter = (SOAPStructureMember) eachRequestWsdlParameter.next();

            ParameterMode mode = ParameterMode.IN;
            if (currentParameter.getLinkedParameter() != null) {
                mode = ParameterMode.INOUT;
            }

            addParameterTo(operationInfo, currentWsdlParameter, mode);
        }
        if (eachResponseParameter != null) {
            while (eachResponseParameter.hasNext()) {
                Parameter currentParameter = (Parameter) eachResponseParameter.next();
                SOAPStructureMember currentWsdlParameter = (SOAPStructureMember) eachResponseWsdlParameter.next();

                if (currentParameter.getLinkedParameter() != null) {
                    continue;
                }

                addParameterTo(operationInfo, currentWsdlParameter, ParameterMode.OUT);
            }
        }

        operationInfo.setProperty(Call.OPERATION_STYLE_PROPERTY, "rpc");
        operationInfo.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, soapEncodingConstants.getURIEncoding());
    }

    protected void buildDocumentOperation(OperationInfo operationInfo, Operation operationModel) {
        JavaMethod methodModel = operationModel.getJavaMethod();
        Request requestModel = operationModel.getRequest();
        Response responseModel = operationModel.getResponse();
        Block requestWsdlModel = null;
        Block responseWsdlModel = null;
        Iterator eachRequestParameter = null;
        Iterator eachResponseParameter = null;

        if (requestModel != null) {
            Iterator requestIter = requestModel.getBodyBlocks();
            if (requestIter.hasNext())
                requestWsdlModel = (Block) requestIter.next();
            eachRequestParameter = requestModel.getParameters();
        }
        if (responseModel != null) {
            Iterator responseIter = responseModel.getBodyBlocks();
            if (responseIter.hasNext())
                responseWsdlModel = (Block) responseIter.next();
            eachResponseParameter = responseModel.getParameters();
        } else
            operationInfo.setIsOneWay(true);

        LiteralType requestType = null;
        QName requestWsdlName = null;
        QName requestXmlType = null;
        if (requestWsdlModel != null) {
            requestType = (LiteralType) requestWsdlModel.getType();
            requestXmlType = requestType.getName();
            requestWsdlName = requestWsdlModel.getName();
        }
        LiteralType responseType = null;
        QName responseWsdlName = null;
        if (responseWsdlModel != null) {
            responseType = (LiteralType) responseWsdlModel.getType();
            responseWsdlName = responseWsdlModel.getName();
        }

        //make sure it's not void
        JavaType returnJavaTypeModel = methodModel.getReturnType();
        if (returnJavaTypeModel != null) {
            if (!"void".equals(returnJavaTypeModel.getName())) {
                if (eachResponseParameter.hasNext()) {
                    Parameter returnParameter = (Parameter) eachResponseParameter.next();
                    LiteralType returnParameterLitType = (LiteralType) returnParameter.getType();

                    // LiteralElementMember returnWsdlParameter = (LiteralElementMember) eachResponseWsdlParameter.next();
                    //LiteralType parameterParameterLitType = returnWsdlParameter.getType();
                    ArrayList memberInfos = new ArrayList();
                    if ((returnParameterLitType instanceof LiteralSequenceType)
                            || (returnParameterLitType instanceof LiteralStructuredType)) {
                        Iterator members =
                                ((LiteralStructuredType) returnParameterLitType).getElementMembers();
                        while (members.hasNext()) {
                            LiteralElementMember member = (LiteralElementMember) members.next();
                            String memName = member.getName().getLocalPart();
                            LiteralType memberType = member.getType();
                            JavaType javaType = memberType.getJavaType();
                            Class javaClass = getJavaClassFor(memberType);
                            QName xmlTypeName = memberType.getName();
                            ParameterMemberInfo info = new ParameterMemberInfo();
                            info.addParameterMemberInfo(memName, xmlTypeName, javaClass);
                            memberInfos.add(info);
                        }
                    }

                    operationInfo.setReturnTypeQName(returnParameterLitType.getName());
                    operationInfo.setReturnMembers(memberInfos);
                    //maybe can use 3rd param javaStructureMem
                    operationInfo.setReturnTypeModel(new LiteralElementMember(responseWsdlName, responseType));
                    Class returnJavaType = getJavaClassFor(returnParameter.getType());
                    operationInfo.setReturnClassName(((JavaType) returnParameter.getType().getJavaType()).getName());
                    //todo:need to add qname of return type??
                    operationInfo.setReturnType(responseWsdlName, returnJavaType);
                    operationInfo.setReturnTypeQName(returnParameterLitType.getName());
                    operationInfo.setReturnMembers(memberInfos);
                }
            }
        }

        while (eachRequestParameter.hasNext()) {
            Parameter currentParameter = (Parameter) eachRequestParameter.next();

            LiteralType currentParameterLitType = (LiteralType) currentParameter.getType();

            // LiteralElementMember returnWsdlParameter = (LiteralElementMember) eachResponseWsdlParameter.next();
            //LiteralType parameterParameterLitType = returnWsdlParameter.getType();
            ArrayList memberInfos = new ArrayList();
            if ((currentParameterLitType instanceof LiteralSequenceType)
                    || (currentParameterLitType instanceof LiteralStructuredType)) {
                Iterator members =
                        ((LiteralStructuredType) currentParameterLitType).getElementMembers();
                while (members.hasNext()) {
                    LiteralElementMember member = (LiteralElementMember) members.next();
                    String memName = member.getName().getLocalPart();
                    LiteralType memberType = member.getType();
                    JavaType javaType = memberType.getJavaType();
                    Class javaClass = getJavaClassFor(memberType);
                    QName xmlTypeName = memberType.getName();
                    ParameterMemberInfo info = new ParameterMemberInfo();
                    info.addParameterMemberInfo(memName, xmlTypeName, javaClass);
                    memberInfos.add(info);
                }
            }


            ParameterMode mode = ParameterMode.IN;
            if (currentParameter.getLinkedParameter() != null) {
                mode = ParameterMode.INOUT;
            }
            operationInfo.addMemberInfos(memberInfos);
            Class parameterJavaType = getJavaClassFor(currentParameter.getType());
            //todo:add java structure member as well
            operationInfo.addParameterModel(requestWsdlName.getLocalPart(),
                    new LiteralElementMember(requestWsdlName, requestType));

            //operationInfo.addParameter(requestWsdlName.getLocalPart(),
            //                           requestWsdlName, parameterJavaType, mode);
            //operationInfo.addParameterXmlTypeQName(currentParameterLitType.getName());
            operationInfo.addParameter(requestWsdlName.getLocalPart(),
                    requestWsdlName, parameterJavaType, mode);
            operationInfo.addMemberInfos(memberInfos);
            operationInfo.addParameterXmlTypeQName(currentParameterLitType.getName());

        }

        //bug fix -todo:kw get bug number
        if (eachResponseParameter != null) {
            while (eachResponseParameter.hasNext()) {
                Parameter currentParameter = (Parameter) eachResponseParameter.next();
                LiteralType currentParameterLitType = (LiteralType) currentParameter.getType();

                // LiteralElementMember returnWsdlParameter = (LiteralElementMember) eachResponseWsdlParameter.next();
                //LiteralType parameterParameterLitType = returnWsdlParameter.getType();
                ArrayList memberInfos = new ArrayList();
                if ((currentParameterLitType instanceof LiteralSequenceType)
                        || (currentParameterLitType instanceof LiteralStructuredType)) {
                    Iterator members =
                            ((LiteralStructuredType) currentParameterLitType).getElementMembers();
                    while (members.hasNext()) {
                        LiteralElementMember member = (LiteralElementMember) members.next();
                        String memName = member.getName().getLocalPart();
                        LiteralType memberType = member.getType();
                        JavaType javaType = memberType.getJavaType();
                        Class javaClass = getJavaClassFor(memberType);
                        QName xmlTypeName = memberType.getName();
                        ParameterMemberInfo info = new ParameterMemberInfo();
                        info.addParameterMemberInfo(memName, xmlTypeName, javaClass);
                        memberInfos.add(info);
                    }
                }
                operationInfo.addMemberInfos(memberInfos);

                if (currentParameter.getLinkedParameter() != null) {
                    continue;
                }

                Class parameterJavaType = getJavaClassFor(currentParameter.getType());
                //todo:add java structure member as well
                operationInfo.addParameterModel(requestWsdlName.getLocalPart(),
                        new LiteralElementMember(requestWsdlName, requestType));
                operationInfo.addParameterXmlTypeQName(currentParameterLitType.getName());
                operationInfo.addParameter(responseWsdlName.getLocalPart(),
                        responseWsdlName, parameterJavaType, ParameterMode.OUT);
                operationInfo.addParameterXmlTypeQName(currentParameterLitType.getName());
                operationInfo.addMemberInfos(memberInfos);
            }
        }

        if (operationModel.getStyle() == SOAPStyle.DOCUMENT)
            operationInfo.setProperty(Call.OPERATION_STYLE_PROPERTY, "document");
        operationInfo.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");
    }

    //needs same checking as buildRPCOperation for nulls-
    protected void buildRpcLiteralOperation(OperationInfo operationInfo, Operation operationModel) {
        JavaMethod methodModel = operationModel.getJavaMethod();
        Request requestModel = operationModel.getRequest();
        Response responseModel = operationModel.getResponse();

        Block requestWsdlModel = null;
        Block responseWsdlModel = null;
        LiteralStructuredType requestType = null;
        LiteralStructuredType responseType = null;
        QName requestWsdlName = null;
        QName responseWsdlName = null;
        Iterator eachRequestParameter = null;
        Iterator eachResponseParameter = null;
        Iterator eachRequestWsdlParameter = null;
        Iterator eachResponseWsdlParameter = null;

        if (requestModel != null) {
            requestWsdlModel = (Block) requestModel.getBodyBlocks().next();
            eachRequestParameter = requestModel.getParameters();
            if (requestWsdlModel != null) {
                requestType = (LiteralStructuredType) requestWsdlModel.getType();
                eachRequestWsdlParameter = requestType.getElementMembers();
                requestWsdlName = requestWsdlModel.getName();
            }
        }

        if (responseModel != null) {
            responseWsdlModel = (Block) responseModel.getBodyBlocks().next();
            eachResponseParameter = responseModel.getParameters();
            if (responseWsdlModel != null) {
                responseType = (LiteralStructuredType) responseWsdlModel.getType();
                eachResponseWsdlParameter = responseType.getElementMembers();
                responseWsdlName = responseWsdlModel.getName();
            }
        } else
            operationInfo.setIsOneWay(true);

        operationInfo.setRequestQName(requestWsdlName);
        operationInfo.setResponseQName(responseWsdlName);

        JavaType returnJavaTypeModel = methodModel.getReturnType();
        if (returnJavaTypeModel != null) {
            if (!"void".equals(returnJavaTypeModel.getName())) {
                if (eachResponseParameter != null) {
                    Parameter returnParameter = (Parameter) eachResponseParameter.next();
                    // this parameter must be consumed even if it is not currently being used
                    LiteralElementMember returnWsdlParameter = (LiteralElementMember) eachResponseWsdlParameter.next();
                    LiteralType parameterParameterLitType = returnWsdlParameter.getType();
                    ArrayList memberInfos = new ArrayList();
                    if ((parameterParameterLitType instanceof LiteralSequenceType)
                            || (parameterParameterLitType instanceof LiteralStructuredType)) {
                        Iterator members =
                                ((LiteralStructuredType) parameterParameterLitType).getElementMembers();
                        while (members.hasNext()) {
                            LiteralElementMember member = (LiteralElementMember) members.next();
                            String memName = member.getName().getLocalPart();
                            LiteralType memberType = member.getType();
                            JavaType javaType = memberType.getJavaType();
                            Class javaClass = getJavaClassFor(memberType);
                            QName xmlTypeName = memberType.getName();
                            ParameterMemberInfo info = new ParameterMemberInfo();
                            info.addParameterMemberInfo(memName, xmlTypeName, javaClass);
                            memberInfos.add(info);
                        }
                    }
                    operationInfo.setReturnTypeModel(returnWsdlParameter);
                    operationInfo.setReturnMembers(memberInfos);
                    QName returnXmlType = returnParameter.getType().getName();
                    String parameterName =
                            returnParameter.getType().getName().getLocalPart();
                    Class returnJavaType = getJavaClassFor(returnParameter.getType());
                    //need to add qname
                    operationInfo.setReturnClassName(returnParameter.getType().getJavaType().getName());
                    operationInfo.setReturnType(returnXmlType, returnJavaType);
                }
            }
        }

        if (eachRequestParameter != null) {
            while (eachRequestParameter.hasNext()) {
                Parameter currentParameter = (Parameter) eachRequestParameter.next();
                LiteralElementMember currentWsdlParameter = (LiteralElementMember) eachRequestWsdlParameter.next();
                LiteralType parameterParameterLitType = currentWsdlParameter.getType();
                ArrayList memberInfos = new ArrayList();
                if ((parameterParameterLitType instanceof LiteralSequenceType)
                        || (parameterParameterLitType instanceof LiteralStructuredType)) {
                    Iterator members =
                            ((LiteralStructuredType) parameterParameterLitType).getElementMembers();
                    while (members.hasNext()) {
                        LiteralElementMember member = (LiteralElementMember) members.next();
                        String memName = member.getName().getLocalPart();
                        LiteralType memberType = member.getType();
                        JavaType javaType = memberType.getJavaType();
                        Class javaClass = getJavaClassFor(memberType);
                        QName xmlTypeName = memberType.getName();
                        ParameterMemberInfo info = new ParameterMemberInfo();
                        info.addParameterMemberInfo(memName, xmlTypeName, javaClass);
                        memberInfos.add(info);
                    }
                }

                ParameterMode mode = ParameterMode.IN;
                if (currentParameter.getLinkedParameter() != null) {
                    mode = ParameterMode.INOUT;
                }
                operationInfo.addMemberInfos(memberInfos);
                addParameterTo(operationInfo, currentWsdlParameter, mode);
            }
        }
        if (eachResponseParameter != null) {
            while (eachResponseParameter.hasNext()) {
                Parameter currentParameter = (Parameter) eachResponseParameter.next();
                LiteralElementMember currentWsdlParameter = (LiteralElementMember) eachResponseWsdlParameter.next();
                LiteralType parameterParameterLitType = currentWsdlParameter.getType();
                ArrayList memberInfos = new ArrayList();
                if ((parameterParameterLitType instanceof LiteralSequenceType)
                        || (parameterParameterLitType instanceof LiteralStructuredType)) {
                    Iterator members =
                            ((LiteralStructuredType) parameterParameterLitType).getElementMembers();
                    while (members.hasNext()) {
                        LiteralElementMember member = (LiteralElementMember) members.next();
                        String memName = member.getName().getLocalPart();
                        LiteralType memberType = member.getType();
                        JavaType javaType = memberType.getJavaType();
                        Class javaClass = getJavaClassFor(memberType);
                        QName xmlTypeName = memberType.getName();
                        ParameterMemberInfo info = new ParameterMemberInfo();
                        info.addParameterMemberInfo(memName, xmlTypeName, javaClass);
                        memberInfos.add(info);
                    }
                }
                if (currentParameter.getLinkedParameter() != null) {
                    continue;
                }
                operationInfo.addMemberInfos(memberInfos);
                addParameterTo(operationInfo, currentWsdlParameter, ParameterMode.OUT);
            }
        }

        //kw modified
        operationInfo.setProperty(Call.OPERATION_STYLE_PROPERTY, "rpc");
        operationInfo.setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, "");
    }


    protected void addParameterTo(OperationInfo operationInfo, SOAPStructureMember currentParameter, ParameterMode mode) {
        String parameterName = currentParameter.getName().getLocalPart();
        QName parameterXmlType = currentParameter.getType().getName();
        Class parameterJavaType = getJavaClassFor(currentParameter.getType());

        operationInfo.addParameter(parameterName, parameterXmlType, parameterJavaType, mode);
    }

    protected void addParameterTo(OperationInfo operationInfo,
                                  LiteralElementMember currentParameter,
                                  ParameterMode mode) {
        String parameterName = currentParameter.getName().getLocalPart();
        QName parameterXmlType = currentParameter.getType().getName();
        Class parameterJavaType = getJavaClassFor(currentParameter.getType());

        operationInfo.addParameterModel(parameterName, currentParameter);
        operationInfo.addParameter(parameterName, parameterXmlType, parameterJavaType, mode);
    }

//need lookup table for std types
    //kw modified - needs rewrite todo - temporary
    protected Class getJavaClassFor(AbstractType parameterType) {
        JavaType javaType = parameterType.getJavaType();

        String parameterJavaTypeName = javaType.getName();
        //need to clean this up - use constants
        if (parameterJavaTypeName.indexOf("[][]") != -1) {
            String elementTypeName = getArrayElementTypeName((JavaType) javaType);
            if (elementTypeName != null)
                parameterJavaTypeName = "[[L" + elementTypeName + ";";
        } else if (parameterJavaTypeName.indexOf("[]") != -1) {
            String elementTypeName = getArrayElementTypeName((JavaType) javaType);
            if (elementTypeName != null)
                parameterJavaTypeName = "[L" + elementTypeName + ";";
        }
        Class parameterJavaType = null;
        try {
            if (parameterJavaTypeName != null) {
                parameterJavaType =
                        getClassForName(parameterJavaTypeName, Arrays.asList(getPackages()));
                return parameterJavaType;
            }
        } catch (java.lang.NoClassDefFoundError e) {
            String message = e.getMessage();
            if (message.indexOf("wrong name:") != -1) {
                String qualifiedName = findQualifiedName(message);
                if (qualifiedName != null)
                    try {
                        parameterJavaType = Class.forName(qualifiedName);
                    } catch (Exception ex) {
//for debugging only
//e.printStackTrace();
                    }
            }
        } catch (Exception ex) {
            //todo:need to log
            //System.out.println("ServiceInfoBuilder " + ex.getMessage());
        }
        return parameterJavaType;
    }

    private String getArrayElementTypeName(JavaType javaType) {
        JavaType elementType = null;
        if (javaType instanceof JavaArrayType)
            elementType = ((JavaArrayType) javaType).getElementType();
        if (elementType != null)
            return elementType.getName();
        return null;
    }

    //workaround need package info
    private String findQualifiedName(String message) {

        StringTokenizer tokens = new StringTokenizer(message);
        while (tokens.hasMoreElements()) {
            String currentToken = tokens.nextToken();
            if (currentToken.equals("name:")) {
                String qualifiedToken = tokens.nextToken();
                if (qualifiedToken.indexOf("\\") != -1) {
                    qualifiedToken = qualifiedToken.replace('\\', '.');

                } else {
                    if (qualifiedToken.indexOf("/") != -1)
                        qualifiedToken = qualifiedToken.replace('/', '.');
                }
                qualifiedToken = qualifiedToken.replace(')', ' ');
                return qualifiedToken.trim();
            }
        }
        return null;
    }

    protected Class getClassForName(String name, Collection packages) {
        // first type the class name alone
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException cnfe) {
            //ok now try with the known packages
            Iterator iter = packages.iterator();
            while (iter.hasNext()) {
                String qualifiedName = (String) ((Package) iter.next()).getName() + "." + name;
                try {
                    Class kclass = Class.forName(qualifiedName);
                    if (kclass != null)
                        return kclass;
                } catch (ClassNotFoundException cfe) {
                    continue;
                }
            }
        }
        return null;
    }

    private Model getModel(String wsdlLocation) throws ServiceException {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Model model = null;
        try {
            Enumeration urls = null;
            try {
                urls = loader.getResources("META-INF/client.xml");
                if ((urls == null) || (!urls.hasMoreElements()))
                    urls = loader.getResources("client.xml");
            } catch (IOException e) {
                throw new ServiceException(e);
            }
            //use first one found
            if (urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();

                InputStream in = null;
                try {
                    in = url.openStream();
                } catch (IOException e) {
                    throw new ServiceException(e);
                }
                WebServicesClientParser parser = new WebServicesClientParser();
                WebServicesClient client = parser.parse(in);
                Iterator iter = client.getWebServices().iterator();
                while (iter.hasNext()) {

                    WebService webservice = (WebService) iter.next();
                    String webServiceLocation = webservice.getWsdlLocation();
                    String webServiceModel = webservice.getModel();

                    if (wsdlLocation.equalsIgnoreCase(webServiceLocation)) {

                        URL modelURL = null;
                        try {
                            modelURL = new URL(webServiceModel);
                        } catch (MalformedURLException e) {
                            throw new ServiceException(e);
                        }
                        InputStream is = null;
                        try {
                            is = modelURL.openStream();
                        } catch (IOException e) {
                            throw new ServiceException(e);
                        }

                        ModelImporter importer = new ModelImporter(is);
                        model = importer.doImport();
                        return model;
                    }
                }
            }
        } catch (ModelException ex) {
            throw new ServiceException(ex);
        }
        return model;
    }
}


