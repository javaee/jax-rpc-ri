/*
 * $Id: SOAPSerializationState.java,v 1.1 2006-04-12 20:33:11 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SOAPSerializationState {

    private Object obj;
    private String id;
    private ReferenceableSerializer serializer;

    public SOAPSerializationState(
        Object obj,
        String id,
        ReferenceableSerializer serializer) {
            
        this.obj = obj;
        this.id = id;
        this.serializer = serializer;
    }

    public Object getObject() {
        return obj;
    }

    public String getID() {
        return id;
    }

    public ReferenceableSerializer getSerializer() {
        return serializer;
    }
}
