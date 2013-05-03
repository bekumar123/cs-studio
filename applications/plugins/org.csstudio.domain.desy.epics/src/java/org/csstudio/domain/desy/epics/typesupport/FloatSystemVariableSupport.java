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
package org.csstudio.domain.desy.epics.typesupport;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ListFloat;
import org.epics.vtype.VType;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;

/**
 * System variable support for {@link Float};
 *
 * @author bknerr
 * @since 11.05.2011
 */
final class FloatSystemVariableSupport extends EpicsSystemVariableSupport<Float> {
    /**
     * Constructor.
     */
    public FloatSystemVariableSupport() {
        super(Float.class);
    }

    @Override
    @Nonnull
    protected IValue convertEpicsSystemVariableToIValue(@Nonnull final EpicsSystemVariable<Float> sysVar) {
        return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(sysVar.getTimestamp()),
                                              EpicsIValueTypeSupport.toSeverity(sysVar.getAlarm().getSeverity()),
                                              sysVar.getAlarm().getStatus().toString(),
                                              null,
                                              null,
                                              new double[] {sysVar.getData().doubleValue()});
    }

    @Override
    @Nonnull
    protected IValue convertCollectionToIValue(@Nonnull final Collection<Float> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) {
        final Collection<Double> doubles =
            Collections2.transform(data,
                                   new Function<Float, Double> () {
                                       @Override
                                       @Nonnull
                                       public Double apply(@Nonnull final Float from) {
                                           return Double.valueOf(from);
                                       }
                                   });
        return ValueFactory.createDoubleValue(BaseTypeConversionSupport.toTimestamp(timestamp),
                                              EpicsIValueTypeSupport.toSeverity(alarm.getSeverity()),
                                              alarm.getStatus().toString(),
                                              null,
                                              null,
                                              Doubles.toArray(doubles));
    }

    @Override
    @Nonnull
    protected IValue convertToIMinMaxDoubleValue(@Nonnull final IAlarmSystemVariable<Float> sysVar,
                                                 @Nonnull final Float min,
                                                 @Nonnull final Float max) throws TypeSupportException {
        return createMinMaxDoubleValueFromNumber(sysVar.getTimestamp(),
                                                 (EpicsAlarm) sysVar.getAlarm(),
                                                 sysVar.getData(),
                                                 min,
                                                 max);
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Nonnull
//    protected EpicsSystemVariable<Collection<Float>> createCollectionEpicsVariable(@Nonnull final String name,
//                                                                                   @Nonnull final Class<?> typeClass,
//                                                                                   @Nonnull final Collection<Float> values,
//                                                                                   @Nonnull final ControlSystem system,
//                                                                                   @Nonnull final TimeInstant timestamp) throws TypeSupportException {
//        try {
//            @SuppressWarnings("unchecked")
//            final Collection<Float> newCollection = (Collection<Float>) typeClass.newInstance();
//            for (final Float v : values) {
//                newCollection.add(v);
//            }
//            return new EpicsSystemVariable<Collection<Float>>(name, newCollection, system, timestamp, EpicsAlarm.UNKNOWN);
//        } catch (final InstantiationException e) {
//            throw new TypeSupportException("Collection type could not be instantiated from Class<?> object.", e);
//        } catch (final IllegalAccessException e) {
//            throw new TypeSupportException("Collection type could not be instantiated from Class<?> object.", e);
//        }
//    }
    @Override
    @Nonnull
    protected VType convertEpicsSystemVariableToVType(@Nonnull final EpicsSystemVariable<Float> sysVar) {
    	return org.epics.vtype.ValueFactory.newVDouble(sysVar.getData().doubleValue(),
				getAlarm(sysVar.getAlarm()),
				getTime(sysVar.getTimestamp()),
				getDisplay(sysVar.getMetaData()));
    }

    @Override
    @Nonnull
    protected VType convertCollectionToVType(@Nonnull final Collection<Float> data,
                                               @Nonnull final EpicsAlarm alarm,
                                               @Nonnull final TimeInstant timestamp) {

    	   final ListFloat l=new ArrayFloat( Floats.toArray(data), true);
		   return  org.epics.vtype.ValueFactory.newVFloatArray( l, getAlarm(alarm), getTime(timestamp), org.epics.vtype.ValueFactory.displayNone());
    }



}
