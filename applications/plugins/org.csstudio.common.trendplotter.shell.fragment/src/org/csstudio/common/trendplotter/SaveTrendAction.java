
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.common.trendplotter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.csstudio.common.trendplotter.editor.DataBrowserModelEditorInput;
import org.csstudio.common.trendplotter.model.AxisConfig;
import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.ui.Plot;
import org.csstudio.swt.xygraph.figures.Axis;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author jhatje
 *
 */
public class SaveTrendAction extends Action {
    
    private static final Logger LOG = LoggerFactory.getLogger(SaveTrendAction.class);
    private Model _model;
    private Shell _shell;
    private Plot _plot;

    public SaveTrendAction(Shell shell, Model model, Plot plot) {
        _shell = shell;
        _model = model;
        _plot = plot;
        this.setText("Save Trend...");
        this.setToolTipText("Save Trend in Workspace");
        this.setEnabled(true);
    }
    
    @Override
    public void run()   {
        IFile file = promptForFile(null);
        LOG.info("------------------" + file.getName());
        if (file == null)
            return;
        if (! saveToFile(new NullProgressMonitor(), file))
            return;
    }
    
    /** Save current model content to given file, mark editor as clean.
    *
    *  @param monitor <code>IProgressMonitor</code>, may be null.
    *  @param file The file to use. May not exist, but I think its container has to.
    *  @return Returns <code>true</code> when successful.
    */
   private boolean saveToFile(final IProgressMonitor monitor, final IFile file)
   {
       monitor.beginTask(Messages.Save, IProgressMonitor.UNKNOWN);
       try
       {
           // Update model with info that's kept in plot

           // TODO Review. Why update the model when _saving_?
           // The model should always have the correct info
           // because it's listening to the plot,
           // and here the data is simply written.

           //TIME AXIS
           Axis timeAxis = _plot.getXYGraph().getXAxisList().get(0);
           AxisConfig confTime = _model.getTimeAxis();
           if(confTime == null)
           {
               confTime = new AxisConfig(timeAxis.getTitle());
               _model.setTimeAxis(confTime);
           }
           setAxisConfig(confTime, timeAxis);

           for (int i=0; i<_model.getAxisCount(); i++)
           {
               AxisConfig conf = _model.getAxis(i);
               int axisIndex = _model.getAxisIndex(conf);
               Axis axis = _plot.getXYGraph().getYAxisList().get(axisIndex);
               setAxisConfig(conf, axis);
           }

           _model.setGraphSettings(_plot.getGraphSettings());
           _model.setAnnotations(_plot.getAnnotations(), false);

           // Write model to string
           ByteArrayOutputStream buf = new ByteArrayOutputStream();

           _model.write(buf);
           buf.close();

           final ByteArrayInputStream in = new ByteArrayInputStream(buf.toByteArray());
           // Write buffer to file
           if (file.exists())
               file.setContents(in, IResource.FORCE, monitor);
           else
               file.create(in, true, monitor);

       }
       catch (Exception ex)
       {
           MessageDialog.openError(_shell,
                   Messages.Error,
                   NLS.bind(Messages.FileSaveErrorFmt, file.getName(), ex.getMessage()));
           return false;
       }
       finally
       {
           monitor.done();
       }
       return true;
   }

   
   /**
    * Set AxisConfigProperties from Axis
    * @param conf
    * @param axis
    */
   private void setAxisConfig(AxisConfig conf , Axis axis){

        //Don't fire axis change event to avoid SWT Illegal Thread Access
        conf.setFireEvent(false);

        conf.setFontData(axis.getTitleFontData());
        conf.setColor(axis.getForegroundColorRGB());
        conf.setScaleFontData(axis.getScaleFontData());


        //MIN MAX RANGE
        conf.setRange(axis.getRange().getLower(), axis.getRange().getUpper());

        //GRID
        conf.setShowGridLine(axis.isShowMajorGrid());
        conf.setDashGridLine(axis.isDashGridLine());
        conf.setGridLineColor(axis.getMajorGridColorRGB());

        //FORMAT
        conf.setAutoFormat(axis.isAutoFormat());
        conf.setTimeFormatEnabled(axis.isDateEnabled());
        conf.setFormat(axis.getFormatPattern());

        conf.setFireEvent(true);
   } 
   
    /** Prompt for file name
     *  @param old_file Old file name or <code>null</code>
     *  @return IFile for new file name
     */
    private IFile promptForFile(final IFile old_file) {
        final SaveAsDialog dlg = new SaveAsDialog(_shell);
        dlg.setBlockOnOpen(true);
        if (old_file != null)
            dlg.setOriginalFile(old_file);
        if (dlg.open() != Window.OK)
            return null;

        // The path to the new resource relative to the workspace
        IPath path = dlg.getResult();
        if (path == null)
            return null;
        // Assert it's an '.xml' file
        final String ext = path.getFileExtension();
        if (ext == null  ||  !ext.equals(Model.FILE_EXTENSION))
            path = path.removeFileExtension().addFileExtension(Model.FILE_EXTENSION);
        // Get the file for the new resource's path.
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.getFile(path);
    }
}
