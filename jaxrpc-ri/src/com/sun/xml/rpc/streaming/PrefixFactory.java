/*
 * $Id: PrefixFactory.java,v 1.1 2006-04-12 20:32:52 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.streaming;

/**
 * <p> Interface for prefix factories. </p>
 *
 * <p> A prefix factory is able to create a new prefix for a URI that
 * was encountered for the first time when writing a document
 * using an XMLWriter. </p>
 *
 * @author JAX-RPC Development Team
 */
public interface PrefixFactory {
    /**
     * Return a brand new prefix for the given URI.
     */
    public String getPrefix(String uri);
}
