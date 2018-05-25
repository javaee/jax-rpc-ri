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

package com.sun.xml.rpc.util;


//package java.util;

import java.security.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author Vivek Pandey
 *
 * Modified visibility to hide it from applications.
 * 
 */

/**
 * 
 * A class that represents a universally unique identifier (UUID). A UUID
 * represents a 128-bit value.
 *
 * <p>There exist different variants of these global identifiers. The methods
 * of this class are for manipulating the Leach-Salz variant, although the
 * constructors allow the creation of any variant of UUID (described below).
 * 
 * <p>The layout of a variant 2 (Leach-Salz) UUID is as follows:
 *
 * The most significant long consists of the following unsigned fields:
 * <pre>
 * 0xFFFFFFFF00000000 time_low
 * 0x00000000FFFF0000 time_mid
 * 0x000000000000F000 version
 * 0x0000000000000FFF time_hi
 * </pre>
 * The least significant long consists of the following unsigned fields:
 * <pre>
 * 0xC000000000000000 variant
 * 0x3FFF000000000000 clock_seq
 * 0x0000FFFFFFFFFFFF node
 * </pre>
 *
 * <p>The variant field contains a value which identifies the layout of
 * the <tt>UUID</tt>. The bit layout described above is valid only for
 * a <tt>UUID</tt> with a variant value of 2, which indicates the
 * Leach-Salz variant.
 *
 * <p>The version field holds a value that describes the type of this
 * <tt>UUID</tt>. There are four different basic types of UUIDs: time-based,
 * DCE security, name-based, and randomly generated UUIds. These types
 * have a version value of 1, 2, 3 and 4, respectively.
 * 
 * <p>For more information including algorithms used to create <tt>UUID</tt>s,
 * see the expired Internet-Draft <a href="http://www.opengroup.org/dce/info/draft-leach-uuids-guids-01.txt">UUIDs and GUIDs</a>
 * or the standards body definition at
 * <a href="http://www.iso.ch/cate/d2229.html">ISO/IEC 11578:1996</a>.
 *
 * @version 1.3, 12/19/03
 * @since   JDK1.5
 */
final class UUID 
//implements java.io.Serializable, Comparable<UUID> {
implements java.io.Serializable{    

    /**
     * Explicit serialVersionUID for interoperability.
     */
     private static final long serialVersionUID = -4856846361193249489L;

    /*
     * The most significant 64 bits of this UUID.
     *
     * @serial
     */
    private long mostSigBits;

    /*
     * The least significant 64 bits of this UUID.
     *
     * @serial
     */
    private long leastSigBits;

    /*
     * The version number associated with this UUID. Computed on demand.
     */
    private transient int version = -1;

    /*
     * The variant number associated with this UUID. Computed on demand.
     */
    private transient int variant = -1;

    /*
     * The timestamp associated with this UUID. Computed on demand.
     */
    private transient long timestamp = -1;

    /*
     * The clock sequence associated with this UUID. Computed on demand.
     */
    private transient int sequence = -1;

    /*
     * The node number associated with this UUID. Computed on demand.
     */
    private transient long node = -1;

    /*
     * The random number generator used by this class to create random
     * based UUIDs.
     */
    private static SecureRandom numberGenerator = null;

    // Constructors and Factories

    /*
     * Private constructor which uses a byte array to construct the new UUID.
     */
    private UUID(byte[] data) {
        //assert data.length == 16;
        if(data.length != 16) {
            //TBD: throw exception, vivekp
        }
        for (int i=0; i<8; i++)
            mostSigBits = (mostSigBits << 8) | (data[i] & 0xff);
        for (int i=8; i<16; i++)
            leastSigBits = (leastSigBits << 8) | (data[i] & 0xff);
    }

    /**
     * Constructs a new <tt>UUID</tt> using the specified data.
     * <tt>mostSigBits</tt> is used for the most significant 64 bits of the
     * <tt>UUID</tt> and <tt>leastSig</tt> becomes the least significant 64
     * bits of the <tt>UUID</tt>.
     *
     * @param  mostSig
     * @param  leastSig
     */
    private UUID(long mostSigBits, long leastSigBits) {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    /**
     * Constructs a new <tt>UUID</tt> using 16 bytes read from the specified 
     * data source in standard network byte order.
     *
     * @param  in - the input stream to read data from
     * @throws java.io.IOException - if an error occurs while reading 16 bytes
     */
    private UUID(java.io.DataInput in) throws IOException {
    this.mostSigBits = in.readLong();
    this.leastSigBits = in.readLong();
    }

    /**
     * Static factory to retrieve a type 4 (pseudo randomly generated) UUID.
     *
     * @return  a randomly generated <tt>UUID</tt>.
     */
    protected static UUID randomUUID() {
        if (numberGenerator == null)
            numberGenerator = new SecureRandom();

        byte[] randomBytes = new byte[16];
        numberGenerator.nextBytes(randomBytes);
        randomBytes[6]  &= 0x0f;  /* clear version        */
        randomBytes[6]  |= 0x40;  /* set to version 4     */
        randomBytes[8]  &= 0x3f;  /* clear variant        */
        randomBytes[8]  |= 0x80;  /* set to IETF variant  */
        UUID result = new UUID(randomBytes);
        return new UUID(randomBytes);
    }

    /**
     * Static factory to retrieve a type 3 (name based) <tt>UUID</tt> based on
     * the specified String.
     *
     * @param  a string to be used to construct a <tt>UUID</tt>.
     * @return  a <tt>UUID</tt generated from the specified string.
     */
    private static UUID nameUUIDFromString(String name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported");
        }
        byte[] md5Bytes;
        try {
            md5Bytes = md.digest(name.getBytes("8859_1"));
        } catch (UnsupportedEncodingException uee) {
            throw new InternalError("8859_1 not supported");
        }
        md5Bytes[6]  &= 0x0f;  /* clear version        */
        md5Bytes[6]  |= 0x30;  /* set to version 3     */
        md5Bytes[8]  &= 0x3f;  /* clear variant        */
        md5Bytes[8]  |= 0x80;  /* set to IETF variant  */
        return new UUID(md5Bytes);
    }

    /**
     * Static factory to retrieve a type 3 (name based) <tt>UUID</tt> based on
     * the specified byte array.
     *
     * @param  a byte array to be used to construct a <tt>UUID</tt>.
     * @return  a <tt>UUID</tt generated from the specified array.
     */
    private static UUID nameUUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported");
        }
        byte[] md5Bytes = md.digest(name);
        md5Bytes[6]  &= 0x0f;  /* clear version        */
        md5Bytes[6]  |= 0x30;  /* set to version 3     */
        md5Bytes[8]  &= 0x3f;  /* clear variant        */
        md5Bytes[8]  |= 0x80;  /* set to IETF variant  */
        return new UUID(md5Bytes);
    }

    /**
     * Creates a <tt>UUID</tt> from the string standard representation as
     * described in the toString() method.
     *
     * @param  a string that specifies a specific <tt>UUID</tt>.
     * @return  a <tt>UUID</tt with the specified value.
     */
    private static UUID fromString(String name) {
        String[] components = name.split("-");
        if (components.length != 5)
            throw new IllegalArgumentException("Invalid UUID string: "+name);
        for (int i=0; i<5; i++)
            components[i] = "0x"+components[i];

        long mostSigBits = Long.decode(components[0]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[1]).longValue();
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(components[2]).longValue();

        long leastSigBits = Long.decode(components[3]).longValue();
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(components[4]).longValue();

        return new UUID(mostSigBits, leastSigBits);
    }

    // Field Accessor Methods

    /**
     * The version number associated with this <tt>UUID</tt>. The version 
     * number describes how this <tt>UUID</tt> was generated.
     *
     * The version number has the following meaning:<p>
     * <ul>
     * <li>1    Time-based UUID
     * <li>2    DCE security UUID
     * <li>3    Name-based UUID
     * <li>4    Randomly generated UUID
     * </ul>
     *
     * @return  the version number of this <tt>UUID</tt>.
     */
    private int version() {
        if (version < 0) {
            // Version is bits masked by 0x000000000000F000 in MS long
            version = (int)((mostSigBits >> 12) & 0x0f);
        }
        return version;
    }

    /**
     * The variant number associated with this <tt>UUID</tt>. The variant 
     * number describes the layout of the <tt>UUID</tt>.
     *
     * The variant number has the following meaning:<p>
     * <ul>
     * <li>0    Reserved for NCS backward compatibility
     * <li>2    The Leach-Salz variant (used by this class)
     * <li>6    Reserved, Microsoft Corporation backward compatibility
     * <li>7    Reserved for future definition
     * </ul>
     *
     * @return  the variant number of this <tt>UUID</tt>.
     */
    private int variant() {
        if (variant < 0) {
            // This field is composed of a varying number of bits
            if ((leastSigBits >>> 63) == 0) {
                variant = 0;
            } else if ((leastSigBits >>> 62) == 2) {
                variant = 2;
            } else {
                variant = (int)(leastSigBits >>> 61);
            }
        }
        return variant;
    }

    /**
     * The timestamp value associated with this UUID.
     *
     * <p>The 60 bit timestamp value is constructed from the time_low,
     * time_mid, and time_hi fields of this <tt>UUID</tt>. The resulting 
     * timestamp is measured in 100-nanosecond units since midnight, 
     * October 15, 1582 UTC.<p>
     *
     * The timestamp value is only meaningful in a time-based UUID, which
     * has version type 1. If this <tt>UUID</tt> is not a time-based UUID then
     * this method throws UnsupportedOperationException.
     * 
     * @throws UnsupportedOperationException if this UUID is not a 
     *         version 1 UUID.
     */
    private long timestamp() {
        if (version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
        timestamp = (mostSigBits & 0x0000000000000FFFL) << 48;
        timestamp |= ((mostSigBits >> 16) & 0xFFFFL) << 32;
        timestamp |= mostSigBits >>> 32;
        return timestamp;
    }

    /**
     * The clock sequence value associated with this UUID.
     *
     * <p>The 14 bit clock sequence value is constructed from the clock
     * sequence field of this UUID. The clock sequence field is used to
     * guarantee temporal uniqueness in a time-based UUID.<p>
     *
     * The  clockSequence value is only meaningful in a time-based UUID, which
     * has version type 1. If this UUID is not a time-based UUID then
     * this method throws UnsupportedOperationException.
     * 
     * @return  the clock sequence of this <tt>UUID</tt>.
     * @throws UnsupportedOperationException if this UUID is not a 
     *         version 1 UUID.
     */
    private int clockSequence() {
        if (version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
        sequence = (int)((leastSigBits & 0x3FFF000000000000L) >>> 48);
        return sequence;
    }

    /**
     * The node value associated with this UUID.
     *
     * <p>The 48 bit node value is constructed from the node field of
     * this UUID. This field is intended to hold the IEEE 802 address 
     * of the machine that generated this UUID to guarantee spatial
     * uniqueness.<p>
     *
     * The node value is only meaningful in a time-based UUID, which
     * has version type 1. If this UUID is not a time-based UUID then
     * this method throws UnsupportedOperationException.
     * 
     * @return  the node value of this <tt>UUID</tt>.
     * @throws UnsupportedOperationException if this UUID is not a
     *         version 1 UUID.
     */
    private long node() {
        if (version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
        node = leastSigBits & 0x0000FFFFFFFFFFFFL;
        return node;
    }

    // Object Inherited Methods

    /**
     * Returns a <code>String</code> object representing this
     * <code>UUID</code>.
     * 
     * <p>The UUID string representation is as described by this BNF : 
     * <pre>
     *  UUID                   = <time_low> "-" <time_mid> "-"
     *                           <time_high_and_version> "-"
     *                           <variant_and_sequence> "-"
     *                           <node>
     *  time_low               = 4*<hexOctet>
     *  time_mid               = 2*<hexOctet>
     *  time_high_and_version  = 2*<hexOctet>
     *  variant_and_sequence   = 2*<hexOctet>
     *  node                   = 6*<hexOctet>
     *  hexOctet               = <hexDigit><hexDigit>
     *  hexDigit               =
     *        "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
     *        | "a" | "b" | "c" | "d" | "e" | "f"
     *        | "A" | "B" | "C" | "D" | "E" | "F"
     * </pre>
     *
     * @return  a string representation of this <tt>UUID</tt>.
     */
    public String toString() {
    return (digits(mostSigBits >> 32, 8) + "-" +
        digits(mostSigBits >> 16, 4) + "-" +
        digits(mostSigBits, 4) + "-" +
        digits(leastSigBits >> 48, 4) + "-" +
        digits(leastSigBits, 12));
    }

    /** Returns val represented by the specified number of hex digits. */
    private static String digits(long val, int digits) {
    long hi = 1L << (digits * 4);
    return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    /**
     * Returns a hash code for this <code>UUID</code>.
     *
     * @return  a hash code value for this <tt>UUID</tt>. 
     */
    public int hashCode() {
    return (int)((mostSigBits >> 32) ^
                     mostSigBits ^
                     (leastSigBits >> 32) ^
                     leastSigBits);
    }

    /**
     * Compares this object to the specified object.  The result is
     * <tt>true</tt> if and only if the argument is not
     * <tt>null</tt>, is is a <tt>UUID</tt> object, has the same variant,
     * and contains the same value, bit for bit, as this <tt>UUID</tt>.
     *
     * @param   obj   the object to compare with.
     * @return  <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
    if (!(obj instanceof UUID))
        return false;
        if (((UUID)obj).variant() != this.variant())
            return false;
        UUID id = (UUID)obj;
    return (mostSigBits == id.mostSigBits &&
                leastSigBits == id.leastSigBits);
    }

    // Comparison Operations

    /**
     * Compares this UUID with the specified UUID.
     * 
     * <p>The first of two UUIDs follows the second if the most significant
     * field in which the UUIDs differ is greater for the first UUID.
     * 
     * <p>An IllegalArgumentException is thrown if the argument is not of the
     * same variant type as this <tt>UUID</tt>.
     *
     * @param  val <tt>UUID</tt> to which this <tt>UUID</tt> is to be compared.
     * @return -1, 0 or 1 as this <tt>UUID</tt> is less than, equal
     *         to, or greater than <tt>val</tt>.
     * @throws  IllegalArgumentException if <tt>val</tt> is a different
     *          variant of <tt>UUID</tt>.
     */
    private int compareTo(UUID val) {
        if (val.variant() != this.variant())
            throw new IllegalArgumentException();
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        return (this.mostSigBits < val.mostSigBits ? -1 : 
                (this.mostSigBits > val.mostSigBits ? 1 :
                 (this.leastSigBits < val.leastSigBits ? -1 :
                  (this.leastSigBits > val.leastSigBits ? 1 :
                   0))));
    }
}
