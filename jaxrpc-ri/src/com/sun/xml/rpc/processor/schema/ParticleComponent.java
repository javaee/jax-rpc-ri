/*
 * $Id: ParticleComponent.java,v 1.3 2007-07-13 23:36:19 ofung Exp $
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

package com.sun.xml.rpc.processor.schema;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ParticleComponent extends Component {
    
    public static final int TERM_MODEL_GROUP = 1;
    public static final int TERM_WILDCARD = 2;
    public static final int TERM_ELEMENT = 3;
    
    public ParticleComponent() {}
    
    public int getMinOccurs() {
        return _minOccurs;
    }
    
    public void setMinOccurs(int i) {
        _minOccurs = i;
    }
    
    public int getMaxOccurs() {
        if (_maxOccurs == UNBOUNDED) {
            throw new IllegalStateException();
        }
        return _maxOccurs;
    }
    
    public void setMaxOccurs(int i) {
        _maxOccurs = i;
    }
    
    public boolean isMaxOccursUnbounded() {
        return _maxOccurs == UNBOUNDED;
    }
    
    public void setMaxOccursUnbounded() {
        _maxOccurs = UNBOUNDED;
    }
    
    public boolean doesNotOccur() {
        return _minOccurs == 0 && _maxOccurs == 0;
    }
    
    public boolean occursOnce() {
        return _minOccurs == 1 && _maxOccurs == 1;
    }
    
    public boolean occursAtMostOnce() {
        return _minOccurs <= 1 && _maxOccurs == 1;
    }
    
    public boolean occursAtLeastOnce() {
        return _minOccurs >= 1;
    }
    
    public boolean occursZeroOrMore() {
        return _minOccurs == 0 && _maxOccurs == UNBOUNDED;
    }
    
    public boolean occursOnceOrMore() {
        return _minOccurs == 1 && _maxOccurs == UNBOUNDED;
    }
    
    public boolean mayOccurMoreThanOnce() {
        return _maxOccurs > 1 || _maxOccurs == UNBOUNDED;
    }
    
    public int getTermTag() {
        return _termTag;
    }
    
    public void setTermTag(int i) {
        _termTag = i;
    }
    
    public ModelGroupComponent getModelGroupTerm() {
        return _modelGroupTerm;
    }
    
    public void setModelGroupTerm(ModelGroupComponent c) {
        _modelGroupTerm = c;
    }
    
    public ElementDeclarationComponent getElementTerm() {
        return _elementTerm;
    }
    
    public void setElementTerm(ElementDeclarationComponent c) {
        _elementTerm = c;
    }
    
    public WildcardComponent getWildcardTerm() {
        return _wildcardTerm;
    }
    
    public void setWildcardTerm(WildcardComponent c) {
        _wildcardTerm = c;
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    public boolean occursZeroOrOne() {
        return _minOccurs == 0 && _maxOccurs == 1;
    }
    
    private int _minOccurs;
    private int _maxOccurs;
    private int _termTag;
    private ModelGroupComponent _modelGroupTerm;
    private WildcardComponent _wildcardTerm;
    private ElementDeclarationComponent _elementTerm;
    
    private static final int UNBOUNDED = -1;
}
