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
package org.csstudio.archive.common.reader;

import java.io.Serializable;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.vtype.trendplotter.ArchiveVEnum;
import org.csstudio.archive.vtype.trendplotter.ArchiveVNumber;
import org.csstudio.archive.vtype.trendplotter.ArchiveVNumberArray;
import org.csstudio.archive.vtype.trendplotter.ArchiveVStatistics;
import org.csstudio.archive.vtype.trendplotter.ArchiveVString;
import org.csstudio.archive.vtype.trendplotter.VTypeHelper;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VEnum;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VStatistics;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Raw value iterator for service infrastructure.
 *
 * @author bknerr
 * @since 21.12.2010
 * @param <V> the base type of this channel
 */
public class DesyArchiveValueIterator<V extends Serializable> extends AbstractValueIterator {

    @SuppressWarnings("unused")
    private static final Logger LOG =
        LoggerFactory.getLogger(DesyArchiveValueIterator.class);

    /**
     * Constructor.
     */
    DesyArchiveValueIterator(@SuppressWarnings("rawtypes") @Nonnull final Iterable<IArchiveSample> iterable,
                             @Nonnull final String channelName,
                             @Nonnull final TimeInstant start,
                             @Nonnull final TimeInstant end) {
        super(iterable, channelName, start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return getIterator().hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public VType nextold() throws Exception {
        final VType value = ARCH_SAMPLE_2_VTYPE_FUNC.apply(getIterator().next());
        if (value == null) {
            throw new TypeSupportException("Sample could not be converted to " + VType.class.getName() + " type.", null);
        }
        return value;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public VType next() throws Exception {
        if(!getIterator().hasNext()) {
            return null;
        }
        final Object o=getIterator().next();
        final VType value =(VType) ARCH_SAMPLE_2_VTYPE_FUNC.apply(o);
        final String q=((IArchiveSample) o).getRequestType();
        final Timestamp time =VTypeHelper.getTimestamp(value);
        if(value instanceof VStatistics){
            final VStatistics st = (VStatistics) value;
            return new ArchiveVStatistics(time, st.getAlarmSeverity(), st.getAlarmName(), st, q, st.getAverage(), st.getMin(), st.getMax(), st.getStdDev(), st.getNSamples());
        }
        if (value instanceof VNumber)
        {
            final VNumber number = (VNumber) value;

            return new ArchiveVNumber(time, number.getAlarmSeverity(), number.getAlarmName(), number, number.getValue(),q);
        }
        if (value instanceof VString)
        {
            final VString string = (VString) value;
            if(string instanceof ArchiveVString ) {
                return new ArchiveVString(time, string.getAlarmSeverity(), string.getAlarmName(),q, string.getValue());
            } else {
                return new ArchiveVString(time, string.getAlarmSeverity(), string.getAlarmName(),q, string.getValue());
            }
        }
        if (value instanceof VNumberArray)
        {
            final VNumberArray number = (VNumberArray) value;
            return new ArchiveVNumberArray(time, number.getAlarmSeverity(), number.getAlarmName(), number,q, number.getData());
        }
        if (value instanceof VEnum)
        {
            final VEnum labelled = (VEnum) value;
            return new ArchiveVEnum(time, labelled.getAlarmSeverity(), labelled.getAlarmName(),q, labelled.getLabels(), labelled.getIndex());
        }
     return value;
    }
}
