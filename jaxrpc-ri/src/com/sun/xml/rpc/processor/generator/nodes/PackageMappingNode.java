/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.xml.rpc.processor.generator.nodes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author  Qingqing Ouyang
 * @version 
 */
public class PackageMappingNode extends JaxRpcMappingNode {

    /**
     * Default constructor.
     */
    public PackageMappingNode() {
    }

    /**
     * write the appropriate information to a DOM tree and return it
     *
     * @param parent node in the DOM tree 
     * @param nodeName name for the root element for this DOM tree fragment
     * @param packageName fully qualified Java package name
     * @param namespace the URI for target namespace
     * @return the DOM tree top node
     */
    public Node write(
        Node parent,
        String nodeName,
        String packageName,
        String namespace)
        throws Exception {

        debug(MYNAME, "packageName = " + packageName);
        debug(MYNAME, "namespace = " + namespace);

        Element node = appendChild(parent, nodeName);
        if (packageName != null && namespace != null) {
            appendTextChild(
                node,
                JaxRpcMappingTagNames.PACKAGE_TYPE,
                packageName);
            appendTextChild(
                node,
                JaxRpcMappingTagNames.NAMESPACEURI,
                namespace);
        }

        return node;
    }

    private final static String MYNAME = "PackageMappingNode";
}
