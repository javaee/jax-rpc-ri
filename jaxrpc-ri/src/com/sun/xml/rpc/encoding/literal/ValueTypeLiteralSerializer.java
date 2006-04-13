/*
 * $Id: ValueTypeLiteralSerializer.java,v 1.2 2006-04-13 01:27:39 ofung Exp $
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

package com.sun.xml.rpc.encoding.literal;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.DynamicInternalTypeMappingRegistry;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * Serializes and Deserializes JavaBeans/Data Objects. Uses a combinatin of
 * reflection and introspection to determine how to get and set values into
 * and out of the object.
 *
 * @author JAX-RPC Development Team
 */

public class ValueTypeLiteralSerializer
    extends GenericLiteralObjectSerializer {
    protected String memberNamespace = null;

    //work outaround for lack of modelpropagation and
    //therefore lack of memberorder for dynamicProxy/dii valueTypes

    public ValueTypeLiteralSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {

        super(
            type != null ? type : new QName(""),
            encodeType,
            isNullable,
            encodingStyle);
    }

    public ValueTypeLiteralSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        Collection params) {

        super(
            type != null ? type : new QName(""),
            encodeType,
            isNullable,
            encodingStyle);
        this.memberOrder = params;
    }

    public ValueTypeLiteralSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        Class targetClass) {

        this(type, encodeType, isNullable, encodingStyle);
        super.setTargetClass(targetClass);
    }

    public ValueTypeLiteralSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        Class targetClass,
        String memberNamespace) {

        this(type, encodeType, isNullable, encodingStyle, targetClass);
        this.memberNamespace = memberNamespace;

    }

    public ValueTypeLiteralSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        Class targetClass,
        Collection paramOrder) {

        this(type, encodeType, isNullable, encodingStyle, paramOrder);
        super.setTargetClass(targetClass);

    }

    protected void doSetTargetClass(Class targetClass) {
        try {
            introspectTargetClass(targetClass);
            reflectTargetClass(targetClass);
        } catch (Exception e) {
            throw new DeserializationException(
                "nestedSerializationError",
                new LocalizableExceptionAdapter(e));
        }
    }

    protected void introspectTargetClass(Class targetClass) throws Exception {
        BeanInfo beanInfoForTarget = Introspector.getBeanInfo(targetClass);
        PropertyDescriptor[] targetProperties =
            beanInfoForTarget.getPropertyDescriptors();
        //workaround for lack of wsdl propagation
        targetProperties = membersByParameterOrder(targetProperties);
        //order targetProperties by wsdl order
        for (int i = 0; i < targetProperties.length; ++i) {
            final Method getterMethod = targetProperties[i].getReadMethod();
            final Method setterMethod = targetProperties[i].getWriteMethod();

            if (getterMethod == null || setterMethod == null) {
                continue;
            }
            MemberInfo member = new MemberInfo();

            //I don't recall this being here?
            member.name =
                new QName(memberNamespace, targetProperties[i].getName());
            Class baseJavaType = targetProperties[i].getPropertyType();
            member.javaType = getBoxedClassFor(baseJavaType);
            member.xmlType = (QName) javaToXmlType.get(baseJavaType);

            //for doclit and valueType todo:check for doclit
            if (DynamicInternalTypeMappingRegistry
                .isValueType(member.javaType)) {
                if (member.xmlType == null)
                    member.xmlType = member.name;
            }

            member.getter = new GetterMethod() {
                public Object get(Object instance) throws Exception {
                    return getterMethod.invoke(instance, new Object[0]);
                }
            };
            member.setter = new SetterMethod() {
                public void set(Object instance, Object value)
                    throws Exception {
                    setterMethod.invoke(instance, new Object[]{value});
                }
            };

            super.addMember(member);
        }
    }

    protected void reflectTargetClass(Class targetClass) throws Exception {
        Field[] targetFields = targetClass.getFields();
        for (int i = 0; i < targetFields.length; ++i) {
            final Field currentField = targetFields[i];

            int fieldModifiers = currentField.getModifiers();
            if (!Modifier.isPublic(fieldModifiers)) {
                continue;
            }
            if (Modifier.isTransient(fieldModifiers)) {
                continue;
            }
            if (Modifier.isFinal(fieldModifiers)) {
                continue;
            }

            MemberInfo member = new MemberInfo();

            member.name = new QName(memberNamespace, currentField.getName());
            Class baseJavaType = targetFields[i].getType();
            member.javaType = getBoxedClassFor(baseJavaType);
            member.xmlType = (QName) javaToXmlType.get(baseJavaType);

            member.getter = new GetterMethod() {
                public Object get(Object instance) throws Exception {
                    Field field = currentField;
                    return field.get(instance);
                }
            };
            member.setter = new SetterMethod() {
                public void set(Object instance, Object value)
                    throws Exception {
                    Field field = currentField;
                    field.set(instance, value);
                }
            };

            super.addMember(member);
        }
    }

    private PropertyDescriptor[] membersByParameterOrder(
        PropertyDescriptor[] targetProperties) {

        if (targetProperties == null)
            return null;
        if ((memberOrder == null) || (memberOrder.size() == 0))
            return targetProperties;

        Collection members = new ArrayList();
        Iterator namesIter = memberOrder.iterator();
        while (namesIter.hasNext()) {
            String name = (String) namesIter.next();

            for (int j = 0; j < targetProperties.length; j++) {
                String propertyName = targetProperties[j].getName();
                if (propertyName.equalsIgnoreCase(name)) {
                    members.add(targetProperties[j]);
                }
            }
        }
        if (members.size() == 0)
            return targetProperties;

        return (PropertyDescriptor[]) members.toArray(
            new PropertyDescriptor[members.size()]);
    }

    private static Class getBoxedClassFor(Class possiblePrimitiveType) {
        if (!possiblePrimitiveType.isPrimitive())
            return possiblePrimitiveType;

        if (possiblePrimitiveType == boolean.class)
            return Boolean.class;
        if (possiblePrimitiveType == byte.class)
            return Byte.class;
        if (possiblePrimitiveType == short.class)
            return Short.class;
        if (possiblePrimitiveType == int.class)
            return Integer.class;
        if (possiblePrimitiveType == long.class)
            return Long.class;
        if (possiblePrimitiveType == char.class)
            return Character.class;
        if (possiblePrimitiveType == float.class)
            return Float.class;
        if (possiblePrimitiveType == double.class)
            return Double.class;

        // should never get here
        return possiblePrimitiveType;
    }
}
