package org.csstudio.config.ioconfig.editorparts;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.AbstractGsdPropertyModel;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.KeyValuePair;
import org.eclipse.swt.widgets.Group;
import org.hamcrest.core.Is;
import org.junit.Test;

public class AbstractGsNodeEditorTest {

    private static class BitMaskInterceptor implements BitMask {

        private int dataMinBit;
        private int dataMaxBit;
        private List<Integer> values;
        
        @Override
        public int getValueFromBitMask(int dataMinBit, int dataMaxBit, List<Integer> values) {
            this.dataMinBit = dataMinBit;
            this.dataMaxBit = dataMaxBit;
            this.values = values;
            return 0;
        }

        public int getDataMinBit() {
            return dataMinBit;
        }

        public int getDataMaxBit() {
            return dataMaxBit;
        }

        public List<Integer> getValues() {
            return values;
        }

        @Override
        public int getValueFromBitMask(int dataMinBit, int dataMaxBit, Integer lowByte) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getValueFromBitMask(int dataMinBit, int dataMaxBit, Integer highByte, Integer lowByte) {
            // TODO Auto-generated method stub
            return 0;
        }
        
    }
    
    private static class TestClass extends AbstractGsdNodeEditor<AbstractNodeSharedImpl<?,?>> {

        private List<Integer> userDataList;
        
        @Override
        public void fill(GSDFileDBO gsdFile) throws PersistenceException {
        }

        @Override
        GSDFileDBO getGsdFile() {
            return null;
        }

        @Override
        AbstractGsdPropertyModel getGsdPropertyModel() throws IOException {
            return null;
        }

        @Override
        Integer getPrmUserData(Integer index) {
            return null;
        }

        @Override
        List<Integer> getPrmUserDataList() {
            return userDataList;
        }

        @Override
        void setGsdFile(GSDFileDBO gsdFile) {
        }

        @Override
        void setPrmUserData(Integer index, Integer value, boolean firstAccess) {
        }
        
        //@formatter:off
        int testGetUserPrmDataValue(
                final List<Integer> userDataList,
                final KeyValuePair extUserPrmDataRef,
                final ExtUserPrmData extUserPrmData,
                final BitMask bitMaskIntcerceptor) {
            //@formatter:on
            this.userDataList = userDataList;
            return getUserPrmDataValue(extUserPrmDataRef, extUserPrmData, bitMaskIntcerceptor);
        }

    }
    
    private static class ExtUserPrmDataMock extends ExtUserPrmData {
        
        public ExtUserPrmDataMock(final int minBit, final int maxBit) {
            super(null, 0, "test");
            this.setMinBit(String.valueOf(minBit));
            this.setMaxBit(String.valueOf(maxBit));
        }
        
    }
        
    @Test
    public void testGetUserPrmDataValueMocked() {
        TestClass testClass = new TestClass();
                
        KeyValuePair key = new KeyValuePair("Ext_User_Prm_Data_Const(0)", null);
      
        ExtUserPrmData extUserPrmData = new ExtUserPrmDataMock(1,4);
                
        BitMaskInterceptor bmi = new BitMaskInterceptor();
        
        testClass.testGetUserPrmDataValue(getOneByteTestData(), key, extUserPrmData, bmi);
        
        assertThat(bmi.getValues().size(), Is.is(1));
        assertThat(bmi.getDataMinBit(), Is.is(1));
        assertThat(bmi.getDataMaxBit(), Is.is(4));
        assertThat(bmi.getValues().get(0), Is.is(15));
        
    }

    @Test
    public void testGetUserPrmDataValueReal() {
        TestClass testClass = new TestClass();
                
        KeyValuePair key = new KeyValuePair("Ext_User_Prm_Data_Const(0)",null);
      
        ExtUserPrmData extUserPrmData = new ExtUserPrmDataMock(0,1);
                
        BitMask bmi = new BitMaskImpl();
        
        int result = testClass.testGetUserPrmDataValue(getOneByteTestData(), key, extUserPrmData, bmi);
        
        assertThat(result, Is.is(3));
        
    }

    @Test
    public void testGetUserPrmDataValueRealWith2Bytes() {
        TestClass testClass = new TestClass();
                
        KeyValuePair key = new KeyValuePair("Ext_User_Prm_Data_Const(0)",null);
      
        ExtUserPrmData extUserPrmData = new ExtUserPrmDataMock(8,8);
                
        BitMask bmi = new BitMaskImpl();
        
        int result = testClass.testGetUserPrmDataValue(getTwoByteTestData(), key, extUserPrmData, bmi);
        
        assertThat(result, Is.is(1));
        
    }

    @Test(expected = IllegalStateException.class)
    public void testGetUserPrmDataValueRealWith4Bytes() {
        TestClass testClass = new TestClass();
                
        KeyValuePair key = new KeyValuePair("Ext_User_Prm_Data_Const(2)",null);
      
        ExtUserPrmData extUserPrmData = new ExtUserPrmDataMock(12,14);
                
        BitMask bmi = new BitMaskImpl();
        
        testClass.testGetUserPrmDataValue(getThreeByteTestData(), key, extUserPrmData, bmi);
                
    }

    private List<Integer> getOneByteTestData() {
        List<Integer> testData = new ArrayList<Integer>();
        testData.add(15);
        return testData;
    }
    
    private List<Integer> getTwoByteTestData() {
        List<Integer> testData = new ArrayList<Integer>();
        testData.add(0);
        testData.add(1);
        return testData;
    }
    
    private List<Integer> getThreeByteTestData() {
        List<Integer> testData = new ArrayList<Integer>();
        testData.add(0);
        testData.add(1);
        testData.add(1);
        return testData;
    }

}
