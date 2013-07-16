package org.csstudio.sds.model;

import java.util.Collections;
import java.util.Map;

import org.csstudio.dal.CssApplicationContext;
import org.csstudio.dal.simple.ISimpleDalBroker;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.eclipse.core.runtime.IPath;

/**
 * Collects runtime information for a display.
 * 
 * When a display is opened in run mode, we bind this runtime context to the
 * {@link DisplayModel} to be able to access that information via widget
 * controllers, e.g. to close the current shell on a menu button click.
 * 
 * @author Sven Wende
 * 
 */
public class RuntimeContext {
	private IPath _displayFilePath;
	private Map<String, String> _aliases;
	private RunModeBoxInput _runModeBoxInput;
	private ISimpleDalBroker _broker;

	//TODO CME: when setAliases or setDisplayFilePath are called the state of RunTimeContext and RunModeBoxInput will be inconsistent! 
	//The connection between RuntimeContext and RunModeBoxInput is strange.
	/**
	 * Constructor.
	 * 
	 * @param windowHandle
	 *            a runtime window handle
	 * 
	 * @param displayFilePath
	 *            the path of the opened file at runtime
	 * 
	 * @param aliases
	 *            the runtime aliases
	 */
	public RuntimeContext(RunModeBoxInput input) {
		_displayFilePath = input.getFilePath();
		_aliases = input.getAliases();
		
		switch (input.getDataAccessType()) {
		case REALTIME:
			_broker = SdsPlugin.getDefault().getRealtimeDalBroker(new CssApplicationContext("CSS"));
			break;
		case HISTORY:
			_broker = SdsPlugin.getDefault().getHistoryDalBroker();
		default:
			break;
		}
	}

	public IPath getDisplayFilePath() {
		return _displayFilePath;
	}

	public void setDisplayFilePath(IPath displayFilePath) {
		_displayFilePath = displayFilePath;
	}

	public Map<String, String> getAliases() {
		return Collections.unmodifiableMap(_aliases);
	}

	public void setAliases(Map<String, String> aliases) {
		_aliases = aliases;
	}

	public RunModeBoxInput getRunModeBoxInput() {
		return _runModeBoxInput;
	}

	public void setRunModeBoxInput(RunModeBoxInput runModeBoxInput) {
		_runModeBoxInput = runModeBoxInput;
	}

	public void setBroker(ISimpleDalBroker broker) {
		_broker = broker;
	}

	public ISimpleDalBroker getBroker() {
		return _broker;
	}
}
