/*
 * $Id: Stream.java,v 1.1 2006-04-12 20:32:47 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

/**
 * @author JAX-RPC Development Team
 */

public interface Stream {
    public int next(Event event);
}
