/*
 * $Id: DuplicateEntityException.java,v 1.1 2006-04-12 20:32:56 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.framework;

/**
 * An exception signalling that an entity with the given name/id has already been defined.
 *
 * @author JAX-RPC Development Team
 */
public class DuplicateEntityException extends ValidationException {

    public DuplicateEntityException(GloballyKnown entity) {
        super(
            "entity.duplicateWithType",
            new Object[] {
                entity.getElementName().getLocalPart(),
                entity.getName()});
    }

    public DuplicateEntityException(Identifiable entity) {
        super(
            "entity.duplicateWithType",
            new Object[] {
                entity.getElementName().getLocalPart(),
                entity.getID()});
    }

    public DuplicateEntityException(Entity entity, String name) {
        super(
            "entity.duplicateWithType",
            new Object[] { entity.getElementName().getLocalPart(), name });
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.wsdl";
    }
}
