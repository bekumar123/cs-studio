package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.metamodel.PromptGroup;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.util.Immutable;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Represents an implicit base record that contains all standard field
 * definitions for a record definition. Each real record in a model will inherit
 * from a base record.
 * 
 * @author Sven Wende
 * 
 */
public final class BaseRecord implements IRecord {

	private static final long serialVersionUID = 1L;

	@Nullable
	private IRecordDefinition recordDefinition;

	@NotNull
	private List<IRecord> inheritingRecords = new ArrayList<IRecord>();

	@NotNull
	private Map<String, String> fields;

	public BaseRecord(@Nullable IRecordDefinition recordDefinition) {
		setRecordDefinition(recordDefinition);
	}

	/**
	 * Sets the record definition.
	 * 
	 * @param recordDefinition
	 *            the record definition
	 */
	public void setRecordDefinition(@Nullable IRecordDefinition recordDefinition) {
		this.recordDefinition = recordDefinition;

		fields = new LinkedHashMap<String, String>();

		if (recordDefinition != null) {
			for (IFieldDefinition fd : recordDefinition.getFieldDefinitions()) {

				if (fd.getPromptGroup() != null
						&& PromptGroup.UNDEFINED != fd.getPromptGroup()) {
					// determine default value
					String defaultValue = "";

					if (fd.getInitial() != null && fd.getInitial().length() > 0) {
						defaultValue = fd.getInitial();
					} else {
						if (fd.getMenu() != null
								&& fd.getMenu().getChoices() != null
								&& fd.getMenu().getChoices().size() > 0) {
							defaultValue = fd.getMenu().getChoices().get(0)
									.getDescription();
						}
					}

					fields.put(fd.getName(), defaultValue);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addField(String name, String value) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void addProperty(String name, String value) {
	}

	/**
	 * {@inheritDoc}
	 */
	public IContainer getContainer() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getField(@NotNull String name) {
		checkNotNull(name);
		return fields.get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getFields() {
		return fields;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getDefaultFields() {
		return getFields();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getFinalFields() {
		return getFields();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getFinalProperties() {
		return Collections.EMPTY_MAP;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNameFromHierarchy() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEpicsName() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEpicsNameFromHierarchy() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEpicsName(String epicsName) {
	}

	/**
	 * {@inheritDoc}
	 */
	public IRecord getParentRecord() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Immutable
	public Map<String, String> getProperties() {
		return ImmutableMap.copyOf(new HashMap<String, String>());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProperty(String name) {
		throw null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return recordDefinition.getType();
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
	public void removeField(String name) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeProperty(String name) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void setContainer(IContainer container) {
	}

	/**
	 * {@inheritDoc}
	 */
	public UUID getId() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return recordDefinition != null ? recordDefinition.getType() : "??";
	}

	/**
	 * {@inheritDoc}
	 */
	public void setName(String name) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDependentRecord(IRecord record) {
		checkNotNull(record);
		checkArgument(record.getParentRecord() == this,
				"Record must inherit from here.");
		inheritingRecords.add(record);
	}

	/**
	 * {@inheritDoc}
	 */
	@Immutable
	public List<IRecord> getDependentRecords() {
		return ImmutableList.copyOf(inheritingRecords);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDependentRecord(IRecord record) {
		checkNotNull(record);
		checkArgument(record.getParentRecord() == this,
				"Record must inherit from here.");
		inheritingRecords.remove(record);
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	public IRecordDefinition getRecordDefinition() {
		return recordDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasProperty(String name) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IVisitor visitor) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Immutable
	public Map<String, String> getFinalParameterValues() {
		return ImmutableMap.copyOf(new HashMap<String, String>());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAbstract() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Boolean getDisabled() {
		return false;
	}

    @Override
    public IContainer getRootContainer() {
        throw new IllegalStateException("Should not be called");
    }

}
