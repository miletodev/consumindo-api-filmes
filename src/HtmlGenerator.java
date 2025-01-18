import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HtmlGenerator {
    public static void generateHtml(List<Movie> movies, String filePath) {
        String head =
                """
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
                        integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
                </head>
                """; // HTML head

        String divTemplate =
                """
                <div class="col-md-4 d-flex align-items-stretch">
                    <div class="card text-white bg-dark mb-3" style="max-width: 18rem;">
                        <h4 class="card-header">%s</h4>
                        <div class="card-body">
                            <img class="card-img" src="https://image.tmdb.org/t/p/w500%s" alt="%s">
                            <p class="card-text mt-2">Nota: %s - Ano: %s</p>
                        </div>
                    </div>
                </div>
                """; // Movie card template

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