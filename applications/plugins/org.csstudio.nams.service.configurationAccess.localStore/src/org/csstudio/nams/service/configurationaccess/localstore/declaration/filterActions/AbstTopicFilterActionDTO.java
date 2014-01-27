package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;

public abstract class AbstTopicFilterActionDTO extends FilterActionDTO {

	public void setReceiver(TopicDTO receiver) {
		this._receiver = receiver;
		this.setIReceiverRef(receiver.getId());
	}

	public TopicDTO getReceiver() {
		return (TopicDTO) _receiver;
	}
	
	@Override
	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		// not used
	}

	@Override
	public void loadJoinData(Mapper mapper) throws Throwable {
		this.setReceiver(mapper.findForId(TopicDTO.class, this
				.getIReceiverRef(), false));
	}

	@Override
	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		// not used
	}

}
