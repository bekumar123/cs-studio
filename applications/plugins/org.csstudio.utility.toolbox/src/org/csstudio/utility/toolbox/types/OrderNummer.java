package org.csstudio.utility.toolbox.types;

import java.math.BigDecimal;

public class OrderNummer {
    
    private final BigDecimal value;

    public OrderNummer(BigDecimal value) {
       super();
       this.value = value;
    }

    public OrderNummer(String value) {
        this(new BigDecimal(value));
     }

    public BigDecimal getValue() {
       return value;
    }
    
}
