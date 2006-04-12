/*
 * $Id: _Initializable.java,v 1.1 2006-04-12 20:33:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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