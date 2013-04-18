package org.csstudio.dct.model;

import java.util.List;
import java.util.Map;

import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.model.internal.BaseRecord;
import org.eclipse.gef.commands.CommandStack;

import com.google.common.base.Optional;

/**
 * Represents a project.
 * 
 * @author Sven Wende
 * 
 */
public interface IProject extends IElement {

    /**
     * Returns the database definition (the meta model which is parsed from a
     * dbd file).
     * 
     * @return the database definition
     */
    IDatabaseDefinition getDatabaseDefinition();

    /**
     * Sets the database definition (the meta model which is parsed from a dbd
     * file)
     * 
     * @param databaseDefinition
     *            the database definition
     */
    void setDatabaseDefinition(IDatabaseDefinition databaseDefinition);

    /**
     * Returns a base record for a specified record type.
     * 
     * @param type
     *            the record type
     * @return a base record or null
     */
    BaseRecord getBaseRecord(String type);

    /**
     * Returns a map with all currently available base records.
     * 
     * @return a map with all currently available base record
     */
    Map<String, BaseRecord> getBaseRecords();

    /**
     * Sets the available base records.
     * 
     * @param baseRecords
     *            the available base records
     */
    void setBaseRecords(Map<String, BaseRecord> baseRecords);

    /**
     * Returns the dbd file path.
     * 
     * @return the dbd file path
     */
    String getDbdPath();

    /**
     * Sets the dbd file path.
     * 
     * @param path
     *            the dbd file path.
     */
    void setDbdPath(String path);

    /**
     * Returns the library file path.
     * 
     * @return the library file path
     */
    String getLibraryPath();

    /**
     * Sets the library file path.
     * 
     * @param path
     *            the library file path.
     */
    void setLibraryPath(String path);
        

    /**
     * Returns the library file path.
     * 
     * @return the library file path
     */
    String getActiveLibraryPath();

    /**
     * Sets the library file path.
     * 
     * @param path
     *            the library file path.
     */
    void setActiveLibraryPath(String path);

    /**
     * Returns the name of an associated IOC.
     * 
     * @return the name of an associated IOC
     */
    String getIoc();

    /**
     * Sets the name of an associated IOC.
     * 
     * @param ioc
     *            the name of an associated IOC
     */
    void setIoc(String ioc);

    /**
     * Returns the list of records that will be rendered in a db file.
     * 
     * @deprecated svw: use a visitor to replace the functionality
     * @return the list of records that will be rendered in a db file
     */
    List<IRecord> getFinalRecords();

    /**
     * Returns the Library Folder of this project.
     * 
     * @return the Library Folder as an option value.
     */
    Optional<IFolder> getLibraryFolder();

    /**
     * Returns the Prototypes Folder of this project.
     * 
     * @return the Prototypes Folder as an option value.
     */
    Optional<IFolder> getPrototypesFolder();

    /**
     * Returns the Instances Folder of this project.
     * 
     * @return the Prototypes Folder as an option value.
     */
    Optional<IFolder> getInstancesFolder();

    /**
     * Add content of library-project to Prototypesfolder
     * 
     * @param libraryProject
     */
    void addPrototypesToLibrary(IProject libraryProject);
    
    /**
     * Make sure that changes in the Library are propagated to the main project
     * 
     */
    void refreshFromLibrary(CommandStack commandStack);

}
