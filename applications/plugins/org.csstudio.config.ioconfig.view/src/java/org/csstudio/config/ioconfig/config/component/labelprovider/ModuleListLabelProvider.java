package org.csstudio.config.ioconfig.config.component.labelprovider;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBOReadOnly;
import org.csstudio.config.ioconfig.model.types.ModuleInfo;
import org.csstudio.config.ioconfig.model.types.ModuleLabel;
import org.csstudio.config.ioconfig.model.types.ModuleList;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ParsedModuleInfo;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Table;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 07.01.2009
 */
public class ModuleListLabelProvider extends LabelProvider implements IFontProvider, IColorProvider {

    private final ModuleList moduleList;
    
    private final ParsedModuleInfo parsedModuleInfo;

    /**
     * Font for Module that have an Input or Output. (Style is Normal)
     */
    private static Font _NORMAL;
    /**
     * Font for Module that have an Input and Output. (Style is Bold)
     */
    private static Font _BOLD;
    /**
     * Font for Module without an Input or Output. (Style is Italic)
     */
    private static Font _ITALIC;
    /**
     * The color for Modules without an Input or Output.
     */
    private static Color _GRAY;
    /**
     * The default font color.
     */
    private static final Color BLACK = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_BLACK);

    /**
     * The Table font height.
     */
    private static int _HEIGHT;
    /**
     * The Table Font name.
     */
    private static String _NAME;

    /**
     * Default Constructor.
     * 
     * @param table
     *            the Table how use this LabelProvider.
     * @param moduleList 
     * @param moduleList
     * @param file
     */
    //@formatter:off
    public ModuleListLabelProvider(@Nonnull 
            final Table table,
            final ModuleList moduleList, 
            final ParsedModuleInfo parsedModuleInfo) {
            //@formatter:on

        this.moduleList = moduleList;
        this.parsedModuleInfo = parsedModuleInfo;

        final FontData fontData = table.getFont().getFontData()[0];
        if (_GRAY == null) {
            _HEIGHT = fontData.getHeight();
            _NAME = fontData.getName();
            _GRAY = CustomMediaFactory.getInstance().getColor(CustomMediaFactory.COLOR_BLACK);
            _BOLD = CustomMediaFactory.getInstance().getFont(_NAME, _HEIGHT, SWT.BOLD);
            _NORMAL = CustomMediaFactory.getInstance().getFont(_NAME, _HEIGHT, SWT.NORMAL);
            _ITALIC = CustomMediaFactory.getInstance().getFont(_NAME, _HEIGHT, SWT.ITALIC);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void dispose() {
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public final Color getBackground(@Nullable final Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final Font getFont(@Nullable final Object element) {
        if (element instanceof GSDModuleDBOReadOnly) {
            ModuleNumber moduleNumber = ((GSDModuleDBOReadOnly) element).getModuleNumber();
            ModuleInfo moduleInfo = parsedModuleInfo.getModuleInfo(moduleNumber);
            if (moduleInfo.isHasInputs() && moduleInfo.isHasOutputs()) {
                return _BOLD;
            } else if (moduleInfo.isHasInputs() || moduleInfo.isHasOutputs()) {
                return _NORMAL;
            } else {
                return _ITALIC;
            }
        }
        return _NORMAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final Color getForeground(@Nullable final Object element) {
        if (element instanceof GSDModuleDBOReadOnly) {
            ModuleNumber moduleNumber = ((GSDModuleDBOReadOnly) element).getModuleNumber();
            ModuleInfo moduleInfo = parsedModuleInfo.getModuleInfo(moduleNumber);
            if (!(moduleInfo.isHasInputs() && moduleInfo.isHasOutputs())) {
                return _GRAY;
            }
            return BLACK;
        }
        return BLACK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final String getText(@Nonnull final Object element) {
        if (element instanceof GSDModuleDBOReadOnly) {
            GSDModuleDBOReadOnly gsdModuleDBO = (GSDModuleDBOReadOnly)element;
            ModuleLabel moduleLabel = moduleList.getModuleLabel(gsdModuleDBO.getModuleNumber());
            return moduleLabel.buildLabel();
        }
        return element.toString();
    }
}
