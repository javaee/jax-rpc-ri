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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.processor.config.TypeMappingRegistryInfo;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.util.ClassNameInfo;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralTypeModeler implements RmiConstants {
    public LiteralElementMember modelTypeLiteral(
        QName elemName,
        String typeUri,
        RmiType type) {
            
        return modelTypeLiteral(elemName, typeUri, type, false, false);
    }

    public LiteralElementMember modelTypeLiteral(
        QName elemName,
        String typeUri,
        RmiType type,
        boolean topLevel,
        boolean allowHolders) {
            
        String tmp = (String) unsupportedClasses.get(type.toString());
        if (tmp != null)
            if (tmp.equals(OBJECT)) {
                throw new ModelerException("rmimodeler.object.is.not.supported");
            } else {
                throw new ModelerException(
                    "rmimodeler.type.is.not.supported",
                    new Object[] { tmp, type.toString()});
            }
        // we might have mapped 2 different types for arrays depending on if 
        // it is a top level array or not.
        LiteralType typeNode =
            getMappedLiteralType(
                type,
                topLevel && type.getTypeCode() == TC_ARRAY);
        if (typeNode != null) {
            LiteralElementMember elemMember =
                new LiteralElementMember(elemName, typeNode);
            JavaStructureMember javaMember =
                new JavaStructureMember(
                    elemMember.getName().getLocalPart(),
                    typeNode.getJavaType(),
                    elemMember,
                    false);
            env.getNames().setJavaStructureMemberMethodNames(javaMember);
            elemMember.setJavaStructureMember(javaMember);
            elemMember.setRepeated(
                !topLevel
                    && type.getTypeCode() == TC_ARRAY
                    && !(typeNode
                        .equals(literalTypes.XSD_BYTE_ARRAY_LITERALTYPE)));
            // but not byte[]s
            elemMember.setNillable(!topLevel && type.isNillable());
            elemMember.setRequired(topLevel);
            return elemMember;
        }
        int typeCode = type.getTypeCode();
        String packageName = ClassNameInfo.getQualifier(type.toString());
        String namespaceURI = modeler.getNamespaceURI(packageName);
        if (namespaceURI == null)
            namespaceURI = typeUri;
        switch (typeCode) {
            case TC_VOID :
                return null;
            case TC_ARRAY :
                RmiType elemRmiType = type.getElementType();
                LiteralElementMember elemMember = null;
                // handle multi-dimensional arrays, except byte[]s
                if (elemRmiType.getTypeCode() == TC_ARRAY
                    && !(BYTE_ARRAY_CLASSNAME.equals(elemRmiType.toString()))) {
                    elemMember =
                        modelTypeLiteral(
                            elemName,
                            typeUri,
                            elemRmiType,
                            true,
                            false);
                } else {
                    elemMember =
                        modelTypeLiteral(elemName, typeUri, elemRmiType);
                }
                String fixedupClassName = type.typeString(false);
                JavaArrayType javaArType =
                    new JavaArrayType(
                        fixedupClassName,
                        InternalEncodingConstants
                            .ARRAY_ELEMENT_NAME
                            .getLocalPart(),
                        elemMember.getJavaStructureMember().getType());
                JavaStructureMember javaMember =
                    new JavaStructureMember(
                        elemMember.getName().getLocalPart(),
                        javaArType,
                        elemMember,
                        false);
                env.getNames().setJavaStructureMemberMethodNames(javaMember);
                elemMember.setJavaStructureMember(javaMember);
                elemMember.setRepeated(true);
                elemMember.setNillable(
                    !topLevel && elemMember.getType().isNillable());
                elemMember.setRequired(topLevel);

                if (topLevel) {
                    // we need to create a wrapper for the array
                    // usually only done for rpc/lit
                    String servicePackage = modeler.getServicePackage();
                    if (servicePackage != null
                        && servicePackage.length() > 0) {
                        servicePackage = servicePackage + DOTC;
                    } else {
                        servicePackage = "";
                    }
                    servicePackage = servicePackage + "_arrays";

                    elemMember.setNillable(elemMember.getType().isNillable());
                    elemMember.setRequired(false);
                    LiteralElementMember topMember = new LiteralElementMember();
                    topMember.setName(elemName);
                    topMember.setNillable(false);
                    topMember.setRequired(true);
                    LiteralArrayWrapperType seqType =
                        new LiteralArrayWrapperType();
                    String tmpNamespace = typeUri;
                    String elemPackage =
                        Names.getPackageName(
                            elemMember.getType().getJavaType().getName());
                    if (elemPackage.startsWith(servicePackage)) {
                        tmpNamespace =
                            elemMember.getType().getName().getNamespaceURI();
                    } else if (!elemPackage.equals(servicePackage)) {
                        tmpNamespace =
                            Names.getAdjustedURI(
                                tmpNamespace,
                                "arrays/" + elemPackage.replace('.', '/'));
                    }
                    seqType.setName(
                        new QName(
                            tmpNamespace,
                            generateSchemaNameForArrayWrapper(
                                elemRmiType,
                                env)));
                    String arraysPackage = servicePackage + DOTC;
                    String fixedupMemberClassName;
                    JavaStructureMember jsMember;
                    if (elemRmiType.getTypeCode() == TC_ARRAY) {
                        String str;

                        // handle byte[]s
                        if (elemMember.getType()
                            instanceof LiteralSimpleType) {
                            str =
                                arraysPackage
                                    + env.getNames().validJavaClassName(
                                        elemRmiType.toString());
                        } else {
                            str = elemMember.getType().getJavaType().getName();
                        }
                        fixedupMemberClassName = str + ARRAY_STR;
                    } else if (elemRmiType.getTypeCode() == TC_CLASS) {
                        fixedupMemberClassName =
                            arraysPackage
                                + elemRmiType.getClassName()
                                + ARRAY_STR;
                    } else {
                        fixedupMemberClassName =
                            arraysPackage
                                + env.getNames().validJavaClassName(
                                    elemRmiType.toString() + ARRAY_STR);
                    }
                    fixedupMemberClassName =
                        fixedupMemberClassName.replace(SIGC_INNERCLASS, '_');
                    JavaStructureType javaStruct =
                        new JavaStructureType(
                            fixedupMemberClassName,
                            false,
                            seqType);
                    jsMember =
                        new JavaStructureMember(
                            elemName.getLocalPart(),
                            javaStruct,
                            topMember);
                    env.getNames().setJavaStructureMemberMethodNames(jsMember);
                    seqType.setJavaType(javaStruct);
                    seqType.add(elemMember);
                    javaMember.setName("value");
                    env.getNames().setJavaStructureMemberMethodNames(
                        javaMember);

                    if (elemRmiType.getTypeCode() == TC_ARRAY
                        && // not a byte[]
                        !(elemMember.getType()
                                instanceof LiteralSimpleType)) {
                        String memName =
                            env.getNames().getClassMemberName(
                                elemMember
                                    .getType()
                                    .getJavaType()
                                    .getRealName());
                        JavaArrayType ja2 =
                            new JavaArrayType(
                                elemMember.getType().getJavaType().getName()
                                    + BRACKETS,
                                memName,
                                elemMember.getType().getJavaType());
                        JavaStructureMember tmpMember =
                            new JavaStructureMember(memName, ja2, elemMember);
                        env.getNames().setJavaStructureMemberMethodNames(
                            tmpMember);
                        javaStruct.add(tmpMember);
                        // set the JavaArrayType
                        JavaArrayType javaArray =
                            new JavaArrayType(
                                ((LiteralArrayWrapperType) elemMember
                                    .getType())
                                    .getJavaArrayType()
                                    .getName()
                                    + BRACKETS,
                                memName,
                                elemMember.getType().getJavaType());
                        seqType.setJavaArrayType(javaArray);
                    } else {
                        javaStruct.add(elemMember.getJavaStructureMember());
                        seqType.setJavaArrayType(
                            (JavaArrayType) elemMember
                                .getJavaStructureMember()
                                .getType());
                    }
                    elemMember.setName(new QName("value"));
                    topMember.setType(seqType);
                    topMember.setJavaStructureMember(jsMember);
                    javaMember.setOwner(topMember);
                    mapLiteralType(type, topMember.getType(), true);
                    return topMember;

                }
//                // don't map this case, it is mapped by TC_CLASS or primitive
//                mapLiteralType(type, elemMember.getType(), false);
                return elemMember;
            case TC_CLASS :
                // Check if the type is a holder
                if (!allowHolders
                    && getHolderValueType(env, modeler.getDefHolder(), type)
                        != null) {
                    throw new ModelerException(
                        "rmimodeler.no.literal.holders",
                        new Object[] { type.toString()});
                }
                LiteralElementMember holderMember =
                    modelHolder(elemName, modeler, env, typeUri, type);
                if (holderMember != null) {
                    mapLiteralType(type, holderMember.getType());
                    return holderMember;
                }
                QName structName =
                    new QName(
                        namespaceURI,
                        type.typeString(true).replace(SIGC_INNERCLASS, DOTC));
                LiteralSequenceType struct =
                    new LiteralSequenceType(structName);
                struct.setNillable(true);

                // take care of inner class case
                fixedupClassName = type.getClassName();
                addTypeName(structName, fixedupClassName);

                // prevent recursion by putting the LiteralStructuredType into the map
                mapLiteralType(type, struct);

                JavaStructureType javaStruct =
                    new JavaStructureType(fixedupClassName, true, struct);
                struct.setJavaType(javaStruct);

                Map members = RmiStructure.modelTypeSOAP(env, type);
                Map members2 = JavaBean.modelTypeSOAP(env, type);
                if (members.size() != 0 && members2.size() != 0) {
                    Iterator keys = members.keySet().iterator();
                    String key;
                    while (keys.hasNext()) {
                        key = (String) keys.next();
                        if (members2.containsKey(key)) {
                            throw new ModelerException(
                                "rmimodeler.javabean.property.has.public.member",
                                new Object[] { type.toString(), key });
                        }
                    }
                }
                members.putAll(members2);
                if (members.size() == 0) {
                    throw new ModelerException(
                        "rmimodeler.invalid.rmi.type",
                        type.toString());
                }
                Class typeClass;
                try {
                    typeClass = type.getTypeClass(env.getClassLoader());
                    if (typeClass.isInterface()
                        || Modifier.isAbstract(typeClass.getModifiers())) {
                        ((JavaStructureType) struct.getJavaType()).setAbstract(
                            true);
                    }
                } catch (ClassNotFoundException e) {
                    throw new ModelerException(
                        "rmimodeler.class.not.found",
                        type.toString());
                }
                List sortedMembers =
                    RmiTypeModeler.sortMembers(typeClass, members, env);
                fillInStructure(
                    typeUri,
                    struct,
                    javaStruct,
                    sortedMembers,
                    type);
                elemMember = new LiteralElementMember(elemName, struct);
                elemMember.setNillable(!topLevel);
                javaMember =
                    new JavaStructureMember(
                        elemMember.getName().getLocalPart().replace('.', '_'),
                        struct.getJavaType(),
                        elemMember,
                        false);
                env.getNames().setJavaStructureMemberMethodNames(javaMember);
                elemMember.setJavaStructureMember(javaMember);
                return elemMember;
            default :
                throw new ModelerException(
                    "rmimodeler.unexpected.type",
                    type.toString());
        }
    }

    public LiteralSimpleTypeCreator getLiteralTypes() {
        return literalTypes;
    }

    private LiteralElementMember modelHolder(
        QName elemName,
        RmiModeler modeler,
        ProcessorEnvironment env,
        String typeUri,
        RmiType type) {
            
        RmiType holderValueType =
            getHolderValueType(env, modeler.getDefHolder(), type);
        if (holderValueType == null)
            return null;
        LiteralElementMember holderMember =
            modelTypeLiteral(elemName, typeUri, holderValueType, true, true);
        JavaType javaType = holderMember.getType().getJavaType();
        javaType.setHolder(true);
        javaType.setHolderPresent(true);
        javaType.setHolderName(type.toString());
        return holderMember;
    }

    private void modelSubclasses(String typeUri, JavaStructureType type) {
        if (!type.isPresent())
            return;
        try {
            Class typeClass =
                RmiUtils.getClassForName(
                    type.getRealName(),
                    env.getClassLoader());
            Class[] interfaces = typeClass.getInterfaces();
            if (RmiTypeModeler.multipleClasses(interfaces, env)) {
                throw new ModelerException(
                    "rmimodeler.type.implements.more.than.one.interface",
                    new Object[] {
                        type.getRealName(),
                        interfaces[0].getName(),
                        interfaces[1].getName()});
            }
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                "rmimodeler.class.not.found",
                type.getRealName());
        }
        int startSize = complexTypeMap.size();
        int curSize = 0;
        while (curSize != startSize) {
            curSize = startSize;
            Iterator iterator = complexTypeMap.entrySet().iterator();
            LiteralType extraType;
            JavaStructureType javaType;
            while (iterator.hasNext()) {
                extraType = (LiteralType) ((Entry) iterator.next()).getValue();
                if (extraType instanceof LiteralStructuredType) {
                    javaType = (JavaStructureType) extraType.getJavaType();
                    if (!type.equals(javaType)
                        && javaType.isPresent()
                        && isSubclass(
                            javaType.getRealName(),
                            type.getRealName(),
                            env.getClassLoader())) {
                        modelHierarchy(typeUri, javaType, type);
                        curSize = complexTypeMap.size();
                        if (curSize != startSize) {
                            startSize = curSize;
                            curSize = 0;
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean modelHierarchy(
        String typeUri,
        JavaStructureType subclass,
        JavaStructureType superclassType) {
            
        String classSig;
        String superClassName = superclassType.getRealName();
        LiteralStructuredType superLiteralType =
            (LiteralStructuredType) superclassType.getOwner();
        LiteralStructuredType literalType =
            (LiteralStructuredType) subclass.getOwner();
        if (literalType.getParentType() != null) {
            if (literalType.getParentType().equals(superLiteralType)) {
                return true;
            }
            // determine if the parent is a subclass of or implements superClassName
            if (isSubclass(literalType
                .getParentType()
                .getJavaType()
                .getRealName(),
                superClassName,
                env.getClassLoader())) {
                LiteralStructuredType tmpType = literalType;
                while (tmpType.getParentType() != null) {
                    tmpType = tmpType.getParentType();
                    if (tmpType
                        .getJavaType()
                        .getRealName()
                        .equals(superClassName))
                        return true;
                    if (!isSubclass(tmpType.getJavaType().getRealName(),
                        superClassName,
                        env.getClassLoader())) {
                        throw new ModelerException(
                            "rmimodeler.type.is.used.as.two.types",
                            new Object[] {
                                subclass.getRealName(),
                                tmpType.getJavaType().getRealName(),
                                superClassName });
                    }
                }
                return modelHierarchy(
                    typeUri,
                    (JavaStructureType) tmpType.getJavaType(),
                    superclassType);
            } else if (
                !isSubclass(superClassName,
                literalType.getParentType().getJavaType().getRealName(),
                env.getClassLoader())) {
                if (literalType
                    .getParentType()
                    .getJavaType()
                    .getRealName()
                    .equals(superClassName)) {
                    return true;
                }
                throw new ModelerException(
                    "rmimodeler.type.is.used.as.two.types",
                    new Object[] {
                        subclass.getRealName(),
                        literalType.getParentType().getJavaType().getRealName(),
                        superClassName });
            }
        }

        try {
            Class subclassClass =
                RmiUtils.getClassForName(
                    subclass.getRealName(),
                    env.getClassLoader());
            Class superclass = subclassClass.getSuperclass();

            if (superclass == null
                || (!superclass.getName().equals(superClassName)
                    && !isSubclass(superclass.getName(),
                        superClassName,
                        env.getClassLoader()))) {
                Class[] interfaces = subclassClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    if (interfaces[i].getName().equals(superClassName)) {
                        superLiteralType.addSubtype(
                            (LiteralStructuredType) subclass.getOwner());
                        (
                            (JavaStructureType) superLiteralType
                                .getJavaType())
                                .addSubclass(
                            subclass);
                        return true;
                    }
                    if (isSubclass(interfaces[i].getName(),
                        superClassName,
                        env.getClassLoader())) {
                        RmiType type = RmiType.getRmiType(interfaces[i]);
                        LiteralElementMember elemMember =
                            modelTypeLiteral(
                                new QName(
                                    modeler.generateNameFromType(type, env)),
                                typeUri,
                                type);
                        LiteralStructuredType superType =
                            (LiteralStructuredType) elemMember.getType();
                        superType.addSubtype(
                            (LiteralStructuredType) subclass.getOwner());
                        (
                            (JavaStructureType) superType
                                .getJavaType())
                                .addSubclass(
                            subclass);
                        return modelHierarchy(
                            typeUri,
                            (JavaStructureType) superType.getJavaType(),
                            superclassType);
                    }
                }
            }

            RmiType type = RmiType.getRmiType(superclass);
            LiteralElementMember elemMember =
                modelTypeLiteral(
                    new QName(modeler.generateNameFromType(type, env)),
                    typeUri,
                    type);
            LiteralStructuredType superType =
                (LiteralStructuredType) elemMember.getType();
            superType.addSubtype((LiteralStructuredType) subclass.getOwner());
            ((JavaStructureType) superType.getJavaType()).addSubclass(subclass);

            if (!superclass.getName().equals(superClassName)) {
                return modelHierarchy(
                    typeUri,
                    (JavaStructureType) superType.getJavaType(),
                    superclassType);
            }
            return true;
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                "rmimodeler.class.not.found",
                subclass.getRealName());
        }
    }

    public static boolean isSubclass(
        String subtypeName,
        String supertypeName,
        ClassLoader classLoader) {
            
        return RmiTypeModeler.isSubclass(
            subtypeName,
            supertypeName,
            classLoader);
    }

    public static RmiType getHolderValueType(
        ProcessorEnvironment env,
        Class defHolder,
        RmiType type) {
            
        return RmiTypeModeler.getHolderValueType(env, defHolder, type);
    }

    private void fillInStructure(
        String typeUri,
        LiteralStructuredType struct,
        JavaStructureType javaStruct,
        List sortedMembers,
        RmiType type) {

        ProcessorEnvironment env = modeler.getProcessorEnvironment();

        MemberInfo memInfo;
        LiteralElementMember member;
        JavaStructureMember javaMember;
        for (Iterator iter = sortedMembers.iterator(); iter.hasNext();) {
            memInfo = (MemberInfo) iter.next();
            member =
                modelTypeLiteral(
                    new QName(memInfo.getName()),
                    typeUri,
                    memInfo.getType());
            javaMember = member.getJavaStructureMember();
            javaMember.setReadMethod(memInfo.getReadMethod());
            javaMember.setWriteMethod(memInfo.getWriteMethod());
            javaMember.setPublic(memInfo.isPublic());
            if (memInfo.getDeclaringClass() != null) {
                javaMember.setDeclaringClass(
                    memInfo.getDeclaringClass().getName());
            }
            javaStruct.add(javaMember);
            struct.add(member);
        }
        markInheritedMembers(env, struct);
    }

    public static void markInheritedMembers(
        ProcessorEnvironment env,
        LiteralStructuredType struct) {
            
        String className = struct.getJavaType().getRealName();
        try {
            // Element Members
            Iterator members = struct.getElementMembers();
            LiteralElementMember literalMember;
            JavaStructureMember javaMember;
            Class javaClass =
                RmiUtils.getClassForName(className, env.getClassLoader());
            if (javaClass.isInterface()
                || Modifier.isAbstract(javaClass.getModifiers())) {
                ((JavaStructureType) struct.getJavaType()).setAbstract(true);
            }
            Class superclass = javaClass.getSuperclass();
            while (members.hasNext()) {
                literalMember = (LiteralElementMember) members.next();
                javaMember = literalMember.getJavaStructureMember();
                if (javaMember.isPublic()) {
                    try {
                        Field field =
                            javaClass.getDeclaredField(javaMember.getName());
                        if (!field.getDeclaringClass().equals(javaClass)) {
                            javaMember.setInherited(true);
                            literalMember.setInherited(true);
                        }
                    } catch (NoSuchFieldException e) {
                        javaMember.setInherited(true);
                        literalMember.setInherited(true);
                    }
                } else {
                    String methodName;
                    methodName = javaMember.getReadMethod();
                    Class[] args = new Class[0];
                    boolean isInherited =
                        RmiTypeModeler.isMethodInherited(
                            methodName,
                            args,
                            javaClass);
                    methodName = javaMember.getWriteMethod();
                    if (methodName != null) {
                        isInherited =
                            isInherited
                                ? RmiTypeModeler.isMethodInherited(
                                    methodName,
                                    args,
                                    javaClass)
                                : false;
                    }
                    if (isInherited) {
                        javaMember.setInherited(true);
                        literalMember.setInherited(true);
                    }
                }
            }

            // Attribute Members
            members = struct.getAttributeMembers();
            LiteralAttributeMember attributeMember;
            while (members.hasNext()) {
                attributeMember = (LiteralAttributeMember) members.next();
                javaMember = attributeMember.getJavaStructureMember();
                if (javaMember.isPublic()) {
                    try {
                        Field field =
                            javaClass.getDeclaredField(javaMember.getName());
                        if (!field.getDeclaringClass().equals(javaClass)) {
                            javaMember.setInherited(true);
                            attributeMember.setInherited(true);
                        }
                    } catch (NoSuchFieldException e) {
                        javaMember.setInherited(true);
                        attributeMember.setInherited(true);
                    }
                } else {
                    String methodName;
                    methodName = javaMember.getReadMethod();
                    Class[] args = new Class[0];
                    boolean isInherited =
                        RmiTypeModeler.isMethodInherited(
                            methodName,
                            args,
                            javaClass);
                    methodName = javaMember.getWriteMethod();
                    if (methodName != null) {
                        isInherited =
                            isInherited
                                ? RmiTypeModeler.isMethodInherited(
                                    methodName,
                                    args,
                                    javaClass)
                                : false;
                    }
                    if (isInherited) {
                        javaMember.setInherited(true);
                        attributeMember.setInherited(true);
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            throw new ModelerException("rmimodeler.class.not.found", className);
        }
    }

    public void modelSubclasses(String typeUri) {
        Set abstractTypes = new HashSet();
        int startSize = complexTypeMap.size();
        Iterator iter = complexTypeMap.entrySet().iterator();
        LiteralType type;
        while (iter.hasNext()) {
            type = (LiteralType) ((Entry) iter.next()).getValue();
            if (type instanceof LiteralStructuredType) { // &&
                //                ((JavaStructureType)type.getJavaType()).isAbstract()) {
                abstractTypes.add(type);
            }
        }
        iter = abstractTypes.iterator();
        while (iter.hasNext()) {
            type = (LiteralType) iter.next();
            modelSubclasses(typeUri, (JavaStructureType) type.getJavaType());
        }
        if (startSize != complexTypeMap.size()) {
            iter = complexTypeMap.entrySet().iterator();
            while (iter.hasNext()) {
                type = (LiteralType) ((Entry) iter.next()).getValue();
                if (type instanceof LiteralStructuredType
                    && ((JavaStructureType) type.getJavaType()).isAbstract()
                    && !abstractTypes.contains(type)) {

                    modelSubclasses(
                        typeUri,
                        (JavaStructureType) type.getJavaType());
                }
            }
        }
    }

    public boolean nameClashes(String name) {
        name = name.toUpperCase();

        Iterator iter = complexTypeMap.values().iterator();
        LiteralType type;
        String typeName;
        while (iter.hasNext()) {
            type = (LiteralType) iter.next();
            typeName =
                ClassNameInfo
                    .getName(type.getJavaType().getRealName())
                    .toUpperCase();
            if (typeName.equals(name))
                return true;
        }

        return false;
    }

    /**
     * returns the LiteralType for a mapped type, null if the
     * type is not in the map
     */
    private LiteralType getMappedLiteralType(RmiType type, boolean topLevel) {
        LiteralType retType = null;
        String name = type.toString();
        retType = (LiteralType) simpleTypeMap.get(name);
        if (retType == null)
            retType = (LiteralType) complexTypeMap.get(name + topLevel);
        return retType;
    }

    private void mapLiteralType(RmiType type, LiteralType literalType) {
        mapLiteralType(type, literalType, false);
    }

    private void mapLiteralType(
        RmiType type,
        LiteralType literalType,
        boolean topLevel) {
            
        mapLiteralType(type.toString() + topLevel, literalType);
    }

    private void mapLiteralType(String name, LiteralType literalType) {
        complexTypeMap.put(name, literalType);
    }

    private void addTypeName(QName typeName, String javaType) {
        if (typeNames.contains(typeName)) {
            throw new ModelerException(
                "rmimodeler.duplicate.type.name",
                new Object[] { typeName.toString(), javaType });
        }
        typeNames.add(typeName);
    }

    public String generateSchemaNameForArrayWrapper(
        RmiType type,
        ProcessorEnvironment env) {
        int typeCode = type.getTypeCode();
        String base;
        switch (typeCode) {
            case TC_BOOLEAN :
            case TC_BYTE :
            case TC_CHAR :
            case TC_SHORT :
            case TC_INT :
            case TC_LONG :
            case TC_FLOAT :
            case TC_DOUBLE :
                base = type.toString();
                break;
            case TC_ARRAY :
                base =
                    generateSchemaNameForArrayWrapper(
                        type.getElementType(),
                        env);
                break;
            case TC_CLASS :
                RmiType holderValueType =
                    RmiTypeModeler.getHolderValueType(
                        env,
                        modeler.getDefHolder(),
                        type);
                if (holderValueType != null) {
                    return generateSchemaNameForArrayWrapper(
                        holderValueType,
                        env);
                }
                String tmp = ClassNameInfo.getName(type.getClassName());
                base = ClassNameInfo.replaceInnerClassSym(tmp);
                break;
            default :
                throw new Error("unexpected type code: " + typeCode);
        }
        return base + ARRAY_STR;
    }

    private static void log(ProcessorEnvironment env, String msg) {
        if (env.verbose()) {
            System.out.println("[RmiTypeModeler: " + msg + "]");
        }
    }

    public void initializeTypeMap(Map typeMap) {
        literalTypes.initializeTypeMap(typeMap);
    }

    private LiteralTypeModeler() {
        typeNames = new HashSet();
        simpleTypeMap = new HashMap();
        complexTypeMap = new HashMap();
        literalTypes = new LiteralSimpleTypeCreator();
        initializeTypeMap(simpleTypeMap);
    }

    LiteralTypeModeler(RmiModeler modeler, ProcessorEnvironment env) {
        this.modeler = modeler;
        this.env = env;
        typeMappingRegistry = modeler.getTypeMappingRegistryInfo();
        typeNames = new HashSet();
        simpleTypeMap = new HashMap();
        complexTypeMap = new HashMap();
        literalTypes = new LiteralSimpleTypeCreator();
        initializeTypeMap(simpleTypeMap);
    }
    // Java to WSDL type map
    private Map simpleTypeMap;
    private Map complexTypeMap;
    private Set typeNames = new HashSet();
    private RmiModeler modeler;
    private ProcessorEnvironment env;
    private TypeMappingRegistryInfo typeMappingRegistry;
    private LiteralSimpleTypeCreator literalTypes;

    private static final Set boxedPrimitiveSet = new HashSet();
    private static final Map unsupportedClasses = new HashMap();
    private static final String COLLECTION = "Collection";
    private static final String ATTACHMENT = "Attachment";
    private static final String OBJECT = "Object";

    static {
        boxedPrimitiveSet.add(BOXED_BOOLEAN_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_BYTE_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_DOUBLE_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_FLOAT_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_INTEGER_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_LONG_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_SHORT_CLASSNAME);

        unsupportedClasses.put(OBJECT_CLASSNAME, null);
        unsupportedClasses.put(IMAGE_CLASSNAME, ATTACHMENT);
        unsupportedClasses.put(MIME_MULTIPART_CLASSNAME, ATTACHMENT);
        unsupportedClasses.put(SOURCE_CLASSNAME, ATTACHMENT);
        unsupportedClasses.put(DATA_HANDLER_CLASSNAME, ATTACHMENT);

        // Collections
        unsupportedClasses.put(COLLECTION_CLASSNAME, COLLECTION);
        unsupportedClasses.put(LIST_CLASSNAME, COLLECTION);
        unsupportedClasses.put(SET_CLASSNAME, COLLECTION);
        unsupportedClasses.put(VECTOR_CLASSNAME, COLLECTION);
        unsupportedClasses.put(STACK_CLASSNAME, COLLECTION);
        unsupportedClasses.put(LINKED_LIST_CLASSNAME, COLLECTION);
        unsupportedClasses.put(ARRAY_LIST_CLASSNAME, COLLECTION);
        unsupportedClasses.put(HASH_SET_CLASSNAME, COLLECTION);
        unsupportedClasses.put(TREE_SET_CLASSNAME, COLLECTION);

        // Maps
        unsupportedClasses.put(MAP_CLASSNAME, COLLECTION);
        unsupportedClasses.put(HASH_MAP_CLASSNAME, COLLECTION);
        unsupportedClasses.put(TREE_MAP_CLASSNAME, COLLECTION);
        unsupportedClasses.put(HASHTABLE_CLASSNAME, COLLECTION);
        unsupportedClasses.put(PROPERTIES_CLASSNAME, COLLECTION);
        //    unsupportedClasses.put(WEAK_HASH_MAP_CLASSNAME, COLLECTION);
        unsupportedClasses.put(JAX_RPC_MAP_ENTRY_CLASSNAME, COLLECTION);
    }

}
