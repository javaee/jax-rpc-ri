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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A delegate for the JAX-RPC dispatcher servlet.
 * <p>
 * This interface is implemented by 
 * com.sun.xml.rpc.server.http.ServletDelegate
 *
 */
public interface ServletDelegate {
    public void init(ServletConfig servletConfig) throws ServletException;
    public void destroy();
    public void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException;
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException;
    public void registerEndpointUrlPattern(RuntimeEndpointInfo info);
    public void setSecondDelegate(ServletSecondDelegate delegate);
    public void setSystemHandlerDelegate(SystemHandlerDelegate systemHandlerDelegate);
}
