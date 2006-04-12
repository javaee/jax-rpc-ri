/*
 * $Id: XSDIntEncoder.java,v 1.1 2006-04-12 20:34:29 kohlert Exp $
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
public class XSDIntEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDIntEncoder();

    private XSDIntEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer) {
        if (obj == null) {
            return null;
        }
        return ((Integer) obj).toString();
    }

    public Object stringToObject(String str, XMLReader reader) {
        if (str == null) {
            return null;
        }
        return new Integer(EncoderUtils.collapseWhitespace(str));
    }

    public void writeValue(Object obj, XMLWriter writer) throws Exception {
        writer.writeCharsUnquoted(objectToString(obj, writer));
    }
}
