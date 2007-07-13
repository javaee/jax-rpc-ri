/*
 * $Id: ObjectSerializerBase.java,v 1.3 2007-07-13 23:35:57 ofung Exp $
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

import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.streaming.XMLWriterUtil;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class ObjectSerializerBase extends SerializerBase {

    protected ObjectSerializerBase(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {
        super(type, encodeType, isNullable, encodingStyle);
    }

    /**
     *  Deserialize each element coming out of <code>reader</code> into <code>state</code> with the aid of <code>context</code>
     *  <p>
     *  Example:
     *  <code>
     *  protected Object doDeserialize(SOAPDeserializationState state, XMLReader reader, SOAPDeserializationContext context)
     *      throws Exception {
     *
     *      Foo instance = new Foo();
     *      Foo_SOAPBuilder builder = null;
     *      Object member;
     *      boolean isComplete = true;
     *      QName elementName;
     *
     *      reader.nextElementContent();
     *      elementName = reader.getName();
     *      if (elementName.equals(FooMember_QNAME)) { // check to see if this is the name of the next expected member
     *          member = FooMemberDeserializer.deserialize(FooMember_QNAME, reader, context);
     *          if (member instanceof SOAPDeserializationState) {
     *              if (builder == null) {
     *                  builder = new FooMemberBuilder();
     *              }
     *              state = registerWithMemberState(instance, state, member, MEMBER_INDEX, builder); // MEMBER_INDEX is the index of the member within the object
     *              isComplete = false;
     *          } else {
     *              instance.setMember((FooMember)member); // "setMember" is whatever setter is appropriate for the member
     *          }
     *      }
     *
     *      reader.nextElementContent();
     *      XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
     *      return (isComplete ? (Object)instance : (Object)state);
     *  }
     *  </code>
     */
    protected abstract Object doDeserialize(
        SOAPDeserializationState state,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception;

    /**
     *  Serialize each data member of <code>obj</code> into <code>writer</code> with the aid of <code>context</code>
     */
    protected abstract void doSerializeInstance(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception;

    protected void doSerializeAttributes(
        Object obj,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {
    }

    public void serialize(
        Object obj,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context) {

        boolean pushedEncodingStyle = false;
        try {
            if (obj == null) {
                if (!isNullable) {
                    throw new SerializationException("soap.unexpectedNull");
                }
                serializeNull(name, writer, context);
            } else {
                writer.startElement((name != null) ? name : type);
                if (callback != null) {
                    callback.onStartTag(obj, name, writer, context);
                }

                if (encodingStyle != null) {
                    pushedEncodingStyle =
                        context.pushEncodingStyle(encodingStyle, writer);
                }

                if (encodeType) {
                    String attrVal = XMLWriterUtil.encodeQName(writer, type);
                    writer.writeAttributeUnquoted(
                        XSDConstants.QNAME_XSI_TYPE,
                        attrVal);
                }
                doSerializeAttributes(obj, writer, context);
                doSerializeInstance(obj, writer, context);
                writer.endElement();
            }
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

    protected void serializeNull(
        QName name,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        writer.startElement((name != null) ? name : type);

        boolean pushedEncodingStyle = false;
        if (encodingStyle != null)
            pushedEncodingStyle =
                context.pushEncodingStyle(encodingStyle, writer);

        if (encodeType) {
            String attrVal = XMLWriterUtil.encodeQName(writer, type);
            writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_TYPE, attrVal);
        }

        writer.writeAttributeUnquoted(XSDConstants.QNAME_XSI_NIL, "1");
        writer.endElement();
        if (pushedEncodingStyle) {
            context.popEncodingStyle();
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
                verifyName(reader, name);
            }

            String id = getID(reader);
            boolean isNull = getNullStatus(reader);
            if (!isNull) {
                verifyType(reader);

                SOAPDeserializationState state = null;
                if (id != null) {
                    state = context.getStateFor(id);
                    state.setDeserializer(this);
                }

                Object instance = doDeserialize(state, reader, context);
                /* TODO: This eventually needs to be removed to handle inheritence 
                 * of exceptions and value types when no xsi:type information is 
                 * included on the element.  We do this because we fall back to the 
                 * base type serializer in this case and not all of the elements are
                 * consumed, they should be consumed by the 
                 * base_type_inerface_SOAPSerializer.
                 */
                //                XMLReaderUtil.verifyReaderState(reader, XMLReader.END);

                if (instance instanceof SOAPDeserializationState) {
                    state = (SOAPDeserializationState) instance;
                } else if (state != null) {
                    state.setInstance(instance);
                }

                if (state != null) {
                    state.doneReading();
                    return state;
                }

                return instance;
            } else {
                if (!isNullable) {
                    throw new DeserializationException("soap.unexpectedNull");
                }

                skipEmptyContent(reader);

                if (id != null) {
                    SOAPDeserializationState state = context.getStateFor(id);
                    state.setDeserializer(this);
                    state.setInstance(null);
                    state.doneReading();
                }

                return null;
            }
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

    public static SOAPDeserializationState registerWithMemberState(
        Object instance,
        SOAPDeserializationState state,
        Object member,
        int memberIndex,
        SOAPInstanceBuilder builder) {
            
        try {
            SOAPDeserializationState deserializationState;
            if (state == null) {
                deserializationState = new SOAPDeserializationState();
            } else {
                deserializationState = state;
            }

            deserializationState.setInstance(instance);
            if (deserializationState.getBuilder() == null) {
                if (builder == null) {
                    throw new IllegalArgumentException();
                }
                deserializationState.setBuilder(builder);
            }

            SOAPDeserializationState memberState =
                (SOAPDeserializationState) member;
            memberState.registerListener(deserializationState, memberIndex);

            return deserializationState;
        } catch (JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        } catch (Exception e) {
            throw new DeserializationException(
                new LocalizableExceptionAdapter(e));
        }
    }
}