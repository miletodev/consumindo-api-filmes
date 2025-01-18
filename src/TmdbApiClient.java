import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TmdbApiClient é responsável por fazer requisições à API do The Movie Database (TMDb) e retornar uma lista de filmes.
 * A classe é instanciada com uma chave de API, que é necessária para fazer as requisições.
 *
 * @see <a href="https://www.themoviedb.org/documentation/api">Documentação da API do TMDb</a>
 */
public class TmdbApiClient {
    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();

    public TmdbApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Faz uma requisição à API do TMDb para buscar filmes.
     * A busca é feita por um termo específico, como "popular" ou "top_rated".
     * O número total de páginas a serem buscadas é especificado pelo parâmetro totalPages.
     * A lista de filmes é limitada a 250 filmes e ordenada por rating.
     * Filmes duplicados são filtrados, mantendo apenas o mais antigo.
     * @see <a href="https://developers.themoviedb.org/3/movies/get-movie-details">Detalhes do endpoint de filmes</a>
     *
     * @param search Termo de busca, como "popular" ou "top_rated"
     * @param totalPages Número total de páginas a serem buscadas
     * @return Lista de filmes
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public List<Movie> fetchMovies(String search, int totalPages) throws URISyntaxException, IOException, InterruptedException {
        List<Movie> allMovies = new ArrayList<>();
        // Faz uma requisição para cada página. O número total de páginas é especificado pelo parâmetro totalPages
        // A TMDB retorna um número máximo n de filmes por página,
        // então é necessário fazer várias requisições para obter todos os filmes para o Top 250 pedido no desafio.
        for (int page = 1; page <= totalPages; page++) {
            // Monta a URI da requisição
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.themoviedb.org/3/movie/" + search + "?api_key=" + apiKey + "&language=pt-BR&page=" + page))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

//            System.out.println("API Response: " + json); // Debug statement

            TmdbMovieJsonParser parser = new TmdbMovieJsonParser(json);
            List<Movie> movies = parser.parse();
//            System.out.println("Parsed Movies: " + movies); // Debug statement

            allMovies.addAll(movies); // Adiciona os filmes da página atual à lista de filmes

            // Filtra filmes duplicados e mantém apenas o mais antigo
            allMovies = new ArrayList<>(allMovies.stream()
                    .filter(movie -> movie.title() != null && !movie.title().isEmpty())
                    .collect(Collectors.toMap(
                            Movie::title,
                            Function.identity(),
                            (movie1, movie2) -> movie1.year() < movie2.year() ? movie1 : movie2
                    ))
                    .values()); // Filtra filmes duplicados

            if (allMovies.size() >= 250) {
                break;
            }
        }

        List<Movie> sortedMovies = allMovies.stream()
                .sorted(Comparator.comparingDouble(Movie::rating).reversed())
                .limit(250)
                .collect(Collectors.toList());

//        System.out.println("Sorted Movies: " + sortedMovies); // Debug statement

        return sortedMovies;
    }
}