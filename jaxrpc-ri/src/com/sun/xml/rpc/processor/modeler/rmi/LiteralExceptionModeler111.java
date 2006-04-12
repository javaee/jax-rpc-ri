/*
 * $Id: LiteralExceptionModeler111.java,v 1.1 2006-04-12 20:33:03 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.processor.modeler.rmi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.util.StringUtils;

/**
 *
 * @author JAX-RPC Development Team
 */
public class LiteralExceptionModeler111
    extends LiteralExceptionModeler
    implements RmiConstants {

    public LiteralExceptionModeler111(
        RmiModeler modeler,
        LiteralTypeModeler typeModeler) {
            
        super(modeler, typeModeler);
    }

    protected boolean isNewMemberNillable(LiteralElementMember member) {
        return false;
    }
}
