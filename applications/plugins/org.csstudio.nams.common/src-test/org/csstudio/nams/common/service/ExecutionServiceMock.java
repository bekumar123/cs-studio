package org.csstudio.nams.common.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

/**
 * Ein Mock-Execution service, das ausführen erfolgt synchron und manuell.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is in draft state
 */
public class ExecutionServiceMock implements ExecutionService {

	// return errors; Marek: -2602

	private final Map<Enum<?>, List<StepByStepProcessor>> allStepByStepProcessors = new HashMap<Enum<?>, List<StepByStepProcessor>>();

	public <GT extends Enum<?> & ThreadType> void executeAsynchronously(
			final GT groupId, final StepByStepProcessor runnable) {
		// System.out.println("ExecutionServiceMock.executeAsynchronsly():
		// "+groupId+ ", time: "+System.nanoTime()+", all:
		// "+allStepByStepProcessors.toString());

		// for (Enum<?> enuum :allStepByStepProcessors.keySet()) {
		// System.out.println(enuum.toString() + " = " + groupId.toString() + "
		// is " + (enuum == groupId));
		// }

		final List<StepByStepProcessor> list = this.allStepByStepProcessors
				.get(groupId);
		if (list == null) {
			// System.out.println("ExecutionServiceMock.executeAsynchronsly():
			// "+groupId+ ", time: "+System.nanoTime()+", all:
			// "+allStepByStepProcessors.toString());
			Assert.fail("group not registered: " + groupId);
		}
		list.add(runnable);
		this.allStepByStepProcessors.put(groupId, list);
	}

	public <GT extends Enum<?> & ThreadType> Iterable<GT> getCurrentlyUsedGroupIds() {
		Assert.fail("unexpected method call");
		return null;
	}

	public <GT extends Enum<?> & ThreadType> ThreadGroup getRegisteredGroup(
			final GT groupId) {
		Assert.fail("unexpected method call");
		return null;
	}

	public <GT extends Enum<?> & ThreadType> Iterable<StepByStepProcessor> getRunnablesOfGroupId(
			final GT groupId) {
		List<StepByStepProcessor> list = this.allStepByStepProcessors
				.get(groupId);
		if (list == null) {
			list = Collections.emptyList();
		}
		return list;
	}

	public <GT extends Enum<?> & ThreadType> boolean hasGroupRegistered(
			final GT groupId) {
		return this.allStepByStepProcessors.keySet().contains(groupId);
	}

	public <GT extends Enum<?> & ThreadType> void mockExecuteOneStepOf(
			final GT groupId) throws Throwable {
		final List<StepByStepProcessor> list = this.allStepByStepProcessors
				.get(groupId);
		if (list == null) {
			Assert.fail("group not registered.");
		}
		for (final StepByStepProcessor stepByStepProcessor : list) {
			stepByStepProcessor.doRunOneSingleStep();
		}
	}

	public <GT extends Enum<?> & ThreadType> void registerGroup(
			final GT groupId, final ThreadGroup group) {
		this.allStepByStepProcessors.put(groupId,
				new LinkedList<StepByStepProcessor>());
		// System.out.println("ExecutionServiceMock.registerGroup(): "+groupId+
		// ", time: "+System.nanoTime()+", all:
		// "+allStepByStepProcessors.toString());
	}
}
