/*
 * $Id: XSDListTypeEncoder.java,v 1.2 2006-04-13 01:27:57 ofung Exp $
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

import java.lang.reflect.Array;
import java.util.StringTokenizer;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 * Serializes and Deserializes arrays.
 *
 * @author JAX-RPC Development Team
 */
public class XSDListTypeEncoder extends SimpleTypeEncoderBase {
    private SimpleTypeEncoder encoder = null;
    private Class typeClass = null;

    protected XSDListTypeEncoder(SimpleTypeEncoder encoder, Class typeClass) {
        this.encoder = encoder;
        this.typeClass = typeClass;
    }

    //bug fix: 4906014 - this class is re-written, gets the type's class and forms 
    //array or gets string from array. Also sim[plified how encoding/decoding is done.
    public static SimpleTypeEncoder getInstance(
        SimpleTypeEncoder itemEnc,
        Class typeClass) {
            
        return new XSDListTypeEncoder(itemEnc, typeClass);
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (null == obj)
            return null;

        if (!obj.getClass().isArray())
            throw new IllegalArgumentException();

        StringBuffer ret = new StringBuffer();

        int len = Array.getLength(obj);
        if (len == 0)
            return "";

        for (int i = 0; i < len; i++) {
            ret.append(Array.get(obj, i));
            if (i + 1 < len)
                ret.append(' ');
        }
        return ret.toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null)
            return null;
        StringTokenizer in = new StringTokenizer(str.trim(), " ");
        Object objArray = Array.newInstance(typeClass, in.countTokens());
        if (in.countTokens() == 0)
            return objArray;
        int i = 0;
        while (in.hasMoreTokens()) {
            Array.set(
                objArray,
                i++,
                encoder.stringToObject(in.nextToken(), reader));
        }
        return objArray;
    }

    public void writeValue(Object obj, XMLWriter writer) throws Exception {
        writer.writeCharsUnquoted(objectToString(obj, writer));
    }
}
