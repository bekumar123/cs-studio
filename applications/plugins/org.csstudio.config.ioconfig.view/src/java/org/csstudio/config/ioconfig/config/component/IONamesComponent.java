package org.csstudio.config.ioconfig.config.component;

import java.util.Map.Entry;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class IONamesComponent implements IComponent {

    private final Composite composite;

    private Text ioNamesText;

    private Text channelNameText;

    private Text channelsDescText;

    private boolean disableListener = false;
    
    public IONamesComponent(Composite composite) {
        this.composite = composite;
    }

    @Override
    public void buildComponent() {

        Display display = Display.getCurrent();

        composite.setLayout(new GridLayout(3, false));
        buildLabel(composite, "Name");
        buildLabel(composite, "IO Name");
        buildLabel(composite, "Short Description");

        GridData layoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        layoutData.minimumWidth = 150;
        layoutData.widthHint = 150;
        channelNameText = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
        channelNameText.setEditable(false);
        channelNameText.setBackground(display.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
        channelNameText.setLayoutData(layoutData);

        layoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        layoutData.minimumWidth = 350;
        layoutData.widthHint = 350;
        ioNamesText = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.BORDER);
        ioNamesText.setLayoutData(layoutData);

        channelsDescText = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
        channelsDescText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        channelNameText.addPaintListener(new SetTopIndexPaintListener(ioNamesText, channelsDescText, channelNameText));
        ioNamesText.addPaintListener(new SetTopIndexPaintListener(channelNameText, channelsDescText, ioNamesText));
        channelsDescText.addPaintListener(new SetTopIndexPaintListener(ioNamesText, channelNameText, channelsDescText));

    }

    private void buildLabel(@Nonnull final Composite comp, @Nonnull final String text) {
        final Label nameLabel = new Label(comp, SWT.NONE);
        GridData layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        nameLabel.setLayoutData(layoutData);
        nameLabel.setText(text);
    }

    public void addModifyCallbackIONames(ModifiedCallback callback) {
        ioNamesText.addModifyListener(new SaveModifyListener("IONames", ioNamesText, callback));
    }

    public void addModifyCallbackChannelDesc(ModifiedCallback callback) {
        channelsDescText.addModifyListener(new SaveModifyListener("ChannelsDesc", ioNamesText, callback));
    }

    public void undo() {
        disableListener = true;
        channelNameText.setText((String) channelNameText.getData());
        ioNamesText.setText((String) ioNamesText.getData());
        channelsDescText.setText((String) channelsDescText.getData());
        disableListener = false;
    }

    public void updateIONamesText(ModuleDBO module) {
        disableListener = true;
        final StringBuilder ioNamesSB = new StringBuilder();
        final StringBuilder channelNamesSB = new StringBuilder();
        final StringBuilder channelDescSB = new StringBuilder();
        final Set<Entry<Short, ChannelStructureDBO>> channelStructureEntrySet = module.getChildrenAsMap().entrySet();
        for (final Entry<Short, ChannelStructureDBO> channelStructureEntry : channelStructureEntrySet) {
            final Set<Entry<Short, ChannelDBO>> channelEntrySet = channelStructureEntry.getValue().getChildrenAsMap()
                    .entrySet();
            for (final Entry<Short, ChannelDBO> channelEntry : channelEntrySet) {
                String channelName = channelEntry.getValue().getName();
                if (channelName == null) {
                    channelName = "";
                }
                channelNamesSB.append(channelName);
                channelNamesSB.append("\n");

                String ioName = channelEntry.getValue().getIoName();
                if (ioName == null) {
                    ioName = "";
                }
                ioNamesSB.append(ioName);
                ioNamesSB.append("\n");

                String desc = channelEntry.getValue().getDescription();
                if (desc == null) {
                    desc = " ";
                }
                if (desc.contains("\r\n")) {
                    String[] split = desc.split("\r\n");
                    desc = split.length > 0 ? split[0] : " ";
                }
                channelDescSB.append(desc);
                channelDescSB.append("\n");
            }
        }
        channelNameText.setText(channelNamesSB.toString());
        channelNameText.setData(channelNamesSB.toString());
        ioNamesText.setText(ioNamesSB.toString());
        ioNamesText.setData(ioNamesSB.toString());
        channelsDescText.setText(channelDescSB.toString());
        channelsDescText.setData(channelDescSB.toString());
        disableListener = false;
    }

    public void save(ModuleDBO module) throws PersistenceException {
        int i = 0;
        final String[] ionames = ioNamesText.getText().split("\n");
        final String[] descs = channelsDescText.getText().split("\n");
        final Collection<ChannelStructureDBO> channelStructs = module.getChildrenAsMap().values();
        for (final ChannelStructureDBO channelStructure : channelStructs) {
            final Collection<ChannelDBO> channels = channelStructure.getChildrenAsMap().values();
            for (final ChannelDBO channel : channels) {
                if (i < ionames.length) {
                    channel.setIoName(ionames[i].trim());
                }
                if (i < descs.length) {
                    String descPost = "";
                    final String description = channel.getDescription();
                    if (description != null) {
                        final int indexOf = description.indexOf("\r\n");
                        if (indexOf >= 0) {
                            descPost = description.substring(indexOf);
                        }
                    }
                    channel.setDescription(descs[i].trim() + descPost);
                }
                channel.assembleEpicsAddressString();
                i++;
            }
        }
        ioNamesText.setData(ioNamesText.getText());
        channelsDescText.setData(channelsDescText.getText());
    }

    private final class SaveModifyListener implements ModifyListener {

        private final String event;
        private final Text modifiedText;
        private final ModifiedCallback callback;

        public SaveModifyListener(@Nonnull final String event, @Nonnull final Text modifiedText,
                ModifiedCallback callback) {
            this.event = event;
            this.modifiedText = modifiedText;
            this.callback = callback;
        }

        @Override
        public void modifyText(@Nonnull final ModifyEvent e) {
            // only trigger change event if change was done by user
            if (disableListener) {
                return;
            }
            callback.modified(event, !modifiedText.getText().equals(modifiedText.getData()));
        }
    }

    private static final class SetTopIndexPaintListener implements PaintListener {

        private final Text text1;
        private final Text text2;
        private final Text getText;

        /**
         * Constructor.
         */
        public SetTopIndexPaintListener(@Nonnull final Text setText1, @Nonnull final Text setText2,
                @Nonnull final Text getText) {
            this.text1 = setText1;
            this.text2 = setText2;
            this.getText = getText;
        }

        @Override
        public void paintControl(@Nonnull final PaintEvent e) {
            text1.setTopIndex(getText.getTopIndex());
            text2.setTopIndex(getText.getTopIndex());
        }
    }

}
