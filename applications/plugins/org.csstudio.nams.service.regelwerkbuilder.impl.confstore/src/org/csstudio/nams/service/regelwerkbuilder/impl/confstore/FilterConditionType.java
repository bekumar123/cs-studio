
package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVarFiltCondDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.PropertyCompareFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;

public enum FilterConditionType {
	// TODO find a nice place to settle

	// STRING(1),
	// TIMEBASED(2),
	// STRING_ARRAY(3),
	// PV(4),
	// JUNCTOR(5)
	// PROPERTY_COMPARE(6);

	STRING(StringFilterConditionDTO.class), TIMEBASED(
			TimeBasedFilterConditionDTO.class), STRING_ARRAY(
			StringArFilterConditionDTO.class), PV(
			ProcessVarFiltCondDTO.class), JUNCTOR(
			JunctorConditionDTO.class), JUNCTOR_FOR_TREE(
			JunctorCondForFilterTreeDTO.class), NEGATION(
			NegationCondForFilterTreeDTO.class), PROPERTY_COMPARE(PropertyCompareFilterConditionDTO.class);

	public static FilterConditionType valueOf(
			final Class<? extends FilterConditionDTO> clazz) {
		for (final FilterConditionType enumC : FilterConditionType
				.values()) {
			if (enumC._clazz == clazz) {
				return enumC;
			}
		}
		return null;
	}

	private final Class<? extends FilterConditionDTO> _clazz;

	private FilterConditionType(
			final Class<? extends FilterConditionDTO> clazz) {
		// this.id = iD;
		this._clazz = clazz;
	}
}
