/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.editorparts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.GSDLabelProvider;
import org.csstudio.config.ioconfig.config.view.helper.ShowFileSelectionListener;
import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.GSDFileTypes;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.AbstractGsdPropertyModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.KeyValuePair;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmText;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.PrmTextItem;
import org.csstudio.config.ioconfig.model.types.BitData;
import org.csstudio.config.ioconfig.model.types.BitPos;
import org.csstudio.config.ioconfig.model.types.BitRange;
import org.csstudio.config.ioconfig.model.types.HighByte;
import org.csstudio.config.ioconfig.model.types.LowByte;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 20.04.2011
 * @param <T>
 *            {@link AbstractNodeSharedImpl} to edited.
 */
public abstract class AbstractGsdNodeEditor<T extends AbstractNodeSharedImpl<?, ?>> extends AbstractNodeEditor<T>
        implements ICurrentUserParamDataItemCreator {

    /**
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class ExtUserPrmDataContentProvider implements IStructuredContentProvider {
        /**
         * Constructor.
         */
        public ExtUserPrmDataContentProvider() {
            // Constructor.
        }

        @Override
        public void dispose() {
            // nothing to dispose
        }

        @Override
        @CheckForNull
        public Object[] getElements(@Nullable final Object inputElement) {
            if (inputElement instanceof ExtUserPrmData) {
                final ExtUserPrmData eUPD = (ExtUserPrmData) inputElement;
                final PrmText prmText = eUPD.getPrmText();
                if (prmText == null) {
                    final PrmTextItem[] prmTextArray = new PrmTextItem[eUPD.getMaxValue() - eUPD.getMinValue() + 1];
                    for (int i = eUPD.getMinValue(); i <= eUPD.getMaxValue(); i++) {
                        prmTextArray[i] = new PrmTextItem(Integer.toString(i), i);
                    }
                    return prmTextArray;
                }
                return prmText.getPrmTextItems().toArray();
            }
            return null;
        }

        @Override
        public void inputChanged(@Nullable final Viewer viewer, @Nullable final Object oldInput,
                @Nullable final Object newInput) {
            // nothing to do.
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @since 14.06.2010
     */
    private final class GSDFileChangeListener implements ISelectionChangedListener {
        private final Button _fileSelect;

        /**
         * Constructor.
         * 
         * @param fileSelect
         */
        protected GSDFileChangeListener(@Nonnull final Button fileSelect) {
            _fileSelect = fileSelect;
        }

        @Override
        public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
            final StructuredSelection selection = (StructuredSelection) event.getSelection();
            if (selection == null || selection.isEmpty()) {
                _fileSelect.setEnabled(false);
                return;
            }
            final GSDFileDBO file = (GSDFileDBO) selection.getFirstElement();
            _fileSelect.setEnabled(getNode().needGSDFile() == GSDFileTypes.Master == file.isMasterNonHN());
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @since 14.06.2010
     */
    private final class GSDFileRemoveListener implements SelectionListener {
        private final TableViewer _tableViewer;
        private final Logger _log = LoggerFactory.getLogger(AbstractGsdNodeEditor.GSDFileRemoveListener.class);

        /**
         * Constructor.
         * 
         * @param tableViewer
         */
        protected GSDFileRemoveListener(@Nonnull final TableViewer tableViewer) {
            _tableViewer = tableViewer;
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            // TODO:
        }

        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            final StructuredSelection selection = (StructuredSelection) _tableViewer.getSelection();
            final GSDFileDBO removeFile = (GSDFileDBO) selection.getFirstElement();

            if (removeFile != null) {
                if (MessageDialog.openQuestion(getShell(), "Datei aus der Datenbank entfernen",
                        "Sind sie sicher das sie die Datei " + removeFile.getName() + " entfernt werden soll")) {
                    try {
                        Repository.removeGSDFiles(removeFile);
                        final List<GSDFileDBO> gsdFiles = getGsdFiles();
                        gsdFiles.remove(removeFile);
                        _tableViewer.setInput(gsdFiles);
                    } catch (final PersistenceException pE) {
                        DeviceDatabaseErrorDialog.open(null, "Can't remove file from Database!", pE);
                        _log.error("Can't remove file from Database!", pE);
                    }
                }
            }

        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @since 14.06.2010
     */
    private final class GSDFileSelectionListener implements SelectionListener {
        private final TableViewer _tableViewer;
        private final Text _tSelected;
        private final Logger _log = LoggerFactory.getLogger(AbstractGsdNodeEditor.GSDFileSelectionListener.class);

        /**
         * Constructor.
         * 
         * @param tableViewer
         * @param tSelected
         */
        protected GSDFileSelectionListener(@Nonnull final TableViewer tableViewer, @Nonnull final Text tSelected) {
            _tableViewer = tableViewer;
            _tSelected = tSelected;
        }

        private void doFileAdd() {
            try {
                setGsdFile((GSDFileDBO) ((StructuredSelection) _tableViewer.getSelection()).getFirstElement());
                final GSDFileDBO gsdFile = getGsdFile();
                if (gsdFile != null) {
                    fill(gsdFile);
                    _tSelected.setText(gsdFile.getName());
                    setSavebuttonEnabled("GSDFile", true);
                }
            } catch (final PersistenceException e) {
                _log.error("Can't read GSDFile! Database error.", e);
                DeviceDatabaseErrorDialog.open(null, "Can't read GSDFile! Database error.", e);
            }
        }

        @Override
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            doFileAdd();
        }

        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            doFileAdd();
        }
    }

    /**
     * {@link PrmTextItem} Label provider that mark the default selection with a
     * '*'. The {@link ExtUserPrmData} give the default.
     * 
     * @author hrickens
     * @author $Author: $
     * @since 18.10.2010
     */
    private final class PrmTextComboLabelProvider extends LabelProvider {

        private final ExtUserPrmData _extUserPrmData;

        /**
         * Constructor.
         * 
         * @param extUserPrmData
         */
        public PrmTextComboLabelProvider(@Nonnull final ExtUserPrmData extUserPrmData) {
            _extUserPrmData = extUserPrmData;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getText(@Nullable final Object element) {
            if (element instanceof PrmTextItem) {
                final PrmTextItem prmText = (PrmTextItem) element;
                if (prmText.getIndex() == _extUserPrmData.getDefault()) {
                    return "*" + element.toString();
                }
            }
            return super.getText(element);
        }
    }

    /**
     * {@link PrmTextItem} Sorter
     * 
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class PrmTextViewerSorter extends ViewerSorter {
        /**
         * Constructor.
         */
        public PrmTextViewerSorter() {
            // Constructor.
        }

        @Override
        public int compare(@Nullable final Viewer viewer, @Nullable final Object e1, @Nullable final Object e2) {
            if (e1 instanceof PrmTextItem && e2 instanceof PrmTextItem) {
                final PrmTextItem eUPD1 = (PrmTextItem) e1;
                final PrmTextItem eUPD2 = (PrmTextItem) e2;
                return eUPD1.getIndex() - eUPD2.getIndex();
            }
            return super.compare(viewer, e1, e2);
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @since 14.06.2010
     */
    private final class ViewerSorterExtension extends ViewerSorter {

        public ViewerSorterExtension() {
            // default constructor
        }

        @Override
        public int compare(@Nullable final Viewer viewer, @Nullable final Object e1, @Nullable final Object e2) {
            if (e1 instanceof GSDFileDBO && e2 instanceof GSDFileDBO) {
                final GSDFileDBO file1 = (GSDFileDBO) e1;
                final GSDFileDBO file2 = (GSDFileDBO) e2;
                final int result = bothFilesMasterOrSlave(file1, file2);
                if (result != 0) {
                    return result;
                }
                switch (getNode().needGSDFile()) {
                case Master: // if master -> master file to top
                    return masters2Top(file1, file2);
                case Slave: // if slave -> slave file to top
                    return slaves2Top(file1, file2);
                default:
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            }
            return super.compare(viewer, e1, e2);
        }

        private int bothFilesMasterOrSlave(@Nonnull final GSDFileDBO file1, @Nonnull final GSDFileDBO file2) {
            int result = 0;
            if (!isMasterOrSalve(file1) && isMasterOrSalve(file2)) {
                result = -1;
            } else if (isMasterOrSalve(file1) && !isMasterOrSalve(file2)) {
                result = 1;
            }
            return result;
        }

        private boolean isMasterOrSalve(@Nonnull final GSDFileDBO file1) {
            return file1.isMasterNonHN() || file1.isSlaveNonHN();
        }

        private int slaves2Top(@Nonnull final GSDFileDBO file1, @Nonnull final GSDFileDBO file2) {
            if (file1.isSlaveNonHN() && !file2.isSlaveNonHN()) {
                return -1;
            } else if (!file1.isSlaveNonHN() && file2.isSlaveNonHN()) {
                return 1;
            }
            return file1.getName().compareToIgnoreCase(file2.getName());
        }

        private int masters2Top(@Nonnull final GSDFileDBO file1, @Nonnull final GSDFileDBO file2) {
            if (file1.isMasterNonHN() && !file2.isMasterNonHN()) {
                return -1;
            } else if (!file1.isMasterNonHN() && file2.isMasterNonHN()) {
                return 1;
            } else {
                return file1.getName().compareToIgnoreCase(file2.getName());
            }
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractGsdNodeEditor.class);
    private final ArrayList<Object> _prmTextCV = new ArrayList<Object>();

    /**
     * @param currentUserParamDataComposite
     * @throws IOException
     */
    public void buildCurrentUserPrmData(@Nonnull final Composite currentUserParamDataComposite) throws IOException {
        final AbstractGsdPropertyModel parsedGsdFileModel = getGsdPropertyModel();
        if (parsedGsdFileModel != null) {
            _prmTextCV.clear();
            final Collection<KeyValuePair> extUserPrmDataRefMap = parsedGsdFileModel.getExtUserPrmDataRefMap().values();
            // extUserPrmDataRef is e.g. Ext_User_Prm_Data_Ref(1) = 30
            for (final KeyValuePair extUserPrmDataRef : extUserPrmDataRefMap) {
                // extUserPrmData is e.g. 30 : Sondenlaenge(Unsigned16)
                final ExtUserPrmData extUserPrmData = parsedGsdFileModel.getExtUserPrmData(extUserPrmDataRef
                        .getIntValue());
                if (extUserPrmData != null) {
                    //@formatter:off
                    final Integer value = getUserPrmDataValue(
                            extUserPrmDataRef.getIndex(), 
                            getPrmUserDataList(),
                            extUserPrmData, 
                            new BitMaskImpl());
                            //@formatter:on
                    makeCurrentUserParamDataItem(currentUserParamDataComposite, extUserPrmData, value);
                }
            }
        }
    }

    public boolean hasCurrentUserPrmData() throws IOException {
        final AbstractGsdPropertyModel parsedGsdFileModel = getGsdPropertyModel();
        if (parsedGsdFileModel == null) {
            return false;
        }
        final Collection<KeyValuePair> extUserPrmDataRefMap = parsedGsdFileModel.getExtUserPrmDataRefMap().values();
        return extUserPrmDataRefMap.size() > 0;
    }

    public void undoSelectionCurrentUserPrmData() {
        for (final Object prmTextObject : _prmTextCV) {
            if (prmTextObject instanceof ComboViewer) {
                cancelComboViewer(prmTextObject);
            } else if (prmTextObject instanceof Text) {
                cancelText(prmTextObject);
            }
        }
    }

    private void createButtonArea(@Nonnull final TabFolder tabFolder, @Nonnull final Composite comp,
            @Nonnull final Text selectedText, @Nonnull final TableViewer gsdFileTableViewer) {
        new Label(comp, SWT.NONE);
        final Button fileSelect = new Button(comp, SWT.PUSH);
        fileSelect.setText("Select");
        fileSelect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileSelect.addSelectionListener(new GSDFileSelectionListener(gsdFileTableViewer, selectedText));
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);
        final Button fileAdd = new Button(comp, SWT.PUSH);
        fileAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileAdd.setText("Add File");
        fileAdd.addSelectionListener(new GSDFileAddListener(this, tabFolder, gsdFileTableViewer, comp));
        final Button fileRemove = new Button(comp, SWT.PUSH);
        fileRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        fileRemove.setText("Remove File");
        fileRemove.addSelectionListener(new GSDFileRemoveListener(gsdFileTableViewer));

        gsdFileTableViewer.addSelectionChangedListener(new GSDFileChangeListener(fileSelect));

        new Label(comp, SWT.NONE);
    }

    /**
     * @param columnNum
     * @param comp
     * @return
     */
    @Nonnull
    private TableViewer createChooserArea(final int columnNum, @Nonnull final Composite comp) {
        final Group gAvailable = new Group(comp, SWT.NONE);
        gAvailable.setText("Available GSD File:");
        gAvailable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, columnNum, 1));
        gAvailable.setLayout(new GridLayout(1, false));

        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        final Composite tableComposite = new Composite(gAvailable, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);

        final TableViewer gsdFileTableViewer = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
                | SWT.FULL_SELECTION);
        gsdFileTableViewer.setContentProvider(new ArrayContentProvider());
        gsdFileTableViewer.setSorter(new ViewerSorterExtension());
        gsdFileTableViewer.setLabelProvider(new GSDLabelProvider(getNode().needGSDFile()));
        gsdFileTableViewer.getTable().setHeaderVisible(false);
        gsdFileTableViewer.getTable().setLinesVisible(false);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(gsdFileTableViewer.getTable());

        try {
            final List<GSDFileDBO> load = Repository.load(GSDFileDBO.class);
            setGsdFiles(load);
        } catch (final PersistenceException e) {
            DeviceDatabaseErrorDialog.open(null, "Can't read GSDFiles from Database!", e);
            LOG.error("Can't read GSDFiles from Database!", e);
        }
        final List<GSDFileDBO> gsdFiles = getGsdFiles();
        if (!gsdFiles.isEmpty()) {
            gsdFileTableViewer.setInput(gsdFiles.toArray(new GSDFileDBO[gsdFiles.size()]));
        }
        return gsdFileTableViewer;
    }

    /**
     * (@inheritDoc)
     */
    @Override
    public void createPartControl(@Nonnull final Composite parent) {
        setParent(parent);
        setBackgroundComposite();
        if (getNode().needGSDFile() != GSDFileTypes.NONE) {
            makeGSDFileChooser(getTabFolder(), "GSD File List");
        }
        documents();
    }

    @Nonnull
    private Text createSelectionArea(final int columnNum, @Nonnull final Composite comp) {
        final Text tSelected;
        final Group gSelected = new Group(comp, SWT.NONE);
        gSelected.setText("Selected GSD File:");
        gSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, columnNum, 1));
        gSelected.setLayout(new GridLayout(1, false));

        tSelected = new Text(gSelected, SWT.SINGLE | SWT.READ_ONLY);
        tSelected.setEditable(false);
        tSelected.setEnabled(false);
        tSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        final GSDFileDBO gsdFile = getGsdFile();
        if (gsdFile != null) {
            setGsdFile(gsdFile);
            tSelected.setText(gsdFile.getName());
        }
        return tSelected;
    }

    /**
     * Fill the View whit data from GSDFile.
     * 
     * @param gsdFile
     *            the GSDFile whit the data.
     * @throws PersistenceException
     */
    public abstract void fill(@Nullable GSDFileDBO gsdFile) throws PersistenceException;

    @CheckForNull
    abstract GSDFileDBO getGsdFile();

    @CheckForNull
    abstract AbstractGsdPropertyModel getGsdPropertyModel() throws IOException;

    @Nonnull
    abstract Integer getPrmUserData(@Nonnull Integer index);

    @Nonnull
    abstract List<Integer> getPrmUserDataList();

    //@formatter:off
    int getUserPrmDataValue(
            @Nonnull final Integer index,
            @Nonnull final List<Integer> prmUserDataList, 
            @Nonnull final ExtUserPrmData extUserPrmData, 
            @Nonnull final BitMask bitMask) {
            //@formatter:on

        if (index != null && index < prmUserDataList.size()) {

            Optional<HighByte> highByte;
            LowByte lowByte;
            
            BitRange bitRange = new BitRange(new BitPos(extUserPrmData.getMinBit()), new BitPos(extUserPrmData.getMaxBit()));
            DataType dataType;
            
            if (bitRange.needsTwoBytes()) {
                if ((index + 1) < prmUserDataList.size()) {
                    highByte = Optional.of(new HighByte(prmUserDataList.get(index + 0)));
                    lowByte = new LowByte(prmUserDataList.get(index + 1));
                    dataType = DataType.UINT16;
                } else {
                    throw new IllegalStateException("Not enough data for two byte value");
                }
            } else {
                highByte = Optional.absent();
                boolean signedDataType = extUserPrmData.isSigned();
                Integer value =  prmUserDataList.get(index);        
                
                lowByte = new LowByte(value);                    
                if (signedDataType) {
                    dataType = DataType.INT8;
                } else {
                    dataType = DataType.UINT8;                    
                }                
            }
            
            //@formatter:off
            final int val = bitMask.getValueFromBitMask(
                    bitRange,
                    highByte, 
                    lowByte);
                    //@formatter:on
                        
            if (dataType.isUnsigned()) {
                return val;                
            } else {
                BitData bitData = new BitData(val);
                if (bitData.isHighestBitSet(dataType)) {
                    return bitData.asTwoComplement();
                } else {
                    return val;
                }
            }

        }

        return 0;

    }

    private void handleComboViewer(@Nonnull final ComboViewer prmTextCV, @Nonnull final Integer byteIndex) {
        if (!prmTextCV.getCombo().isDisposed()) {
            final ExtUserPrmData extUserPrmData = (ExtUserPrmData) prmTextCV.getInput();
            final StructuredSelection selection = (StructuredSelection) prmTextCV.getSelection();
            final Integer bitValue = ((PrmTextItem) selection.getFirstElement()).getIndex();
            setValue2BitMask(extUserPrmData, byteIndex, bitValue);
            final Integer indexOf = prmTextCV.getCombo().indexOf(selection.getFirstElement().toString());
            prmTextCV.getCombo().setData(indexOf);
        }
    }

    @Nonnull
    private void handleText(@Nonnull final Text prmText, @Nonnull final Integer byteIndex) {
        if (!prmText.isDisposed()) {
            final Object data = prmText.getData("ExtUserPrmData");
            if (data instanceof ExtUserPrmData) {
                final ExtUserPrmData extUserPrmData = (ExtUserPrmData) data;

                final String value = prmText.getText();
                Integer bitValue;
                
                if (value == null || value.isEmpty()) {
                    bitValue = extUserPrmData.getDefault();
                } else {
                    bitValue = Integer.parseInt(value);
                }                
                
                setValue2BitMask(extUserPrmData, byteIndex, bitValue);
                prmText.setData(bitValue);
                
            }
        }
    }

    /**
     * 
     * @param parent
     *            the Parent Composite.
     * @param value
     *            the Selected currentUserParamData Value.
     * @param extUserPrmData
     * @param prmText
     * @return a ComboView for are currentUserParamData Property
     */
    @Nonnull
    ComboViewer makeComboViewer(@Nonnull final Composite parent, @Nullable final Integer value,
            @Nonnull final ExtUserPrmData extUserPrmData, @CheckForNull final PrmText prmText) {
        Integer localValue = value;

        final ComboViewer prmTextCV = new ComboViewer(parent);
        final RowData data = new RowData();
        data.exclude = false;

        final Formatter f = new Formatter();

        //@formatter:off
        f.format("Ref-Index: %d, min Bit: %d, max Bit: %d", 
                extUserPrmData.getIndex(), 
                extUserPrmData.getMinBit(),
                extUserPrmData.getMaxBit());
                //@formatter:on
        prmTextCV.getCombo().setToolTipText(f.toString());
        f.close();

        prmTextCV.getCombo().setLayoutData(data);
        prmTextCV.setLabelProvider(new PrmTextComboLabelProvider(extUserPrmData));
        prmTextCV.setContentProvider(new ExtUserPrmDataContentProvider());
        prmTextCV.getCombo().addModifyListener(getMLSB());
        prmTextCV.setSorter(new PrmTextViewerSorter());

        if (localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmTextCV.setInput(extUserPrmData);

        if (prmText != null) {
            final PrmTextItem prmTextItem = prmText.getPrmTextItem(localValue);
            if (prmTextItem != null) {
                prmTextCV.setSelection(new StructuredSelection(prmTextItem));
            } else {
                prmTextCV.getCombo().select(0);
            }
        } else {
            prmTextCV.getCombo().select(localValue);
        }
        prmTextCV.getCombo().setData(prmTextCV.getCombo().getSelectionIndex());

        return prmTextCV;
    }

    //@formatter:off
    private void makeCurrentUserParamDataItem(
            @Nonnull final Composite currentUserParamDataGroup,
            @Nullable final ExtUserPrmData extUserPrmData, 
            @Nullable final Integer value) {
            //@formatter:on

        PrmText prmText = null;

        final Text text = new Text(currentUserParamDataGroup, SWT.SINGLE | SWT.READ_ONLY);

        if (extUserPrmData != null) {
            text.setText(extUserPrmData.getText() + ":");
            prmText = extUserPrmData.getPrmText();
            if ((prmText == null || prmText.isEmpty())
                    && extUserPrmData.getMaxValue() - extUserPrmData.getMinValue() > 10) {
                _prmTextCV.add(makeTextField(currentUserParamDataGroup, value, extUserPrmData));
            } else {
                _prmTextCV.add(makeComboViewer(currentUserParamDataGroup, value, extUserPrmData, prmText));
            }
        }
        new Label(currentUserParamDataGroup, SWT.SEPARATOR | SWT.HORIZONTAL);// .setLayoutData(new
    }

    /**
     * 
     * @param tabFolder
     *            The Tab Folder to add the Tab Item.
     * @param head
     *            Headline for the Tab.
     * @return Tab Item Composite.
     */
    @Nonnull
    private Composite makeGSDFileChooser(@Nonnull final TabFolder tabFolder, @Nonnull final String head) {
        final int columnNum = 7;
        final Composite comp = ConfigHelper.getNewTabItem(head, tabFolder, columnNum, 520, 200);

        final Text selectedText = createSelectionArea(columnNum, comp);

        final TableViewer gsdFileTableViewer = createChooserArea(columnNum, comp);

        createButtonArea(tabFolder, comp, selectedText, gsdFileTableViewer);

        createGSDFileActions(gsdFileTableViewer);

        return comp;

    }

    /**
     * 
     * @param currentUserParamDataGroup
     * @param value
     * @param extUserPrmData
     * @return
     */
    //@formatter:off
    @Nonnull
    Text makeTextField(
            @Nonnull final Composite currentUserParamDataGroup, 
            @CheckForNull final Integer value,
            @Nonnull final ExtUserPrmData extUserPrmData) {
            //@formatter:on
        Integer localValue = value;
        final Text prmText = new Text(currentUserParamDataGroup, SWT.BORDER | SWT.SINGLE | SWT.RIGHT);
        final Formatter f = new Formatter();
        //@formatter:off
        f.format("Ref-Index: %d, min Bit: %d, max Bit: %d \r\nMin: %d, Max: %d Default: %d",
                extUserPrmData.getIndex(),
                extUserPrmData.getMinBit(), 
                extUserPrmData.getMaxBit(), 
                extUserPrmData.getMinValue(),
                extUserPrmData.getMaxValue(), 
                extUserPrmData.getDefault());
        //@formatter:on
        prmText.setToolTipText(f.toString());
        f.close();

        int maxinputLength = Integer.toString(extUserPrmData.getMaxValue()).length();

        // if neagtive values are allowed add 1 to allow input for sign
        if ((extUserPrmData.getMinValue() < 0) || (extUserPrmData.getMaxValue() < 0)) {
            maxinputLength++;
        }

        prmText.setTextLimit(maxinputLength);

        if (localValue == null) {
            localValue = extUserPrmData.getDefault();
        }
        prmText.setText(localValue.toString());
        prmText.setData(localValue.toString());
        prmText.setData("ExtUserPrmData", extUserPrmData);

        prmText.addModifyListener(getMLSB());

        prmText.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(@Nonnull final VerifyEvent e) {
                String currentInput = e.text;
                if (!Strings.isNullOrEmpty(currentInput)) {
                    e.doit = currentInput.equals("-") || Character.isDigit(currentInput.charAt(0));
                }
            }

        });

        return prmText;
    }

    private List<Integer> visitedIndices;

    /**
     * @throws IOException
     * 
     */
    protected void saveUserPrmData() throws IOException {

        final AbstractGsdPropertyModel gsdPropertyModel = getGsdPropertyModel();

        if (gsdPropertyModel != null) {
            final Collection<KeyValuePair> extUserPrmDataRefMap = gsdPropertyModel.getExtUserPrmDataRefMap().values();

            if (extUserPrmDataRefMap.size() == _prmTextCV.size()) {

                int i = 0;
                visitedIndices = new ArrayList<Integer>();

                for (final KeyValuePair ref : extUserPrmDataRefMap) {
                    final Object prmTextObject = _prmTextCV.get(i);
                    final Integer index = ref.getIndex();

                    if (index != null) {
                        if (prmTextObject instanceof ComboViewer) {
                            final ComboViewer prmTextCV = (ComboViewer) prmTextObject;
                            handleComboViewer(prmTextCV, index);
                        } else if (prmTextObject instanceof Text) {
                            final Text prmText = (Text) prmTextObject;
                            handleText(prmText, index);
                        }
                    }

                    i++;

                }

            }
        }

    }

    /**
     * @param prmTextObject
     */
    private final void cancelComboViewer(@Nonnull final Object prmTextObject) {
        final ComboViewer prmTextCV = (ComboViewer) prmTextObject;
        if (!prmTextCV.getCombo().isDisposed()) {
            final Integer index = (Integer) prmTextCV.getCombo().getData();
            if (index != null) {
                prmTextCV.getCombo().select(index);
            }
        }
    }

    /**
     * @param prmTextObject
     */
    private final void cancelText(@Nonnull final Object prmTextObject) {
        final Text prmText = (Text) prmTextObject;
        if (!prmText.isDisposed()) {
            final String value = (String) prmText.getData();
            if (value != null) {
                prmText.setText(value);
            }
        }
    }

    abstract void setGsdFile(@Nullable GSDFileDBO gsdFile);

    abstract void setPrmUserData(@Nonnull Integer index, @Nonnull Integer value, boolean firstAccess);

    /**
     * Change the a value on the Bit places, that is given from the input, to
     * the bitValue.
     * 
     * @param extUserPrmData
     *            give the start and end Bit position.
     * @param bitValue
     *            the new Value for the given Bit position.
     * @param firstAccess
     * @param value
     *            the value was changed.
     * @return the changed value.
     */
    @Nonnull
    //@formatter:off
    private void setValue2BitMask(
            @Nonnull final ExtUserPrmData extUserPrmData, 
            @Nonnull final Integer byteIndex,
            @Nonnull final Integer bitValue) {
            //@formatter:on

        int minBit = extUserPrmData.getMinBit();
        int maxBit = extUserPrmData.getMaxBit();

        if ((minBit != 0) && (bitValue < 0)) {
            throw new IllegalStateException("Negative values must start on bit 0.");
        }
        
        BitRange bitRange = new BitRange(new BitPos(minBit), new BitPos(maxBit));                    
        BitData bitData = new BitData(Math.abs(bitValue));
        bitData = bitData.shiftLeftToBit(new BitPos(minBit));
        
        if (bitRange.needsTwoBytes()) {
            // BigEndian Order => first HighByte than LowByte
            setPrmUserData(byteIndex, bitData.getHighByte().getValue(), !indexVisited(byteIndex));
            setPrmUserData(byteIndex + 1, bitData.getLowByte().getValue(), !indexVisited(byteIndex + 1));
        } else {
            if (bitValue < 0) {
                setPrmUserData(byteIndex, bitData.getLowByte().getValue() * -1, !indexVisited(byteIndex));                
            } else {
                setPrmUserData(byteIndex, bitData.getLowByte().getValue(), !indexVisited(byteIndex));
            }
        }

    }

    private boolean indexVisited(Integer index) {
        Preconditions.checkNotNull(index, "Index must not be null.");
        Preconditions.checkArgument(index.intValue() >= 0, "Index must not be negativ.");        
        if (visitedIndices.contains(index)) {
            return true;
        } else {
            visitedIndices.add(index);
            return false;
        }
    }

    private static void createGSDFileActions(@Nonnull final TableViewer viewer) {
        final Menu menu = new Menu(viewer.getControl());
        final MenuItem showItem = new MenuItem(menu, SWT.PUSH);
        showItem.addSelectionListener(new ShowFileSelectionListener(viewer));
        showItem.setText("&Show");
        showItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));
        viewer.getTable().setMenu(menu);
    }

}
