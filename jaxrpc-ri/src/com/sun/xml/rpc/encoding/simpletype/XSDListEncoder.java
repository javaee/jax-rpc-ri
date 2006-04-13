/*
 * $Id: XSDListEncoder.java,v 1.2 2006-04-13 01:27:56 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
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
