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

package net.sf.hulp.measure;

/**
 * To measure time intervals such as functions and accumulate these
 * measurements a profiler package can be used. The profiler package
 * is a plug in; the plug in will be used if it is there; if it is
 * not found, there will be minimal overhead due to measuring since
 * no measurements are taking place.<br><br>
 *
 * This class represents a single measurement. Use the factory method
 * begin() to create a new measurement. Typical usage is:<br><br>
 *
 * <code>
 * Measurement m = Measurement.begin(topic);<br>
 * ... do something ...<br>
 * d.end();<br>
 * </code>
 * <br><br>
 *
 * Notes:<br>
 * - A measurement has a topic and a subtopic.
 * - Topic and sub topic can be set afterwards.
 * - A measurement is not reusable.
 */
public class Measurement {

    /**
     * Creates a factory
     */
    public interface FactoryFactory {
        Factory newFactory();
    }

    /**
     * Creates a measurement
     */
    public interface Factory {
        Measurement create(String topic, String subtopic);
    }

    private static Factory s_Factory;
    private static Measurement s_voidMeasurement;
    private static final String FACTORYNAME = "net.sf.hulp.profiler.FactoryFactory";

    /**
     * Constructor; forces callers to use factory method begin()
     */
    protected Measurement() {
    }

    /**
     * Factory method: returns a new (when measuring) or no-op Measurement (when not
     * measuring)
     */
    public static Measurement begin(String topic) {
        return begin(topic, null);
    }

    // Bootstrap: ensures that the begin() factory method will return a void or
    // concrete measurement
    static {
        try {
            // Try to load factory
            String name = System.getProperty(FACTORYNAME, FACTORYNAME);
            Class c = Class.forName(name);
            FactoryFactory f = (FactoryFactory) c.newInstance();
            s_Factory = f.newFactory();
            System.out.println("Measurement factory loaded: " + name + " (" + s_Factory + ")");
        } catch (Throwable ex) {
            // Ignore error
        }

        // Factory failed to load? Disable measurements
        if (s_Factory == null) {
            s_voidMeasurement = new Measurement();
        }
    }

    /**
     * Factory method: returns a new (when measuring) or no-op Measurement (when not
     * measuring)
     */
    public static Measurement begin(String topic, String subTopic) {
        if (s_voidMeasurement != null) {
            return s_voidMeasurement;
        } else {
            return s_Factory.create(topic, subTopic);
        }
    }

    /**
     * Returns if there is a measuring infrastructure installed
     * @return true if is installed
     */
    public static boolean isInstalled() {
        return s_voidMeasurement == null;
    }

    /**
     * Ends the time interval and adds the measurement to the profiler list.
     * This method is overridden in the case a profiler plug-in is used.
     */
    public void end() {

    }

    /**
     * Resets the topic of the measurement
     * This method is overridden in the case a profiler plug-in is used.
     */
    public void setTopic(String topic) {

    }

    /**
     * Further specifies the measurement
     * This method is overridden in the case a profiler plug-in is used.
     */
    public void setSubtopic(String subTopic) {

    }
}