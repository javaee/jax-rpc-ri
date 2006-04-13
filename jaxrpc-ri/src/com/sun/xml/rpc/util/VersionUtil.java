/*
 * $Id: VersionUtil.java,v 1.2 2006-04-13 01:33:53 ofung Exp $
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
package com.sun.xml.rpc.util;

import java.util.StringTokenizer;

/**
 * Provides some version utilities.
 * 
 * @author JAX-RPC Development Team
 */

public final class VersionUtil implements Version {
	/**
	 * Check if java version is greater than 1.3
	 * 
	 * Method isJavaVersionGreaterThan1_3.
	 * @return boolean
	 */
	public static boolean isJavaVersionGreaterThan1_3() {
		try {
			Class.forName("java.net.URI");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
    
    /**
     * Check if java version is greater than 1.4
     *      
     * @return boolean
     */
    public static boolean isJavaVersionGreaterThan1_4() {
        try {
            Class.forName("java.util.UUID");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
	/**
	 * GetJAX-RPC full version, like: "JAX-RPC Reference Implementation 1.1 EA-R16"
	 * 
	 * Method getJAXRPCCompleteVersion.
	 * @return String
	 */
	public static String getJAXRPCCompleteVersion() {
		return PRODUCT_NAME + VERSION_NUMBER + BUILD_NUMBER;

	}

	/**
	 * Method getJAXRPCVersion.
	 * @return String
	 */
	public static String getJAXRPCVersion() {
		return VERSION_NUMBER;
	}

	/**
	 * Method getJAXRPCBuildNumber.
	 * @return String
	 */
	public static String getJAXRPCBuildNumber() {
		return BUILD_NUMBER;
	}

	/**
	 * Method getJAXRPCProductName.
	 * @return String
	 */
	public static String getJAXRPCProductName() {
		return PRODUCT_NAME;
	}

	/** 
	 * How many versions should we support, considering 2 right now.
	 */

	/**
	 * Check if the version is JAXRPC 1.01.
	 * @param version check if the required version is 1.01
	 * @return boolean ture if 1.01, flase otherwise
	 */
	public static boolean isVersion101(String version) {
		return JAXRPC_VERSION_101.equals(version);
	}

	/**
	 * Check if the version is JAXRPC 1.03
	 * 
	 * @param version version check if the required version is 1.03
	 * @return boolean ture if 1.03, flase otherwise
	 */
	public static boolean isVersion103(String version) {
		return JAXRPC_VERSION_103.equals(version);
	}

	/**
	     * @param version
	     * @return
	     */
	public static boolean isVersion11(String version) {
		return JAXRPC_VERSION_11.equals(version);
	}
    
    public static boolean isVersion111(String version) {
        return JAXRPC_VERSION_111.equals(version);
    }    

    public static boolean isVersion112(String version) {
        return JAXRPC_VERSION_112.equals(version) ||
               JAXRPC_VERSION_112_01.equals(version) ||
               JAXRPC_VERSION_112_02.equals(version);
    } 
	/**
	 * @param version
	 * @return
	 */
	public static boolean isValidVersion(String version) {
		return isVersion101(version)
			|| isVersion103(version)
			|| isVersion11(version) 
            || isVersion111(version)
            || isVersion112(version);
	}
	
	/**
	 * BugFix# 4948171
	 * Method getCanonicalVersion.
	 * 
	 * Converts a given version to the format "a.b.c.d"
	 * a - major version
	 * b - minor version
	 * c - minor minor version
	 * d - patch version
	 * 
	 * @return int[] Canonical version number
	 */
	public static int[] getCanonicalVersion(String version) {
		int[] canonicalVersion = new int[4];
		
		// initialize the default version numbers
		canonicalVersion[0] = 1;
		canonicalVersion[1] = 1;
		canonicalVersion[2] = 0;
		canonicalVersion[3] = 0;

		final String DASH_DELIM = "_";
		final String DOT_DELIM = ".";

		StringTokenizer tokenizer =
			new StringTokenizer(version, DOT_DELIM);
		String token = tokenizer.nextToken();

		// first token is major version and must not have "_"
		canonicalVersion[0] = Integer.parseInt(token);

		// resolve the minor version
		token = tokenizer.nextToken();
		if (token.indexOf(DASH_DELIM) == -1) {
			// a.b
			canonicalVersion[1] = Integer.parseInt(token);
		} else {
			// a.b_c
			StringTokenizer subTokenizer =
				new StringTokenizer(token, DASH_DELIM);
			canonicalVersion[1] = Integer.parseInt(subTokenizer.nextToken());
			// leave minorMinor default
			
			canonicalVersion[3] = Integer.parseInt(subTokenizer.nextToken());
		}

		// resolve the minorMinor and patch version, if any
		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (token.indexOf(DASH_DELIM) == -1) {
				// minorMinor
				canonicalVersion[2] = Integer.parseInt(token);

				// resolve patch, if any
				if (tokenizer.hasMoreTokens())
				canonicalVersion[3] = Integer.parseInt(tokenizer.nextToken());
			} else {
				// a.b.c_d
				StringTokenizer subTokenizer =
					new StringTokenizer(token, DASH_DELIM);
				// minorMinor
				canonicalVersion[2] = Integer.parseInt(subTokenizer.nextToken());
				
				// patch
				canonicalVersion[3] = Integer.parseInt(subTokenizer.nextToken());
			}
		}

		return canonicalVersion;
	}
	
	/**
	 * 
	 * @param version1
	 * @param version2
	 * @return -1, 0 or 1 based upon the comparison results 
	 * -1 if version1 is less than version2
	 * 0 if version1 is equal to version2
	 * 1 if version1 is greater than version2
	 */
	public static int compare(String version1, String version2) {
		int[] canonicalVersion1 = getCanonicalVersion(version1);
		int[] canonicalVersion2 = getCanonicalVersion(version2);
		
		if (canonicalVersion1[0] < canonicalVersion2[0]) {
			return -1;
		} else if (canonicalVersion1[0] > canonicalVersion2[0]) {
			return 1;
		} else {
			if (canonicalVersion1[1] < canonicalVersion2[1]) {
				return -1;
			} else if (canonicalVersion1[1] > canonicalVersion2[1]) {
				return 1;
			} else {
				if (canonicalVersion1[2] < canonicalVersion2[2]) {
					return -1;
				} else if (canonicalVersion1[2] > canonicalVersion2[2]) {
					return 1;
				} else {
					if (canonicalVersion1[3] < canonicalVersion2[3]) {
						return -1;
					} else if (canonicalVersion1[3] > canonicalVersion2[3]) {
						return 1;
					} else
						return 0;
				}
			}
		}
	}

	public static final String JAXRPC_VERSION_101 = "1.0.1";
	public static final String JAXRPC_VERSION_103 = "1.0.3"; 
	public static final String JAXRPC_VERSION_11 = "1.1";
    public static final String JAXRPC_VERSION_111 = "1.1.1";
    public static final String JAXRPC_VERSION_112 = "1.1.2";
    public static final String JAXRPC_VERSION_112_01 = "1.1.2_01";
    public static final String JAXRPC_VERSION_112_02 = "1.1.2_02";
    public static final String JAXRPC_VERSION_113 = "1.1.3";
    	// the latest version is default
	public static final String JAXRPC_VERSION_DEFAULT = JAXRPC_VERSION_113;
}
