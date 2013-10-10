package org.csstudio.dct.model.internal;

import static org.junit.Assert.assertThat;

import org.csstudio.dct.model.IRecord;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

public class RecordTest {

    private IRecord record;

    @Before
    public void setUp() {
        this.record = new Record();
    }

    @Test
    public void testSetIsMarkedForAchivedIsTrue() {
        record.setRecordArchived(true);
        assertThat(record.getRecordArchived(), Is.is(true));
    }
    
    @Test
    public void testSetIsMarkedForAchivedIsFalse() {
        record.setRecordArchived(false);
        assertThat(record.getRecordArchived(), Is.is(false));
    }

    @Test
    public void testSetArchived() {
        record.setArchived("testTrue", true);
        record.setArchived("testFalse", false);
        assertThat(record.getArchived("testTrue"), Is.is(true));
        assertThat(record.getArchived("testFalse"), Is.is(false));
    }

}
