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
import org.csstudio.config.ioconfig.model.types.BitRange;
import org.csstudio.config.ioconfig.model.types.HighByte;
import org.csstudio.config.ioconfig.model.types.LowByte;
import org.hamcrest.core.Is;
import org.junit.Test;

import com.google.common.base.Optional;

public class AbstractGsNodeEditorTest {

    private static class BitMaskInterceptor implements BitMask {

        private int dataMinBit;
        private int dataMaxBit;
        
        public int getDataMinBit() {
            return dataMinBit;
        }

        public int getDataMaxBit() {
            return dataMaxBit;
        }

        @Override
        public int getValueFromBitMask(BitRange bitRange, Optional<HighByte> highByte, LowByte lowByte) {
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
        testData.add(1);
        testData.add(0);
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
