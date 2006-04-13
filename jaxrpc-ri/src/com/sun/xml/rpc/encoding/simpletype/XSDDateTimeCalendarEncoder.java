/*
 * $Id: XSDDateTimeCalendarEncoder.java,v 1.2 2006-04-13 01:27:51 ofung Exp $
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

package com.sun.xml.rpc.encoding.simpletype;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import com.sun.msv.datatype.xsd.DateTimeType;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.util.VersionUtil;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDDateTimeCalendarEncoder extends XSDDateTimeDateEncoder {
    private static final SimpleTypeEncoder encoder =
        new XSDDateTimeCalendarEncoder();

    private XSDDateTimeCalendarEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
  /*      if (obj == null) {
            return null;
        }
        Calendar c = (Calendar)obj;
        SimpleDateFormat calFormatter;
        if (c.get(Calendar.ERA) == GregorianCalendar.BC) {   
            calFormatter = calendarFormatterBC;
        } else {
            calFormatter = calendarFormatter;
        }
        synchronized (calendarFormatter) {
            return calFormatter.format(c.getTime());
        }
*/            
        if (obj == null) {
            return null;
        }
        Calendar c = (Calendar)obj;
        String zone;
        String offsetStr;  
        StringBuffer resultBuf = new StringBuffer();
        if (c.get(Calendar.ERA) == GregorianCalendar.BC) {   
            resultBuf.append('-');     
        }
        synchronized (calendarFormatter) {
            zoneFormatter.setTimeZone(c.getTimeZone());
            zone = zoneFormatter.format(c.getTime());
            calendarFormatter.setTimeZone(c.getTimeZone());
            resultBuf.append(calendarFormatter.format(c.getTime()));
        }
        offsetStr = zone.substring(0, 3)+":"+zone.substring(3,5);
        resultBuf.append(offsetStr);
        return resultBuf.toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception { 
            
        if (str == null) {
            return null;
        }
        Calendar cal;
        boolean isNeg = str.charAt(0) == '-';

        StringBuffer zone = new StringBuffer(10);
        Date date = decodeDateUtil(str, zone);
        String zoneStr = zone.toString();
        cal = Calendar.getInstance(gmtTimeZone);
        cal.setTime(date);
        // UTC time
        if (zoneStr.length() != 0) {
            TimeZone tz = TimeZone.getTimeZone("GMT"+zoneStr);
//            System.out.println("caltimetime: " + cal.getTime().getTime());            
            if (isNeg) {
                cal.set(Calendar.ERA, GregorianCalendar.BC);
                cal.setTime(date);
            }     
            cal.setTimeZone(tz);
        }  else {

        if (isNeg)
            cal.set(Calendar.ERA, GregorianCalendar.BC);
        else
            cal.set(Calendar.ERA, GregorianCalendar.AD);
        }

        return cal;
    }

    private int getDSTSavings(TimeZone tz) {
        if (VersionUtil.isJavaVersionGreaterThan1_3()) {
            //jdk1.4
            return tz.getDSTSavings();
        }
        //this is < 1.4
        return ((SimpleTimeZone) tz).getDSTSavings();
    }

    private static final SimpleDateFormat zoneFormatter =
        new SimpleDateFormat("Z", locale);
    private static final SimpleDateFormat calendarFormatter =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", locale);
    private static final SimpleDateFormat calendarFormatterBC =
        new SimpleDateFormat("'-'yyyy-MM-dd'T'HH:mm:ss.SSS", locale);
    
    static {
        calendarFormatter.setTimeZone(gmtTimeZone);
        calendarFormatterBC.setTimeZone(gmtTimeZone);
    }

    public void writeAdditionalNamespaceDeclarations(
        Object obj,
        XMLWriter writer)
        throws Exception {
    }
}
