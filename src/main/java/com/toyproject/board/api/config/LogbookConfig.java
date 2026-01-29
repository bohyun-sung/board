package com.toyproject.board.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.zalando.logbook.*;
import org.zalando.logbook.core.CurlHttpLogFormatter;
import org.zalando.logbook.core.DefaultHttpLogFormatter;
import org.zalando.logbook.core.DefaultHttpLogWriter;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.json.JsonBodyFilters;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.servlet.LogbookFilter;

import java.io.IOException;
import java.util.Set;

import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.Conditions.requestTo;
import static org.zalando.logbook.core.HeaderFilters.replaceHeaders;

@Configuration
public class LogbookConfig {

    @Bean
    public Logbook logbook() {
        ObjectMapper mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);

        return Logbook.builder()
                // 1. 특정 경로 제외
                .condition(exclude(
                        requestTo("/v3/api-docs/**"),
                        requestTo("/swagger-ui/**"),
                        requestTo("/actuator/**")
                ))
                // 2. 헤더 마스킹 (이름이 일치하는 헤더의 값을 "XXXXX"로 대체)
                .headerFilter(replaceHeaders("Authorization", "XXXXX"))
                .headerFilter(replaceHeaders("Set-Cookie", "XXXXX"))

                // 3. 바디 마스킹 (ContentType과 Body 두 개의 인자를 받습니다)
                .bodyFilter(JsonBodyFilters.replaceJsonStringProperty(
                        Set.of("password", "token", "secret"), // 마스킹할 필드명들
                        "****"                                 // 대체할 값
                ))
                // 4. 출력 설정 (JSON 포맷)
                .sink(new DefaultSink(
//                        new JsonHttpLogFormatter(mapper),
                        new CustomLogFormatter(mapper),
                        new DefaultHttpLogWriter()
                ))
                .build();
    }

    // 요청과 응답을 서로 다른 포맷으로 보여주는 커스텀 포맷터 클래스
    private static class CustomLogFormatter implements HttpLogFormatter {
        private final JsonHttpLogFormatter jsonFormatter;
        private final CurlHttpLogFormatter curlFormatter = new CurlHttpLogFormatter();

        public CustomLogFormatter(ObjectMapper mapper) {
            this.jsonFormatter = new JsonHttpLogFormatter(mapper);
        }

        @Override
        public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
            // 요청은 cURL 명령어로 출력해서 바로 복사해서 쓸 수 있게 함
            return "\n[REQUEST]\n" + curlFormatter.format(precorrelation, request);
        }

        @Override
        public String format(Correlation correlation, HttpResponse response) throws IOException {
            // 응답은 예쁜 JSON과 실행 시간(ms)을 포함
            return String.format("\n[RESPONSE] ID: %s | Duration: %dms\n%s",
                    correlation.getId(),
                    correlation.getDuration().toMillis(),
                    jsonFormatter.format(correlation, response));
        }
    }
}
