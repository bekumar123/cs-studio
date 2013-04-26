package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.sync.ModelSync;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.csstudio.dct.util.Immutable;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.Nullable;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * Represents a project. A project is the root of the hierarchy.
 * 
 * @author Sven Wende
 */
public final class Project extends Folder implements IProject {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(Project.class);
    
    public static boolean IS_UNIT_TEST = false;

    @NotNull
    private transient Map<String, BaseRecord> baseRecords = new HashMap<String, BaseRecord>();;

    @Nullable
    private transient IDatabaseDefinition databaseDefinition;

    @Nullable
    private String path;

    @Nullable
    private String libraryPath;

    @Nullable
    private String activeLibraryPath;

    @Nullable
    private String ioc;

    protected Project(String name, UUID id) {
        super(name, id);
        checkNotNull(name, id);
        databaseDefinition = null;
    }

    /**
     * {@inheritDoc}
     */
    public IDatabaseDefinition getDatabaseDefinition() {
        return databaseDefinition;
    }

    /**
     * {@inheritDoc}
     */
    public void setDatabaseDefinition(IDatabaseDefinition databaseDefinition) {
        this.databaseDefinition = databaseDefinition;
    }

    /**
     * {@inheritDoc}
     */
    public BaseRecord getBaseRecord(String type) {
        if (!baseRecords.containsKey(type)) {
            baseRecords.put(type, new BaseRecord(null));
        }
        return baseRecords.get(type);
    }

    /**
     * {@inheritDoc}
     */
    @Immutable
    public Map<String, BaseRecord> getBaseRecords() {
        return ImmutableMap.copyOf(baseRecords);
    }

    /**
     * {@inheritDoc}
     */
    public void setBaseRecords(@NotNull Map<String, BaseRecord> baseRecords) {
        checkNotNull(baseRecords);
        this.baseRecords = baseRecords;
    }

    /**
     * {@inheritDoc}
     */
    public String getDbdPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    public void setDbdPath(String path) {
        checkNotNull(path);
        this.path = path;
    }

    @Override
    public String getLibraryPath() {
        return libraryPath;
    }

    @Override
    public void setLibraryPath(String path) {
        libraryPath = path;
    }
    
    @Override
    public String getActiveLibraryPath() {
        return activeLibraryPath;
    }

    @Override
    public void setActiveLibraryPath(String path) {
        activeLibraryPath = path;
    }

    /**
     * {@inheritDoc}
     */
    public String getIoc() {
        return ioc;
    }

    /**
     * {@inheritDoc}
     */
    public void setIoc(String ioc) {
        this.ioc = ioc;
    }

    /**
     * {@inheritDoc}
     */
    public List<IRecord> getFinalRecords() {
        return getFinalRecords(this);
    }

    /**
     * {@inheritDoc}
     */
    public Optional<IFolder> getLibraryFolder() {
        for (IFolderMember member : getMembers()) {
            if (member instanceof Folder) {
                Folder folder = (Folder) member;
                if (folder.isLibraryFolder()) {
                    return Optional.of((IFolder) folder);
                }
            }
        }
        return Optional.absent();
    }

    /**
     * {@inheritDoc}
     */
    public Optional<IFolder> getPrototypesFolder() {
        for (IFolderMember member : getMembers()) {
            if (member instanceof Folder) {
                Folder folder = (Folder) member;
                if (folder.isPrototypesFolder()) {
                    return Optional.of((IFolder) folder);
                }
            }
        }
        return Optional.absent();
    }

    /**
     * {@inheritDoc}
     */
    public Optional<IFolder> getInstancesFolder() {
        for (IFolderMember member : getMembers()) {
            if (member instanceof Folder) {
                Folder folder = (Folder) member;
                if (folder.isInstancesFolder()) {
                    return Optional.of((IFolder) folder);
                }
            }
        }
        return Optional.absent();
    }

    @Override
    public void addPrototypesToLibrary(IProject libraryProject) {
        Optional<IFolder> prototypesFolder = libraryProject.getPrototypesFolder();
        if (!prototypesFolder.isPresent()) {
            throw new IllegalStateException("No Prototypes folder in library file");
        }
        Optional<IFolder> libraryFolder = getLibraryFolder();
        if (!libraryFolder.isPresent()) {
            return;
        }
        IFolder prototypes = prototypesFolder.get();
        IFolder library = libraryFolder.get();
        for (IFolderMember libraryElement : library.getMembers()) {
            library.removeMember(libraryElement);
        }
        for (IFolderMember prototypeElement : prototypes.getMembers()) {
            prototypeElement.setParentFolder(null);
            library.addMember(prototypeElement);
            prototypeElement.setParentFolder(this.getLibraryFolder().get());
        }
    }

    public void refreshFromLibrary(CommandStack commandStack) {
        LOG.info("*** refreshing from library ***");
        Optional<IFolder> prototypesFolder = getPrototypesFolder();
        if (!prototypesFolder.isPresent()) {
            return;
        }
        List<IInstance> instances = prototypesFolder.get().getAllInstancesInHierachie();
        ModelSync merger = new ModelSync(instances);
        List<Command> commands = merger.calculateCommands();
        for (Command command : commands) {
            commandStack.execute(command);
        }
    }

    private List<IRecord> getFinalRecords(@NotNull IFolder folder) {

        List<IRecord> result = new ArrayList<IRecord>();

        for (IFolderMember m : folder.getMembers()) {
            if (m instanceof IRecord) {
                Boolean disabled = AliasResolutionUtil.getPropertyViaHierarchy(m, ModelProperty.DISABLED);
                if (disabled != null && !disabled) {
                    result.add((IRecord) m);
                }
            } else if (m instanceof IInstance) {
                result.addAll(getFinalRecords((IInstance) m));
            } else if (m instanceof IFolder) {
                result.addAll(getFinalRecords((IFolder) m));
            }
        }

        return result;
    }

    private List<IRecord> getFinalRecords(@NotNull IInstance instance) {
        List<IRecord> result = new ArrayList<IRecord>();

        for (IRecord r : instance.getRecords()) {
            Boolean disabled = AliasResolutionUtil.getPropertyViaHierarchy(r, ModelProperty.DISABLED);

            if (disabled != null && !disabled) {
                result.add(r);
            }
        }

        for (IInstance i : instance.getInstances()) {
            result.addAll(getFinalRecords(i));
        }

        return result;
    }
    
}
