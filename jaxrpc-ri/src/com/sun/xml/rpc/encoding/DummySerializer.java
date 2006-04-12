/*
 * $Id: DummySerializer.java,v 1.1 2006-04-12 20:33:10 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.*;
import javax.xml.namespace.QName;
import javax.activation.DataHandler;

/**
 * @author JAX-RPC Development Team
 */
public class DummySerializer implements CombinedSerializer {

    private static final DummySerializer _instance = new DummySerializer();

    public static DummySerializer getInstance() {
        return _instance;
    }

    private DummySerializer() {
    }

    public void serialize(
        Object obj,
        QName name,
        SerializerCallback callback,
        XMLWriter writer,
        SOAPSerializationContext context) {
    }

    public Object deserialize(
        QName name,
        XMLReader element,
        SOAPDeserializationContext context) {

        return null;
    }

    public Object deserialize(
        DataHandler dataHandler,
        SOAPDeserializationContext context) {

        return null;
    }

    public QName getXmlType() {
        throw new UnsupportedOperationException();
    }

    public boolean getEncodeType() {
        throw new UnsupportedOperationException();
    }

    public boolean isNullable() {
        throw new UnsupportedOperationException();
    }

    public String getEncodingStyle() {
        throw new UnsupportedOperationException();
    }

    public CombinedSerializer getInnermostSerializer() {
        return this;
    }

    public String getMechanismType() {
        return com.sun.xml.rpc.encoding.EncodingConstants.JAX_RPC_RI_MECHANISM;
    }
}
