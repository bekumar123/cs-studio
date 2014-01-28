package org.csstudio.dal2.epics.mapping;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.service.cs.ICsTypeMapper;

public interface IEpicsTypeMapper<T> extends ICsTypeMapper<T> {

	/**
	 * Maps an DBR value object to a given java type including a typecheck
	 * 
	 * @param dbrValue
	 *            the dbrValue to be mapped; must not be null
	 * @param characteristics 
	 */
	T mapValue(DBR dbrValue, Characteristics characteristics);
	
	/**
	 * Provide a corresponding DBR control type, if such an type is available.
	 * Otherwise the "best known" type is provided.
	 */
	DBRType getDBRCtrlType();
}
