package org.csstudio.nams.application.department.decision.office.decision;

import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;

public interface FilterWorker extends Arbeitsfaehig {

	Regelwerk getRegelwerk();

	Inbox<Document> getInbox();
}
