/*
 * $Id: SingletonDeserializerFactory.java,v 1.1 2006-04-12 20:33:09 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import java.util.Iterator;

import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.DeserializerFactory;

import com.sun.xml.rpc.util.SingleElementIterator;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SingletonDeserializerFactory implements DeserializerFactory {
    protected Deserializer deserializer;

    public SingletonDeserializerFactory(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    public Deserializer getDeserializerAs(String mechanismType) {
        if (!EncodingConstants.JAX_RPC_RI_MECHANISM.equals(mechanismType)) {
            throw new TypeMappingException(
                "typemapping.mechanism.unsupported",
                mechanismType);
        }
        return deserializer;
    }

    public Iterator getSupportedMechanismTypes() {
        return new SingleElementIterator(
            EncodingConstants.JAX_RPC_RI_MECHANISM);
    }

    public Iterator getDeserializers() {
        return new SingleElementIterator(deserializer);
    }
}