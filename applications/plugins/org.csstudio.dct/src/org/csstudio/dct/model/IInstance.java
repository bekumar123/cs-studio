package org.csstudio.dct.model;

import java.util.List;

/**
 * Represents an instance.
 * 
 * @author Sven Wende
 * 
 */
public interface IInstance extends IContainer, IRootFolder, IDisabledRecordCounter {

    /**
     * Returns the prototype this instances is derived from.
     * 
     * @return the prototype
     */
    IPrototype getPrototype();

    /**
     * 
     * @return all Record from this Instance including all childs
     */
    List<IRecord> getAllRecordsInHierarchy();

    /**
     * 
     * @return true if this instance was created from a Library-Prototype
     */
    boolean isFromLibrary();

    /**
     * Set the protoype folder that this instance gets it's protoype from.
     * 
     * @param prototypeFolder
     */
    void setPrototypeFolder(String prototypeFolder);

    /**
     * Returns the protoype folder that this instance gets it's protoype from.
     * 
     * @return prototypeFolder
     */
    public String getPrototypeFolder();

}
