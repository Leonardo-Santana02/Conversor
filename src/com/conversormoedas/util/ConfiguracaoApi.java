package com.conversormoedas.util;
public class ConfiguracaoApi {
    private static String apiKey;

    public static void setApiKey(String chave) {
        apiKey = chave;
    }

    public static String getApiKey() {
        return apiKey;
    }
}