/*
 * $Id: SimpleMultiTypeSerializer.java,v 1.1 2006-04-12 20:33:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;

/**
 *
 * @author JAX-RPC Development Team
 */
public class SimpleMultiTypeSerializer extends SimpleTypeSerializer {

    private Set supportedTypes;

    public SimpleMultiTypeSerializer(
        QName type,
        boolean encodeType,
        boolean isNullable,
        String encodingStyle,
        SimpleTypeEncoder encoder,
        QName[] types) {
            
        super(type, encodeType, isNullable, encodingStyle, encoder);
        supportedTypes = new HashSet();
        for (int i = 0; i < types.length; i++)
            supportedTypes.add(types[i]);
    }

    protected boolean isAcceptableType(QName actualType) {
        return supportedTypes.contains(actualType);
    }
}
