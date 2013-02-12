package org.csstudio.nams.configurator.beans;

import java.util.Comparator;

import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;

public class FilterbedingungBeanComparator implements Comparator<FilterbedingungBean> {

	enum ConditionType {
		CONDITION_AND, CONDITION_OR, CONDITION_NOT, CONDITION_OTHER;
	}
	
	@Override
	public int compare(FilterbedingungBean filterBedingung1, FilterbedingungBean filterBedingung2) {
		int result = 0;
		
		if(filterBedingung1 != filterBedingung2) {
			if(filterBedingung1 == null) {
				// null is smaller than anything else 
				result = -1;
			}
			// Both conditions != null
			else if(filterBedingung1.equals(filterBedingung2)) {
				result = 0;
			}
			else {
				FilterbedingungBeanComparator.ConditionType condition1 = getConditionType(filterBedingung1);
				FilterbedingungBeanComparator.ConditionType condition2 = getConditionType(filterBedingung2);
				
				result = condition1.compareTo(condition2);
				
				if(result == 0) {
					if(condition1 == ConditionType.CONDITION_AND || condition1 == ConditionType.CONDITION_OR) {
						result = filterBedingung1.hashCode() - filterBedingung2.hashCode();
					}
					else if(condition1 == ConditionType.CONDITION_NOT) {
						FilterbedingungBean filterbedingungBean1 = ((NotConditionForFilterTreeBean)filterBedingung1).getFilterbedingungBean();
						FilterbedingungBean filterbedingungBean2 = ((NotConditionForFilterTreeBean)filterBedingung2).getFilterbedingungBean();
						result = compare(filterbedingungBean1, filterbedingungBean2);
					}
					else {
						result = filterBedingung1.getDisplayName().compareTo(filterBedingung2.getDisplayName());
					}
				}
			}
		}
		
		return result;
	}
	
	private FilterbedingungBeanComparator.ConditionType getConditionType(FilterbedingungBean filterBedingung) {
		FilterbedingungBeanComparator.ConditionType result = ConditionType.CONDITION_OTHER;
		
		if(filterBedingung instanceof JunctorConditionForFilterTreeBean) {
			if(((JunctorConditionForFilterTreeBean) filterBedingung).getJunctorConditionType() == JunctorConditionType.AND) {
				result = ConditionType.CONDITION_AND;
			}
			else {
				result = ConditionType.CONDITION_OR;
			}
		}
		else if(filterBedingung instanceof NotConditionForFilterTreeBean) {
			result = ConditionType.CONDITION_NOT;
		}
		
		return result;
	}
}