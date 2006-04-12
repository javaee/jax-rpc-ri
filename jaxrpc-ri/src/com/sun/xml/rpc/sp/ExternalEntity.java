/*
 * $Id: ExternalEntity.java,v 1.1 2006-04-12 20:34:21 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.sp;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 *
 * @author JAX-RPC RI Development Team
 */
class ExternalEntity extends EntityDecl {
    String systemId; // resolved URI (not relative)
    String publicId; // "-//xyz//....//en"
    String notation;

    public ExternalEntity(Locator l) {
    }

    public InputSource getInputSource(EntityResolver r)
        throws SAXException, IOException {
        InputSource retval;

        retval = r.resolveEntity(publicId, systemId);
        // SAX sez if null is returned, use the URI directly
        if (retval == null)
            retval = Resolver.createInputSource(new URL(systemId), false);
        return retval;
    }
}
