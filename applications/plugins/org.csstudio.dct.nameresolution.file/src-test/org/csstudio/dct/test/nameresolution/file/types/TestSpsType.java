package org.csstudio.dct.test.nameresolution.file.types;

import org.csstudio.dct.nameresolution.file.service.SpsParseException;
import org.csstudio.dct.nameresolution.file.types.SpsAddress;
import org.csstudio.dct.nameresolution.file.types.SpsType;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Optional;

public class TestSpsType {

   private final static Optional<SpsType> NO_BEFORE_TYPE = Optional.absent();

   @Test
   public void testStringType() throws SpsParseException {
      SpsType spsType = SpsType.getSpsType("STRING[14]");
      Assert.assertThat(spsType.getSize(), Is.is(16));
   }

   @Test
   public void testArrayType() throws SpsParseException {
      SpsType spsType = SpsType.getSpsType("ARRAY[0..9]");
      Assert.assertThat(spsType.getSize(), Is.is(10));
   }
   
   @Test
   public void testFirstEntry() {
      SpsType spsType = SpsType.BOOL;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(0), NO_BEFORE_TYPE);
      Assert.assertThat(spsAddress.getAddress(), Is.is(0));
   }

   @Test
   public void testFromBoolToByte() {
      SpsType spsType = SpsType.BYTE;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(0), Optional.of(SpsType.BOOL));
      Assert.assertThat(spsAddress.getAddress(), Is.is(1));
   }

   @Test
   public void testFromByteToByte() {
      SpsType spsType = SpsType.BYTE;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(1), Optional.of(SpsType.BYTE));
      Assert.assertThat(spsAddress.getAddress(), Is.is(2));
   }

   @Test
   public void testFromByteToInt() {
      SpsType spsType = SpsType.INT;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(2), Optional.of(SpsType.BYTE));
      Assert.assertThat(spsAddress.getAddress(), Is.is(4));
   }

   @Test
   public void testFromIntToBool() {
      SpsType spsType = SpsType.BOOL;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(4), Optional.of(SpsType.INT));
      Assert.assertThat(spsAddress.getAddress(), Is.is(6));
   }

   @Test
   public void testFromBoolToDInt() {
      SpsType spsType = SpsType.DINT;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(6), Optional.of(SpsType.BOOL));
      Assert.assertThat(spsAddress.getAddress(), Is.is(8));
   }

   @Test
   public void testFromDIntToWord() {
      SpsType spsType = SpsType.WORD;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(8), Optional.of(SpsType.DINT));
      Assert.assertThat(spsAddress.getAddress(), Is.is(12));
   }

   @Test
   public void testFromWordToDword() {
      SpsType spsType = SpsType.DWORD;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(12), Optional.of(SpsType.WORD));
      Assert.assertThat(spsAddress.getAddress(), Is.is(14));
   }

   @Test
   public void testFromDwordToReal() {
      SpsType spsType = SpsType.REAL;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(14), Optional.of(SpsType.DWORD));
      Assert.assertThat(spsAddress.getAddress(), Is.is(18));
   }

   @Test
   public void testFromRealToChar() {
      SpsType spsType = SpsType.CHAR;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(18), Optional.of(SpsType.REAL));
      Assert.assertThat(spsAddress.getAddress(), Is.is(22));
   }

   @Test
   public void testFromRealToDS33() {
      SpsType spsType = SpsType.DS33;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(18), Optional.of(SpsType.REAL));
      Assert.assertThat(spsAddress.getAddress(), Is.is(22));
   }

   @Test
   public void testFromDS33ToReal() {
      SpsType spsType = SpsType.REAL;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(18), Optional.of(SpsType.DS33));
      Assert.assertThat(spsAddress.getAddress(), Is.is(24));
   }

   @Test
   public void testBool() {
      SpsType spsType = SpsType.BOOL;
      SpsAddress spsAddress = spsType.calculateAddress(new SpsAddress(0,0), Optional.of(SpsType.BOOL));
      Assert.assertThat(spsAddress.getAddress(), Is.is(0));
      Assert.assertThat(spsAddress.getBitPos().get(), Is.is(1));      
      SpsAddress nextSpsAddress = spsType.calculateAddress(spsAddress, Optional.of(SpsType.BOOL));
      Assert.assertThat(nextSpsAddress.getBitPos().get(), Is.is(2));      
   }

}
