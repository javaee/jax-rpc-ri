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
 * $Id: ModelImporter.java,v 1.3 2007-07-13 23:36:05 ofung Exp $
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

import java.io.InputStream;

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
import com.sun.xml.rpc.processor.model.java.JavaParameter;
import com.sun.xml.rpc.processor.model.java.JavaMethod;
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

import com.sun.xml.rpc.processor.model.soap.RPCRequestOrderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCRequestUnorderedStructureType;
import com.sun.xml.rpc.processor.model.soap.RPCResponseStructureType;
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

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.Version;

/**
 * @author JAX-RPC Development Team
 */
public class ModelImporter extends ImporterBase implements Constants {
    
    public ModelImporter(InputStream s) {
        super(s);
    }
    
    public Model doImport() {
        Object result = internalDoImport();
        if (!(result instanceof Model)) {
            throw new ModelException("model.importer.nonModel");
        }
        return (Model) result;
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
    
	protected String getTargetVersion() {
		return targetModelVersion;
	}
    
    protected void failInvalidSyntax(XMLReader reader) {
        throw new ModelException("model.importer.syntaxError",
            Integer.toString(reader.getLineNumber()));
    }
    
    protected void failInvalidVersion(XMLReader reader, String version) {
        throw new ModelException("model.importer.invalidVersion",
            new Object[] { Integer.toString(reader.getLineNumber()), version });
    }
    
	protected void failInvalidMinorMinorOrPatchVersion(
		XMLReader reader,
		String targetVersion,
		String currentVersion) {
		throw new ModelException(
			"model.importer.invalidMinorMinorOrPatchVersion",
			new Object[] {
				Integer.toString(reader.getLineNumber()),
				targetVersion,
				currentVersion });
	}
	
    protected void failInvalidClass(XMLReader reader, String className) {
        throw new ModelException("model.importer.invalidClass",
            new Object[] { Integer.toString(reader.getLineNumber()),
                className });
    }
    
    protected void failInvalidId(XMLReader reader, Integer id) {
        throw new ModelException("model.importer.invalidId",
            new Object[] { Integer.toString(reader.getLineNumber()),
                id.toString() });
    }
    
    protected void failInvalidLiteral(XMLReader reader, String type,
        String value) {
            
        throw new ModelException("model.importer.invalidLiteral",
            Integer.toString(reader.getLineNumber()));
    }
    
    protected void failInvalidProperty(XMLReader reader, Object subject,
        String name, Object value) {
            
        throw new ModelException("model.importer.invalidProperty",
            Integer.toString(reader.getLineNumber()));
    }
    
    /* BEGIN GENERATED CODE */
    
    protected void property(XMLReader reader, Object subject, String name, Object value) {
        if (subject instanceof LiteralWildcardMember) {
            propertyLiteralWildcardMember(reader, (LiteralWildcardMember) subject, name, value);
        }
        else if (subject instanceof ImportedDocumentInfo) {
            propertyImportedDocumentInfo(reader, (ImportedDocumentInfo) subject, name, value);
        }
        else if (subject instanceof JavaInterface) {
            propertyJavaInterface(reader, (JavaInterface) subject, name, value);
        }
        else if (subject instanceof JavaCustomType) {
            propertyJavaCustomType(reader, (JavaCustomType) subject, name, value);
        }
        else if (subject instanceof Operation) {
            propertyOperation(reader, (Operation) subject, name, value);
        }
        else if (subject instanceof HandlerChainInfo) {
            propertyHandlerChainInfo(reader, (HandlerChainInfo) subject, name, value);
        }
        else if (subject instanceof JavaException) {
            propertyJavaException(reader, (JavaException) subject, name, value);
        }
        else if (subject instanceof JavaStructureType) {
            propertyJavaStructureType(reader, (JavaStructureType) subject, name, value);
        }
        else if (subject instanceof JavaSimpleType) {
            propertyJavaSimpleType(reader, (JavaSimpleType) subject, name, value);
        }
        else if (subject instanceof JavaStructureMember) {
            propertyJavaStructureMember(reader, (JavaStructureMember) subject, name, value);
        }
        else if (subject instanceof Block) {
            propertyBlock(reader, (Block) subject, name, value);
        }
        else if (subject instanceof LiteralElementMember) {
            propertyLiteralElementMember(reader, (LiteralElementMember) subject, name, value);
        }
        else if (subject instanceof LiteralArrayWrapperType) {
            propertyLiteralArrayWrapperType(reader, (LiteralArrayWrapperType) subject, name, value);
        }
        else if (subject instanceof LiteralSequenceType) {
            propertyLiteralSequenceType(reader, (LiteralSequenceType) subject, name, value);
        }
        else if (subject instanceof RPCRequestUnorderedStructureType) {
            propertyRPCRequestUnorderedStructureType(reader, (RPCRequestUnorderedStructureType) subject, name, value);
        }
        else if (subject instanceof JavaEnumerationEntry) {
            propertyJavaEnumerationEntry(reader, (JavaEnumerationEntry) subject, name, value);
        }
        else if (subject instanceof Response) {
            propertyResponse(reader, (Response) subject, name, value);
        }
        else if (subject instanceof RPCRequestOrderedStructureType) {
            propertyRPCRequestOrderedStructureType(reader, (RPCRequestOrderedStructureType) subject, name, value);
        }
        else if (subject instanceof LiteralEnumerationType) {
            propertyLiteralEnumerationType(reader, (LiteralEnumerationType) subject, name, value);
        }
        else if (subject instanceof Request) {
            propertyRequest(reader, (Request) subject, name, value);
        }
        else if (subject instanceof LiteralAllType) {
            propertyLiteralAllType(reader, (LiteralAllType) subject, name, value);
        }
        else if (subject instanceof JavaArrayType) {
            propertyJavaArrayType(reader, (JavaArrayType) subject, name, value);
        }
        else if (subject instanceof Port) {
            propertyPort(reader, (Port) subject, name, value);
        }
        else if (subject instanceof LiteralAttributeMember) {
            propertyLiteralAttributeMember(reader, (LiteralAttributeMember) subject, name, value);
        }
        else if (subject instanceof HandlerInfo) {
            propertyHandlerInfo(reader, (HandlerInfo) subject, name, value);
        }
        else if (subject instanceof Service) {
            propertyService(reader, (Service) subject, name, value);
        }
        else if (subject instanceof SOAPStructureMember) {
            propertySOAPStructureMember(reader, (SOAPStructureMember) subject, name, value);
        }
        else if (subject instanceof JavaParameter) {
            propertyJavaParameter(reader, (JavaParameter) subject, name, value);
        }
        else if (subject instanceof Model) {
            propertyModel(reader, (Model) subject, name, value);
        }
        else if (subject instanceof LiteralSimpleType) {
            propertyLiteralSimpleType(reader, (LiteralSimpleType) subject, name, value);
        }
        else if (subject instanceof LiteralArrayType) {
            propertyLiteralArrayType(reader, (LiteralArrayType) subject, name, value);
        }
        else if (subject instanceof LiteralListType) {
            propertyLiteralListType(reader, (LiteralListType) subject, name, value);
        }
        else if (subject instanceof JavaEnumerationType) {
            propertyJavaEnumerationType(reader, (JavaEnumerationType) subject, name, value);
        }
        else if (subject instanceof SOAPCustomType) {
            propertySOAPCustomType(reader, (SOAPCustomType) subject, name, value);
        }
        else if (subject instanceof LiteralFragmentType) {
            propertyLiteralFragmentType(reader, (LiteralFragmentType) subject, name, value);
        }
        else if (subject instanceof SOAPArrayType) {
            propertySOAPArrayType(reader, (SOAPArrayType) subject, name, value);
        }
        else if (subject instanceof SOAPUnorderedStructureType) {
            propertySOAPUnorderedStructureType(reader, (SOAPUnorderedStructureType) subject, name, value);
        }
        else if (subject instanceof Message) {
            propertyMessage(reader, (Message) subject, name, value);
        }
        else if (subject instanceof HeaderFault) {
            propertyHeaderFault(reader, (HeaderFault) subject, name, value);
        }
        else if (subject instanceof JavaMethod) {
            propertyJavaMethod(reader, (JavaMethod) subject, name, value);
        }
        else if (subject instanceof SOAPAnyType) {
            propertySOAPAnyType(reader, (SOAPAnyType) subject, name, value);
        }
        else if (subject instanceof SOAPSimpleType) {
            propertySOAPSimpleType(reader, (SOAPSimpleType) subject, name, value);
        }
        else if (subject instanceof SOAPOrderedStructureType) {
            propertySOAPOrderedStructureType(reader, (SOAPOrderedStructureType) subject, name, value);
        }else if (subject instanceof SOAPAttributeMember) {
            propertySOAPAttributeMember(reader, (SOAPAttributeMember) subject, name, value);
        }
        else if (subject instanceof RPCResponseStructureType) {
            propertyRPCResponseStructureType(reader, (RPCResponseStructureType) subject, name, value);
        }
        else if (subject instanceof Parameter) {
            propertyParameter(reader, (Parameter) subject, name, value);
        }
        else if (subject instanceof TypeMappingInfo) {
            propertyTypeMappingInfo(reader, (TypeMappingInfo) subject, name, value);
        }
        else if (subject instanceof Fault) {
            propertyFault(reader, (Fault) subject, name, value);
        }
        else if (subject instanceof LiteralContentMember) {
            propertyLiteralContentMember(reader, (LiteralContentMember) subject, name, value);
        }
        else if (subject instanceof SOAPEnumerationType) {
            propertySOAPEnumerationType(reader, (SOAPEnumerationType) subject, name, value);
        }
        else if (subject instanceof JavaType) {
            propertyJavaType(reader, (JavaType) subject, name, value);
        }
        else if (subject instanceof ModelObject) {
            propertyModelObject(reader, (ModelObject) subject, name, value);
        }
        else if (subject instanceof LiteralIDType) {
            propertyLiteralIDType(reader, (LiteralIDType) subject, name, value);
        }else if (subject instanceof SOAPListType) {
            propertySOAPListType(reader, (SOAPListType) subject, name, value);
        }else if (subject instanceof LiteralAttachmentType) {
            propertyLiteralAttachmentType(reader, (LiteralAttachmentType) subject, name, value);
        }
        else {
            super.property(reader, subject, name, value);
        }
    }
    
    /**
     * @param reader
     * @param type
     * @param name
     * @param value
     */
    private void propertyLiteralAttachmentType(XMLReader reader, LiteralAttachmentType subject, String name, Object value) {
        if (name.equals("mimeType")) {
            subject.setMIMEType((String) value);
        }
        else if (name.equals("alternateMIMETypes")) {
            subject.setAlternateMIMETypes((java.util.List) value);
        }
        else if (name.equals("contentId")) {
            subject.setContentID((String) value);
        }
        else if (name.equals("isSwaRef")) {
            subject.setSwaRef(((Boolean) value).booleanValue());
        }
        else if (name.equals("javaType")) {
            subject.setJavaType((JavaType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
        
    }

    protected void propertyLiteralWildcardMember(XMLReader reader, LiteralWildcardMember subject, String name, Object value) {
        if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
        else if (name.equals("javaStructureMember")) {
            subject.setJavaStructureMember((com.sun.xml.rpc.processor.model.java.JavaStructureMember) value);
        }
        else if (name.equals("required")) {
            subject.setRequired(((Boolean) value).booleanValue());
        }
        else if (name.equals("repeated")) {
            subject.setRepeated(((Boolean) value).booleanValue());
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.literal.LiteralType) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("excludedNamespaceName")) {
            subject.setExcludedNamespaceName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyImportedDocumentInfo(XMLReader reader, ImportedDocumentInfo subject, String name, Object value) {
        if (name.equals("namespace")) {
            subject.setNamespace((java.lang.String) value);
        }
        else if (name.equals("type")) {
            subject.setType(((Integer) value).intValue());
        }
        else if (name.equals("location")) {
            subject.setLocation((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaInterface(XMLReader reader, JavaInterface subject, String name, Object value) {
        if (name.equals("realName")) {
            subject.setRealName((java.lang.String) value);
        }
        else if (name.equals("formalName")) {
            subject.setFormalName((java.lang.String) value);
        }
        else if (name.equals("methodsList")) {
            subject.setMethodsList((java.util.List) value);
        }
        else if (name.equals("interfacesList")) {
            subject.setInterfacesList((java.util.List) value);
        }
        else if (name.equals("impl")) {
            subject.setImpl((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaCustomType(XMLReader reader, JavaCustomType subject, String name, Object value) {
        if (name.equals("realName")) {
            subject.setRealName((java.lang.String) value);
        }
        else if (name.equals("formalName")) {
            subject.setFormalName((java.lang.String) value);
        }
        else if (name.equals("present")) {
            subject.setPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("holder")) {
            subject.setHolder(((Boolean) value).booleanValue());
        }
        else if (name.equals("holderPresent")) {
            subject.setHolderPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("initString")) {
            subject.setInitString((java.lang.String) value);
        }
        else if (name.equals("holderName")) {
            subject.setHolderName((java.lang.String) value);
        }
        else if (name.equals("typeMappingInfo")) {
            subject.setTypeMappingInfo((com.sun.xml.rpc.processor.config.TypeMappingInfo) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyOperation(XMLReader reader, Operation subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("uniqueName")) {
            subject.setUniqueName((java.lang.String) value);
        }
        else if (name.equals("request")) {
            subject.setRequest((com.sun.xml.rpc.processor.model.Request) value);
        }
        else if (name.equals("response")) {
            subject.setResponse((com.sun.xml.rpc.processor.model.Response) value);
        }
        else if (name.equals("faultsSet")) {
            subject.setFaultsSet((java.util.Set) value);
        }
        else if (name.equals("javaMethod")) {
            subject.setJavaMethod((com.sun.xml.rpc.processor.model.java.JavaMethod) value);
        }
        else if (name.equals("SOAPAction")) {
            subject.setSOAPAction((java.lang.String) value);
        }
        else if (name.equals("use")) {
            subject.setUse((com.sun.xml.rpc.wsdl.document.soap.SOAPUse) value);
        }
        else if (name.equals("style")) {
            subject.setStyle((com.sun.xml.rpc.wsdl.document.soap.SOAPStyle) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyHandlerChainInfo(XMLReader reader, HandlerChainInfo subject, String name, Object value) {
        if (name.equals("handlersList")) {
            subject.setHandlersList((java.util.List) value);
        }
        else if (name.equals("roles")) {
            subject.setRoles((java.util.Set) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaException(XMLReader reader, JavaException subject, String name, Object value) {
        if (name.equals("realName")) {
            subject.setRealName((java.lang.String) value);
        }
        else if (name.equals("formalName")) {
            subject.setFormalName((java.lang.String) value);
        }
        else if (name.equals("present")) {
            subject.setPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("holder")) {
            subject.setHolder(((Boolean) value).booleanValue());
        }
        else if (name.equals("holderPresent")) {
            subject.setHolderPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("initString")) {
            subject.setInitString((java.lang.String) value);
        }
        else if (name.equals("holderName")) {
            subject.setHolderName((java.lang.String) value);
        }
        else if (name.equals("membersList")) {
            subject.setMembersList((java.util.List) value);
        }
        else if (name.equals("subclassesSet")) {
            subject.setSubclassesSet((java.util.Set) value);
        }
        else if (name.equals("abstract")) {
            subject.setAbstract(((Boolean) value).booleanValue());
        }
        else if (name.equals("owner")) {
            subject.setOwner((java.lang.Object) value);
        }
        else if (name.equals("superclass")) {
            subject.setSuperclass((com.sun.xml.rpc.processor.model.java.JavaStructureType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaStructureType(XMLReader reader, JavaStructureType subject, String name, Object value) {
        if (name.equals("realName")) {
            subject.setRealName((java.lang.String) value);
        }
        else if (name.equals("formalName")) {
            subject.setFormalName((java.lang.String) value);
        }
        else if (name.equals("present")) {
            subject.setPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("holder")) {
            subject.setHolder(((Boolean) value).booleanValue());
        }
        else if (name.equals("holderPresent")) {
            subject.setHolderPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("initString")) {
            subject.setInitString((java.lang.String) value);
        }
        else if (name.equals("holderName")) {
            subject.setHolderName((java.lang.String) value);
        }
        else if (name.equals("membersList")) {
            subject.setMembersList((java.util.List) value);
        }
        else if (name.equals("subclassesSet")) {
            subject.setSubclassesSet((java.util.Set) value);
        }
        else if (name.equals("abstract")) {
            subject.setAbstract(((Boolean) value).booleanValue());
        }
        else if (name.equals("owner")) {
            subject.setOwner((java.lang.Object) value);
        }
        else if (name.equals("superclass")) {
            subject.setSuperclass((com.sun.xml.rpc.processor.model.java.JavaStructureType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaSimpleType(XMLReader reader, JavaSimpleType subject, String name, Object value) {
        if (name.equals("realName")) {
            subject.setRealName((java.lang.String) value);
        }
        else if (name.equals("formalName")) {
            subject.setFormalName((java.lang.String) value);
        }
        else if (name.equals("present")) {
            subject.setPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("holder")) {
            subject.setHolder(((Boolean) value).booleanValue());
        }
        else if (name.equals("holderPresent")) {
            subject.setHolderPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("initString")) {
            subject.setInitString((java.lang.String) value);
        }
        else if (name.equals("holderName")) {
            subject.setHolderName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaStructureMember(XMLReader reader, JavaStructureMember subject, String name, Object value) {
        if (name.equals("readMethod")) {
            subject.setReadMethod((java.lang.String) value);
        }
        else if (name.equals("writeMethod")) {
            subject.setWriteMethod((java.lang.String) value);
        }
        else if (name.equals("inherited")) {
            subject.setInherited(((Boolean) value).booleanValue());
        }
        else if (name.equals("constructorPos")) {
            subject.setConstructorPos(((Integer) value).intValue());
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("public")) {
            subject.setPublic(((Boolean) value).booleanValue());
        }
        else if (name.equals("declaringClass")) {
            subject.setDeclaringClass((java.lang.String) value);
        }
        else if (name.equals("owner")) {
            subject.setOwner((java.lang.Object) value);
        }
        else if (name.equals("name")) {
            subject.setName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyBlock(XMLReader reader, Block subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.AbstractType) value);
        }
        else if (name.equals("location")) {
            subject.setLocation(((Integer) value).intValue());
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyLiteralElementMember(XMLReader reader, LiteralElementMember subject, String name, Object value) {
        if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
        else if (name.equals("javaStructureMember")) {
            subject.setJavaStructureMember((com.sun.xml.rpc.processor.model.java.JavaStructureMember) value);
        }
        else if (name.equals("required")) {
            subject.setRequired(((Boolean) value).booleanValue());
        }
        else if (name.equals("repeated")) {
            subject.setRepeated(((Boolean) value).booleanValue());
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.literal.LiteralType) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("inherited")) {
            subject.setInherited(((Boolean) value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyLiteralSequenceType(XMLReader reader, LiteralSequenceType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        }
        else if (name.equals("attributeMembersList")) {
            subject.setAttributeMembersList((java.util.List) value);
        }
        else if (name.equals("elementMembersList")) {
            subject.setElementMembersList((java.util.List) value);
        }
        else if (name.equals("contentMember")) {
            subject.setContentMember((com.sun.xml.rpc.processor.model.literal.LiteralContentMember) value);
        }
        else if (name.equals("subtypesSet")) {
            subject.setSubtypesSet((java.util.Set) value);
        }
        else if (name.equals("parentType")) {
            subject.setParentType((com.sun.xml.rpc.processor.model.literal.LiteralStructuredType) value);
        } 
        else if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
        else if (name.equals("rpcWrapper")) {
            subject.setRpcWrapper(((Boolean)value).booleanValue());
        }
        else if (name.equals("isUnwrapped")) {
            subject.setUnwrapped(((Boolean)value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }

    protected void propertyLiteralArrayWrapperType(XMLReader reader, LiteralArrayWrapperType subject, String name, Object value) {
        if (name.equals("javaArrayType")) {
            subject.setJavaArrayType((com.sun.xml.rpc.processor.model.java.JavaArrayType) value);
        } 
        else {
            propertyLiteralSequenceType(reader, subject, name, value);    
        }
    }
        
    protected void propertyRPCRequestUnorderedStructureType(XMLReader reader, RPCRequestUnorderedStructureType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("membersList")) {
            subject.setMembersList((java.util.List) value);
        }
        else if (name.equals("attributeMembersList")) {
            subject.setAttributeMembersList((java.util.List) value);
        }
        else if (name.equals("subtypesSet")) {
            subject.setSubtypesSet((java.util.Set) value);
        }
        else if (name.equals("parentType")) {
            subject.setParentType((com.sun.xml.rpc.processor.model.soap.SOAPStructureType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaEnumerationEntry(XMLReader reader, JavaEnumerationEntry subject, String name, Object value) {
        if (name.equals("literalValue")) {
            subject.setLiteralValue((java.lang.String) value);
        }
        else if (name.equals("value")) {
            subject.setValue((java.lang.Object) value);
        }
        else if (name.equals("name")) {
            subject.setName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyResponse(XMLReader reader, Response subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("bodyBlocksMap")) {
            subject.setBodyBlocksMap((java.util.Map) value);
        }
        else if (name.equals("headerBlocksMap")) {
            subject.setHeaderBlocksMap((java.util.Map) value);
        }
        else if (name.equals("parametersList")) {
            subject.setParametersList((java.util.List) value);
        }
        else if (name.equals("faultBlocksMap")) {
            subject.setFaultBlocksMap((java.util.Map) value);
        }
        else if (name.equals("attachmentBlocksMap")) {
            subject.setAttachmentBlocksMap((java.util.Map) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyRPCRequestOrderedStructureType(XMLReader reader, RPCRequestOrderedStructureType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("membersList")) {
            subject.setMembersList((java.util.List) value);
        }
        else if (name.equals("attributeMembersList")) {
            subject.setAttributeMembersList((java.util.List) value);
        }
        else if (name.equals("subtypesSet")) {
            subject.setSubtypesSet((java.util.Set) value);
        }
        else if (name.equals("parentType")) {
            subject.setParentType((com.sun.xml.rpc.processor.model.soap.SOAPStructureType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyLiteralEnumerationType(XMLReader reader, LiteralEnumerationType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        }
        else if (name.equals("baseType")) {
            subject.setBaseType((com.sun.xml.rpc.processor.model.literal.LiteralType) value);
        } 
        else if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyRequest(XMLReader reader, Request subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("bodyBlocksMap")) {
            subject.setBodyBlocksMap((java.util.Map) value);
        }
        else if (name.equals("headerBlocksMap")) {
            subject.setHeaderBlocksMap((java.util.Map) value);
        }
        else if (name.equals("parametersList")) {
            subject.setParametersList((java.util.List) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyLiteralAllType(XMLReader reader, LiteralAllType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        }
        else if (name.equals("attributeMembersList")) {
            subject.setAttributeMembersList((java.util.List) value);
        }
        else if (name.equals("elementMembersList")) {
            subject.setElementMembersList((java.util.List) value);
        }
        else if (name.equals("contentMember")) {
            subject.setContentMember((com.sun.xml.rpc.processor.model.literal.LiteralContentMember) value);
        }
        else if (name.equals("subtypesSet")) {
            subject.setSubtypesSet((java.util.Set) value);
        }
        else if (name.equals("parentType")) {
            subject.setParentType((com.sun.xml.rpc.processor.model.literal.LiteralStructuredType) value);
        } 
        else if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
        else if (name.equals("rpcWrapper")) {
            subject.setRpcWrapper(((Boolean)value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaArrayType(XMLReader reader, JavaArrayType subject, String name, Object value) {
        if (name.equals("realName")) {
            subject.setRealName((java.lang.String) value);
        }
        else if (name.equals("formalName")) {
            subject.setFormalName((java.lang.String) value);
        }
        else if (name.equals("present")) {
            subject.setPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("holder")) {
            subject.setHolder(((Boolean) value).booleanValue());
        }
        else if (name.equals("holderPresent")) {
            subject.setHolderPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("initString")) {
            subject.setInitString((java.lang.String) value);
        }
        else if (name.equals("holderName")) {
            subject.setHolderName((java.lang.String) value);
        }
        else if (name.equals("elementName")) {
            subject.setElementName((java.lang.String) value);
        }
        else if (name.equals("elementType")) {
            subject.setElementType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }//bug fix:4904604
        else if (name.equals("soapArrayHolderName")) {
            subject.setSOAPArrayHolderName((java.lang.String) value);
        }        
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyPort(XMLReader reader, Port subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("operationsList")) {
            subject.setOperationsList((java.util.List) value);
        }
        else if (name.equals("javaInterface")) {
            subject.setJavaInterface((com.sun.xml.rpc.processor.model.java.JavaInterface) value);
        }
        else if (name.equals("clientHandlerChainInfo")) {
            subject.setClientHandlerChainInfo((com.sun.xml.rpc.processor.config.HandlerChainInfo) value);
        }
        else if (name.equals("serverHandlerChainInfo")) {
            subject.setServerHandlerChainInfo((com.sun.xml.rpc.processor.config.HandlerChainInfo) value);
        }
        else if (name.equals("SOAPVersion")) {
            subject.setSOAPVersion((com.sun.xml.rpc.soap.SOAPVersion) value);
        }
        else if (name.equals("address")) {
            subject.setAddress((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyLiteralAttributeMember(XMLReader reader, LiteralAttributeMember subject, String name, Object value) {
        if (name.equals("javaStructureMember")) {
            subject.setJavaStructureMember((com.sun.xml.rpc.processor.model.java.JavaStructureMember) value);
        }
        else if (name.equals("required")) {
            subject.setRequired(((Boolean) value).booleanValue());
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.literal.LiteralType) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("inherited")) {
            subject.setInherited(((Boolean) value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyHandlerInfo(XMLReader reader, HandlerInfo subject, String name, Object value) {
        if (name.equals("handlerClassName")) {
            subject.setHandlerClassName((java.lang.String) value);
        }
        else if (name.equals("headerNames")) {
            subject.setHeaderNames((java.util.Set) value);
        }
        else if (name.equals("properties")) {
            subject.setProperties((java.util.Map) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyService(XMLReader reader, Service subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("javaInterface")) {
            subject.setJavaInterface((com.sun.xml.rpc.processor.model.java.JavaInterface) value);
        }
        else if (name.equals("portsList")) {
            subject.setPortsList((java.util.List) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPStructureMember(XMLReader reader, SOAPStructureMember subject, String name, Object value) {
        if (name.equals("inherited")) {
            subject.setInherited(((Boolean) value).booleanValue());
        }
        else if (name.equals("javaStructureMember")) {
            subject.setJavaStructureMember((com.sun.xml.rpc.processor.model.java.JavaStructureMember) value);
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.soap.SOAPType) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaParameter(XMLReader reader, JavaParameter subject, String name, Object value) {
        if (name.equals("holder")) {
            subject.setHolder(((Boolean) value).booleanValue());
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("parameter")) {
            subject.setParameter((com.sun.xml.rpc.processor.model.Parameter) value);
        }
        else if (name.equals("name")) {
            subject.setName((java.lang.String) value);
        }
        else if (name.equals("holderName")) {
            subject.setHolderName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyModel(XMLReader reader, Model subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("targetNamespaceURI")) {
            subject.setTargetNamespaceURI((java.lang.String) value);
        }
        else if (name.equals("servicesList")) {
            subject.setServicesList((java.util.List) value);
        }
        else if (name.equals("extraTypesSet")) {
            subject.setExtraTypesSet((java.util.Set) value);
        }
        else if (name.equals("importedDocumentsMap")) {
            subject.setImportedDocumentsMap((java.util.Map) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("target")) {
            subject.setSource((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyLiteralSimpleType(XMLReader reader, LiteralSimpleType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        } 
        else if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
           
    protected void propertyLiteralArrayType(XMLReader reader, LiteralArrayType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        }
        else if (name.equals("elementType")) {
            subject.setElementType((com.sun.xml.rpc.processor.model.literal.LiteralType) value);
        } 
        else if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }

    protected void propertyLiteralListType(XMLReader reader, LiteralListType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        }
        else if (name.equals("itemType")) {
            subject.setItemType((com.sun.xml.rpc.processor.model.literal.LiteralType) value);
        }
        else if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaEnumerationType(XMLReader reader, JavaEnumerationType subject, String name, Object value) {
        if (name.equals("realName")) {
            subject.setRealName((java.lang.String) value);
        }
        else if (name.equals("formalName")) {
            subject.setFormalName((java.lang.String) value);
        }
        else if (name.equals("present")) {
            subject.setPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("holder")) {
            subject.setHolder(((Boolean) value).booleanValue());
        }
        else if (name.equals("holderPresent")) {
            subject.setHolderPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("initString")) {
            subject.setInitString((java.lang.String) value);
        }
        else if (name.equals("holderName")) {
            subject.setHolderName((java.lang.String) value);
        }
        else if (name.equals("baseType")) {
            subject.setBaseType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("entriesList")) {
            subject.setEntriesList((java.util.List) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPCustomType(XMLReader reader, SOAPCustomType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyLiteralFragmentType(XMLReader reader, LiteralFragmentType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        }
        else if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }        
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPArrayType(XMLReader reader, SOAPArrayType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("elementName")) {
            subject.setElementName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("elementType")) {
            subject.setElementType((com.sun.xml.rpc.processor.model.soap.SOAPType) value);
        }
        else if (name.equals("rank")) {
            subject.setRank(((Integer) value).intValue());
        }
        else if (name.equals("size")) {
            subject.setSize((int[]) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPUnorderedStructureType(XMLReader reader, SOAPUnorderedStructureType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("membersList")) {
            subject.setMembersList((java.util.List) value);
        }
        else if (name.equals("attributeMembersList")) {
            subject.setAttributeMembersList((java.util.List) value);
        }
        else if (name.equals("subtypesSet")) {
            subject.setSubtypesSet((java.util.Set) value);
        }
        else if (name.equals("parentType")) {
            subject.setParentType((com.sun.xml.rpc.processor.model.soap.SOAPStructureType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyMessage(XMLReader reader, Message subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("bodyBlocksMap")) {
            subject.setBodyBlocksMap((java.util.Map) value);
        }
        else if (name.equals("headerBlocksMap")) {
            subject.setHeaderBlocksMap((java.util.Map) value);
        }
        else if (name.equals("parametersList")) {
            subject.setParametersList((java.util.List) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyHeaderFault(XMLReader reader, HeaderFault subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("block")) {
            subject.setBlock((com.sun.xml.rpc.processor.model.Block) value);
        }
        else if (name.equals("javaException")) {
            subject.setJavaException((com.sun.xml.rpc.processor.model.java.JavaException) value);
        }
        else if (name.equals("parentFault")) {
            subject.setParentFault((com.sun.xml.rpc.processor.model.Fault) value);
        }
        else if (name.equals("subfaultsSet")) {
            subject.setSubfaultsSet((java.util.Set) value);
        }
        else if (name.equals("name")) {
            subject.setName((java.lang.String) value);
        }
        else if (name.equals("elementName")) {
            subject.setElementName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("message")) {
            subject.setMessage((javax.xml.namespace.QName) value);
        }
        else if (name.equals("part")) {
            subject.setPart((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaMethod(XMLReader reader, JavaMethod subject, String name, Object value) {
        if (name.equals("parametersList")) {
            subject.setParametersList((java.util.List) value);
        }
        else if (name.equals("exceptionsList")) {
            subject.setExceptionsList((java.util.List) value);
        }
        else if (name.equals("returnType")) {
            subject.setReturnType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("declaringClass")) {
            subject.setDeclaringClass((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPAnyType(XMLReader reader, SOAPAnyType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPSimpleType(XMLReader reader, SOAPSimpleType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("referenceable")) {
            subject.setReferenceable(((Boolean) value).booleanValue());
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPOrderedStructureType(XMLReader reader, SOAPOrderedStructureType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("membersList")) {
            subject.setMembersList((java.util.List) value);
        }
        else if (name.equals("attributeMembersList")) {
            subject.setAttributeMembersList((java.util.List) value);
        }
        else if (name.equals("subtypesSet")) {
            subject.setSubtypesSet((java.util.Set) value);
        }
        else if (name.equals("parentType")) {
            subject.setParentType((com.sun.xml.rpc.processor.model.soap.SOAPStructureType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPAttributeMember(XMLReader reader, SOAPAttributeMember subject, String name, Object value) {
        if (name.equals("javaStructureMember")) {
            subject.setJavaStructureMember((com.sun.xml.rpc.processor.model.java.JavaStructureMember) value);
        }
        else if (name.equals("required")) {
            subject.setRequired(((Boolean) value).booleanValue());
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.soap.SOAPType) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("inherited")) {
            subject.setInherited(((Boolean) value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }

    
    protected void propertyRPCResponseStructureType(XMLReader reader, RPCResponseStructureType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("membersList")) {
            subject.setMembersList((java.util.List) value);
        }
        else if (name.equals("attributeMembersList")) {
            subject.setAttributeMembersList((java.util.List) value);
        }
        else if (name.equals("subtypesSet")) {
            subject.setSubtypesSet((java.util.Set) value);
        }
        else if (name.equals("parentType")) {
            subject.setParentType((com.sun.xml.rpc.processor.model.soap.SOAPStructureType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyParameter(XMLReader reader, Parameter subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("block")) {
            subject.setBlock((com.sun.xml.rpc.processor.model.Block) value);
        }
        else if (name.equals("javaParameter")) {
            subject.setJavaParameter((com.sun.xml.rpc.processor.model.java.JavaParameter) value);
        }
        else if (name.equals("linkedParameter")) {
            subject.setLinkedParameter((com.sun.xml.rpc.processor.model.Parameter) value);
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.AbstractType) value);
        }
        else if (name.equals("embedded")) {
            subject.setEmbedded(((Boolean) value).booleanValue());
        }
        else if (name.equals("name")) {
            subject.setName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyTypeMappingInfo(XMLReader reader, TypeMappingInfo subject, String name, Object value) {
        if (name.equals("encodingStyle")) {
            subject.setEncodingStyle((java.lang.String) value);
        }
        else if (name.equals("XMLType")) {
            subject.setXMLType((javax.xml.namespace.QName) value);
        }
        else if (name.equals("javaTypeName")) {
            subject.setJavaTypeName((java.lang.String) value);
        }
        else if (name.equals("serializerFactoryName")) {
            subject.setSerializerFactoryName((java.lang.String) value);
        }
        else if (name.equals("deserializerFactoryName")) {
            subject.setDeserializerFactoryName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyFault(XMLReader reader, Fault subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("block")) {
            subject.setBlock((com.sun.xml.rpc.processor.model.Block) value);
        }
        else if (name.equals("javaException")) {
            subject.setJavaException((com.sun.xml.rpc.processor.model.java.JavaException) value);
        }
        else if (name.equals("parentFault")) {
            subject.setParentFault((com.sun.xml.rpc.processor.model.Fault) value);
        }
        else if (name.equals("subfaultsSet")) {
            subject.setSubfaultsSet((java.util.Set) value);
        }
        else if (name.equals("name")) {
            subject.setName((java.lang.String) value);
        }
        else if (name.equals("elementName")) {
            subject.setElementName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("javaMemberName")) {
            subject.setJavaMemberName((String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyLiteralContentMember(XMLReader reader, LiteralContentMember subject, String name, Object value) {
        if (name.equals("javaStructureMember")) {
            subject.setJavaStructureMember((com.sun.xml.rpc.processor.model.java.JavaStructureMember) value);
        }
        else if (name.equals("type")) {
            subject.setType((com.sun.xml.rpc.processor.model.literal.LiteralType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertySOAPEnumerationType(XMLReader reader, SOAPEnumerationType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("baseType")) {
            subject.setBaseType((com.sun.xml.rpc.processor.model.soap.SOAPType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyJavaType(XMLReader reader, JavaType subject, String name, Object value) {
        if (name.equals("realName")) {
            subject.setRealName((java.lang.String) value);
        }
        else if (name.equals("formalName")) {
            subject.setFormalName((java.lang.String) value);
        }
        else if (name.equals("present")) {
            subject.setPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("holder")) {
            subject.setHolder(((Boolean) value).booleanValue());
        }
        else if (name.equals("holderPresent")) {
            subject.setHolderPresent(((Boolean) value).booleanValue());
        }
        else if (name.equals("initString")) {
            subject.setInitString((java.lang.String) value);
        }
        else if (name.equals("holderName")) {
            subject.setHolderName((java.lang.String) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    protected void propertyModelObject(XMLReader reader, ModelObject subject, String name, Object value) {
        if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }

    protected void propertyLiteralIDType(XMLReader reader, LiteralIDType subject, String name, Object value) {
	if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("schemaTypeRef")) {
            subject.setSchemaTypeRef((javax.xml.namespace.QName) value);
        } 
        else if (name.equals("nillable")) {
            subject.setNillable(((Boolean) value).booleanValue());
        }
	else  if (name.equals("resolveIDREF")) {
            subject.setResolveIDREF(((Boolean) value).booleanValue());
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }       

    }

    protected void propertySOAPListType(XMLReader reader, SOAPListType subject, String name, Object value) {
        if (name.equals("javaType")) {
            subject.setJavaType((com.sun.xml.rpc.processor.model.java.JavaType) value);
        }
        else if (name.equals("propertiesMap")) {
            subject.setPropertiesMap((java.util.Map) value);
        }
        else if (name.equals("version")) {
            subject.setVersion((java.lang.String) value);
        }
        else if (name.equals("name")) {
            subject.setName((javax.xml.namespace.QName) value);
        }
        else if (name.equals("itemType")) {
            subject.setItemType((com.sun.xml.rpc.processor.model.soap.SOAPType) value);
        }
        else {
            failInvalidProperty(reader, subject, name, value);
        }
    }
    
    /* END GENERATED CODE */
        
}
