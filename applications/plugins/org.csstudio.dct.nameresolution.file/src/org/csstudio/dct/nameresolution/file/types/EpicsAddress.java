package org.csstudio.dct.nameresolution.file.types;

import com.google.common.base.Preconditions;

public class EpicsAddress {

   private final String address;

   public EpicsAddress(final String address) {
      Preconditions.checkNotNull(address, "address must not be null");
      Preconditions.checkArgument(!address.isEmpty(), "address must not be empty");
      this.address = address;
   }

   public String getAddress() {
      return address;
   }

}
