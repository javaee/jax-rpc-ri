/*
 * $Id: ModelGroupComponent.java,v 1.1 2006-04-12 20:35:09 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
