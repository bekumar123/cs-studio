package org.csstudio.nams.common.material.regelwerk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

	public class StringRegelComparator {
		
		private final StringRegelOperator stringRegelOperator;
		private final boolean useWildcards;
		private final SimpleDateFormat amsDateFormat;
		
		public StringRegelComparator(StringRegelOperator operator, boolean useWildcards) {
			stringRegelOperator = operator;
			this.useWildcards = useWildcards;
			this.amsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		}
		
		public boolean compare(String string1, String string2) throws Exception {
			boolean result = false;


				switch (this.stringRegelOperator) {
				
				    // text compare
				    case OPERATOR_TEXT_EQUAL:
				    	if(useWildcards) {
				    		result = this.wildcardStringCompare(string1, string2);
				    	} else {
				    		result = string1.equalsIgnoreCase(string2);
				    	}
				        break;
				    case OPERATOR_TEXT_NOT_EQUAL:
				        result = !this.wildcardStringCompare(string1,
						string2);
				        break;

		            // numeric compare
		            case OPERATOR_NUMERIC_LT:
		                if (!string1.isEmpty()) {
		                    result = this.numericCompare(string1, string2) < 0;
		                } else {
		                    result = false;
		                }
		                break;
		            case OPERATOR_NUMERIC_LT_EQUAL:
		                if (!string1.isEmpty()) {
		                    result = this.numericCompare(string1, string2) <= 0;
		                } else {
		                    result = false;
		                }
		                break;
		            case OPERATOR_NUMERIC_EQUAL:
		                if (!string1.isEmpty()) {
		                    result = this.numericCompare(string1, string2) == 0;
		                } else {
		                    result = false;
		                }
		                break;
		            case OPERATOR_NUMERIC_GT_EQUAL:
		                if (!string1.isEmpty()) {
		                    result = this.numericCompare(string1, string2) >= 0;
		                } else {
		                    result = false;
		                }
		                break;
		            case OPERATOR_NUMERIC_GT:
		                if (!string1.isEmpty()) {
		                    result = this.numericCompare(string1, string2) > 0;
		                } else {
		                    result = false;
		                }
		                break;
		            case OPERATOR_NUMERIC_NOT_EQUAL:
		                if (!string1.isEmpty()) {
		                    result = this.numericCompare(string1, string2) != 0;
		                } else {
		                    result = false;
		                }
		                break;

	    			// time compare
	    			case OPERATOR_TIME_BEFORE:
	    				result = this.timeCompare(string1, string2) < 0;
	    				break;
	    			case OPERATOR_TIME_BEFORE_EQUAL:
	    				result = this.timeCompare(string1, string2) <= 0;
	    				break;
	    			case OPERATOR_TIME_EQUAL:
	    				result = this.timeCompare(string1, string2) == 0;
	    				break;
	    			case OPERATOR_TIME_AFTER_EQUAL:
	    				result = this.timeCompare(string1, string2) >= 0;
	    				break;
	    			case OPERATOR_TIME_AFTER:
	    				result = this.timeCompare(string1, string2) > 0;
	    				break;
	    			case OPERATOR_TIME_NOT_EQUAL:
	    				result = this.timeCompare(string1, string2) != 0;
	    				break;
			}

			return result;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((amsDateFormat == null) ? 0 : amsDateFormat.hashCode());
			result = prime
					* result
					+ ((stringRegelOperator == null) ? 0 : stringRegelOperator
							.hashCode());
			result = prime * result + (useWildcards ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StringRegelComparator other = (StringRegelComparator) obj;
			if (amsDateFormat == null) {
				if (other.amsDateFormat != null)
					return false;
			} else if (!amsDateFormat.equals(other.amsDateFormat))
				return false;
			if (stringRegelOperator != other.stringRegelOperator)
				return false;
			if (useWildcards != other.useWildcards)
				return false;
			return true;
		}

		private boolean wildcardStringCompare(final String value,
				final String wildcardStr) {
			try {
				return Pattern.compile(
						wildcardToRegex(wildcardStr),
						Pattern.CASE_INSENSITIVE).matcher(value).matches();
			} catch (final Exception e) {
				// TODO handle Exception
				return true;
			}
		}
		
		// Search for "*" and "?" wildcards, make Regex conform
		private String wildcardToRegex(final String wildcard) {
			final StringBuffer s = new StringBuffer(wildcard.length());
			s.append('^');

			for (int i = 0, is = wildcard.length(); i < is; i++) {
				final char c = wildcard.charAt(i);

				switch (c) {
				case '*':
					s.append(".*");
					break;
				case '?':
					s.append(".");
					break;
				// escape special regexp-characters
				case '(':
				case ')':
				case '[':
				case ']':
				case '$':
				case '^':
				case '.':
				case '{':
				case '}':
				case '|':
				case '\\':
					s.append("\\");
					s.append(c);
					break;
				default:
					s.append(c);
					break;
				}
			}

			s.append('$');
			return (s.toString());
		}
		
		private int numericCompare(final String value, final String compare)
				throws NumberFormatException {
			final double dVal = Double.parseDouble(value);
			final double dCompVal = Double.parseDouble(compare);

			return Double.compare(dVal, dCompVal);
		}

		private int timeCompare(final String value, final String compare)
				throws ParseException {

		    final Date dateValue = amsDateFormat.parse(value);
		    final Date dateCompValue = amsDateFormat.parse(compare);

			return dateValue.compareTo(dateCompValue);
		}
	}