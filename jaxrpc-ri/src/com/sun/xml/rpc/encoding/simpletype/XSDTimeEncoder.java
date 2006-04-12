/*
 * $Id: XSDTimeEncoder.java,v 1.1 2006-04-12 20:34:26 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.msv.datatype.xsd.TimeType;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 * Encoder for xsd:time. For this type returns java.util.Calendar object.
 *
 * @author JAX-RPC Development Team
 */

public class XSDTimeEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDTimeEncoder();

    protected XSDTimeEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (null == obj)
            return null;
        return TimeType.theInstance.serializeJavaObject(obj, null);
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null)
            return null;

        return TimeType.theInstance._createJavaObject(str, null);
    }

}
