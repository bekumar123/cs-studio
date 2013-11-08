package org.csstudio.alarm.service;

import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;
import org.csstudio.servicelocator.ServiceLocator;

public class GetStatusCommand implements IManagementCommand {

	@Override
	public CommandResult execute(CommandParameters parameters) {
		final String result = ServiceLocator.getService(IAlarmConnection.class).getStatusAsString();
		return CommandResult.createMessageResult(result);
	}

}
