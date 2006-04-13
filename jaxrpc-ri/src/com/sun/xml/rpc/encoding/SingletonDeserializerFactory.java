/*
 * $Id: SingletonDeserializerFactory.java,v 1.2 2006-04-13 01:27:25 ofung Exp $
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