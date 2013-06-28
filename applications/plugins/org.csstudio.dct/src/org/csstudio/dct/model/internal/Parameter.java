package org.csstudio.dct.model.internal;

import java.io.Serializable;

import org.csstudio.dct.util.CompareUtil;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a parameter with a default value.
 * 
 * @author Sven Wende
 */
public final class Parameter implements Serializable {

    private static final long serialVersionUID = 5417684183030531306L;
	
    @NotNull
    private String name;
    
	private String defaultValue;
	
	private String description;

	public Parameter(@NotNull String name, @Nullable String defaultValue,  @Nullable String description) {
	    checkNotNull(name);
		this.name = name;
		this.defaultValue = defaultValue;
		this.description = description;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the default value.
	 * 
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 * 
	 * @param defaultValue
	 *            the default value
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

    /**
     * Returns the description value.
     * 
     * @return the description value
     */
	public String getDescription() {
        return description;
    }

    /**
     * Sets the description value.
     * 
     * @param description
     *            the description value
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
	 *{@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj instanceof Parameter) {
			Parameter parameter = (Parameter) obj;
			// .. name
			if (CompareUtil.equals(getName(), parameter.getName())) {
				// .. default value
				if (CompareUtil.equals(getDefaultValue(), parameter.getDefaultValue())) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * Clones the parameter.
	 */
	public Parameter clone() {
		return new Parameter(name, defaultValue, description);
	}
}
