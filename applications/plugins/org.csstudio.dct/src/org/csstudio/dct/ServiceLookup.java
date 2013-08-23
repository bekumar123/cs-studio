package org.csstudio.dct;

import org.csstudio.dct.nameresolution.file.service.IoNameResolutionFromFile;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

public class ServiceLookup {

   public static IoNameResolutionFromFile getIoNameResolutionFromFileService() {
      Bundle bundle = FrameworkUtil.getBundle(new ServiceLookup().getClass());
      BundleContext context = bundle.getBundleContext();
      //@formatter:off
    ServiceTracker<IoNameResolutionFromFile, Object> tracker =
          new ServiceTracker<IoNameResolutionFromFile, Object>(context, IoNameResolutionFromFile.class.getName(), null);
          //@formatter:on
      tracker.open();
      IoNameResolutionFromFile ioNameResolutionFromFileService = (IoNameResolutionFromFile) tracker.getService();
      tracker.close();
      return ioNameResolutionFromFileService;
   }

}
