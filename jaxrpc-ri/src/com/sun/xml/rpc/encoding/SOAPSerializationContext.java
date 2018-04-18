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
 * $Id: SOAPSerializationContext.java,v 1.3 2007-07-13 23:35:57 ofung Exp $
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

package com.sun.xml.rpc.encoding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.soap.SOAPMessage;

import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPEncodingConstants;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPSerializationContext {

    protected HashMap map;
    protected Map properties;
    protected LinkedList list;
    protected String prefix;
    protected long next;
    protected Stack encodingStyleContext = new Stack();
    protected String curEncodingStyle = null;
    protected Set activeObjects;
    protected SOAPMessage message;

    private SOAPVersion soapVer = SOAPVersion.SOAP_11;

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
        this.soapVer = ver;
    }

    public SOAPSerializationContext() {
        this(null);
    }

    public SOAPSerializationContext(String prefix) {
        this(prefix, SOAPVersion.SOAP_11); // default is SOAP 1.1   
    }

    public SOAPSerializationContext(String prefix, SOAPVersion ver) {
        init(ver);
        if (prefix == null) {
            prefix = "ID";
        }

        map = new HashMap();
        properties = new HashMap();
        list = new LinkedList();
        this.prefix = prefix;
        next = 1;
    }

    public SOAPSerializationState registerObject(
        Object obj,
        ReferenceableSerializer serializer) {

        MapKey key = new MapKey(obj);
        SOAPSerializationState state = (SOAPSerializationState) map.get(key);

        if (state == null) {
            state = new SOAPSerializationState(obj, nextID(), serializer);
            map.put(key, state);
            list.add(state);
        }

        return state;
    }

    public boolean isRegistered(Object obj) {
        return (map.get(new MapKey(obj)) != null) ? true : false;
    }

    public SOAPSerializationState lookupObject(Object obj) {

        MapKey key = new MapKey(obj);
        return (SOAPSerializationState) map.get(key);
    }

    public void serializeMultiRefObjects(XMLWriter writer) {

        while (!list.isEmpty()) {
            SOAPSerializationState state =
                (SOAPSerializationState) list.removeFirst();
            Object obj = state.getObject();
            ReferenceableSerializer ser = state.getSerializer();
            ser.serializeInstance(obj, null, true, writer, this);
        }
    }

    public String nextID() {
        return prefix + next++;
    }

    private static class MapKey {
        Object obj;

        public MapKey(Object obj) {
            this.obj = obj;
        }

        public boolean equals(Object o) {
            if (!(o instanceof MapKey)) {
                return false;
            }

            return (obj == ((MapKey) o).obj);
        }

        public int hashCode() {
            return System.identityHashCode(obj);
        }
    }

    public boolean setImplicitEncodingStyle(String newEncodingStyle) {
        if (newEncodingStyle == curEncodingStyle
            || newEncodingStyle.equals(curEncodingStyle)) {
            return false;
        }
        encodingStyleContext.push(newEncodingStyle);
        initEncodingStyleInfo();
        return true;
    }

    public boolean pushEncodingStyle(String newEncodingStyle, XMLWriter writer)
        throws Exception {
            
        if (newEncodingStyle == curEncodingStyle
            || newEncodingStyle.equals(curEncodingStyle)) {
            return false;
        }
        writer.writeAttribute(
            soapEncodingConstants.getQNameEnvelopeEncodingStyle(),
            newEncodingStyle);
        encodingStyleContext.push(newEncodingStyle);
        initEncodingStyleInfo();
        return true;
    }

    public void popEncodingStyle() {
        encodingStyleContext.pop();
        initEncodingStyleInfo();
    }

    private void initEncodingStyleInfo() {
        if (encodingStyleContext.empty()) {
            curEncodingStyle = null;
        } else {
            curEncodingStyle = (String) encodingStyleContext.peek();
        }
    }

    public String getEncodingStyle() {
        return curEncodingStyle;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void removeProperty(String key) {
        properties.remove(key);
    }

    public void beginFragment() {
        activeObjects = new HashSet();
    }

    public void beginSerializing(Object obj) throws SerializationException {
        if (obj != null && activeObjects != null) {
            if (activeObjects.contains(obj)) {
                throw new SerializationException(
                    "soap.circularReferenceDetected",
                    new Object[] { obj });
            } else {
                activeObjects.add(obj);
            }
        }
    }

    public void doneSerializing(Object obj) throws SerializationException {
        if (obj != null && activeObjects != null) {
            activeObjects.remove(obj);
        }
    }

    public void endFragment() {
        activeObjects = null;
    }

    public void setMessage(SOAPMessage m) {
        message = m;
    }

    public SOAPMessage getMessage() {
        return message;
    }

    public SOAPVersion getSOAPVersion() {
        return this.soapVer;
    }

    private SOAPEncodingConstants soapEncodingConstants = null;
}
