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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * Serializes and Deserializes JavaBeans/Data Objects. Uses a combinatin of
 * reflection and introspection to determine how to get and set values into
 * and out of the object.
 *
 * @author JAX-RPC Development Team
 */

public class ValueTypeSerializer extends GenericObjectSerializer {
    protected String memberNamespace = null;

    public ValueTypeSerializer(
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

    public ValueTypeSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        Class targetClass) {

        this(type, encodeType, isNullable, encodingStyle);

        super.setTargetClass(targetClass);
    }

    public ValueTypeSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        Class targetClass,
        String memberNamespace) {

        this(type, encodeType, isNullable, encodingStyle, targetClass);

        this.memberNamespace = memberNamespace;
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
        for (int i = 0; i < targetProperties.length; ++i) {
            final Method getterMethod = targetProperties[i].getReadMethod();
            final Method setterMethod = targetProperties[i].getWriteMethod();

            if (getterMethod == null || setterMethod == null) {
                continue;
            }
            MemberInfo member = new MemberInfo();

            member.name =
                new QName(memberNamespace, targetProperties[i].getName());
            Class baseJavaType = targetProperties[i].getPropertyType();
            member.javaType = getBoxedClassFor(baseJavaType);
            member.xmlType = (QName) javaToXmlType.get(baseJavaType);

            member.getter = new GetterMethod() {
                public Object get(Object instance) throws Exception {
                    return getterMethod.invoke(instance, new Object[0]);
                }
            };
            member.setter = new SetterMethod() {
                public void set(Object instance, Object value)
                    throws Exception {
                    setterMethod.invoke(instance, new Object[] { value });
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
