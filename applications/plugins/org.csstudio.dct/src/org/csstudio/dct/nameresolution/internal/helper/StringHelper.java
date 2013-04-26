package org.csstudio.dct.nameresolution.internal.helper;

public class StringHelper {

    public static String removePrefix(String value, String prefix) {
        if (value.startsWith(prefix)) {
            return value.substring(prefix.length());
        } else {
            return prefix;
        }
    }
}
