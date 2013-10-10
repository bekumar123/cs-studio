package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultWrong {

    private GSDFileDBO kryoTest;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        kryoTest = GSDTestFiles.YP013051.getFileAsGSDFileDBO();
    }

    @Test
    public void userKryoDataTest() throws Exception {
        final ParsedGsdFileModel parsedGsdFileModel = kryoTest.getParsedGsdFileModel();
        final List<Integer> out = parsedGsdFileModel.getExtUserPrmDataConst();
        assertNotNull(out);
        
                
        SortedMap<Integer, KeyValuePair> result = parsedGsdFileModel.getExtUserPrmDataRefMap();
       
        for (Map.Entry<Integer, KeyValuePair> entry : result.entrySet()) {
            System.out.println(entry.getValue().getKey());
            System.out.println(entry.getValue().getValue());
            System.out.println(entry.getValue());
        }
        
    }

}
