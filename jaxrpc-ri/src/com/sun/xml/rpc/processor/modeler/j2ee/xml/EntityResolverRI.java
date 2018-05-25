/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2002 International Business Machines Corp. 2002. All rights reserved.
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
