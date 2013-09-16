
package org.csstudio.nams.service.regelwerkbuilder.declaration;

import java.util.List;

import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.NewRegelwerk;

public interface RegelwerkBuilderService {

	/**
	 * Loads all {@link Regelwerk}-elements from configured configuration.
	 * 
	 * @return A unmodifyable list of {@link Regelwerk}, not null.
	 * @throws RegelwerksBuilderException
	 *             If an error occurs on loading or creating {@link Regelwerk}-elements.
	 */
	public List<NewRegelwerk> gibAlleRegelwerke()
			throws RegelwerksBuilderException;
}
