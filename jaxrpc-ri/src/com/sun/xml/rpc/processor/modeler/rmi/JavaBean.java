/*
 * $Id: JavaBean.java,v 1.1 2006-04-12 20:33:02 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.modeler.rmi;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.ProcessorEnvironment;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JavaBean implements RmiConstants {

    private static JavaBean forType(ProcessorEnvironment env, RmiType type) {
        JavaBean bean = null;
        try {
            String implClassName = type.getClassName();
            //URLClassLoader classLoader = env.getClassLoader();
			//Frank: Added new classloader for JAX-RPC that provides a way to shutdown the classloader
			ClassLoader classLoader = env.getClassLoader();
            Class beanClass = classLoader.loadClass(implClassName);
            bean = new JavaBean(env, beanClass);
            bean.initialize();
        } catch (ClassNotFoundException e) {
            throw new ModelerException(
                RMI_MODELER_NESTED_RMI_MODELER_ERROR,
                new LocalizableExceptionAdapter(e));
        }
        return bean;
    }

    public static Map modelTypeSOAP(ProcessorEnvironment env, RmiType type) {

        JavaBean bean = JavaBean.forType(env, type);
        if (bean == null) {
            return null;
        }
        return bean.getMembers();
    }

    private HashMap getMembers() {
        return (HashMap) members.clone();
    }

    /** all the properties of this class */
    private HashMap members;

    private Class remoteBean;

    ProcessorEnvironment env;

    private JavaBean(ProcessorEnvironment env, Class remoteBean) {
        this.env = env;
        this.remoteBean = remoteBean;
    }

    private void initialize() {
        members = new HashMap();
        collectMembers(remoteBean, members, remoteBean);
    }

    private static void collectMembers(
        Class remoteBean,
        Map members,
        Class baseBean) {
            
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(remoteBean);
        } catch (java.beans.IntrospectionException e) {
            throw new ModelerException(
                "rmimodeler.invalid.rmi.type:",
                remoteBean.getName().toString());
        }
        PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        Method readMethod;
        Method writeMethod;
        Class propertyType;
        MemberInfo memInfo;
        for (int i = 0; i < properties.length; i++) {
            // we now accept indexed properties as a fix to BugID: 4700629
            /*            if (properties[i] instanceof IndexedPropertyDescriptor) {
                            continue;
                        }
             */
            propertyType = properties[i].getPropertyType();
            readMethod = properties[i].getReadMethod();
            writeMethod = properties[i].getWriteMethod();
            if (propertyType == null
                || readMethod == null
                || writeMethod == null
                || readMethod.getParameterTypes().length != 0
                || writeMethod.getParameterTypes().length != 1) {
                continue;
            }
            String propertyName = properties[i].getName();
            RmiType type = RmiType.getRmiType(readMethod.getReturnType());
            if (type == null) {
                throw new ModelerException(
                    "rmimodeler.could.not.resolve.property.type",
                    remoteBean.getName() + ":" + propertyName);
            }
            memInfo = new MemberInfo(propertyName, type, false);
            memInfo.setReadMethod(readMethod.getName());
            memInfo.setWriteMethod(writeMethod.getName());
            if (!writeMethod.getDeclaringClass().equals(baseBean)) {
                memInfo.setDeclaringClass(writeMethod.getDeclaringClass());
            }
            members.put(propertyName, memInfo);
        }
        if (remoteBean.getSuperclass() != null)
            collectMembers(remoteBean.getSuperclass(), members, baseBean);
        Class[] interfaces = remoteBean.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            collectMembers(interfaces[i], members, baseBean);
        }
    }

    private static void log(ProcessorEnvironment env, String msg) {
        if (env.verbose()) {
            System.out.println("[JavaBean: " + msg + "]");
        }
    }
}
