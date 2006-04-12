/*
 * $Id: ParameterMemberInfo.java,v 1.1 2006-04-12 20:33:59 kohlert Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.xml.rpc.client.dii;

import javax.xml.namespace.QName;

public class ParameterMemberInfo {

           String memberName;
           QName memberXmlType;
           Class memberJavaClass;

           public void addParameterMemberInfo(String parameterName, QName parameterXmlType,
                                                 Class memberJavaType){
                 memberName = parameterName;
                 memberXmlType = parameterXmlType;
                 memberJavaClass = memberJavaType;
           }

           public String getMemberName(){
               return memberName;
           }

           public QName getMemberXmlType(){
               return memberXmlType;
           }

           public Class getMemberJavaClass(){
               return memberJavaClass;
           }
}
