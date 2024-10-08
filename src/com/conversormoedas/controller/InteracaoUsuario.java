package com.conversormoedas.controller;

import com.conversormoedas.model.HistoricoDeConversao;
import com.conversormoedas.model.Moedas;
import com.conversormoedas.service.ApiConversao;
import com.conversormoedas.util.ConfiguracaoApi;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

public class InteracaoUsuario {
    private final HistoricoDeConversao historico;
    private final String[] moedasSimbolos;
    private final String[] moedasSiglas;
    private final Moedas[] moedas;

    public InteracaoUsuario() {
        this.historico = new HistoricoDeConversao();
        this.moedas = Moedas.values();
        this.moedasSiglas = Moedas.siglas;
        this.moedasSimbolos = Moedas.simbolos;

    }

    public void converterMoeda() {
        Scanner scanner = new Scanner(System.in);

        solicitarApiKey(scanner);

        boolean continuar = true;
        while(true) {
            try {
                exibirMenu();

                int opcaoOrigem;
                try {
                    opcaoOrigem = obterOpcaoMenuPrincipal(scanner);

                    if(opcaoOrigem == this.moedas.length + 1) {
                        System.out.println("Obrigado por usar o Conversor de Moedas. Até logo!");
                        break;
                    }

                    if(opcaoOrigem == this.moedas.length + 2) {
                        historico.exibirHistorico();
                        System.out.println("\nPressione 'Enter' para continuar...");
                        scanner.nextLine();
                        continue;
                    }

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                    if(!tratarErroContinuar(scanner)) break;
                    else continue;
                }

                Moedas moeda = this.moedas[opcaoOrigem - 1];
                System.out.println("===>>> Você escolheu a moeda " + moeda.name() +
                        " (" + moeda.getDescricao() + ")");

                boolean opcaoDestinoValida = false;
                int opcaoDestino = 0;
                while(!opcaoDestinoValida) {
                    System.out.println("\nPara qual moeda você quer converter?");
                    exibirOpcoesMoedas();

                    try {
                        opcaoDestino = obterOpcaoMoedas(scanner);
                        opcaoDestinoValida = true;
                    } catch(Exception e) {
                        System.out.println(e.getMessage());
                        if(!tratarErroContinuar(scanner)) {
                            continuar = false;
                            break;
                        }
                    }
                }
                if(!continuar) break;

                moeda = this.moedas[opcaoDestino - 1];
                System.out.println("===>>> Você escolheu a moeda " + moeda.name() +
                        " (" + moeda.getDescricao() + ")");
                System.out.println();

                String moedaOrigem = moedasSiglas[opcaoOrigem - 1];
                String moedaDestino = moedasSiglas[opcaoDestino - 1];
                String simboloOrigem = moedasSimbolos[opcaoOrigem - 1];
                String simboloDestino = moedasSimbolos[opcaoDestino - 1];

                double valor = 0;
                boolean valorValido = false;

                while(!valorValido) {
                    System.out.printf("Digite o valor em %s (%s): ", moedaOrigem, simboloOrigem);
                    String valorString = scanner.nextLine().trim();

                    if(valorString.isEmpty()) {
                        System.out.println("Você não digitou nenhum valor.");
                        if(!tratarErroContinuar(scanner)) {
                            continuar = false;
                            break;
                        } else continue;
                    }

                    valorString = valorString.replace(',', '.');

                    try {
                        NumberFormat format = NumberFormat.getInstance(Locale.US);
                        Number number = format.parse(valorString);
                        valor = number.doubleValue();

                        if(valor < 0) {
                            System.out.println("Número negativo não é aceito. Favor digitar um " +
                                    "número positivo maior que zero.");
                            continue;
                        }

                        if(valor == 0) {
                            System.out.println("Valor inválido. Favor digitar um número maior que zero.");
                            continue;
                        }
                        valorValido = true;
                    } catch(Exception e) {
                        System.out.println("Somente números são aceitos!");
                        System.out.println("Favor digitar novamente.");
                    }
                }
                if(!continuar) break;

                try {
                    double taxaCambio = ApiConversao.obterTaxaCambio(moedaOrigem, moedaDestino);
                    double resultado = valor * taxaCambio;

                    System.out.printf("O valor em (%s) %s%.2f equivale a %s%.2f (%s)%n",
                            obterNomeMoeda(moedaOrigem), simboloOrigem, valor,
                            simboloDestino, resultado, obterNomeMoeda(moedaDestino));

                    // Adiciona a conversão ao histórico
                    adicionarConversaoAoHistorico(valor, moedaOrigem, simboloOrigem,
                            resultado, moedaDestino, simboloDestino);
                } catch(Exception e) {
                    System.out.println("Erro ao converter moeda: " + e.getMessage());
                    if(!tratarErroContinuar(scanner)) break;
                    else continue;
                }

                if(!tratarDesejaRealizarOutraConversao(scanner)) break;
            } catch(Exception e) {
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage());
                if(!tratarErroContinuar(scanner)) break;
            }
        }
        scanner.close();
    }

    private void exibirMenu() {
        System.out.println("**************************************************");
        System.out.println(">>>>>> Bem-vindo(a) ao Conversor de Moedas! <<<<<<\n");
        System.out.println("De qual moeda você deseja converter?");
        exibeMoedas();
        int opcoesExtras = this.moedas.length + 1;
        System.out.printf("%d - Sair da Aplicação\n", opcoesExtras++);
        System.out.printf("%d - Exibir Histórico de Conversões\n", opcoesExtras);
        System.out.println("**************************************************");
        System.out.print("Digite aqui o número da sua escolha: ");
    }

    private void exibirOpcoesMoedas() {
        exibeMoedas();
        System.out.print("Digite aqui o número da sua escolha: ");
    }

    private void exibeMoedas() {
        for(Moedas moeda : this.moedas) {
            System.out.printf("%d - %s (%s)\n", moeda.ordinal() + 1, moeda.name(), moeda.getDescricao());
        }
    }

    private int obterOpcaoMenuPrincipal(Scanner scanner) {
        int limit = this.moedas.length + 2;
        String entrada = scanner.nextLine().trim();
        String mensagemErro = "Favor digitar um número entre 1 e " + limit + ".";
        if(entrada.isEmpty()) {
            throw new IllegalArgumentException("Você não escolheu nenhuma opção. " + mensagemErro);
        }
        try {
            int opcao = Integer.parseInt(entrada);
            if(opcao < 1 || opcao > limit) {
                throw new IllegalArgumentException("Opção inválida! " + mensagemErro);
            }
            return opcao;
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Opção inválida! " + mensagemErro);
        }
    }

    private int obterOpcaoMoedas(Scanner scanner) throws IllegalArgumentException {
        String entrada = scanner.nextLine().trim();
        String mensagemErro = "Favor digitar um número entre 1 e " + this.moedas.length + ".";
        if(entrada.isEmpty()) {
            throw new IllegalArgumentException("Você não escolheu nenhuma opção. " + mensagemErro);
        }
        try {
            int opcao = Integer.parseInt(entrada);
            if(opcao < 1 || opcao > this.moedas.length) {
                throw new IllegalArgumentException("Opção inválida! " + mensagemErro);
            }
            return opcao;
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Opção inválida! " + mensagemErro);
        }
    }

    private boolean tratarErroContinuar(Scanner scanner) {
        while(true) {
            System.out.print("Deseja continuar? (Digite 's' para sim ou 'n' para não): ");
            String resposta = scanner.nextLine().trim().toLowerCase();
            if(resposta.isEmpty()) {
                System.out.println("Você não escolheu nenhuma opção.");
                continue;
            }
            if(resposta.equals("s")) {
                return true;
            } else if(resposta.equals("n")) {
                System.out.println("Obrigado por usar o Conversor de Moedas. Até logo!");
                return false;
            } else {
                System.out.println("Resposta inválida. Favor digitar 's' para sim ou 'n' para não.");
            }
        }
    }

    private boolean tratarDesejaRealizarOutraConversao(Scanner scanner) {
        while(true) {
            System.out.println("Deseja realizar outra conversão? (Digite 's' para sim ou 'n' para não)");
            String resposta = scanner.nextLine().trim().toLowerCase();
            if(resposta.isEmpty()) {
                System.out.println("Você não escolheu nenhuma opção.");
                continue;
            }
            if(resposta.equals("s")) {

                return true;
            } else if(resposta.equals("n")) {

                System.out.println("Deseja exibir o histórico de conversões? (Digite 's' para sim ou 'n' para não)");
                resposta = scanner.nextLine().trim().toLowerCase();
                if(resposta.isEmpty()) {
                    System.out.println("Você não escolheu nenhuma opção. O histórico não será exibido.");
                } else if(resposta.equals("s")) {
                    historico.exibirHistorico();
                }
                System.out.println("Obrigado por usar o Conversor de Moedas. Até logo!");
                return false;
            } else {
                System.out.println("Resposta inválida. Favor digitar 's' para sim ou 'n' para não.");
            }
        }
    }

    private void adicionarConversaoAoHistorico(double valor, String moedaOrigem, String simboloOrigem,
                                               double resultado,
                                               String moedaDestino,
                                               String simboloDestino) {
        String dataHora = LocalDateTime.now()
                .format(DateTimeFormatter
                        .ofPattern("dd/MM/yyyy HH:mm:ss"));
        String nomeValorOrigem = String.format("%.2f %s", valor, moedaOrigem);
        String nomeValorDestino = String.format("%.2f %s", resultado, moedaDestino);
        String conversao = String.format("%s - Valor em %s %s = %s %s em %s.",
                dataHora, obterNomeMoeda(moedaOrigem), simboloOrigem + " " + nomeValorOrigem,
                simboloDestino, nomeValorDestino, obterNomeMoeda(moedaDestino));
        historico.adicionarConversao(conversao);
    }

    public String obterNomeValor(String moeda, double valor) {
        return String.format("%.2f %s", valor, moeda);
    }


    public String obterNomeMoeda(String moedaSigla) {
        for(Moedas moeda : this.moedas) {
            if(moeda.name().equalsIgnoreCase(moedaSigla)) return moeda.getDescricao();
        }
        return "Moeda Desconhecida";
    }

    private void solicitarApiKey(Scanner scanner) {
        String apiKey;
        do {
            System.out.print("Insira aqui a sua API Key: ");
            apiKey = scanner.nextLine().trim();
            if(apiKey.isEmpty()) {
                System.out.println("Aviso: A API Key não pode estar vazia. Favor tentar novamente.");
            }
        } while(apiKey.isEmpty());
        ConfiguracaoApi.setApiKey(apiKey);
    }
}
