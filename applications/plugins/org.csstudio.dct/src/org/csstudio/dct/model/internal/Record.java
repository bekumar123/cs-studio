package org.csstudio.dct.model.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.util.CompareUtil;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.Nullable;
import org.csstudio.dct.util.Unique;

/**
 * Standard implementation of {@link IRecord}.
 * 
 * Nas no parent only a parentRecord.
 * 
 * @author Sven Wende
 */
public final class Record extends AbstractPropertyContainer implements IRecord {
    private static final long serialVersionUID = -909182136862019398L;

    @NotNull
    private String type;

    @Nullable
    private String epicsName;

    @NotNull
    private Map<String, String> fields = new HashMap<String, String>();

    @NotNull
    private Map<String, Boolean> archived = new HashMap<String, Boolean>();

    @Nullable
    private IRecord parentRecord;

    @Nullable
    private transient IContainer container;

    @NotNull
    private transient List<IRecord> inheritingRecords = new ArrayList<IRecord>();

    @Nullable
    private Boolean disabled;

    @Nullable
    private Boolean recordArchived;

    public Record() {
    }

    public Record(@NotNull String name, @NotNull String type, @NotNull @Unique UUID id) {
        super(name, id);
        checkNotNull(name);
        checkNotNull(type);
        checkNotNull(id);
        this.type = type;
    }

    public Record(IRecord parentRecord, UUID id) {
        super(null, id);
        checkNotNull(id);
        this.parentRecord = parentRecord;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        if ((type == null) && (parentRecord == null)) {
            throw new IllegalStateException("either type or parentRecord must be != null");
        }
        return type != null ? type : parentRecord.getType();
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getFinalProperties() {
        Map<String, String> result = new HashMap<String, String>();

        Stack<IRecord> stack = getRecordStack();

        // add the field values of the parent hierarchy, values can be overriden
        // by children
        while (!stack.isEmpty()) {
            IRecord top = stack.pop();
            result.putAll(top.getProperties());
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void addField(String key, String value) {
        checkNotNull(key);
        checkNotNull(value);
        fields.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public String getField(String key) {
        checkNotNull(key);
        return fields.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public void removeField(String key) {
        checkNotNull(key);
        fields.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public void setFields(Map<String, String> fields) {
        checkNotNull(fields);
        this.fields = fields;
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
    public Map<String, String> getFinalFields() {
        Map<String, String> result = new LinkedHashMap<String, String>();

        Stack<IRecord> stack = getRecordStack();

        while (!stack.isEmpty()) {
            IRecord top = stack.pop();
            result.putAll(top.getFields());
        }

        // result.put("HOPR", "12");

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getDefaultFields() {
        Map<String, String> result = new HashMap<String, String>();

        Stack<IRecord> stack = getRecordStack();

        // add the field values of the parent hierarchy, values can be overriden
        // by children
        if (!stack.isEmpty()) {
            IRecord top = stack.pop();

            if (top instanceof BaseRecord) {
                result.putAll(top.getFields());
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getEpicsName() {
        return epicsName;
    }

    /**
     * {@inheritDoc}
     */
    public void setEpicsName(@Nullable String epicsName) {
        this.epicsName = epicsName;
    }

    /**
     * {@inheritDoc}
     */
    public String getEpicsNameFromHierarchy() {
        String name = "unknown";

        Stack<IRecord> stack = getRecordStack();

        while (!stack.isEmpty()) {
            IRecord top = stack.pop();

            if (top.getEpicsName() != null && top.getEpicsName().length() > 0) {
                name = top.getEpicsName();
            }
        }

        return name;
    }

    /**
     * {@inheritDoc}
     */
    public IRecord getParentRecord() {
        return parentRecord;
    }

    /**
     * {@inheritDoc}
     */
    public IContainer getContainer() {
        return container;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentRecord(@Nullable IRecord parentRecord) {
        this.parentRecord = parentRecord;
    }

    /**
     * {@inheritDoc}
     */
    public void setContainer(@Nullable IContainer container) {
        this.container = container;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAbstract() {
        return getRootContainer(getContainer()) instanceof IPrototype;
    }

    public void setDisabled(@Nullable Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean getDisabled() {
        return disabled;
    }

    @Override
    public Boolean getArchived(String name) {
        checkNotNull(name);
        Boolean value = archived.get(name);
        if (value == null) {
            Boolean parentValue = parentRecord.getArchived(name);
            if (parentValue == null) {
                return false;
            }
            return parentValue;
        } else {
           return value; 
        }
    }

    @Override
    public void setArchived(String name, Boolean value) {
        checkNotNull(name);
        checkNotNull(value);
        archived.put(name, value);
    }

    @Override
    public Boolean getRecordArchived() {
        return this.recordArchived;
    }

    @Override
    public void setRecordArchived(Boolean value) {
        this.recordArchived = value;
    }

    public IContainer getRootContainer() {
        return getRootContainer(getContainer());
    }

    /**
     * Recursive helper method which determines the root container.
     * 
     * @param container
     *            a starting container
     * 
     * @return the root container of the specified starting container
     */
    private IContainer getRootContainer(IContainer container) {
        if (container == null) {
            throw new IllegalStateException("container must not be null");
        }
        if (container.getContainer() != null) {
            return getRootContainer(container.getContainer());
        } else {
            return container;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInherited() {
        IRecord p = getParentRecord();
        boolean result = p != null && !(p instanceof BaseRecord);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void addDependentRecord(IRecord record) {
        checkNotNull(record);
        checkArgument(record.getParentRecord() == this);
        inheritingRecords.add(record);
    }

    /**
     * {@inheritDoc}
     */
    public List<IRecord> getDependentRecords() {
        return inheritingRecords;
    }

    /**
     * {@inheritDoc}
     */
    public void removeDependentRecord(IRecord record) {
        checkNotNull(record);
        checkArgument(record.getParentRecord() == this);
        inheritingRecords.remove(record);
    }

    /**
     * {@inheritDoc}
     */
    public IRecordDefinition getRecordDefinition() {
        IRecord base = getRecordStack().pop();
        return base.getRecordDefinition();
    }

    /**
     * {@inheritDoc}
     */
    public void accept(IVisitor visitor) {
        checkNotNull(visitor);
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof Record) {
            Record record = (Record) obj;

            if (super.equals(obj)) {
                // .. type
                if (CompareUtil.equals(getType(), record.getType())) {
                    // .. fields
                    if (getFields().equals(record.getFields())) {
                        // .. parent record id (we check the id only, to prevent
                        // stack overflows)
                        if (CompareUtil.idsEqual(getParentRecord(), record.getParentRecord())) {
                            // .. container (we check the id only, to prevent
                            // stack overflows)
                            if (CompareUtil.idsEqual(getContainer(), record.getContainer())) {
                                result = true;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public String toString() {
        // @formatter:off
	    return com.google.common.base.Objects.toStringHelper(this)
	            .add("name", this.getName())
	            .add("id", this.getId())
	            .add("container", this.getContainer())
	            .add("parentRecord", this.getParentRecord())
	            .toString();
	            // @formatter:on
    }

    /**
     * Collect all parent records in a stack. On top of the returned stack is
     * the parent that resides at the top of the hierarchy.
     * 
     * @return all parent records, including this
     */
    private Stack<IRecord> getRecordStack() {
        Stack<IRecord> stack = new Stack<IRecord>();

        IRecord r = this;

        while (r != null) {
            stack.add(r);
            r = r.getParentRecord();
        }
        return stack;
    }

}
