package org.csstudio.common.trendplotter;

import java.util.logging.Level;
import org.csstudio.common.trendplotter.Activator;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.editor.DataBrowserEditor;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.ui.Controller;
import org.csstudio.common.trendplotter.ui.Plot;
import org.csstudio.domain.desy.ui.action.OpenScreenshotAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * Open a db2 trend in a shell. Most parts are from class {@link DataBrowserEditor}
 * from Kay Kasemir.
 *
 * TODO: Error handling, add context menu, ...
 *
 * @author jhatje
 * @author $Author: jhatje $
 * @version $Revision: 1.7 $
 * @since 19.11.2010
 */
public class DB2Shell {
    
    private IFile _file;
    private Shell _shell;
    private Model _model = new Model();
    
    public DB2Shell(IFile file) {
        _file = file;
        _shell = new Shell();
        _shell.setText(_file.getName());
        _shell.setLocation(10, 10);
        _shell.setSize(800, 600);
        createMenuBar();
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
    
    public void openShell() {
        // If it's a file, load content into Model
        final IFile file = _file;
        if (file != null) {
            try {
                _model.read(file.getContents(true));
            } catch (Exception ex) {
                Activator.getLogger().log(Level.SEVERE, "Error reading file", ex); //$NON-NLS-1$
            }
        }
        
        // Create GUI elements (Plot)
        GridLayout layout = new GridLayout();
        _shell.setLayout(layout);
        
        
        // Canvas that holds the graph
        final Canvas plot_box = new Canvas(_shell, 0);
        plot_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        
        Plot plot = Plot.forCanvas(plot_box);
        
        // Create and start controller
        final Controller controller = new Controller(_shell, _model, plot);
        try {
            controller.start();
        } catch (Exception ex) {
            MessageDialog.openError(_shell, Messages.Error, NLS.bind(Messages.ControllerStartErrorFmt, ex.getMessage()));
        }
        // add dispose listener
        _shell.addDisposeListener(new DisposeListener() {
            /**
             * {@inheritDoc}
             */
            public void widgetDisposed(final DisposeEvent e) {
                controller.stop();
                _model = null;
            }
        });
        
        // open the shell
        _shell.open();
        
    }
}
