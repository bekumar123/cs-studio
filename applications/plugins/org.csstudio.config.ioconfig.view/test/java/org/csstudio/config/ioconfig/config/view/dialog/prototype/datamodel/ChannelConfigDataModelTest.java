package org.csstudio.config.ioconfig.config.view.dialog.prototype.datamodel;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.csstudio.config.ioconfig.config.view.dialog.prototype.datamodel.helper.DataModelTestData;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.junit.Before;
import org.junit.Test;

public class ChannelConfigDataModelTest {

    private ChannelConfigDialogDataModel channelConfigDialogDataModel; 

    @Before
    public void setUp() {
        //@formatter:off
        channelConfigDialogDataModel = new ChannelConfigDialogDataModel(
                DataModelTestData.createSelectedSlave(), 
                DataModelTestData.createPrototypeList(), 
                DataModelTestData.createParsedModuleInfo());
                //@formatter:on
    }
    
    @Test
    public void shouldReturnModuleNumber() {
        ModuleNumber currentModuleNumber = channelConfigDialogDataModel.getCurrentModuleNumber();
        assertThat(currentModuleNumber.getValue(), is(210));
    }
}
