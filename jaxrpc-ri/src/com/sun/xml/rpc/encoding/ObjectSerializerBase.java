/*
 * $Id: ObjectSerializerBase.java,v 1.1 2006-04-12 20:33:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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