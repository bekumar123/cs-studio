package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.NotUnique;
import org.csstudio.dct.util.Nullable;
import org.csstudio.dct.util.Unique;

import com.google.common.collect.ImmutableList;

/**
 * Standard implementation of {@link IFolder}.
 * 
 * A folder has no parent, but a folder has a parentFolder.
 * 
 * @author Sven Wende
 */
public class Folder extends AbstractElement implements IFolderMember, IFolder {

    private static final long serialVersionUID = 1L;

    private static final String INSTANCES_FOLDER_NAME = "Instances";

    private static final String PROTOTYPES_FOLDER_NAME = "Prototypes";

    private static final String LIBRARY_FOLDER_NAME = "Library";

    public static final Folder INSTANCES = new Folder(INSTANCES_FOLDER_NAME);

    public static final Folder PROTOTYPES = new Folder(PROTOTYPES_FOLDER_NAME);

    public static final Folder LIBRARY = new Folder(LIBRARY_FOLDER_NAME);

    @NotNull
    private List<IFolderMember> members = new ArrayList<IFolderMember>();

    @Nullable
    private IFolder parentFolder;

    public Folder(@NotNull @NotUnique String name) {
        super(name);
        checkNotNull(name);
    }

    public Folder(@NotNull @NotUnique String name, @NotNull @Unique UUID id) {
        super(name, id);
        checkNotNull(name);
        checkNotNull(id);
    }

    /**
     * {@inheritDoc}
     */
    public final List<IFolderMember> getMembers() {
        return ImmutableList.copyOf(members);
    }

    /**
     * {@inheritDoc}
     */
    public final void addMember(IFolderMember member) {
        checkNotNull(member);
        checkArgument(member.getParentFolder() == null);
        members.add(member);
    }

    /**
     * {@inheritDoc}
     */
    public final void setMember(int index, IFolderMember member) {
        checkArgument(index >= 0);
        checkNotNull(member);
        // .. fill with nulls
        while (index >= members.size()) {
            members.add(null);
        }
        members.set(index, member);
    }

    /**
     * {@inheritDoc}
     */
    public final void addMember(int index, IFolderMember member) {
        checkArgument(index >= 0);
        checkNotNull(member);
        checkArgument(member.getParentFolder() == null);
        members.add(index, member);
    }

    /**
     * {@inheritDoc}
     */
    public final void removeMember(IFolderMember member) {
        checkArgument(member.getParentFolder() == this);
        members.remove(member);
    }

    /**
     * {@inheritDoc}
     */
    public final void removeMember(int index) {
        IFolderMember member = members.remove(index);
        checkArgument(member.getParentFolder() == this);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    public final IFolder getParentFolder() {
        return parentFolder;
    }

    /**
     * {@inheritDoc}
     */
    public final void setParentFolder(@Nullable IFolder folder) {
        parentFolder = folder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Folder) {
            Folder instance = (Folder) obj;
            if (super.equals(obj)) {
                // .. members
                if (getMembers().equals(instance.getMembers())) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((members == null) ? 0 : members.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isInherited() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public final void accept(IVisitor visitor) {
        checkNotNull(visitor);
        visitor.visit((Folder) this);
        for (IFolderMember member : getMembers()) {
            member.accept(visitor);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IProject getProject() {
        if (this instanceof IProject) {
            return (IProject) this;
        } else {
            return parentFolder.getProject();
        }
    }

    @Override
    public boolean isPrototypesFolder() {
        return this.getName().equals(Folder.PROTOTYPES.getName());
    }

    @Override
    public boolean isInstancesFolder() {
        return this.getName().equals(Folder.INSTANCES.getName());
    }

    @Override
    public boolean isLibraryFolder() {
        return this.getName().equals(Folder.LIBRARY.getName());
    }

    @Override
    public IFolder getRootFolder() {
        IFolder folder = this;
        while ((folder != null) && (!folder.isRootFolder())) {
            folder = folder.getParentFolder();
        }
        return folder;
    }

    @Override
    public boolean isRootFolder() {
        return isPrototypesFolder() || isInstancesFolder() || isLibraryFolder();
    }
}
