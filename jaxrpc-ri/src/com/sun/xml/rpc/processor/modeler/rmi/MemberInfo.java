/*
 * $Id: MemberInfo.java,v 1.1 2006-04-12 20:33:02 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.modeler.rmi;

/**
 *
 * @author JAX-RPC Development Team
 */
public class MemberInfo {
    private RmiType type;
    private boolean isPublic = false;
    private String readMethod;
    private String writeMethod;
    private String name;
    private Class declaringClass;
    private Class sortingClass;

    private MemberInfo() {
    }

    public MemberInfo(String name, RmiType type, boolean isPublic) {

        this.type = type;
        this.isPublic = isPublic;
        this.name = name;
    }
    public MemberInfo(RmiType type, boolean isPublic) {
        this.type = type;
        this.isPublic = isPublic;
    }
    public RmiType getType() {
        return type;
    }
    public boolean isPublic() {
        return isPublic;
    }
    public String getReadMethod() {
        return readMethod;
    }
    public void setReadMethod(String readMethod) {
        this.readMethod = readMethod;
    }
    public String getWriteMethod() {
        return writeMethod;
    }
    public void setWriteMethod(String writeMethod) {
        this.writeMethod = writeMethod;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Class getDeclaringClass() {
        return declaringClass;
    }
    public void setDeclaringClass(Class declaringClass) {
        this.declaringClass = declaringClass;
    }

    public Class getSortingClass() {
        return sortingClass;
    }
    public void setSortingClass(Class sortingClass) {
        this.sortingClass = sortingClass;
    }
}
