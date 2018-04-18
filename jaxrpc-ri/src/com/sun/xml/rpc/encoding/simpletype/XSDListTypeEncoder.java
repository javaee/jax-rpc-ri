/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: XSDListTypeEncoder.java,v 1.3 2007-07-13 23:35:59 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
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
