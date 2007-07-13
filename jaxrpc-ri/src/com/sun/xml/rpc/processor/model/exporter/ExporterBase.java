/*
 * $Id: ExporterBase.java,v 1.3 2007-07-13 23:36:05 ofung Exp $
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

package com.sun.xml.rpc.processor.model.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import java.util.zip.GZIPOutputStream;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.simpletype.XSDDateTimeCalendarEncoder;
import com.sun.xml.rpc.processor.util.PrettyPrintingXMLWriterFactoryImpl;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.PrefixFactoryImpl;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterException;
import com.sun.xml.rpc.util.IdentityMap;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.document.soap.SOAPUse;

/**
 * @author JAX-RPC Development Team
 */
public abstract class ExporterBase {
    
    public ExporterBase(OutputStream s) {
        out = s;
        try {
            writer = new PrettyPrintingXMLWriterFactoryImpl().createXMLWriter(
                new GZIPOutputStream(s));
        } catch (IOException e) {
            throw new XMLWriterException("xmlwriter.ioException",
                new LocalizableExceptionAdapter(e));
        }
        writer.setPrefixFactory(new PrefixFactoryImpl("ns"));
    }
    
    protected void initialize() {
        obj2id = new IdentityMap();
        immutableObj2id = new HashMap();
        obj2serialize = new HashSet();
        obj2serializeStack = new Stack();
        nextId = 1;
        
        /*
         * "immutable" classes are classes whose instances are immutable.
         * immutability lets us replace values which are equal (through
         * the equals() method) with one another, so that we don't have to
         * have 50 copies of the integer 10 or of some uninterned string
         * that occurs over and over in the model.
         */
        immutableClasses = new HashSet();
        immutableClasses.add(Boolean.class);
        immutableClasses.add(Integer.class);
        immutableClasses.add(Short.class);
        immutableClasses.add(Long.class);
        immutableClasses.add(Float.class);
        immutableClasses.add(Double.class);
        immutableClasses.add(Byte.class);
        immutableClasses.add(String.class);
        immutableClasses.add(BigDecimal.class);
        immutableClasses.add(BigInteger.class);
        immutableClasses.add(javax.xml.namespace.QName.class);
        immutableClasses.add(
            com.sun.xml.rpc.wsdl.document.soap.SOAPStyle.class);
        immutableClasses.add(com.sun.xml.rpc.wsdl.document.soap.SOAPUse.class);
        immutableClasses.add(com.sun.xml.rpc.soap.SOAPVersion.class);
        
        // bug fix: 4923072
        immutableClasses.add(java.net.URI.class);
        
        /*
         * "immediate" classes are classes which are well-known and considered
         * primitive for model serialization purposes, but are not necessarily
         * immutable. instances of these classes are compared using equality
         * (==) during serialization. this avoids having different empty maps in
         * the model be serialized to the same empty map, which breaks things in
         * a spectacular way if a processor action tries to modify the model
         * after it's been deserialized.
         */
        immediateClasses = new HashSet();
        immediateClasses.addAll(immutableClasses);
        try {
            immediateClasses.add(Class.forName("[I"));
            immediateClasses.add(Class.forName("[B"));
            immediateClasses.add(Class.forName("[Ljava.lang.String;"));
        } catch (ClassNotFoundException e) {
            // cannot happen
        }
        immediateClasses.add(java.util.ArrayList.class);
        immediateClasses.add(java.util.HashSet.class);
        immediateClasses.add(java.util.HashMap.class);
        immediateClasses.add(java.util.GregorianCalendar.class);
    }
    
    protected void internalDoExport(Object root) {
        initialize();
        writer.startElement(getContainerName());
        
		// bugfix# 4948171
        if (getVersion() != null) {
			int[] version = VersionUtil.getCanonicalVersion(getVersion());
			writer.writeAttribute(
				ATTR_VERSION,
				version[0]
					+ "."
					+ version[1]
					+ "."
					+ version[2]
					+ "."
					+ version[3]);
        }
        int id = getId(root);
        while (!obj2serializeStack.empty()) {
            Object obj = obj2serializeStack.pop();
            obj2serialize.remove(obj);
            visit(obj);
        }
        writer.endElement();
        writer.close();
    }
    
    protected void visit(Object obj) {
        if (obj == null) {
            return;
        }
        failUnsupportedClass(obj.getClass());
    }
    
    protected boolean isImmediate(Object obj) {
        if (obj == null) {
            return true;
        }
        return immediateClasses.contains(obj.getClass());
    }
    
    protected boolean isImmutable(Object obj) {
        if (obj == null) {
            return true;
        }
        return immutableClasses.contains(obj.getClass());
    }
    
    protected int getId(Object obj) {
        if (obj == null) {
            return 0;
        }
        Integer id = (Integer) obj2id.get(obj);
        if (id != null) {
            return id.intValue();
        }
        boolean immutable = isImmutable(obj);
        if (immutable) {
            id = (Integer) immutableObj2id.get(obj);
            if (id != null) {
                return id.intValue();
            }
        }
        id = newId();
        obj2id.put(obj, id);
        if (immutable) {
            immutableObj2id.put(obj, id);
        }
        if (isImmediate(obj)) {
            defineImmediate(obj, id);
        } else {
            define(obj, id);
        }
        return id.intValue();
    }
    
    protected void defineImmediate(Object obj, Integer id) {
        String value = getImmediateObjectValue(obj);
        writer.startElement(getDefineImmediateObjectName());
        writer.writeAttribute(ATTR_ID, id.toString());
        writer.writeAttribute(ATTR_TYPE, obj.getClass().getName());
        writer.writeAttribute(ATTR_VALUE, value);
        writer.endElement();
    }
    
    protected String getImmediateObjectValue(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof QName) {
            return obj.toString();
        } else if ((obj instanceof Boolean) ||
            (obj instanceof Short) ||
            (obj instanceof Integer) ||
            (obj instanceof Long) ||
            (obj instanceof Float) ||
            (obj instanceof Double) ||
            (obj instanceof Byte) ||
            (obj instanceof BigDecimal) ||
            (obj instanceof BigInteger) ||
            (obj instanceof java.net.URI)) { //bug fix: 4923072
                
            return obj.toString();
        } else if (obj instanceof SOAPStyle) {
            return (obj == SOAPStyle.RPC ? "rpc" : "document");
        } else if (obj instanceof SOAPUse) {
            return (obj == SOAPUse.ENCODED ? "encoded" : "literal");
        } else if (obj instanceof SOAPVersion) {
            return obj.toString();
        } else if (obj instanceof int[]) {
            int[] a = (int[]) obj;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < a.length; ++i) {
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(Integer.toString(a[i]));
            }
            return sb.toString();
        } else if (obj instanceof byte[]) {
            byte[] a = (byte[]) obj;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < a.length; ++i) {
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(Byte.toString(a[i]));
            }
            return sb.toString();
        } else if (obj instanceof String[]) {
            String[] a = (String[]) obj;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < a.length; ++i) {
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(a[i]);
            }
            return sb.toString();
        } else if (obj instanceof ArrayList) {
            ArrayList a = (ArrayList) obj;
            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (Iterator iter = a.iterator(); iter.hasNext();) {
                Object element = iter.next();
                if (!first) {
                    sb.append(' ');
                }
                sb.append(Integer.toString(getId(element)));
                first = false;
            }
            return sb.toString();
        } else if (obj instanceof HashSet) {
            HashSet s = (HashSet) obj;
            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (Iterator iter = s.iterator(); iter.hasNext();) {
                Object element = iter.next();
                if (!first) {
                    sb.append(' ');
                }
                sb.append(Integer.toString(getId(element)));
                first = false;
            }
            return sb.toString();
        } else if (obj instanceof HashMap) {
            HashMap m = (HashMap) obj;
            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (Iterator iter = m.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (!first) {
                    sb.append(' ');
                }
                sb.append(Integer.toString(getId(entry.getKey())));
                sb.append(' ');
                sb.append(Integer.toString(getId(entry.getValue())));
                first = false;
            }
            return sb.toString();
        } else if(obj instanceof GregorianCalendar) {
            try {
                return XSDDateTimeCalendarEncoder.getInstance().objectToString(
                    obj, null);
            } catch (Exception e) {
                failUnsupportedClass(obj.getClass());
            }
            return "UNKOWN";
        } else {
            failUnsupportedClass(obj.getClass());
            
            // keep javac happy, previous method must throw an exception
            return "UNKOWN"; 
        }
    }
    
    protected void define(Object obj, Integer id) {
        writer.startElement(getDefineObjectName());
        writer.writeAttribute(ATTR_ID, id.toString());
        writer.writeAttribute(ATTR_TYPE, obj.getClass().getName());
        writer.endElement();
        obj2serialize.add(obj);
        obj2serializeStack.push(obj);
    }
    
    protected void property(String name, Object subject, Object object) {
        int sid = getId(subject);
        int oid = getId(object);
        writer.startElement(getPropertyName());
        writer.writeAttribute(ATTR_NAME, name);
        writer.writeAttribute(ATTR_SUBJECT, Integer.toString(sid));
        writer.writeAttribute(ATTR_VALUE, Integer.toString(oid));
        writer.endElement();
    }
    
    protected Integer newId() {
        return new Integer(nextId++);
    }
    
    protected abstract QName getContainerName();
    
    protected String getVersion() {
        return null;
    }
    
    protected QName getDefineObjectName() {
        return DEF_OBJ_NAME;
    }
    
    protected QName getDefineImmediateObjectName() {
        return DEF_IMM_OBJ_NAME;
    }
    
    protected QName getPropertyName() {
        return PROP_NAME;
    }
    
    //the following method must throw an exception
    protected abstract void failUnsupportedClass(Class klass);
    
    protected OutputStream out;
    protected XMLWriter writer;
    protected Map obj2id;
    protected Map immutableObj2id;
    protected int nextId;
    protected Set obj2serialize;
    protected Stack obj2serializeStack;
    protected Set immediateClasses;
    protected Set immutableClasses;
    
    protected static final QName DEF_OBJ_NAME = new QName("object");
    protected static final QName DEF_IMM_OBJ_NAME = new QName("iobject");
    protected static final QName PROP_NAME = new QName("property");
    
    protected static final String ATTR_VERSION = "version";
    protected static final String ATTR_ID = "id";
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_TYPE = "type";
    protected static final String ATTR_VALUE = "value";
    protected static final String ATTR_SUBJECT = "subject";
}
