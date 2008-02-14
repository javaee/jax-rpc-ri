/*
 * $Id: HttpClientTransport.java,v 1.2.2.3 2008-02-14 17:27:03 venkatajetti Exp $
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

/**
 * @author JAX-RPC Development Team
 */
package com.sun.xml.rpc.client.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.security.PrivilegedAction;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportException;
import com.sun.xml.rpc.client.StubPropertyConstants;
import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDBase64BinaryEncoder;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class HttpClientTransport
    implements ClientTransport, StubPropertyConstants {
    static Logger _logger = Logger.getLogger(HttpClientTransport.class.getName());
    
    public static final String HTTP_SOAPACTION_PROPERTY = "http.soap.action";
    private static final SimpleTypeEncoder base64Encoder =
        XSDBase64BinaryEncoder.getInstance();
    private static String LAST_ENDPOINT = "";
    private static boolean redirect = true;
    private static final int START_REDIRECT_COUNT = 3;
    private static int redirectCount = START_REDIRECT_COUNT;

    public HttpClientTransport() {
        this(null);
    }

    public HttpClientTransport(OutputStream logStream) {
        try {
            _messageFactory = MessageFactory.newInstance();
            _logStream = logStream;
        } catch (Exception e) {
            throw new ClientTransportException("http.client.cannotCreateMessageFactory");
        }
    }

    public void invoke(String endpoint, SOAPMessageContext context) throws ClientTransportException {
        //using an HttpURLConnection the soap message is sent
        //over the wire
        try {

            HttpURLConnection httpConnection =
                createHttpConnection(endpoint, context);

            setupContextForInvoke(context);

            CookieJar cookieJar = sendCookieAsNeeded(context, httpConnection);

            moveHeadersFromContextToConnection(context, httpConnection);
            
            if (DEBUG) {
                checkMessageContentType(httpConnection.getRequestProperty("Content-Type"), false);
            }

            writeMessageToConnection(context, httpConnection);
            
            boolean isFailure = connectForResponse(httpConnection, context);
            int statusCode = httpConnection.getResponseCode();

            //http URL redirection does not redirect http requests
            //to an https endpoint probably due to a bug in the jdk
            //or by intent - to workaround this if an error code
            //of HTTP_MOVED_TEMP or HTTP_MOVED_PERM is received then
            //the jaxrpc client will reinvoke the original request
            //to the new endpoint - kw bug 4890118
            if (checkForRedirect(statusCode)){
                redirectRequest(httpConnection, context);
                return;
            }

            MimeHeaders headers = collectResponseMimeHeaders(httpConnection);
            
            saveCookieAsNeeded(context, httpConnection, cookieJar);

            SOAPMessage response = null;
            //get the response from the HttpURLConnection
            try {
                response = readResponse(httpConnection, isFailure, headers);
            } catch (SOAPException e) {
                if (statusCode == HttpURLConnection.HTTP_NO_CONTENT
                    || (isFailure
                        && statusCode != HttpURLConnection.HTTP_INTERNAL_ERROR)) {
                    throw new ClientTransportException(
                        "http.status.code",
                        new Object[] {
                            new Integer(statusCode),
                            httpConnection.getResponseMessage()});
                }
                throw e;
            }
            httpConnection = null;

            logResponseMessage(context, response);

            if (DEBUG) {
                checkMessageContentType(headers.getHeader("Content-Type")[0], true);
            }
            
            context.setMessage(response);
            // do not set the failure flag, because stubs cannot rely on it,
            // since transports different from HTTP may not be able to set it
            // context.setFailure(isFailure);

        } catch (ClientTransportException e) {
            // let these through unmodified
            throw e;
        } catch (Exception e) {
            if (e instanceof Localizable) {
                throw new ClientTransportException(
                    "http.client.failed",
                    (Localizable) e);
            } else {
                throw new ClientTransportException(
                    "http.client.failed",
                    new LocalizableExceptionAdapter(e));
            }
        } 
    }

    public void invokeOneWay(String endpoint, SOAPMessageContext context) {

        //one way send of message over the wire
        //no response will be returned
        try {
            HttpURLConnection httpConnection =
                createHttpConnection(endpoint, context);

            setupContextForInvoke(context);

            moveHeadersFromContextToConnection(context, httpConnection);

            writeMessageToConnection(context, httpConnection);

            forceMessageToBeSent(httpConnection, context);

        } catch (Exception e) {
            if (e instanceof Localizable) {
                throw new ClientTransportException(
                    "http.client.failed",
                    (Localizable) e);
            } else {
                throw new ClientTransportException(
                    "http.client.failed",
                    new LocalizableExceptionAdapter(e));
            }
        }
    }

    protected void logResponseMessage(
        SOAPMessageContext context,
        SOAPMessage response)
        throws IOException, SOAPException {

        if (_logStream != null) {
            String s = "Response\n";
            _logStream.write(s.getBytes());
            s =
                "Http Status Code: "
                    + context.getProperty(StubPropertyConstants.HTTP_STATUS_CODE)
                    + "\n\n";
            _logStream.write(s.getBytes());
            for (Iterator iter =
                response.getMimeHeaders().getAllHeaders();
                iter.hasNext();
                ) {
                MimeHeader header = (MimeHeader) iter.next();
                s = header.getName() + ": " + header.getValue() + "\n";
                _logStream.write(s.getBytes());
            }
            _logStream.flush();
            response.writeTo(_logStream);
            s = "******************\n\n";
            _logStream.write(s.getBytes());
        }
    }

    protected SOAPMessage readResponse(
        HttpURLConnection httpConnection,
        boolean isFailure,
        MimeHeaders headers)
        throws IOException, SOAPException {
        ByteInputStream in;
        InputStream contentIn =
            (isFailure
                ? httpConnection.getErrorStream()
                : httpConnection.getInputStream());

        byte[] bytes = readFully(contentIn);
        int length =
            httpConnection.getContentLength() == -1
                ? bytes.length
                : httpConnection.getContentLength();
        in = new ByteInputStream(bytes, length);

        // If in debug mode and we got HTML, print it out
        if (DEBUG) {
            if (httpConnection.getContentType().indexOf("text/html") >= 0) {
                System.out.println("");
                for (int i = 0; i < length; i++) {
                    System.out.print((char) bytes[i]);
                }
                System.out.println("");
            }
        }
        
        SOAPMessage response = _messageFactory.createMessage(headers, in);

        contentIn.close();

        return response;
    }

    protected MimeHeaders collectResponseMimeHeaders(HttpURLConnection httpConnection) {
        MimeHeaders headers = new MimeHeaders();
        for (int i = 1;; ++i) {
            String key = httpConnection.getHeaderFieldKey(i);
            if (key == null) {
                break;
            }
            String value = httpConnection.getHeaderField(i);
            try {
                headers.addHeader(key, value);
            } catch (IllegalArgumentException e) {
                // ignore headers that are illegal in MIME
            }
        }
        return headers;
    }

    protected boolean connectForResponse(
        HttpURLConnection httpConnection,
        SOAPMessageContext context)
        throws IOException {

        httpConnection.connect();
        return checkResponseCode(httpConnection, context);
    }

    protected void forceMessageToBeSent(
        HttpURLConnection httpConnection,
        SOAPMessageContext context)
        throws IOException {

        try {
            httpConnection.connect();
            // CR-6660386, Merge from JavaCAPS RTS for backward compatibility
            // If there is an 404 HTTP status code getInputStream() will throw an IOException and will make checkResponseCode unreachable. 
            //Move this call so that this method will be call in all scenario as it was designed. 
            //checkResponseCode can throw IOException which has been designed to supressed.
            checkResponseCode(httpConnection, context);
            httpConnection.getInputStream();

        } catch (IOException io) {
        }
    }

    /*
     * Will throw an exception instead of returning 'false' if there is no
     * return message to be processed (i.e., in the case of an UNAUTHORIZED
     * response from the servlet or 404 not found)
     */
    protected boolean checkResponseCode(
        HttpURLConnection httpConnection,
        SOAPMessageContext context)
        throws IOException {
        boolean isFailure = false;
        try {

            int statusCode = httpConnection.getResponseCode();
            context.setProperty(
                StubPropertyConstants.HTTP_STATUS_CODE,
                Integer.toString(statusCode));
            if ((httpConnection.getResponseCode()
                == HttpURLConnection.HTTP_INTERNAL_ERROR)) {
                isFailure = true;
                //added HTTP_ACCEPT for 1-way operations
            } else if (
                httpConnection.getResponseCode()
                    == HttpURLConnection.HTTP_UNAUTHORIZED) {

                // no soap message returned, so skip reading message and throw exception
                throw new ClientTransportException(
                    "http.client.unauthorized",
                    httpConnection.getResponseMessage());
            } else if (
                httpConnection.getResponseCode()
                    == HttpURLConnection.HTTP_NOT_FOUND) {

                // no message returned, so skip reading message and throw exception
                throw new ClientTransportException(
                    "http.not.found",
                    httpConnection.getResponseMessage());
            } else if (
                (statusCode == HttpURLConnection.HTTP_MOVED_TEMP) ||
                (statusCode == HttpURLConnection.HTTP_MOVED_PERM)){
                isFailure = true;

                if (!redirect || (redirectCount <=0)){
                throw new ClientTransportException(
                    "http.status.code",
                    new Object[] {
                        new Integer(statusCode),
                        getStatusMessage(httpConnection)});
                }
            } else if (
                statusCode < 200 || (statusCode >= 303 && statusCode < 500)) {
                throw new ClientTransportException(
                    "http.status.code",
                    new Object[] {
                        new Integer(statusCode),
                        getStatusMessage(httpConnection)});
            } else if (statusCode >= 500) {
                isFailure = true;
            }
        } catch (IOException e) {
            // on JDK1.3.1_01, we end up here, but then getResponseCode() succeeds!
            if (httpConnection.getResponseCode()
                == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                isFailure = true;
            } else {
                throw e;
            }
        }

        return isFailure;

    }

    protected String getStatusMessage(HttpURLConnection httpConnection)
        throws IOException {
        int statusCode = httpConnection.getResponseCode();
        String message = httpConnection.getResponseMessage();
        if (statusCode == HttpURLConnection.HTTP_CREATED
            || (statusCode >= HttpURLConnection.HTTP_MULT_CHOICE
                && statusCode != HttpURLConnection.HTTP_NOT_MODIFIED
                && statusCode < HttpURLConnection.HTTP_BAD_REQUEST)) {
            String location = httpConnection.getHeaderField("Location");
            if (location != null)
                message += " - Location: " + location;
        }
        return message;
    }

    protected void logRequestMessage(SOAPMessageContext context)
        throws IOException, SOAPException {

        if (_logStream != null) {
            String s = "******************\nRequest\n";
            _logStream.write(s.getBytes());
            for (Iterator iter =
                context.getMessage().getMimeHeaders().getAllHeaders();
                iter.hasNext();
                ) {
                MimeHeader header = (MimeHeader) iter.next();
                s = header.getName() + ": " + header.getValue() + "\n";
                _logStream.write(s.getBytes());
            }
            _logStream.flush();
            context.getMessage().writeTo(_logStream);
            s = "\n";
            _logStream.write(s.getBytes());
            _logStream.flush();
        }
    }

    protected void writeMessageToConnection(
        SOAPMessageContext context,
        final HttpURLConnection httpConnection)
        throws IOException, SOAPException {
        //OutputStream contentOut = httpConnection.getOutputStream();
        //Performance improvement: wrap HttpURLConnection.getOutputStream in a doPrivileged block 
        //to avoid the call for security check by the security manager for getPropertyAction 
        OutputStream contentOut = (OutputStream) java.security.AccessController
            .doPrivileged(new PrivilegedAction() {
                public Object run() {
                    try {
                        return httpConnection.getOutputStream();
                    } catch (IOException e) {
                        _logger.log(Level.SEVERE, "cannot get httpConnection outputstream", e);
                    }
                    return null;
                }   
            });
        context.getMessage().writeTo(contentOut);
        contentOut.flush();
        contentOut.close();
        logRequestMessage(context);
    }

    protected void moveHeadersFromContextToConnection(
        SOAPMessageContext context,
        HttpURLConnection httpConnection) {
        for (Iterator iter =
            context.getMessage().getMimeHeaders().getAllHeaders();
            iter.hasNext();
            ) {
            MimeHeader header = (MimeHeader) iter.next();
            httpConnection.setRequestProperty(
                header.getName(),
                header.getValue());
        }
    }

    protected CookieJar sendCookieAsNeeded(
        SOAPMessageContext context,
        HttpURLConnection httpConnection) {
        Boolean shouldMaintainSessionProperty =
            (Boolean) context.getProperty(SESSION_MAINTAIN_PROPERTY);
        boolean shouldMaintainSession =
            (shouldMaintainSessionProperty == null
                ? false
                : shouldMaintainSessionProperty.booleanValue());
        if (shouldMaintainSession) {
            CookieJar cookieJar =
                (CookieJar) context.getProperty(
                    StubPropertyConstants.HTTP_COOKIE_JAR);
            if (cookieJar == null) {
                cookieJar = new CookieJar();
            }
            cookieJar.applyRelevantCookies(httpConnection);
            return cookieJar;
        } else {
            return null;
        }
    }

    protected void saveCookieAsNeeded(
        SOAPMessageContext context,
        HttpURLConnection httpConnection,
        CookieJar cookieJar) {
        if (cookieJar != null) {
            cookieJar.recordAnyCookies(httpConnection);
            context.setProperty(
                StubPropertyConstants.HTTP_COOKIE_JAR,
                cookieJar);
        }
    }

    protected void setupContextForInvoke(SOAPMessageContext context)
        throws SOAPException, Exception {
        if (context.getMessage().saveRequired()) {
            context.getMessage().saveChanges();
        }
        String soapAction =
            (String) context.getProperty(HTTP_SOAPACTION_PROPERTY);
        // From SOAP 1.1 spec section 6.1.1 "The header field value of empty string ("") means that
        // the intent of the SOAP message is provided by the HTTP Request-URI. No value means that
        // there is no indication of the intent of the message." Here I provide a mechanism for
        // providing "no value" (PBG):
        //kw null soapaction? made not null-
        if (soapAction == null) {
            context.getMessage().getMimeHeaders().setHeader(
                "SOAPAction",
                "\"\"");
            // httpConnection.setRequestProperty("SOAPAction", "");
        } else {
            context.getMessage().getMimeHeaders().setHeader(
                "SOAPAction",
                "\"" + soapAction + "\"");
            // httpConnection.setRequestProperty("SOAPAction", "\"" + soapAction + "\"");
        }
        //set up Basic Authentication mime header
        String credentials = (String) context.getProperty(USERNAME_PROPERTY);
        if (credentials != null) {
            credentials += ":"
                + (String) context.getProperty(PASSWORD_PROPERTY);
            credentials =
                base64Encoder.objectToString(credentials.getBytes(), null);
            context.getMessage().getMimeHeaders().setHeader(
                "Authorization",
                "Basic " + credentials);
        }
    }

    protected HttpURLConnection createHttpConnection(
        String endpoint,
        SOAPMessageContext context)
        throws IOException {

        boolean verification = false;
        // does the client want client hostname verification by the service
        String verificationProperty =
            (String) context.getProperty(
                StubPropertyConstants.HOSTNAME_VERIFICATION_PROPERTY);
        if (verificationProperty != null) {
            if (verificationProperty.equalsIgnoreCase("true"))
                verification = true;
        }

         // does the client want request redirection to occur
        String redirectProperty =
            (String) context.getProperty(
                StubPropertyConstants.REDIRECT_REQUEST_PROPERTY);
        if (redirectProperty != null) {
            if (redirectProperty.equalsIgnoreCase("false"))
                redirect = false;
        }

        checkEndpoints(endpoint);

        HttpURLConnection httpConnection = createConnection(endpoint);

        if (!verification) {
            // for https hostname verification  - turn off by default
            if (httpConnection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) httpConnection).setHostnameVerifier(
                    new HttpClientVerifier());
            }
        }

        // allow interaction with the web page - user may have to supply
        // username, password id web page is accessed from web browser
        httpConnection.setAllowUserInteraction(true);
        // enable input, output streams
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        // the soap message is always sent as a Http POST
        // HTTP Get is disallowed by BP 1.0
        httpConnection.setRequestMethod("POST");
        // Content type must be xml
        httpConnection.setRequestProperty("Content-Type", "text/xml");
        
        return httpConnection;
    }

    private java.net.HttpURLConnection createConnection(String endpoint)
        throws IOException {
        return (HttpURLConnection) new URL(endpoint).openConnection();
    }

    private void redirectRequest(HttpURLConnection httpConnection, SOAPMessageContext context){
        String redirectEndpoint = httpConnection.getHeaderField("Location");
        if (redirectEndpoint != null){
            httpConnection.disconnect();
            invoke(redirectEndpoint, context);
        }
        else System.out.println("redirection Failed");
    }

    private boolean checkForRedirect(int statusCode){
        return (((statusCode == 301) || (statusCode == 302)) && redirect && (redirectCount-- > 0));
    }

    private void checkEndpoints(String currentEndpoint){
        if (!LAST_ENDPOINT.equalsIgnoreCase(currentEndpoint)){
            redirectCount = START_REDIRECT_COUNT;
            LAST_ENDPOINT = currentEndpoint;
        }
    }

    private byte[] readFully(InputStream istream) throws IOException {
        if (istream == null)
            return new byte[0];
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num = 0;
        while ((num = istream.read(buf)) != -1) {
            bout.write(buf, 0, num);
        }
        byte[] ret = bout.toByteArray();
        return ret;
    }

    private static void checkMessageContentType(String contentType, boolean response) {
        if (contentType.indexOf("text/html") >= 0) {
            System.out.println("##### WARNING " + 
                (response ? "RESPONSE" : "REQUEST") +
                " CONTENT TYPE INCLUDES 'text/html'");
            return;     // Allow HTML
        }
        
        System.out.println("##### CHECKING " +
            (response ? "RESPONSE" : "REQUEST") +
            " CONTENT TYPE '" + contentType + "'");
        
        String negotiation = 
            System.getProperty(com.sun.xml.rpc.client.StubPropertyConstants.CONTENT_NEGOTIATION_PROPERTY, "none").intern();
        
        // Use indexOf() to handle Multipart/related types
        if (negotiation == "none") {
            // OK only if XML
            if (contentType.indexOf("text/xml") < 0) {
                throw new RuntimeException("Invalid content type '" + contentType 
                    + "' in " + (response ? "response" : "request") + 
                    " with conneg set to '" + negotiation + "'.");
            }
        }
        else if (negotiation == "optimistic") {
            // OK only if FI
            if (contentType.indexOf("application/fastinfoset") < 0) {
                throw new RuntimeException("Invalid content type '" + contentType 
                    + "' in " + (response ? "response" : "request") + 
                    " with conneg set to '" + negotiation + "'.");
            }
        }
        else if (negotiation == "pessimistic") {
            // OK if FI request is anything and response is FI
            if (response && 
                    contentType.indexOf("application/fastinfoset") < 0) {
                throw new RuntimeException("Invalid content type '" + contentType 
                    + "' in " + (response ? "response" : "request") + 
                    " with conneg set to '" + negotiation + "'.");
            }
        }
    }
    
    // overide default SSL HttpClientVerifier to always return true
    // effectively overiding Hostname client verification when using SSL
    static class HttpClientVerifier implements HostnameVerifier {
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
    
    /**
     * Flag used to enable conneg content type check.
     */
    static private boolean DEBUG;
    static {
        final String value = System.getProperty("debug", "false");       
        DEBUG = value.equals("on") || value.equals("true");        
    }
    
    private MessageFactory _messageFactory;
    private OutputStream _logStream;
}
