import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * O método main é responsável por fazer a requisição a API do The Movie DB
 * e imprimir o resultado no console.
 * Ele carrega a chave da API de um arquivo .env para proteção da chave individual e faz a requisição a API
 * com a chave e a busca desejada.
 *
 * @throws URISyntaxException Exceção lançada quando a URI passada para o HttpRequest não é válida
 * @throws IOException Exceção lançada quando ocorre um erro de I/O
 * @throws InterruptedException Exceção lançada quando a thread é interrompida
 */

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Map<String, String> env = loadEnv(".env");
        String key = env.get("API_KEY");

        if (key == null) {
            System.out.println("API_KEY não encontrada");
            return;
        }


        String search = "top_rated";
        int pagina = 1;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.themoviedb.org/3/movie/" + search + "?api_key=" + key + "&language=pt-BR&page=" + pagina)) //https://api.themoviedb.org/3/movie/top_rated?api_key=8e479da2b3a497455374bff051aab732&language=pt-BR&page=1
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();

        System.out.println(json);
    }

    /**
     * Carrega as variáveis de ambiente de um arquivo .env
     *
     * @param filePath Caminho do arquivo .env
     * @return Map com as variáveis de ambiente
     */
    private static Map<String, String> loadEnv(String filePath) {
        Map<String, String> env = new HashMap<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bf.readLine()) != null) {
                String[] parts = line.split("=");
                env.put(parts[0], parts[1]);
            }
            return env;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}