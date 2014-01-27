package org.csstudio.nams.configurator.editor;

import java.util.Set;

import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.NotConditionForFilterTreeBean;
import org.eclipse.jface.viewers.ITreeContentProvider;

public abstract class FilterTreeContentProvider implements ITreeContentProvider {

	private JunctorConditionForFilterTreeBean rootCondition;

	public FilterTreeContentProvider() {
		super();
	}

	@Override
	public void dispose() {
	    // Not used yet
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		FilterbedingungBean[] result = new FilterbedingungBean[0];
		if (parentElement instanceof JunctorConditionForFilterTreeBean) {
			final JunctorConditionForFilterTreeBean junctorEditionElement = (JunctorConditionForFilterTreeBean) parentElement;
			final Set<FilterbedingungBean> operands = junctorEditionElement
					.getOperands();
			result = operands.toArray(new FilterbedingungBean[operands.size()]);
		}
		if (parentElement instanceof NotConditionForFilterTreeBean) {
			final NotConditionForFilterTreeBean not = (NotConditionForFilterTreeBean) parentElement;
			if (not.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
				final Set<FilterbedingungBean> operands = ((JunctorConditionForFilterTreeBean) not
						.getFilterbedingungBean()).getOperands();
				result = operands.toArray(new FilterbedingungBean[operands
						.size()]);
			}
		}
		return result;
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		Object[] result = null;
		
		if(rootCondition != null) {
			result = new Object[] { rootCondition };
		}
		
		return result;
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof JunctorConditionForFilterTreeBean) {
			return ((JunctorConditionForFilterTreeBean) element).hasOperands();
		} else {
			if (element instanceof NotConditionForFilterTreeBean) {
				final NotConditionForFilterTreeBean not = (NotConditionForFilterTreeBean) element;
				if (not.getFilterbedingungBean() instanceof JunctorConditionForFilterTreeBean) {
					return ((JunctorConditionForFilterTreeBean) not
							.getFilterbedingungBean()).hasOperands();
				}
	
			}
		}
		return false;
	}

	@Override
	public Object getParent(Object element) {
		return findParentRecursive(rootCondition, element);
	}

	private Object findParentRecursive(final Object potentialParent, final Object element) {
	
		if (this.hasChildren(potentialParent)) {
			final FilterbedingungBean[] children = (FilterbedingungBean[]) this
					.getChildren(potentialParent);
			for (final FilterbedingungBean child : children) {
				if (child == element) {
					return potentialParent;
				}
				final Object result = this.findParentRecursive(child, element);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	protected JunctorConditionForFilterTreeBean getRootCondition() {
		return rootCondition;
	}
	
	protected void setRootCondition(JunctorConditionForFilterTreeBean rootCondition) {
		this.rootCondition = rootCondition;
	}
	
}