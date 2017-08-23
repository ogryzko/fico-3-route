package ru.mbtc.fico3.route.util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BodyParser {
    public static String getMemberCode(InputStream input) throws IOException {
        if(input != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            Pattern pattern = Pattern.compile("<MemberCode>(.+?)</MemberCode>");
            String line;
            Matcher matcher;
            while ((line = reader.readLine()) != null) {
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return null;
    }
}
