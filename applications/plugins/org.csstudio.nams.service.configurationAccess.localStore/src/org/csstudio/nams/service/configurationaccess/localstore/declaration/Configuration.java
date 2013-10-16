
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StrgArFiltCondCompValDTO;
import org.csstudio.nams.service.logging.declaration.ILogger;

@Entity
public class Configuration {
	@SuppressWarnings("unused")
	private static ILogger _logger;

	public static void staticInject(final ILogger logger) {
		Configuration._logger = logger;
	}

	private final Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	private final Collection<TopicDTO> alleAlarmtopics;
	private final Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;

	private final Collection<DefaultFilterDTO> allDefaultFilters;
	private final Collection<TimeBasedFilterDTO> allTimebasedFilters;
	private final Collection<WatchDogFilterDTO> allWatchDogFilters;
	private final Collection<FilterConditionsToFilterDTO> allFilterConditionMappings;
	private final Collection<FilterConditionDTO> allFilterConditions;
	private final Collection<RubrikDTO> alleRubriken;
	private final List<User2UserGroupDTO> alleUser2UserGroupMappings;
	private final Collection<StrgArFiltCondCompValDTO> allCompareValues;

	private final Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO;

	public Configuration(
			final Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter,
			final Collection<TopicDTO> alleAlarmtopics,
			final Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen,
			final Collection<FilterDTO> allFilters,
			final Collection<FilterConditionDTO> allFilterConditions,
			final Collection<RubrikDTO> alleRubriken,
			final Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO) {
		super();
		this.alleAlarmbarbeiter = (alleAlarmbarbeiter == null) ? new ArrayList<AlarmbearbeiterDTO>() : alleAlarmbarbeiter;
		this.alleAlarmtopics = (alleAlarmtopics == null) ? new ArrayList<TopicDTO>() : alleAlarmtopics;
		this.alleAlarmbearbeiterGruppen = (alleAlarmbearbeiterGruppen == null) ? new ArrayList<AlarmbearbeiterGruppenDTO>() : alleAlarmbearbeiterGruppen;
		this.allDefaultFilters = new ArrayList<DefaultFilterDTO>();
		this.allTimebasedFilters = new ArrayList<TimeBasedFilterDTO>();
		this.allWatchDogFilters = new ArrayList<WatchDogFilterDTO>();
		for (FilterDTO filterDTO : allFilters) {
			if(filterDTO instanceof DefaultFilterDTO) {
				this.allDefaultFilters.add((DefaultFilterDTO) filterDTO);
			}
			else if(filterDTO instanceof TimeBasedFilterDTO) {
				this.allTimebasedFilters.add((TimeBasedFilterDTO) filterDTO);
			}
			else if(filterDTO instanceof WatchDogFilterDTO) {
				this.allWatchDogFilters.add((WatchDogFilterDTO) filterDTO);
			}
		}
		this.allFilterConditionMappings = new LinkedList<FilterConditionsToFilterDTO>();
		this.allFilterConditions = (allFilterConditions == null) ? new ArrayList<FilterConditionDTO>() : allFilterConditions;
		this.alleRubriken = (alleRubriken == null) ? new ArrayList<RubrikDTO>() : alleRubriken;
		this.alleUser2UserGroupMappings = new LinkedList<User2UserGroupDTO>();
		this.allCompareValues = new LinkedList<StrgArFiltCondCompValDTO>();
		this.allDefaultFilterTextDTO = (allDefaultFilterTextDTO == null) ? new ArrayList<DefaultFilterTextDTO>() : allDefaultFilterTextDTO;
	}

	public Collection<DefaultFilterTextDTO> getAllDefaultFilterTexts() {
		return this.allDefaultFilterTextDTO;
	}

	@Deprecated
	public Collection<FilterConditionsToFilterDTO> getAllFilterConditionMappings() {
		return this.allFilterConditionMappings;
	}

	@Deprecated
	public Collection<StrgArFiltCondCompValDTO> getAllStringArrayCompareValues() {
		return this.allCompareValues;
	}

	@Deprecated
	public List<User2UserGroupDTO> getAllUser2UserGroupDTOs() {
		return this.alleUser2UserGroupMappings;
	}

	public Collection<AlarmbearbeiterDTO> gibAlleAlarmbearbeiter() {
		return this.alleAlarmbarbeiter;
	}

	public Collection<AlarmbearbeiterGruppenDTO> gibAlleAlarmbearbeiterGruppen() {
		return this.alleAlarmbearbeiterGruppen;
	}

	public Collection<TopicDTO> gibAlleAlarmtopics() {
		return this.alleAlarmtopics;
	}

	/**
	 * Returns a list of all FilterDTO's
	 */
	public Collection<DefaultFilterDTO> gibAlleDefaultFilter() {
		return this.allDefaultFilters;
	}
	
	public Collection<FilterDTO> gibAlleFilter() {
		List<FilterDTO> result = new ArrayList<>(this.allDefaultFilters.size() + this.allTimebasedFilters.size() + this.allWatchDogFilters.size());
		
		result.addAll(this.allDefaultFilters);
		result.addAll(this.allTimebasedFilters);
		result.addAll(this.allWatchDogFilters);
		
		return result;
	}

	public Collection<FilterConditionDTO> gibAlleFilterConditions() {
		return this.allFilterConditions;
	}

	public Collection<RubrikDTO> gibAlleRubriken() {
		return this.alleRubriken;
	}

}
