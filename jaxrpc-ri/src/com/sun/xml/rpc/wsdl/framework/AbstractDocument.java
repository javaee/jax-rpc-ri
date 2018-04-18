/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * $Id: AbstractDocument.java,v 1.3 2007-07-13 23:36:46 ofung Exp $
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

package com.sun.xml.rpc.wsdl.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * An abstract class for documents containing entities.
 *
 * @author JAX-RPC Development Team
 */
public abstract class AbstractDocument {

    protected AbstractDocument() {
        _kinds = new HashMap();
        _identifiables = new HashMap();
        _importedEntities = new ArrayList();
        _importedDocuments = new HashSet();
        _includedEntities = new ArrayList();
        _includedDocuments = new HashSet();
    }

    public String getSystemId() {
        return _systemId;
    }

    public void setSystemId(String s) {
        if (_systemId != null && !_systemId.equals(s)) {
            // avoid redefinition of a system identifier
            throw new IllegalArgumentException();
        }

        _systemId = s;
        if (s != null) {
            _importedDocuments.add(s);
        }
    }

    public void addIncludedDocument(String systemId) {
        _includedDocuments.add(systemId);
    }

    // bug fix: 6264237, fix for curcular dependency
    public boolean isIncludedDocument(String systemId) {
        return _includedDocuments.contains(systemId) || _importedDocuments.contains(systemId);
    }

    public void addIncludedEntity(Entity entity) {
        _includedEntities.add(entity);
    }

    public void addImportedDocument(String systemId) {
        _importedDocuments.add(systemId);
    }

    // bug fix: 6264237, fix for curcular dependency
    public boolean isImportedDocument(String systemId) {
        return _importedDocuments.contains(systemId) || _includedDocuments.contains(systemId);
    }

    public void addImportedEntity(Entity entity) {
        _importedEntities.add(entity);
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        if (getRoot() != null) {
            action.perform(getRoot());
        }

        for (Iterator iter = _importedEntities.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }

        for (Iterator iter = _includedEntities.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
    }

    public Map getMap(Kind k) {
        Map m = (Map) _kinds.get(k.getName());
        if (m == null) {
            m = new HashMap();
            _kinds.put(k.getName(), m);
        }
        return m;
    }

    public void define(GloballyKnown e) {        
        Map map = getMap(e.getKind());
        if (e.getName() == null)
            return;

        QName name =
            new QName(e.getDefining().getTargetNamespaceURI(), e.getName());
        // bug fix: 6264237, fix for curcular dependency, we dont throw error on duplicate entities.
        //rollback this fix, as it breaks how wsdl is generated

        if (map.containsKey(name))
            throw new DuplicateEntityException(e);
        else
            map.put(name, e);
    }

    public void undefine(GloballyKnown e) {
        Map map = getMap(e.getKind());
        if (e.getName() == null)
            return;
        QName name =
            new QName(e.getDefining().getTargetNamespaceURI(), e.getName());

        if (map.containsKey(name))
            throw new NoSuchEntityException(name);
        else
            map.remove(name);
    }

    public GloballyKnown find(Kind k, QName name) {
        Map map = getMap(k);
        Object result = map.get(name);
        if (result == null)
            throw new NoSuchEntityException(name);
        return (GloballyKnown) result;
    }

    public void defineID(Identifiable e) {
        String id = e.getID();
        if (id == null)
            return;

        if (_identifiables.containsKey(id))
            throw new DuplicateEntityException(e);
        else
            _identifiables.put(id, e);
    }

    public void undefineID(Identifiable e) {
        String id = e.getID();

        if (id == null)
            return;

        if (_identifiables.containsKey(id))
            throw new NoSuchEntityException(id);
        else
            _identifiables.remove(id);
    }

    public Identifiable findByID(String id) {
        Object result = _identifiables.get(id);
        if (result == null)
            throw new NoSuchEntityException(id);
        return (Identifiable) result;
    }

    public Set collectAllQNames() {
        final Set result = new HashSet();
        EntityAction action = new EntityAction() {
            public void perform(Entity entity) {
                entity.withAllQNamesDo(new QNameAction() {
                    public void perform(QName name) {
                        result.add(name);
                    }
                });
                entity.withAllSubEntitiesDo(this);
            }
        };
        withAllSubEntitiesDo(action);
        return result;
    }

    public Set collectAllNamespaces() {
        final Set result = new HashSet();

        EntityAction action = new EntityAction() {
            public void perform(Entity entity) {
                entity.withAllQNamesDo(new QNameAction() {
                    public void perform(QName name) {
                        result.add(name.getNamespaceURI());
                    }
                });

                entity.withAllSubEntitiesDo(this);
            }
        };
        withAllSubEntitiesDo(action);
        return result;
    }

    public void validateLocally() {
        LocallyValidatingAction action = new LocallyValidatingAction();
        withAllSubEntitiesDo(action);
        if (action.getException() != null) {
            throw action.getException();
        }
    }

    public void validate() {
        validate(null);
    }

    public abstract void validate(EntityReferenceValidator validator);

    protected abstract Entity getRoot();

    private Map _kinds;
    private Map _identifiables;
    private String _systemId;
    private Set _importedDocuments;
    private List _importedEntities;
    private Set _includedDocuments;
    private List _includedEntities;

    private class LocallyValidatingAction implements EntityAction {
        public LocallyValidatingAction() {
        }

        public void perform(Entity entity) {
            try {
                entity.validateThis();
                entity.withAllSubEntitiesDo(this);
            } catch (ValidationException e) {
                if (_exception == null) {
                    _exception = e;
                }
            }
        }

        public ValidationException getException() {
            return _exception;
        }

        private ValidationException _exception;
    }
}
