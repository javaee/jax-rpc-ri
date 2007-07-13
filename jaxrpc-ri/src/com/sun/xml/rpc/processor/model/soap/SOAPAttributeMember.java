/*
 * $Id: SOAPAttributeMember.java,v 1.3 2007-07-13 23:36:06 ofung Exp $
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
package com.sun.xml.rpc.processor.model.soap;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.model.java.JavaStructureMember;

/**
 * @author JAX-RPC Development Team
 *
 */
public class SOAPAttributeMember {

    public SOAPAttributeMember() {}
    
    public SOAPAttributeMember(QName name, SOAPType type) {
        this(name, type, null);
    }
    
    public SOAPAttributeMember(QName name, SOAPType type,
        JavaStructureMember javaStructureMember) {
            
        _name = name;
        _type = type;
        _javaStructureMember = javaStructureMember;
    }
    
    public QName getName() {
        return _name;
    }
    
    public void setName(QName n) {
        _name = n;
    }
    
    public SOAPType getType() {
        return _type;
    }
    
    public void setType(SOAPType t) {
        _type = t;
    }
    
    public JavaStructureMember getJavaStructureMember() {
        return _javaStructureMember;
    }
    
    public void setJavaStructureMember(
        JavaStructureMember javaStructureMember) {
            
        _javaStructureMember = javaStructureMember;
    }
    
    public boolean isRequired() {
        return _required;
    }
    
    public void setRequired(boolean b) {
        _required = b;
    }
    
    public boolean isInherited() {
        return isInherited;
    }
    
    public void setInherited(boolean b) {
        isInherited = b;
    }
    
    private QName _name;
    private SOAPType _type;
    private JavaStructureMember _javaStructureMember;
    private boolean _required;
    private boolean isInherited = false;
}

