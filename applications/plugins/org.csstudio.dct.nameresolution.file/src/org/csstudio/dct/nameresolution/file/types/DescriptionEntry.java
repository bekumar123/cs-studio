package org.csstudio.dct.nameresolution.file.types;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class DescriptionEntry {

   private static final String SPS_TYPE = "S7";

   private final TcpConnectionNr tcpConnectionNr;
   private final IoName ioName;
   private final SpsType spsType;
   private final SpsAddress spsAddress;
   private final String fileName;

   //@formatter:off
   public DescriptionEntry(
         final TcpConnectionNr tcpConnectionNr, 
         final IoName ioName, 
         final SpsType spsType, 
         final SpsAddress spsAddress,
         final String fileName) {
         //@formatter:on

      Preconditions.checkNotNull(tcpConnectionNr, "tcpConnectionNr must not be null");
      Preconditions.checkNotNull(ioName, "ioName must not be null");
      Preconditions.checkNotNull(spsType, "spsType must not be null");
      Preconditions.checkNotNull(spsAddress, "spsAddress must not be null");
      Preconditions.checkNotNull(fileName, "fileName must not be null");

      this.ioName = ioName;
      this.spsType = spsType;
      this.spsAddress = spsAddress;
      this.tcpConnectionNr = tcpConnectionNr;
      this.fileName = fileName;

   }

   public IoName getIoName() {
      return ioName;
   }

   public SpsAddress getSpsAddress() {
      return spsAddress;
   }

   public String getFileName() {
      return fileName;
   }

   public EpicsAddress getEcpisAddress() {
      //@formatter:off
      String text = SPS_TYPE + ": " 
            + tcpConnectionNr.toString() + "/" + spsAddress.toString() 
            + " T=" + spsType.getTypeName();
            //@formatter:on
      if (spsType == SpsType.BOOL) {
         Optional<Integer> bitPos = spsAddress.getBitPos();
         int bit;
         if (bitPos.isPresent()) {
            bit = bitPos.get();
         } else {
            throw new IllegalStateException("BOOLEAN but bit position is not set: " + ioName.getIoName());
         }
         text = text + " B=" + bit;
      }
      return new EpicsAddress(text);
   }

   public String toString() {
      return getEcpisAddress().getAddress();
   }

}
