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
 * $Id: ConfiguredService.java,v 1.3 2007-07-13 23:35:56 ofung Exp $
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

import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMapping;

import com.sun.xml.rpc.client.BasicService;
import com.sun.xml.rpc.client.ServiceExceptionImpl;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPEncodingConstants;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.Holders;

/**
 * @author JAX-RPC Development Team
 */

public class ConfiguredService extends BasicService {
    protected java.net.URL wsdlDocumentLocation;
    protected ServiceInfo configuration;
    protected DynamicProxyBuilder dynamicProxyBuilder;
    private ServiceException serviceException;
    private SOAPEncodingConstants soapEncodingConstants = null;

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
                SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public ConfiguredService(QName name, URL wsdlLocation) {
        this(name, wsdlLocation, SOAPVersion.SOAP_11);
    }

    public ConfiguredService(QName name, URL wsdlLocation, SOAPVersion ver) {
        super(name);
        init(ver);
        //created by ServiceFactoryImpl.createService(service QName, wsdlLocaltion url)
        wsdlDocumentLocation = wsdlLocation;

        //create the ServiceInfoBuilder which will examine the service, collect service
        //information
        ServiceInfoBuilder configurationBuilder =
                new ServiceInfoBuilder(wsdlLocation.toExternalForm(), name);
        try {
            configuration = configurationBuilder.buildServiceInfo();
        } catch (ModelerException ex) {
            serviceException = new ServiceException(ex);
        } catch (ServiceException ex) {
            serviceException = ex;
        }
        if (configuration == null)
            return;

        //service information is collected, create the DynamicProxyBuilder
        dynamicProxyBuilder = createDynamicProxyBuilder();

        //add all the port names to the configured service
        Iterator eachPortName = configuration.getPortNames();
        while (eachPortName.hasNext()) {
            QName currentPortName = (QName) eachPortName.next();
            addPort(currentPortName);
        }
    }

    public ServiceException getServiceException() {
        return serviceException;
    }

    protected DynamicProxyBuilder createDynamicProxyBuilder() {
        return new DynamicProxyBuilder(internalTypeRegistry,
                getHandlerRegistry(),
                configuration);
    }

    public java.net.URL getWSDLDocumentLocation() {
        return wsdlDocumentLocation;
    }

    public Call[] getCalls(QName portName) throws ServiceException {
        ArrayList calls = new ArrayList();

        //get the portInformation by name
        PortInfo portInfo = getPortInfo(portName);
        //iterate through each port operation and create the call for
        //that operation and add to the call list
        //this method implements the java api method
        //service.getCalls(portname)
        Iterator eachOperation = portInfo.getOperations();
        while (eachOperation.hasNext()) {
            OperationInfo currentOperation =
                    (OperationInfo) eachOperation.next();
            calls.add(createCall(portName, currentOperation.getName()));
        }

        return (Call[]) calls.toArray(new Call[calls.size()]);
    }

    public Call createCall(QName portName, String operationName) throws ServiceException {
        return createCall(portName, new QName(operationName));
    }

    public Call createCall(QName portName, QName operationName)
            throws ServiceException {

        ConfiguredCall call = new ConfiguredCall(internalTypeRegistry, getHandlerRegistry(), configuration);
        call.setPortName(portName);
        call.setOperationName(operationName);

        return call;
    }

    protected PortInfo getPortInfo(QName portName) throws ServiceException {
        if (!ports.contains(portName)) {
            throw portNotFoundException(portName);
        }

        return configuration.getPortInfo(portName);
    }

    protected ServiceExceptionImpl portNotFoundException(QName portName) {
        return new ServiceExceptionImpl("dii.service.does.not.contain.port",
                new Object[]{name, portName});
    }

    public Iterator getPorts() {
        //implements Service.getPorts()
        return ports.iterator();
    }

    public Remote getPort(Class portInterface) throws ServiceException {
        //implements Service.getPort(port interface)
        QName portName = getPortNameForInterface(portInterface);
        return getPort(portName, portInterface);
    }

    protected QName getPortNameForInterface(Class portInterface) {
        TypeMapping mapping =
                typeRegistry.getTypeMapping(soapEncodingConstants.getSOAPEncodingNamespace());

        Iterator eachPortInfo = configuration.getPortNames();
        while (eachPortInfo.hasNext()) {
            QName currentPortName = (QName) eachPortInfo.next();
            PortInfo currentPort = configuration.getPortInfo(currentPortName);

            // find out if the currentPortInfo matches the interface
            // Does it have the same number of operations as the interface has methods?
            Method[] methods = portInterface.getMethods();
            if (currentPort.getOperationCount() != methods.length) {
                continue;
            }

            // For eachMethod in the interface
            boolean allMethodsMatched = true;
            for (int i = 0; i < methods.length; i++) {
                // Find out if the currentPortInfo has an operation that matches the method
                // For eachOperation in the currentPortInfo
                Iterator eachOperation = currentPort.getOperations();
                boolean operationMatchesMethod = false;
                while (eachOperation.hasNext()) {
                    OperationInfo currentOperation =
                            (OperationInfo) eachOperation.next();
                    // Does the currentOperation name match the method name?
                    if (!currentOperation
                            .getName()
                            .getLocalPart()
                            .equals(methods[i].getName())) {
                        continue;
                    }
                    // Does it have the same number of parameters?
                    //not fail proof currentOperation has request and response

                    Class[] parameters = methods[i].getParameterTypes();
                    int paramLength = parameters.length;

                    if (currentOperation.getParameterCount()
                            != paramLength) {
                        continue;
                    }
                    // For eachMethodParameter
                    boolean parametersMatched = true;

                    //can't count on registered serializers even for simple Types
                    //in docliteral and rpcliteral- or for arrays or beans
                    //skip this for now- change when get wsdl-
                    /* for (int j = 0; j < parameters.length; j++) {
                         parametersMatched = false;

                         // For eachOperationParameter
                         QName[] operationParameters =
                             currentOperation.getParameterXmlTypes();
                         for (int k = 0; k < operationParameters.length; k++) {
                             // If there is a typeMapping between the currentMethodParameter class and the currentOperationParameter xmlType
                             Class methodParameter =
                                 Holders.stripHolderClass(parameters[j]);

                             if (mapping
                                 .getSerializer(
                                     methodParameter,
                                     operationParameters[k])
                                 != null) {
                                 parametersMatched = true;
                                 break;
                             }
                         }
                         if (!parametersMatched) {
                             break;
                         }
                     }
                      */
                    // If all the parameters matched then consider the operation to match
                    if (parametersMatched) {
                        operationMatchesMethod = true;
                    }
                }
                if (!operationMatchesMethod) {
                    allMethodsMatched = false;
                    break;
                }
            }
            //If all the methods had a matching operation then the
            //currentPortInfo is a match. Return it's name.
            if (allMethodsMatched) {
                return currentPortName;
            }
        }
        return null;
    }

    public Remote getPort(QName portName, Class portInterface)
            throws ServiceException {
        //implements Service.getPort(QName, portInterface)
        //build dynamic proxy and returns
        return dynamicProxyBuilder.buildDynamicProxyFor(getPortInfo(portName),
                portInterface);
    }

}
