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

	private final AlarmMessage alarmNachricht;
	private final CasefileId id;
	private CasefileId closedByFileId;
	private boolean isClosedByTimeout = false;
	private FilterId handledByFilter;

	public MessageCasefile(final CasefileId kennung, final AlarmMessage nachricht) {
		Contract.requireNotNull("nachricht", nachricht);

		this.alarmNachricht = nachricht;
		this.id = kennung;
		this.handledByFilter = null;
	}

	public void abgeschlossenDurchTimeOut() {
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
		if (!this.alarmNachricht.equals(other.alarmNachricht)) {
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
		result = prime * result + this.alarmNachricht.hashCode();
		return result;
	}

	public MessageCasefile erstelleKopieFuer(final String bearbeiter) {
		return new MessageCasefile(CasefileId.valueOf(this.id, bearbeiter), this.alarmNachricht.clone());
	}
	
	public CasefileId gibAbschliessendeMappenkennung() {
		return this.closedByFileId;
	}

	public AlarmMessage getAlarmMessage() {
		return this.alarmNachricht;
	}

	public CasefileId getCasefileId() {
		return this.id;
	}

	public boolean istAbgeschlossen() {
		return (this.closedByFileId != null);
	}

	public boolean istAbgeschlossenDurchTimeOut() {
		return this.isClosedByTimeout;
	}

	public void pruefungAbgeschlossenDurch(final CasefileId mappenkennung) {
		this.closedByFileId = mappenkennung;
	}

	@Override
	public String toString() {
		return this.id.toString() + " " + this.alarmNachricht;
	}

	// TODO Ggf. spaeter Kapitel einfuehren für einzelne Bereiche!
}
