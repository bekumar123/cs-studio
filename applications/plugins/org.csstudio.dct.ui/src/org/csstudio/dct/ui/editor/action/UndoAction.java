package org.csstudio.dct.ui.editor.action;

import org.csstudio.dct.ui.editor.AbstractCommandStackAction;
import org.csstudio.dct.ui.editor.DctEditor;
import org.eclipse.gef.commands.CommandStack;

/**
 * Undo Action that handles the {@link CommandStack} of a
 * {@link DctEditor}.
 * 
 * @author Sven Wende
 */
public final class UndoAction extends AbstractCommandStackAction {

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void doRun(CommandStack commandStack) {
		commandStack.undo();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected boolean isActionEnabled(CommandStack commandStack) {
		return commandStack.canUndo();
	}

}
