/**
 * Classe que representa um filme. Ela é usada para armazenar os atributos de um filme.
 * A classe é imutável e possui um método toString que retorna uma representação textual do filme.
 * A classe implementa a interface Content.
 */

public record Movie(String title, String urlImage, double rating, int year) implements Content {
    // Atributos da classe
    @Override
    public String title() {
        return title;
    }

    @Override
    public String urlImage() {
        return urlImage;
    }

    @Override
    public double rating() {
        return rating;
    }

    @Override
    public int year() {
        return year;
    }
}