/*
 * $Id: ProcessorAction.java,v 1.1 2006-04-12 20:34:53 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor;

import java.util.Properties;

import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.config.Configuration;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface ProcessorAction {
    public void perform(Model model, Configuration config, Properties options);
}
