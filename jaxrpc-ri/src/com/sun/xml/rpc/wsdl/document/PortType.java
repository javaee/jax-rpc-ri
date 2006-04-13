/*
 * $Id: PortType.java,v 1.2 2006-04-13 01:34:11 ofung Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.xml.rpc.wsdl.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.Defining;
import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.EntityAction;
import com.sun.xml.rpc.wsdl.framework.GlobalEntity;
import com.sun.xml.rpc.wsdl.framework.Kind;
import com.sun.xml.rpc.wsdl.framework.ValidationException;

/**
 * Entity corresponding to the "portType" WSDL element.
 *
 * @author JAX-RPC Development Team
 */
public class PortType extends GlobalEntity {

    public PortType(Defining defining) {
        super(defining);
        _operations = new ArrayList();
        _operationKeys = new HashSet();
    }

    public void add(Operation operation) {
        String key = operation.getUniqueKey();
        if (_operationKeys.contains(key))
            throw new ValidationException(
                "validation.ambiguousName",
                operation.getName());
        _operationKeys.add(key);
        _operations.add(operation);
    }

    public Iterator operations() {
        return _operations.iterator();
    }

    public Set getOperationsNamed(String s) {
        Set result = new HashSet();
        for (Iterator iter = _operations.iterator(); iter.hasNext();) {
            Operation operation = (Operation) iter.next();
            if (operation.getName().equals(s)) {
                result.add(operation);
            }
        }
        return result;
    }

    public Kind getKind() {
        return Kinds.PORT_TYPE;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_PORT_TYPE;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        super.withAllSubEntitiesDo(action);

        for (Iterator iter = _operations.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        for (Iterator iter = _operations.iterator(); iter.hasNext();) {
            ((Operation) iter.next()).accept(visitor);
        }
        visitor.postVisit(this);
    }

    public void validateThis() {
        if (getName() == null) {
            failValidation("validation.missingRequiredAttribute", "name");
        }
    }

    private Documentation _documentation;
    private List _operations;
    private Set _operationKeys;
}
