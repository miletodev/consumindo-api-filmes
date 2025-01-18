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

public class TmdbApiClient {
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

//            System.out.println("API Response: " + json); // Debug statement

            TmdbMovieJsonParser parser = new TmdbMovieJsonParser(json);
            List<Movie> movies = parser.parse();
//            System.out.println("Parsed Movies: " + movies); // Debug statement

            allMovies.addAll(movies);

            // Filtra filmes duplicados e mantém apenas o mais antigo
            allMovies = new ArrayList<>(allMovies.stream()
                    .filter(movie -> movie.title() != null && !movie.title().isEmpty())
                    .collect(Collectors.toMap(
                            Movie::title,
                            Function.identity(),
                            (movie1, movie2) -> movie1.year() < movie2.year() ? movie1 : movie2
                    ))
                    .values());

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