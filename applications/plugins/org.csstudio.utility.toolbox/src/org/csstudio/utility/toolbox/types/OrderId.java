package org.csstudio.utility.toolbox.types;

import java.math.BigDecimal;

public class OrderId {

   private final BigDecimal value;

   public OrderId(BigDecimal value) {
      super();
      this.value = value;
   }

   public BigDecimal getValue() {
      return value;
   }

}
