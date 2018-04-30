/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.simpletype.XSDDateTimeCalendarEncoder;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderException;
import com.sun.xml.rpc.streaming.XMLReaderFactoryImpl;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.util.VersionUtil;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.document.soap.SOAPUse;

/**
 * @author JAX-RPC Development Team
 */
public abstract class ImporterBase {
    
    public ImporterBase(InputStream s) {
        in = s;
        try {
            reader = new XMLReaderFactoryImpl().createXMLReader(
                new GZIPInputStream(s));
        } catch (IOException e) {
            throw new XMLReaderException("xmlreader.ioException",
                new LocalizableExceptionAdapter(e));
        }
    }
    
    protected void initialize() {
        id2obj = new HashMap();
        id2obj.put(new Integer(0), null);
        immediateClassNames = new HashSet();
        immediateClassNames.add("java.lang.Boolean");
        immediateClassNames.add("java.lang.Integer");
        immediateClassNames.add("java.lang.Short");
        immediateClassNames.add("java.lang.Long");
        immediateClassNames.add("java.lang.Float");
        immediateClassNames.add("java.lang.Double");
        immediateClassNames.add("java.lang.Byte");
        immediateClassNames.add("[I");
        immediateClassNames.add("[B");
        immediateClassNames.add("[Ljava.lang.String;");
        immediateClassNames.add("java.util.GregorianCalendar");
        immediateClassNames.add("java.lang.String");
        immediateClassNames.add("java.math.BigDecimal");
        immediateClassNames.add("java.math.BigInteger");
        immediateClassNames.add("java.util.ArrayList");
        immediateClassNames.add("java.util.HashSet");
        immediateClassNames.add("java.util.HashMap");
        immediateClassNames.add("javax.xml.namespace.QName");
        immediateClassNames.add("com.sun.xml.rpc.wsdl.document.soap.SOAPStyle");
        immediateClassNames.add("com.sun.xml.rpc.wsdl.document.soap.SOAPUse");
        immediateClassNames.add("com.sun.xml.rpc.soap.SOAPVersion");
        
        // bug fix: 4923072
        immediateClassNames.add("java.net.URI");
    }
    
    protected Object internalDoImport() {
        initialize();
        reader.nextElementContent();
        if (reader.getState() != XMLReader.START) {
            failInvalidSyntax(reader);
        }
        if (!reader.getName().equals(getContainerName())) {
            failInvalidSyntax(reader);
        }
        checkVersion();
        while (reader.nextElementContent() != XMLReader.END) {
            if (reader.getName().equals(getDefineImmediateObjectName())) {
                parseDefineImmediateObject(reader);
            } else if (reader.getName().equals(getDefineObjectName())) {
                parseDefineObject(reader);
            } else if (reader.getName().equals(getPropertyName())) {
                parseProperty(reader);
            } else {
                failInvalidSyntax(reader);
            }
        }
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        
        // the root object is invariably the first
        return id2obj.get(new Integer(1));
    }
    
    protected void parseDefineImmediateObject(XMLReader reader) {
        String idAttr = getRequiredAttribute(reader, ATTR_ID);
        String typeAttr = getRequiredAttribute(reader, ATTR_TYPE);
        if (!immediateClassNames.contains(typeAttr)) {
        	failInvalidClass(reader, typeAttr);
        }
        String valueAttr = getRequiredAttribute(reader, ATTR_VALUE);
        Integer id = parseId(reader, idAttr);
        if (getObjectForId(id) != null) {
            failInvalidId(reader, id);
        }
        Object obj = createImmediateObject(reader, typeAttr, valueAttr);
        if (obj == null)
        	checkMinorMinorAndPatchVersion(reader);
        id2obj.put(id, obj);
        verifyNoContent(reader);
    }
    
    protected void parseDefineObject(XMLReader reader) {
        String idAttr = getRequiredAttribute(reader, ATTR_ID);
        String typeAttr = getRequiredAttribute(reader, ATTR_TYPE);
        Integer id = parseId(reader, idAttr);
        if (getObjectForId(id) != null) {
            failInvalidId(reader, id);
        }
        Object obj = createInstanceOfType(reader, typeAttr);
		if (obj == null)
			checkMinorMinorAndPatchVersion(reader);
        id2obj.put(id, obj);
        verifyNoContent(reader);
    }
    
    protected void parseProperty(XMLReader reader) {
        String nameAttr = getRequiredAttribute(reader, ATTR_NAME);
        String subjectAttr = getRequiredAttribute(reader, ATTR_SUBJECT);
        String valueAttr = getRequiredAttribute(reader, ATTR_VALUE);
        Object subject =
            mustGetObjectForId(reader, parseId(reader, subjectAttr));
        if (subject == null) {
            failInvalidSyntax(reader);
        }
        Integer valueId = parseId(reader, valueAttr);
        Object value = (isNullId(valueId) ? null : mustGetObjectForId(reader,
            valueId));
        try {
            property(reader, subject, nameAttr, value);
        } catch (ClassCastException e) {
            failInvalidProperty(reader, subject, nameAttr, value);
        }
        verifyNoContent(reader);
    }
    
    protected Object createImmediateObject(XMLReader reader, String type,
        String value) {
            
        if (type.equals("java.lang.Integer")) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.lang.Short")) {
            try {
                return Short.valueOf(value);
            } catch (NumberFormatException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.lang.Long")) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.lang.Byte")) {
            try {
                return Byte.valueOf(value);
            } catch (NumberFormatException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.lang.Float")) {
            try {
                return Float.valueOf(value);
            } catch (NumberFormatException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.lang.Double")) {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.math.BigDecimal")) {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.math.BigInteger")) {
            try {
                return new BigInteger(value);
            } catch (NumberFormatException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.lang.String")) {
            return value;
        } else if (type.equals("javax.xml.namespace.QName")) {
            try {
                return QName.valueOf(value);
            } catch (IllegalArgumentException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("java.lang.Boolean")) {
            return Boolean.valueOf(value);
        } else if (type.equals(
            "com.sun.xml.rpc.wsdl.document.soap.SOAPStyle")) {
                
            if (value.equals("rpc")) {
                return SOAPStyle.RPC;
            } else if (value.equals("document")) {
                return SOAPStyle.DOCUMENT;
            } else {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("com.sun.xml.rpc.wsdl.document.soap.SOAPUse")) {
            if (value.equals("literal")) {
                return SOAPUse.LITERAL;
            } else if (value.equals("encoded")) {
                return SOAPUse.ENCODED;
            } else {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("com.sun.xml.rpc.soap.SOAPVersion")) {
            if (value.equals(SOAPVersion.SOAP_11.toString())) {
                return SOAPVersion.SOAP_11;
            } else if (value.equals(SOAPVersion.SOAP_12.toString())) {
                return SOAPVersion.SOAP_12;
            } else {
                failInvalidLiteral(reader, type, value);
            }
        } else if (type.equals("[I")) {
            List l = XmlUtil.parseTokenList(value);
            int[] result = new int[l.size()];
            int i = 0;
            for (Iterator iter = l.iterator(); iter.hasNext(); ++i) {
                String element = (String) iter.next();
                try {
                    result[i] = Integer.parseInt(element);
                } catch (NumberFormatException e) {
                    failInvalidLiteral(reader, type, value);
                }
            }
            return result;
        } else if (type.equals("[B")) {
            List l = XmlUtil.parseTokenList(value);
            byte[] result = new byte[l.size()];
            int i = 0;
            for (Iterator iter = l.iterator(); iter.hasNext(); ++i) {
                String element = (String) iter.next();
                try {
                    result[i] = Byte.parseByte(element);
                } catch (NumberFormatException e) {
                    failInvalidLiteral(reader, type, value);
                }
            }
            return result;
        } else if (type.equals("[Ljava.lang.String;")) {
            List l = XmlUtil.parseTokenList(value);
            String[] result = new String[l.size()];
            int i = 0;
            for (Iterator iter = l.iterator(); iter.hasNext(); ++i) {
                String element = (String) iter.next();
                try {
                    result[i] = element;
                } catch (NumberFormatException e) {
                    failInvalidLiteral(reader, type, value);
                }
            }
            return result;
        } else if (type.equals("java.util.ArrayList")) {
            List l = XmlUtil.parseTokenList(value);
            ArrayList result = new ArrayList();
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                result.add(mustGetObjectForId(reader,
                    parseId(reader, element)));
            }
            return result;
        } else if (type.equals("java.util.HashSet")) {
            List l = XmlUtil.parseTokenList(value);
            HashSet result = new HashSet();
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                result.add(
                    mustGetObjectForId(reader, parseId(reader, element)));
            }
            return result;
        } else if (type.equals("java.util.HashMap")) {
            List l = XmlUtil.parseTokenList(value);
            HashMap result = new HashMap();
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                String entryKey = (String) iter.next();
                if (!iter.hasNext()) {
                    failInvalidLiteral(reader, type, value);
                }
                String entryValue = (String) iter.next();
                result.put(
                    mustGetObjectForId(reader, parseId(reader, entryKey)),
                    mustGetObjectForId(reader, parseId(reader, entryValue)));
            }
            return result;
        } else if (type.equals("java.net.URI")) {  //bug fix: 4923072
            try {
                return new java.net.URI(value);
            } catch (URISyntaxException e) {
                failInvalidLiteral(reader, type, value);
            }
        } else if(type.equals("java.util.GregorianCalendar")){
            try {
                return XSDDateTimeCalendarEncoder.getInstance().stringToObject(
                    value, null);
            } catch (Exception e) {
                failInvalidLiteral(reader, type, value);
            }
        } else {
            
            // should not happen
            failInvalidLiteral(reader, type, value);
        }
        return null; // keep compiler happy
    }
    
    protected void verifyNoContent(XMLReader reader) {
        if (reader.nextElementContent() != XMLReader.END) {
            failInvalidSyntax(reader);
        }
    }
    
    protected String getRequiredAttribute(XMLReader reader, String name) {
        Attributes attributes = reader.getAttributes();
        String value = attributes.getValue(name);
        if (value == null) {
            failInvalidSyntax(reader);
        }
        return value;
    }
    
    protected Integer parseId(XMLReader reader, String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            failInvalidSyntax(reader);
            return null; // keep compiler happy
        }
    }
    
    protected boolean isNullId(Integer id) {
        return id.intValue() == 0;
    }
    
    protected Object getObjectForId(Integer id) {
        return id2obj.get(id);
    }
    
    protected Object mustGetObjectForId(XMLReader reader, Integer id) {
        Object result = getObjectForId(id);
        if (result == null) {
            failInvalidId(reader, id);
        }
        return result;
    }
    
    protected Object createInstanceOfType(XMLReader reader, String typename) {
        try {
            Class klass = Class.forName(typename);
            return klass.newInstance();
        } catch (InstantiationException e) {
			checkMinorMinorAndPatchVersion(reader);
            failInvalidClass(reader, typename);
        } catch (IllegalAccessException e) {
			checkMinorMinorAndPatchVersion(reader);
            failInvalidClass(reader, typename);
        } catch (ClassNotFoundException e) {
        	checkMinorMinorAndPatchVersion(reader);
            failInvalidClass(reader, typename);
        }
        return null; // keep compiler happy
    }
    
    protected void property(XMLReader reader, Object subject,
        String name, Object value) {
            
        failInvalidClass(reader, subject.getClass().getName());
    }
    
    protected abstract QName getContainerName();
    
    /*
     * Check to see if version of model is a known (current
     * or previous) version.
     */
    private void checkVersion() {
        if (getVersion() != null) {
        	targetModelVersion = getRequiredAttribute(reader, ATTR_VERSION);
			int[] targetVersion =
				VersionUtil.getCanonicalVersion(targetModelVersion);
			int[] currentVersion =
				VersionUtil.getCanonicalVersion(getVersion());
            
			// check major and minor version for current or previous
			if ((targetVersion[0] > currentVersion[0])
				|| (targetVersion[0] == currentVersion[0]
					&& currentVersion[1] > currentVersion[1])) {
						failInvalidVersion(
							reader,
							String.valueOf(targetVersion[0])
								+ "."
								+ String.valueOf(targetVersion[1])
								+ "."
								+ String.valueOf(targetVersion[2])
								+ "."
								+ String.valueOf(targetVersion[3]));
					}
        }
    }

	/*
	 * If model version (target) is greater than runtime
	 * version (current), then reject it
	 */
	private void checkMinorMinorAndPatchVersion(XMLReader reader) {
		if (getVersion() != null) {
			int[] targetVersion =
				VersionUtil.getCanonicalVersion(getTargetVersion());
			int[] currentVersion =
				VersionUtil.getCanonicalVersion(getVersion());
            
			if ((targetVersion[2] > currentVersion[2])
				|| (targetVersion[2] == currentVersion[2]
					&& targetVersion[3] > currentVersion[3])) {
						failInvalidMinorMinorOrPatchVersion(
							reader,
							String.valueOf(targetVersion[0])
								+ "."
								+ String.valueOf(targetVersion[1])
								+ "."
								+ String.valueOf(targetVersion[2])
								+ "."
								+ String.valueOf(targetVersion[3]),
						getVersion());
					}
		}
	}
    
	protected String getVersion() {
		return null;
	}
    
	protected String getTargetVersion() {
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
    
    // all the following methods must throw an exception
    protected abstract void failInvalidSyntax(XMLReader reader);
	protected abstract void failInvalidVersion(XMLReader reader,
		String version);
	protected abstract void failInvalidMinorMinorOrPatchVersion(
		XMLReader reader,
		String targetVersion,
		String currentVersion);
    protected abstract void failInvalidClass(XMLReader reader,
        String className);
    protected abstract void failInvalidId(XMLReader reader, Integer id);
    protected abstract void failInvalidLiteral(XMLReader reader, String type,
        String value);
    protected abstract void failInvalidProperty(XMLReader reader,
        Object subject, String name, Object value);
    
    protected InputStream in;
    protected XMLReader reader;
    protected Map id2obj;
    protected Set immediateClassNames;
    protected String targetModelVersion = "";
    
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
