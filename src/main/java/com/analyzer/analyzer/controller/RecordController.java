package com.analyzer.analyzer.controller;

import com.analyzer.analyzer.utilities.AnalyzerUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class RecordController {

    @GetMapping("/top10PagesRequested")
    public HashMap<String, Map<String, Long>> getTop10Pages() {
        Map<String, Long> topRequestedPagesOccurrences =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(5)).collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Map<String, Long> topTenRequestedPages =
                topRequestedPagesOccurrences.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        HashMap<String, Map<String, Long>> map = new HashMap<>();
        map.put("topPagesRequested", topTenRequestedPages);
        return map;
    }

    @GetMapping("/successfulRequestsPercentage")
    public HashMap<String, Float> successfulRequestsPercentage() {
        Map<String, Long> httpCodeOccurrences =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(6)).collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        long sumOfSuccessfulCodes = httpCodeOccurrences.entrySet().stream()
                .filter(o -> o.getKey().matches("(^2|^3).*"))
                .mapToLong(o -> o.getValue()).sum();
        HashMap<String, Float> map = new HashMap<>();
        map.put("successfulRequestsPercentage", ((sumOfSuccessfulCodes * 100.0f) / AnalyzerUtils.analyzedLogsList.size()));
        return map;
    }

    @GetMapping("/unsuccessfulRequestsPercentage")
    public HashMap<String, Float> unSuccessfulRequestsPercentage() {
        Map<String, Long> httpCodeOccurrences =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(6))
                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        long sumOfUnsuccessfulCodes = httpCodeOccurrences.entrySet().stream()
                .filter(o -> !o.getKey().matches("(^2|^3).*"))
                .mapToLong(o -> o.getValue()).sum();
        HashMap<String, Float> map = new HashMap<>();
        map.put("unsuccessfulRequestsPercentage", ((sumOfUnsuccessfulCodes * 100.0f) / AnalyzerUtils.analyzedLogsList.size()));
        return map;
    }

    @GetMapping("/top10FailedRequests")
    public HashMap<String, Map<String, Long>> getTop10failedRequests() {

        List<Matcher> failedRequests =
                AnalyzerUtils.analyzedLogsList.stream()
                        .filter(x -> !x.group(6).matches("(^2|^3).*"))
                        .collect(Collectors.toList());

        Map<String, Long> occurrencesOfFailedRequests =
                failedRequests.stream().map(x -> x.group(1))
                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Map<String, Long> topTenFailedRequestsOccurrences = getTop10OccurrencesSorted(occurrencesOfFailedRequests);

        HashMap<String, Map<String, Long>> map = new HashMap<>();
        map.put("top10FailedRequests", topTenFailedRequestsOccurrences);
        return map;
    }

    @GetMapping("/top10Requests")
    public HashMap<String, Map<String, Long>> getTop10Requests() {

        Map<String, Long> occurrencesOfRequests =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(1))
                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Map<String, Long> topTenOccurrencesOfRequests = getTop10OccurrencesSorted(occurrencesOfRequests);

        HashMap<String, Map<String, Long>> map = new HashMap<>();
        map.put("top10Requests", topTenOccurrencesOfRequests);
        return map;
    }

    @GetMapping("/top10RequestsDetailed")
    public HashMap<String, HashMap<String, Map<Object, Long>>> getTop10RequestsDetailed() {
        HashMap<String, Map<Object, Long>> top10RequestsDetailedOutput = new HashMap<>();
        Map<String, Long> occurrencesOfRequests =
                AnalyzerUtils.analyzedLogsList.stream().map(x -> x.group(1))
                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        List<Map.Entry<String, Long>> topTenOccurrencesOfRequests =
                occurrencesOfRequests.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toList());

        for (Map.Entry<String, Long> pageMatcher : topTenOccurrencesOfRequests) {
            Map<Object, Long> topPagesRequestedForEachTop10 =
                    AnalyzerUtils.analyzedLogsList.stream()
                            .filter(x -> x.group(1).matches(pageMatcher.getKey()))
                            .collect(Collectors.groupingBy(x -> x.group(5), Collectors.counting()));

            Map<Object, Long> topFiveDetailed =
                    topPagesRequestedForEachTop10.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                            .limit(5)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            top10RequestsDetailedOutput.put(pageMatcher.getKey(), topFiveDetailed);
        }
        HashMap<String, HashMap<String, Map<Object, Long>>> map = new HashMap<>();
        map.put("top10Requests", top10RequestsDetailedOutput);
        return map;
    }

    private Map<String, Long> getTop10OccurrencesSorted(Map<String, Long> occurrences) {
        return occurrences.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
