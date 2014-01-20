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
 * $Id: GSDModule.java,v 1.3 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.csstudio.config.ioconfig.model.DBClass;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.types.ModuleInfo;
import org.csstudio.config.ioconfig.model.types.ModuleLabel;
import org.csstudio.config.ioconfig.model.types.ModuleName;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ModuleVersionInfo;
import org.hibernate.annotations.BatchSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * The Hibernate Persistence DataModel for the Profibus GSD Module.
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 02.10.2008
 */

@Entity
@Table(name = "ddb_GSD_Module", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "gSDFile_Id", "moduleId" }) })
public class GSDModuleDBO extends DBClass implements Comparable<GSDModuleDBO>, IDocumentable, GSDModuleDBOReadOnly {

    protected static final Logger LOG = LoggerFactory.getLogger(GSDModuleDBO.class);
    
    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 16.05.2011
     */
    private static final class ComparatorImplementation implements Comparator<ModuleChannelPrototypeDBO>, Serializable {
        
        private static final long serialVersionUID = 1L;
        /**
         * Constructor.
         */
        public ComparatorImplementation() {
            // Constructor.
        }

        @Override
        public int compare(@Nonnull final ModuleChannelPrototypeDBO o1, @Nonnull final ModuleChannelPrototypeDBO o2) {
            if(o1.isInput()&&!o2.isInput()) {
                return -1;
            }
            if(!o1.isInput()&&o2.isInput()) {
                return 1;
            }
            if(o1.getOffset()!=o2.getOffset()) {
                return o1.getOffset()-o2.getOffset();
            }
            // this is a Error handling
            LOG.warn("GSDModule sort is invalid");
            return o1.getId() - o2.getId();
        }
    }

    /**
     * The Module Name.
     */
    private String _name;

    private int _moduleId;

    private Set<ModuleChannelPrototypeDBO> _moduleChannelPrototypes;

    private GSDFileDBO _gsdFile;

    private boolean _isDirty;

    private Set<DocumentDBO> _documents;

    private String _versionTag;
    
    private String _versionNote;
    
    /**
     * Default Constructor needed by Hibernate.
     */
    public GSDModuleDBO() {
        _isDirty = false;
    }

    /**
     * Constructor.
     *
     * @param name
     *            The Module name.
     */
    public GSDModuleDBO(@Nonnull final String name) {
        this();
        setName(name);
    }

    public GSDModuleDBO(final ModuleInfo info, final GSDFileDBO gsdFileDBO) {
        this();
        
        Preconditions.checkNotNull(info, "info must not be null");
        Preconditions.checkNotNull(gsdFileDBO, "gsdFileDBO must not be null");

        assignCreated();
        assingUpdated();
        
        setGSDFile(gsdFileDBO);
        setModuleId(info.getModuleNumber().getValue());
        setName(info.getModuleName().getValue());
    }

    public int getModuleId() {
        return _moduleId;
    }

    @Transient
    public ModuleNumber getModuleNumber() {
        return ModuleNumber.moduleNumber(_moduleId).get();
    }

    @Transient
    public boolean isVersioned() {
        return getModuleNumber().isVersioned();
    }

    public void setModuleId(final int moduleId) {
        _moduleId = moduleId;
    }

    /**
     *
     * @return the parent {@link GSDFileDBO}.
     */
    @ManyToOne
    @CheckForNull
    public GSDFileDBO getGSDFile() {
        return _gsdFile;
    }

    /**
     *
     * @param gsdFile set the parent {@link GSDFileDBO}.
     */
    public void setGSDFile(@Nullable final GSDFileDBO gsdFile) {
        _gsdFile = gsdFile;
    }

    /**
     *
     * @param name
     *            set the Name of this GSD Module.
     */
    @Column(nullable=false)
    public void setName(@Nonnull final String name) {
        this._name = name;
    }

    /**
     *
     * @return the Name of this GSD Module.
     */
    @Nonnull
    public String getName() {
        return _name;
    }

    @Transient
    public boolean moduleNameContains(String filterText) {        
        ModuleLabel moduleLabel = getModuleLabel();
        return moduleLabel.buildLabel().contains(filterText);
    }

    @Transient
    public ModuleLabel getModuleLabel() {
        ModuleVersionInfo moduleVersionInfo = ModuleVersionInfo.build(getModuleNumber(), getVersionTag(), getVersionNote());
        return moduleVersionInfo.getModuleLabel(new ModuleName(getName()));
    }

    @Override
    @Transient
    public GSDModuleDBO cloneAsNewVersion(ModuleNumber nextVersionedModuleNumber, ModuleVersionInfo moduleVersionInfo) {
        
        Preconditions.checkArgument(nextVersionedModuleNumber.isVersioned(),
                "nextVersionedModuleNumber must  be versioned");

        GSDModuleDBO versionedGSDModuleDBO = new GSDModuleDBO();
        versionedGSDModuleDBO.setGSDFile(getGSDFile());
        versionedGSDModuleDBO.setModuleId(nextVersionedModuleNumber.getValue());
        versionedGSDModuleDBO.setName(getName());
        versionedGSDModuleDBO.setVersionTag(moduleVersionInfo.getVersionTag().getValue());
        versionedGSDModuleDBO.setVersionNote(moduleVersionInfo.getVersionNote().getValue());
        versionedGSDModuleDBO.assignCreated();
        versionedGSDModuleDBO.assingUpdated();
        
        return versionedGSDModuleDBO;
    }
    
    public void assignCreated() {
        setCreatedBy("Roger");
        setCreatedOn(new Date());        
    }

    public void assingUpdated() {
        setUpdatedBy("Roger");
        setUpdatedOn(new Date());        
    }

    @Column(name="version_tag", nullable=true)
    public String getVersionTag() {
        return _versionTag;
    }

    public void setVersionTag(String versionTag) {
        this._versionTag = versionTag;
    }

    @Column(name="version_note", nullable=true)
    public String getVersionNote() {
        return _versionNote;
    }

    public void setVersionNote(String versionNote) {
        this._versionNote = versionNote;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "GSDModule", fetch = FetchType.EAGER)
    @BatchSize(size=32)
    @CheckForNull
    public Set<ModuleChannelPrototypeDBO> getModuleChannelPrototype() {
        return _moduleChannelPrototypes;
    }
    
    public void setModuleChannelPrototype(@Nullable final Set<ModuleChannelPrototypeDBO> moduleChannelPrototypes) {
        _moduleChannelPrototypes = moduleChannelPrototypes;
    }
    
    @Transient
    @Nonnull
    public TreeSet<ModuleChannelPrototypeDBO> getModuleChannelPrototypeNH() {
        final TreeSet<ModuleChannelPrototypeDBO> moduleChannelPrototypes = new TreeSet<ModuleChannelPrototypeDBO>(new ComparatorImplementation());
        if(_moduleChannelPrototypes!=null) {
            moduleChannelPrototypes.addAll(_moduleChannelPrototypes);
        }
        return moduleChannelPrototypes;
    }

    public void addModuleChannelPrototype(@Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if (_moduleChannelPrototypes == null) {
            _moduleChannelPrototypes = new TreeSet<ModuleChannelPrototypeDBO>(new ComparatorImplementation());
        }
        _moduleChannelPrototypes.add(moduleChannelPrototype);
        moduleChannelPrototype.setGSDModule(this);
    }

    public void removeModuleChannelPrototype(@Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype) {
        if (_moduleChannelPrototypes == null) {
            return;
        }
        _moduleChannelPrototypes.remove(moduleChannelPrototype);
        moduleChannelPrototype.setGSDModule(null);
    }

    public void removeModuleChannelPrototype(@Nonnull final Collection<ModuleChannelPrototypeDBO> moduleChannelPrototypes) {
        if (_moduleChannelPrototypes == null) {
            return;
        }
        _moduleChannelPrototypes.removeAll(moduleChannelPrototypes);
        for (ModuleChannelPrototypeDBO moduleChannelPrototype : moduleChannelPrototypes) {
            moduleChannelPrototype.setGSDModule(null);
        }
    }

    /**
     * @return is only true if the {@link GSDModuleDBO} persistent at the DB.
     */
    @Transient
    public boolean isExisting() {
        return getId() != 0;
    }

    @Transient
    public int getGsdFileId() {
        final GSDFileDBO gsdFile = getGSDFile();
        return gsdFile==null?-1:gsdFile.getId();
    }
    
    /**
     * @return is only true has this Module unsaved values.
     */
    @Override
    @Transient
    public boolean isDirty() {
        return _isDirty;
    }

    /**
     * @param dirty set true when the Module have unsaved values.
     */
    @Override
    public void setDirty(final boolean dirty) {
        _isDirty = dirty;
    }

    /**
     *  Die Tabellen MIME_FILES und MIME_FILES_DDB_MCPROTOTYPE liegen auf einer anderen DB.
     *  Daher wird hier mit einem Link gearbeitet der folgenden Rechte benoetigt.
     *  -  Fuer MIME_FILES ist das Grant: select.
     *  -  Fuer MIME_FILES_DDB_MCPROTOTYPE ist das Grant: select, insert, update, delete.
     *
     * @return Documents for the Node.
     */
    @Override
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "MIME_FILES_DDB_MCPROTOTYPE_LNK", joinColumns = @JoinColumn(name = "prototype_id", referencedColumnName = "id", unique = true), inverseJoinColumns = @JoinColumn(name = "docs_id", referencedColumnName = "id"))
    @Nonnull
    public Set<DocumentDBO> getDocuments() {
        return _documents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public void setDocuments(@Nonnull final Set<DocumentDBO> documents) {
        _documents = documents;

    }

    /**
     * @return The Name of this GSD Module.
     */
    @Override
    @Nonnull
    public String toString() {
        return getName();
    }

    /**
     * Compare to GSDModule instances on the basis of the DB Key (ID).
     *
     * @param other
     *            the other GSDModule to compare whit this one.
     * @return {@inheritDoc}
     */
    @Override
    public int compareTo(@CheckForNull final GSDModuleDBO other) {
        return other!=null ? getId() - other.getId() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getId();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@CheckForNull final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GSDModuleDBO other = (GSDModuleDBO) obj;
        return (getId() == other.getId());
    }
    

}
