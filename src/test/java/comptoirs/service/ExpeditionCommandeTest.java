package comptoirs.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import comptoirs.dao.CommandeRepository;




@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class ExpeditionCommandeTest {
    static final int NUMERO_COMMANDE_DEJA_LIVREE = 99999;
    static final int NUMERO_COMMANDE_PAS_LIVREE  = 99998;
    static final int REFERENCE_PRODUIT_DISPONIBLE_1 = 93;
    static final int REFERENCE_PRODUIT_DISPONIBLE_2 = 94;
    static final int REFERENCE_PRODUIT_DISPONIBLE_3 = 95;
    static final int REFERENCE_PRODUIT_DISPONIBLE_4 = 96;
    static final int REFERENCE_PRODUIT_INDISPONIBLE = 97;

    @Autowired
    CommandeService service;

    @Autowired
    CommandeRepository daoCommande;

    @Test
    // note : pas de @Transactional ici, car on veut récupérer l'exception générée par le service
    void laCommandeNeDoitPasEtreDejaEnvoyee() {
        assertThrows(IllegalStateException.class,
            () -> service.enregistreExpedition(NUMERO_COMMANDE_DEJA_LIVREE),
            "Cette commande a déjà été envoyée");
    }

    @Test
    @Transactional
    //  note : @Transactional ici, car on veut tester les modifications faites par le service sur les entités
    void lesQuantitesSontMisesAJour() {
        var commande = daoCommande.findById(NUMERO_COMMANDE_PAS_LIVREE).orElseThrow();
        assertNull(commande.getEnvoyeele(), "Cette commande n'a pas encore été envoyée");

        // Note : on ne vérifie que pour la première ligne de la commande
        // Pour bien faire, il fadrait vérifier pour chaque ligne
        var premiereLigne = commande.getLignes().get(0);
        int stockAvant = premiereLigne.getProduit().getUnitesEnStock();
        int unitesCommandeesAvant = premiereLigne.getProduit().getUnitesCommandees();
        var quantiteLigne = premiereLigne.getQuantite();

        service.enregistreExpedition(NUMERO_COMMANDE_PAS_LIVREE);

        assertNotNull(commande.getEnvoyeele(), "La date d'envoi doit être mise à jour");

        int stockApres = premiereLigne.getProduit().getUnitesEnStock();
        assertEquals(stockAvant - quantiteLigne, stockApres, "La quantité en stock doit être décrémentée");

        int unitesCommandeesApres = premiereLigne.getProduit().getUnitesCommandees();
        assertEquals(unitesCommandeesAvant - quantiteLigne, unitesCommandeesApres, "La quantité en commande doit être décrémentée");
    }
}
