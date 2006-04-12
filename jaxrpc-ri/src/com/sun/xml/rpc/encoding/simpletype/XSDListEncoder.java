/*
 * $Id: XSDListEncoder.java,v 1.1 2006-04-12 20:34:30 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Encoder for xsd:time. For this type returns java.util.Calendar object.
 *
 * @author JAX-RPC Development Team
 */

public class XSDListEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDListEncoder();

    protected XSDListEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (null == obj)
            return null;

        if (!(obj instanceof java.util.List))
            throw new IllegalArgumentException();

        if (((List) obj).isEmpty())
            return new String();

        ListIterator li = ((List) obj).listIterator();
        StringBuffer result = new StringBuffer();
        while (li.hasNext()) {
            result.append(li.next());
            result.append(' ');
        }
        return result.toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null)
            return null;
        ArrayList list = new ArrayList();
        StringTokenizer in = new StringTokenizer(str.trim(), " ");
        while (in.hasMoreTokens()) {
            list.add(in.nextToken());
        }
        return list;
    }
}
