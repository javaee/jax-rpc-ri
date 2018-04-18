/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: ExceptionModelerBase.java,v 1.3 2007-07-13 23:36:16 ofung Exp $
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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class ExceptionModelerBase implements RmiConstants {

    protected RmiModeler modeler;
    protected Class defRuntimeException;
    protected ProcessorEnvironment env;
    protected static final String THROWABLE_CLASSNAME =
        java.lang.Throwable.class.getName();
    protected static final String OBJECT_CLASSNAME =
        java.lang.Object.class.getName();
    protected static final int MESSAGE_FLAG = 2;
    protected static final int LOCALIZED_MESSAGE_FLAG = 4;
    protected static Method GET_MESSAGE_METHOD;
    // Java to WSDL type map
    protected Map faultMap;

    static {
        try {
            GET_MESSAGE_METHOD =
                java.lang.Throwable.class.getDeclaredMethod(
                    GET_MESSAGE,
                    new Class[0]);
        } catch (Exception e) {
        }
    }

    public ExceptionModelerBase(RmiModeler modeler) {
        this.modeler = modeler;
        env = modeler.getProcessorEnvironment();
        faultMap = new HashMap();
        /*
         * Initialize cached definitions for the RuntimeException class.
         */
        try {
            defRuntimeException =
                RmiUtils.getClassForName(
                    RuntimeException.class.getName(),
                    env.getClassLoader());
            GET_MESSAGE_METHOD =
                RmiUtils
                    .getClassForName(
                        java.lang.Throwable.class.getName(),
                        env.getClassLoader())
                    .getDeclaredMethod(GET_MESSAGE, new Class[0]);
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                "rmimodeler.class.not.found",
                RuntimeException.class.getName());
        } catch (NoSuchMethodException e) {
            throw new ModelerException(
                "rmimodeler.no.such.method",
                new Object[] { GET_MESSAGE, Throwable.class.getName()});
        }
    }

    public Fault modelException(
        String typeUri,
        String wsdlUri,
        Class exceptionClass) {
            
        String className = exceptionClass.getName();
        checkForJavaExceptions(className);
        return createFault(typeUri, wsdlUri, exceptionClass);
    }

    protected void checkForJavaExceptions(String className) {
        if (Names.isInJavaOrJavaxPackage(className)) {
            throw new ModelerException(
                "rmimodeler.java.exceptions.not.allowed",
                className);
        }
    }

    public abstract Fault createFault(
        String typeUri,
        String wsdlUri,
        Class classDef);

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

    // Modified this method to call the static method below for bug fix 4923650
    public void collectMembers(Class classDef, Map members) {
        try {
            if (defRuntimeException.isAssignableFrom(classDef)) {
                throw new ModelerException(
                    "rmimodeler.must.not.extend.runtimeexception",
                    classDef.getName());
            }
            collectExceptionMembers(classDef, members);
        } catch (Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
    }

    // This static method was added to support bug fix: 4923650   
    public static void collectExceptionMembers(Class classDef, Map members) {
        try {
            if (classDef.equals(Throwable.class)) {
                members.put(GET_MESSAGE, GET_MESSAGE_METHOD);
                return;
            }
            if (classDef.getSuperclass() != null)
                collectExceptionMembers(classDef.getSuperclass(), members);
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
                if ((memberName.startsWith("get")
                    || memberName.startsWith("is"))
                    && methods[i].getParameterTypes().length == 0) {
                    //                    if (!members.containsKey(memberName) &&
                    //                        !memberName.equals(GET_LOCALIZED_MESSAGE)) { 
                    if (!members.containsKey(memberName)) {
                        members.put(memberName, methods[i]);
                    }
                }
            }
        } catch (Exception e) {
            throw new ModelerException(new LocalizableExceptionAdapter(e));
        }
    }

    /**
     * returns the Fault for a mapped exception, null if the
     * type is not in the map
     */
    private Fault getMappedFault(String className) {
        return (Fault) faultMap.get(className);
    }

    private void log(ProcessorEnvironment env, String msg) {
        if (env.verbose()) {
            System.out.println(
                "["
                    + Names.stripQualifier(this.getClass().getName())
                    + ": "
                    + msg
                    + "]");
        }
    }
}
