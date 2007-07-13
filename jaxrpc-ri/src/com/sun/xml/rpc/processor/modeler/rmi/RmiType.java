/*
 * $Id: RmiType.java,v 1.3 2007-07-13 23:36:17 ofung Exp $
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

package com.sun.xml.rpc.processor.modeler.rmi;

import java.util.Hashtable;

public class RmiType implements RmiConstants {
    
    /**
     * This hashtable is used to cache types
     */
    private static final Hashtable typeHash = new Hashtable(231);

    private String typeSig;
    private int typeCode = 0;
    /*
     * Commenting it out the caching of classobj objects in getTypeClass()
     * so that Class objs get GCed and env's URLClassLoader gets GCed. That
     * releases any opened jar files and that is a problem on windows while
     * redployment.
     *
     * private Class classObj = null;
     */
    
    /*
     * Predefined types.
     */
    public static final RmiType tVoid       = new RmiType(TC_VOID, 	SIG_VOID);
    public static final RmiType tBoolean    = new RmiType(TC_BOOLEAN, 	SIG_BOOLEAN);
    public static final RmiType tByte       = new RmiType(TC_BYTE, 	SIG_BYTE);
    public static final RmiType tChar       = new RmiType(TC_CHAR, 	SIG_CHAR);
    public static final RmiType tShort      = new RmiType(TC_SHORT, 	SIG_SHORT);
    public static final RmiType tInt        = new RmiType(TC_INT, 	SIG_INT);
    public static final RmiType tFloat      = new RmiType(TC_FLOAT, 	SIG_FLOAT);
    public static final RmiType tLong       = new RmiType(TC_LONG, 	SIG_LONG);
    public static final RmiType tDouble     = new RmiType(TC_DOUBLE, 	SIG_DOUBLE);
    public static final RmiType tObject     = RmiType.classType(OBJECT_CLASSNAME);
    public static final RmiType tClassDesc  = RmiType.classType(CLASS_CLASSNAME);
    public static final RmiType tString     = RmiType.classType(STRING_CLASSNAME);

    protected RmiType() {
    }
    
    protected RmiType(int typeCode, String typeSig) {
        this.typeCode = typeCode;
        this.typeSig = typeSig;
        typeHash.put(typeSig, this);
    }

    /**
     * Return the Java type signature.
     */
    public final String getTypeSignature() {
        return typeSig;
    }

    
    public int getTypeCode() {
        return typeCode;
    }
    
    public RmiType getElementType() {
        throw new UnsupportedOperationException();
    }
    
    public String getClassName() {
        throw new UnsupportedOperationException();
    }
    
    public int getArrayDimension() {
        return 0;
    }
    
    public Class getTypeClass(ClassLoader loader) throws ClassNotFoundException {
	    if (typeSig.length() == 1) {
		    return RmiUtils.getClassForName(typeString(false), loader);
	    } else {
		    String sig = getTypeSigClassName(typeSig);
		    return Class.forName(sig, true, loader);
	    }
    }
    
    private static String getTypeSigClassName(String typeSig) {
        String sig = typeSig;
        if (sig.charAt(0) == SIGC_CLASS) {
            sig = sig.substring(1, sig.length()-1).replace(SIGC_PACKAGE, '.');
        } 
        return sig;
    }
    
    public static RmiType classType(String className) {
        String sig =
            new String(SIG_CLASS + className + SIG_ENDCLASS);
        RmiType t = (RmiType)typeHash.get(sig);
        if (t == null) {
            t = new ClassType(sig, className);
        }
        
        return t;
    }
    
    public static RmiType arrayType(RmiType elem) {
        String sig = new String(SIG_ARRAY + elem.getTypeSignature());
        RmiType t = (RmiType)typeHash.get(sig);
        if (t == null) {
            t = new ArrayType(sig, elem);
        }
        return t;
    }
    
    
    public static RmiType getRmiType(Class classObj) {
        String sig;
        if (classObj.isArray()) {
            sig = classObj.getName();
        } else {
            sig = RmiUtils.getTypeSig(classObj.getName());            
        }
        return getRmiType(sig);
    }
    
    public static RmiType getRmiType(String sig) {
        RmiType type = (RmiType)typeHash.get(sig);
        if (type != null) {
            return type;
        }
        switch (sig.charAt(0)) {
          case SIGC_ARRAY:
            return arrayType(getRmiType(sig.substring(1)));
        
          case SIGC_CLASS:
            return classType(sig.substring(1, sig.length() - 1).replace(SIGC_PACKAGE, '.'));
        }        
        return type;
    }
    
    public String typeString(boolean abbrev) {
        switch (typeCode) {
            case TC_VOID:   return VOID_CLASSNAME;    
            case TC_BOOLEAN:return BOOLEAN_CLASSNAME; 
            case TC_BYTE:   return BYTE_CLASSNAME;		
            case TC_CHAR:   return CHAR_CLASSNAME;	
            case TC_SHORT:  return SHORT_CLASSNAME;
            case TC_INT:    return INT_CLASSNAME;
            case TC_LONG:   return LONG_CLASSNAME;
            case TC_FLOAT:  return FLOAT_CLASSNAME;
            case TC_DOUBLE: return DOUBLE_CLASSNAME;
            default:        return "unknown";
        }
    }
    
    public boolean isNillable() {
        return false;
    }

    public String toString() {
        return typeString(false);
    }    
}


