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
 * $Id: ModuleChannelPrototype.java,v 1.1 2009/08/26 07:08:44 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.config.ioconfig.model.DBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;

import com.google.common.base.Optional;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 25.11.2008
 */
@Entity
@Table(name = "ddb_Channel_Prototype")
public class ModuleChannelPrototypeDBO extends DBClass implements Comparable<ModuleChannelPrototypeDBO> {
    
    /**
     * The byte offset.
     */
    private int _offset;
    
    /**
     * The Name of Prototype.
     */
    private String _name;
    
    /**
     * The Data type.
     */
    private DataType _type;
    
    /**
     * The Bit shift. Lower 1 is no shift.
     */
    private int _shift;
    
    /**
     * Set the Channel to Input or Output. If true the Channel is a Input.
     */
    private boolean _input;
    
    /**
     * Set true can have the Channel a structure.
     */
    private boolean _structure;
    
    /**
     * The Module for this prototype.
     */
    private GSDModuleDBO _gSDModule;
    
    /**
     * The lowest Value.
     */
    private Integer _minimum;
    
    /**
     * The highest Value.
     */
    private Integer _maximum;
    
    /**
     * The Byte ordering.
     */
    private Integer _byteOrdering;
    
    /**
     * Default Constructor. Need by Hibernate.
     */
    public ModuleChannelPrototypeDBO() {
        setOffset(0);
        setType(DataType.INT8);
        setShift(-1);
        setInput(false);
    }
    
    @Override
    public  int compareTo(@CheckForNull final ModuleChannelPrototypeDBO other) {
        if(other==null) {
            return -1;
        }
        if(getId() == other.getId()) {
            return 0;
        }else if(isInput()==other.isInput()) {
            return 1;
        }
        if(getOffset()!=other.getOffset()) {
            return getOffset()-other.getOffset();
        }
        // this is a Error handling
        return getId() - other.getId();
        
    }
    
    @Override
    public final boolean equals(@CheckForNull final Object obj) {
        return super.equals(obj);
    }
    
    @Column(nullable=true)
    @CheckForNull
    public Integer getByteOrdering() {
        return _byteOrdering;
    }
    
    @Transient
    public Optional<ModuleNumber> getModuleNumber() {
        return ModuleNumber.moduleNumber(_gSDModule.getModuleId());
    }
    
    @ManyToOne
    @CheckForNull
    public GSDModuleDBO getGSDModule() {
        return _gSDModule;
    }
    
    @CheckForNull
    public Integer getMaximum() {
        return _maximum;
    }
    
    @CheckForNull
    public Integer getMinimum() {
        return _minimum;
    }
    
    @Column(nullable = false)
    @Nonnull
    public String getName() {
        return _name;
    }
    
    @Column(nullable = false)
    public int getOffset() {
        return _offset;
    }
    
    public int getShift() {
        return _shift;
    }
    
    // Transients
    @Transient
    public int getSize() {
        return getType().getByteSize();
    }
    
    /**
     * 
     * @return the {@link DataType}
     */
    @Column(nullable = false)
    @Nonnull
    public DataType getType() {
        return _type;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Column(nullable=false)
    public boolean isInput() {
        return _input;
    }
    
    @Column(nullable=false)
    public boolean isStructure() {
        return _structure;
    }
    
    @Override
    public void save() throws PersistenceException {
        Repository.saveOrUpdate(this);
    }
    
    public void setByteOrdering(@Nullable final Integer byteOrdering) {
        _byteOrdering = byteOrdering;
    }
    
    public void setGSDModule(@Nullable final GSDModuleDBO module) {
        _gSDModule = module;
    }
    
    /**
     * 
     * @param input
     *            set only <b>true</b> if the channel direction is <b>input</b> <br>
     *            also the direction is input output.
     */
    public void setInput(final boolean input) {
        _input = input;
    }
    
    @Column(nullable=true)
    public void setMaximum(@Nullable final Integer maximum) {
        _maximum = maximum;
    }
    
    @Column(nullable=true)
    public void setMinimum(@Nullable final Integer minimum) {
        _minimum = minimum;
    }
    
    public void setName(@Nonnull final String name) {
        _name = name;
    }
    
    public void setOffset(final int offset) {
        _offset = offset;
    }
    
    public void setShift(final int shift) {
        _shift = shift;
    }
    
    public void setStructure(final boolean structure) {
        _structure = structure;
    }
    
    public void setType(@Nonnull final DataType type) {
        _type = type;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if(_input) {
            sb.append("-i-> ");
        } else {
            sb.append("<-o- ");
        }
        sb.append(_offset).append(":").append(_name);
        return sb.toString();
    }
    
}
