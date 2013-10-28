package org.csstudio.config.ioconfig.config.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

public class WatchableValue <T> {
    
    private final List<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
    
    T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        for (PropertyChangeListener propertyChangeListener : listeners) {
            propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "value", this.value, value));
        }
        this.value = value;
    }
        
    public final boolean addListener(final PropertyChangeListener x) {
        if(x == null) return false;
        return this.listeners.add(x);
    }
    public final boolean removeListener(final PropertyChangeListener x) {
        return this.listeners.remove(x);
    }

}
