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

package com.sun.xml.rpc.wsdl.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.Definitions;
import com.sun.xml.rpc.wsdl.document.Import;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.schema.Schema;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;
import com.sun.xml.rpc.wsdl.framework.ParserContext;
import com.sun.xml.rpc.wsdl.framework.WriterContext;

/**
 * WSDL Utilities.
 *
 * @author JAX-RPC Development Team
 */
public class WSDLUtil implements com.sun.xml.rpc.spi.tools.WSDLUtil {
    public WSDLUtil() {
    }

    /**
     * Collect all relative imports from a web service's main wsdl document.
     *
     *@param wsdlRelativeImports outupt param in which wsdl relative imports 
     * will be added
     *
     *@param schemaRelativeImports outupt param in which schema relative 
     * imports will be added
     */
    public void getRelativeImports(
        URL wsdlURL,
        Collection wsdlRelativeImports,
        Collection schemaRelativeImports)
        throws IOException {

        // Parse the wsdl document to find all import statements
        InputStream wsdlInputStream =
            new BufferedInputStream(wsdlURL.openStream());
        InputSource wsdlDocumentSource = new InputSource(wsdlInputStream);
        WSDLParserOverride wsdlParser = new WSDLParserOverride();
        // We only want to grab the import statements in the initial 
        // wsdl document. No need to fully resolve them.
        wsdlParser.setFollowImports(false);
        WSDLDocument wsdlDoc = wsdlParser.parse(wsdlDocumentSource);

        for (Iterator iter = wsdlDoc.getDefinitions().imports();
            iter.hasNext();
            ) {
            Import next = (Import) iter.next();
            String location = next.getLocation();
            // If it's a relative import
            if ((location.indexOf(":") == -1)) {
                wsdlRelativeImports.add(next);
            }
        }

        Collection schemaImports = wsdlParser.getSchemaImports();
        for (Iterator iter = schemaImports.iterator(); iter.hasNext();) {
            Import next = (Import) iter.next();
            String location = next.getLocation();
            // If it's a relative import
            if ((location.indexOf(":") == -1)) {
                schemaRelativeImports.add(next);
            }
        }

        wsdlInputStream.close();

        return;
    }

    /**
     * Subclass of WSDLParser that skips processing of imports.  Only
     * needed temporarily until jaxrpc code uses value of setFollowImports()
     */
    private static class WSDLParserOverride extends WSDLParser {

        private SchemaExtensionHandlerOverride schemaHandler;

        public WSDLParserOverride() {
            super();
            schemaHandler = new SchemaExtensionHandlerOverride();
            // Override the schema handler
            register(schemaHandler);
        }

        public Collection getSchemaImports() {
            return schemaHandler.getImports();
        }

        protected Definitions parseDefinitions(
            ParserContext context,
            InputSource source,
            String expectedTargetNamespaceURI) {
            Definitions definitions =
                parseDefinitionsNoImport(
                    context,
                    source,
                    expectedTargetNamespaceURI);
            return definitions;
        }
    }

    private static class SchemaExtensionHandlerOverride
        extends ExtensionHandler {

        private SchemaParserOverride parser;

        public SchemaExtensionHandlerOverride() {
            parser = new SchemaParserOverride();
        }

        public Collection getImports() {
            return parser.getImports();
        }

        public String getNamespaceURI() {
            return Constants.NS_XSD;
        }

        public boolean doHandleExtension(
            ParserContext context,
            Extensible parent,
            Element e) {
            if (XmlUtil.matchesTagNS(e, SchemaConstants.QNAME_SCHEMA)) {
                parent.addExtension(parser.parseSchema(context, e, null));
                return true;
            } else {
                return false;
            }
        }

        public void doHandleExtension(
            WriterContext context,
            Extension extension)
            throws IOException {
            throw new IllegalArgumentException("unsupported operation");
        }
    }

    private static class SchemaParserOverride extends SchemaParser {
        private Collection imports = new HashSet();

        public Collection getImports() {
            return imports;
        }

        protected void processImports(
            ParserContext context,
            InputSource src,
            Schema schema) {
            for (Iterator iter = schema.getContent().children();
                iter.hasNext();
                ) {
                SchemaElement child = (SchemaElement) iter.next();
                if (child.getQName().equals(SchemaConstants.QNAME_IMPORT)) {
                    String location =
                        child.getValueOfAttributeOrNull(
                            Constants.ATTR_SCHEMA_LOCATION);
                    String namespace =
                        child.getValueOfAttributeOrNull(
                            Constants.ATTR_NAMESPACE);
                    if ((location != null) && (namespace != null)) {
                        Import schemaImport = new Import();
                        schemaImport.setLocation(location);
                        schemaImport.setNamespace(namespace);
                        imports.add(schemaImport);
                    }
                }
            }
        }
    }
}
