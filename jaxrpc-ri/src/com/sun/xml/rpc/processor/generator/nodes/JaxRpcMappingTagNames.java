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
package com.sun.xml.rpc.processor.generator.nodes;

/**
 * This class holds all tag names for j2ee_jaxrpc_mapping_1.1.xsd
 *
 * @author Qingqing Ouyang
 */
public interface JaxRpcMappingTagNames {

    public static final String ANONYMOUS_TYPE_QNAME = "anonymous-type-qname";
    public static final String CONSTRUCTOR_PARAMETER_ORDER =
        "constructor-parameter-order";
    public static final String DATA_MEMBER = "data-member";

    public static final String ELEMENT_NAME = "element-name";
    public static final String EXCEPTION_MAPPING = "exception-mapping";
    public static final String EXCEPTION_TYPE = "exception-type";
    public static final String J2EE_DEFAULTNAMESPACEPREFIX = "j2ee";
    public static final String J2EE_NAMESPACE =
        "http://java.sun.com/xml/ns/j2ee";
    public static final String JAVA_METHOD_NAME = "java-method-name";
    public static final String JAVA_PORT_NAME = "java-port-name";
    public static final String JAVA_TYPE = "java-type";
    public static final String JAVA_VARIABLE_NAME = "java-variable-name";
    public static final String JAVA_WSDL_MAPPING = "java-wsdl-mapping";
    public static final String JAVA_XML_TYPE_MAPPING = "java-xml-type-mapping";

    public static final String METHOD_PARAM_PARTS_MAPPING =
        "method-param-parts-mapping";
    public static final String METHOD_RETURN_VALUE = "method-return-value";
    public static final String NAMESPACEURI = "namespaceURI";
    public static final String PACKAGE_MAPPING = "package-mapping";
    public static final String PACKAGE_TYPE = "package-type";
    public static final String PARAMETER_MODE = "parameter-mode";
    public static final String PARAM_POSITION = "param-position";
    public static final String PARAM_TYPE = "param-type";
    public static final String PORT_MAPPING = "port-mapping";
    public static final String PORT_NAME = "port-name";
    public static final String QNAME_SCOPE = "qname-scope";
    public static final String ROOT_TYPE_QNAME = "root-type-qname";

    public static final String SERVICE_ENDPOINT_INTERFACE_MAPPING =
        "service-endpoint-interface-mapping";
    public static final String SERVICE_ENDPOINT_INTERFACE =
        "service-endpoint-interface";
    public static final String SERVICE_ENDPOINT_METHOD_MAPPING =
        "service-endpoint-method-mapping";
    public static final String SERVICE_INTERFACE_MAPPING =
        "service-interface-mapping";
    public static final String SERVICE_INTERFACE = "service-interface";
    public static final String SOAP_HEADER = "soap-header";

    public static final String VARIABLE_MAPPING = "variable-mapping";
    public static final String VERSION = "version";
    public static final String WRAPPED_ELEMENT = "wrapped-element";
    public static final String WSDL_BINDING = "wsdl-binding";
    public static final String WSDL_MESSAGE_MAPPING = "wsdl-message-mapping";
    public static final String WSDL_MESSAGE_PART_NAME =
        "wsdl-message-part-name";
    public static final String WSDL_MESSAGE = "wsdl-message";
    public static final String WSDL_OPERATION = "wsdl-operation";
    public static final String WSDL_PORT_TYPE = "wsdl-port-type";
    public static final String WSDL_RETURN_VALUE_MAPPING =
        "wsdl-return-value-mapping";
    public static final String WSDL_SERVICE_NAME = "wsdl-service-name";

    public static final String XML_ATTRIBUTE_NAME = "xml-attribute-name";
    public static final String XML_ELEMENT_NAME = "xml-element-name";
    public static final String XML_WILDCARD = "xml-wildcard";

}
