package org.csstudio.dct.nameresolution.file.types;

import org.csstudio.dct.nameresolution.file.parser.Constant;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class SpsAddress {

   private final Integer address;

   private final Optional<Integer> bitPos;

   public SpsAddress(Integer address) {
      Preconditions.checkNotNull(address, "address must not be null");
      Preconditions.checkArgument(address.intValue() >= 0, "address must be >= 0");
      this.address = address;
      this.bitPos = Optional.absent();
   }

   public SpsAddress(Integer address, Integer bitPos) {
      Preconditions.checkNotNull(address, "address must not be null");
      Preconditions.checkNotNull(bitPos, "bitPos must not be null");
      Preconditions.checkArgument(address.intValue() >= 0, "address must be >= 0");
      Preconditions.checkArgument(bitPos.intValue() >= 0, "bitPos must be >= 0");
      Preconditions.checkArgument(bitPos.intValue() <= Constant.MAX_BIT, "bitPos must be <= 7");
      this.address = address;
      this.bitPos = Optional.of(bitPos);
   }

   public Integer getAddress() {
      return address;
   }

   public Optional<Integer> getBitPos() {
      return bitPos;
   }

   public String toString() {
      return address.toString();
   }

}
