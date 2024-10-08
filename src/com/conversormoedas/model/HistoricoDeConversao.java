package com.conversormoedas.model;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HistoricoDeConversao {
    private List<String> conversoes;
    private static final int TAMANHO_MAXIMO = 10;
    private static final String ARQUIVO_HISTORICO = "resources/historico_conversoes.json";
    private final Gson gson;

    public HistoricoDeConversao() {
        this.conversoes = new ArrayList<>();
        this.gson = new Gson();
        carregarHistorico();
    }

    public void adicionarConversao(String conversao) {
        if (conversoes.size() >= TAMANHO_MAXIMO) {
            conversoes.remove(0);
        }
        conversoes.add(conversao);
        salvarHistorico();
    }

    public void exibirHistorico() {
        if (conversoes.isEmpty()) {
            System.out.println("Aviso: O histórico de conversões está vazio.");
            System.out.println("Realize uma conversão para preencher o histórico.");
        } else {
            System.out.println("Histórico de Conversões:");
            for (int i = 0; i < conversoes.size(); i++) {
                System.out.println((i + 1) + ". " + conversoes.get(i));
            }
        }
    }

    private void salvarHistorico() {
        try (Writer writer = new FileWriter(ARQUIVO_HISTORICO)) {
            gson.toJson(conversoes, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar o histórico: " + e.getMessage());
        }
    }

    private void carregarHistorico() {
        try (Reader reader = new FileReader(ARQUIVO_HISTORICO)) {
            Type tipoLista = new TypeToken<ArrayList<String>>(){}.getType();
            List<String> historicoCarregado = gson.fromJson(reader, tipoLista);
            if (historicoCarregado != null) {
                conversoes = historicoCarregado;
                while (conversoes.size() > TAMANHO_MAXIMO) {
                    conversoes.remove(0);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.out.println("Erro ao carregar o histórico: " + e.getMessage());
        }
    }
}