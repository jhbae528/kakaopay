package com.kakaopay.payments.api.util;

import org.springframework.lang.Nullable;

public class StringUtils {

    public static boolean isNull(@Nullable Object str) {
        return (str == null || "".equals(str));
    }
}
