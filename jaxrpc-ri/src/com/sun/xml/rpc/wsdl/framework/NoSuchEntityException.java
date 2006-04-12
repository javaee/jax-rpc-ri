/*
 * $Id: NoSuchEntityException.java,v 1.1 2006-04-12 20:32:59 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.namespace.QName;

/**
 * An exception signalling that an entity with the given name/id does not exist.
 *
 * @author JAX-RPC Development Team
 */
public class NoSuchEntityException extends ValidationException {

    public NoSuchEntityException(QName name) {
        super(
            "entity.notFoundByQName",
            new Object[] { name.getLocalPart(), name.getNamespaceURI()});
    }

    public NoSuchEntityException(String id) {
        super("entity.notFoundByID", id);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdl";
    }
}
