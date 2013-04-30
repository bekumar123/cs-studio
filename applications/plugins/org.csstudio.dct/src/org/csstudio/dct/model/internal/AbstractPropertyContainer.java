package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.Nullable;

/**
 * Standard implementation of {@link IPropertyContainer}.
 * 
 * @author Sven Wende
 */
public abstract class AbstractPropertyContainer extends AbstractElement implements IPropertyContainer {

	private static final long serialVersionUID = 6872019128384162503L;
	private Map<String, String> properties;

	public AbstractPropertyContainer() {
	}

	// name can be null since an Instance or Record is initialized with name = null
	public AbstractPropertyContainer(@Nullable String name, @NotNull UUID id) {
		super(name, id);
		if (this instanceof IPrototype) {
	        checkNotNull(name);		    
		}
		checkNotNull(id);
		properties = new HashMap<String, String>();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addProperty(String key, String value) {
		checkNotNull(key);
		checkNotNull(value);
		properties.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getProperty(String key) {
		checkNotNull(key);
		return properties.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void removeProperty(String key) {
		checkNotNull(key);
		properties.remove(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean hasProperty(String key) {
		checkNotNull(key);
		return properties.containsKey(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public final Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof AbstractPropertyContainer) {
			AbstractPropertyContainer container = (AbstractPropertyContainer) obj;

			// .. super
			if (super.equals(obj)) {
				// .. properties
				if (getProperties().equals(container.getProperties())) {
					result = true;
				}
			}
		}

		return result;
	}
}
