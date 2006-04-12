/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
