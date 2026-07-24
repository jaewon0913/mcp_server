package com.example.mcpserver.weather;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * wttr.in 을 사용한 초간단 날씨 조회 툴.
 * API 키가 필요 없어서 별도 설정 없이 바로 동작합니다.
 * (나중에 OpenWeatherMap 등 정식 API로 교체하고 싶다면
 *  이 클래스의 getWeatherByCity 구현부만 바꾸면 됩니다.)
 */
@Service
public class WeatherService {

    private final RestClient restClient;

    public WeatherService(@Value("${weather.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Tool(description = "도시 이름으로 현재 날씨를 간단히 조회합니다. 예: Seoul, Busan, Tokyo")
    public String getWeatherByCity(
            @ToolParam(description = "날씨를 조회할 도시 이름 (영문 권장, 예: Seoul)") String city) {

        try {
            String result = restClient.get()
                    // format=3 -> "도시: 날씨아이콘 기온 등" 형태의 한 줄 요약
                    .uri("/{city}?format=3", city)
                    .retrieve()
                    .body(String.class);

            if (result == null || result.isBlank()) {
                return "\"" + city + "\"에 대한 날씨 정보를 찾을 수 없습니다.";
            }
            return result.trim();

        } catch (RestClientException e) {
            // 모델이 이해할 수 있는 메시지로 감싸서 반환 (에러 그대로 던지지 않기)
            return "날씨 조회 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
