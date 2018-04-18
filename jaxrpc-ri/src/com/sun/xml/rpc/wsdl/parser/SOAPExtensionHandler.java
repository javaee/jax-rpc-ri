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
 * $Id: SOAPExtensionHandler.java,v 1.3 2007-07-13 23:36:48 ofung Exp $
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
import java.util.Iterator;

import org.w3c.dom.Element;

import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.soap.SOAPAddress;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBinding;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPFault;
import com.sun.xml.rpc.wsdl.document.soap.SOAPHeader;
import com.sun.xml.rpc.wsdl.document.soap.SOAPHeaderFault;
import com.sun.xml.rpc.wsdl.document.soap.SOAPOperation;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.document.soap.SOAPUse;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;
import com.sun.xml.rpc.wsdl.framework.ParserContext;
import com.sun.xml.rpc.wsdl.framework.WriterContext;

/**
 * The SOAP extension handler for WSDL.
 *
 * @author JAX-RPC Development Team
 */
public class SOAPExtensionHandler extends ExtensionHandlerBase {

    public SOAPExtensionHandler() {
    }

    public String getNamespaceURI() {
        return Constants.NS_WSDL_SOAP;
    }

    protected boolean handleDefinitionsExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        Util.fail(
            "parsing.invalidExtensionElement",
            e.getTagName(),
            e.getNamespaceURI());
        return false; // keep compiler happy
    }

    protected boolean handleTypesExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        Util.fail(
            "parsing.invalidExtensionElement",
            e.getTagName(),
            e.getNamespaceURI());
        return false; // keep compiler happy
    }

    protected boolean handleBindingExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        if (XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_BINDING)) {
            context.push();
            context.registerNamespaces(e);

            SOAPBinding binding = new SOAPBinding();

            // NOTE - the "transport" attribute is required according to section 3.3 of the WSDL 1.1 spec,
            // but optional according to the schema in appendix A 4.2 of the same document!
            String transport =
                Util.getRequiredAttribute(e, Constants.ATTR_TRANSPORT);
            binding.setTransport(transport);

            String style = XmlUtil.getAttributeOrNull(e, Constants.ATTR_STYLE);
            if (style != null) {
                if (style.equals(Constants.ATTRVALUE_RPC)) {
                    binding.setStyle(SOAPStyle.RPC);
                } else if (style.equals(Constants.ATTRVALUE_DOCUMENT)) {
                    binding.setStyle(SOAPStyle.DOCUMENT);
                } else {
                    Util.fail(
                        "parsing.invalidAttributeValue",
                        Constants.ATTR_STYLE,
                        style);
                }
            }
            parent.addExtension(binding);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_BINDING, binding);
            return true;
        } else {
            Util.fail(
                "parsing.invalidExtensionElement",
                e.getTagName(),
                e.getNamespaceURI());
            return false; // keep compiler happy
        }
    }

    protected boolean handleOperationExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        if (XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_OPERATION)) {
            context.push();
            context.registerNamespaces(e);

            SOAPOperation operation = new SOAPOperation();

            String soapAction =
                XmlUtil.getAttributeOrNull(e, Constants.ATTR_SOAP_ACTION);
            if (soapAction != null) {
                operation.setSOAPAction(soapAction);
            }

            String style = XmlUtil.getAttributeOrNull(e, Constants.ATTR_STYLE);
            if (style != null) {
                if (style.equals(Constants.ATTRVALUE_RPC)) {
                    operation.setStyle(SOAPStyle.RPC);
                } else if (style.equals(Constants.ATTRVALUE_DOCUMENT)) {
                    operation.setStyle(SOAPStyle.DOCUMENT);
                } else {
                    Util.fail(
                        "parsing.invalidAttributeValue",
                        Constants.ATTR_STYLE,
                        style);
                }
            }
            parent.addExtension(operation);
            context.pop();
            context.fireDoneParsingEntity(
                SOAPConstants.QNAME_OPERATION,
                operation);
            return true;
        } else {
            Util.fail(
                "parsing.invalidExtensionElement",
                e.getTagName(),
                e.getNamespaceURI());
            return false; // keep compiler happy
        }
    }

    protected boolean handleInputExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        return handleInputOutputExtension(context, parent, e);
    }
    protected boolean handleOutputExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        return handleInputOutputExtension(context, parent, e);
    }

    protected boolean handleMIMEPartExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        return handleInputOutputExtension(context, parent, e);
    }

    protected boolean handleInputOutputExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        if (XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_BODY)) {
            context.push();
            context.registerNamespaces(e);

            SOAPBody body = new SOAPBody();

            String use = XmlUtil.getAttributeOrNull(e, Constants.ATTR_USE);
            if (use != null) {
                if (use.equals(Constants.ATTRVALUE_LITERAL)) {
                    body.setUse(SOAPUse.LITERAL);
                } else if (use.equals(Constants.ATTRVALUE_ENCODED)) {
                    body.setUse(SOAPUse.ENCODED);
                } else {
                    Util.fail(
                        "parsing.invalidAttributeValue",
                        Constants.ATTR_USE,
                        use);
                }
            }

            String namespace =
                XmlUtil.getAttributeOrNull(e, Constants.ATTR_NAMESPACE);
            if (namespace != null) {
                body.setNamespace(namespace);
            }

            String encodingStyle =
                XmlUtil.getAttributeOrNull(e, Constants.ATTR_ENCODING_STYLE);
            if (encodingStyle != null) {
                body.setEncodingStyle(encodingStyle);
            }

            String parts = XmlUtil.getAttributeOrNull(e, Constants.ATTR_PARTS);
            if (parts != null) {
                body.setParts(parts);
            }

            parent.addExtension(body);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_BODY, body);
            return true;
        } else if (XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_HEADER)) {
            context.push();
            context.registerNamespaces(e);

            SOAPHeader header = new SOAPHeader();

            String use = XmlUtil.getAttributeOrNull(e, Constants.ATTR_USE);
            if (use != null) {
                if (use.equals(Constants.ATTRVALUE_LITERAL)) {
                    header.setUse(SOAPUse.LITERAL);
                } else if (use.equals(Constants.ATTRVALUE_ENCODED)) {
                    header.setUse(SOAPUse.ENCODED);
                } else {
                    Util.fail(
                        "parsing.invalidAttributeValue",
                        Constants.ATTR_USE,
                        use);
                }
            }

            String namespace =
                XmlUtil.getAttributeOrNull(e, Constants.ATTR_NAMESPACE);
            if (namespace != null) {
                header.setNamespace(namespace);
            }

            String encodingStyle =
                XmlUtil.getAttributeOrNull(e, Constants.ATTR_ENCODING_STYLE);
            if (encodingStyle != null) {
                header.setEncodingStyle(encodingStyle);
            }

            String part = XmlUtil.getAttributeOrNull(e, Constants.ATTR_PART);
            if (part != null) {
                header.setPart(part);
            }

            String messageAttr =
                XmlUtil.getAttributeOrNull(e, Constants.ATTR_MESSAGE);
            if (messageAttr != null) {
                header.setMessage(context.translateQualifiedName(messageAttr));
            }

            for (Iterator iter = XmlUtil.getAllChildren(e); iter.hasNext();) {
                Element e2 = Util.nextElement(iter);
                if (e2 == null)
                    break;

                if (XmlUtil
                    .matchesTagNS(e2, SOAPConstants.QNAME_HEADERFAULT)) {
                    context.push();
                    context.registerNamespaces(e);

                    SOAPHeaderFault headerfault = new SOAPHeaderFault();

                    String use2 =
                        XmlUtil.getAttributeOrNull(e2, Constants.ATTR_USE);
                    if (use2 != null) {
                        if (use2.equals(Constants.ATTRVALUE_LITERAL)) {
                            headerfault.setUse(SOAPUse.LITERAL);
                        } else if (use.equals(Constants.ATTRVALUE_ENCODED)) {
                            headerfault.setUse(SOAPUse.ENCODED);
                        } else {
                            Util.fail(
                                "parsing.invalidAttributeValue",
                                Constants.ATTR_USE,
                                use2);
                        }
                    }

                    String namespace2 =
                        XmlUtil.getAttributeOrNull(
                            e2,
                            Constants.ATTR_NAMESPACE);
                    if (namespace2 != null) {
                        headerfault.setNamespace(namespace2);
                    }

                    String encodingStyle2 =
                        XmlUtil.getAttributeOrNull(
                            e2,
                            Constants.ATTR_ENCODING_STYLE);
                    if (encodingStyle2 != null) {
                        headerfault.setEncodingStyle(encodingStyle2);
                    }

                    String part2 =
                        XmlUtil.getAttributeOrNull(e2, Constants.ATTR_PART);
                    if (part2 != null) {
                        headerfault.setPart(part2);
                    }

                    String messageAttr2 =
                        XmlUtil.getAttributeOrNull(e2, Constants.ATTR_MESSAGE);
                    if (messageAttr2 != null) {
                        headerfault.setMessage(
                            context.translateQualifiedName(messageAttr2));
                    }

                    header.add(headerfault);
                    context.pop();
                } else {
                    Util.fail(
                        "parsing.invalidElement",
                        e2.getTagName(),
                        e2.getNamespaceURI());
                }
            }

            parent.addExtension(header);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_HEADER, header);
            return true;
        } else {
            Util.fail(
                "parsing.invalidExtensionElement",
                e.getTagName(),
                e.getNamespaceURI());
            return false; // keep compiler happy
        }
    }

    protected boolean handleFaultExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        if (XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_FAULT)) {
            context.push();
            context.registerNamespaces(e);

            SOAPFault fault = new SOAPFault();

            String name = XmlUtil.getAttributeOrNull(e, Constants.ATTR_NAME);
            if (name != null) {
                fault.setName(name);
            }

            String use = XmlUtil.getAttributeOrNull(e, Constants.ATTR_USE);
            if (use != null) {
                if (use.equals(Constants.ATTRVALUE_LITERAL)) {
                    fault.setUse(SOAPUse.LITERAL);
                } else if (use.equals(Constants.ATTRVALUE_ENCODED)) {
                    fault.setUse(SOAPUse.ENCODED);
                } else {
                    Util.fail(
                        "parsing.invalidAttributeValue",
                        Constants.ATTR_USE,
                        use);
                }
            }

            String namespace =
                XmlUtil.getAttributeOrNull(e, Constants.ATTR_NAMESPACE);
            if (namespace != null) {
                fault.setNamespace(namespace);
            }

            String encodingStyle =
                XmlUtil.getAttributeOrNull(e, Constants.ATTR_ENCODING_STYLE);
            if (encodingStyle != null) {
                fault.setEncodingStyle(encodingStyle);
            }

            parent.addExtension(fault);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_FAULT, fault);
            return true;
        } else {
            Util.fail(
                "parsing.invalidExtensionElement",
                e.getTagName(),
                e.getNamespaceURI());
            return false; // keep compiler happy
        }
    }

    protected boolean handleServiceExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        Util.fail(
            "parsing.invalidExtensionElement",
            e.getTagName(),
            e.getNamespaceURI());
        return false; // keep compiler happy
    }

    protected boolean handlePortExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        if (XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_ADDRESS)) {
            context.push();
            context.registerNamespaces(e);

            SOAPAddress address = new SOAPAddress();

            String location =
                Util.getRequiredAttribute(e, Constants.ATTR_LOCATION);
            address.setLocation(location);

            parent.addExtension(address);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_ADDRESS, address);
            return true;
        } else {
            Util.fail(
                "parsing.invalidExtensionElement",
                e.getTagName(),
                e.getNamespaceURI());
            return false; // keep compiler happy
        }
    }

    public void doHandleExtension(WriterContext context, Extension extension)
        throws IOException {
        // NOTE - this ugliness can be avoided by moving all the XML parsing/writing code
        // into the document classes themselves
        if (extension instanceof SOAPAddress) {
            SOAPAddress address = (SOAPAddress) extension;
            context.writeStartTag(address.getElementName());
            context.writeAttribute(
                Constants.ATTR_LOCATION,
                address.getLocation());
            context.writeEndTag(address.getElementName());
        } else if (extension instanceof SOAPBinding) {
            SOAPBinding binding = (SOAPBinding) extension;
            context.writeStartTag(binding.getElementName());
            context.writeAttribute(
                Constants.ATTR_TRANSPORT,
                binding.getTransport());
            String style =
                (binding.getStyle() == null
                    ? null
                    : (binding.getStyle() == SOAPStyle.DOCUMENT
                        ? Constants.ATTRVALUE_DOCUMENT
                        : Constants.ATTRVALUE_RPC));
            context.writeAttribute(Constants.ATTR_STYLE, style);
            context.writeEndTag(binding.getElementName());
        } else if (extension instanceof SOAPBody) {
            SOAPBody body = (SOAPBody) extension;
            context.writeStartTag(body.getElementName());
            context.writeAttribute(
                Constants.ATTR_ENCODING_STYLE,
                body.getEncodingStyle());
            context.writeAttribute(Constants.ATTR_PARTS, body.getParts());
            String use =
                (body.getUse() == null
                    ? null
                    : (body.getUse() == SOAPUse.LITERAL
                        ? Constants.ATTRVALUE_LITERAL
                        : Constants.ATTRVALUE_ENCODED));
            context.writeAttribute(Constants.ATTR_USE, use);
            context.writeAttribute(
                Constants.ATTR_NAMESPACE,
                body.getNamespace());
            context.writeEndTag(body.getElementName());
        } else if (extension instanceof SOAPFault) {
            SOAPFault fault = (SOAPFault) extension;
            context.writeStartTag(fault.getElementName());
            context.writeAttribute(Constants.ATTR_NAME, fault.getName());
            context.writeAttribute(
                Constants.ATTR_ENCODING_STYLE,
                fault.getEncodingStyle());
            String use =
                (fault.getUse() == null
                    ? null
                    : (fault.getUse() == SOAPUse.LITERAL
                        ? Constants.ATTRVALUE_LITERAL
                        : Constants.ATTRVALUE_ENCODED));
            context.writeAttribute(Constants.ATTR_USE, use);
            context.writeAttribute(
                Constants.ATTR_NAMESPACE,
                fault.getNamespace());
            context.writeEndTag(fault.getElementName());
        } else if (extension instanceof SOAPHeader) {
            SOAPHeader header = (SOAPHeader) extension;
            context.writeStartTag(header.getElementName());
            context.writeAttribute(Constants.ATTR_MESSAGE, header.getMessage());
            context.writeAttribute(Constants.ATTR_PART, header.getPart());
            context.writeAttribute(
                Constants.ATTR_ENCODING_STYLE,
                header.getEncodingStyle());
            String use =
                (header.getUse() == null
                    ? null
                    : (header.getUse() == SOAPUse.LITERAL
                        ? Constants.ATTRVALUE_LITERAL
                        : Constants.ATTRVALUE_ENCODED));
            context.writeAttribute(Constants.ATTR_USE, use);
            context.writeAttribute(
                Constants.ATTR_NAMESPACE,
                header.getNamespace());
            context.writeEndTag(header.getElementName());
        } else if (extension instanceof SOAPHeaderFault) {
            SOAPHeaderFault headerfault = (SOAPHeaderFault) extension;
            context.writeStartTag(headerfault.getElementName());
            context.writeAttribute(
                Constants.ATTR_MESSAGE,
                headerfault.getMessage());
            context.writeAttribute(Constants.ATTR_PART, headerfault.getPart());
            context.writeAttribute(
                Constants.ATTR_ENCODING_STYLE,
                headerfault.getEncodingStyle());
            String use =
                (headerfault.getUse() == null
                    ? null
                    : (headerfault.getUse() == SOAPUse.LITERAL
                        ? Constants.ATTRVALUE_LITERAL
                        : Constants.ATTRVALUE_ENCODED));
            context.writeAttribute(Constants.ATTR_USE, use);
            context.writeAttribute(
                Constants.ATTR_NAMESPACE,
                headerfault.getNamespace());
            context.writeEndTag(headerfault.getElementName());
        } else if (extension instanceof SOAPOperation) {
            SOAPOperation operation = (SOAPOperation) extension;
            context.writeStartTag(operation.getElementName());
            context.writeAttribute(
                Constants.ATTR_SOAP_ACTION,
                operation.getSOAPAction());
            String style =
                (operation.getStyle() == null
                    ? null
                    : (operation.isDocument()
                        ? Constants.ATTRVALUE_DOCUMENT
                        : Constants.ATTRVALUE_RPC));
            context.writeAttribute(Constants.ATTR_STYLE, style);
            context.writeEndTag(operation.getElementName());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
