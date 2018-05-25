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

package com.sun.xml.rpc.processor.generator.nodes;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;

/**
 * @author  Qingqing Ouyang
 * @version 
 */
public class ServiceInterfaceMappingNode extends JaxRpcMappingNode {

    /**
     * Default constructor.
     */
    public ServiceInterfaceMappingNode() {
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
        Service service)
        throws Exception {

        Element node = appendChild(parent, nodeName);

        ProcessorEnvironment env =
            (com.sun.xml.rpc.processor.util.ProcessorEnvironment) config
                .getEnvironment();
        QName serviceQName = service.getName();
        String serviceNS = serviceQName.getNamespaceURI();
        String serviceJavaName =
            env.getNames().customJavaTypeClassName(service.getJavaInterface());

        //service-interface
        appendTextChild(
            node,
            JaxRpcMappingTagNames.SERVICE_INTERFACE,
            serviceJavaName);

        //wsdl-service-name
        //XXX FIXME  Need to handle QName better
        Element wsdlServiceName =
            (Element) appendTextChild(node,
                JaxRpcMappingTagNames.WSDL_SERVICE_NAME,
                "serviceNS:" + serviceQName.getLocalPart());
        wsdlServiceName.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns:serviceNS",
            serviceNS);

        //port-mapping*
        for (Iterator portIter = service.getPorts(); portIter.hasNext();) {
            Port port = (Port) portIter.next();
            QName portQName =
                (QName) port.getProperty(
                    ModelProperties.PROPERTY_WSDL_PORT_NAME);
            String portName = portQName.getLocalPart();
            String portJavaName = Names.getPortName(port);

            //port-mapping
            Node portMappingNode =
                appendChild(node, JaxRpcMappingTagNames.PORT_MAPPING);

            //port-name
            appendTextChild(
                portMappingNode,
                JaxRpcMappingTagNames.PORT_NAME,
                portName);

            //java-port-name
            appendTextChild(
                portMappingNode,
                JaxRpcMappingTagNames.JAVA_PORT_NAME,
                portJavaName);
        }

        return node;
    }

    private final static String MYNAME = "ServiceInterfaceMappingNode";
}
