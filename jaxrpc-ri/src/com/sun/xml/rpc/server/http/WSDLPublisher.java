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

package com.sun.xml.rpc.server.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 *
 * @author JAX-RPC Development Team
 */
public class WSDLPublisher {

    public WSDLPublisher(
        ServletContext context,
        JAXRPCRuntimeInfo jaxrpcInfo) {
        this.servletContext = context;
        this.jaxrpcInfo = jaxrpcInfo;
        templatesByEndpointInfo = new HashMap();
        localizer = new Localizer();
        messageFactory =
            new LocalizableMessageFactory("com.sun.xml.rpc.resources.jaxrpcservlet");
    }

    public void handle(
        RuntimeEndpointInfo targetEndpoint,
        Map fixedUrlPatternEndpoints,
        HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServletException {
        Iterator urlPatterns = fixedUrlPatternEndpoints.keySet().iterator();
        String urlPattern = (String) urlPatterns.next();

        // need to find correct url pattern in map to create baseAddress
        while (targetEndpoint != fixedUrlPatternEndpoints.get(urlPattern)) {
            urlPattern = (String) urlPatterns.next();
        }
        response.setContentType("text/xml");
        response.setStatus(HttpServletResponse.SC_OK);
        OutputStream outputStream = response.getOutputStream();
        String actualAddress =
            request.getScheme()
                + "://"
                + request.getServerName()
                + ":"
                + request.getServerPort()
                + request.getRequestURI();
        String baseAddress =
            actualAddress.substring(0, actualAddress.lastIndexOf(urlPattern));

        Templates templates;
        synchronized (this) {
            templates = (Templates) templatesByEndpointInfo.get(targetEndpoint);
            if (templates == null) {
                templates = createTemplatesFor(fixedUrlPatternEndpoints);
                templatesByEndpointInfo.put(targetEndpoint, templates);
            }
        }
        try {
            Iterator iter = fixedUrlPatternEndpoints.keySet().iterator();
            while (iter.hasNext()) {
                logger.fine(
                    localizer.localize(
                        messageFactory.getMessage(
                            "publisher.info.applyingTransformation",
                            baseAddress + iter.next())));
            }
            Source wsdlDocument =
                new StreamSource(
                    servletContext.getResourceAsStream(
                        targetEndpoint.getWSDLFileName()));
            Transformer transformer = templates.newTransformer();
            transformer.setParameter("baseAddress", baseAddress);
            transformer.transform(wsdlDocument, new StreamResult(outputStream));
        } catch (TransformerConfigurationException e) {
            throw new JAXRPCServletException("exception.cannotCreateTransformer");
        } catch (TransformerException e) {
            throw new JAXRPCServletException(
                "exception.transformationFailed",
                e.getMessageAndLocation());
        }
    }

    protected Templates createTemplatesFor(Map patternToPort) {
        try {
            // create the stylesheet
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(bos, "UTF-8");

            writer.write(
                "<xsl:transform version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\">\n");
            writer.write("<xsl:param name=\"baseAddress\"/>\n");

            writer.write(
                "<xsl:template match=\"/\"><xsl:apply-templates mode=\"copy\"/></xsl:template>\n");

            Iterator iter = patternToPort.keySet().iterator();
            while (iter.hasNext()) {
                String pattern = (String) iter.next();
                RuntimeEndpointInfo info =
                    (RuntimeEndpointInfo) patternToPort.get(pattern);
                writer.write(
                    "<xsl:template match=\"wsdl:definitions[@targetNamespace='");
                writer.write(info.getPortName().getNamespaceURI());
                writer.write("']/wsdl:service[@name='");
                writer.write(info.getServiceName().getLocalPart());
                writer.write("']/wsdl:port[@name='");
                writer.write(info.getPortName().getLocalPart());
                writer.write("']/soap:address\" mode=\"copy\">");
                writer.write("<soap:address><xsl:attribute name=\"location\">");
                writer.write(
                    "<xsl:value-of select=\"$baseAddress\"/>" + pattern);
                writer.write("</xsl:attribute></soap:address></xsl:template>");
            }

            writer.write(
                "<xsl:template match=\"@*|node()\" mode=\"copy\"><xsl:copy><xsl:apply-templates select=\"@*\" mode=\"copy\"/><xsl:apply-templates mode=\"copy\"/></xsl:copy></xsl:template>\n");
            writer.write("</xsl:transform>\n");
            writer.close();
            byte[] stylesheet = bos.toByteArray();
            Source stylesheetSource =
                new StreamSource(new ByteArrayInputStream(stylesheet));
            TransformerFactory transformerFactory =
                TransformerFactory.newInstance();
            Templates templates =
                transformerFactory.newTemplates(stylesheetSource);
            return templates;
        } catch (Exception e) {
            throw new JAXRPCServletException("exception.templateCreationFailed");
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

    private ServletContext servletContext;
    private Localizer localizer;
    private LocalizableMessageFactory messageFactory;
    private JAXRPCRuntimeInfo jaxrpcInfo;
    private Map templatesByEndpointInfo;
    private static final Logger logger =
        Logger.getLogger(
            com.sun.xml.rpc.util.Constants.LoggingDomain + ".server.http");
}
