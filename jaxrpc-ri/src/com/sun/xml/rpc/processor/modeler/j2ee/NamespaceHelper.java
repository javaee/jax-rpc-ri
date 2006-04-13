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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/tools/wsdlc1_1/NamespaceHelper.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:36:53 [10/7/02 11:55:33]
/*************************************************************************
   Licensed Materials - Property of IBM
   5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business M
achines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with
   IBM Corp.
**************************************************************************/
package com.sun.xml.rpc.processor.modeler.j2ee;

import java.util.HashMap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.ComplexType;

/**
 * Helper class used to convert a QName from a
 * xsd:QName string in an XML schema instance.
 * The xsd:QName string is of the form prefix:localpart, and the
 * mapping of prefix to a real namesapce may be defined at any level
 * in the traversal of the XML instance.  There, this class is
 * intended to be used as follows:
 *<br>
 *  When traversing a new child node BaseType, the push() method must be called
 * to allow the NamespaceHelper to fetch any new prefix and namespace 
 * declaration in the child node.  When done with visiting the child
 * node, the pop() method must be called to revert back to previous
 * settings.
 */
public class NamespaceHelper {
    private HashMap map; // HashMap of local prefix to namespace
    private NamespaceHelper prev; // previous top of stack
    public NamespaceHelper() {
        map = null;
        prev = null;
    }

    private NamespaceHelper(NamespaceHelper prev, ComplexType ct) {
        map = new HashMap();
        this.prev = prev;
        String[] attrNames = ct.getAttributeNames();
        for (int i = 0; i < attrNames.length; i++) {
            String attrName = attrNames[i];
            if (attrName.equals("xmlns") || attrName.startsWith("xmlns:")) {
                map.put(attrName, ct.getAttributeValue(attrName));
            }
        }
    }

    /**
     * Push a BaseType during traversal of BaseType.
     * @param bt  The BaseType being visited
     * @return a new instnace of NamespaceHelper to be used to resolve
     * QName string to a QName instance
     */
    public NamespaceHelper push(ComplexType ct) {
        return new NamespaceHelper(this, ct);
    }

    /**
     * Pop the NamespaceHelper to signal that we're done visiting the 
     * Basetype
     * @return NamespaceHelper that can be used to resolve Qname string
     * to a QName instance with the context that was set prior to
     * visiting the BaseType
     */
    public NamespaceHelper pop() {
        return prev;
    }

    /**
     * Convert a QName string with namespace prefix to a QName
     * instance containing real namespace.
     * @param nsString the QName string in the XML instnace.  It can be
     * of the form "name", or "prefix:name". 
     * @return QName derived from the QName string, or null if QName can't be
     * determined.
     */
    public QName getQName(String nsString) {
        String attrName;
        // name of namespace prefix attribute to look up in map
        String local; // local part of QName

        int idx = nsString.indexOf(':');
        if (idx < nsString.length() - 1) {
            /* nsString is of the form "prefix:local" */
            String prefix = nsString.substring(0, idx);
            attrName = "xmlns:" + prefix;
            local = nsString.substring(idx + 1);
        } else {
            attrName = "xmlns";
            local = nsString;
        }
        return getQNameInternal(attrName, local);
    }

    private QName getQNameInternal(String attrName, String local) {
        if (map == null)
            return null;
        String namespace = (String) map.get(attrName);
        if (namespace != null) {
            return new QName(namespace, local);
        } else if (prev != null) {
            return prev.getQNameInternal(attrName, local);
        } else
            return null;
    }
};
