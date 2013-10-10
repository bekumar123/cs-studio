package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.yaams.DefaultRegelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.NewRegelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.NichtRegel;
import org.csstudio.nams.common.material.regelwerk.yaams.OderRegel;
import org.csstudio.nams.common.material.regelwerk.yaams.ProcessVariableRegel;
import org.csstudio.nams.common.material.regelwerk.yaams.PropertyVergleichsRegel;
import org.csstudio.nams.common.material.regelwerk.yaams.Regel;
import org.csstudio.nams.common.material.regelwerk.yaams.StringRegel;
import org.csstudio.nams.common.material.regelwerk.yaams.TimebasedRegelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.TimebasedRegelwerk.TimeoutType;
import org.csstudio.nams.common.material.regelwerk.yaams.UndRegel;
import org.csstudio.nams.common.material.regelwerk.yaams.WatchDogRegelwerk;
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
	public List<NewRegelwerk> gibAlleRegelwerke() throws RegelwerksBuilderException {
		final List<NewRegelwerk> results = new LinkedList<NewRegelwerk>();

		try {

			final LocalStoreConfigurationService confStoreService = RegelwerkBuilderServiceImpl.configurationStoreService;
			// get all filters
			Collection<FilterDTO> listOfFilters = null;
			listOfFilters = confStoreService.getEntireFilterConfiguration().gibAlleFilter();

			// we do assume, that the first level filtercondition are conjugated
			for (final FilterDTO filterDTO : listOfFilters) {
				NewRegelwerk regelwerk = null;
				Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf(filterDTO.getIFilterID(), filterDTO.getName());

				if (filterDTO instanceof DefaultFilterDTO) {
					final List<FilterConditionDTO> filterConditions = ((DefaultFilterDTO) filterDTO).getFilterConditions();

					// create a list of first level filterconditions
					final List<Regel> versandRegels = new LinkedList<Regel>();
					for (final FilterConditionDTO filterConditionDTO : filterConditions) {
						try {
							versandRegels.add(this.createRegel(filterConditionDTO));
						} catch (Throwable t) {
							RegelwerkBuilderServiceImpl.logger.logErrorMessage(this, "Failed to create Versand-Regel from DTO: " + filterConditionDTO
									+ " for Filter: " + filterDTO, t);
						}
					}

					UndRegel hauptRegel = new UndRegel(versandRegels);
					regelwerk = new DefaultRegelwerk(regelwerkskennung, hauptRegel);

				} else if (filterDTO instanceof TimeBasedFilterDTO) {
					TimeBasedFilterDTO timeBasedFilterDTO = (TimeBasedFilterDTO) filterDTO;
					Regel startRegel = createRegel(timeBasedFilterDTO.getStartFilterCondition());
					Regel stopRegel = createRegel(timeBasedFilterDTO.getStopFilterCondition());
					TimeoutType timeoutType = (timeBasedFilterDTO.isSendOnTimeout()) ? TimeoutType.SENDE_BEI_TIMEOUT : TimeoutType.SENDE_BEI_STOP_REGEL;
					regelwerk = new TimebasedRegelwerk(regelwerkskennung, startRegel, stopRegel, Millisekunden.valueOf(timeBasedFilterDTO
							.getTimeout() * 1000), timeoutType); 
				} else if (filterDTO instanceof WatchDogFilterDTO) {
					WatchDogFilterDTO watchDogFilterDTO = (WatchDogFilterDTO) filterDTO;
					Regel rootRegel = createRegel(watchDogFilterDTO.getFilterCondition());
					regelwerk = new WatchDogRegelwerk(regelwerkskennung, rootRegel, Millisekunden.valueOf(watchDogFilterDTO.getTimeout() * 1000));
				}
				results.add(regelwerk);
			}

		} catch (final Throwable t) {
			RegelwerkBuilderServiceImpl.logger.logErrorMessage(this, "failed to load Regelwerke!", t);
			throw new RegelwerksBuilderException("failed to load Regelwerke!", t);
		}
		return results;
	}

	protected Regel createRegel(final FilterConditionDTO filterConditionDTO) {
		// mapping the type information in the aggrFilterConditionTObject to a
		// VersandRegel

		// FIXME (gs) hier knallt es bei JCFF oder NCFF Bedingungen

		final FilterConditionTypeRefToVersandRegelMapper fctr = FilterConditionTypeRefToVersandRegelMapper.valueOf(filterConditionDTO.getClass());
		switch (fctr) {
		//
		case STRING: {
			final StringFilterConditionDTO stringCondition = (StringFilterConditionDTO) filterConditionDTO;

			return new StringRegel(stringCondition.getOperatorEnum(), stringCondition.getKeyValueEnum(), stringCondition.getCompValue(), logger);
		}
		case PROPERTY_COMPARE: {
			final PropertyCompareFilterConditionDTO propertyCompareCondition = (PropertyCompareFilterConditionDTO) filterConditionDTO;
			
			return new PropertyVergleichsRegel(propertyCompareCondition.getOperatorEnum(), propertyCompareCondition.getMessageKeyValueEnum(), logger);
		}
		case TIMEBASED: {
			// nach entfernen der zeitbasierten Filterbedingungen darf dieser
			// Fall nicht mehr eintreten, dann also IAE werfen..
			// throw new IllegalArgumentException("Unsupported FilterType, see "
			// + this.getClass().getPackage() + "." +
			// this.getClass().getName());
			return null;
		}
		case JUNCTOR: {
			final List<Regel> children = new ArrayList<Regel>(2);

			final JunctorConditionDTO junctorCondition = (JunctorConditionDTO) filterConditionDTO;
			final FilterConditionDTO firstFilterCondition = junctorCondition.getFirstFilterCondition();
			final FilterConditionDTO secondFilterCondition = junctorCondition.getSecondFilterCondition();

			children.add(this.createRegel(firstFilterCondition));
			children.add(this.createRegel(secondFilterCondition));

			switch (junctorCondition.getJunctor()) {
			case OR:
				return new OderRegel(children);
			case AND:
				return new UndRegel(children);
			default:
				throw new IllegalArgumentException("Unsupported Junctor.");
			}
		}
		// oder verknüpfte Stringregeln
		case STRING_ARRAY: {
			final List<Regel> children = new LinkedList<Regel>();

			final StringArFilterConditionDTO stringArayCondition = (StringArFilterConditionDTO) filterConditionDTO;

			final List<String> compareValueList = stringArayCondition.getCompareValueStringList();

			final MessageKeyEnum keyValue = stringArayCondition.getKeyValueEnum();
			final StringRegelOperator operatorEnum = stringArayCondition.getOperatorEnum();
			for (final String string : compareValueList) {
				children.add(new StringRegel(operatorEnum, keyValue, string, logger));
			}

			return new OderRegel(children);
		}
		case PV: {
			final ProcessVarFiltCondDTO pvCondition = (ProcessVarFiltCondDTO) filterConditionDTO;

			return new ProcessVariableRegel(RegelwerkBuilderServiceImpl.pvConnectionService, pvCondition.getPVAddress(), pvCondition.getPVOperator(),
					pvCondition.getSuggestedPVType(), pvCondition.getCCompValue(), logger);
		}
		case NEGATION: {
			final NegationCondForFilterTreeDTO notCondition = (NegationCondForFilterTreeDTO) filterConditionDTO;

			return new NichtRegel(this.createRegel(notCondition.getNegatedFilterCondition()));
		}
		case JUNCTOR_FOR_TREE: {
			final JunctorCondForFilterTreeDTO junctorCondition = (JunctorCondForFilterTreeDTO) filterConditionDTO;

			final Set<FilterConditionDTO> operands = junctorCondition.getOperands();
			final List<Regel> children = new ArrayList<Regel>(operands.size());
			for (FilterConditionDTO operand : operands) {
				children.add(this.createRegel(operand));
			}

			if (junctorCondition.getOperator() == JunctorConditionType.AND) {
				return new UndRegel(children);
			} else if (junctorCondition.getOperator() == JunctorConditionType.OR) {
				return new OderRegel(children);
			} else {
				throw new IllegalArgumentException("Unsupported FilterType, see " + this.getClass().getPackage() + "." + this.getClass().getName());
			}
		}
		default:
			throw new IllegalArgumentException("Unsupported FilterType, see " + this.getClass().getPackage() + "." + this.getClass().getName());
		}
	}
}
