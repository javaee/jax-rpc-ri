/*
 * $Id: IDREFSerializerHelper.java,v 1.1 2006-04-12 20:33:13 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.encoding;

/**
 * A valueType which is of xsd:ID type, its serializer should
 * implement this interface in order for IDREF serializers to get the
 * ID string.
 *
 * @author JAX-RPC Development Team
 */
public interface IDREFSerializerHelper {
    /**
     * 
     * @param obj The class instance which contains the ID property. The serializer implementing this 
     *            interface will use this object to invoke the method to get ID string.
     *
     * @return String ID value.
     *
     */

    public String getID(Object obj);
}
