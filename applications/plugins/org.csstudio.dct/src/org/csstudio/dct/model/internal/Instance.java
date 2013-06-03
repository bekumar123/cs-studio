package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.util.CompareUtil;
import org.csstudio.dct.util.Immutable;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.NotUnique;
import org.csstudio.dct.util.Nullable;
import org.csstudio.dct.util.Unique;

import com.google.common.collect.ImmutableMap;

/**
 * Standard implementation of {@link IInstance}.
 * 
 * An Instance must have a parent of Type Instance or Prototype.
 * 
 * @author Sven Wende
 */
public class Instance extends AbstractContainer implements IInstance {

    private static final long serialVersionUID = -7749937096138079752L;

    private String prototypeFolder;

    @NotNull
    private Map<String, String> parameterValues = new HashMap<String, String>();

    public Instance() {
    }

    public Instance(@NotNull IContainer parent, @NotNull @Unique UUID id) {
        super(null, parent, id);
        checkNotNull(parent);
        checkNotNull(id);
        if (!(parent instanceof Prototype || parent instanceof Instance)) {
            throw new IllegalStateException(
                    "An instance can only have another Instance or a Prototye as it's parent but I got "
                            + parent.getClass().getName());
        }
    }

    public Instance(@NotNull @NotUnique String name, @NotNull IPrototype prototype, @NotNull @Unique UUID id) {
        this(prototype, id);
        checkNotNull(name);
        setName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IRecord> getAllRecordsInHierarchy() {
        return getFinalRecords(this);
    }

    private List<IRecord> getFinalRecords(@NotNull IInstance instance) {
        List<IRecord> result = new ArrayList<IRecord>();
        for (IRecord r : instance.getRecords()) {
            result.add(r);
        }
        for (IInstance i : instance.getInstances()) {
            result.addAll(getFinalRecords(i));
        }
        return result;
    }

    @Override
    public IFolder getRootFolder() {
        IContainer container = getFirstContainerWithParentFolder();
        if (container == null) {
            container = this;
        }
        IFolder folder = container.getParentFolder();
        return folder.getRootFolder();
    }

    private IContainer getFirstContainerWithParentFolder() {
        IContainer container = getContainer();
        while ((container != null) && (container.getParentFolder() == null)) {
            container = container.getContainer();
        }
        return container;
    }

    /**
     * {@inheritDoc}
     */
    @Immutable
    public Map<String, String> getParameterValues() {
        return ImmutableMap.copyOf(parameterValues);
    }

    /**
     * {@inheritDoc}
     */
    public void setParameterValue(@NotNull String key, @Nullable String value) {
        checkNotNull(key);
        if (value != null && value.length() > 0) {
            parameterValues.put(key, value);
        } else {
            parameterValues.remove(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getParameterValue(@NotNull String key) {
        checkNotNull(key);
        return parameterValues.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasParameterValue(@NotNull String key) {
        checkNotNull(key);
        return parameterValues.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public IPrototype getPrototype() {
        IContainer parent = getParent();

        if (parent instanceof IPrototype) {
            return (IPrototype) parent;
        } else {
            if (parent instanceof IInstance) {
                return ((IInstance) parent).getPrototype();
            } else {
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInherited() {
        return getParent() instanceof IInstance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFromLibrary() {
        if (getPrototype() != null) {
            IPrototype prototype = getPrototype();
            IFolder rootFolder = prototype.getRootFolder();
            if (rootFolder == null) {
                return false;
            }
            return rootFolder.isLibraryFolder();
        }
        return false;
    }

    @Override
    public void setPrototypeFolder(String prototypeFolder) {
        this.prototypeFolder = prototypeFolder;
    }

    @Override
    public String getPrototypeFolder() {
        return prototypeFolder;
    }

    /**
     * {@inheritDoc}
     */
    public void accept(@NotNull IVisitor visitor) {
        checkNotNull(visitor);

        visitor.visit(this);

        for (IInstance instance : getInstances()) {
            instance.accept(visitor);
        }

        for (IRecord record : getRecords()) {
            record.accept(visitor);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof Instance) {
            Instance instance = (Instance) obj;

            if (super.equals(obj)) {
                // .. parameter values
                if (getParameterValues().equals(instance.getParameterValues())) {
                    // .. container
                    if (CompareUtil.idsEqual(getContainer(), instance.getContainer())) {
                        // .. folder
                        if (CompareUtil.idsEqual(getParentFolder(), instance.getParentFolder())) {
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
        int result = super.hashCode();
        return result;

    }

    public String toString() {
        // @formatter:off
		return com.google.common.base.Objects.toStringHelper(this)
				.add("name", this.getName())
				.add("id", this.getId())
                .add("protoypeFolder", this.getPrototypeFolder())
				.add("container", this.getContainer())
				.add("parent", this.getParent())
				.toString();
		// @formatter:on
    }

}
