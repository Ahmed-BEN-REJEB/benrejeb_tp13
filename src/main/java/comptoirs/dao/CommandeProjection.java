package comptoirs.dao;

import java.math.BigDecimal;

public interface CommandeProjection {
    Integer getNumeroCommande();
    BigDecimal  getPort();
    BigDecimal getMontantArticles();
}
