/*
 * $Id: ExtendedTypeMapping.java,v 1.1 2006-04-12 20:33:12 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;

/**
 * An extension of the standard TypeMapping interface
 *
 * @author JAX-RPC Development Team
 */
public interface ExtendedTypeMapping extends TypeMapping {
    public Class getJavaType(QName xmlType);
    public QName getXmlType(Class javaType);
}
