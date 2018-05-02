/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.rpc.processor.modeler.j2ee;

import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.J2EEModelInfo;
import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler111;
import com.sun.xml.rpc.wsdl.document.Message;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
/**
 *
 * @author JAX-RPC RI Development Team
 */
public class J2EEModeler111 extends WSDLModeler111 implements J2EEModelerIf {

    public J2EEModeler111(J2EEModelInfo modelInfo, Properties options) {
        super(modelInfo, options);
        helper = new J2EEModelerHelper(this, modelInfo);
    }

    /**
     * @param document
     * @param _modelInfo
     * @param _options
     * @param _conflictingClassNames
     * @param _javaTypes
     * @return
     */
    protected SchemaAnalyzerBase getSchemaAnalyzerInstance(   
        WSDLDocument document,
        WSDLModelInfo _modelInfo,
        Properties _options,
        Set _conflictingClassNames,
        JavaSimpleTypeCreator _javaTypes) {

        return new J2EESchemaAnalyzer111(
            document,
            (J2EEModelInfo) _modelInfo,
            _options,
            _conflictingClassNames,
            _javaTypes);
    }

    protected String getServiceInterfaceName(
        QName serviceQName,
        com.sun.xml.rpc.wsdl.document.Service wsdlService) {
        return helper.getServiceInterfaceName(serviceQName, wsdlService);
    }

    protected String getJavaNameOfPort(QName portQName) {
        return helper.getJavaNameOfPort(portQName);
    }

    protected void setJavaOperationNameProperty(Message inputMessage) {
        helper.setJavaOperationNameProperty(inputMessage);
    }


    /**
     * This is a complete hack. We should really be reading the mapping file and generate the
     * java methods.  Since we are retro-fitting the mapping information, we have to force
     * jaxrpc to create the explicit context, i.e. handling soap headerfault.
     */
    protected boolean useExplicitServiceContextForDocLit(Message inputMessage) {
        return helper.useExplicitServiceContextForDocLit(inputMessage);
    }

    /**
     * This is a complete hack. We should really be reading the mapping file and generate the
     * java methods.  Since we are retro-fitting the mapping information, we have to force
     * jaxrpc to create the explicit context, i.e. handling soap headerfault.
     */
    protected boolean useExplicitServiceContextForRpcLit(Message inputMessage) {
        return helper.useExplicitServiceContextForRpcLit(inputMessage);
    }
    

    /**
     * This is a complete hack. We should really be reading the mapping file and generate the
     * java methods.  Since we are retro-fitting the mapping information, we have to force
     * jaxrpc to create the explicit context, i.e. handling soap headerfault.
     */
    protected boolean useExplicitServiceContextForRpcEncoded(Message inputMessage) {
        return helper.useExplicitServiceContextForRpcEncoded(inputMessage);
    }

    protected boolean isUnwrappable(Message inputMessage) {
        return helper.isUnwrappable(inputMessage);
    }

    protected void setCurrentPort(Port port) {
        helper.setCurrentPort(port);
    }

    protected String getJavaNameOfSEI(Port port) {
        return helper.getJavaNameOfSEI(port);
    }

    public LiteralType getElementTypeToLiteralType(QName elementType) {        
        return helper.getElementTypeToLiteralType(elementType);
    }
    
    protected AbstractType verifyResultType(
        AbstractType type,
        Operation operation) {
            
        return helper.verifyResultType(type, operation);    
    }

    protected AbstractType verifyParameterType(
        AbstractType type,
        String partName,
        Operation operation) {
            
        return helper.verifyParameterType(type, partName, operation);
    }

    protected void postProcessSOAPOperation(Operation operation) {
        helper.postProcessSOAPOperation(operation);
    }

    protected WSDLExceptionInfo getExceptionInfo(Fault fault) {
        return helper.getExceptionInfo(fault);
    }

    protected void setSOAPUse() {
        helper.setSOAPUse();
    }

    protected String getJavaNameForOperation(Operation operation) {
        return helper.getJavaNameForOperation(operation);
    }

    public boolean useSuperExplicitServiceContextForDocLit(Message inputMessage) {
        return super.useExplicitServiceContextForDocLit(inputMessage);
    }

    public boolean useSuperExplicitServiceContextForRpcLit(Message inputMessage) {
        return super.useExplicitServiceContextForRpcLit(inputMessage);
    }

    public boolean useSuperExplicitServiceContextForRpcEncoded(Message inputMessage) {
        return super.useExplicitServiceContextForRpcEncoded(inputMessage);
    }

    public boolean isSuperUnwrappable() {
        return super.isUnwrappable();
    }

    public LiteralType getSuperElementTypeToLiteralType(QName elementType) {
        return super.getElementTypeToLiteralType(elementType);
    }

    public String getSuperJavaNameForOperation(Operation operation) {
        return super.getJavaNameForOperation(operation);
    }

    public ProcessSOAPOperationInfo getInfo() {
        return info;
    }

    public Message getSuperOutputMessage() {
        return super.getOutputMessage();
    }
    
    public Message getSuperInputMessage() {
        return super.getInputMessage();
    }
    
    public SOAPBody getSuperSOAPRequestBody() {
        return super.getSOAPRequestBody();
    }
    
    public SOAPBody getSuperSOAPResponseBody() {
        return super.getSOAPResponseBody();
    }

    public JavaSimpleTypeCreator getJavaTypes() {
        return _javaTypes;
    }
    
    private static final String PROPERTY_OPERATION_JAVA_NAME =
        "com.sun.enterprise.webservice.mapping.operationJavaName";
    private static final String WSDL_PARAMETER_ORDER =
        "com.sun.xml.rpc.processor.modeler.wsdl.parameterOrder";
    private J2EEModelerHelper helper;
}
