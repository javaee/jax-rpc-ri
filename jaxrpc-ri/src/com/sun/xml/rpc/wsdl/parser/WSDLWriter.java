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
 * $Id: WSDLWriter.java,v 1.3 2007-07-13 23:36:49 ofung Exp $
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

package com.sun.xml.rpc.wsdl.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.document.Binding;
import com.sun.xml.rpc.wsdl.document.BindingFault;
import com.sun.xml.rpc.wsdl.document.BindingInput;
import com.sun.xml.rpc.wsdl.document.BindingOperation;
import com.sun.xml.rpc.wsdl.document.BindingOutput;
import com.sun.xml.rpc.wsdl.document.Definitions;
import com.sun.xml.rpc.wsdl.document.Documentation;
import com.sun.xml.rpc.wsdl.document.Fault;
import com.sun.xml.rpc.wsdl.document.Import;
import com.sun.xml.rpc.wsdl.document.Input;
import com.sun.xml.rpc.wsdl.document.Message;
import com.sun.xml.rpc.wsdl.document.MessagePart;
import com.sun.xml.rpc.wsdl.document.Operation;
import com.sun.xml.rpc.wsdl.document.Output;
import com.sun.xml.rpc.wsdl.document.Port;
import com.sun.xml.rpc.wsdl.document.PortType;
import com.sun.xml.rpc.wsdl.document.Service;
import com.sun.xml.rpc.wsdl.document.Types;
import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.WSDLDocumentVisitor;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;
import com.sun.xml.rpc.wsdl.framework.Extension;
import com.sun.xml.rpc.wsdl.framework.Kind;
import com.sun.xml.rpc.wsdl.framework.WriterContext;

/**
 * A writer for WSDL documents.
 *
 * @author JAX-RPC Development Team
 */
public class WSDLWriter {

    public WSDLWriter() throws IOException {
        _extensionHandlers = new HashMap();

        // register handlers for default extensions
        register(new SOAPExtensionHandler());
        register(new HTTPExtensionHandler());
        register(new MIMEExtensionHandler());
        register(new SchemaExtensionHandler());
    }

    public void register(ExtensionHandler h) {
        _extensionHandlers.put(h.getNamespaceURI(), h);
        h.setExtensionHandlers(_extensionHandlers);
    }

    public void unregister(ExtensionHandler h) {
        _extensionHandlers.put(h.getNamespaceURI(), null);
        h.setExtensionHandlers(null);
    }

    public void unregister(String uri) {
        _extensionHandlers.put(uri, null);
    }

    public void write(final WSDLDocument document, OutputStream os)
        throws IOException {
        final WriterContext context = new WriterContext(os);
        try {
            document.accept(new WSDLDocumentVisitor() {
                public void preVisit(Definitions definitions)
                    throws Exception {
                    context.push();
                    initializePrefixes(context, document);
                    context.writeStartTag(definitions.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        definitions.getName());
                    context.writeAttribute(
                        Constants.ATTR_TARGET_NAMESPACE,
                        definitions.getTargetNamespaceURI());
                    context.writeAllPendingNamespaceDeclarations();
                }
                public void postVisit(Definitions definitions)
                    throws Exception {
                    context.writeEndTag(definitions.getElementName());
                    context.pop();
                }
                public void visit(Import i) throws Exception {
                    context.writeStartTag(i.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAMESPACE,
                        i.getNamespace());
                    context.writeAttribute(
                        Constants.ATTR_LOCATION,
                        i.getLocation());
                    context.writeEndTag(i.getElementName());
                }
                public void preVisit(Types types) throws Exception {
                    context.writeStartTag(types.getElementName());
                }
                public void postVisit(Types types) throws Exception {
                    context.writeEndTag(types.getElementName());
                }
                public void preVisit(Message message) throws Exception {
                    context.writeStartTag(message.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        message.getName());
                }
                public void postVisit(Message message) throws Exception {
                    context.writeEndTag(message.getElementName());
                }
                public void visit(MessagePart part) throws Exception {
                    context.writeStartTag(part.getElementName());
                    context.writeAttribute(Constants.ATTR_NAME, part.getName());

                    QName dname = part.getDescriptor();
                    Kind dkind = part.getDescriptorKind();
                    if (dname != null && dkind != null) {
                        if (dkind.equals(SchemaKinds.XSD_ELEMENT)) {
                            context.writeAttribute(
                                Constants.ATTR_ELEMENT,
                                dname);
                        } else if (dkind.equals(SchemaKinds.XSD_TYPE)) {
                            context.writeAttribute(Constants.ATTR_TYPE, dname);
                        } else {
                            // TODO - add support for attribute extensions here
                        }
                    }
                    context.writeEndTag(part.getElementName());
                }
                public void preVisit(PortType portType) throws Exception {
                    context.writeStartTag(portType.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        portType.getName());
                }
                public void postVisit(PortType portType) throws Exception {
                    context.writeEndTag(portType.getElementName());
                }
                public void preVisit(Operation operation) throws Exception {
                    context.writeStartTag(operation.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        operation.getName());
                    //bug fix: 4947340, parameterOder="" should not be generated
                    if(operation.getParameterOrder() != null && 
                        operation.getParameterOrder().length() > 0) {
                        context.writeAttribute(
                            Constants.ATTR_PARAMETER_ORDER,
                            operation.getParameterOrder());
                    }
                }
                public void postVisit(Operation operation) throws Exception {
                    context.writeEndTag(operation.getElementName());
                }
                public void preVisit(Input input) throws Exception {
                    context.writeStartTag(input.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        input.getName());
                    context.writeAttribute(
                        Constants.ATTR_MESSAGE,
                        input.getMessage());
                }
                public void postVisit(Input input) throws Exception {
                    context.writeEndTag(input.getElementName());
                }
                public void preVisit(Output output) throws Exception {
                    context.writeStartTag(output.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        output.getName());
                    context.writeAttribute(
                        Constants.ATTR_MESSAGE,
                        output.getMessage());
                }
                public void postVisit(Output output) throws Exception {
                    context.writeEndTag(output.getElementName());
                }
                public void preVisit(Fault fault) throws Exception {
                    context.writeStartTag(fault.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        fault.getName());
                    context.writeAttribute(
                        Constants.ATTR_MESSAGE,
                        fault.getMessage());
                }
                public void postVisit(Fault fault) throws Exception {
                    context.writeEndTag(fault.getElementName());
                }
                public void preVisit(Binding binding) throws Exception {
                    context.writeStartTag(binding.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        binding.getName());
                    context.writeAttribute(
                        Constants.ATTR_TYPE,
                        binding.getPortType());
                }
                public void postVisit(Binding binding) throws Exception {
                    context.writeEndTag(binding.getElementName());
                }

                public void preVisit(BindingOperation operation)
                    throws Exception {
                    context.writeStartTag(operation.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        operation.getName());
                }
                public void postVisit(BindingOperation operation)
                    throws Exception {
                    context.writeEndTag(operation.getElementName());
                }
                public void preVisit(BindingInput input) throws Exception {
                    context.writeStartTag(input.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        input.getName());
                }
                public void postVisit(BindingInput input) throws Exception {
                    context.writeEndTag(input.getElementName());
                }
                public void preVisit(BindingOutput output) throws Exception {
                    context.writeStartTag(output.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        output.getName());
                }
                public void postVisit(BindingOutput output) throws Exception {
                    context.writeEndTag(output.getElementName());
                }
                public void preVisit(BindingFault fault) throws Exception {
                    context.writeStartTag(fault.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        fault.getName());
                }
                public void postVisit(BindingFault fault) throws Exception {
                    context.writeEndTag(fault.getElementName());
                }

                public void preVisit(Service service) throws Exception {
                    context.writeStartTag(service.getElementName());
                    context.writeAttribute(
                        Constants.ATTR_NAME,
                        service.getName());
                }
                public void postVisit(Service service) throws Exception {
                    context.writeEndTag(service.getElementName());
                }
                public void preVisit(Port port) throws Exception {
                    context.writeStartTag(port.getElementName());
                    context.writeAttribute(Constants.ATTR_NAME, port.getName());
                    context.writeAttribute(
                        Constants.ATTR_BINDING,
                        port.getBinding());
                }
                public void postVisit(Port port) throws Exception {
                    context.writeEndTag(port.getElementName());
                }
                public void preVisit(Extension extension) throws Exception {
                    ExtensionHandler h =
                        (ExtensionHandler) _extensionHandlers.get(
                            extension.getElementName().getNamespaceURI());
                    h.doHandleExtension(context, extension);
                }
                public void postVisit(Extension extension) throws Exception {
                }
                public void visit(Documentation documentation)
                    throws Exception {
                    context.writeTag(WSDLConstants.QNAME_DOCUMENTATION, null);
                }
            });
            context.flush();
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                // entirely unexpected exception
                throw new IllegalStateException();
            }
        }
    }

    private void initializePrefixes(
        WriterContext context,
        WSDLDocument document)
        throws IOException {
        // deal with the target namespace first
        String tnsURI = document.getDefinitions().getTargetNamespaceURI();
        if (tnsURI != null) {
            context.setTargetNamespaceURI(tnsURI);
            context.declarePrefix(TARGET_NAMESPACE_PREFIX, tnsURI);
        }

        // then with the WSDL namespace
        context.declarePrefix("", Constants.NS_WSDL);

        // then with all other namespaces
        Set namespaces = document.collectAllNamespaces();
        for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
            String nsURI = (String) iter.next();
            if (context.getPrefixFor(nsURI) != null)
                continue;

            String prefix = (String) _commonPrefixes.get(nsURI);
            if (prefix == null) {
                // create a new prefix for it
                prefix = context.findNewPrefix(NEW_NAMESPACE_PREFIX_BASE);
            }
            context.declarePrefix(prefix, nsURI);
        }
    }

    private Map _extensionHandlers;

    ////////

    private static Map _commonPrefixes;

    static {
        _commonPrefixes = new HashMap();
        _commonPrefixes.put(Constants.NS_WSDL, "wsdl");
        _commonPrefixes.put(Constants.NS_WSDL_SOAP, "soap");
        _commonPrefixes.put(Constants.NS_WSDL_HTTP, "http");
        _commonPrefixes.put(Constants.NS_WSDL_MIME, "mime");
        _commonPrefixes.put(Constants.NS_XSD, "xsd");
        _commonPrefixes.put(Constants.NS_XSI, "xsi");
    }

    private final static String TARGET_NAMESPACE_PREFIX = "tns";
    private final static String NEW_NAMESPACE_PREFIX_BASE = "ns";
}
