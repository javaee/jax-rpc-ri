/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: JAXRPCServletDelegate.java,v 1.3 2007-07-13 23:36:25 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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

package com.sun.xml.rpc.server.http.ea;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.server.http.Implementor;
import com.sun.xml.rpc.server.http.JAXRPCServletException;
import com.sun.xml.rpc.server.http.MessageContextProperties;
import com.sun.xml.rpc.server.http.ServletDelegate;
import com.sun.xml.rpc.server.http.ServletEndpointContextImpl;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;

/**
 * The implementation class for the JAX-RPC dispatcher servlet.
 *
 * @author JAX-RPC Development Team
 */
public class JAXRPCServletDelegate implements ServletDelegate {

    public void init(ServletConfig servletConfig) throws ServletException {
        try {
            _servletConfig = servletConfig;
            _servletContext = servletConfig.getServletContext();
            _localizer = new Localizer();
            _localizerMap = new HashMap();
            _localizerMap.put(_localizer.getLocale(), _localizer);
            _messageFactory =
                new LocalizableMessageFactory("com.sun.xml.rpc.resources.jaxrpcservlet");

            if (_logger.isLoggable(Level.INFO)) {
                _logger.info(
                    _localizer.localize(
                        _messageFactory.getMessage(
                            "info.servlet.initializing")));
            }

            _implementorFactory =
                new ImplementorFactory(
                    servletConfig,
                    getConfigFile(servletConfig));
            _wsdlPublisher =
                new WSDLPublisher(servletConfig, getConfigFile(servletConfig));

        } catch (JAXRPCServletException e) {
            String message = _localizer.localize(e);
            throw new ServletException(message);
        } catch (Throwable e) {
            String message =
                _localizer.localize(
                    _messageFactory.getMessage(
                        "error.servlet.caughtThrowable",
                        new Object[] { e }));
            throw new ServletException(message);
        }
    }

    protected InputStream getConfigFile(ServletConfig servletConfig) {
        String configFilePath =
            servletConfig.getInitParameter(CONFIG_FILE_PROPERTY);
        if (configFilePath == null) {
            throw new JAXRPCServletException(
                "error.servlet.init.config.parameter.missing",
                new Object[] { CONFIG_FILE_PROPERTY });
        }
        InputStream configFile =
            _servletContext.getResourceAsStream(configFilePath);
        if (configFile == null) {
            throw new JAXRPCServletException(
                "error.servlet.init.config.fileNotFound",
                new Object[] { configFilePath });
        }

        return configFile;
    }

    public void destroy() {
        if (_logger.isLoggable(Level.INFO)) {
            _logger.info(
                _localizer.localize(
                    _messageFactory.getMessage("info.servlet.destroying")));
        }
        if (_implementorFactory != null) {
            _implementorFactory.destroy();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException {
        try {
            MimeHeaders headers = getHeaders(req);
            InputStream is = req.getInputStream();

            byte[] bytes = readFully(is);
            int length =
                req.getContentLength() == -1
                    ? bytes.length
                    : req.getContentLength();
            ByteInputStream in = new ByteInputStream(bytes, length);

            SOAPMessageContext messageContext = new SOAPMessageContext();
            SOAPMessage message = messageContext.createMessage(headers, in);

            if (message == null) {
                if (_logger.isLoggable(Level.INFO)) {
                    _logger.info(
                        _localizer.localize(
                            _messageFactory.getMessage(
                                "info.servlet.gotEmptyRequestMessage")));
                }
                messageContext.writeInternalServerErrorResponse();
            } else {
                messageContext.setMessage(message);

                /*
                System.err.println("----");
                System.err.println("CONTEXT PATH   : " + req.getContextPath());
                System.err.println("PATH INFO      : " + req.getPathInfo());
                System.err.println("PATH TRANSLATED: " + req.getPathTranslated());
                System.err.println("QUERY STRING   : " + req.getQueryString());
                System.err.println("REQUEST URI    : " + req.getRequestURI());
                System.err.println("REQUEST SCHEME : " + req.getScheme());
                System.err.println("SERVLET NAME   : " + _servletConfig.getServletName());
                System.err.println();
                System.err.println("TENTATIVE URI  : http://" +
                    req.getServerName() + ":" + req.getServerPort() +
                    req.getRequestURI());
                */

                String pathInfo = req.getPathInfo();
                if (pathInfo != null && pathInfo.length() > 1) {
                    String name =
                        (pathInfo.charAt(0) == '/'
                            ? pathInfo.substring(1)
                            : pathInfo);

                    if (_logger.isLoggable(Level.FINEST)) {
                        _logger.finest(
                            _localizer.localize(
                                _messageFactory.getMessage(
                                    "trace.servlet.requestForPortNamed",
                                    name)));
                    }

                    Implementor implementor =
                        _implementorFactory.getImplementorFor(name);

                    if (implementor == null) {
                        _logger.severe(
                            _localizer.localize(
                                _messageFactory.getMessage(
                                    "error.servlet.noImplementorForPort",
                                    name)));
                        messageContext.writeSimpleErrorResponse(
                            SOAPConstants.FAULT_CODE_SERVER,
                            FAULT_STRING_PORT_NOT_FOUND + "(\"" + name + "\")");
                    } else {
                        if (_logger.isLoggable(Level.FINEST)) {
                            _logger.finest(
                                _localizer.localize(
                                    _messageFactory.getMessage(
                                        "trace.servlet.handingRequestOverToImplementor",
                                        implementor.toString())));
                        }

                        ServletEndpointContextImpl endpointContext =
                            implementor.getContext();
                        try {
                            // set up all context information
                            endpointContext.setMessageContext(messageContext);
                            endpointContext.setHttpServletRequest(req);

                            // non-standard message context properties
                            messageContext.setProperty(
                                MessageContextProperties.SERVLET_CONTEXT,
                                _servletContext);
                            messageContext.setProperty(
                                MessageContextProperties.HTTP_SERVLET_REQUEST,
                                req);
                            messageContext.setProperty(
                                MessageContextProperties.HTTP_SERVLET_RESPONSE,
                                resp);
                            messageContext.setProperty(
                                MessageContextProperties.IMPLEMENTOR,
                                implementor);

                            // dispatch the request
                            implementor.getTie().handle(messageContext);
                        } catch (Exception e) {
                            throw e;
                        } finally {
                            endpointContext.clear();
                        }

                        if (_logger.isLoggable(Level.FINEST)) {
                            _logger.finest(
                                _localizer.localize(
                                    _messageFactory.getMessage(
                                        "trace.servlet.gotResponseFromImplementor",
                                        implementor.toString())));
                        }
                        _implementorFactory.releaseImplementor(
                            name,
                            implementor);
                    }
                } else {
                    _logger.severe(
                        _localizer.localize(
                            _messageFactory.getMessage(
                                "error.servlet.noPortSpecified")));
                    messageContext.writeSimpleErrorResponse(
                        SOAPConstants.FAULT_CODE_SERVER,
                        FAULT_STRING_MISSING_PORT);
                }
            }

            SOAPMessage reply = messageContext.getMessage();

            if (reply.saveRequired()) {
                reply.saveChanges();
            }

            writeReply(resp, messageContext);
        } catch (JAXRPCExceptionBase e) {
            _logger.log(Level.SEVERE, _localizer.localize(e), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                SOAPMessageContext messageContext = new SOAPMessageContext();
                messageContext.writeSimpleErrorResponse(
                    SOAPConstants.FAULT_CODE_SERVER,
                    FAULT_STRING_INTERNAL_SERVER_ERROR
                        + " ("
                        + _localizer.localize(e)
                        + ")");
                writeReply(resp, messageContext);
            } catch (Throwable e2) {
                _logger.log(
                    Level.SEVERE,
                    _localizer.localize(
                        _messageFactory.getMessage(
                            "error.servlet.caughtThrowableWhileRecovering",
                            new Object[] { e2 })),
                    e2);
            }
        } catch (Throwable e) {
            if (e instanceof Localizable) {
                _logger.log(
                    Level.SEVERE,
                    _localizer.localize((Localizable) e),
                    e);
            } else {
                _logger.log(
                    Level.SEVERE,
                    _localizer.localize(
                        _messageFactory.getMessage(
                            "error.servlet.caughtThrowable",
                            new Object[] { e })),
                    e);
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                SOAPMessageContext messageContext = new SOAPMessageContext();
                messageContext.writeSimpleErrorResponse(
                    SOAPConstants.FAULT_CODE_SERVER,
                    FAULT_STRING_INTERNAL_SERVER_ERROR
                        + " ("
                        + e.toString()
                        + ")");
                writeReply(resp, messageContext);
            } catch (Throwable e2) {
                _logger.log(
                    Level.SEVERE,
                    _localizer.localize(
                        _messageFactory.getMessage(
                            "error.servlet.caughtThrowableWhileRecovering",
                            new Object[] { e2 })),
                    e2);
            }
        }
    }

    protected void writeReply(
        HttpServletResponse resp,
        SOAPMessageContext messageContext)
        throws SOAPException, IOException {
        SOAPMessage reply = messageContext.getMessage();

        if (messageContext.isFailure()) {
            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest(
                    _localizer.localize(
                        _messageFactory.getMessage(
                            "trace.servlet.writingFaultResponse")));
            }
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
            if (_logger.isLoggable(Level.FINEST)) {
                _logger.finest(
                    _localizer.localize(
                        _messageFactory.getMessage(
                            "trace.servlet.writingSuccessResponse")));
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        OutputStream os = resp.getOutputStream();
        String[] headers = reply.getMimeHeaders().getHeader("Content-Type");
        if (headers != null && headers.length > 0) {
            resp.setContentType(headers[0]);
        } else {
            resp.setContentType("text/xml");
        }
        putHeaders(reply.getMimeHeaders(), resp);
        reply.writeTo(os);

        os.flush();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {

        Localizer localizer = getLocalizerFor(request);

        try {

            if (request.getPathInfo() != null) {

                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter httpOut = response.getWriter();
                httpOut.println("<html>");
                httpOut.println("<head><title>");
                httpOut.println(
                    localizer.localize(
                        _messageFactory.getMessage("html.nonRootPage.title")));
                httpOut.println("</title></head><body>");
                httpOut.println(
                    localizer.localize(
                        _messageFactory.getMessage("html.nonRootPage.body1")));

                String requestURI = request.getRequestURI();
                int i = requestURI.lastIndexOf(request.getPathInfo());
                if (i == -1) {
                    httpOut.println(
                        localizer.localize(
                            _messageFactory.getMessage(
                                "html.nonRootPage.body2")));
                } else {
                    httpOut.println(
                        localizer.localize(
                            _messageFactory.getMessage(
                                "html.nonRootPage.body3a")));
                    httpOut.println(requestURI.substring(0, i));
                    httpOut.println(
                        localizer.localize(
                            _messageFactory.getMessage(
                                "html.nonRootPage.body3b")));
                }

                httpOut.println("</body></html>");
            } else if (
                request.getQueryString() != null
                    && request.getQueryString().equals(WSDL_QUERY_STRING)) {
                if (_wsdlPublisher.hasDocument()) {
                    _wsdlPublisher.publish(
                        request.getScheme()
                            + "://"
                            + request.getServerName()
                            + ":"
                            + request.getServerPort()
                            + request.getRequestURI()
                            + "/",
                        response);
                } else {
                    response.setContentType("text/html");
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter httpOut = response.getWriter();
                    httpOut.println("<html>");
                    httpOut.println("<head><title>");
                    httpOut.println(
                        localizer.localize(
                            _messageFactory.getMessage("html.wsdlPage.title")));
                    httpOut.println("</title></head><body>");
                    httpOut.println(
                        localizer.localize(
                            _messageFactory.getMessage(
                                "html.wsdlPage.noWsdl")));
                    httpOut.println("</body></html>");
                }
            } else {
                /*
                System.err.println("----");
                System.err.println("CONTEXT PATH   : " + request.getContextPath());
                System.err.println("PATH INFO      : " + request.getPathInfo());
                System.err.println("PATH TRANSLATED: " + request.getPathTranslated());
                System.err.println("QUERY STRING   : " + request.getQueryString());
                System.err.println("REQUEST URI    : " + request.getRequestURI());
                System.err.println("REQUEST SCHEME : " + request.getScheme());
                System.err.println("SERVLET NAME   : " + _servletConfig.getServletName());
                System.err.println();
                System.err.println("TENTATIVE URI  : http://" +
                    request.getServerName() + ":" + request.getServerPort() +
                    request.getRequestURI());
                */

                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter httpOut = response.getWriter();
                httpOut.println("<html>");
                httpOut.println("<head><title>");
                httpOut.println(
                    localizer.localize(
                        _messageFactory.getMessage("html.rootPage.title")));
                httpOut.println("</title></head><body>");
                httpOut.println(
                    localizer.localize(
                        _messageFactory.getMessage("html.rootPage.body1")));

                if (_implementorFactory != null) {
                    httpOut.println(
                        localizer.localize(
                            _messageFactory.getMessage(
                                "html.rootPage.body2a")));
                    Iterator iterator = _implementorFactory.names();
                    if (!iterator.hasNext()) {
                        httpOut.print("NONE");
                    } else {
                        boolean first = true;
                        while (iterator.hasNext()) {
                            String portName = (String) iterator.next();
                            if (!first) {
                                httpOut.print(", ");
                            }
                            httpOut.print('"');
                            httpOut.print(portName);
                            String portURI =
                                request.getScheme()
                                    + "://"
                                    + request.getServerName()
                                    + ":"
                                    + request.getServerPort()
                                    + request.getRequestURI()
                                    + "/"
                                    + portName;
                            httpOut.print('"');
                            httpOut.print(" (");
                            httpOut.print(portURI);
                            httpOut.print(')');
                            first = false;
                        }
                    }
                    httpOut.println(
                        localizer.localize(
                            _messageFactory.getMessage(
                                "html.rootPage.body2b")));

                    if (_wsdlPublisher.hasDocument()) {
                        httpOut.println(
                            localizer.localize(
                                _messageFactory.getMessage(
                                    "html.rootPage.body3a")));
                        httpOut.println(
                            request.getScheme()
                                + "://"
                                + request.getServerName()
                                + ":"
                                + request.getServerPort()
                                + request.getRequestURI()
                                + "?WSDL");
                        httpOut.println(
                            localizer.localize(
                                _messageFactory.getMessage(
                                    "html.rootPage.body3b")));
                    }
                } else {
                    httpOut.println(
                        localizer.localize(
                            _messageFactory.getMessage("html.rootPage.body4")));
                }

                httpOut.println("</body></html>");
            }
        } catch (IOException e) {
            _logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }

    protected Localizer getLocalizerFor(ServletRequest request) {
        Locale locale = request.getLocale();
        if (locale.equals(_localizer.getLocale())) {
            return _localizer;
        }

        synchronized (_localizerMap) {
            Localizer localizer = (Localizer) _localizerMap.get(locale);
            if (localizer == null) {
                localizer = new Localizer(locale);
                _localizerMap.put(locale, localizer);
            }
            return localizer;
        }
    }

    protected static MimeHeaders getHeaders(HttpServletRequest req) {
        Enumeration enums = req.getHeaderNames();
        MimeHeaders headers = new MimeHeaders();

        while (enums.hasMoreElements()) {
            String headerName = (String) enums.nextElement();
            String headerValue = req.getHeader(headerName);
            headers.addHeader(headerName, headerValue);
        }

        return headers;
    }

    protected static void putHeaders(
        MimeHeaders headers,
        HttpServletResponse res) {
        headers.removeHeader("Content-Type");
        headers.removeHeader("Content-Length");
        Iterator it = headers.getAllHeaders();
        while (it.hasNext()) {
            MimeHeader header = (MimeHeader) it.next();
            res.setHeader(header.getName(), header.getValue());
        }
    }

    protected static byte[] readFully(InputStream istream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num = 0;
        while ((num = istream.read(buf)) != -1) {
            bout.write(buf, 0, num);
        }
        byte[] ret = bout.toByteArray();
        return ret;
    }

    public void registerEndpointUrlPattern(
        com.sun.xml.rpc.spi.runtime.RuntimeEndpointInfo info) {
        throw new UnsupportedOperationException();
    }

    public void setSecondDelegate(
        com.sun.xml.rpc.spi.runtime.ServletSecondDelegate delegate) {
        throw new UnsupportedOperationException();
    }

    public void setSystemHandlerDelegate(
        com.sun.xml.rpc.spi.runtime.SystemHandlerDelegate systemHandlerDelegate) {
        throw new UnsupportedOperationException();
    }

    private ServletConfig _servletConfig;
    private ServletContext _servletContext;
    private ImplementorFactory _implementorFactory;
    private WSDLPublisher _wsdlPublisher;
    private Localizer _localizer;
    private Map _localizerMap;
    private LocalizableMessageFactory _messageFactory;

    private static final String CONFIG_FILE_PROPERTY = "configuration.file";
    private static final String WSDL_QUERY_STRING = "WSDL";
    private static final String FAULT_STRING_MISSING_PORT =
        "Missing port information";
    private static final String FAULT_STRING_PORT_NOT_FOUND = "Port not found";
    private static final String FAULT_STRING_INTERNAL_SERVER_ERROR =
        "Internal Server Error";

    private static final Logger _logger =
        Logger.getLogger(
            com.sun.xml.rpc.util.Constants.LoggingDomain + ".server.http");
}
