/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.rpc.encoding;

/**
 *
 * @author JAX-RPC Development Team
 */
public interface SOAPInstanceBuilder {
    public static final int GATES_CONSTRUCTION = 1;
    public static final int GATES_INITIALIZATION = 2;

    public static final int REQUIRES_CREATION = 4;
    public static final int REQUIRES_INITIALIZATION = 8;
    public static final int REQUIRES_COMPLETION = 16;

    /**
     *  Return the activity that is gated by a member along with the state
     *  that member has to be in in order to allow the activity to proceed.
     *  A member either <code>GATES_CONSTRUCTION</code> or it
     *  <code>GATES_INITIALIZATION</code>. A member that needs to be
     *  created in order to allow its parent object to be created or
     *  initialized <code>REQUIRES_CREATION</code> if it needs to be
     *  fully initialized in order to be used in the activity that it
     *  gates then it <code>REQUIRES_INITIALIZATION</code>. If it needs
     *  all of its own members to be initialized all the way down
     *  to the leaf members (members without sub-members), then it
     *  <code>REQUIRES_COMPLETION</code>. Note that in an object graph
     *  that contains loops none of the nodes on a looping branch will
     *  ever be considered to be in the "complete" state.
     *  <p>
     *  Here is a sample implementation:
     *  <code>
     *  public int memberGateType(int memberIndex) {
     *      switch (memberIndex) {
     *          case CONSTRUCTOR_PARAM1:
     *              return GATES_CONSTRUCTION + REQUIRES_INITIALIZATION;
     *          case CONSTRUCTOR_PARAM2:
     *              return GATES_CONSTRUCTION + REQUIRES_CREATION;
     *          case CONSTRUCTOR_PARAM3:
     *              return GATES_CONSTRUCTION + REQUIRES_COMPLETION;
     *          case USED_IN_SETTER1:
     *              return GATES_INITIALIZATION + REQUIRES_COMPLETION;
     *          case USED_IN_SETTER2:
     *              return GATES_INITIALIZATION + REQUIRES_INITIALIZATION;
     *          case USED_IN_SETTER3:
     *              return GATES_INITIALIZATION + REQUIRES_CREATION;
     *          default :
     *              return GATES_INITIALIZATION + REQUIRES_CREATION;
     *      }
     *}
     *  </code>
     */
    public int memberGateType(int memberIndex);

    /**
     *  Create a new instance optionally using cached values as constructor 
     *  parameters:
     *  <p>
     *  <code>
     *  public void construct() {
     *      instance = new myType();
     *
     *      OR
     *
     *      instance = new myType(args.param1, args.param2, args.param3, 
     *                      args.param4 ...);
     *  }
     *  </code>
     */
    public void construct();

    /**
     *  Initialize the value of a member of the object or cache the value for
     *  later use during construction:
     *  <p>
     *  <code>
     *  public void setMember(int index, Object memberValue) {
     *      switch (index) {
     *          case INT_MEMBER1:
     *              // Set a public data member
     *              instance.intMember1 = ((Integer) memberValue).intValue();
     *
     *              // Or use a setter
     *              instance.setIntMember1(((Integer) memberValue).intValue());
     *
     *              // Or if there is no default constructor you save them up 
     *              // for construction or initialization
     *              this.intMember1 = ((Integer) memberValue).intValue();
     *          break;
     *          case OBJECT_MEMBER1:
     *              instance.setObjectMember1((myObjectType) memberValue);
     *          break;
     *      }
     *  }
     *  </code
     */
    public void setMember(int index, Object memberValue);

    /**
     *  If there is no default constructor then the constructor arguments will
     *  have to be cached until they all arrive. In that case all of the other
     *  members of the object will have to be cached as well since they may 
     *  arrive before the object is constructed. This method is called after 
     *  the object is constructed and values for all members have arrived. If 
     *  you have cached values in <code>setmember</code> now is the time to 
     *  use them to initialize members.
     *  <p>
     *  <code>
     *  public void initialize() {
     *
     *      instance.setIntMember1(members.intMember1);
     *      instance.setObjectMember1(members.objectMember1);
     *      etc...
     *
     *  }
     *  </code
     */
    public void initialize();

    /**
     *  Use this method to cache an instance of the object.
     */
    public void setInstance(Object instance);

    /**
     *  Return the instance that is being built.
     */
    public Object getInstance();
}
