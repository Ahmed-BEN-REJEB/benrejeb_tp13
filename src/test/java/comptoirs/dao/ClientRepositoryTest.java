package comptoirs.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import comptoirs.entity.Client;
import lombok.extern.log4j.Log4j2;

@Log4j2 // Génère le 'logger' pour afficher les messages de trace
@DataJpaTest
class ClientRepositoryTest {

	@Autowired
	private CommandeRepository daoCommande;

	@Autowired
	private ClientRepository daoClient;

	@Autowired
	private LigneRepository daoLigne;

	@Test
	@Sql("small_data.sql")
	void onPeutTrouverUnClientEtSesCommandes() {
		log.info("Recherche d'un client");
		// On cherche le client 2COM d'après sa clé
		Client c  = daoClient.findById("2COM").orElseThrow();
		// On vérifie qu'il a des commandes
		assertFalse(c.getCommandes().isEmpty(),
			"Le client a des commandes");
	}

	@Test
	@Sql("small_data.sql")
    @SuppressWarnings("null") // Bug Eclipse ?
	void supprimerUnClientSupprimeAussiSesCommandes() {
		log.info("On supprime un client");
		// On vérifie qu'au début, on a deux commandes
		assertEquals(2, daoCommande.count(), "On doit trouver deux commandes");
		// On cherche le client 2COM d'après sa clé
		Client c  = daoClient.findById("2COM").orElseThrow();
		// On supprime le client
		daoClient.delete(c);
		// On vérifie qu'il ne reste aucune commande
		assertEquals(0, daoCommande.count(), "Il ne doit rester aucune commandes");
		// On vérifie qu'il ne reste aucune ligne
		assertEquals(0, daoLigne.count(), "Il ne doit rester aucune ligne");
	}
}
