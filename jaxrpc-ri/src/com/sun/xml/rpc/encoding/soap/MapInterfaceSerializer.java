/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

// Helper class generated by wscompile, do not edit.
// Contents subject to change without notice.

package com.sun.xml.rpc.encoding.soap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.CombinedSerializer;
import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InterfaceSerializerBase;
import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializationException;
import com.sun.xml.rpc.encoding.SerializerCallback;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

public class MapInterfaceSerializer
    extends InterfaceSerializerBase
    implements Initializable, InternalEncodingConstants {
    private CombinedSerializer hashMapSerializer;
    private CombinedSerializer treeMapSerializer;
    private CombinedSerializer hashtableSerializer;
    private CombinedSerializer propertiesSerializer;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        this.soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public MapInterfaceSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {
            
        this(type, encodeType, isNullable, encodingStyle, SOAPVersion.SOAP_11);
    }

    public MapInterfaceSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        SOAPVersion ver) {
            
        super(type, encodeType, isNullable, encodingStyle);
        init(ver); //Initialize SOAP version
    }

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {
            
        hashMapSerializer =
            (CombinedSerializer) registry.getSerializer(
                encodingStyle,
                HashMap.class,
                QNAME_TYPE_HASH_MAP);
        hashMapSerializer = hashMapSerializer.getInnermostSerializer();
        treeMapSerializer =
            (CombinedSerializer) registry.getSerializer(
                encodingStyle,
                TreeMap.class,
                QNAME_TYPE_TREE_MAP);
        treeMapSerializer = treeMapSerializer.getInnermostSerializer();
        hashtableSerializer =
            (CombinedSerializer) registry.getSerializer(
                encodingStyle,
                Hashtable.class,
                QNAME_TYPE_HASHTABLE);
        hashtableSerializer = hashtableSerializer.getInnermostSerializer();
        propertiesSerializer =
            (CombinedSerializer) registry.getSerializer(
                encodingStyle,
                Properties.class,
                QNAME_TYPE_PROPERTIES);
        propertiesSerializer = propertiesSerializer.getInnermostSerializer();
    }

    public Object doDeserialize(
        QName name,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {
            
        QName elementType = getType(reader);
        if (elementType.equals(QNAME_TYPE_MAP)
            || elementType.equals(QNAME_TYPE_HASH_MAP)) {
            return hashMapSerializer.deserialize(name, reader, context);
        } else if (elementType.equals(QNAME_TYPE_TREE_MAP)) {
            return treeMapSerializer.deserialize(name, reader, context);
        } else if (elementType.equals(QNAME_TYPE_HASHTABLE)) {
            return hashtableSerializer.deserialize(name, reader, context);
        } else if (elementType.equals(QNAME_TYPE_PROPERTIES)) {
            return propertiesSerializer.deserialize(name, reader, context);
        }
        throw new DeserializationException(
            "soap.unexpectedElementType",
            new Object[] { "", elementType.toString()});
    }

    public void doSerializeInstance(
        Object obj,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        if (obj instanceof HashMap) {
            hashMapSerializer.serialize(obj, name, callback, writer, context);
        } else if (obj instanceof Properties) {
            propertiesSerializer.serialize(
                obj,
                name,
                callback,
                writer,
                context);
        } else if (obj instanceof Hashtable) {
            hashtableSerializer.serialize(obj, name, callback, writer, context);
        } else if (obj instanceof TreeMap) {
            treeMapSerializer.serialize(obj, name, callback, writer, context);
        } else if (obj instanceof Map) {
            hashMapSerializer.serialize(obj, name, callback, writer, context);
        } else {
            throw new SerializationException(
                "soap.cannot.serialize.type",
                obj.getClass().getName());
        }
    }
}
