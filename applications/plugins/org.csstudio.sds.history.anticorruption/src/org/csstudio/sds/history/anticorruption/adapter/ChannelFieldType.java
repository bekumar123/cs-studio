package org.csstudio.sds.history.anticorruption.adapter;


/**
 * No Description.
 * 
 * CME: Maybe the functionality to distinguish channel fields should move to different content enricher.
 * 
 * @author Christian Mein
 *
 */
public enum ChannelFieldType {
	VAL, SEVR, STAT;

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
		} else if (channelName.endsWith(".STAT")) {
			return ChannelFieldType.STAT;
		}
		return ChannelFieldType.VAL;
	}

	
	/**
	 * Returns the given channelName without the field type.
	 * 
	 * Defined for .SEVR ; .STAT ; .VAL
	 * 
	 * @param channelName
	 * @param fieldType
	 * @return
	 */
	public static String removeFieldTypeFromChannelName(String channelName) {
		if (channelName.endsWith(".SEVR"))
			return channelName.substring(0, channelName.length() - 5);
		else if (channelName.endsWith(".STAT"))
			return channelName.substring(0, channelName.length() - 5);
		else if (channelName.endsWith(".VAL"))
			return channelName.substring(0, channelName.length() - 4);
		else
			return channelName;
	}
}