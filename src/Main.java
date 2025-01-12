import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        String[] moviesArray = parseJsonMovies(json); // Extrai os filmes do JSON retornado

        List<String> titles = parseTitles(moviesArray); // Extrai os títulos dos filmes
        titles.forEach(System.out::println);

        List<String> urlImages = parseUrlImages(moviesArray); // Extrai as URLs das imagens dos filmes
        urlImages.forEach(System.out::println);

        List<Integer> years = parseYears(moviesArray); // Extrai os anos de lançamento dos filmes
        years.forEach(System.out::println);

        List<String> ratings = parseRating(moviesArray); // Extrai as avaliações dos filmes
        ratings.forEach(System.out::println);

    }

    /**
     * Extrai os filmes do JSON retornado pela API
     *
     * @param json JSON retornado pela API
     * @return Array de Strings com os filmes
     */
    private static String[] parseJsonMovies(String json) {
        Matcher matcher = Pattern.compile("\".*\\\\[(.*)\\\\].*\"").matcher(json);
        if (!matcher.matches()) {
            System.out.println("No movies found");
            return new String[0]; // Retorna um array vazio se não encontrar filmes
        } else {
            String[] moviesArray = matcher.group(1).split("\\},\\{");
            moviesArray[0] = moviesArray[0].substring(1);
            int last = moviesArray.length - 1;
            String lastString = moviesArray[last];
            moviesArray[last] = lastString.substring(0, lastString.length() - 1);
            return moviesArray; // Retorna um array com os filmes encontrados
        }
    }
        private static List<String> parseTitles(String[] moviesArray) {
            return parseAttribute(moviesArray, 3); // Extrai os títulos dos filmes
        }

        private static List<String> parseUrlImages(String[] moviesArray) {
            return parseAttribute(moviesArray, 5); // Extrai as URLs das imagens dos filmes
        }

        private static List<Integer> parseYears(String[] moviesArray) {
            return parseAttribute(moviesArray, 6).stream()  // Extrai os anos de lançamento dos filmes
                    .map(Integer::parseInt) // Converte os anos de String para Integer
                    .collect(Collectors.toList()); // Retorna uma lista com os anos de lançamento dos filmes
        }

        private static List<String> parseRating(String[] moviesArray) {
            return parseAttribute(moviesArray, 7); // Extrai as avaliações dos filmes
        }


        private static List<String> parseAttribute(String[] moviesArray, int index) {
            return Stream.of(moviesArray) // Extrai um atributo específico dos filmes
                    .map(movie -> movie.split(",")) // Separa os atributos dos filmes
                    .map(movie -> movie[index].split(":")[1]) // Separa o atributo específico dos filmes
                    .map(attribute -> attribute.substring(1, attribute.length() - 1)) // Remove as aspas do atributo
                    .collect(Collectors.toList()); // Retorna uma lista com o atributo específico dos filmes
        }

    /**
     * Carrega variáveis de ambiente de um arquivo .env
     *
     * @param filePath Caminho do arquivo .env
     * @return Map com as variáveis de ambiente
     */

    private static Map<String, String> loadEnv(String filePath) {
        Map<String, String> env = new HashMap<>(); // Carrega variáveis de ambiente de um arquivo .env
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