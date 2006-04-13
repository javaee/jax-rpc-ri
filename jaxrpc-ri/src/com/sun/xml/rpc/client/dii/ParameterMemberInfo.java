/*
 * $Id: ParameterMemberInfo.java,v 1.2 2006-04-13 01:26:48 ofung Exp $
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
