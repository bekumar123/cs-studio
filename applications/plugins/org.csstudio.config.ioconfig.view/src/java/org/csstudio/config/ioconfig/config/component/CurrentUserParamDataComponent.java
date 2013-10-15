package org.csstudio.config.ioconfig.config.component;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.editorparts.ICurrentUserParamDataItemCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class CurrentUserParamDataComponent implements IComponent {

    protected static final Logger LOG = LoggerFactory.getLogger(CurrentUserParamDataComponent.class);

    private final Composite topGroup;
    private final ICurrentUserParamDataItemCreator currentUserParamDataItemCreator;

    private Group currentUserParamDataGroup;
    private Composite currentUserParamDataComposite;

    // @Formatter:off
    public CurrentUserParamDataComponent(@Nonnull final Composite topGroup,
            @Nonnull final ICurrentUserParamDataItemCreator currentUserParamDataItemCreator) {
        Preconditions.checkNotNull(topGroup, "topGroup must not be null");
        Preconditions.checkNotNull(currentUserParamDataItemCreator, "currentUserParamDataItemCreator must not be null");

        this.topGroup = topGroup;
        this.currentUserParamDataItemCreator = currentUserParamDataItemCreator;
    }

    @Override
    public void buildComponent() {

        currentUserParamDataGroup = new Group(topGroup, SWT.NONE);
        final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        layoutData.minimumWidth = 150;
        
        currentUserParamDataGroup.setLayoutData(layoutData);
        currentUserParamDataGroup.setLayout(new FillLayout());
        currentUserParamDataGroup.setText("Current User Param Data");

        final ScrolledComposite scrollComposite = new ScrolledComposite(currentUserParamDataGroup, SWT.V_SCROLL);

        currentUserParamDataComposite = new Composite(scrollComposite, SWT.NONE);
        final RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
        rowLayout.wrap = false;
        rowLayout.fill = true;

        currentUserParamDataComposite.setLayout(rowLayout);
        scrollComposite.setContent(currentUserParamDataComposite);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);

        currentUserParamDataGroup.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(@Nullable final ControlEvent e) {
                updateScrolledComposite(scrollComposite);
            }
        });
        scrollComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(@Nullable final ControlEvent e) {
                updateScrolledComposite(scrollComposite);
            }
        });

        try {
            currentUserParamDataItemCreator.buildCurrentUserPrmData(currentUserParamDataComposite);
        } catch (IOException e) {
            LOG.error("Error", e);
        }

        topGroup.layout();
    }

    public void undo() {
        currentUserParamDataItemCreator.undoSelectionCurrentUserPrmData();
    }
    
    private void updateScrolledComposite(ScrolledComposite scrollComposite) {
        final Rectangle r = scrollComposite.getClientArea();
        scrollComposite.setMinSize(currentUserParamDataComposite.computeSize(r.width, SWT.DEFAULT));
    }

    public void dispose() {
        currentUserParamDataGroup.dispose();
    }

}
