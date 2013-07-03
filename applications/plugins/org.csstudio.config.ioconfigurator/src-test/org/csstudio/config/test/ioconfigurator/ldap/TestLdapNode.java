package org.csstudio.config.test.ioconfigurator.ldap;

import static junit.framework.Assert.assertTrue;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.csstudio.config.ioconfigurator.ldap.LdapNode;
import org.junit.Test;

import com.google.common.base.Optional;

public class TestLdapNode {

    @Test
    public void testCreation() throws InvalidNameException {
        new LdapNode(new LdapName("econ=test"));
    }

    @Test
    public void testIsEpicsControll() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("ou=test")).isEpicsControll());
    }

    @Test
    public void testIsFacility() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("efan=test")).isFacility());
    }

    @Test
    public void testIsEpicsIOC() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("ecom=test")).isEpicsIOC());
    }

    @Test
    public void testIsLeaf() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("econ=test")).isLeaf());
    }
    

    @Test
    public void testAllowRemovalOfChilds() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("econ=test")).allowsRemovalOfChilds());
    }

    @Test
    public void testIsParentOfEcon() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("ecom=test")).isParentOfEcon());
    }
    
    @Test
    public void testChildAttributeEcom() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("efan=test")).getChildAttribute().get().equals("ecom"));        
    }

    @Test
    public void testChildAttributeEfan() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("ou=test")).getChildAttribute().get().equals("efan"));        
    }

    @Test
    public void testChildAttributeEcon() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("ecom=test")).getChildAttribute().get().equals("econ"));        
    }
    
    @Test
    public void testChildAttributeValueEcom() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("efan=test")).getChildAttributeValue().equals("epicsComponent"));        
    }

    @Test
    public void testChildAttributeValueEcon() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("ecom=test")).getChildAttributeValue().equals("epicsController"));        
    }

    @Test
    public void testChildAttributeValueEfan() throws InvalidNameException {
        assertTrue(new LdapNode(new LdapName("out=test")).getChildAttributeValue().equals("epicsFacility"));        
    }

    
}
