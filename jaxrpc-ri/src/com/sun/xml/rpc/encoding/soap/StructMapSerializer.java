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

package com.sun.xml.rpc.encoding.soap;

import java.util.Iterator;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.ObjectSerializerBase;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.util.StructMap;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * A data-driven (de)serializer.
 *
 * @author JAX-RPC Development Team
 */

public class StructMapSerializer
    extends ObjectSerializerBase
    implements Initializable {
        
    protected InternalTypeMappingRegistry registry;

    public StructMapSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {
            
        super(type, encodeType, isNullable, encodingStyle);
    }

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {
            
        this.registry = registry;
    }

    protected void doSerializeInstance(
        Object instance,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        StructMap struct = (StructMap) instance;
        Iterator eachKey = struct.keys().iterator();
        Iterator eachValue = struct.values().iterator();

        while (eachKey.hasNext()) {
            Object value = eachValue.next();
            QName key = (QName) eachKey.next();

            if (value != null) {
                JAXRPCSerializer serializer =
                    (JAXRPCSerializer) registry.getSerializer(
                        encodingStyle,
                        value.getClass());
                serializer.serialize(value, key, null, writer, context);
            } else {
                serializeNull(key, writer, context);
            }
        }
    }

    protected Object doDeserialize(
        SOAPDeserializationState state,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {

        StructMap instance = new StructMap();
        Object member;
        StructMapBuilder builder = null;
        boolean isComplete = true;

        int memberIndex = 0;
        //bug fix 484627
        while (reader.nextElementContent() != XMLReader.END) {
            QName key = reader.getName();
            if (!getNullStatus(reader)) {
                JAXRPCDeserializer deserializer =
                    (JAXRPCDeserializer) registry.getDeserializer(
                        encodingStyle,
                        getType(reader));
                member = deserializer.deserialize(key, reader, context);
                if (member instanceof SOAPDeserializationState) {
                    if (builder == null) {
                        builder = new StructMapBuilder(instance);
                    }
                    state =
                        registerWithMemberState(
                            instance,
                            state,
                            member,
                            memberIndex,
                            builder);
                    isComplete = false;
                }

                // We have to reserve space for the eventual value and remember the name of the member no matter what
                instance.put(key, member);
            } else {
                instance.put(key, null);
            }
        }

        return (isComplete ? (Object) instance : (Object) state);
    }

    protected class StructMapBuilder implements SOAPInstanceBuilder {

        StructMap instance;

        StructMapBuilder(StructMap instance) {
            this.instance = instance;
        }

        public int memberGateType(int memberIndex) {
            return (
                SOAPInstanceBuilder.GATES_INITIALIZATION
                    | SOAPInstanceBuilder.REQUIRES_CREATION);
        }

        public void construct() {
            return;
        }

        public void setMember(int index, Object memberValue) {
            try {
                instance.set(index, memberValue);
            } catch (Exception e) {
                throw new DeserializationException(
                    "nestedSerializationError",
                    new LocalizableExceptionAdapter(e));
            }
        }

        public void initialize() {
            return;
        }

        public void setInstance(Object instance) {
            instance = (StructMap) instance;
        }

        public Object getInstance() {
            return instance;
        }
    }
}