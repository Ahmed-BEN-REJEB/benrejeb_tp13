package comptoirs.dao;

/**
 * Une "projection" de l'entité Produit.
 * On conserve uniquement le nom du produit
 * et le libellé de la catégorie associée
 */
public interface ProduitProjection {
    String getNom();
    interface CategorieProjection {
        String getLibelle();
    }
    CategorieProjection getCategorie();
}
