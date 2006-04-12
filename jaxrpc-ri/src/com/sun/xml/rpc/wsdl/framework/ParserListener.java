/*
 * $Id: ParserListener.java,v 1.1 2006-04-12 20:33:00 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.namespace.QName;

/**
 * A listener for parsing-related events.
 *
 * @author JAX-RPC Development Team
 */
public interface ParserListener {
    public void ignoringExtension(QName name, QName parent);
    public void doneParsingEntity(QName element, Entity entity);
}
