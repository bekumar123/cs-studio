
/*
 * Copyright (c) C1 WPS mbH, HAMBURG, GERMANY. All Rights Reserved.
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

package org.csstudio.nams.application.department.decision.office.decision;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.fachwert.Milliseconds;

public final class TimerMessage implements Document {

	private final CasefileId casefileId;
	private final Milliseconds timeout;

	private final int recipientWorkerId;

	public static TimerMessage valueOf(
			final CasefileId betreffendeVorgangsmappe,
			final Milliseconds zeitBisZurBenachrichtigung,
			final int idDesZuInformierendenSachbearbeiters) {
		return new TimerMessage(betreffendeVorgangsmappe,
				zeitBisZurBenachrichtigung,
				idDesZuInformierendenSachbearbeiters);
	}

	private TimerMessage(final CasefileId betreffendeVorgangsmappe,
			final Milliseconds zeitBisZurBenachrichtigung,
			final int idDesZuInformierendenSachbearbeiters) {
		Contract.require(betreffendeVorgangsmappe != null,
				"betreffendeVorgangsmappe!=null");
		Contract.require(zeitBisZurBenachrichtigung != null,
				"zeitBisZurBenachrichtigung!=null");
		this.casefileId = betreffendeVorgangsmappe;
		this.timeout = zeitBisZurBenachrichtigung;
		this.recipientWorkerId = idDesZuInformierendenSachbearbeiters;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TimerMessage)) {
			return false;
		}
		final TimerMessage other = (TimerMessage) obj;
		if (!this.casefileId
				.equals(other.casefileId)) {
			return false;
		}
		if (this.recipientWorkerId != other.recipientWorkerId) {
			return false;
		}
		if (!this.timeout
				.equals(other.timeout)) {
			return false;
		}
		return true;
	}

	public int getRecipientWorkerId() {
		return this.recipientWorkerId;
	}

	public CasefileId getCasefileId() {
		return this.casefileId;
	}

	public Milliseconds getTimeout() {
		return this.timeout;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.casefileId.hashCode();
		result = prime * result + recipientWorkerId;
		result = prime * result + this.timeout.hashCode();
		return result;
	}

}
