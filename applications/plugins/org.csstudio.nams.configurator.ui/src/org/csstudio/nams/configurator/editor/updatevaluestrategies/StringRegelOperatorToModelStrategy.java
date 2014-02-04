
package org.csstudio.nams.configurator.editor.updatevaluestrategies;

import org.csstudio.nams.common.material.regelwerk.StringFilterConditionOperator;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class StringRegelOperatorToModelStrategy extends UpdateValueStrategy {
	@Override
	public Object convert(final Object value) {
		return StringFilterConditionOperator.valueOf((String) value);
	}
}
