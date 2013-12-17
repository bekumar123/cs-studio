package org.csstudio.sds.history.anticorruption.adapter;

public enum ChannelFieldType {
	VALUE, SEVR, AM;

	/**
	 * Returns the field type for the given channel name, e.g. ChannelFieldType.SEVR for 'name.SEVR'.
	 * Default is ChannelFieldType.VALUE
	 * 
	 * @param channelName 
	 * @return 
	 */
	public static ChannelFieldType getChannelFieldType(String channelName) {
		if (channelName.endsWith(".SEVR")) {
			return ChannelFieldType.SEVR;
		} else if (channelName.endsWith(".AM")) {
			return ChannelFieldType.AM;
		}
		return ChannelFieldType.VALUE;
	}
	
	/**
	 * Returns the given channelName without the field type.
	 * 
	 * Defined for '.SEVR', '.AM'.
	 * 
	 * @param channelName
	 * @param fieldType
	 * @return
	 */
	public static String getChannelNameWithoutFieldType(String channelName, ChannelFieldType fieldType) {
		String channelNameWithoutField = channelName;
		
		switch (fieldType) {
		case SEVR:
			if (channelName.endsWith(".SEVR")) {
				channelNameWithoutField = channelName.substring(0, channelName.length() - 5);
			}
			break;
		case AM:
			if (channelName.endsWith(".AM")) {
				channelNameWithoutField = channelName.substring(0, channelName.length() - 3);
			}
			break;
		default:
			break;
		}
		
		return channelNameWithoutField;
	}
}