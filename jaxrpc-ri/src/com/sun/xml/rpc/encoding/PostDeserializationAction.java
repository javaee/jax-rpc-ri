/*
 * $Id: PostDeserializationAction.java,v 1.1 2006-04-12 20:33:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

/**
 *
 * The action object. Implemented by an inner class from IDREF serializer and 
 * added to deserilization context in a list and latter can be executed, for 
 * example to resolve the xsd:ID.
 *
 * @author JAX-RPC Development Team
 *
 */

public interface PostDeserializationAction {
    public void run(SOAPDeserializationContext deserContext);
}
