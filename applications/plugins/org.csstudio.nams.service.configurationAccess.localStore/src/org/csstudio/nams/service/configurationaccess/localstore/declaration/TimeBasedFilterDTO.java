
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;

@Entity
@DiscriminatorValue("timebased")
@SecondaryTable(name="AMS_FILTER_TIMEBASED", pkJoinColumns=@PrimaryKeyJoinColumn(name="IFILTERREF", referencedColumnName="IFILTERID"))
public class TimeBasedFilterDTO extends FilterDTO implements NewAMSConfigurationElementDTO {

	@Column(name = "iTimeout", nullable = false, table="AMS_FILTER_TIMEBASED")
	private int timeout = 10000; // INT default -1 NOT NULL,
	
	@JoinColumn(name = "ISTARTFILTERCONDITIONREF", referencedColumnName="IFILTERCONDITIONREF", nullable = false, table="AMS_FILTER_TIMEBASED")
	@OneToOne(cascade= CascadeType.ALL)
	private JunctorCondForFilterTreeDTO startFilterCondition = new JunctorCondForFilterTreeDTO();
	
	@JoinColumn(name = "ISTOPFILTERCONDITIONREF", referencedColumnName="IFILTERCONDITIONREF", nullable = false, table="AMS_FILTER_TIMEBASED")
	@OneToOne(cascade= CascadeType.ALL)
	private JunctorCondForFilterTreeDTO stopFilterCondition = new JunctorCondForFilterTreeDTO();
	
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

	public boolean isSimpleStringBasedFilter() {
		return false;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		super.loadJoinData(mapper);
//		
//		final List<FilterConditionsToFilterDTO> joins = mapper.loadAll(
//				FilterConditionsToFilterDTO.class, false);
//	
//		this.startFilterConditons.clear();
//		this.stopFilterConditons.clear();
//	
//		for (final FilterConditionsToFilterDTO join : joins) {
//			if (join.getIFilterRef() == this.getIFilterID()) {
//				final FilterConditionDTO gefunden = mapper.findForId(
//						FilterConditionDTO.class,
//						join.getIFilterConditionRef(), true);
//				assert gefunden != null : "Es existiert eine FC mit der ID "
//						+ join.getIFilterConditionRef();
//	
//				this.filterConditons.add(gefunden);
//			}
//		}
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		super.storeJoinLinkData(mapper);
		
		startFilterCondition.storeJoinLinkData(mapper);
		stopFilterCondition.storeJoinLinkData(mapper);
		
//		List<JunctorCondForFilterTreeDTO> operands = Arrays.asList(startFilterCondition, stopFilterCondition);
//		
//		for (final JunctorCondForFilterTreeDTO operand : operands) {
//			final FilterConditionDTO fc = mapper.findForId(
//					JunctorCondForFilterTreeDTO.class, operand.getIFilterConditionID(),
//					true);
//	
//			if (fc != null) {
//				((HasManuallyJoinedElements) operand).storeJoinLinkData(mapper);
//			} else {
//				mapper.save(operand);
//			}
//		}
	}

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		super.deleteJoinLinkData(mapper);
		
		startFilterCondition.deleteJoinLinkData(mapper);
		stopFilterCondition.deleteJoinLinkData(mapper);
		
//		final List<FilterConditionsToFilterDTO> joins = mapper.loadAll(
//				FilterConditionsToFilterDTO.class, true);
//	
//		for (final FilterConditionsToFilterDTO fctf : joins) {
//			if (fctf.getIFilterRef() == this.getIFilterID()) {
//				mapper.delete(fctf);
//			}
//		}
//	
//		for (final FilterConditionDTO condition : this.getFilterConditions()) {
//			if ((condition instanceof JunctorCondForFilterTreeDTO)
//					|| (condition instanceof NegationCondForFilterTreeDTO)) {
//				final FilterConditionDTO foundFC = mapper.findForId(
//						FilterConditionDTO.class, condition
//								.getIFilterConditionID(), true);
//				((HasManuallyJoinedElements) foundFC)
//						.deleteJoinLinkData(mapper);
//	
//				mapper.delete(foundFC);
//			}
//		}
	
	}

	private FilterConditionsToFilterDTO findForId(final int id,
			final Collection<FilterConditionsToFilterDTO> fcs) {
		for (final FilterConditionsToFilterDTO t : fcs) {
			if (t.getIFilterRef() == this.getIFilterID() && t.getIFilterConditionRef() == id) {
				return t;
			}
		}
		return null;
	}
}
