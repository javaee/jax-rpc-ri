/*
 * $Id: ProcessorActionsIf.java,v 1.1 2006-04-12 20:33:18 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.tools.wscompile;

import com.sun.xml.rpc.processor.Processor;

/**
 * @author JAX-RPC Development Team
 *
 */
public interface ProcessorActionsIf {
    public void registerActions(Processor processor);
}
