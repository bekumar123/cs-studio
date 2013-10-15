package org.csstudio.config.ioconfig.config.component;

import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.eclipse.jface.viewers.StructuredSelection;

import com.google.common.base.Optional;

public interface IModuleNumberProvider {
    Optional<ModuleNumber> getModuleNumber();
}
