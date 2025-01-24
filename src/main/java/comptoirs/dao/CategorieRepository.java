package comptoirs.dao;

import comptoirs.entity.Categorie;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


// This will be AUTO IMPLEMENTED by Spring into a Bean called ProductCodeRepository
// CRUD refers Create, Read, Update, Delete

/**
 * Un repository avec des méthodes de recherche spécifiques, auto-implémentées par Spring
 */

public interface CategorieRepository extends JpaRepository<Categorie, Integer> {	
	/**
	 * Recherche une catégorie par son libellé (unique)
	 * @param libelle le libellé recherché
	 * @return Une catégorie avec ce libellé
	 */
	Categorie findByLibelle(String libelle);	
	/**
	 * Recherche les catégories dont le libellé contient une sous-chaine
	 * @param substring la sous-chaine à rechercher dans le libellé
	 * @return la liste des catégories dont le libellé contient substring
	 */
	List<Categorie> findByLibelleContaining(String substring);
}
