/**
 * Interface Content representa uma classe genérica que contém informações sobre um conteúdo, como título, imagem, avaliação e ano.
 * Pode ser utilizada caso seja necessário implementar uma nova classe de conteúdo, como por exemplo, uma série.
 *
 * @version 1.0
 */

public interface Content {
        // Retorna o título do conteúdo
        String title();
        // Retorna a URL da imagem do conteúdo que será usada no HTML
        String urlImage();
        // Retorna a avaliação do conteúdo para ser ordenado do maior pelo menor
        double rating();
        // Retorna o ano de lançamento do conteúdo
        int year();
}
