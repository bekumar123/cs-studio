package org.csstudio.dal2.epics.mapping;

import gov.aps.jca.dbr.DBRType;

import org.csstudio.dal2.dv.Type;

/**
 * The TypeMapper provides the mapping of epics data types to dal2 data types. A
 * concrete mapper should be registered for every Type defined in {@link Type}
 * 
 * @param <T>
 */
public abstract class TypeMapper<T> implements IEpicsTypeMapper<T> {

	private static final int CTRL_TYPE_OFFSET = 28;

	public static enum MapperRole {
		PRIMARY, PRIMARY_SEQUENCE, ADDITIONAL
	}
	
	/** Seconds of epoch start since UTC time start. */
	public static long TS_EPOCH_SEC_PAST_1970 = 7305 * 86400;

	private Type<T> _type;

	private DBRType _dbrType;

	private MapperRole _role;

	/**
	 * Constructor
	 * <p>
	 * <b>Note: Only one primary mapper is allowed for a dbr type</b>
	 * 
	 * @param type
	 *            the DAL type mapped by this mapper
	 * @param dbrType
	 *            the suitable dbr type
	 * @param flag
	 *            to indicate wether this is the primary mapper for the dbr
	 *            type.
	 * 
	 * @require type != null
	 * @require dbrType != null
	 */
	TypeMapper(Type<T> type, DBRType dbrType, MapperRole role) {
		assert type != null : "Precondition: type != null";
		assert dbrType != null : "Precondition: dbrType != null";
		assert role != null : "Precondition: role != null";

		_type = type;
		_dbrType = dbrType;
		_role = role;
	}

	@Override
	public Type<T> getType() {
		return _type;
	}

	/**
	 * Provides the DBRType mapped by this mapper
	 */
	protected DBRType getDBRType() {
		return _dbrType;
	}

	protected boolean isPrimary() {
		return _role == MapperRole.PRIMARY;
	}
	
	protected boolean isPrimarySquence() {
		return _role == MapperRole.PRIMARY_SEQUENCE;
	}

	@Override
	public DBRType getDBRCtrlType() {

		if (_dbrType.isENUM()) {
			// No CTRL type available for enum. Use best known.
			return DBRType.LABELS_ENUM;
		} else {
			return DBRType.forValue(_dbrType.getValue() + CTRL_TYPE_OFFSET);
		}
	}

	@Override
	public String toString() {
		return "TypeMapper: " + _dbrType + " <-> " + _type;
	}

}
