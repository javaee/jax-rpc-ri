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
package com.sun.xml.rpc.spi.runtime;

import com.sun.xml.rpc.spi.runtime.SOAPMessageContext;

/**
 * The methods of this interface are invoked by the JAXRPCServletDelegate of
 * on the path to web sevice endpoints deployed as servlets.
 *
 * NOTE: The methods of this interface may also be called on the client side of
 * jaxrpc invocations, although at this time, we have not decided from
 * where such invocations will be made.
 *
 * @author Ron Monzillo
 */

public interface SystemHandlerDelegate {

   /**
    * The processRequest method is invoked with an object that 
    * implements com.sun.xml.rpc.spi.runtime.SOAPMessageContext.
    * <p>
    * When this method is called by the JAXRPCServletDelegate
    * (on the server side of jaxrpc servlet container invocation processing)
    * it must be called just before the call to implementor.getTie().handle(),
    * and at the time of the request message and the following properties 
    * must have been set on the SOAPMessageContext.
    * <p>
    * com.sun.xml.rpc.server.http.MessageContextProperties.IMPLEMENTOR
    * <br>
    * This property must be set to the com.sun.xml.rpc.spi.runtime.Implementor 
    * object corresponding to the target endpoint.
    *
    * NOTE: I'd like us to be able to hang the ServletAuthContext off the Implementor.
    *
    * <p>
    * com.sun.xml.rpc.server.http.MessageContextProperties.HTTP_SERVLET_REQUEST
    * <br>
    * This property must be
    * set to the javax.servlet.http.HttpServletRequest object containing the 
    * JAXRPC invocation.
    * <p>
    * com.sun.xml.rpc.server.http.MessageContextProperties.HTTP_SERVLET_RESPONSE
    * <br>
    * This property must be
    * set to the javax.servlet.http.HttpServletResponse object corresponding to
    * the JAXRPC invocation.
    * <p>
    * com.sun.xml.rpc.server.MessageContextProperties.HTTP_SERVLET_CONTEXT
    * <br>
    * This property must be
    * set to the javax.servlet.ServletContext object corresponding to web application
    * in which the JAXRPC servlet is running.
    * @param messageContext the SOAPMessageContext object containing the request
    * message and the properties described above.
    * @return true if processing by the delegate was such that the caller
    * should continue with its normal message processing. Returns false if the
    * processing by the delegate resulted in the messageContext containing a response
    * message that should be returned without the caller proceding to its normal
    * message processing. 
    * @throws java.lang.RuntimeException when the processing by the delegate failed,
    * without yielding a response message. In this case, the expectation is that
    * the caller will return a HTTP layer response code reporting that an internal
    * error occured.
    */
    public boolean processRequest(SOAPMessageContext messageContext);

   /**
    * The processResponse method is invoked with an object that 
    * implements com.sun.xml.rpc.spi.runtime.SOAPMessageContext.
    * <p>
    * When this method is called by the JAXRPCServletDelegate
    * (on the server side of jaxrpc servlet container invocation processing)
    * it must be called just just after the call to implementor.getTie().handle().
    * In the special case where the handle method throws an exception, the
    * processResponse message must not be called.
    * <p>
    * The SOAPMessageContext passed to the processRequest and handle messages is
    * passed to the processResponse method.
    * @throws java.lang.RuntimeException when the processing by the delegate failed,
    * in which case the caller is expected to return an HTTP layer 
    * response code reporting that an internal error occured.
    */
    public void processResponse(SOAPMessageContext messageContext);
}
