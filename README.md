# Conversor de Moedas

Um conversor de moedas simples e eficiente que permite a conversão entre 9 tipos de moedas diferentes e gera um histórico de uso.

# Sobre

Este conversor de moedas foi desenvolvido para facilitar a conversão entre diferentes moedas, permitindo que o(a) usuário(a) realize conversões rápidas e acessíveis. Além disso, o aplicativo mantém um histórico de todas as conversões realizadas, para que o(a) usuário(a) possa acompanhar suas atividades.

## Funcionalidades

- Converte valores entre as moedas suportadas.
- Utiliza as taxas de câmbio mais recentes fornecidas pela API de Taxas de Câmbio.
- Exibe as taxas de câmbio utilizadas no momento da conversão.
- Interface amigável para o usuário.

# Moedas Suportadas

Esse conversor é capaz de converter até 9 tipos diferentes de moedas. Elas são:

- **BRL**: Real Brasileiro (R$)
- **ARS**: Peso Argentino ($)
- **BOB**: Boliviano (Bs)
- **CLP**: Peso Chileno ($)
- **COP**: Peso Colombiano ($)
- **USD**: Dólar Americano (U$)
- **CAD**: Dólar Canadense (C$)
- **JPY**: Iene Japonês (¥)
- **CNY**: Yuan Chinês (¥)

O conversor pode ser facilmente expandio para suportar novos tipos de moeda futuramente.

##Instalação

1. Clone este repositório:

    ```bash
    git clone https://github.com/seu_usuario/currency-converter.git
    ```

2. Navegue até o diretório do projeto:

    ```bash
    cd currency-converter
    ```

3. Configure sua chave de API para a API de Taxas de Câmbio. Atualize o arquivo `config.properties` ou o código-fonte diretamente para adicionar sua chave:

    ```properties
    exchange_rate_api_key=SUA_CHAVE_AQUI
    ```

4. Compile e execute o programa:

    ```bash
    javac -d bin src/*.java
    java -cp bin CurrencyConverter
    ```

5. Insira a API Key:
- **Para conseguir uma API Key, você precisa se cadastrar no site da ExchangeRate-API{https://www.exchangerate-api.com/} e clicar em Get Free Key!**
- **Com sua API Key em mãos, basta copiar e colar os dados da sua Key no terminal.**

