package org.csstudio.common.trendplotter;

import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.model.ArchiveDataSource;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.common.trendplotter.preferences.Preferences;
import org.csstudio.common.trendplotter.ui.Controller;
import org.csstudio.common.trendplotter.ui.Plot;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.domain.desy.ui.action.OpenScreenshotAction;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/** Handle pv from object contribution.
 *  @author jhatje
 */
public class PVpopupHandler extends AbstractHandler {
    
    private Shell _shell;
    private Model _model = new Model();

    public Object execute(ExecutionEvent event) throws ExecutionException {
        _shell = new Shell();
        _shell.setText("Trendplotter Shell");
        _shell.setLocation(10, 10);
        _shell.setSize(800, 600);
        createMenuBar();
        
        
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
        for (ProcessVariable pv : pvs) {
            try {
                add(_model, pv, null);
            } catch (final Exception ex) {
                MessageDialog.openError(_shell, Messages.Error, NLS.bind(Messages.ControllerStartErrorFmt, ex.getMessage()));
            }
        }
        
        // Create GUI elements (Plot)
        final GridLayout layout = new GridLayout();
        _shell.setLayout(layout);
        
        // Canvas that holds the graph
        final Canvas plot_box = new Canvas(_shell, 0);
        plot_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        
        final Plot plot = Plot.forCanvas(plot_box);
        
        // Create and start controller
        final Controller controller = new Controller(_shell, _model, plot);
        try {
            controller.start();
        } catch (final Exception ex) {
            MessageDialog.openError(_shell, Messages.Error, NLS.bind(Messages.ControllerStartErrorFmt, ex.getMessage()));
        }

        // add dispose listener
        _shell.addDisposeListener(new DisposeListener() {
            /**
             * {@inheritDoc}
             */
            public void widgetDisposed(final DisposeEvent e) {
                controller.stop();
            }
        });

        // open the shell
        _shell.open();

        return null;
    }
    
    /** Add item
    *  @param model Model to which to add the item
    *  @param pv PV to add
    *  @param archive Archive to use or <code>null</code>
    *  @throws Exception on error
    */
    private void add(final Model model, final ProcessVariable pv, final ArchiveDataSource archive) throws Exception {
        final double period = Preferences.getScanPeriod();
        final PVItem item = new PVItem(pv.getName(), period);
        if (archive == null) {
            item.useDefaultArchiveDataSources();
        } else {
            item.addArchiveDataSource(archive);
        }
        // Add item to new axes
        item.setAxis(model.addAxis());
        model.addItem(item);
    }
    
    private void createMenuBar() {
        
        MenuManager menuManager = new MenuManager();
        
        MenuManager plotMenu = new MenuManager("Plot");
        
        plotMenu.add(new RemovePvAction(_model));
        menuManager.add(plotMenu);
        // Added by Markus Moeller, 2009-01-26
        // Search for the screenshot plugin
        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        IConfigurationElement[] confElements = extReg
                .getConfigurationElementsFor("org.csstudio.utility.screenshot.ImageWorker");
        
        if (confElements.length > 0) {
            for (int i = 0; i < confElements.length; i++) {
                if (confElements[i].getContributor().getName()
                        .compareToIgnoreCase("org.csstudio.utility.screenshot") == 0) {
                    MenuManager captureManager = new MenuManager("Screenshot");
                    
                    captureManager.add(new OpenScreenshotAction());
                    menuManager.add(captureManager);
                    
                }
            }
        }
//                    Menu menuBar = menuManager.createMenuBar(new Decorations(_shell, SWT.BAR));
        Menu menu = menuManager.createMenuBar(_shell);
        _shell.setMenuBar(menu);
    }
}
