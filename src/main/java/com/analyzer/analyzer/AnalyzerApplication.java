package com.analyzer.analyzer;

import com.analyzer.analyzer.utilities.AnalyzerUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class AnalyzerApplication {


	public static void main(String[] args) {
		SpringApplication.run(AnalyzerApplication.class, args);
		AnalyzerUtils.analyzedLogsList = AnalyzerUtils.analyzeLogFile(AnalyzerUtils.LogFilePath, AnalyzerUtils.fileName);
		System.out.println("File was analyzed...");
	}
}
