/*
 * $Id: Fault.java,v 1.3 2007-07-13 23:36:41 ofung Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.rpc.wsdl.document;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.EntityReferenceAction;
import com.sun.xml.rpc.wsdl.framework.QNameAction;

/**
 * Entity corresponding to the "fault" child element of a port type operation.
 *
 * @author JAX-RPC Development Team
 */
public class Fault extends Entity {

    public Fault() {
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public QName getMessage() {
        return _message;
    }

    public void setMessage(QName n) {
        _message = n;
    }

    public Message resolveMessage(AbstractDocument document) {
        return (Message) document.find(Kinds.MESSAGE, _message);
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_FAULT;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void withAllQNamesDo(QNameAction action) {
        if (_message != null) {
            action.perform(_message);
        }
    }

    public void withAllEntityReferencesDo(EntityReferenceAction action) {
        super.withAllEntityReferencesDo(action);
        if (_message != null) {
            action.perform(Kinds.MESSAGE, _message);
        }
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        visitor.postVisit(this);
    }

    public void validateThis() {
        if (_name == null) {
            failValidation("validation.missingRequiredAttribute", "name");
        }
        if (_message == null) {
            failValidation("validation.missingRequiredAttribute", "message");
        }
    }

    private Documentation _documentation;
    private String _name;
    private QName _message;
}
