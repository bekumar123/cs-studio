/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.alarm.treeview.preferences;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.domain.desy.preferences.AbstractPreference;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * Constant definitions for alarm tree preferences (mimicked enum with inheritance).
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
public final class AlarmTreePreference<T> extends AbstractPreference<T> {

    public static final AlarmTreePreference<String> JMS_QUEUE =
        new AlarmTreePreference<String>("jms.queue", "ALARM,ACK");

    public static final AlarmTreePreference<String> RES_ICON_PATH =
        new AlarmTreePreference<String>("res.icon.path", "./res/icons");

    public static final AlarmTreePreference<String> ALARM_DISPLAY_ALIAS =
        new AlarmTreePreference<String>("alarmDisplayAlias", "chan");

	public static final AlarmTreePreference<Font> FONT = new AlarmTreePreference<Font>("font", Display.getCurrent().getSystemFont());
    
    /**
     * Constructor.
     * @param keyAsString
     * @param defaultValue
     */
    private AlarmTreePreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) AlarmTreePreference.class;
    }

    @Override
    @Nonnull
    public String getPluginID() {
        return AlarmTreePlugin.PLUGIN_ID;
    }

}
