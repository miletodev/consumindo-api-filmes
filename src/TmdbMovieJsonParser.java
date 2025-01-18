import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parseia um JSON de filmes do The Movie Database (TMDb).
 * A classe implementa a interface JsonParser.
 * Ela possui um construtor que recebe uma String com o JSON e um método parse que retorna uma lista de filmes.
 * O método parse utiliza expressões regulares para extrair os atributos dos filmes do JSON.
 * A classe é usada para converter um JSON de filmes do TMDb em uma lista de objetos Movie.
 * A classe é usada no método fetchMovies da classe TmdbApiClient.
 * @see TmdbApiClient
 */
public class TmdbMovieJsonParser implements JsonParser {
    private final String json;

    public TmdbMovieJsonParser(String json) {
        this.json = json;
    }

    /**
     * Parseia o JSON de filmes do TMDb e retorna uma lista de objetos Movie.
     * O método utiliza expressões regulares para extrair os atributos dos filmes do JSON.
     * @see Movie
     * @return Uma lista de objetos Movie.
     */
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
        // Cria uma lista de objetos Movie com os atributos extraídos do JSON
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < moviesArray.length; i++) {
            // Se não houver um atributo, adiciona uma String vazia
            //Permite que a lista de filmes seja preenchida mesmo que um dos atributos esteja faltando
            //Percorre o array de filmes e cria um objeto Movie com os atributos extraídos
            String title = i < titles.size() ? titles.get(i) : "";
            String urlImage = i < urlImages.size() ? urlImages.get(i) : "";
            double rating = i < ratings.size() ? ratings.get(i) : 0.0;
            int year = i < years.size() ? years.get(i) : 0;

            movies.add(new Movie(title, urlImage, rating, year));
        }
        return movies;
    }

    /**
     * Parseia o JSON de filmes do TMDb e retorna um array de Strings com os filmes.
     * O método utiliza expressões regulares para extrair os filmes do JSON.
     * @see Movie
     * @param json JSON de filmes do TMDb.
     * @return Um array de Strings com os filmes.
     */
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