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
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.wam.Material;

@Material
public class Vorgangsmappe implements Ablagefaehig {

	private final AlarmNachricht alarmNachricht;
	private final Vorgangsmappenkennung kennung;
	private Vorgangsmappenkennung abgeschlossenDurchMappenkennung;
	private boolean abgeschlossenDurchTimeOut = false;
	private WeiteresVersandVorgehen weiteresVersandVorgehen;
	private Regelwerkskennung bearbeitetMitRegelWerk;

	public Vorgangsmappe(final Vorgangsmappenkennung kennung, final AlarmNachricht nachricht) {
		Contract.requireNotNull("nachricht", nachricht);

		this.alarmNachricht = nachricht;
		this.kennung = kennung;
		this.weiteresVersandVorgehen = WeiteresVersandVorgehen.NOCH_NICHT_GEPRUEFT;
		this.bearbeitetMitRegelWerk = null;
	}

	public void abgeschlossenDurchTimeOut() {
		this.abgeschlossenDurchMappenkennung = this.kennung;
		this.abgeschlossenDurchTimeOut = true;
	}
	
	public void setBearbeitetMitRegelWerk(Regelwerkskennung bearbeitetMitRegelWerk) {
		this.bearbeitetMitRegelWerk = bearbeitetMitRegelWerk;
	}
	
	public Regelwerkskennung getBearbeitetMitRegelWerk() {
		return bearbeitetMitRegelWerk;
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
		final Vorgangsmappe other = (Vorgangsmappe) obj;
		if (!this.alarmNachricht.equals(other.alarmNachricht)) {
			return false;
		}
		if (!this.kennung.equals(other.kennung)) {
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

	public Vorgangsmappe erstelleKopieFuer(final String bearbeiter) {
		return new Vorgangsmappe(Vorgangsmappenkennung.valueOf(this.kennung, bearbeiter), this.alarmNachricht.clone());
	}
	
	public WeiteresVersandVorgehen getWeiteresVersandVorgehen() {
		return weiteresVersandVorgehen;
	}
	
	public void setWeiteresVersandVorgehen(WeiteresVersandVorgehen weiteresVersandVorgehen) {
		this.weiteresVersandVorgehen = weiteresVersandVorgehen;
	}

	public Vorgangsmappenkennung gibAbschliessendeMappenkennung() {
		return this.abgeschlossenDurchMappenkennung;
	}

	public AlarmNachricht getAlarmNachricht() {
		return this.alarmNachricht;
	}

	public Vorgangsmappenkennung gibMappenkennung() {
		return this.kennung;
	}

	public boolean istAbgeschlossen() {
		return (this.abgeschlossenDurchMappenkennung != null);
	}

	public boolean istAbgeschlossenDurchTimeOut() {
		return this.abgeschlossenDurchTimeOut;
	}

	public void pruefungAbgeschlossenDurch(final Vorgangsmappenkennung mappenkennung) {
		this.abgeschlossenDurchMappenkennung = mappenkennung;
	}

	@Override
	public String toString() {
		return this.kennung.toString() + " " + this.alarmNachricht;
	}

	// TODO Ggf. spaeter Kapitel einfuehren für einzelne Bereiche!
}
