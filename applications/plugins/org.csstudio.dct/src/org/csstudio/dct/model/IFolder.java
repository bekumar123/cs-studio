package org.csstudio.dct.model;

import java.util.List;

/**
 * Represents a file system like folder. For example Prototype or Instances are
 * folder.
 * 
 * @author Sven Wende
 * 
 */
public interface IFolder extends IElement, IFolderMember, IRootFolder {

    /**
     * Returns all members.
     * 
     * @return all members
     */
    List<IFolderMember> getMembers();

    /**
     * Adds a member.
     * 
     * @param member
     *            a member
     */
    void addMember(IFolderMember member);

    /**
     * Adds a member at the specified index.
     * 
     * @param index
     *            the position index
     * 
     * @param member
     *            the member
     */
    void addMember(int index, IFolderMember member);

    /**
     * Removes a member.
     * 
     * @param member
     *            the member
     */
    void removeMember(IFolderMember member);

    /**
     * Replaces the member at the specified position.
     * 
     * @param index
     *            the list index
     * @param member
     *            the member
     */
    void setMember(int index, IFolderMember member);

    /**
     * Removes the member at the specified index.
     * 
     * @param index
     *            the position index
     */
    void removeMember(int index);

    /**
     * Return true if this is the special Prototypes Folder
     */
    boolean isPrototypesFolder();

    /**
     * Return true if this is the special Instances Folder
     */
    boolean isInstancesFolder();

    /**
     * Return true if this is the special Library Folder
     */
    boolean isLibraryFolder();
    
    /**
     * return the topmost folder this folder belongs to
     */
    IFolder getRootFolder();
    
    /**
     * Return true if folder is Instances, Prototype or LIbrary folder.
     * 
     * @return true if folder is Instances, Prototype or LIbrary folder.
     */
    boolean isRootFolder();

}
