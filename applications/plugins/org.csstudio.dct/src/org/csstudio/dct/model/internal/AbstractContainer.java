package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.internal.sync.RecordSync;
import org.csstudio.dct.util.CompareUtil;
import org.csstudio.dct.util.Immutable;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.Nullable;

import com.google.common.collect.ImmutableList;

/**
 * Standard implementation for {@link IContainer}. Base class for
 * {@link Instance} and {@link Prototype}.
 * 
 * @author Sven Wende
 */
public abstract class AbstractContainer extends AbstractPropertyContainer implements IContainer, IFolderMember {

    private static final long serialVersionUID = 1L;

    @Nullable
    private IContainer container;

    /**
     * The parent in the inheritance hierarchy.
     */
    @Nullable
    private IContainer parent;

    /**
     * The folder, this container resides in.
     */
    @NotNull
    private transient IFolder folder;

    /**
     * All containers (instances or prototypes) that inherit from this
     * container.
     */
    @NotNull
    private transient Set<IContainer> dependentContainers = new HashSet<IContainer>();

    /**
     * Contained instances.
     */
    @NotNull
    private List<IInstance> instances = new ArrayList<IInstance>();

    /**
     * Contained records.
     */
    @NotNull
    private List<IRecord> records = new ArrayList<IRecord>();

    public AbstractContainer() {
    }

    public AbstractContainer(@Nullable String name, @Nullable IContainer parent, @NotNull UUID id) {
        super(name, id);
        checkNotNull(id);
        if (this instanceof IPrototype || this instanceof IFolder) {
            checkArgument(parent == null, "only prototypes and folders have no parent");
        } else {
            checkNotNull(parent, "each instance must have a parent");
        }
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    public final IContainer getContainer() {
        return container;
    }

    /**
     * {@inheritDoc}
     */
    public final void setContainer(IContainer container) {
        this.container = container;
    }

    /**
     * {@inheritDoc}
     */
    public final IContainer getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    public final @Immutable
    List<IInstance> getInstances() {
        return ImmutableList.copyOf(instances);
    }

    /**
     * {@inheritDoc}
     */
    public final IInstance getInstance(int index) {
        return instances.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public final Set<IContainer> getDependentContainers() {
        return dependentContainers;
    }

    /**
     * {@inheritDoc}
     */
    public final void addDependentContainer(IContainer container) {
        checkNotNull(container);
        checkArgument(container.getParent() == this);
        dependentContainers.add(container);
    }

    /**
     * {@inheritDoc}
     */
    public final void removeDependentContainer(IContainer container) {
        checkNotNull(container);
        checkArgument(container.getParent() == this);
        dependentContainers.remove(container);
    }

    /**
     * {@inheritDoc}
     */
    public final void addInstance(IInstance instance) {
        checkNotNull(instance);
        checkNotNull(instance.getParent(), "Instance must have a hierarchical parent");
        checkArgument(instance.getContainer() == null, "Instance must not be in a container yet");
        instances.add(instance);
    }

    /**
     * {@inheritDoc}
     */
    public final void setInstance(int index, IInstance instance) {
        checkNotNull(instance);
        checkNotNull(instance.getParent(), "Instance must have a hierarchical parent");
        checkArgument(instance.getContainer() == null, "Instance must not be in a container yet");
        // .. fill with nulls
        while (index >= instances.size()) {
            instances.add(null);
        }
        instances.set(index, instance);
    }

    /**
     * {@inheritDoc}
     */
    public final void addInstance(int index, IInstance instance) {
        checkNotNull(instance);
        checkNotNull(instance.getParent(), "Instance must have a hierarchical parent");
        checkArgument(instance.getContainer() == null, "Instance must not be in a container yet");
        instances.add(index, instance);
    }

    /**
     * {@inheritDoc}
     */
    public final void removeInstance(IInstance instance) {
        checkNotNull(instance);
        if (instance.getContainer() != null && instance.getContainer() != this) {
            if (!(instance.getContainer() == this)) {
                throw new IllegalStateException("The physical container must equal this");
            }
        }
        instances.remove(instance);
    }

    /**
     * {@inheritDoc}
     */
    public final @Immutable
    List<IRecord> getRecords() {
        List<IRecord> result = new ArrayList<IRecord>();
        for (int i=0; i < records.size(); i++) {
            if (records.get(i) != null) {
                result.add(records.get(i));
            }
        }
        if (result.size() != records.size()) {
            records = result;
        }
        return ImmutableList.copyOf(records);
    }

    /**
     * {@inheritDoc}
     */
    public final void addRecord(IRecord record) {
        checkArgument(record.getContainer() == null, "Record must not be part of another container");
        records.add(record);
    }

    /**
     * {@inheritDoc}
     */
    public final void setRecord(int index, IRecord record) {
        checkArgument(record.getContainer() == null, "Record must not be part of another container");
        // .. fill with nulls
        while (index >= records.size()) {
            records.add(null);
        }
        records.set(index, record);
    }

    /**
     * {@inheritDoc}
     */
    public final void addRecord(int index, IRecord record) {
        checkArgument(record.getContainer() == null, "Record must not be part of another container");
        records.add(index, record);
    }

    /**
     * {@inheritDoc}
     */
    public final void removeRecord(IRecord record) {
        checkArgument(record.getContainer() == this, "Record must be part of this container");
        records.remove(record);
    }

    /**
     * {@inheritDoc}
     */
    public final IFolder getParentFolder() {
        return folder;
    }

    /**
     * {@inheritDoc}
     */
    public final IProject getProject() {
        IFolder f;

        if (folder != null) {
            f = folder;
            while (f != null && f.getParentFolder() != null) {
                f = f.getParentFolder();
            }
            if (f == null) {
                throw new IllegalStateException("f must not be null");
            }
            if (!(f instanceof IProject)) {
                throw new IllegalStateException("f must be of type IProject");
            }
            return (IProject) f;
        } else {
            return parent.getProject();
        }

    }

    /**
     * {@inheritDoc}
     */
    public final void setParentFolder(IFolder folder) {
        this.folder = folder;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    public final Map<String, String> getFinalParameterValues() {
        Map<String, String> result = new HashMap<String, String>();

        Stack<IContainer> stack = getParentStack();

        while (!stack.isEmpty()) {
            IContainer top = stack.pop();
            result.putAll(top.getParameterValues());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public final Map<String, String> getFinalProperties() {
        Map<String, String> result = new HashMap<String, String>();

        Stack<IContainer> stack = getParentStack();

        while (!stack.isEmpty()) {
            IContainer top = stack.pop();
            result.putAll(top.getProperties());
        }

        return result;
    }

    /**
     * Collect all parent containers in a stack. On top of the returned stack is
     * the parent that resides at the top of the hierarchy.
     * 
     * @return all parent containers, including this
     */
    protected final Stack<IContainer> getParentStack() {
        Stack<IContainer> stack = new Stack<IContainer>();

        IContainer c = this;

        while (c != null) {
            stack.add(c);
            c = c.getParent();
        }
        return stack;
    }

    /**
     * {@inheritDoc}
     */
    public final @Immutable
    List<IRecordContainer> getDependentRecordContainers() {
        return ImmutableList.copyOf(new ArrayList<IRecordContainer>(dependentContainers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof AbstractContainer) {
            AbstractContainer c = (AbstractContainer) obj;

            // .. super
            if (super.equals(obj)) {
                // .. instances
                if (getInstances().equals(c.getInstances())) {
                    // .. records
                    if (getRecords().equals(c.getRecords())) {
                        // .. parent (we check the id only, to prevent stack
                        // overflows)
                        if (CompareUtil.idsEqual(getParent(), c.getParent())) {
                            result = true;
                        }
                    }
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
        return super.hashCode();
    }
}
