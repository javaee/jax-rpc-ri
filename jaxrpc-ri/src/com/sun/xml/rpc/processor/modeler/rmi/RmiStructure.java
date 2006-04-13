/*
 * $Id: RmiStructure.java,v 1.2 2006-04-13 01:31:19 ofung Exp $
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;

/**
 *
 * @author JAX-RPC Development Team
 */
public class RmiStructure implements RmiConstants {

    private static RmiStructure forClass(
        ProcessorEnvironment env,
        Class implClassDef) {
            
        RmiStructure sc = new RmiStructure(env, implClassDef);
        sc.initialize();
        return sc;
    }

    public static Map modelTypeSOAP(ProcessorEnvironment env, RmiType type) {
        Class cDec = null;
        RmiStructure rt = null;
        try {
            cDec = type.getTypeClass(env.getClassLoader());
            rt = RmiStructure.forClass(env, cDec);
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                "rmimodeler.class.not.found",
                type.toString());
        }
        if (rt == null) {
            return null;
        }
        return rt.getMembers();
    }

    private HashMap getMembers() {
        return (HashMap) members.clone();
    }

    /** wscompile environment for this object */
    private ProcessorEnvironment env;

    /** the remote implementation class this object corresponds to */
    private Class implClassDef;

    /** all the properties of this class */
    private HashMap members;

    /** cached definition for certain classes used in this environment */
    private Class defRemote;

    /**
     * Create a RmiStructure instance for the given class.  The resulting
     * object is not yet initialized.
     */
    private RmiStructure(ProcessorEnvironment env, Class implClassDef) {
        this.env = env;
        this.implClassDef = implClassDef;
    }

    /**
     * Validate that the serializable implementation class is properly formed
     * and fill in the data structures required by the public interface.
     */
    private void initialize() {
        //        defRemote = java.rmi.Remote.class; //Class.forName(REMOTE_CLASSNAME);
        try {
            defRemote =
                RmiUtils.getClassForName(
                    REMOTE_CLASSNAME,
                    env.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                "rmimodeler.class.not.found",
                RuntimeException.class.getName());
        }

        // if it is not an interface or an abstract class
        if (!(implClassDef.isInterface()
            || Modifier.isAbstract(implClassDef.getModifiers()))) {

            // check for package accessible empty contructor
            boolean hasDefaultConstructor = false;
            try {
                hasDefaultConstructor =
                    implClassDef.getConstructor(new Class[0]) != null
                        ? true
                        : false;
            } catch (NoSuchMethodException e) {
            }
            if (!hasDefaultConstructor) {
                throw new ModelerException(
                    "rmimodeler.no.empty.constructor",
                    implClassDef.getName().toString());
            }
        }

        members = new HashMap();

        /*
         * Here we find all of the remote interfaces of our remote
         * implementation class.  For each class up the superclass
         * chain, add each directly-implemented interface that
         * somehow extends Remote to a list.
         */
        Vector interfacesImplemented = new Vector();
        // list of remote interfaces found

        interfacesImplemented.addElement(implClassDef);
        if (defRemote.isAssignableFrom(implClassDef)) {
            log(
                env,
                "remote interface implemented by: " + implClassDef.getName());
            throw new ModelerException(
                "rmimodeler.type.cannot.implement.remote",
                implClassDef.getName());
        }
        /*
         * Now we collect the structure members by finding set/get/is methods
         * from all of the interfaces into the properties hashtable.
         */
        if (!collectMembers(implClassDef, members)) {
            members = new HashMap();
        }
    }

    public boolean collectMembers(Class interfaceDef, HashMap map) {

        /*
         * Search interface's members for methods.
         */
        Field[] fields = interfaceDef.getFields();
        MemberInfo memInfo;
        for (int i = 0; fields != null && i < fields.length; i++) {
            int modifier = fields[i].getModifiers();
            if (!Modifier.isPublic(modifier)
                || (Modifier.isFinal(modifier) && Modifier.isStatic(modifier))
                || Modifier.isTransient(
                    modifier)) { // no final static, transient, non-public
                continue;
            }
            // can't already have one from subclass
            if (map.get(fields[i].getName()) == null) {
                memInfo =
                    new MemberInfo(
                        fields[i].getName(),
                        RmiType.getRmiType(fields[i].getType()),
                        true);
                if (fields[i].getDeclaringClass().equals(interfaceDef)) {
                    memInfo.setDeclaringClass(fields[i].getDeclaringClass());
                }
                map.put(fields[i].getName(), memInfo);
            }
        }
        return true;
    }

    private static void log(ProcessorEnvironment env, String msg) {
        if (env.verbose()) {
            System.out.println("[RmiStructure: " + msg + "]");
        }
    }
}
