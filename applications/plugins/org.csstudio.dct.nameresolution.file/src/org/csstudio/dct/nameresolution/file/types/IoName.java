package org.csstudio.dct.nameresolution.file.types;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class IoName {

   private String ioName;

   public IoName(String ioName) {
      Preconditions.checkNotNull(ioName, "ioName must not be null");
      Preconditions.checkArgument(!ioName.isEmpty(), "ioName must not be empty");
      this.ioName = ioName;
   }

   public String getIoName() {
      return ioName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.ioName);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final IoName other = (IoName) obj;
      return Objects.equal(this.ioName, other.ioName);
   }

   public boolean isEmpty() {
      return Strings.isNullOrEmpty(ioName);
   }

}
