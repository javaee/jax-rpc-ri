/*
 * $Id: XSDDateTimeCalendarEncoder.java,v 1.4 2007-07-13 23:35:59 ofung Exp $
 */

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

package com.sun.xml.rpc.encoding.simpletype;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

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
		SimpleDateFormat calendarFormat = getCalendarFormat();
		SimpleDateFormat zoneFormat = getZoneFormat();
        zoneFormat.setTimeZone(c.getTimeZone());
        zone = zoneFormat.format(c.getTime());
        calendarFormat.setTimeZone(c.getTimeZone());
        resultBuf.append(calendarFormat.format(c.getTime()));
        offsetStr = zone.substring(0, 3)+":"+zone.substring(3,5);
        resultBuf.append(offsetStr);
        return resultBuf.toString();
    }

	public SimpleDateFormat getCalendarFormat() {
		SimpleDateFormat format = (SimpleDateFormat)calendarFormatter.get();
		if (format == null) {
			format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", locale);
        	format.setTimeZone(gmtTimeZone);
			calendarFormatter.set(format);
		}
		return format;
	}

	public SimpleDateFormat getZoneFormat() {
		SimpleDateFormat format = (SimpleDateFormat)zoneFormatter.get();
		if (format == null) {
			format = new SimpleDateFormat("Z", locale);
			zoneFormatter.set(format);
		}
		return format;
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

    private static final ThreadLocal zoneFormatter = new ThreadLocal();
    private static final ThreadLocal calendarFormatter = new ThreadLocal();

/*
    private static final SimpleDateFormat zoneFormatter =
        new SimpleDateFormat("Z", locale);
    private static final SimpleDateFormat calendarFormatter =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", locale);
*/

    public void writeAdditionalNamespaceDeclarations(
        Object obj,
        XMLWriter writer)
        throws Exception {
    }
}
