package org.csstudio.dct.nameresolution.file.impl.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.csstudio.dct.nameresolution.file.parser.CatalogEntry;
import org.csstudio.dct.nameresolution.file.parser.CatalogParser;
import org.csstudio.dct.nameresolution.file.parser.SpsDescriptionParser;
import org.csstudio.dct.nameresolution.file.service.IoNameResolutionFromFile;
import org.csstudio.dct.nameresolution.file.service.SpsParseException;
import org.csstudio.dct.nameresolution.file.types.IoName;
import org.csstudio.dct.nameresolution.file.types.IoNameDictionary;
import org.csstudio.dct.nameresolution.file.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class IoNameResolutionFromFileImpl implements IoNameResolutionFromFile {

   private static final Logger LOG = LoggerFactory.getLogger(IoNameResolutionFromFileImpl.class);

   private static IoNameDictionary ioNameDictionary = new IoNameDictionary();

   /**
    * Translate an ioname to a an Epics-Address.
    */
   @Override
   public String resolveName(final String ioName, final String fieldName) {
      Preconditions.checkNotNull(ioName, "ioName must not be null");
      Preconditions.checkNotNull(fieldName, "fieldName must not be null");
      return ioNameDictionary.get(new IoName(ioName)).getAddress();
   }

   /**
    * Load all files listed in the catalog file.
    */
   @Override
   public void loadCatalog(final String catalog) throws SpsParseException, IOException {

      Preconditions.checkNotNull(catalog, "catalog must not be null");

      LOG.debug("loading catalog file: " + catalog);

      List<CatalogEntry> catalogEntries = readCatalogEntries(catalog);

      String catalogDirectory = getCatalogDirectory(catalog);
      ioNameDictionary = new IoNameDictionary();

      for (CatalogEntry entry : catalogEntries) {
         String nextFile = catalogDirectory + entry.getFileName();

         List<String> spsContent = Utils.readTextFromFile(nextFile);

         //@formatter:off
            SpsDescriptionParser spsDescriptionParser = new SpsDescriptionParser(
                  entry.getTcpConnectionNr(),
                  spsContent,
                  nextFile);
                  //@formatter:on

         spsDescriptionParser.parse();

         ioNameDictionary.addEntries(spsDescriptionParser.getParseResult());
      }

   }

   @Override
   public void clearDictionary() {
      ioNameDictionary = new IoNameDictionary();
   }

   private List<CatalogEntry> readCatalogEntries(String catalog) throws IOException, SpsParseException {
      List<String> content = Utils.readTextFromFile(catalog);
      CatalogParser catalogParser = new CatalogParser(content);
      catalogParser.parse();
      return catalogParser.getParseResult();
   }

   private String getCatalogDirectory(String catalog) {
      File catalogFile = new File(catalog);
      String catalogDirectory = catalogFile.getParent();
      if (!catalog.endsWith(File.separator)) {
         return catalogDirectory + File.separator;
      } else {
         return catalogDirectory;
      }

   }

}
