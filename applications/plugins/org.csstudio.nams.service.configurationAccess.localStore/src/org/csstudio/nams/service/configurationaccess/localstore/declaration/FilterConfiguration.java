
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

public class FilterConfiguration {

	private Collection<DefaultFilterDTO> allaFilter;
	
	public FilterConfiguration(Collection<DefaultFilterDTO> allaFilter) {
		this.allaFilter = allaFilter;
	}

	public Collection<DefaultFilterDTO> gibAlleFilter() {
		return this.allaFilter;
	}
}
