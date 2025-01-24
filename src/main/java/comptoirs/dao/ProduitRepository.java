package comptoirs.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import comptoirs.entity.Produit;

// Cette interface sera auto-implémentée par Spring
public interface ProduitRepository extends JpaRepository<Produit, Integer> {
	/**
	 * Trouve les produits à partir du libellé de la categorie
	 * version JPQL
	 * @param libelleDeCategorie le libellé à chercher
	 * @return les produits de cette catégorie
	 */
	@Query("""
		SELECT p
		FROM Produit p
		WHERE p.categorie.libelle = :libelleDeCategorie""")
	List<Produit> produitsPourCategorieJPQL(String libelleDeCategorie);

	/**
	 * Trouve les produits à partir du libellé de la categorie
	 * version SQL
	 * @param libelleDeCategorie le libellé à chercher
	 * @return les produits de cette catégorie
	 */
	@Query(value = """
		SELECT *
		FROM Produit
		INNER JOIN Categorie ON Produit.categorie_code = Categorie.code
		WHERE Categorie.libelle = :libelleDeCategorie""",
		nativeQuery = true)
	List<Produit> produitsPourCategorieSQL(String libelleDeCategorie);

    /**
     * Calcule le nombre d'unités vendues pour chaque produit d'une catégorie donnée.
     * La requête est écrite en JPQL
     * @param codeCategorie la catégorie à traiter
     * @return le nombre d'unités commandées pour chaque produit,
     *		sous la forme d'une liste de projections UnitesCommandeesParProduit
     */
    // La requête JPQL est exprimée en termes du modèle conceptuel de données
    // Avec cette requête JPQL, JPA va générer une jointure entre Ligne et Produit
    @Query(
        // Chaîne de caractères multilignes
        """
         SELECT l.produit.nom as nomProduit, SUM(l.quantite) AS unitesCommandees
         FROM Ligne l
         WHERE l.produit.categorie.code = :codeCategorie
         GROUP BY nomProduit
        """)
    List<UnitesCommandeesParProduit> produitsVendusJPQL(Integer codeCategorie);

    /**
     * Calcule le nombre d'unités vendues pour chaque produit d'une catégorie donnée.
     * La requête est écrite en SQL natif
     * @param codeCategorie la catégorie à traiter
     * @return le nombre d'unités vendus pour chaque produit,
     *		sous la forme d'une liste de projections UnitesCommandeesParProduit
     */
    // La requête SQL native est exprimée en termes du modèle logique (relationnel) de données,
    //            on doit expliciter les jointures
    @Query( value = """
            SELECT nom as nomProduit, SUM(quantite) AS unitesCommandees FROM Produit
            INNER JOIN Ligne ON Produit_Reference = Reference
            WHERE Categorie_Code = :codeCategorie
            GROUP BY nomProduit
    """,
        nativeQuery = true )
    List<UnitesCommandeesParProduit> produitsVendusSQL(Integer codeCategorie);

	/**
	 * Calcule le nombre d'unités vendues pour chaque produit d'une catégorie donnée.
	 * pas d'utilisation de DTO
	 * @param codeCategorie la catégorie à traiter
	 * @return le nombre d'unités vendus pour chaque produit,
	 *	   sous la forme d'une liste de tableaux de valeurs non typées
	 */
	@Query("""
		SELECT p.nom, SUM(li.quantite)
		FROM Categorie c
		JOIN c.produits p
		JOIN p.lignes li
		WHERE c.code = :codeCategorie
		GROUP BY p.nom""")
	List<Object> produitsVendusPourV2(Integer codeCategorie);

	@Query("select p from Produit p")
    List<ProduitProjection> findAllWithProjection();

}
