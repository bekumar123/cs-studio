package org.csstudio.config.ioconfig.editorparts;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.Is;
import org.junit.Test;

public class BitMaskImplTest {

    private BitMask bitMask = new BitMaskImpl();

    @Test
    public void testBitMaskForBit0And0 () {
        int result = bitMask.getValueFromBitMask(0, 0, getOneByteTestData());
        assertThat(result, Is.is(1));
    }

    @Test
    public void testBitMaskForBit1And2 () {
        int result = bitMask.getValueFromBitMask(1, 2, getOneByteTestData());
        assertThat(result, Is.is(3));
    }
    
    @Test
    public void testBitMaskForBit2And3 () {
        int result = bitMask.getValueFromBitMask(2, 3, getOneByteTestData());
        assertThat(result, Is.is(3));
    }

    @Test
    public void testBitMaskForBit1And3 () {
        int result = bitMask.getValueFromBitMask(1, 3, getOneByteTestData());
        assertThat(result, Is.is(7));
    }

    @Test
    public void testBitMaskForBit1AndTwoByte () {
        int result = bitMask.getValueFromBitMask(8, 8, getTwoByteTestData());
        assertThat(result, Is.is(1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWith3ByteData () {
        bitMask.getValueFromBitMask(8, 8, getThreeByteTestData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWithWrongData () {
        bitMask.getValueFromBitMask(8, 8, getWrongTestData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLegalMinBit () {
        bitMask.getValueFromBitMask(0, 8, getWrongTestData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIllegalMinBit () {
        bitMask.getValueFromBitMask(-8, 8, getWrongTestData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIllegalMaxBit () {
        bitMask.getValueFromBitMask(0, 16, getWrongTestData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLegalMaxBit () {
        bitMask.getValueFromBitMask(0, 15, getWrongTestData());
    }
    
    @Test
    public void testBitMaskForMinBitGreaterMaxBit () {
        int result = bitMask.getValueFromBitMask(1, 0, getOneByteTestData());
        assertThat(result, Is.is(1));
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

    private List<Integer> getWrongTestData() {
        List<Integer> testData = new ArrayList<Integer>();
        testData.add(300);
        testData.add(1);
        return testData;
    }

}
