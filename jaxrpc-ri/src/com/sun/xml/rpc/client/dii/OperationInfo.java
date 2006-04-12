/*
 * $Id: OperationInfo.java,v 1.1 2006-04-12 20:33:56 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.xml.rpc.client.dii;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;

import com.sun.xml.rpc.processor.model.literal.LiteralElementMember;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.soap.SOAPConstantsFactory;
import com.sun.xml.rpc.soap.SOAPVersion;

public class OperationInfo {
    String namespace;
    String localName;
    QName qualifiedName;
    HashMap parameterModels;
    ArrayList parameterMembers;
    ArrayList parameterNames;
    //this for doclit is the element xmlname
    ArrayList parameterXmlTypes;
    //for doclit - this is real xmltype --ie schemaRefName
    ArrayList parameterXmlTypeQNames;
    ArrayList parameterJavaTypes;
    ArrayList parameterModes;
    String endPointAddress;
    QName requestQName;
    QName requestXmlType;
    //LiteralType requestLiteralType;
    LiteralType returnLiteralType;
    QName responseQName;
    QName returnXmlType;
    QName returnXmlTypeQName;
    Class returnJavaType;
    String returnClassName;
    ArrayList returnMembers;
    //this is for literal only-
    LiteralElementMember returnTypeModel;
    Map properties;
    boolean isDocumentOperationFlag;
    boolean isRPCLiteralOperationFlag;
    boolean isOneWay;

    private com.sun.xml.rpc.soap.SOAPEncodingConstants soapEncodingConstants = null;

    private void init(SOAPVersion ver) {
        soapEncodingConstants = SOAPConstantsFactory.getSOAPEncodingConstants(ver);
    }

    public OperationInfo(String localName) {
        this(localName, SOAPVersion.SOAP_11);
    }

    public OperationInfo(String localName, SOAPVersion ver) {
        init(ver); //Initialize SOAP constants
        init();
        this.localName = localName;
    }

    protected void init() {
        namespace = "";
        localName = "";
        qualifiedName = null;
        parameterMembers = new ArrayList();
        parameterModels = new HashMap();
        parameterNames = new ArrayList();
        parameterXmlTypes = new ArrayList();
        parameterXmlTypeQNames = new ArrayList();
        parameterJavaTypes = new ArrayList();
        parameterModes = new ArrayList();
        endPointAddress = "";
        returnXmlType = null;
        returnJavaType = null;
        returnXmlTypeQName = null;
        returnMembers = new ArrayList();
        requestQName = null;
        requestXmlType = null;
        responseQName = null;
        properties = new HashMap();
        isDocumentOperationFlag = false;
        setProperty(Call.OPERATION_STYLE_PROPERTY, "rpc");
        setProperty(Call.ENCODINGSTYLE_URI_PROPERTY, soapEncodingConstants.getURIEncoding());
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
        this.qualifiedName = null;
    }

    public String getNamespace() {
        return namespace;
    }

    public QName getName() {
        if (qualifiedName == null) {
            qualifiedName = new QName(namespace, localName);
        }

        return qualifiedName;
    }

    public void addParameter(String parameterName, QName parameterXmlType) {
        addParameter(parameterName, parameterXmlType, null, ParameterMode.IN);
    }

    public void addParameter(String parameterName, QName parameterXmlType, Class javaType, ParameterMode mode) {
        parameterNames.add(parameterName);
        parameterXmlTypes.add(parameterXmlType);
        parameterJavaTypes.add(javaType);
        parameterModes.add(mode);
    }

    public void addParameterModel(String parameterName, LiteralElementMember parameterWsdlModel) {
        parameterModels.put(parameterName, parameterWsdlModel);
    }

    public Collection getParameterModels() {
        return parameterModels.values();
    }

    public void setReturnType(QName returnXmlType) {
        setReturnType(returnXmlType, null);
    }

    public void setReturnTypeQName(QName returnXmlTypeQName) {
        this.returnXmlTypeQName = returnXmlTypeQName;
    }

    public QName getReturnXmlTypeQName() {
        return this.returnXmlTypeQName;
    }

    public void setReturnType(QName returnXmlType, Class returnJavaType) {
        this.returnXmlType = returnXmlType;
        this.returnJavaType = returnJavaType;
    }

    public void addParameterXmlTypeQName(QName parameterXmlTypeQName) {
        parameterXmlTypeQNames.add(parameterXmlTypeQName);
    }

    public QName[] getParameterXmlTypeQNames() {
        return (QName[])
                parameterXmlTypeQNames.toArray(new QName[parameterXmlTypeQNames.size()]);
    }

    public void setReturnClassName(String name) {
        this.returnClassName = name;
    }

    public ParameterMemberInfo[] getReturnMembers() {
        return (ParameterMemberInfo[])
                returnMembers.toArray(new ParameterMemberInfo[returnMembers.size()]);
    }

    public void setReturnMembers(ArrayList members) {
        this.returnMembers = members;
    }

    public String getReturnClassName() {
        return this.returnClassName;
    }

    public Class getReturnClass() {
        return this.returnJavaType;
    }

    public void setRequestQName(QName name) {
        requestQName = name;
    }

    public QName getRequestQName() {
        return requestQName;
    }

    public void setResponseQName(QName name) {
        responseQName = name;
    }

    public QName getResponseQName() {
        return responseQName;
    }

    public QName getReturnXmlType() {
        return returnXmlType;
    }

    public void setReturnTypeModel(LiteralElementMember returnTypeModel) {
        this.returnTypeModel = returnTypeModel;
    }

    public String[] getParameterNames() {
        return (String[]) parameterNames.toArray(new String[parameterNames.size()]);
    }

    public int getParameterCount() {
        return parameterNames.size();
    }

    public QName[] getParameterXmlTypes() {
        return (QName[]) parameterXmlTypes.toArray(new QName[parameterXmlTypes.size()]);
    }

    //added kw
    public Class[] getParameterJavaTypes() {
        return (Class[]) parameterJavaTypes.toArray(new QName[parameterJavaTypes.size()]);
    }

    public ParameterMode[] getParameterModes() {
        return (ParameterMode[]) parameterModes.toArray(new ParameterMode[parameterModes.size()]);
    }

    public void addMemberInfos(ArrayList infos) {
        parameterMembers.add(infos);
    }

    public ParameterMemberInfo[] getMemberInfo(int index) {
        if (parameterMembers.size() > index) {
            ArrayList infosByParameterIndex = (ArrayList) parameterMembers.get(index);
            return (ParameterMemberInfo[]) infosByParameterIndex.toArray(new ParameterMemberInfo[infosByParameterIndex.size()]);
        }
        return new ParameterMemberInfo[0];
    }

    public String makeKey(String parameterName, Class parameterClass) {
        String className = "";
        if (parameterClass != null)
            className = parameterClass.getName();
        return new String(parameterName + className);
    }

    public void setEndPointAddress(String address) {
        endPointAddress = address;
    }

    public String getEndPointAddress() {
        return endPointAddress;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Iterator getPropertyKeys() {
        return properties.keySet().iterator();
    }

    public void beDocumentOperation() {
        isDocumentOperationFlag = true;
    }

    public boolean isDocumentOperation() {
        return isDocumentOperationFlag;
    }

    public void setIsOneWay(boolean oneway) {
        this.isOneWay = oneway;
    }

    public boolean isOneWay() {
        return this.isOneWay;
    }
}
