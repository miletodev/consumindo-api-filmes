import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TmdbMovieJsonParser implements JsonParser {
    private final String json;

    public TmdbMovieJsonParser(String json) {
        this.json = json;
    }

    @Override
    public List<Movie> parse() {
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

            movies.add(new Movie(title, urlImage, rating, year));
        }
        return movies;
    }

    private static String[] parseJsonMovies(String json) {
        Matcher matcher = Pattern.compile("\"results\":\\[(.*)]", Pattern.DOTALL).matcher(json);

        if (!matcher.find()) {
            System.out.println("Error: Could not find 'results' field in JSON.");
            throw new IllegalArgumentException("Field 'results' not found in JSON.");
        }

        String moviesJsonArray = matcher.group(1).trim();
        return moviesJsonArray.split("(?<=}),\\s*(?=\\{)");
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