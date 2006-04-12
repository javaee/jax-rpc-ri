/*
 * $Id: DataHandlerAttachmentEncoder.java,v 1.1 2006-04-12 20:34:32 kohlert Exp $
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
public class DataHandlerAttachmentEncoder implements AttachmentEncoder {
    private static final AttachmentEncoder encoder =
        new DataHandlerAttachmentEncoder();

    private DataHandlerAttachmentEncoder() {
    }

    public static AttachmentEncoder getInstance() {
        return encoder;
    }

    public DataHandler objectToDataHandler(Object obj) throws Exception {
        return (DataHandler) obj;
    }

    public Object dataHandlerToObject(DataHandler dataHandler)
        throws Exception {
        return dataHandler;
    }
}
