/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.framework.Defining;
import com.sun.xml.rpc.wsdl.framework.DuplicateEntityException;
import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.EntityAction;
import com.sun.xml.rpc.wsdl.framework.ExtensibilityHelper;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;

/**
 * Entity corresponding to the "definitions" WSDL element.
 *
 * @author JAX-RPC Development Team
 */
public class Definitions extends Entity implements Defining, Extensible {

    public Definitions(AbstractDocument document) {
        _document = document;
        _bindings = new ArrayList();
        _imports = new ArrayList();
        _messages = new ArrayList();
        _portTypes = new ArrayList();
        _services = new ArrayList();
        _importedNamespaces = new HashSet();
        _helper = new ExtensibilityHelper();
    }

    public String getName() {
        return _name;
    }

    public void setName(String s) {
        _name = s;
    }

    public String getTargetNamespaceURI() {
        return _targetNsURI;
    }

    public void setTargetNamespaceURI(String s) {
        _targetNsURI = s;
    }

    public void setTypes(Types t) {
        _types = t;
    }

    public Types getTypes() {
        return _types;
    }

    public void add(Message m) {
    	try{
            _document.define(m);
        }catch(DuplicateEntityException e){            
            return;
        }
        //_document.define(m);
        _messages.add(m);
    }

    public void add(PortType p) {
    	try{
            _document.define(p);
        }catch(DuplicateEntityException e){            
            return;
        }
        //_document.define(p);
        _portTypes.add(p);
    }

    public void add(Binding b) {
    	try{
            _document.define(b);
        }catch(DuplicateEntityException e){            
            return;
        }
        //_document.define(b);
        _bindings.add(b);
    }

    public void add(Service s) {
    	try{
            _document.define(s);
        }catch(DuplicateEntityException e){            
            return;
        }
        //_document.define(s);
        _services.add(s);
    }

    public void addServiceOveride(Service s) {
        //  _document.define(s);  //kw used for spi
        _services.add(s);
    }

    public void add(Import i) {
        if (_importedNamespaces.contains(i.getNamespace())) {
            throw new DuplicateEntityException(i, i.getNamespace());
        }

        _imports.add(i);
    }

    public Iterator imports() {
        return _imports.iterator();
    }

    public Iterator messages() {
        return _messages.iterator();
    }

    public Iterator portTypes() {
        return _portTypes.iterator();
    }

    public Iterator bindings() {
        return _bindings.iterator();
    }

    public Iterator services() {
        return _services.iterator();
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_DEFINITIONS;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void addExtension(Extension e) {
        _helper.addExtension(e);
    }

    public Iterator extensions() {
        return _helper.extensions();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        if (_types != null) {
            action.perform(_types);
        }
        for (Iterator iter = _messages.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
        for (Iterator iter = _portTypes.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
        for (Iterator iter = _bindings.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
        for (Iterator iter = _services.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
        for (Iterator iter = _imports.iterator(); iter.hasNext();) {
            action.perform((Entity) iter.next());
        }
        _helper.withAllSubEntitiesDo(action);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);

        for (Iterator iter = _imports.iterator(); iter.hasNext();) {
            ((Import) iter.next()).accept(visitor);
        }

        if (_types != null) {
            _types.accept(visitor);
        }

        for (Iterator iter = _messages.iterator(); iter.hasNext();) {
            ((Message) iter.next()).accept(visitor);
        }
        for (Iterator iter = _portTypes.iterator(); iter.hasNext();) {
            ((PortType) iter.next()).accept(visitor);
        }
        for (Iterator iter = _bindings.iterator(); iter.hasNext();) {
            ((Binding) iter.next()).accept(visitor);
        }
        for (Iterator iter = _services.iterator(); iter.hasNext();) {
            ((Service) iter.next()).accept(visitor);
        }

        _helper.accept(visitor);
        visitor.postVisit(this);
    }

    public void validateThis() {
    }

    private AbstractDocument _document;
    private ExtensibilityHelper _helper;
    private Documentation _documentation;
    private String _name;
    private String _targetNsURI;
    private Types _types;
    private List _messages;
    private List _portTypes;
    private List _bindings;
    private List _services;
    private List _imports;
    private Set _importedNamespaces;
}
