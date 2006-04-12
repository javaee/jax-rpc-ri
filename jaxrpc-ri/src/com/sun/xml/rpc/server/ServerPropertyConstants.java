/*
 * $Id: ServerPropertyConstants.java,v 1.1 2006-04-12 20:35:25 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.server;

/**
 * @author Vivek Pandey
 *
 * Defines server side constants
 * 
 */
public interface ServerPropertyConstants {
    /*public static final String ATTACHMENT_CONTEXT =
        "com.sun.xml.rpc.attachment.AttachmentContext";*/
    public static final String SET_ATTACHMENT_PROPERTY =
        "com.sun.xml.rpc.attachment.SetAttachmentContext";
    public static final String GET_ATTACHMENT_PROPERTY =
        "com.sun.xml.rpc.attachment.GetAttachmentContext";
}
