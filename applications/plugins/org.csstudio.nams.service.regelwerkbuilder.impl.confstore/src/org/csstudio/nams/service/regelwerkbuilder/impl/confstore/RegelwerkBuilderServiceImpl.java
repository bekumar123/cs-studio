package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.material.regelwerk.AndFilterCondition;
import org.csstudio.nams.common.material.regelwerk.DefaultFilter;
import org.csstudio.nams.common.material.regelwerk.Filter;
import org.csstudio.nams.common.material.regelwerk.FilterCondition;
import org.csstudio.nams.common.material.regelwerk.NotFilterCondition;
import org.csstudio.nams.common.material.regelwerk.OrFilterCondition;
import org.csstudio.nams.common.material.regelwerk.ProcessVariableFilterCondition;
import org.csstudio.nams.common.material.regelwerk.PropertyCompareFilterCondition;
import org.csstudio.nams.common.material.regelwerk.StringFilterCondition;
import org.csstudio.nams.common.material.regelwerk.StringFilterConditionOperator;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter.TimeoutType;
import org.csstudio.nams.common.material.regelwerk.WatchDogFilter;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DefaultFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TimeBasedFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.WatchDogFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVarFiltCondDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.PropertyCompareFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerksBuilderException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;

public class RegelwerkBuilderServiceImpl implements RegelwerkBuilderService {

	private static IProcessVariableConnectionService pvConnectionService;
	private static LocalStoreConfigurationService configurationStoreService;
	private static ILogger logger;

	public static void staticInject(final IProcessVariableConnectionService pvConnectionService) {
		RegelwerkBuilderServiceImpl.pvConnectionService = pvConnectionService;
	}

	public static void staticInject(final LocalStoreConfigurationService configurationStoreService) {
		RegelwerkBuilderServiceImpl.configurationStoreService = configurationStoreService;
	}

	public static void staticInject(final ILogger logger) {
		RegelwerkBuilderServiceImpl.logger = logger;
	}

	@Override
	public List<Filter> getAllFilters() throws RegelwerksBuilderException {
		final List<Filter> results = new LinkedList<Filter>();

		try {

			final LocalStoreConfigurationService confStoreService = RegelwerkBuilderServiceImpl.configurationStoreService;
			// get all filters
			Collection<FilterDTO> listOfFilters = null;
			listOfFilters = confStoreService.getEntireFilterConfiguration().gibAlleFilter();

			// we do assume, that the first level filtercondition are conjugated
			for (final FilterDTO filterDTO : listOfFilters) {
				Filter regelwerk = null;
				FilterId regelwerkskennung = FilterId.valueOf(filterDTO.getIFilterID());

				if (filterDTO instanceof DefaultFilterDTO) {
					final List<FilterConditionDTO> filterConditions = ((DefaultFilterDTO) filterDTO).getFilterConditions();

					// create a list of first level filterconditions
					boolean isValidFilter = true;
					final List<FilterCondition> filterconditions = new LinkedList<FilterCondition>();
					for (final FilterConditionDTO filterConditionDTO : filterConditions) {
						try {
							filterconditions.add(this.createFilterCondition(filterConditionDTO));
						} catch (UnsupportedFilterConditionTypeException t) {
							RegelwerkBuilderServiceImpl.logger.logErrorMessage(this, "Failed to create filter condition from DTO: " + filterConditionDTO
									+ " for Filter: " + filterDTO);
							if (t.getFilterConditionType() == FilterConditionType.TIMEBASED) {
								// legacy support for timebased filter conditions (needed until all time based conditions have been migrated into time based filters)
								logger.logErrorMessage(this, "Unsupported filter condition type: " + FilterConditionType.TIMEBASED);
								isValidFilter = false;
								break;
							} else {
								throw t;
							}
						}
					}
					if (isValidFilter) {
						AndFilterCondition hauptRegel = new AndFilterCondition(filterconditions);
						regelwerk = new DefaultFilter(regelwerkskennung, hauptRegel);
					}

				} else if (filterDTO instanceof TimeBasedFilterDTO) {
					TimeBasedFilterDTO timeBasedFilterDTO = (TimeBasedFilterDTO) filterDTO;
					FilterCondition startRegel = createFilterCondition(timeBasedFilterDTO.getStartFilterCondition());
					FilterCondition stopRegel = createFilterCondition(timeBasedFilterDTO.getStopFilterCondition());
					TimeoutType timeoutType = (timeBasedFilterDTO.isSendOnTimeout()) ? TimeoutType.SENDE_BEI_TIMEOUT : TimeoutType.SENDE_BEI_STOP_REGEL;
					regelwerk = new TimebasedFilter(regelwerkskennung, startRegel, stopRegel, Milliseconds.valueOf(timeBasedFilterDTO
							.getTimeout() * 1000), timeoutType); 
				} else if (filterDTO instanceof WatchDogFilterDTO) {
					WatchDogFilterDTO watchDogFilterDTO = (WatchDogFilterDTO) filterDTO;
					FilterCondition rootRegel = createFilterCondition(watchDogFilterDTO.getFilterCondition());
					regelwerk = new WatchDogFilter(regelwerkskennung, rootRegel, Milliseconds.valueOf(watchDogFilterDTO.getTimeout() * 1000));
				}
				
				if (regelwerk != null) {
					results.add(regelwerk);
				}
			}

		} catch (final Throwable t) {
			RegelwerkBuilderServiceImpl.logger.logErrorMessage(this, "failed to load Regelwerke!", t);
			throw new RegelwerksBuilderException("failed to load Regelwerke!", t);
		}
		return results;
	}

	protected FilterCondition createFilterCondition(final FilterConditionDTO filterConditionDTO) throws UnsupportedFilterConditionTypeException {
		final FilterConditionType type = FilterConditionType.valueOf(filterConditionDTO.getClass());
		switch (type) {
		//
		case STRING: {
			final StringFilterConditionDTO stringCondition = (StringFilterConditionDTO) filterConditionDTO;

			return new StringFilterCondition(stringCondition.getOperatorEnum(), stringCondition.getKeyValueEnum(), stringCondition.getCompValue(), logger);
		}
		case PROPERTY_COMPARE: {
			final PropertyCompareFilterConditionDTO propertyCompareCondition = (PropertyCompareFilterConditionDTO) filterConditionDTO;
			
			return new PropertyCompareFilterCondition(propertyCompareCondition.getOperatorEnum(), propertyCompareCondition.getMessageKeyValueEnum(), logger);
		}
		case TIMEBASED: {
			// nach entfernen der zeitbasierten Filterbedingungen darf dieser
			// Fall nicht mehr eintreten, dann also IAE werfen..
			throw new UnsupportedFilterConditionTypeException(FilterConditionType.TIMEBASED);
		}
		case JUNCTOR: {
			final List<FilterCondition> children = new ArrayList<FilterCondition>(2);

			final JunctorConditionDTO junctorCondition = (JunctorConditionDTO) filterConditionDTO;
			final FilterConditionDTO firstFilterCondition = junctorCondition.getFirstFilterCondition();
			final FilterConditionDTO secondFilterCondition = junctorCondition.getSecondFilterCondition();

			children.add(this.createFilterCondition(firstFilterCondition));
			children.add(this.createFilterCondition(secondFilterCondition));

			switch (junctorCondition.getJunctor()) {
			case OR:
				return new OrFilterCondition(children);
			case AND:
				return new AndFilterCondition(children);
			default:
				throw new IllegalArgumentException("Unsupported Junctor.");
			}
		}
		// oder verknüpfte Stringregeln
		case STRING_ARRAY: {
			final List<FilterCondition> children = new LinkedList<FilterCondition>();

			final StringArFilterConditionDTO stringArayCondition = (StringArFilterConditionDTO) filterConditionDTO;

			final List<String> compareValueList = stringArayCondition.getCompareValueStringList();

			final MessageKeyEnum keyValue = stringArayCondition.getKeyValueEnum();
			final StringFilterConditionOperator operatorEnum = stringArayCondition.getOperatorEnum();
			for (final String string : compareValueList) {
				children.add(new StringFilterCondition(operatorEnum, keyValue, string, logger));
			}

			return new OrFilterCondition(children);
		}
		case PV: {
			final ProcessVarFiltCondDTO pvCondition = (ProcessVarFiltCondDTO) filterConditionDTO;

			return new ProcessVariableFilterCondition(RegelwerkBuilderServiceImpl.pvConnectionService, pvCondition.getPVAddress(), pvCondition.getPVOperator(),
					pvCondition.getSuggestedPVType(), pvCondition.getCCompValue(), logger);
		}
		case NEGATION: {
			final NegationCondForFilterTreeDTO notCondition = (NegationCondForFilterTreeDTO) filterConditionDTO;

			return new NotFilterCondition(this.createFilterCondition(notCondition.getNegatedFilterCondition()));
		}
		case JUNCTOR_FOR_TREE: {
			final JunctorCondForFilterTreeDTO junctorCondition = (JunctorCondForFilterTreeDTO) filterConditionDTO;

			final Set<FilterConditionDTO> operands = junctorCondition.getOperands();
			final List<FilterCondition> children = new ArrayList<FilterCondition>(operands.size());
			for (FilterConditionDTO operand : operands) {
				children.add(this.createFilterCondition(operand));
			}

			if (junctorCondition.getOperator() == JunctorConditionType.AND) {
				return new AndFilterCondition(children);
			} else if (junctorCondition.getOperator() == JunctorConditionType.OR) {
				return new OrFilterCondition(children);
			} else {
				throw new IllegalArgumentException("Unsupported FilterType, see " + this.getClass().getPackage() + "." + this.getClass().getName());
			}
		}
		default:
			throw new IllegalArgumentException("Unsupported FilterType, see " + this.getClass().getPackage() + "." + this.getClass().getName());
		}
	}
	
	class UnsupportedFilterConditionTypeException extends Exception {

		private static final long serialVersionUID = 4582775255444273290L;
		private FilterConditionType type;
		
		public UnsupportedFilterConditionTypeException(FilterConditionType type) {
			super(type.toString());
			this.type = type;
		}
		
		public FilterConditionType getFilterConditionType() {
			return type;
		}
		
	}
}
