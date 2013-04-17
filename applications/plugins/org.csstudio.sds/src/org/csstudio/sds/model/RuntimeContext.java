package org.csstudio.sds.model;

import java.util.Collections;
import java.util.Map;

import org.csstudio.dal.simple.ISimpleDalBroker;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.eclipse.core.runtime.IPath;

import de.c1wps.geneal.desy.service.common.tracker.IGenericServiceListener;

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
public class RuntimeContext implements IGenericServiceListener<ISimpleDalBroker>{
	private IPath _displayFilePath;
	private Map<String, String> _aliases;
	private RunModeBoxInput _runModeBoxInput;
	private ISimpleDalBroker _broker;

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
	public RuntimeContext(IPath displayFilePath, Map<String, String> aliases) {
		_displayFilePath = displayFilePath;
		_aliases = aliases;
		
		SdsPlugin.getDefault().addDalBrokerListener(this);
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

	@Override
	public void bindService(ISimpleDalBroker service) {
		System.err.println("------- bind dalbroker im runtimeContext");
		_broker = service;
	}

	@Override
	public void unbindService(ISimpleDalBroker service) {
		_broker = null;
	}
}
