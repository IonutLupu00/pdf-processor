package org.jwlf_api.pdf_processor.common.util;

import java.util.function.Consumer;

public class GeneralUtils {

    public static <T> void runIfNotNull(T value, Consumer<T> action) {
        if (value != null) {
            action.accept(value);
        }
    }
}
