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
 * $Id: BuiltInTypes.java,v 1.3 2007-07-13 23:36:43 ofung Exp $
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

package com.sun.xml.rpc.wsdl.document.schema;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface BuiltInTypes {
    public QName STRING = SchemaConstants.QNAME_TYPE_STRING;
    public QName NORMALIZED_STRING =
        SchemaConstants.QNAME_TYPE_NORMALIZED_STRING;
    public QName TOKEN = SchemaConstants.QNAME_TYPE_TOKEN;
    public QName BYTE = SchemaConstants.QNAME_TYPE_BYTE;
    public QName UNSIGNED_BYTE = SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE;
    public QName BASE64_BINARY = SchemaConstants.QNAME_TYPE_BASE64_BINARY;
    public QName HEX_BINARY = SchemaConstants.QNAME_TYPE_HEX_BINARY;
    public QName INTEGER = SchemaConstants.QNAME_TYPE_INTEGER;
    public QName POSITIVE_INTEGER = SchemaConstants.QNAME_TYPE_POSITIVE_INTEGER;
    public QName NEGATIVE_INTEGER = SchemaConstants.QNAME_TYPE_NEGATIVE_INTEGER;
    public QName NON_NEGATIVE_INTEGER =
        SchemaConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER;
    public QName NON_POSITIVE_INTEGER =
        SchemaConstants.QNAME_TYPE_NON_POSITIVE_INTEGER;
    public QName INT = SchemaConstants.QNAME_TYPE_INT;
    public QName UNSIGNED_INT = SchemaConstants.QNAME_TYPE_UNSIGNED_INT;
    public QName LONG = SchemaConstants.QNAME_TYPE_LONG;
    public QName UNSIGNED_LONG = SchemaConstants.QNAME_TYPE_UNSIGNED_LONG;
    public QName SHORT = SchemaConstants.QNAME_TYPE_SHORT;
    public QName UNSIGNED_SHORT = SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT;
    public QName DECIMAL = SchemaConstants.QNAME_TYPE_DECIMAL;
    public QName FLOAT = SchemaConstants.QNAME_TYPE_FLOAT;
    public QName DOUBLE = SchemaConstants.QNAME_TYPE_DOUBLE;
    public QName BOOLEAN = SchemaConstants.QNAME_TYPE_BOOLEAN;
    public QName TIME = SchemaConstants.QNAME_TYPE_TIME;
    public QName DATE_TIME = SchemaConstants.QNAME_TYPE_DATE_TIME;
    public QName DURATION = SchemaConstants.QNAME_TYPE_DURATION;
    public QName DATE = SchemaConstants.QNAME_TYPE_DATE;
    public QName G_MONTH = SchemaConstants.QNAME_TYPE_G_MONTH;
    public QName G_YEAR = SchemaConstants.QNAME_TYPE_G_YEAR;
    public QName G_YEAR_MONTH = SchemaConstants.QNAME_TYPE_G_YEAR_MONTH;
    public QName G_DAY = SchemaConstants.QNAME_TYPE_G_DAY;
    public QName G_MONTH_DAY = SchemaConstants.QNAME_TYPE_G_MONTH_DAY;
    public QName NAME = SchemaConstants.QNAME_TYPE_NAME;
    public QName QNAME = SchemaConstants.QNAME_TYPE_QNAME;
    public QName NCNAME = SchemaConstants.QNAME_TYPE_NCNAME;
    public QName ANY_URI = SchemaConstants.QNAME_TYPE_ANY_URI;
    public QName ID = SchemaConstants.QNAME_TYPE_ID;
    public QName IDREF = SchemaConstants.QNAME_TYPE_IDREF;
    public QName IDREFS = SchemaConstants.QNAME_TYPE_IDREFS;
    public QName ENTITY = SchemaConstants.QNAME_TYPE_ENTITY;
    public QName ENTITIES = SchemaConstants.QNAME_TYPE_ENTITIES;
    public QName NOTATION = SchemaConstants.QNAME_TYPE_NOTATION;
    public QName NMTOKEN = SchemaConstants.QNAME_TYPE_NMTOKEN;
    public QName NMTOKENS = SchemaConstants.QNAME_TYPE_NMTOKENS;
    public QName LANGUAGE = SchemaConstants.QNAME_TYPE_LANGUAGE;
    public QName ANY_SIMPLE_URTYPE = SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE;

    //xsd:list
    public QName LIST = SchemaConstants.QNAME_LIST;
}
