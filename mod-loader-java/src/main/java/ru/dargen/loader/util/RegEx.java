package ru.dargen.loader.util;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class RegEx {

    public String replace(String string, Pattern pattern, Function<Matcher, String> replacement) {
        StringBuffer result = new StringBuffer();
        val matcher = pattern.matcher(string);
        while (matcher.find()) {
            matcher.appendReplacement(result,  Matcher.quoteReplacement(replacement.apply(matcher)));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public String replaceAll(String string, Function<Matcher, String> replacement, Pattern... patterns) {
        for (Pattern pattern : patterns) {
            string = replace(string, pattern, replacement);
        }

        return string;
    }

}
