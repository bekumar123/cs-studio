package org.csstudio.dal2.dv;

import org.epics.vtype.VDouble;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VFloat;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VInt;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VShort;
import org.epics.vtype.VShortArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VType;

public class VTypes<T extends VType> extends Type<T> {

	public VTypes(Class<T> javaType) {
		super(javaType);
	}

	// public static final Type<EpicsAlarmSeverity> SEVERITY = new
	// Type<EpicsAlarmSeverity>(EpicsAlarmSeverity.class);
	public static final Type<VEnum> ENUM = new Type<VEnum>(VEnum.class);

	public static final Type<VString> STRING = new Type<VString>(VString.class);
	public static final Type<VDouble> DOUBLE = new Type<VDouble>(VDouble.class);
//	public static final Type<VByte> BYTE = new Type<VByte>(VByte.class);
	public static final Type<VInt> LONG = new Type<VInt>(VInt.class);
	public static final Type<VFloat> FLOAT = new Type<VFloat>(VFloat.class);
	public static final Type<VShort> SHORT = new Type<VShort>(VShort.class);

	public static final Type<VStringArray> STRING_SEQ = new Type<VStringArray>(
			VStringArray.class);
	public static final Type<VDoubleArray> DOUBLE_SEQ = new Type<VDoubleArray>(
			VDoubleArray.class);
//	public static final Type<VByteArray> BYTE_SEQ = new Type<VByteArray>(
//			VByteArray.class);
	public static final Type<VIntArray> LONG_SEQ = new Type<VIntArray>(
			VIntArray.class);
	public static final Type<VFloatArray> FLOAT_SEQ = new Type<VFloatArray>(
			VFloatArray.class);
	public static final Type<VShortArray> SHORT_SEQ = new Type<VShortArray>(
			VShortArray.class);

}
