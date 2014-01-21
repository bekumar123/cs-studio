/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.engine;

import gov.aps.jca.JCALibrary;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.preferences.AbstractPreference;


/**
 * Access to engine related RDB archive preferences.
 *
 * @author bknerr
 * @since 16.11.2010
 */
public class ArchiveEnginePreferencesService {

    /**
     * Type safe delegator to Eclipse preference service.
     *
     * @author bknerr
     * @since 24.08.2011
     * @param <T> the type of the preference
     */
    private static final class ArchiveEnginePreference<T> extends AbstractPreference<T>{
        public static final ArchiveEnginePreference<Integer> REG_GROUP_ID =
                new ArchiveEnginePreference<Integer>("regGroupId", Integer.valueOf(0));
        public static final ArchiveEnginePreference<Integer> WRITE_PERIOD_IN_S =
            new ArchiveEnginePreference<Integer>("writePeriodInS", Integer.valueOf(5));
        public static final ArchiveEnginePreference<Integer> HEARTBEAT_PERIOD_IN_S =
            new ArchiveEnginePreference<Integer>("heartBeatPeriodInS", Integer.valueOf(1));
        public static final ArchiveEnginePreference<String> VERSION =
            new ArchiveEnginePreference<String>("version", "0.0.1-beta");
        public static final ArchiveEnginePreference<String> HTTP_ADMIN_VALUE =
            new ArchiveEnginePreference<String>("httpAdmin", "");
        /**
         * @author wxu
         * set caContext variable value
         */

        public static final ArchiveEnginePreference<String> CA_CONTEXT_NAME =
                new ArchiveEnginePreference<String>("caContext",
                                                    "");
        /**
         * @author wxu
         * set queue size variable value
         */
        public static final ArchiveEnginePreference<Integer> QUEUE_WARN_SIZE =
                new ArchiveEnginePreference<Integer>("queueWarnSize",  Integer.valueOf(50000));
        public static final ArchiveEnginePreference<Integer> QUEUE_MAXI_SIZE =
                new ArchiveEnginePreference<Integer>("queueMaxiSize", Integer.valueOf(600000));
        /**
         * Constructor.
         */
        protected ArchiveEnginePreference(@Nonnull final String keyAsString,
                                          @Nonnull final T defaultValue) {
            super(keyAsString, defaultValue);
        }


        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        @Nonnull
        protected Class<? extends AbstractPreference<T>> getClassType() {
            return (Class<? extends AbstractPreference<T>>) ArchiveEnginePreference.class;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getPluginID() {
            return ArchiveEngineActivator.PLUGIN_ID;
        }
    }

    /**
     * Constructor.
     */
    public ArchiveEnginePreferencesService() {
        // Empty
    }

    @Nonnull
    public String getVersion() {
        return ArchiveEnginePreference.VERSION.getValue();
    }
    @Nonnull
    public Integer getWritePeriodInS() {
        return ArchiveEnginePreference.WRITE_PERIOD_IN_S.getValue();
    }
    @Nonnull
    public Integer getRegGroupId() {
        return ArchiveEnginePreference.REG_GROUP_ID.getValue();
    }
    @Nonnull
    public Integer getHeartBeatPeriodInS() {
        return ArchiveEnginePreference.HEARTBEAT_PERIOD_IN_S.getValue();
    }
    @Nonnull
    public String getHttpAdminValue() {
        return ArchiveEnginePreference.HTTP_ADMIN_VALUE.getValue();
    }
    /**
     * @author wxu
     * set queue size variable value
     */
    @Nonnull
    public Integer getQueueWarnSize() {
        return ArchiveEnginePreference.QUEUE_WARN_SIZE.getValue();
    }
    @Nonnull
    public Integer getQueueMaxiSize() {
        return ArchiveEnginePreference.QUEUE_MAXI_SIZE.getValue();
    }
    @Nonnull
    public String getHttpAdminKey() {
        return ArchiveEnginePreference.HTTP_ADMIN_VALUE.getKeyAsString();
    }
    /**
     * @author wxu
     * set caContext variable value
     */
    public String getCaContextValue() {
        if ("CHANNEL_ACCESS_SERVER_JAVA".equalsIgnoreCase(ArchiveEnginePreference.CA_CONTEXT_NAME.getValue())) {
            return JCALibrary.CHANNEL_ACCESS_SERVER_JAVA;
        } else if ("JNI_THREAD_SAFE".equalsIgnoreCase(ArchiveEnginePreference.CA_CONTEXT_NAME.getValue())) {
            return JCALibrary.JNI_THREAD_SAFE;
        } else if ("JNI_SINGLE_THREADED".equalsIgnoreCase(ArchiveEnginePreference.CA_CONTEXT_NAME.getValue())) {
            return JCALibrary.JNI_SINGLE_THREADED;
        } else {
            return JCALibrary.CHANNEL_ACCESS_JAVA;
        }

    }
}

