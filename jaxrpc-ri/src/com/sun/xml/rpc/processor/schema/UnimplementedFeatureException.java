/*
 * $Id: UnimplementedFeatureException.java,v 1.1 2006-04-12 20:35:05 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.schema;

import com.sun.xml.rpc.processor.model.ModelException;

/**
 * A FeatureNotSupportedException is a ModelException signaling that
 * an unsupported XML Schema feature was encountered during processing.
 *
 * @author JAX-RPC Development Team
 */
public class UnimplementedFeatureException extends ModelException {
    
    public UnimplementedFeatureException(String arg) {
        super("model.schema.notImplemented", arg);
    }
}
