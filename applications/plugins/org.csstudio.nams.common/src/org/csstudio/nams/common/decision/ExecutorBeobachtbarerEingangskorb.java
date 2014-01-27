/* 
 * Copyright (c) 2011 C1 WPS mbH, 
 * HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */

package org.csstudio.nams.common.decision;

import java.util.concurrent.Executor;

import org.csstudio.nams.common.SerialExecutor;

public class ExecutorBeobachtbarerEingangskorb<T extends Document> extends DefaultDocumentBox<T> implements ObservableInbox<T> {

	private final SerialExecutor executor;
	private InboxObserver beobachter;

	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (beobachter != null) {
				beobachter.onNewDocument();
			}
		}
	};
	
	public ExecutorBeobachtbarerEingangskorb(Executor executor) {
		this.executor = new SerialExecutor(executor);
	}
	
	public void put(T dokument) throws InterruptedException {		
		super.put(dokument);
		
		if (beobachter != null) {
			executor.execute(runnable);
		}
		
	};
	
	@Override
	public void setObserver(InboxObserver beobachter) {
		this.beobachter = beobachter;
	}
	
	@Override
	public int documentCount() {
		return executor.getQueueSize();
	}
}
