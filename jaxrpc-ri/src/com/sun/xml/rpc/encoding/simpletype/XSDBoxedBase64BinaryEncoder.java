/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.encoding.simpletype;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDBoxedBase64BinaryEncoder extends XSDBase64EncoderBase {
    private static final SimpleTypeEncoder encoder =
        new XSDBoxedBase64BinaryEncoder();

    private XSDBoxedBase64BinaryEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        Byte[] value = (Byte[]) obj;
        if (value.length == 0) {
            return "";
        }
        int blockCount = value.length / 3;
        int partialBlockLength = value.length % 3;

        if (partialBlockLength != 0) {
            ++blockCount;
        }

        int encodedLength = blockCount * 4;
        StringBuffer encodedValue = new StringBuffer(encodedLength);

        int idx = 0;
        for (int i = 0; i < blockCount; ++i) {
            int b1 = value[idx++].byteValue();
            int b2 = (idx < value.length) ? value[idx++].byteValue() : 0;
            int b3 = (idx < value.length) ? value[idx++].byteValue() : 0;

            if (b1 < 0) {
                b1 += 256;
            }
            if (b2 < 0) {
                b2 += 256;
            }
            if (b3 < 0) {
                b3 += 256;
            }

            char encodedChar;

            encodedChar = encodeBase64[b1 >> 2];
            encodedValue.append(encodedChar);

            encodedChar = encodeBase64[((b1 & 0x03) << 4) | (b2 >> 4)];
            encodedValue.append(encodedChar);

            encodedChar = encodeBase64[((b2 & 0x0f) << 2) | (b3 >> 6)];
            encodedValue.append(encodedChar);

            encodedChar = encodeBase64[b3 & 0x3f];
            encodedValue.append(encodedChar);
        }

        switch (partialBlockLength) {
            case 0 :
                // do nothing
                break;
            case 1 :
                encodedValue.setCharAt(encodedLength - 1, '=');
                encodedValue.setCharAt(encodedLength - 2, '=');
                break;
            case 2 :
                encodedValue.setCharAt(encodedLength - 1, '=');
                break;
        }

        return encodedValue.toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        String uri = "";
        String encodedValue = EncoderUtils.removeWhitespace(str);
        int encodedLength = encodedValue.length();
        if (encodedLength == 0) {
            return new Byte[0];
        }
        int blockCount = encodedLength / 4;
        int partialBlockLength = 3;

        if (encodedValue.charAt(encodedLength - 1) == '=') {
            --partialBlockLength;
            if (encodedValue.charAt(encodedLength - 2) == '=') {
                --partialBlockLength;
            }
        }

        int valueLength = (blockCount - 1) * 3 + partialBlockLength;
        Byte[] value = new Byte[valueLength];

        int idx = 0;
        int encodedIdx = 0;
        for (int i = 0; i < blockCount; ++i) {
            int x1 = decodeBase64[encodedValue.charAt(encodedIdx++) - '+'];
            int x2 = decodeBase64[encodedValue.charAt(encodedIdx++) - '+'];
            int x3 = decodeBase64[encodedValue.charAt(encodedIdx++) - '+'];
            int x4 = decodeBase64[encodedValue.charAt(encodedIdx++) - '+'];

            value[idx++] = new Byte((byte) ((x1 << 2) | (x2 >> 4)));
            if (idx < valueLength) {
                value[idx++] =
                    new Byte((byte) (((x2 & 0x0f) << 4) | (x3 >> 2)));
            }
            if (idx < valueLength) {
                value[idx++] = new Byte((byte) (((x3 & 0x03) << 6) | x4));
            }
        }

        return value;
    }
}
