
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
@DiscriminatorValue("watchdog")
@SecondaryTable(name="AMS_FILTER_WATCHDOG", pkJoinColumns=@PrimaryKeyJoinColumn(name="IFILTERREF", referencedColumnName="IFILTERID"))
public class WatchDogFilterDTO extends FilterDTO implements NewAMSConfigurationElementDTO {

	@Column(name = "iTimeout", nullable = false, table="AMS_FILTER_WATCHDOG")
	private int timeout = 10; // INT default -1 NOT NULL,
	
	@JoinColumn(name = "IFILTERCONDITIONREF", referencedColumnName="IFILTERCONDITIONREF", nullable = false, table="AMS_FILTER_WATCHDOG")
	@OneToOne(cascade= CascadeType.ALL)
	private JunctorCondForFilterTreeDTO filterCondition = new JunctorCondForFilterTreeDTO();
	
	public WatchDogFilterDTO() {
		this.filterCondition.setOperator(JunctorConditionType.AND);
	}
	
	public WatchDogFilterDTO(int id) {
		this();
		this.setIFilterID(id);
	}
	
	public JunctorCondForFilterTreeDTO getFilterCondition() {
		return filterCondition;
	}
	
	public void setFilterCondition(
			JunctorCondForFilterTreeDTO filterCondition) {
		this.filterCondition = filterCondition;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public void loadJoinData(final Mapper mapper) throws Throwable {
		super.loadJoinData(mapper);
	}

	@Override
	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		super.storeJoinLinkData(mapper);
		
		filterCondition.storeJoinLinkData(mapper);
	}

	@Override
	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		super.deleteJoinLinkData(mapper);
		
		filterCondition.deleteJoinLinkData(mapper);
	
	}
}
