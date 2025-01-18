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

## Licença

Este projeto está licenciado sob a Licença MIT. Veja o arquivo `LICENSE` para mais detalhes.