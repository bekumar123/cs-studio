package org.csstudio.dct.test.nameresolution.test.file.parser;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.csstudio.dct.nameresolution.file.parser.CatalogEntry;
import org.csstudio.dct.nameresolution.file.parser.CatalogParser;
import org.csstudio.dct.nameresolution.file.service.SpsParseException;
import org.csstudio.dct.nameresolution.file.util.Utils;
import org.junit.Test;

public class TestCatalogParser {

   @Test
   public void testParser() throws Exception {
      InputStream is = getClass().getResourceAsStream("/fixtures/catalog.txt");

      try {
         List<String> content = Utils.readTextFromInputStream(is);

         CatalogParser catalogParser = new CatalogParser(content);
         catalogParser.parse();
         List<CatalogEntry> result = catalogParser.getParseResult();

         assertThat(result.size(), is(2));

         assertThat(result.get(0).getFileName(), is("complex.txt"));
         assertThat(result.get(0).getTcpConnectionNr().getTcpConnectionNr(), is(0));

         assertThat(result.get(1).getFileName(), is("test.txt"));
         assertThat(result.get(1).getTcpConnectionNr().getTcpConnectionNr(), is(1));

      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   @Test(expected = SpsParseException.class)
   public void testParserWithException() throws IOException, SpsParseException {
      InputStream is = getClass().getResourceAsStream("/fixtures/invalid_catalog.txt");
      List<String> content = Utils.readTextFromInputStream(is);
      CatalogParser catalogParser = new CatalogParser(content);
      catalogParser.parse();
   }
}
