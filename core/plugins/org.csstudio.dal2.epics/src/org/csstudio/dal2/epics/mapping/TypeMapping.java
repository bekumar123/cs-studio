package org.csstudio.dal2.epics.mapping;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;

import java.nio.channels.IllegalSelectorException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.mapping.TypeMapper.MapperRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A type mapping represents a consistent set of type mapper to convert between
 * the cs specific types and the client compatible types
 */
public class TypeMapping implements IEpicsTypeMapping {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TypeMapping.class);

	private final Map<Type<?>, TypeMapper<?>> _typeMapper = new HashMap<Type<?>, TypeMapper<?>>();

	/**
	 * Mapping from dbrType to TypeMapper. Only primary mapper are added to this
	 * map.
	 * <p>
	 * Example:<br/>
	 * ({@link DBRType#Double <-> {@link Type#DOUBLE}) is added<br/>
	 * ({@link DBRType#Double <-> {@link Type#DOUBLE_SEQ}) is <b>not</b> added
	 * <br/>
	 */
	private final Map<DBRType, TypeMapper<?>> _dbr2mapper = new HashMap<DBRType, TypeMapper<?>>();

	/**
	 * Mapping from dbrType to TypeMapper. Only primary sequence mapper are
	 * added to this map.
	 * <p>
	 * Example:<br/>
	 * ({@link DBRType#Double <-> {@link Type#DOUBLE}) is <b>not</b> added<br/>
	 * ({@link DBRType#Double <-> {@link Type#DOUBLE_SEQ}) is added <br/>
	 */
	private final Map<DBRType, TypeMapper<?>> _dbr2seqMapper = new HashMap<DBRType, TypeMapper<?>>();

	protected void registerMapper(TypeMapper<?> mapper) {
		_typeMapper.put(mapper.getType(), mapper);
		if (mapper.isPrimary()) {
			DBRType dbrType = mapper.getDBRType();
			if (_dbr2mapper.containsKey(dbrType)) {
				LOGGER.error("Invalid type mapping: Multiple primary type mapper using the same dbr type ("
						+ dbrType + ")");
			}
			_dbr2mapper.put(dbrType, mapper);
		} else if (mapper.isPrimarySquence()) {
			DBRType dbrType = mapper.getDBRType();
			if (_dbr2seqMapper.containsKey(dbrType)) {
				LOGGER.error("Invalid type mapping: Multiple primary sequence type mapper using the same dbr type ("
						+ dbrType + ")");
			}
			_dbr2seqMapper.put(dbrType, mapper);
		}
	}

	/**
	 * Provides the mapper for a given dal2 type
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeMapper<T> getMapper(Type<T> type) {
		TypeMapper<?> mapper = _typeMapper.get(type);
		if (mapper == null) {
			throw new IllegalStateException("no mapper registred for type "
					+ type);
		}
		return (TypeMapper<T>) mapper;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> IEpicsTypeMapper<T> getMapper(Type<T> type, Type nativeType) {

		assert type != null : "Precondition: type != null";
		assert nativeType != null : "Precondition: nativeType != null";

		TypeMapper<T> mapper;
		if (type == Type.NATIVE) {
			mapper = (TypeMapper<T>) createWrapper(getMapper(nativeType));
		} else {
			mapper = getMapper(type);
		}
		return mapper;
	}

	/**
	 * Provides the suitable type for a given dbr type
	 * 
	 * @throws IllegalSelectorException
	 *             if no mapping is defined
	 * @require dbrType != null
	 * @require elementCount >= 0
	 */
	@Override
	public Type<?> getType(DBRType dbrType, int elementCount) {
		assert dbrType != null : "Precondition: dbrType != null";
		assert elementCount >= 0 : "Precondition: elementCount >= 0 : "
				+ elementCount;

		if (elementCount > 1) {
			TypeMapper<?> mapper = _dbr2seqMapper.get(dbrType);
			if (mapper == null) {
				throw new IllegalStateException(
						"No mapping defined for dbrType " + dbrType);
			}
			assert mapper.isPrimarySquence() : "Check: mapper.isPrimarySquence()";
			return mapper.getType();
		} else {
			TypeMapper<?> mapper = _dbr2mapper.get(dbrType);
			if (mapper == null) {
				throw new IllegalStateException(
						"No mapping defined for dbrType " + dbrType);
			}
			assert mapper.isPrimary() : "Check: mapper.isPrimary()";
			return mapper.getType();
		}
	}

	protected Set<Type<?>> getMappedTypes() {
		return _typeMapper.keySet();
	}

	/**
	 * Creates a type mapper wrapping the mapper of the native type
	 * 
	 * @param nativeTypeMapper
	 * @return
	 */
	private static TypeMapper<Object> createWrapper(
			final TypeMapper<?> nativeTypeMapper) {
		return new TypeMapper<Object>(Type.NATIVE, DBRType.UNKNOWN,
				MapperRole.ADDITIONAL) {

			@Override
			public Object mapValue(DBR dbrValue, Characteristics characteristics) {
				return nativeTypeMapper.mapValue(dbrValue, characteristics);
			}

			@Override
			public DBRType getDBRCtrlType() {
				return nativeTypeMapper.getDBRCtrlType();
			}
		};
	}

}
