/*
 * $Id: MimeMultipartAttachmentEncoder.java,v 1.1 2006-04-12 20:34:31 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.encoding.simpletype;

import javax.activation.DataHandler;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author JAX-RPC Development Team
 */
public class MimeMultipartAttachmentEncoder implements AttachmentEncoder {
    private static final AttachmentEncoder encoder =
        new MimeMultipartAttachmentEncoder();

    private MimeMultipartAttachmentEncoder() {
    }

    public static AttachmentEncoder getInstance() {
        return encoder;
    }

    public DataHandler objectToDataHandler(Object obj) throws Exception {
        String contentType = ((MimeMultipart) obj).getContentType();
        DataHandler dataHandler = new DataHandler(obj, contentType);

        return dataHandler;
    }

    public Object dataHandlerToObject(DataHandler dataHandler)
        throws Exception {
            
        return dataHandler.getContent();
    }
}
