package com.changanford.home;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断是否为html..
 */
public class HtmlHelper {
    private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    private Pattern pattern = Pattern.compile(HTML_PATTERN);

    public boolean hasHTMLTags(String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
