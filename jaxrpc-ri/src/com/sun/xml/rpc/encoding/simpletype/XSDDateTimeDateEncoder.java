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

package com.sun.xml.rpc.encoding.simpletype;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;

/**
 *
 * @author JAX-RPC Development Team
 */
public class XSDDateTimeDateEncoder extends SimpleTypeEncoderBase {
    private static final SimpleTypeEncoder encoder =
        new XSDDateTimeDateEncoder();

    protected XSDDateTimeDateEncoder() {
    }

    public static SimpleTypeEncoder getInstance() {
        return encoder;
    }

    public String objectToString(Object obj, XMLWriter writer)
        throws Exception {
            
        if (obj == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance(gmtTimeZone);
        calendar.setTime((Date) obj);
        boolean isBC = (calendar.get(Calendar.ERA) == GregorianCalendar.BC);
        StringBuffer buf = new StringBuffer();
        if (isBC) {
            calendar.set(Calendar.ERA, GregorianCalendar.AD);
            buf.append("-");
        }
        synchronized (dateFormatter) {
            buf.append(dateFormatter.format(calendar.getTime()));
        }
//        System.out.println("date: "+buf.toString());
        return buf.toString();
    }

    public Object stringToObject(String str, XMLReader reader)
        throws Exception {
            
        if (str == null) {
            return null;
        }
        return decodeDateUtil(str, null);
    }

    public static void validateDateStr(String dateStr) throws Exception {
        // TODO how much should we validate
        if (dateStr.length() < 19)
            throw new DeserializationException("xsd.invalid.date", dateStr);
    }

    protected static String getDateFormatPattern(String xsdDateTime) {
        String formatPattern = "yyyy";
        int idx = xsdDateTime.indexOf('-', 4);
        for (int i = 4; i < idx; i++)
            formatPattern += "y";
        formatPattern += "-MM-dd'T'HH:mm:ss";
        idx = xsdDateTime.indexOf('.');
        for (int i = idx; i < xsdDateTime.length() - 1 && i < idx + 3; i++) {
            if (Character.isDigit(xsdDateTime.charAt(i + 1))) {
                if (i == idx) {
                    formatPattern += ".";
                }
                formatPattern += "S";
            } else {
                break;
            }
        }
        return formatPattern;
    }

    protected static Date decodeDateUtil(String str, StringBuffer zone)
        throws Exception {
            
        // this code is synchronized because SimpleDateFormat is not multi-thread safe
        if (str == null) {
            return null;
        } 
        str = EncoderUtils.collapseWhitespace(str);
        Calendar cal = Calendar.getInstance();
        boolean isNeg = false;

        str = EncoderUtils.collapseWhitespace(str);
        if (str.charAt(0) == '+') {
            str = str.substring(1);
        }
        if (str.charAt(0) == '-') {
            str = str.substring(1);
            isNeg = true;
        }            
        validateDateStr(str);
        StringBuffer strBuf = new StringBuffer(30);
        int dateLen = getDateFormatPattern(str, strBuf);
        String pattern = strBuf.toString();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(gmtTimeZone);
        String tmp = str.substring(0, dateLen);
        Date date = df.parse(str.substring(0, dateLen));

        // handle fractional milliseconds and timezone
        if (dateLen < str.length()) {
            int start = dateLen;
            if (Character.isDigit(str.charAt(start))) {
                int end = start;
                String fraction;

                while (end < str.length()
                    && Character.isDigit(str.charAt(end)))
                    end++;

                String tmp2 = str.substring(start, start + 1);
                int fractmilli =
                    Integer.parseInt(str.substring(start, start + 1));
                if (fractmilli >= 5)
                    date.setTime(date.getTime() + 1);
                start = end;
            }
            // do timezone
            if (start < str.length()) {
                if (str.charAt(start) != 'Z') {
                    if (zone != null)
                        zone.append(str.substring(start));
                    tmp = str.substring(start + 1);
                    Date tzOffset;
                    synchronized (timeZoneFormatter) {
                        tzOffset =
                            timeZoneFormatter.parse(str.substring(start + 1));
                    }
                    long millis =
                        str.charAt(start) == '+'
                            ? - (tzOffset.getTime())
                            : tzOffset.getTime();
                    date.setTime(date.getTime() + millis);
                } else if (str.charAt(start) == 'Z') {
                    cal.setTimeZone(gmtTimeZone);                
                }
            }
        }
        if (isNeg) {
            cal.setTime(date);
            cal.set(Calendar.ERA, GregorianCalendar.BC);
            date = cal.getTime();
        }
        return date;
    }

    protected static int getDateFormatPattern(
        String dateStr,
        StringBuffer strBuf) {
            
        String formatPattern = "yyyy";
        strBuf.append(formatPattern);
        int idx = dateStr.indexOf('-', 4);
        for (int i = 4; i < idx; i++)
            strBuf.append('y');
        strBuf.append("-MM-dd'T'HH:mm:ss");
        idx = dateStr.indexOf('.');
        for (int i = idx;
            idx > 0 && i < dateStr.length() - 1 && i < idx + 3;
            i++) {
            if (Character.isDigit(dateStr.charAt(i + 1))) {
                if (i == idx) {
                    strBuf.append('.');
                }
                strBuf.append('S');
            } else {
                break;
            }
        }
        return strBuf.length() - 2;
    }

    protected static final Locale locale = new Locale("en_US");
    protected static final SimpleDateFormat timeZoneFormatter =
        new SimpleDateFormat("HH:mm", locale);
        
    protected static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
    
    protected static final SimpleDateFormat dateFormatter =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
        
    static {
        dateFormatter.setTimeZone(gmtTimeZone);
        timeZoneFormatter.setTimeZone(gmtTimeZone);
    }

}
