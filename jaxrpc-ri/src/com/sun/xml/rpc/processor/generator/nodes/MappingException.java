/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.generator.nodes;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;

public class MappingException extends JAXRPCExceptionBase {

    public MappingException(String key, Object[] args) {
        super(key, args);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.j2ee";
    }
}