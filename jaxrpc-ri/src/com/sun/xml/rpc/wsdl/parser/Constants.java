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

package com.sun.xml.rpc.wsdl.parser;

/**
 * An interface defining constants needed to read and write WSDL documents.
 *
 * @author JAX-RPC Development Team
 */
public interface Constants {
    // WSDL element tags
    public static String TAG_BINDING = "binding";
    public static String TAG_DEFINITIONS = "definitions";
    public static String TAG_DOCUMENTATION = "documentation";
    public static String TAG_MESSAGE = "message";
    public static String TAG_PART = "part";
    public static String TAG_PORT_TYPE = "portType";
    public static String TAG_TYPES = "types";
    public static String TAG_OPERATION = "operation";
    public static String TAG_INPUT = "input";
    public static String TAG_OUTPUT = "output";
    public static String TAG_FAULT = "fault";
    public static String TAG_SERVICE = "service";
    public static String TAG_PORT = "port";
    public static String TAG_ = "";

    // WSDL attribute names
    public static String ATTR_ELEMENT = "element";
    public static String ATTR_NAME = "name";
    public static String ATTR_REQUIRED = "required";
    public static String ATTR_TARGET_NAMESPACE = "targetNamespace";
    public static String ATTR_TYPE = "type";
    public static String ATTR_MESSAGE = "message";
    public static String ATTR_BINDING = "binding";
    public static String ATTR_LOCATION = "location";
    public static String ATTR_TRANSPORT = "transport";
    public static String ATTR_STYLE = "style";
    public static String ATTR_USE = "use";
    public static String ATTR_NAMESPACE = "namespace";
    public static String ATTR_ENCODING_STYLE = "encodingStyle";
    public static String ATTR_PART = "part";
    public static String ATTR_PARTS = "parts";
    public static String ATTR_SOAP_ACTION = "soapAction";
    public static String ATTR_PARAMETER_ORDER = "parameterOrder";
    public static String ATTR_VERB = "verb";

    // schema attribute names
    public static String ATTR_ID = "id";
    public static String ATTR_VERSION = "version";
    public static String ATTR_ATTRIBUTE_FORM_DEFAULT = "attributeFormDefault";
    public static String ATTR_BLOCK_DEFAULT = "blockDefault";
    public static String ATTR_ELEMENT_FORM_DEFAULT = "elementFormDefault";
    public static String ATTR_FINAL_DEFAULT = "finalDefault";
    public static String ATTR_ABSTRACT = "abstract";
    public static String ATTR_NILLABLE = "nillable";
    public static String ATTR_DEFAULT = "default";
    public static String ATTR_FIXED = "fixed";
    public static String ATTR_FORM = "form";
    public static String ATTR_BLOCK = "block";
    public static String ATTR_FINAL = "final";
    public static String ATTR_REF = "ref";
    public static String ATTR_SUBSTITUTION_GROUP = "substitutionGroup";
    public static String ATTR_MIN_OCCURS = "minOccurs";
    public static String ATTR_MAX_OCCURS = "maxOccurs";
    public static String ATTR_PROCESS_CONTENTS = "processContents";
    public static String ATTR_MIXED = "mixed";
    public static String ATTR_BASE = "base";
    public static String ATTR_VALUE = "value";
    public static String ATTR_XPATH = "xpath";
    public static String ATTR_SCHEMA_LOCATION = "schemaLocation";
    public static String ATTR_REFER = "refer";
    public static String ATTR_ITEM_TYPE = "itemType";
    public static String ATTR_PUBLIC = "public";
    public static String ATTR_SYSTEM = "system";
    public static String ATTR_MEMBER_TYPES = "memberTypes";
    public static String ATTR_ = "";

    // WSDL attribute values
    public static String ATTRVALUE_RPC = "rpc";
    public static String ATTRVALUE_DOCUMENT = "document";
    public static String ATTRVALUE_LITERAL = "literal";
    public static String ATTRVALUE_ENCODED = "encoded";

    // schema attribute values
    public static String ATTRVALUE_QUALIFIED = "qualified";
    public static String ATTRVALUE_UNQUALIFIED = "unqualified";
    public static String ATTRVALUE_ALL = "#all";
    public static String ATTRVALUE_SUBSTITUTION = "substitution";
    public static String ATTRVALUE_EXTENSION = "extension";
    public static String ATTRVALUE_RESTRICTION = "restriction";
    public static String ATTRVALUE_LIST = "list";
    public static String ATTRVALUE_UNION = "union";
    public static String ATTRVALUE_UNBOUNDED = "unbounded";
    public static String ATTRVALUE_PROHIBITED = "prohibited";
    public static String ATTRVALUE_OPTIONAL = "optional";
    public static String ATTRVALUE_REQUIRED = "required";
    public static String ATTRVALUE_LAX = "lax";
    public static String ATTRVALUE_SKIP = "skip";
    public static String ATTRVALUE_STRICT = "strict";
    public static String ATTRVALUE_ANY = "##any";
    public static String ATTRVALUE_LOCAL = "##local";
    public static String ATTRVALUE_OTHER = "##other";
    public static String ATTRVALUE_TARGET_NAMESPACE = "##targetNamespace";
    public static String ATTRVALUE_ = "";

    // namespace URIs
    public static String NS_XML = "http://www.w3.org/XML/1998/namespace";
    public static String NS_XMLNS = "http://www.w3.org/2000/xmlns/";
    public static String NS_WSDL = "http://schemas.xmlsoap.org/wsdl/";
    public static String NS_WSDL_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static String NS_WSDL_HTTP = "http://schemas.xmlsoap.org/wsdl/http/";
    public static String NS_WSDL_MIME = "http://schemas.xmlsoap.org/wsdl/mime/";
    public static String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    public static String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public static String NS_ = "";

    // other constants
    public static String XMLNS = "xmlns";
    public static String TRUE = "true";
    public static String FALSE = "false";
}
