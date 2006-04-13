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
