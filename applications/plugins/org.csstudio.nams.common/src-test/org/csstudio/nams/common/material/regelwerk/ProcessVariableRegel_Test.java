package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class ProcessVariableRegel_Test extends
		AbstractTestObject<ProcessVariableRegel> {

	private ConnectionServiceMock _connectionServiceMock;
	private TestLogger logger;

	@Test
	public void testMatchOfDoubleValuesEquals5() throws Throwable {

		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.EQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = 5d;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.0));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Double(5.1));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(5.0));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Double(5.0000001));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfDoubleValuesSmaller5_compValueAsString()
			throws Throwable {

		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.SMALLER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = "5.0";

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(6.0));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Double(5.0));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.9));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfDoubleValuesGreaterThan5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.GREATER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = 5d;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.0));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Double(5.0));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(6.1));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfDoubleValuesSmallerThan5() throws Throwable {

		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.SMALLER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = 5d;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(6.0));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Double(5.0));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.9));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfDoubleValuesUnequals5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.UNEQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = 5d;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(5.0));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Double(5.000001));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.9));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Double(5.1));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfLongValuesEquals5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.EQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		final Object compValue = 5l;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Long(4));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Long(6));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Long(50));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Long(5));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfLongValuesGreaterThan5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.GREATER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		final Object compValue = 5l;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Long(4));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Long(5));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Long(6));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfLongValuesSmallerThan5() throws Throwable {

		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.SMALLER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		final Object compValue = 5l;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Long(6));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Long(5));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Long(4));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfLongValuesUnequals5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.UNEQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		final Object compValue = 5l;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Long(5));
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Long(4));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Long(6));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		this._connectionServiceMock.sendNewValue(new Long(50));
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfStringValueEquals() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.EQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		final Object compValue = "Foo";

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue("NotFoo");
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue("Foo");
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	@Test
	public void testMatchOfStringValueUnequals() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.UNEQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		final Object compValue = "Foo";

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue, logger);

		// Without connection:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue("Foo");
		Assert.assertFalse(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));

		// With matching value:
		this._connectionServiceMock.sendNewValue("NotFoo");
		Assert.assertTrue(pvRegel.pruefeNachricht(new AlarmNachricht("Nachricht")));
	}

	ConnectionServiceMock createPVServiceMock() {
		return new ConnectionServiceMock();
	}

	@Override
	protected ProcessVariableRegel getNewInstanceOfClassUnderTest() {
		final IProcessVariableConnectionService pvService = this
				.createPVServiceMock();
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.EQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		final Object compValue = "test";

		return new ProcessVariableRegel(pvService, channelName, operator,
				suggestedProcessVariableType, compValue, logger);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected ProcessVariableRegel[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final ProcessVariableRegel[] regels = new ProcessVariableRegel[3];
		{
			final IProcessVariableConnectionService pvService = this
					.createPVServiceMock();
			final IProcessVariableAddress channelName = this
					.createDefaultPVAdress();
			final Operator operator = Operator.EQUALS;
			final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
			final Object compValue = "test2";

			regels[0] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue, logger);
		}
		{
			final IProcessVariableConnectionService pvService = this
					.createPVServiceMock();
			final IProcessVariableAddress channelName = this
					.createDefaultPVAdress();
			final Operator operator = Operator.UNEQUALS;
			final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
			final Object compValue = "test";

			regels[1] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue, logger);
		}
		{
			final IProcessVariableConnectionService pvService = this
					.createPVServiceMock();
			final IProcessVariableAddress channelName = this
					.createDefaultPVAdress();
			final Operator operator = Operator.EQUALS;
			final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
			final Object compValue = 42l;

			regels[2] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue, logger);
		}
		return regels;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this._connectionServiceMock = this.createPVServiceMock();
		logger = new TestLogger();
	}

	private class TestLogger implements ILogger {

		@Override
		public void logDebugMessage(Object caller, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logDebugMessage(Object caller, String message,
				Throwable throwable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logErrorMessage(Object caller, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logErrorMessage(Object caller, String message,
				Throwable throwable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logFatalMessage(Object caller, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logFatalMessage(Object caller, String message,
				Throwable throwable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logInfoMessage(Object caller, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logInfoMessage(Object caller, String message,
				Throwable throwable) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logWarningMessage(Object caller, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void logWarningMessage(Object caller, String message,
				Throwable throwable) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private IProcessVariableAddress createDefaultPVAdress() {
		return new IProcessVariableAddress() {

			@Override
            public String getCharacteristic() {
				Assert.fail();
				return null;
			}

			@Override
            public ControlSystemEnum getControlSystem() {
				Assert.fail();
				return null;
			}

			@Override
            public String getDevice() {
				Assert.fail();
				return null;
			}

			@Override
            public String getFullName() {
				Assert.fail();
				return null;
			}

			@Override
            public String getProperty() {
				Assert.fail();
				return null;
			}

			@Override
            public String getRawName() {
				Assert.fail();
				return null;
			}

			@Override
            public ValueType getValueTypeHint() {
				Assert.fail();
				return null;
			}

			@Override
            public boolean isCharacteristic() {
				Assert.fail();
				return false;
			}

			@Override
            public RemoteInfo toDalRemoteInfo() {
				Assert.fail();
				return null;
			}

			@Override
            public IProcessVariableAddress deriveNoCharacteristicPart() {
				Assert.fail();
				return null;
			}

			@Override
            public IProcessVariableAddress deriveCharacteristic(
					String characteristic) {
				Assert.fail();
				return null;
			}
		};
	}

}
