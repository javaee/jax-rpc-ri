/*
 * $Id: SerializerCallback.java,v 1.1 2006-04-12 20:33:11 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface SerializerCallback {

    public void onStartTag(
        Object obj,
        QName name,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws SerializationException;
}