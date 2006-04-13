/*
 * $Id: ModelGroupComponent.java,v 1.2 2006-04-13 01:31:46 ofung Exp $
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

package com.sun.xml.rpc.processor.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author JAX-RPC Development Team
 */
public class ModelGroupComponent extends Component {
    
    public ModelGroupComponent() {
        _particles = new ArrayList();
    }
    
    public Symbol getCompositor() {
        return _compositor;
    }
    
    public void setCompositor(Symbol s) {
        _compositor = s;
    }
    
    public Iterator particles() {
        return _particles.iterator();
    }
    
    public void addParticle(ParticleComponent c) {
        _particles.add(c);
    }
    
    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
    
    private Symbol _compositor;
    private List _particles;
    private AnnotationComponent _annotation;
}
