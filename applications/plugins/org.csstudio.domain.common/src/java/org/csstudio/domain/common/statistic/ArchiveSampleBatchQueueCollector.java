/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.domain.common.statistic;


/**resc.
 * @author claus
 *
 */
public class ArchiveSampleBatchQueueCollector {
	
	/**dfgdfg.
	 * 
	 */
	private static 	ArchiveSampleBatchQueueCollector 	_thisArchiveSampleBatchQueueCollector = null;
	private Collector	_archiveSampleBatchQueue	= null;
	private Collector	_archiveSample_mBatchQueue	= null;
	private Collector	_archiveSample_hBatchQueue	= null;
	private String applicationName;

	public ArchiveSampleBatchQueueCollector () {
	
        applicationName = "Archive-Batch-Queue_Sample";
      

	}
	
	
	public synchronized static ArchiveSampleBatchQueueCollector getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( _thisArchiveSampleBatchQueueCollector == null) {
			_thisArchiveSampleBatchQueueCollector = new ArchiveSampleBatchQueueCollector();
		}
		return _thisArchiveSampleBatchQueueCollector;
	}
	public final Collector getArchiveBatchQueueApplication() {
		return null;
	}

	public final Collector getArchiveSampleBatchQueueApplication() {
		
		if (_archiveSampleBatchQueue == null) {
	        // CPU used by Application
			_archiveSampleBatchQueue = new Collector();
			_archiveSampleBatchQueue.setApplication(applicationName);
			_archiveSampleBatchQueue.setDescriptor(applicationName+" By WriteSample");
			_archiveSampleBatchQueue.getAlarmHandler().setDeadband(100.0);
			_archiveSampleBatchQueue.getAlarmHandler().setHighAbsoluteLimit(500000);	// Buffer full
		}
		return _archiveSampleBatchQueue;
	}


	public final void setArchiveSampleBatchQueueApplication(final Collector archiveSampleBatchQueue) {
		this._archiveSampleBatchQueue = archiveSampleBatchQueue;
	}


	public final Collector getArchiveSample_mBatchQueueApplication() {
		
		if (_archiveSample_mBatchQueue == null) {
			// CPU used by System
			_archiveSample_mBatchQueue = new Collector();
			_archiveSample_mBatchQueue.setApplication(applicationName+"_m");
			_archiveSample_mBatchQueue.setDescriptor(applicationName+"_m By WriteSample_m");
			_archiveSample_mBatchQueue.getAlarmHandler().setDeadband(10.0);
			_archiveSample_mBatchQueue.getAlarmHandler().setHighAbsoluteLimit(500000);	// Buffer full
		}
		return _archiveSample_mBatchQueue;
	}


	public final void setArchiveSample_mBatchQueueApplication(final Collector archiveSample_mBatchQueue) {
		this._archiveSample_mBatchQueue = archiveSample_mBatchQueue;
	}


	public final Collector getArchiveSample_hBatchQueueApplication() {
		
		if ( _archiveSample_hBatchQueue == null ) {
	        // Memory used by Application
			_archiveSample_hBatchQueue = new Collector();
			_archiveSample_hBatchQueue.setApplication(applicationName+"_h");
			_archiveSample_hBatchQueue.setDescriptor(applicationName+"_h By WriteSample_h");
			_archiveSample_hBatchQueue.getAlarmHandler().setDeadband(10.0);
			_archiveSample_hBatchQueue.getAlarmHandler().setHighAbsoluteLimit(500000.0);	// Buffer full
		}
		return _archiveSample_hBatchQueue;
	}


	public final void setArchiveSample_hBatchQueueApplication(final Collector archiveSample_hBatchQueue) {
		this._archiveSample_hBatchQueue = archiveSample_hBatchQueue;
	}



}
