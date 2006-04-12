/*
 * $Id: XSDBooleanEncoder.java,v 1.1 2006-04-12 20:34:26 kohlert Exp $
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
public class XSDBooleanEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDBooleanEncoder();

    private XSDBooleanEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        return ((Boolean) obj).toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        String tmp = EncoderUtils.collapseWhitespace(str);

        // "true" and "false" are first because they occur more often!

        if (tmp.equals("true")) {
            return Boolean.TRUE;
        }
        if (tmp.equals("false")) {
            return Boolean.FALSE;
        }

        if (tmp.equals("1")) {
            return Boolean.TRUE;
        }
        if (tmp.equals("0")) {
            return Boolean.FALSE;
        }

        throw new com.sun.xml.rpc.encoding.DeserializationException(
            "xsd.invalid.boolean",
            tmp);
    }

    public void writeValue(Object obj, XMLWriter writer) throws Exception {
        writer.writeCharsUnquoted(objectToString(obj, writer));
    }
}
