package org.csstudio.config.ioconfigurator.ldap;

import javax.naming.ldap.LdapName;

import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;

import com.google.common.base.Optional;

public class LdapNode {

    private final LdapName name;

    public LdapNode(LdapName name) {
        this.name = name;
    }

    public boolean isEpicsControll() {
        return name.toString().startsWith("ou");
    }

    public boolean isFacility() {
        return name.toString().startsWith(LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName());
    }

    public boolean isEpicsIOC() {
        return name.toString().startsWith(LdapEpicsControlsConfiguration.COMPONENT.getNodeTypeName());
    }

    public boolean isLeaf() {
        return name.toString().startsWith(LdapEpicsControlsConfiguration.IOC.getNodeTypeName());
    }

    public boolean allowsRemovalOfChilds() {
        return isLeaf();
    }

    public boolean isParentOfEcon() {
        return getChildAttributeValue().equals("epicsController");
    }
    
    public Optional<String> getChildAttribute() {
        String nodeName = name.toString();
        if (nodeName.startsWith(LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName())) {
            return Optional.of(LdapEpicsControlsConfiguration.COMPONENT.getNodeTypeName());
        } else if (nodeName.startsWith("ou")) {
            return Optional.of(LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName());
        } else if (nodeName.startsWith(LdapEpicsControlsConfiguration.COMPONENT.getNodeTypeName())) {
            return Optional.of(LdapEpicsControlsConfiguration.IOC.getNodeTypeName());
        } else {
            return Optional.absent();
        }
    }

    public String getChildAttributeValue() {
        Optional<String> childAttribute = getChildAttribute();
        if (!childAttribute.isPresent()) {
            throw new IllegalStateException("Can't determine child attribute for : "  + name.toString()); 
        }
        if (getChildAttribute().get().startsWith(LdapEpicsControlsConfiguration.COMPONENT.getNodeTypeName())) {
            return "epicsComponent";
        } else if (getChildAttribute().get().startsWith(LdapEpicsControlsConfiguration.IOC.getNodeTypeName())) {
            return "epicsController";
        } else  if (getChildAttribute().get().startsWith(LdapEpicsControlsConfiguration.FACILITY.getNodeTypeName())) {
            return "epicsFacility";
        } else {
            throw new IllegalStateException("Can't determine child attribute value for : "  + name.toString()); 
        }
    }

}
