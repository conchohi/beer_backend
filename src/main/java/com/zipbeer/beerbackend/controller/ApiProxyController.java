package com.zipbeer.beerbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/word")
public class ApiProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ApiProxyController.class);

    @Value("${word_api_key}")
    private String apiKey;

    @Value("${api_certkey_no}")
    private String certkeyNo;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 생성자에서 RestTemplate과 ObjectMapper를 주입받습니다.
    public ApiProxyController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 단어 유효성을 검증하는 엔드포인트입니다.
     *
     * @param word 검증할 단어
     * @return 단어가 유효한지 여부를 Boolean 값으로 반환
     * @throws IOException JSON 파싱 오류 시 예외 발생
     */
    @GetMapping("/validate-word")
    public ResponseEntity<Boolean> validateWord(@RequestParam String word) throws IOException {
        // API 호출 URL을 구성합니다.
        String apiUrl = String.format("https://opendict.korean.go.kr/api/search?certkey_no=%s&key=%s&target_type=search&req_type=json&part=word&q=%s&sort=dict&start=1&num=10",
                certkeyNo, apiKey, word);

        // RestTemplate을 사용하여 API 호출을 수행하고 응답을 String으로 받습니다.
        String response = restTemplate.getForObject(apiUrl, String.class);

        // ObjectMapper를 사용하여 JSON 응답을 Map으로 파싱합니다.
        Map<String, Object> responseBody = objectMapper.readValue(response, Map.class);
        Map<String, Object> channel = (Map<String, Object>) responseBody.get("channel");

        // items 리스트가 null인지 확인합니다.
        List<Map<String, Object>> items = (List<Map<String, Object>>) channel.get("item");
        if (items == null) {
            logger.info("단어 '{}'이(가) 유효하지 않습니다. (items 리스트가 null입니다)", word);
            return ResponseEntity.ok(false);
        }

        // 단어가 유효한지 여부를 검증합니다.
        boolean isValid = items.stream().anyMatch(item -> word.equals(item.get("word")));

        // 단어 검증 성공 시 로그를 남깁니다.
        if (isValid) {
            logger.info("단어 '{}'이(가) 유효합니다.", word);
        } else {
            logger.info("단어 '{}'이(가) 유효하지 않습니다.", word);
        }

        // 단어 유효성 결과를 반환합니다.
        return ResponseEntity.ok(isValid);
    }
}
