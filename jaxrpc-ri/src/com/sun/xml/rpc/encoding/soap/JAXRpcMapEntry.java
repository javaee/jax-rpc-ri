/*
 * $Id: JAXRpcMapEntry.java,v 1.2 2006-04-13 01:28:08 ofung Exp $
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
