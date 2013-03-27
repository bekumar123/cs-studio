package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.checkArgument;
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
import org.csstudio.dct.util.Immutable;
import org.csstudio.dct.util.NotNull;

import com.google.common.collect.ImmutableList;

/**
 * Standard implementation of {@link IPrototype}.
 * 
 * A Prototype has no parent.
 * 
 * @author Sven Wende
 */
public final class Prototype extends AbstractContainer implements IPrototype {
    private static final long serialVersionUID = 2845048590453820494L;

    @NotNull
    private List<Parameter> parameters = new ArrayList<Parameter>();

    public Prototype() {
    }

    public Prototype(@NotNull String name, @NotNull UUID id) {
        super(name, null, id);
        checkNotNull(name);
        checkNotNull(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Immutable
    List<Parameter> getParameters() {
        return ImmutableList.copyOf(parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void addParameter(@NotNull Parameter parameter) {
        checkNotNull(parameter);
        parameters.add(parameter);
    }

    /**
     * {@inheritDoc}
     */
    public void addParameter(int index, @NotNull Parameter parameter) {
        checkArgument(index >= 0);
        checkNotNull(parameter);
        parameters.add(index, parameter);
    }

    /**
     * {@inheritDoc}
     */
    public void removeParameter(@NotNull Parameter parameter) {
        checkNotNull(parameter);
        parameters.remove(parameter);
    }

    /**
     * {@inheritDoc}
     */
    public void removeParameter(int index) {
        checkArgument(index >= 0);
        parameters.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasParameter(@NotNull String key) {
        checkNotNull(key);
        boolean result = false;
        for (Parameter p : parameters) {
            if (p.getName().equals(key)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getParameterValues() {
        Map<String, String> result = new HashMap<String, String>();

        for (Parameter p : parameters) {
            result.put(p.getName(), p.getDefaultValue());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInherited() {
        return false;
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
        if (obj instanceof Prototype) {
            Prototype prototype = (Prototype) obj;
            if (super.equals(obj)) {
                // .. parameters
                if (getParameters().equals(prototype.getParameters())) {
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
        return getId().hashCode();

    }

    /**
     * {@inheritDoc}
     */
    public String getParameterValue(@NotNull String key) {
        checkNotNull(key);
        return getParameterValues().get(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasParameterValue(@NotNull String key) {
        checkNotNull(key);
        return getParameterValues().get(key) != null;
    }

    /**
     * {@inheritDoc}
     */
    public void setParameterValue(@NotNull String key, @NotNull String value) {
        checkNotNull(key);
        checkNotNull(value);
        for (Parameter p : parameters) {
            if (key.equals(p.getName())) {
                p.setDefaultValue(value);
            }
        }
    }

    @Override
    public void switchParameters(int moveFrom, int moveTo) {
        Parameter first = parameters.get(moveFrom);
        parameters.set(moveFrom, parameters.get(moveTo));
        parameters.set(moveTo, first);
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
   
    public String toString() {
        // @formatter::off
        return com.google.common.base.Objects.toStringHelper(this).add("name", this.getName()).add("id", this.getId())
                .add("container", this.getContainer()).toString();
        // @formatter::on
    }

}
