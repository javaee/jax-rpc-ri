/*
 * $Id: MIMEMultipartRelated.java,v 1.1 2006-04-12 20:35:15 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.document.mime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.EntityAction;
import com.sun.xml.rpc.wsdl.framework.Extension;
import com.sun.xml.rpc.wsdl.framework.ExtensionVisitor;

/**
 * A MIME multipartRelated extension.
 *
 * @author JAX-RPC Development Team
 */
public class MIMEMultipartRelated extends Extension {

    public MIMEMultipartRelated() {
        _parts = new ArrayList();
    }

    public QName getElementName() {
        return MIMEConstants.QNAME_MULTIPART_RELATED;
    }

    public void add(MIMEPart part) {
        _parts.add(part);
    }

    public Iterator getParts() {
        return _parts.iterator();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        super.withAllSubEntitiesDo(action);

        for (Iterator iter = _parts.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
    }

    public void accept(ExtensionVisitor visitor) throws Exception {
        visitor.preVisit(this);
        visitor.postVisit(this);
    }

    public void validateThis() {
    }

    private List _parts;
}
