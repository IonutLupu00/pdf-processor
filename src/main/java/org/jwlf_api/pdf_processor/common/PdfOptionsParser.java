package org.jwlf_api.pdf_processor.common;

import org.apache.logging.log4j.util.Strings;
import org.jwlf_api.pdf_processor.split.SplitOption;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PdfOptionsParser {

    private static final String OPTIONS_PATTERN = "^[^=]+=?[^=]*$";
    private static final String OPTIONS_SEPARATOR = ";";
    
    public static Map<SplitOption, String> parseOptions(String options) throws PdfException {
        if (Strings.isBlank(options)) {
            return Collections.emptyMap();
        }

        Pattern optionsPattern = Pattern.compile(OPTIONS_PATTERN);
        if (!Strings.isBlank(options) && !optionsPattern.matcher(options).matches()) {
            throw new PdfException("Invalid options pattern. Example of valid options: 'option1=value1;option2=value2;'");
        }

        return Arrays.stream(options.split(OPTIONS_SEPARATOR))
                .map(optionDeclaration -> optionDeclaration.split("="))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> SplitOption.valueOf(parts[0].trim()),
                        parts -> parts[1].trim()
                ));
    }
}