/*
 * $Id: XSDDoubleEncoder.java,v 1.1 2006-04-12 20:34:26 kohlert Exp $
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
public class XSDDoubleEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDDoubleEncoder();

    private XSDDoubleEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        Double d = (Double) obj;
        double dVal = d.doubleValue();
        if (d.isInfinite()) {
            if (dVal == Double.NEGATIVE_INFINITY) {
                return "-INF";
            } else {
                return "INF";
            }
        } else if (d.isNaN()) {
            return "NaN";
        }
        return d.toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        str = EncoderUtils.collapseWhitespace(str);
        if (str.equals("-INF")) {
            return new Double(Double.NEGATIVE_INFINITY);
        } else if (str.equals("INF")) {
            return new Double(Double.POSITIVE_INFINITY);
        } else if (str.equals("NaN")) {
            return new Double(Double.NaN);
        }
        return new Double(str);
    }

    public void writeValue(Object obj, XMLWriter writer) throws Exception {
        writer.writeCharsUnquoted(objectToString(obj, writer));
    }
}
