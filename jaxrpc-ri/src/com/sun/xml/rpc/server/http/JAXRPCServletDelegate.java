/*
 * $Id: JAXRPCServletDelegate.java,v 1.2.2.2 2009-02-18 16:27:37 anbubala Exp $
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

package com.sun.xml.rpc.server.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;
import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;
import com.sun.xml.rpc.client.StubPropertyConstants;

// Dependency with our SAAJ impl for FI
import com.sun.xml.messaging.saaj.soap.MessageImpl;

/**
 * @author JAX-RPC Development Team
 */
public class JAXRPCServletDelegate implements ServletDelegate {

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        init(servletConfig, SOAPVersion.SOAP_11);
    }

    public void init(ServletConfig servletConfig, SOAPVersion ver)
        throws ServletException {
        init(ver); //Initialize SOAP constants
        defaultLocalizer = new Localizer();
        localizerMap = new HashMap();
        localizerMap.put(defaultLocalizer.getLocale(), defaultLocalizer);
        messageFactory =
            new LocalizableMessageFactory("com.sun.xml.rpc.resources.jaxrpcservlet");

        this.servletConfig = servletConfig;
        this.servletContext = servletConfig.getServletContext();

        if (logger.isLoggable(Level.INFO)) {
            logger.info(
                defaultLocalizer.localize(
                    messageFactory.getMessage("servlet.info.initialize")));
        }

        fixedUrlPatternEndpoints = new HashMap();
        pathUrlPatternEndpoints = new ArrayList();

        jaxrpcInfo =
            (JAXRPCRuntimeInfo) servletContext.getAttribute(
                JAXRPCServlet.JAXRPC_RI_RUNTIME_INFO);
        if (jaxrpcInfo == null) {
            warnMissingContextInformation();
        } else {
            Map endpointsByName = new HashMap();
            for (Iterator iter = jaxrpcInfo.getEndpoints().iterator();
                iter.hasNext();
                ) {
                RuntimeEndpointInfo info = (RuntimeEndpointInfo) iter.next();
                if (endpointsByName.containsKey(info.getName())) {
                    logger.warning(
                        defaultLocalizer.localize(
                            messageFactory.getMessage(
                                "servlet.warning.duplicateEndpointName",
                                info.getName())));
                } else {
                    endpointsByName.put(info.getName(), info);
                    registerEndpointUrlPattern(info);
                }
            }
        }

        String publishWSDLParam =
            servletContext.getInitParameter(
                JAXRPCServlet.JAXRPC_RI_PROPERTY_PUBLISH_WSDL);
        publishWSDL =
            (publishWSDLParam == null
                ? true
                : Boolean.valueOf(publishWSDLParam).booleanValue());
        String publishModelParam =
            servletContext.getInitParameter(
                JAXRPCServlet.JAXRPC_RI_PROPERTY_PUBLISH_MODEL);
        publishModel =
            (publishModelParam == null
                ? true
                : Boolean.valueOf(publishModelParam).booleanValue());
        String publishStatusPageParam =
            servletContext.getInitParameter(
                JAXRPCServlet.JAXRPC_RI_PROPERTY_PUBLISH_STATUS_PAGE);
        publishStatusPage =
            (publishStatusPageParam == null
                ? true
                : Boolean.valueOf(publishStatusPageParam).booleanValue());

        implementorCache = (ImplementorCache) createImplementorCache();
        publisher = new WSDLPublisher(servletContext, jaxrpcInfo);

        if (secondDelegate != null)
            secondDelegate.postInit(servletConfig);
    }

    public void destroy() {
        implementorCache.destroy();
        if (logger.isLoggable(Level.INFO)) {
            logger.info(
                defaultLocalizer.localize(
                    messageFactory.getMessage("servlet.info.destroy")));
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {

        if (secondDelegate != null)
            secondDelegate.doGet(request, response);
        else
            doGetDefault(request, response);
    }

    private void doGetDefault(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException {
        try {

            MimeHeaders headers = getHeaders(request);
            Localizer localizer = getLocalizerFor(request);

            if (checkForContent(headers)) {
                writeInvalidMethodType(
                    localizer,
                    response,
                    "Invalid Method Type");
                if (logger.isLoggable(Level.INFO)) {
                    logger.severe(
                        defaultLocalizer.localize(
                            messageFactory.getMessage("servlet.html.method")));
                    logger.severe("Must use Http POST for the service request");
                }
                return;
            }

            RuntimeEndpointInfo targetEndpoint = getEndpointFor(request);
            if (targetEndpoint != null && request.getQueryString() != null) {
                if (request.getQueryString().equals("WSDL")) {
                    if (publishWSDL
                        && targetEndpoint.getWSDLFileName() != null) {
                        // return a WSDL document
                        publisher.handle(
                            targetEndpoint,
                            fixedUrlPatternEndpoints,
                            request,
                            response);
                    } else {
                        writeNotFoundErrorPage(
                            localizer,
                            response,
                            "Invalid request");
                    }
                } else if (request.getQueryString().equals("model")) {
                    if (publishModel
                        && targetEndpoint.getModelFileName() != null) {
                        response.setContentType("application/x-gzip");
                        InputStream istream =
                            servletContext.getResourceAsStream(
                                targetEndpoint.getModelFileName());
                        copyStream(istream, response.getOutputStream());
                        istream.close();
                    } else {
                        writeNotFoundErrorPage(
                            localizer,
                            response,
                            "Invalid request");
                    }
                } else {
                    writeNotFoundErrorPage(
                        localizer,
                        response,
                        "Invalid request");
                }
            } else if (request.getPathInfo() == null) {
                if (publishStatusPage) {
                    // standard browsable page
                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("<html>");
                    out.println("<head><title>");
                    // out.println("Web Services");
                    out.println(
                        localizer.localize(
                            messageFactory.getMessage("servlet.html.title")));
                    out.println("</title></head>");
                    out.println("<body>");
                    // out.println("<h1>Web Services</h1>");
                    out.println(
                        localizer.localize(
                            messageFactory.getMessage("servlet.html.title2")));
                    if (jaxrpcInfo == null) {
                        // out.println("<p>No JAX-RPC context information available.</p>");
                        out.println(
                            localizer.localize(
                                messageFactory.getMessage(
                                    "servlet.html.noInfoAvailable")));
                    } else {
                        out.println("<table width='100%' border='1'>");
                        out.println("<tr>");
                        out.println("<td>");
                        // out.println("Port Name");
                        out.println(
                            localizer.localize(
                                messageFactory.getMessage(
                                    "servlet.html.columnHeader.portName")));
                        out.println("</td>");
                        out.println("<td>");
                        // out.println("Status");
                        out.println(
                            localizer.localize(
                                messageFactory.getMessage(
                                    "servlet.html.columnHeader.status")));
                        out.println("</td>");
                        out.println("<td>");
                        // out.println("Information");
                        out.println(
                            localizer.localize(
                                messageFactory.getMessage(
                                    "servlet.html.columnHeader.information")));
                        out.println("</td>");
                        out.println("</tr>");
                        String baseAddress =
                            request.getScheme()
                                + "://"
                                + request.getServerName()
                                + ":"
                                + request.getServerPort()
                                + request.getContextPath();

                        for (Iterator iter =
                            jaxrpcInfo.getEndpoints().iterator();
                            iter.hasNext();
                            ) {
                            RuntimeEndpointInfo info =
                                (RuntimeEndpointInfo) iter.next();
                            String endpointAddress =
                                baseAddress + getValidPathForEndpoint(info);
                            out.println("<tr>");
                            out.println("<td>" + info.getName() + "</td>");
                            out.println("<td>");
                            if (info.isDeployed()) {
                                // out.println("ACTIVE");
                                out.println(
                                    localizer.localize(
                                        messageFactory.getMessage(
                                            "servlet.html.status.active")));
                            } else {
                                // out.println("ERROR");
                                out.println(
                                    localizer.localize(
                                        messageFactory.getMessage(
                                            "servlet.html.status.error")));
                            }
                            out.println("</td>");
                            out.println("<td>");
                            out.println(
                                localizer.localize(
                                    messageFactory.getMessage(
                                        "servlet.html.information.table",
                                        new Object[] {
                                            endpointAddress,
                                            info.getPortName(),
                                            info.getRemoteInterface().getName(),
                                            info
                                                .getImplementationClass()
                                                .getName()})));

                            out.println("</td>");
                            out.println("</tr>");
                        }
                        out.println("</table>");
                    }
                    out.println("</body>");
                    out.println("</html>");
                } else {
                    writeNotFoundErrorPage(
                        localizer,
                        response,
                        "Invalid request");
                }
            } else {
                if (publishStatusPage) {
                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("<html>");
                    out.println("<head><title>");
                    // out.println("Web Services");
                    out.println(
                        localizer.localize(
                            messageFactory.getMessage("servlet.html.title")));
                    out.println("</title></head>");
                    out.println("<body>");
                    out.println("</body>");
                    out.println("</html>");
                } else {
                    writeNotFoundErrorPage(
                        localizer,
                        response,
                        "Invalid request");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }

    public void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException {
        try {

            MimeHeaders headers = getHeaders(request);
            SOAPMessageContext messageContext = new SOAPMessageContext();

            if (!checkContentType(headers)) {
                writeInvalidContentType(response, headers);
                return;
            }

            SOAPMessage message =
                getSOAPMessageFromRequest(request, headers, messageContext);

            if (message == null) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.info(
                        defaultLocalizer.localize(
                            messageFactory.getMessage(
                                "servlet.info.emptyRequestMessage")));
                }
                messageContext.writeSimpleErrorResponse(
                    soapEncodingConstants.getFaultCodeClient(),
                    defaultLocalizer.localize(
                        messageFactory.getMessage(
                            "servlet.info.emptyRequestMessage")));
                //no saop message -
            } 
            else {                
                messageContext.setMessage(message);
                RuntimeEndpointInfo targetEndpoint = getEndpointFor(request);

                if (targetEndpoint != null) {
                    if (logger.isLoggable(Level.FINEST)) {
                        logger.finest(
                            defaultLocalizer.localize(
                                messageFactory.getMessage(
                                    "servlet.trace.gotRequestForEndpoint",
                                    targetEndpoint.getName())));
                    }

                    Implementor implementor =
                        (Implementor) implementorCache.getImplementorFor(
                            targetEndpoint);

                    if (implementor == null) {
                        logger.severe(
                            defaultLocalizer.localize(
                                messageFactory.getMessage(
                                    "servlet.error.noImplementorForEndpoint",
                                    targetEndpoint.getName())));
                        messageContext.writeSimpleErrorResponse(
                            soapEncodingConstants.getFaultCodeServer(),
                            defaultLocalizer.localize(
                                messageFactory.getMessage(
                                    "servlet.faultstring.portNotFound",
                                    targetEndpoint.getName())));
                        //internal server error status code?
                    } else {
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest(
                                defaultLocalizer.localize(
                                    messageFactory.getMessage(
                                        "servlet.trace.invokingImplementor",
                                        implementor.toString())));
                        }

                        ServletEndpointContextImpl endpointContext =
                            implementor.getContext();
                        SOAPMessageContext clientContext = null;
                        try {
                            // set up all context information
                            endpointContext.setMessageContext(messageContext);
                            endpointContext.setHttpServletRequest(request);

                            // non-standard message context properties
                            messageContext.setProperty(
                                MessageContextProperties.SERVLET_CONTEXT,
                                servletContext);
                            messageContext.setProperty(
                                MessageContextProperties.HTTP_SERVLET_REQUEST,
                                request);
                            messageContext.setProperty(
                                MessageContextProperties.HTTP_SERVLET_RESPONSE,
                                response);
                            messageContext.setProperty(
                                MessageContextProperties.IMPLEMENTOR,
                                implementor);
			    
                            if (systemHandlerDelegate == null) {
                                implementor.getTie().handle(messageContext);
                            } else if (systemHandlerDelegate.processRequest(messageContext)) {
                                implementor.getTie().handle(messageContext);
                                systemHandlerDelegate.processResponse(messageContext);
                            }
                        } catch (Exception e) {
                            throw e;
                        } finally {
                            endpointContext.clear();
                        }

                        implementorCache.releaseImplementor(
                            targetEndpoint,
                            implementor);
                    }
                } else { //target endpoint is null
                    logger.severe(
                        defaultLocalizer.localize(
                            messageFactory.getMessage(
                                "servlet.error.noEndpointSpecified")));
                    messageContext.writeSimpleErrorResponse(
                        soapEncodingConstants.getFaultCodeClient(),
                        defaultLocalizer.localize(
                            messageFactory.getMessage(
                                "servlet.faultstring.missingPort")));
                }
            }

            SOAPMessage reply = messageContext.getMessage();

            if (reply.saveRequired()) {
                reply.saveChanges();
            }

            writeReply(response, messageContext);
        } catch (JAXRPCExceptionBase e) {
            logger.log(Level.SEVERE, defaultLocalizer.localize(e), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                SOAPMessageContext messageContext = new SOAPMessageContext();
                messageContext.writeSimpleErrorResponse(
                    soapEncodingConstants.getFaultCodeServer(),
                    defaultLocalizer.localize(
                        messageFactory.getMessage(
                            "servlet.faultstring.internalServerError",
                            defaultLocalizer.localize(e))));
                writeReply(response, messageContext);
            } catch (Throwable e2) {
                logger.log(
                    Level.SEVERE,
                    "caught throwable while recovering",
                    e2);
            }
        } catch (Throwable e) {
            if (e instanceof Localizable) {
                logger.log(
                    Level.SEVERE,
                    defaultLocalizer.localize((Localizable) e),
                    e);
            } else {
                logger.log(Level.SEVERE, "caught throwable", e);
            }

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                SOAPMessageContext messageContext = new SOAPMessageContext();
                messageContext.writeSimpleErrorResponse(
                    soapEncodingConstants.getFaultCodeServer(),
                    defaultLocalizer.localize(
                        messageFactory.getMessage(
                            "servlet.faultstring.missingPort")));
                writeReply(response, messageContext);
            } catch (Throwable e2) {
                logger.log(
                    Level.SEVERE,
                    "caught throwable while recovering",
                    e2);
                return;
            }
        }
    }

    protected void writeReply(
        HttpServletResponse resp,
        SOAPMessageContext messageContext)
        throws SOAPException, IOException {

        // in case of one-way operation, send no reply or fault
        if (checkMessageContextProperty(messageContext,
            MessageContextProperties.ONE_WAY_OPERATION)) {
            return;
        }

        SOAPMessage reply = messageContext.getMessage();
        int statusCode = 0;
        if (messageContext.isFailure()) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(
                    defaultLocalizer.localize(
                        messageFactory.getMessage(
                            "servlet.trace.writingFaultResponse")));
            }

            if (checkMessageContextProperty(messageContext,
                MessageContextProperties.CLIENT_BAD_REQUEST)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                //bad client request
                setContentTypeAndFlush(resp);
                return;
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(
                    defaultLocalizer.localize(
                        messageFactory.getMessage(
                            "servlet.trace.writingSuccessResponse")));
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

    protected void writeNotFoundErrorPage(
        Localizer localizer,
        HttpServletResponse response,
        String message)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>");
        out.println(
            localizer.localize(
                messageFactory.getMessage("servlet.html.title")));
        out.println("</title></head>");
        out.println("<body>");
        out.println(
            localizer.localize(
                messageFactory.getMessage("servlet.html.notFound", message)));
        out.println("</body>");
        out.println("</html>");
    }

    // no fault for 415 client error
    protected void writeInvalidContentType(HttpServletResponse response,
        MimeHeaders headers) throws SOAPException, IOException 
    {
        response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        String[] contentTypes = headers.getHeader("Content-Type");
        if ((contentTypes != null) && (contentTypes.length >= 1)) {
            response.setHeader("ContentType-Received", contentTypes[0]);
        }
        //bad client content-type
        setContentTypeAndFlush(response);
    }

    /*
     * Used to send back the message after a 4XX response code has been set
     */
    private void setContentTypeAndFlush(HttpServletResponse response)
        throws IOException {
        response.setContentType("text/xml");
        response.flushBuffer(); // prevent html message in response
        response.getWriter().close();
    }

    protected void writeInvalidMethodType(
        Localizer localizer,
        HttpServletResponse response,
        String message)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>");
        out.println(
            localizer.localize(
                messageFactory.getMessage("servlet.html.title")));
        out.println("</title></head>");
        out.println("<body>");
        out.println(
            localizer.localize(
                messageFactory.getMessage("servlet.html.method", message)));
        out.println("</body>");
        out.println("</html>");

    }

    protected void warnMissingContextInformation() {
        if (secondDelegate != null)
            secondDelegate.warnMissingContextInformation();
        else
            logger.warning(
                defaultLocalizer.localize(
                    messageFactory.getMessage(
                        "servlet.warning.missingContextInformation")));
    }

    protected com
        .sun
        .xml
        .rpc
        .spi
        .runtime
        .ImplementorCache createImplementorCache() {
        if (secondDelegate != null) {
            return secondDelegate.createImplementorCache(servletConfig);
        }
        return new ImplementorCache(servletConfig);
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
        // CR-6495282, Merge from JavaCAPS RTS for backward compatibility
        //headers.removeHeader("Content-Length");
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

        if (istream != null) {
            while ((num = istream.read(buf)) != -1) {
                bout.write(buf, 0, num);
            }
        }
        byte[] ret = bout.toByteArray();
        return ret;
    }

    public void registerEndpointUrlPattern(
        com.sun.xml.rpc.spi.runtime.RuntimeEndpointInfo info) {
        String urlPattern = ((RuntimeEndpointInfo) info).getUrlPattern();
        if (urlPattern.indexOf("*.") != -1) {
            // cannot deal with implicit mapping right now
            logger.warning(
                defaultLocalizer.localize(
                    messageFactory.getMessage(
                        "servlet.warning.ignoringImplicitUrlPattern",
                        ((RuntimeEndpointInfo) info).getName())));
        } else if (urlPattern.endsWith("/*")) {
            pathUrlPatternEndpoints.add(info);
        } else {
            if (fixedUrlPatternEndpoints.containsKey(urlPattern)) {
                logger.warning(
                    defaultLocalizer.localize(
                        messageFactory.getMessage(
                            "servlet.warning.duplicateEndpointUrlPattern",
                            ((RuntimeEndpointInfo) info).getName())));
            } else {
                fixedUrlPatternEndpoints.put(urlPattern, info);
            }
        }
    }

    protected String getValidPathForEndpoint(RuntimeEndpointInfo info) {
        String s = info.getUrlPattern();
        if (s.endsWith("/*")) {
            return s.substring(0, s.length() - 2);
        } else {
            return s;
        }
    }

    protected RuntimeEndpointInfo getEndpointFor(HttpServletRequest request) {

        /*System.err.println("----");
        System.err.println("CONTEXT PATH   : " + request.getContextPath());
        System.err.println("PATH INFO      : " + request.getPathInfo());
        System.err.println("PATH TRANSLATED: " + request.getPathTranslated());
        System.err.println("QUERY STRING   : " + request.getQueryString());
        System.err.println("REQUEST URI    : " + request.getRequestURI());
        System.err.println();
         */

        String path =
            request.getRequestURI().substring(
                request.getContextPath().length());
        RuntimeEndpointInfo result =
            (RuntimeEndpointInfo) fixedUrlPatternEndpoints.get(path);
        if (result == null) {
            for (Iterator iter = pathUrlPatternEndpoints.iterator();
                iter.hasNext();
                ) {
                RuntimeEndpointInfo candidate =
                    (RuntimeEndpointInfo) iter.next();
                if (candidate.getUrlPattern().startsWith(path)) {
                    result = candidate;
                    break;
                }
            }
        }

        return result;
    }

    protected SOAPMessage getSOAPMessageFromRequest(
        HttpServletRequest request,
        MimeHeaders headers,
        SOAPMessageContext messageContext)
        throws IOException {
        SOAPMessage message = null;

        InputStream is = request.getInputStream();

        byte[] bytes = readFully(is);

        int length =
            request.getContentLength() == -1
                ? bytes.length
                : request.getContentLength();
        ByteInputStream in = new ByteInputStream(bytes, length);
        message = messageContext.createMessage(headers, in);

        return message;
    }

    protected boolean checkContentType(MimeHeaders headers) {
        String[] contentTypes = headers.getHeader("Content-Type");
        if ((contentTypes != null) && (contentTypes.length >= 1)) {
            final String contentType = contentTypes[0];
            if (contentType.indexOf("text/xml") != -1 ||
                contentType.indexOf("application/fastinfoset") != -1)
            {
                return true;
            }
        }
        return false;
    }

    protected boolean checkContentLength(MimeHeaders headers) {
        String[] contentLength = headers.getHeader("Content-Length");
        if ((contentLength != null) && (contentLength.length > 0)) {
            int length = new Integer(contentLength[0]).intValue();
            if (length > 0) {
                return true;
            }
        }
        return false;
    }

    boolean checkForContent(MimeHeaders headers) {
        if (checkContentType(headers)) {
            if (checkContentLength(headers))
                return true;
        }
        return false;
    }

    protected Localizer getLocalizerFor(ServletRequest request) {
        Locale locale = request.getLocale();
        if (locale.equals(defaultLocalizer.getLocale())) {
            return defaultLocalizer;
        }

        synchronized (localizerMap) {
            Localizer localizer = (Localizer) localizerMap.get(locale);
            if (localizer == null) {
                localizer = new Localizer(locale);
                localizerMap.put(locale, localizer);
            }
            return localizer;
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

    boolean checkMessageContextProperty(
        SOAPMessageContext messageContext,
        String property) {

        String prop = (String) messageContext.getProperty(property);
        if (prop != null) {
            if (prop.equalsIgnoreCase("true"))
                return true;
        }
        return false;
    }

    void setMessageContextProperty(
        SOAPMessageContext messageContext,
        String property) {
        messageContext.setProperty(property, "true");
    }

    public void setSecondDelegate(
        com.sun.xml.rpc.spi.runtime.ServletSecondDelegate secondDelegate) {
        this.secondDelegate = secondDelegate;
    }

    public void setSystemHandlerDelegate(
        com.sun.xml.rpc.spi.runtime.SystemHandlerDelegate systemHandlerDelegate) {
        this.systemHandlerDelegate = systemHandlerDelegate;
    }

    private ServletConfig servletConfig;
    private ServletContext servletContext;
    private JAXRPCRuntimeInfo jaxrpcInfo;
    private Localizer defaultLocalizer;
    private LocalizableMessageFactory messageFactory;
    private ImplementorCache implementorCache;
    private Map fixedUrlPatternEndpoints;
    private List pathUrlPatternEndpoints;
    private Map localizerMap;
    private WSDLPublisher publisher;
    private boolean publishWSDL;
    private boolean publishModel;
    private boolean publishStatusPage;

    private static final Logger logger =
        Logger.getLogger(
            com.sun.xml.rpc.util.Constants.LoggingDomain + ".server.http");

    private com.sun.xml.rpc.spi.runtime.ServletSecondDelegate secondDelegate =
        null;

    private com.sun.xml.rpc.spi.runtime.SystemHandlerDelegate systemHandlerDelegate =
        null;
}
