
package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.MessageExtensionBean;

public class MessageExtensionView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.messageextension"; //$NON-NLS-1$

	@Override
	protected Class<? extends IConfigurationBean> getBeanClass() {
		return MessageExtensionBean.class;
	}

	@Override
	protected IConfigurationBean[] getTableContent() {
		return AbstractNamsView.getConfigurationBeanService()
				.getMessageExtensionBeans();
	}
}
