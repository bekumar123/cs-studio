package org.csstudio.dal2.epics.mapping;

import gov.aps.jca.dbr.DBRType;

import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.cs.ICsTypeMapping;

public interface IEpicsTypeMapping extends ICsTypeMapping {

	@Override
	<T> IEpicsTypeMapper<T> getMapper(Type<T> type);

	@SuppressWarnings("rawtypes")
	@Override
	<T> IEpicsTypeMapper<T> getMapper(Type<T> type, Type nativeType);

	/**
	 * Provides the corresponding type for a dbr type.
	 * <p>
	 * If multiple mapper are registred for the dbr type the primary mapper is used.

	 * @param dbrType
	 * @return
	 */
	Type<?> getType(DBRType dbrType, int elementCount);

}
