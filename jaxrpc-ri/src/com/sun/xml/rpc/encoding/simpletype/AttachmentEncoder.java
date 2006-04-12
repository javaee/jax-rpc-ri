/*
 * $Id: AttachmentEncoder.java,v 1.1 2006-04-12 20:34:30 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import javax.activation.DataHandler;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface AttachmentEncoder {
    public DataHandler objectToDataHandler(Object obj) throws Exception;
    public Object dataHandlerToObject(DataHandler dataHandler)
        throws Exception;
}
