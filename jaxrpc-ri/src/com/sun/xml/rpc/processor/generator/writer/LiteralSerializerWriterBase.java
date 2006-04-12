/*
 * $Id: LiteralSerializerWriterBase.java,v 1.1 2006-04-12 20:35:11 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;

/**
 *
 * @author JAX-RPC Development Team
 */
public abstract class LiteralSerializerWriterBase
    extends SerializerWriterBase {

    public LiteralSerializerWriterBase(AbstractType type, Names names) {
        super(type, names);
    }

    protected String getEncodingStyleString() {
        return "\"\"";
    }
}
