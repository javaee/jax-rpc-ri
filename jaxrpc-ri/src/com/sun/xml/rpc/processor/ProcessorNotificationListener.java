/*
 * $Id: ProcessorNotificationListener.java,v 1.1 2006-04-12 20:34:54 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor;

import com.sun.xml.rpc.util.localization.Localizable;

/**
 * A ProcessorNotificationListener is registered with a Processor and receives
 * notifications of errors, warnings and informational messages.
 *
 * @author JAX-RPC Development Team
 */
public interface ProcessorNotificationListener {
    public void onError(Localizable msg);
    public void onWarning(Localizable msg);
    public void onInfo(Localizable msg);
}
