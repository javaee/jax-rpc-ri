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

package com.sun.xml.rpc.encoding.literal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.client.dii.ParameterMemberInfo;
import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.DynamicInternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.EncodingException;
import com.sun.xml.rpc.encoding.Initializable;
import com.sun.xml.rpc.encoding.InternalTypeMappingRegistry;
import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.SOAPDeserializationContext;
import com.sun.xml.rpc.encoding.SOAPDeserializationState;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.encoding.SOAPSerializationContext;
import com.sun.xml.rpc.encoding.SerializerBase;
import com.sun.xml.rpc.processor.modeler.rmi.LiteralSimpleTypeCreator;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLReaderUtil;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * A data-driven (de)serializer.
 *
 * @author JAX-RPC RI Development Team
 */

public class GenericLiteralObjectSerializer
    extends LiteralObjectSerializerBase
    implements Initializable {
    protected Class targetClass = null;
    protected List members = new ArrayList();
    protected Map xmlToJavaType = new HashMap();
    protected Map javaToXmlType = new HashMap();
    protected InternalTypeMappingRegistry registry;
    protected Collection memberOrder;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants = null;

    private void init(SOAPVersion ver) {
        soapEncodingConstants = SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public GenericLiteralObjectSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle) {
        this(type, encodeType, isNullable, encodingStyle, SOAPVersion.SOAP_11);
    }

    public GenericLiteralObjectSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        SOAPVersion ver) {
        super(type, isNullable, encodingStyle, encodeType);
        init(ver); // Initialize SOAP constants

        LiteralSimpleTypeCreator typeCreator = new LiteralSimpleTypeCreator();
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


        ParameterMemberInfo[] pmemberInfos =
            ((DynamicInternalTypeMappingRegistry) registry).getDynamicRegistryMembers(targetClass,
                                                                                      type, "");

        int msize = members.size();
        int pmsize = 0;
        if (pmemberInfos != null) {
            pmsize = pmemberInfos.length;
            members = orderCurrentMembers(members, pmemberInfos);
        }


        Iterator eachMember = members.iterator();
        //int i = 0;
        // while (eachMember.hasNext()) {
        for (int i = 0; i < members.size(); i++) {
            MemberInfo currentMember = (MemberInfo) members.get(i);
            Class pmJavaClass = null;
            QName pmXmlType = null;
            String pmName = null;

            if (i < pmsize) {
                ParameterMemberInfo pmInfo = (ParameterMemberInfo) pmemberInfos[i];
                pmJavaClass = pmInfo.getMemberJavaClass();
                pmXmlType = pmInfo.getMemberXmlType();
                pmName = pmInfo.getMemberName();
                /*if (members.size() == pmsize) {
                    if (pmXmlType != null)
                        currentMember.xmlType = pmXmlType;
                    if (pmJavaClass != null)
                        currentMember.javaType = pmJavaClass;
                    if (pmName != null)
                        currentMember.name = new QName("", pmName);

                } */
            }

            //todo:?? why not set ?? model propagation?? for int[] array need to make sure xmlType is set -rpclit
            if ((currentMember.xmlType == null) && (currentMember.javaType.isArray()))
                currentMember.xmlType = ((currentMember.name != null) ? currentMember.name : currentMember.xmlType);

            //for docLit allObjectType
            //if (currentMember.xmlType == null)
            //    currentMember.xmlType = currentMember.name;

            if ((currentMember.javaType == pmJavaClass) &&
                    (currentMember.name.getLocalPart().equalsIgnoreCase(pmName))){
                if (pmXmlType != null)
                    currentMember.xmlType = pmXmlType;

                if (pmName != null)
                    currentMember.name = new QName("", pmName);

            }

            //this will make sure the parameters are in the corrent order


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
                    new Object[]{member.name});
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


    protected void doSerialize(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        doSerializeInstance(obj, writer, context);
    }

    protected void doSerializeAttributes(Object obj, XMLWriter writer, SOAPSerializationContext context) throws Exception {
    }

    protected Object doDeserialize(XMLReader reader, SOAPDeserializationContext context) throws Exception {
        return doDeserialize(null, reader, context);
    }

    protected void doSerializeInstance(
        Object instance,
        XMLWriter writer,
        SOAPSerializationContext context)
        throws Exception {

        ParameterMemberInfo[] pmemberInfos =
            ((DynamicInternalTypeMappingRegistry) registry).getDynamicRegistryMembers(targetClass,
                                                                                      type, "");

        int msize = members.size();
        int pmsize = 0;
        if (pmemberInfos != null)
            pmsize = pmemberInfos.length;

        if ((memberOrder == null) || (memberOrder.size() == 0))
            members = getMemberOrder(instance, members);
        else
            members = checkFieldCase(members);

        for (int i = 0; i < members.size(); i++) {
            MemberInfo currentMember = (MemberInfo) members.get(i);

            Class pmJavaClass = null;
            QName pmXmlType = null;
            String pmName = null;

            if (i < pmsize) {
                ParameterMemberInfo pmInfo = (ParameterMemberInfo) pmemberInfos[i];
                pmJavaClass = pmInfo.getMemberJavaClass();
                pmXmlType = pmInfo.getMemberXmlType();
                pmName = pmInfo.getMemberName();
            }

            if ((currentMember.javaType == pmJavaClass) &&
                    (currentMember.name.getLocalPart().equalsIgnoreCase(pmName))){
                if (pmXmlType != null)
                    currentMember.xmlType = pmXmlType;
                //should look at the namespaceURI
                currentMember.name = new QName("", pmName);

            }

            if (currentMember.serializer == null) {
                Class javaType = instance.getClass();
                currentMember.serializer =
                    (JAXRPCSerializer) registry.getSerializer(
                        encodingStyle,
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
        ParameterMemberInfo[] pmemberInfos =
            ((DynamicInternalTypeMappingRegistry) registry).getDynamicRegistryMembers(targetClass,
                                                                                      type, "");

        int msize = members.size();
        int pmsize = 0;
        if (pmemberInfos != null)
            pmsize = pmemberInfos.length;


        if ((memberOrder == null) || (memberOrder.size() == 0))
            members = getMemberOrder(instance, members);
        else
            members = checkFieldCase(members);

        Object member;
        SOAPGenericObjectInstanceBuilder builder = null;
        boolean isComplete = true;

        int lastMemberIndex = members.size();
        int memberIndex;
        ///reader.type
        reader.nextElementContent();
        for (int memberCount = 0;
             memberCount < lastMemberIndex;
             memberCount++) {
            //do I need this here
            //want to be in start state here
            memberIndex = memberCount;

            do {
                Class pmJavaClass = null;
                QName pmXmlType = null;
                String pmName = null;

                if (memberIndex < pmsize) {
                    ParameterMemberInfo pmInfo = (ParameterMemberInfo) pmemberInfos[memberIndex];
                    pmJavaClass = pmInfo.getMemberJavaClass();
                    pmXmlType = pmInfo.getMemberXmlType();
                    pmName = pmInfo.getMemberName();
                }

                QName elementName = reader.getName();
                MemberInfo currentMember =
                    (MemberInfo) members.get(memberIndex);

                if ((currentMember.javaType == pmJavaClass)&&
                (currentMember.name.getLocalPart().equalsIgnoreCase(pmName))){
                    if (pmXmlType != null)
                        currentMember.xmlType = pmXmlType;
                    if (pmName != null)                   //should look at the namespaceURI
                        currentMember.name = new QName("", pmName);
                }
                //int i = 0;



                //this is a workaround for a bug - sankar dii test
                int i = 0;
                while ((reader.getState() == XMLReader.END) && (i < lastMemberIndex)) {
                    //next is member
                    reader.nextElementContent();
                }

                if (reader.getState() == XMLReader.START) {

                    ////todo: model propagation issue -kw put in for problem with QNames of literal array members
                    if ((currentMember.serializer != null) && (currentMember.javaType.isArray()))
                        currentMember.name = ((LiteralObjectSerializerBase) currentMember.serializer).getXmlType();

                    if (reader.getName().equals(currentMember.name)) {

                        if (currentMember.deserializer == null) {
                            QName xmlType = currentMember.xmlType != null ? currentMember.xmlType : SerializerBase.getType(reader);
                            currentMember.deserializer =
                                (JAXRPCDeserializer) registry.getDeserializer(
                                    encodingStyle,
                                    currentMember.javaType,
                                    currentMember.xmlType);//currentMember.name ??
                        }

                        member =
                            currentMember.deserializer.deserialize(
                                currentMember.name,
                                reader,
                                context);

                        //work on this serializer tomorrow
                        /* if ( (reader.getState()==XMLReader.END)&&
                                 (memberIndex < lastMemberIndex) &&
                                 !currentMember.name.equals(reader.getName()) ){
                              reader.nextElementContent();
                         }
                         */
                        //System.out.println("Reader.getName() " + reader.getName().getLocalPart());

                        //reader.nextElementContent();
                        //todo: take out
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
                        memberIndex++;
                    }
                } //is this where it should be?

            } while (memberIndex < memberCount);

        }

        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (isComplete ? (Object) instance : (Object) state);
    }

    protected void verifyType(XMLReader reader) throws Exception {
        QName actualType = getType(reader);
        //dii has no way to know about multiple namespaces - just use localPart
        if (actualType != null) {
            /*if (!actualType.getLocalPart().equalsIgnoreCase(type.getLocalPart())){ //&&
                //!isAcceptableType(actualType)) {
                throw new DeserializationException("xsd.unexpectedElementType",
                                                        new Object[] {
                                                            type.toString(),
                                                            actualType.toString()
                                                        });
            }
            */
        }

    }

    private List checkFieldCase(List members) {

        Iterator iter = memberOrder.iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            for (int i = 0; i < members.size(); i++) {

                MemberInfo info = (MemberInfo) members.get(i);
                if (name.equalsIgnoreCase(info.name.getLocalPart())) {
                    //is the name case correct?
                    if (!name.equals(info.name.getLocalPart())) {
                        //if the case isn't correct fix it
                        String ns = info.name.getNamespaceURI();
                        info.name = new QName(ns, name);
                        //xmlType for this is the name
                        info.xmlType = new QName(ns, name);
                    }
                }
            }
        }
        return members;
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
                MemberInfo info = (MemberInfo) membersAlphabetically.get(j);
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

    private List orderCurrentMembers(List members, ParameterMemberInfo[] pmemberInfos) {

        ArrayList newMembers = new ArrayList();

        MemberInfo[] memberInfos =
            (MemberInfo[]) members.toArray(new MemberInfo[members.size()]);

        /*pmJavaClass = pmInfo.getMemberJavaClass();
                    pmXmlType = pmInfo.getMemberXmlType();
                    pmName = pmInfo.getMemberName();
          */
        if (memberInfos.length != pmemberInfos.length)
            return members;


        for (int i = 0; i < pmemberInfos.length; i++) {
            ParameterMemberInfo pminfo = pmemberInfos[i];
            Class pmJavaClass = pminfo.getMemberJavaClass();
            QName pmXmlType = pminfo.getMemberXmlType();
            String pmName = pminfo.getMemberName();
            for (int j = 0; j < memberInfos.length; j++) {
                MemberInfo minfo = memberInfos[j];


                if (minfo.javaType == pmJavaClass) {
                    //check xmlType?
                    if (minfo.name.getLocalPart().equalsIgnoreCase(pmName)) {
                        minfo.xmlType = pmXmlType;
                        newMembers.add(minfo);
                        break;
                    }
                }


            }

        }
        if (newMembers.size() != members.size())
            return members;
        return newMembers;
    }


    //todo:take this out
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
