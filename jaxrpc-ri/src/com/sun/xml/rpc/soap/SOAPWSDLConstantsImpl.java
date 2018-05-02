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

package com.sun.xml.rpc.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.wsdl.document.soap.SOAP12Constants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;

/**
 * @author JAX-RPC Development Team
 */

class SOAPWSDLConstantsImpl implements SOAPWSDLConstants {

	SOAPWSDLConstantsImpl(SOAPVersion ver) {
		this.ver = ver;
		if (ver == SOAPVersion.SOAP_11)
			initSOAP11();
		else if (ver == SOAPVersion.SOAP_12)
			initSOAP12();
	}

	/** NS_WSDL_SOAP */
	public String getWSDLSOAPNamespace() {
		return NS_WSDL_SOAP;
	}

	/** NS_SOAP_ENCODING */
	public String getSOAPEncodingNamespace() {
		return NS_SOAP_ENCODING;
	}

	/** URI_SOAP_TRANSPORT_HTTP */
	public String getSOAPTransportHttpURI() {
		return URI_SOAP_TRANSPORT_HTTP;
	}

	/** QNames */
	public QName getQNameAddress() {
		return QNAME_ADDRESS;
	}

	public QName getQNameBinding() {
		return QNAME_BINDING;
	}

	public QName getQNameBody() {
		return QNAME_BODY;
	}

	public QName getQNameFault() {
		return QNAME_FAULT;
	}

	public QName getQNameHeader() {
		return QNAME_HEADER;
	}

	public QName getQNameHeaderFault() {
		return QNAME_HEADERFAULT;
	}

	public QName getQNameOperation() {
		return QNAME_OPERATION;
	}

	/** SOAP encoding QNames */
	public QName getQNameTypeArray() {
		return QNAME_TYPE_ARRAY;
	}

	public QName getQNameAttrGroupCommonAttributes() {
		return QNAME_ATTR_GROUP_COMMON_ATTRIBUTES;
	}

	public QName getQNameAttrArrayType() {
		return QNAME_ATTR_ARRAY_TYPE;
	}

	public QName getQNameAttrOffset() {
		return QNAME_ATTR_OFFSET;
	}

	public QName getQNameAttrPosition() {
		return QNAME_ATTR_POSITION;
	}

	public QName getQNameTypeBase64() {
		return QNAME_TYPE_BASE64;
	}

	public QName getQNameElementString() {
		return QNAME_ELEMENT_STRING;
	}

	public QName getQNameElementNormalizedString() {
		return QNAME_ELEMENT_NORMALIZED_STRING;
	}

	public QName getQNameElementToken() {
		return QNAME_ELEMENT_TOKEN;
	}

	public QName getQNameElementByte() {
		return QNAME_ELEMENT_BYTE;
	}

	public QName getQNameElementUnsignedByte() {
		return QNAME_ELEMENT_UNSIGNED_BYTE;
	}

	public QName getQNameElementBase64Binary() {
		return QNAME_ELEMENT_BASE64_BINARY;
	}

	public QName getQNameElementHexBinary() {
		return QNAME_ELEMENT_HEX_BINARY;
	}

	public QName getQNameElementInteger() {
		return QNAME_ELEMENT_INTEGER;
	}

	public QName getQNameElementPositiveInteger() {
		return QNAME_ELEMENT_POSITIVE_INTEGER;
	}

	public QName getQNameElementNegativeInteger() {
		return QNAME_ELEMENT_NEGATIVE_INTEGER;
	}

	public QName getQNameElementNonNegativeInteger() {
		return QNAME_ELEMENT_NON_NEGATIVE_INTEGER;
	}

	public QName getQNameElementNonPositiveInteger() {
		return QNAME_ELEMENT_NON_POSITIVE_INTEGER;
	}

	public QName getQNameElementInt() {
		return QNAME_ELEMENT_INT;
	}

	public QName getQNameElementUnsignedInt() {
		return QNAME_ELEMENT_UNSIGNED_INT;
	}

	public QName getQNameElementLong() {
		return QNAME_ELEMENT_LONG;
	}

	public QName getQNameElementUnsignedLong() {
		return QNAME_ELEMENT_UNSIGNED_LONG;
	}

	public QName getQNameElementShort() {
		return QNAME_ELEMENT_SHORT;
	}

	public QName getQNameElementUnsignedShort() {
		return QNAME_ELEMENT_UNSIGNED_SHORT;
	}

	public QName getQNameElementDecimal() {
		return QNAME_ELEMENT_DECIMAL;
	}

	public QName getQNameElementFloat() {
		return QNAME_ELEMENT_FLOAT;
	}

	public QName getQNameElementDouble() {
		return QNAME_ELEMENT_DOUBLE;
	}

	public QName getQNameElementBoolean() {
		return QNAME_ELEMENT_BOOLEAN;
	}

	public QName getQNameElementTime() {
		return QNAME_ELEMENT_TIME;
	}

	public QName getQNameElementDateTime() {
		return QNAME_ELEMENT_DATE_TIME;
	}

	public QName getQNameElementDuration() {
		return QNAME_ELEMENT_DURATION;
	}

	public QName getQNameElementDate() {
		return QNAME_ELEMENT_DATE;
	}

	public QName getQNameElementGMonth() {
		return QNAME_ELEMENT_G_MONTH;
	}

	public QName getQNameElementGYear() {
		return QNAME_ELEMENT_G_YEAR;
	}

	public QName getQNameElementGYearMonth() {
		return QNAME_ELEMENT_G_YEAR_MONTH;
	}

	public QName getQNameElementGDay() {
		return QNAME_ELEMENT_G_DAY;
	}

	public QName getQNameElementGMonthDay() {
		return QNAME_ELEMENT_G_MONTH_DAY;
	}

	public QName getQNameElementName() {
		return QNAME_ELEMENT_NAME;
	}

	public QName getQNameElementQName() {
		return QNAME_ELEMENT_QNAME;
	}

	public QName getQNameElementNCNAME() {
		return QNAME_ELEMENT_NCNAME;
	}

	public QName getQNameElementAnyURI() {
		return QNAME_ELEMENT_ANY_URI;
	}

	public QName getQNameElementID() {
		return QNAME_ELEMENT_ID;
	}

	public QName getQNameElementIDREF() {
		return QNAME_ELEMENT_IDREF;
	}

	public QName getQNameElementIDREFS() {
		return QNAME_ELEMENT_IDREFS;
	}

	public QName getQNameElementEntity() {
		return QNAME_ELEMENT_ENTITY;
	}

	public QName getQNameElementEntities() {
		return QNAME_ELEMENT_ENTITIES;
	}

	public QName getQNameElementNotation() {
		return QNAME_ELEMENT_ENTITIES;
	}

	public QName getQNameElementNMTOKEN() {
		return QNAME_ELEMENT_NMTOKEN;
	}

	public QName getQNameElementNMTOKENS() {
		return QNAME_ELEMENT_NMTOKENS;
	}

	public QName getQNameTypeString() {
		return QNAME_TYPE_STRING;
	}

	public QName getQNameTypeNormalizedString() {
		return QNAME_TYPE_NORMALIZED_STRING;
	}

	public QName getQNameTypeToken() {
		return QNAME_TYPE_TOKEN;
	}

	public QName getQNameTypeByte() {
		return QNAME_TYPE_BYTE;
	}

	public QName getQNameTypeUnsignedByte() {
		return QNAME_TYPE_UNSIGNED_BYTE;
	}

	public QName getQNameTypeBase64Binary() {
		return QNAME_TYPE_BASE64_BINARY;
	}

	public QName getQNameTypeHexBinary() {
		return QNAME_TYPE_HEX_BINARY;
	}

	public QName getQNameTypeInteger() {
		return QNAME_TYPE_INTEGER;
	}

	public QName getQNameTypePositiveInteger() {
		return QNAME_TYPE_POSITIVE_INTEGER;
	}

	public QName getQNameTypeNegativeInteger() {
		return QNAME_TYPE_NEGATIVE_INTEGER;
	}

	public QName getQNameTypeNonNegativeInteger() {
		return QNAME_TYPE_NON_NEGATIVE_INTEGER;
	}

	public QName getQNameTypeNonPositiveInteger() {
		return QNAME_TYPE_NON_POSITIVE_INTEGER;
	}

	public QName getQNameTypeInt() {
		return QNAME_TYPE_INT;
	}

	public QName getQNameTypeUnsignedInt() {
		return QNAME_TYPE_UNSIGNED_INT;
	}

	public QName getQNameTypeLong() {
		return QNAME_TYPE_LONG;
	}

	public QName getQNameTypeUnsignedLong() {
		return QNAME_TYPE_UNSIGNED_LONG;
	}

	public QName getQNameTypeShort() {
		return QNAME_TYPE_SHORT;
	}

	public QName getQNameTypeUnsignedShort() {
		return QNAME_TYPE_UNSIGNED_SHORT;
	}

	public QName getQNameTypeDecimal() {
		return QNAME_TYPE_DECIMAL;
	}

	public QName getQNameTypeFloat() {
		return QNAME_TYPE_FLOAT;
	}

	public QName getQNameTypeDouble() {
		return QNAME_TYPE_DOUBLE;
	}

	public QName getQNameTypeBoolean() {
		return QNAME_TYPE_BOOLEAN;
	}

	public QName getQNameTypeTime() {
		return QNAME_TYPE_TIME;
	}

	public QName getQNameTypeDateTime() {
		return QNAME_TYPE_DATE_TIME;
	}

	public QName getQNameTypeDuration() {
		return QNAME_TYPE_DURATION;
	}

	public QName getQNameTypeDate() {
		return QNAME_TYPE_DATE;
	}

	public QName getQNameTypeGMonth() {
		return QNAME_TYPE_G_MONTH;
	}

	public QName getQNameTypeGYear() {
		return QNAME_TYPE_G_YEAR;
	}

	public QName getQNameTypeGYearMonth() {
		return QNAME_TYPE_G_YEAR_MONTH;
	}

	public QName getQNameTypeGDay() {
		return QNAME_TYPE_G_DAY;
	}

	public QName getQNameTypeGMonthDay() {
		return QNAME_TYPE_G_MONTH_DAY;
	}

	public QName getQNameTypeName() {
		return QNAME_TYPE_NAME;
	}

	public QName getQNameTypeQName() {
		return QNAME_TYPE_QNAME;
	}

	public QName getQNameTypeNCNAME() {
		return QNAME_TYPE_NCNAME;
	}

	public QName getQNameTypeAnyURI() {
		return QNAME_TYPE_ANY_URI;
	}

	public QName getQNameTypeID() {
		return QNAME_TYPE_ID;
	}

	public QName getQNameTypeIDREF() {
		return QNAME_TYPE_IDREF;
	}

	public QName getQNameTypeIDREFS() {
		return QNAME_TYPE_IDREFS;
	}

	public QName getQNameTypeENTITY() {
		return QNAME_TYPE_ENTITY;
	}

	public QName getQNameTypeENTITIES() {
		return QNAME_TYPE_ENTITIES;
	}

	public QName getQNameTypeNotation() {
		return QNAME_TYPE_NOTATION;
	}

	public QName getQNameTypeNMTOKEN() {
		return QNAME_TYPE_NMTOKEN;
	}

	public QName getQNameTypeNMTOKENS() {
		return QNAME_TYPE_NMTOKENS;
	}

	public QName getQNameTypeLanguage() {
		return QNAME_TYPE_LANGUAGE;
	}

	/** SOAP attributes with non-colonized names */
	public QName getQNameAttrID() {
		return QNAME_ATTR_ID;
	}

	public QName getQNameAttrHREF() {
		return QNAME_ATTR_HREF;
	}

	/** SOAP Version used */
	public SOAPVersion getSOAPVersion() {
		return ver;
	}

	private void initSOAP11() {
		NS_WSDL_SOAP = SOAPConstants.NS_WSDL_SOAP;
		NS_SOAP_ENCODING = SOAPConstants.NS_SOAP_ENCODING;

		// other URIs
		URI_SOAP_TRANSPORT_HTTP = SOAPConstants.URI_SOAP_TRANSPORT_HTTP;

		// QNames
		QNAME_ADDRESS = SOAPConstants.QNAME_ADDRESS;
		QNAME_BINDING = SOAPConstants.QNAME_BINDING;
		QNAME_BODY = SOAPConstants.QNAME_BODY;
		QNAME_FAULT = SOAPConstants.QNAME_FAULT;
		QNAME_HEADER = SOAPConstants.QNAME_HEADER;
		QNAME_HEADERFAULT = SOAPConstants.QNAME_HEADERFAULT;
		QNAME_OPERATION = SOAPConstants.QNAME_OPERATION;

		// SOAP encoding QNames
		QNAME_TYPE_ARRAY = SOAPConstants.QNAME_TYPE_ARRAY;
		QNAME_ATTR_GROUP_COMMON_ATTRIBUTES =
			SOAPConstants.QNAME_ATTR_GROUP_COMMON_ATTRIBUTES;
		QNAME_ATTR_ARRAY_TYPE = SOAPConstants.QNAME_ATTR_ARRAY_TYPE;
		//QNAME_ATTR_ITEM_TYPE = null;
		//QNAME_ATTR_ARRAY_SIZE = null;
		QNAME_ATTR_OFFSET = SOAPConstants.QNAME_ATTR_OFFSET;
		QNAME_ATTR_POSITION = SOAPConstants.QNAME_ATTR_POSITION;

		QNAME_TYPE_BASE64 = SOAPConstants.QNAME_TYPE_BASE64;

		QNAME_ELEMENT_STRING = SOAPConstants.QNAME_ELEMENT_STRING;
		QNAME_ELEMENT_NORMALIZED_STRING =
			SOAPConstants.QNAME_ELEMENT_NORMALIZED_STRING;
		QNAME_ELEMENT_TOKEN = SOAPConstants.QNAME_ELEMENT_TOKEN;
		QNAME_ELEMENT_BYTE = SOAPConstants.QNAME_ELEMENT_TOKEN;
		QNAME_ELEMENT_UNSIGNED_BYTE = SOAPConstants.QNAME_ELEMENT_UNSIGNED_BYTE;
		QNAME_ELEMENT_BASE64_BINARY = SOAPConstants.QNAME_ELEMENT_BASE64_BINARY;
		QNAME_ELEMENT_HEX_BINARY = SOAPConstants.QNAME_ELEMENT_HEX_BINARY;
		QNAME_ELEMENT_INTEGER = SOAPConstants.QNAME_ELEMENT_INTEGER;
		QNAME_ELEMENT_POSITIVE_INTEGER =
			SOAPConstants.QNAME_ELEMENT_POSITIVE_INTEGER;
		QNAME_ELEMENT_NEGATIVE_INTEGER =
			SOAPConstants.QNAME_ELEMENT_NEGATIVE_INTEGER;
		QNAME_ELEMENT_NON_NEGATIVE_INTEGER =
			SOAPConstants.QNAME_ELEMENT_NON_NEGATIVE_INTEGER;
		QNAME_ELEMENT_NON_POSITIVE_INTEGER =
			SOAPConstants.QNAME_ELEMENT_NON_POSITIVE_INTEGER;
		QNAME_ELEMENT_INT = SOAPConstants.QNAME_ELEMENT_INT;
		QNAME_ELEMENT_UNSIGNED_INT = SOAPConstants.QNAME_ELEMENT_UNSIGNED_INT;
		QNAME_ELEMENT_LONG = SOAPConstants.QNAME_ELEMENT_LONG;
		QNAME_ELEMENT_UNSIGNED_LONG = SOAPConstants.QNAME_ELEMENT_UNSIGNED_LONG;
		QNAME_ELEMENT_SHORT = SOAPConstants.QNAME_ELEMENT_SHORT;
		QNAME_ELEMENT_UNSIGNED_SHORT =
			SOAPConstants.QNAME_ELEMENT_UNSIGNED_SHORT;
		QNAME_ELEMENT_DECIMAL = SOAPConstants.QNAME_ELEMENT_DECIMAL;
		QNAME_ELEMENT_FLOAT = SOAPConstants.QNAME_ELEMENT_FLOAT;
		QNAME_ELEMENT_DOUBLE = SOAPConstants.QNAME_ELEMENT_DOUBLE;
		QNAME_ELEMENT_BOOLEAN = SOAPConstants.QNAME_ELEMENT_BOOLEAN;
		QNAME_ELEMENT_TIME = SOAPConstants.QNAME_ELEMENT_TIME;
		QNAME_ELEMENT_DATE_TIME = SOAPConstants.QNAME_ELEMENT_DATE_TIME;
		QNAME_ELEMENT_DURATION = SOAPConstants.QNAME_ELEMENT_DURATION;
		QNAME_ELEMENT_DATE = SOAPConstants.QNAME_ELEMENT_DATE;
		QNAME_ELEMENT_G_MONTH = SOAPConstants.QNAME_ELEMENT_G_MONTH;
		QNAME_ELEMENT_G_YEAR = SOAPConstants.QNAME_ELEMENT_G_YEAR;
		QNAME_ELEMENT_G_YEAR_MONTH = SOAPConstants.QNAME_ELEMENT_G_YEAR_MONTH;
		QNAME_ELEMENT_G_DAY = SOAPConstants.QNAME_ELEMENT_G_DAY;
		QNAME_ELEMENT_G_MONTH_DAY = SOAPConstants.QNAME_ELEMENT_G_MONTH_DAY;
		QNAME_ELEMENT_NAME = SOAPConstants.QNAME_ELEMENT_NAME;
		QNAME_ELEMENT_QNAME = SOAPConstants.QNAME_ELEMENT_QNAME;
		QNAME_ELEMENT_NCNAME = SOAPConstants.QNAME_ELEMENT_NCNAME;
		QNAME_ELEMENT_ANY_URI = SOAPConstants.QNAME_ELEMENT_ANY_URI;
		QNAME_ELEMENT_ID = SOAPConstants.QNAME_ELEMENT_ID;
		QNAME_ELEMENT_IDREF = SOAPConstants.QNAME_ELEMENT_IDREF;
		QNAME_ELEMENT_IDREFS = SOAPConstants.QNAME_ELEMENT_IDREFS;
		QNAME_ELEMENT_ENTITY = SOAPConstants.QNAME_ELEMENT_ENTITY;
		QNAME_ELEMENT_ENTITIES = SOAPConstants.QNAME_ELEMENT_ENTITIES;
		QNAME_ELEMENT_NOTATION = SOAPConstants.QNAME_ELEMENT_NOTATION;
		QNAME_ELEMENT_NMTOKEN = SOAPConstants.QNAME_ELEMENT_NMTOKEN;
		QNAME_ELEMENT_NMTOKENS = SOAPConstants.QNAME_ELEMENT_NMTOKENS;

		QNAME_TYPE_STRING = SOAPConstants.QNAME_TYPE_STRING;
		QNAME_TYPE_NORMALIZED_STRING =
			SOAPConstants.QNAME_TYPE_NORMALIZED_STRING;
		QNAME_TYPE_TOKEN = SOAPConstants.QNAME_TYPE_TOKEN;
		QNAME_TYPE_BYTE = SOAPConstants.QNAME_TYPE_BYTE;
		QNAME_TYPE_UNSIGNED_BYTE = SOAPConstants.QNAME_TYPE_UNSIGNED_BYTE;
		QNAME_TYPE_BASE64_BINARY = SOAPConstants.QNAME_TYPE_BASE64_BINARY;
		QNAME_TYPE_HEX_BINARY = SOAPConstants.QNAME_TYPE_HEX_BINARY;
		QNAME_TYPE_INTEGER = SOAPConstants.QNAME_TYPE_INTEGER;
		QNAME_TYPE_POSITIVE_INTEGER = SOAPConstants.QNAME_TYPE_POSITIVE_INTEGER;
		QNAME_TYPE_NEGATIVE_INTEGER = SOAPConstants.QNAME_TYPE_NEGATIVE_INTEGER;
		QNAME_TYPE_NON_NEGATIVE_INTEGER =
			SOAPConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER;
		QNAME_TYPE_NON_POSITIVE_INTEGER =
			SOAPConstants.QNAME_TYPE_NON_POSITIVE_INTEGER;
		QNAME_TYPE_INT = SOAPConstants.QNAME_TYPE_INT;
		QNAME_TYPE_UNSIGNED_INT = SOAPConstants.QNAME_TYPE_UNSIGNED_INT;
		QNAME_TYPE_LONG = SOAPConstants.QNAME_TYPE_LONG;
		QNAME_TYPE_UNSIGNED_LONG = SOAPConstants.QNAME_TYPE_UNSIGNED_LONG;
		QNAME_TYPE_SHORT = SOAPConstants.QNAME_TYPE_SHORT;
		QNAME_TYPE_UNSIGNED_SHORT = SOAPConstants.QNAME_TYPE_UNSIGNED_SHORT;
		QNAME_TYPE_DECIMAL = SOAPConstants.QNAME_TYPE_DECIMAL;
		QNAME_TYPE_FLOAT = SOAPConstants.QNAME_TYPE_FLOAT;
		QNAME_TYPE_DOUBLE = SOAPConstants.QNAME_TYPE_DOUBLE;
		QNAME_TYPE_BOOLEAN = SOAPConstants.QNAME_TYPE_BOOLEAN;
		QNAME_TYPE_TIME = SOAPConstants.QNAME_TYPE_TIME;
		QNAME_TYPE_DATE_TIME = SOAPConstants.QNAME_TYPE_DATE_TIME;
		QNAME_TYPE_DURATION = SOAPConstants.QNAME_TYPE_DURATION;
		QNAME_TYPE_DATE = SOAPConstants.QNAME_TYPE_DATE;
		QNAME_TYPE_G_MONTH = SOAPConstants.QNAME_TYPE_G_MONTH;
		QNAME_TYPE_G_YEAR = SOAPConstants.QNAME_TYPE_G_YEAR;
		QNAME_TYPE_G_YEAR_MONTH = SOAPConstants.QNAME_TYPE_G_YEAR_MONTH;
		QNAME_TYPE_G_DAY = SOAPConstants.QNAME_TYPE_G_DAY;
		QNAME_TYPE_G_MONTH_DAY = SOAPConstants.QNAME_TYPE_G_MONTH_DAY;
		QNAME_TYPE_NAME = SOAPConstants.QNAME_TYPE_NAME;
		QNAME_TYPE_QNAME = SOAPConstants.QNAME_TYPE_QNAME;
		QNAME_TYPE_NCNAME = SOAPConstants.QNAME_TYPE_NCNAME;
		QNAME_TYPE_ANY_URI = SOAPConstants.QNAME_TYPE_ANY_URI;
		QNAME_TYPE_ID = SOAPConstants.QNAME_TYPE_ID;
		QNAME_TYPE_IDREF = SOAPConstants.QNAME_TYPE_IDREF;
		QNAME_TYPE_IDREFS = SOAPConstants.QNAME_TYPE_IDREFS;
		QNAME_TYPE_ENTITY = SOAPConstants.QNAME_TYPE_ENTITY;
		QNAME_TYPE_ENTITIES = SOAPConstants.QNAME_TYPE_ENTITIES;
		QNAME_TYPE_NOTATION = SOAPConstants.QNAME_TYPE_NOTATION;
		QNAME_TYPE_NMTOKEN = SOAPConstants.QNAME_TYPE_NMTOKEN;
		QNAME_TYPE_NMTOKENS = SOAPConstants.QNAME_TYPE_NMTOKENS;
		QNAME_TYPE_LANGUAGE = SOAPConstants.QNAME_TYPE_LANGUAGE;

		// SOAP attributes with non-colonized names
		QNAME_ATTR_ID = SOAPConstants.QNAME_ATTR_ID;
		QNAME_ATTR_HREF = SOAPConstants.QNAME_ATTR_HREF;
	}

	private void initSOAP12() {
		NS_WSDL_SOAP = SOAP12Constants.NS_WSDL_SOAP;
		NS_SOAP_ENCODING = SOAP12Constants.NS_SOAP_ENCODING;

		// other URIs
		URI_SOAP_TRANSPORT_HTTP = SOAP12Constants.URI_SOAP_TRANSPORT_HTTP;

		// QNames
		QNAME_ADDRESS = SOAP12Constants.QNAME_ADDRESS;
		QNAME_BINDING = SOAP12Constants.QNAME_BINDING;
		QNAME_BODY = SOAP12Constants.QNAME_BODY;
		QNAME_FAULT = SOAP12Constants.QNAME_FAULT;
		QNAME_HEADER = SOAP12Constants.QNAME_HEADER;
		QNAME_HEADERFAULT = SOAP12Constants.QNAME_HEADERFAULT;
		QNAME_OPERATION = SOAP12Constants.QNAME_OPERATION;

		// SOAP encoding QNames
		QNAME_TYPE_ARRAY = SOAP12Constants.QNAME_TYPE_ARRAY;
		QNAME_ATTR_GROUP_COMMON_ATTRIBUTES =
			SOAP12Constants.QNAME_ATTR_GROUP_COMMON_ATTRIBUTES;
		QNAME_ATTR_ARRAY_TYPE = SOAP12Constants.QNAME_ATTR_ARRAY_TYPE;
		QNAME_ATTR_ITEM_TYPE = SOAP12Constants.QNAME_ATTR_ITEM_TYPE;
		QNAME_ATTR_ARRAY_SIZE = SOAP12Constants.QNAME_ATTR_ARRAY_SIZE;
		QNAME_ATTR_OFFSET = SOAP12Constants.QNAME_ATTR_OFFSET;
		QNAME_ATTR_POSITION = SOAP12Constants.QNAME_ATTR_POSITION;

		QNAME_TYPE_BASE64 = SOAP12Constants.QNAME_TYPE_BASE64;

		QNAME_ELEMENT_STRING = SOAP12Constants.QNAME_ELEMENT_STRING;
		QNAME_ELEMENT_NORMALIZED_STRING =
			SOAP12Constants.QNAME_ELEMENT_NORMALIZED_STRING;
		QNAME_ELEMENT_TOKEN = SOAP12Constants.QNAME_ELEMENT_TOKEN;
		QNAME_ELEMENT_BYTE = SOAP12Constants.QNAME_ELEMENT_TOKEN;
		QNAME_ELEMENT_UNSIGNED_BYTE =
			SOAP12Constants.QNAME_ELEMENT_UNSIGNED_BYTE;
		QNAME_ELEMENT_BASE64_BINARY =
			SOAP12Constants.QNAME_ELEMENT_BASE64_BINARY;
		QNAME_ELEMENT_HEX_BINARY = SOAP12Constants.QNAME_ELEMENT_HEX_BINARY;
		QNAME_ELEMENT_INTEGER = SOAP12Constants.QNAME_ELEMENT_INTEGER;
		QNAME_ELEMENT_POSITIVE_INTEGER =
			SOAP12Constants.QNAME_ELEMENT_POSITIVE_INTEGER;
		QNAME_ELEMENT_NEGATIVE_INTEGER =
			SOAP12Constants.QNAME_ELEMENT_NEGATIVE_INTEGER;
		QNAME_ELEMENT_NON_NEGATIVE_INTEGER =
			SOAP12Constants.QNAME_ELEMENT_NON_NEGATIVE_INTEGER;
		QNAME_ELEMENT_NON_POSITIVE_INTEGER =
			SOAP12Constants.QNAME_ELEMENT_NON_POSITIVE_INTEGER;
		QNAME_ELEMENT_INT = SOAP12Constants.QNAME_ELEMENT_INT;
		QNAME_ELEMENT_UNSIGNED_INT = SOAP12Constants.QNAME_ELEMENT_UNSIGNED_INT;
		QNAME_ELEMENT_LONG = SOAP12Constants.QNAME_ELEMENT_LONG;
		QNAME_ELEMENT_UNSIGNED_LONG =
			SOAP12Constants.QNAME_ELEMENT_UNSIGNED_LONG;
		QNAME_ELEMENT_SHORT = SOAP12Constants.QNAME_ELEMENT_SHORT;
		QNAME_ELEMENT_UNSIGNED_SHORT =
			SOAP12Constants.QNAME_ELEMENT_UNSIGNED_SHORT;
		QNAME_ELEMENT_DECIMAL = SOAP12Constants.QNAME_ELEMENT_DECIMAL;
		QNAME_ELEMENT_FLOAT = SOAP12Constants.QNAME_ELEMENT_FLOAT;
		QNAME_ELEMENT_DOUBLE = SOAP12Constants.QNAME_ELEMENT_DOUBLE;
		QNAME_ELEMENT_BOOLEAN = SOAP12Constants.QNAME_ELEMENT_BOOLEAN;
		QNAME_ELEMENT_TIME = SOAP12Constants.QNAME_ELEMENT_TIME;
		QNAME_ELEMENT_DATE_TIME = SOAP12Constants.QNAME_ELEMENT_DATE_TIME;
		QNAME_ELEMENT_DURATION = SOAP12Constants.QNAME_ELEMENT_DURATION;
		QNAME_ELEMENT_DATE = SOAP12Constants.QNAME_ELEMENT_DATE;
		QNAME_ELEMENT_G_MONTH = SOAP12Constants.QNAME_ELEMENT_G_MONTH;
		QNAME_ELEMENT_G_YEAR = SOAP12Constants.QNAME_ELEMENT_G_YEAR;
		QNAME_ELEMENT_G_YEAR_MONTH = SOAP12Constants.QNAME_ELEMENT_G_YEAR_MONTH;
		QNAME_ELEMENT_G_DAY = SOAP12Constants.QNAME_ELEMENT_G_DAY;
		QNAME_ELEMENT_G_MONTH_DAY = SOAP12Constants.QNAME_ELEMENT_G_MONTH_DAY;
		QNAME_ELEMENT_NAME = SOAP12Constants.QNAME_ELEMENT_NAME;
		QNAME_ELEMENT_QNAME = SOAP12Constants.QNAME_ELEMENT_QNAME;
		QNAME_ELEMENT_NCNAME = SOAP12Constants.QNAME_ELEMENT_NCNAME;
		QNAME_ELEMENT_ANY_URI = SOAP12Constants.QNAME_ELEMENT_ANY_URI;
		QNAME_ELEMENT_ID = SOAP12Constants.QNAME_ELEMENT_ID;
		QNAME_ELEMENT_IDREF = SOAP12Constants.QNAME_ELEMENT_IDREF;
		QNAME_ELEMENT_IDREFS = SOAP12Constants.QNAME_ELEMENT_IDREFS;
		QNAME_ELEMENT_ENTITY = SOAP12Constants.QNAME_ELEMENT_ENTITY;
		QNAME_ELEMENT_ENTITIES = SOAP12Constants.QNAME_ELEMENT_ENTITIES;
		QNAME_ELEMENT_NOTATION = SOAP12Constants.QNAME_ELEMENT_NOTATION;
		QNAME_ELEMENT_NMTOKEN = SOAP12Constants.QNAME_ELEMENT_NMTOKEN;
		QNAME_ELEMENT_NMTOKENS = SOAP12Constants.QNAME_ELEMENT_NMTOKENS;

		QNAME_TYPE_STRING = SOAP12Constants.QNAME_TYPE_STRING;
		QNAME_TYPE_NORMALIZED_STRING =
			SOAP12Constants.QNAME_TYPE_NORMALIZED_STRING;
		QNAME_TYPE_TOKEN = SOAP12Constants.QNAME_TYPE_TOKEN;
		QNAME_TYPE_BYTE = SOAP12Constants.QNAME_TYPE_BYTE;
		QNAME_TYPE_UNSIGNED_BYTE = SOAP12Constants.QNAME_TYPE_UNSIGNED_BYTE;
		QNAME_TYPE_BASE64_BINARY = SOAP12Constants.QNAME_TYPE_BASE64_BINARY;
		QNAME_TYPE_HEX_BINARY = SOAP12Constants.QNAME_TYPE_HEX_BINARY;
		QNAME_TYPE_INTEGER = SOAP12Constants.QNAME_TYPE_INTEGER;
		QNAME_TYPE_POSITIVE_INTEGER =
			SOAP12Constants.QNAME_TYPE_POSITIVE_INTEGER;
		QNAME_TYPE_NEGATIVE_INTEGER =
			SOAP12Constants.QNAME_TYPE_NEGATIVE_INTEGER;
		QNAME_TYPE_NON_NEGATIVE_INTEGER =
			SOAP12Constants.QNAME_TYPE_NON_NEGATIVE_INTEGER;
		QNAME_TYPE_NON_POSITIVE_INTEGER =
			SOAP12Constants.QNAME_TYPE_NON_POSITIVE_INTEGER;
		QNAME_TYPE_INT = SOAP12Constants.QNAME_TYPE_INT;
		QNAME_TYPE_UNSIGNED_INT = SOAP12Constants.QNAME_TYPE_UNSIGNED_INT;
		QNAME_TYPE_LONG = SOAP12Constants.QNAME_TYPE_LONG;
		QNAME_TYPE_UNSIGNED_LONG = SOAP12Constants.QNAME_TYPE_UNSIGNED_LONG;
		QNAME_TYPE_SHORT = SOAP12Constants.QNAME_TYPE_SHORT;
		QNAME_TYPE_UNSIGNED_SHORT = SOAP12Constants.QNAME_TYPE_UNSIGNED_SHORT;
		QNAME_TYPE_DECIMAL = SOAP12Constants.QNAME_TYPE_DECIMAL;
		QNAME_TYPE_FLOAT = SOAP12Constants.QNAME_TYPE_FLOAT;
		QNAME_TYPE_DOUBLE = SOAP12Constants.QNAME_TYPE_DOUBLE;
		QNAME_TYPE_BOOLEAN = SOAP12Constants.QNAME_TYPE_BOOLEAN;
		QNAME_TYPE_TIME = SOAP12Constants.QNAME_TYPE_TIME;
		QNAME_TYPE_DATE_TIME = SOAP12Constants.QNAME_TYPE_DATE_TIME;
		QNAME_TYPE_DURATION = SOAP12Constants.QNAME_TYPE_DURATION;
		QNAME_TYPE_DATE = SOAP12Constants.QNAME_TYPE_DATE;
		QNAME_TYPE_G_MONTH = SOAP12Constants.QNAME_TYPE_G_MONTH;
		QNAME_TYPE_G_YEAR = SOAP12Constants.QNAME_TYPE_G_YEAR;
		QNAME_TYPE_G_YEAR_MONTH = SOAP12Constants.QNAME_TYPE_G_YEAR_MONTH;
		QNAME_TYPE_G_DAY = SOAP12Constants.QNAME_TYPE_G_DAY;
		QNAME_TYPE_G_MONTH_DAY = SOAP12Constants.QNAME_TYPE_G_MONTH_DAY;
		QNAME_TYPE_NAME = SOAP12Constants.QNAME_TYPE_NAME;
		QNAME_TYPE_QNAME = SOAP12Constants.QNAME_TYPE_QNAME;
		QNAME_TYPE_NCNAME = SOAP12Constants.QNAME_TYPE_NCNAME;
		QNAME_TYPE_ANY_URI = SOAP12Constants.QNAME_TYPE_ANY_URI;
		QNAME_TYPE_ID = SOAP12Constants.QNAME_TYPE_ID;
		QNAME_TYPE_IDREF = SOAP12Constants.QNAME_TYPE_IDREF;
		QNAME_TYPE_IDREFS = SOAP12Constants.QNAME_TYPE_IDREFS;
		QNAME_TYPE_ENTITY = SOAP12Constants.QNAME_TYPE_ENTITY;
		QNAME_TYPE_ENTITIES = SOAP12Constants.QNAME_TYPE_ENTITIES;
		QNAME_TYPE_NOTATION = SOAP12Constants.QNAME_TYPE_NOTATION;
		QNAME_TYPE_NMTOKEN = SOAP12Constants.QNAME_TYPE_NMTOKEN;
		QNAME_TYPE_NMTOKENS = SOAP12Constants.QNAME_TYPE_NMTOKENS;
		QNAME_TYPE_LANGUAGE = SOAP12Constants.QNAME_TYPE_LANGUAGE;

		// SOAP attributes with non-colonized names
		QNAME_ATTR_ID = SOAP12Constants.QNAME_ATTR_ID;
		QNAME_ATTR_HREF = SOAP12Constants.QNAME_ATTR_HREF;
	}

	public QName getQNameAttrItemType() {
		return QNAME_ATTR_ITEM_TYPE;
	}

	public QName getQNameAttrArraySize() {
		return QNAME_ATTR_ARRAY_SIZE;
	}

	private SOAPVersion ver;
	private String NS_WSDL_SOAP;
	private String NS_SOAP_ENCODING;
	private String URI_SOAP_TRANSPORT_HTTP;
	private QName QNAME_ADDRESS;
	private QName QNAME_BINDING;
	private QName QNAME_BODY;
	private QName QNAME_FAULT;
	private QName QNAME_HEADER;
	private QName QNAME_HEADERFAULT;
	private QName QNAME_OPERATION;
	private QName QNAME_TYPE_ARRAY;
	private QName QNAME_ATTR_GROUP_COMMON_ATTRIBUTES;
	private QName QNAME_ATTR_ARRAY_TYPE;
	private QName QNAME_ATTR_ITEM_TYPE;
	private QName QNAME_ATTR_ARRAY_SIZE;
	private QName QNAME_ATTR_OFFSET;
	private QName QNAME_ATTR_POSITION;
	private QName QNAME_TYPE_BASE64;
	private QName QNAME_ELEMENT_STRING;
	private QName QNAME_ELEMENT_NORMALIZED_STRING;
	private QName QNAME_ELEMENT_TOKEN;
	private QName QNAME_ELEMENT_BYTE;
	private QName QNAME_ELEMENT_UNSIGNED_BYTE;
	private QName QNAME_ELEMENT_BASE64_BINARY;
	private QName QNAME_ELEMENT_HEX_BINARY;
	private QName QNAME_ELEMENT_INTEGER;
	private QName QNAME_ELEMENT_POSITIVE_INTEGER;
	private QName QNAME_ELEMENT_NEGATIVE_INTEGER;
	private QName QNAME_ELEMENT_NON_NEGATIVE_INTEGER;
	private QName QNAME_ELEMENT_NON_POSITIVE_INTEGER;
	private QName QNAME_ELEMENT_INT;
	private QName QNAME_ELEMENT_UNSIGNED_INT;
	private QName QNAME_ELEMENT_LONG;
	private QName QNAME_ELEMENT_UNSIGNED_LONG;
	private QName QNAME_ELEMENT_SHORT;
	private QName QNAME_ELEMENT_UNSIGNED_SHORT;
	private QName QNAME_ELEMENT_DECIMAL;
	private QName QNAME_ELEMENT_FLOAT;
	private QName QNAME_ELEMENT_DOUBLE;
	private QName QNAME_ELEMENT_BOOLEAN;
	private QName QNAME_ELEMENT_TIME;
	private QName QNAME_ELEMENT_DATE_TIME;
	private QName QNAME_ELEMENT_DURATION;
	private QName QNAME_ELEMENT_DATE;
	private QName QNAME_ELEMENT_G_MONTH;
	private QName QNAME_ELEMENT_G_YEAR;
	private QName QNAME_ELEMENT_G_YEAR_MONTH;
	private QName QNAME_ELEMENT_G_DAY;
	private QName QNAME_ELEMENT_G_MONTH_DAY;
	private QName QNAME_ELEMENT_NAME;
	private QName QNAME_ELEMENT_QNAME;
	private QName QNAME_ELEMENT_NCNAME;
	private QName QNAME_ELEMENT_ANY_URI;
	private QName QNAME_ELEMENT_ID;
	private QName QNAME_ELEMENT_IDREF;
	private QName QNAME_ELEMENT_IDREFS;
	private QName QNAME_ELEMENT_ENTITY;
	private QName QNAME_ELEMENT_ENTITIES;
	private QName QNAME_ELEMENT_NOTATION;
	private QName QNAME_ELEMENT_NMTOKEN;
	private QName QNAME_ELEMENT_NMTOKENS;
	private QName QNAME_TYPE_STRING;
	private QName QNAME_TYPE_NORMALIZED_STRING;
	private QName QNAME_TYPE_TOKEN;
	private QName QNAME_TYPE_BYTE;
	private QName QNAME_TYPE_UNSIGNED_BYTE;
	private QName QNAME_TYPE_BASE64_BINARY;
	private QName QNAME_TYPE_HEX_BINARY;
	private QName QNAME_TYPE_INTEGER;
	private QName QNAME_TYPE_POSITIVE_INTEGER;
	private QName QNAME_TYPE_NEGATIVE_INTEGER;
	private QName QNAME_TYPE_NON_NEGATIVE_INTEGER;
	private QName QNAME_TYPE_NON_POSITIVE_INTEGER;
	private QName QNAME_TYPE_INT;
	private QName QNAME_TYPE_UNSIGNED_INT;
	private QName QNAME_TYPE_LONG;
	private QName QNAME_TYPE_UNSIGNED_LONG;
	private QName QNAME_TYPE_SHORT;
	private QName QNAME_TYPE_UNSIGNED_SHORT;
	private QName QNAME_TYPE_DECIMAL;
	private QName QNAME_TYPE_FLOAT;
	private QName QNAME_TYPE_DOUBLE;
	private QName QNAME_TYPE_BOOLEAN;
	private QName QNAME_TYPE_TIME;
	private QName QNAME_TYPE_DATE_TIME;
	private QName QNAME_TYPE_DURATION;
	private QName QNAME_TYPE_DATE;
	private QName QNAME_TYPE_G_MONTH;
	private QName QNAME_TYPE_G_YEAR;
	private QName QNAME_TYPE_G_YEAR_MONTH;
	private QName QNAME_TYPE_G_DAY;
	private QName QNAME_TYPE_G_MONTH_DAY;
	private QName QNAME_TYPE_NAME;
	private QName QNAME_TYPE_QNAME;
	private QName QNAME_TYPE_NCNAME;
	private QName QNAME_TYPE_ANY_URI;
	private QName QNAME_TYPE_ID;
	private QName QNAME_TYPE_IDREF;
	private QName QNAME_TYPE_IDREFS;
	private QName QNAME_TYPE_ENTITY;
	private QName QNAME_TYPE_ENTITIES;
	private QName QNAME_TYPE_NOTATION;
	private QName QNAME_TYPE_NMTOKEN;
	private QName QNAME_TYPE_NMTOKENS;
	private QName QNAME_TYPE_LANGUAGE;
	private QName QNAME_ATTR_ID;
	private QName QNAME_ATTR_HREF;
}
