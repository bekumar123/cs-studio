/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.IRule;
import org.csstudio.sds.util.ColorAndFontUtil;

/**
 * Map integers from control system to widget states defines as integers.
 *
 * @author jhatje
 */
public class SwitchStateMapping implements IRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Object[] arguments) {

        if (arguments != null) {

            if (arguments.length >= 1 && compare(arguments[0], arguments[1])) {
                return -1;
            }
            if (arguments.length >= 2 && compare(arguments[0], arguments[2])) {
                return 0;
            }
            if (arguments.length >= 3 && compare(arguments[0], arguments[3])) {
                return 1;
            }
            if (arguments.length >= 4 && compare(arguments[0], arguments[4])) {
                return 6;
            }
            if (arguments.length >= 5 && compare(arguments[0], arguments[5])) {
                return 8;
            }

            return null;
        }
        return ColorAndFontUtil.toHex(138, 43, 226);
    }

    /**
     * @param object
     * @param object2
     * @return
     */
    private boolean compare(final Object object1, final Object object2) {
        if ( object1 instanceof Number && object2 instanceof Number) {
            final Number value1 = (Number) object1;
            final Number value2 = (Number) object2;
            return value1.doubleValue() == value2.doubleValue();
        }
        if (object1 instanceof Number) {
            return compareNo2Obj((Number) object1, object2);
        }
        if (object2 instanceof Number) {
            return compareNo2Obj((Number) object2, object1);
        }

        return object1 != null && object1.toString().equals(object2.toString());
    }

    /**
     * @param object1
     * @param object2
     * @return
     */
    private boolean compareNo2Obj(final Number no1, final Object object2) {
        if (object2 instanceof String) {
            final String val2 = (String) object2;
            try {
                final Double double2 = new Double(val2);
                return double2 == no1.doubleValue();
            }catch (final NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Vergleicht den Wert des Channel mit den in der Spalte 'Default Value' eingetragenen Werten für 'Unknown' bis 'Gestoert' und stellt diesen Zustand dar.";
    }

}
