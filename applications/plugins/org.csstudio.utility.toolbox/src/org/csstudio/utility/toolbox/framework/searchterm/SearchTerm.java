package org.csstudio.utility.toolbox.framework.searchterm;

import java.util.Locale;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;

public class SearchTerm {

   private Property property;

   private String value;

   private final SearchTermType searchTermType;

   private final String operator;;

   private String prefix = "";

   public SearchTerm(Property property, String value, SearchTermType searchTermType) {
      
      Validate.notNull(property, "property must not be null");
      Validate.notNull(searchTermType, "searchTermType must not be null");
      
      this.property = property;
      this.value = value;
      this.searchTermType = searchTermType;
      this.operator = " = ";
   }

   public SearchTerm(Property property, String value, SearchTermType searchTermType, String operator) {

      Validate.notNull(property, "property must not be null");
      Validate.notNull(searchTermType, "searchTermType must not be null");
      Validate.notNull(operator, "operator must not be null");

      this.property = property;
      this.value = value;
      this.searchTermType = searchTermType;
      this.operator = " " + operator + " ";
   }

   public Property getProperty() {
      return property;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setProperty(Property property) {
      this.property = property;
   }

   public SearchTermType getSearchTermType() {
      return searchTermType;
   }

   public boolean hasValue() {
      if (value == null) {
         return false;
      }
      if (value.isEmpty()) {
         return false;
      }
      return true;
   }

   public String asJpaTerm(String alias) {
      
      Validate.notNull(alias, "alias must not be null");
      
      if (searchTermType == SearchTermType.STRING) {
         return "Upper(" + getPropertyPath(alias) + ") LIKE '%" + value.toUpperCase(Locale.getDefault()) + "%'";
      } else if (searchTermType == SearchTermType.STRING_SEARCH_EXACT) {
         return getPropertyPath(alias) + operator + "'" + value + "'";
      } else if (searchTermType == SearchTermType.DATE) {
         return buildDateTerm(alias);
      } else {
         return getPropertyPath(alias) + operator + value.toUpperCase(Locale.getDefault());
      }
   }

   private String getPrefix() {
      return prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix + ".";
   }

   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }

   private String buildDateTerm(String alias) {
      String dateTerm = value.replace(" ", "");
      if (dateTerm.startsWith(">")) {
         return getPropertyPath(alias) + " > to_date('" + dateTerm.substring(1) + "', 'dd.mm.yyyy')";
      } else if (dateTerm.startsWith("<")) {
         return getPropertyPath(alias) + " < to_date('" + dateTerm.substring(1) + "', 'dd.mm.yyyy')";
      } else if (dateTerm.contains(",")) {
         String[] parts = dateTerm.split(",");
         return "((" + getPropertyPath(alias) + " >= to_date('" + parts[0] + "', 'dd.mm.yyyy'))" + " and ("
               + getPropertyPath(alias) + " <= to_date('" + parts[1] + "', 'dd.mm.yyyy')))";
      } else {
         return getPropertyPath(alias) + " = to_date('" + dateTerm + "', 'dd.mm.yyyy')";
      }
   }

   private String getPropertyPath(String alias) {
      return alias + "." + getPrefix() + property.getName();
   }

}
