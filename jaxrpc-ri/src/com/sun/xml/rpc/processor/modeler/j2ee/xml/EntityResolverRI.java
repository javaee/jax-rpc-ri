/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*************************************************************************
   Licensed Materials - Property of IBM
   5639-D57
   (C) COPYRIGHT International Business Machines Corp. 2002
   All Rights Reserved
   US Government Users Restricted Rights - Use, duplication, or
   disclosure restricted by GSA ADP Schedule Contract  with 
   IBM Corp.
**************************************************************************/
/*********************************************************************
Change History
Date     user       defect    purpose
---------------------------------------------------------------------------
08/06/02 mcheng    141298     move to new DTD
07/13/02 mcheng    142177     Add support for deploy.xml
08/21/02 mcheng    143206     move to proposed final DTD
08/28/02 mcheng    144340     new DOCTYPE
09/30/02 mcheng    148356     XML schema support
09/30/02 mcheng    148405     sun-j2ee-ri_1_4.dtd
*********************************************************************/
package com.sun.xml.rpc.processor.modeler.j2ee.xml;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/*
 * Entity resolver to resolve JSR109 related DTDs locally
 * @author Michael Cheng
 */
public class EntityResolverRI implements EntityResolver {

    /* constructor */
    public EntityResolverRI() {
    }

    /*
     * @return InputSrouce for the DTD, or null if this EntityResolver
     *         does not handle the DTD.
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws IOException {

        //System.out.println("[EntityResolverRI]--> publicID = " + publicId);
        //System.out.println("[EntityResolverRI]--> systemId = " + systemId);

        String resource = null;
        if (publicId == null) {

            // unspecified schema
            if (systemId == null
                || systemId.lastIndexOf('/') == systemId.length()) {
                return null;
            }

            if (systemId.endsWith("j2ee_jaxrpc_mapping_1_1.xsd")) {
                resource =
                    "com/sun/xml/rpc/processor/modeler/j2ee/xml/j2ee_jaxrpc_mapping_1_1.xsd";
            } else if (systemId.endsWith("j2ee_1_4.xsd")) {
                resource =
                    "com/sun/xml/rpc/processor/modeler/j2ee/xml/j2ee_1_4.xsd";
            } else if (systemId.endsWith("j2ee_web_services_client_1_1.xsd")) {
                resource =
                    "com/sun/xml/rpc/processor/modeler/j2ee/xml/j2ee_web_services_client_1_1.xsd";
            } else if (systemId.endsWith("xml.xsd")) {
                resource = "com/sun/xml/rpc/processor/modeler/j2ee/xml/xml.xsd";
            }

        } else {

            // unspecified schema
            if (systemId == null
                || systemId.lastIndexOf('/') == systemId.length()) {
                return null;
            }

            if (systemId.endsWith("XMLSchema.dtd")) {
                resource =
                    "com/sun/xml/rpc/processor/modeler/j2ee/xml/XMLSchema.dtd";
            } else if (systemId.endsWith("datatypes.dtd")) {
                resource =
                    "com/sun/xml/rpc/processor/modeler/j2ee/xml/datatypes.dtd";
            }
        }

        if (resource == null) {
            //XXX FIXME  log it better
            System.out.println(systemId + " not resolved");
            return null;
        }

        //System.out.println("[EntityResolverRI]--> resource = " + resource);

        InputStream inStrm = getClassLoader().getResourceAsStream(resource);

        if (inStrm == null) {
            System.out.println("unable to locate resource " + resource);
            throw new java.io.IOException(
                "unable to locate resource " + resource);
        }

        InputSource is = new InputSource(inStrm);
        is.setSystemId(systemId);
        return is;
    }

    private ClassLoader getClassLoader() {
        ClassLoader loader = this.getClass().getClassLoader();

        //In case the bootstrap loader is null, i.e. through launcher
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }

        return loader;
    }

};
