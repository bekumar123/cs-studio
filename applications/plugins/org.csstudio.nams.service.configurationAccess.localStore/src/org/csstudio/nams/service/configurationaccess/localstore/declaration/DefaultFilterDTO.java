
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;

@Entity
@DiscriminatorValue("default")
public class DefaultFilterDTO extends FilterDTO implements NewAMSConfigurationElementDTO {

	@Transient
	private List<FilterConditionDTO> filterConditons = new LinkedList<FilterConditionDTO>();

	public List<FilterConditionDTO> getFilterConditions() {
		return this.filterConditons;
	}

	public void setFilterConditions(
			final List<FilterConditionDTO> filterConditonDTOs) {
		this.filterConditons = filterConditonDTOs;
	}

	public boolean isSimpleStringBasedFilter() {
		boolean result = false;

		final List<FilterConditionDTO> filterConditions = this.getFilterConditions();

		if (filterConditions.size() == 1) {
			FilterConditionDTO aFilterConditionDTO = filterConditions.get(0);
			if(aFilterConditionDTO instanceof StringFilterConditionDTO) {
				result = ((StringFilterConditionDTO) aFilterConditionDTO).getOperatorEnum() == StringRegelOperator.OPERATOR_TEXT_EQUAL;
			}
		}

		return result;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		super.loadJoinData(mapper);
		
		final List<FilterConditionsToFilterDTO> joins = mapper.loadAll(
				FilterConditionsToFilterDTO.class, false);
	
		this.filterConditons.clear();
	
		for (final FilterConditionsToFilterDTO join : joins) {
			if (join.getIFilterRef() == this.getIFilterID()) {
				final FilterConditionDTO gefunden = mapper.findForId(
						FilterConditionDTO.class,
						join.getIFilterConditionRef(), true);
				assert gefunden != null : "Es existiert eine FC mit der ID "
						+ join.getIFilterConditionRef();
	
				this.filterConditons.add(gefunden);
			}
		}
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		super.storeJoinLinkData(mapper);
		
		final List<FilterConditionsToFilterDTO> joins = mapper.loadAll(
				FilterConditionsToFilterDTO.class, true);
	
		final List<FilterConditionDTO> ehemalsReferenziert = new LinkedList<FilterConditionDTO>();
	
		for (final FilterConditionsToFilterDTO join : joins) {
			if (join.getIFilterRef() == this.getIFilterID()) {
				final FilterConditionDTO found = mapper.findForId(
						FilterConditionDTO.class,
						join.getIFilterConditionRef(), true);
				ehemalsReferenziert.add(found);
			}
		}
	
		final List<FilterConditionDTO> operands = this.getFilterConditions();
	
		for (final FilterConditionDTO operand : operands) {
			final FilterConditionDTO fc = mapper.findForId(
					FilterConditionDTO.class, operand.getIFilterConditionID(),
					true);
	
			if (fc != null) {
				if (!ehemalsReferenziert.remove(fc)) {
					final FilterConditionsToFilterDTO newJoin = new FilterConditionsToFilterDTO(
							this.getIFilterID(), fc.getIFilterConditionID());
					mapper.save(newJoin);
				}
				if ((operand instanceof JunctorCondForFilterTreeDTO)
						|| (operand instanceof NegationCondForFilterTreeDTO)) {
					((HasManuallyJoinedElements) operand)
							.storeJoinLinkData(mapper);
				}
			} else {
				mapper.save(operand);
				final FilterConditionsToFilterDTO newJoin = new FilterConditionsToFilterDTO(
						this.getIFilterID(), operand.getIFilterConditionID());
				mapper.save(newJoin);
			}
		}
	
		for (final FilterConditionDTO toRemove : ehemalsReferenziert) {
			final FilterConditionsToFilterDTO found = this.findForId(toRemove
					.getIFilterConditionID(), joins);
			mapper.delete(found);
			if (toRemove instanceof JunctorCondForFilterTreeDTO) {
				mapper.delete(toRemove);
			}
			if (toRemove instanceof NegationCondForFilterTreeDTO) {
				mapper.delete(toRemove);
			}
		}
	
	}

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		super.deleteJoinLinkData(mapper);
		
		final List<FilterConditionsToFilterDTO> joins = mapper.loadAll(
				FilterConditionsToFilterDTO.class, true);
	
		for (final FilterConditionsToFilterDTO fctf : joins) {
			if (fctf.getIFilterRef() == this.getIFilterID()) {
				mapper.delete(fctf);
			}
		}
	
		for (final FilterConditionDTO condition : this.getFilterConditions()) {
			if ((condition instanceof JunctorCondForFilterTreeDTO)
					|| (condition instanceof NegationCondForFilterTreeDTO)) {
				final FilterConditionDTO foundFC = mapper.findForId(
						FilterConditionDTO.class, condition
								.getIFilterConditionID(), true);
				((HasManuallyJoinedElements) foundFC)
						.deleteJoinLinkData(mapper);
	
				mapper.delete(foundFC);
			}
		}
	
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
