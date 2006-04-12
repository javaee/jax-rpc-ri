/*
 * $Id: ReferenceableSerializer.java,v 1.1 2006-04-12 20:33:16 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.*;
import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface ReferenceableSerializer extends JAXRPCSerializer {
    public void serializeInstance(
        Object obj,
        QName name,
        boolean isMultiRef,
        XMLWriter writer,
        SOAPSerializationContext context);
}
