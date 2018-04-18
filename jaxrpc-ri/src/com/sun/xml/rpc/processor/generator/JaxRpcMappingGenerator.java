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

package com.sun.xml.rpc.processor.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.generator.nodes.JavaWsdlMappingNode;
import com.sun.xml.rpc.processor.generator.nodes.JaxRpcMappingTagNames;
import com.sun.xml.rpc.processor.model.Model;

/**
 *
 * @author  Qingqing Ouyang
 */
public class JaxRpcMappingGenerator implements ProcessorAction {

    private boolean debug = false;
    private File mappingFile;

    public JaxRpcMappingGenerator(File mappingFile) {
        this.mappingFile = mappingFile;
    }

    public void perform(
        Model model,
        Configuration config,
        Properties options) {

        Document document = buildMapping(model, config);
        write(document, mappingFile);
    }

    private Document buildMapping(Model model, Configuration config) {
        debug("building mapping");

        Document root = newDocument();
        try {
            (new JavaWsdlMappingNode()).write(
                root,
                JaxRpcMappingTagNames.JAVA_WSDL_MAPPING,
                model,
                config);
        } catch (Exception ex) {
            //XXX FIXME. i18n JAXRPC logger ??
            ex.printStackTrace();
            throw new RuntimeException(ex.toString());
        }
        return root;
    }

    /**
     * Creates and returns a new DOM document based on the current 
     * configuration.
     *
     * @return the new DOM Document object
     */
    private Document newDocument() {
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();

            DOMImplementation domImplementation =
                builder.getDOMImplementation();

            Document document = builder.newDocument();
            return document;
        } catch (Exception e) {
            //XXX FIXME. i18n JAXRPC logger ??
            e.printStackTrace();
        }
        return null;
    }

    private void write(Document document, final File resultFile) {
        try {
            FileOutputStream out = new FileOutputStream(resultFile);
            Result output = new StreamResult(out);

            Source source = new DOMSource(document);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount",
                "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, output);

            out.close();

        } catch (Exception e) {
            //XXX FIXME. i18n JAXRPC logger ??
            e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
    }

    private void debug(String msg) {
        if (debug) {
            System.out.println("[JaxRpcMappingGenerator] --> " + msg);
        }
    }
}
