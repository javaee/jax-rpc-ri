/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
