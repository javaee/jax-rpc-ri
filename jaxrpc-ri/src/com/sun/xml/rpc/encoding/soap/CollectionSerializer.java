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

import java.lang.reflect.Array;
import java.util.Collection;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializationException;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class CollectionSerializer
    extends CollectionSerializerBase
    implements Initializable {

    protected Class collClass = null;
    protected JAXRPCSerializer elemSer;
    protected JAXRPCDeserializer elemDeser;

    public CollectionSerializer(
        QName type,
        Class collClass,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        QName elemName,
        QName elemType,
        Class elemClass) {
            
        this(
            type,
            collClass,
            encodeType,
            isNullable,
            encodingStyle,
            elemName,
            elemType,
            elemClass,
            SOAPVersion.SOAP_11);
    }

    public CollectionSerializer(
        QName type,
        Class collClass,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        QName elemName,
        QName elemType,
        Class elemClass,
        SOAPVersion version) {
            
        super(
            type,
            encodeType,
            isNullable,
            encodingStyle,
            elemName,
            elemType,
            elemClass,
            1,
            null);
        this.collClass = collClass;
    }

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {
            
        elemSer =
            (JAXRPCSerializer) registry.getSerializer(
                encodingStyle,
                elemClass,
                elemType);
        elemDeser =
            (JAXRPCDeserializer) registry.getDeserializer(
                encodingStyle,
                elemClass,
                elemType);
    }

    protected Object[] convertToArray(Object obj) throws Exception {
        return ((Collection) obj).toArray();
    }

    protected void serializeArrayInstance(
        Object obj,
        int[] dims,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        serializeArrayElements((Object[]) obj, 0, dims, writer, context);
    }

    protected void serializeArrayElements(
        Object[] arr,
        int level,
        int[] dims,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        if (arr == null || arr.length != dims[level]) {
            throw new SerializationException("soap.irregularMultiDimensionalArray");
        }

        boolean serializeLeaves = (level == dims.length - 1);

        for (int i = 0; i < dims[level]; ++i) {
            Object elem = arr[i];
            if (serializeLeaves) {
                elemSer.serialize(elem, elemName, null, writer, context);
            } else {
                serializeArrayElements(
                    (Object[]) elem,
                    level + 1,
                    dims,
                    writer,
                    context);
            }
        }
    }

    protected Object deserializeArrayInstance(
        XMLReader reader,
        SOAPDeserializationContext context,
        int[] dims)
        throws Exception {

        Collection instance = (Collection) collClass.newInstance();
        String id = getID(reader);
        SOAPDeserializationState state =
            ((id != null) ? context.getStateFor(id) : null);
        boolean isComplete = true;
        boolean emptyDims = isEmptyDimensions(dims);
        final int[] dimOffsets = getDimensionOffsets(dims);

        int[] offset = getArrayOffset(reader, dims);
        if (offset == null) {
            offset = new int[emptyDims ? 1 : dims.length];
        }

        Object[] value = null;
        int maxPosition = 0;
        int length = 0;

        if (reader.nextElementContent() != XMLReader.END) {
            int[] position = getArrayElementPosition(reader, dims);
            boolean isSparseArray = (position != null);

            if (!isSparseArray) {
                position = offset;
            }

            if (emptyDims) {
                maxPosition = position[0];
                length = Math.max(maxPosition * 2, 1024);
                value = (Object[]) Array.newInstance(elemClass, length);
            } else {
                value = (Object[]) Array.newInstance(elemClass, dims);
            }

            while (true) {
                if (!emptyDims && !isPositionWithinBounds(position, dims)) {
                    if (isSparseArray) {
                        throw new DeserializationException(
                            "soap.outOfBoundsArrayElementPosition",
                            encodeArrayDimensions(position));
                    } else {
                        throw new DeserializationException("soap.tooManyArrayElements");
                    }
                }

                if (emptyDims) {
                    if (position[0] >= length) {
                        int newLength = length * 2;
                        while (position[0] >= newLength) {
                            newLength *= 2;
                        }
                        Object[] newValue =
                            (Object[]) Array.newInstance(elemClass, newLength);
                        System.arraycopy(value, 0, newValue, 0, length);
                        value = newValue;
                        length = newLength;
                    }
                }

                Object elem = null;
                elem = elemDeser.deserialize(elemName, reader, context);

                if (elem instanceof SOAPDeserializationState) {
                    SOAPDeserializationState elemState =
                        (SOAPDeserializationState) elem;
                    isComplete = false;

                    if (state == null) {
                        // i'm a single-ref instance
                        state = new SOAPDeserializationState();
                    }

                    // ensure that state (and therefore builder) contains a reference
                    // to the current array since registerListener could call back
                    // on the builder if the element object has already been created
                    state.setInstance(instance);

                    if (state.getBuilder() == null) {
                        state.setBuilder(
                            new CollectionInstanceBuilder(value, dimOffsets));
                    }

                    elemState.registerListener(
                        state,
                        indexFromPosition(position, dimOffsets));
                } else {
                    setElement(value, position, elem);
                }

                if (reader.nextElementContent() == XMLReader.END) {
                    break;
                }

                if (isSparseArray) {
                    position = getArrayElementPosition(reader, dims);
                    if (position == null) {
                        // all elements of a sparse array must have a position attribute
                        throw new DeserializationException("soap.missingArrayElementPosition");
                    }
                } else {
                    if (emptyDims) {
                        ++position[0];
                    } else {
                        incrementPosition(position, dims);
                    }
                }

                if (emptyDims) {
                    maxPosition = Math.max(position[0], maxPosition);
                }
            }

            if (emptyDims) {
                if (length != maxPosition + 1) {
                    int newLength = maxPosition + 1;
                    Object[] newValue =
                        (Object[]) Array.newInstance(elemClass, newLength);
                    System.arraycopy(value, 0, newValue, 0, newLength);
                    value = newValue;
                    length = newLength;
                }
            }
        } else {
            if (emptyDims) {
                value = (Object[]) Array.newInstance(elemClass, 0);
            } else {
                value = (Object[]) Array.newInstance(elemClass, dims);
            }
        }

        if (state != null) {
            state.setDeserializer(this);
            state.setInstance(instance);
            state.doneReading();
        }

        if (isComplete) {
            return arrayToCollection(instance, value);
        } else {
            return state;
        }
    }

    public static void setElement(
        Object[] value,
        int[] position,
        Object elem) {
            
        Object[] arr = value;
        for (int i = 0; i < position.length - 1; ++i) {
            arr = (Object[]) arr[position[i]];
        }

        arr[position[position.length - 1]] = elem;
    }

    /********************* CollectionInstanceBuilder ************************/
    private class CollectionInstanceBuilder implements SOAPInstanceBuilder {

        int[] dimOffsets = null;
        Object[] arrInstance = null;
        Collection instance = null;

        CollectionInstanceBuilder(Object[] arrInstance, int[] dimOffsets) {
            this.arrInstance = arrInstance;
            this.dimOffsets = dimOffsets;
        }

        public int memberGateType(int memberIndex) {
            return (
                SOAPInstanceBuilder.GATES_INITIALIZATION
                    + SOAPInstanceBuilder.REQUIRES_INITIALIZATION);
        }

        public void construct() {
            throw new IllegalStateException();
        }

        public void setMember(int index, Object memberValue) {
            int[] position = positionFromIndex(index, dimOffsets);
            setElement(arrInstance, position, memberValue);
        }

        public void initialize() {
            this.instance = arrayToCollection(instance, arrInstance);
        }

        public void setInstance(Object instance) {
            this.instance = (Collection) instance;
        }

        public Object getInstance() {
            return instance;
        }
    }

    /*********************** arrayToCollection **************************/
    public static Collection arrayToCollection(
        Collection collInstance,
        Object[] arrInstance) {
        if (arrInstance != null) {
            // put the members of an array into a given collection
            for (int i = 0; i < arrInstance.length; i++) {
                Object collItem = arrInstance[i];
                collInstance.add(collItem);
            }
        }
        return collInstance;
    }
}
