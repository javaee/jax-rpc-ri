/*
 * $Id: Tie.java,v 1.2 2006-04-13 01:32:03 ofung Exp $
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

package com.sun.xml.rpc.server;

import java.rmi.Remote;

/** Tie interface supports delegation mechanism for the implementation
 *  of RPC-based service. In the delegation approach, an implementation 
 *  class implements the methods defined in the Remote interface. Tie 
 *  instance delegates the incoming RPC call to the target 
 *  implementation object.
 *
 * @author JAX-RPC Development Team
 */
public interface Tie extends com.sun.xml.rpc.spi.runtime.Tie {

    /** Signals the Tie that it's about to be disposed of, giving
     *  it a chance to release any resources it might hold.
    **/
    public void destroy();

    /** Sets the target service implementation object (that 
     *  implements java.rmi.Remote interface) for this Tie 
     *  instance.
    **/

    /** Gets the target service implementation object (that 
     *  implements java.rmi.Remote interface) for this Tie 
     *  instance.
    **/
    public Remote getTarget();
}
