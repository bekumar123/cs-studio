package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/*
 * Encapsulate version info about a Prototype.
 * Only a Prototype that is versioned can have a versionTag or a versionNote.
 * The versionTag is required if the Protoype is versioned.
 */
public class ModuleVersionInfo {

    private final ModuleNumber moduleNumber;

    private final Optional<VersionTag> versionTag;
    
    private final Optional<VersionNote> versionNote;

    ModuleVersionInfo(final ModuleNumber moduleNumber, final Optional<VersionTag> versionTag, final Optional<VersionNote> versionNote) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");
        Preconditions.checkNotNull(versionTag, "versionTag must not be null");
        Preconditions.checkNotNull(versionNote, "versionNote must not be null");

        this.moduleNumber = moduleNumber;
        this.versionTag = versionTag;
        this.versionNote = versionNote;
    }

    public static ModuleVersionInfo build(final ModuleNumber moduleNumber, final String versionTag, final String versionNote) {
        
        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");
        
        if (moduleNumber.isVersioned()) {
            return ModuleVersionInfo.buildNewVersion(moduleNumber, versionTag, versionNote);
        } else {
            //@formatter:off
            return new ModuleVersionInfo(moduleNumber, 
                    Optional.<VersionTag>absent(), 
                    Optional.<VersionNote>absent());
                    //@formatter:on            
        }
    }

    public static ModuleVersionInfo buildNewVersion(final ModuleNumber moduleNumber, final String versionTag, final String versionNote) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");
        Preconditions.checkNotNull(versionTag, "versionTag must not be null");
        Preconditions.checkArgument(!versionTag.isEmpty(), "versionTag must not be empty");
        
        //@formatter:off
        return new ModuleVersionInfo(moduleNumber, 
                Optional.of(new VersionTag(versionTag)), 
                Optional.of(new VersionNote(versionNote)));
                //@formatter:on
    }

    public ModuleNumber getModuleNumber() {
        return moduleNumber;
    }
        
    public VersionTag getVersionTag() {
        return versionTag.get();
    }

    public VersionNote getVersionNote() {
        return versionNote.get();
    }
    
    public ModuleLabel getModuleLabel(ModuleName moduleName) {
        //@formatter:off
        return new ModuleLabel(getModuleNumber(),                
                moduleName, 
                versionTag);
                //@formatter:on
    }
    
}
