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
