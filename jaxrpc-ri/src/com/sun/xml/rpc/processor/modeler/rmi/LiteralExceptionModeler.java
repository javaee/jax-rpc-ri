/*
 * $Id: LiteralExceptionModeler.java,v 1.2 2006-04-13 01:31:15 ofung Exp $
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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.util.StringUtils;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralExceptionModeler
    extends ExceptionModelerBase
    implements RmiConstants {

    protected LiteralTypeModeler literalTypeModeler;
    public LiteralExceptionModeler(
        RmiModeler modeler,
        LiteralTypeModeler typeModeler) {
            
        super(modeler);
        literalTypeModeler = typeModeler;
    }

    protected void checkForJavaExceptions(String className) {
    }

    public Fault createFault(String typeUri, String wsdlUri, Class classDef) {
        String exceptionName = classDef.getName();
        Fault fault = (Fault) faultMap.get(exceptionName);
        if (fault != null)
            return fault;
        HashMap members = new HashMap();
        collectMembers(classDef, members);
        int getMessageFlags = 0;
        if (members.containsKey(GET_MESSAGE)) {
            Iterator iterator = members.entrySet().iterator();
            while (iterator.hasNext()) {
                Method member =
                    (Method) ((Map.Entry) iterator.next()).getValue();
                if (member.getReturnType().equals(String.class)
                    && !member.getName().equals(GET_MESSAGE)) {
                    members.remove(GET_MESSAGE);
                    break;
                }
            }
        }
        if (members.size() == 0) {
            members.put(GET_MESSAGE, GET_MESSAGE_METHOD);
        }
        if (members.containsKey(GET_MESSAGE)) {
            getMessageFlags = MESSAGE_FLAG;
        }
        boolean hasDuplicates = false;
        Set duplicateMembers = getDuplicateMembers(members);
        if (duplicateMembers.size() > 0) {
            hasDuplicates = true;
        }
        if (members.size() > 0 && !hasDuplicates) {
            Constructor[] constrs = classDef.getConstructors();
            LiteralElementMember[] literalMembers;
            Constructor defaultConstructor = null;
            for (int i = 0; i < constrs.length && fault == null; i++) {
                if (constrs[i].getParameterTypes().length == 0) {
                    defaultConstructor = constrs[i];
                    continue;
                }
                if ((literalMembers =
                    constructorMatches(
                        typeUri,
                        wsdlUri,
                        classDef,
                        constrs[i],
                        members,
                        getMessageFlags))
                    != null) {
                    fault =
                        createFault(typeUri, wsdlUri, classDef, literalMembers);
                }
            }
            if (fault == null
                && defaultConstructor != null
                && (literalMembers =
                    constructorMatches(
                        typeUri,
                        wsdlUri,
                        classDef,
                        defaultConstructor,
                        members,
                        getMessageFlags))
                    != null) {
                fault = createFault(typeUri, wsdlUri, classDef, literalMembers);
            }
        }
        if (fault == null) {
            List newMembers = new ArrayList();
            if (!members.containsKey(GET_MESSAGE)) {
                newMembers.add(GET_MESSAGE);
            }
            fault =
                createFault(
                    typeUri,
                    wsdlUri,
                    classDef,
                    addMessage(
                        typeUri,
                        wsdlUri,
                        classDef,
                        members,
                        newMembers));
        }
        faultMap.put(classDef.getName().toString(), fault);
        return fault;
    }

    private LiteralElementMember[] constructorMatches(
        String typeUri,
        String wsdlUri,
        Class classDef,
        Constructor cstr,
        Map members,
        int getMessageFlags) {
            
        Class[] args = cstr.getParameterTypes();
        Object[] memberArray = members.values().toArray();
        boolean memberCountMatch = args.length == memberArray.length;
        if (!memberCountMatch
            && ((getMessageFlags == 0)
                || (args.length != memberArray.length - 1))) {
            return null;
        }
        LiteralElementMember[] literalMembers =
            new LiteralElementMember[args.length];
        for (int i = 0; i < args.length; i++) {
            for (int j = 0;
                j < memberArray.length && literalMembers[i] == null;
                j++) {
                if (!memberCountMatch) {
                    String memberName = ((Method) memberArray[j]).getName();
                    if ((getMessageFlags == MESSAGE_FLAG
                        && memberName.equals(GET_MESSAGE))) {
                        continue;
                    }
                }
                if (args[i]
                    .equals(((Method) memberArray[j]).getReturnType())) {
                    literalMembers[i] =
                        createLiteralMember(
                            typeUri,
                            wsdlUri,
                            classDef,
                            (Method) memberArray[j],
                            i);
                }
            }
            if (literalMembers[i] == null) {
                return null;
            }
        }
        return literalMembers;
    }

    public LiteralElementMember createLiteralMember(
        String typeUri,
        String wsdlUri,
        Class classDef,
        Method member,
        int cstrPos) {
            
        String packageName = classDef.getPackage().getName();
        RmiType memberType = RmiType.getRmiType(member.getReturnType());
        String readMethod = member.getName();
        String namespaceURI = modeler.getNamespaceURI(packageName);
        if (namespaceURI == null)
            namespaceURI = wsdlUri;
        String propertyName;
        if (readMethod.startsWith("get"))
            propertyName = StringUtils.decapitalize(readMethod.substring(3));
        else // must be "is"
            propertyName = StringUtils.decapitalize(readMethod.substring(2));
        QName propertyQName = new QName("", propertyName);
        LiteralElementMember literalMember =
            literalTypeModeler.modelTypeLiteral(
                propertyQName,
                typeUri,
                memberType);
        JavaStructureMember javaMember = literalMember.getJavaStructureMember();
        javaMember.setConstructorPos(cstrPos);
        javaMember.setReadMethod(readMethod);
        return literalMember;
    }

    public LiteralElementMember[] addMessage(
        String typeUri,
        String wsdlUri,
        Class classDef,
        Map members,
        List newMembers) {
            
        String packageName = classDef.getPackage().getName();
        String namespaceURI = modeler.getNamespaceURI(packageName);
        if (namespaceURI == null)
            namespaceURI = wsdlUri;
        LiteralElementMember[] literalMembers =
            new LiteralElementMember[members.size() + newMembers.size()];
        Method argMember;
        Iterator iter = members.entrySet().iterator();
        for (int i = 0; iter.hasNext(); i++) {
            argMember = (Method) ((Map.Entry) iter.next()).getValue();
            literalMembers[i] =
                createLiteralMember(typeUri, wsdlUri, classDef, argMember, -1);
        }
        // message or localizedMessage
        iter = newMembers.iterator();
        String tmp;
        for (int i = members.size(); iter.hasNext(); i++) {
            tmp = (String) iter.next();
            String propertyName;
            if (tmp.startsWith("get"))
                propertyName = StringUtils.decapitalize(tmp.substring(3));
            else // must be "is"
                propertyName = StringUtils.decapitalize(tmp.substring(2));
            QName propertyQName = new QName("", propertyName);
            LiteralType propertyType =
                literalTypeModeler.getLiteralTypes().XSD_STRING_LITERALTYPE;
            LiteralElementMember literalMember =
                new LiteralElementMember(propertyQName, propertyType, null);
            literalMember.setNillable(isNewMemberNillable(literalMember));
            JavaStructureMember javaMember =
                new JavaStructureMember(
                    propertyName,
                    propertyType.getJavaType(),
                    literalMember);
            literalMember.setJavaStructureMember(javaMember);
            //            javaMember.setReadMethod("get"+StringUtils.capitalize(propertyName));
            javaMember.setReadMethod(tmp);
            literalMember.setJavaStructureMember(javaMember);
            literalMembers[i] = literalMember;
        }
        return literalMembers;
    }

    protected boolean isNewMemberNillable(LiteralElementMember member) {
        return true;
    }


    public Fault createFault(
        String typeUri,
        String wsdlUri,
        Class classDef,
        LiteralElementMember[] literalMembers) {
            
        String packageName = classDef.getPackage().getName();
        String namespaceURI = modeler.getNamespaceURI(packageName);
        if (namespaceURI == null)
            namespaceURI = typeUri;

        Set sortedMembers = sortMembers(classDef, literalMembers);
        Fault fault = new Fault(Names.stripQualifier(classDef.getName()));
        LiteralSequenceType literalStruct =
            new LiteralSequenceType(new QName(namespaceURI, fault.getName()));
        namespaceURI = modeler.getNamespaceURI(packageName);
        if (namespaceURI == null)
            namespaceURI = wsdlUri;
        QName faultQName = new QName(namespaceURI, fault.getName());
        JavaException javaException =
            new JavaException(classDef.getName(), true, literalStruct);
        LiteralElementMember member;
        for (Iterator iter = sortedMembers.iterator(); iter.hasNext();) {
            member = (LiteralElementMember) iter.next();
            literalStruct.add(member);
            javaException.add(member.getJavaStructureMember());
        }

        Block faultBlock;
        faultBlock = new Block(faultQName, literalStruct);
        fault.setBlock(faultBlock);
        literalStruct.setJavaType(javaException);
        fault.setJavaException(javaException);
        return fault;
    }

    public static Set sortMembers(
        Class classDef,
        LiteralElementMember[] unsortedMembers) {
        Set sortedMembers =
            new TreeSet(new LiteralElementMemberComparator(classDef));

        for (int i = 0; i < unsortedMembers.length; i++) {
            sortedMembers.add(unsortedMembers[i]);
        }
        return sortedMembers;
    }

    public static class LiteralElementMemberComparator implements Comparator {
        Class classDef;
        public LiteralElementMemberComparator(Class classDef) {
            this.classDef = classDef;
        }

        public int compare(Object o1, Object o2) {
            LiteralElementMember mem1 = (LiteralElementMember) o1;
            LiteralElementMember mem2 = (LiteralElementMember) o2;
            return sort(mem1, mem2);
        }

        protected int sort(
            LiteralElementMember mem1,
            LiteralElementMember mem2) {
                
            String key1, key2;
            key1 = mem1.getJavaStructureMember().getName();
            key2 = mem2.getJavaStructureMember().getName();
            Class class1 = getDeclaringClass(classDef, mem1);
            Class class2 = getDeclaringClass(classDef, mem2);
            if (class1.equals(class2))
                return key1.compareTo(key2);
            if (class1.equals(Throwable.class)
                || class1.equals(Exception.class))
                return 1;
            if (class1.isAssignableFrom(class2))
                return -1;
            return 1;
        }

        protected Class getDeclaringClass(
            Class testClass,
            LiteralElementMember member) {
                
            String readMethod = member.getJavaStructureMember().getReadMethod();
            Class retClass =
                RmiTypeModeler.getDeclaringClassMethod(
                    testClass,
                    readMethod,
                    new Class[0]);
            return retClass;
        }
    }
}
