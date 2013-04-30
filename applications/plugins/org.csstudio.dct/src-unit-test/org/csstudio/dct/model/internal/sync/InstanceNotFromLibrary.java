package org.csstudio.dct.model.internal.sync;

import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.util.NotNull;
import org.csstudio.dct.util.NotUnique;
import org.csstudio.dct.util.Unique;

@SuppressWarnings("serial")
class InstanceNotFromLibrary extends Instance implements IInstance {

    public InstanceNotFromLibrary(@NotNull @NotUnique String name, @NotNull IPrototype prototype,
            @NotNull @Unique UUID id) {
        super(name, prototype, id);
    }

    public boolean isFromLibrary() {
        return false;
    }
}