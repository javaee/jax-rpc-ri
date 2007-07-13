/*
 * $Id: GenericObjectSerializer.java,v 1.3 2007-07-13 23:35:57 ofung Exp $
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

import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.modeler.rmi.SOAPSimpleTypeCreatorBase;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.util.JAXRPCClassFactory;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.encoding.literal.GenericLiteralObjectSerializer;

/**
 * A data-driven (de)serializer.
 *
 * @author JAX-RPC Development Team
 */

public class GenericObjectSerializer
    extends ObjectSerializerBase
    implements Initializable {
    protected Class targetClass = null;
    protected List members = new ArrayList();
    protected Map xmlToJavaType = new HashMap();
    protected Map javaToXmlType = new HashMap();
    protected InternalTypeMappingRegistry registry;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants =
        null;

    private void init(SOAPVersion ver) {
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public GenericObjectSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {

        this(type, encodeType, isNullable, encodingStyle, SOAPVersion.SOAP_11);
    }

    public GenericObjectSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        SOAPVersion ver) {

        super(type, encodeType, isNullable, encodingStyle);
        init(ver); // Initialize SOAP constants
        SOAPSimpleTypeCreatorBase typeCreator =
            JAXRPCClassFactory.newInstance().createSOAPSimpleTypeCreator(
                false,
                ver);
        typeCreator.initializeJavaToXmlTypeMap(javaToXmlType);
    }

    public interface GetterMethod {
        public Object get(Object target) throws Exception;
    }

    public interface SetterMethod {
        public void set(Object target, Object value) throws Exception;
    }
    public static class MemberInfo {
        QName name = null;
        QName xmlType = null;
        Class javaType = null;
        JAXRPCSerializer serializer = null;
        JAXRPCDeserializer deserializer = null;
        GetterMethod getter = null;
        SetterMethod setter = null;
    }

    public void addTypeRelation(Class javaType, QName xmlType) {
        if (javaType == null || xmlType == null) {
            throw new IllegalArgumentException("Neither javaType nor xmlType may be null");
        }

        javaToXmlType.put(javaType, xmlType);
        xmlToJavaType.put(xmlType, javaType);
    }

    public void setTargetClass(Class targetClass) {
        clearMembers();
        doSetTargetClass(targetClass);
        this.targetClass = targetClass;
    }

    protected void doSetTargetClass(Class targetClass) {
        // default is to do nothing and let the members be set by hand
    }

    public void initialize(InternalTypeMappingRegistry registry)
        throws Exception {

        this.registry = registry;

        Iterator eachMember = members.iterator();

        while (eachMember.hasNext()) {
            MemberInfo currentMember = (MemberInfo) eachMember.next();
            currentMember.serializer =
                (JAXRPCSerializer) registry.getSerializer(
                    encodingStyle,
                    currentMember.javaType,
                    currentMember.xmlType);
            currentMember.deserializer =
                (JAXRPCDeserializer) registry.getDeserializer(
                    encodingStyle,
                    currentMember.javaType,
                    currentMember.xmlType);
        }
    }

    public void clearMembers() {
        members.clear();
    }

    public void addMember(MemberInfo member) throws Exception {
        Iterator eachMember = members.iterator();

        while (eachMember.hasNext()) {
            MemberInfo existingMember = (MemberInfo) eachMember.next();
            if (existingMember.name.equals(member.name)) {
                throw new EncodingException(
                    "soap.duplicate.data.member",
                    new Object[] { member.name });
            }
        }

        if (member.xmlType == null) {
            member.xmlType = (QName) javaToXmlType.get(member.javaType);
        }
        if (member.javaType == null) {
            member.javaType = (Class) xmlToJavaType.get(member.xmlType);
        }
        members.add(member);
    }

    protected void doSerializeInstance(
        Object instance,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        members = getMemberOrder(instance, members);

        for (int i = 0; i < members.size(); ++i) {
            MemberInfo currentMember = (MemberInfo) members.get(i);

            if (currentMember.serializer == null) {
                Class javaType = instance.getClass();
                currentMember.serializer =
                    (JAXRPCSerializer) registry.getSerializer(
                        soapEncodingConstants.getSOAPEncodingNamespace(),
                        javaType,
                        currentMember.xmlType);
            }

            currentMember.serializer.serialize(
                currentMember.getter.get(instance),
                currentMember.name,
                null,
                writer,
                context);
        }
    }

    protected Object doDeserialize(
        SOAPDeserializationState state,
        XMLReader reader,
        SOAPDeserializationContext context)
        throws Exception {

        Object instance = targetClass.newInstance();
        Object member;
        SOAPGenericObjectInstanceBuilder builder = null;
        boolean isComplete = true;

        members = getMemberOrder(instance, members);

        int lastMemberIndex = members.size() - 1;
        int memberIndex;
        for (int memberCount = 0;
            memberCount <= lastMemberIndex;
            ++memberCount) {
            reader.nextElementContent();
            memberIndex = memberCount;
            do {
                MemberInfo currentMember =
                    (MemberInfo) members.get(memberIndex);

                if (reader.getName().equals(currentMember.name)) {

                    if (currentMember.deserializer == null) {
                        QName xmlType =
                            currentMember.xmlType != null
                                ? currentMember.xmlType
                                : SerializerBase.getType(reader);
                        currentMember.deserializer =
                            (JAXRPCDeserializer) registry.getDeserializer(
                                soapEncodingConstants
                                    .getSOAPEncodingNamespace(),
                                currentMember.javaType,
                                xmlType);
                    }

                    member =
                        currentMember.deserializer.deserialize(
                            currentMember.name,
                            reader,
                            context);

                    if (member instanceof SOAPDeserializationState) {
                        if (builder == null) {
                            builder =
                                new SOAPGenericObjectInstanceBuilder(instance);
                        }
                        state =
                            registerWithMemberState(
                                instance,
                                state,
                                member,
                                memberIndex,
                                builder);
                        isComplete = false;
                    } else {
                        currentMember.setter.set(instance, member);
                    }
                    break;
                }

                if (memberIndex == lastMemberIndex) {
                    memberIndex = 0;
                } else {
                    ++memberIndex;
                }
            } while (memberIndex != memberCount);
        }

        return (isComplete ? (Object) instance : (Object) state);
    }

    //will not work for inherited classes    -- makeing work for inherited classes
       protected List getMemberOrder(Object instance, List membersAlphabetically) {
           //okay reflect object to see if we can get member order
           if (membersAlphabetically == null)
               return null;

           List members = new ArrayList();

           Class targetClass = instance.getClass();

           int introspectedSize = membersAlphabetically.size();

           Field[] fields = targetClass.getDeclaredFields();
           int flen = fields != null ? fields.length : 0;

           ArrayList fieldList = new ArrayList(Arrays.asList(fields));

           if (flen < introspectedSize) {
               //must have superclass fields
               Class superClass = targetClass.getSuperclass();
               while (superClass != null) {
                   //get the superClass fields
                   ArrayList superList = null;
                   Field[] superFields = superClass.getDeclaredFields();
                   if (superFields != null) {
                       superList = new ArrayList(Arrays.asList(superFields));
                       //now add these fields to the begining of the field List
                       for (int i = 0; i < superList.size(); i++) {
                           Object obj = superList.get(i);
                           fieldList.add(i, obj);
                       }
                   }
                   superClass = superClass.getSuperclass();
               }
           }

           fields = null; //clear it

           fields = (Field[]) fieldList.toArray(new Field[fieldList.size()]);
           for (int i = 0; i < fields.length; i++) {
               Field field = fields[i];

               int fieldModifiers = field.getModifiers();
               //if (!Modifier.isPublic(fieldModifiers)) {
               //    continue;
               //}
               if (Modifier.isTransient(fieldModifiers)) {
                   continue;
               }
               if (Modifier.isFinal(fieldModifiers)) {
                   continue;
               }
               // Class type = field.getType();
               String name = field.getName();
               members.add(name);
           } //end for

           if (members.size() == 0) {
               return membersAlphabetically;
           }
           List orderedMembers = new ArrayList();
           for (int i = 0; i < members.size(); i++) {
               String name = (String) members.get(i);
               for (int j = 0; j < membersAlphabetically.size(); j++) {
                   GenericObjectSerializer.MemberInfo info =
                           (GenericObjectSerializer.MemberInfo) membersAlphabetically.get(j);
                   if (name.equalsIgnoreCase(info.name.getLocalPart())) {
                       //is the name case correct?
                       if (!name.equals(info.name.getLocalPart())) {
                           //if the case isn't correct fix it
                           String ns = info.name.getNamespaceURI();
                           info.name = new QName(ns, name);
                           //xmlType for this is the name
                           info.xmlType = new QName(ns, name);
                       }
                       orderedMembers.add(info);
                   }
               }
           }


           if (orderedMembers.size() == 0)
               return membersAlphabetically;
           return orderedMembers;
       }


    protected class SOAPGenericObjectInstanceBuilder
        implements SOAPInstanceBuilder {

        Object instance;

        SOAPGenericObjectInstanceBuilder(Object instance) {
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
                ((MemberInfo) members.get(index)).setter.set(
                    instance,
                    memberValue);
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
            instance = (Object) instance;
        }

        public Object getInstance() {
            return instance;
        }
    }
}
