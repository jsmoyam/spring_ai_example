package org.example.ia;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class IaApiClientTest {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public IaApiClientTest(String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    /**
     * Llama al endpoint /ai/generate para generar una respuesta.
     *
     * @param message El mensaje que se enviará al API.
     * @return Un mapa con la respuesta generada.
     */
    public Map<String, String> generate(String message) {
        String url = String.format("%s/ai/generate?message=%s", baseUrl, message);
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }

    public Map<String, String> ask(String message) {
        String url = String.format("%s/ai/ask?question=%s", baseUrl, message);
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }

    public String translate(String sourceLang, String destinationLang, String text) {
        // URL del endpoint de traducción
        String url = "http://localhost:8080/ai/translate"
                + "?sourceLanguage=" + sourceLang
                + "&destinationLanguage=" + destinationLang
                + "&text=" + text;

        // Realizar la solicitud GET y capturar la respuesta
        String response = restTemplate.getForObject(url, String.class);
        return response;

    }
}


