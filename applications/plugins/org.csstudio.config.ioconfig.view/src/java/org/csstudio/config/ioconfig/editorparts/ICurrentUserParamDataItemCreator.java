package org.csstudio.config.ioconfig.editorparts;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.eclipse.swt.widgets.Composite;

public interface ICurrentUserParamDataItemCreator {

    void buildCurrentUserPrmData(@Nonnull final Composite currentUserParamDataComposite) throws IOException;
    
    boolean hasCurrentUserPrmData() throws IOException;
    
    void undoSelectionCurrentUserPrmData();
    
}
