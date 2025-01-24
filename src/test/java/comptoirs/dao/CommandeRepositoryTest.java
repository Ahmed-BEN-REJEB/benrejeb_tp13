package comptoirs.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import comptoirs.entity.Client;
import comptoirs.entity.Commande;
import comptoirs.entity.Ligne;
import comptoirs.entity.Produit;
import lombok.extern.log4j.Log4j2;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@Log4j2 // Génère le 'logger' pour afficher les messages de trace
@DataJpaTest
class CommandeRepositoryTest {

	@Autowired
	private CommandeRepository commandeDao;

	@Autowired
	private ClientRepository clientDao;

	@Autowired
	private ProduitRepository produitDao;

	@Autowired
	private LigneRepository ligneDao;

    private Commande commandeAvecProduits;

    private Commande commandeSansProduits;

    @BeforeEach
    void setUp() {
        var client = clientDao.findById("0COM").orElseThrow();
        var p1 = produitDao.findById(93).orElseThrow();
        var p2 = produitDao.findById(94).orElseThrow();
        // Une commande sans produits
        commandeSansProduits = new Commande();
        commandeSansProduits.setClient(client);
        commandeSansProduits.setPort(new BigDecimal("10.00"));
        commandeSansProduits.setSaisiele(LocalDate.now());
        commandeSansProduits.setRemise(new BigDecimal("0.10")); // 10% remise
        commandeDao.save(commandeSansProduits);
        // Une commande avec des produits
        commandeAvecProduits = new Commande();
        commandeAvecProduits.setClient(client);
        commandeAvecProduits.setPort(new BigDecimal("20.00"));
        commandeAvecProduits.setSaisiele(LocalDate.now());
        commandeAvecProduits.setRemise(new BigDecimal("0.20")); // 20% remise
        // Deux lignes dans la commande
        commandeAvecProduits.getLignes().add(new Ligne(commandeAvecProduits, p1, 10));
        commandeAvecProduits.getLignes().add(new Ligne(commandeAvecProduits, p2, 20));
        commandeDao.save(commandeAvecProduits);
    }

    @Test
    void testCommandeAvecProduits() {
        var commande = commandeDao.findById(commandeAvecProduits.getNumero()).orElseThrow();
        assertEquals(commandeAvecProduits, commande);
        assertEquals(2, commande.getLignes().size());
    }


    @Test
    void montantArticles_InvalidCommandeNumber_ReturnsNull() {

        var montantTotal = commandeDao.montantArticles(-1);

        assertNull(montantTotal);
    }

    @Test
    void montantArticles_NoLinesInCommande_ReturnsNull() {
        var montantTotal = commandeDao.montantArticles(commandeSansProduits.getNumero());
        // Assert
        assertNull(montantTotal);
    }

    @Test
    void montantArticles_ValidCommandeNumber_ReturnsCorrectAmount() {
        var produit1 = produitDao.findById(93).orElseThrow();
        var produit2 = produitDao.findById(94).orElseThrow();

        var montantTotal = commandeDao.montantArticles(commandeAvecProduits.getNumero());
        var expected = produit1.getPrixUnitaire().multiply(new BigDecimal("10"))
            .add(produit2.getPrixUnitaire().multiply(new BigDecimal("20")))
            .multiply(new BigDecimal("0.80")); // Remise
        // Assert
        assertEquals(expected, montantTotal);
    }

    @Test
	@Sql("small_data.sql")
	void onPeutCreerUneCommandeEtSesLignes() {
		log.info("Création d'une commande avec ses lignes");
        long nombreDeCommandes = commandeDao.count();
		// On cherche les infos nécessaires dans le jeu d'essai
		Produit p1 = produitDao.findById(98).orElseThrow();
		Produit p2 = produitDao.findById(99).orElseThrow();
		Client c1  = clientDao.findById("0COM").orElseThrow();

		// On crée une commande
		Commande nouvelle = new Commande(c1);
		nouvelle.setRemise(BigDecimal.ZERO);

		// On crée deux lignes pour la nouvelle commande
		Ligne l1 = new Ligne(nouvelle, p1, 4);

		Ligne l2 = new Ligne(nouvelle, p2, 99);
        // On ajoute les deux lignes à la commande
        nouvelle.getLignes().add(l1);
        nouvelle.getLignes().add(l2);


		// On enregistre la commande (provoque l'enregistrement des lignes)
		commandeDao.save(nouvelle);

		// On regarde si ça s'est bien passé
        assertEquals(nombreDeCommandes + 1, commandeDao.count(), "Il doit y avoir une commande de plus");
		assertEquals(3, p1.getLignes().size(), "Il doit y avoir 3 lignes pour le produit p1");
		assertEquals(2, p2.getLignes().size(), "Il doit y avoir 2 lignes pour le produit p2");
		assertTrue(p2.getLignes().contains(l2), "La nouvelle ligne doit avoir été ajoutée au produit p2");
		assertTrue(p1.getLignes().contains(l1), "La nouvelle ligne doit avoir été ajoutée au produit p1");
	}

	@Test
	@Sql("small_data.sql")
	void pasDeuxFoisLeMemeProduitDansUneCommande() {
		log.info("Tentative de création d'une commande avec doublon");
		// On cherche les infos nécessaires dans le jeu d'essai
		Produit p1 = produitDao.findById(99).orElseThrow();
		Client c1  = clientDao.findById("0COM").orElseThrow();

		// On crée une commande
		Commande nouvelle = new Commande(c1);


		// On crée deux lignes pour la nouvelle commande avec le même produit
		Ligne l1 = new Ligne(nouvelle, p1, 4);
		Ligne l2 = new Ligne(nouvelle, p1, 10);

		// On ajoute les deux lignes à la commande
        nouvelle.getLignes().add(l1);
        nouvelle.getLignes().add(l2);

		try { // La création de la commande doit produire une erreur
			commandeDao.save(nouvelle);
			fail("La commande ne doit pas être sauvegardée");
		} catch (DataIntegrityViolationException e) {
			log.info("La création a échoué : {}", e.getMessage());
		}
	}

	@Test
	@Sql("small_data.sql")
	// La liste des lignes d'une commandes est annotée par "orphanRemoval=true"
	void onPeutSupprimerDesLignesDansUneCommande() {
		long nombreDeLignes = ligneDao.count(); // Combien de lignes en tout ?
		log.info("Supression de lignes dans une commande");
		Commande c = commandeDao.findById(99999).orElseThrow(); // Cette commande a 2 lignes
		c.getLignes().remove(1); // On supprime la dernière ligne
		commandeDao.save(c); // On l'enregistre (provoque la suppression de la ligne)
		assertEquals(nombreDeLignes - 1, ligneDao.count(), "On doit avoir supprimé une ligne");
	}

	@Test
	@Sql("small_data.sql")
	void onPeutModifierDesLignesDansUneCommande() {
		log.info("Modification des lignes d'une commande");
        long nombreDeLignes = ligneDao.count(); // Combien de lignes en tout ?
		Commande c = commandeDao.findById(99999).orElseThrow(); // Cette commande a 2 lignes
		Ligne l = c.getLignes().get(1); // On prend la deuxième
		l.setQuantite(99); // On la modifie
		commandeDao.save(c); // On enregistre la commande (provoque la modification de la ligne)

		assertEquals(nombreDeLignes, ligneDao.count(), "Le nombre de lignes n'a pas changé");
	}

}
