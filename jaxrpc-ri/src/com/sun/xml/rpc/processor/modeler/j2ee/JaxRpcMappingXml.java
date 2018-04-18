/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
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

// @(#) 1.1 jsr109ri/src/java/com/ibm/webservices/ri/tools/wsdlc1_1/JaxRpcMappingXml1_1.java, jsr109ri, jsr10911, b0240.03 9/30/02 11:36:51 [10/7/02 11:55:32]
/*************************************************************************
   Licensed Materials - Property of IBM
   5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business M
achines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with
   IBM Corp.
**************************************************************************/
/*********************************************************************
Change History
Date     user       defect    purpose
---------------------------------------------------------------------------
08/12/02 mcheng     142035    new code drop
*********************************************************************/
package com.sun.xml.rpc.processor.modeler.j2ee;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.xml.sax.InputSource;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.javaWsdlMapping;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.javaWsdlMappingFactory;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.packageMappingType;

/*
 * Representation of JaxRpcMapping meta data .xml file
 * @author Michael Cheng
 */
public class JaxRpcMappingXml {

    /* Constructor
     * @param systemId location of mapping meta data. Can be URL or file name
     */
    public JaxRpcMappingXml(String systemId) throws java.io.IOException {

        //System.out.println("[JaxRpcMappingXml] ==> systemId = " + systemId);

        factory = new javaWsdlMappingFactory();
        InputSource src = new InputSource(systemId);
        factory.setPackageName("com.sun.xml.rpc.processor.modeler.j2ee.xml");

        javaWsdlMap =
            (javaWsdlMapping) factory.loadDocument("javaWsdlMapping", src);
        if (javaWsdlMap == null) {
            throw new java.io.IOException(
                "Unable to load mapping meta data at: " + systemId);
        }
    }

    /*
     * @return JavaBean that represents &lt;java-wsdl-mapping&gt; element 
     */
    public javaWsdlMapping getJavaWsdlMapping() {
        return javaWsdlMap;
    }

    /**
     * @return Hashmap of namespace to package name mappings
     */
    public HashMap getNSToPkgMapping() {
        if (nsMap == null) {
            nsMap = new HashMap();
            int numPkgMap = javaWsdlMap.getPackageMappingCount();
            for (int i = 0; i < numPkgMap; i++) {
                packageMappingType pkgMap = javaWsdlMap.getPackageMapping(i);
                nsMap.put(
                    pkgMap.getNamespaceURI().getElementValue(),
                    pkgMap.getPackageType().getElementValue());
            }
        }
        return nsMap;
    }

    /**
     * unit test
     */
    public static void main(String[] argv) {
        try {
            if (argv.length != 1) {
                System.out.println(
                    "usage: com.ibm.webservices.ri.deploy.JaxRpcMappingXml systemId");
                System.exit(1);
            }
            JaxRpcMappingXml jaxRpcMap = new JaxRpcMappingXml(argv[0]);
            HashMap nsMap = jaxRpcMap.getNSToPkgMapping();
            Set keys = nsMap.keySet();
            System.out.println(nsMap.size() + " namespace to package mapping:");
            for (Iterator it = keys.iterator(); it.hasNext();) {
                String ns = (String) it.next();
                String pkg = (String) nsMap.get(ns);
                System.out.println("'" + ns + "' : '" + pkg + "'");
            }

            javaWsdlMapping javaWsdlMap = jaxRpcMap.getJavaWsdlMapping();
            int numJavaXmlTypeMapping =
                javaWsdlMap.getJavaXmlTypeMappingCount();
            System.out.println(
                "There are "
                    + numJavaXmlTypeMapping
                    + " java-xml-type-mapping");

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    javaWsdlMappingFactory factory;
    javaWsdlMapping javaWsdlMap;
    HashMap nsMap; // ns to pkg mapping
}
