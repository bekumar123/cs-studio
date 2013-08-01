package org.csstudio.sds.history;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class HistoryPerspective implements IPerspectiveFactory {

	public final static String	ID	= "org.csstudio.sds.history.historyperspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
	}

}
