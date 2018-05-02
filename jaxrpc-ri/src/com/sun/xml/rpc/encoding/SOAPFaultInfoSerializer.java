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
