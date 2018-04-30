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

import java.lang.reflect.Constructor;
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

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.StringUtils;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 * @author JAX-RPC Development Team
 *
 * ExceptionModeler for JAXRPC version 1.0.3. Though it extends ExceptionModelerBase, overrides all 
 * the methods to suit 1.0.3.
 */
public class ExceptionModeler103
    extends ExceptionModelerBase
    implements RmiConstants {
        
    private RmiTypeModeler rmiTypeModeler;

    /**
     * @param modeler
     * @param typeModeler
     */
    public ExceptionModeler103(
        RmiModeler modeler,
        RmiTypeModeler typeModeler) {
            
        super(modeler);
        rmiTypeModeler = typeModeler;
    }

    /* (non-Javadoc)
     * @see com.sun.xml.rpc.processor.modeler.rmi.ExceptionModelerBase#createFault(java.lang.String, java.lang.String, java.lang.Class)
     */
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
            SOAPStructureMember[] soapMembers;
            for (int i = 0; i < constrs.length && fault == null; i++) {
                if ((soapMembers =
                    constructorMatches(
                        typeUri,
                        wsdlUri,
                        classDef,
                        constrs[i],
                        members,
                        getMessageFlags))
                    != null) {
                    fault =
                        createFault(typeUri, wsdlUri, classDef, soapMembers);
                }
            }
        }
        if (fault == null) {
            List newMembers = new ArrayList();
            if (!members.containsKey(GET_MESSAGE)) {
                newMembers.add(GET_MESSAGE);
            }
            if (!members.containsKey(GET_LOCALIZED_MESSAGE)) {
                newMembers.add(GET_LOCALIZED_MESSAGE);
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

    private SOAPStructureMember[] constructorMatches(
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
                || (args.length != memberArray.length - 1)))
            return null;
        SOAPStructureMember[] soapMembers =
            new SOAPStructureMember[args.length];
        for (int i = 0; i < args.length; i++) {
            for (int j = 0;
                j < memberArray.length && soapMembers[i] == null;
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
                    soapMembers[i] =
                        createSOAPMember(
                            typeUri,
                            wsdlUri,
                            classDef,
                            (Method) memberArray[j],
                            i);
                }
            }
            if (soapMembers[i] == null)
                return null;
        }
        return soapMembers;
    }

    public Fault createFault(
        String typeUri,
        String wsdlUri,
        Class classDef,
        SOAPStructureMember[] soapMembers) {
            
        String packageName = classDef.getPackage().getName();
        String namespaceURI = modeler.getNamespaceURI(packageName);
        if (namespaceURI == null)
            namespaceURI = typeUri;

        Set sortedMembers = sortMembers(classDef, soapMembers);
        Fault fault = new Fault(Names.stripQualifier(classDef.getName()));
        SOAPStructureType soapStruct =
            new SOAPOrderedStructureType(
                new QName(namespaceURI, fault.getName()));
        namespaceURI = modeler.getNamespaceURI(packageName);
        if (namespaceURI == null)
            namespaceURI = wsdlUri;
        QName faultQName = new QName(namespaceURI, fault.getName());
        JavaException javaException =
            new JavaException(classDef.getName(), true, soapStruct);
        SOAPStructureMember member;
        for (Iterator iter = sortedMembers.iterator(); iter.hasNext();) {
            member = (SOAPStructureMember) iter.next();
            soapStruct.add(member);
            javaException.add(member.getJavaStructureMember());
        }
        Block faultBlock;
        faultBlock = new Block(faultQName, soapStruct);
        fault.setBlock(faultBlock);
        soapStruct.setJavaType(javaException);
        fault.setJavaException(javaException);
        return fault;
    }

    public SOAPStructureMember[] addMessage(
        String typeUri,
        String wsdlUri,
        Class classDef,
        Map members,
        List newMembers) {
            
        String packageName = classDef.getPackage().getName();
        String namespaceURI = modeler.getNamespaceURI(packageName);
        if (namespaceURI == null)
            namespaceURI = wsdlUri;
        SOAPStructureMember[] soapMembers =
            new SOAPStructureMember[members.size() + newMembers.size()];
        Method argMember;
        Iterator iter = members.entrySet().iterator();
        for (int i = 0; iter.hasNext(); i++) {
            argMember = (Method) ((Map.Entry) iter.next()).getValue();
            soapMembers[i] =
                createSOAPMember(typeUri, wsdlUri, classDef, argMember, -1);
        }
        iter = newMembers.iterator();
        for (int i = members.size(); iter.hasNext(); i++) {
            String propertyName =
                StringUtils.decapitalize(((String) iter.next()).substring(3));
            QName propertyQName = new QName("", propertyName);
            SOAPType propertyType =
                rmiTypeModeler.getSOAPTypes().XSD_STRING_SOAPTYPE;
            SOAPStructureMember soapMember =
                new SOAPStructureMember(propertyQName, propertyType, null);
            JavaStructureMember javaMember =
                new JavaStructureMember(
                    propertyName,
                    propertyType.getJavaType(),
                    soapMember);
            soapMember.setJavaStructureMember(javaMember);
            javaMember.setReadMethod(
                "get" + StringUtils.capitalize(propertyName));
            soapMember.setJavaStructureMember(javaMember);
            soapMembers[i] = soapMember;
        }
        return soapMembers;
    }

    public SOAPStructureMember createSOAPMember(
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
        String propertyName = StringUtils.decapitalize(readMethod.substring(3));
        QName propertyQName = new QName("", propertyName);
        SOAPType propertyType =
            rmiTypeModeler.modelTypeSOAP(typeUri, memberType);
        SOAPStructureMember soapMember =
            new SOAPStructureMember(propertyQName, propertyType, null);
        JavaStructureMember javaMember =
            new JavaStructureMember(
                propertyName,
                propertyType.getJavaType(),
                soapMember);
        soapMember.setJavaStructureMember(javaMember);
        javaMember.setConstructorPos(cstrPos);
        javaMember.setReadMethod(readMethod);
        soapMember.setJavaStructureMember(javaMember);
        return soapMember;
    }

    public static Set sortMembers(
        Class classDef,
        SOAPStructureMember[] unsortedMembers) {
            
        Set sortedMembers =
            new TreeSet(new SOAPStructureMemberComparator(classDef));

        for (int i = 0; i < unsortedMembers.length; i++) {
            sortedMembers.add(unsortedMembers[i]);
        }
        return sortedMembers;
    }

    protected static Set getDuplicateMembers(Map members) {
        Set types = new HashSet();
        Set duplicateMembers = new HashSet();
        Iterator iter = members.entrySet().iterator();
        Method member;
        RmiType type;
        String memberName;
        while (iter.hasNext()) {
            member = (Method) ((Map.Entry) iter.next()).getValue();
            type = RmiType.getRmiType(member.getReturnType());
            memberName = member.getName();
            if (types.contains(type)) {
                duplicateMembers.add(member);
            } else {
                types.add(type);
            }
        }
        return duplicateMembers;
    }

    public void collectMembers(Class classDef, Map members) {
        try {
            if (defRuntimeException.isAssignableFrom(classDef)) {
                throw new ModelerException(
                    "rmimodeler.must.not.extend.runtimeexception",
                    classDef.getName());
            }
            if (classDef.equals(Throwable.class)
                || classDef.equals(Object.class)) {
                return;
            }
            if (classDef.getSuperclass() != null)
                collectMembers(classDef.getSuperclass(), members);
            Method[] methods = classDef.getMethods();
            Class decClass;
            for (int i = 0; i < methods.length; i++) {
                decClass = methods[i].getDeclaringClass();
                if (Modifier.isStatic(methods[i].getModifiers())
                    || (decClass.equals(Throwable.class)
                        || decClass.equals(Object.class))) {
                    continue;
                }
                String memberName = methods[i].getName();
                if (memberName.startsWith("get")
                    && methods[i].getParameterTypes().length == 0) {
                    if (!members.containsKey(memberName)
                        && !memberName.equals(GET_LOCALIZED_MESSAGE)) {
                        members.put(memberName, methods[i]);
                    }
                }
            }
        } catch (Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
    }

    public static class SOAPStructureMemberComparator implements Comparator {
        Class classDef;
        public SOAPStructureMemberComparator(Class classDef) {
            this.classDef = classDef;
        }

        public int compare(Object o1, Object o2) {
            SOAPStructureMember mem1 = (SOAPStructureMember) o1;
            SOAPStructureMember mem2 = (SOAPStructureMember) o2;
            return sort(mem1, mem2);
        }

        protected int sort(
            SOAPStructureMember mem1,
            SOAPStructureMember mem2) {
                
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
            SOAPStructureMember member) {
                
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
