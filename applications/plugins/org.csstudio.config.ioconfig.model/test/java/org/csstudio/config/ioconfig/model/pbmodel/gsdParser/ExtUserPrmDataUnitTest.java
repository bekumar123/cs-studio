package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.types.ValueRange;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 20.07.2011
 */
public class ExtUserPrmDataUnitTest {
    
    private GSDFileDBO _gsdFileDBO;
    
    private final static String EXPECTED_OUTPUT = "BitArea(2-3)";
    private final static String WRONG_EXAMPLE = "BitArea(2-3) 1";
    private final static String SIMPLE_EXAMPLE = "BitArea(2-3)  0 0-1";
    private final static String EXAMPLE_WITH_COMMENT = "BitArea(2-3)  0 0-1; ignore me";
    private final static String EXAMPLE_WITH_TAB = "BitArea(2-3) \t  0 0-1; ignore me";
    private final static String EXAMPLE_WITH_MULTIPLE_SPACES = "BitArea(2-3)       0     0-1    ;   ignore me";
    private final static String EXAMPLE_WITH_WHITESPACE = "BitArea(2-3) \t \t\n 0 0-1\t\t";
    
    @Before
    public void setUp() throws Exception {
        _gsdFileDBO = new GSDFileDBO("JUnitTest", "#Profibus_DP\nVendor_Name            = JUnitTest");
    }

    @Test
    public void testBuildDataTypeParameter() {
        final ExtUserPrmData extUserPrmData = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        extUserPrmData.buildDataTypeParameter(SIMPLE_EXAMPLE);
        assertThat(extUserPrmData.getDataType(), Is.is(EXPECTED_OUTPUT));
    }

    @Test
    public void testBuildDataTypeParameterWithErrpr() {
        final ExtUserPrmData extUserPrmData = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        extUserPrmData.buildDataTypeParameter(WRONG_EXAMPLE);
        assertThat(extUserPrmData.getDataType(), Is.is(""));
    }

    @Test
    public void testBuildDataTypeParameterWithComment() {
        final ExtUserPrmData extUserPrmData = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        extUserPrmData.buildDataTypeParameter(EXAMPLE_WITH_COMMENT);
        assertThat(extUserPrmData.getDataType(), Is.is(EXPECTED_OUTPUT));        
    }
    
    @Test
    public void testBuildDataTypeParameterWithTab() {
        final ExtUserPrmData extUserPrmData = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        extUserPrmData.buildDataTypeParameter(EXAMPLE_WITH_TAB);
        assertThat(extUserPrmData.getDataType(), Is.is(EXPECTED_OUTPUT));        
    }

    @Test
    public void testBuildDataTypeParameterWithMultipleSpaces() {
        final ExtUserPrmData extUserPrmData = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        extUserPrmData.buildDataTypeParameter(EXAMPLE_WITH_MULTIPLE_SPACES);
        assertThat(extUserPrmData.getDataType(), Is.is(EXPECTED_OUTPUT));        
    }
    
    @Test
    public void testBuildDataTypeParameterWithWhitespace() {
        final ExtUserPrmData extUserPrmData = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        extUserPrmData.buildDataTypeParameter(EXAMPLE_WITH_WHITESPACE);
        assertThat(extUserPrmData.getDataType(), Is.is(EXPECTED_OUTPUT));        
    }
    
    @Test
    public void defaults() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        assertTrue(out.getDefault()==0);
        out.setDefault("0");
        assertTrue(out.getDefault()==0);
        out.setDefault("-100000000");
        assertTrue(out.getDefault()==-100000000);
        out.setDefault("100000000");
        assertTrue(out.getDefault()==100000000);
        
        
        out.setDefault("0xA");
        assertFalse(out.getDefault()==10);
        assertTrue(out.getDefault()==0);
        out.setDefault("ten");
        assertFalse(out.getDefault()==10);
        assertTrue(out.getDefault()==0);
    }
    
    @Test
    public void maxBit() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("0");
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("-100000000");
        assertTrue(out.getMaxBit()==-100000000);
        out.setMaxBit("100000000");
        assertTrue(out.getMaxBit()==100000000);
        
        
        out.setMaxBit("0xA");
        assertFalse(out.getMaxBit()==10);
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("ten");
        assertFalse(out.getMaxBit()==10);
        assertTrue(out.getMaxBit()==0);
        
    }
    
    
    @Test
    public void maxValue() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        
        out.setValueRange("-100", "0");
        assertEquals(-100, out.getMinValue());
        assertEquals(0, out.getMaxValue());
        
        out.setValueRange("-200000000", "-100000000");
        assertEquals(-200000000, out.getMinValue());
        assertEquals(-100000000, out.getMaxValue());
        
        out.setValueRange("200000000", "100000000");
        assertEquals(100000000, out.getMinValue());
        assertEquals(200000000, out.getMaxValue());
        
        out.setValueRange("0xA", "0xA0");
        assertEquals(10, out.getMinValue());
        assertEquals(160, out.getMaxValue());
        
    }
    
    @Test
    public void minBit() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "");
        assertTrue(out.getMinBit()==0);
        assertTrue(out.getMinBit()==0);
        out.setMinBit("0");
        assertTrue(out.getMinBit()==0);
        out.setMinBit("-100000000");
        assertTrue(out.getMinBit()==-100000000);
        out.setMinBit("100000000");
        assertTrue(out.getMinBit()==100000000);
                
        out.setMinBit("0xA");
        assertFalse(out.getMinBit()==10);
        assertTrue(out.getMinBit()==0);
        out.setMinBit("ten");
        assertFalse(out.getMinBit()==10);
        assertTrue(out.getMinBit()==0);
    }
        
    @Test
    public void text() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "desc");
        assertEquals(out.getText(), "desc");
        out.setText("");
        assertEquals(out.getText(), "");
        out.setText("^1234567890ߴqwertzuiop�+asdfghjkl��#yxcvbnm,.-QAY\\\"");
        assertEquals(out.getText(), "^1234567890ߴqwertzuiop�+asdfghjkl��#yxcvbnm,.-QAY\\\"");
        
    }
    
    @Test
    public void MinMaxBitFromDataType() {
        final ExtUserPrmData out = new ExtUserPrmData(new ParsedGsdFileModel(_gsdFileDBO), 1, "desc");
        ValueRange valueRange = new ValueRange(0,  511);
        out.setDataType("UINT8", Optional.of(valueRange));
        assertThat(out.getMinBit(), Is.is(0));
        assertThat(out.getMaxBit(), Is.is(8));        
    }
}
