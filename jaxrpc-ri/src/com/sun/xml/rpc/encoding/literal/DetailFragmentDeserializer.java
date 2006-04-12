/*
 * $Id: DetailFragmentDeserializer.java,v 1.1 2006-04-12 20:34:36 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.literal;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializerBase;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XmlTreeReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class DetailFragmentDeserializer extends LiteralObjectSerializerBase {

    protected SOAPFactory soapFactory;

    public DetailFragmentDeserializer(QName type, String encodingStyle) {
        this(type, false, encodingStyle);
    }

    public DetailFragmentDeserializer(
        QName type,
        boolean isNillable,
        String encodingStyle) {
        super(type, isNillable, encodingStyle);

        try {

            soapFactory = SOAPFactory.newInstance();

        } catch (SOAPException e) {
            // TODO - report this
        }
    }

    protected void writeAdditionalNamespaceDeclarations(
        Object obj,
        XMLWriter writer)
        throws Exception {
        // no-op
    }

    protected Object internalDeserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {

        boolean pushedEncodingStyle = context.processEncodingStyle(reader);
        try {
            context.verifyEncodingStyle(encodingStyle);

            if (name != null) {
                QName actualName = reader.getName();
                if (!actualName.equals(name)) {
                    throw new DeserializationException(
                        "xsd.unexpectedElementName",
                        new Object[] { name.toString(), actualName.toString()});
                }
            }

            Attributes attrs = reader.getAttributes();

            /*           String typeVal = attrs.getValue(XSDConstants.URI_XSI, "type");
                       if (typeVal != null) {
                            QName actualType = XMLReaderUtil.decodeQName(reader, typeVal);
                            if (!actualType.equals(type)) {
                                throw new DeserializationException("xsd.unexpectedElementType",
                                new Object[] {
                                    type.toString(),
                                    actualType.toString()
                                });
                            }
                        }
            */
            String nullVal = attrs.getValue(XSDConstants.URI_XSI, "nil");
            boolean isNull =
                (nullVal != null && SerializerBase.decodeBoolean(nullVal));
            Object obj = null;

            if (isNull) {
                if (!isNullable) {
                    throw new DeserializationException("xsd.unexpectedNull");
                }
                reader.next();
            } else {
                obj = doDeserialize(reader, context);
            }

            XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
            return obj;
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }

    protected Object deserializeElement(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {

        boolean pushedEncodingStyle = context.processEncodingStyle(reader);
        try {
            context.verifyEncodingStyle(encodingStyle);

            if (name != null) {
                QName actualName = reader.getName();
                if (!actualName.equals(name)) {
                    throw new DeserializationException(
                        "xsd.unexpectedElementName",
                        new Object[] { name.toString(), actualName.toString()});
                }
            }

            Attributes attrs = reader.getAttributes();

            String nullVal = attrs.getValue(XSDConstants.URI_XSI, "nil");
            boolean isNull = 
                (nullVal != null && SerializerBase.decodeBoolean(nullVal));
            Object obj = null;

            if (isNull) {
                // bug fix: 4974593
//                if (!isNullable) {
//                    throw new DeserializationException("xsd.unexpectedNull");
//                }
                reader.next();
            } else {
                obj = doDeserializeElement(reader, context);
            }

            XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
            return obj;
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }

    protected Object doDeserialize(
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {
            
        Detail detail;
        detail = soapFactory.createDetail();
        String elementURI = reader.getURI();
        Name name;
        if (elementURI == null || elementURI.equals("")) {
            name = soapFactory.createName(reader.getLocalName());
        } else {
            name =
                soapFactory.createName(
                    reader.getLocalName(),
                    FIRST_PREFIX,
                    elementURI);
        }
        DetailEntry entry = detail.addDetailEntry(name);
        doDeserializeElement(entry, reader, context);
        return detail;
    }

    protected Object doDeserializeElement(
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {
            
        SOAPElement element;
        String elementURI = reader.getURI();
        if (elementURI == null || elementURI.equals("")) {
            element = soapFactory.createElement(reader.getLocalName());
		//Narayana Murthy P: Modified qualified element creation based on the prefix instead based on the namespace
        } else if (reader instanceof XmlTreeReader) {
			SOAPElement soapElement = (SOAPElement) ((XmlTreeReader) reader)
					.getCurrentNode();
			if (soapElement != null && soapElement.getPrefix() == null) {
				element = soapFactory.createElement(reader.getLocalName(),
						null, null);
			} else {
				element = soapFactory.createElement(reader.getLocalName(),
						FIRST_PREFIX, reader.getURI());
			}
		}  else {
            element =
                soapFactory.createElement(
                    reader.getLocalName(),
                    FIRST_PREFIX,
                    reader.getURI());
        }
        doDeserializeElement(element, reader, context);
        return element;
    }

    protected void doDeserializeElement(
        SOAPElement element,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {
            
        String defaultURI = reader.getURI("");
        if (defaultURI != null) {
            element.addNamespaceDeclaration("", defaultURI);
        }

        for (Iterator iter = reader.getPrefixes(); iter.hasNext();) {
            String prefix = (String) iter.next();
            String uri = reader.getURI(prefix);
            element.addNamespaceDeclaration(prefix, uri);
        }

        Attributes attributes = reader.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            if (attributes.isNamespaceDeclaration(i)) {
                continue;
            }

            Name name;
            String uri = attributes.getURI(i);
            if (uri == null) {
                // non-qualified attribute
                name = soapFactory.createName(attributes.getLocalName(i));
            } else {
                // qualified attribute
                String prefix = attributes.getPrefix(i);
                name =
                    soapFactory.createName(
                        attributes.getLocalName(i),
                        prefix,
                        uri);
            }
            element.addAttribute(name, attributes.getValue(i));
        }

        reader.next();
        while (reader.getState() != XMLReader.END) {
            int state = reader.getState();
            if (state == XMLReader.START) {
                SOAPElement child =
                    (SOAPElement) deserializeElement(null, reader, context);
                element.addChildElement(child);
            } else if (state == XMLReader.CHARS) {
                element.addTextNode(reader.getValue());
            }

            reader.next();
        }
    }

    protected void doSerialize(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {
        // unused
    }

    protected void doSerializeAttributes(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {
        // unused
    }

    private static final String FIRST_PREFIX = "ns";
}
