
package org.csstudio.nams.service.regelwerkbuilder.declaration;

import java.util.List;

import org.csstudio.nams.common.material.regelwerk.Filter;

public interface RegelwerkBuilderService {

	/**
	 * Loads all {@link Filter}-elements from configured configuration.
	 * 
	 * @return A unmodifyable list of {@link Filter}, not null.
	 * @throws RegelwerksBuilderException
	 *             If an error occurs on loading or creating {@link Filter}-elements.
	 */
	public List<Filter> gibAlleRegelwerke()
			throws RegelwerksBuilderException;
}
