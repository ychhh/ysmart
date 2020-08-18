package com.ysmart.util;

import org.apache.commons.lang3.StringUtils;

public class StringConversionUtil {

    /**
     * 类名首字母转小写
     */
    public static String toLowercaseIndex(String name) {
        if (isNotEmpty(name)) {
            return name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
        }
        return name;
    }

    /**
     * 类名首字母转大写
     */
    public static String toUpperCaseIndex(String name) {
        if (isNotEmpty(name)) {
            return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        }
        return name;
    }
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }
}
