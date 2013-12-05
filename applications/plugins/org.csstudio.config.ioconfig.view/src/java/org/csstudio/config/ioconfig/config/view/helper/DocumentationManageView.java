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
 * $Id: DocumentationManageView.java,v 1.10 2010/08/20 13:33:00 hrickens Exp $
 */
package org.csstudio.config.ioconfig.config.view.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.IHasDocumentableObject;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.IOConfigActivatorUI;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.10 $
 * @since 20.02.2008
 */
public class DocumentationManageView extends Composite {

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class AddAllDocumentsSelectionListener implements SelectionListener {
        /**
         * Constructor.
         */
        protected AddAllDocumentsSelectionListener() {
            // Constructor.
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            addAll();
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            addAll();
        }

        private void addAll() {
            getDocumentSelected().addAll(getDocumentResource());
            getDocumentResource().clear();
            setDocSelectedTableInput();
            setDocResourceTableInput();
            setSaveButton();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class AddDocSelectionListener implements SelectionListener {

        /**
         * Constructor.
         */
        public AddDocSelectionListener() {
            // Constructor.
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            doAddDoc();
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            doAddDoc();
        }

        @SuppressWarnings("unchecked")
        private void doAddDoc() {
            final IStructuredSelection sSelect = (IStructuredSelection) documentResourcesTableViewer.getSelection();
            getDocumentResource().removeAll(sSelect.toList());
            getDocumentSelected().addAll(sSelect.toList());
            setDocSelectedTableInput();
            setDocResourceTableInput();
            setSaveButton();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private static final class FilterModifyListener implements ModifyListener {
        private final Text _search;
        private final ViewerFilterExtension _filter;
        private final TableViewer _targetTableViewer;

        /**
         * Constructor.
         * 
         * @param search
         * @param filter
         * @param docResorceTableViewer
         */
        protected FilterModifyListener(@Nonnull final Text search, @Nonnull final ViewerFilterExtension filter,
                @Nonnull final TableViewer targetTableViewer) {
            _search = search;
            _filter = filter;
            _targetTableViewer = targetTableViewer;
        }

        @Override
        public void modifyText(@Nonnull final ModifyEvent e) {
            _filter.setFilterText(_search.getText());
            _targetTableViewer.refresh();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class RefreshDocumnetsSelectionListener implements SelectionListener {
        /**
         * Constructor.
         */
        protected RefreshDocumnetsSelectionListener() {
            // Constructor
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            refreshDocuments();
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            refreshDocuments();
        }

        private void refreshDocuments() {
            try {
                setDocumentResource(Repository.loadDocument(true));
                setDocResourceTableInput();
            } catch (final PersistenceException e) {
                DeviceDatabaseErrorDialog.open(null, "Can't load Documents!", e);
                LOG.error("Can't load Documents!", e);
            }
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class RemoveAllDocumentsSelectionListener implements SelectionListener {
        /**
         * Constructor.
         */
        protected RemoveAllDocumentsSelectionListener() {
            // Constructor.
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            getDocumentResource().addAll(getDocumentSelected());
            getDocumentSelected().clear();
            setDocSelectedTableInput();
            setDocResourceTableInput();
            setSaveButton();
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            getDocumentResource().addAll(getDocumentSelected());
            getDocumentSelected().clear();
            setDocSelectedTableInput();
            setDocResourceTableInput();
            setSaveButton();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class RemoveSelectionListener implements SelectionListener {

        /**
         * Constructor.
         * 
         * @param docAvailableTableViewer
         */
        public RemoveSelectionListener() {
            // Constructor.
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            doRemoveDoc();
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            doRemoveDoc();
        }

        @SuppressWarnings("unchecked")
        private void doRemoveDoc() {
            final IStructuredSelection sSelect = (IStructuredSelection) selectedDocumentsTableViewer.getSelection();
            getDocumentResource().addAll(sSelect.toList());
            getDocumentSelected().removeAll(sSelect.toList());
            setDocSelectedTableInput();
            setDocResourceTableInput();
            setSaveButton();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.10 $
     * @since 07.08.2009
     */
    protected static final class ViewerFilterExtension extends ViewerFilter {
        private String _filterString = "";

        @Override
        public boolean select(@Nullable final Viewer viewer, @Nullable final Object parentElement,
                @Nullable final Object element) {
            if (element instanceof IDocument) {
                boolean status = false;
                final IDocument doc = (IDocument) element;

                final String subject = doc.getSubject();
                if (subject != null) {
                    status |= subject.toLowerCase().contains(_filterString.toLowerCase());
                }
                final String mimeType = doc.getMimeType();
                if (mimeType != null) {
                    status |= mimeType.toLowerCase().contains(_filterString.toLowerCase());
                }
                final String desclong = doc.getDesclong();
                if (desclong != null) {
                    status |= desclong.toLowerCase().contains(_filterString.toLowerCase());
                }
                final Date createdDate = doc.getCreatedDate();
                if (createdDate != null) {
                    status |= createdDate.toString().contains(_filterString);
                }
                final String keywords = doc.getKeywords();
                if (keywords != null) {
                    status |= keywords.toLowerCase().contains(_filterString.toLowerCase());
                }
                return status;
            }
            return false;
        }

        public void setFilterText(@Nonnull final String filterString) {
            _filterString = filterString;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(DocumentationManageView.class);

    private TableViewer selectedDocumentsTableViewer;
    
    private TableViewer documentResourcesTableViewer;

    private List<DocumentDBO> selectedDocuments = new ArrayList<DocumentDBO>();
    
    private List<DocumentDBO> allDocumentResources;
    
    private List<DocumentDBO> documentResources;

    // list with documents before any changes to the selection were made
    private ArrayList<DocumentDBO> originalDocumentSelection;

    private boolean isActivate;
    private final Composite mainComposite;
    
    private final IHasDocumentableObject parentNode;

    //@formatter:off
    public DocumentationManageView(
            @Nonnull final Composite parent, 
            final int style,
            @Nonnull final IHasDocumentableObject parentNode) {
            //@formatter:on

        super(parent, style);
        this.setLayout(new GridLayout(1, false));
        final GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        this.setLayoutData(layoutData);
        this.parentNode = parentNode;

        // -Body
        final GridLayoutFactory fillDefaults = GridLayoutFactory.fillDefaults();
        final ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        fillDefaults.numColumns(3);
        scrolledComposite.setLayout(fillDefaults.create());

        mainComposite = new Composite(scrolledComposite, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        fillDefaults.numColumns(3);
        mainComposite.setLayout(fillDefaults.create());

        scrolledComposite.setContent(mainComposite);
        scrolledComposite.setMinSize(700, 250);

        makeSearchDocTable();
        makeChooser();
        makeAvailableDocTable();
    }

    public final void cancel() {
        if (documentResources != null) {
            documentResources.addAll(selectedDocuments);
        }
        selectedDocuments.clear();
        if (originalDocumentSelection != null) {
            selectedDocuments.addAll(originalDocumentSelection);
            if (documentResources != null) {
                documentResources.removeAll(originalDocumentSelection);
            }
        }
        setDocSelectedTableInput();
        setDocResourceTableInput();
    }

    /**
     * @return all available Documents.
     */
    @Nonnull
    public final Set<DocumentDBO> getDocuments() {
        final Set<DocumentDBO> set = new HashSet<DocumentDBO>(selectedDocuments);
        return set;
    }

    public final void onActivate() {
        if (!isActivate) {
            refresh();
        }
    }

    /**
     * @param chosserComposite
     */
    private void buildAddAllButton(@Nonnull final Composite chosserComposite) {
        final Button addAllButton = new Button(chosserComposite, SWT.PUSH);
        addAllButton.setText(">>");
        addAllButton.setToolTipText("Add all Documents");
        addAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addAllButton.addSelectionListener(new AddAllDocumentsSelectionListener());
    }

    /**
     * @param chosserComposite
     */
    private void buildAddButton(@Nonnull final Composite chosserComposite) {
        final Button addButton = new Button(chosserComposite, SWT.PUSH);
        addButton.setText(">");
        addButton.setToolTipText("Add all selceted Documents");
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addButton.addSelectionListener(new AddDocSelectionListener());
    }

    /**
     * @param chosserComposite
     */
    private void buildNewDocButton(@Nonnull final Composite chosserComposite) {
        
        final Button addNewDocButton = new Button(chosserComposite, SWT.PUSH);
        
        addNewDocButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addNewDocButton.setText("<New");
        addNewDocButton.setToolTipText("Add a new Document from the File-System");
        addNewDocButton.setToolTipText("Add a new Document to the Database");
        addNewDocButton.setEnabled(true);
        
        Runnable newDocumentAddedCallback = new Runnable() {           
            @Override
            public void run() {
                try {
                    allDocumentResources = new ArrayList<DocumentDBO>(Repository.loadDocument(true));
                    Display.getCurrent().asyncExec(new Runnable() {                        
                        @Override
                        public void run() {
                            refresh();
                        }
                    });
                } catch (PersistenceException e) {
                    e.printStackTrace();
                }
            }
        };
        
        addNewDocButton.addSelectionListener(DocumentTableViewerBuilder.getAddFile2DBSelectionListener(
                null,
                Optional.of(newDocumentAddedCallback)));
    }

    /**
     * @param chosserComposite
     */
    private void buildRemoveAllButton(@Nonnull final Composite chosserComposite) {
        final Button removeAllButton = new Button(chosserComposite, SWT.PUSH);
        removeAllButton.setText("<<");
        removeAllButton.setToolTipText("Remove all Documents");
        removeAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        removeAllButton.addSelectionListener(new RemoveAllDocumentsSelectionListener());
    }

    /**
     * @param chosserComposite
     */
    private void buildRemoveButton(@Nonnull final Composite chosserComposite) {
        final Button removeButton = new Button(chosserComposite, SWT.PUSH);
        removeButton.setText("<");
        removeButton.setToolTipText("Remove all selceted Documents");
        removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        removeButton.addSelectionListener(new RemoveSelectionListener());
    }

    private void makeAvailableDocTable() {
        final Composite availableGroup = makeGroup("Selected");
        selectedDocumentsTableViewer = DocumentTableViewerBuilder.createDocumentTable(availableGroup, false);
        DocumentTableViewerBuilder.makeMenus(selectedDocumentsTableViewer);
    }

    private void makeChooser() {
        final Composite chosserComposite = new Composite(mainComposite, SWT.NONE);
        final GridData layoutData = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
        chosserComposite.setLayoutData(layoutData);
        final GridLayoutFactory fillDefaults = GridLayoutFactory.fillDefaults();
        final GridLayout create = fillDefaults.create();
        create.marginTop = 15;
        chosserComposite.setLayout(create);

        final Button refreshButton = new Button(chosserComposite, SWT.FLAT);
        refreshButton.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false));
        refreshButton.setImage(IOConfigActivatorUI.getImageDescriptor("icons/refresh.gif").createImage());
        refreshButton.setToolTipText("Refresh List of Documents");
        refreshButton.addSelectionListener(new RefreshDocumnetsSelectionListener());
        Label label = new Label(chosserComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

        buildNewDocButton(chosserComposite);
        new Label(chosserComposite, SWT.NONE).setText("");
        buildAddAllButton(chosserComposite);
        buildAddButton(chosserComposite);
        buildRemoveButton(chosserComposite);
        buildRemoveAllButton(chosserComposite);

        label = new Label(chosserComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

    }

    @Nonnull
    private Composite makeGroup(@Nonnull final String groupHead) {
        final Group searchGroup = new Group(mainComposite, SWT.NO_SCROLL);
        final GridLayout layout = new GridLayout(1, true);
        searchGroup.setLayout(layout);
        searchGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        searchGroup.setText(groupHead);
        return searchGroup;
    }

    /**
     * Generate the Table with the received Documents.
     */
    private void makeSearchDocTable() {
        final ViewerFilterExtension filter = new ViewerFilterExtension();

        // GROUP Layout
        final Composite searchGroup = makeGroup("Search");

        final Text search = new Text(searchGroup, SWT.SINGLE | SWT.SEARCH | SWT.LEAD | SWT.BORDER);
        search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        search.setMessage("Filter");

        documentResourcesTableViewer = DocumentTableViewerBuilder.createDocumentTable(searchGroup, false);

        search.addModifyListener(new FilterModifyListener(search, filter, documentResourcesTableViewer));
        try {
            allDocumentResources = new ArrayList<DocumentDBO>(Repository.loadDocument(false));
            documentResources = new ArrayList<DocumentDBO>(allDocumentResources);
        } catch (final PersistenceException e) {
            documentResources = new ArrayList<DocumentDBO>();
            DeviceDatabaseErrorDialog.open(null, "Can't load Documents!", e);
            LOG.error("Can't load Documents!", e);
        }
        documentResourcesTableViewer.addFilter(filter);
        documentResourcesTableViewer.setFilters(new ViewerFilter[] { filter });
        TableViewerEditor.create(documentResourcesTableViewer, new ColumnViewerEditorActivationStrategy(
                documentResourcesTableViewer), ColumnViewerEditor.DEFAULT);

        DocumentTableViewerBuilder.makeMenus(documentResourcesTableViewer);
    }

    public void refresh() {
        final IDocumentable node = parentNode.getDocumentableObject();
        if (node != null) {
            setDocs(node.getDocuments());
            isActivate = true;
        }
    }

    private void setDocs(@CheckForNull final Set<DocumentDBO> set) {
        originalDocumentSelection = new ArrayList<DocumentDBO>();
        selectedDocuments = new ArrayList<DocumentDBO>();
        if (set != null) {
            originalDocumentSelection.addAll(set);
            selectedDocuments.addAll(set);
            setDocSelectedTableInput();
            if (documentResources != null) {
                documentResources.clear();
                documentResources.addAll(allDocumentResources);
                documentResources.removeAll(set);
            }
        }
        setDocResourceTableInput();
    }

    @Nonnull
    protected final List<DocumentDBO> getDocumentSelected() {
        return selectedDocuments;
    }

    @Nonnull
    protected final List<DocumentDBO> getDocumentResource() {
        return documentResources;
    }

    protected final void setDocSelectedTableInput() {
        selectedDocumentsTableViewer.setInput(selectedDocuments);
    }

    protected final void setDocResourceTableInput() {
        documentResourcesTableViewer.setInput(documentResources);
    }

    protected final void setDocumentResource(@Nonnull final List<DocumentDBO> documentResorce) {
        documentResources = documentResorce;
    }

    protected final void setSaveButton() {
        if (originalDocumentSelection.size() == selectedDocuments.size()) {
            final ArrayList<DocumentDBO> temp = new ArrayList<DocumentDBO>(originalDocumentSelection);
            temp.removeAll(selectedDocuments);
            parentNode.setSavebuttonEnabled("documents", temp.size() != 0);
        } else {
            parentNode.setSavebuttonEnabled("documents", true);
        }
    }

}
