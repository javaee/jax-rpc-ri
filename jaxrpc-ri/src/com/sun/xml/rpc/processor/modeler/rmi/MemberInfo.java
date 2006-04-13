/*
 * $Id: MemberInfo.java,v 1.2 2006-04-13 01:31:17 ofung Exp $
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
