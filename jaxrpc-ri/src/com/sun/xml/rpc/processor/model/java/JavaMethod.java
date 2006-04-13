/*
 * $Id: JavaMethod.java,v 1.2 2006-04-13 01:29:43 ofung Exp $
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

package com.sun.xml.rpc.processor.model.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.xml.rpc.processor.model.ModelException;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JavaMethod {
    
    public JavaMethod() {}
    
    public JavaMethod(String name) {
        this.name = name;
        this.returnType = null;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public JavaType getReturnType() {
        return returnType;
    }
    
    public void setReturnType(JavaType returnType) {
        this.returnType = returnType;
    }
    
    public boolean hasParameter(String paramName) {
        for (int i=0; i<parameters.size();i++) {
            if (paramName.equals(
                ((JavaParameter)parameters.get(i)).getName())) {
                    
                return true;
            }
        }
        return false;
    }
    
    public void addParameter(JavaParameter param) {
        
        // verify that this member does not already exist
        if (hasParameter(param.getName())) {
            throw new ModelException("model.uniqueness");
        }
        parameters.add(param);
    }
    
    public Iterator getParameters() {
        return parameters.iterator();
    }
    
    public int getParameterCount() {
        return parameters.size();
    }
    
    /* serialization */
    public List getParametersList() {
        return parameters;
    }
    
    /* serialization */
    public void setParametersList(List l) {
        parameters = l;
    }
    
    public boolean hasException(String exception) {
        return exceptions.contains(exception);
    }
    
    public void addException(String exception) {
        
        // verify that this exception does not already exist
        if (hasException(exception)) {
            throw new ModelException("model.uniqueness");
        }
        exceptions.add(exception);
    }
    
    public Iterator getExceptions() {
        return exceptions.iterator();
    }
    
    /* serialization */
    public List getExceptionsList() {
        return exceptions;
    }
    
    /* serialization */
    public void setExceptionsList(List l) {
        exceptions = l;
    }
    
    public String getDeclaringClass() {
        return declaringClass;
    }
    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }
    
    private String name;
    private List parameters = new ArrayList();
    private List exceptions = new ArrayList();
    private JavaType returnType;
    private String declaringClass;
}
