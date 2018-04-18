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
 * $Id: Fault.java,v 1.3 2007-07-13 23:36:04 ofung Exp $
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

package com.sun.xml.rpc.processor.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.generator.GeneratorUtil;
import com.sun.xml.rpc.processor.model.java.JavaException;

/**
 *
 * @author JAX-RPC Development Team
 */
public class Fault extends ModelObject {
    
    public Fault() {}
    
    public Fault(String name) {
        this.name = name;
        parentFault = null;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String s) {
        name = s;
    }
    
    public Block getBlock() {
        return block;
    }
    
    public void setBlock(Block b) {
        block = b;
    }
    
    public JavaException getJavaException() {
        return javaException;
    }
    
    public void setJavaException(JavaException e) {
        javaException = e;
    }
    
    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public Fault getParentFault() {
        return parentFault;
    }
    
    public void setParentFault(Fault parentFault) {
        if (this.parentFault != null &&
            parentFault != null &&
            !this.parentFault.equals(parentFault)) {
                
            throw new ModelException("model.parent.fault.already.set",
                new Object[] {
                    getName().toString(),
                    this.parentFault.getName().toString(),
                    parentFault.getName().toString()
            });
        }
        this.parentFault = parentFault;
    }
    
    public void addSubfault(Fault fault) {
        subfaults.add(fault);
        fault.setParentFault(this);
    }
    
    public Iterator getSubfaults() {
        if (subfaults.size() == 0) {
            return null;
        }
        return subfaults.iterator();
    }
    
    public Iterator getSortedSubfaults() {
        Set sortedFaults = new TreeSet(new GeneratorUtil.FaultComparator());
        sortedFaults.addAll(subfaults);
        return sortedFaults.iterator();
    }    
    
    /* serialization */
    public Set getSubfaultsSet() {
        return subfaults;
    }
    
    /* serialization */
    public void setSubfaultsSet(Set s) {
        subfaults = s;
    }
    
    public Iterator getAllFaults() {
        Set allFaults = getAllFaultsSet();
        if (allFaults.size() == 0) {
            return null;
        }
        return allFaults.iterator();
    }
    
    public Set getAllFaultsSet() {
        Set transSet = new HashSet();
        Iterator iter = subfaults.iterator();
        while (iter.hasNext()) {
            transSet.addAll(((Fault)iter.next()).getAllFaultsSet());
        }
        transSet.addAll(subfaults);
        return transSet;
    }
    
    public QName getElementName() {
        return elementName;
    }
    
    public void setElementName(QName elementName) {
        this.elementName = elementName;
    }

    public String getJavaMemberName() {
        return javaMemberName;
    }
    
    public void setJavaMemberName(String javaMemberName) {
        this.javaMemberName = javaMemberName;
    }

    
    private String name;
    private Block block;
    private JavaException javaException;
    private Fault parentFault;
    private Set subfaults = new HashSet();
    private QName elementName = null;
    private String javaMemberName = null;
}
