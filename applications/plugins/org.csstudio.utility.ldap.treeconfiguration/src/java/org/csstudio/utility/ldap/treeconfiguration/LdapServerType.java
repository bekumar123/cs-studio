package org.csstudio.utility.ldap.treeconfiguration;

public enum LdapServerType {
    SUN_LDAP(false), OPEN_LDAP(true);
    
    private final boolean active;
    
    private LdapServerType(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
}
