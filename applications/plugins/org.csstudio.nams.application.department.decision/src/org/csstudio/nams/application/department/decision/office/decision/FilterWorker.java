package org.csstudio.nams.application.department.decision.office.decision;

import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Worker;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.material.regelwerk.Filter;

public interface FilterWorker extends Worker {

	Filter getFilter();

	Inbox<Document> getInbox();
}
