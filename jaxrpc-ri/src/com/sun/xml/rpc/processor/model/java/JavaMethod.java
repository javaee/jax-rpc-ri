/*
 * $Id: JavaMethod.java,v 1.1 2006-04-12 20:34:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
