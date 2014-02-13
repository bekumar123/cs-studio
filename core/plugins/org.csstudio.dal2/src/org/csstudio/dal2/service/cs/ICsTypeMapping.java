package org.csstudio.dal2.service.cs;

import org.csstudio.dal2.dv.Type;


/**
 * The {@link ICsTypeMapping} acts as registry for {@link ICsTypeMapper} used to map between
 * the types of the control system and the types requested by the client
 */
public interface ICsTypeMapping {

	/**
	 * Provides the dal2 type mapped by this mapper
	 */
	<T> ICsTypeMapper<T> getMapper(Type<T> type);

	/**
	 * Provides the mapper for a given type or uses the native type if the type
	 * is {@link Type#NATIVE}
	 * 
	 * @param type
	 *            the type
	 * @param nativeType
	 *            the native type
	 * @return
	 * 
	 * @require type != null
	 * @require nativeType != null
	 */
	@SuppressWarnings("rawtypes")
	<T> ICsTypeMapper<T> getMapper(Type<T> type, Type nativeType);

}
