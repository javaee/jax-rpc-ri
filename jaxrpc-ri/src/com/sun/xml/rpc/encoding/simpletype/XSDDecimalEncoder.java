/*
 * $Id: XSDDecimalEncoder.java,v 1.1 2006-04-12 20:34:27 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import java.math.BigDecimal;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDDecimalEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDDecimalEncoder();

    private XSDDecimalEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        return ((BigDecimal) obj).toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        return new BigDecimal(EncoderUtils.collapseWhitespace(str));
    }
}
