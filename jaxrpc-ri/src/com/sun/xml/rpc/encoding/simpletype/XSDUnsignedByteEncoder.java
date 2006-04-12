/*
 * $Id: XSDUnsignedByteEncoder.java,v 1.1 2006-04-12 20:34:31 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.msv.datatype.xsd.UnsignedByteType;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDUnsignedByteEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder =
        new XSDUnsignedByteEncoder();

    private XSDUnsignedByteEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        return ((Short) obj).toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        Object obj = UnsignedByteType.theInstance._createJavaObject(str, null);
        if (obj != null)
            return obj;
        throw new com.sun.xml.rpc.encoding.DeserializationException(
            "xsd.invalid.unsignedShort",
            str);
    }

    public void writeValue(Object obj, XMLWriter writer) throws Exception {
        writer.writeCharsUnquoted(objectToString(obj, writer));
    }
}
