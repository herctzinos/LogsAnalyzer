package com.analyzer.analyzer.controller;

import com.analyzer.analyzer.utilities.AnalyzerUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class RecordController {

    @GetMapping("/top10PagesRequested")
    public HashMap<String, Map<String, Long>> getTop10Pages() {
        Map<String, Long> occurrences =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(5)).collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Map<String, Long> topTen =
                occurrences.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        HashMap<String, Map<String, Long>> map = new HashMap<>();
        map.put("topPagesRequested", topTen);
        return map;
    }

    @GetMapping("/successfulRequestsPercentage")
    public HashMap<String, Float> successfulRequestsPercentage() {
        Map<String, Long> occurrences =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(6)).collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Map<String, Long> topHttp =
                occurrences.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        long sum = topHttp.entrySet().stream()
                .filter(o -> o.getKey().matches("(^2|^3).*"))
                .mapToLong(o -> o.getValue()).sum();
        HashMap<String, Float> map = new HashMap<>();
        map.put("successfulRequestsPercentage", ((sum * 100.0f) / AnalyzerUtils.analyzedLogsList.size()));
        return map;
    }

    @GetMapping("/unsuccessfulRequestsPercentage")
    public HashMap<String, Float> unSuccessfulRequestsPercentage() {
        Map<String, Long> occurrences =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(6))
                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Map<String, Long> topHttp =
                occurrences.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        long sum = topHttp.entrySet().stream()
                .filter(o -> !o.getKey().matches("(^2|^3).*"))
                .mapToLong(o -> o.getValue()).sum();
        HashMap<String, Float> map = new HashMap<>();
        map.put("unsuccessfulRequestsPercentage", ((sum * 100.0f) / AnalyzerUtils.analyzedLogsList.size()));
        return map;
    }

    @GetMapping("/top10FailedRequests")
    public HashMap<String, Map<String, Long>> getTop10failedRequests() {

        List<Matcher> failedrequests =
                AnalyzerUtils.analyzedLogsList.stream()
                        .filter(x -> !x.group(6).matches("(^2|^3).*"))
                        .collect(Collectors.toList());

        Map<String, Long> failedOccurrences =
                failedrequests.stream().map(x -> x.group(1))
                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Map<String, Long> topTen = getTop10OccurrencesSorted(failedOccurrences);

        HashMap<String, Map<String, Long>> map = new HashMap<>();
        map.put("top10FailedRequests", topTen);
        return map;
    }

    @GetMapping("/top10Requests")
    public HashMap<String, Map<String, Long>> getTop10Requests() {

        Map<String, Long> occurrences =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(1))
                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Map<String, Long> topTen = getTop10OccurrencesSorted(occurrences);

        HashMap<String, Map<String, Long>> map = new HashMap<>();
        map.put("top10Requests", topTen);
        return map;
    }

    @GetMapping("/top10RequestsDetailed")
    public HashMap<String, HashMap<String, Map<Object, Long>>> getTop10RequestsDetailed() {
        HashMap<String, Map<Object, Long>> finalOutput = new HashMap<>();
        Map<String, Long> occurrences =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(1))
                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        List<Map.Entry<String, Long>> topTen =
                occurrences.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toList());

        for (Map.Entry<String, Long> pageMatcher : topTen) {
            Map<Object, Long> topPagesRequestedForEachTop10 =
                    AnalyzerUtils.analyzedLogsList.stream()
                            .filter(x -> x.group(1).matches(pageMatcher.getKey()))
                            .collect(Collectors.groupingBy(x -> x.group(5), Collectors.counting()));

            Map<Object, Long> topFiveDetailed =
                    topPagesRequestedForEachTop10.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                            .limit(5)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            finalOutput.put(pageMatcher.getKey(), topFiveDetailed);
        }
        HashMap<String, HashMap<String, Map<Object, Long>>> map = new HashMap<>();
        map.put("top10Requests", finalOutput);
        return map;
    }

    private Map<String, Long> getTop10OccurrencesSorted(Map<String, Long> occurrences) {
        return occurrences.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
