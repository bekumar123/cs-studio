package org.csstudio.nams.application.department.decision;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractTestObject;

public class ThreadTypesOfDecisionDepartment_Test extends
		AbstractTestObject<ThreadTypesOfDecisionDepartment> {

	public void testNumberOfElements() {
		Assert.assertEquals(4, ThreadTypesOfDecisionDepartment.values().length);
	}

	@Override
	protected ThreadTypesOfDecisionDepartment getNewInstanceOfClassUnderTest() {
		return ThreadTypesOfDecisionDepartment.THREADED_MESSAGE_DISPATCHER;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected ThreadTypesOfDecisionDepartment[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new ThreadTypesOfDecisionDepartment[] {
				ThreadTypesOfDecisionDepartment.AUSGANGSKORBBEARBEITER,
				ThreadTypesOfDecisionDepartment.FILTER_WORKER,
				ThreadTypesOfDecisionDepartment.TERMINASSISTENZ };
	}

}
