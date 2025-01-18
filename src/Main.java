import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * O método main é responsável por fazer a requisição a API do The Movie DB
 * e imprimir o resultado no console.
 * Ele carrega a chave da API de um arquivo .env para proteção da chave individual e faz a requisição a API
 * com a chave e a busca desejada.
 *
 * @throws java.net.URISyntaxException Exceção lançada quando a URI passada para o HttpRequest não é válida
 * @throws java.io.IOException Exceção lançada quando ocorre um erro de I/O
 * @throws java.lang.InterruptedException Exceção lançada quando a thread é interrompida
 */
public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Map<String, String> env = loadEnv(); // Carrega as variáveis de ambiente
        String key = env.get("API_KEY");

        if (key == null) { // Verifica se a chave da API foi encontrada no arquivo .env.
            // Caso não tenha uma chave válida, o programa é encerrado.
            System.out.println("API_KEY não encontrada");
            return;
        }

        TmdbApiClient apiClient = new TmdbApiClient(key); // Cria uma instância da classe ImdbApiClient
        List<Movie> allMovies = apiClient.fetchMovies("popular", 496); // Busca os filmes populares

        allMovies = allMovies.stream()
                .sorted(Comparator.comparingDouble(Movie::rating).reversed()) // Ordena os filmes por avaliação
                .limit(250)
                .collect(Collectors.toList()); // Limita a lista a 250 filmes

        HtmlGenerator.generateHtml(allMovies, "filmes.html"); // Gera um arquivo HTML com a lista de filmes
        System.out.println("Arquivo gerado com sucesso! Top: " + allMovies.size() + " filmes."); // Mensagem de sucesso
    }

    private static Map<String, String> loadEnv() {
        Map<String, String> env = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    env.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger(Main.class.getName()); // Cria um logger
            logger.log(Level.SEVERE, "Erro ao carregar variáveis de ambiente", e); // Registro de erro
        }
        return env;
    }
}


