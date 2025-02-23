# Consumindo API de Filmes

Este projeto é uma aplicação Java que consome a API do The Movie DB para buscar e exibir informações sobre filmes.
Ele é feito a partir do desafio da Alura de #7DaysOfCode instruído pelo [Paulo Silveira](https://www.linkedin.com/in/paulosilveira/) e tem como objetivo praticar a integração com APIs RESTful.

## Funcionalidades

- Carrega a chave da API de um arquivo `.env` para proteção da chave individual.
- Faz requisições à API do The Movie DB para buscar filmes.
- Exibe os filmes retornados no console.

## Requisitos

- Java 16 ou superior
- Biblioteca `java.net.http` para fazer requisições HTTP
- Biblioteca `java.util.logging` para logging
- Arquivo `.env` contendo a chave da API (`API_KEY`)

## Como usar

1. Clone o repositório:

    ```sh
    git clone <https://github.com/miletodev/consumindo-api-filmes.git>
    cd consumindo-api-filmes
    ```

2. Crie um arquivo `.env` na raiz do projeto e adicione sua chave da API:

    ```env
    API_KEY=your_api_key_here
    ```
 Para gerar uma chave da API, acesse o site do [The Movie DB](https://www.themoviedb.org/) e crie uma conta. Depois, vá em `Settings` > `API` e gere uma nova chave.

3. Compile e execute o projeto:

    ```sh
    javac -d out/production/consumindo-api-filmes src/Main.java
    java -cp out/production/consumindo-api-filmes Main
    ```

## Estrutura do Projeto

- `src/Main.java`: Contém a lógica principal para carregar a chave da API, fazer a requisição e exibir os filmes.
- `.env`: Arquivo contendo a chave da API.

## Exemplo de Saída

Os Sete Samurais (1954) - 8.5/10
Os Bons Companheiros (1990) - 8.5/10
O Túmulo dos Vagalumes (1988) - 8.5/10
Cinema Paradiso (1988) - 8.4/10

## Contribuição

1. Faça um fork do projeto.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`).
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`).
4. Faça push para a branch (`git push origin feature/nova-feature`).
5. Abra um Pull Request.

## Estrutura do Objeto Movie (#7DaysOfCode - Dia 3)

Este texto aborda as decisões sobre a estrutura do objeto Movie no contexto do desafio #7DaysOfCode.

### Setters e Construtor Padrão:

O uso de record em Java torna o objeto Movie automaticamente imutável. Essa imutabilidade é crucial porque:

- Garante a integridade dos dados do filme, evitando alterações acidentais após a criação.
- Alinha-se com a natureza de entidades de domínio simples como "Filme", cujos atributos são geralmente fixos.
- Simplifica a manutenção e previne bugs relacionados a mudanças de estado inesperadas.

Portanto, a inclusão de setters é desnecessária, pois o estado do filme é definido na criação e não deve ser modificado posteriormente.

### Interfaceamento

O record já oferece uma estrutura concisa e suficiente para encapsular os dados de um filme. Interfaces seriam relevantes para definir comportamentos comuns entre diferentes tipos de objetos relacionados (ex: Movie e Series). No entanto, como Movie é uma entidade simples, a adição de uma interface não se justifica pela ausência de métodos adicionais.

### Imutabilidade:

A imutabilidade do objeto Movie é essencial pelos seguintes motivos:

- Integridade dos dados: Previne alterações acidentais após a criação.
- Consistência com o domínio: Reflete a natureza estática dos atributos de um filme.
- Facilidade de manutenção e prevenção de bugs: Evita erros decorrentes de mudanças de estado imprevistas.
- Compatibilidade com programação funcional: Facilita o uso com APIs como streams e coleções imutáveis.
- Segurança em threads: Permite o compartilhamento seguro entre threads, eliminando a necessidade de sincronização.
- Simplicidade da API: Dispensa métodos de acesso e modificação.
- Previsibilidade e confiabilidade: Torna o código mais fácil de entender e depurar.
- Integração com práticas modernas: Alinha-se com as melhores práticas de desenvolvimento.

Em resumo: O uso de record garante a imutabilidade do objeto Movie, o que traz diversos benefícios em termos de segurança, manutenibilidade e alinhamento com boas práticas de programação. A imutabilidade dispensa a necessidade de setters e, dada a simplicidade da entidade, o uso de interfaces não se justifica.

## Estrutura Final do Projeto

O projeto é estruturado para garantir clareza, manutenibilidade e integração eficiente com a API do The Movie DB. Abaixo está uma visão geral dos principais componentes e a justificativa por trás de seu design:

### 1. `TmdbApiClient`

- **Propósito**: Lida com as requisições à API do The Movie DB e processa as respostas.
- **Principais Funcionalidades**:
  - Utiliza `HttpClient` para fazer requisições HTTP.
  - Faz o parse das respostas JSON usando `TmdbMovieJsonParser`.
  - Filtra e ordena os filmes com base em critérios específicos.
  - Limita o número de filmes a 250 para garantir desempenho.

### 2. `TmdbMovieJsonParser`

- **Propósito**: Faz o parse das respostas JSON da API do The Movie DB.
- **Principais Funcionalidades**:
  - Implementa a interface `JsonParser`.
  - Extrai atributos dos filmes como título, caminho do poster, nota e data de lançamento.
  - Lida com possíveis erros de parsing de forma graciosa.

### 3. `Movie`

- **Propósito**: Representa uma entidade de filme com atributos como título, URL da imagem, nota e ano de lançamento.
- **Principais Funcionalidades**:
  - Definido como um `record` para garantir imutabilidade.
  - Implementa a interface `Content` para consistência com outros tipos de conteúdo.
  - Fornece métodos de acesso para cada atributo.

### Justificativa para as Escolhas de Design

- **Imutabilidade**: A classe `Movie` é projetada como um `record` para garantir que os dados do filme permaneçam inalterados após a criação. Isso previne modificações acidentais e alinha-se com as melhores práticas para lidar com entidades de domínio.
- **Separação de Responsabilidades**: O projeto separa as responsabilidades de comunicação com a API (`TmdbApiClient`) e parsing de JSON (`TmdbMovieJsonParser`). Essa abordagem modular melhora a manutenibilidade e testabilidade.
- **Tratamento de Erros**: O parser inclui tratamento de erros para gerenciar possíveis problemas com a estrutura do JSON, garantindo robustez.
- **Desempenho**: O cliente limita o número de filmes a 250 e filtra duplicatas, otimizando o desempenho e a relevância dos dados.

Essa estrutura garante que o projeto seja fácil de entender, manter e estender, enquanto adere às práticas modernas de desenvolvimento em Java.

## Resultado

![Resultado](src/images/htmlGenerated.png)

## Licença

Este projeto está licenciado sob a Licença MIT. Veja o arquivo `LICENSE` para mais detalhes.