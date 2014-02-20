package org.csstudio.dal2.simulator.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.dal2.dv.ControlSystemId;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.csstudio.dal2.service.cs.ICsPvAccess;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;
import org.csstudio.dal2.service.cs.ICsResponseListener;
import org.csstudio.dal2.simulator.service.data.RandomLongGenerator;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class SimulatorPvAccessFactory implements ICsPvAccessFactory {

	/**
	 * Countdown generator pattern. The pattern reads the following variables in
	 * the process variable name
	 * <code> local://property COUNTDOWN:{from}:{to}:{period}:{update} </code>,
	 * for example <code>local://abc COUNTDOWN:100:0:10000:200</code> will count
	 * down from 100 to 0 in 10 seconds and an update event will be fired each
	 * 200 ms.
	 */
	private static final Pattern COUNTDOWN_PATTERN = Pattern
			.compile("^.* COUNTDOWN:([0-9]+):([0-9]+):([0-9]+):([0-9]+)$");

	/**
	 * Random number generator pattern. The pattern reads the following
	 * variables in the process variable name
	 * <code> local://property RND:{from}:{to}:{period} </code>, for example
	 * <code>local://abc RND:1:100:10</code> which creates random numbers
	 * between 1 and 100 every 10 milliseconds.
	 */
	private static final Pattern RANDOM_NUMBER_PATTERN = Pattern
			.compile("^.* RND:([0-9]+):([0-9]+):([0-9]+)$");

	private static final Pattern MEMORIZED_PATTERN = Pattern.compile("^.*");

	private static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(
			5, new ThreadFactoryBuilder().setNameFormat("dal2-simulator-%d")
					.build());

	@Override
	public ControlSystemId getControlSystemId() {
		return ControlSystemId.SIMULATOR;
	}

	@Override
	public <T> ICsPvAccess<T> createPVAccess(PvAddress pv, Type<T> type) {

		String address = pv.getAddress();
		Matcher matcher;
		
		Type type2 = type;
		if (type.equals(Type.NATIVE)) {
			type2 = Type.LONG;
		}

		matcher = RANDOM_NUMBER_PATTERN.matcher(address);
		if (matcher.matches()) {
			String[] options = { matcher.group(1), matcher.group(2) };
			ValueProvider<T> valueProvider = createRandomGenerator(type2,
					options);
			long period = Long.parseLong(matcher.group(3));
			return new SimulatedPvAccess<T>(pv, type2, valueProvider, EXECUTOR,
					period);
		} else {
			// TODO add other patterns
			throw new IllegalArgumentException("Unsupported PV: " + address);
		}
	}

	/**
	 * @param type
	 * @param options
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> ValueProvider<T> createRandomGenerator(Type<T> type,
			String[] options) {
		if (Type.LONG.equals(type)) {
			return (ValueProvider<T>) new RandomLongGenerator(options);
		} else {
			throw new IllegalArgumentException(
					"Type unsupported by simulator for random value generation: "
							+ type);
		}
	}

	@Override
	public ICsOperationHandle requestNativeType(PvAddress pv,
			final ICsResponseListener<Type<?>> callback) throws DalException {
		ScheduledFuture<?> future = EXECUTOR.schedule(new Runnable() {
			@Override
			public void run() {
				callback.onSuccess(Type.DOUBLE);
			}
		}, 50, TimeUnit.MILLISECONDS);
		return new OperationHandle(future);
	}

}
