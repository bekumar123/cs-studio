package org.csstudio.dct.model.internal.sync;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.commands.RemoveRecordCommand;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.Record;
import org.csstudio.dct.model.internal.sync.RecordSync;
import org.eclipse.gef.commands.Command;
import org.junit.Before;
import org.junit.Test;

public class TestRecordSync {

    private IPrototype prototype;

    private InstanceFromLibrary instance;

    private List<IInstance> instances;

    @Before
    public void setUp() {
        prototype = new Prototype("testprototype", UUID.randomUUID());
        instance = new InstanceFromLibrary("test", prototype, UUID.randomUUID());
        instances = new ArrayList<IInstance>();
        instances.add(instance);
    }

    @Test
    public void testEmpty() {
        RecordSync rms = new RecordSync(instances);
        List<Command> commands = rms.calculateCommands();
        assertTrue(commands.isEmpty());
    }

    @Test
    public void testInstanceNotFromLibrary() {
        List<IInstance> instances = new ArrayList<IInstance>();
        instances.add(new InstanceNotFromLibrary("test", prototype, UUID.randomUUID()));
        RecordSync rms = new RecordSync(instances);
        List<Command> commands = rms.calculateCommands();
        assertTrue(commands.isEmpty());
    }

    @Test
    public void testAddRecord() {

        prototype.addRecord(new Record("test", "type", UUID.randomUUID()));

        RecordSync rms = new RecordSync(instances);

        List<Command> commands = rms.calculateCommands();
        assertTrue(commands.size() == 1);
        assertTrue(commands.get(0) instanceof AddRecordCommand);
    }

    @Test
    public void testRemoveRecord() {

        instance.addRecord(new Record("test", "type", UUID.randomUUID()));

        RecordSync rms = new RecordSync(instances);

        List<Command> commands = rms.calculateCommands();
        assertTrue(commands.size() == 1);
        assertTrue(commands.get(0) instanceof RemoveRecordCommand);
    }

    @Test
    public void testRemoveAndAddRecord() {

        instance.addRecord(new Record("test2", "type", UUID.randomUUID()));
        prototype.addRecord(new Record("test1", "type", UUID.randomUUID()));

        RecordSync rms = new RecordSync(instances);

        List<Command> commands = rms.calculateCommands();
        assertTrue(commands.size() == 2);
        assertTrue(commands.get(0) instanceof AddRecordCommand);
        assertTrue(commands.get(1) instanceof RemoveRecordCommand);
    }

    @Test
    public void testRemoveAndAddRecordComplex() {

        instance.addRecord(new Record("test2", "type", UUID.randomUUID()));
        instances.add(new InstanceNotFromLibrary("test", prototype, UUID.randomUUID()));

        prototype.addRecord(new Record("test1", "type", UUID.randomUUID()));

        RecordSync rms = new RecordSync(instances);

        List<Command> commands = rms.calculateCommands();
        assertTrue(commands.size() == 2);
        assertTrue(commands.get(0) instanceof AddRecordCommand);
        assertTrue(commands.get(1) instanceof RemoveRecordCommand);
    }

}
