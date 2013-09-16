
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCondition_PropertyCompare.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 *  create table AMS_FilterCond_PropCompare
 *  (
 *  iFilterConditionRef	INT NOT NULL,
 *  cMessageKeyValue		VARCHAR(16),
 *  sOperator		SMALLINT,
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_FilterCond_PropCompare")
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
public class PropertyCompareFilterConditionDTO extends FilterConditionDTO {

	@Column(name = "cMessageKeyValue", length = 16)
	private String messageKeyValue;

	@Column(name = "sOperator")
	private short operator;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PropertyCompareFilterConditionDTO)) {
			return false;
		}
		final PropertyCompareFilterConditionDTO other = (PropertyCompareFilterConditionDTO) obj;
		if (this.messageKeyValue == null) {
			if (other.messageKeyValue != null) {
				return false;
			}
		} else if (!this.messageKeyValue.equals(other.messageKeyValue)) {
			return false;
		}
		if (this.operator != other.operator) {
			return false;
		}
		return true;
	}

	public MessageKeyEnum getMessageKeyValueEnum() {
		final MessageKeyEnum valueOf = MessageKeyEnum.getEnumFor(this.messageKeyValue);
		return valueOf;
	}

	public StringRegelOperator getOperatorEnum() {
		return StringRegelOperator.valueOf(this.operator);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.messageKeyValue == null) ? 0 : this.messageKeyValue.hashCode());
		result = prime * result + this.operator;
		return result;
	}

	public void setMessageKeyValue(final MessageKeyEnum messageKeyValue) {
		Contract.requireNotNull("messageKeyValue", messageKeyValue);

		this.setMessageKeyValue(messageKeyValue.getStringValue());
	}

	/**
	 * TODO Rename to sth. like setStringOperator
	 */
	public void setOperatorEnum(final StringRegelOperator op) {
		this.setOperator(op.databaseValue());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(super.toString());
		builder.append(" + message key: ");
		builder.append(this.messageKeyValue);
		builder.append(", operator: ");
		builder.append(this.operator);
		return builder.toString();
	}

	/**
	 * @param keyValue
	 *            the keyValue to set
	 */
	protected void setMessageKeyValue(final String messageKeyValue) {
		this.messageKeyValue = messageKeyValue;
	}

	/**
	 * @return the keyValue
	 */
	@SuppressWarnings("unused")
	private String getMessageKeyValue() {
		return this.messageKeyValue;
	}

	/**
	 * @return the operator
	 */
	@SuppressWarnings("unused")
	private short getOperator() {
		return this.operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	private void setOperator(final short operator) {
		this.operator = operator;
	}

}
