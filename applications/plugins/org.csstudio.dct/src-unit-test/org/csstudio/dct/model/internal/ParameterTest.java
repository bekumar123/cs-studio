/**
 * 
 */
package org.csstudio.dct.model.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.csstudio.dct.model.internal.Parameter;
import org.junit.Before;
import org.junit.Test;

/**
 * Testcases for {@link Parameter}.
 * 
 * @author Sven Wende
 *
 */
public final class ParameterTest {
	private Parameter parameter;
	private String name;
	private String defaultValue;
    private String descripion;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		name = "name";
		defaultValue = "defaultValue";
		descripion = "descriptionValue";
		parameter = new Parameter(name, defaultValue, descripion);
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals(name, parameter.getName());
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#getDefaultValue()}.
	 */
	@Test
	public void testGetDefaultValue() {
		assertEquals(defaultValue, parameter.getDefaultValue());
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#setDefaultValue(java.lang.String)}.
	 */
	@Test
	public void testSetDefaultValue() {
		assertEquals(defaultValue, parameter.getDefaultValue());
		parameter.setDefaultValue("defaultValue2");
		assertEquals("defaultValue2", parameter.getDefaultValue());
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Parameter#equals}.
	 */
	@Test
	public void testEquals() {
		assertEquals(parameter, new Parameter(parameter.getName(), parameter.getDefaultValue(), ""));
		assertNotSame(parameter, new Parameter(parameter.getName()+"x", parameter.getDefaultValue(), ""));
		assertNotSame(parameter, new Parameter(parameter.getName(), parameter.getDefaultValue()+"x", ""));
	}
}
