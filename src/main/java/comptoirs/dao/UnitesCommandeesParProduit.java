package comptoirs.dao;
/*
 *Utilisé pour représenter le résultat des requêtes statistiques
 * Cette interface sera auto-implémentée par Spring
 */
public interface UnitesCommandeesParProduit {
	String getNomProduit();
	Long getUnitesCommandees();
}
