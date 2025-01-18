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

public class TmdbApiClient implements JsonParser {
    @Override
    public List<? extends Content> parse() {
        return List.of();
    }

    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();

    public TmdbApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<Movie> fetchMovies(String search, int totalPages) throws URISyntaxException, IOException, InterruptedException {
        List<Movie> allMovies = new ArrayList<>();
        for (int page = 1; page <= totalPages; page++) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.themoviedb.org/3/movie/" + search + "?api_key=" + apiKey + "&language=pt-BR&page=" + page))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            List<Movie> movies = parseMovies(json);
            allMovies.addAll(movies);

            // Filtra filmes duplicados e mantém apenas o mais antigo
            allMovies = new ArrayList<>(allMovies.stream()
                    .filter(movie -> movie.title() != null && !movie.title().isEmpty())
                    .collect(Collectors.toMap(
                            Movie::title,
                            Function.identity(),
                            (movie1, movie2) -> Integer.parseInt(movie1.year()) < Integer.parseInt(movie2.year()) ? movie1 : movie2
                    ))
                    .values());

            if (allMovies.size() >= 250) {
                break;
            }
        }
        return allMovies.stream()
                .sorted(Comparator.comparingDouble(movie -> movie.rating()).reversed())
                .limit(250)
                .collect(Collectors.toList());
    }

    private List<Movie> parseMovies(String json) {
        // Implementation of parseMovies method
        // This should parse the JSON and return a list of Movie objects
        return new ArrayList<>();
    }
}