/**
 * Classe que representa um filme. Ela é usada para armazenar os atributos de um filme.
 * A classe é imutável e possui um método toString que retorna uma representação textual do filme.
 * @param title Título do filme
 * @param urlImage URL da imagem do filme
 * @param year Ano de lançamento do filme
 * @param rating Avaliação do filme
 */

public record Movie(String title, String urlImage, int year, double rating) {
    @Override
    public String toString() { // Retorna uma representação textual do filme
        return title + " (" + year + ") - " + rating + "/10"; // Formato: "Título (Ano) - Avaliação/10"
    }
}
