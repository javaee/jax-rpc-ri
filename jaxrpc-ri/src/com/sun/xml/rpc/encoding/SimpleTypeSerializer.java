
/*
 * $Id: SimpleTypeSerializer.java,v 1.3 2007-07-13 23:35:58 ofung Exp $
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

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.simpletype.SimpleTypeConstants;
import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

import com.sun.xml.rpc.streaming.FastInfosetReader;
import com.sun.xml.rpc.encoding.simpletype.XSDBase64BinaryEncoder;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SimpleTypeSerializer
    extends SerializerBase
    implements SimpleTypeConstants {

    protected SimpleTypeEncoder encoder;

    public SimpleTypeSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        SimpleTypeEncoder encoder) {
            
        super(type, encodeType, isNullable, encodingStyle);
        this.encoder = encoder;
    }

    public void serialize(
        Object obj,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context) {
            
        boolean pushedEncodingStyle = false;
        try {
            writer.startElement((name != null) ? name : type);
            if (callback != null) {
                callback.onStartTag(obj, name, writer, context);
            }

            if (encodingStyle != null)
                pushedEncodingStyle =
                    context.pushEncodingStyle(encodingStyle, writer);

            if (encodeType) {
                String attrVal = XMLWriterUtil.encodeQName(writer, type);
                writer.writeAttributeUnquoted(QNAME_XSI_TYPE, attrVal);
            }

            if (obj == null) {
                if (!isNullable) {
                    throw new SerializationException("xsd.unexpectedNull");
                }

                writer.writeAttributeUnquoted(QNAME_XSI_NIL, "1");
            } else {
                encoder.writeAdditionalNamespaceDeclarations(obj, writer);
                encoder.writeValue(obj, writer);
            }

            writer.endElement();
        } catch (SerializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        } catch (Exception e) {
            throw new SerializationException(
                new LocalizableExceptionAdapter(e));
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }

    public Object deserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context) {

        boolean pushedEncodingStyle = false;
        try {
            pushedEncodingStyle = context.processEncodingStyle(reader);
            if (encodingStyle != null)
                context.verifyEncodingStyle(encodingStyle);

            if (name != null) {
                QName actualName = reader.getName();
                if (!actualName.equals(name)) {
                    throw new DeserializationException(
                        "xsd.unexpectedElementName",
                        new Object[] { name.toString(), actualName.toString()});
                }
            }

            verifyType(reader);

            Attributes attrs = reader.getAttributes();

            String nullVal = attrs.getValue(URI_XSI, "nil");
            boolean isNull =
                (nullVal != null
                    && (nullVal.equals("true") || nullVal.equals("1")));

            reader.next();
            Object obj = null;

            if (isNull) {
                if (!isNullable) {
                    throw new DeserializationException("xsd.unexpectedNull");
                }
            } else {
                String val = null;

                switch (reader.getState()) {
                    case XMLReader.CHARS :
                        /*
                            * An FI reader *may* get bytes using an encoding algorithm. If so, return
                            * the bytes directly without base64 decoding.
                            */
                        if (reader instanceof FastInfosetReader &&
                            encoder instanceof XSDBase64BinaryEncoder) 
                        {
                            final FastInfosetReader fiReader = (FastInfosetReader) reader;
                            obj = fiReader.getTextAlgorithmBytes();
                            if (obj != null && fiReader.getTextAlgorithmIndex() ==
                                    org.jvnet.fastinfoset.EncodingAlgorithmIndexes.BASE64) 
                            {
                                obj = fiReader.getTextAlgorithmBytesClone();
                                reader.next();
                                break;      // ends switch
                            } 
                        }
                         
                        val = reader.getValue();
                        obj = encoder.stringToObject(val, reader);
                        reader.next();
                        break;
                    case XMLReader.END :
                        val = "";
                        obj = encoder.stringToObject(val, reader);
                        break;
                }
            }

            XMLReaderUtil.verifyReaderState(reader, XMLReader.END);

            return obj;
        } catch (DeserializationException e) {
            throw e;
        } catch (JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        } catch (Exception e) {
            throw new DeserializationException(
                new LocalizableExceptionAdapter(e));
        } finally {
            if (pushedEncodingStyle) {
                context.popEncodingStyle();
            }
        }
    }
}
