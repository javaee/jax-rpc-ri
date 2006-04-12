/*
 * $Id: Tie.java,v 1.1 2006-04-12 20:35:24 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
