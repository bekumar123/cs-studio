package org.csstudio.dct.test.nameresolution.file.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;

import org.csstudio.dct.nameresolution.file.impl.service.IoNameResolutionFromFileImpl;
import org.csstudio.dct.nameresolution.file.service.IoNameResolutionFromFile;
import org.csstudio.dct.nameresolution.file.service.SpsParseException;
import org.junit.Test;

public class TestIoNameResolutionFromFile {

   //@Test
   public void testLookupSimpleCatalog() throws SpsParseException, IOException {
      IoNameResolutionFromFile ioNameResolutionFromFile = new IoNameResolutionFromFileImpl();
      ioNameResolutionFromFile.loadCatalog(getSimpleCatalogFile());
      String address = ioNameResolutionFromFile.resolveName("Bsp_BOOL", "fieldName");
      assertThat(address, is("@Siemens_S7: 0/0 'T=BOOL B=0'"));
   }

   //@Test
   public void testLookupCatalog2() throws SpsParseException, IOException {
      IoNameResolutionFromFile ioNameResolutionFromFile = new IoNameResolutionFromFileImpl();
      ioNameResolutionFromFile.loadCatalog(getCatalog2File());
      String address = ioNameResolutionFromFile.resolveName("Bsp_INT_Last", "fieldName");
      assertThat(address, is("@Siemens_S7: 0/312 'T=INT'"));
   }

   @Test
   public void testLookupDesyCatalog() throws SpsParseException, IOException {
      IoNameResolutionFromFile ioNameResolutionFromFile = new IoNameResolutionFromFileImpl();
      ioNameResolutionFromFile.loadCatalog(getDesyCatalogFile());
      String address = ioNameResolutionFromFile.resolveName("SE_44SI1211_Value", "fieldName");
      assertThat(address, is("@Siemens_S7: 0/0 'T=FLOAT'"));
   }

   private String getSimpleCatalogFile() {
      URL location = getClass().getResource("/fixtures/simple_catalog.txt");
      return location.getPath();
   }

   private String getCatalog2File() {
      URL location = getClass().getResource("/fixtures/catalog2.txt");
      return location.getPath();
   }

   private String getDesyCatalogFile() {
      URL location = getClass().getResource("/fixtures/desy/catalog.txt");
      return location.getPath();
   }

}