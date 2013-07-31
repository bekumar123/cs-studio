package org.csstudio.utility.toolbox;

import org.csstudio.utility.toolbox.view.ToolBoxView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

   public static final String ID = "org.csstudio.utility.toolbox.perspective";
   
   @Override
   public void createInitialLayout(IPageLayout layout) {
      layout.setEditorAreaVisible(true);
      layout.addView(ToolBoxView.ID, IPageLayout.LEFT, 0.2f, layout.getEditorArea());
   }

}
