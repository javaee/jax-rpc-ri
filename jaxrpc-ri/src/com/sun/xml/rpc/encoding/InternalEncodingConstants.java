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

package com.sun.xml.rpc.encoding;

import javax.xml.namespace.QName;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface InternalEncodingConstants {
    // internal URI
    public static final String JAXRPC_URI               = "http://java.sun.com/jax-rpc-ri/internal";

    // Attachment Types
    public static final QName QNAME_TYPE_IMAGE          = new QName(JAXRPC_URI, "image");
    public static final QName QNAME_TYPE_MIME_MULTIPART = new QName(JAXRPC_URI, "multipart");
    public static final QName QNAME_TYPE_SOURCE         = new QName(JAXRPC_URI, "text_xml");
    public static final QName QNAME_TYPE_DATA_HANDLER   = new QName(JAXRPC_URI, "datahandler");

    // Arrays
    public static final QName ARRAY_ELEMENT_NAME        = new QName("item");

    // Collections
    public static final QName COLLECTION_ELEMENT_NAME   = new QName("item");
    public static final QName QNAME_TYPE_COLLECTION     = new QName(JAXRPC_URI, "collection");
    public static final QName QNAME_TYPE_LIST           = new QName(JAXRPC_URI, "list");
    public static final QName QNAME_TYPE_SET            = new QName(JAXRPC_URI, "set");
    public static final QName QNAME_TYPE_ARRAY_LIST     = new QName(JAXRPC_URI, "arrayList");
    public static final QName QNAME_TYPE_VECTOR         = new QName(JAXRPC_URI, "vector");
    public static final QName QNAME_TYPE_STACK          = new QName(JAXRPC_URI, "stack");
    public static final QName QNAME_TYPE_LINKED_LIST    = new QName(JAXRPC_URI, "linkedList");
    public static final QName QNAME_TYPE_HASH_SET       = new QName(JAXRPC_URI, "hashSet");
    public static final QName QNAME_TYPE_TREE_SET       = new QName(JAXRPC_URI, "treeSet");

    // Maps
    public static final QName JAX_RPC_MAP_ENTRY_KEY_NAME    = new QName("key");
    public static final QName JAX_RPC_MAP_ENTRY_VALUE_NAME  = new QName("value");
    public static final QName QNAME_TYPE_MAP                = new QName(JAXRPC_URI, "map");
    public static final QName QNAME_TYPE_JAX_RPC_MAP_ENTRY  = new QName(JAXRPC_URI, "mapEntry");
    public static final QName QNAME_TYPE_HASH_MAP           = new QName(JAXRPC_URI, "hashMap");
    public static final QName QNAME_TYPE_TREE_MAP           = new QName(JAXRPC_URI, "treeMap");
    public static final QName QNAME_TYPE_HASHTABLE          = new QName(JAXRPC_URI, "hashtable");
    public static final QName QNAME_TYPE_PROPERTIES         = new QName(JAXRPC_URI, "properties");
//    public static final QName QNAME_TYPE_WEAK_HASH_MAP      = new QName(JAXRPC_URI, "weakhashmap");

}