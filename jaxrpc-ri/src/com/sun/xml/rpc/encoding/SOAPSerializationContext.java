/*
 * $Id: SOAPSerializationContext.java,v 1.1 2006-04-12 20:33:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
