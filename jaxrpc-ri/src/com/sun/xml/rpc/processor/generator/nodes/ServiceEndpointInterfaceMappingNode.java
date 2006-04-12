/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.generator.nodes;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.StubTieGeneratorBase;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralWildcardMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;

/**
 * @author  Qingqing Ouyang
 * @version 
 */
public class ServiceEndpointInterfaceMappingNode extends JaxRpcMappingNode {

    /**
     * Default constructor.
     */
    public ServiceEndpointInterfaceMappingNode() {
    }

    /**
     * write the appropriate information to a DOM tree and return it
     *
     * @param parent node in the DOM tree 
     * @param nodeName name for the root element for this DOM tree fragment
     * @param model jaxrpc model to write
     * @param config jaxrpc configuration
     * @return the DOM tree top node
     */
    public Node write(
        Node parent,
        String nodeName,
        Configuration config,
        Port port)
        throws Exception {

        Element node = appendChild(parent, nodeName);

        ProcessorEnvironment env =
            (com.sun.xml.rpc.processor.util.ProcessorEnvironment) config
                .getEnvironment();

        JavaInterface intf = port.getJavaInterface();
        String className = env.getNames().customJavaTypeClassName(intf);
        QName portTypeQName =
            (QName) port.getProperty(
                ModelProperties.PROPERTY_WSDL_PORT_TYPE_NAME);
        QName bindingQName =
            (QName) port.getProperty(
                ModelProperties.PROPERTY_WSDL_BINDING_NAME);

        //service-endpoint-interface
        appendTextChild(
            node,
            JaxRpcMappingTagNames.SERVICE_ENDPOINT_INTERFACE,
            className);

        //wsdl-port-type
        //XXX FIXME  Need to handle QName better
        Element wsdlPortTypeNode =
            (Element) appendTextChild(node,
                JaxRpcMappingTagNames.WSDL_PORT_TYPE,
                "portTypeNS:" + portTypeQName.getLocalPart());
        wsdlPortTypeNode.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns:portTypeNS",
            portTypeQName.getNamespaceURI());

        //wsdl-binding
        //XXX FIXME  Need to handle QName better
        Element wsdlBindingNode =
            (Element) appendTextChild(node,
                JaxRpcMappingTagNames.WSDL_BINDING,
                "bindingNS:" + bindingQName.getLocalPart());
        wsdlBindingNode.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns:bindingNS",
            bindingQName.getNamespaceURI());

        //service-endpoint-method-mapping*
        for (Iterator iter = port.getOperations(); iter.hasNext();) {
            Operation operation = (Operation) iter.next();
            boolean isDocument =
                operation.getStyle() != null
                    && operation.getStyle().equals(SOAPStyle.DOCUMENT);
            JavaMethod method = operation.getJavaMethod();
            String methodName = method.getName();
            Request request = operation.getRequest();

            debug(
                MYNAME,
                "0000 request.class = " + request.getClass().getName());

            Iterator blocks = request.getBodyBlocks();
            int i = 0;
            while (blocks.hasNext()) {
                Block b = (Block) blocks.next();
                debug(
                    MYNAME,
                    "int i = " + b.getName() + "; type = " + b.getLocation());
                i++;
            }

            QName inputMsgQName =
                (QName) request.getProperty(
                    ModelProperties.PROPERTY_WSDL_MESSAGE_NAME);
            Response response = operation.getResponse();
            QName outputMsgQName = null;
            if (response != null) {
                outputMsgQName =
                    (QName) response.getProperty(
                        ModelProperties.PROPERTY_WSDL_MESSAGE_NAME);
            }

            /* isWrapped is true for doc-lit, where members of an
               "element" are mapped to individual input parameters */
            boolean isWrapped = false;
            Block requestBlock = null;
            AbstractType requestType = null;
            if (request.getBodyBlockCount() > 0) {
                requestBlock = (Block) request.getBodyBlocks().next();
                requestType = requestBlock.getType();
            }
            if (isDocument && request.getBodyBlockCount() > 0) {
                if (requestType instanceof LiteralSequenceType) {
                    isWrapped =
                        ((LiteralSequenceType) requestType).isUnwrapped();
                }
            }

            //service-endpoint-method-mapping
            Node semNode =
                appendChild(
                    node,
                    JaxRpcMappingTagNames.SERVICE_ENDPOINT_METHOD_MAPPING);

            //java-method-name
            appendTextChild(
                semNode,
                JaxRpcMappingTagNames.JAVA_METHOD_NAME,
                methodName);

            //wsdl-operation
            appendTextChild(
                semNode,
                JaxRpcMappingTagNames.WSDL_OPERATION,
                operation.getName().getLocalPart());

            //wrapped-element
            if (isWrapped) {
                forceAppendTextChild(
                    semNode,
                    JaxRpcMappingTagNames.WRAPPED_ELEMENT,
                    null);
            }

            int paramPos = 0;
            for (Iterator iter2 = method.getParameters();
                iter2.hasNext();
                paramPos++) {
                JavaParameter javaParam = (JavaParameter) iter2.next();
                Parameter param = javaParam.getParameter();
                Block paramBlock = param.getBlock();
                boolean soapHeader = paramBlock.getLocation() == Block.HEADER;

                String paramType = null;
                String paramMode = null;
                QName wsdlMsgQName = null;
                if (javaParam.isHolder()) {
                    paramType = javaParam.getType().getName();
                    if (param.getLinkedParameter() != null) {
                        /* A holder with a linked param is an inout param */
                        paramMode = "INOUT";
                        wsdlMsgQName = inputMsgQName;
                        debug(MYNAME, "0100 prameMode INOUT");
                    } else {
                        /* a holder without a linked param is an in param */
                        paramMode = "OUT";
                        wsdlMsgQName = outputMsgQName;
                        debug(MYNAME, "0200 prameMode OUT");
                    }
                } else {
                    /* not a holder, must be an input param */
                    paramMode = "IN";
                    // bug fix: 4927549
                    paramType = getJavaTypeName(javaParam.getType());
                    wsdlMsgQName = inputMsgQName;
                    debug(MYNAME, "0300 prameMode IN");
                }

                //NOTE the soap header points to a different message than that
                //of the request
                if (soapHeader) {
                    wsdlMsgQName =
                        (QName) paramBlock.getProperty(
                            ModelProperties.PROPERTY_WSDL_MESSAGE_NAME);
                }

                debug(
                    MYNAME,
                    "0301 paramName = "
                        + javaParam.getName()
                        + "; soapHeader = "
                        + soapHeader);

                String partName = null;
                // bug fix: 4931493
                partName =
                    (String) param.getProperty(
                        ModelProperties.PROPERTY_PARAM_MESSAGE_PART_NAME);
                debug(MYNAME, "0206 property partName = " + partName);
                // bug fix: 4931493, the if statment was added
                if (partName == null) {
                    if (soapHeader) {
                        // bug fix: 4931493
                        // partName = javaParam.getName();
                        partName =
                            paramBlock.getType().getName().getLocalPart();

                        debug(MYNAME, "0302 header partName = " + partName);
                    } else {
                        JavaStructureMember javaStructMember =
                            StubTieGeneratorBase.getJavaMember(param);

                        if (javaStructMember == null) {
                            partName = javaParam.getName();
                        } else {

                            Object owner = javaStructMember.getOwner();
                            debug(
                                MYNAME,
                                "0400 owner.class = "
                                    + owner.getClass().getName());

                            if (owner instanceof SOAPStructureMember) {
                                /* RPC/Encoded */
                                SOAPStructureMember soapStructMember =
                                    (SOAPStructureMember) owner;
                                partName =
                                    soapStructMember.getName().getLocalPart();
                            } else if (owner instanceof LiteralElementMember) {
                                /* doc-lit, wrapped */

                                LiteralElementMember litMember =
                                    (LiteralElementMember) owner;

                                debug(
                                    MYNAME,
                                    "0500 LiteralElementMember litMember.getName = "
                                        + litMember.getName());
                                debug(
                                    MYNAME,
                                    "0600 partName= "
                                        + (String) param.getProperty(
                                            ModelProperties
                                                .PROPERTY_PARAM_MESSAGE_PART_NAME));

                                if (owner instanceof LiteralWildcardMember) {
                                    partName =
                                        (String) param.getProperty(
                                            ModelProperties
                                                .PROPERTY_PARAM_MESSAGE_PART_NAME);
                                } else {
                                    partName =
                                        litMember.getName().getLocalPart();
                                }

                                debug(
                                    MYNAME,
                                    "0700 LiteralElementMember partName = "
                                        + partName);
                            } else {
                                /* must be doc-lit without being wrapped */
                                partName =
                                    ((String) param
                                        .getProperty(
                                            ModelProperties
                                                .PROPERTY_PARAM_MESSAGE_PART_NAME));
                                debug(MYNAME, "0800 partName = " + partName);
                            }
                            debug(
                                MYNAME,
                                "0303 not header partName = " + partName);
                        }
                    }
                }

                if (wsdlMsgQName == null) {
                    debug(MYNAME, "0900 wsdlMsgQName NULL");
                } else {
                    debug(MYNAME, "1000 wsdlMsgQName NOT NULL" + wsdlMsgQName);
                }

                debug(MYNAME, "1101 param-position = " + paramPos);
                debug(MYNAME, "1102 param-type = " + paramType);
                debug(MYNAME, "1103 partname = " + partName);
                debug(MYNAME, "1104 paramMode = " + paramMode);

                //method-param-parts-mapping
                Node methodParamPartsNode =
                    appendChild(
                        semNode,
                        JaxRpcMappingTagNames.METHOD_PARAM_PARTS_MAPPING);

                //param-position
                appendTextChild(
                    methodParamPartsNode,
                    JaxRpcMappingTagNames.PARAM_POSITION,
                    paramPos);

                //param-type
                appendTextChild(
                    methodParamPartsNode,
                    JaxRpcMappingTagNames.PARAM_TYPE,
                    paramType);

                //wsdl-message-mapping
                Node wsdlMsgMappingNode =
                    appendChild(
                        methodParamPartsNode,
                        JaxRpcMappingTagNames.WSDL_MESSAGE_MAPPING);

                //wsdl-message
                //XXX FIXME  Need to handle QName better
                Element wsdlMsg =
                    (Element) appendTextChild(wsdlMsgMappingNode,
                        JaxRpcMappingTagNames.WSDL_MESSAGE,
                        "wsdlMsgNS:" + wsdlMsgQName.getLocalPart());
                wsdlMsg.setAttributeNS(
                    "http://www.w3.org/2000/xmlns/",
                    "xmlns:wsdlMsgNS",
                    wsdlMsgQName.getNamespaceURI());

                //wsdl-message-part-name
                appendTextChild(
                    wsdlMsgMappingNode,
                    JaxRpcMappingTagNames.WSDL_MESSAGE_PART_NAME,
                    partName);

                //parameter-mode
                appendTextChild(
                    wsdlMsgMappingNode,
                    JaxRpcMappingTagNames.PARAMETER_MODE,
                    paramMode);

                //soap-header
                if (soapHeader) {
                    forceAppendTextChild(
                        wsdlMsgMappingNode,
                        JaxRpcMappingTagNames.SOAP_HEADER,
                        null);
                }

            }

            String retType;
            JavaType returnType = method.getReturnType();
            boolean hasReturn =
                returnType != null
                    && !returnType.getName().equals(
                        ModelerConstants.VOID_CLASSNAME);

            if (!hasReturn)
                retType = "void";
            else {
                // bug fix: 4927549
                retType = getJavaTypeName(method.getReturnType());
                //                retType = method.getReturnType().getRealName();
            }

            if (hasReturn) {

                //wsdl-return-value-mapping
                Node wsdlReturnValueNode =
                    appendChild(
                        semNode,
                        JaxRpcMappingTagNames.WSDL_RETURN_VALUE_MAPPING);

                //method-return-value
                appendTextChild(
                    wsdlReturnValueNode,
                    JaxRpcMappingTagNames.METHOD_RETURN_VALUE,
                    retType);

                /* need to write out the message part name */
                Parameter resultParam =
                    (Parameter) response.getParameters().next();
                JavaStructureMember resultJavaStructMember =
                    StubTieGeneratorBase.getJavaMember(resultParam);

                String retPartName = null;
                // bug fix: 4931493
                retPartName =
                    ((String) resultParam
                        .getProperty(
                            ModelProperties.PROPERTY_PARAM_MESSAGE_PART_NAME));
                debug(MYNAME, "RET.  partName = " + retPartName);
                // bug fix: 4931493, if statement added
                if (retPartName == null) {
                    if (resultJavaStructMember == null) {
                        retPartName = resultParam.getName();
                    } else {

                        Object owner = resultJavaStructMember.getOwner();
                        debug(
                            MYNAME,
                            "return.owner.class = "
                                + owner.getClass().getName());
                        if (owner instanceof SOAPStructureMember) {
                            /* RPC/Encoded */
                            retPartName =
                                ((SOAPStructureMember) owner)
                                    .getName()
                                    .getLocalPart();
                        } else if (owner instanceof LiteralElementMember) {
                            /* doc-lit, wrapped */

                            LiteralElementMember litMember =
                                (LiteralElementMember) owner;
                            retPartName = litMember.getName().getLocalPart();
                            debug(
                                MYNAME,
                                "RET. LiteralElementMember partName = "
                                    + retPartName);
                        } else {
                            /* doc-lit, use the wsdl message's one and only
                               part name*/
                            retPartName =
                                ((String) resultParam
                                    .getProperty(
                                        ModelProperties
                                            .PROPERTY_PARAM_MESSAGE_PART_NAME));
                            debug(
                                MYNAME,
                                "RET. doc lit partName = " + retPartName);
                        }
                    }
                }

                //wsdl-message
                //XXX FIXME  Need to handle QName better
                Element wsdlMsgNode =
                    (Element) appendTextChild(wsdlReturnValueNode,
                        JaxRpcMappingTagNames.WSDL_MESSAGE,
                        "wsdlMsgNS:" + outputMsgQName.getLocalPart());
                wsdlMsgNode.setAttributeNS(
                    "http://www.w3.org/2000/xmlns/",
                    "xmlns:wsdlMsgNS",
                    outputMsgQName.getNamespaceURI());

                //wsdl-message-part-name
                appendTextChild(
                    wsdlReturnValueNode,
                    JaxRpcMappingTagNames.WSDL_MESSAGE_PART_NAME,
                    retPartName);

            }

            /* exceptions */
            Iterator exceptions = method.getExceptions();
            String exception;
            while (exceptions.hasNext()) {
                exception = (String) exceptions.next();
            }
        }

        return node;
    }

    private String getJavaTypeName(JavaType type) {
        String typeName;
        if (type instanceof JavaStructureType
            && ((JavaStructureType) type).getOwner()
                instanceof LiteralArrayWrapperType) {
            typeName =
                ((LiteralArrayWrapperType) ((JavaStructureType) type)
                    .getOwner())
                    .getJavaArrayType()
                    .getName();

        } else {
            typeName = type.getName();
        }
        debug(
            MYNAME,
            "getting JavaTypeName: "
                + type.getName()
                + " returned: "
                + typeName);
        return typeName;
    }

    private final static String MYNAME = "ServiceEndpointInterfaceMappingNode";
}
