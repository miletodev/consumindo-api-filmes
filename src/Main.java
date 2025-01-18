import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public static void main() throws URISyntaxException, IOException, InterruptedException {
    Map<String, String> env = loadEnv(); //Carrega as variáveis de ambiente
    String key = env.get("API_KEY");

    if (key == null) { //Verifica se a chave da API foi encontrada no arquivo .env.
        // Caso não tenha uma chave válida, o programa é encerrado.
        System.out.println("API_KEY não encontrada");
        return;
    }

    String search = "top_rated";
    int pagina = 1;

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("https://api.themoviedb.org/3/movie/" + search + "?api_key=" + key + "&language=pt-BR&page=" + pagina))
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    String json = response.body(); // JSON retornado pela API
    String[] moviesArray = parseJsonMovies(json); // Divide o JSON em objetos JSON individuais

    List<String> titles = parseTitles(moviesArray);
    titles.forEach(System.out::println);
    List<String> urlImages = parseUrlImages(moviesArray);
    urlImages.forEach(System.out::println);
    List<Integer> years = parseYears(moviesArray);
    years.forEach(System.out::println);
    List<Double> ratings = parseRatings(moviesArray);
    ratings.forEach(System.out::println);
}

/**
 * Método que faz o parse do JSON retornado pela API do The Movie DB.
 * Ele captura o campo "results" do JSON e divide os objetos JSON dentro do array.
 * @param json JSON retornado pela API
 * @return Array de strings com os objetos JSON
 */
private static String[] parseJsonMovies(String json) {
    // Atualiza regex para capturar "results" corretamente, permitindo múltiplas linhas
    Matcher matcher = Pattern.compile("\"results\":\\[(.*)]", Pattern.DOTALL).matcher(json);

    if (!matcher.find()) { // Verifica se o campo "results" foi encontrado no JSON retornado
        System.out.println("Erro: Não foi possível encontrar o campo 'results' no JSON.");
        throw new IllegalArgumentException("Campo 'results' não encontrado no JSON.");
    }

    String moviesJsonArray = matcher.group(1).trim(); // Captura o campo "results" do JSON
    // Divide objetos JSON dentro do array, usando delimitadores precisos
    return moviesJsonArray.split("(?<=}),\\s*(?=\\{)"); // Divide o array de filmes
}

/**
 * Analisa o array de filmes e retorna uma lista com os títulos dos filmes.
 *
 * @param moviesArray Array de filmes
 * @return Lista com os títulos dos filmes
 */
private static List<String> parseTitles(String[] moviesArray) {
    return parseAttribute(moviesArray, "title");
}

/**
 * Analisa o array de filmes e retorna uma lista com as URLs das imagens dos filmes.
 *
 * @param moviesArray Array de filmes
 * @return Lista com as URLs das imagens dos filmes
 */
private static List<String> parseUrlImages(String[] moviesArray) {
    return parseAttribute(moviesArray, "poster_path");
}

/**
 * Analisa o array de filmes e retorna uma lista com os anos de lançamento dos filmes.
 *
 * @param moviesArray Array de filmes
 * @return Lista com os anos de lançamento dos filmes
 */
private static List<Integer> parseYears(String[] moviesArray) {
    return parseAttribute(moviesArray, "release_date").stream()
            .filter(date -> !date.isEmpty()) // Filtra datas vazias
            .map(date -> Integer.parseInt(date.split("-")[0]))
            .collect(Collectors.toList());
}

/**
 * Analisa o array de filmes e retorna uma lista com as avaliações dos filmes.
 *
 * @param moviesArray Array de filmes
 * @return Lista com as avaliações dos filmes
 */
private static List<Double> parseRatings(String[] moviesArray) {
    return parseAttribute(moviesArray, "vote_average", false).stream()
            .filter(value -> !value.isEmpty()) // Filtra valores vazios
            .map(Double::parseDouble)
            .map(value -> BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue())
            .collect(Collectors.toList());
}

private static List<String> parseAttribute(String[] moviesArray, String attribute) {
    return parseAttribute(moviesArray, attribute, true); // Chama o método sobrecarregado
}

/**
 * Analisa o array de filmes e extrai um atributo específico.
 *
 * @param moviesArray O array de filmes em formato JSON
 * @param attribute O nome do atributo a ser extraído
 * @return Uma lista de valores do atributo extraído
 */
private static List<String> parseAttribute(String[] moviesArray, String attribute, boolean isString) {
    List<String> attributes = new ArrayList<>();
    String regex = isString ? "\"" + attribute + "\":\"(.*?)\"" : "\"" + attribute + "\":(\\d+\\.\\d+|\\d+)";
    Pattern pattern = Pattern.compile(regex);

    for (String movieJson : moviesArray) {
        Matcher matcher = pattern.matcher(movieJson); // Procura o atributo no JSON
        if (matcher.find()) {
            attributes.add(matcher.group(1));
        } else {
            attributes.add(""); // Adiciona string vazia caso o atributo não seja encontrado
        }
    }
    return attributes;
}

/**
 * Carrega as variáveis de ambiente de um arquivo .env
 *
 * @return Map com as variáveis de ambiente
 */

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
        Logger logger = Logger.getLogger(com.sun.tools.javac.Main.class.getName()); // Cria um logger
        logger.log(Level.SEVERE, "Erro ao carregar variáveis de ambiente", e); // Registra o erro
    }
    return env;
}