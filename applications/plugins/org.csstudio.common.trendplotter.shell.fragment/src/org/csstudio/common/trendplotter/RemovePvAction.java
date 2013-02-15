
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

import org.csstudio.common.trendplotter.model.Model;
import org.csstudio.common.trendplotter.model.ModelItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author jhatje
 *
 */
public class RemovePvAction extends Action {
    
    private static final Logger LOG = LoggerFactory.getLogger(RemovePvAction.class);
    private Model _model;

    public RemovePvAction(Model model) {
        _model = model;
        this.setText("Remove PVs...");
        this.setToolTipText("Remove PVs from Plot");
        this.setEnabled(true);
    }
    
    @Override
    public void run()   {
            String[] pvNames = new String[_model.getItemCount()];
            for (int i=0; i<_model.getItemCount(); i++) {
                ModelItem item = _model.getItem(i);
                pvNames[i] = item.getName();
            }
            final RemovePvDialog dlg = new RemovePvDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), pvNames );
            if (dlg.open() != Window.OK)
                return;

            String selection = dlg.getNameToDelete();
            LOG.debug("Remove pv from trendplotter shell: " + selection);
            _model.removeItem(_model.getItem(selection));
    }
}
