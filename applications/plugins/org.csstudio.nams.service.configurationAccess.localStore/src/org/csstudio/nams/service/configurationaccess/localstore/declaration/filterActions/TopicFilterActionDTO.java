
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("10")
public class TopicFilterActionDTO extends AbstTopicFilterActionDTO {

	public TopicFilterActionDTO() {
		filterActionType = AlarmTopicFilterActionType.TOPIC;
	}
}
