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
 * $Id: AttributesExImpl.java,v 1.3 2007-07-13 23:36:27 ofung Exp $
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

package com.sun.xml.rpc.sp;

import org.xml.sax.Attributes;

/**
 * Implementation of the SAX Attributes interface which
 * provides additional features to support editor-oriented DOM
 * features:  exposing attribute defaulting.
 *
 * @author David Brownell
 * @author JAX-RPC RI Development Team
 */
final class AttributesExImpl implements AttributesEx {

    private static final int MAX_ATTRS = 10000;
    ////////////////////////////////////////////////////////////////////
    // Constructors.
    ////////////////////////////////////////////////////////////////////

    /**
     * Construct a new, empty AttributesImpl object.
     */
    public AttributesExImpl() {
        length = 0;
        data = null;
    }

    /**
     * Copy an existing Attributes object.
     *
     * <p>This constructor is especially useful inside a
     * {@link org.xml.sax.ContentHandler#startElement startElement} event.</p>
     *
     * @param atts The existing Attributes object.
     */
    public AttributesExImpl(Attributes atts) {
        setAttributes(atts);
    }

    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.Attributes.
    ////////////////////////////////////////////////////////////////////

    /**
     * Return the number of attributes in the list.
     *
     * @return The number of attributes in the list.
     * @see org.xml.sax.Attributes#getLength
     */
    public int getLength() {
        return length;
    }

    /**
     * Return an attribute's Namespace URI.
     *
     * @param index The attribute's index (zero-based).
     * @return The Namespace URI, the empty string if none is
     *         available, or null if the index is out of range.
     * @see org.xml.sax.Attributes#getURI
     */
    public String getURI(int index) {
        if (index >= 0 && index < length) {
            return data[index * 7];
        } else {
            return null;
        }
    }

    /**
     * Return an attribute's local name.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's local name, the empty string if 
     *         none is available, or null if the index if out of range.
     * @see org.xml.sax.Attributes#getLocalName
     */
    public String getLocalName(int index) {
        if (index >= 0 && index < length) {
            return data[index * 7 + 1];
        } else {
            return null;
        }
    }

    /**
     * Return an attribute's qualified (prefixed) name.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's qualified name, the empty string if 
     *         none is available, or null if the index is out of bounds.
     * @see org.xml.sax.Attributes#getQName
     */
    public String getQName(int index) {
        if (index >= 0 && index < length) {
            return data[index * 7 + 2];
        } else {
            return null;
        }
    }

    /**
     * Return an attribute's type by index.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's type, "CDATA" if the type is unknown, or null
     *         if the index is out of bounds.
     * @see org.xml.sax.Attributes#getType(int)
     */
    public String getType(int index) {
        if (index >= 0 && index < length) {
            return data[index * 7 + 3];
        } else {
            return null;
        }
    }

    /**
     * Return an attribute's value by index.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's value or null if the index is out of bounds.
     * @see org.xml.sax.Attributes#getValue(int)
     */
    public String getValue(int index) {
        if (index >= 0 && index < length) {
            return data[index * 7 + 4];
        } else {
            return null;
        }
    }

    /**
     * Return the default value of an attribute in this list (by position).
     */
    public String getDefault(int index) {
        if (index >= 0 && index < length) {
            return data[index * 7 + 5];
        } else {
            return null;
        }
    }

    /**
     * Returns true if the value was specified by a parsed document
     * (by position; no by-name variant).
     */
    public boolean isSpecified(int index) {
        if (index >= 0 && index < length) {
            return data[index * 7 + 6] == SPECIFIED_TRUE;
        } else {
            return false;
        }
    }

    /**
     * Look up an attribute's index by Namespace name.
     *
     * <p>In many cases, it will be more efficient to look up the name once and
     * use the index query methods rather than using the name query methods
     * repeatedly.</p>
     *
     * @param uri The attribute's Namespace URI, or the empty
     *        string if none is available.
     * @param localName The attribute's local name.
     * @return The attribute's index, or -1 if none matches.
     * @see org.xml.sax.Attributes#getIndex(java.lang.String,java.lang.String)
     */
    public int getIndex(String uri, String localName) {
        int max = length * 7;
        for (int i = 0; i < max; i += 7) {
            if (data[i].equals(uri) && data[i + 1].equals(localName)) {
                return i / 7;
            }
        }
        return -1;
    }

    /**
     * Look up an attribute's index by qualified (prefixed) name.
     *
     * @param qName The qualified name.
     * @return The attribute's index, or -1 if none matches.
     * @see org.xml.sax.Attributes#getIndex(java.lang.String)
     */
    public int getIndex(String qName) {
        int max = length * 7;
        for (int i = 0; i < max; i += 7) {
            if (data[i + 2].equals(qName)) {
                return i / 7;
            }
        }
        return -1;
    }

    /**
     * Look up an attribute's type by Namespace-qualified name.
     *
     * @param uri The Namespace URI, or the empty string for a name
     *        with no explicit Namespace URI.
     * @param localName The local name.
     * @return The attribute's type, or null if there is no
     *         matching attribute.
     * @see org.xml.sax.Attributes#getType(java.lang.String,java.lang.String)
     */
    public String getType(String uri, String localName) {
        int max = length * 7;
        for (int i = 0; i < max; i += 7) {
            if (data[i].equals(uri) && data[i + 1].equals(localName)) {
                return data[i + 3];
            }
        }
        return null;
    }

    /**
     * Look up an attribute's type by qualified (prefixed) name.
     *
     * @param qName The qualified name.
     * @return The attribute's type, or null if there is no
     *         matching attribute.
     * @see org.xml.sax.Attributes#getType(java.lang.String)
     */
    public String getType(String qName) {
        int max = length * 7;
        for (int i = 0; i < max; i += 7) {
            if (data[i + 2].equals(qName)) {
                return data[i + 3];
            }
        }
        return null;
    }

    /**
     * Look up an attribute's value by Namespace-qualified name.
     *
     * @param uri The Namespace URI, or the empty string for a name
     *        with no explicit Namespace URI.
     * @param localName The local name.
     * @return The attribute's value, or null if there is no
     *         matching attribute.
     * @see org.xml.sax.Attributes#getValue(java.lang.String,java.lang.String)
     */
    public String getValue(String uri, String localName) {
        int max = length * 7;
        for (int i = 0; i < max; i += 7) {
            if (data[i].equals(uri) && data[i + 1].equals(localName)) {
                return data[i + 4];
            }
        }
        return null;
    }

    /**
     * Look up an attribute's value by qualified (prefixed) name.
     *
     * @param qName The qualified name.
     * @return The attribute's value, or null if there is no
     *         matching attribute.
     * @see org.xml.sax.Attributes#getValue(java.lang.String)
     */
    public String getValue(String qName) {
        int max = length * 7;
        for (int i = 0; i < max; i += 7) {
            if (data[i + 2].equals(qName)) {
                return data[i + 4];
            }
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////
    // Manipulators.
    ////////////////////////////////////////////////////////////////////

    /**
     * Clear the attribute list for reuse.
     *
     * <p>Note that no memory is actually freed by this call:
     * the current arrays are kept so that they can be 
     * reused.</p>
     */
    public void clear() {
        /*
            by clearing the list, we let the JVM collect
            more garbage, which wins us more speed overall
        */
        int max = length * 7;
        for (int i = 0; i < max; ++i) {
            data[i] = null;
        }

        length = 0;
    }

    /**
     * Copy an entire Attributes object.
     *
     * <p>It may be more efficient to reuse an existing object
     * rather than constantly allocating new ones.</p>
     * 
     * @param atts The attributes to copy.
     */
    public void setAttributes(Attributes atts) {
        clear();
        length = atts.getLength();
        if (length > 0) {
            data = new String[length * 7];
            for (int i = 0; i < length; i++) {
                data[i * 7] = atts.getURI(i);
                data[i * 7 + 1] = atts.getLocalName(i);
                data[i * 7 + 2] = atts.getQName(i);
                data[i * 7 + 3] = atts.getType(i);
                data[i * 7 + 4] = atts.getValue(i);
            }
        }
    }

    /**
     * Add an attribute to the end of the list.
     *
     * <p>For the sake of speed, this method does no checking
     * to see if the attribute is already in the list: that is
     * the responsibility of the application.</p>
     *
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param qName The qualified (prefixed) name, or the empty string
     *        if qualified names are not available.
     * @param type The attribute type as a string.
     * @param value The attribute value.
     */
    public void addAttribute(
        String uri,
        String localName,
        String qName,
        String type,
        String value) {
        if (ensureCapacity(length + 1)) {
            data[length * 7] = uri;
            data[length * 7 + 1] = localName;
            data[length * 7 + 2] = qName;
            data[length * 7 + 3] = type;
            data[length * 7 + 4] = value;
            length++;
        }
    }

    /**
     * Add an attribute to this list
     */
    public void addAttribute(
        String uri,
        String localName,
        String qName,
        String type,
        String value,
        String defaultValue,
        boolean isSpecified) {
        if (ensureCapacity(length + 1)) {
            data[length * 7] = uri;
            data[length * 7 + 1] = localName;
            data[length * 7 + 2] = qName;
            data[length * 7 + 3] = type;
            data[length * 7 + 4] = value;
            data[length * 7 + 5] = defaultValue;
            data[length * 7 + 6] = (isSpecified ? SPECIFIED_TRUE : null);
            length++;
        }
    }

    /**
     * Set an attribute in the list.
     *
     * <p>For the sake of speed, this method does no checking
     * for name conflicts or well-formedness: such checks are the
     * responsibility of the application.</p>
     *
     * @param index The index of the attribute (zero-based).
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param qName The qualified name, or the empty string
     *        if qualified names are not available.
     * @param type The attribute type as a string.
     * @param value The attribute value.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setAttribute(
        int index,
        String uri,
        String localName,
        String qName,
        String type,
        String value) {
        if (index >= 0 && index < length) {
            data[index * 7] = uri;
            data[index * 7 + 1] = localName;
            data[index * 7 + 2] = qName;
            data[index * 7 + 3] = type;
            data[index * 7 + 4] = value;
        } else {
            badIndex(index);
        }
    }

    public void setAttribute(
        int index,
        String uri,
        String localName,
        String qName,
        String type,
        String value,
        String defaultValue,
        boolean isSpecified) {
        if (index >= 0 && index < length) {
            data[index * 7] = uri;
            data[index * 7 + 1] = localName;
            data[index * 7 + 2] = qName;
            data[index * 7 + 3] = type;
            data[index * 7 + 4] = value;
            data[index * 7 + 5] = defaultValue;
            data[index * 7 + 6] = (isSpecified ? SPECIFIED_TRUE : null);
        } else {
            badIndex(index);
        }
    }

    /**
     * Remove an attribute from the list.
     *
     * @param index The index of the attribute (zero-based).
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void removeAttribute(int index) {
        if (index >= 0 && index < length) {
            data[index * 7] = null;
            data[index * 7 + 1] = null;
            data[index * 7 + 2] = null;
            data[index * 7 + 3] = null;
            data[index * 7 + 4] = null;
            data[index * 7 + 5] = null;
            data[index * 7 + 6] = null;
            if (index < length - 1) {
                System.arraycopy(
                    data,
                    (index + 1) * 7,
                    data,
                    index * 7,
                    (length - index - 1) * 7);
            }
            length--;
        } else {
            badIndex(index);
        }
    }

    /**
     * Set the Namespace URI of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param uri The attribute's Namespace URI, or the empty
     *        string for none.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setURI(int index, String uri) {
        if (index >= 0 && index < length) {
            data[index * 7] = uri;
        } else {
            badIndex(index);
        }
    }

    /**
     * Set the local name of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param localName The attribute's local name, or the empty
     *        string for none.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setLocalName(int index, String localName) {
        if (index >= 0 && index < length) {
            data[index * 7 + 1] = localName;
        } else {
            badIndex(index);
        }
    }

    /**
     * Set the qualified name of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param qName The attribute's qualified name, or the empty
     *        string for none.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setQName(int index, String qName) {
        if (index >= 0 && index < length) {
            data[index * 7 + 2] = qName;
        } else {
            badIndex(index);
        }
    }

    /**
     * Set the type of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param type The attribute's type.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setType(int index, String type) {
        if (index >= 0 && index < length) {
            data[index * 7 + 3] = type;
        } else {
            badIndex(index);
        }
    }

    /**
     * Set the value of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param value The attribute's value.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setValue(int index, String value) {
        if (index >= 0 && index < length) {
            data[index * 7 + 4] = value;
        } else {
            badIndex(index);
        }
    }

    /**
     * Set the default value of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param value The attribute's default value.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setDefault(int index, String defaultValue) {
        if (index >= 0 && index < length) {
            data[index * 7 + 5] = defaultValue;
        } else {
            badIndex(index);
        }
    }

    /**
     * Set the specified flag of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param specified The attribute's specified flag.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setSpecified(int index, boolean specified) {
        if (index >= 0 && index < length) {
            data[index * 7 + 6] = (specified ? SPECIFIED_TRUE : null);
        } else {
            badIndex(index);
        }
    }

    /**
     * Returns the name of the ID attribute.
     */
    public String getIdAttributeName() {
        return idAttributeName;
    }

    /**
     * Allows parser to set the name of the ID attribute.
     */
    void setIdAttributeName(String name) {
        idAttributeName = name;
    }

    ////////////////////////////////////////////////////////////////////
    // Internal methods.
    ////////////////////////////////////////////////////////////////////

    /**
     * Ensure the internal array's capacity.
     *
     * @param n The minimum number of attributes that the array must
     *        be able to hold.
     */
    private boolean ensureCapacity(int n) {
        if (n <= 0) {
            return true;
        }
        if (n > MAX_ATTRS) {
            return false;
        }
        int max;
        if (data == null || data.length == 0) {
            max = 35;
        } else if (data.length >= n * 7) {
            return true;
        } else {
            max = data.length;
        }
        while (max < n * 7) {
            max *= 2;
        }

        String newData[] = new String[max];
        if (length > 0) {
            System.arraycopy(data, 0, newData, 0, length * 7);
        }
        data = newData;
        return true;
    }

    /**
     * Report a bad array index in a manipulator.
     *
     * @param index The index to report.
     * @exception java.lang.ArrayIndexOutOfBoundsException Always.
     */
    private void badIndex(int index) throws ArrayIndexOutOfBoundsException {
        String msg = "Attempt to modify attribute at illegal index: " + index;
        throw new ArrayIndexOutOfBoundsException(msg);
    }

    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    int length;
    String data[];

    // ID attribute name, as declared
    private String idAttributeName;

    private final static String SPECIFIED_TRUE = "";
}
