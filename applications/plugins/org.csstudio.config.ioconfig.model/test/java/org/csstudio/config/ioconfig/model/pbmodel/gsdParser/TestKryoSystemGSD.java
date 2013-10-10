package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.junit.Before;
import org.junit.Test;

public class TestKryoSystemGSD {

    private GSDFileDBO kryoTest;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
       kryoTest = GSDTestFiles.DESY_Kryo_IO_System.getFileAsGSDFileDBO();
    }

    @Test
    public void userKryoDataTest() throws Exception {
        final ParsedGsdFileModel parsedGsdFileModel = kryoTest.getParsedGsdFileModel();
        final List<Integer> out = parsedGsdFileModel.getExtUserPrmDataConst();
        assertNotNull(out);
        
        System.out.println("Start KRYO: ");
        
        System.out.println(out.size());
        for (Integer i : out) {
            System.out.println(i);
        }

        System.out.println("KRYO Data : ");
        
        Map<Integer, GsdModuleModel2>  map = parsedGsdFileModel.getModuleMap();
        
        SortedMap<Integer, KeyValuePair> result = map.get(1).getExtUserPrmDataRefMap();
       
        for (Map.Entry<Integer, KeyValuePair> entry : result.entrySet()) {
            System.out.println(entry.getValue().getKey());
            System.out.println(entry.getValue().getValue());
            System.out.println("*********");
            System.out.println(entry.getValue());
        }
        
        System.out.println("END KRYO\n\n");
    }

}
