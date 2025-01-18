import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A classe HtmlGenerator é responsável por gerar um arquivo HTML com a lista de filmes.
 * Ela possui um método estático generateHtml que recebe uma lista de filmes e um caminho para o arquivo HTML.
 * O método gera um arquivo HTML com os filmes passados e os salva no caminho especificado.
 * Caso ocorra um erro ao gerar o arquivo, é registrado um log de erro.
 * @see Movie
 */
public class HtmlGenerator {
    public static void generateHtml(List<Movie> movies, String filePath) {
        // HTML head
        String head =
                """
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
                        integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
                    <style>
                        body {
                            background-color: #000000;
                        }
                    </style>
                </head>
                """;

        /**
         * Template para a div que contém as informações de um filme.
         * O template contém placeholders para o título, a URL da imagem, a nota, e o ano do filme.
         * Os valores dos placeholders são substituídos pelos valores dos atributos do filme.
         *
         * Disponibilizado pelo Paulo Silveira (https://www.linkedin.com/in/paulosilveira/)
         */

        String divTemplate =
                """
                <div class="col-md-4 col-sm-6 col-12 d-flex align-items-stretch">
                    <div class="card text-white bg-dark mb-3" style="max-width: 18rem;">
                        <h4 class="card-header">%s</h4>
                        <div class="card-body">
                            <img class="card-img" src="https://image.tmdb.org/t/p/w500%s" alt="%s">
                            <p class="card-text mt-2">Nota: %s - Ano: %s</p>
                        </div>
                    </div>
                </div>
                """; // Movie card template

        // Escreve o conteúdo no arquivo HTML

        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println(head);
            writer.println("<body>");
            writer.println("<div class=\"container\">");
            writer.println("<div class=\"row justify-content-center\">");

            for (Movie movie : movies) {
                writer.printf(divTemplate, movie.title(), movie.urlImage(), movie.title(), movie.rating(), movie.year());
            }

            writer.println("</div>");
            writer.println("</div>");
            writer.println("</body>");
            writer.println("</html>");
        } catch (IOException e) {
            Logger logger = Logger.getLogger(HtmlGenerator.class.getName());
            logger.log(Level.SEVERE, "Erro ao gerar HTML", e);
        }
    }
}