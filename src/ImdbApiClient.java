import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ImdbApiClient {
    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();

    public ImdbApiClient(String apiKey) {
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
                            (movie1, movie2) -> movie1.year() < movie2.year() ? movie1 : movie2
                    ))
                    .values());

            if (allMovies.size() >= 250) {
                break;
            }
        }
        return allMovies.stream()
                .sorted(Comparator.comparingDouble(Movie::rating).reversed())
                .limit(250)
                .collect(Collectors.toList());
    }

    private static String[] parseJsonMovies(String json) {
        Matcher matcher = Pattern.compile("\"results\":\\[(.*)]", Pattern.DOTALL).matcher(json);

        if (!matcher.find()) {
            System.out.println("Erro: Não foi possível encontrar o campo 'results' no JSON.");
            throw new IllegalArgumentException("Campo 'results' não encontrado no JSON.");
        }

        String moviesJsonArray = matcher.group(1).trim();
        return moviesJsonArray.split("(?<=}),\\s*(?=\\{)");
    }

    private static List<Movie> parseMovies(String json) {
        String[] moviesArray = parseJsonMovies(json);

        List<String> titles = parseAttribute(moviesArray, "title");
        List<String> urlImages = parseAttribute(moviesArray, "poster_path");
        List<Double> ratings = parseAttribute(moviesArray, "vote_average", false).stream()
                .map(Double::parseDouble)
                .map(value -> BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue())
                .toList();
        List<Integer> years = parseAttribute(moviesArray, "release_date").stream()
                .filter(date -> !date.isEmpty())
                .map(date -> Integer.parseInt(date.split("-")[0]))
                .toList();

        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < moviesArray.length; i++) {
            String title = i < titles.size() ? titles.get(i) : "";
            String urlImage = i < urlImages.size() ? urlImages.get(i) : "";
            double rating = i < ratings.size() ? ratings.get(i) : 0.0;
            int year = i < years.size() ? years.get(i) : 0;

            movies.add(new Movie(title, urlImage, year, rating));
        }
        return movies;
    }

    private static List<String> parseAttribute(String[] moviesArray, String attribute) {
        return parseAttribute(moviesArray, attribute, true);
    }

    private static List<String> parseAttribute(String[] moviesArray, String attribute, boolean isString) {
        List<String> attributes = new ArrayList<>();
        String regex = isString ? "\"" + attribute + "\":\"(.*?)\"" : "\"" + attribute + "\":(\\d+\\.\\d+|\\d+)";
        Pattern pattern = Pattern.compile(regex);

        for (String movieJson : moviesArray) {
            Matcher matcher = pattern.matcher(movieJson);
            attributes.add(matcher.find() ? matcher.group(1) : "");
        }
        return attributes;
    }
}
