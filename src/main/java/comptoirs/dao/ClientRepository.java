package comptoirs.dao;


import comptoirs.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called ProductCodeRepository
// CRUD refers Create, Read, Update, Delete

public interface ClientRepository extends JpaRepository<Client, String> {
    /**
     * Calcule le nombre d'articles commandés par un client
     * @param clientCode la clé du client
     */
    @Query("SELECT 0")
    int nombreArticlesCommandesPar(String clientCode);

    /**
     * Recherche un client par son nom de société
     * @param societe le nom de la société à rechercher
     * @return un Optional contenant le client trouvé, ou Optional.empty() si aucun client ne correspond
     */
    Optional<Client> findBySociete(String societe);

    /**
     * Trouve pour un client donné le nombre de produits différents qu'il a commandé
     * @param codeClient le code du client à traiter
     *             (le code est la clé primaire de l'entité Client)
     * @return le nombre de produits différents commandés par ce client
     */
    @Query("SELECT COUNT(DISTINCT l.produit.reference) FROM Ligne l WHERE l.commande.client.code = :codeClient")
    int countDistinctProduitsByCode(String codeClient);

    /**
     * Pour chaque client, renvoyer le nombre de produits différents qu'il a commandés
     * @return une liste de projections contenant la société du client et le nombre de produits différents commandés
     */
    @Query("SELECT l.commande.client.societe as societe, COUNT(DISTINCT l.produit.reference) AS nombre FROM Ligne l GROUP BY societe")
    List<NombreDeProduitsDifferentsParClient> produitsParClient();

}
