
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;

@Entity
@DiscriminatorValue("timebased")
@SecondaryTable(name="AMS_FILTER_TIMEBASED", pkJoinColumns=@PrimaryKeyJoinColumn(name="IFILTERREF", referencedColumnName="IFILTERID"))
public class TimeBasedFilterDTO extends FilterDTO implements NewAMSConfigurationElementDTO {

	@Column(name = "iTimeout", nullable = false, table="AMS_FILTER_TIMEBASED")
	private int timeout = 10; // INT default -1 NOT NULL,
	
	@JoinColumn(name = "ISTARTFILTERCONDITIONREF", referencedColumnName="IFILTERCONDITIONREF", nullable = false, table="AMS_FILTER_TIMEBASED")
	@OneToOne(cascade= CascadeType.ALL)
	private JunctorCondForFilterTreeDTO startFilterCondition = new JunctorCondForFilterTreeDTO();
	
	@JoinColumn(name = "ISTOPFILTERCONDITIONREF", referencedColumnName="IFILTERCONDITIONREF", nullable = false, table="AMS_FILTER_TIMEBASED")
	@OneToOne(cascade= CascadeType.ALL)
	private JunctorCondForFilterTreeDTO stopFilterCondition = new JunctorCondForFilterTreeDTO();

	@Column(name = "iSendOnTimeout", nullable = false, table = "AMS_FILTER_TIMEBASED")
	private boolean sendOnTimeout;
	
	public TimeBasedFilterDTO() {
		this.startFilterCondition.setOperator(JunctorConditionType.AND);
		this.stopFilterCondition.setOperator(JunctorConditionType.AND);
	}
	
	public TimeBasedFilterDTO(int id) {
		this();
		this.setIFilterID(id);
	}
	
	public JunctorCondForFilterTreeDTO getStartFilterCondition() {
		return startFilterCondition;
	}
	
	public void setStartFilterCondition(
			JunctorCondForFilterTreeDTO startFilterCondition) {
		this.startFilterCondition = startFilterCondition;
	}
	
	public JunctorCondForFilterTreeDTO getStopFilterCondition() {
		return stopFilterCondition;
	}
	
	public void setStopFilterCondition(
			JunctorCondForFilterTreeDTO stopFilterCondition) {
		this.stopFilterCondition = stopFilterCondition;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setSendOnTimeout(boolean sendOnTimeout) {
		this.sendOnTimeout = sendOnTimeout;
	}

	public boolean isSendOnTimeout() {
		return sendOnTimeout;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		super.loadJoinData(mapper);
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		super.storeJoinLinkData(mapper);
		
		startFilterCondition.storeJoinLinkData(mapper);
		stopFilterCondition.storeJoinLinkData(mapper);
	}

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		super.deleteJoinLinkData(mapper);
		
		startFilterCondition.deleteJoinLinkData(mapper);
		stopFilterCondition.deleteJoinLinkData(mapper);		
	}
}
