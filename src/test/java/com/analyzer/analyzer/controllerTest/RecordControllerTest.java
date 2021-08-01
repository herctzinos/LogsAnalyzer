package com.analyzer.analyzer.controllerTest;

import com.analyzer.analyzer.controller.RecordController;
import com.analyzer.analyzer.utilities.AnalyzerUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RecordControllerTest {

    @Test
    public void Should_Return_Correct_Regex() throws Exception {
        String expected = "^(\\S+) (\\S+) (\\S+) \\[(.+?)\\] \\\"(.+?)\\\" (\\d{3}) (\\S+)";
        String actual = AnalyzerUtils.getAccessLogRegex();
        assertEquals(expected, actual);
    }

    @Test
    public void Should_Contain_Correct_String() {
        AnalyzerUtils.analyzedLogsList = AnalyzerUtils.analyzeLogFile(AnalyzerUtils.LogFilePath, AnalyzerUtils.fileName);
        RecordController recordController = new RecordController();
        HashMap<String, Map<String, Long>> top10Pages = recordController.getTop10Pages();
        String x = "{topPagesRequested={GET /images/NASA-logosmall.gif HTTP/1.0=96841";
        Assert.assertThat(top10Pages.toString(), CoreMatchers.containsString(x));
    }

}
