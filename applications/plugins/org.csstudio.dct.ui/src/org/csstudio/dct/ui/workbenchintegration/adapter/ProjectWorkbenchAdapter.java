package org.csstudio.dct.ui.workbenchintegration.adapter;

import org.csstudio.dct.model.IFolder;

/**
 * UI adapter for projects.
 * 
 * @author Sven Wende
 */
public final class ProjectWorkbenchAdapter extends FolderWorkbenchAdapter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doGetIcon(IFolder folder) {
		return "icons/project.png";
	}

}
