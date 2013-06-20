/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.reader;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.system.SystemVariableSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.vtype.VType;

import com.google.common.base.Function;

/**
 * Static converter function.
 *
 * @author bknerr
 * @since 22.12.2010
 *
 * @param <V> the basic data type of the system variable
 */
public final class ArchiveSampleToVTypeFunction<V extends Serializable> implements
        Function<IArchiveSample<V, IAlarmSystemVariable<V>>, VType> {

    /**
     * Constructor.
     */
    public ArchiveSampleToVTypeFunction() {
        // Empty
    }

    @Override
    @CheckForNull
    public VType apply(@Nonnull final IArchiveSample<V, IAlarmSystemVariable<V>> from) {
        if(from==null) {
            return null;
        }
        try {
            if (IArchiveMinMaxSample.class.isAssignableFrom(from.getClass())) {

                final V min = ((IArchiveMinMaxSample<V, IAlarmSystemVariable<V>>) from).getMinimum();
                final V max = ((IArchiveMinMaxSample<V, IAlarmSystemVariable<V>>) from).getMaximum();
              //  return null;
               return SystemVariableSupport.toVStatisticsValue(from.getSystemVariable(), min, max);
            }
           return SystemVariableSupport.toVType(from.getSystemVariable());
        } catch (final TypeSupportException e) {
            return null;
        }
    }
}
