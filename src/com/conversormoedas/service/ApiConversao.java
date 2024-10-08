package com.conversormoedas.service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonParser;
import com.conversormoedas.util.ConfiguracaoApi;
import com.google.gson.JsonObject;
import java.time.Duration;

public class ApiConversao {
    private static final String API_URL_BASE = "https://v6.exchangerate-api.com/v6/";

    public static double obterTaxaCambio(String moedaOrigem, String moedaDestino) throws Exception {
        String apiKey = ConfiguracaoApi.getApiKey();
        String urlStr = API_URL_BASE + apiKey + "/latest/" + moedaOrigem;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        if (statusCode != 200) {
            throw new Exception("Falha na requisição. Código de status: " + statusCode);
        }

        String contentType = response.headers().firstValue("Content-Type")
                .orElse("");
        if (!contentType.contains("application/json")) {
            throw new Exception("Tipo de conteúdo inesperado: " + contentType);
        }

        String responseBody = response.body();
        JsonObject jsonObject = JsonParser
                .parseString(responseBody)
                .getAsJsonObject();

        String resultado = jsonObject.get("result")
                .getAsString();

        if ("success".equals(resultado)) {
            JsonObject taxas = jsonObject.getAsJsonObject("conversion_rates");
            return taxas.get(moedaDestino)
                    .getAsDouble();
        } else {
            throw new Exception("Falha ao obter taxa de câmbio: " + jsonObject.get("error-type")
                    .getAsString());
        }
    }
}
