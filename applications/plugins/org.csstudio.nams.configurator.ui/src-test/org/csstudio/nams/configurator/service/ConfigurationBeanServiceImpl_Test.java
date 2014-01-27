
package org.csstudio.nams.configurator.service;

import java.util.Collection;
import java.util.LinkedList;
import junit.framework.TestCase;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.DefaultFilterBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.TimebasedFilterBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DefaultFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ExtendedMessagePvDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TimeBasedFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationBeanServiceImpl_Test extends TestCase {

	private LocalStoreConfigurationService confService;
	private DefaultFilterDTO testDefaultFilterDTO;
	private TimeBasedFilterDTO timebasedFilterDTO;

	@Override
	@Before
	public void setUp() throws Exception {
		timebasedFilterDTO = new TimeBasedFilterDTO(0);
		testDefaultFilterDTO = new DefaultFilterDTO(1);

		this.confService = EasyMock
				.createMock(LocalStoreConfigurationService.class);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		EasyMock.verify(this.confService);
		this.confService = null;
	}

	@Test
	public void testConfigurationBeanServiceImpl() throws Throwable {
		final Configuration startConfiguration = this.createTestData();

		EasyMock.expect(this.confService.getEntireConfiguration()).andReturn(
				startConfiguration).once();
		EasyMock.replay(this.confService);

		final ConfigurationBeanService service = new ConfigurationBeanServiceImpl();
		((ConfigurationBeanServiceImpl) service)
				.setNewConfigurationStore(this.confService);

		service.refreshData();

		AlarmbearbeiterBean[] alarmBearbeiterBeans = service.getAlarmBearbeiterBeans();
		assertEquals(1, alarmBearbeiterBeans.length);
		AlarmbearbeiterBean alarmbearbeiterBean = alarmBearbeiterBeans[0];
		assertEquals(1, alarmbearbeiterBean.getID());
		
		FilterBean<?>[] filterBeans = service.getFilterBeans();
		assertEquals(2, filterBeans.length);
		
		boolean containsTimebasedFilterBean = false;
		boolean containsDefaultFilterBean = false;
		for (FilterBean<?> filterBean : filterBeans) {
			if(filterBean instanceof TimebasedFilterBean) {
				containsTimebasedFilterBean = true;
			}
			else if(filterBean instanceof DefaultFilterBean) {
				containsDefaultFilterBean = true;
			}
		}
		assertTrue(containsTimebasedFilterBean && containsDefaultFilterBean);
		
		EasyMock.verify(this.confService);
	}

	private Configuration createTestData() {
		final Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter = new LinkedList<AlarmbearbeiterDTO>();
		final Collection<TopicDTO> alleAlarmtopics = new LinkedList<TopicDTO>();
		final Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen = new LinkedList<AlarmbearbeiterGruppenDTO>();
		final Collection<FilterDTO> allFilters = new LinkedList<FilterDTO>();
		final Collection<FilterConditionDTO> allFilterConditions = new LinkedList<FilterConditionDTO>();
		final Collection<RubrikDTO> alleRubriken = new LinkedList<RubrikDTO>();
		final Collection<DefaultFilterTextDTO> allDefaultFilterTexts = new LinkedList<DefaultFilterTextDTO>();
		final Collection<ExtendedMessagePvDTO> allExtendedMessagePvDTOs = new  LinkedList<ExtendedMessagePvDTO>();

		alleAlarmbarbeiter.add(new AlarmbearbeiterDTO(1, 0, "TestUserName", "test@testdomain.de", "phone#Mobile", "phone#Fest", "OK", "ACK", true));
		
		allFilters.add(timebasedFilterDTO);
		allFilters.add(testDefaultFilterDTO);
		
		// TODO Mit weiteren konsistenen!! daten füllen
		
		
		return new Configuration(alleAlarmbarbeiter, alleAlarmtopics,
				alleAlarmbearbeiterGruppen, allFilters, allFilterConditions,
				alleRubriken, allDefaultFilterTexts, allExtendedMessagePvDTOs);
	}

}
