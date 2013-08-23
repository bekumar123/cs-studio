package org.csstudio.dct.test.nameresolution;

import org.csstudio.dct.test.nameresolution.file.service.TestIoNameResolutionFromFile;
import org.csstudio.dct.test.nameresolution.file.types.TestIoNameDictionary;
import org.csstudio.dct.test.nameresolution.file.types.TestSpsType;
import org.csstudio.dct.test.nameresolution.test.file.parser.TestCatalogParser;
import org.csstudio.dct.test.nameresolution.test.file.parser.TestSpsDescriptionParser;
import org.csstudio.dct.test.nameresolution.test.file.parser.TestSpsDescriptionParserDs33;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
//@formatter:off
@Suite.SuiteClasses({
   TestIoNameDictionary.class, 
   TestIoNameResolutionFromFile.class,
   TestSpsType.class, 
   TestCatalogParser.class,
   TestSpsDescriptionParserDs33.class,
   TestSpsDescriptionParser.class})
   //@formatter:on
public class RunAllTests {

}
