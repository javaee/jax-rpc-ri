/*
 * $Id: SerializerWriterFactory.java,v 1.1 2006-04-12 20:35:11 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * SerializerWriter.java
 *
 * Created on January 11, 2002, 12:58 PM
 */

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.encoding.InternalEncodingConstants;
import com.sun.xml.rpc.processor.model.AbstractType;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract interface SerializerWriterFactory
    extends InternalEncodingConstants {
    public SerializerWriter createWriter(String basePackage, AbstractType type);
}
