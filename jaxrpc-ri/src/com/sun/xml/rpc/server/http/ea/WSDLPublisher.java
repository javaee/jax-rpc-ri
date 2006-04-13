/*
 * $Id: WSDLPublisher.java,v 1.2 2006-04-13 01:32:16 ofung Exp $
 */

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

package com.sun.xml.rpc.server.http.ea;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.rpc.server.http.JAXRPCServletException;

/**
 * A publisher of WSDL documents.
 *
 * @author JAX-RPC Development Team
 */
public class WSDLPublisher {

    public WSDLPublisher(ServletConfig servletConfig) {
        _servletConfig = servletConfig;
    }

    public WSDLPublisher(
        ServletConfig servletConfig,
        InputStream configInputStream) {
        if (configInputStream == null) {
            throw new IllegalArgumentException("error.wsdlPublisher.noInputStream");
        }
        _servletConfig = servletConfig;
        _servletContext = servletConfig.getServletContext();
        readFrom(configInputStream);
    }

    public boolean hasDocument() {
        return _wsdlLocation != null;
    }

    public void publish(String prefix, HttpServletResponse response)
        throws IOException {
        response.setContentType("text/xml");
        response.setStatus(HttpServletResponse.SC_OK);
        OutputStream outputStream = response.getOutputStream();

        if (_wsdlTransform) {
            try {
                Source wsdlDoc =
                    new StreamSource(
                        _servletContext.getResourceAsStream(_wsdlLocation));
                Transformer transformer = _xsltTemplates.newTransformer();
                transformer.setParameter("baseURI", prefix);
                transformer.transform(wsdlDoc, new StreamResult(outputStream));
            } catch (TransformerConfigurationException e) {
                throw new IOException("cannot create transformer");
            } catch (TransformerException e) {
                throw new IOException("transformation failed");
            }
        } else {
            InputStream is = _servletContext.getResourceAsStream(_wsdlLocation);
            copyStream(is, outputStream);
            is.close();
        }
    }

    protected void readFrom(InputStream inputStream) {

        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();

            _wsdlLocation =
                properties.getProperty(PROPERTY_WSDL + "." + PROPERTY_LOCATION);

            if (_wsdlLocation != null) {
                _wsdlLocation = _wsdlLocation.trim();

                // verify that we can read the WSDL document
                InputStream wsdlFile =
                    _servletContext.getResourceAsStream(_wsdlLocation);
                if (wsdlFile != null) {
                    wsdlFile.close();
                } else {
                    _wsdlLocation = null;
                    return;
                }

                _wsdlTransform = true; // default value
                String transform =
                    properties.getProperty(
                        PROPERTY_WSDL + "." + PROPERTY_TRANSFORM);
                if (transform != null
                    && Boolean.valueOf(transform).booleanValue() == false) {
                    _wsdlTransform = false;
                }

                if (_wsdlTransform) {
                    int portCount =
                        Integer.parseInt(
                            properties.getProperty(PROPERTY_PORT_COUNT));
                    for (int i = 0; i < portCount; ++i) {
                        String portPrefix =
                            PROPERTY_PORT + Integer.toString(i) + ".";
                        String name =
                            properties.getProperty(portPrefix + PROPERTY_NAME);

                        String portWsdlPrefix =
                            portPrefix + PROPERTY_WSDL + ".";
                        String targetNamespace =
                            properties.getProperty(
                                portWsdlPrefix + PROPERTY_TNS);
                        String serviceName =
                            properties.getProperty(
                                portWsdlPrefix + PROPERTY_SERVICE_NAME);
                        String portName =
                            properties.getProperty(
                                portWsdlPrefix + PROPERTY_PORT_NAME);

                        if (name == null
                            || targetNamespace == null
                            || serviceName == null
                            || portName == null) {
                            continue;
                        }

                        _ports.put(
                            name,
                            new WSDLPortInfo(
                                targetNamespace,
                                serviceName,
                                portName));
                    }

                    // create the stylesheet
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    OutputStreamWriter writer =
                        new OutputStreamWriter(bos, "UTF-8");

                    writer.write(
                        "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\">\n");
                    writer.write("<xsl:param name=\"baseURI\"/>\n");

                    writer.write(
                        "<xsl:template match=\"/\"><xsl:apply-templates mode=\"copy\"/></xsl:template>\n");

                    for (Iterator iter = _ports.keySet().iterator();
                        iter.hasNext();
                        ) {
                        String name = (String) iter.next();
                        WSDLPortInfo portInfo = (WSDLPortInfo) _ports.get(name);

                        writer.write(
                            "<xsl:template match=\"wsdl:definitions[@targetNamespace='");
                        writer.write(portInfo.getTargetNamespace());
                        writer.write("']/wsdl:service[@name='");
                        writer.write(portInfo.getServiceName());
                        writer.write("']/wsdl:port[@name='");
                        writer.write(portInfo.getPortName());
                        writer.write("']/soap:address\" mode=\"copy\">");
                        writer.write(
                            "<soap:address><xsl:attribute name=\"location\"><xsl:value-of select=\"$baseURI\"/><xsl:text>");
                        writer.write(name);
                        writer.write(
                            "</xsl:text></xsl:attribute></soap:address></xsl:template>");
                    }

                    writer.write(
                        "<xsl:template match=\"@*|node()\" mode=\"copy\"><xsl:copy><xsl:apply-templates select=\"@*\" mode=\"copy\"/><xsl:apply-templates mode=\"copy\"/></xsl:copy></xsl:template>\n");
                    writer.write("</xsl:stylesheet>\n");
                    writer.close();
                    _xsltDocument = bos.toByteArray();

                    try {
                        // cache a Templates for the stylesheet
                        Source xsltDoc =
                            new StreamSource(
                                new ByteArrayInputStream(_xsltDocument));
                        TransformerFactory transformerFactory =
                            TransformerFactory.newInstance();
                        _xsltTemplates =
                            transformerFactory.newTemplates(xsltDoc);
                    } catch (TransformerConfigurationException e) {
                        _wsdlTransform = false;
                    }

                }
            }
        } catch (IOException e) {
            throw new JAXRPCServletException("error.wsdlPublisher.cannotReadConfiguration");
        }
    }

    protected static void copyStream(InputStream istream, OutputStream ostream)
        throws IOException {
        byte[] buf = new byte[1024];
        int num = 0;
        while ((num = istream.read(buf)) != -1) {
            ostream.write(buf, 0, num);
        }
        ostream.flush();
    }

    private ServletConfig _servletConfig;
    private ServletContext _servletContext;
    private String _wsdlLocation;
    private boolean _wsdlTransform;
    private Map _ports = new HashMap();
    private byte[] _xsltDocument;
    private Templates _xsltTemplates;

    private final static String PROPERTY_PORT_COUNT = "portcount";
    private final static String PROPERTY_PORT = "port";
    private final static String PROPERTY_NAME = "name";
    private final static String PROPERTY_WSDL = "wsdl";
    private final static String PROPERTY_TNS = "targetNamespace";
    private final static String PROPERTY_SERVICE_NAME = "serviceName";
    private final static String PROPERTY_PORT_NAME = "portName";
    private final static String PROPERTY_LOCATION = "location";
    private final static String PROPERTY_TRANSFORM = "transform";
}
