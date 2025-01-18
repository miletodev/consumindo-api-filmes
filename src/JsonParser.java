import java.util.List;

/**
 * Interface para classes que fazem parse de JSON.
 */
public interface JsonParser {
    /**
     * Faz o parse do JSON e retorna uma lista de objetos que implementam a interface Content.
     * O tipo de objeto retornado depende da implementação da classe que implementa esta interface.
     * @return Lista de objetos que implementam a interface Content.
     */
    public List<? extends Content> parse();
}
