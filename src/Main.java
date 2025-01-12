import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Carrega variáveis de ambiente de um arquivo .env e as utiliza no programa
 * Programa feito para fins de estudo do #7DaysOfCode da Alura para desenvolver uma aplicação que consome uma API de filmes.
 * A API utilizada aqui é a do TMDB (The Movie Database) e é necessário um token de leitura da API para utilizá-la.
 */

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, String> env = loadEnv(".env"); // Carrega variáveis de ambiente de um arquivo .env
        String apiKey = env.get("API_KEY");
        String apiToken = env.get("API_TOKEN");
        URI uriTMDB = URI.create("https://api.themoviedb.org/3/movie/550?api_key=" + apiKey); // URI da API do TMDB

        if (apiKey == null || apiToken == null) {
            System.out.println("API_KEY or API_TOKEN is not set"); // Verifica se as variáveis de ambiente
            // foram carregadas corretamente e se não, exibe uma mensagem de erro
            return;
        }

        //Cria uma nova instância de HttpClient e faz uma requisição GET para a URI da API do TMDB
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriTMDB)
                .build();

        //Recebe a resposta da requisição e exibe o JSON retornado
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        System.out.println("Resposta: " + json);

    }

    /**
     * Carrega variáveis de ambiente de um arquivo .env
     *
     * @param filePath Caminho do arquivo .env
     * @return Map com as variáveis de ambiente
     */

    private static Map<String, String> loadEnv(String filePath) {
        Map<String, String> env = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    env.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return env;
    }
}