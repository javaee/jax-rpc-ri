/*
 * $Id: HolderException.java,v 1.1 2006-04-12 20:32:04 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.util;

import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.Localizable;

/**
 * @author JAX-RPC Development Team
 */

public class HolderException extends JAXRPCExceptionBase {

	public HolderException(String key) {
		super(key);
	}

	public HolderException(String key, String arg) {
		super(key, arg);
	}

	public HolderException(String key, Localizable localizable) {
		super(key, localizable);
	}

	public HolderException(String key, Object[] args) {
		super(key, args);
	}

	public String getResourceBundleName() {
		return "com.sun.xml.rpc.resources.util";
	}
}
