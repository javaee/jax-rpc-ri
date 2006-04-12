/*
 * $Id: JAXRpcMapEntry.java,v 1.1 2006-04-12 20:34:07 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.soap;

import java.io.Serializable;

/**
 *
 * @author JAX-RPC Development Team
 */
public class JAXRpcMapEntry implements Serializable {

    private Object key = null;
    private Object value = null;

    public JAXRpcMapEntry() {
    }

    public JAXRpcMapEntry(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj != null && getClass() == obj.getClass()) {
            JAXRpcMapEntry map_entry = (JAXRpcMapEntry) obj;
            return (
                (key == null && map_entry.key == null)
                    || (key != null && key.equals(map_entry.key)))
                && ((value == null && map_entry.value == null)
                    || (value != null && value.equals(map_entry.value)));
        } else {
            return false;
        }
    }

}
