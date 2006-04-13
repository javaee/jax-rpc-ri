/*
 * $Id: ExtensionHandlerBase.java,v 1.2 2006-04-13 01:34:41 ofung Exp $
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

package com.sun.xml.rpc.wsdl.parser;

import org.w3c.dom.Element;

import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.mime.MIMEConstants;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.ParserContext;
/**
 * A base class for WSDL extension handlers.
 *
 * @author JAX-RPC Development Team
 */
public abstract class ExtensionHandlerBase extends ExtensionHandler {

    protected ExtensionHandlerBase() {
    }

    public boolean doHandleExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        if (parent.getElementName().equals(WSDLConstants.QNAME_DEFINITIONS)) {
            return handleDefinitionsExtension(context, parent, e);
        } else if (parent.getElementName().equals(WSDLConstants.QNAME_TYPES)) {
            return handleTypesExtension(context, parent, e);
        } else if (
            parent.getElementName().equals(WSDLConstants.QNAME_BINDING)) {
            return handleBindingExtension(context, parent, e);
        } else if (
            parent.getElementName().equals(WSDLConstants.QNAME_OPERATION)) {
            return handleOperationExtension(context, parent, e);
        } else if (parent.getElementName().equals(WSDLConstants.QNAME_INPUT)) {
            return handleInputExtension(context, parent, e);
        } else if (
            parent.getElementName().equals(WSDLConstants.QNAME_OUTPUT)) {
            return handleOutputExtension(context, parent, e);
        } else if (parent.getElementName().equals(WSDLConstants.QNAME_FAULT)) {
            return handleFaultExtension(context, parent, e);
        } else if (
            parent.getElementName().equals(WSDLConstants.QNAME_SERVICE)) {
            return handleServiceExtension(context, parent, e);
        } else if (parent.getElementName().equals(WSDLConstants.QNAME_PORT)) {
            return handlePortExtension(context, parent, e);
        } else if (parent.getElementName().equals(MIMEConstants.QNAME_PART)) {
            return handleMIMEPartExtension(context, parent, e);
        } else {
            return false;
        }
    }

    protected abstract boolean handleDefinitionsExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handleTypesExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handleBindingExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handleOperationExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handleInputExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handleOutputExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handleFaultExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handleServiceExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handlePortExtension(
        ParserContext context,
        Extensible parent,
        Element e);
    protected abstract boolean handleMIMEPartExtension(
        ParserContext context,
        Extensible parent,
        Element e);
}
