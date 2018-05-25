/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
