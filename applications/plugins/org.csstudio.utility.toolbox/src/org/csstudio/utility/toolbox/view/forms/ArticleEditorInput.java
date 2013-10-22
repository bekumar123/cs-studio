package org.csstudio.utility.toolbox.view.forms;

import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.func.Func1Void;

public class ArticleEditorInput<T extends BindingEntity> extends GenericEditorInput<T> {
	
	public ArticleEditorInput() {
		super();
		this.setBeforeSave(new Func1Void<T>() {
            @Override
            public void apply(T arg0) {
                System.out.println(arg0);                
            }
        });
	}
	
	@Override
	public int hashCode() {
		if (!hasData()) {
			return getEditorId();
		}
		String gruppeArtikel = getDataPropertyValueByName("gruppeArtikel");
		return gruppeArtikel.hashCode();
	}
	 
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GenericEditorInput<?>)) {
			return false;
		}
		GenericEditorInput<?> editorInput = (GenericEditorInput<?>) obj;
		if (!getTypeLiteral().equals(editorInput.getTypeLiteral())) {
			return false;
		}
		if (!hasData()) {
			return editorInput.getEditorId() == getEditorId();
		}					
		if (!editorInput.hasData()) {
			return editorInput.getEditorId() == getEditorId();
		}					
		String thisGruppeArtikel = getDataPropertyValueByName("gruppeArtikel");
		String objGruppeArtikel = editorInput.getDataPropertyValueByName("gruppeArtikel");
		return thisGruppeArtikel.equals(objGruppeArtikel);
	}
		
}
