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

package com.sun.xml.rpc.client.dii.webservice.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.xml.rpc.client.dii.webservice.WebService;
import com.sun.xml.rpc.client.dii.webservice.WebServicesClient;
import com.sun.xml.rpc.client.dii.webservice.WebServicesClientException;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderException;
import com.sun.xml.rpc.streaming.XMLReaderFactory;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;

/**
 * @author JAX-RPC Development Team
 */
public class WebServicesClientParser {
    private LocalizableMessageFactory messageFactory =
        new LocalizableMessageFactory("com.sun.xml.rpc.resources.client");

    public WebServicesClientParser() {
    }

    public WebServicesClient parse(InputStream is)
        throws WebServicesClientException {
        try {
            XMLReader reader =
                XMLReaderFactory.newInstance().createXMLReader(is);
            reader.next();
            return parseWebServicesClient(reader);
        } catch (XMLReaderException e) {
            throw new WebServicesClientException("client.xmlReader", e);
        }
    }

    protected WebServicesClient parseWebServicesClient(XMLReader reader) {
        if (!reader.getName().equals(Constants.QNAME_CLIENT)) {
            ParserUtil.failWithFullName("client.invalidElement", reader);
        }

        WebServicesClient client = new WebServicesClient();
        if (reader.getState() == XMLReader.START) {
            client.setWebServices(parseWebServices(reader));
        } else {
            ParserUtil.fail("client.missing.service", reader);
        }

        if (reader.nextElementContent() != XMLReader.EOF) {
            ParserUtil.fail("client.unexpectedContent", reader);
        }

        reader.close();
        return client;
    }

    protected List parseWebServices(XMLReader reader) {

        List webServices = new ArrayList();

        while (reader.nextElementContent() == XMLReader.START) {

            if (!reader.getName().equals(Constants.QNAME_SERVICE)) {
                ParserUtil.failWithFullName("service.invalidElement", reader);
            }

            String wsdlLocation =
                ParserUtil.getAttribute(reader, Constants.ATTR_WSDL_LOCATION);
            if (wsdlLocation == null) {
                ParserUtil.failWithLocalName(
                    "client.invalidwsdlLocation",
                    reader);
            }

            String model =
                ParserUtil.getAttribute(reader, Constants.ATTR_MODEL);
            if (model == null) {
                ParserUtil.failWithLocalName("client.invalidModel", reader);
            }

            WebService service = new WebService(wsdlLocation, model);
            webServices.add(service);

            if (reader.nextElementContent() != XMLReader.END) {
                ParserUtil.fail("client.unexpectedContent", reader);
            }
        }
        return webServices;
    }
}
