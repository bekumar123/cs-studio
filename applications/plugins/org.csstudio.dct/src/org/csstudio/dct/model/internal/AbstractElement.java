package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IRootFolder;
import org.csstudio.dct.util.CompareUtil;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.NotUnique;
import org.csstudio.dct.util.Nullable;
import org.csstudio.dct.util.Unique;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * Standard implementation of {@link IElement}.
 * 
 * @author Sven Wende
 * 
 */
public abstract class AbstractElement implements IElement, IAdaptable, Serializable {
    private static final long serialVersionUID = 6033398826670082191L;

    @Nullable
    @NotUnique
    private String name;

    @NotNull
    @Unique
    private UUID id;

    public AbstractElement() {
    }

    public AbstractElement(@Nullable @NotUnique String name) {
        this.name = name;
        id = UUID.randomUUID();
    }

    public AbstractElement(@Nullable @NotUnique String name, @Unique UUID id) {
        checkNotNull(id);
        this.name = name;
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @NotUnique
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * name is null if the XmlElement containts {inherited}
     */
    public final void setName(@Nullable @NotUnique String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public final UUID getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof AbstractElement) {
            AbstractElement element = (AbstractElement) obj;

            if (CompareUtil.equals(getName(), element.getName())) {
                if (CompareUtil.equals(getId(), element.getId())) {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public final Object getAdapter(@NotNull Class adapter) {
        checkNotNull(adapter);
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

}
