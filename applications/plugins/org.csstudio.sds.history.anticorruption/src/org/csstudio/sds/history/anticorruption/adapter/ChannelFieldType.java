package org.csstudio.sds.history.anticorruption.adapter;


/**
 * No Description.
 * 
 * CME: Maybe the idea of this class is not good.
 * 
 * @author cmein
 *
 */
public enum ChannelFieldType {
	VAL, SEVR;

	/**
	 * Returns the field type for the given channel name, e.g.
	 * ChannelFieldType.SEVR for 'name.SEVR'. Default is ChannelFieldType.VALUE
	 * 
	 * @param channelName
	 * @return
	 */
	public static ChannelFieldType getChannelFieldType(String channelName) {
		if (channelName.endsWith(".SEVR")) {
			return ChannelFieldType.SEVR;
		}
		return ChannelFieldType.VAL;
	}

	/**
	 * Returns the given channelName without the field type.
	 * 
	 * Defined for '.SEVR'
	 * 
	 * @param channelName
	 * @param fieldType
	 * @return
	 */
	public static String getChannelNameWithoutFieldType(String channelName,
			ChannelFieldType fieldType) {
		String channelNameWithoutField = channelName;

		switch (fieldType) {
		case SEVR:
			if (channelName.endsWith(".SEVR"))
				return channelName.substring(0, channelName.length() - 5);
		default:
			break;
		}
		return channelNameWithoutField;
	}

	public static String removeValAndSevrFromChannelName(String channelName) {
		if (channelName.endsWith(".SEVR"))
			return channelName.substring(0, channelName.length() - 5);
		else if (channelName.endsWith(".VAL"))
			return channelName.substring(0, channelName.length() - 4);
		else
			return channelName;
	}
}