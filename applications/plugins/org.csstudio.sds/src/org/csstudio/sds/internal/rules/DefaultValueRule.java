/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.internal.rules;

import org.csstudio.sds.model.IRule;

/**
 * This rule passes the value like direct connection rule.
 * Only if this rule is selected the ConnectionUtilNew update the widget with 
 * the value in the default value and no connection to the control system will be
 * established. in other words this rule is used as a marker.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class DefaultValueRule implements IRule {
	/**
	 * The ID for the standard rule that simply routes the control
	 * system events to the target widget model.
	 */
	public static final String TYPE_ID = "defaultValue"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final Object[] arguments) {
		Object result = 0;

		if ((arguments != null) && (arguments.length > 0)) {
			result = arguments[0];
		}

		return result;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "Pass Value from column 'Default Value'. The channel name must be 'defaultValue'.";
    }

}
