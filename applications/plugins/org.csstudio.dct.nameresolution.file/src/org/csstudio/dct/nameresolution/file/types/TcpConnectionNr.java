package org.csstudio.dct.nameresolution.file.types;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class TcpConnectionNr {

    private final Integer tcpConnectionNr;

    public TcpConnectionNr(int tcpConnectionNr) {
        Preconditions.checkArgument(tcpConnectionNr >= 0, "tcpConnectionNr must be >= 0");
        this.tcpConnectionNr = tcpConnectionNr;
    }

    public int getTcpConnectionNr() {
        return tcpConnectionNr;
    }

    public String toString() {
    	// 2013-11-11: JP grausamer Hack eingebaut
    	// tcpConnectionNr wird modulo 10 ausgegeben
    	// Damit wird folgendes erreicht: Die IP-Configuration erlaubt nur eine Richtung (write oder read) je TCP-Verbindung.
    	// Um beides zu ermöglichen, wird write zb. '3' und read '33' bezeichnet, hier wird read wieder auf '3' zurück gebogen.
    	// Das muss geändert werden, indem die IP-Configuration erweitert wird.
    	
        return String.valueOf(tcpConnectionNr % 10);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.tcpConnectionNr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TcpConnectionNr other = (TcpConnectionNr) obj;
        return Objects.equal(this.tcpConnectionNr, other.tcpConnectionNr);
    }

}
