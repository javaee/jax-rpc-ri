/*
 * $Id: ModelExporter.java,v 1.3 2007-07-13 23:36:05 ofung Exp $
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

package com.sun.xml.rpc.processor.model.exporter;

import java.io.OutputStream;

import javax.xml.namespace.QName;

import com.sun.xml.rpc.processor.config.HandlerChainInfo;
import com.sun.xml.rpc.processor.config.HandlerInfo;
import com.sun.xml.rpc.processor.config.ImportedDocumentInfo;
import com.sun.xml.rpc.processor.config.TypeMappingInfo;

import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.HeaderFault;
import com.sun.xml.rpc.processor.model.Message;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.ModelObject;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Request;
import com.sun.xml.rpc.processor.model.Response;
import com.sun.xml.rpc.processor.model.Service;

import com.sun.xml.rpc.processor.model.java.JavaArrayType;
import com.sun.xml.rpc.processor.model.java.JavaCustomType;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationEntry;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationType;
import com.sun.xml.rpc.processor.model.java.JavaException;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaSimpleType;
import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import com.sun.xml.rpc.processor.model.java.JavaType;

import com.sun.xml.rpc.processor.model.literal.LiteralAllType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayType;
import com.sun.xml.rpc.processor.model.literal.LiteralArrayWrapperType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttachmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralAttributeMember;
import com.sun.xml.rpc.processor.model.literal.LiteralContentMember;
import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralEnumerationType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralIDType;
import com.sun.xml.rpc.processor.model.literal.LiteralListType;
import com.sun.xml.rpc.processor.model.literal.LiteralSequenceType;
import com.sun.xml.rpc.processor.model.literal.LiteralSimpleType;
import com.sun.xml.rpc.processor.model.literal.LiteralWildcardMember;

import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPAttributeMember;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPEnumerationType;
import com.sun.xml.rpc.processor.model.soap.SOAPListType;
import com.sun.xml.rpc.processor.model.soap.SOAPOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.model.soap.SOAPStructureMember;
import com.sun.xml.rpc.processor.model.soap.SOAPUnorderedStructureType;

import com.sun.xml.rpc.util.Version;

/**
 * @author JAX-RPC Development Team
 */
public class ModelExporter extends ExporterBase implements Constants {
    
    public ModelExporter(OutputStream s) {
        super(s);
    }
    
    public void doExport(Model m) {
        internalDoExport(m);
    }
    
    protected QName getContainerName() {
        return QNAME_MODEL;
    }
    
    /*
     * Version is the same as jaxrpc major.minor version
     */
    protected String getVersion() {
        return Version.VERSION_NUMBER;
    }
    
    protected void failUnsupportedClass(Class klass) {
        throw new ModelException("model.exporter.unsupportedClass",
            klass.getName());
    }

    /* BEGIN GENERATED CODE */
    
    protected void visit(Object obj) {
        if (obj == null)
            return;
        if (obj instanceof LiteralWildcardMember) {
            visitLiteralWildcardMember((LiteralWildcardMember) obj);
        }
        else if (obj instanceof ImportedDocumentInfo) {
            visitImportedDocumentInfo((ImportedDocumentInfo) obj);
        }
        else if (obj instanceof JavaInterface) {
            visitJavaInterface((JavaInterface) obj);
        }
        else if (obj instanceof JavaCustomType) {
            visitJavaCustomType((JavaCustomType) obj);
        }
        else if (obj instanceof Operation) {
            visitOperation((Operation) obj);
        }
        else if (obj instanceof HandlerChainInfo) {
            visitHandlerChainInfo((HandlerChainInfo) obj);
        }
        else if (obj instanceof JavaException) {
            visitJavaException((JavaException) obj);
        }
        else if (obj instanceof JavaStructureType) {
            visitJavaStructureType((JavaStructureType) obj);
        }
        else if (obj instanceof JavaSimpleType) {
            visitJavaSimpleType((JavaSimpleType) obj);
        }
        else if (obj instanceof JavaStructureMember) {
            visitJavaStructureMember((JavaStructureMember) obj);
        }
        else if (obj instanceof Block) {
            visitBlock((Block) obj);
        }
        else if (obj instanceof LiteralElementMember) {
            visitLiteralElementMember((LiteralElementMember) obj);
        }
        else if (obj instanceof LiteralArrayWrapperType) {
            visitLiteralArrayWrapperType((LiteralArrayWrapperType) obj);
        }
        else if (obj instanceof LiteralSequenceType) {
            visitLiteralSequenceType((LiteralSequenceType) obj);
        }
        else if (obj instanceof RPCRequestUnorderedStructureType) {
            visitRPCRequestUnorderedStructureType((RPCRequestUnorderedStructureType) obj);
        }
        else if (obj instanceof JavaEnumerationEntry) {
            visitJavaEnumerationEntry((JavaEnumerationEntry) obj);
        }
        else if (obj instanceof Response) {
            visitResponse((Response) obj);
        }
        else if (obj instanceof RPCRequestOrderedStructureType) {
            visitRPCRequestOrderedStructureType((RPCRequestOrderedStructureType) obj);
        }
        else if (obj instanceof LiteralEnumerationType) {
            visitLiteralEnumerationType((LiteralEnumerationType) obj);
        }
        else if (obj instanceof Request) {
            visitRequest((Request) obj);
        }
        else if (obj instanceof LiteralAllType) {
            visitLiteralAllType((LiteralAllType) obj);
        }
        else if (obj instanceof JavaArrayType) {
            visitJavaArrayType((JavaArrayType) obj);
        }
        else if (obj instanceof Port) {
            visitPort((Port) obj);
        }
        else if (obj instanceof LiteralAttributeMember) {
            visitLiteralAttributeMember((LiteralAttributeMember) obj);
        }
        else if (obj instanceof HandlerInfo) {
            visitHandlerInfo((HandlerInfo) obj);
        }
        else if (obj instanceof Service) {
            visitService((Service) obj);
        }
        else if (obj instanceof SOAPStructureMember) {
            visitSOAPStructureMember((SOAPStructureMember) obj);
        }
        else if (obj instanceof JavaParameter) {
            visitJavaParameter((JavaParameter) obj);
        }
        else if (obj instanceof Model) {
            visitModel((Model) obj);
        }
        else if (obj instanceof LiteralSimpleType) {
            visitLiteralSimpleType((LiteralSimpleType) obj);
        }	
        else if (obj instanceof LiteralArrayType) {
            visitLiteralArrayType((LiteralArrayType) obj);
        }
        else if (obj instanceof LiteralListType) {
            visitLiteralListType((LiteralListType) obj);
        }
        else if (obj instanceof JavaEnumerationType) {
            visitJavaEnumerationType((JavaEnumerationType) obj);
        }
        else if (obj instanceof SOAPCustomType) {
            visitSOAPCustomType((SOAPCustomType) obj);
        }
        else if (obj instanceof LiteralFragmentType) {
            visitLiteralFragmentType((LiteralFragmentType) obj);
        }
        else if (obj instanceof SOAPArrayType) {
            visitSOAPArrayType((SOAPArrayType) obj);
        }
        else if (obj instanceof SOAPUnorderedStructureType) {
            visitSOAPUnorderedStructureType((SOAPUnorderedStructureType) obj);
        }
        else if (obj instanceof Message) {
            visitMessage((Message) obj);
        }
        else if (obj instanceof HeaderFault) {
            visitHeaderFault((HeaderFault) obj);
        }
        else if (obj instanceof JavaMethod) {
            visitJavaMethod((JavaMethod) obj);
        }
        else if (obj instanceof SOAPAnyType) {
            visitSOAPAnyType((SOAPAnyType) obj);
        }
        else if (obj instanceof SOAPSimpleType) {
            visitSOAPSimpleType((SOAPSimpleType) obj);
        }
        else if (obj instanceof SOAPOrderedStructureType) {
            visitSOAPOrderedStructureType((SOAPOrderedStructureType) obj);
        }else if (obj instanceof SOAPAttributeMember) {
            visitSOAPAttributeMember((SOAPAttributeMember) obj);
        }        
        else if (obj instanceof RPCResponseStructureType) {
            visitRPCResponseStructureType((RPCResponseStructureType) obj);
        }
        else if (obj instanceof Parameter) {
            visitParameter((Parameter) obj);
        }
        else if (obj instanceof TypeMappingInfo) {
            visitTypeMappingInfo((TypeMappingInfo) obj);
        }
        else if (obj instanceof Fault) {
            visitFault((Fault) obj);
        }
        else if (obj instanceof LiteralContentMember) {
            visitLiteralContentMember((LiteralContentMember) obj);
        }
        else if (obj instanceof SOAPEnumerationType) {
            visitSOAPEnumerationType((SOAPEnumerationType) obj);
        }
        else if (obj instanceof JavaType) {
            visitJavaType((JavaType) obj);
        }
        else if (obj instanceof ModelObject) {
            visitModelObject((ModelObject) obj);
        }
        else if (obj instanceof LiteralIDType) {
            visitLiteralIDType((LiteralIDType) obj);
        }else if (obj instanceof SOAPListType) {
            visitSOAPListType((SOAPListType) obj);
        }else if (obj instanceof LiteralAttachmentType) {
            visitLiteralAttachmentType((LiteralAttachmentType) obj);
        }
        else {
            super.visit(obj);
        }
    }
    
    /**
     * @param type
     */
    private void visitLiteralAttachmentType(LiteralAttachmentType target) {
        property("mimeType", target, target.getMIMEType());
        property("alternateMIMETypes", target, target.getAlternateMIMETypes());
        property("contentId", target, target.getContentID());
        property("isSwaRef", target, new Boolean(target.isSwaRef()));
        property("javaType", target, target.getJavaType());
    }

    protected void visitLiteralWildcardMember(LiteralWildcardMember target) {
        property("nillable", target, new Boolean(target.isNillable()));
        property("javaStructureMember", target, target.getJavaStructureMember());
        property("required", target, new Boolean(target.isRequired()));
        property("repeated", target, new Boolean(target.isRepeated()));
        property("type", target, target.getType());
        property("name", target, target.getName());
        property("excludedNamespaceName", target, target.getExcludedNamespaceName());
    }
    
    protected void visitImportedDocumentInfo(ImportedDocumentInfo target) {
        property("namespace", target, target.getNamespace());
        property("type", target, new Integer(target.getType()));
        property("location", target, target.getLocation());
    }
    
    protected void visitJavaInterface(JavaInterface target) {
        property("realName", target, target.getRealName());
        property("formalName", target, target.getFormalName());
        property("methodsList", target, target.getMethodsList());
        property("interfacesList", target, target.getInterfacesList());
        property("impl", target, target.getImpl());
    }
    
    protected void visitJavaCustomType(JavaCustomType target) {
        property("realName", target, target.getRealName());
        property("formalName", target, target.getFormalName());
        property("present", target, new Boolean(target.isPresent()));
        property("holder", target, new Boolean(target.isHolder()));
        property("holderPresent", target, new Boolean(target.isHolderPresent()));
        property("initString", target, target.getInitString());
        property("holderName", target, target.getHolderName());
        property("typeMappingInfo", target, target.getTypeMappingInfo());
    }
    
    protected void visitOperation(Operation target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("uniqueName", target, target.getUniqueName());
        property("request", target, target.getRequest());
        property("response", target, target.getResponse());
        property("faultsSet", target, target.getFaultsSet());
        property("javaMethod", target, target.getJavaMethod());
        property("SOAPAction", target, target.getSOAPAction());
        property("use", target, target.getUse());
        property("style", target, target.getStyle());
        property("name", target, target.getName());
    }
    
    protected void visitHandlerChainInfo(HandlerChainInfo target) {
        property("handlersList", target, target.getHandlersList());
        property("roles", target, target.getRoles());
    }
    
    protected void visitJavaException(JavaException target) {
        property("realName", target, target.getRealName());
        property("formalName", target, target.getFormalName());
        property("present", target, new Boolean(target.isPresent()));
        property("holder", target, new Boolean(target.isHolder()));
        property("holderPresent", target, new Boolean(target.isHolderPresent()));
        property("initString", target, target.getInitString());
        property("holderName", target, target.getHolderName());
        property("membersList", target, target.getMembersList());
        property("subclassesSet", target, target.getSubclassesSet());
        property("abstract", target, new Boolean(target.isAbstract()));
        property("owner", target, target.getOwner());
        property("superclass", target, target.getSuperclass());
    }
    
    protected void visitJavaStructureType(JavaStructureType target) {
        property("realName", target, target.getRealName());
        property("formalName", target, target.getFormalName());
        property("present", target, new Boolean(target.isPresent()));
        property("holder", target, new Boolean(target.isHolder()));
        property("holderPresent", target, new Boolean(target.isHolderPresent()));
        property("initString", target, target.getInitString());
        property("holderName", target, target.getHolderName());
        property("membersList", target, target.getMembersList());
        property("subclassesSet", target, target.getSubclassesSet());
        property("abstract", target, new Boolean(target.isAbstract()));
        property("owner", target, target.getOwner());
        property("superclass", target, target.getSuperclass());
    }
    
    protected void visitJavaSimpleType(JavaSimpleType target) {
        property("realName", target, target.getRealName());
        property("formalName", target, target.getFormalName());
        property("present", target, new Boolean(target.isPresent()));
        property("holder", target, new Boolean(target.isHolder()));
        property("holderPresent", target, new Boolean(target.isHolderPresent()));
        property("initString", target, target.getInitString());
        property("holderName", target, target.getHolderName());
    }
    
    protected void visitJavaStructureMember(JavaStructureMember target) {
        property("readMethod", target, target.getReadMethod());
        property("writeMethod", target, target.getWriteMethod());
        property("inherited", target, new Boolean(target.isInherited()));
        property("constructorPos", target, new Integer(target.getConstructorPos()));
        property("type", target, target.getType());
        property("public", target, new Boolean(target.isPublic()));
        property("declaringClass", target, target.getDeclaringClass());
        property("owner", target, target.getOwner());
        property("name", target, target.getName());
    }
    
    protected void visitBlock(Block target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("type", target, target.getType());
        property("location", target, new Integer(target.getLocation()));
        property("name", target, target.getName());
    }
    
    protected void visitLiteralElementMember(LiteralElementMember target) {
        property("nillable", target, new Boolean(target.isNillable()));
        property("javaStructureMember", target, target.getJavaStructureMember());
        property("required", target, new Boolean(target.isRequired()));
        property("repeated", target, new Boolean(target.isRepeated()));
        property("type", target, target.getType());
        property("name", target, target.getName());
        property("inherited", target, new Boolean(target.isInherited()));
    }
    
    protected void visitLiteralSequenceType(LiteralSequenceType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("schemaTypeRef", target, target.getSchemaTypeRef());
        property("attributeMembersList", target, target.getAttributeMembersList());
        property("elementMembersList", target, target.getElementMembersList());
        property("contentMember", target, target.getContentMember());
        property("subtypesSet", target, target.getSubtypesSet());
        property("parentType", target, target.getParentType());
        property("nillable", target, new Boolean(target.isNillable()));
        property("rpcWrapper", target, new Boolean(target.isRpcWrapper()));
        property("isUnwrapped", target, new Boolean(target.isUnwrapped()));
    }
    
    protected void visitLiteralArrayWrapperType(LiteralArrayWrapperType target) {
        visitLiteralSequenceType(target);
        property("javaArrayType", target, target.getJavaArrayType());
    }

    protected void visitRPCRequestUnorderedStructureType(RPCRequestUnorderedStructureType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("membersList", target, target.getMembersList());
        property("attributeMembersList", target, target.getAttributeMembersList());
        property("subtypesSet", target, target.getSubtypesSet());
        property("parentType", target, target.getParentType());
    }
    
    protected void visitJavaEnumerationEntry(JavaEnumerationEntry target) {
        property("literalValue", target, target.getLiteralValue());
        property("value", target, target.getValue());
        property("name", target, target.getName());
    }
    
    protected void visitResponse(Response target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("bodyBlocksMap", target, target.getBodyBlocksMap());
        property("headerBlocksMap", target, target.getHeaderBlocksMap());
        property("parametersList", target, target.getParametersList());
        property("faultBlocksMap", target, target.getFaultBlocksMap());
        property("attachmentBlocksMap", target, target.getAttachmentBlocksMap());
    }
    
    protected void visitRPCRequestOrderedStructureType(RPCRequestOrderedStructureType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("membersList", target, target.getMembersList());
        property("subtypesSet", target, target.getSubtypesSet());
        property("parentType", target, target.getParentType());
    }
    
    protected void visitLiteralEnumerationType(LiteralEnumerationType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("schemaTypeRef", target, target.getSchemaTypeRef());
        property("baseType", target, target.getBaseType());
        property("nillable", target, new Boolean(target.isNillable()));
    }
    
    protected void visitRequest(Request target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("bodyBlocksMap", target, target.getBodyBlocksMap());
        property("headerBlocksMap", target, target.getHeaderBlocksMap());
        property("parametersList", target, target.getParametersList());
    }
    
    protected void visitLiteralAllType(LiteralAllType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("schemaTypeRef", target, target.getSchemaTypeRef());
        property("attributeMembersList", target, target.getAttributeMembersList());
        property("elementMembersList", target, target.getElementMembersList());
        property("contentMember", target, target.getContentMember());
        property("subtypesSet", target, target.getSubtypesSet());
        property("parentType", target, target.getParentType());
        property("nillable", target, new Boolean(target.isNillable()));
        property("rpcWrapper", target, new Boolean(target.isRpcWrapper()));
     }
    
    protected void visitJavaArrayType(JavaArrayType target) {
        property("realName", target, target.getRealName());
        property("formalName", target, target.getFormalName());
        property("present", target, new Boolean(target.isPresent()));
        property("holder", target, new Boolean(target.isHolder()));
        property("holderPresent", target, new Boolean(target.isHolderPresent()));
        property("initString", target, target.getInitString());
        property("holderName", target, target.getHolderName());
        property("elementName", target, target.getElementName());
        property("elementType", target, target.getElementType());
        //bug fix:4904604
        property("soapArrayHolderName", target, target.getSOAPArrayHolderName());
    }
    
    protected void visitPort(Port target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("operationsList", target, target.getOperationsList());
        property("javaInterface", target, target.getJavaInterface());
        property("clientHandlerChainInfo", target, target.getClientHandlerChainInfo());
        property("serverHandlerChainInfo", target, target.getServerHandlerChainInfo());
        property("SOAPVersion", target, target.getSOAPVersion());
        property("address", target, target.getAddress());
        property("name", target, target.getName());
    }
    
    protected void visitLiteralAttributeMember(LiteralAttributeMember target) {
        property("javaStructureMember", target, target.getJavaStructureMember());
        property("required", target, new Boolean(target.isRequired()));
        property("type", target, target.getType());
        property("name", target, target.getName());
        property("inherited", target, new Boolean(target.isInherited()));
    }
    
    protected void visitHandlerInfo(HandlerInfo target) {
        property("handlerClassName", target, target.getHandlerClassName());
        property("headerNames", target, target.getHeaderNames());
        property("properties", target, target.getProperties());
    }
    
    protected void visitService(Service target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("javaInterface", target, target.getJavaInterface());
        property("portsList", target, target.getPortsList());
        property("name", target, target.getName());
    }
    
    protected void visitSOAPStructureMember(SOAPStructureMember target) {
        property("inherited", target, new Boolean(target.isInherited()));
        property("javaStructureMember", target, target.getJavaStructureMember());
        property("type", target, target.getType());
        property("name", target, target.getName());
    }
    
    protected void visitSOAPAttributeMember(SOAPAttributeMember target) {
        property("javaStructureMember", target, target.getJavaStructureMember());
        property("required", target, new Boolean(target.isRequired()));
        property("type", target, (com.sun.xml.rpc.processor.model.soap.SOAPType)target.getType());
        property("name", target, target.getName());
        property("inherited", target, new Boolean(target.isInherited()));
    }
    
    protected void visitJavaParameter(JavaParameter target) {
        property("holder", target, new Boolean(target.isHolder()));
        property("type", target, target.getType());
        property("parameter", target, target.getParameter());
        property("name", target, target.getName());
        property("holderName", target, target.getHolderName());
    }
    
    protected void visitModel(Model target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("targetNamespaceURI", target, target.getTargetNamespaceURI());
        property("servicesList", target, target.getServicesList());
        property("extraTypesSet", target, target.getExtraTypesSet());
        property("importedDocumentsMap", target, target.getImportedDocumentsMap());
        property("name", target, target.getName());
        property("target", target, target.getSource());
    }
    
    protected void visitLiteralSimpleType(LiteralSimpleType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("schemaTypeRef", target, target.getSchemaTypeRef());
        property("nillable", target, new Boolean(target.isNillable()));
    }
    
    protected void visitLiteralArrayType(LiteralArrayType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("schemaTypeRef", target, target.getSchemaTypeRef());
        property("elementType", target, target.getElementType());
        property("nillable", target, new Boolean(target.isNillable()));
    }

    protected void visitLiteralListType(LiteralListType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("schemaTypeRef", target, target.getSchemaTypeRef());
        property("itemType", target, target.getItemType());
        property("nillable", target, new Boolean(target.isNillable()));
    }
    
    protected void visitJavaEnumerationType(JavaEnumerationType target) {
        property("realName", target, target.getRealName());
        property("formalName", target, target.getFormalName());
        property("present", target, new Boolean(target.isPresent()));
        property("holder", target, new Boolean(target.isHolder()));
        property("holderPresent", target, new Boolean(target.isHolderPresent()));
        property("initString", target, target.getInitString());
        property("holderName", target, target.getHolderName());
        property("baseType", target, target.getBaseType());
        property("entriesList", target, target.getEntriesList());
    }
    
    protected void visitSOAPCustomType(SOAPCustomType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
    }
    
    protected void visitLiteralFragmentType(LiteralFragmentType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("schemaTypeRef", target, target.getSchemaTypeRef());
        property("nillable", target, new Boolean(target.isNillable()));
    }
    
    protected void visitSOAPArrayType(SOAPArrayType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("elementName", target, target.getElementName());
        property("elementType", target, target.getElementType());
        property("rank", target, new Integer(target.getRank()));
        property("size", target, target.getSize());
    }
    
    protected void visitSOAPUnorderedStructureType(SOAPUnorderedStructureType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("membersList", target, target.getMembersList());
        property("subtypesSet", target, target.getSubtypesSet());
        property("parentType", target, target.getParentType());
    }
    
    protected void visitMessage(Message target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("bodyBlocksMap", target, target.getBodyBlocksMap());
        property("headerBlocksMap", target, target.getHeaderBlocksMap());
        property("parametersList", target, target.getParametersList());
    }
    
    protected void visitHeaderFault(HeaderFault target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("block", target, target.getBlock());
        property("javaException", target, target.getJavaException());
        property("parentFault", target, target.getParentFault());
        property("subfaultsSet", target, target.getSubfaultsSet());
        property("name", target, target.getName());
        property("elementName", target, target.getElementName());
        property("message", target, target.getMessage());
        property("part", target, target.getPart());
    }
    
    protected void visitJavaMethod(JavaMethod target) {
        property("parametersList", target, target.getParametersList());
        property("exceptionsList", target, target.getExceptionsList());
        property("returnType", target, target.getReturnType());
        property("declaringClass", target, target.getDeclaringClass());
        property("name", target, target.getName());
    }
    
    protected void visitSOAPAnyType(SOAPAnyType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
    }
    
    protected void visitSOAPSimpleType(SOAPSimpleType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("referenceable", target, new Boolean(target.isReferenceable()));
        property("schemaTypeRef", target, target.getSchemaTypeRef());
    }
    
    protected void visitSOAPOrderedStructureType(SOAPOrderedStructureType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("membersList", target, target.getMembersList());
        property("attributeMembersList", target, target.getAttributeMembersList());
        property("subtypesSet", target, target.getSubtypesSet());
        property("parentType", target, target.getParentType());
    }
    
    protected void visitRPCResponseStructureType(RPCResponseStructureType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("membersList", target, target.getMembersList());
        property("attributeMembersList", target, target.getAttributeMembersList());
        property("subtypesSet", target, target.getSubtypesSet());
        property("parentType", target, target.getParentType());
    }
    
    protected void visitParameter(Parameter target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("block", target, target.getBlock());
        property("javaParameter", target, target.getJavaParameter());
        property("linkedParameter", target, target.getLinkedParameter());
        property("type", target, target.getType());
        property("embedded", target, new Boolean(target.isEmbedded()));
        property("name", target, target.getName());
    }
    
    protected void visitTypeMappingInfo(TypeMappingInfo target) {
        property("encodingStyle", target, target.getEncodingStyle());
        property("XMLType", target, target.getXMLType());
        property("javaTypeName", target, target.getJavaTypeName());
        property("serializerFactoryName", target, target.getSerializerFactoryName());
        property("deserializerFactoryName", target, target.getDeserializerFactoryName());
    }
    
    protected void visitFault(Fault target) {
        property("propertiesMap", target, target.getPropertiesMap());
        property("block", target, target.getBlock());
        property("javaException", target, target.getJavaException());
        property("parentFault", target, target.getParentFault());
        property("subfaultsSet", target, target.getSubfaultsSet());
        property("name", target, target.getName());
        property("elementName", target, target.getElementName());
        property("javaMemberName", target, target.getJavaMemberName());
    }
    
    protected void visitLiteralContentMember(LiteralContentMember target) {
        property("javaStructureMember", target, target.getJavaStructureMember());
        property("type", target, target.getType());
    }
    
    protected void visitSOAPEnumerationType(SOAPEnumerationType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("baseType", target, target.getBaseType());
    }
    
    protected void visitJavaType(JavaType target) {
        property("realName", target, target.getRealName());
        property("formalName", target, target.getFormalName());
        property("present", target, new Boolean(target.isPresent()));
        property("holder", target, new Boolean(target.isHolder()));
        property("holderPresent", target, new Boolean(target.isHolderPresent()));
        property("initString", target, target.getInitString());
        property("holderName", target, target.getHolderName());
    }
    
    protected void visitModelObject(ModelObject target) {
        property("propertiesMap", target, target.getPropertiesMap());
    }

    protected void visitLiteralIDType(LiteralIDType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("schemaTypeRef", target, target.getSchemaTypeRef());
        property("nillable", target, new Boolean(target.isNillable()));
        property("resolveIDREF", target, new Boolean(target.getResolveIDREF()));
    }   

    protected void visitSOAPListType(SOAPListType target) {
        property("javaType", target, target.getJavaType());
        property("propertiesMap", target, target.getPropertiesMap());
        property("version", target, target.getVersion());
        property("name", target, target.getName());
        property("itemType", target, target.getItemType());
    }
    /* END GENERATED CODE */
        
}
