/*
 * $Id: J2EEModeler112.java,v 1.1 2006-04-12 20:34:57 kohlert Exp $
*/

package com.sun.xml.rpc.processor.modeler.j2ee;

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

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
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler112;
import com.sun.xml.rpc.wsdl.document.Message;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
/**
 *
 * @author JAX-RPC RI Development Team
 */
public class J2EEModeler112 extends WSDLModeler112 implements J2EEModelerIf {


    public J2EEModeler112(J2EEModelInfo modelInfo, Properties options) {
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

        return new J2EESchemaAnalyzer112(
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
    protected boolean useExplicitServiceContextForRpcEncoded(
        Message inputMessage) {
            
        return helper.useExplicitServiceContextForRpcEncoded(inputMessage);
    }

    protected boolean isUnwrappable(Message inputMessage) {
        boolean unwrap = helper.isUnwrappable(inputMessage);
        if (unwrap) {
            this.info.operation.setProperty("J2EE_UNWRAP", "true");
        }
        return unwrap;
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

    public boolean useSuperExplicitServiceContextForDocLit(
        Message inputMessage) {
            
        return super.useExplicitServiceContextForDocLit(inputMessage);
    }

    public boolean useSuperExplicitServiceContextForRpcLit(
        Message inputMessage) {
            
        return super.useExplicitServiceContextForRpcLit(inputMessage);
    }

    public boolean useSuperExplicitServiceContextForRpcEncoded(
        Message inputMessage) {
            
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
    
    public boolean isConflictingServiceClassName(String name) {
        return false;
    }

    public boolean isConflictingPortClassName(String name) {
        return false;
    }

    public boolean isConflictingExceptionClassName(String name) {
        return false;
    }
    
    private static final String PROPERTY_OPERATION_JAVA_NAME =
        "com.sun.enterprise.webservice.mapping.operationJavaName";
    private static final String WSDL_PARAMETER_ORDER =
        "com.sun.xml.rpc.processor.modeler.wsdl.parameterOrder";
    private J2EEModelerHelper helper;
}
