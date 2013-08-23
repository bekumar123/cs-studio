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
      return String.valueOf(tcpConnectionNr);
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
