package org.csstudio.sds.history.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public enum HistoryControlImages {

	PLAY("images/play_nav.gif"),
	PAUSE("images/pause_nav2.gif"),
	STEP_FORWARD("images/forward_nav2.gif"),
	STEP_BACKWARD("images/backward_nav2.gif");

	private final String path;

	private HistoryControlImages(final String path) {
		this.path = path;
	}

	public Image getImage() {
		final ImageRegistry registry = HistoryUiActivator.getDefault().getImageRegistry();
		Image image = registry.get(path);
		if (image == null) {
			addImageDescriptor();
			image = registry.get(path);
		}
		return image;
	}

	private void addImageDescriptor() {
		final HistoryUiActivator plugin = HistoryUiActivator.getDefault();
		final ImageDescriptor descriptor = ImageDescriptor.createFromURL(plugin.getBundle().getEntry(path));
		plugin.getImageRegistry().put(path, descriptor);
	}

	public ImageDescriptor getImageDescriptor() {
		final ImageRegistry registry = HistoryUiActivator.getDefault().getImageRegistry();
		ImageDescriptor imageDescriptor = registry.getDescriptor(path);
		if (imageDescriptor == null) {
			addImageDescriptor();
			imageDescriptor = registry.getDescriptor(path);
		}
		return imageDescriptor;
	}
}
