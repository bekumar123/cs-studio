package org.csstudio.sds.history.pvinformation.service;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.sds.history.domain.service.IPvInformationService;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariableAttribute;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PvAttributeNames;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.DoubleValue;

public class PvInformationService implements IPvInformationService {

	IArchiveReaderFacade _archiveReader = null;

	@Override
	public ProcessVariable getProcessVariable(String controlSystemAddress) {
		ProcessVariable newPv = new ProcessVariable(new PlantUnitId(), controlSystemAddress);
		newPv.setControlSystemAddress(controlSystemAddress);

		if (_archiveReader != null) {
			try {
				IArchiveChannel archiveChannel = _archiveReader.getChannelByName(controlSystemAddress);
				if (archiveChannel != null && archiveChannel.getDisplayLimits() != null) {
					Limits<?> displayLimits = archiveChannel.getDisplayLimits();

					Object high = displayLimits.getHigh();
					Object low = displayLimits.getLow();

					// TODO CME: type check for display limits
					ProcessVariableAttribute displayHigh = new ProcessVariableAttribute(new PlantUnitId(), PvAttributeNames.MAX, "display high",
							new DoubleValue(high));
					newPv.addAttribute(displayHigh);

					ProcessVariableAttribute displayLow = new ProcessVariableAttribute(new PlantUnitId(), PvAttributeNames.MIN, "display low", new DoubleValue(
							low));
					newPv.addAttribute(displayLow);
				}
				
			} catch (ArchiveServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return newPv;
	}

	public void bindArchiveReaderFacade(IArchiveReaderFacade archiveReader) {
		_archiveReader = archiveReader;
	}

	public void unbindArchiveReaderFacade(IArchiveReaderFacade archiveReaderFacade) {
		_archiveReader = null;
	}

}
