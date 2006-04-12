/*
 * $Id: Names11.java,v 1.1 2006-04-12 20:33:47 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.generator;

/**
 * @author JAX-RPC Development Team
 *
 * JAXRPC 1.1 Names class
 */
public class Names11 extends Names {

    private String makeSafeClassName(String basePackage, String className) {
        if (className.startsWith("java.") || className.startsWith("javax."))
            className = basePackage + ".serializers." + className;
        return className;
    }

    protected String serializerClassName(
        String basePackage,
        String className,
        String suffix) {
        className = makeSafeClassName(basePackage, className);
        if (serializerNameInfix != null)
            className += serializerNameInfix;
        return (className + suffix).replace('$', '_');
    }

    protected String builderClassName(
        String basePackage,
        String className,
        String suffix) {
        className = makeSafeClassName(basePackage, className);
        if (serializerNameInfix != null)
            className += serializerNameInfix;
        return (className + suffix).replace('$', '_');
    }

}
