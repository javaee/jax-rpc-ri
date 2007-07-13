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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingInfo;
import com.sun.xml.rpc.processor.config.NamespaceMappingRegistryInfo;
import com.sun.xml.rpc.processor.config.RmiModelInfo;
import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;

/**
 * @author  Qingqing Ouyang
 * @version 
 */
public class JavaWsdlMappingNode extends JaxRpcMappingNode {

    private final static String JAXRPC_MAPPING_SCHEMA_VERSION = "1.1";
    private final static String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    private final static String W3C_XML_SCHEMA_INSTANCE =
        "http://www.w3.org/2001/XMLSchema-instance";
    private final static String SCHEMA_LOCATION_TAG = "xsi:schemaLocation";
    private final static String JAXRPC_MAPPING_SCHEMA_LOCATION =
        "http://java.sun.com/xml/ns/j2ee"
            + "    "
            + "http://www.ibm.com/webservices/xsd/j2ee_jaxrpc_mapping_1_1.xsd";

    /**
     * Default constructor.
     */
    public JavaWsdlMappingNode() {
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
        Model model,
        Configuration config)
        throws Exception {
        Element node = appendChild(parent, nodeName);
        addNodeAttributes(node);

        TypeVisitor visitor = new TypeVisitor(config);
        visitor.visit(model);

        //package-mapping+
        writePackageMapping(node, model, config, visitor);

        //java-xml-type-mapping*
        writeJavaXmlTypeMapping(node, model, config, visitor);

        //exception-mapping*
        writeExceptionMapping(node, model, visitor);

        //(service-interface-mapping?, service-endpoint-interface-mapping+)*
        for (Iterator iter = model.getServices(); iter.hasNext();) {
            Service service = (Service) iter.next();

            //service-interface-mapping?
            ServiceInterfaceMappingNode siNode =
                new ServiceInterfaceMappingNode();
            siNode.write(
                node,
                JaxRpcMappingTagNames.SERVICE_INTERFACE_MAPPING,
                config,
                service);

            //service-endpoint-interface-mapping+
            for (Iterator portIter = service.getPorts(); portIter.hasNext();) {
                Port port = (Port) portIter.next();

                QName bindingQName =
                    (QName) port.getProperty(
                        ModelProperties.PROPERTY_WSDL_BINDING_NAME);

                if (!_bindingSet.contains(bindingQName)) {
                    _bindingSet.add(bindingQName);

                    ServiceEndpointInterfaceMappingNode seiNode =
                        new ServiceEndpointInterfaceMappingNode();
                    seiNode.write(
                        node,
                        JaxRpcMappingTagNames
                            .SERVICE_ENDPOINT_INTERFACE_MAPPING,
                        config,
                        port);
                }
            }
        }

        return node;
    }

    private void addNodeAttributes(Element node) {
        node.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns",
            JaxRpcMappingTagNames.J2EE_NAMESPACE);
        node.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns:xsi",
            W3C_XML_SCHEMA_INSTANCE);
        node.setAttributeNS(
            W3C_XML_SCHEMA_INSTANCE,
            SCHEMA_LOCATION_TAG,
            JAXRPC_MAPPING_SCHEMA_LOCATION);
        node.setAttribute(
            JaxRpcMappingTagNames.VERSION,
            JAXRPC_MAPPING_SCHEMA_VERSION);
    }

    private void writePackageMapping(
        Node parent,
        Model model,
        Configuration config,
        TypeVisitor visitor)
        throws Exception {

        ModelInfo modelInfo =
            (com.sun.xml.rpc.processor.config.ModelInfo) config.getModelInfo();

        /*
        * Default Namespace to package mapping
         */
        String targetNamespace = null;
        String javaPackage = null;
        if (modelInfo instanceof WSDLModelInfo) {
            WSDLModelInfo wsdlModelInfo = (WSDLModelInfo) modelInfo;
            targetNamespace = model.getTargetNamespaceURI();
            javaPackage = wsdlModelInfo.getJavaPackageName();
        } else if (modelInfo instanceof RmiModelInfo) {
            String typeNamespace = null;
            RmiModelInfo rmiModelInfo = (RmiModelInfo) modelInfo;
            targetNamespace = rmiModelInfo.getTargetNamespaceURI();
            typeNamespace = rmiModelInfo.getTypeNamespaceURI();
            javaPackage = rmiModelInfo.getJavaPackageName();

            PackageMappingNode pmNode = new PackageMappingNode();
            pmNode.write(
                parent,
                JaxRpcMappingTagNames.PACKAGE_MAPPING,
                javaPackage,
                typeNamespace);
            _namespaceSet.add(typeNamespace);
        }

        PackageMappingNode pmNode = new PackageMappingNode();
        pmNode.write(
            parent,
            JaxRpcMappingTagNames.PACKAGE_MAPPING,
            javaPackage,
            targetNamespace);
        _namespaceSet.add(targetNamespace);

        /*
        * Namespace to package mapping defined in NamespaceRegistry
        * if any.
        */
        NamespaceMappingRegistryInfo nsInfo =
            modelInfo.getNamespaceMappingRegistry();

        if (nsInfo != null) {
            for (Iterator i = nsInfo.getNamespaceMappings(); i.hasNext();) {
                NamespaceMappingInfo ns = (NamespaceMappingInfo) i.next();
                String namespace = ns.getNamespaceURI();
                String packageName = ns.getJavaPackageName();

                if (!(_namespaceSet.contains(namespace))) {
                    pmNode = new PackageMappingNode();
                    pmNode.write(
                        parent,
                        JaxRpcMappingTagNames.PACKAGE_MAPPING,
                        packageName,
                        namespace);
                }
            }
        }

        /*
         * Finally, write out the namespaces that jax-rpc internals
         * might have invented in the case of generation from SEI
         * to distinguish classes with the same name but from different
         * packages.
         */
        Set namespaces = visitor.getNamespacePackages().keySet();
        for (Iterator i = namespaces.iterator(); i.hasNext();) {
            String namespace = (String) i.next();
            if (!(_namespaceSet.contains(namespace))) {
                String packageName =
                    (String) visitor.getNamespacePackages().get(namespace);
                pmNode = new PackageMappingNode();
                pmNode.write(
                    parent,
                    JaxRpcMappingTagNames.PACKAGE_MAPPING,
                    packageName,
                    namespace);
            }
        }
    }

    private void writeJavaXmlTypeMapping(
        Node parent,
        Model model,
        Configuration config,
        TypeVisitor visitor)
        throws Exception {

        Set complexTypeSet = visitor.getComplexTypes();
        for (Iterator it = complexTypeSet.iterator(); it.hasNext();) {
            AbstractType type = (AbstractType) it.next();
            JavaXmlTypeMappingNode javaxmlNode = new JavaXmlTypeMappingNode();
            javaxmlNode.write(
                parent,
                JaxRpcMappingTagNames.JAVA_XML_TYPE_MAPPING,
                type,
                config,
                false);
        }

        //Now we post-process anonymous array types, since the jax-rpc
        //internal datastucture does not match the 109 mapping requirement
        //well.
        for (Iterator it = complexTypeSet.iterator(); it.hasNext();) {
            AbstractType type = (AbstractType) it.next();
            if (type instanceof LiteralStructuredType
                && type.getProperty(
                    ModelProperties.PROPERTY_ANONYMOUS_ARRAY_TYPE_NAME)
                    != null) {

                LiteralStructuredType litStructType =
                    (LiteralStructuredType) type;
                JavaXmlTypeMappingNode javaxmlNode =
                    new JavaXmlTypeMappingNode();
                javaxmlNode.writeAnonymousArrayType(
                    parent,
                    JaxRpcMappingTagNames.JAVA_XML_TYPE_MAPPING,
                    litStructType,
                    config,
                    false);
            }
        }

        Set simpleTypeSet = visitor.getSimpleTypes();
        for (Iterator it = simpleTypeSet.iterator(); it.hasNext();) {
            AbstractType type = (AbstractType) it.next();
            JavaXmlTypeMappingNode javaxmlNode = new JavaXmlTypeMappingNode();
            javaxmlNode.write(
                parent,
                JaxRpcMappingTagNames.JAVA_XML_TYPE_MAPPING,
                type,
                config,
                true);
        }
    }

    private void writeExceptionMapping(
        Node parent,
        Model model,
        TypeVisitor visitor)
        throws Exception {

        Set faultSet = visitor.getFaults();
        for (Iterator it = faultSet.iterator(); it.hasNext();) {

            Fault fault = (Fault) it.next();
            Block block = fault.getBlock();
            QName wsdlMsg = block.getName();
            if (!_faultSet.contains(wsdlMsg)) {
                _faultSet.add(wsdlMsg);
                ExceptionMappingNode exceptionNode = new ExceptionMappingNode();
                exceptionNode.write(
                    parent,
                    JaxRpcMappingTagNames.EXCEPTION_MAPPING,
                    fault);
            }
        }
    }

    private final static String MYNAME = "JavaWsdlMappingNode";
    private Set _faultSet = new HashSet(); // QName of faults already processed
    private Set _bindingSet = new HashSet();
    // Qname of bindings already processed
    private Set _namespaceSet = new HashSet();
    // Namespace URI of packages already processed
}
