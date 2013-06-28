package org.csstudio.dct.model;

import java.util.List;

/**
 * Represents a folder member.
 * 
 * @author Sven Wende
 * 
 */
public interface IFolderMember extends IElement {
	/**
	 * Sets the parent folder.
	 * 
	 * @param folder
	 *            the parent folder
	 */
	void setParentFolder(IFolder folder);

	/**
	 * Returns the parent folder.
	 * 
	 * @return the parent folder
	 */
	IFolder getParentFolder();
	    
	/**
	 * Returns the root folder, which is also the project.
	 * 
	 * @return the project or root folder
	 */
	IProject getProject();
	
}
