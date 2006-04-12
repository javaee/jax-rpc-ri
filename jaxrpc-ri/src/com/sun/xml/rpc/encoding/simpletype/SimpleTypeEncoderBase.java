/*
 * $Id: SimpleTypeEncoderBase.java,v 1.1 2006-04-12 20:34:29 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class SimpleTypeEncoderBase implements SimpleTypeEncoder {

    public abstract String objectToString(Object obj, XMLWriter writer)
        throws Exception;
        
    public abstract Object stringToObject(String str, XMLReader reader)
        throws Exception;

    public void writeValue(Object obj, XMLWriter writer) throws Exception {
        // NOTE - this method is only called when the value (obj, that is) is non-null
        writer.writeChars(objectToString(obj, writer));
    }

    public void writeAdditionalNamespaceDeclarations(
        Object obj,
        XMLWriter writer)
        throws Exception {
        // no-op
    }
}
