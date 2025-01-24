package comptoirs.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import comptoirs.entity.Produit;
import lombok.extern.log4j.Log4j2;

@Log4j2 // Génère le 'logger' pour afficher les messages de trace
@DataJpaTest
class ProduitRepositoryTest {
	@Autowired
	private ProduitRepository daoProduit;

	@Test
	@Sql("small_data.sql")
	void calculCorrectDesStatistiques()  {
		log.info("Calcul des statistiques");
		int categorieAvecProduit = 98;
		int categorieSansProduit = 99;
		var results  = daoProduit.produitsVendusJPQL(categorieSansProduit);
		assertEquals(0, results.size(),
			"La catégorie 99 n'a pas de produit dans le jeu de test");

		results = daoProduit.produitsVendusJPQL(categorieAvecProduit);
		assertEquals(2, results.size(),
			"La catégorie 98 a deux produits différents vendus dans le jeu de test");
		assertEquals(70, results.get(1).getUnitesCommandees(), "On a vendu 70 unités du produit dans le jeu de test");
	}

	@Test
	@Sql("small_data.sql")
	void jpqlEtSqlDonnentLeMemeResultat()  {
		String libelleDeCategorie ="2prods";
		log.info("Comparaison de requêtes JPQL etSQL");
		log.info("Requête avec JPQL");
		List<Produit> resulatJPQL = daoProduit.produitsPourCategorieJPQL(libelleDeCategorie);
		log.info("Requête avec SQL");
		List<Produit> resultatSQL = daoProduit.produitsPourCategorieSQL(libelleDeCategorie);
		assertEquals(resultatSQL, resulatJPQL, "On doit avoir le même résultat");
		log.info("Résultats de la requête : {}", resulatJPQL);
	}

	@Test
	@Sql("small_data.sql")
	void testeProjection() {
		List<ProduitProjection> result = daoProduit.findAllWithProjection();
		result.forEach( p -> log.info("Produit {} de catégorie {}", p.getNom(), p.getCategorie().getLibelle()));
		log.info("Résultats de la requête : {}", result);
	}
}
