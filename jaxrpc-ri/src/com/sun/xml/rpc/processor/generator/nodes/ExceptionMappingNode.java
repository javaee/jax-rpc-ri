/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.xml.rpc.processor.generator.nodes;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.HeaderFault;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralStructuredType;
import com.sun.xml.rpc.processor.model.literal.LiteralWildcardMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureType;

/**
 * @author  Qingqing Ouyang
 * @version 
 */
public class ExceptionMappingNode extends JaxRpcMappingNode {

    /**
     * Default constructor.
     */
    public ExceptionMappingNode() {
    }

    /**
     * write the appropriate information to a DOM tree and return it
     *
     * @param parent node in the DOM tree 
     * @param nodeName name for the root element for this DOM tree fragment
     * @param fault 
     * @return the DOM tree top node
     */
    public Node write(Node parent, String nodeName, Fault fault)
        throws Exception {

        Element node = appendChild(parent, nodeName);

        Block block = fault.getBlock();
        JavaException javaEx = fault.getJavaException();
        String javaExName = javaEx.getName();
        String wsdlMsgStr;
        QName wsdlMsg = null;
        if (fault instanceof HeaderFault) {
            wsdlMsg = ((HeaderFault) fault).getMessage();
            wsdlMsgStr = wsdlMsg.getLocalPart();
        } else {
            wsdlMsg = block.getName();
            wsdlMsgStr = fault.getName();
        }

        //exception-type
        appendTextChild(node, JaxRpcMappingTagNames.EXCEPTION_TYPE, javaExName);

        //wsdl-message
        //XXX FIXME  Need to handle QName better
        Element wsdlMessage =
            (Element) appendTextChild(node,
                JaxRpcMappingTagNames.WSDL_MESSAGE,
                "exMsgNS:" + wsdlMsgStr);
        wsdlMessage.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns:exMsgNS",
            wsdlMsg.getNamespaceURI());

        //wsdl-message-part-name?
        if (fault instanceof HeaderFault) {
            appendTextChild(
                node,
                JaxRpcMappingTagNames.WSDL_MESSAGE_PART_NAME,
                ((HeaderFault) fault).getPart());
        }

        AbstractType faultType = block.getType();

        debug(MYNAME, "000 exName = " + javaExName);
        debug(MYNAME, "111 faultType = " + faultType.getClass().getName());
        debug(MYNAME, "333 fault.block = " + fault.getBlock().getName());
        debug(MYNAME, "555 fault = " + fault.getClass().getName());

        if (faultType instanceof SOAPStructureType) {
            debug(MYNAME, "222. found soapstructuretype");
            /* exception is mapped from a complex type.
               Output constructor parameter order */

            JavaStructureType t = (JavaStructureType) fault.getJavaException();
            if (t.getMembersCount() > 0) {

                //constructor-parameter-order?
                Node constParamOrderNode =
                    appendChild(
                        node,
                        JaxRpcMappingTagNames.CONSTRUCTOR_PARAMETER_ORDER);

                for (Iterator i = t.getMembers(); i.hasNext();) {

                    JavaStructureMember o = (JavaStructureMember) i.next();
                    SOAPStructureMember owner =
                        (SOAPStructureMember) o.getOwner();
                    String elem = owner.getName().getLocalPart();

                    //element-name+
                    appendTextChild(
                        constParamOrderNode,
                        JaxRpcMappingTagNames.ELEMENT_NAME,
                        elem);
                }
            }
        } else if (faultType instanceof LiteralStructuredType) {
            debug(MYNAME, "333. found literalstructuredtype");

            JavaStructureType t = (JavaStructureType) fault.getJavaException();
            if (t.getMembersCount() > 0) {

                //constructor-parameter-order?
                Node constParamOrderNode =
                    appendChild(
                        node,
                        JaxRpcMappingTagNames.CONSTRUCTOR_PARAMETER_ORDER);

                for (Iterator i = t.getMembers(); i.hasNext();) {

                    Object o = ((JavaStructureMember) i.next()).getOwner();
                    debug(MYNAME, "666 owner type = " + o.getClass().getName());

                    String elemName = null;
                    if (o instanceof LiteralWildcardMember) {
                        LiteralWildcardMember owner = (LiteralWildcardMember) o;
                        elemName = owner.getJavaStructureMember().getName();
                    } else if (o instanceof LiteralElementMember) {
                        LiteralElementMember owner = (LiteralElementMember) o;
                        elemName = owner.getName().getLocalPart();
                    } else if (o instanceof LiteralAttributeMember) {
                        LiteralAttributeMember owner =
                            (LiteralAttributeMember) o;
                        elemName = owner.getName().getLocalPart();
                    }

                    if (elemName != null) {
                        //element-name+
                        appendTextChild(
                            constParamOrderNode,
                            JaxRpcMappingTagNames.ELEMENT_NAME,
                            elemName);
                    }
                }
            }
        }

        return node;
    }

    private static final String MYNAME = "ExceptionMappingNode";
}
