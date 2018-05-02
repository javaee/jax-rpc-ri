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

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;

/**
 * Serializes and Deserializes objects dynamically based on their Java type or XML type respectively
 *
 * @author JAX-RPC Development Team
 */

public class DynamicSerializer
    extends SerializerBase
    implements SchemaConstants, Initializable {

    InternalTypeMappingRegistry registry = null;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public DynamicSerializer(
        QName xmlType,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {

        this(
            xmlType,
            encodeType,
            isNullable,
            encodingStyle,
            SOAPVersion.SOAP_11);
    }

    public DynamicSerializer(
        QName xmlType,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        SOAPVersion ver) {

        super(xmlType, encodeType, isNullable, encodingStyle);
        init(ver); // Initialize SOAP constants
    }

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {

        this.registry = registry;
    }

    public void serialize(
        Object obj,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context) {

        if (obj == null) {
            serializeNull(name, writer);
            return;
        }

        JAXRPCSerializer serializer = getSerializerForObject(obj);

        if (serializer != null) {
            serializer.serialize(obj, name, callback, writer, context);
        }
    }

    protected JAXRPCSerializer getSerializerForObject(Object obj) {
        JAXRPCSerializer serializer = null;
        try {
            serializer =
                (JAXRPCSerializer) registry.getSerializer(
                    soapEncodingConstants.getURIEncoding(),
                    obj.getClass());
            if (serializer instanceof DynamicSerializer) {
                throw new SerializationException(
                    "typemapping.serializer.is.dynamic",
                    new Object[] { obj.getClass()});
            }
        } catch (SerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new SerializationException(
                "nestedSerializationError",
                new LocalizableExceptionAdapter(e));
        }

        return serializer;
    }

    protected void serializeNull(QName name, XMLWriter writer) {

        try {
            writer.startElement((name != null) ? name : QNAME_ANY);

            String attrVal = XMLWriterUtil.encodeQName(writer, super.type);
            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_TYPE, attrVal);

            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_NIL, "1");
            writer.endElement();
        } catch (JAXRPCExceptionBase e) {
            throw new SerializationException("nestedSerializationError", e);
        }
    }

    public Object deserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context) {

        try {
            JAXRPCDeserializer deserializer =
                getDeserializerForElement(reader, context);

            if (deserializer == null) {
                return null;
            } else {
                return deserializer.deserialize(name, reader, context);
            }
        } catch (DeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new DeserializationException(
                "nestedDeserializationError",
                new LocalizableExceptionAdapter(e));
        }
    }

    protected JAXRPCDeserializer getDeserializerForElement(
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {

        if (getNullStatus(reader) == true) {
            skipEmptyContent(reader);
            return null;
        }

        QName objectXMLType = getType(reader);
        return (JAXRPCDeserializer) registry.getDeserializer(
            soapEncodingConstants.getURIEncoding(),
            objectXMLType);
    }
}
