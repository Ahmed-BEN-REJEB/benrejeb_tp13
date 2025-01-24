package comptoirs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
public class Produit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE) // la clé est autogénérée par la BD, On ne veut pas de "setter"
	private Integer reference = null;

	@NonNull
	@Column(unique=true, length = 255)
    @Size(min = 1, max = 255)
	private String nom;

	@ToString.Exclude
    // Inutilisé dans cette application
	private int fournisseur = 1;

	private String quantiteParUnite = "Une boîte de 12";

    @Basic(optional = false)
    @Column(precision = 18, scale = 2)
    @PositiveOrZero(message = "Le prix ne peut pas être négatif")
	private BigDecimal prixUnitaire = BigDecimal.TEN;

	@ToString.Exclude
    // Contrainte métier à respecter : >= unitesCommandees
	private int unitesEnStock = 0;

	@ToString.Exclude
    // Nombre d'unités commandées et pas encore livrées
	private int unitesCommandees = 0;

	@ToString.Exclude
    // Contrainte métier : si > unitesEnStock, il faut réapprovisionner ce produit
	private int niveauDeReappro = 0;

	// 0 = FALSE
    // note : le getter généré par lombok s'appelle isIndisponible()
	private boolean indisponible = false;

	@ManyToOne(optional = false)
	@NonNull
	private Categorie categorie ;

	@ToString.Exclude
	@OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
	private List<Ligne> lignes = new LinkedList<>();


}
