
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

public class FilterConfiguration {

	private Collection<FilterDTO> alleFilter;
	
	public FilterConfiguration(Collection<FilterDTO> alleFilter) {
		this.alleFilter = alleFilter;
	}

	public Collection<FilterDTO> gibAlleFilter() {
		return this.alleFilter;
	}
}
