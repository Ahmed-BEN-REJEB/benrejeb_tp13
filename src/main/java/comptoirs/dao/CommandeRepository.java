package comptoirs.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import comptoirs.entity.Commande;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called CommandeRepository

public interface CommandeRepository extends JpaRepository<Commande, Integer> {
    /**
     * Calcule le montant total d'une commande
     * @param numeroCommande le numéro de la commande à traiter
     * @return le montant total de la commande
     */
    @Query("""
        SELECT SUM(l.quantite * l.produit.prixUnitaire * (1 - l.commande.remise)) as montantTotal
        FROM Ligne l
        WHERE l.commande.numero = :numeroCommande
    """)
    BigDecimal montantArticles(Integer numeroCommande);

    /**
     * Pour le client passé en paramètre, renvoie une liste de projections contenant le numéro de commande, sa date de saisie et son montant total
     * @param codeClient le code du client à traiter
     * @return une liste de projections contenant le numéro de commande, sa date de saisie et son montant total
     */
    @Query("""
        SELECT  l.commande.numero as numeroCommande, l.commande.port as port,
                SUM(l.quantite * l.produit.prixUnitaire * (1 - l.commande.remise)) as montantArticles
        FROM Ligne l
        WHERE l.commande.client.code = :codeClient
        GROUP BY numeroCommande, port
    """)
    List<CommandeProjection> commandesPourClient(String codeClient);
}
