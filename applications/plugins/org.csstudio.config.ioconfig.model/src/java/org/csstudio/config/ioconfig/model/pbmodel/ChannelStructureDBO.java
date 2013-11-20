/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id: ChannelStructure.java,v 1.8 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.INodeVisitor;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.NodeType;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.hibernate.annotations.BatchSize;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.8 $
 * @since 18.12.2008
 */
@Entity
@BatchSize(size=32)
@Table(name = "ddb_Profibus_Channel_Structure")
public class ChannelStructureDBO extends AbstractNodeSharedImpl<ModuleDBO, ChannelDBO> {

    private static final long serialVersionUID = 1L;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private String _structureType;
    private boolean _simple;

    /**
     * Default Constructor used only by Hibernate. To crate a {@link ChannelStructureDBO} use the
     * Factory methods.
     */
    public ChannelStructureDBO() {
        // Constructor for Hibernate
    }

    private ChannelStructureDBO(@Nonnull final ModuleDBO module, final boolean simple, final boolean isInput, @Nonnull final DataType type,
                                @Nonnull final String name) throws PersistenceException {
        super(module);
        setSimple(simple);
        setName("Struct of " + name);
        setStructureType(type);
        buildChildren(type, isInput, name);
    }

    /**
     * Constructor.
     * Build ChannelStructure with simple = false and build his children.
     */
    public ChannelStructureDBO(@Nonnull final ModuleDBO module,
                               @Nonnull final ModuleChannelPrototypeDBO channelPrototype) throws PersistenceException {
        super(module);
        setSimple(false);
        setName("Struct of " + channelPrototype.getName());
        setStructureType(channelPrototype.getType());
        buildChildren(channelPrototype);
    }

    @Override
    @Nonnull
    @Transient
    public ModuleDBO getParent() {
        return super.getParent();
    }

    // TODO (hrickens) [27.07.2011]: kann weg. Nur die Tests m�ssen noch umgestellt werden!
    @Nonnull
    public static ChannelStructureDBO makeChannelStructure(@Nonnull final ModuleDBO module, final boolean isInput,
                                                           @Nonnull final DataType type, @Nonnull final String name) throws PersistenceException {
        final ChannelStructureDBO channelStructureDBO = new ChannelStructureDBO(module, false, isInput, type, name);
        channelStructureDBO.buildChildren(type, isInput, name);
        return channelStructureDBO;
    }
    @Nonnull
    public static ChannelStructureDBO makeChannelStructure(@Nonnull final ModuleDBO module,
                                                           @Nonnull final ModuleChannelPrototypeDBO channelPrototype) throws PersistenceException {
        final ChannelStructureDBO channelStructureDBO = new ChannelStructureDBO(module, channelPrototype);
        return channelStructureDBO;
    }

    @SuppressWarnings("unused")
    @Nonnull
    public static ChannelStructureDBO makeSimpleChannel(@Nonnull final ModuleDBO module,
                                                        @Nonnull final String name,
                                                        final boolean isInput,
                                                        final boolean isDigit) throws PersistenceException {
        final ChannelStructureDBO channelStructure = new ChannelStructureDBO(module, true, isInput,
                                                                             DataType.SIMPLE, name);
        new ChannelDBO(channelStructure, name, isInput, isDigit, channelStructure.getSortIndex());
        return channelStructure;
    }

    // CHECKSTYLE OFF: StrictDuplicateCode
    @Override
    public void accept(@Nonnull final INodeVisitor visitor) {
        visitor.visit(this);
    }
    @Override
    public void assembleEpicsAddressString() throws PersistenceException {
        for (final ChannelDBO node : getChildrenAsMap().values()) {
            if (node != null) {
                node.localUpdate();
                if (node.isDirty()) {
                    node.save();
                }
            }
        }
    }

    private void buildChildren(@Nonnull final DataType type,
                               final boolean isInput,
                               @Nonnull final String name) throws PersistenceException {
        if (isSimple()) {
            return;
        }

        final DataType[] structer = type.getStructure();
        for (short sortIndex = 0; sortIndex < structer.length; sortIndex++) {
            final ChannelDBO channel = new ChannelDBO(this,
                                                      name + sortIndex,
                                                      isInput,
                                                      structer[sortIndex].getByteSize() < 8,
                                                      sortIndex);
            // Use setChannelType to reduce the local Updates.
            // Make a local Update after add all Channels.
            channel.setChannelType(structer[sortIndex]);
        }
        getModule().update();
        getModule().update();
    }

    /**
     * @param channelPrototype
     */
    private void buildChildren(@Nonnull final ModuleChannelPrototypeDBO channelPrototype) throws PersistenceException {
        if (isSimple()) {
            return;
        }

        final DataType[] structer = channelPrototype.getType().getStructure();
        final String name = channelPrototype.getName();
        for (short sortIndex = 0; sortIndex < structer.length; sortIndex++) {
            final ChannelDBO channel = new ChannelDBO(this,
                                                      name + sortIndex,
                                                      channelPrototype.isInput(),
                                                      structer[sortIndex].getByteSize() < 8,
                                                      sortIndex);

            // Use setChannelType to reduce the local Updates.
            // Make a local Update after add all Channels.
            channel.setChannelType(structer[sortIndex]);
            channel.setChannelNumber(channel.getNextFreeChannelNumberFromParent(channelPrototype.getOffset()));
        }
        getModule().update();
        getModule().update();
    }

    /**
     * {@inheritDoc}
     * @throws PersistenceException
     */
    @Override
    @Nonnull
    public ChannelStructureDBO copyParameter(@Nonnull final ModuleDBO parentNode) throws PersistenceException {
        final ModuleDBO module = parentNode;
        String name = getName();
        if(name==null){
            name="";
        }
        final ChannelStructureDBO copy = new ChannelStructureDBO(module,
                                                                 isSimple(),
                                                                 true,
                                                                 getStructureType(),
                                                                 name);
        copy.setSortIndex((int) getSortIndex());
        copy.removeAllChild();
        for (final ChannelDBO node : getChildrenAsMap().values()) {
            final ChannelDBO childrenCopy = node.copyThisTo(copy, null);
            childrenCopy.setSortIndexNonHibernate(node.getSortIndex());
        }
        return copy;
    }

    @Override
    @Nonnull
    public ChannelStructureDBO copyThisTo(@Nonnull final ModuleDBO parentNode, @CheckForNull final String namePrefix) throws PersistenceException {
        final ChannelStructureDBO copy = (ChannelStructureDBO) super.copyThisTo(parentNode, namePrefix);
        copy.setName(getName());
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ChannelDBO createChild() throws PersistenceException {
        throw new UnsupportedOperationException("No simple child can be created for node type " + getClass().getName());
    }

    @Override
    public boolean equals(@CheckForNull final Object obj) {
        return super.equals(obj);
    }

    @Transient
    @CheckForNull
    public ChannelDBO getFirstChannel() {
        final TreeMap<Short, ChannelDBO> treeMap = (TreeMap<Short, ChannelDBO>) getChildrenAsMap();
        return treeMap.get(treeMap.firstKey());
    }

    @Transient
    @Override
    public int getFirstFreeStationAddress() throws PersistenceException {
        return isSimple()?getSortIndex():super.getFirstFreeStationAddress();
    }

    @Transient
    @CheckForNull
    public ChannelDBO getLastChannel() {
        final TreeMap<Short, ChannelDBO> treeMap = (TreeMap<Short, ChannelDBO>) getChildrenAsMap();
        return treeMap.isEmpty()?null:treeMap.get(treeMap.lastKey());
    }

    /**
     *
     * @return the parent Module.
     */
    @ManyToOne
    @Nonnull
    public ModuleDBO getModule() {
        return getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    @Nonnull
    public NodeType getNodeType() {
        return NodeType.CHANNEL_STRUCTURE;
    }

    @Nonnull
    public DataType getStructureType() {
        return DataType.valueOf(_structureType);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    // CHECKSTYLE ON: StrictDuplicateCode

    public boolean isSimple() {
        return _simple;
    }

    @Override
    protected void localUpdate() throws PersistenceException {
        final Collection<ChannelDBO> values = getChildrenAsMap().values();
        for (final ChannelDBO channel : values) {
            channel.localUpdate();
        }
    }

    @Override
    public void setCreatedBy(@Nonnull final String createdBy) {
        super.setCreatedBy(createdBy);
        for (final NamedDBClass node : getChildren()) {
            node.setCreatedBy(createdBy);
        }
    }

    @Override
    public void setCreatedOn(@Nonnull final Date createdOn) {
        super.setCreatedOn(createdOn);
        for (final ChannelDBO node : getChildren()) {
            node.setCreatedOn(createdOn);
        }
    }

    /**
     *
     * @param module
     *            the parent Module.
     */
    public void setModule(@Nonnull final ModuleDBO module) {
        this.setParent(module);
    }

    public void setSimple(final boolean simple) {
        _simple = simple;
    }

    public void setStructureType(@CheckForNull final DataType type) {
        if (type == null) {
            _structureType = DataType.BIT.name();
        } else {
            _structureType = type.name();
        }
    }

    @Override
    public void setUpdatedBy(@Nonnull final String updatedBy) {
        super.setUpdatedBy(updatedBy);
        for (final NamedDBClass node : getChildren()) {
            node.setUpdatedBy(updatedBy);
        }
    }

    @Override
    public void setUpdatedOn(@Nonnull final Date updatedOn) {
        super.setUpdatedOn(updatedOn);
        for (final ChannelDBO node : getChildren()) {
            node.setUpdatedOn(updatedOn);
        }
    }

    @Override
    @Nonnull
    public String toString() {
        ChannelDBO channel;
        final StringBuilder sb = new StringBuilder();
        try {
            channel = getChildrenAsMap().get((short) 0);
            if (channel != null) {
                sb.append(channel.getFullChannelNumber());
                sb.append(":");
            }
            sb.append(getName() + " (" + getStructureType().name() + " structure)");

            for (final NamedDBClass node : getChildrenAsMap().values()) {
                sb.append(LINE_SEPARATOR);
                sb.append("\t- ");
                sb.append(node.toString());
            }

        } catch (final PersistenceException e) {
            sb.append("Device Database ERROR: ");
            sb.append(e.getMessage());
        }
        return sb.toString();
    }

}
