/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2018 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.xml.rpc.processor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.Service;

import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.literal.LiteralTypeVisitor;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.model.soap.SOAPTypeVisitor;


/**
 * This class writes out a Model. It is intended for debugging purposes only.
 *
 * @author JAX-RPC Development Team
 */
public class CanonicalModelWriter extends ModelWriter
    implements ProcessorAction, SOAPTypeVisitor, LiteralTypeVisitor {
    
    public CanonicalModelWriter(IndentingWriter w) {
        super(w);
    }
    
    public CanonicalModelWriter(OutputStream out) {
        super(out);
    }
    
    public CanonicalModelWriter(File f) throws FileNotFoundException {
        this(new FileOutputStream(f));
    }
    
    public void visit(Model model) throws Exception {
        preVisit(model);
        Set sortedServices = new TreeSet(new GetNameComparator(Service.class));
        for (Iterator iter = model.getServices(); iter.hasNext();) {
            Service service = (Service) iter.next();
            sortedServices.add(service);
        }
        for (Iterator iter = sortedServices.iterator(); iter.hasNext();) {
            Service service = (Service) iter.next();
            preVisit(service);
            Set sortedPorts = new TreeSet(new GetNameComparator(Port.class));
            for (Iterator iter2 = service.getPorts(); iter2.hasNext();) {
                Port port = (Port) iter2.next();
                sortedPorts.add(port);
            }
            for (Iterator iter2 = sortedPorts.iterator(); iter2.hasNext();) {
                Port port = (Port) iter2.next();
                preVisit(port);
                Set sortedOperations =
                    new TreeSet(new GetNameComparator(Operation.class));
                for (Iterator iter3 = port.getOperations(); iter3.hasNext();) {
                    Operation operation = (Operation) iter3.next();
                    sortedOperations.add(operation);
                }
                for (Iterator iter3 =
                    sortedOperations.iterator(); iter3.hasNext();) {
                        
                    Operation operation = (Operation) iter3.next();
                    preVisit(operation);
                    Request request = operation.getRequest();
                    if (request != null) {
                        preVisit(request);
                        Set sortedHeaderBlocks =
                            new TreeSet(new GetNameComparator(Block.class));
                        for (Iterator iter4 = request.getHeaderBlocks();
                            iter4.hasNext();) {
                                
                            Block block = (Block) iter4.next();
                            sortedHeaderBlocks.add(block);
                        }
                        for (Iterator iter4 = sortedHeaderBlocks.iterator();
                            iter4.hasNext();) {
                                
                            Block block = (Block) iter4.next();
                            visitHeaderBlock(block);
                        }
                        Set sortedBodyBlocks =
                            new TreeSet(new GetNameComparator(Block.class));
                        for (Iterator iter4 = request.getBodyBlocks();
                            iter4.hasNext();) {
                                
                            Block block = (Block) iter4.next();
                            sortedBodyBlocks.add(block);
                        }
                        for (Iterator iter4 = sortedBodyBlocks.iterator();
                            iter4.hasNext();) {
                                
                            Block block = (Block) iter4.next();
                            visitBodyBlock(block);
                        }
                        Set sortedParams =
                            new TreeSet(new GetNameComparator(Parameter.class));
                        for (Iterator iter4 = request.getParameters();
                            iter4.hasNext();) {
                                
                            Parameter parameter = (Parameter) iter4.next();
                            sortedParams.add(parameter);
                        }
                        for (Iterator iter4 = sortedParams.iterator();
                            iter4.hasNext();) {
                                
                            Parameter parameter = (Parameter) iter4.next();
                            visit(parameter);
                        }
                        postVisit(request);
                    }
                    
                    Response response = operation.getResponse();
                    if (request != null) {
                        preVisit(response);
                        Set sortedHeaderBlocks =
                            new TreeSet(new GetNameComparator(Block.class));
                        for (Iterator iter4 = response.getHeaderBlocks();
                            iter4.hasNext();) {
                                
                            Block block = (Block) iter4.next();
                            sortedHeaderBlocks.add(block);
                        }
                        for (Iterator iter4 = sortedHeaderBlocks.iterator();
                            iter4.hasNext();) {
                                
                            Block block = (Block) iter4.next();
                            visitHeaderBlock(block);
                        }
                        Set sortedBodyBlocks =
                            new TreeSet(new GetNameComparator(Block.class));
                        for (Iterator iter4 = response.getBodyBlocks();
                            iter4.hasNext();) {
                                
                            Block block = (Block) iter4.next();
                            sortedBodyBlocks.add(block);
                        }
                        for (Iterator iter4 = sortedBodyBlocks.iterator();
                            iter4.hasNext();) {
                                
                            Block block = (Block) iter4.next();
                            visitBodyBlock(block);
                        }
                        Set sortedParams =
                            new TreeSet(new GetNameComparator(Parameter.class));
                        for (Iterator iter4 = response.getParameters();
                            iter4.hasNext();) {
                                
                            Parameter parameter = (Parameter) iter4.next();
                            sortedParams.add(parameter);
                        }
                        for (Iterator iter4 = sortedParams.iterator();
                            iter4.hasNext();) {
                                
                            Parameter parameter = (Parameter) iter4.next();
                            visit(parameter);
                        }
                        postVisit(response);
                    }
                    
                    Set sortedFaults =
                        new TreeSet(new GetNameComparator(Operation.class));
                    for (Iterator iter4 = operation.getFaults();
                        iter4.hasNext();) {
                            
                        Fault fault = (Fault) iter4.next();
                        sortedFaults.add(fault);
                    }
                    for (Iterator iter4 = sortedFaults.iterator();
                        iter4.hasNext();) {
                            
                        Fault fault = (Fault) iter4.next();
                        preVisit(fault);
                        visitFaultBlock(fault.getBlock());
                        postVisit(fault);
                    }
                    postVisit(operation);
                }
                postVisit(port);
            }
            postVisit(service);
        }
        postVisit(model);
    }
    
    
    protected void processTypes(Model model) throws Exception {
        Set sortedTypes =
            new TreeSet(new GetNameComparator(AbstractType.class));
        for (Iterator iter = model.getExtraTypes(); iter.hasNext();) {
            AbstractType extraType = (AbstractType) iter.next();
            sortedTypes.add(extraType);
        }
        for (Iterator iter = sortedTypes.iterator(); iter.hasNext();) {
            AbstractType extraType = (AbstractType) iter.next();
            if (extraType.isLiteralType()) {
                describe((LiteralType) extraType);
            } else if (extraType.isSOAPType()) {
                describe((SOAPType) extraType);
            }
        }
    }
    
    protected void processAttributeMembers(LiteralStructuredType type)
        throws Exception {
            
        Set sortedAttMem =
            new TreeSet(new GetNameComparator(LiteralAttributeMember.class));
        for (Iterator iter = type.getAttributeMembers(); iter.hasNext();) {
            LiteralAttributeMember attribute =
                (LiteralAttributeMember) iter.next();
            sortedAttMem.add(attribute);
        }
        for (Iterator iter = sortedAttMem.iterator(); iter.hasNext();) {
            LiteralAttributeMember attribute =
                (LiteralAttributeMember) iter.next();
            writeAttributeMember(attribute);
        }
    }
    
    protected void processElementMembers(LiteralStructuredType type)
        throws Exception {
            
        Set sortedElemMem =
            new TreeSet(new GetNameComparator(LiteralElementMember.class));
        for (Iterator iter = type.getElementMembers(); iter.hasNext();) {
            LiteralElementMember element = (LiteralElementMember) iter.next();
            sortedElemMem.add(element);
        }
        for (Iterator iter = sortedElemMem.iterator(); iter.hasNext();) {
            LiteralElementMember element = (LiteralElementMember) iter.next();
            writeElementMember(element);
        }
    }
    
    protected void processMembers(SOAPStructureType type) throws Exception {
        Set sortedMembers =
            new TreeSet(new GetNameComparator(SOAPStructureMember.class));
        for (Iterator iter = type.getMembers(); iter.hasNext();) {
            SOAPStructureMember member = (SOAPStructureMember) iter.next();
            sortedMembers.add(member);
        }
        for (Iterator iter = sortedMembers.iterator(); iter.hasNext();) {
            SOAPStructureMember member = (SOAPStructureMember) iter.next();
            writeMember(member);
        }
    }
    
    public static int compareNames(Object o1, Object o2) {
        if (o1 instanceof QName) {
            return ((QName)o1).toString().compareTo(((QName)o2).toString());
        }
        return ((String)o1).compareTo((String)o2);
    }
    
    public static class GetNameComparator implements Comparator {
        private Method getNameMethod  = null;
        
        public GetNameComparator(Class objClass) {
            try {
                Class[] argsClass = new Class[] {};
                getNameMethod = objClass.getMethod("getName", argsClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public int compare(Object o1, Object o2) {
            try {
                Object[] args = new Object[] {};
                return compareNames(getNameMethod.invoke(o1, args),
                    getNameMethod.invoke(o2, args));
            } catch (Exception e) {
                e.printStackTrace();;
            }
            return 0;
        }
    }
}
