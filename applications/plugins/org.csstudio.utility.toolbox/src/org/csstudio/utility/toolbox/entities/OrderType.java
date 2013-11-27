package org.csstudio.utility.toolbox.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Entity
@NamedQueries({ @NamedQuery(name = OrderType.FIND_ALL, query = "select l from OrderType l order by l.id"),
      @NamedQuery(name = OrderType.FIND_BY_TEXT, query = "select l from OrderType l where text = :text") })
@Table(name = "order_type")
public class OrderType implements TextValue {

   public static final String FIND_ALL = "OrderType.findAll";

   public static final String FIND_BY_TEXT = "OrderType.findByText";

   @Id
   private BigDecimal id;

   private String text;

   public BigDecimal getId() {
      return id;
   }

   public String getText() {
      return text;
   }

   @Override
   public String getValue() {
      return text;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return new HashCodeBuilder(17, 37).append(id).toHashCode();
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (obj == this) {
         return true;
      }
      if (obj.getClass() != getClass()) {
         return false;
      }

      OrderType rhs = (OrderType) obj;

      return new EqualsBuilder().append(id, rhs.id).isEquals();
   }

}
