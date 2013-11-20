package com.cosylab.vdct.dbd;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.junit.Test;

public class DBDResolverTest {

    @Test
    public void testResolution() {
        assertThat(DBDResolver.getGUIType(DBDResolver.getGUIString(DBDConstants.DCT_ARCHIVE)),
                Is.is(DBDConstants.DCT_ARCHIVE));
    }
}
