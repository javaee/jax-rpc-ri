/*
 * $Id: XSDHexBinaryEncoder.java,v 1.2 2006-04-13 01:27:54 ofung Exp $
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

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDHexBinaryEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder = new XSDHexBinaryEncoder();

    private XSDHexBinaryEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        byte[] value = (byte[]) obj;
        if (value.length == 0) {
            return "";
        }

        StringBuffer encodedValue = new StringBuffer(value.length * 2);
        for (int i = 0; i < value.length; ++i) {
            encodedValue.append(encodeHex[(value[i] >> 4) & 0xf]);
            encodedValue.append(encodeHex[value[i] & 0xf]);
        }

        return encodedValue.toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        String encodedValue = EncoderUtils.collapseWhitespace(str);
        int valueLength = encodedValue.length() / 2;
        byte[] value = new byte[valueLength];

        int encodedIdx = 0;
        for (int i = 0; i < valueLength; ++i) {
            int nibble1 = decodeHex[encodedValue.charAt(encodedIdx++) - '0'];
            int nibble2 = decodeHex[encodedValue.charAt(encodedIdx++) - '0'];
            value[i] = (byte) ((nibble1 << 4) | nibble2);
        }

        return value;
    }

    private static final char encodeHex[] =
        {
            '0','1','2','3','4','5','6','7',
            '8','9','a','b','c','d','e','f' };

    private static final int decodeHex[] = {
        /*'0'*/ 0,
        /*'1'*/ 1,
        /*'2'*/ 2,
        /*'3'*/ 3,
        /*'4'*/ 4,
        /*'5'*/ 5,
        /*'6'*/ 6,
        /*'7'*/ 7,
        /*'8'*/ 8,
        /*'9'*/ 9, -1, -1, -1, -1, -1, -1, -1,
        /*'A'*/ 10,
        /*'B'*/ 11,
        /*'C'*/ 12,
        /*'D'*/ 13,
        /*'E'*/ 14,
        /*'F'*/ 15,
        /*'G'-'Z'*/-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        /*'[' - '`'*/ -1, -1, -1, -1, -1, -1,
        /*'a'*/ 10,
        /*'b'*/ 11,
        /*'c'*/ 12,
        /*'d'*/ 13,
        /*'e'*/ 14,
        /*'f'*/ 15 };
}
