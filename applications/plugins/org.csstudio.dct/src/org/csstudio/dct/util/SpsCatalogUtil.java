package org.csstudio.dct.util;

import java.io.File;

import org.csstudio.dct.ServiceLookup;
import org.csstudio.dct.nameresolution.file.service.IoNameResolutionFromFile;

public class SpsCatalogUtil {
   
   public void reload(String catalog) throws Exception {
      File catalogFile = new File(catalog);
      if (catalogFile.exists()) {
         try {
            IoNameResolutionFromFile service = ServiceLookup.getIoNameResolutionFromFileService();
            if (service != null) {
               service.loadCatalog(catalog);
            }
         } catch (Exception e) {
            throw new Exception(e);
         }
      } else {
         throw new IllegalStateException("Catalog not found: " + catalog);
      }
   }

   public void clear() {
      IoNameResolutionFromFile service = ServiceLookup.getIoNameResolutionFromFileService();
      service.clearDictionary();      
   }
   
}
