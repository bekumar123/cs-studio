
package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.AndFilterCondition;
import org.csstudio.nams.common.material.regelwerk.Filter;
import org.csstudio.nams.common.material.regelwerk.FilterCondition;
import org.csstudio.nams.common.material.regelwerk.NotFilterCondition;
import org.csstudio.nams.common.material.regelwerk.OrFilterCondition;
import org.csstudio.nams.common.material.regelwerk.StringFilterCondition;
import org.csstudio.nams.common.material.regelwerk.StringFilterConditionOperator;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DefaultFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StrgArFiltCondCompValDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StrgArFiltCondCompValDTOPK;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerksBuilderException;
import org.csstudio.nams.service.regelwerkbuilder.impl.confstore.RegelwerkBuilderServiceImpl.UnsupportedFilterConditionTypeException;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IConnector;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.SettableState;
import org.junit.Assert;
import org.junit.Test;

public class RegelwerkbuilderServiceImpl_Test extends TestCase {

	private RegelwerkBuilderServiceImpl regelwerkBuilderService;
	private StringFilterCondition childRegel;
	private StringFilterConditionDTO childDTO;
	private StringFilterConditionDTO childDTO2;
	private StringFilterCondition childRegel2;
	
	private List<FilterDTO> localStoreConfigurationServiceFilterDTOs;

	@Override
	protected void setUp() throws Exception {
		
		regelwerkBuilderService = (RegelwerkBuilderServiceImpl) new RegelwerkBuilderServiceFactoryImpl()
				.createService();
		RegelwerkBuilderServiceImpl.staticInject(createMockProcessVariableConnectionService());

		localStoreConfigurationServiceFilterDTOs = new ArrayList<FilterDTO>();
		RegelwerkBuilderServiceImpl.staticInject(createMockLocalStoreConfigurationService());
		
		childRegel = new StringFilterCondition(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL, MessageKeyEnum.HOST, "gnarf", null);
		childRegel2 = new StringFilterCondition(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL, MessageKeyEnum.HOST, "gnarf2", null);

		childDTO = new StringFilterConditionDTO();
		childDTO.setOperatorEnum(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL);
		childDTO.setKeyValue(MessageKeyEnum.HOST);
		childDTO.setCompValue("gnarf");

		childDTO2 = new StringFilterConditionDTO();
		childDTO2.setOperatorEnum(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL);
		childDTO2.setKeyValue(MessageKeyEnum.HOST);
		childDTO2.setCompValue("gnarf2");
	}

	@Test
	public void testBuildStringCondition() throws UnsupportedFilterConditionTypeException {
		assertEquals(childRegel, regelwerkBuilderService
				.createFilterCondition(childDTO));
	}

	@Test
	public void testBuildStringArrayCondition() throws UnsupportedFilterConditionTypeException {
		StringArFilterConditionDTO arrayDTO = new StringArFilterConditionDTO();
		arrayDTO.setKeyValue(MessageKeyEnum.HOST);
		arrayDTO.setOperatorEnum(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL);

		ArrayList<StrgArFiltCondCompValDTO> arrayList = new ArrayList<StrgArFiltCondCompValDTO>();

		StrgArFiltCondCompValDTO compareValuesDTO = new StrgArFiltCondCompValDTO();
		StrgArFiltCondCompValDTOPK valuesDTO_PK = new StrgArFiltCondCompValDTOPK();
		valuesDTO_PK.setCompValue("gnarf");
		compareValuesDTO.setPk(valuesDTO_PK);
		arrayList.add(compareValuesDTO);

		compareValuesDTO = new StrgArFiltCondCompValDTO();
		valuesDTO_PK = new StrgArFiltCondCompValDTOPK();
		valuesDTO_PK.setCompValue("gnarf2");
		compareValuesDTO.setPk(valuesDTO_PK);
		arrayList.add(compareValuesDTO);

		arrayDTO.setCompareValues(arrayList);

		List<FilterCondition> regeln = new ArrayList<FilterCondition>(2);
		regeln.add(new StringFilterCondition(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.HOST, "gnarf", null));
		regeln.add(new StringFilterCondition(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.HOST, "gnarf2", null));
		FilterCondition zielRegel = new OrFilterCondition(regeln);

		assertEquals(zielRegel, regelwerkBuilderService
				.createFilterCondition(arrayDTO));
	}

	@Test
	public void testBuildJunctorConditionOr() throws UnsupportedFilterConditionTypeException {
		JunctorConditionDTO junctorDTO = new JunctorConditionDTO();
		junctorDTO.setJunctor(JunctorConditionType.OR);
		junctorDTO.setFirstFilterCondition(childDTO);

		StringFilterConditionDTO childDTO2 = new StringFilterConditionDTO();
		childDTO2.setOperatorEnum(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL);
		childDTO2.setKeyValue(MessageKeyEnum.HOST);
		childDTO2.setCompValue("gnarf2");
		junctorDTO.setSecondFilterCondition(childDTO2);

		List<FilterCondition> regeln = new ArrayList<FilterCondition>(2);
		regeln.add(childRegel);
		regeln.add(new StringFilterCondition(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.HOST, "gnarf2", null));
		FilterCondition zielRegel = new OrFilterCondition(regeln);

		assertEquals(zielRegel, regelwerkBuilderService
				.createFilterCondition(junctorDTO));
	}

	@Test
	public void testBuildJunctorConditionAnd() throws UnsupportedFilterConditionTypeException {
		JunctorConditionDTO junctorDTO = new JunctorConditionDTO();
		junctorDTO.setJunctor(JunctorConditionType.AND);
		junctorDTO.setFirstFilterCondition(childDTO);
		junctorDTO.setSecondFilterCondition(childDTO2);

		List<FilterCondition> regeln = new ArrayList<FilterCondition>(2);
		regeln.add(childRegel);
		regeln.add(childRegel2);
		FilterCondition zielRegel = new AndFilterCondition(regeln);

		assertEquals(zielRegel, regelwerkBuilderService
				.createFilterCondition(junctorDTO));
	}

	@Test
	public void testBuildJunctorCondtionTreeNegation() throws UnsupportedFilterConditionTypeException {
		NegationCondForFilterTreeDTO negationDTO = new NegationCondForFilterTreeDTO();
		negationDTO.setNegatedFilterCondition(childDTO);
		FilterCondition zielRegel = new NotFilterCondition(childRegel);
		assertEquals(zielRegel, regelwerkBuilderService
				.createFilterCondition(negationDTO));
	}

	@Test
	public void testBuildJunctorConditionTreeAnd() throws UnsupportedFilterConditionTypeException {
		JunctorCondForFilterTreeDTO junctorDTO = new JunctorCondForFilterTreeDTO();
		junctorDTO.setOperator(JunctorConditionType.AND);
		Set<FilterConditionDTO> childConditions = new HashSet<FilterConditionDTO>();
		childConditions.add(childDTO);
		childConditions.add(childDTO2);
		junctorDTO.setOperands(childConditions);
		
		List<FilterCondition> regeln = new ArrayList<FilterCondition>(2);
		regeln.add(childRegel);
		regeln.add(childRegel2);
		FilterCondition zielRegel = new AndFilterCondition(regeln);
		
		assertEquals(zielRegel, regelwerkBuilderService.createFilterCondition(junctorDTO));
	}

	@Test
	public void testBuildJunctorConditionTreeOr() throws UnsupportedFilterConditionTypeException {
		JunctorCondForFilterTreeDTO junctorDTO = new JunctorCondForFilterTreeDTO();
		junctorDTO.setOperator(JunctorConditionType.OR);
		Set<FilterConditionDTO> childConditions = new HashSet<FilterConditionDTO>();
		childConditions.add(childDTO);
		childConditions.add(childDTO2);
		junctorDTO.setOperands(childConditions);
		
		List<FilterCondition> regeln = new ArrayList<FilterCondition>(2);
		regeln.add(childRegel);
		regeln.add(childRegel2);
		FilterCondition zielRegel = new OrFilterCondition(regeln);
		
		assertEquals(zielRegel, regelwerkBuilderService.createFilterCondition(junctorDTO));
	}
	
	@Test
	public void testGibKomplexeRegelwerke() throws RegelwerksBuilderException {
		DefaultFilterDTO complexStringFilterDTO = new DefaultFilterDTO();
		complexStringFilterDTO.setFilterConditions(Arrays.asList((FilterConditionDTO)childDTO, childDTO2));
		
		localStoreConfigurationServiceFilterDTOs.add(complexStringFilterDTO);

		List<Filter> komplexeRegelwerke = regelwerkBuilderService.getAllFilters();
		assertEquals(1, komplexeRegelwerke.size());

		DefaultFilterDTO simpleStringFilterDTO = new DefaultFilterDTO();
		simpleStringFilterDTO.setFilterConditions(Arrays.asList((FilterConditionDTO)childDTO2));
		
		localStoreConfigurationServiceFilterDTOs.add(simpleStringFilterDTO);

		komplexeRegelwerke = regelwerkBuilderService.getAllFilters();
		assertEquals(2, komplexeRegelwerke.size());
		
		DefaultFilterDTO stringFilterDtoWrongOperator = new DefaultFilterDTO();
		StringFilterConditionDTO stringFilterCondition = new StringFilterConditionDTO();
		stringFilterCondition.setOperatorEnum(StringFilterConditionOperator.OPERATOR_NUMERIC_EQUAL);
		stringFilterCondition.setKeyValue(MessageKeyEnum.ACK_TIME);
		stringFilterCondition.setCompValue("1");
		stringFilterDtoWrongOperator.setFilterConditions(Arrays.asList((FilterConditionDTO)stringFilterCondition));
		
		localStoreConfigurationServiceFilterDTOs.add(stringFilterDtoWrongOperator);
		
		assertEquals(3, regelwerkBuilderService.getAllFilters().size());

		DefaultFilterDTO otherFilterDtoWrongOperator = new DefaultFilterDTO();
		
		StringArFilterConditionDTO arrayDTO = new StringArFilterConditionDTO();
		arrayDTO.setKeyValue(MessageKeyEnum.HOST);
		arrayDTO.setOperatorEnum(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL);

		ArrayList<StrgArFiltCondCompValDTO> arrayList = new ArrayList<StrgArFiltCondCompValDTO>();

		StrgArFiltCondCompValDTO compareValuesDTO = new StrgArFiltCondCompValDTO();
		StrgArFiltCondCompValDTOPK valuesDTO_PK = new StrgArFiltCondCompValDTOPK();
		valuesDTO_PK.setCompValue("gnarf");
		compareValuesDTO.setPk(valuesDTO_PK);
		arrayList.add(compareValuesDTO);

		compareValuesDTO = new StrgArFiltCondCompValDTO();
		valuesDTO_PK = new StrgArFiltCondCompValDTOPK();
		valuesDTO_PK.setCompValue("gnarf2");
		compareValuesDTO.setPk(valuesDTO_PK);
		arrayList.add(compareValuesDTO);

		arrayDTO.setCompareValues(arrayList);

		otherFilterDtoWrongOperator.setFilterConditions(Arrays.asList((FilterConditionDTO)arrayDTO));
		
		localStoreConfigurationServiceFilterDTOs.add(otherFilterDtoWrongOperator);
		
		assertEquals(4, regelwerkBuilderService.getAllFilters().size());
	}

	private LocalStoreConfigurationService createMockLocalStoreConfigurationService() {
		return new LocalStoreConfigurationService() {
	
			@Override
		    public void deleteDTO(NewAMSConfigurationElementDTO dto)
					throws StorageError, StorageException,
					InconsistentConfigurationException {
	
			}
	
			@Override
		    public ReplicationStateDTO getCurrentReplicationState()
					throws StorageError, StorageException,
					InconsistentConfigurationException {
				return null;
			}
	
			@Override
		    public Configuration getEntireConfiguration()
					throws StorageError, StorageException,
					InconsistentConfigurationException {
				return null;
			}
	
			@Override
		    public FilterConfiguration getEntireFilterConfiguration()
					throws StorageError, StorageException,
					InconsistentConfigurationException {
				
				return new FilterConfiguration(localStoreConfigurationServiceFilterDTOs);
			}
	
			@Override
		    public void prepareSynchonization() throws StorageError,
					StorageException,
					InconsistentConfigurationException {
			}
	
			@Override
		    public void saveCurrentReplicationState(
					ReplicationStateDTO currentState)
					throws StorageError, StorageException,
					UnknownConfigurationElementError {
			}
	
			@Override
		    public void saveDTO(NewAMSConfigurationElementDTO dto)
					throws StorageError, StorageException,
					InconsistentConfigurationException {
			}
	
			@Override
		    public void saveHistoryDTO(HistoryDTO historyDTO)
					throws StorageError, StorageException,
					InconsistentConfigurationException {
			}
		};
	}

	private IProcessVariableConnectionService createMockProcessVariableConnectionService() {
		return new IProcessVariableConnectionService() {
	
			@Override
		    public List<IConnector> getConnectors() {
				return null;
			}
	
			@Override
		    public SettableState checkWriteAccessSynchronously(IProcessVariableAddress pv) {
				return null;
			}
	
			@Override
			public void readValueAsynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType,
					IProcessVariableValueListener listener) {
			}
	
			@Override
			public <E> E readValueSynchronously(IProcessVariableAddress processVariableAddress, ValueType valueType)
					throws ConnectionException {
				return null;
			}
	
			@Override
			public void register(IProcessVariableValueListener listener, IProcessVariableAddress pv, ValueType valueType) {
				
			}
	
			@Override
			public void unregister(IProcessVariableValueListener listener) {
				
			}
	
			@Override
			public void writeValueAsynchronously(IProcessVariableAddress processVariableAddress, Object value,
					ValueType expectedValueType, IProcessVariableWriteListener listener) {
				
			}
	
			@Override
			public boolean writeValueSynchronously(IProcessVariableAddress processVariableAddress, Object value, ValueType expectedValueType) {
				Assert.fail("unexpected call of method.");
				return false;
			}
	
			@Override
			public int getNumberOfActiveConnectors() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}
	
}
