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

package com.sun.xml.rpc.processor.modeler.j2ee;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.J2EEModelInfo;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.ModelProperties;
import com.sun.xml.rpc.processor.model.java.JavaSimpleType;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.JavaSimpleTypeCreator;
import com.sun.xml.rpc.processor.modeler.rmi.ExceptionModelerBase;
import com.sun.xml.rpc.processor.modeler.rmi.MemberInfo;
import com.sun.xml.rpc.processor.modeler.rmi.RmiType;
import com.sun.xml.rpc.processor.modeler.rmi.RmiTypeModeler;
import com.sun.xml.rpc.processor.modeler.rmi.RmiUtils;
import com.sun.xml.rpc.processor.modeler.wsdl.SchemaAnalyzerBase.SchemaJavaMemberInfo;
import com.sun.xml.rpc.processor.schema.ComplexTypeDefinitionComponent;
import com.sun.xml.rpc.processor.schema.ElementDeclarationComponent;
import com.sun.xml.rpc.processor.schema.TypeDefinitionComponent;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.processor.util.StringUtils;

public class J2EESchemaAnalyzerHelper {
    //  bug fix: 4923650
    private J2EESchemaAnalyzerIf base;
    private JavaSimpleTypeCreator javaSimpleTypeCreator;
    private J2EEModelInfo _j2eeModelInfo;
    private ProcessorEnvironment _env;
    
    public J2EESchemaAnalyzerHelper(
        J2EESchemaAnalyzerIf base,
        J2EEModelInfo modelInfo,
        ProcessorEnvironment env,
        JavaSimpleTypeCreator javaTypes) {

        this.base = base;
        _j2eeModelInfo = (J2EEModelInfo) modelInfo;
        _env = env;
        javaSimpleTypeCreator = new JavaSimpleTypeCreator();
    }

    protected String getJavaNameOfType(
        TypeDefinitionComponent component,
        QName nameHint) {
        String className =  _j2eeModelInfo.javaNameOfType(component);
        return getLoadableClassName(className);
    }

    private String getLoadableClassName(String className) {
        if (className != null) {
            try {
                // Done to take care of inner classes. mycom.outer.inner
                // becomes mycom.outer$inner
                className = RmiUtils.getLoadableClassName(className,
                    _env.getClassLoader());
            } catch(ClassNotFoundException ce) {
            }
        }
        return className;        
    }

    // Sets abstract if the java type is abstract or interface
    protected void updateModifiers(JavaStructureType javaStructureType) {
        Class typeClass;
        try {
            String javaName = javaStructureType.getName();
            typeClass = RmiUtils.getClassForName(javaName, _env.getClassLoader());
            if (typeClass.isInterface()
                  || Modifier.isAbstract(typeClass.getModifiers())) {
                javaStructureType.setAbstract(true);
            }
        } catch (ClassNotFoundException e) {
        }
    }

    protected String getJavaNameOfSOAPStructureType(
        SOAPStructureType structureType,
        TypeDefinitionComponent component,
        QName nameHint) {
        return getJavaNameOfType(component, nameHint);
    }

    protected SchemaJavaMemberInfo getJavaMemberInfo(
        TypeDefinitionComponent component,
        ElementDeclarationComponent element) {

        return _j2eeModelInfo.javaMemberInfo(
            component,
            element.getName().getLocalPart());
    }

    protected String getJavaNameOfElementType(
        LiteralStructuredType structureType,
        TypeDefinitionComponent component,
        QName nameHint) {
        String isAnonymousName =
            (String) structureType.getProperty(
                ModelProperties.PROPERTY_ANONYMOUS_TYPE_NAME);
        String className =  _j2eeModelInfo.javaNameOfElementType(
            structureType.getName(),
            isAnonymousName);
        return getLoadableClassName(className);
    }

    protected SchemaJavaMemberInfo getJavaMemberOfElementInfo(
        QName typeName,
        String memberName) {

        return _j2eeModelInfo.javaMemberOfElementInfo(typeName, memberName);
    }

    /*----------------------------------------------------------------*/
    // The following methods were added as part of bug fix: 4923650

    protected SOAPType getSOAPMemberType(
        ComplexTypeDefinitionComponent component,
        SOAPStructureType structureType,
        ElementDeclarationComponent element,
        QName nameHint,
        boolean occursZeroOrOne) {
        SOAPType memberType =
            base.getSuperSOAPMemberType(
                component,
                structureType,
                element,
                nameHint,
                occursZeroOrOne);
        JavaType javaType =
            getMemberJavaType(memberType, structureType, component, element);
        if (javaType != null)
            memberType.setJavaType(javaType);

        return memberType;
    }

    protected JavaType getMemberJavaType(
        AbstractType memberType,
        AbstractType structureType,
        ComplexTypeDefinitionComponent component,
        ElementDeclarationComponent element) {
        JavaType javaType = null;
        try {
            Class javaClass =
                RmiUtils.getClassForName(
                    structureType.getJavaType().getName(),
                    _env.getClassLoader());
            SchemaJavaMemberInfo memberInfo =
                getJavaMemberInfo(component, element);
            Map members = null;
            if (Exception.class.isAssignableFrom(javaClass)) {
                members = new HashMap();
                Map exceptionMembers = new HashMap();
                ExceptionModelerBase.collectExceptionMembers(
                    javaClass,
                    exceptionMembers);
                Iterator iter = exceptionMembers.entrySet().iterator();
                Method member;
                while (iter.hasNext()) {
                    member = (Method) ((Entry) iter.next()).getValue();
                    RmiType returnType =
                        RmiType.getRmiType(member.getReturnType());
                    String readMethod = member.getName();
                    String propertyName;
                    if (readMethod.startsWith("get"))
                        propertyName =
                            StringUtils.decapitalize(readMethod.substring(3));
                    else // must be "is"
                        propertyName =
                            StringUtils.decapitalize(readMethod.substring(2));
                    MemberInfo memInfo =
                        new MemberInfo(propertyName, returnType, false);
                    memInfo.setReadMethod(readMethod);
                    members.put(propertyName, memInfo);
                }
            } else {
                members =
                    RmiTypeModeler.collectMembers(
                        _env,
                        RmiType.getRmiType(javaClass));
            }
            RmiType rmiType = getMemberType(memberInfo, members);
            if (rmiType == null) {
                memberInfo = base.getSuperJavaMemberInfo(component, element);
                rmiType = getMemberType(memberInfo, members);
            }

            // dont mess with String[] or byte[] simple types
            if (rmiType != null && rmiType.getTypeCode() != RmiType.TC_ARRAY) {
                String typeString = rmiType.typeString(false);
                if (!memberType.getJavaType().getName().equals(typeString)) {
                    if (memberType.getJavaType() instanceof JavaSimpleType) {
                        javaType =
                            javaSimpleTypeCreator.getJavaSimpleType(typeString);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            // TODO fill this in?
        }
        return javaType;
    }

    protected LiteralType getLiteralMemberType(
        ComplexTypeDefinitionComponent component,
        LiteralType memberType,
        ElementDeclarationComponent element,
        LiteralStructuredType structureType) {

        JavaType javaType =
            getMemberJavaType(memberType, structureType, component, element);
        if (javaType != null) {
            memberType.setJavaType(javaType);
        }
        return memberType;
    }

    private RmiType getMemberType(
        SchemaJavaMemberInfo javaMemberInfo,
        Map members) {
        RmiType type = null;
        MemberInfo memInfo;
        String memberName = javaMemberInfo.javaMemberName;
        for (Iterator iter = members.entrySet().iterator(); iter.hasNext();) {
            memInfo = (MemberInfo) ((Entry) iter.next()).getValue();
            if (memInfo.getName().equals(memberName)) {
                return memInfo.getType();
            }
            if (memInfo.getWriteMethod() != null
                && memInfo.getWriteMethod().substring(3).equalsIgnoreCase(
                    memberName)) {
                return memInfo.getType();
            }
        }

        return type;
    }
    // End of bug fix: 4923650    
    /*---------------------------------------------------------------------------*/

}
