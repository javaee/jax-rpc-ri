/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.EntityAction;
import com.sun.xml.rpc.wsdl.framework.EntityReferenceAction;
import com.sun.xml.rpc.wsdl.framework.EntityReferenceValidator;
import com.sun.xml.rpc.wsdl.framework.GloballyKnown;
import com.sun.xml.rpc.wsdl.framework.Kind;
import com.sun.xml.rpc.wsdl.framework.NoSuchEntityException;
import com.sun.xml.rpc.wsdl.framework.ValidationException;

/**
 * A WSDL document.
 *
 * @author JAX-RPC Development Team
 */
public class WSDLDocument
    extends AbstractDocument
    implements com.sun.xml.rpc.spi.tools.WSDLDocument {

    public WSDLDocument() {
    }

    public Definitions getDefinitions() {
        return _definitions;
    }

    public void setDefinitions(Definitions d) {
        _definitions = d;
    }

    public Set collectAllNamespaces() {
        Set result = super.collectAllNamespaces();
        if (_definitions.getTargetNamespaceURI() != null) {
            result.add(_definitions.getTargetNamespaceURI());
        }
        return result;
    }

    public QName[] getAllServiceQNames() {

        ArrayList serviceQNames = new ArrayList();

        for (Iterator iter = getDefinitions().services(); iter.hasNext();) {
            Service next = (Service) iter.next();
            String targetNamespace = next.getDefining().getTargetNamespaceURI();
            String localName = next.getName();
            QName serviceQName = new QName(targetNamespace, localName);
            serviceQNames.add(serviceQName);
        }
        return (QName[]) serviceQNames.toArray(new QName[serviceQNames.size()]);
    }

    public QName[] getAllPortQNames() {
        ArrayList portQNames = new ArrayList();

        for (Iterator iter = getDefinitions().services(); iter.hasNext();) {
            Service next = (Service) iter.next();
            //Iterator ports = next.ports();
            for (Iterator piter = next.ports(); piter.hasNext();) {
                // If it's a relative import
                Port pnext = (Port) piter.next();
                String targetNamespace =
                    pnext.getDefining().getTargetNamespaceURI();
                String localName = pnext.getName();
                QName portQName = new QName(targetNamespace, localName);
                portQNames.add(portQName);
            }
        }
        return (QName[]) portQNames.toArray(new QName[portQNames.size()]);
    }

    public QName[] getPortQNames(String serviceNameLocalPart) {

        ArrayList portQNames = new ArrayList();

        for (Iterator iter = getDefinitions().services(); iter.hasNext();) {
            Service next = (Service) iter.next();
            if (next.getName().equals(serviceNameLocalPart)) {
                for (Iterator piter = next.ports(); piter.hasNext();) {
                    Port pnext = (Port) piter.next();
                    String targetNamespace =
                        pnext.getDefining().getTargetNamespaceURI();
                    String localName = pnext.getName();
                    QName portQName = new QName(targetNamespace, localName);
                    portQNames.add(portQName);
                }
            }
        }
        return (QName[]) portQNames.toArray(new QName[portQNames.size()]);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        _definitions.accept(visitor);
    }

    public void validate(EntityReferenceValidator validator) {
        GloballyValidatingAction action =
            new GloballyValidatingAction(this, validator);
        withAllSubEntitiesDo(action);
        if (action.getException() != null) {
            throw action.getException();
        }
    }

    protected Entity getRoot() {
        return _definitions;
    }

    private Definitions _definitions;

    private class GloballyValidatingAction
        implements EntityAction, EntityReferenceAction {
        public GloballyValidatingAction(
            AbstractDocument document,
            EntityReferenceValidator validator) {
            _document = document;
            _validator = validator;
        }

        public void perform(Entity entity) {
            try {
                entity.validateThis();
                entity.withAllEntityReferencesDo(this);
                entity.withAllSubEntitiesDo(this);
            } catch (ValidationException e) {
                if (_exception == null) {
                    _exception = e;
                }
            }
        }

        public void perform(Kind kind, QName name) {
            try {
                GloballyKnown entity = _document.find(kind, name);
            } catch (NoSuchEntityException e) {
                // failed to resolve, check with the validator
                if (_exception == null) {
                    if (_validator == null
                        || !_validator.isValid(kind, name)) {
                        _exception = e;
                    }
                }
            }
        }

        public ValidationException getException() {
            return _exception;
        }

        private ValidationException _exception;
        private AbstractDocument _document;
        private EntityReferenceValidator _validator;
    }
}
