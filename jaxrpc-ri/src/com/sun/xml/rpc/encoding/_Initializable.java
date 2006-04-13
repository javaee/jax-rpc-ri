/*
 * $Id: _Initializable.java,v 1.2 2006-04-13 01:27:32 ofung Exp $
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

package com.sun.xml.rpc.encoding;

/**
 * Stubs/Serializers implement this interface so that they can initialize
 * the serializers they will need
 * 
 * @author JAX-RPC Development Team
 */
public interface _Initializable {
    /** Allows the implementors to retrieve and cache serializers during
     * system intialization
     *
     *  @throws java.lang.Exception This exception may be
     *          thrown if there is a problem initializing
    **/
    public void _initialize(InternalTypeMappingRegistry registry)
        throws Exception;
}