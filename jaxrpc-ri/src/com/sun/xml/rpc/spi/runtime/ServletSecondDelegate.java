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
 * This is the delegate of the ServletDelegate, which allows some
 * implementation of the ServletDelegate to be overwritten.  Though
 * it screams for a better name.  ServletDelegateDelegate??
 * <p>
 * S1AS will extend this class provide its implementation of
 * the ServletDelegate behavior.
 */
public abstract class ServletSecondDelegate {

    public ServletSecondDelegate() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {
        //no op
    }

    /**
     * This method should be called after ServletDelegate.init()
     * is done.  Any initialization needed by the second delegate
     * should be done by overriding this method, i.e. the implementation
     * of ServletDelegate should call _secondDelegate.postInit()
     * at the end of its init() call.
     * @see ServletDelegate
     */
    public void postInit(ServletConfig config) throws ServletException {
        //no op
    }

    public void warnMissingContextInformation() {
        // context info not used within j2ee integration, so override
        // this method to prevent warning message
    }

    public ImplementorCache createImplementorCache(ServletConfig config) {
        return null;
    }
}
