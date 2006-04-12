/*
 * $Id: JAXRPCDeserializer.java,v 1.1 2006-04-12 20:33:17 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;

import com.sun.xml.rpc.streaming.XMLReader;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface JAXRPCDeserializer extends Deserializer {
    public Object deserialize(
        QName name,
        XMLReader element,
        SOAPDeserializationContext context);
    public Object deserialize(
        DataHandler dataHandler,
        SOAPDeserializationContext context);
}