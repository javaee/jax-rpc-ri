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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.processor.ProcessorOptions;
import com.sun.xml.rpc.processor.config.TypeMappingInfo;
import com.sun.xml.rpc.processor.config.TypeMappingRegistryInfo;
import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaCustomType;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPEncodingConstants;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.util.ClassNameInfo;
import com.sun.xml.rpc.util.JAXRPCClassFactory;
import com.sun.xml.rpc.util.VersionUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RmiTypeModeler implements RmiConstants {
    public SOAPType modelTypeSOAP(String typeUri, RmiType type) {
        if (modeler.isStrictCompliant()
            && optionalTypes.contains(
                type.typeString(false).replace(SIGC_INNERCLASS, DOTC))) {
            throw new ModelerException(
                "rmimodeler.type.not.strict.compliant",
                type.typeString(false).replace(SIGC_INNERCLASS, DOTC));
        }
        SOAPType typeNode = getMappedSoapType(type);
        if (typeNode != null) {
            return typeNode;
        }
        if (typeMappingRegistry != null) {
            TypeMappingInfo typeMapping =
                typeMappingRegistry.getTypeMappingInfo(
                    soapEncodingConstants.getURIEncoding(),
                    type.toString());
            if (typeMapping != null) {
                return processTypeMapping(env, typeMapping);
            }
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
                // to prevent recursion by placing the SOAPArrayType in the
                // soapTypeMap with a temporary name
                QName arrName = new QName(namespaceURI, "tmp");

                String fixedupClassName = type.typeString(false);

                JavaArrayType javaArType = new JavaArrayType(fixedupClassName);

                javaArType.setElementName(
                    InternalEncodingConstants
                        .ARRAY_ELEMENT_NAME
                        .getLocalPart());
                SOAPArrayType arrType = new SOAPArrayType(arrName, soapVersion);
                arrType.setJavaType(javaArType);
                arrType.setElementName(
                    InternalEncodingConstants.ARRAY_ELEMENT_NAME);
                mapSOAPType(type, arrType);

                SOAPType elemType;
                elemType = modelTypeSOAP(typeUri, type.getElementType());
                /* Fix for bug: 4712053
                 * short[] and Short[] ended up with the same name
                 * ArrayOfshort, now short[] will be ArrayOfshort
                 * and Short[] will be ArrayOfShort
                 * we need to handle byte[]s specially since they are
                 * simple types
                 */
                String tmp =
                    boxedPrimitiveSet.contains(
                        elemType.getJavaType().getRealName())
                        ? ClassNameInfo.getName(elemType.getJavaType().getName())
                        : elemType.getName().getLocalPart();

                // replace the temporary name with the real one
                arrName = new QName(namespaceURI, "ArrayOf" + tmp);
                SOAPType tmpSOAPType = elemType;
                while (tmpSOAPType instanceof SOAPArrayType) {
                    tmpSOAPType =
                        ((SOAPArrayType) tmpSOAPType).getElementType();
                }
                if (!(tmpSOAPType instanceof SOAPSimpleType)) {
                    addTypeName(arrName, fixedupClassName);
                }
                arrType.setName(arrName);

                arrType.setElementType(elemType);
                if (elemType instanceof SOAPArrayType)
                    arrType.setRank(((SOAPArrayType) elemType).getRank() + 1);
                else
                    arrType.setRank(1);
                javaArType.setElementType(elemType.getJavaType());
                return arrType;
            case TC_CLASS :
                // Check if the type is a holder
                SOAPType holderType = modelHolder(modeler, env, typeUri, type);
                if (holderType != null) {
                    mapSOAPType(type, holderType);
                    return holderType;
                }
                QName structName =
                    new QName(
                        namespaceURI,
                        type.typeString(true).replace(SIGC_INNERCLASS, DOTC));
                SOAPStructureType struct =
                    new SOAPOrderedStructureType(structName, soapVersion);

                // take care of inner class case
                fixedupClassName = type.getClassName();
                addTypeName(structName, fixedupClassName);

                // prevent recursion by putting the SOAPStructureType into the map
                mapSOAPType(type, struct);

                JavaStructureType javaStruct =
                    new JavaStructureType(fixedupClassName, true, struct);
                struct.setJavaType(javaStruct);
                Map members = collectMembers(env, type);
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
                List sortedMembers = null;
                //return unsorted list for target 1.0.1
                if (VersionUtil.isVersion101(targetVersion))
                    sortedMembers = sortMembers101(typeClass, members, env);
                else
                    sortedMembers = sortMembers(typeClass, members, env);
                fillInStructure(
                    typeUri,
                    struct,
                    javaStruct,
                    sortedMembers,
                    type);
                return struct;
            default :
                throw new ModelerException(
                    "rmimodeler.unexpected.type",
                    type.toString());
        }
    }

    public static Map collectMembers(ProcessorEnvironment env, RmiType type) {
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
        return members;
    }

    public static List sortMembers(
        Class typeClass,
        Map members,
        ProcessorEnvironment env) {
            
        Set sorted = new TreeSet(new MemberInfoComparator());
        MemberInfo memInfo;
        for (Iterator iter = members.entrySet().iterator(); iter.hasNext();) {
            memInfo = (MemberInfo) ((Entry) iter.next()).getValue();
            memInfo.setSortingClass(getDeclaringClass(typeClass, memInfo, env));
            sorted.add(memInfo);
        }
        List list = new ArrayList(sorted.size());
        Iterator iter = sorted.iterator();
        while (iter.hasNext()) {
            memInfo = (MemberInfo) iter.next();
            list.add(memInfo);
        }
        return list;
    }

    /**
     * @param typeClass
     * @param members
     * @param env
     * @return
     */
    private static List sortMembers101(
        Class typeClass,
        Map members,
        ProcessorEnvironment env) {
            
        List sorted = new ArrayList();
        MemberInfo memInfo;
        for (Iterator iter = members.entrySet().iterator(); iter.hasNext();) {
            memInfo = (MemberInfo) ((Entry) iter.next()).getValue();
            memInfo.setSortingClass(getDeclaringClass(typeClass, memInfo, env));
            sorted.add(memInfo);
        }
        return sorted;
    }

    public static Class getDeclaringClass(
        Class theClass,
        MemberInfo memInfo,
        ProcessorEnvironment env) {
            
        Class retClass = null;
        if (retClass == null) {
            if (memInfo.isPublic()) {
                Class superClass = theClass.getSuperclass();
                if (superClass != null)
                    retClass = getDeclaringClass(superClass, memInfo, env);
                if (retClass == null) {
                    Class[] interfaces = theClass.getInterfaces();
                    for (int i = 0;
                        i < interfaces.length && retClass == null;
                        i++)
                        retClass =
                            getDeclaringClass(interfaces[i], memInfo, env);
                }
                try {
                    Field field = theClass.getDeclaredField(memInfo.getName());
                    if (field.getDeclaringClass().equals(theClass)) {
                        retClass = theClass;
                    }
                } catch (NoSuchFieldException e) {
                }
            } else {
                try {
                    Class typeClass =
                        memInfo.getType().getTypeClass(
                            theClass.getClassLoader());
                    Class readClass =
                        getDeclaringClassMethod(
                            theClass,
                            memInfo.getReadMethod(),
                            new Class[0]);
                    Class writeClass =
                        getDeclaringClassMethod(
                            theClass,
                            memInfo.getWriteMethod(),
                            new Class[] { typeClass });
                    if (readClass.equals(writeClass)) {
                        retClass = readClass;
                    } else if (readClass.isAssignableFrom(writeClass)) {
                        retClass = writeClass;
                    } else {
                        retClass = readClass;
                    }
                } catch (ClassNotFoundException e) {
                    throw new ModelerException(
                        "rmimodeler.class.not.found",
                        memInfo.getType().getClassName());
                }
            }
        }
        return retClass;
    }

    public static Class getDeclaringClassMethod(
        Class theClass,
        String methodName,
        Class[] args) {
            
        Class retClass = null;
        Class superClass = theClass.getSuperclass();
        if (superClass != null)
            retClass = getDeclaringClassMethod(superClass, methodName, args);
        if (retClass == null) {
            Class[] interfaces = theClass.getInterfaces();
            for (int i = 0; i < interfaces.length && retClass == null; i++)
                retClass =
                    getDeclaringClassMethod(interfaces[i], methodName, args);
        }
        if (retClass == null) {
            try {
                Method method = theClass.getMethod(methodName, args);
                if (method.getDeclaringClass().equals(theClass)) {
                    retClass = theClass;
                }
            } catch (NoSuchMethodException e) {
            }
        }
        return retClass;
    }

    public SOAPSimpleTypeCreatorBase getSOAPTypes() {
        return soapTypes;
    }

    private SOAPType modelHolder(
        RmiModeler modeler,
        ProcessorEnvironment env,
        String typeUri,
        RmiType type) {
            
        RmiType holderValueType =
            getHolderValueType(env, modeler.getDefHolder(), type);
        if (holderValueType == null)
            return null;
        SOAPType holderSOAPType = modelTypeSOAP(typeUri, holderValueType);
        JavaType javaType = holderSOAPType.getJavaType();
        javaType.setHolder(true);
        javaType.setHolderPresent(true);
        javaType.setHolderName(type.toString());
        return holderSOAPType;
    }

    public static boolean multipleClasses(
        Class[] classes,
        ProcessorEnvironment env) {
        if (classes.length < 2)
            return false;
        ArrayList tmpList = new ArrayList(classes.length);
        for (int i = 0; i < classes.length; i++) {
            RmiType type = RmiType.getRmiType(classes[i]);
            if (RmiStructure.modelTypeSOAP(env, type).size() > 0
                || JavaBean.modelTypeSOAP(env, type).size() > 0)
                tmpList.add(classes[i]);
        }
        classes = new Class[tmpList.size()];
        classes = (Class[]) tmpList.toArray((Class[]) classes);
        if (classes.length < 2)
            return false;
        boolean isSubclass = true;
        for (int i = 0; i < classes.length && isSubclass; i++) {
            isSubclass = false;
            for (int j = i; j < classes.length && !isSubclass; j++) {
                if (classes[i].isAssignableFrom(classes[j])
                    || classes[j].isAssignableFrom(classes[i])) {
                    isSubclass = true;
                }
            }
        }
        return isSubclass;
    }

    private void modelSubclasses(String typeUri, JavaStructureType type) {
        try {
            Class typeClass =
                RmiUtils.getClassForName(
                    type.getRealName(),
                    env.getClassLoader());
            Class[] interfaces = typeClass.getInterfaces();
            if (multipleClasses(interfaces, env)) {
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
        int startSize = soapTypeMap.size();
        int curSize = 0;
        while (curSize != startSize) {
            curSize = startSize;
            Iterator iterator = soapTypeMap.entrySet().iterator();
            SOAPType extraType;
            JavaStructureType javaType;
            while (iterator.hasNext()) {
                extraType = (SOAPType) ((Entry) iterator.next()).getValue();
                if (extraType instanceof SOAPStructureType) {
                    javaType = (JavaStructureType) extraType.getJavaType();
                    if (!type.equals(javaType)
                        && isSubclass(
                            javaType.getRealName(),
                            type.getRealName(),
                            env.getClassLoader())) {
                        modelHierarchy(typeUri, javaType, type);
                        curSize = soapTypeMap.size();
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
        SOAPStructureType superSOAPType =
            (SOAPStructureType) superclassType.getOwner();
        SOAPStructureType soapType = (SOAPStructureType) subclass.getOwner();
        if (soapType.getParentType() != null) {
            if (soapType.getParentType().equals(superSOAPType)) {
                return true;
            }
            // determine if the parent is a subclass of or implements superClassName
            if (isSubclass(soapType.getParentType().getJavaType().getRealName(),
                superClassName,
                env.getClassLoader())) {
                SOAPStructureType tmpType = soapType;
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
                soapType.getParentType().getJavaType().getRealName(),
                env.getClassLoader())) {
                if (soapType
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
                        soapType.getParentType().getJavaType().getRealName(),
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
                        superSOAPType.addSubtype(
                            (SOAPStructureType) subclass.getOwner());
                        (
                            (JavaStructureType) superSOAPType
                                .getJavaType())
                                .addSubclass(
                            subclass);
                        return true;
                    }
                    if (isSubclass(interfaces[i].getName(),
                        superClassName,
                        env.getClassLoader())) {
                        RmiType type = RmiType.getRmiType(interfaces[i]);
                        SOAPStructureType superType =
                            (SOAPStructureType) modelTypeSOAP(typeUri, type);
                        superType.addSubtype(
                            (SOAPStructureType) subclass.getOwner());
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
            SOAPStructureType superType =
                (SOAPStructureType) modelTypeSOAP(typeUri, type);
            superType.addSubtype((SOAPStructureType) subclass.getOwner());
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
            
        String className = subtypeName;
        if (subtypeName.equals(supertypeName))
            return false;
        try {
            Class subClass = RmiUtils.getClassForName(className, classLoader);
            className = supertypeName;
            Class supertypeClass = Class.forName(className, true, classLoader);
            return supertypeClass.isAssignableFrom(subClass);
        } catch (ClassNotFoundException e) {
            throw new ModelerException("rmimodeler.class.not.found", className);
        }
    }

    public static RmiType getHolderValueType(
        ProcessorEnvironment env,
        Class defHolder,
        RmiType type) {
            
        if (type.getTypeCode() != TC_CLASS) {
            return null;
        }
        Class def = null;
        try {
            def = type.getTypeClass(env.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                "rmimodeler.class.not.found",
                type.toString());
        }
        Class[] interfaces = def.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class interfaceDef = interfaces[i];
            if (defHolder.isAssignableFrom(interfaces[i])) {
                Field member = getValueMember(env, def);
                return (member != null)
                    ? RmiType.getRmiType(member.getType())
                    : null;
            }
        }
        return null;
    }

    public static Field getValueMember(
        ProcessorEnvironment env,
        Class classDef) {
            
        Field member = null;
        try {
            member = classDef.getDeclaredField("value");
        } catch (NoSuchFieldException e) {
        }
        if (member == null) {
            Class superDec = classDef.getSuperclass();
            member = getValueMember(env, superDec);
        }
        return member;
    }

    private SOAPType processTypeMapping(
        ProcessorEnvironment env,
        TypeMappingInfo typeMapping) {
            
        log(env, "creating custom type for: " + typeMapping.getJavaTypeName());
        // TODO this should be fixed to use the SOAPtype specified by the TypeMappingInfo
        SOAPCustomType soapType =
            new SOAPCustomType(typeMapping.getXMLType(), soapVersion);
        JavaCustomType javaType =
            new JavaCustomType(typeMapping.getJavaTypeName(), typeMapping);
        soapType.setJavaType(javaType);
        mapSOAPType(javaType.getRealName(), soapType);
        return soapType;
    }

    private void fillInStructure(
        String typeUri,
        SOAPStructureType struct,
        JavaStructureType javaStruct,
        List sortedMembers,
        RmiType type) {

        ProcessorEnvironment env = modeler.getProcessorEnvironment();

        MemberInfo memInfo;
        SOAPStructureMember member;
        JavaStructureMember javaMember;
        for (Iterator iter = sortedMembers.iterator(); iter.hasNext();) {
            memInfo = (MemberInfo) iter.next();
            //            log(env, "creating soap struct member: " + memInfo.getName());
            member =
                new SOAPStructureMember(
                    new QName(null, memInfo.getName()),
                    modelTypeSOAP(typeUri, memInfo.getType()));
            javaMember =
                new JavaStructureMember(
                    memInfo.getName(),
                    member.getType().getJavaType(),
                    member,
                    memInfo.isPublic());
            member.setJavaStructureMember(javaMember);
            javaMember.setReadMethod(memInfo.getReadMethod());
            javaMember.setWriteMethod(memInfo.getWriteMethod());
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
        SOAPStructureType struct) {
            
        String className = struct.getJavaType().getRealName();
        try {
            Iterator members = struct.getMembers();
            SOAPStructureMember soapMember;
            JavaStructureMember javaMember;
            Class javaClass =
                RmiUtils.getClassForName(className, env.getClassLoader());
            if (javaClass.isInterface()
                || Modifier.isAbstract(javaClass.getModifiers())) {
                ((JavaStructureType) struct.getJavaType()).setAbstract(true);
            }
            Class superclass = javaClass.getSuperclass();
            while (members.hasNext()) {
                soapMember = (SOAPStructureMember) members.next();
                javaMember = soapMember.getJavaStructureMember();
                if (javaMember.isPublic()) {
                    try {
                        Field field =
                            javaClass.getDeclaredField(javaMember.getName());
                        if (!field.getDeclaringClass().equals(javaClass)) {
                            javaMember.setInherited(true);
                            soapMember.setInherited(true);
                        }
                    } catch (NoSuchFieldException e) {
                        javaMember.setInherited(true);
                        soapMember.setInherited(true);
                    }
                } else {
                    String methodName;
                    methodName = javaMember.getReadMethod();
                    Class[] args = new Class[0];
                    boolean isInherited =
                        isMethodInherited(methodName, args, javaClass);
                    methodName = javaMember.getWriteMethod();
                    if (methodName != null) {
                        isInherited =
                            isInherited
                                ? isMethodInherited(methodName, args, javaClass)
                                : false;
                    }
                    if (isInherited) {
                        javaMember.setInherited(true);
                        soapMember.setInherited(true);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ModelerException("rmimodeler.class.not.found", className);
        }
    }

    public static boolean isMethodInherited(
        String methodName,
        Class[] args,
        Class javaClass) {
            
        return methodMemberClass(methodName, args, javaClass) != javaClass;
    }

    private static Class methodMemberClass(
        String methodName,
        Class[] args,
        Class javaClass) {
            
        Class retClass = null;
        Class superclass = javaClass.getSuperclass();
        if (superclass != null) {
            if ((retClass = methodMemberClass(methodName, args, superclass))
                != null) {
                return retClass;
            }
        }
        Class[] interfaces = javaClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if ((retClass = methodMemberClass(methodName, args, interfaces[i]))
                != null) {
                return retClass;
            }
        }
        Method method;
        try {
            method = javaClass.getDeclaredMethod(methodName, args);
            return javaClass;
        } catch (NoSuchMethodException e) {
        }
        return retClass;
    }

    public void modelSubclasses(String typeUri) {
        Set abstractTypes = new HashSet();
        int startSize = soapTypeMap.size();
        Iterator iter = soapTypeMap.entrySet().iterator();
        SOAPType type;
        while (iter.hasNext()) {
            type = (SOAPType) ((Entry) iter.next()).getValue();
            if (type instanceof SOAPStructureType) { 
                abstractTypes.add(type);
            }
        }
        iter = abstractTypes.iterator();
        while (iter.hasNext()) {
            type = (SOAPType) iter.next();
            modelSubclasses(typeUri, (JavaStructureType) type.getJavaType());
        }
        if (startSize != soapTypeMap.size()) {
            iter = soapTypeMap.entrySet().iterator();
            while (iter.hasNext()) {
                type = (SOAPType) ((Entry) iter.next()).getValue();
                if (type instanceof SOAPStructureType
                    && ((JavaStructureType) type.getJavaType()).isAbstract()
                    && !abstractTypes.contains(type)) {

                    modelSubclasses(
                        typeUri,
                        (JavaStructureType) type.getJavaType());
                }
            }
        }
    }

    /**
     * returns the SOAPType for a mapped type, null if the
     * type is not in the map
     */
    private SOAPType getMappedSoapType(RmiType type) {
        String name = type.toString();
        return (SOAPType) soapTypeMap.get(name);
    }

    private void mapSOAPType(RmiType type, SOAPType soapType) {
        mapSOAPType(type.toString(), soapType);
    }

    private void mapSOAPType(String name, SOAPType soapType) {
        soapTypeMap.put(name, soapType);
    }

    private void addTypeName(QName typeName, String javaType) {
        if (typeNames.contains(typeName)) {
            throw new ModelerException(
                "rmimodeler.duplicate.type.name",
                new Object[] { typeName.toString(), javaType });
        }
        typeNames.add(typeName);
    }

    public SOAPVersion getSOAPVersion() {
        return soapVersion;
    }

    private static void log(ProcessorEnvironment env, String msg) {
        if (env.verbose()) {
            System.out.println("[RmiTypeModeler: " + msg + "]");
        }
    }

    public void initializeTypeMap(Map typeMap) {
        soapTypes.initializeTypeMap(typeMap);
    }

    protected RmiTypeModeler(RmiModeler modeler, ProcessorEnvironment env) {
        this(modeler, env, SOAPVersion.SOAP_11);
    }

    protected RmiTypeModeler(
        RmiModeler modeler,
        ProcessorEnvironment env,
        SOAPVersion soapVersion) {
            
        this.modeler = modeler;
        this.env = env;
        typeMappingRegistry = modeler.getTypeMappingRegistryInfo();
        typeNames = new HashSet();
        soapTypeMap = new HashMap();
        this.soapVersion = soapVersion;
        soapEncodingConstants =
            SOAPConstantsFactory.getSOAPEncodingConstants(soapVersion);
        soapTypes =
            JAXRPCClassFactory.newInstance().createSOAPSimpleTypeCreator(
                modeler.isStrictCompliant(),
                soapVersion);
        initializeTypeMap(soapTypeMap);
        targetVersion =
            modeler.getOptions().getProperty(
                ProcessorOptions.JAXRPC_SOURCE_VERSION);
    }
    
    // Java to WSDL type map
    private Map soapTypeMap;
    private Set typeNames = new HashSet();
    private RmiModeler modeler;
    private ProcessorEnvironment env;
    private TypeMappingRegistryInfo typeMappingRegistry;
    private SOAPSimpleTypeCreatorBase soapTypes;
    private SOAPEncodingConstants soapEncodingConstants;
    private SOAPVersion soapVersion;

    // target version - currently used for jaxrpc 1.0.1
    private String targetVersion;

    private static final Set boxedPrimitiveSet = new HashSet();
    private static final Set optionalTypes = new HashSet();
    static {
        boxedPrimitiveSet.add(BOXED_BOOLEAN_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_BYTE_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_DOUBLE_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_FLOAT_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_INTEGER_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_LONG_CLASSNAME);
        boxedPrimitiveSet.add(BOXED_SHORT_CLASSNAME);

        optionalTypes.add(OBJECT_CLASSNAME);

        // Collection Types
        optionalTypes.add(COLLECTION_CLASSNAME);
        optionalTypes.add(LIST_CLASSNAME);
        optionalTypes.add(SET_CLASSNAME);
        optionalTypes.add(VECTOR_CLASSNAME);
        optionalTypes.add(STACK_CLASSNAME);
        optionalTypes.add(LINKED_LIST_CLASSNAME);
        optionalTypes.add(ARRAY_LIST_CLASSNAME);
        optionalTypes.add(HASH_SET_CLASSNAME);
        optionalTypes.add(TREE_SET_CLASSNAME);

        // Map Types
        optionalTypes.add(MAP_CLASSNAME);
        optionalTypes.add(HASH_MAP_CLASSNAME);
        optionalTypes.add(TREE_MAP_CLASSNAME);
        optionalTypes.add(HASHTABLE_CLASSNAME);
        optionalTypes.add(PROPERTIES_CLASSNAME);
        //        optionalTypes.add(WEAK_HASH_MAP_CLASSNAME);

    }

    public static class MemberInfoComparator implements Comparator {
        public MemberInfoComparator() {
        }

        public int compare(Object o1, Object o2) {
            MemberInfo mem1 = (MemberInfo) o1;
            MemberInfo mem2 = (MemberInfo) o2;
            return sort(mem1, mem2);
        }

        protected int sort(MemberInfo mem1, MemberInfo mem2) {
            String key1, key2;
            key1 = mem1.getName();
            key2 = mem2.getName();
            Class class1 = mem1.getSortingClass();
            Class class2 = mem2.getSortingClass();
            if (class1.equals(class2)) {
                return key1.compareTo(key2);
            }
            if (class1.isAssignableFrom(class2)
                && !class2.isAssignableFrom(class1)) {
                return -1;
            }
            return 1;
        }
    }
}
