/* 
 * Copyright (c) 2008 C1 WPS mbH, 
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

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.wam.Material;

@Material
public class MessageCasefile implements Document {

	private final AlarmMessage alarmMessage;
	private final CasefileId id;
	private CasefileId closedByFileId;
	private boolean isClosedByTimeout = false;
	private FilterId handledByFilter;

	public MessageCasefile(final CasefileId id, final AlarmMessage message) {
		Contract.requireNotNull("message", message);

		this.alarmMessage = message;
		this.id = id;
		this.handledByFilter = null;
	}

	public void closeWithTimeOut() {
		this.closedByFileId = this.id;
		this.isClosedByTimeout = true;
	}
	
	public void setHandledWithFilter(FilterId bearbeitetMitRegelWerk) {
		this.handledByFilter = bearbeitetMitRegelWerk;
	}
	
	public FilterId getHandledByFilterId() {
		return handledByFilter;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!this.getClass().isAssignableFrom(obj.getClass())) {
			return false;
		}
		final MessageCasefile other = (MessageCasefile) obj;
		if (!this.alarmMessage.equals(other.alarmMessage)) {
			return false;
		}
		if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.alarmMessage.hashCode();
		return result;
	}

	public MessageCasefile getCopyFor(final String bearbeiter) {
		return new MessageCasefile(CasefileId.valueOf(this.id, bearbeiter), this.alarmMessage.clone());
	}
	
	public CasefileId getClosedByFileId() {
		return this.closedByFileId;
	}

	public AlarmMessage getAlarmMessage() {
		return this.alarmMessage;
	}

	public CasefileId getCasefileId() {
		return this.id;
	}

	public boolean isClosed() {
		return (this.closedByFileId != null);
	}

	public boolean isClosedByTimeout() {
		return this.isClosedByTimeout;
	}

	public void closeWithFileId(final CasefileId casefileId) {
		this.closedByFileId = casefileId;
	}

	@Override
	public String toString() {
		return this.id.toString() + " " + this.alarmMessage;
	}
}
