/*
 * $Id: XSDFloatEncoder.java,v 1.1 2006-04-12 20:34:27 kohlert Exp $
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
public class XSDFloatEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDFloatEncoder();

    private XSDFloatEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        Float f = (Float) obj;
        float fVal = f.floatValue();
        if (f.isInfinite()) {
            if (fVal == Float.NEGATIVE_INFINITY) {
                return "-INF";
            } else {
                return "INF";
            }
        } else if (f.isNaN()) {
            return "NaN";
        }
        return f.toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        str = EncoderUtils.collapseWhitespace(str);
        if (str.equals("-INF")) {
            return new Float(Float.NEGATIVE_INFINITY);
        } else if (str.equals("INF")) {
            return new Float(Float.POSITIVE_INFINITY);
        } else if (str.equals("NaN")) {
            return new Float(Float.NaN);
        }
        return new Float(str);
    }

    public void writeValue(Object obj, XMLWriter writer) throws Exception {
        writer.writeCharsUnquoted(objectToString(obj, writer));
    }
}
