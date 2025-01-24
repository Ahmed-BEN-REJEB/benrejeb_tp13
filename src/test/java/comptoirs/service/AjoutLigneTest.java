package comptoirs.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import comptoirs.dao.ProduitRepository;
import jakarta.validation.ConstraintViolationException;


@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class AjoutLigneTest {
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
    ProduitRepository daoProduit;

    @Test
    void onPeutAjouterDesLignesSiPasLivre() {
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1);
        assertNotNull(ligne.getId(), "La ligne doit être enregistrée, sa clé générée");
    }

    @Test
    void ajouterDesLignesIncrementeLaQuantiteCommandee() {
        var produit = daoProduit.findById(REFERENCE_PRODUIT_DISPONIBLE_2).orElseThrow();
        var quantiteAvant = produit.getUnitesCommandees();
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_2, 1);
        assertEquals(quantiteAvant + 1, ligne.getProduit().getUnitesCommandees(), "La quantité commandée doit être incrémentée");
    }

    @Test
    void laQuantiteEstPositive() {
        assertThrows(ConstraintViolationException.class,
            () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 0),
            "La quantite d'une ligne doit être positive");
    }

    @Test
    void impossibleAjouterDesLignesSiLivre() {
        assertThrows(IllegalStateException.class,
            () -> service.ajouterLigne(NUMERO_COMMANDE_DEJA_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1),
            "On ne peut pas ajouter de ligne à une commande déjà livrée");
    }

    @Test
    void ilFautAssezDeStock() {
        var produit = daoProduit.findById(REFERENCE_PRODUIT_DISPONIBLE_4).orElseThrow();
        var stock = produit.getUnitesEnStock();
        var enCommande = produit.getUnitesCommandees();
        int quantiteMaximumCommandable = stock - enCommande;
        assertThrows(IllegalStateException.class,
            () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_4, quantiteMaximumCommandable + 1),
            "La quantité commandée ne doit pas dépasser le stock disponible");
    }
}
