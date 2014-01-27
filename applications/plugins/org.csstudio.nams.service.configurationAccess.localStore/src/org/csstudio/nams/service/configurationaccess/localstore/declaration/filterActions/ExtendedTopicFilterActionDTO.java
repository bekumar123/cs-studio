
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("11")
public class ExtendedTopicFilterActionDTO extends AbstTopicFilterActionDTO {

	public ExtendedTopicFilterActionDTO() {
		filterActionType = AlarmTopicFilterActionType.TOPIC_EXTENDED;
	}
}
