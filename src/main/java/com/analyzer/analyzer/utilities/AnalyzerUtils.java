package com.analyzer.analyzer.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzerUtils {

    public static String LogFilePath = "C:\\Users\\tzinos\\Downloads\\NASA_access_log_Aug95";
    public static String fileName = "access_log_Aug95";
    public static ArrayList<Matcher> analyzedLogsList = new ArrayList<>();

    public static String getAccessLogRegex() {

        String regex1 = "^(\\S+)";                              // Client IP
        String regex2 = " (\\S+)";                              // -
        String regex3 = " (\\S+)";                              // -
        String regex4 = " \\[(.+?)\\]";                         // Date
        String regex5 = " \\\"(.+?)\\\"";                       // request method and url
        String regex6 = " (\\d{3})";                            // HTTP code
        String regex7 = " (\\S+)";                              // Number of bytes
        return regex1 + regex2 + regex3 + regex4 + regex5 + regex6 + regex7;
    }

    public static ArrayList<Matcher> analyzeLogFile(String path, String fileName) {
        ArrayList<Matcher> accessLogEntryMatcherList = new ArrayList<>();
        Pattern accessLogPattern = Pattern.compile(AnalyzerUtils.getAccessLogRegex());
        Matcher accessLogEntryMatcher;

        try {
            final BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path + "\\" + fileName), StandardCharsets.UTF_8));
            String line;
            int lineIndex = 1;
            while ((line = in.readLine()) != null) {
                accessLogEntryMatcher = accessLogPattern.matcher(line);
                if (accessLogEntryMatcher.matches()) {
                    accessLogEntryMatcherList.add(accessLogEntryMatcher);
                } else {
                    System.out.println("Format error in line " + lineIndex);
                }
                lineIndex++;
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return accessLogEntryMatcherList;
    }
}
