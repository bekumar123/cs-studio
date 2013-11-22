package org.csstudio.dct.test.nameresolution.file.types;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.InputStream;
import java.util.List;

import org.csstudio.dct.nameresolution.file.parser.SpsDescriptionParser;
import org.csstudio.dct.nameresolution.file.types.DescriptionEntry;
import org.csstudio.dct.nameresolution.file.types.EpicsAddress;
import org.csstudio.dct.nameresolution.file.types.IoName;
import org.csstudio.dct.nameresolution.file.types.IoNameDictionary;
import org.csstudio.dct.nameresolution.file.types.TcpConnectionNr;
import org.csstudio.dct.nameresolution.file.util.Utils;
import org.junit.Before;
import org.junit.Test;

public class TestIoNameDictionary {

   private List<DescriptionEntry> result;
   private IoNameDictionary ioNameDictionary;

   @Before
   public void init() throws Exception {
      InputStream is = getClass().getResourceAsStream("/fixtures/test.txt");
      try {
         List<String> content = Utils.readTextFromInputStream(is);
         SpsDescriptionParser spsDescriptionParser = new SpsDescriptionParser(new TcpConnectionNr(0), content, "test.txt");
         spsDescriptionParser.parse();
         result = spsDescriptionParser.getParseResult();
         ioNameDictionary = new IoNameDictionary();
         ioNameDictionary.addEntries(result);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Test
   public void testGetExistingName() {
      EpicsAddress epcisAddress = ioNameDictionary.get(new IoName("Bsp_BOOL"));
      assertThat(epcisAddress.getAddress(), is("@Siemens_S7: 0/0 'T=INT8,B=0'"));
      epcisAddress = ioNameDictionary.get(new IoName("Bsp_BYTE_ARRAY"));
      assertThat(epcisAddress.getAddress(), is("@Siemens_S7: 0/30 'T=ARRAY'"));
   }

   @Test
   public void testGetNonExistingName() {
      EpicsAddress epcisAddress = ioNameDictionary.get(new IoName("Bsp_BOO111L"));
      assertThat(epcisAddress.getAddress(), is("<Error: unknown Bsp_BOO111L>"));
   }

   @Test(expected = IllegalStateException.class)
   public void testAddDuplicate() {
      ioNameDictionary.addEntries(result);
   }

}
