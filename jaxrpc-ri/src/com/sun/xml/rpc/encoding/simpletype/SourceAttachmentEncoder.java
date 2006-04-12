/*
 * $Id: SourceAttachmentEncoder.java,v 1.1 2006-04-12 20:34:26 kohlert Exp $
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
public class SourceAttachmentEncoder implements AttachmentEncoder {
    private static final AttachmentEncoder encoder =
        new SourceAttachmentEncoder();

    private SourceAttachmentEncoder() {
    }

    public static AttachmentEncoder getInstance() {
        return encoder;
    }

    public DataHandler objectToDataHandler(Object obj) throws Exception {
        DataHandler dataHandler = new DataHandler(obj, "text/xml");

        return dataHandler;
    }

    public Object dataHandlerToObject(DataHandler dataHandler)
        throws Exception {
        return dataHandler.getContent();
    }
}
