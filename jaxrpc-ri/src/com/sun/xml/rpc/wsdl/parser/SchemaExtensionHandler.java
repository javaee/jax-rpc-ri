/*
 * $Id: SchemaExtensionHandler.java,v 1.1 2006-04-12 20:34:52 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.wsdl.parser;

import java.io.IOException;

import org.w3c.dom.Element;

import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.schema.Schema;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.framework.Extensible;
import com.sun.xml.rpc.wsdl.framework.Extension;
import com.sun.xml.rpc.wsdl.framework.ParserContext;
import com.sun.xml.rpc.wsdl.framework.WriterContext;

/**
 * The XML Schema extension handler for WSDL.
 *
 * @author JAX-RPC Development Team
 */
public class SchemaExtensionHandler extends ExtensionHandler {

    public SchemaExtensionHandler() {
    }

    public String getNamespaceURI() {
        return Constants.NS_XSD;
    }

    public boolean doHandleExtension(
        ParserContext context,
        Extensible parent,
        Element e) {
        if (XmlUtil.matchesTagNS(e, SchemaConstants.QNAME_SCHEMA)) {
            SchemaParser parser = new SchemaParser();
            parent.addExtension(parser.parseSchema(context, e, null));
            return true;
        } else {
            return false;
        }
    }

    public void doHandleExtension(WriterContext context, Extension extension)
        throws IOException {
        if (extension instanceof Schema) {
            SchemaWriter writer = new SchemaWriter();
            writer.writeSchema(context, (Schema) extension);
        } else {
            // unknown extension
            throw new IllegalArgumentException();
        }
    }
}
