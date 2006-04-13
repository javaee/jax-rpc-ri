/*
 * $Id: SOAPFaultInfoSerializer.java,v 1.2 2006-04-13 01:27:18 ofung Exp $
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

package com.sun.xml.rpc.encoding;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;

import com.sun.xml.rpc.encoding.literal.DetailFragmentDeserializer;
import com.sun.xml.rpc.encoding.literal.LiteralFragmentSerializer;
import com.sun.xml.rpc.encoding.simpletype.XSDQNameEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDStringEncoder;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.soap.message.SOAPFaultInfo;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPFaultInfoSerializer
    extends ObjectSerializerBase
    implements Initializable {
    protected static final QName FAULTACTOR_QNAME = new QName("", "faultactor");
    protected static final QName XSD_STRING_TYPE_QNAME =
        SchemaConstants.QNAME_TYPE_STRING;
    protected static final QName XSD_QNAME_TYPE_QNAME =
        SchemaConstants.QNAME_TYPE_QNAME;
    private static final int DETAIL_INDEX = 0;

    protected static final CombinedSerializer _XSDStringSerializer =
        new SimpleTypeSerializer(
            XSD_STRING_TYPE_QNAME,
            DONT_ENCODE_TYPE,
            NULLABLE,
            null,
            XSDStringEncoder.getInstance());
    protected static final CombinedSerializer _XSDQNameSerializer =
        new SimpleTypeSerializer(
            XSD_QNAME_TYPE_QNAME,
            DONT_ENCODE_TYPE,
            NULLABLE,
            null,
            XSDQNameEncoder.getInstance());
    protected static final QName FAULTCODE_QNAME = new QName("", "faultcode");
    protected static final QName FAULTSTRING_QNAME =
        new QName("", "faultstring");
    protected static final QName DETAIL_QNAME = new QName("", "detail");
    protected static final QName SOAPELEMENT_QNAME = new QName("", "element");

    public SOAPFaultInfoSerializer(boolean encodeType, boolean isNullable) {
        super(SOAPConstants.QNAME_SOAP_FAULT, encodeType, isNullable, null);
    }

    public SOAPFaultInfoSerializer(
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {
            
        super(
            SOAPConstants.QNAME_SOAP_FAULT,
            encodeType,
            isNullable,
            encodingStyle);
    }

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {
    }

    public Object doDeserialize(
        SOAPDeserializationState state,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {

        SOAPFaultInfo instance = null;
        boolean isComplete = true;
        QName elementName;
        QName code = null;
        String string = null;
        String actor = null;
        Object detail = null;
        SOAPInstanceBuilder builder = null;

        reader.nextElementContent();
        XMLReaderUtil.verifyReaderState(reader, XMLReader.START);
        elementName = reader.getName();
        if (elementName.equals(FAULTCODE_QNAME)) {
            code =
                (QName) _XSDQNameSerializer.deserialize(
                    FAULTCODE_QNAME,
                    reader,
                    context);
        }
        reader.nextElementContent();
        XMLReaderUtil.verifyReaderState(reader, XMLReader.START);
        elementName = reader.getName();
        if (elementName.equals(FAULTSTRING_QNAME)) {
            string =
                (String) _XSDStringSerializer.deserialize(
                    FAULTSTRING_QNAME,
                    reader,
                    context);
        }
        if (reader.nextElementContent() == XMLReader.START) {
            elementName = reader.getName();
            if (elementName.equals(FAULTACTOR_QNAME)) {
                actor =
                    (String) _XSDStringSerializer.deserialize(
                        FAULTACTOR_QNAME,
                        reader,
                        context);
                if (reader.nextElementContent() == XMLReader.START) {
                    elementName = reader.getName();
                }
            }
            instance = new SOAPFaultInfo(code, string, actor, detail);
            if (elementName.equals(DETAIL_QNAME)) {
                detail = deserializeDetail(state, reader, context, instance);

                if (detail instanceof SOAPDeserializationState) {
                    state = (SOAPDeserializationState) detail;
                    isComplete = false;
                } else {
                    instance.setDetail(detail);
                }
                reader.nextElementContent();
            }
        }
        if (instance == null) {
            instance = new SOAPFaultInfo(code, string, actor, detail);
        }

        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (isComplete ? (Object) instance : (Object) state);
    }

    public void doSerializeInstance(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        SOAPFaultInfo instance = (SOAPFaultInfo) obj;

        _XSDQNameSerializer.serialize(
            instance.getCode(),
            FAULTCODE_QNAME,
            null,
            writer,
            context);
        _XSDStringSerializer.serialize(
            instance.getString(),
            FAULTSTRING_QNAME,
            null,
            writer,
            context);
        if (instance.getActor() != null) {
            _XSDStringSerializer.serialize(
                instance.getActor(),
                FAULTACTOR_QNAME,
                null,
                writer,
                context);
        }
        serializeDetail(instance.getDetail(), writer, context);
    }

    protected Object deserializeDetail(
        SOAPDeserializationState state,
        XMLReader reader,
        SOAPDeserializationContext context,
        SOAPFaultInfo instance)
        throws Exception {

        reader.nextElementContent();
        return deserializeDetail(reader, context);
    }

    protected void serializeDetail(
        Object detail,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {
            
        if (detail instanceof Detail) {
            writer.startElement(DETAIL_QNAME);
            Iterator iter = ((Detail) detail).getDetailEntries();
            while (iter.hasNext()) {
                DetailEntry entry = (DetailEntry) iter.next();
                Name elementName = entry.getElementName();
                QName elementQName =
                    new QName(elementName.getURI(), elementName.getLocalName());
                LiteralFragmentSerializer serializer =
                    new LiteralFragmentSerializer(
                        DETAIL_QNAME,
                        isNullable,
                        encodingStyle);
                serializer.serialize(
                    entry,
                    elementQName,
                    null,
                    writer,
                    context);
            }
            writer.endElement();
        } else if (detail instanceof SOAPElement) {
            Iterator iter = ((SOAPElement) detail).getChildElements();
            if (iter.hasNext()) {
                SOAPElement entry = (SOAPElement) iter.next();
                Name elementName = entry.getElementName();
                QName elementQName =
                    new QName(elementName.getURI(), elementName.getLocalName());
                LiteralFragmentSerializer serializer =
                    new LiteralFragmentSerializer(
                        SOAPELEMENT_QNAME,
                        isNullable,
                        encodingStyle);
                serializer.serialize(
                    entry,
                    elementQName,
                    null,
                    writer,
                    context);
            }
            writer.endElement();
        }
    }

    protected Object deserializeDetail(
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {
            
        DetailFragmentDeserializer detailDeserializer =
            new DetailFragmentDeserializer(
                DETAIL_QNAME,
                isNullable,
                encodingStyle);
        Object detail =
            detailDeserializer.deserialize(reader.getName(), reader, context);
        return detail;
    }

    protected void skipRemainingDetailEntries(XMLReader reader)
        throws Exception {
            
        while (reader.getState() != XMLReader.END) {
            reader.skipElement();
            reader.nextElementContent();
        }
    }
}
