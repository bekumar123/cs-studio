package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.nameresolution.FieldFunctionContentProposal;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class GenericContentProposingTextCellEditor extends TextCellEditor implements IContentProposalListener2 {

    public GenericContentProposingTextCellEditor(Composite parent, IContainer container) {
        super(parent);

        char[] autoActivationCharacters = new char[] { '$' };

        KeyStroke keyStroke;
        try {
            keyStroke = KeyStroke.getInstance("Ctrl+Space");
            ContentProposalAdapter adapter = new ContentProposalAdapter(getControl(), new TextContentAdapter(),
                    new MyContentProposalProvider(container), keyStroke, autoActivationCharacters);
            adapter.setPropagateKeys(true);
            adapter.setPopupSize(new Point(400, 300));
            adapter.addContentProposalListener(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean dependsOnExternalFocusListener() {
        return true;
    }

    boolean contentProposalOpen = false;

    public void proposalPopupClosed(ContentProposalAdapter adapter) {
        contentProposalOpen = false;
    }

    public void proposalPopupOpened(ContentProposalAdapter adapter) {
        contentProposalOpen = true;
    }

    @Override
    protected void focusLost() {
        if (isActivated() && !contentProposalOpen) {
            fireApplyEditorValue();
            deactivate();
        }
    }

    private static final class MyContentProposalProvider implements IContentProposalProvider {

        private IContainer container;

        private MyContentProposalProvider(IContainer container) {
            this.container = container;
        }

        public IContentProposal[] getProposals(String contents, int position) {
            List<IContentProposal> proposals = new ArrayList<IContentProposal>();
            Map<String, String> parameters = container.getParameterValues();
            for (String key : parameters.keySet()) {
                proposals.add(new FieldFunctionContentProposal(key + "()", key + ">" + "dd", "ddd", 1));
            }
            return proposals.toArray(new IContentProposal[proposals.size()]);
        }

    }

}
